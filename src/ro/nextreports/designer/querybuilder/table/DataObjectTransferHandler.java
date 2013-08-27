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
package ro.nextreports.designer.querybuilder.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DesignerTablePanel;

import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;

/**
 * @author Decebal Suiu
 */
public class DataObjectTransferHandler extends TransferHandler {

    private static DropHandler dropHandler = new DropHandler();

    private DataFlavor dataFlavor;
    private Point dragPoint;
    private Point dropPoint;
    private Component dragComponent;
    private Component dropComponent;
    private SelectQuery selectQuery;

    public DataObjectTransferHandler(DataFlavor dataFlavor, SelectQuery selectQuery) {
        super();
        this.dataFlavor = dataFlavor;
        this.selectQuery = selectQuery;
    }

    public Point getDropPoint() {
        return dropPoint;
    }

    public void setDropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
    }

    public Component getDragComponent() {
        return dragComponent;
    }

    public void setDragComponent(Component dragComponent) {
        this.dragComponent = dragComponent;
    }

    public Point getDragPoint() {
        return dragPoint;
    }

    public void setDragPoint(Point dragPoint) {
        this.dragPoint = dragPoint;
    }

    public Component getDropComponent() {
        return dropComponent;
    }

    public void setDropComponent(Component dropComponent) {
        this.dropComponent = dropComponent;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    @Override
    public boolean canImport(JComponent c, DataFlavor[] dataFlavors) {
        if (c.isEnabled() && (c instanceof JTable)) {
            for (DataFlavor dataFlavor : dataFlavors) {
                if (dataFlavor.equals(this.dataFlavor)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void exportAsDrag(JComponent c, InputEvent event, int action) {
        setDragComponent(c);
        setDragPoint(((MouseEvent) event).getPoint());
        super.exportAsDrag(c, event, action);
    }

    protected Transferable createTransferable(JComponent c) {
        Transferable transferable = null;
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            int[] rows = table.getSelectedRows();
            Vector<Object> selectedRows = new Vector<Object>();
            for (int row : rows) {
                Object object = ((DesignerTablePanel.MyTableModel) table.getModel()).getObjectForRow(row);
                selectedRows.add(object);
            }
            transferable = new ListTransferable(selectedRows, dataFlavor);
        }

        return transferable;
    }

    @Override
    public boolean importData(JComponent c, Transferable transferable) {
        if (!canImport(c, transferable.getTransferDataFlavors())) {
            return false;
        }

        try {
            if (getDragComponent() != c) {
                List list = (List) transferable.getTransferData(dataFlavor);
                JTable table = (JTable) c;
                DesignerTablePanel.MyTableModel model = ((DesignerTablePanel.MyTableModel) table.getModel());
                int insertRow;
                if (getDropPoint() != null) {
                    insertRow = table.rowAtPoint(getDropPoint());
                } else {
                    insertRow = table.getSelectedRow();
                }

                for (int i = 0; i < list.size(); i++) {
                    model.insertRow(insertRow + i, (Vector) list.get(i));
                }

                table.getSelectionModel().clearSelection();
                table.getSelectionModel().setSelectionInterval(insertRow, insertRow + list.size() - 1);
                table.requestFocus();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void exportDone(JComponent source, Transferable transferable, int action) {
        if (action != DnDConstants.ACTION_MOVE) {
            return;
        }

        try {
            List list = (List) transferable.getTransferData(dataFlavor);
            JTable table = (JTable) source;
            DesignerTablePanel.MyTableModel model = ((DesignerTablePanel.MyTableModel) table.getModel());
            if (source != getDropComponent()) {
                int index;
                for (int i = 0; i < list.size(); i++) {
                    index = model.getRowForObject(list.get(i));
                    model.removeRow(index);
                }
            } else {                
                int index;
                int insertRow = table.rowAtPoint(getDropPoint());
                for (int i = 0; i < list.size(); i++) {
                    MyRow objectFrom = (MyRow) list.get(i);
                    index = model.getRowForObject(objectFrom);
                    Column from = objectFrom.column;
                    if (insertRow + i == -1) {
                    	continue;
                    }
                    MyRow objectTo = (MyRow) model.getObjectForRow(insertRow + i);
                    Column to = objectTo.column;
                    model.moveRow(index, index, insertRow + i);
                    selectQuery.moveColumn(from, selectQuery.getColumnIndex(to));
                    Globals.getMainFrame().getQueryBuilderPanel().getDesignPanel().updateGroupByItems(index, insertRow+i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DropHandler getDropHandler() {
        return dropHandler;
    }

    static class DropHandler implements DropTargetListener, Serializable {

        private boolean canImport;

        private boolean actionSupported(int action) {
            return (action & DnDConstants.ACTION_COPY_OR_MOVE ) !=
                   DnDConstants.ACTION_NONE;
        }

        public void dragEnter(DropTargetDragEvent event) {
            DataFlavor[] dataFlavors = event.getCurrentDataFlavors();
            JComponent c = (JComponent) event.getDropTargetContext().getComponent();
            TransferHandler transferHandler = c.getTransferHandler();

            if ((transferHandler != null) && transferHandler.canImport(c, dataFlavors)) {
                canImport = true;
            } else {
                canImport = false;
            }

            int dropAction = event.getDropAction();

            if (canImport && actionSupported(dropAction)) {
                event.acceptDrag(dropAction);
            } else {
                event.rejectDrag();
            }
        }

        public void dragOver(DropTargetDragEvent event) {
            int dropAction = event.getDropAction();
            if (canImport && actionSupported(dropAction)) {
                JTable table = (JTable) event.getDropTargetContext().getComponent();
                int row = table.rowAtPoint(event.getLocation());
                table.getSelectionModel().setSelectionInterval(row, row);
                event.acceptDrag(dropAction);
            } else {
                event.rejectDrag();
            }
        }

        public void dragExit(DropTargetEvent event) {
        }

        public void drop(DropTargetDropEvent event) {
            int dropAction = event.getDropAction();
            JComponent c = (JComponent) event.getDropTargetContext().getComponent();
            DataObjectTransferHandler transferHandler = (DataObjectTransferHandler) c.getTransferHandler();

            if (canImport && (transferHandler != null) && actionSupported(dropAction)) {
                event.acceptDrop(dropAction);
                try {
                    Transferable transferable = event.getTransferable();
                    transferHandler.setDropPoint(event.getLocation());
                    transferHandler.setDropComponent(c);
                    event.dropComplete(transferHandler.importData(c, transferable));
                } catch (RuntimeException e) {
                    event.dropComplete(false);
                }
            } else {
                event.rejectDrop();
            }
        }

        public void dropActionChanged(DropTargetDragEvent event) {
            int dropAction = event.getDropAction();
            if (canImport && actionSupported(dropAction)) {
                event.acceptDrag(dropAction);
            } else {
                event.rejectDrag();
            }
        }

    }

}
