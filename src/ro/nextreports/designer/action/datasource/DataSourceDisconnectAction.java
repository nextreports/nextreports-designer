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

import javax.swing.*;

import ro.nextreports.designer.action.query.NewQueryAction;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TreeUtil;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 4:21:50 PM
 */
public class DataSourceDisconnectAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;
    private boolean executed;

    public DataSourceDisconnectAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("disconnect"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database"));
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("disconnect.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("disconnect.long.desc"));
        this.tree = tree;
        this.selectedNode = selectedNode;
        executed = true;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();
        try {
            // clear what is selected
            NewQueryAction nq = new NewQueryAction();
            nq.actionPerformed(null);
            if (!nq.executed()) {
                executed = false;
                return;
            }

            DataSourceManager manager = DefaultDataSourceManager.getInstance();
            manager.disconnect(name);
            if (selectedNode.getChildCount() > 0) {
                TreeUtil.collapseAllFromNode(tree, selectedNode);
                int no = selectedNode.getChildCount();
                for (int i = no-1; i >= 0; i--) {
                    tree.removeNode((DBBrowserNode) selectedNode.getChildAt(i));
                }
            }
        } catch (Exception e1) {
            Show.error(e1);
        }
    }

    public boolean executed() {
        return executed;
    }

}
