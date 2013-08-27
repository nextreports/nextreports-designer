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
package ro.nextreports.designer.action.report;


import javax.swing.*;

import ro.nextreports.designer.action.datasource.DataSourceDisconnectAction;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.wizrep.RepWizard;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 9, 2008
 * Time: 2:24:27 PM
 */
public class WizardAction extends AbstractAction {

    private DBBrowserTree tree;

    public WizardAction(DBBrowserTree tree) {
        this.tree = tree;
        putValue(Action.NAME, I18NSupport.getString("wizard.action.name"));
        Icon icon = ImageUtil.getImageIcon("wizard");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("wizard.mnemonic",  new Integer('W')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("wizard.accelerator", "control W")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("wizard.action.name"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("wizard.action.name"));
    }

    public void actionPerformed(ActionEvent e) {

        boolean doit = true;
        DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
        if (ds != null) {
            DBBrowserNode foundNode = tree.searchNode(ds.getName(), DBObject.DATABASE);
            if (foundNode != null) {                
                DataSourceDisconnectAction da = new DataSourceDisconnectAction(tree, foundNode);
                da.actionPerformed(e);
                if (!da.executed()) {
                    doit = false;
                }
            }
        }

        if(doit) {            
            new RepWizard();
        }
    }

}
