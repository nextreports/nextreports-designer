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
package ro.nextreports.designer;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.grid.event.SelectionModelListener;
import ro.nextreports.designer.util.TreeUtil;

import ro.nextreports.engine.band.Band;

/**
 * @author Decebal Suiu
 */
public class SelectionController implements SelectionModelListener,
        TreeSelectionListener {

    private boolean ignoreEvent;

    public void valueChanged(TreeSelectionEvent event) {
        if (ignoreEvent) {
            return;
        }

        JTree tree = (JTree) event.getSource();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null) {
            return;
        }

        SelectionModel selectionModel = Globals.getReportGrid().getSelectionModel();
        selectionModel.clearSelection();
        List<Cell> selectedCells = new ArrayList<Cell>();
        List<Integer> selectedRows = new ArrayList<Integer>();
        for (TreePath path : paths) {
            StructureTreeNode node = (StructureTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();                  
            if (userObject instanceof ReportGridCell) {
            	// cell layout properties
                boolean filter = Globals.getReportDesignerPanel().getStructurePanel().getStructureTreeModel().isActivatedFilter();
                if ((filter && node.isVisible()) || !filter) {
                    selectedCells.add((Cell) userObject);
                }
            } else if (userObject instanceof Integer) { 
            	// row layout properties
            	Integer i = (Integer)userObject;
            	String bandName = ((Band)((StructureTreeNode)node.getParent()).getUserObject()).getName();
            	int gridRow = LayoutHelper.getReportLayout().getGridRow(bandName, i);
            	selectedRows.add(gridRow);            	
            } else if (node.isRoot()) {
                // report layout properties
                selectionModel.addRootSelection();
                return;   
            } else {
                // other nodes in tree (band)
                selectionModel.emptySelection(); 
                return;
            }
        }

        if (selectedCells.size() > 0) {
            ignoreEvent = true;
            selectionModel.addSelectionCells(selectedCells);
            ignoreEvent = false;
        } else if (selectedRows.size() > 0) {
        	ignoreEvent = true;
            selectionModel.addSelectionRows(selectedRows);   // used by PropertyPanel to select properties                                
            ignoreEvent = false;        
        } else {
            selectionModel.clearSelection();
        }
        Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();
    }

    public void selectionChanged(SelectionModelEvent event) {
        if (ignoreEvent || event.isEmpty()) {            
            return;
        }               

        if (event.isRootSelection()) {            
            JTree tree = Globals.getReportDesignerPanel().getStructurePanel().getStructureTree();
            getStructureTree().setSelectionPath(new TreePath(tree.getModel().getRoot()));
            tree.scrollRectToVisible(new Rectangle());                        
            return;
        }

        SelectionModel selectionModel = (SelectionModel) event.getSource();

        List<TreePath> paths = new ArrayList<TreePath>();
        List<Cell> selectedCells = selectionModel.getSelectedCells();             
        TreePath lastVisiblePath = null;        
        for (Cell cell : selectedCells) {        	
        	TreePath path = TreeUtil.getTreePath(cell.getRow(), cell.getColumn());        	
            if (path != null) {                
                paths.add(path);
                lastVisiblePath = path;
            }
        }

        ignoreEvent = true;
        getStructureTree().setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
        Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();
        if (lastVisiblePath != null) {
            JTree tree = Globals.getReportDesignerPanel().getStructurePanel().getStructureTree();
            Rectangle rectangle = tree.getPathBounds(lastVisiblePath);
            if (rectangle != null) {
                tree.scrollRectToVisible(rectangle);
            }
        }
        ignoreEvent = false;
    }    

    private JTree getStructureTree() {
        StructurePanel structurePanel = Globals.getReportDesignerPanel().getStructurePanel();
        return structurePanel.getStructureTree();
    }

}
