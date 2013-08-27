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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.grid.event.GridModelEvent;
import ro.nextreports.designer.grid.event.GridModelListener;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TreeUtil;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class StructurePanel extends JPanel implements GridModelListener, ActionListener {

    private JToggleButton showEmptyButton;
    private StructureTreeNode rootNode;
    private JXTree structureTree;
    private StructureTreeModel structureTreeModel;
    private JPopupMenu popup;
    // when layout is cleared entirely, we must create a ReportGridCell for the first row inserted and
    // firstReportCell variable becomes true
    // on columns inserted event we must test this variable and if it is true we must return
    private boolean firstReportCell = false;

    public StructurePanel() {
        super();
        initComponents();
    }

    public JXTree getStructureTree() {
        return structureTree;
    }

    public StructureTreeModel getStructureTreeModel() {
        return structureTreeModel;
    }

    public void refresh() {
        rootNode = new StructureTreeNode();
        for (Band band : LayoutHelper.getReportLayout().getBands()) {
            rootNode.add(new StructureTreeNode(band));
        }
        structureTreeModel.setRoot(rootNode);
    }
    
    public StructureTreeNode getBandElementTreeNode(int row, int column) {
        Enumeration en = rootNode.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            while (en.hasMoreElements()) {
                StructureTreeNode node = (StructureTreeNode) en.nextElement();
                Object userObject = node.getUserObject();
                if (userObject instanceof ReportGridCell) {
                    ReportGridCell reportGridCell = (ReportGridCell) userObject;
                    if ((row == reportGridCell.getRow()) && (column == reportGridCell.getColumn())) {
                        return node;
                    }
                }
            }
        }

        return null;
    }
    
    public StructureTreeNode getRowElementTreeNode(int row) {
        Enumeration en = rootNode.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            while (en.hasMoreElements()) {
                StructureTreeNode node = (StructureTreeNode) en.nextElement();
                Object userObject = node.getUserObject();
                if (userObject instanceof Band) {
                	Band band = (Band) userObject;
                	for (int i=0, size=node.getChildCount(); i<size; i++) {
                		StructureTreeNode child = (StructureTreeNode)node.getChildAt(i);
                		int bandRow = (Integer) child.getUserObject();
                		int gridRow = LayoutHelper.getReportLayout().getGridRow(band.getName(), bandRow);
                		if (gridRow == row) {
                			return child;
                		}
                	}                   
                }
            }
        }

        return null;
    }

    public void gridChanged(GridModelEvent event) {
//        System.out.println("StructurePanel.gridChanged()");

        int eventType = event.getType();
        if (eventType == GridModelEvent.ROWS_INSERTED) {
//            System.out.println("Row inserted ..............");
            int row = event.getFirstRow();
//            System.out.println("row = " + row);
            String bandName = Globals.getReportGrid().getBandName(row);
//            System.out.println("bandName = " + bandName);
            int columnCount = Globals.getReportGrid().getColumnCount();
            BandLocation bandLocation = Globals.getReportGrid().getBandLocation(bandName);
            // after a clear all
            if (bandLocation == null) {
                return;
            }
            int bandRow = bandLocation.getRow(row);
            DefaultMutableTreeNode bandTreeNode = getBandRowInsertedTreeNode(bandName, bandRow, row);
            if ((row == 0) && (columnCount == 0)) {
                StructureTreeNode elementNode = new StructureTreeNode(new ReportGridCell(null, row, 0));
                elementNode.setVisible(false);
                bandTreeNode.add(elementNode);
                firstReportCell = true;
            }
            for (int column = 0; column < columnCount; column++) {
                //System.out.println("column = " + column);
                StructureTreeNode elementNode = new StructureTreeNode(new ReportGridCell(null, row, column));
                elementNode.setVisible(false);
                bandTreeNode.add(elementNode);
            }

            structureTreeModel.reload();
            //((DefaultTreeModel) structureTree.getModel()).nodeStructureChanged(bandTreeNode.getParent());
//            System.out.println("...............................");

        } else if (eventType == GridModelEvent.ROWS_DELETED) {
//            System.out.println("Row deleted ..............");
            int row = event.getFirstRow();
            int lastRow = event.getLastRow();
            for (int i = lastRow; i >= row; i--) {
//                System.out.println("row = " + i);
                String bandName = Globals.getReportGrid().getBandName(i);
//                System.out.println("bandName = " + bandName);
                int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(i);
                deleteBandRowTreeNode(bandName, bandRow, i);
            }
//            System.out.println("...............................");

        } else if (eventType == GridModelEvent.CELLS_UPDATED) {
//            System.out.println("Cells updated ............");
            int row = event.getFirstRow();
//            System.out.println("row = " + row);
            int column = event.getFirstColumn();
//            System.out.println("column = " + column);
            String bandName = Globals.getReportGrid().getBandName(row);
//            System.out.println("bandName = " + bandName);
//            System.out.println(Globals.getReportGrid().getBandLocations());
            DefaultGridModel gridModel = (DefaultGridModel) event.getSource();
            BandElement element = (BandElement) gridModel.getValueAt(row, column);
            int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);
            StructureTreeNode elementNode = getBandElementTreeNode(bandName, bandRow, column);
            elementNode.setVisible(element != null);
            elementNode.setUserObject(new ReportGridCell(element, row, column));
            TreePath[] selectionPath = structureTree.getSelectionPaths();
            // must use reload to take visble/invisible null cells into account
            structureTreeModel.reload(elementNode.getParent());

            // reselect the nodes
            if ((selectionPath != null) && ((element != null) || !structureTreeModel.isActivatedFilter())) {
                structureTree.setSelectionPaths(selectionPath);
            }
            if (element == null) {
                Globals.getReportDesignerPanel().getPropertiesPanel().refresh();
            }

        } else if (eventType == GridModelEvent.COLUMNS_INSERTED) {
//            System.out.println("Columns inserted ..............");
            int column = event.getFirstColumn();
//            System.out.println("column = " + column);
            if (firstReportCell) {
                firstReportCell = false;
            } else {
                insertColumnNodes(column);
                structureTreeModel.nodeStructureChanged(rootNode);
            }
//            System.out.println("...............................");

        } else if (eventType == GridModelEvent.COLUMNS_DELETED) {
//            System.out.println("Columns deleted ..............");
            int column = event.getFirstColumn();
            int lastColumn = event.getLastColumn();
//            System.out.println("column = " + column);
            for (int i = lastColumn; i >= column; i--) {
                deleteColumnNodes(i);
            }
//            System.out.println("...............................");
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        rootNode = new StructureTreeNode();
        rootNode.add(new StructureTreeNode(new Band(ReportLayout.HEADER_BAND_NAME)));
        rootNode.add(new StructureTreeNode(new Band(ReportLayout.DETAIL_BAND_NAME)));
        rootNode.add(new StructureTreeNode(new Band(ReportLayout.FOOTER_BAND_NAME)));
        structureTreeModel = new StructureTreeModel(rootNode);

        structureTree = new JXTree(structureTreeModel);
        structureTree.setShowsRootHandles(true);
        structureTree.setCellRenderer(new StructureTreeCellRenderer());
        structureTree.setRolloverEnabled(true);
        structureTree.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 

        createPopup();

        // by default do not show empty cells
        Action showEmptyAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                TreePath lastPath = structureTree.getSelectionPath();
                structureTreeModel.activateFilter(!showEmptyButton.isSelected());
                structureTreeModel.reload();
                if (lastPath != null) {
                    structureTree.expandPath(lastPath.getParentPath());
                    structureTree.setSelectionPath(lastPath);
                }
            }

        };
        showEmptyAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("empty_cell"));
        showEmptyAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("report.structure.show.empty"));
        showEmptyButton = new JToggleButton(showEmptyAction);
        structureTreeModel.activateFilter(true);

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);

        // add expand action
        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("expandall");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("querybuilder.expand.all");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                TreeUtil.expandAll(structureTree);
            }

        });

        // add collapse action
        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("collapseall");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("querybuilder.collapse.all");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                TreeUtil.collapseAll(structureTree);
            }

        });

        toolBar.add(showEmptyButton);

        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(structureTree), BorderLayout.CENTER);
    }

    public void addGroup(String groupName, int groupMask) {
        int children = rootNode.getChildCount();

        List<Band> headerBands = LayoutHelper.getReportLayout().getGroupHeaderBands();
        int headers = 0;
        if (headerBands != null) {
            headers = headerBands.size();
        }
        List<Band> footerBands = LayoutHelper.getReportLayout().getGroupFooterBands();
        int footers = 0;
        if (footerBands != null) {
            footers = footerBands.size();
        }
        rootNode.insert(new StructureTreeNode(new Band(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + groupName)), children - footers);
        rootNode.insert(new StructureTreeNode(new Band(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName)), headers);
        ((DefaultTreeModel) structureTree.getModel()).nodeStructureChanged(rootNode);
    }

    public void deleteGroup(String groupName) {
        DefaultMutableTreeNode footerBandNode = getBandTreeNode(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + groupName);
        if (footerBandNode != null) {
            structureTreeModel.removeNodeFromParent(footerBandNode);
        }
        DefaultMutableTreeNode headerBandNode = getBandTreeNode(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName);
        if (headerBandNode != null) {
            structureTreeModel.removeNodeFromParent(headerBandNode);
        }
    }

    private void createPopup() {
        popup = new JPopupMenu();
        JMenuItem mi = new JMenuItem(I18NSupport.getString("insert.row"));
        mi.addActionListener(this);
        mi.setActionCommand("insert");
        popup.add(mi);
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);

        structureTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                show(e);
            }

            public void mouseReleased(MouseEvent e) {
                show(e);
            }

            private void show(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath path = structureTree.getSelectionPath();
                    if (path != null) {
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (dmtn.getUserObject() instanceof Band) {
                            popup.show((JComponent) e.getSource(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getBandTreeNode(String bandName) {
        Enumeration en = rootNode.children();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof Band) {
                Band band = (Band) userObject;
                if (bandName.equals(band.getName())) {
                    return node;
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getBandRowTreeNode(String bandName, int row) {
        DefaultMutableTreeNode bandNode = getBandTreeNode(bandName);
        Enumeration rows = bandNode.children();
        while (rows.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rows.nextElement();
            Object userObject = node.getUserObject();
            if ((Integer) userObject == row) {
                return node;
            }
        }

        StructureTreeNode rowNode = new StructureTreeNode(row);
        bandNode.insert(rowNode, row);

        return rowNode;
    }

    private StructureTreeNode getBandElementTreeNode(String bandName, int row, int column) {
        DefaultMutableTreeNode bandNode = getBandRowTreeNode(bandName, row);
        StructureTreeNode child;
        if (bandNode.getChildCount() <= column) {
            child = new StructureTreeNode(new ReportGridCell(null, row, column));
            child.setVisible(false);
            bandNode.insert(child, column);
        } else {
            child = (StructureTreeNode) bandNode.getChildAt(column);
        }

        return child;
    }

    @SuppressWarnings("unchecked")
    private DefaultMutableTreeNode getBandRowInsertedTreeNode(String bandName, int bandRow, int row) {

        DefaultMutableTreeNode bandNode = getBandTreeNode(bandName);

        // tree must be traversed in preorder (band by band)        
        Enumeration en = rootNode.preorderEnumeration();
        String currentBandName = ReportLayout.HEADER_BAND_NAME;
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof Band) {
                currentBandName = ((Band) userObject).getName();
            } else if (userObject instanceof ReportGridCell) {
                // increment rows for all report grid cells which are in the rows
                // following the inserted row
                ReportGridCell reportGridCell = (ReportGridCell) userObject;
                if (row <= reportGridCell.getRow()) {
                    reportGridCell.setRow(reportGridCell.getRow() + 1);
                }
            } else if (userObject instanceof Integer) {
                // modify 'row' nodes in the tree (for the current band)
                if (bandName.equals(currentBandName)) {
                    if ((Integer) userObject == bandRow) {
                        DefaultMutableTreeNode sibling = node.getNextSibling();
                        node.setUserObject(((Integer) node.getUserObject()).intValue() + 1);
                        while (sibling != null) {
                            sibling.setUserObject(((Integer) sibling.getUserObject()).intValue() + 1);
                            sibling = sibling.getNextSibling();
                        }
                    }
                }
            }
        }

        DefaultMutableTreeNode rowNode = new StructureTreeNode(bandRow);
        bandNode.insert(rowNode, bandRow);

        return rowNode;
    }

    private void deleteBandRowTreeNode(String bandName, int bandRow, int row) {

        // tree must be traversed in preorder (band by band)
        Enumeration en = rootNode.preorderEnumeration();
        String currentBandName = ReportLayout.HEADER_BAND_NAME;
        DefaultMutableTreeNode nodeToDelete = null;
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof Band) {
                currentBandName = ((Band) userObject).getName();
            } else if (userObject instanceof ReportGridCell) {
                // decrement rows for all report grid cells which are in the rows
                // following the inserted row
                ReportGridCell reportGridCell = (ReportGridCell) userObject;
                if (row <= reportGridCell.getRow()) {
                    reportGridCell.setRow(reportGridCell.getRow() - 1);
                }
            } else if (userObject instanceof Integer) {
                // modify 'row' nodes in the tree (for the current band)
                if (bandName.equals(currentBandName)) {
                    if ((Integer) userObject == bandRow) {
                        if (nodeToDelete == null) {
                            nodeToDelete = node;
                            DefaultMutableTreeNode sibling = node.getNextSibling();
                            while (sibling != null) {
                                sibling.setUserObject(((Integer) sibling.getUserObject()).intValue() - 1);
                                sibling = sibling.getNextSibling();
                            }
                        }

                    }
                }
            }
        }
        if (nodeToDelete != null) {
            structureTreeModel.removeNodeFromParent(nodeToDelete);
        }
    }

    private void insertColumnNodes(int column) {
        Enumeration en = rootNode.preorderEnumeration();        
        List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
        List<DefaultMutableTreeNode> nodesToAdd = new ArrayList<DefaultMutableTreeNode>();
        int columnCount = Globals.getReportGrid().getColumnCount();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof ReportGridCell) {
                ReportGridCell reportGridCell = (ReportGridCell) userObject;
                if ((column == reportGridCell.getColumn()) ||
                        ((column == reportGridCell.getColumn() + 1) && (column + 1 == columnCount))) {
                    StructureTreeNode colNode = new StructureTreeNode(
                            new ReportGridCell(null, reportGridCell.getRow(), column));
                    colNode.setVisible(false);
                    nodes.add((DefaultMutableTreeNode) node.getParent());
                    nodesToAdd.add(colNode);
                }
                // increment column for all report grid cells which are in the columns
                // following the inserted column
                if (column <= reportGridCell.getColumn()) {
                    reportGridCell.setColumn(reportGridCell.getColumn() + 1);
                }
            }
        }
        for (int i = 0, size = nodes.size(); i < size; i++) {
            nodes.get(i).insert(nodesToAdd.get(i), column);
        }
    }

    private void deleteColumnNodes(int column) {
        Enumeration en = rootNode.preorderEnumeration();
        List<DefaultMutableTreeNode> nodesToDelete = new ArrayList<DefaultMutableTreeNode>();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object userObject = node.getUserObject();
            if (userObject instanceof ReportGridCell) {
                ReportGridCell reportGridCell = (ReportGridCell) userObject;
                if (column == reportGridCell.getColumn()) {
                    nodesToDelete.add(node);
                }
                // decrement column for all report grid cells which are in the columns
                // following the deleted column
                if (column <= reportGridCell.getColumn()) {
                    reportGridCell.setColumn(reportGridCell.getColumn() - 1);
                }
            }
        }
        for (DefaultMutableTreeNode node : nodesToDelete) {
            structureTreeModel.removeNodeFromParent(node);
        }
    }


    public void actionPerformed(ActionEvent e) {
        DefaultMutableTreeNode node;
        TreePath path = structureTree.getSelectionPath();
        node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (e.getActionCommand().equals("insert")) {

            final NumberSelectionPanel panel = new NumberSelectionPanel(I18NSupport.getString("insert.row.number"));
            final BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("insert.row.after.action.name"), true) {
                public boolean okPressed() {
                    if ((panel.getNumber() < 1) || (panel.getNumber() > BandUtil.MAX)) {
                        Show.info(this, I18NSupport.getString("rowCol.max", BandUtil.MAX));
                        return false;
                    }
                    return true;
                }
            };
            dialog.pack();
            dialog.setLocationRelativeTo(Globals.getMainFrame());
            dialog.setVisible(true);
            if (!dialog.okPressed()) {
                return;
            }

            ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

            int rows = panel.getNumber();

            String bandName = ((Band) node.getUserObject()).getName();
            Band band = LayoutHelper.getReportLayout().getBand(bandName);            
            int row = Globals.getReportGrid().getBandLocation(bandName).getLastGridRow();
            int cols = Globals.getReportGrid().getColumnCount();
            if (cols == 0) {
            	// empty report : we will add one column
            	cols = 1;
            }
            for (int i = 0; i < rows; i++) {
                Globals.getReportLayoutPanel().getReportGridPanel().insertRow(band);                
                for (int j=0; j<cols; j++) {
                	BandUtil.insertElement(new BandElement(""), row+i, j);
                }
            }
            Globals.getReportGrid().getSelectionModel().clearSelection();

            ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
            Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.row.insert.before")));
        }
    }

}
