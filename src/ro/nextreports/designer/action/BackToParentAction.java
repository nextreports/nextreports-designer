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
package ro.nextreports.designer.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.report.OpenReportAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.chart.Chart;

public class BackToParentAction extends AbstractAction {

    private boolean cancel = false;

    public BackToParentAction () {
        putValue(Action.NAME, I18NSupport.getString("back.parent.action.name"));
        Icon icon = ImageUtil.getImageIcon("back");
        putValue(Action.SMALL_ICON, icon);
        putValue(MNEMONIC_KEY, new Integer('B'));       
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("back.parent.action.name"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("back.parent.action.name"));
    }

    public void actionPerformed(ActionEvent e) {
        if (!Globals.isInner()) {
        	return;
        }
        
        String message;
        final boolean wasChart = Globals.isChartLoaded();
        if (wasChart) {
        	message = "save.chart";
        } else {
        	message = "save.report";
        }
                
		if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString(message))) {
            return;
        }
		
		final String subPath = Globals.popPath();
		String path;		
		if (Globals.isInner()) {
			// we are in another subreport
			path = Globals.peekPath();
		} else {
			// take the master
			path = Globals.popPath();
		}
		
		// open parent report			
		final String baseParentName = FileUtil.getBaseFileName(path);		
		final String baseSubName = FileUtil.getBaseFileName(subPath);		
		OpenReportAction openAction = new OpenReportAction() {
			public void afterOpen() {				    
				    
				    if (!Globals.isInner()) {
				    	Globals.getMainToolBar().enableBackAction(false, "");				    	
				    } else {
				    	String parentPath = Globals.peekPrePath();				    	
				    	Globals.getMainToolBar().enableBackAction(true, new File(parentPath).getName());			
				    }
				    
				    // save report/chart on parent report
					if (wasChart) {						
						ChartBandElement cbe = LayoutHelper.getReportLayout().getChartBandElement(baseSubName);						
						if (cbe != null) {
							final Chart chart = ChartUtil.loadChart(subPath);
							cbe.setChart(chart);
						}
					} else {
						ReportBandElement rbe = LayoutHelper.getReportLayout().getReportBandElement(baseSubName + FormSaver.REPORT_FULL_EXTENSION);						
						if (rbe != null) {
							final Report report = FormLoader.getInstance().load(subPath, false);
							rbe.setReport(report);
						}
					}
					new SaveAction().actionPerformed(null);
														    	
				    // delete temporary file and temporary folder				    
				    new File(subPath).delete();		
				    if (!Globals.isInner()) {				    	
				    	String tempPath;
				    	if (wasChart) {	
				    		tempPath = FileReportPersistence.getChartsAbsolutePath() + File.separator + FileReportPersistence.SUBREPORT_TEMP_DIR;
				    	} else {
				    		tempPath = FileReportPersistence.getReportsAbsolutePath() + File.separator + FileReportPersistence.SUBREPORT_TEMP_DIR;
				    	}
				    	FileUtil.deleteDir(new File(tempPath));
				    }
			}
		};		
		openAction.setReportName(baseParentName);
	    openAction.setReportPath(path);			 
	    openAction.setTestInner(false);
	    openAction.actionPerformed(new ActionEvent(this, 0, ""));
	    	  	    	   
    }

}
