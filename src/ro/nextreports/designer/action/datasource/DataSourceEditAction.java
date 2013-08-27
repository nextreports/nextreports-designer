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

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.ConnectionDialog;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DataSourceType;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 4:32:14 PM
 */
public class DataSourceEditAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DataSourceEditAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("datasource.edit"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_edit"));
        putValue(Action.MNEMONIC_KEY, new Integer('E'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("datasource.edit.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("datasource.edit.long.desc"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();
        try {
            DataSourceManager manager = DefaultDataSourceManager.getInstance();
            DataSource ds = manager.getDataSource(name);
            if (ds.getStatus() == DataSourceType.CONNECTED) {
                Show.info(I18NSupport.getString("datasource.edit.connected", ds.getName()));
                return;
            }
            ConnectionDialog dialog = new ConnectionDialog(Globals.getMainFrame(),
                    I18NSupport.getString("datasource.edit.long.desc"), ds, false);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);
            if (dialog.wasModified()) {
                String oldName = dialog.getOldName();
                String newName = dialog.getName();
                //System.out.println("oldName=" + oldName + " newName=" + newName);
                if (!oldName.equals(newName)) {
                    (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + oldName)).
                            renameTo(new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + newName));
                    tree.renameNode(selectedNode, newName, null);

                    SchemaManagerUtil.renameVisibleSchemas(oldName, newName);
                }
                Globals.getReportLayoutPanel().selectConnectedDataSource();
                Globals.getChartLayoutPanel().selectConnectedDataSource();
                
                if (dialog.getAutoConnect()) {
                	DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
                	tree.selectNode(newName, DBObject.DATABASE);
                	DataSourceConnectAction ca= new DataSourceConnectAction(tree, tree.getSelectionPath());
                	ca.actionPerformed(null);
                }
            }
        } catch (Exception e1) {
            Show.error(e1);
        }
    }

}
