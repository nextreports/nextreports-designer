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

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ExpressionBandElement;

import javax.swing.*;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.ExpressionCellEditor;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 30-Apr-2010
 * Time: 14:15:50
 */
public class InsertExpressionAction extends AbstractAction {

	private static final String DEFAULT_TEXT = "?";
    // isStatic = true  : means expression is in a static band
    //          = false : means expresion is in detail or group bands
    private boolean isStatic = false;
    private boolean isHeaderOrFooter = false;

    public InsertExpressionAction(boolean isStatic, boolean isHeaderOrFooter) {
        super();
        this.isStatic = isStatic;
        this.isHeaderOrFooter = isHeaderOrFooter;
        putValue(Action.NAME, I18NSupport.getString("expression.insert"));
    }

    public void actionPerformed(ActionEvent event) {
        ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();

		int row = selectionModel.getSelectedCell().getRow();
        int column = selectionModel.getSelectedCell().getColumn();

        BandElement element = new ExpressionBandElement("", "");
        BandUtil.copySettings(grid.getBandElement(selectionModel.getSelectedCell()), element);
        
        grid.putClientProperty("layoutBeforeInsert", ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout()));
        BandUtil.insertElement(element, row, column);

        ExpressionCellEditor editor = (ExpressionCellEditor)grid.getCellEditor(row, column);
        editor.setStatic(isStatic);
        editor.setIsHeaderOrFooter(isHeaderOrFooter);
        editor.setBandName(BandUtil.getBand(LayoutHelper.getReportLayout(), selectionModel.getSelectedCell().getRow()).getName());
        grid.editCellAt(row, column, event);
    }
}
