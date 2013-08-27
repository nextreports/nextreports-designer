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
package ro.nextreports.designer.action.report.layout.cell;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.MergeAlgorithm;
import ro.nextreports.designer.PasteContext;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultSpanModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Band;

/**
 * @author Decebal Suiu
 */
public class PasteAction extends AbstractAction {

    public PasteAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("paste.cell.action.name"));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.paste.accelerator", "control V")));
    }

    public void actionPerformed(ActionEvent event) {
        ReportGrid grid = Globals.getReportGrid();
        SelectionModel selectionModel = grid.getSelectionModel();

        List<Cell> cells = selectionModel.getSelectedCells();
        if ((cells == null) || (cells.size() == 0)) {
            return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String data = null;
        try {
            data = (String) (clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Clipboard data is a PasteContext object with one BandElement in list for single cell copy/paste
        // and more band elements in list for multiple copy/paste cells
        PasteContext obj = (PasteContext)XStreamFactory.createXStream().fromXML(data);
        boolean multipleCopy = obj.getElements().size() > 1;        

        if (multipleCopy && (cells.size() > 1)) {
            Show.info(I18NSupport.getString("paste.cell.action.invalid"));
            return;
        }

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        // paste a cell in one or more cells
        if (!multipleCopy) {
            for (Cell cell : cells) {
                PasteContext pasteContext = (PasteContext) XStreamFactory.createXStream().fromXML(data);
                pasteElement(grid, pasteContext.getElements().get(0), cell, pasteContext.getColumnSizes().get(0));
            }
        // paste more cells starting from the selected cell
        } else {
            PasteContext pasteContext = (PasteContext) XStreamFactory.createXStream().fromXML(data);
            List<Cell> copyCells =pasteContext.getCells();
            Cell topLeft = copyCells.get(0);
            Cell bottomRight = MergeAlgorithm.getLastCell(copyCells);
            Cell selected = cells.get(0);

            int rows = bottomRight.getRow() - topLeft.getRow() + 1;
            int columns = bottomRight.getColumn() - topLeft.getColumn() + 1;

            String bandName = grid.getBandName(selected);
            Band band = LayoutHelper.getReportLayout().getBand(bandName);
            int bandRows = band.getRowCount();
            int bandColumns = band.getColumnCount();
            int insertRows = grid.getBandLocation(bandName).getRow(selected.getRow()) + rows - bandRows;
            int insertColumns = selected.getColumn() + columns - bandColumns;

            //System.out.println("bandName=" + bandName);
            //System.out.println("insertRows=" + insertRows);
            //System.out.println("insertColumns=" + insertColumns);

            // insert eventual rows and columns (at the end of the band)
            if (insertRows > 0) {
                for (int i = 0; i < insertRows; i++) {
                    Globals.getReportLayoutPanel().getReportGridPanel().insertRows(
                           LayoutHelper.getReportLayout().getGridRow(bandName, bandRows - 1 + i),
                           selected.getColumn(), 1, true);
                }
            }
            if (insertColumns > 0) {
                for (int i = 0; i < insertColumns; i++) {
                    Globals.getReportLayoutPanel().getReportGridPanel().insertColumns(
                            selected.getRow(), bandColumns - 1 + i, 1, true);
                }
            }
            selectionModel.clearSelection();

            // paste all elements
            List<BandElement> elements = pasteContext.getElements();
            List<Integer> columnSizes = pasteContext.getColumnSizes();
            int dR = selected.getRow() - topLeft.getRow();
            int dC = selected.getColumn() - topLeft.getColumn();
            for (int i=0, size=copyCells.size(); i<size; i++) {
                Cell copyCell = copyCells.get(i);
                BandElement element = elements.get(i);
                int whereX = selected.getRow()+dR-(selected.getRow()-copyCell.getRow());
                int whereY = selected.getColumn()+dC-(selected.getColumn()-copyCell.getColumn());
                //System.out.println("copyCell="+copyCell + "  elem="+element);
                //System.out.println("dR=" +dR + " dC="+dC);
                //System.out.println("whereX="+whereX + "  whereY="+whereY);
                pasteElement(grid, element, new Cell(whereX,whereY), columnSizes.get(i));
            }

            // create the span for pasted elements
            DefaultSpanModel spanModel = (DefaultSpanModel) grid.getSpanModel();
            List<CellSpan> visitedSpans = new ArrayList<CellSpan>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (spanModel.isCellSpan(topLeft.getRow() + i, topLeft.getColumn() + j)) {
                        CellSpan span = spanModel.getSpanOver(topLeft.getRow() + i, topLeft.getColumn() + j);
                        if (!visitedSpans.contains(span)) {
                            visitedSpans.add(span);                            
                            CellSpan newSpan = new CellSpan(selected.getRow() + i, selected.getColumn() + j, span.getRowCount(), span.getColumnCount());
                            spanModel.addSpan(newSpan);                            
                            BandUtil.updateBandElement(newSpan);
                        }
                    }
                }
            }
        }

        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.insert.element")));
    }

    private void pasteElement(ReportGrid grid, BandElement element, Cell cell, int columnSize) {
        int row = cell.getRow();
        int column = cell.getColumn();

        if (element == null) {
        	BandUtil.insertElement(null, row, column);
            return;
        }
        BandElement target = grid.getBandElement(cell);
        // if cell is not empty, clear it first
        if (target != null) {
            BandUtil.deleteElement(row, column);
        }

        CellSpan cellSpan = grid.getSpanModel().getSpanOver(row, column);
        // for merge cells insert only in the first cell of the merge
        if ((cellSpan != null) && (((cellSpan.getColumnCount() > 1) && (column > cellSpan.getFirstColumn())) ||
                ((cellSpan.getRowCount() > 1) && (row > cellSpan.getFirstRow())))) {
            return;
        }
                                                	
        BandElement newElement = BandUtil.copyBandElement(element);                	
                
      //System.out.println("cellSpan=" + cellSpan + " row="+row + "  col="+column);
        BandElement oldElement = BandUtil.insertElement(newElement, row, column); // it's null

        // resize column -- do we wish ??? (comment for now)
//        if (LayoutHelper.getReportLayout().isUseSize()) {
//            ReportLayoutUtil.resizeColumn(grid, column, columnSize);
//        }

        // repaint headers
        Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();
    }

}
