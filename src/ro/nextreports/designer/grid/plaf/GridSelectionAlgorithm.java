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
package ro.nextreports.designer.grid.plaf;


import java.awt.*;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Sep-2009
// Time: 15:11:49

import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.SelectionRegion;

/**
 * Selection Algorithm for a grid updates a selectionRegion object
 * <p/>
 * There are four types of selections from a starting point P :
 * <p/>
 * <pre>
 *             |
 * left-top    | right-top
 * ------------P------------
 * left-bottom | right-bottom
 *             |
 * </pre>
 * <p/>
 * Inside any of the four regions we can move left, right, up, down
 */
public class GridSelectionAlgorithm {

    private JGrid grid;
    private int RIGHT_BOTTOM = 1;
    private int RIGHT_TOP = 2;
    private int LEFT_TOP = 3;
    private int LEFT_BOTTOM = 4;
    private int type = 0;

    public GridSelectionAlgorithm(JGrid grid) {
        this.grid = grid;
    }

    public void update(Point currentPoint,
                       Point startPoint,
                       Point previousPoint,
                       SelectionRegion selectionRegion) {

        int currentRow = grid.rowAtPoint(currentPoint);
        int currentColumn = grid.columnAtPoint(currentPoint);
        int startRow = grid.rowAtPoint(startPoint);
        int startColumn = grid.columnAtPoint(startPoint);
        int previousRow  = grid.rowAtPoint(previousPoint);
        int previuosColumn = grid.columnAtPoint(previousPoint);

        update(currentRow, currentColumn, startRow, startColumn, previousRow, previuosColumn, selectionRegion);

    }
    

    public void update(int currentRow, int currentColumn,
                       int startRow, int startColumn,
                       int previousRow, int previousColumn,
                       SelectionRegion selectionRegion) {

        int lastRow = currentRow;
        int lastColumn = currentColumn;
        int firstRow = currentRow;
        int firstColumn = currentColumn;
        if (grid.getSpanModel().isCellSpan(currentRow, currentColumn)) {
            CellSpan cellSpan = grid.getSpanModel().getSpanOver(currentRow, currentColumn);
            firstRow = cellSpan.getFirstRow();
            firstColumn = cellSpan.getFirstColumn();
            lastRow = cellSpan.getLastRow();
            lastColumn = cellSpan.getLastColumn();
        }

        if (currentColumn >= startColumn) {
            if (currentRow >= startRow) {
                // right-bottom region
                selectionRegion.setFirstRow(Math.min(firstRow, selectionRegion.getFirstRow()));
                selectionRegion.setFirstColumn(Math.min(firstColumn, selectionRegion.getFirstColumn()));
                if (currentColumn >= previousColumn) {
                    selectionRegion.setLastColumn(Math.max(lastColumn, selectionRegion.getLastColumn()));
                } else {
                    selectionRegion.setLastColumn(lastColumn);
                }
                if (currentRow >= previousRow) {
                    selectionRegion.setLastRow(Math.max(lastRow, selectionRegion.getLastRow()));
                } else {
                    selectionRegion.setLastRow(lastRow);
                }

                // we can be here for RIGHT_TOP or LEFT_BOTTOM region because
                // there is an equality in the isLeftRight and isUpDown methods
                if (type != RIGHT_BOTTOM) {
                    if (startRow == currentRow) {
                        selectionRegion.setFirstRow(firstRow);
                    }
                    if (startColumn == currentColumn) {
                        selectionRegion.setFirstColumn(firstColumn);
                    }
                }
                type = RIGHT_BOTTOM;
            } else {
                // right-top region
                selectionRegion.setFirstColumn(Math.min(firstColumn, selectionRegion.getFirstColumn()));
                selectionRegion.setLastRow(Math.max(lastRow, selectionRegion.getLastRow()));
                if (currentColumn >= previousColumn) {
                    selectionRegion.setLastColumn(Math.max(lastColumn, selectionRegion.getLastColumn()));
                } else {
                    selectionRegion.setLastColumn(lastColumn);
                }
                if (currentRow >= previousRow) {
                    selectionRegion.setFirstRow(firstRow);
                } else {
                    selectionRegion.setFirstRow(Math.min(firstRow, selectionRegion.getFirstRow()));
                }
                if (type != RIGHT_TOP) {
                    if (startColumn == currentColumn) {
                        selectionRegion.setFirstColumn(firstColumn);
                    }
                }
                type = RIGHT_TOP;
            }

        } else {
            if (currentRow < startRow) {
                // left-top region
                selectionRegion.setLastRow(Math.max(lastRow, selectionRegion.getLastRow()));
                selectionRegion.setLastColumn(Math.max(lastColumn, selectionRegion.getLastColumn()));
                if (currentColumn < previousColumn) {
                    selectionRegion.setFirstColumn(Math.min(firstColumn, selectionRegion.getFirstColumn()));
                } else {
                    selectionRegion.setFirstColumn(firstColumn);
                }
                if (currentRow < previousRow) {
                    selectionRegion.setFirstRow(Math.min(firstRow, selectionRegion.getFirstRow()));
                } else {
                    selectionRegion.setFirstRow(firstRow);
                }
                type = LEFT_TOP;
            } else {
                // left-bottom region
                selectionRegion.setFirstRow(Math.min(firstRow, selectionRegion.getFirstRow()));
                selectionRegion.setLastColumn(Math.max(lastColumn, selectionRegion.getLastColumn()));
                if (currentColumn < previousColumn) {
                    selectionRegion.setFirstColumn(Math.min(firstColumn, selectionRegion.getFirstColumn()));
                } else {
                    selectionRegion.setFirstColumn(firstColumn);
                }
                if (currentRow < previousRow) {
                    selectionRegion.setLastRow(lastRow);
                } else {
                    selectionRegion.setLastRow(Math.max(lastRow, selectionRegion.getLastRow()));
                }
                if (type != LEFT_BOTTOM) {
                    if (startRow == currentRow) {
                        selectionRegion.setFirstRow(firstRow);
                    }
                }
                type = LEFT_BOTTOM;
            }
        }

    }
}
