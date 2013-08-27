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

import java.util.List;

import ro.nextreports.designer.grid.event.SelectionModelListener;


/**
 * <code>SelectionModel</code> stores selection information for
 * a grid of cells.
 * 
 * @author Decebal Suiu
 */
public interface SelectionModel {
	
	/**
	* Add listener to model
	*/
	public void addSelectionModelListener(SelectionModelListener listener);

	/**
	 * Remove listener from model
	 */
	public void removeSelectionModelListener(SelectionModelListener listener);

	/**
	 * Returns true if the specified cell is selected
	 */
	public boolean isSelected(int row, int column);

    public boolean isRowSelected(int row);

    public boolean isColumnSelected(int column);

    /**
	 * Clear selection, but do not notify listeners
	 */
	public void clearSelection();

    /**
     * Empty selection and notify listeners
     */
    public void emptySelection();

    /**
	 * Returns true if the value is undergoing a series of changes.
	 */
	public boolean getValueIsAdjusting();

	/**
	 * This property is true if upcoming changes to the value of the 
	 * model should be considered a single event.
	 */
	public void setValueIsAdjusting(boolean isAdjusting);

    public void addRootSelection();

    public void addSelectionCell(Cell cell);

    public void removeSelectionCell(Cell cell);

    public void addSelectionCells(List<Cell> cells);
       
    public Cell getSelectedCell();
    
	public List<Cell> getSelectedCells();

    public Cell getLastSelectedCell();

    public void setFirstCell(Cell cell);

    public void setLastCell(Cell cell);
    
    public void addSelectionRow(int row);

    public void removeSelectionRow(int row);

    public void addSelectionRows(List<Integer> rows);
    
    public boolean isFullRowSelected(int row);
    
    public List<Integer> getSelectedRows();
	
}
