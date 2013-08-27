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


import ro.nextreports.engine.FluentReportRunner;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportRunner;
import ro.nextreports.engine.util.ReportUtil;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.persistence.FileReportPersistence;

/**
 * @author Decebal Suiu
 */
public class FluentReportRunnerTest {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            DataSource ds = createDataSource();
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(ds.getUrl(), ds.getUser(), ds.getPassword());
            final Report report = load(ds, "Timesheet");
            System.out.println("report="+report);

            FileOutputStream stream = new FileOutputStream("test.html");
            FluentReportRunner.report(report)
            	.connectTo(connection)
            	.withQueryTimeout(60)
            	.withParameterValues(createParameterValues())
            	.formatAs(ReportRunner.HTML_FORMAT)
            	.run(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> createParameterValues() {
        Map<String, Object> parameterValues = new HashMap<String, Object>();
        parameterValues.put("Project", new Object[] {1, 2, 3});
        parameterValues.put("Name", new Object[] {1, 2, 3, 4});

        // for empty list
        //parameterValues.put("Name", new Object[]{ParameterUtil.NULL});

        Calendar cal = Calendar.getInstance();
        cal.set(2008, Calendar.SEPTEMBER, 1);
        parameterValues.put("start_date", cal.getTime());
        cal.set(2009, Calendar.JANUARY, 1);
        parameterValues.put("end_date", cal.getTime());
        return parameterValues;
    }

    public static DataSource createDataSource() {
        DataSource source = new DataSource();
        source.setName("Demo");
        source.setType("Derby Embedded");
        source.setDriver("org.apache.derby.jdbc.EmbeddedDriver");
        source.setUrl("jdbc:derby:demo/data;create=false");
        return source;
    }

    public static Report load(DataSource ds, String reportName) {
        FileInputStream fis = null;        
        try {
            fis = new FileInputStream(FileReportPersistence.CONNECTIONS_DIR + File.separator + ds.getName() +
                    File.separator + FileReportPersistence.REPORTS_FOLDER +
                    File.separator + reportName + FormSaver.REPORT_EXTENSION_SEPARATOR +
                    FormSaver.REPORT_EXTENSION);            
            return ReportUtil.loadReport(fis);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
