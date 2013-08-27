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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultSpanModel;
import ro.nextreports.designer.grid.SpanModel;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 10, 2008
 * Time: 11:36:44 AM
 */
public class MergeAlgorithm {

    public static final int VALID = 1;
    public static final int INVALID_ONE = 2;
    public static final int INVALID_CONTENT = 3;
    public static final int INVALID_BAND = 4;
    public static final int INVALID_SPAN = 5;
    public static final int INVALID_NEIGHBOUR = 6;
    public static final int INVALID_MATRIX = 7;

    // A merge algorithm is possible on a list of ordered cells if the cells
    // are grouped in a matrix.
    public static int isPossible(List<Cell> cells) {
        if ((cells == null) || (cells.size() == 1)) {
            return INVALID_ONE;
        }

        if (!oneNotNull(cells)) {
            return INVALID_CONTENT;
        }

        if (!sameBand(cells)) {
            return INVALID_BAND;
        }

        if (containsSpan(cells)) {
            return INVALID_SPAN;
        }

        if (!neighbours(cells)) {
            return INVALID_NEIGHBOUR;
        }

        if (!isMatrix(cells)) {
            return INVALID_MATRIX;
        }

        return VALID;
    }

    public static int isCopyPossible(List<Cell> selCells, SpanModel spanModel) {

        List<Cell> cells = getAllCells(selCells, spanModel);
        
        if ((cells == null) || (cells.size() == 1)) {
            return INVALID_ONE;
        }

        if (!sameBand(cells)) {
            return INVALID_BAND;
        }

        if (!neighbours(cells)) {
            return INVALID_NEIGHBOUR;
        }

        if (!isMatrix(cells)) {
            return INVALID_MATRIX;
        }

        return VALID;
    }

    public static Cell getLastCell(List<Cell> selCells) {
        Cell first = selCells.get(0);
        int rows = first.getRow();
        int columns = first.getColumn();
        boolean computeColumns = true;
        for (int i=1; i<selCells.size(); i++) {
            Cell cell = selCells.get(i);
            if (first.getRow() == cell.getRow()) {
                if (computeColumns) {
                    columns = cell.getColumn();
                }
            } else {
                computeColumns = false;
                first = cell;
                rows = cell.getRow();
            }
        }
        return new Cell(rows, columns);
    }        


    private static boolean oneNotNull(List<Cell> cells) {
        ReportGrid grid = Globals.getReportGrid();
        boolean flag = false;
        for (Cell cell : cells) {
            if ((grid.getBandElement(cell) != null) && (!"".equals(grid.getBandElement(cell).getText())) ) {
                if (!flag) {
                    flag = true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    private static boolean sameBand(List<Cell> cells) {
        String bandName = null;
        ReportGrid grid = Globals.getReportGrid();
        for (Cell cell : cells) {
            String tmp = grid.getBandName(cell);
            if (bandName == null) {
                bandName = tmp;
            }
            if (!bandName.equals(tmp)) {
                return false;
            }
        }
        return true;
    }


    private static boolean containsSpan(List<Cell> cells) {
        ReportGrid grid = Globals.getReportGrid();
        for (Cell cell : cells) {
            if (grid.getSpanModel().isCellSpan(cell.getRow(), cell.getColumn())) {
                return true;
            }
        }
        return false;
    }

    // test to see cells are neighbours
    private static boolean neighbours(List<Cell> cells) {
        Cell firstOnRow = null;
        int size = cells.size();
        for (int i = 0; i < size - 1; i++) {
            Cell cellOne = cells.get(i);
            Cell cellTwo = cells.get(i + 1);
            Cell firstCell, secondCell;
            if (i == 0) {
                firstOnRow = cellOne;
                if (cellOne.getRow() != cellTwo.getRow()) {
                    firstOnRow = cellTwo;
                }
                firstCell = cellOne;
                secondCell = cellTwo;
            } else {
                if (cellOne.getRow() != cellTwo.getRow()) {
                    firstCell = new Cell(firstOnRow.getRow(), firstOnRow.getColumn());
                    secondCell = cellTwo;
                    firstOnRow = cellTwo;
                } else {
                    firstCell = cellOne;
                    secondCell = cellTwo;
                }
            }
            if (!neighbours(firstCell, secondCell)) {
                return false;
            }
        }

        return true;
    }

    private static boolean neighbours(Cell cellOne, Cell cellTwo) {
        if (cellOne.equals(cellTwo)) {
            throw new IllegalArgumentException("Cells must be different!");
        }

        if (cellOne.getRow() == cellTwo.getRow()) {
            return Math.abs(cellOne.getColumn() - cellTwo.getColumn()) == 1;
        } else if (cellOne.getColumn() == cellTwo.getColumn()) {
            return Math.abs(cellOne.getRow() - cellTwo.getRow()) == 1;
        } else {
            return false;
        }
    }

    private static boolean neighbours(Cell cellOne, Cell cellTwo, SpanModel spanModel) {
        if (cellOne.equals(cellTwo)) {
            throw new IllegalArgumentException("Cells must be different!");
        }

        int rowOne = cellOne.getRow();
        int colOne = cellOne.getColumn();
        int rowTwo = cellTwo.getRow();
        int colTwo = cellTwo.getColumn();
        if (spanModel.isCellSpan(rowOne, colOne)) {
            CellSpan span = spanModel.getSpanOver(rowOne, colOne);
            rowOne = span.getLastRow();
            colOne = span.getLastColumn();
        }

        if (cellOne.getRow() == cellTwo.getRow()) {
            return Math.abs(cellOne.getColumn() - cellTwo.getColumn()) == 1;
        } else if (cellOne.getColumn() == cellTwo.getColumn()) {
            return Math.abs(cellOne.getRow() - cellTwo.getRow()) == 1;
        } else {
            return false;
        }
    }

    // test if list with neighbour cells is matrix
    private static boolean isMatrix(List<Cell> cells) {
        // all rows must have same number of cells
        // all columns must have same number of cells
        Map<Integer, Integer> rows = new HashMap<Integer, Integer>();
        Map<Integer, Integer> cols = new HashMap<Integer, Integer>();
        int size = cells.size();
        for (int i = 0; i < size; i++) {
            int cellRow = cells.get(i).getRow();
            int cellColumn = cells.get(i).getColumn();
            Integer rowNo = rows.get(cellRow);
            Integer colNo = cols.get(cellColumn);
            int rowN = (rowNo == null) ? 0 : rowNo;
            int colN = (colNo == null) ? 0 : colNo;
            rows.put(cellRow, rowN + 1);
            cols.put(cellColumn, colN + 1);
        }

        int no = -1;
        for (Integer row : rows.keySet()) {
            int value = rows.get(row);
            if (no == -1) {
                no = value;
            } else if (no != value) {
                return false;
            }
        }

        no = -1;
        for (Integer col : cols.keySet()) {
            int value = cols.get(col);
            if (no == -1) {
                no = value;
            } else if (no != value) {
                return false;
            }
        }

        return true;
    }

    // get all cells (without span) : a span means more cells!
    private static List<Cell> getAllCells(List<Cell> cells, SpanModel spanModel) {
        List<Cell> allCells = new ArrayList<Cell>();
        allCells.addAll(cells);
        for (Cell cell : cells) {
            if (spanModel.isCellSpan(cell.getRow(), cell.getColumn())) {
                CellSpan span = spanModel.getSpanOver(cell.getRow(), cell.getColumn());
                for (int i = span.getFirstRow(); i <= span.getLastRow(); i++) {
                    for (int j = span.getFirstColumn(); j <= span.getLastColumn(); j++) {
                        Cell newCell = new Cell(i, j);
                        if (!allCells.contains(newCell)) {
                            allCells.add(newCell);
                        }
                    }
                }
            }
        }
        Collections.sort(allCells, new Comparator<Cell>() {
            public int compare(Cell o1, Cell o2) {
                 if (o1.getRow() < o2.getRow()) {
                     return -1;
                 } else if  (o1.getRow() == o2.getRow()) {
                     return o1.getColumn() - o2.getColumn();
                 } else {
                     return 1;
                 }
            }
        });
        return allCells;
    }

    public static void main(String[] args) {
        List<Cell> cells = new ArrayList<Cell>();
//        cells.add(new Cell(0, 0));
//        cells.add(new Cell(0, 1));
//        cells.add(new Cell(0, 2));
//        cells.add(new Cell(1, 0));
//        cells.add(new Cell(1, 1));
//        cells.add(new Cell(1, 2));
//        System.out.println(MergeAlgorithm.isPossible(cells));

        cells.add(new Cell(0, 0));
        cells.add(new Cell(0, 2));
        cells.add(new Cell(1, 2));
        cells.add(new Cell(2, 0));
        cells.add(new Cell(2, 1));

        DefaultSpanModel model = new DefaultSpanModel();
        model.addSpan(new CellSpan(0,0,2,2));
        model.addSpan(new CellSpan(2,1,1,2));

        //System.out.println("validStatus = "+isCopyPossible(cells, model));
        System.out.println("last cell = " + getLastCell(cells));



    }

}
