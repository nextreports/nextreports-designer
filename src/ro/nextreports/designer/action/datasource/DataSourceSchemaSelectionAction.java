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
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.datasource.SchemaSelectionDialog;
import ro.nextreports.designer.datasource.SchemaSelectionPanel;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 3:56:09 PM
 */
public class DataSourceSchemaSelectionAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DataSourceSchemaSelectionAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("schema.selection.schemas"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_schema"));
        putValue(Action.MNEMONIC_KEY, new Integer('S'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("schema.selection.schemas"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("schema.selection.schemas"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();
        DataSource ds = DefaultDataSourceManager.getInstance().getDataSource(name);
        SchemaSelectionPanel panel = new SchemaSelectionPanel(ds);
        SchemaSelectionDialog dialog = new SchemaSelectionDialog(panel);
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            SchemaManagerUtil.updateVisibleSchemas(name, dialog.getVisibleSchemas());
            tree.refreshSchemas(ds.getName());
        }
    }



}
