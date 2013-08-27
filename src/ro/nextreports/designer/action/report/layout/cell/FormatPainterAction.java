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
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.ModifyElementsEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.band.BandElement;

/**
 * @author Decebal Suiu
 */
public class FormatPainterAction extends AbstractAction {

    public FormatPainterAction() {
        super();
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("picker.paste.tooltip"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("paste_settings"));
    }

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent event) {
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String data = null;
		try {
			data = (String) (clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		BandElement from = (BandElement) XStreamFactory.createXStream().fromXML(data);
        if (from != null) {
            List<Cell> cells = selectionModel.getSelectedCells();
            if (cells.size() == 0) {
                Show.info(I18NSupport.getString("picker.paste"));
            } else {
            	List<BandElement> elements = new ArrayList<BandElement>();
            	List<Integer> rows = new ArrayList<Integer>();
            	List<Integer> columns = new ArrayList<Integer>();            	
                for (Cell cell : cells) {                            
                    BandElement element = grid.getBandElement(cell);
                    if (element != null) {
                    	elements.add(element);
                    	rows.add(cell.getRow());
                    	columns.add(cell.getColumn());
                    }
                }
                int n = elements.size();
                if (n > 0) {
	                DefaultGridModel gridModel = (DefaultGridModel) grid.getModel();
	            	List<BandElement> oldElements = (List<BandElement>) ObjectCloner.silenceDeepCopy(elements);
	                for (int i = 0; i < n; i++) {
	                	TemplateManager.restoreBandElement(elements.get(i), from);
	                    gridModel.fireGridCellUpdated(rows.get(i), columns.get(i));
	                }
	        		Globals.getReportUndoManager().addEdit(new ModifyElementsEdit(ObjectCloner.silenceDeepCopy(elements), 
	        				oldElements, rows, columns));
                }
            }
        } else {
            Show.info(I18NSupport.getString("picker.paste"));
        }                
	}
	
}
