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

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.*;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ShortcutsUtil;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ObjectCloner;

/**
 * @author Decebal Suiu
 */
public class ClearCellAction extends AbstractAction {

    private boolean clearFirstCell;

    public ClearCellAction() {
        this(false);
    }

    public ClearCellAction(boolean clearFirstCell) {
        super();
        this.clearFirstCell = clearFirstCell;
        putValue(Action.NAME, I18NSupport.getString("clear.cell.action.name"));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.delete.accelerator", "DELETE")));
    }

    public void actionPerformed(ActionEvent event) {

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
		List<Cell> cells = selectionModel.getSelectedCells();
		for (Cell cell : cells) {
			if (grid.getBandElement(cell) != null) {
				BandUtil.deleteElement(cell.getRow(), cell.getColumn());
			}
            if (clearFirstCell) {
                break;
            }
        }

        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("clear.cell.action.name")));
    }
        
}
