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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.report.OpenReportAction;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.NextReportsUtil;

import ro.nextreports.engine.band.ReportBandElement;

public class EditReportAction extends AbstractAction {
	
	 public EditReportAction() {
	        super();
	        putValue(Action.NAME, I18NSupport.getString("edit.report.action.name"));
	    }

	    public void actionPerformed(final ActionEvent event) {
	    	
	    	String masterPath = Globals.getCurrentReportAbsolutePath();	    	
	    				    			
	        final ReportGrid grid = Globals.getReportGrid();
			SelectionModel selectionModel = grid.getSelectionModel();			
			Cell cell = selectionModel.getSelectedCell();
			ReportBandElement rbe = (ReportBandElement)grid.getBandElement(cell.getRow(), cell.getColumn());
			if ((rbe != null) && (rbe.getReport() != null)) {
				
				// extract subreport in a temporary folder
				String tempPath = FileReportPersistence.getReportsAbsolutePath() + File.separator + FileReportPersistence.SUBREPORT_TEMP_DIR;				
				boolean create = new File(tempPath).mkdirs();	
				String subReportName = rbe.getReport().getName();
				String subreportPath = tempPath + File.separator + subReportName;        
				File file = new File(subreportPath);
				boolean save = FormSaver.getInstance().save(file, rbe.getReport());				
				
				// must save master report (if it was modified without save) before open the subreport
				if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("edit.report.action.name"), JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION)) {
		            return;
		        }
				
				if (!Globals.isInner()) {
					// add master report path
					Globals.pushPath(masterPath);
				}
				
				// open subreport
				OpenReportAction openAction = new OpenReportAction();
				openAction.setReportName(rbe.getReport().getBaseName());
			    openAction.setReportPath(subreportPath);			    
			    openAction.setTestInner(false);
			    openAction.actionPerformed(new ActionEvent(this, 0, ""));
			    
			    // add subreport path
			    Globals.pushPath(subreportPath);
			    
			    Globals.getMainToolBar().enableBackAction(true, new File(masterPath).getName());
			         
			}
	    }
}
