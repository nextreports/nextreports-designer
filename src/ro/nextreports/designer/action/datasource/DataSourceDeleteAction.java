/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.action.datasource;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Iterator;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DataSourceType;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.DefaultSchemaManager;
import ro.nextreports.designer.datasource.PersistedSchema;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 4:35:59 PM
 */
public class DataSourceDeleteAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DataSourceDeleteAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("delete"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_delete"));
        putValue(Action.MNEMONIC_KEY, new Integer('T'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T,
//                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("delete.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("delete.long.desc"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();
        String dir = FileReportPersistence.CONNECTIONS_DIR + File.separator + name;

        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                "<HTML>" + I18NSupport.getString("delete.datasource", name) + "<br>" +
                 I18NSupport.getString("delete.datasource.directory", dir)  + "</HTML>",
                I18NSupport.getString("delete.long.desc"),
                JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            DataSourceManager manager = DefaultDataSourceManager.getInstance();
            DataSource ds = manager.getDataSource(name);
            if (ds.getStatus() == DataSourceType.CONNECTED) {
                manager.disconnect(name);
            }
            manager.removeDataSource(name);
            manager.save();

            boolean del = FileUtil.deleteDir(new File(dir));
            tree.removeNode(selectedNode);
            if (!del) {
                Show.info(I18NSupport.getString("delete.datasource.directory.warning", dir));
            } else {
                SchemaManagerUtil.deleteVisibleSchemas(ds);
                Globals.getReportLayoutPanel().selectConnectedDataSource();
                Globals.getChartLayoutPanel().selectConnectedDataSource();
            }
        } catch (Exception e1) {
            Show.error(e1);
        }
    }

}
