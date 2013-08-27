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

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ro.nextreports.designer.ui.ComponentTitledBorder;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.querybuilder.sql.JoinType;
import ro.nextreports.engine.querybuilder.sql.Operator;

/**
 * @author Decebal Suiu
 */
public class JoinPropertiesPanel extends JPanel {

    private static final String[] operators = {
        Operator.EQUAL,
        Operator.NOT_EQUAL,
        Operator.GREATER,
        Operator.GREATER_EQUAL,
        Operator.LESS,
        Operator.LESS_EQUAL,
        Operator.LIKE,
        Operator.NOT_LIKE,
        Operator.IN,
        Operator.NOT_IN
    };

    private JCheckBox outerCheckBox = new JCheckBox(I18NSupport.getString("join.properties.outer"));
    private JRadioButton leftRadioButton = new JRadioButton(I18NSupport.getString("join.properties.left"), true);
    private JRadioButton rightRadioButton = new JRadioButton(I18NSupport.getString("join.properties.right"));
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField leftTextField = new JTextField(15);
    private JTextField rightTextField = new JTextField(15);
    private JComboBox operatorsComboBox = new JComboBox(operators);
    private JoinLine joinLine;
    private String operator;
    private boolean outerJoin;
    private boolean leftJoin;

    public JoinPropertiesPanel(JoinLine joinLine) {
        super();

        this.joinLine = joinLine;
        operator = joinLine.getJoinCriteria().getOperator();
        outerJoin = JoinType.isOuter(joinLine.getJoinCriteria().getJoinType());
        leftJoin = JoinType.LEFT_OUTER_JOIN.equals(joinLine.getJoinCriteria().getJoinType());
        initUI();
    }

    private void initUI() {
        final JPanel outerJoinPanel = new JPanel();
        outerJoinPanel.add(leftRadioButton);
        outerJoinPanel.add(rightRadioButton);

        // create the layout
        double[] columns = {
                TableLayoutConstants.FILL,
        };
        double[] rows = {
                TableLayoutConstants.PREFERRED,
                TableLayoutConstants.PREFERRED
        };
        TableLayout layout = new TableLayout(columns, rows);
        layout.setVGap(6);
        this.setLayout(layout);

        this.add(outerJoinPanel, "0, 0");
        this.add(createJoinPanel(), "0, 1");

        outerCheckBox.setFocusPainted(false);
        ComponentTitledBorder componentBorder = new ComponentTitledBorder(outerCheckBox,
                outerJoinPanel, BorderFactory.createEtchedBorder());

        outerCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e){
                // enable or disable all components
                boolean enable = outerCheckBox.isSelected();
                operatorsComboBox.setEnabled(!enable);
                Component comp[] = outerJoinPanel.getComponents();
                for (int i = 0; i < comp.length; i++) {
                    comp[i].setEnabled(enable);
                }
            }

        });
        outerJoinPanel.setBorder(componentBorder);

        buttonGroup.add(leftRadioButton);
        buttonGroup.add(rightRadioButton);

        outerCheckBox.setSelected(outerJoin);
        operatorsComboBox.setEnabled(!outerCheckBox.isSelected());
        leftRadioButton.setSelected(leftJoin);
        leftRadioButton.setEnabled(outerCheckBox.isSelected());
        rightRadioButton.setSelected(!leftJoin);
        rightRadioButton.setEnabled(outerCheckBox.isSelected());

        leftTextField.setEditable(false);
        leftTextField.setText(joinLine.getFirstColumn().toString());

        operatorsComboBox.setSelectedItem(operator);

        rightTextField.setEditable(false);
        rightTextField.setText(joinLine.getSecondColumn().toString());
                
        outerCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                leftRadioButton.setEnabled(outerCheckBox.isSelected());
                rightRadioButton.setEnabled(outerCheckBox.isSelected());
                outerJoin = outerCheckBox.isSelected();

                if (outerCheckBox.isSelected()) {
                    leftJoin = leftRadioButton.isSelected();

                    // only EQUAL allowed for outer joins
                    operator = Operator.EQUAL;
                    operatorsComboBox.setSelectedItem(operator);
                    joinLine.getJoinCriteria().setOperator(operator);
                }
            }

        });

        leftRadioButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                leftJoin = leftRadioButton.isSelected();
            }

        });

        rightRadioButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                leftJoin = !rightRadioButton.isSelected();
            }

        });

        operatorsComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                operator = (String) operatorsComboBox.getSelectedItem();                
                joinLine.getJoinCriteria().setOperator(operator);
            }

        });

    }

    private JPanel createJoinPanel() {
        JPanel joinPanel = new JPanel();

        // create the layout
        double[] columns = {
                TableLayoutConstants.PREFERRED,
                TableLayoutConstants.PREFERRED,
                TableLayoutConstants.PREFERRED
        };
        double[] rows = {
                TableLayoutConstants.PREFERRED
        };
        TableLayout layout = new TableLayout(columns, rows);
        layout.setVGap(6);
        layout.setHGap(6);
        joinPanel.setLayout(layout);

        joinPanel.add(leftTextField, "0, 0");
        joinPanel.add(operatorsComboBox, "1, 0");
        joinPanel.add(rightTextField, "2, 0");

        return joinPanel;
    }

    public JoinLine getJoinLine() {
        return joinLine;
    }

    public boolean isLeftJoin() {
        return leftJoin;
    }

    public boolean isOuterJoin() {
        return outerJoin;
    }

    public String getOperator() {
        return operator;
    }

}
