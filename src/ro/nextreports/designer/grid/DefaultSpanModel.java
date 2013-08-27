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


import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ro.nextreports.designer.BandUtil;

/**
 * @author Decebal Suiu
 */
public class DefaultSpanModel extends AbstractSpanModel implements
		ResizableGrid {
	
	// used to specially paint spanned cells by BasicGridUI
	private HashSet<CellSpan> spanSet = new HashSet<CellSpan>();

	// set of cells that are part of (non-atomic) spans
	private HashSet<Point> spanCellSet = new HashSet<Point>();

	private Point cell = new Point();

	public DefaultSpanModel() {
	}

	public CellSpan getSpanOver(int row, int column) {
		Iterator<CellSpan> it = getSpanIterator();
		while (it.hasNext()) {
			CellSpan span = it.next();
			if (row >= span.getFirstRow() && row <= span.getLastRow()
					&& column >= span.getFirstColumn()
					&& column <= span.getLastColumn()) {
				return span;
			}
		}
		
		// Return atomic span
		return new CellSpan(row, column, 1, 1);
	}

	public boolean isCellSpan(int row, int column) {
		cell.x = column;
		cell.y = row;
		
		return spanCellSet.contains(cell);
	}

	public Iterator<CellSpan> getSpanIterator() {
		return spanSet.iterator();
	}

	public void addSpan(CellSpan span) {
		for (int row = span.getFirstRow(); row <= span.getLastRow(); row++) {
			for (int column = span.getFirstColumn(); column <= span
					.getLastColumn(); column++) {
				spanCellSet.add(new Point(column, row));
			}
		}
		spanSet.add(span);
		fireCellSpanAdded(span);
	}

	public void removeSpan(CellSpan span) {
		for (int row = span.getFirstRow(); row <= span.getLastRow(); row++) {
			for (int column = span.getFirstColumn(); column <= span
					.getLastColumn(); column++) {
				spanCellSet.remove(new Point(column, row));
			}
		}
		spanSet.remove(span);
		fireCellSpanRemoved(span);
	}

	private void rebuildSpanCellSet() {
		spanCellSet.clear();
		Iterator<CellSpan> it = spanSet.iterator();
		while (it.hasNext()) {
			CellSpan span = (CellSpan) it.next();
			for (int rowIndex = span.getFirstRow(); rowIndex <= span
					.getLastRow(); rowIndex++) {
				for (int colIndex = span.getFirstColumn(); colIndex <= span
						.getLastColumn(); colIndex++) {
					spanCellSet.add(new Point(colIndex, rowIndex));
				}
			}
		}
	}

	public void insertRows(int row, int rowCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> it = oldSpanSet.iterator();
		while (it.hasNext()) {
			CellSpan span = (CellSpan) it.next();
			if (span.getLastRow() < row) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstRow() >= row) {
				// move span down
				CellSpan newSpan = new CellSpan(span.getRow() + rowCount, span.getColumn(),
                        span.getRowCount(), span.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// increase span
				CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn(),
                        span.getRowCount() + rowCount, span.getColumnCount());
                // todo outside span model??
                BandUtil.updateBandElement(newSpan);
                spanSet.add(newSpan);
			}
		}

		rebuildSpanCellSet();
	}

	public void removeRows(int row, int rowCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> it = oldSpanSet.iterator();
		while (it.hasNext()) {
			CellSpan span = (CellSpan) it.next();
            if (span.getLastRow() < row) {
				// leave span unchanged
                spanSet.add(span);
            } else if (row < span.getFirstRow()) {
                 // move span up
                if (span.getRow() >= rowCount) {
                    CellSpan newSpan = new CellSpan(span.getRow() - rowCount, span.getColumn(),
                            span.getRowCount(), span.getColumnCount());
                    spanSet.add(newSpan);
                }
            } else if ((span.getFirstRow() <= row) && (span.getLastRow() >= row)) {
				// decrease span                
                CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn(),
                        span.getRowCount() - rowCount, span.getColumnCount());
                // todo outside span model??
                BandUtil.updateBandElement(newSpan);
                if ((newSpan.getRowCount() > 1) || (newSpan.getColumnCount() > 1)) {
					spanSet.add(newSpan);
				}
            }
		}
		rebuildSpanCellSet();
	}

	public void insertColumns(int column, int columnCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> it = oldSpanSet.iterator();
		while (it.hasNext()) {
			CellSpan span = (CellSpan) it.next();
			if (span.getLastColumn() < column) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstColumn() >= column) {
				// move span right
				CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn()	+ columnCount,
                        span.getRowCount(), span.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// increase span
				CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn(),
                        span.getRowCount(), span.getColumnCount() + columnCount);                
                // todo outside span model??
                BandUtil.updateBandElement(newSpan);
                spanSet.add(newSpan);
			}
		}
		rebuildSpanCellSet();
	}

	public void removeColumns(int column, int columnCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> it = oldSpanSet.iterator();
		while (it.hasNext()) {
			CellSpan span = (CellSpan) it.next();
			if (span.getLastColumn() < column) {
				// leave span unchanged
				spanSet.add(span);
			} else if (column < span.getFirstColumn()) {
				// move span left
                if (span.getColumn() >= columnCount) {
                    CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn() - columnCount,
                            span.getRowCount(), span.getColumnCount());
                    spanSet.add(newSpan);
                }
            } else if ((span.getFirstColumn() <= column) && (span.getLastColumn() >= column)){
				// decrease span
				CellSpan newSpan = new CellSpan(span.getRow(),	span.getColumn(),
                        span.getRowCount(), span.getColumnCount() - columnCount);
                // todo outside span model??
                BandUtil.updateBandElement(newSpan);
                if (newSpan.getColumnCount() > 1) {
					spanSet.add(newSpan);
				}
			}
		}
		rebuildSpanCellSet();
	}
	
}
