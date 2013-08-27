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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.band.BandElement;

/**
 * @author Decebal Suiu
 */
public class FormatPickerAction extends AbstractAction {

    public FormatPickerAction() {
        super();
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("picker.copy.tooltip"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("copy_settings"));
    }

	public void actionPerformed(ActionEvent event) {
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
        List<Cell> cells = selectionModel.getSelectedCells();                
        boolean isSpan = true;                
        for (Cell cell : cells) {
            isSpan = grid.getSpanModel().isCellSpan(cell.getRow(), cell.getColumn());
            if (!isSpan) {
               break;
            }
        }
        
        if (((cells.size() != 1) && !isSpan) || (cells.size() == 0)) {
           Show.info(I18NSupport.getString("picker.copy"));
        } else {
            int row = cells.get(0).getRow();
            int column = cells.get(0).getColumn();
    		BandElement element = grid.getBandElement(row, column); 
    		if (element != null) {				
    			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    			StringSelection data = new StringSelection(XStreamFactory.createXStream().toXML(element));
    			clipboard.setContents(data, data);
    		}
    	}
	}
	
}
