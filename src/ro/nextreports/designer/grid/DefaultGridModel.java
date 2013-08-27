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
package ro.nextreports.designer.grid;

/**
 * This is an implementation of <code>GridModel</code> that is backed
 * by arrays.
 * 
 * @author Decebal Suiu
 */
public class DefaultGridModel extends AbstractGridModel implements ResizableGrid {
	
    private Object[][] data;
    private boolean[][] editable;
    private int rowSize;
    private int columnSize;
    
    public DefaultGridModel(int rows, int columns) {
        data = new Object[rows][columns];
        editable = new boolean[rows][columns];
        rowSize = rows;
        columnSize = columns;
    }
    
    public int getRowCount() {
        return rowSize;
    }
    
    public int getColumnCount() {
        return columnSize;
    }
    
    public Object getValueAt(int row, int column) {
        return data[row][column];
    }
    
    public boolean isCellEditable(int row, int column) {
        return editable[row][column];
    }
    
    public void setValueAt(Object value, int row, int column) {
        data[row][column] = value;
        fireGridCellUpdated(row, column);
    }
    
    public void setCellEditable(boolean cellEditable, int row, int column) {
        editable[row][column] = cellEditable;
    }
    
    public void insertRows(int row, int rowCount) {
        Object newData[][] = new Object[rowSize + rowCount][columnSize];
        System.arraycopy(data, 0, newData, 0, row);
        System.arraycopy(data, row, newData, row + rowCount, rowSize - row);
        
        boolean newEditable[][] = new boolean[rowSize + rowCount][columnSize];
        System.arraycopy(editable, 0, newEditable, 0, row);
        System.arraycopy(editable, row, newEditable, row + rowCount, rowSize - row);        
        
        // Update grid model
        rowSize += rowCount;
        data = newData;
        editable = newEditable;
        
        fireGridRowsInserted(row, (row + rowCount - 1));
    }
    
    public void removeRows(int row, int rowCount) {
        Object newData[][] = new Object[rowSize - rowCount][columnSize];
        System.arraycopy(data, 0, newData, 0, row);
        System.arraycopy(data, row + rowCount, newData, row, rowSize - rowCount - row);
        
        boolean newEditable[][] = new boolean[rowSize - rowCount][columnSize];
        System.arraycopy(editable, 0, newEditable, 0, row);
        System.arraycopy(editable, row + rowCount, newEditable, row, rowSize - rowCount - row);
        
        // Update grid model
        rowSize -= rowCount;
        data = newData;
        editable = newEditable;
        
        fireGridRowsDeleted(row, (row + rowCount - 1));        
    }
    
    public void insertColumns(int column, int columnCount) {
        Object newData[][] = new Object[rowSize][columnSize + columnCount];
        boolean newEditable[][] = new boolean[rowSize][columnSize + columnCount];
        // copy contents of each row into newData
        for (int row = 0; row < rowSize; row++) {
            System.arraycopy(data[row], 0, newData[row], 0, column);
            System.arraycopy(data[row], column, newData[row], column + columnCount, columnSize - column);
            
            System.arraycopy(editable[row], 0, newEditable[row], 0, column);
            System.arraycopy(editable[row], column, newEditable[row], column + columnCount, columnSize - column);
        }
        
        // Update grid model
        columnSize += columnCount;
        data = newData;
        editable = newEditable;
        
        fireGridColumnsInserted(column, (column + columnCount - 1));
    }
    
    public void removeColumns(int column, int columnCount) {
        Object newData[][] = new Object[rowSize][columnSize - columnCount];
        boolean newEditable[][] = new boolean[rowSize][columnSize - columnCount];
        // copy contents of each row into newData
        for (int row = 0; row < rowSize; row++) {
            System.arraycopy(data[row], 0, newData[row], 0, column);
            System.arraycopy(data[row], column + columnCount, newData[row], column, columnSize - columnCount - column);
            
            System.arraycopy(editable[row], 0, newEditable[row], 0, column);
            System.arraycopy(editable[row], column + columnCount, newEditable[row], column, columnSize - columnCount - column);
        }
        
        // Update grid model
        columnSize -= columnCount;
        data = newData;
        editable = newEditable;        
        
        fireGridColumnsDeleted(column, (column + columnCount - 1));        
    }
    
}
