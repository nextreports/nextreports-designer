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
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.MergeAlgorithm;
import ro.nextreports.designer.PasteContext;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.band.BandElement;

/**
 * @author Decebal Suiu
 */
public class CopyAction extends AbstractAction {

    private boolean ok = true;

    public CopyAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("copy.cell.action.name"));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.copy.accelerator", "control C")));
    }

    public void actionPerformed(ActionEvent event) {
        ReportGrid grid = Globals.getReportGrid();
        SelectionModel selectionModel = grid.getSelectionModel();

        // copy a matrix of cells
        if (selectionModel.getSelectedCells().size() > 1) {
            List<Cell> selCells = selectionModel.getSelectedCells();
            int copy = MergeAlgorithm.isCopyPossible(selCells, grid.getSpanModel());
            if (copy == MergeAlgorithm.VALID) {
                List<BandElement> elements = new ArrayList<BandElement>();
                List<Cell> cells = new ArrayList<Cell>();
                List<Integer> columnSizes = new ArrayList<Integer>();
                for (Cell cell : selCells) {
                    elements.add(grid.getBandElement(cell));
                    cells.add(cell);
                    columnSizes.add(grid.getColumnWidth(cell.getColumn()));
                }
                PasteContext pasteContext = new PasteContext(elements, cells, columnSizes);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection data = new StringSelection(XStreamFactory.createXStream().toXML(pasteContext));
                clipboard.setContents(data, data);
            } else {
                ok = false;
                Show.info(I18NSupport.getString("copy.cell.action.invalid"));
            }

            // copy a single cell
        } else {
            Cell cell = selectionModel.getSelectedCell();
            if (cell == null) {
                return;
            }

            BandElement element = grid.getBandElement(cell);
            if (element != null) {

                List<BandElement> elements = new ArrayList<BandElement>();
                List<Cell> cells = new ArrayList<Cell>();
                List<Integer> columnSizes = new ArrayList<Integer>();
                elements.add(element);
                cells.add(cell);
                columnSizes.add(grid.getColumnWidth(cell.getColumn()));
                PasteContext pasteContext = new PasteContext(elements, cells, columnSizes);

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection data = new StringSelection(XStreamFactory.createXStream().toXML(pasteContext));
                clipboard.setContents(data, data);
            }
        }
    }

    public boolean isOk() {
        return ok;
    }
}
