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
package ro.nextreports.designer.action.report.layout.export;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.XlsExporter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;

/**
 * @author mihai.panaitescu
 */
public class ExportToExcelAction extends ExportAction {

    public ExportToExcelAction(Report report) {
		super(report);
        exportType = ReportRunner.EXCEL_FORMAT;
        putValue(NAME, I18NSupport.getString("export.excel.short.desc"));
		putValue(SMALL_ICON, ImageUtil.getImageIcon("excel"));
		putValue(MNEMONIC_KEY, new Integer('E'));
		putValue(SHORT_DESCRIPTION, I18NSupport.getString("export.excel.short.desc"));
		putValue(LONG_DESCRIPTION, I18NSupport.getString("export.excel.long.desc"));
	}	
	
	@Override
	protected String getFileExtension() {		
		return "xls";
	}

	@Override
	protected ResultExporter getResultExporter(ExporterBean bean) {
		ResultExporter exporter = new XlsExporter(bean);
		exporter.setImageChartPath(Globals.USER_DATA_DIR + "/reports");
		return exporter;
	}
	
	@Override
	protected void afterExport(String filePath, String reportName)  {    	
		XlsExporter.createSummaryInformation(filePath, reportName);
    }
}
