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
 * @author Decebal Suiu
 */
public class SelectionRegion {

	private int firstRow;
	private int firstColumn;
	private int lastRow;
	private int lastColumn;
	
	public int getFirstRow() {
		return firstRow;
	}
	
	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	
	public int getFirstColumn() {
		return firstColumn;
	}
	
	public void setFirstColumn(int firstColumn) {
		this.firstColumn = firstColumn;
	}
	
	public int getLastRow() {
		return lastRow;
	}
	
	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}
	
	public int getLastColumn() {
		return lastColumn;
	}
	
	public void setLastColumn(int lastColumn) {
		this.lastColumn = lastColumn;
	}


    public String toString() {
        return "SelectionRegion{" +
                "firstRow=" + firstRow +
                ", firstColumn=" + firstColumn +
                ", lastRow=" + lastRow +
                ", lastColumn=" + lastColumn +
                '}';
    }
}
