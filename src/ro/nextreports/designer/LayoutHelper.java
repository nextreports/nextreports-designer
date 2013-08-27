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

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.ReportLayout;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 7, 2006
 * Time: 10:57:41 AM
 */
public class LayoutHelper {

    public static String DEFAULT_REPORT_TITLE = I18NSupport.getString("report.default.title");
    public static String DEFAULT_CHART_TITLE = I18NSupport.getString("chart.default.title");

    private static ReportLayout reportLayout;

    static {
        reportLayout = new ReportLayout();
    }    

    public static ReportLayout getReportLayout() {
        return reportLayout;
    }

    public static void setReportLayout(ReportLayout reportLayout) {
        LayoutHelper.reportLayout = reportLayout;
    }

    public static void reset() {
        reportLayout = new ReportLayout();
    }

    public static void fetchDefaultReportTitle() {
        DEFAULT_REPORT_TITLE = I18NSupport.getString("report.default.title");
        DEFAULT_CHART_TITLE = I18NSupport.getString("chart.default.title");
    }
}
