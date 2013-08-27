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

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.CellUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.NumberSelectionPanel;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.ReportGridPanel;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportLayout;

/**
 * @author Decebal Suiu
 */
public class InsertAfterRowAction extends AbstractAction {

    private int selectedRow = -1;

    public InsertAfterRowAction() {
        this(-1);
    }

    public InsertAfterRowAction(int selectedRow) {
        super();
        this.selectedRow = selectedRow;
        putValue(Action.NAME, I18NSupport.getString("insert.row.after.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
        final NumberSelectionPanel panel = new NumberSelectionPanel(I18NSupport.getString("insert.row.number"));
        final BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("insert.row.after.action.name"), true) {
            public boolean okPressed() {
                    if ((panel.getNumber() < 1) || (panel.getNumber() > BandUtil.MAX)) {
                        Show.info(this, I18NSupport.getString("rowCol.max", BandUtil.MAX));
                        return false;
                    }
                    return super.okPressed();
                }
        };
		dialog.pack();
		dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);
        if (!dialog.okPressed()) {
			return;
		}

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        int rowCount = panel.getNumber();

        ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		ReportGridPanel reportGridPanel = Globals.getReportLayoutPanel().getReportGridPanel();
		
		Cell cell = CellUtil.getCellFromSelectedRow(grid,selectedRow);

        for (int i = 0; i < rowCount; i++) {             
            reportGridPanel.insertRows(cell.getRow() + i, cell.getColumn(), 1, true);
        }
        selectionModel.clearSelection();
        
        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.row.insert.after")));
    }

}
