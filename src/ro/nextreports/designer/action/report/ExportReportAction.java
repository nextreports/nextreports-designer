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


import javax.swing.*;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.io.File;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 10-Apr-2009
// Time: 12:08:52

//
public class ExportReportAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public ExportReportAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("export.report"));
        Icon icon = ImageUtil.getImageIcon("report_export");
        putValue(Action.SMALL_ICON, icon);
        //putValue(Action.MNEMONIC_KEY, new Integer('Y'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("export.report"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("export.report"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent e) {

        String name = selectedNode.getDBObject().getName() + FormSaver.REPORT_EXTENSION_SEPARATOR +
                FormSaver.REPORT_EXTENSION;
        String absolutePath = selectedNode.getDBObject().getAbsolutePath();

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(I18NSupport.getString("export.report.title"));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String exportPath = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.NEXT_REPORT_EXPORT_PATH);
        if (exportPath == null) {
            exportPath = FileReportPersistence.CONNECTIONS_DIR;
        }
        fc.setCurrentDirectory(new File(exportPath));
        int returnVal = fc.showSaveDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (f != null) {
                try {
                    File destFile = new File(f.getAbsolutePath() + File.separator + name);
                    boolean copy = false;
                    if (destFile.exists()) {
                        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), I18NSupport.getString("export.report.exists", name), "", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            copy = true;
                        }
                    } else {
                        copy = true;
                    }
                    if (copy) {
                        f.mkdirs();
                        File reportFile = new File(absolutePath);
                        FileUtil.copyToDir(reportFile, f, true);
                        FileUtil.copyImages(reportFile, f);
                        ReporterPreferencesManager.getInstance().storeParameter(
                            ReporterPreferencesManager.NEXT_REPORT_EXPORT_PATH ,f.getAbsolutePath());
                        Show.info(I18NSupport.getString("export.report.success"));
                    }
                } catch (Exception ex) {
                    Show.error(ex);
                }
            }
        }

    }

}
