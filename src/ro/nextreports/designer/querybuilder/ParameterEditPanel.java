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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.Serializable;

import javax.swing.*;

import ro.nextreports.engine.queryexec.QueryParameter;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

/**
 * @author Decebal Suiu
 */
public class ParameterEditPanel extends JPanel {

    public static final String SINGLE_SELECTION = "Single";
    public static final String MULTIPLE_SELECTION = "Multiple";

    private JTextField nameTextField;
    private JTextField runtimeNameTextField;
    private JComboBox typeComboBox;
    private JTextArea sourceTextArea;
    private JButton sourceButton;
    private JButton manualSourceButton;
    private JButton clearButton;
    private JComboBox selectionComboBox;
    private JCheckBox mandatoryCheck;
    private JTextArea descriptionTextArea;
    private JCheckBox procedureCheck;
    private JTextField previewValueTextField;
    private JXList defaultValuesList;
    private DefaultListModel defaultValuesModel;
    private JButton defAddButton;
    private JButton defRemoveButton;
    private JTextArea defaultSourceTextArea;
    private JButton defaultSourceButton;
    private JButton defaultClearButton;
    private JCheckBox hiddenCheck;

    private QueryParameter parameter;
    private boolean manualSource = false;
    private String schema;
    private byte orderBy = QueryParameter.ORDER_BY_NAME;

    public ParameterEditPanel(QueryParameter parameter) {
        this.parameter = parameter;
        if (parameter != null) {
        	this.schema = parameter.getSchema();
        }
        initUI();
    }

    public QueryParameter getParameter() {
        return parameter;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getRuntimeNameTextField() {
        return runtimeNameTextField;
    }

    public JComboBox getTypeComboBox() {
        return typeComboBox;
    }

    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public String getSource() {
        return sourceTextArea.getText();
    }

    public String getDefaultSource() {
        return defaultSourceTextArea.getText();
    }

    public String getSelection() {
        return getParameterType(selectionComboBox.getSelectedIndex());
    }

    public boolean getMandatory() {
        return mandatoryCheck.isSelected();
    }

    public boolean getHidden() {
        return hiddenCheck.isSelected();
    }

    public boolean getManualSource() {
        return manualSource;
    }

    public boolean getProcedureParameter() {
        return procedureCheck.isSelected();
    }

    public String getPreviewValue() {
        if (!getProcedureParameter()) {
            return null;
        }
        String result = previewValueTextField.getText();
        if (result.trim().equals("")) {
            return null;
        }
        return result;
    }

    private void initUI() {
        this.setLayout(new GridBagLayout());

        JTabbedPane tabPanel = new JTabbedPane();
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new GridBagLayout());

        generalPanel.add(new JXTitledSeparator(I18NSupport.getString("parameter.general")),
                new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
        generalPanel.add(new JLabel(I18NSupport.getString("parameter.name")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        generalPanel.add(nameTextField = new JTextField(25),
                new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
        generalPanel.add(new JLabel(I18NSupport.getString("parameter.runtime.name")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        generalPanel.add(runtimeNameTextField = new JTextField(25),
                new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        generalPanel.add(new JLabel(I18NSupport.getString("parameter.source")),
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        JScrollPane scrollArea = new JScrollPane(sourceTextArea = new JTextArea(5, 20));
        scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        generalPanel.add(scrollArea,
                new GridBagConstraints(1, 3, 1, 3, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        sourceTextArea.setEditable(false);

        sourceButton = new JButton(ImageUtil.getImageIcon("add"));
        sourceButton.setToolTipText(I18NSupport.getString("parameter.column.selection"));
        Dimension buttonDim = new Dimension(20, 20);
        sourceButton.setPreferredSize(buttonDim);
        sourceButton.setMinimumSize(buttonDim);
        sourceButton.setMaximumSize(buttonDim);
        sourceButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String schema = ParameterEditPanel.this.schema;

                if (schema == null) {
                    if ((parameter == null) || (parameter.getSchema() == null)) {
                        try {
                            schema = Globals.getDBViewer().getUserSchema();
                            List<String> schemas = Globals.getDBViewer().getSchemas();
                            boolean noschema = true;
                            for (String s : schemas) {
                                if (s.equals(schema)) {
                                    noschema = false;
                                }
                            }
                            if (noschema) {
                                schema = DefaultDBViewer.NO_SCHEMA_NAME;
                            }
                        } catch (NextSqlException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        schema = parameter.getSchema();
                    }
                }
                SelectionColumnPanel scp = new SelectionColumnPanel(schema);
                String source = getSource();
                if (!source.equals("") && !manualSource) {
                    scp.setColumns(source);
                }

                SelectionColumnDialog dlg = new SelectionColumnDialog(scp);
                dlg.pack();
                dlg.setResizable(false);
                Show.centrateComponent(Globals.getMainFrame(), dlg);
                dlg.setVisible(true);
                ParameterEditPanel.this.schema = scp.getSchema();
                DBColumn column = dlg.getColumn();
                DBColumn shownColumn = dlg.getShownColumn();
                if (column != null) {
                    String text = column.getTable() + "." + column.getName();
                    String type = dlg.getJavaType();
                    if (shownColumn != null) {
                        text += "." + shownColumn.getName();
                    }
                    sourceTextArea.setText(text);
                    typeComboBox.setSelectedItem(type);
                    manualSource = false;
                }
            }

        });

        manualSourceButton = new JButton(ImageUtil.getImageIcon("sql"));
        manualSourceButton.setToolTipText(I18NSupport.getString("parameter.manual.select"));
        manualSourceButton.setPreferredSize(buttonDim);
        manualSourceButton.setMinimumSize(buttonDim);
        manualSourceButton.setMaximumSize(buttonDim);
        manualSourceButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String source = null;
                if (manualSource) {
                    source = sourceTextArea.getText();
                }

                if (parameter != null) {
                    orderBy = parameter.getOrderBy();
                }                
                SourceDialog sd = new SourceDialog(source, orderBy, false);
                Show.centrateComponent(Globals.getMainFrame(), sd);
                sd.setVisible(true);
                if (sd.okPressed()) {
                    orderBy = sd.getOrderBy();
                    List<String> types = sd.getTypes();
                    sourceTextArea.setText(sd.getSource());
                    String type = types.get(0);
                    if (!findJavaType(type)) {
                        Show.error(I18NSupport.getString("parameter.manual.select.error", type));
                        type = null;
                    }
                    typeComboBox.setSelectedItem(type);
                    manualSource = true;
                }
            }
        });

        generalPanel.add(sourceButton,
                new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));
        generalPanel.add(manualSourceButton,
                new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

        clearButton = new JButton(ImageUtil.getImageIcon("clear"));
        clearButton.setToolTipText(I18NSupport.getString("parameter.clear.source"));
        clearButton.setPreferredSize(buttonDim);
        clearButton.setMinimumSize(buttonDim);
        clearButton.setMaximumSize(buttonDim);
        clearButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sourceTextArea.setText("");
                if (parameter != null) {
                    parameter.setSource(null);
                    parameter.setManualSource(false);
                    typeComboBox.setEnabled(true);
                }
            }

        });

        generalPanel.add(clearButton,
                new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

        typeComboBox = new JComboBox(QueryParameter.ALL_VALUES);
        typeComboBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                String tmp = (String) value;
                // type of the column is not specified in dialect getTypeClassName method!!!
                if (tmp == null) {
                    return this;
                }
                tmp = tmp.substring(tmp.lastIndexOf('.') + 1);
                setText(tmp);

                return this;
            }

        });
        typeComboBox.setSelectedItem(QueryParameter.STRING_VALUE); // default
        typeComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                defaultValuesModel.clear();
            }
        });

        generalPanel.add(new JLabel(I18NSupport.getString("parameter.type")),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        generalPanel.add(typeComboBox,
                new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

        String[] SELECTIONS = {
                getType(SINGLE_SELECTION),
                getType(MULTIPLE_SELECTION)
        };

        generalPanel.add(new JLabel(I18NSupport.getString("parameter.selection")),
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        generalPanel.add(selectionComboBox = new JComboBox(SELECTIONS),
                new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

        generalPanel.add(new JLabel(I18NSupport.getString("parameter.mandatory")),
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        generalPanel.add(mandatoryCheck = new JCheckBox(),
                new GridBagConstraints(1, 8, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

        generalPanel.add(new JLabel(I18NSupport.getString("parameter.description")),
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        generalPanel.add(new JScrollPane(descriptionTextArea = new JTextArea(5, 20)),
                new GridBagConstraints(1, 9, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

        tabPanel.addTab(I18NSupport.getString("parameter.general.name"), generalPanel);

        JPanel defPanel = new JPanel();
        defPanel.setLayout(new GridBagLayout());

        defaultValuesList = new JXList();
        defaultValuesModel = new DefaultListModel();
        JScrollPane scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(20, 60));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().add(defaultValuesList, null);
        defaultValuesList.setModel(defaultValuesModel);
        defAddButton = new JButton(ImageUtil.getImageIcon("add"));
        defAddButton.setToolTipText(I18NSupport.getString("parameter.default.add"));
        defAddButton.setPreferredSize(buttonDim);
        defAddButton.setMinimumSize(buttonDim);
        defAddButton.setMaximumSize(buttonDim);
        defAddButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (!"".equals(defaultSourceTextArea.getText().trim())) {
                    Show.info(I18NSupport.getString("parameter.default.duplicate"));
                    return;
                }
                ParameterValueSelectionPanel panel = new ParameterValueSelectionPanel((String) getTypeComboBox().getSelectedItem());
                ParameterValueSelectionDialog dialog = new ParameterValueSelectionDialog(panel);

                dialog.pack();
                Show.centrateComponent(Globals.getMainFrame(), dialog);
                dialog.setVisible(true);
                if (dialog.okPressed()) {
                    List<Serializable> values = dialog.getValues();
                    boolean add = false;
                    for (Serializable value : values) {
                        if (!defaultValuesModel.contains(value)) {
                            defaultValuesModel.addElement(value);
                            add = true;
                        }
                    }
                    if (!add && (values.size() > 0)) {
                        Show.info(I18NSupport.getString("parameter.default.value.exists"));
                    }
                }
            }

        });

        defRemoveButton = new JButton(ImageUtil.getImageIcon("clear"));
        defRemoveButton.setToolTipText(I18NSupport.getString("parameter.default.remove"));
        defRemoveButton.setPreferredSize(buttonDim);
        defRemoveButton.setMinimumSize(buttonDim);
        defRemoveButton.setMaximumSize(buttonDim);
        defRemoveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int[] indices = defaultValuesList.getSelectedIndices();
                for (int i=indices.length-1; i>=0; i--) {                    
                    defaultValuesModel.removeElementAt(indices[i]);
                }
            }

        });

        defPanel.add(new JXTitledSeparator(I18NSupport.getString("parameter.defValues")),
                new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
        defPanel.add(new JLabel(I18NSupport.getString("parameter.hidden")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        defPanel.add(hiddenCheck = new JCheckBox(),
                new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        defPanel.add(new JLabel(I18NSupport.getString("parameter.default")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
        defPanel.add(scroll,
                new GridBagConstraints(1, 2, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0));

        defPanel.add(defAddButton,
                new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));
        defPanel.add(defRemoveButton,
                new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

        defPanel.add(new JLabel(I18NSupport.getString("parameter.default.source")),
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        JScrollPane scrollDefault = new JScrollPane(defaultSourceTextArea = new JTextArea(5, 20));
        scrollDefault.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        defPanel.add(scrollDefault,
                new GridBagConstraints(1, 4, 1, 3, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0));
        defaultSourceTextArea.setEditable(false);

        defaultSourceButton = new JButton(ImageUtil.getImageIcon("sql"));
        defaultSourceButton.setToolTipText(I18NSupport.getString("parameter.default.source.add"));
        defaultSourceButton.setPreferredSize(buttonDim);
        defaultSourceButton.setMinimumSize(buttonDim);
        defaultSourceButton.setMaximumSize(buttonDim);
        defaultSourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (defaultValuesModel.size() > 0) {
                    Show.info(I18NSupport.getString("parameter.default.duplicate"));
                    return;
                }
                String source = defaultSourceTextArea.getText();
                SourceDialog sd = new SourceDialog(source, QueryParameter.NO_ORDER, true);
                Show.centrateComponent(Globals.getMainFrame(), sd);
                sd.setVisible(true);
                if (sd.okPressed()) {
                    if (sd.getSource().contains("${")) {
                        Show.info(I18NSupport.getString("parameter.default.source.invalid"));
                        return;
                    }
                    List<String> types = sd.getTypes();
                    defaultSourceTextArea.setText(sd.getSource());
                    String type = types.get(0);
                    if (!findJavaType(type)) {
                        Show.error(I18NSupport.getString("parameter.default.source.select.error", type));
                        type = null;
                    }
                }
            }
        });


        defPanel.add(defaultSourceButton,
                new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));


        defaultClearButton = new JButton(ImageUtil.getImageIcon("clear"));
        defaultClearButton.setToolTipText(I18NSupport.getString("parameter.clear.source"));
        defaultClearButton.setPreferredSize(buttonDim);
        defaultClearButton.setMinimumSize(buttonDim);
        defaultClearButton.setMaximumSize(buttonDim);
        defaultClearButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                defaultSourceTextArea.setText("");
                if (parameter != null) {
                    parameter.setDefaultSource(null);
                }
            }

        });

        defPanel.add(defaultClearButton,
                        new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                GridBagConstraints.NONE, new Insets(0, 2, 5, 5), 0, 0));

        tabPanel.addTab(I18NSupport.getString("parameter.defValues.name"), defPanel);

        procedureCheck = new JCheckBox();
        procedureCheck.setFocusPainted(false);
        previewValueTextField = new JTextField();
        procedureCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // enable or disable all components
                boolean enable = procedureCheck.isSelected();
                enableProcedureComponents(enable);
            }
        });
        enableProcedureComponents(false);

        JPanel procPanel = new JPanel();
        procPanel.setLayout(new GridBagLayout());

        procPanel.add(new JXTitledSeparator(I18NSupport.getString("parameter.procedure")),
                new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));

        procPanel.add(new JLabel(I18NSupport.getString("parameter.procedure.use")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        procPanel.add(procedureCheck,
                new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

        procPanel.add(new JLabel(I18NSupport.getString("parameter.procedure.preview")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        procPanel.add(previewValueTextField,
                new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        procPanel.add(new JLabel(""),
                new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        tabPanel.addTab(I18NSupport.getString("parameter.procedure.name"), procPanel);

        add(tabPanel,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));        

        if (parameter != null) {            
            nameTextField.setText(parameter.getName());
            runtimeNameTextField.setText(parameter.getRuntimeName());
            descriptionTextArea.setText(parameter.getDescription());
            typeComboBox.setSelectedItem(parameter.getValueClassName());
            sourceTextArea.setText(parameter.getSource());
            selectionComboBox.setSelectedItem(getType(parameter.getSelection()));
            mandatoryCheck.setSelected(parameter.isMandatory());
            manualSource = parameter.isManualSource();
            procedureCheck.setSelected(parameter.isProcedureParameter());
            enableProcedureComponents(parameter.isProcedureParameter());
            previewValueTextField.setText(parameter.getPreviewValue());
            if (parameter.getDefaultValues() != null) {
                for (Serializable defValue : parameter.getDefaultValues()) {
                    defaultValuesModel.addElement(defValue);
                }
            }
            defaultSourceTextArea.setText(parameter.getDefaultSource());
            hiddenCheck.setSelected(parameter.isHidden());
            orderBy = parameter.getOrderBy();
        }
    }

    private void enableProcedureComponents(boolean enable) {
        previewValueTextField.setEnabled(enable);
    }


    private String getType(String parameterType) {
        if (SINGLE_SELECTION.equals(parameterType)) {
            return I18NSupport.getString("parameter.type.single");
        } else {
            return I18NSupport.getString("parameter.type.multiple");
        }
    }

    private String getParameterType(int selectionIndex) {
        if (selectionIndex == 0) {
            return SINGLE_SELECTION;
        } else {
            return MULTIPLE_SELECTION;
        }
    }

    public String getSchema() {
        return schema;
    }

    private boolean findJavaType(String type) {
        for (String s : QueryParameter.ALL_VALUES) {
            if (s.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public byte getOrderBy() {
        return orderBy;
    }

    public ArrayList<Serializable> getDefaultValues() {
        ArrayList<Serializable> result =new ArrayList<Serializable>();
        for (Enumeration en = defaultValuesModel.elements(); en.hasMoreElements();) {
            result.add((Serializable)en.nextElement());
        }
        return result;
    }
}
