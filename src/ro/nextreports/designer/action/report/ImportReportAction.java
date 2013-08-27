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
package ro.nextreports.designer.action.report;

import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.ReleaseInfoAdapter;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TreeUtil;
import ro.nextreports.designer.util.file.ReportFilter;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 24, 2008
 * Time: 1:51:36 PM
 */
public class ImportReportAction extends AbstractAction {

    private String destinationPath;

    public ImportReportAction(String destinationPath) {
        this(destinationPath, true);
    }

    public ImportReportAction(String destinationPath, boolean fullName) {
        this.destinationPath = destinationPath;
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("import.report"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("open.report.small"));
        }
        Icon icon = ImageUtil.getImageIcon("report_import");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("import.report"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("import.report"));                
    }

    public ImportReportAction() {
        this(null, true);
    }

    public ImportReportAction(boolean fullName) {
        this(null, fullName);
    }

    public void actionPerformed(ActionEvent e) {

        if (!LicenseUtil.allowToAddAnotherReport()) {
            return;
        }

        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        DataSource ds = manager.getConnectedDataSource();
        if(ds == null) {
            Show.info(I18NSupport.getString("no.data.source.connected"));
            return;
        }
        String name = FileReportPersistence.getReportsRelativePath();
        File destFolder;
        if (destinationPath == null) {
            destFolder = new File(name);
        } else {
            destFolder = new File(destinationPath);
        }        

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(I18NSupport.getString("import.report.title", ds.getName()));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ReportFilter());
        String importPath = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.NEXT_REPORT_IMPORT_PATH);
        if (importPath == null) {
            importPath = FileReportPersistence.CONNECTIONS_DIR;
        }
        fc.setCurrentDirectory(new File(importPath));
        int returnVal = fc.showSaveDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (f != null) {                
                try {
                    
                    byte status = ReportUtil.isValidReportVersion(f.getAbsolutePath());                    
                    if (ReportUtil.REPORT_INVALID_OLDER == status) {
                        Show.error(I18NSupport.getString("report.version.invalid.older"));
                        return;
                    } else if (ReportUtil.REPORT_INVALID_NEWER == status) {
                        Show.error(I18NSupport.getString("report.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
                        return;
                    }

                    File destFile = new File(destFolder.getAbsolutePath() + File.separator + f.getName());

                    boolean copy= false;
                    if (destFile.exists()) {
                        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), I18NSupport.getString("import.report.exists", f.getName()), "", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            copy = true;
                        }
                    } else {
                        copy = true;
                    }
                    if (copy) {
                        FileUtil.copyToDir(f, destFolder, true);
                        FileUtil.copyImages(f, destFolder);
                        TreeUtil.refreshReports();
                        ReporterPreferencesManager.getInstance().storeParameter(
                            ReporterPreferencesManager.NEXT_REPORT_IMPORT_PATH ,f.getParentFile().getAbsolutePath());
                        Show.info(I18NSupport.getString("import.report.success"));
                    }
                } catch (Exception ex) {
                    Show.error(ex);
                }
            }
        }

    }

}

