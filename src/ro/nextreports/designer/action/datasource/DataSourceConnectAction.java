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

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 4:10:55 PM
 */
public class DataSourceConnectAction extends AbstractAction {

    private DBBrowserTree tree;
    private TreePath selPath;

    private static final Log LOG = LogFactory.getLog(DataSourceConnectAction.class);

    public DataSourceConnectAction(DBBrowserTree tree, TreePath selPath) {
        putValue(Action.NAME, I18NSupport.getString("connect"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_connect"));
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
//                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("connect.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("connect.long.desc"));
        this.tree = tree;
        this.selPath = selPath;
    }

    public void actionPerformed(final ActionEvent ev) {

        Thread executorThread = new Thread(new Runnable() {

            public void run() {

                final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
                final String name = selectedNode.getDBObject().getName();

                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("connect.to",name));
                activator.start();

                try {
                    DataSourceManager manager = DefaultDataSourceManager.getInstance();
                    DataSource ds = manager.getConnectedDataSource();
                    if (ds != null) {
                        DBBrowserNode node = tree.searchNode(ds.getName(), DBObject.DATABASE);
                        DataSourceDisconnectAction daction = new DataSourceDisconnectAction(tree, node);
                        daction.actionPerformed(ev);
                        if (!daction.executed()) {
                            return;
                        }
                    }
                    manager.connect(name);
                    Globals.getReportLayoutPanel().resetRunDataSource(); // combobox with data sources must be empty
                    afterJob();
                    // possibly node was not expanded, give it a chance to load children
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (selectedNode.getChildCount() == 0) {
                                tree.startExpandingTree(selectedNode, false, null);
                            }
                            tree.fireTreeExpanded(selPath);
                            tree.selectNode(name, DBObject.DATABASE);
                            
                            afterCreate();
                        }
                    });

                } catch (Exception e1) {
                    LOG.error(e1.getMessage(), e1);
                    Show.error(e1);
                } finally {
                    if (activator != null) {
                        activator.stop();
                    }
                }
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }
    
    // NON-EDT
    protected void afterJob() {}
    
    // EDT
    protected void afterCreate() {}
}
