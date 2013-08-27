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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * @author Decebal Suiu
 */
public class CopyTableMouseAdapter extends MouseAdapter {

    private boolean popupShown;
    private int row;
    private int col;
    private JTable table;
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public CopyTableMouseAdapter(JTable table) {
        super();
        this.table = table;
    }

    private void showPopup(int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        Point point = new Point(x, y);
        row = table.rowAtPoint(point);
        col = table.columnAtPoint(point);
        
        // add copy table action
        popup.add(new AbstractAction(I18NSupport.getString("copy.table")) {
            
            public void actionPerformed(ActionEvent ev) {
                copy();
            }
            
        });
        
        // add copy column action        
        if (col >= 0) {
            popup.add(new AbstractAction(I18NSupport.getString("copy.column")) {
                
                public void actionPerformed(ActionEvent ev) {
                    copyColumn();
                }
                
            });
        }
                
        if (row >= 0) {
            // add copy row action
            popup.add(new AbstractAction(I18NSupport.getString("copy.row")) {
                
                public void actionPerformed(ActionEvent ev) {
                    copyRow();
                }
                
            });
            
            // add copy cell action
            popup.add(new AbstractAction(I18NSupport.getString("copy.cell")) {
                
                public void actionPerformed(ActionEvent ev) {
                    copyCell();
                }
                
            });
        }
        
        if (table.getSelectedRowCount() > 0) {
            // add copy selection action
            popup.add(new AbstractAction(I18NSupport.getString("copy.selection")) {
                
                public void actionPerformed(ActionEvent ev) {
                    copySelection();
                }
                
            });
        }
        
        popup.show(table, x, y);
    }

    private void copyCell() {
        Object value = table.getValueAt(row, col);
        String valueAsString = "";
        if (value != null) {
            valueAsString = getFormattedValue(value).toString();
        }
        clipboard.setContents(new TableContentsTransferable(valueAsString), null);
    }

    private void copyRow() {
        int colCount = table.getColumnCount();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < colCount; i++) {
            Object value = table.getValueAt(row, i);
            if (value != null) {            	
                sb.append(getFormattedValue(value));
            }
            sb.append("\t");
        }
        clipboard.setContents(new TableContentsTransferable(sb.toString()), null);
    }

    private void copyColumn() {
        if (col < 0) {
            return;
        }
        
        int rowCount = table.getRowCount();
        StringBuffer sb = new StringBuffer();
        sb.append(table.getColumnName(col));
        sb.append("\r\n");
        for (int i = 0; i < rowCount; i++) {
            Object value = table.getValueAt(i, col);
            if (value != null) {
                sb.append(getFormattedValue(value));
            }
            sb.append("\r\n");
        }
        clipboard.setContents(new TableContentsTransferable(sb.toString()), null);
    }

    private void copy() {
        int rowCount = table.getRowCount();
        int colCount = table.getColumnCount();
        StringBuffer sb = new StringBuffer();
        for (int col = 0; col < colCount; col++) {
            sb.append(table.getColumnName(col)).append("\t");
        }
        sb.append("\r\n");
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                Object value = table.getValueAt(row, col);
                if (value != null) {
                    sb.append(getFormattedValue(value));
                }
                sb.append("\t");
            }
            sb.append("\r\n");
        }
        clipboard.setContents(new TableContentsTransferable(sb.toString()), null);
    }

    private void copySelection() {
        int[] selectedRows = table.getSelectedRows();
        int colCount = table.getColumnCount();
        StringBuffer sb = new StringBuffer();
        for (int col = 0; col < colCount; col++) {
            sb.append(table.getColumnName(col)).append("\t");
        }
        sb.append("\r\n");
        for (int row = 0; row < selectedRows.length; row++) {
            for (int col = 0; col < colCount; col++) {
                Object value = table.getValueAt(selectedRows[row], col);
                if (value != null) {
                    sb.append(getFormattedValue(value));
                }
                sb.append("\t");
            }
            sb.append("\r\n");
        }
        clipboard.setContents(new TableContentsTransferable(sb.toString()), null);
    }
    
    public void mousePressed(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            showPopup(ev.getX(), ev.getY());
            popupShown = true;
        }
    }

    public void mouseReleased(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            if (!popupShown) {
                showPopup(ev.getX(), ev.getY());
                popupShown = false;
            }
        }
    }
    
    class TableContentsTransferable implements Transferable {

        private String data;

        public TableContentsTransferable(String data) {
            this.data = data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.stringFlavor);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (!flavor.equals(DataFlavor.stringFlavor)) {
                return null;
            }
            
            return data;
        }
        
    }
    
    protected Object getFormattedValue(Object value) {    	
    	if (value instanceof Double) {
    		return NumberFormat.getInstance().format((Double)value);
    	} else if ( value instanceof Date) {
    		return DateFormat.getDateInstance().format((Date)value);
    	}
    	return value;
    }

}
