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
 * This interface defines the methods to implement for models (
 * <code>StyleModel</code>, <code>HeaderModel</code>, <code>SpanModel</code>,
 * <code>SelectionModel</code>) that need to sync dimensions with
 * <code>GridModel</code>.
 * 
 * @author Decebal Suiu
 */
public interface ResizableGrid {
	
	/**
	 * Insert <code>rowCount</code> rows at <code>row</code>.
	 * 
	 * @param row
	 *            the row to insert at
	 * @param rowCount
	 *            the number of rows to insert
	 */
	public void insertRows(int row, int rowCount);

	/**
	 * Remove <code>rowCount</code> rows at <code>row</code>.
	 * 
	 * @param row
	 *            the row to remove from
	 * @param rowCount
	 *            the number of rows to remove
	 */
	public void removeRows(int row, int rowCount);

	/**
	 * Insert <code>columnCount</code> columns at <code>column</code>.
	 * 
	 * @param column
	 *            the column to insert at
	 * @param columnCount
	 *            the number of columns to insert
	 */
	public void insertColumns(int column, int columnCount);

	/**
	 * Remove <code>columnCount</code> columns at <code>column</code>.
	 * 
	 * @param column
	 *            the column to remove from
	 * @param columnCount
	 *            the number of columns to remove
	 */
	public void removeColumns(int column, int columnCount);
	
}
