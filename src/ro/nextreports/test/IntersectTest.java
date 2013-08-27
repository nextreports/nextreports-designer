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
package ro.nextreports.test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.nextreports.designer.chart.ChartUtil;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.ReportUtil;

public class IntersectTest {
	
	public static void main(String[] args) {
		String path1 = "D:\\Public\\next-reports\\output\\Demo\\Reports\\Timesheet.report";
		String path2 = "D:\\Public\\next-reports\\output\\Demo\\Charts\\Timesheet.chart";
		
		List<Report> list = new ArrayList<Report>();
		try {
			Report report1 = ReportUtil.loadReport(new FileInputStream(path1));
			list.add(report1);
			
			Report report2 = ChartUtil.loadChart(new FileInputStream(path2)).getReport();
			list.add(report2);
			
			Map<String, QueryParameter> map = ParameterUtil.intersectParametersMap(list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
