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
package ro.nextreports.designer.grid.event;

import java.util.EventObject;

import ro.nextreports.designer.grid.GridModel;


/**
 * Events corresponding to changes in a GridModel. These events may be:
 * <ul>
 * <li>Data changed in some cells</li>
 * <li>A continuous set of rows/colums was inserted</li>
 * <li>A continuous set of rows/columns was deleted</li>
 * </ul>
 * 
 * @author Decebal Suiu
 */
public class GridModelEvent extends EventObject {
	
	/**
	 * The entire model was changed.
	 */
	public static final int MODEL_CHANGED = 0;
	
	/**
	 * A range of cells were updated.
	 */
	public static final int CELLS_UPDATED = 1;
	
	/**
	 * A continuous set of rows were updated.
	 */
	public static final int ROWS_UPDATED = 2;
	
	/**
	 * A continuous set of rows were inserted into the model.
	 */
	public static final int ROWS_INSERTED = 3;
	
	/**
	 * A continuous set of rows were deleted from the model.
	 */
	public static final int ROWS_DELETED = 4;
	
	/**
	 * A continuous set of columns were updated.
	 */
	public static final int COLUMNS_UPDATED = 5;
	
	/**
	 * A continuous set of columns were inserted into the model.
	 */
	public static final int COLUMNS_INSERTED = 6;
	
	/**
	 * A continuous set of columns were removed from the model.
	 */
	public static final int COLUMNS_DELETED = 7;

    public static final int COLUMNS_RESIZED = 8;

    private int type;
	private int firstRow;
	private int firstColumn;
	private int lastRow;
	private int lastColumn;

	/** Creates a new instance of GridModelEvent */
	public GridModelEvent(GridModel source, int type, int firstRow,
			int firstColumn, int lastRow, int lastColumn) {
		super(source);
		this.type = type;
		this.firstRow = firstRow;
		this.firstColumn = firstColumn;
		this.lastRow = lastRow;
		this.lastColumn = lastColumn;
	}

	/**
	 * Return the number of rows changed
	 */
	public int getRowCount() {
		return (lastRow - firstRow + 1);
	}

	/**
	 * Return the number of columns changed
	 */
	public int getColumnCount() {
		return (lastColumn - firstColumn + 1);
	}

	/**
	 * Return the index of the first row changed
	 */
	public int getFirstRow() {
		return firstRow;
	}

	/**
	 * Returns the index of the last row changed
	 */
	public int getLastRow() {
		return lastRow;
	}

	/**
	 * Return the index of the first column changed
	 */
	public int getFirstColumn() {
		return firstColumn;
	}

	/**
	 * Return the index of the last column changed
	 */
	public int getLastColumn() {
		return lastColumn;
	}

	/**
	 * Return the event type
	 */
	public int getType() {
		return type;
	}
	
}
