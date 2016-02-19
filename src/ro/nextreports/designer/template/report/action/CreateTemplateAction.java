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
import ro.nextreports.designer.template.report.CreateTemplatePanel;
import ro.nextreports.designer.template.report.TemplateFileFilter;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 22, 2007
 * Time: 5:59:28 PM
 */
public class CreateTemplateAction extends AbstractAction {

    private File savedFile;

    public CreateTemplateAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("create.template"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("template_create"));
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
//                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("create.template"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("create.template"));
    }

    public void actionPerformed(ActionEvent ev) {

        final CreateTemplatePanel panel = new CreateTemplatePanel(true);
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("create.template"), true) {

			@Override
			protected boolean ok() {
				JFileChooser chooser = new JFileChooser();        
		        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        chooser.addChoosableFileFilter(new TemplateFileFilter());
		        chooser.setDialogTitle(I18NSupport.getString("create.template"));
		        
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
		           
				if (savedFile == null) {
					int returnVal = chooser.showSaveDialog(Globals.getMainFrame());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						path = file.getAbsolutePath();
						if (!path.endsWith(TemplateFileFilter.TEMPLATE_FILE_EXT)) {
							path += TemplateFileFilter.TEMPLATE_FILE_EXT;
						}
						savedFile = new File(path);
						ReporterPreferencesManager.getInstance()
								.storeParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY, path);
						try {
							TemplateManager.saveTemplate(panel.getTemplate(),path);
						} catch (Exception e) {
							Show.error(e);
						}
					}
				} else {
					try {
						TemplateManager.saveTemplate(panel.getTemplate(),savedFile.getAbsolutePath());
					} catch (Exception e) {
						Show.error(e);
					}
				}
		        // stay in template dialog
		        return false;
			}
        	
        };
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);                                
    }

    public File getSavedFile() {
        return savedFile;
    }
}
