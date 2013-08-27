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

import java.util.EventObject;
import java.util.List;

import javax.swing.*;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.querybuilder.BrowserDialog;
import ro.nextreports.designer.querybuilder.BrowserPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.chart.Chart;

public class ChartCellEditor extends DefaultGridCellEditor {
	
	private BrowserPanel browser;
	private BrowserDialog dialog;
	private ChartBandElement bandElement;
	
	public ChartCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }
	
	@Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new ChartDelegate();
        }

        return isEditable;
    }

    class ChartDelegate extends EditorDelegate {

    	ChartDelegate() {
    		browser = new BrowserPanel(BrowserPanel.CHART_BROWSER);
            dialog = new BrowserDialog(browser);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
        }

        public void setValue(Object value) {
            bandElement = (ChartBandElement) value;            

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                	dialog.setVisible(true);
                	if (dialog.okPressed()) {                		
                		String chartPath = browser.getSelectedFilePath();
                		Chart chart = ChartUtil.loadChart(chartPath);
                		List<String> names = ChartUtil.incompatibleParametersType(chart);
                		if (names.size() > 0) {
                			cancelCellEditing();
                			new ClearCellAction().actionPerformed(null);
                			Show.info(I18NSupport.getString("insert.chart.action.incompatible", names));
                		} else {
                			bandElement.setChart(chart);
                			stopCellEditing();
                		}
                    } else {
                        cancelCellEditing();
                        if (bandElement.getChart() == null) {
                            new ClearCellAction().actionPerformed(null);
                        }
                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            String chartPath = browser.getSelectedFilePath();
    		Chart chart = ChartUtil.loadChart(chartPath);
    		bandElement.setChart(chart);             		
            registerUndoRedo(oldLayout, I18NSupport.getString("edit.chart"), I18NSupport.getString("edit.chart.insert"));
            return bandElement;
        }

    }

}
