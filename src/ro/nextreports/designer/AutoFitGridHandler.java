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

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.event.GridModelEvent;
import ro.nextreports.designer.grid.event.GridModelListener;
import ro.nextreports.designer.grid.event.SelectionModelEvent;

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.Padding;

/**
 * @author Decebal Suiu
 */
class AutoFitGridHandler implements GridModelListener {

    private ReportGrid grid;
    private Map<Integer, Map<Integer, Integer>> columnSizeCache;
    private Map<Integer, Map<Integer, Integer>> rowSizeCache;

    public AutoFitGridHandler(ReportGrid grid) {
        this.grid = grid;
        columnSizeCache = new HashMap<Integer, Map<Integer, Integer>>();
        rowSizeCache = new HashMap<Integer, Map<Integer, Integer>>();
    }

    public void clearCache() {
        columnSizeCache.clear();
        rowSizeCache.clear();
    }

    public void gridChanged(GridModelEvent event) {
        int eventType = event.getType();
        if (eventType == GridModelEvent.CELLS_UPDATED) {
            onCellsUpdated(event);
        } else if (eventType == GridModelEvent.COLUMNS_DELETED) {
            onColumnsDeleted(event);
        } else if (eventType == GridModelEvent.ROWS_DELETED) {
            onRowsDeleted(event);
        } else if (eventType == GridModelEvent.ROWS_INSERTED) {
            onRowsInserted(event);
        }
    }

    private void onCellsUpdated(GridModelEvent event) {

        int column = event.getFirstColumn();
        int row = event.getFirstRow();        

        BandElement element = grid.getBandElement(row, column);
        //System.out.println("row="+row + "  column="+column + "  element="+element);

        // handle column size
        if (LayoutHelper.getReportLayout().isUseSize()) {
            grid.getColumnHeaderModel().setSize(column, LayoutHelper.getReportLayout().getColumnsWidth().get(column));            
        }

        if (element == null) { // possible a clear cell !?

            // handle column size
            if (!LayoutHelper.getReportLayout().isUseSize()) {              
                Map<Integer, Integer> rowsMap = getRowsMap(column);
                rowsMap.put(row, JGrid.DEFAULT_COLUMN_WIDTH);
                grid.getColumnHeaderModel().setSize(column, Collections.max(rowsMap.values()));
            }

            // handle row size
            Map<Integer, Integer> columnsMap = getColumnsMap(row);
            columnsMap.put(column, JGrid.DEFAULT_ROW_HEIGHT);
            grid.getRowHeaderModel().setSize(row, Collections.max(columnsMap.values()));

            return;
        }

        // in case that property value is deleted from PropertyPanel (close button)
        // set a default value
        setDefaultProperties(element);

        // retrieves a font metrics from the element font
        FontMetrics fontMetrics = grid.getFontMetrics(element.getFont());

        // if the cell is inside a span , divide the width to the number of cells in the span
        // and update all columns / rows
        // todo on start span model is not initialized!!!
        CellSpan span = grid.getSpanModel().getSpanOver(row, column);
        //System.out.println("span="+span);

        // handle column size
        // TODO calculate insets (now it's 8)
        if (!LayoutHelper.getReportLayout().isUseSize()) {
            for (int i = column; i < span.getColumnCount() + column; i++) {
                int columnSize = fontMetrics.stringWidth(element.getText()) / span.getColumnCount() + 8;
                BandElement elementC = grid.getBandElement(row, i);
                if (elementC != null) {
                    Padding padding = elementC.getPadding();
                    if (padding != null) {
                        columnSize += padding.getLeft() + padding.getRight();
                    }
                }
                if (columnSize < JGrid.DEFAULT_COLUMN_WIDTH) {
                    columnSize = JGrid.DEFAULT_COLUMN_WIDTH;
                }
                Map<Integer, Integer> rowsMap = getRowsMap(i);
                rowsMap.put(row, columnSize);
                //System.out.println("i="+i + "  textSize=" + fontMetrics.stringWidth(element.getText()) +  " colSize="+columnSize);
                grid.getColumnHeaderModel().setSize(i, Collections.max(rowsMap.values()));
            }
        }

        // TODO calculate insets (now it's 4)
        // handle row size
        for (int i = row; i < span.getRowCount() + row; i++) {
            int rowSize = fontMetrics.getHeight() / span.getRowCount() + 4;
            BandElement elementC = grid.getBandElement(i, column);
            if (elementC != null) {
                Padding padding = elementC.getPadding();
                if (padding != null) {
                    rowSize += padding.getTop() + padding.getBottom();
                }
            }
            if (rowSize < JGrid.DEFAULT_ROW_HEIGHT) {
                rowSize = JGrid.DEFAULT_ROW_HEIGHT;
            }
            Map<Integer, Integer> columnsMap = getColumnsMap(i);
            columnsMap.put(column, rowSize);
            grid.getRowHeaderModel().setSize(i, Collections.max(columnsMap.values()));
        }

        notifyPropertyPanel();
    }

    private void onRowsInserted(GridModelEvent event) {
        int firstRow = event.getFirstRow();
        int lastRow = event.getLastRow();

        for (int i = firstRow; i < grid.getRowCount(); i++) {
            Map<Integer, Integer> columnsMap = rowSizeCache.remove(i);
            rowSizeCache.put(i + lastRow - firstRow + 1, columnsMap);
        }
    }

    private void onRowsDeleted(GridModelEvent event) {
        int firstRow = event.getFirstRow();
        int lastRow = event.getLastRow();

        for (int i = firstRow; i <= lastRow; i++) {
            rowSizeCache.remove(i);
        }

        for (int i = lastRow + 1; i < grid.getRowCount(); i++) {
            Map<Integer, Integer> columnsMap = rowSizeCache.remove(i);
            rowSizeCache.put(i - lastRow + firstRow - 1, columnsMap);
        }
    }

    private void onColumnsDeleted(GridModelEvent event) {
        int firstColumn = event.getFirstColumn();
        int lastColumn = event.getLastColumn();

        for (int i = lastColumn; i >= firstColumn; i--) {
            columnSizeCache.remove(i);
        }
    }

    private void setDefaultProperties(BandElement element) {
        if (element.getFont() == null) {
            element.setFont(UIManager.getDefaults().getFont("Label.font"));
        }

        if (element.getBackground() == null) {
            element.setBackground(Color.WHITE);
        }

        if (element.getForeground() == null) {
            element.setForeground(Color.BLACK);
        }

        if (element.getPadding() == null) {
            element.setPadding(new Padding(0, 0, 0, 0));
        }

        if (element.getBorder() == null) {
            element.setBorder(new Border(0, 0, 0, 0));
        }
    }

    private void notifyPropertyPanel() {
        SelectionModelEvent selectionEvent = new SelectionModelEvent(Globals.getReportGrid().getSelectionModel(), false);
        Globals.getReportDesignerPanel().getPropertiesPanel().selectionChanged(selectionEvent);
    }

    private Map<Integer, Integer> getRowsMap(int column) {
        Map<Integer, Integer> rowsMap = columnSizeCache.get(column);
        if (rowsMap == null) {
            rowsMap = new HashMap<Integer, Integer>();
            columnSizeCache.put(column, rowsMap);
        }

        return rowsMap;
    }

    private Map<Integer, Integer> getColumnsMap(int row) {
        Map<Integer, Integer> columnsMap = rowSizeCache.get(row);
        if (columnsMap == null) {
            columnsMap = new HashMap<Integer, Integer>();
            rowSizeCache.put(row, columnsMap);
        }

        return columnsMap;
    }

}
