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
package ro.nextreports.designer.util;


import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.StructurePanel;
import ro.nextreports.designer.StructureTreeNode;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.tree.DBNodeExpander;

/**
 * @author Decebal Suiu
 */

public class TreeUtil {

    /**
     * Expand all nodes of a tree.
     *
     * @param tree The tree whose nodes to expand.
     */
    public static void expandAll(JTree tree) {
        expandAll(tree, new TreePath(tree.getModel().getRoot()));
    }

    /**
     * Collapse all nodes of a tree.
     *
     * @param tree The tree whose nodes to expand.
     */
    public static void collapseAll(JTree tree) {
        TreePath pathToRoot = new TreePath(tree.getModel().getRoot());
        collapseAll(tree, pathToRoot);
        if (!tree.isRootVisible()) {
            tree.expandPath(pathToRoot);
        }
    }

    public static void collapseAllFromNode(JTree tree, DBBrowserNode node) {
        TreePath pathToNode = new TreePath(node);
        collapseAll(tree, pathToNode);
        if (!tree.isRootVisible()) {
            tree.expandPath(pathToNode);
        }
    }

    /**
     * Expand a tree node and all its child nodes recursively.
     *
     * @param tree The tree whose nodes to expand.
     * @param path Path to the node to start at.
     */
    public static void expandAll(JTree tree, TreePath path) {
        Object node = path.getLastPathComponent();
        TreeModel model = tree.getModel();
        if (model.isLeaf(node)) {
            return;
        }
        tree.expandPath(path);
        int num = model.getChildCount(node);
        for (int i = 0; i < num; i++) {
            expandAll(tree, path.pathByAddingChild(model.getChild(node, i)));
        }
    }

    /**
     * Collapse a tree node and all its child nodes recursively.
     *
     * @param tree The tree whose nodes to collapse.
     * @param path Path to the node to start at.
     */
    public static void collapseAll(JTree tree, TreePath path) {
        Object node = path.getLastPathComponent();
        TreeModel model = tree.getModel();
        if (model.isLeaf(node)) {
            return;
        }
        int num = model.getChildCount(node);
        for (int i = 0; i < num; i++) {
            collapseAll(tree, path.pathByAddingChild(model.getChild(node, i)));
        }
        tree.collapsePath(path);
    }

    /**
     * Get a copy of the list of expanded tree paths of a tree.
     */
    public static TreePath[] getExpandedPaths(JTree tree) {
        ArrayList<TreePath> expandedPaths = new ArrayList<TreePath>();
        TreePath rootPath = new TreePath(tree.getModel().getRoot());
        Enumeration enumeration = tree.getExpandedDescendants(rootPath);
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                expandedPaths.add((TreePath)enumeration.nextElement());
            }
        }
        TreePath[] array = new TreePath[expandedPaths.size()];
        expandedPaths.toArray(array);
        return array;
    }

    /**
     * Expand all the previously remembered expanded paths.
     */
    public static void setExpandedPaths(JTree tree, TreePath[] expandedPaths) {
        if (expandedPaths == null) {
            return;
        }
        for (int i = 0; i < expandedPaths.length; ++i) {
            TreePath oldPath = expandedPaths[i];
            TreePath newPath = searchPath(tree.getModel(), oldPath);
            if (newPath != null) {
                tree.expandPath(newPath);
            }
        }
    }

    /**
     * Search for a path in the specified tree model, whose nodes have
     * the same name (compared using <code>equals()</code>)
     * as the ones specified in the old path.
     *
     * @return a new path for the specified model, or null if no such path
     *         could be found.
     */
    public static TreePath searchPath(TreeModel model, TreePath oldPath) {
        Object treenode = model.getRoot();
        Object[] oldPathNodes = oldPath.getPath();
        TreePath newPath = new TreePath(treenode);
        for (int i = 0; i < oldPathNodes.length; ++i) {
            Object oldPathNode = oldPathNodes[i];
            if (treenode.toString().equals(oldPathNode.toString())) {
                if (i == (oldPathNodes.length - 1)) {
                    return newPath;
                } else {
                    if (model.isLeaf(treenode)) {
                        return null; // not found
                    } else {
                        int count = model.getChildCount(treenode);
                        boolean foundChild = false;
                        for (int j = 0; j < count; ++j) {
                            Object child = model.getChild(treenode, j);
                            if (child.toString().equals(oldPathNodes[i + 1].toString())) {
                                newPath = newPath.pathByAddingChild(child);
                                treenode = child;
                                foundChild = true;
                                break;
                            }
                        }
                        if (!foundChild) {
                            return null; // couldn't find child with same name
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void refreshCharts() throws Exception {
        refresh(DBNodeExpander.CHARTS);
    }

    public static void refreshReports() throws Exception {
        refresh(DBNodeExpander.REPORTS);
    }

    public static void refreshQueries() throws Exception {
        refresh(DBNodeExpander.QUERIES);
    }

    private static void refresh(String nodeExpander) throws Exception {
        DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
        final DBBrowserNode selectedNode = (DBBrowserNode) tree.getLastSelectedPathComponent();
        DBBrowserNode node = tree.searchNode(nodeExpander);
        node.removeAllChildren();
        tree.startExpandingTree(node, false, null);
        if (selectedNode != null) {
            // is null for QUERIES, REPORTS and CHARTS nodes
            if (selectedNode.getDBObject().getAbsolutePath() != null) {
                tree.selectNode(selectedNode.getDBObject());
            }
        }
    }

    public static void refreshDatabase() throws Exception {        
        DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
        
        DBBrowserNode node = tree.searchNode(DBNodeExpander.TABLES);
        // schema node was not expanded : nothing to refresh
        if (node == null) {
            return;
        }
        node.removeAllChildren();
        tree.startExpandingTree(node, false, null);

        node = tree.searchNode(DBNodeExpander.VIEWS);
        node.removeAllChildren();
        tree.startExpandingTree(node, false, null);

        node = tree.searchNode(DBNodeExpander.PROCEDURES);
        node.removeAllChildren();
        tree.startExpandingTree(node, false, null);

    }

    public static void expandConnectedDataSource() {
        DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
        String name = DefaultDataSourceManager.getInstance().getConnectedDataSource().getName();
        DBBrowserNode node = tree.searchNode(name, DBObject.DATABASE);
        if (node != null) {
            if (node.getChildCount() == 0) {
                tree.startExpandingTree(node, true, null);
                tree.fireTreeExpanded(new TreePath(node.getPath()));
                for (int i = 0, size = node.getChildCount(); i < size; i++) {
                    tree.startExpandingTree((DBBrowserNode) node.getChildAt(i), false, null);
                }
            }
        }
    }

    /** Get the data source node for the current node
     *
     * @param node current node
     * @return data source node if any for the current node or throws IllegalArgumentException for root node
     *
     */
    public static DBBrowserNode getDataSourceNode(DBBrowserNode node) {
        byte type = node.getDBObject().getType();
        if (type == DBObject.DATASOURCE) {
            throw new IllegalArgumentException("Invalid node type : " + type);
        } else if (type == DBObject.DATABASE) {
            return node;
        } else {
            DBBrowserNode parent = (DBBrowserNode)node.getParent();
            while (parent.getDBObject().getType() != DBObject.DATABASE) {
                parent = (DBBrowserNode)parent.getParent();
            }
            return parent;
        }
    }
    
    /**
     * Get tree path for a band element selection
     * @param row band element row
     * @param column band element column
     * @return tree path for a band element selection
     */
    public static  TreePath getTreePath(int row, int column) {
        StructurePanel structurePanel = Globals.getReportDesignerPanel().getStructurePanel();
        StructureTreeNode node = structurePanel.getBandElementTreeNode(row, column);
        TreePath path = null;
        // for cell spans node can be null - see setSelectionRange in DefaultSelectionModel
        if (node != null) {
            boolean filter = Globals.getReportDesignerPanel().getStructurePanel().getStructureTreeModel().isActivatedFilter();
            if ((filter && node.isVisible()) || !filter) {
                path = new TreePath(structurePanel.getStructureTreeModel().getPathToRoot(node));
            }
        }

        return path;
    }
    
    /**
     * Get tree path for a row element selection
     * @param row row element     
     * @return tree path for a row element selection
     */
    public static TreePath getTreePath(int row) {
        StructurePanel structurePanel = Globals.getReportDesignerPanel().getStructurePanel();
        StructureTreeNode node = structurePanel.getRowElementTreeNode(row);
        TreePath path = null;
        // for cell spans node can be null - see setSelectionRange in DefaultSelectionModel
        if (node != null) {
            boolean filter = Globals.getReportDesignerPanel().getStructurePanel().getStructureTreeModel().isActivatedFilter();
            if ((filter && node.isVisible()) || !filter) {
                path = new TreePath(structurePanel.getStructureTreeModel().getPathToRoot(node));
            }
        }

        return path;
    }

}

