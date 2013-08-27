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
package ro.nextreports.designer.property;

import ro.nextreports.engine.exporter.util.StyleFormatConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ro.nextreports.designer.util.I18NSupport;

/**
 * @author Decebal Suiu
 */
public class FieldPatternPanel extends JPanel {
    
    public static final String NO_SAMPLE = "-"; 
    public static final String NO_PATTERN = "-"; 
    
    private static String[] dateFormats = new String[] {
        "dd/MM/yyyy",
        "MM/dd/yyyy",
        "EEEEE dd MMMMM yyyy",   
        "MMMMM dd, yyyy", 
        "dd/MM",
        "dd/MM/yy",
        "dd-MMM",
        "dd-MMM-yy",
        "MMM-yy",
        "MMMMM-yy",
        "dd MMMMM yyyy", 
        "dd/MM/yyyy h:mm a",
        "dd/MM/yyyy HH:mm:ss",
        "MMM",
        "d/M/yyyy",
        "dd-MMM-yyyy"
    };
    
    private static String[] timeFormats = new String[] {
        "HH:mm",
        "h:mm a",
        "HH:mm:ss",
        "h:mm:ss a",
        "mm:ss,S",
        "K:mm a, z"
    };
    
    private int dialogResult = JOptionPane.CANCEL_OPTION;
    private int selectedCategory = -1;

    private JCheckBox thousandsSeparatorCheckBox;
    private JComboBox percentageComboBox;
    private JPanel categoryPanel;
    private JLabel patternLabel;
    private JLabel sampleLabel;
    private JList categoryList;
    private JList dateTypesList;
    private JList negativesList;
    private JList timeTypesList;
    private JPanel currencyPanel;
    private JPanel datePanel;
    private JPanel numberPanel;
    private JPanel romanNumberPanel;
    private JPanel percentagePanel;
    private JPanel scientificPanel;
    private JPanel sheetsPanel;
    private JPanel timePanel;
    private JPanel customPanel;
    private JSpinner numberDecimalsSpinner;
    private JSpinner numberDecimalsSpinner1;
    private JSpinner numberDecimalsSpinner2;
    private JSpinner numberDecimalsSpinner3;
    private JTextField customTextField;
    private int previousCategory = 0;

    public FieldPatternPanel() {
        super();
        initAll();
    }

    private String NUMBER = I18NSupport.getString("pattern.number");
    private String ROMAN_NUMBER = I18NSupport.getString("pattern.roman");
    private String DATE = I18NSupport.getString("pattern.date");
    private String TIME = I18NSupport.getString("pattern.time");
    private String CURRENCY = I18NSupport.getString("pattern.currency");
    private String PERCENTAGE = I18NSupport.getString("pattern.percentage");
    private String SCIENTIFIC = I18NSupport.getString("pattern.scientific");
    private String CUSTOM = I18NSupport.getString("pattern.custom");

    
    public void initAll() {
        initComponents();
        
        DefaultListModel dlm = new DefaultListModel();
        DefaultListModel dlm2 = new DefaultListModel();
        DefaultListModel dlm3 = new DefaultListModel();
        DefaultListModel dlm4 = new DefaultListModel();
        
        dlm.addElement(NUMBER);      // 0
        dlm.addElement(ROMAN_NUMBER);// 1
        dlm.addElement(DATE);        // 2
        dlm.addElement(TIME);        // 3
        dlm.addElement(CURRENCY);    // 4
        dlm.addElement(PERCENTAGE);  // 5
        dlm.addElement(SCIENTIFIC);  // 6
        dlm.addElement(CUSTOM);      // 7
        
        categoryList.setModel(dlm);
        negativesList.setModel(dlm2);
        dateTypesList.setModel(dlm3);
        timeTypesList.setModel(dlm4);

        SpinnerNumberModel sm = new SpinnerNumberModel(2, 0, 100, 1);
        numberDecimalsSpinner.setModel(sm);
        numberDecimalsSpinner1.setModel(sm);
        numberDecimalsSpinner2.setModel(sm);
        numberDecimalsSpinner3.setModel(sm);
        
        sheetsPanel.removeAll();
        ((DefaultComboBoxModel) percentageComboBox.getModel()).addElement("%");
        ((DefaultComboBoxModel) percentageComboBox.getModel()).addElement("\u2030");
        
        sheetsPanel.updateUI();
        categoryList.setSelectedIndex(0);
        updateNegativesList();
        updateDateTypesList();
        updateTimeTypesList();
    }
    
    public void setOnlyDate(boolean b) {
        if (b == true) {
            DefaultListModel dlm = (DefaultListModel) categoryList.getModel();
            dlm.removeAllElements();
            dlm.addElement(DATE);
            selectedCategory = -1;
            categoryList.setSelectedIndex(0);
            categoryListValueChanged(null);
        }
    }
    
    private void initComponents() {
        categoryList = new JList();
        numberDecimalsSpinner = new JSpinner();
        thousandsSeparatorCheckBox = new JCheckBox();
        negativesList = new JList();
        dateTypesList = new JList();
        timeTypesList = new JList();
        numberDecimalsSpinner1 = new JSpinner();
        numberDecimalsSpinner2 = new JSpinner();
        percentageComboBox = new JComboBox();
        numberDecimalsSpinner3 = new JSpinner();

        setLayout(new GridBagLayout());

        // add category panel
        categoryPanel = createCategoryPanel();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        add(categoryPanel, gridBagConstraints);

        JPanel categoryDetailPanel = new JPanel(); 
        categoryDetailPanel.setLayout(new GridBagLayout());

        // add samplePanel
        JPanel samplePanel = createSamplePanel();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 3, 0);
        categoryDetailPanel.add(samplePanel, gridBagConstraints);

        // create sheetsPanel
        sheetsPanel = new JPanel();
        sheetsPanel.setLayout(new BorderLayout());
        numberPanel = createNumberPanel();
        sheetsPanel.add(numberPanel, BorderLayout.CENTER);
        romanNumberPanel = createRomanNumberPanel();
        sheetsPanel.add(romanNumberPanel, BorderLayout.CENTER);
        datePanel = createDatePanel();
        sheetsPanel.add(datePanel, BorderLayout.CENTER);
        timePanel = createTimePanel();
        sheetsPanel.add(timePanel, BorderLayout.CENTER);
        currencyPanel = createCurrencyPanel();
        sheetsPanel.add(currencyPanel, BorderLayout.CENTER);
        percentagePanel = createPercentagePanel();
        sheetsPanel.add(percentagePanel, BorderLayout.CENTER);
        scientificPanel = createScientificPanel();
        sheetsPanel.add(scientificPanel, BorderLayout.CENTER);
        customPanel = createCustomPanel();
        sheetsPanel.add(customPanel, BorderLayout.CENTER);
        
        // add sheetsPanel
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(3, 0, 3, 0);
        categoryDetailPanel.add(sheetsPanel, gridBagConstraints);

        // add patternPanel
        JPanel patternPanel = createPatternPanel();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(3, 0, 0, 0);
        categoryDetailPanel.add(patternPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        add(categoryDetailPanel, gridBagConstraints);
    }

    private JPanel createCustomPanel() {
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        customTextField = new JTextField();
        customTextField.addCaretListener(new CaretListener() {

            public void caretUpdate(CaretEvent ev) {
                String text = customTextField.getText().trim();
                if (text.length() == 0) {
                    patternLabel.setText(NO_PATTERN);
                } else {
                    patternLabel.setText(text);
                }
                
                try {
                    updateSample();
                    customTextField.setBackground(Color.WHITE);
                } catch (IllegalArgumentException e) {
                    customTextField.setBackground(Color.RED.brighter());
                    sampleLabel.setText(NO_SAMPLE);
                    patternLabel.setText(NO_PATTERN);
                }
            }
            
        });
        customPanel.add(customTextField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        customPanel.add(new JPanel(), gridBagConstraints);

        return customPanel;
    }

    private JPanel createPatternPanel() {
        JPanel patternPanel = new JPanel();
        patternPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        JLabel tmpLabel = new JLabel(I18NSupport.getString("pattern.title"));
        tmpLabel.setFont(new Font("SansSerif", 0, 11));
        patternPanel.add(tmpLabel, gridBagConstraints);

        // create patternLabel
        patternLabel = new JLabel(NO_PATTERN);
        patternLabel.setBorder(new LineBorder(Color.BLACK, 1));
        patternLabel.setBackground(Color.WHITE);
        patternLabel.setOpaque(true);
        patternLabel.setFont(new Font("SansSerif", 0, 12));
        patternLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        patternPanel.add(patternLabel, gridBagConstraints);
        
        return patternPanel;
    }

    private JPanel createSamplePanel() {
        JPanel samplePanel = new JPanel();
        samplePanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        JLabel tmpLabel = new JLabel(I18NSupport.getString("pattern.sample"));
        tmpLabel.setFont(new Font("SansSerif", 0, 11));
        samplePanel.add(tmpLabel, gridBagConstraints);

        sampleLabel = new JLabel();
        sampleLabel.setBorder(new LineBorder(Color.BLACK, 1));
        sampleLabel.setBackground(Color.WHITE);
        sampleLabel.setOpaque(true);
        sampleLabel.setFont(new Font("SansSerif", 0, 12));
        sampleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        samplePanel.add(sampleLabel, gridBagConstraints);
        
        return samplePanel;
    }

    private JPanel createScientificPanel() {
        JPanel scientificPanel = new JPanel();
        scientificPanel.setLayout(new GridBagLayout());

        // create decimalPlacesLabel
        JLabel decimalPlacesLabel = new JLabel();
        decimalPlacesLabel.setFont(new Font("SansSerif", 0, 11));
        decimalPlacesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        decimalPlacesLabel.setText(I18NSupport.getString("pattern.decimalplaces"));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        scientificPanel.add(decimalPlacesLabel, gridBagConstraints);

        numberDecimalsSpinner3.setFont(new Font("SansSerif", 0, 11));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        scientificPanel.add(numberDecimalsSpinner3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        scientificPanel.add(new JPanel(), gridBagConstraints);
        
        return scientificPanel;
    }

    private JPanel createPercentagePanel() {
        JPanel percentagePanel = new JPanel();
        percentagePanel.setLayout(new GridBagLayout());

        // create decimalPlacesLabel
        JLabel decimalPlacesLabel = new JLabel();
        decimalPlacesLabel.setFont(new Font("SansSerif", 0, 11));
        decimalPlacesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        decimalPlacesLabel.setText(I18NSupport.getString("pattern.decimalplaces"));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        percentagePanel.add(decimalPlacesLabel, gridBagConstraints);

        numberDecimalsSpinner2.setFont(new Font("SansSerif", 0, 11));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        percentagePanel.add(numberDecimalsSpinner2, gridBagConstraints);

        // create typeLabel
        JLabel typeLabel = new JLabel();
        typeLabel.setFont(new Font("SansSerif", 0, 11));
        typeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        typeLabel.setText(I18NSupport.getString("pattern.percentage.type"));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        percentagePanel.add(typeLabel, gridBagConstraints);

        percentageComboBox.setFont(new Font("SansSerif", 0, 12));
        percentageComboBox.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent ev) {
                updateSample();
            }
            
        });
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 0, 0);
        percentagePanel.add(percentageComboBox, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        percentagePanel.add(new JPanel(), gridBagConstraints);
        
        return percentagePanel;
    }
    
    private JPanel createRomanNumberPanel() {
        JPanel romanPanel = new JPanel();
        romanPanel.setPreferredSize(new Dimension(150, 150));
        romanPanel.setMinimumSize(new Dimension(150, 150));
        return romanPanel;
    }

    private JPanel createCurrencyPanel() {
        JPanel currencyPanel = new JPanel();
        currencyPanel.setLayout(new GridBagLayout());

        // create decimalPlacesLabel
        JLabel decimalPlacesLabel = new JLabel();
        decimalPlacesLabel.setFont(new Font("SansSerif", 0, 11));
        decimalPlacesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        decimalPlacesLabel.setText(I18NSupport.getString("pattern.decimalplaces"));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        currencyPanel.add(decimalPlacesLabel, gridBagConstraints);

        numberDecimalsSpinner1.setFont(new Font("SansSerif", 0, 11));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        currencyPanel.add(numberDecimalsSpinner1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        currencyPanel.add(new JPanel(), gridBagConstraints);
        
        return currencyPanel;
    }

    private JPanel createTimePanel() {
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new GridBagLayout());

        // create typeLabel
        JLabel typeLabel = new JLabel();
        typeLabel.setFont(new Font("SansSerif", 0, 11));
        typeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        typeLabel.setText(I18NSupport.getString("pattern.time.type"));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        timePanel.add(typeLabel, gridBagConstraints);

        timeTypesList.setFont(new Font("SansSerif", 0, 11));
        timeTypesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timeTypesList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent ev) {
                updateSample();
            }
            
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        timePanel.add(new JScrollPane(timeTypesList), gridBagConstraints);
        
        return timePanel;
    }

    private JPanel createDatePanel() {
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new GridBagLayout());

        // create typeLabel
        JLabel typeLabel = new JLabel();
        typeLabel.setFont(new Font("SansSerif", 0, 11));
        typeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        typeLabel.setText(I18NSupport.getString("pattern.date.type"));
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        datePanel.add(typeLabel, gridBagConstraints);

        dateTypesList.setFont(new Font("SansSerif", 0, 11));
        dateTypesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dateTypesList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent ev) {
                updateSample();
            }
            
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        datePanel.add(new JScrollPane(dateTypesList), gridBagConstraints);
        
        return datePanel;
    }

    private JPanel createNumberPanel() {
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridBagLayout());

        // create decimalPlacesLabel
        JLabel decimalPlacesLabel = new JLabel();
        decimalPlacesLabel.setFont(new Font("SansSerif", 0, 11));
        decimalPlacesLabel.setHorizontalAlignment(SwingConstants.LEFT);
        decimalPlacesLabel.setText(I18NSupport.getString("pattern.decimalplaces"));
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        numberPanel.add(decimalPlacesLabel, gridBagConstraints);

        numberDecimalsSpinner.setFont(new Font("SansSerif", 0, 11));
        numberDecimalsSpinner.addChangeListener(new ChangeListener() {
            
            public void stateChanged(ChangeEvent ev) {
                updateSample();
                updateNegativesList();
            }
            
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        numberPanel.add(numberDecimalsSpinner, gridBagConstraints);

        // create thousandsSeparatorLabel
        JLabel thousandsSeparatorLabel = new JLabel();
        thousandsSeparatorLabel.setFont(new Font("SansSerif", 0, 11));
        thousandsSeparatorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        thousandsSeparatorLabel.setText(I18NSupport.getString("pattern.thousand.separator"));
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 0, 0, 3);
        numberPanel.add(thousandsSeparatorLabel, gridBagConstraints);

        thousandsSeparatorCheckBox.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent ev) {
                updateSample();
                updateNegativesList();
            }
            
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 3, 0, 0);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        numberPanel.add(thousandsSeparatorCheckBox, gridBagConstraints);

        // create negativeNumbersLabel
        JLabel negativeNumbersLabel = new JLabel();
        negativeNumbersLabel.setFont(new Font("SansSerif", 0, 11));
        negativeNumbersLabel.setHorizontalAlignment(SwingConstants.LEFT);
        negativeNumbersLabel.setText(I18NSupport.getString("pattern.negative.numbers"));
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        numberPanel.add(negativeNumbersLabel, gridBagConstraints);

        negativesList.setFont(new Font("SansSerif", 0, 11));
        negativesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        negativesList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent ev) {
                updateSample();
            }
            
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        numberPanel.add(new JScrollPane(negativesList), gridBagConstraints);

        return numberPanel;
    }

    private JPanel createCategoryPanel() {
        GridBagConstraints gridBagConstraints;
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new GridBagLayout());
        categoryPanel.setPreferredSize(new Dimension(100, 50));
        categoryPanel.setMinimumSize(new Dimension(100, 50));
        
        // add category label
        JLabel categoryLabel = new JLabel();
        categoryLabel.setFont(new Font("SansSerif", 0, 11));
        categoryLabel.setText(I18NSupport.getString("pattern.category"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        categoryPanel.add(categoryLabel, gridBagConstraints);

        categoryList.setFont(new Font("SansSerif", 0, 11));
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent ev) {
                categoryListValueChanged(ev);
            }
            
        });

        // add category list
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        categoryPanel.add(new JScrollPane(categoryList), gridBagConstraints);
        
        return categoryPanel;
    }

    private void categoryListValueChanged(ListSelectionEvent ev) {
        int category = getSelectedCategory();
        if (category != selectedCategory) {
            selectedCategory = category;
            sheetsPanel.removeAll();
            if (category == 0) {
                sheetsPanel.add(numberPanel);
                updateSample();
            } else if (category == 1) {
            	sheetsPanel.add(romanNumberPanel);
                updateSample();
            } else if (category == 2) {
                sheetsPanel.add(datePanel);
                updateSample();
            } else if (category == 3) {
                sheetsPanel.add(timePanel);
                updateSample();
            } else if (category == 4) {
                sheetsPanel.add(currencyPanel);
                updateSample();
            } else if (category == 5) {
                sheetsPanel.add(percentagePanel);
                updateSample();
            } else if (category == 6) {
                sheetsPanel.add(scientificPanel);
                updateSample();
            } else if (category == 7) {
                sheetsPanel.add(customPanel);
                String pattern = patternLabel.getText();
                if (!NO_PATTERN.equals(pattern)) {
                    updateSample();
                    customTextField.setText(pattern);
                }
            } else {
                updateSample();
            }
            sheetsPanel.updateUI();
            if (category != 6) {
                previousCategory = category;
            }
        }
    }
        
    private void updateSample() {
        String format = createPattern();
        int category = getSelectedCategory();
        if (category == 0) {
            DecimalFormat nf = new DecimalFormat(format);
            sampleLabel.setText(nf.format(1234.43210));
        } else if (category == 1) {
        	sampleLabel.setText(StyleFormatConstants.ROMAN_PATTERN);
        } else if (category == 2) {
             if (dateTypesList.getSelectedIndex() >= 0) {
                sampleLabel.setText("" + dateTypesList.getSelectedValue());
             } else {
                 sampleLabel.setText("");
             }
        } else if (category == 3) {
             if (timeTypesList.getSelectedIndex() >= 0) {
                sampleLabel.setText( "" + timeTypesList.getSelectedValue() );
             } else {
                 sampleLabel.setText("");
             }
        } else if (category == 4) {
            DecimalFormat nf = new DecimalFormat(format);
            sampleLabel.setText(nf.format(1234.43210));
        } else if (category == 5) {
            DecimalFormat nf = new DecimalFormat(format);
            sampleLabel.setText(nf.format(1234.43210));
        } else if (category == 6) {
            DecimalFormat nf = new DecimalFormat(format);
            sampleLabel.setText(nf.format(1234.43210));
        } else {      
        	if (previousCategory == 1) {
        		sampleLabel.setText(StyleFormatConstants.ROMAN_PATTERN);
        	} else if ((previousCategory == 2) || (previousCategory == 3)) {
                SimpleDateFormat nf = new SimpleDateFormat(format);
                sampleLabel.setText(nf.format(new Date()));
            } else  {
                DecimalFormat nf = new DecimalFormat(format);
                sampleLabel.setText(nf.format(1234.43210));
            }            
        }
    }
    
    private String createPattern() {
        String format = "";
        int category = getSelectedCategory();
        // NUMBER FORMATS
        if (category == 0) {
            // format = "###0";
            if (thousandsSeparatorCheckBox.isSelected()) {
                format = "#,##0";
            } else {
                format = "###0";
            }
            int decimals = ((SpinnerNumberModel) numberDecimalsSpinner.getModel()).getNumber().intValue();
            if (decimals > 0) {
                format += ".";
                for (int i = 0; i < decimals; ++i) {
                    format += "0";
                }                
            }
            if (negativesList.getSelectedIndex() >= 0) {
                int selectedIndex = negativesList.getSelectedIndex();
                if (selectedIndex == 0) {
                    format += ";-"+ format + "";
                } else  if (selectedIndex == 1) {
                    format += ";" + format + "-";
                } else if (selectedIndex == 2) {
                    format += ";(" + format + ")";
                } else if (selectedIndex == 3) {
                    format += ";(-" + format + ")";
                } else if (selectedIndex == 4) {
                    format += ";(" + format + "-)";
                }
            }
        } else if (category == 1) {
        		format = StyleFormatConstants.ROMAN_PATTERN;
        } else if (category == 2) {
            if (dateTypesList.getSelectedIndex() >= 0) {
                format = dateFormats[dateTypesList.getSelectedIndex()];
            }
        } else if (category == 3) {
            if (timeTypesList.getSelectedIndex() >= 0) {
                format = timeFormats[timeTypesList.getSelectedIndex()];
            }
        } else if (category == 4) {
            format = "\u00A4 #,##0";
            int decimals = ((SpinnerNumberModel) numberDecimalsSpinner1.getModel()).getNumber().intValue();
            if (decimals > 0) {
                format += ".";
                for (int i = 0; i < decimals; ++i) {
                    format += "0";
                }                
            }
        } else if (category == 5) {
            format = "#,##0";
            int decimals = ((SpinnerNumberModel) numberDecimalsSpinner2.getModel()).getNumber().intValue();
            if (decimals > 0) {
                format += ".";
                for (int i = 0; i < decimals; ++i) {
                    format += "0";
                }                
            }
            format += " " + percentageComboBox.getSelectedItem();
        } else if (category == 6) {
            format = "0";
            int decimals = ((SpinnerNumberModel) numberDecimalsSpinner3.getModel()).getNumber().intValue();
            if (decimals > 0) {
                format += ".0";
                for (int i = 1; i < decimals; ++i) {
                    format += "#";
                }                
            }
            format += "E0";
        } else if (category == 7) {
            format = patternLabel.getText();
        }
        
        patternLabel.setText(format);
        
        return format;
    }
    
    private void updateNegativesList() {
        String format = createPattern();
        DefaultListModel dlm = (DefaultListModel) negativesList.getModel();
        int selected = negativesList.getSelectedIndex();
        dlm.removeAllElements();
        if (format.indexOf(";") >= 0) {
            format = format.substring(0, format.indexOf(";"));
        }
        
        String[] formats = new String[5];
        formats[0] = format + ";-" + format + "";
        formats[1] = format + ";" + format + "-";
        formats[2] = format + ";(" + format + ")";
        formats[3] = format + ";(-" + format + ")";
        formats[4] = format + ";(" + format + "-)";
    
        for (int i = 0; i < formats.length; ++i) {
            DecimalFormat nf = new DecimalFormat(formats[i]);
            dlm.addElement(nf.format(-1234.43210));
        }
        if (selected >= 0) {
            negativesList.setSelectedIndex(selected);
        }
    }
    
    private void updateDateTypesList() {
        DefaultListModel dlm = (DefaultListModel) dateTypesList.getModel();
        for (int i = 0; i < dateFormats.length; ++i) {
            SimpleDateFormat nf = new SimpleDateFormat(dateFormats[i]);
            dlm.addElement(nf.format(new Date()));
        }
        dateTypesList.setSelectedIndex(0);
    }
    
    private void updateTimeTypesList() {
        DefaultListModel dlm = (DefaultListModel) timeTypesList.getModel();
        for (int i = 0; i < timeFormats.length; ++i) {
            SimpleDateFormat nf = new SimpleDateFormat(timeFormats[i]);
            dlm.addElement(nf.format(new Date()));
        }
        timeTypesList.setSelectedIndex(0);
    }
     
     public int getDialogResult() {
         return dialogResult;
     }
     
     public void setDialogResult(int dialogResult) {
         this.dialogResult = dialogResult;
     }
     
     public String getPattern() {
         String pattern = patternLabel.getText(); 
         return NO_PATTERN.equals(pattern) ? null : pattern;
     }
     
	public void setPattern(String pattern) {
		boolean setDecimals = false;
		if (pattern == null) {
			// pattern for first selection (number with 2 decimals)
			pattern = "##0.00";
			patternLabel.setText(pattern);
			setDecimals = true;
		} else {
			patternLabel.setText(pattern);
			previousCategory = getCategory(pattern);			
			categoryList.setSelectedIndex(previousCategory);
		}
		int category = getCategory(pattern);
		if (category == 0) {
			if (pattern.contains(",")) {
				thousandsSeparatorCheckBox.setSelected(true);
			}
			int index = pattern.indexOf(".");
			if (index > -1) {
				int decimals = pattern.substring(index + 1).length();
				numberDecimalsSpinner.setValue(decimals);
				setDecimals = true;
			}
			if (!setDecimals) {
				numberDecimalsSpinner.setValue(0);
			}
		} else if (category == 2) {
			// date
			dateTypesList.setSelectedIndex(getIndex(dateFormats, pattern));	
		} else if (category == 3) {
			// time
			timeTypesList.setSelectedIndex(getIndex(timeFormats, pattern));		
		} else if (category == 4) {
			// currency
			int index = pattern.indexOf(".");
			int decimals = 0;
			if (index > -1) {
				decimals = pattern.substring(index + 1).length();
			}
			numberDecimalsSpinner1.setValue(decimals);
		} else if (category == 5) {
			// percentage
			int index = pattern.indexOf(".");
			int decimals = 0;
			if (index > -1) {
				decimals = pattern.substring(index + 1, pattern.lastIndexOf(" ")).length();				
			}	
			numberDecimalsSpinner2.setValue(decimals);
			if (pattern.contains("%")) {
				percentageComboBox.setSelectedIndex(0);
			} else {
				percentageComboBox.setSelectedIndex(1);
			}
		} else if (category == 6) {
			// scientific
			int index = pattern.indexOf(".");
			int decimals = 0;
			if (index > -1) {
				decimals = pattern.substring(index + 1).length();
			}
			numberDecimalsSpinner3.setValue(decimals);
		}
		
	}
     
     public int getSelectedCategory() {
         String category = "" + categoryList.getSelectedValue();
         if (category.equals(NUMBER)) {
             return 0;
         } else if (category.equals(ROMAN_NUMBER)) {
        	 return 1;
         } else if (category.equals(DATE)) {
             return 2;
         } else if (category.equals(TIME)) {
             return 3;
         } else if (category.equals(CURRENCY)) {
             return 4;
         } else if (category.equals(PERCENTAGE)) {
             return 5;
         } else if (category.equals(SCIENTIFIC)) {
             return 6;
         } else if (category.equals(CUSTOM)) {
             return 7;
         } else {
             return -1;
         }
     }

    public int getCategory(String pattern) {
        if (pattern == null) {
            return 0;
        } else {
        	if (pattern.equals(StyleFormatConstants.ROMAN_PATTERN)) {
        		return 1;
        	} else if (pattern.contains("dd") || pattern.contains("MM") || pattern.contains("yy")) {
                return 2;
            } else if (pattern.contains("HH") || pattern.contains("mm") || pattern.contains("ss")) {
                return 3;
            } else {            	
            	boolean isCurrency = false;
            	int index = pattern.indexOf("#");
            	if (index == -1) {
            		index = pattern.indexOf("0");
            	}
            	if (index != -1) {
            		String s = pattern.substring(0, index);
            		if (s.contains(" ")) {
            			isCurrency = true;
            		}
            	}
            	if (isCurrency) {
            		return 4;
            	} else if (pattern.contains("%") || pattern.contains("\u2030")) {
            		return 5;	
            	} else if (pattern.contains("E")) {
            		return 6;           
            	}
                return 0;
            }
        }
    }
     
     public static void main(String args[]) {
    	 // THIS WON'T WORK - IF YOU NEED TO SET DEFAULT LOCALE AT RUNTIME, USE Locale.setDefault()
    	 // see http://www.avajava.com/tutorials/lessons/how-do-i-set-the-default-locale-via-system-properties.html
//    	 System.setProperty("user.language", "fr");
//    	 System.setProperty("user.country", "CA");
    	 
    	 System.out.println(Locale.getDefault());    	     	     	 

         JFrame frame = new JFrame(I18NSupport.getString("pattern.editor.title"));
         frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         frame.setLayout(new BorderLayout());
         frame.add(new FieldPatternPanel(), BorderLayout.CENTER);
         frame.pack();
         frame.setVisible(true);
     }
     
     private int getIndex(String[] indexes, String pattern) {
    	 for (int i=0, size = indexes.length; i< size; i++) {
    		 if (indexes[i].equals(pattern)) {
    			 return i;
    		 }    		 
    	 }
    	 return 0;
     }
          
     
}
