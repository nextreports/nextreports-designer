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
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.JSONFullExporter;
import ro.nextreports.engine.exporter.ResultExporter;

/**
 * @author daniel.avieritei
 */
public class ExportToJSONFullAction extends ExportAction {

	public ExportToJSONFullAction(Report report) {
		super(report);
		exportType = ReportRunner.JSON_SIMPLE_FORMAT;
		putValue(NAME, I18NSupport.getString("export.jsonfull.short.desc"));
		putValue(SMALL_ICON, ImageUtil.getImageIcon("csv"));
		putValue(MNEMONIC_KEY, new Integer('J'));
		putValue(SHORT_DESCRIPTION, I18NSupport.getString("export.jsonfull.short.desc"));
		putValue(LONG_DESCRIPTION, I18NSupport.getString("export.jsonfull.long.desc"));
	}

	@Override
	protected String getFileExtension() {
		return "json";
	}

	@Override
	protected ResultExporter getResultExporter(ExporterBean bean) {
		JSONFullExporter exporter = new JSONFullExporter(bean, Globals.getCsvDelimiter());
		exporter.setImageChartPath(Globals.USER_DATA_DIR + "/reports");
		return exporter;
	}
}
