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
package ro.nextreports.designer.config;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;

import org.jdesktop.swingx.JXTitledSeparator;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 16-Jun-2009
// Time: 11:40:04


import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LocaleUtil;

//
public class SettingsPanel extends JPanel {

	private JTextField conTimeoutText;
    private JTextField queryTimeoutText;
    private JCheckBox accCheck;
    private JCheckBox maxCheck;
    private JTextField tnsText;
    private JTextField fontText;
    private JCheckBox a4Check;
    private JTextField csvDelimiterText;
    private JCheckBox rulerCheck;
    private JComboBox rulerUnitCombo;
    private JTextField chartPortText;
    private JComboBox localeCombo;

    private Dimension txtDim = new Dimension(150, 20);
    private Dimension btnDim = new Dimension(20,20);

    public SettingsPanel() {

        setLayout(new GridBagLayout());
        
        conTimeoutText = new JTextField();
        conTimeoutText.setPreferredSize(txtDim);
        conTimeoutText.setText(String.valueOf(Globals.getConnectionTimeout()));

        queryTimeoutText = new JTextField();
        queryTimeoutText.setPreferredSize(txtDim);
        queryTimeoutText.setText(String.valueOf(Globals.getQueryTimeout()));

        chartPortText = new JTextField();
        chartPortText.setPreferredSize(txtDim);
        chartPortText.setText(String.valueOf(Globals.getChartWebServerPort()));
        
        maxCheck = new JCheckBox();
        maxCheck.setSelected(Globals.isMaxChecked());

        accCheck = new JCheckBox();
        accCheck.setSelected(Globals.getAccessibilityHtml());

        a4Check = new JCheckBox();
        a4Check.setSelected(Globals.getA4Warning());

        tnsText = new JTextField();        
        tnsText.setPreferredSize(txtDim);
        String oraclePath = Globals.getOracleClientPath();
        if (oraclePath != null) {
            tnsText.setText(FileUtil.getEscapedPath(oraclePath, File.separator));
        }

        fontText = new JTextField();
        fontText.setPreferredSize(txtDim);
        String[] dirs = Globals.getFontDirectories();
        StringBuilder sb = new StringBuilder();
        for (int i=0, size=dirs.length; i<size; i++) {
            sb.append(dirs[i]);
            if (i<size-1) {
                sb.append(",");
            }
        }
        fontText.setText(FileUtil.getEscapedPath(sb.toString(), File.separator));

        JButton oracleButton = new JButton();
        oracleButton.setPreferredSize(btnDim);
        oracleButton.setMinimumSize(btnDim);
        oracleButton.setMaximumSize(btnDim);
        oracleButton.setIcon(ImageUtil.getImageIcon("folder"));
        oracleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(I18NSupport.getString("settings.action.oracle.path.select"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showSaveDialog(Globals.getMainFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f != null) {
                        tnsText.setText(FileUtil.getEscapedPath(f.getAbsolutePath(), File.separator));
                    }
                }
            }
        });

        JButton fontButton = new JButton();
        fontButton.setPreferredSize(btnDim);
        fontButton.setMinimumSize(btnDim);
        fontButton.setMaximumSize(btnDim);
        fontButton.setIcon(ImageUtil.getImageIcon("folder"));
        fontButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(I18NSupport.getString("settings.action.font.select"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showSaveDialog(Globals.getMainFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f != null) {
                    	String newFolder = f.getAbsolutePath();
                    	if (System.getProperty("os.name").startsWith("Windows")) {
                    		newFolder = FileUtil.getEscapedPath(f.getAbsolutePath(), File.separator);
                    	}                        
                        String oldPath = fontText.getText();
                        String text;
                        if ("".equals(oldPath.trim())) {
                           text = newFolder;
                        } else  {
                            text = oldPath + "," + newFolder;
                        }
                        fontText.setText(text);
                    }
                }
            }
        });

        csvDelimiterText = new JTextField();
        csvDelimiterText.setPreferredSize(txtDim);
        csvDelimiterText.setText(String.valueOf(Globals.getCsvDelimiter()));

        rulerCheck = new JCheckBox();
        rulerCheck.setSelected(Globals.isRulerVisible());
        rulerCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                rulerUnitCombo.setEnabled(rulerCheck.isSelected());
            }
        });

        rulerUnitCombo = new JComboBox();
        rulerUnitCombo.addItem(Globals.UNIT_CM);
        rulerUnitCombo.addItem(Globals.UNIT_IN);
        rulerUnitCombo.setSelectedItem(Globals.getRulerUnit());
        rulerUnitCombo.setEnabled(Globals.isRulerVisible());
        
        localeCombo = new JComboBox();
        for (Country c : LocaleUtil.getCountries()) {
        	localeCombo.addItem(c);
        }        
        localeCombo.setSelectedItem(LocaleUtil.getCountry(Globals.getConfigLocale()));   
        localeCombo.setRenderer(new CountryRenderer());

        JTabbedPane tabPanel = new JTabbedPane();

        JPanel uiPanel = new JPanel();
        uiPanel.setLayout(new GridBagLayout());

        uiPanel.add(new JXTitledSeparator(I18NSupport.getString("settings.action.ui.properties")),
                new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
                
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.max.rows")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        uiPanel.add(maxCheck, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.accessibility")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        uiPanel.add(accCheck, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.A4")),
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        uiPanel.add(a4Check, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.ruler.visible")),
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        uiPanel.add(rulerCheck, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.ruler.unit")),
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        uiPanel.add(rulerUnitCombo, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));        
        uiPanel.add(new JLabel(I18NSupport.getString("settings.action.locale")),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        uiPanel.add(localeCombo, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));
        uiPanel.add(new JLabel(), new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        tabPanel.addTab(I18NSupport.getString("settings.action.ui.title"), uiPanel);

        JPanel executionPanel = new JPanel();
        executionPanel.setLayout(new GridBagLayout());

        executionPanel.add(new JXTitledSeparator(I18NSupport.getString("settings.action.exec.properties")),
                new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.con.timeout")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        executionPanel.add(conTimeoutText, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.timeout")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        executionPanel.add(queryTimeoutText, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.oracle.path")),
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        executionPanel.add(tnsText, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        executionPanel.add(oracleButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.font")),
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        executionPanel.add(fontText, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        executionPanel.add(fontButton, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.csv.delimiter")),
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        executionPanel.add(csvDelimiterText, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        executionPanel.add(new JLabel(I18NSupport.getString("settings.action.chart.port")),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 0), 0, 0));
        executionPanel.add(chartPortText, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));

        tabPanel.addTab(I18NSupport.getString("settings.action.exec.title"), executionPanel);

        add(tabPanel,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

    }
    
    public int getConTimeout() throws SettingsException {
        String text = conTimeoutText.getText();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new SettingsException("Invalid Timeout number : " + text);
        }
    }

    public int getTimeout() throws SettingsException {
        String text = queryTimeoutText.getText();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new SettingsException("Invalid Timeout number : " + text);
        }
    }

    public int getChartPort() throws SettingsException {
        String text = chartPortText.getText();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new SettingsException("Invalid Chart Port number : " + text);
        }
    }


    public boolean getHTMLAccessibility() {
        return accCheck.isSelected();
    }
    
    public boolean getMaxRowsChecked() {
        return maxCheck.isSelected();
    }

    public boolean getA4Warning() {
        return a4Check.isSelected();
    }

    public boolean isRulerVisible() {
        return rulerCheck.isSelected();
    }

    public String getRulerUnit() {
        return (String)rulerUnitCombo.getSelectedItem();
    }
    
    public String getLocaleLanguageCountry() {
    	Country c =(Country)localeCombo.getSelectedItem();
    	return c.getLanguage() + "," + c.getCode();
    }

    public String getOracleClientPath() {
        return tnsText.getText();
    }

    public String getFontDirectories() {
        return fontText.getText();
    }

    public char getCsvDelimiter() throws SettingsException {
        String del = csvDelimiterText.getText();
        if (!Globals.isValidCsvDelimiter(del)) {
            throw new SettingsException("Invalid csv delimiter.");
        } else {
            return del.toCharArray()[0];
        }

    }
}
