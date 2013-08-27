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
package ro.nextreports.designer.persistence;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 10, 2006
 * Time: 1:09:28 PM
 */
public class FileReportPersistence  implements ReportPersistence {

	public static final String OUTPUT_DIR = "output";
	public static final String CONNECTIONS_DIR = Globals.USER_DATA_DIR + "/" + OUTPUT_DIR;
	public static final String REPORT_EXTENSION_SEPARATOR = ".";
	public static final String REPORT_EXTENSION = "query";
    public static final String REPORT_FULL_EXTENSION = REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION;

    public static final String QUERIES_FOLDER = "Queries";
    public static final String REPORTS_FOLDER = "Reports";
    public static final String CHARTS_FOLDER = "Charts";
    
    public static final String SUBREPORT_TEMP_DIR = "~temporary";

    private static Log LOG = LogFactory.getLog(FileReportPersistence.class);

    public boolean saveReport(Report report, String path) {

		XStream xstream = XStreamFactory.createXStream();
		DataSourceManager manager = DefaultDataSourceManager.getInstance();
		DataSource ds = manager.getConnectedDataSource();

		FileOutputStream fos = null;
		try {
            File parent = new File(getConnectedDataSourceRelativePath());
            if (!parent.exists()) {
                parent.mkdirs();
                new File(getQueriesRelativePath()).mkdirs();
                new File(getReportsRelativePath()).mkdirs();
                new File(getChartsRelativePath()).mkdirs();
            }            
            fos = new FileOutputStream(path);
			xstream.toXML(report, fos);
			fos.flush();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
            LOG.error(e1.getMessage(), e1);
            return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public Report loadReport(String path) {

		XStream xstream = XStreamFactory.createXStream();
		DataSourceManager manager = DefaultDataSourceManager.getInstance();
		DataSource ds = manager.getConnectedDataSource();

		FileInputStream fis = null;
        InputStreamReader reader = null;
        try {			
            fis = new FileInputStream(path);
            reader = new InputStreamReader(fis, "UTF-8");
            return (Report)xstream.fromXML(reader);
		} catch (Exception e1) {
			e1.printStackTrace();
            LOG.error(e1.getMessage(), e1);
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

    public List<File> getReportFiles(String folderPath) {
        List<File> result = new ArrayList<File>();
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        DataSource ds = manager.getConnectedDataSource();
        if (ds != null) {
            File file = new File(folderPath);
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0, size = files.length; i < size; i++) {
                    int index = files[i].getName().indexOf(REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
                    if (index != -1) {
                        if (files[i].getName().endsWith(REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION)) {
                            result.add(files[i]);
                        }
                    } else {
                        if (file.isDirectory()) {
                            result.add(files[i]);
                        }
                    }
                }
            }
        }
        Collections.sort(result, new Comparator<File>() {
            public int compare(File o1, File o2) {
                if ((o1.isDirectory() && o2.isDirectory()) || (o1.isFile() && o2.isFile())) {
                    return Collator.getInstance().compare(o1.getName(), o2.getName());
                } else {
                    if (o1.isDirectory()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
        return result;
    }

	public boolean deleteReport(String path) {		
		File file = new File(path);
		return file.delete();
	}

	public boolean renameReport(String oldName, String newName, String parentPath) {        
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
		DataSource ds = manager.getConnectedDataSource();
		File file = new File(parentPath+ File.separator + oldName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
		File newFile = new File(parentPath + File.separator + newName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
		boolean result = file.renameTo(newFile);
        if (result) {
            if (file.getAbsolutePath().equals(Globals.getCurrentQueryAbsolutePath())) {
                Globals.setCurrentQueryAbsolutePath(newFile.getAbsolutePath());
            }
            // change name in xml
            Report report = loadReport(newFile.getAbsolutePath());
            report.setName(newName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
            saveReport(report,newFile.getAbsolutePath());
        }
        return result;
    }

    public static String getConnectedDataSourceRelativePath() {        
        return CONNECTIONS_DIR + File.separator + DefaultDataSourceManager.getInstance().getConnectedDataSource().getName();
    }

    public static String getConnectedDataSourceAbsolutePath() {
        return new File(CONNECTIONS_DIR + File.separator + DefaultDataSourceManager.getInstance().getConnectedDataSource().getName()).getAbsolutePath();
    }

    public static String getQueriesRelativePath() {
        return getConnectedDataSourceRelativePath() + File.separator + QUERIES_FOLDER;
    }

    public static String getQueriesAbsolutePath() {
        return new File(getQueriesRelativePath()).getAbsolutePath();
    }

    public static String getReportsRelativePath() {
        return getConnectedDataSourceRelativePath() + File.separator + REPORTS_FOLDER;
    }

    public static String getReportsAbsolutePath() {
        return new File(getReportsRelativePath()).getAbsolutePath();
    }

    public static String getChartsRelativePath() {
        return getConnectedDataSourceRelativePath() + File.separator + CHARTS_FOLDER;
    }

    public static String getChartsAbsolutePath() {
        return new File(getChartsRelativePath()).getAbsolutePath();
    }

}
