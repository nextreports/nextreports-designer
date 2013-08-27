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
package ro.nextreports.designer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.querybuilder.BrowserDialog;
import ro.nextreports.designer.querybuilder.BrowserPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.util.LoadReportException;
import ro.nextreports.engine.util.ReportUtil;

public class ReportCellEditor extends DefaultGridCellEditor {
	
	private BrowserPanel browser;
	private BrowserDialog dialog;
	private ReportBandElement bandElement;
	
	public ReportCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }
	
	@Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new ReportDelegate();
        }

        return isEditable;
    }

    class ReportDelegate extends EditorDelegate {

    	ReportDelegate() {
    		browser = new BrowserPanel(BrowserPanel.REPORT_BROWSER);
            dialog = new BrowserDialog(browser);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
        }

        public void setValue(Object value) {
            bandElement = (ReportBandElement) value;            

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                	dialog.setVisible(true);
                	if (dialog.okPressed()) {                		
                		String reportPath = browser.getSelectedFilePath();
                		Report report = null;
						try {
							report = ReportUtil.loadReport(new FileInputStream(reportPath));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (LoadReportException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		List<String> names = NextReportsUtil.incompatibleParametersType(report);
                		if (names.size() > 0) {
                			cancelCellEditing();
                			new ClearCellAction().actionPerformed(null);
                			Show.info(I18NSupport.getString("insert.report.action.incompatible", names));
                		} else {
                			bandElement.setReport(report);
                			stopCellEditing();
                		}
                    } else {
                        cancelCellEditing();
                        if (bandElement.getReport() == null) {
                            new ClearCellAction().actionPerformed(null);
                        }
                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            String reportPath = browser.getSelectedFilePath();
    		Report report = null;
			try {
				report = ReportUtil.loadReport(new FileInputStream(reportPath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LoadReportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		bandElement.setReport(report);             		
            registerUndoRedo(oldLayout, I18NSupport.getString("edit.report"), I18NSupport.getString("edit.report.insert"));
            return bandElement;
        }

    }

}
