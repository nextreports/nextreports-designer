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
package ro.nextreports.designer.template.chart.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JFileChooser;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.template.chart.TemplateFileFilter;
import ro.nextreports.designer.template.chart.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.ShortcutsUtil;

import ro.nextreports.engine.template.ChartTemplate;

public class ApplyTemplateAction extends AbstractAction {

    private Window owner;    
    private File selected;

    public ApplyTemplateAction() {
        this(Globals.getMainFrame());
    }

    public ApplyTemplateAction(Window owner) {
        super();
        this.owner = owner;                  	
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("template_load"));
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("template.apply.mnemonic",  new Integer('A')));        
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("apply.template"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("apply.template"));
    }

    public void actionPerformed(ActionEvent ev) {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new TemplateFileFilter());
        chooser.setDialogTitle(I18NSupport.getString("apply.template"));

        String path = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY);
        String loadPath = null;
        File loadFile = null;
        File file;
        if (path == null) {
            loadPath = ".";
            file = new File(loadPath);
            loadPath = file.getAbsolutePath() + File.separator + "templates";
            file = new File(loadPath);
        } else {
            file = new File(path);
        }

        if (file.exists()) {
            chooser.setSelectedFile(file);
        }
        if (path != null) {
            loadFile = file;
        }

        int returnVal = chooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selected = chooser.getSelectedFile();
            selection();
            
            ChartTemplate template = TemplateManager.loadTemplate(selected);
            TemplateManager.applyGeneralTemplate(Globals.getChartDesignerPanel().getChart(), template);
            ExtendedColorChooser.loadColorsFromChartTemplate(template);
                            
            ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY, selected.getAbsolutePath());
        }

    }

    public File getSelectedFile() {
        return selected;
    }

    protected void selection()  {
    }

}
