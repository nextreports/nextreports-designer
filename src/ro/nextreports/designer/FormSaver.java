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

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.SaveEntityDialog;
import ro.nextreports.designer.querybuilder.SaveEntityPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import com.thoughtworks.xstream.XStream;

/**
 * @author Decebal Suiu
 */
public class FormSaver {

    private static Log LOG = LogFactory.getLog(FormSaver.class);
    public static final String REPORT_EXTENSION_SEPARATOR = ".";
    public static final String REPORT_EXTENSION = "report";
    public static final String REPORT_FULL_EXTENSION = REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION;

    //////////////////////
    // variables

    private static FormSaver instance = new FormSaver();

    //////////////////////
    // business

    public static FormSaver getInstance() {
        return instance;
    }

    public String save(String title, boolean as) {
        return save(title, as, null);
    }

    public String save(String title, boolean as, Report report) {
        if (!as && (Globals.getCurrentReportAbsolutePath() != null)) {
            File file = new File(Globals.getCurrentReportAbsolutePath());
            save(file, report);
            return getReportFileName(file);
        } else {
            return askSave(title, report);
        }
    }

    private String askSave(String title) {
        return askSave(title, null);
    }

    private String askSave(String title, Report report) {
        SaveEntityPanel savePanel = new SaveEntityPanel(I18NSupport.getString("save.report"), DBObject.REPORTS_GROUP);
        SaveEntityDialog dialog = new SaveEntityDialog(title, savePanel, I18NSupport.getString("report"), true);
        dialog.pack();
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.requestFocus();
        dialog.setVisible(true);

        String name = null;
        if (dialog.okPressed()) {
            Globals.setCurrentReportAbsolutePath(null);
            name = savePanel.getName();
        } else {
            return null;
        }
        if (name == null) {
            return null;
        }

        File parent = new File(FileReportPersistence.getConnectedDataSourceRelativePath());
        if (!parent.exists()) {
            parent.mkdirs();
            new File(FileReportPersistence.getQueriesRelativePath()).mkdirs();
            new File(FileReportPersistence.getReportsRelativePath()).mkdirs();
            new File(FileReportPersistence.getChartsRelativePath()).mkdirs();
        }
       
        name = savePanel.getFolderPath() + File.separator + name + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION;        
        File selectedFile = new File(name);

		if (!name.endsWith(REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION)) {
			selectedFile = new File(selectedFile.getAbsolutePath() + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
		}
		Globals.setCurrentReportAbsolutePath(selectedFile.getAbsolutePath());
		save(selectedFile, report);
		if (dialog.isOverwrite()) {
			return null;
		}
		return getReportFileName(selectedFile);         
    }

    public String getReportFileName(File file) {
        return getReportFileName(file.getName());
    }

    public String getReportFileName(String name) {
        return name.substring(0, name.indexOf(REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION));
    }

    public void save(File file) {
        SaveThread thread = new SaveThread(file);
        thread.start();
        try {
            thread.join();
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public boolean save(File file, Report report) {
        try {
            saveXStream(file, report);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    class SaveThread extends Thread {

        private File file;
        private Report report;

        public SaveThread(File file){
            this(file, null);
        }

        public SaveThread(File file, Report report){
            setName("NEXT : " + getClass().getSimpleName());
            this.file = file;
            this.report = report;
        }

        public void run() {
            try {
                saveXStream(file, report);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }



    }

    private void saveXStream(File file, Report report) throws Exception {        
        XStream xstream = XStreamFactory.createXStream();
        FileOutputStream fos = new FileOutputStream(file);
        if (report == null) {
            report = ro.nextreports.designer.Globals.getMainFrame().getQueryBuilderPanel().createReport(file.getName());
            report.setLayout(LayoutHelper.getReportLayout());
        }
        xstream.toXML(report, fos);
        fos.close();
    }

    public boolean deleteReport(String path) {
        return new File(path).delete();
    }

    public boolean renameReport(String oldName, String newName, String parentPath) {                        
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
		DataSource ds = manager.getConnectedDataSource();
		File file = new File(parentPath+ File.separator + oldName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
        File newFile = new File(parentPath + File.separator + newName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
        boolean result = file.renameTo(newFile);
        if (result) {
            if (file.getAbsolutePath().equals(Globals.getCurrentReportAbsolutePath())) {
                Globals.setCurrentReportAbsolutePath(newFile.getAbsolutePath());
            }
            // change name in xml
            Report report = FormLoader.getInstance().load(newFile.getAbsolutePath());
            report.setName(newName + REPORT_EXTENSION_SEPARATOR + REPORT_EXTENSION);
            try {
				saveXStream(newFile, report);
			} catch (Exception e) {				
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
        }
        return result;
    }

}
