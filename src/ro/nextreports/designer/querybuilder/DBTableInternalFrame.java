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
package ro.nextreports.designer.querybuilder;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.datatransfer.InternalFrameTransferable;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.Table;

/**
 * @author Decebal Suiu
 */
public class DBTableInternalFrame extends JInternalFrame {

    private Table table;
    private JScrollPane scroll;

    private DefaultListModel model = new DefaultListModel();
    private ColumnsListBox columnsListBox = new ColumnsListBox();
    private DBTablesDesktopPane desktop;

    // variable used for selection :
    // for join drag and drop when start dragging in the source (gestureStarted=true) the selection will not be modified
    boolean gestureStarted = false;

    public DBTableInternalFrame(final DBTablesDesktopPane desktop, final Table table, Map<String, List<CheckListItem>> itemMap) {
        super(table.getAlias() + " (" + table.getSchemaName() +  "." + table.getName() + ")", true, true, false, false);
        this.desktop = desktop;
        this.table = table;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        customizeTable();

        String key = table.getAlias();
        if (key == null) {
            key = table.getName();
        }
        List<CheckListItem> items = itemMap.get(key);
        for (CheckListItem i : items) {
            model.addElement(i);
        }

        columnsListBox.setModel(model);

        addComponents();

        setFrameIcon(ImageUtil.TABLE_IMAGE_ICON);

        addInternalFrameListener(new InternalFrameAdapter() {

            public void internalFrameClosing(InternalFrameEvent e) {
                Collection<JoinLine> joinLines = DBTableInternalFrame.this.desktop
                        .getJoinLinesForInternalFrame(DBTableInternalFrame.this);

                boolean remove = false;

                if (joinLines.size() > 0) {
                    String[] message = {
                            I18NSupport.getString("internal.frame.active.joins"),
                            I18NSupport.getString("internal.frame.delete.joins"),
                            I18NSupport.getString("internal.frame.close") };
                    int option = JOptionPane.showOptionDialog(Globals
                            .getMainFrame(), message, I18NSupport.getString("internal.frame.confirm.close"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if (option == JOptionPane.YES_OPTION) {
                        remove = true;
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        DBTableInternalFrame.this.desktop.removeJoinLines(joinLines);
                    }
                } else {
                    remove = true;
                    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                }

                if (remove) {
                    // when closing a table
                    // must remove match criteria
                    // must remove group by columns
                    // must remove the selected columns of that table from model and from select query
                    DBTableInternalFrame.this.desktop.getQuerBuilderPanel().getSelectQuery().removeMatchCriterias(table.getAlias());
                    DBTableInternalFrame.this.desktop.getQuerBuilderPanel().getSelectQuery().removeOrMatchCriterias(table.getAlias(), 0);
                    DBTableInternalFrame.this.desktop.getQuerBuilderPanel().getSelectQuery().removeGroupByColumns(table.getAlias());
                    int size = model.getSize();
                    for (int i = 0; i < size; i++) {
                        CheckListItem item = (CheckListItem) model.getElementAt(i);
                        if (item.isSelected()) {
                            desktop.getQuerBuilderPanel().getDesignPanel().removeColumn((Column) item.getObject());
                        }
                    }

                }
            }

        });        
        columnsListBox.setFrame(this);
        ((BasicInternalFrameUI) getUI()).getNorthPane().setToolTipText(table.getAlias() + " (" + table.getSchemaName() +  "." + table.getName() + ")");
    }

    private void customizeTable() {
        columnsListBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnsListBox.setSelectionModel(new DefaultListSelectionModel() {
            public void setSelectionInterval(int index0, int index1) {
                if (!gestureStarted) {
                    super.setSelectionInterval(index0, index1);
                }
            }
        });

        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(columnsListBox,
                DnDConstants.ACTION_MOVE, new TableDragGestureListener());
        columnsListBox.setDropTarget(new DropTarget(columnsListBox, DnDConstants.ACTION_MOVE,
                new TableDropTargetListener()));
    }

    private void addComponents() {
        scroll = new JScrollPane(columnsListBox);
        getContentPane().add(scroll, BorderLayout.CENTER);

        JScrollBar vScrollBar = scroll.getVerticalScrollBar();
        vScrollBar.addAdjustmentListener(new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent e) {
                desktop.repaint();
            }

        });
    }

    public Column getSelectedColumn() {
        return (Column) ((CheckListItem) columnsListBox.getSelectedValue()).getObject();
    }

    public int getSelectedRow() {
        return columnsListBox.getSelectedIndex();
    }

    public Table getTable() {
        return table;
    }

    public void tableColumnRemoved(String columnName) {
        for (int row = 0; row < model.getSize(); row++) {
            CheckListItem item = (CheckListItem) model.getElementAt(row);
            String value = item.getText();
            if (value.equals(columnName)) {
                try {
                    setSelected(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                item.setSelected(false);
                model.setElementAt(item, row);
            }
        }
    }

    public void allTableColumnsRemoved() {
        for (int row = 0; row < model.getSize(); row++) {
            CheckListItem item = (CheckListItem) model.getElementAt(row);
            boolean checked = item.isSelected();
            if (checked) {
                item.setSelected(false);
                model.setElementAt(item, row);
            }
        }
    }

    public int getJoinY(int row) {
        // y-ul pentru o linie, raportat la dimensiunea tabelei
        int yForRow = getYForRow(row);

        // y-ul partii vizibile a tabelei in viewport, raportat la y-ul tabelei
        int viewRectY = scroll.getViewport().getViewRect().y;

        // inaltimea titlebar-ului ferestrei interne
        int titlebarHeight = ((BasicInternalFrameUI) getUI()).getNorthPane().getHeight();

        // distanta dintre bordura ferestrei si continutul ei
        int borderInsetsTop = getBorder().getBorderInsets(this).top;

        // inaltimea partii vizibile a tabelei
        int extentSizeHeight = scroll.getViewport().getExtentSize().height;

        // daca linia din tabela nu e vizibila si e mai sus decat liniile din
        // viewport, join-ul va porni de la baza titlebar-ului
        int y = borderInsetsTop + titlebarHeight;

        if ((viewRectY <= yForRow) && (yForRow <= (viewRectY + extentSizeHeight))) {
            // linia e vizibila, adaug offset-ul pana la ea
            y += (yForRow - viewRectY);
        }

        if (yForRow > (viewRectY + extentSizeHeight)) {
            // linia nu e vizibila si e mai jos decat liniile din viewport,
            // join-ul va porni de la baza ferestrei
            y += extentSizeHeight;
        }

        return y;
    }

    private int getYForRow(int row) {
        int rowHeight = columnsListBox.getFixedCellHeight();
        int rowCount = columnsListBox.getVisibleRowCount();
        int y = 0;
        for (int i = 0; i < model.getSize(); i++) {
            if (i == row) {
                // am gasit linia pentru join, adaug jumatate din inaltimea ei
                y += (rowHeight / 2);
                break;
            }

            // nu am gasit inca linia pentru join, mai adaug inaltimea unei
            // linii
            y += rowHeight;
        }

        return y;
    }

    private class TableDragGestureListener implements DragGestureListener {

        public void dragGestureRecognized(DragGestureEvent dge) {
            Transferable transferable = new InternalFrameTransferable(DBTableInternalFrame.this);
            dge.startDrag(DragSource.DefaultLinkNoDrop, transferable, new TableDragSourceListener());
        }

    }

    private class TableDragSourceListener extends DragSourceAdapter {

        public void dragEnter(DragSourceDragEvent dsde) {
            // Point point = dsde.getLocation();
            // if (columnsListBox.getBounds().contains(point)) {
            // dsde.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
            // return;
            // }
            gestureStarted = true;            
            if ((dsde.getDropAction() & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultLinkDrop);
            }
        }

        public void dragExit(DragSourceEvent dse) {            
            dse.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            gestureStarted = false;
            if (dsde.getDropSuccess()) {
                int row = columnsListBox.getSelectedIndex();
//                System.out.println("row = " + row);
                CheckListItem item = (CheckListItem) model.getElementAt(row);
//                System.out.println("item = " + item.getText());
//                item.setSelected(true);
                model.setElementAt(item, row);
            }
        }

    }

    private class TableDropTargetListener extends DropTargetAdapter {

        public void dragEnter(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {

                selectRowAtPoint(dtde.getLocation());
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {                
                selectRowAtPoint(dtde.getLocation());
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void drop(DropTargetDropEvent dtde) {
            gestureStarted = false;
            if ((dtde.getDropAction() & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                Transferable t = dtde.getTransferable();

                try {
                    DBTableInternalFrame iFrame = (DBTableInternalFrame) t
                            .getTransferData(InternalFrameTransferable.DATA_FLAVOR);
                    if (iFrame != DBTableInternalFrame.this) {
                        JoinLine joinLine = new JoinLine(iFrame, iFrame
                                .getSelectedRow(), DBTableInternalFrame.this,
                                columnsListBox.getSelectedIndex());
                        desktop.addJoinLine(joinLine);
                        desktop.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dtde.dropComplete(true);
            }
        }

        private boolean isDragOk(DropTargetDragEvent dtde) {
            if (dtde.getCurrentDataFlavors().length < 1) {
                return false;
            }

            if (dtde.getCurrentDataFlavors()[0]
                    .equals(InternalFrameTransferable.DATA_FLAVOR)) {
                return true;
            }

            return false;
        }

        private void selectRowAtPoint(Point point) {
            int rowAtPoint = columnsListBox.locationToIndex(point);
            columnsListBox.setSelectionInterval(rowAtPoint, rowAtPoint);
        }

    }

    public DBTablesDesktopPane getDesktopPane() {
        return desktop;
    }

    public void selectColumns(List<Column> columns) {
        columnsListBox.selectColumns(columns);
    }

    public void selectColumn(Column column) {
        columnsListBox.selectColumn(column);
    }

    public void selectRow(int index) {
        columnsListBox.selectRow(index);
    }

    public int getIndex(Column column) {
        return columnsListBox.getIndex(column);
    }

    public int getIndex(String tableName, String columnName) {
        return columnsListBox.getIndex(tableName, columnName);
    }

    public Column getColumn(int index) {
        return columnsListBox.getColumn(index);
    }

}
