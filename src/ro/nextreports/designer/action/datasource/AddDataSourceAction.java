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


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.ConnectionDialog;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 16, 2006
 * Time: 2:09:10 PM
 */
public class AddDataSourceAction extends AbstractAction {

    private ConnectionDialog dialog;
    private Window owner;

    public AddDataSourceAction() {
        this(Globals.getMainFrame(), true);
    }

    public AddDataSourceAction(boolean fullName) {
        this(Globals.getMainFrame(), true, fullName);
    }

    public AddDataSourceAction(Window owner, boolean withName)  {
        this(owner, withName, true);
    }

    public AddDataSourceAction(Window owner, boolean withName, boolean fullName) {
        this.owner = owner;
        if (withName) {
            if (fullName) {
                putValue(Action.NAME, I18NSupport.getString("add.data.source"));
            } else {
                putValue(Action.NAME, I18NSupport.getString("add.data.source.small"));
            }
        }
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_add"));
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("datasource.add.mnemonic", new Integer('D')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("datasource.add.accelerator", "control D")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("add.data.source.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("add.data.source.desc"));
    }

    public void actionPerformed(ActionEvent ev) {
        if (!LicenseUtil.allowToAddAnotherDataSource()) {
            return;
        }

        if (owner instanceof JFrame) {
            dialog = new ConnectionDialog((JFrame) owner,
                    I18NSupport.getString("add.data.source.desc"), null, false) {
                public void afterSave() {
                    afterSaveAction();
                }
            };
        } else {
            dialog = new ConnectionDialog((JDialog) owner,
                    I18NSupport.getString("add.data.source.desc"), null, false) {
                public void afterSave() {
                    afterSaveAction();
                }
            };
        }
        dialog.pack();
        dialog.setResizable(false);
        Show.centrateComponent(owner, dialog);
        dialog.setVisible(true);
        // create directory for queries and reports
        if (dialog.wasAdded()) {
            (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + dialog.getName() + File.separator + FileReportPersistence.QUERIES_FOLDER)).mkdirs();
            (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + dialog.getName() + File.separator + FileReportPersistence.REPORTS_FOLDER)).mkdirs();
            (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + dialog.getName() + File.separator + FileReportPersistence.CHARTS_FOLDER)).mkdirs();
            SchemaManagerUtil.addDefaultSchemaAsVisible(dialog.getAddedDataSource());

            Globals.getReportLayoutPanel().selectConnectedDataSource();
            Globals.getChartLayoutPanel().selectConnectedDataSource();
            
            if (dialog.getAutoConnect()) {
            	DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
            	tree.selectNode(dialog.getAddedDataSource().getName(), DBObject.DATABASE);
            	DataSourceConnectAction ca= new DataSourceConnectAction(tree, tree.getSelectionPath());
            	ca.actionPerformed(null);
            }
        }
    }

    public void afterSaveAction(){
    }

    public DataSource getAddedDataSource() {
        return dialog.getAddedDataSource();
    }

}
