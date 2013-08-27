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
import ro.nextreports.engine.band.HyperlinkBandElement;

import javax.swing.*;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 12:57:43
 */
public class InsertHyperlinkAction extends AbstractAction {

	private static final String DEFAULT_TEXT = "?";

    public InsertHyperlinkAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("insert.hyperlink.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();

		int row = selectionModel.getSelectedCell().getRow();
        int column = selectionModel.getSelectedCell().getColumn();

        BandElement element = new HyperlinkBandElement(DEFAULT_TEXT, DEFAULT_TEXT);
        BandUtil.copySettings(grid.getBandElement(selectionModel.getSelectedCell()), element);
        
        grid.putClientProperty("layoutBeforeInsert", ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout()));
        BandUtil.insertElement(element, row, column);

        grid.editCellAt(row, column, event);

    }

}
