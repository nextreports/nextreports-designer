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

import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ObjectCloner;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.template.report.ChooserTemplatePreviewPanel;
import ro.nextreports.designer.template.report.TemplateFileFilter;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 23, 2007
 * Time: 10:34:52 AM
 */
public class ApplyTemplateAction extends AbstractAction {

    private Window owner;
    private boolean onlySelection = false;
    private File selected;

    public ApplyTemplateAction(boolean withName) {
        this(Globals.getMainFrame(), withName, false);
    }

    public ApplyTemplateAction(Window owner, boolean withName, boolean onlySelection) {
        super();
        this.owner = owner;
        this.onlySelection = onlySelection;
        if (withName) {
            putValue(Action.NAME, I18NSupport.getString("apply.template"));
        }
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("template_load"));
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("template.apply.mnemonic",  new Integer('A')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("template.apply.accelerator", "control shift A")));
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

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        ChooserTemplatePreviewPanel previewPane = new ChooserTemplatePreviewPanel(loadFile);        
        chooser.setAccessory(previewPane);
	    chooser.addPropertyChangeListener(previewPane);

        int returnVal = chooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selected = chooser.getSelectedFile();
            selection();
            if (!onlySelection) {
                ReportTemplate template = TemplateManager.loadTemplate(selected);
                TemplateManager.applyGeneralTemplate(LayoutHelper.getReportLayout(), template);
                ExtendedColorChooser.loadColorsFromReportTemplate(template);

                ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
                Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("apply.template")));
            }
            ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.TEMPLATE_PATH_KEY, selected.getAbsolutePath());
        }

    }

    public File getSelectedFile() {
        return selected;
    }

    protected void selection()  {
    }

}
