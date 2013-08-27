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
import ro.nextreports.engine.band.ImageBandElement;

import javax.swing.*;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.NextReportsUtil;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 02-Dec-2009
 * Time: 13:35:00
 */
public class InsertImageAction extends AbstractAction {

	private static final String DEFAULT_TEXT = "?";

    public InsertImageAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("insert.image.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
    	    	
    	// report was not saved and we do not know where to copy image
    	if (Globals.getCurrentReportAbsolutePath() == null) {
    		if (!NextReportsUtil.saveReportForInserting(I18NSupport.getString("existing.report.image.save"))) {
    			return;
    		}
    	}
    	
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();

		int row = selectionModel.getSelectedCell().getRow();
        int column = selectionModel.getSelectedCell().getColumn();

        BandElement element = new ImageBandElement(DEFAULT_TEXT);
        grid.putClientProperty("layoutBeforeInsert", ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout()));
        BandUtil.insertElement(element, row, column);

        grid.editCellAt(row, column, event);

    }

}
