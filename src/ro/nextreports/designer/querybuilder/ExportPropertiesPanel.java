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
package ro.nextreports.designer.querybuilder;

import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.ReportRunner;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 16, 2007
 * Time: 1:55:06 PM
 */
public class ExportPropertiesPanel extends JPanel {

    private JLabel fileLabel = new JLabel(I18NSupport.getString("export.properties.file.name"));
    private JTextField fileText = new JTextField();
    private JRadioButton portraitButton = new JRadioButton(I18NSupport.getString("export.properties.portrait"));
    private JRadioButton landscapeButton = new JRadioButton(I18NSupport.getString("export.properties.landscape"));
    private boolean layoutSelection = false;
    private JCheckBox headerCheck = new JCheckBox(I18NSupport.getString("export.properties.header.page"));
    private Dimension textDim = new Dimension(150, 20);

    public ExportPropertiesPanel(String notLoadedName, String exportType, boolean layoutSelection) {
        this.layoutSelection = layoutSelection;

        setLayout(new GridBagLayout());

        fileText.setPreferredSize(textDim);
        String name = Globals.getCurrentReportName();
        if (name == null) {
            if (notLoadedName != null) {
                name = notLoadedName;
            } else  {
                name = "";
            }
        }        
        name = name + String.valueOf(System.currentTimeMillis());
        fileText.setText(name);

        // no need for name of document at export 
//        add(fileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
//                GridBagConstraints.NONE, new Insets(5,0,5,0), 0, 0));
//        add(fileText, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
//                GridBagConstraints.HORIZONTAL, new Insets(5,5,5,0), 0, 0));

        if (layoutSelection) {

            ButtonGroup bg = new ButtonGroup();
            bg.add(portraitButton);
            bg.add(landscapeButton);
            portraitButton.setSelected(true);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(portraitButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(5,5)));
            buttonPanel.add(landscapeButton);
            buttonPanel.add(Box.createGlue());

            add(buttonPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5,0,5,0), 0, 0));

            // RTF has no support for now.
            if (ReportRunner.PDF_FORMAT.equals(exportType)) {
                add(headerCheck, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
            }

        }

    }

    public String getReportName() {
        if (fileText.getText().trim().equals("")) {
            return null;
        } else {
            return fileText.getText();
        }
    }

    public int getLayoutType() {
        if (!layoutSelection) {
            return ResultExporter.PORTRAIT;
        } else {
            if (portraitButton.isSelected()) {
                return ResultExporter.PORTRAIT;
            } else {
                return ResultExporter.LANDSCAPE;
            }
        }
    }

    public boolean getHeaderPerPage() {
        return headerCheck.isSelected();
    }
}
