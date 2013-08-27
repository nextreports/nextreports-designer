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

/**
 * @author Decebal Suiu
 */
public class BandLocation {

	private int firstGridRow;
	private int rowCount;
	private int lastGridRow;
	
	public BandLocation() {
	}
	
	public BandLocation(int firstGridRow, int rowCount) {
		setFirstGridRow(firstGridRow);
		setRowCount(rowCount);
	}
	
	public int getFirstGridRow() {
		return firstGridRow;
	}
	
	public void setFirstGridRow(int firstGridRow) {
		this.firstGridRow = firstGridRow;
	}
	
	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		this.lastGridRow = firstGridRow + rowCount;
	}

	public int getLastGridRow() {
		return lastGridRow;
	}
	
	public void setLastGridRow(int lastGridRow) {
		this.lastGridRow = lastGridRow;
	}

	public boolean containsGridRow(int gridRow) {
		if (gridRow < firstGridRow) {
			return false;
		}
		
		if (gridRow >= lastGridRow) {
			return false;
		}
		
		return true;
	}
	
	public int getRow(int gridRow) {
		if (containsGridRow(gridRow)) {
			return gridRow - firstGridRow;
		}
		
		return -1;
	}

	public void adjustRowCount(int value) {
		setRowCount(rowCount + value);
	}
	
	public void adjustBorder(int value) {
		setFirstGridRow(firstGridRow + value);
		setLastGridRow(lastGridRow + value);
	}

	@Override
    public String toString() {
        return "BandLocation{" +
                "firstGridRow=" + firstGridRow +
                ", rowCount=" + rowCount +
                ", lastGridRow=" + lastGridRow +
                '}';
    }
    
}
