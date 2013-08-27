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

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.ChartBandElement;

public class ExtractChartAction  extends AbstractAction{
	
	 public ExtractChartAction() {
	        super();
	        putValue(Action.NAME, I18NSupport.getString("extract.chart.action.name"));
	    }

	    public void actionPerformed(final ActionEvent event) {
	        final ReportGrid grid = Globals.getReportGrid();
			SelectionModel selectionModel = grid.getSelectionModel();			
			Cell cell = selectionModel.getSelectedCell();
			ChartBandElement cbe = (ChartBandElement)grid.getBandElement(cell.getRow(), cell.getColumn());
			if ((cbe != null) && (cbe.getChart() != null)) {
				ChartUtil.saveChart(I18NSupport.getString("save.chart"), true, cbe.getChart());
			}
	    }
}
