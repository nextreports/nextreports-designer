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
package ro.nextreports.designer.template.report.action;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.template.report.TemplateFileFilter;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;

import java.io.File;
import java.awt.event.ActionEvent;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 22-May-2009
// Time: 14:11:36

//
public class ExtractTemplateAction extends AbstractAction {

    private File savedFile;

    public ExtractTemplateAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("extract.template"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("template_extract"));
        putValue(Action.MNEMONIC_KEY, new Integer('E'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
//                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("extract.template"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("extract.template"));
    }

    public void actionPerformed(ActionEvent ev) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new TemplateFileFilter());
        chooser.setDialogTitle(I18NSupport.getString("extract.template"));
        
        String path = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY);               
        File prevFile;
        if (path == null) {
        	String loadPath = System.getProperty("nextreports.user.data") + File.separator + "templates" +File.separator + "Analyse.ntempl";		           
            prevFile = new File(loadPath);
        } else {
        	prevFile = new File(path);
        }

        if (prevFile.exists()) {
            chooser.setSelectedFile(prevFile);
        }

        int returnVal = chooser.showSaveDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            path = file.getAbsolutePath();
            if (!path.endsWith(TemplateFileFilter.TEMPLATE_FILE_EXT)) {
                path += TemplateFileFilter.TEMPLATE_FILE_EXT;
            }
            savedFile = new File(path);

            boolean ok = false;
            if (savedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                        I18NSupport.getString("extract.template.overwrite", savedFile.getName()), "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    ok = true;
                }
            } else {
                ok = true;
            }

            if (ok) {
                ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY, path);
                try {
                    TemplateManager.saveTemplate(TemplateManager.getGeneralTemplate(LayoutHelper.getReportLayout()), path);
                } catch (Exception e) {
                    Show.error(e);
                }
            }
        }
    }
}
