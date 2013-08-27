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

import ro.nextreports.designer.grid.event.GridModelListener;

/**
 * Describes a rectangular array of cells. Unlike 
 * <code>javax.swing.table.TableModel</code> this data model is suitable
 * for a symmetrical grid. That is, there is no column (or row) focus.
 * 
 * @author Decebal Suiu
 */
public interface GridModel {
	
	/**
	 * Adds a listener to the list that's notified each time a change to 
	 * the data model occurs.
	 * 
	 * @param listener	the GridModelListener to add
	 */
	public void addGridModelListener(GridModelListener listener);

	/**
	 * Removes a listener from the list that's notified each time a 
	 * change to the data model occurs.
	 * 
	 * @param listener	the GridModelListener to remove
	 */
	public void removeGridModelListener(GridModelListener listener);

	/**
	 * Returns the value for the cell at <code>row</code> and <code>column</code>
	 * 
	 * @param row 		row index of cell
	 * @param column	column index of cell
	 * @return 		the value of the cell
	 */
	public Object getValueAt(int row, int column);

	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code>
	 * is editable. Otherwise, <code>setValueAt</code> on the cell will not change 
	 * the value of that cell.
	 * 
	 * @param row		row index of cell
	 * @param column	column index of cell
	 * @return		true if the cell is editable
	 */
	public boolean isCellEditable(int row, int column);

	/**
	 * Sets the value for the cell at <code>row</code> and <code>column</code>
	 * to <code>value</code>
	 * 
	 * @param value		new cell value
	 * @param row		row index of cell
	 * @param column	column index of cell     
	 */
	public void setValueAt(Object value, int row, int column);

	/**
	 * Returns the number of rows in the model. 
	 * 
	 * @return	the number of rows in the model
	 */
	public int getRowCount();

	/**
	 * Return the number of columns in the model.
	 * 
	 * @return	the number of columns in the model
	 */
	public int getColumnCount();
	
}
