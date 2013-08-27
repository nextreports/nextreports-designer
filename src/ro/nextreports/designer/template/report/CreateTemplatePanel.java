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
package ro.nextreports.designer.template.report;

import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.band.BandElement;

import javax.swing.*;

import ro.nextreports.designer.template.report.action.ApplyTemplateAction;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 8, 2008
 * Time: 4:22:03 PM
 */
public class CreateTemplatePanel extends JPanel {

    private Dimension panelDim = new Dimension(220, 115);
    private Dimension buttonDim = new Dimension(20, 20);
    private Dimension txtDim = new Dimension(150, 20);
    private ReportTemplate template;
    private TemplatePropertyPanel titleTemplatePanel;
    private TemplatePropertyPanel headerTemplatePanel;
    private TemplatePropertyPanel detailTemplatePanel;
    private TemplatePropertyPanel footerTemplatePanel;
    private JPanel cardPanel;
    private boolean create;
    private File file;
    private JRadioButton titleButton;
    private JRadioButton headerButton;
    private JRadioButton detailButton;
    private JRadioButton footerButton;

    public CreateTemplatePanel(final boolean create) {

        this.create = create;
        template = TemplateManager.createDefaultReportTemplate();
        final TemplatePreviewPanel panel = new TemplatePreviewPanel(template);

        JLabel templateLabel = new JLabel(I18NSupport.getString("modify.template.select"));
        final JTextField templateText= new JTextField();
        templateText.setPreferredSize(txtDim);
        templateText.setMinimumSize(txtDim);
        templateText.setMaximumSize(txtDim);        
        templateText.setEnabled(false);
        JButton templateButton = new JButton();
        templateButton.setPreferredSize(buttonDim);
        templateButton.setMaximumSize(buttonDim);
        templateButton.setMinimumSize(buttonDim);
        ApplyTemplateAction action = new ApplyTemplateAction(SwingUtilities.getWindowAncestor(this), false, true) {
            protected void selection() {                 
                file = getSelectedFile();
                templateText.setText(file.getName());
                template = TemplateManager.loadTemplate(file);
                titleTemplatePanel.setBandElement(template.getTitleBand());
                headerTemplatePanel.setBandElement(template.getHeaderBand());
                detailTemplatePanel.setBandElement(template.getDetailBand());
                footerTemplatePanel.setBandElement(template.getFooterBand());
                CardLayout layout = (CardLayout) cardPanel.getLayout();
                if (titleButton.isSelected()) {
                    layout.show(cardPanel, "title");
                } else if (headerButton.isSelected()) {
                    layout.show(cardPanel, "header");
                } else if (detailButton.isSelected()) {
                    layout.show(cardPanel, "detail");
                } else if (footerButton.isSelected()) {
                	layout.show(cardPanel, "footer");
                }
                panel.setReportTemplate(template);
            }
        };
        templateButton.setAction(action);
        templateButton.setToolTipText(I18NSupport.getString("modify.template.select"));
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.X_AXIS));
        selectionPanel.add(templateLabel);
        selectionPanel.add(Box.createHorizontalStrut(5));
        selectionPanel.add(templateText);
        selectionPanel.add(Box.createHorizontalStrut(5));
        selectionPanel.add(templateButton);
        selectionPanel.add(Box.createHorizontalGlue());

        titleButton = new JRadioButton(I18NSupport.getString("create.template.title"));
        titleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (create || (file !=  null)) {
                    CardLayout layout = (CardLayout) cardPanel.getLayout();
                    layout.show(cardPanel, "title");
                }
            }
        });


        headerButton = new JRadioButton(I18NSupport.getString("create.template.header"));
        headerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (create || (file != null)) {
                    CardLayout layout = (CardLayout) cardPanel.getLayout();
                    layout.show(cardPanel, "header");
                }
            }
        });

        detailButton = new JRadioButton(I18NSupport.getString("create.template.detail"));
        detailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (create || (file != null)) {
                    CardLayout layout = (CardLayout) cardPanel.getLayout();
                    layout.show(cardPanel, "detail");
                }
            }
        });
        
        footerButton = new JRadioButton(I18NSupport.getString("create.template.footer"));
        footerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (create || (file != null)) {
                    CardLayout layout = (CardLayout) cardPanel.getLayout();
                    layout.show(cardPanel, "footer");
                }
            }
        });


        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(titleButton);
        buttonGroup.add(headerButton);
        buttonGroup.add(detailButton);
        buttonGroup.add(footerButton);
        titleButton.setSelected(true);

        titleTemplatePanel = new TemplatePropertyPanel(template.getTitleBand()) {
            protected void propertySelection(BandElement bandElement) {
                template.setTitleBand(bandElement);
                panel.setReportTemplate(template);
            }
        };
        titleTemplatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titleTemplatePanel.setPreferredSize(panelDim);

        headerTemplatePanel = new TemplatePropertyPanel(template.getHeaderBand()) {
            protected void propertySelection(BandElement bandElement) {
                template.setHeaderBand(bandElement);
                panel.setReportTemplate(template);
            }
        };
        headerTemplatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        headerTemplatePanel.setPreferredSize(panelDim);

        detailTemplatePanel = new TemplatePropertyPanel(template.getDetailBand()) {
            protected void propertySelection(BandElement bandElement) {
                template.setDetailBand(bandElement);
                panel.setReportTemplate(template);
            }
        };
        detailTemplatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        detailTemplatePanel.setPreferredSize(panelDim);
        
        footerTemplatePanel = new TemplatePropertyPanel(template.getFooterBand()) {
            protected void propertySelection(BandElement bandElement) {
                template.setFooterBand(bandElement);
                panel.setReportTemplate(template);
            }
        };
        footerTemplatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        footerTemplatePanel.setPreferredSize(panelDim);

        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        if (!create) {
            JPanel nonePanel = new JPanel();
            nonePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            nonePanel.setPreferredSize(panelDim);
            cardPanel.add(nonePanel, "none");
        }
        cardPanel.add(titleTemplatePanel, "title");
        cardPanel.add(headerTemplatePanel, "header");
        cardPanel.add(detailTemplatePanel, "detail");
        cardPanel.add(footerTemplatePanel, "footer");


        JPanel elementPanel = new JPanel();
        elementPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        elementPanel.setLayout(new GridBagLayout());
        elementPanel.add(titleButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
        elementPanel.add(headerButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
        elementPanel.add(detailButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
        elementPanel.add(footerButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 10, 10, 10), 0, 0));
        elementPanel.add(new JLabel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        setLayout(new GridBagLayout());
        if (!create) {
           add(selectionPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0)); 
        }
        add(elementPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        add(cardPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
        add(panel, new GridBagConstraints(1, 1, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 0, 0), 0, 0));
    }


    public ReportTemplate getTemplate() {
        return template;
    }

    public File getFile() {
        return file;
    }
}
