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

import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.grid.SpanModel;

//
public class CellUtil {

    public static Cell getCellFromSelectedColumn(ReportGrid grid, int selectedColumn) {
		SelectionModel selectionModel = grid.getSelectionModel();
        int row= -1;
        int defRow = -1, defCol = -1;
        int column;
        if (selectedColumn == -1) {
            // insert column from selected cell
            row = selectionModel.getSelectedCell().getRow();
            column = selectionModel.getSelectedCell().getColumn();
        } else {
            // insert column from header
            column = selectedColumn;
            int rows = grid.getRowCount();
            SpanModel spanModel = grid.getSpanModel();
            for (int i=0; i<rows; i++) {
                if (!spanModel.isCellSpan(i, column)) {
                    row = i;
                    break;
                } else {
                    CellSpan span = spanModel.getSpanOver(i,column);
                    // row span
                    if (span.getFirstColumn() == span.getLastColumn()) {
                        row = i;
                        break;
                    } else {
                        if (defRow == -1) {
                            defRow = span.getFirstRow();
                            defCol = span.getFirstColumn();
                        }
                    }
                }
            }
        }

        // if all cells for that column have a column span , the column will be inserted after/before
        // the column span of the first cell
        if (row == -1) {
            row = defRow;
            column = defCol;
        }
        return new Cell(row, column);
    }

    public static Cell getCellFromSelectedRow(ReportGrid grid, int selectedRow) {
		SelectionModel selectionModel = grid.getSelectionModel();
        int row= -1;
        int defRow = -1, defCol = -1;
        int column = -1;
        if (selectedRow == -1) {
            // insert row from selected cell
            row = selectionModel.getSelectedCell().getRow();
            column = selectionModel.getSelectedCell().getColumn();
        } else {
            // insert row from header
            row = selectedRow;
            int columns = grid.getColumnCount();
            SpanModel spanModel = grid.getSpanModel();
            for (int i=0; i<columns; i++) {
                if (!spanModel.isCellSpan(row, i)) {
                    column = i;
                    break;
                } else {
                    CellSpan span = spanModel.getSpanOver(row,i);
                    // col span
                    if (span.getFirstRow() == span.getLastRow()) {
                        column = i;
                        break;
                    } else {
                        if (defRow == -1) {
                            defRow = span.getFirstRow();
                            defCol = span.getFirstColumn();
                        }
                    }
                }
            }
        }

        // if all cells for that row have a row span , the row will be inserted after/before
        // the row span of the first cell
        if (column == -1) {
            row = defRow;
            column = defCol;
        }
        return new Cell(row, column);
    }
}
