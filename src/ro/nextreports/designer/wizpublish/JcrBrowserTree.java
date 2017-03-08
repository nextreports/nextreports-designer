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
package ro.nextreports.designer.wizpublish;

import ro.nextreports.server.api.client.WebServiceClient;
import ro.nextreports.server.api.client.DataSourceMetaData;
import ro.nextreports.server.api.client.EntityConstants;

import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXTree;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 18:05:45

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.tree.NodeExpander;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

//
public class JcrBrowserTree extends JXTree {

    private static final Log LOG = LogFactory.getLog(JcrBrowserTree.class);

    private JcrBrowserTreeModel model;
    private JcrBrowserTree instance;
    private WebServiceClient client;

    public JcrBrowserTree(final byte typeRoot, WebServiceClient client) {
    	this(typeRoot, client, false);
    }
    public JcrBrowserTree(final byte typeRoot, WebServiceClient client, boolean allowMultipleSelection) {
        super();
        this.client = client;
        populateTree(typeRoot);

        setCellRenderer(new JcrBrowserTreeRenderer());
        if (!allowMultipleSelection){
        	 getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }

        instance = this;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
                if (e.isPopupTrigger()) {
                    final TreePath selPath = getPathForLocation(e.getX(), e.getY());
                    if (selPath == null) {
                        return;
                    }
                    setSelectionPath(selPath);
                    try {
                        final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
                        if (selectedNode != null) {

                            JPopupMenu popupMenu = new JPopupMenu();
                            boolean show = false;
                            if ((selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT) ||
                                    (selectedNode.getDBObject().getType() == DBObject.DATABASE) ||
                                    (selectedNode.getDBObject().getType() == DBObject.REPORTS_GROUP) ||
                                    (selectedNode.getDBObject().getType() == DBObject.CHARTS_GROUP)) {
                                JMenuItem menuItem = new JMenuItem(new PublishFolderAction(selectedNode));
                                popupMenu.add(menuItem);
                                show = true;
                            }

                            if ((typeRoot == DBObject.DATABASE) &&
                                    ((selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT) ||
                                            (selectedNode.getDBObject().getType() == DBObject.DATABASE))) {
                                PublishDataSourceAction publishDSAction = new PublishDataSourceAction(selectedNode);

                                JMenuItem menuItem2 = new JMenuItem(publishDSAction);
                                popupMenu.add(menuItem2);
                                show = true;
                            }
                            if (show) {
                                popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
                            }
                        }
                    } catch (Exception ex) {
                        Show.error(ex);
                    }
                }
            }
        });

    }

    class PublishFolderAction extends AbstractAction {
        private DBBrowserNode selectedNode;

        public PublishFolderAction(DBBrowserNode selectedNode) {
            putValue(Action.NAME, I18NSupport.getString("publish.folder"));
            putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("folder"));
            putValue(Action.MNEMONIC_KEY, new Integer('F'));
            putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("publish.folder.desc"));
            putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("publish.folder.desc"));
            this.selectedNode = selectedNode;
        }

        public void actionPerformed(ActionEvent ev) {
            String name = JOptionPane.showInputDialog(I18NSupport.getString("publish.folder.name"));
            if (name != null) {
                try {
                    int found = exists(selectedNode.getDBObject().getAbsolutePath(), name);
                    if (found != EntityConstants.ENTITY_NOT_FOUND) {
                        if (found == EntityConstants.FOLDER_FOUND) {
                            Show.info(SwingUtilities.getWindowAncestor(JcrBrowserTree.this),
                                    I18NSupport.getString("wizard.publish.folder.overwrite", name));
                            return;
                        } else  {
                            Show.info(I18NSupport.getString("wizard.publish.entity.found"));
                            return;
                        }
                    }
                    client.createFolder(selectedNode.getDBObject().getAbsolutePath() + "/" + name);
                    // refresh tree
                    selectedNode.removeAllChildren();
                    expandNode(selectedNode, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Show.error(SwingUtilities.getWindowAncestor(JcrBrowserTree.this),
                            I18NSupport.getString("wizard.publish.folder.error"), e);
                }
            }
        }
    }

    class PublishDataSourceAction extends AbstractAction {

        private DBBrowserNode selectedNode;

        public PublishDataSourceAction(DBBrowserNode selectedNode) {
            putValue(Action.NAME, I18NSupport.getString("publish.datasource"));
            putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_publish"));
            putValue(Action.MNEMONIC_KEY, new Integer('P'));
            putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("publish.datasource.desc"));
            putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("publish.datasource.desc"));
            this.selectedNode = selectedNode;
        }

        public void actionPerformed(ActionEvent ev) {
            DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
            DataSourceMetaData metaData = new DataSourceMetaData();
            metaData.setPath(selectedNode.getDBObject().getAbsolutePath() + "/" + ds.getName());
            metaData.setVendor(ds.getType());
            metaData.setDriver(ds.getDriver());
            metaData.setUrl(ds.getUrl());
            metaData.setUsername(ds.getUser());
            metaData.setPassword(ds.getPassword());
            metaData.setProperties(ds.getProperties());
            try {
                int found = exists(selectedNode.getDBObject().getAbsolutePath(), ds.getName());
                if (found != EntityConstants.ENTITY_NOT_FOUND) {
                    if (found == EntityConstants.DATA_SOURCE_FOUND) {
                        if (!overwrite(I18NSupport.getString("wizard.publish.datasource.overwrite", ds.getName()))) {
                            return;
                        }
                    } else {
                        Show.info(I18NSupport.getString("wizard.publish.entity.found"));
                        return;
                    }
                }

                client.publishDataSource(metaData);
                // refresh tree
                selectedNode.removeAllChildren();
                expandNode(selectedNode, false);
            } catch (Exception e) {
                e.printStackTrace();
                Show.error(SwingUtilities.getWindowAncestor(JcrBrowserTree.this),
                        I18NSupport.getString("wizard.publish.datasource.error"), e);
            }
        }
    }

    private int exists(String path, String name) throws Exception {
        return client.entityExists(path + "/" + name);
    }

    private boolean overwrite(String message) {
        Object[] options = {I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no")};
        int option = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(JcrBrowserTree.this),
                message,
                I18NSupport.getString("report.util.confirm"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);

        return (option == JOptionPane.YES_OPTION);
    }

    private void populateTree(byte typeRoot) {
        DBObject object = new DBObject(getRootName(typeRoot), null, typeRoot);
        if (typeRoot == DBObject.DATABASE) {
            object.setAbsolutePath(JcrNodeExpander.DATABASES_ROOT);
        } else if (typeRoot == DBObject.REPORTS_GROUP) {
            object.setAbsolutePath(JcrNodeExpander.REPORTS_ROOT);
        } else if (typeRoot == DBObject.CHARTS_GROUP) {
            object.setAbsolutePath(JcrNodeExpander.CHARTS_ROOT);
        }
        model = new JcrBrowserTreeModel(new DBBrowserNode(object), client);
        setModel(model);
        DBBrowserNode root = (DBBrowserNode) model.getRoot();
        addTreeExpansionListener(new NodeExpansionListener());
        setShowsRootHandles(true);
        expandNode(root, false);
        if ((typeRoot == DBObject.DATABASE) || (typeRoot == DBObject.REPORTS_GROUP) || (typeRoot == DBObject.CHARTS_GROUP)) {
            if (root.getChildCount() > 0) {
                expandNode((DBBrowserNode) root.getChildAt(0), false);
            }
        }
    }

    private String getRootName(byte typeRoot) {
        switch (typeRoot) {
            case DBObject.DATABASE:
                return JcrNodeExpander.SOURCES;
            case DBObject.REPORTS_GROUP:
                return JcrNodeExpander.REPORTS;
            case DBObject.CHARTS_GROUP:
                return JcrNodeExpander.CHARTS;
            default:
                return "ROOT";
        }
    }

    public void startExpandingTree(DBBrowserNode node, boolean selectNode, Map selectedPathNames) {
        expandNode(node, selectNode);
    }

    private boolean expandNode(DBBrowserNode node, boolean selectNode) {
        if (node == null) {
            throw new IllegalArgumentException("DBBrowserNode is null");
        }

        // if node hasn't already been expanded.
        if (node.getChildCount() == 0) {
            // add together the standard expanders for this node type and any
            // individual expanders that there are for the node and process them.
            final byte nodeType = node.getDBObject().getType();
            NodeExpander[] expanders = model.getExpanders(nodeType);
            new TreeLoader(node, expanders, selectNode).execute();
            return true;
        }

        return false;
    }

    class NodeExpansionListener implements TreeExpansionListener {

        public void treeExpanded(TreeExpansionEvent ev) {
            final TreePath path = ev.getPath();
            final Object parentObj = path.getLastPathComponent();
            if (parentObj instanceof DBBrowserNode) {
                startExpandingTree((DBBrowserNode) parentObj, false, null);
//                expandedPathNames.put(path.toString(), null);
            }
        }

        public void treeCollapsed(TreeExpansionEvent ev) {
//            expandedPathNames.remove(ev.getPath().toString());
        }

    }

    class TreeLoader {

        private DBBrowserNode parentNode;
        private NodeExpander[] expanders;
        private boolean selectParentNode;

        TreeLoader(DBBrowserNode parentNode, NodeExpander[] expanders,
                   boolean selectParentNode) {
            super();
            this.parentNode = parentNode;
            this.expanders = expanders;
            this.selectParentNode = selectParentNode;
        }

        void execute() {
            try {
                try {
                    loadChildren();
                } finally {
                    fireStructureChanged(parentNode);
                    if (selectParentNode) {
                        clearSelection();
                        setSelectionPath(new TreePath(parentNode.getPath()));
                    }
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
                Show.error(I18NSupport.getString("wizard.publish.connection.error"));
            }
        }

        /**
         * This expands the parent node and shows all its children.
         */
        private void loadChildren() throws Exception {
            for (int i = 0; i < expanders.length; ++i) {
                boolean nodeTypeAllowsChildren = false;
                byte lastNodeType = -1;
                List list = expanders[i].createChildren(parentNode);
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Object nextObj = it.next();
                    if (nextObj instanceof DBBrowserNode) {
                        DBBrowserNode childNode = (DBBrowserNode) nextObj;
                        byte childNodeType = childNode.getDBObject().getType();
                        if (childNodeType != lastNodeType) {
                            lastNodeType = childNodeType;
                            if (model.getExpanders(childNodeType).length > 0) {
                                nodeTypeAllowsChildren = true;
                            } else {
                                nodeTypeAllowsChildren = false;
                            }
                        }
                        childNode.setAllowsChildren(nodeTypeAllowsChildren);
                        parentNode.add(childNode);
                    }
                }
            }
        }

        private void fireStructureChanged(final DBBrowserNode node) {
            JcrBrowserTree.this.model.nodeStructureChanged(node);
        }

    }

    class JcrBrowserTreeRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, hasFocus);

            DBBrowserNode node = (DBBrowserNode) value;
            setText((String) node.getUserObject());

            switch (node.getDBObject().getType()) {
                case DBObject.DATABASE:
                    setIcon(ImageUtil.getImageIcon("connection"));
                    break;
                case DBObject.DATASOURCE:
                    setIcon(ImageUtil.getImageIcon("database"));
                    break;
                case DBObject.REPORTS_GROUP:
                    setIcon(ImageUtil.getImageIcon("reports"));
                    break;
                case DBObject.REPORTS:
                    setIcon(ImageUtil.getImageIcon("report"));
                    break;
                case DBObject.CHARTS_GROUP:
                    setIcon(ImageUtil.getImageIcon("charts"));
                    break;
                case DBObject.CHARTS:
                    setIcon(ImageUtil.getImageIcon("chart"));
                    break;
                case DBObject.FOLDER_REPORT:
                    setIcon(ImageUtil.getImageIcon("folder"));
                    break;
            }

            return this;
        }
    }
}
