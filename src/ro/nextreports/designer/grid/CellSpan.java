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

import java.io.Serializable;

/**
 * This class embodies the idea that cells can be merged. That is a cell can span
 * over cells to the right and to the bottom. 
 * The cell which is displayed by the span is  referred to as the anchor cell.
 * 
 * @author Decebal Suiu
 */
public class CellSpan implements Serializable {
	
	private static final long serialVersionUID = 1821008144473875413L;
	
	private int anchorRow;
	private int anchorColumn;
	private int rowCount;
	private int columnCount;
	
	/**
	 * Create a <code>CellSpan</code> that represents a span at
	 * <code>anchorRow</code> and <code>anchorColumn</code>
	 * that spans over the following <code>rowCount</code> rows and 
	 * <code>columnCount</code> columns.
	 */
	public CellSpan(int anchorRow, int anchorColumn, int rowCount, int columnCount) {
		this.anchorRow = anchorRow;
		this.anchorColumn = anchorColumn;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}
	
	/**
	 * Return the anchor row.
	 * 
	 * @return 	row index of anchor cell.
	 */
	public int getRow() {
		return anchorRow;
	}
	
	/**
	 * Return the anchor column.
	 * 
	 * @return	column index of anchor cell.
	 */
	public int getColumn() {
		return anchorColumn;
	}
	
	/**
	 * Return the number of rows that the span covers.
	 * 
	 * @return 	number of rows included in the span.
	 */
	public int getRowCount() {
		return rowCount;
	}
	
	/**
	 * Return the number of columns that the span covers.
	 * 
	 * @return 	number of columns included in the span.
	 */
	public int getColumnCount() {
		return columnCount;
	}
	
	/**
	 * Return the first row that the span covers.
	 * 
	 * @return	the anchor row
	 */
	public int getFirstRow() {
		return anchorRow;
	}
	
	/**
	 * Return the last row that the span covers.
	 * 
	 * @return	the last row of the span.
	 */
	public int getLastRow() {
		return (anchorRow + rowCount - 1);
	}
	
	/**
	 * Return the first column that the span covers.
	 * 
	 * @return	the anchor column
	 */
	public int getFirstColumn() {
		return anchorColumn;
	}
	
	/**
	 * Return the last column that the span covers.
	 * 
	 * @return	the last column of the span.
	 */
	public int getLastColumn() {
		return (anchorColumn + columnCount - 1);
	}
	
	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code>
	 * is part of the span. That is, the specified cell is the anchor cell or is hidden by
	 * the span.
	 */
	public boolean containsCell(int row, int column) {
		return (row >= getFirstRow() && row <= getLastRow() &&
					column >= getFirstColumn() && column <= getLastColumn());
	}
    
    /**
     * Returns true if the span is atomic (ie. getRowCount() == 1 && getColumnCount() == 1)
     */
    public boolean isAtomic() {
        return (rowCount == 1) && (columnCount == 1);
    }


    public String toString() {
        return "CellSpan{" +
                "anchorRow=" + anchorRow +
                ", anchorColumn=" + anchorColumn +
                ", rowCount=" + rowCount +
                ", columnCount=" + columnCount +
                '}';
    }
}
