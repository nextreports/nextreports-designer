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
import ro.nextreports.designer.MergeAlgorithm;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultSpanModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportLayout;

/**
 * @author Decebal Suiu
 */
public class MergeCellsAction extends AbstractAction {

    public MergeCellsAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("merge.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
		List<Cell> cells = selectionModel.getSelectedCells();
        int result = MergeAlgorithm.isPossible(cells);
        if (result == MergeAlgorithm.VALID) {

            ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

            int firstRow = cells.get(0).getRow();
			int firstColumn = cells.get(0).getColumn();
			int lastRow = cells.get(cells.size() - 1).getRow();
			int lastColumn = cells.get(cells.size() - 1).getColumn();
			CellSpan cellSpan = new CellSpan(firstRow, firstColumn,
					(lastRow - firstRow + 1), (lastColumn - firstColumn + 1));
			
			DefaultSpanModel spanModel = (DefaultSpanModel) grid.getSpanModel();
			spanModel.addSpan(cellSpan);

            BandUtil.moveNotEmptyBandElementToTopLeft(cellSpan);
            BandUtil.updateBandElement(cellSpan);
            
            for (int i=firstRow; i<=lastRow; i++) {
            	for (int j=firstColumn; j<=lastColumn; j++) {
            		if ((i == firstRow) && (j == firstColumn)) {
            			continue;
            		}
            		BandUtil.nullifyElement(i, j);
            	}
            }

            ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
            Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("merge.action.name")));
        } else {
			Show.info(I18NSupport.getString("merge.action.invalid") + " : " + message(result));
		}
    }

    private String message(int result) {
        String message= "N/A";
        if (result == MergeAlgorithm.INVALID_ONE) {
           message = I18NSupport.getString("merge.invalid.one");
        } else if (result == MergeAlgorithm.INVALID_CONTENT) {
           message = I18NSupport.getString("merge.invalid.content");
        } else if (result == MergeAlgorithm.INVALID_BAND) {
           message = I18NSupport.getString("merge.invalid.band");
        } else if (result == MergeAlgorithm.INVALID_SPAN) {
           message = I18NSupport.getString("merge.invalid.span");
        } else if (result == MergeAlgorithm.INVALID_NEIGHBOUR) {
           message = I18NSupport.getString("merge.invalid.neighbours");
        } else if (result == MergeAlgorithm.INVALID_MATRIX) {
           message = I18NSupport.getString("merge.invalid.matrix");
        }
        return message;
    }

}
