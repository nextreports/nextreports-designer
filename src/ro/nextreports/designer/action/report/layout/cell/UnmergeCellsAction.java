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

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultSpanModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.ReportLayout;

/**
 * @author Decebal Suiu
 */
public class UnmergeCellsAction extends AbstractAction {

    public UnmergeCellsAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("unmerge.action.name"));
    }

    public void actionPerformed(ActionEvent event) {

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
        DefaultSpanModel spanModel = (DefaultSpanModel) grid.getSpanModel();

        List<Cell> cells = selectionModel.getSelectedCells();
        for (Cell cell : cells) {
            CellSpan cellSpan = grid.getSpanModel().getSpanOver(cell.getRow(), cell.getColumn());
            spanModel.removeSpan(cellSpan);

            BandElement bandElement = grid.getBandElement(cell);
            if (bandElement != null) {
                bandElement.setRowSpan(1);
                bandElement.setColSpan(1);
            } else {
            	BandUtil.deleteElement(cell.getRow(), cell.getColumn());
            }
        }

        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("unmerge.action.name")));

    }

}
