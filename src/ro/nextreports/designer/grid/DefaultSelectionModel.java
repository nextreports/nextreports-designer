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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashSet;

/**
 * Default implementation of <code>SelectionModel</code>.
 * 
 * @author Decebal Suiu
 */
public class DefaultSelectionModel extends AbstractSelectionModel {
	
	private LinkedHashSet<Cell> selectedCells;
	private LinkedHashSet<Integer> selectedRows;

	public DefaultSelectionModel() {
		selectedCells = new LinkedHashSet<Cell>();
		selectedRows = new LinkedHashSet<Integer>();
	}

	public boolean isSelected(int row, int column) {
		for (Cell cell : selectedCells) {
			if ((row == cell.getRow()) && (column == cell.getColumn())) {
				return true;
			}
		}		
		return false;
	}

    public boolean isRowSelected(int row) {
        for (Cell cell : selectedCells) {
			if (row == cell.getRow()) {
                return true;
            }
        }
        return false;
    }

    public boolean isColumnSelected(int column) {
        for (Cell cell : selectedCells) {
			if (column == cell.getColumn()) {
                return true;
            }
        }
        return false;
    }

    public void clearSelection() {
		selectedCells.clear();
		selectedRows.clear();
	}

    public void emptySelection() {
        selectedCells.clear();
        selectedRows.clear();
        fireEmptySelection();
    }

    public void addRootSelection() {
        fireSelectionChanged(true);
    }

    public void addSelectionCell(Cell cell) {    	
        selectedCells.add(cell);
        fireSelectionChanged();
	}
    
    public void addSelectionRow(int row) {    	
        selectedRows.add(row);
        fireSelectionChanged();
	}

    public void removeSelectionCell(Cell cell) {
        selectedCells.remove(cell);
        fireSelectionChanged();
    }
    
    public void removeSelectionRow(int row) {
        selectedRows.remove(row);
        fireSelectionChanged();
    }

    public void addSelectionCells(List<Cell> cells) {    	
        selectedCells.addAll(cells);
        fireSelectionChanged();
	}
    
    public void addSelectionRows(List<Integer> rows) {    	
    	selectedRows.addAll(rows);    		
        fireSelectionChanged();
	}
	
	public List<Cell> getSelectedCells() {
        List<Cell> cells = new ArrayList<Cell>();
        cells.addAll(selectedCells);
        List<Cell> result = clone(cells);
        Collections.sort(result, new Comparator<Cell>() {
        	
            public int compare(Cell c1, Cell c2) {
                 if (c1.getRow() == c2.getRow()) {
                     if (c1.getColumn() == c2.getColumn()) {
                         return 0;
                     } else {
                         return c1.getColumn() - c2.getColumn();
                     }
                 } else {
                     return c1.getRow() - c2.getRow();
                 }
            }
            
        });

        return result;
    }

    private List<Cell> clone(List<Cell> list) {
        List<Cell> result = new ArrayList<Cell>();
        for(Cell cell : list) {
            Cell newCell = new Cell(cell.getRow(), cell.getColumn());
            result.add(cell);
        }
        return result;
    }

    public Cell getLastSelectedCell() {        
        if (selectedCells.size() != 0) {            
            List<Cell> cells = new ArrayList<Cell>();
            cells.addAll(selectedCells);
            return cells.get(cells.size()-1);
		}
        return null;
    }
	
    public Cell getSelectedCell() {
		if (selectedCells.size() != 0) {
            List<Cell> cells = new ArrayList<Cell>();
            cells.addAll(selectedCells);
            return cells.get(0);
		} 
		
		return null;
	}

    public void setFirstCell(Cell cell) {
        selectedCells.remove(cell);
        LinkedHashSet<Cell> result = new LinkedHashSet<Cell>();
        result.add(cell);
        result.addAll(selectedCells);
        selectedCells = result;
    }

    public void setLastCell(Cell cell) {
        selectedCells.remove(cell);
        selectedCells.add(cell);        
    }
    
    public boolean isFullRowSelected(int row) {
    	for (Integer i : selectedRows) {
    		if (row == i.intValue()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public List<Integer> getSelectedRows() {
    	return new ArrayList<Integer>(selectedRows);
    }
        
	
}
