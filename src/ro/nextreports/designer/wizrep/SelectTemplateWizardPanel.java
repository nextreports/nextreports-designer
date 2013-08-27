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
package ro.nextreports.designer.wizrep;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.template.report.TemplateFileFilter;
import ro.nextreports.designer.template.report.TemplatePreviewPanel;
import ro.nextreports.designer.template.report.action.ApplyTemplateAction;
import ro.nextreports.designer.template.report.action.CreateTemplateAction;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 10, 2008
 * Time: 1:28:01 PM
 */
public class SelectTemplateWizardPanel extends WizardPanel {

    private JTextField templateText;
    private JButton templateButton;
    private JButton createTemplateButton;
    private Dimension dim = new Dimension(200, 20);
    private Dimension buttonDim = new Dimension(20, 20);
    private Dimension scrDim = new Dimension(200, 150);
    private File selectedTemplate;
    private CreateTemplateAction templateAction = new CreateTemplateAction();
    private JXList defTemplateList = new JXList();
    private DefaultListModel defTemplateModel = new DefaultListModel();
    private TemplatePreviewPanel previewPanel = new TemplatePreviewPanel();

    public SelectTemplateWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",5,5) + I18NSupport.getString("wizard.panel.seltemplate.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.seltemplate.subtitle"));
        init();
    }

    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
        if (defTemplateModel.size() > 0) {
            defTemplateList.requestFocus();
        }
        JDialog mainDialog = (JDialog)context.getAttribute(WizardConstants.MAIN_FRAME);
        ApplyTemplateAction action = new ApplyTemplateAction(mainDialog, false, true) {
            protected void selection() {
                selectedTemplate = getSelectedFile();
                templateText.setText(selectedTemplate.getName());
            }
        };
        templateButton.setAction(action);
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {
        return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return null;
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return true;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List<String> messages) {
        WizardUtil.openReport(context, selectedTemplate);        
        return true;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

    private void init() {
        setLayout(new BorderLayout());
        templateText = new JTextField();
        JScrollPane scr = new JScrollPane();
        scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scr.setMinimumSize(scrDim);
		scr.setPreferredSize(scrDim);
		scr.getViewport().add(defTemplateList, null);
		defTemplateList.setModel(defTemplateModel);

        defTemplateList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (defTemplateList.getSelectedIndex() > 0) {
                    previewPanel.setFileTemplate(getSelectedTemplateFile());
                } else {
                    previewPanel.setFileTemplate(null);
                }
                chooseTemplate();
            }
        });        
        populateDefTemplates();

        JPanel qPanel = new JPanel(new GridBagLayout());

        JLabel defTemplateLabel =new JLabel(I18NSupport.getString("wizard.panel.seltemplate.default.label"));
        JLabel templateLabel =new JLabel(I18NSupport.getString("wizard.panel.seltemplate.label"));

        templateText.setPreferredSize(dim);
        templateText.setEditable(false);
        templateButton = new JButton();
        templateButton.setPreferredSize(buttonDim);
        templateButton.setMaximumSize(buttonDim);
        templateButton.setMinimumSize(buttonDim);
        createTemplateButton = new JButton();
        createTemplateButton.setIcon(ImageUtil.getImageIcon("template_create"));
        createTemplateButton.setToolTipText(I18NSupport.getString("create.template"));
        createTemplateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templateAction.actionPerformed(e);
                selectedTemplate = templateAction.getSavedFile();
                if (selectedTemplate !=  null) {
                    populateDefTemplates();
                    templateText.setText(selectedTemplate.getName());
                }
            }
        });
        createTemplateButton.setPreferredSize(buttonDim);
        createTemplateButton.setMaximumSize(buttonDim);
        createTemplateButton.setMinimumSize(buttonDim);

        JPanel lowPanel = new JPanel(new GridBagLayout());
        lowPanel.add(templateLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 0, 5, 0), 0, 0));
        lowPanel.add(templateText, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        lowPanel.add(templateButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        lowPanel.add(createTemplateButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        lowPanel.add(new JLabel(""), new GridBagConstraints(4, 0, 1, 2, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        qPanel.add(defTemplateLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        qPanel.add(scr, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(5, 5, 5, 0), 0, 0));
        qPanel.add(previewPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(5, 5, 5, 0), 0, 0));
        qPanel.add(lowPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        add(qPanel, BorderLayout.CENTER);
    }

    private void chooseTemplate() {
        if (defTemplateList.getSelectedIndex() > 0) {
            selectedTemplate = getSelectedTemplateFile();
            templateText.setText(selectedTemplate.getName());
        } else {
            templateText.setText("");
            selectedTemplate = null;
        }
    }

    private void populateDefTemplates() {
        defTemplateModel.clear();
        defTemplateModel.addElement(I18NSupport.getString("wizard.panel.seltemplate.none"));
        File[] files = new File(Globals.USER_DATA_DIR + "/templates").listFiles();
        for (File f : files) {
            if (f.getName().endsWith(TemplateFileFilter.TEMPLATE_FILE_EXT)) {
                defTemplateModel.addElement(f.getName().substring(0, f.getName().indexOf(TemplateFileFilter.TEMPLATE_FILE_EXT)));
            }
        }
        if (defTemplateModel.size() > 0) {
            defTemplateList.setSelectedIndex(0);
        }
    }

    private File getSelectedTemplateFile() {
        String template = (String)defTemplateList.getSelectedValue() + TemplateFileFilter.TEMPLATE_FILE_EXT;
        return new File(Globals.USER_DATA_DIR + "/templates" + File.separator + template);
    }

}

