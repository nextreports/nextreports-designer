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
package ro.nextreports.designer.action.report.layout;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import ro.nextreports.designer.CellUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.ReportGridPanel;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportLayout;

/**
 * @author Decebal Suiu
 */
public class RemoveRowAction extends AbstractAction {

    private int[] selectedRows = new int[0];

    public RemoveRowAction() {
        this (new int[0]);
    }

    public RemoveRowAction(int[] selectedRows) {
        super();
        this.selectedRows = selectedRows;
        putValue(Action.NAME, I18NSupport.getString("remove.row.action.name"));
    }

    public void actionPerformed(ActionEvent event) {

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        ReportGrid reportGrid = Globals.getReportGrid();
        reportGrid.removeEditor();
        SelectionModel selectionModel = reportGrid.getSelectionModel();
		ReportGridPanel reportGridPanel = Globals.getReportLayoutPanel().getReportGridPanel();

        List<Cell> selCells = new ArrayList<Cell>();
        if (selectedRows.length  >  0) {
            for (int i=0; i<selectedRows.length; i++){
                selCells.add(CellUtil.getCellFromSelectedRow(reportGrid, selectedRows[i]));
            }
        } else {
           selCells = Globals.getReportGrid().getSelectionModel().getSelectedCells();
        }
        List<Cell> cells = getSelectedCells(selCells);

        // test for different rows, because otherwise it it will be too difficult to see for merged cells
        if (!differentRows(cells)) {
            Show.info(I18NSupport.getString("remove.row.dif"));
            return;
        }

        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                 I18NSupport.getString("remove.row.ask"),
                 I18NSupport.getString("remove.row.action.name"),
                 JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        //  must delete from last row to first
        int size = cells.size();
        for (int i = size-1; i>=0; i--) {
            Cell cell = cells.get(i);
            int row = cell.getRow();
            int column = cell.getColumn();
            CellSpan cellSpan = reportGrid.getSpanModel().getSpanOver(row, column);
            if (cellSpan == null) {
                reportGridPanel.removeRows(row, 1);
            } else {
                reportGridPanel.removeRows(row, cellSpan.getRowCount());
            }
            if (reportGridPanel.getRowCount() == 0) {
                ((DefaultGridModel) reportGrid.getModel()).removeColumns(0, reportGridPanel.getColumnCount());
                reportGrid.emptyBandLocations();
                LayoutHelper.reset();
            }
        }
        selectionModel.clearSelection();

        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.row.remove")));
    }

    private List<Cell> getSelectedCells(List<Cell> selectedCells) {
        List<Cell> result = new ArrayList<Cell>();
        Collections.sort(selectedCells, new Comparator<Cell>() {
            public int compare(Cell c1, Cell c2) {
                return c1.getRow() - c2.getRow();
            }
        });
        Cell prevCell = null;

        if (sameRowCell(selectedCells)) {
            if (selectedCells.size() >  0) {                
                result.add(selectedCells.get(0));
                return result;
            }
        }

        for (Cell cell : selectedCells) {
            if ((prevCell != null) &&  sameRowSpan(prevCell, cell)) {
                continue;
            }
            result.add(cell);
            prevCell = cell;
        }
        return result;
    }

    private boolean sameRowSpan(Cell c1, Cell c2) {
        CellSpan cs1 = Globals.getReportGrid().getSpanModel().getSpanOver(c1.getRow(), c1.getColumn());
        CellSpan cs2 = Globals.getReportGrid().getSpanModel().getSpanOver(c2.getRow(), c2.getColumn());
        if (c1.getColumn() == c2.getColumn()) {
            if ((cs1 != null) && (cs2 != null)) {
                if (cs1.getFirstRow() == cs2.getFirstRow()) {                    
                    return true;
                }
            }
        }
        return false;
    }

    private boolean sameRowCell(List<Cell> selectedCells) {
        for (int i=0, size=selectedCells.size(); i<size-1; i++) {
            for (int j=i+1; j<size; j++) {
                Cell cellOne = selectedCells.get(i);
                Cell cellTwo = selectedCells.get(j);                
                if (cellOne.getRow() == cellTwo.getRow()) {
                    CellSpan cs1 = Globals.getReportGrid().getSpanModel().getSpanOver(cellOne.getRow(), cellOne.getColumn());
                    CellSpan cs2 = Globals.getReportGrid().getSpanModel().getSpanOver(cellTwo.getRow(), cellTwo.getColumn());                    
                    if ((cs1 != null) && (cs2 != null)) {
                        if ((cs1.getFirstRow() != cs2.getFirstRow()) || (cs1.getColumnCount() != cs2.getColumnCount())) {
                           return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean differentRows(List<Cell> selectedCells) {
        for (int i=0, size=selectedCells.size(); i<size-1; i++) {
            for (int j=i+1; j<size; j++) {
                if (selectedCells.get(i).getRow() == selectedCells.get(j).getRow()) {
                    return false;
                }
            }
        }
        return true;
    }

}
