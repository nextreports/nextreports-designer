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

import java.util.Iterator;

import ro.nextreports.designer.grid.event.SpanModelListener;


/**
 * <code>SpanModel</code> provides information about cell spans for a grid of
 * cells.
 * 
 * @author Decebal Suiu
 */
public interface SpanModel {

	public void addSpanModelListener(SpanModelListener listener);

	public void removeSpanModelListener(SpanModelListener listener);

	/**
	 * Return the span over the cell at row, column.
	 */
	public CellSpan getSpanOver(int row, int column);

	/**
	 * Return true if the cell is part of a span with (rowCount > 1 ||
	 * columnCount > 1)
	 */
	public boolean isCellSpan(int row, int column);

	/**
	 * Return set of spans where (rowCount > 1 || columnCount > 1)
	 */
	public Iterator<CellSpan> getSpanIterator();

}
