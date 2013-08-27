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

import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.MatchCriteria;
import ro.nextreports.engine.querybuilder.sql.Operator;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import ro.nextreports.engine.querybuilder.sql.ParameterConstants;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

import ro.nextreports.engine.queryexec.QueryParameter;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Mar 31, 2006
 * Time: 11:09:22 AM
 */
public class CriteriaPanel extends JPanel {

	private JLabel columnNameLabel;
	private JLabel columnValueLabel;
	private JLabel operatorLabel;
	private JComboBox operatorComboBox;
	private JRadioButton valueRadioButton;
	private JRadioButton paramRadioButton;
	private JTextField valueTextField;
	private JTextField valueTextField2;
	private JComboBox paramComboBox;
	private JComboBox paramComboBox2;
	private JButton addParamButton;
	private JButton addParamButton2;

	private String ADD = I18NSupport.getString("criteria.panel.add");
	private String EDIT = I18NSupport.getString("criteria.panel.edit");
	private String CRITERIA = I18NSupport.getString("criteria.panel.query.criteria");
    private String OR_CRITERIA = I18NSupport.getString("criteria.panel.query.criteria.or");
    private String COLUMN_NAME = I18NSupport.getString("criteria.panel.column.name");
	private String OPERATOR = I18NSupport.getString("criteria.panel.operator");
	private String VALUE = I18NSupport.getString("criteria.panel.value");
	private String PARAMETER = I18NSupport.getString("criteria.panel.parameter");
	private String ADD_PARAM = I18NSupport.getString("criteria.panel.add.parameter");


	private Dimension dim = new Dimension(200, 20);
	private Dimension buttonDim = new Dimension(20, 20);

	private MyRow row;
	private SelectQuery selectQuery;
	private boolean add; // add / edit
    private boolean or;

    public CriteriaPanel(MyRow row, SelectQuery selectQuery, boolean add) {
		this(row,selectQuery,add, false);
	}

    public CriteriaPanel(MyRow row, SelectQuery selectQuery, boolean add, boolean or) {
		if (row == null) {
			throw new IllegalArgumentException("Row is null.");
		}
		this.row = row;
		this.selectQuery = selectQuery;
		this.add = add;
        this.or = or;
        buildUI();
	}

	private void buildUI() {

		setLayout(new GridBagLayout());

		columnNameLabel = new JLabel(COLUMN_NAME);
		columnValueLabel = new JLabel(row.column.getName());
		operatorLabel = new JLabel(OPERATOR);
		operatorComboBox = new JComboBox(Operator.operators);
		valueRadioButton = new JRadioButton(VALUE);
		paramRadioButton = new JRadioButton(PARAMETER);
		ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(valueRadioButton);
		bGroup.add(paramRadioButton);
		bGroup.setSelected(valueRadioButton.getModel(), true);
		valueTextField = new JTextField();
		valueTextField.setPreferredSize(dim);
		valueTextField2 = new JTextField();
		valueTextField2.setPreferredSize(dim);
		paramComboBox = new JComboBox();
		paramComboBox.setPreferredSize(dim);
		paramComboBox2 = new JComboBox();
		paramComboBox2.setPreferredSize(dim);

		addParamButton = new JButton(ImageUtil.getImageIcon("add"));
		addParamButton.setPreferredSize(buttonDim);
		addParamButton.setMinimumSize(buttonDim);
		addParamButton.setMaximumSize(buttonDim);
		addParamButton.setToolTipText(ADD_PARAM);
		addParamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParameterEditPanel pp = new ParameterEditPanel(null);
				ParameterEditDialog dialog = new ParameterEditDialog(pp, ADD_PARAM, true);
				dialog.pack();
				Show.centrateComponent(Globals.getMainFrame(), dialog);
				dialog.setVisible(true);
				if (dialog.okPressed() && (dialog.getParameter() != null)) {
					Globals.getMainFrame().getQueryBuilderPanel().addParameter(dialog.getParameter());
					QueryParameter qp = dialog.getParameter();
					if (qp != null) {
						paramComboBox.addItem(qp.getName());
						paramComboBox.setSelectedItem(qp.getName());
					}
				}
			}
		});

		addParamButton2 = new JButton(ImageUtil.getImageIcon("add"));
		addParamButton2.setPreferredSize(buttonDim);
		addParamButton2.setMinimumSize(buttonDim);
		addParamButton2.setMaximumSize(buttonDim);
		addParamButton2.setToolTipText(ADD_PARAM);
		addParamButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParameterEditPanel pp = new ParameterEditPanel(null);
				ParameterEditDialog dialog = new ParameterEditDialog(pp, ADD_PARAM, true);
				dialog.pack();
				Show.centrateComponent(Globals.getMainFrame(), dialog);
				dialog.setVisible(true);
				if (dialog.okPressed() && (dialog.getParameter() != null)) {
					Globals.getMainFrame().getQueryBuilderPanel().addParameter(dialog.getParameter());
					QueryParameter qp = dialog.getParameter();
					if (qp != null) {
						paramComboBox2.addItem(qp.getName());
						paramComboBox2.setSelectedItem(qp.getName());
					}
				}
			}
		});

		paramComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (paramRadioButton.isSelected()) {
					String paramName = (String)paramComboBox.getSelectedItem();
					QueryParameter param = ParameterManager.getInstance().getParameter(paramName);
					populateOperatorComboBox(param);
				}
			}
		});

		paramComboBox2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (paramRadioButton.isSelected()) {
					String paramName = (String)paramComboBox2.getSelectedItem();
					QueryParameter param = ParameterManager.getInstance().getParameter(paramName);
					populateOperatorComboBox(param);
				}
			}
		});

		List<QueryParameter> params = ParameterManager.getInstance().getParameters();
        for (QueryParameter p : params) {
			paramComboBox.addItem(p.getName());
			paramComboBox2.addItem(p.getName());
		}
        if (params.size() == 0) {
           paramRadioButton.setEnabled(false);
        }


        valueRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueOrParamSelected(valueRadioButton.isSelected());
				if (valueRadioButton.isSelected()) {
					populateOperatorComboBox(null);
				}
			}
		});

		paramRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valueOrParamSelected(!paramRadioButton.isSelected());
				if (paramRadioButton.isSelected()) {
					String paramName = (String)paramComboBox.getSelectedItem();
					QueryParameter param = ParameterManager.getInstance().getParameter(paramName);
					populateOperatorComboBox(param);
				}
			}
		});

		operatorComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String op = (String) operatorComboBox.getSelectedItem();
				if (Operator.isUnar(op)) {
					valueTextField.setEnabled(false);
                    valueTextField2.setText("");
                    valueTextField2.setEnabled(false);
					paramComboBox.setEnabled(false);
					paramComboBox2.setEnabled(false);
					addParamButton.setEnabled(false);
					addParamButton2.setEnabled(false);
				} else {
					valueOrParamSelected(valueRadioButton.isSelected());
				}
			}
		});

		if (!add) {
			MatchCriteria mc;
            if (or) {
                mc = selectQuery.getOrMatchCriteria(row.column, 0);
            } else {
                mc = selectQuery.getMatchCriteria(row.column);
            }
			operatorComboBox.setSelectedItem(mc.getOperator());
			if (mc.isParameter()) {
				bGroup.setSelected(paramRadioButton.getModel(), true);
				String s = mc.getValue();
                if (!mc.isParameter2()) {
					int lastIndex = s.indexOf(ParameterConstants.END_PARAM);
					s = s.substring(ParameterConstants.START_PARAM.length(), lastIndex);
					paramComboBox.setSelectedItem(s);
				} else {
                    String s_2 = mc.getValue2();
                    int firstFirstIndex = s.indexOf(ParameterConstants.START_PARAM);
					int secondFirstIndex = s_2.indexOf(ParameterConstants.START_PARAM);
					int firstLastIndex = s.indexOf(ParameterConstants.END_PARAM);
					int secondLastIndex = s_2.indexOf(ParameterConstants.END_PARAM);
                    String s1 = s.substring(firstFirstIndex + ParameterConstants.START_PARAM.length(), firstLastIndex);
					String s2 = s_2.substring(secondFirstIndex + ParameterConstants.START_PARAM.length(), secondLastIndex);
                    paramComboBox.setSelectedItem(s1);
					paramComboBox2.setSelectedItem(s2);
				}
			} else {
				valueTextField.setText(mc.getValue());
				valueTextField2.setText(mc.getValue2());
			}
		}

		valueOrParamSelected(valueRadioButton.isSelected());

		add(columnNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
		add(columnValueLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
		add(operatorLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(operatorComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		add(valueRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(valueTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		add(valueTextField2, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		add(paramRadioButton, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(paramComboBox, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		add(addParamButton, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		add(paramComboBox2, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
		add(addParamButton2, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));

	}

	private void valueOrParamSelected(boolean valueSelected) {
		String op = getOperator();
		if (Operator.isUnar(op)) {
			return;
		}
		valueTextField.setEnabled(valueSelected);
		paramComboBox.setEnabled(!valueSelected);
		addParamButton.setEnabled(!valueSelected);

		if (Operator.isDoubleValue(op)) {
            if (!valueSelected) {
                valueTextField2.setText("");
            }
            valueTextField2.setEnabled(valueSelected);
			paramComboBox2.setEnabled(!valueSelected);
			addParamButton2.setEnabled(!valueSelected);
		} else {
            valueTextField2.setText("");
            valueTextField2.setEnabled(false);
			paramComboBox2.setEnabled(false);
			addParamButton2.setEnabled(false);
		}
	}

	protected String getTitle() {
        String s = CRITERIA;
        if (or) {
            s = OR_CRITERIA;
        }
        if (add) {
			return ADD + " " + s;
		} else {
			return EDIT + " " + s;
		}
	}

	protected Column getColumn() {
		return row.column;
	}

	protected SelectQuery getSelectQuery() {
		return selectQuery;
	}

	protected boolean added() {
		return add;
	}

	protected String getCriteria() {
		StringBuilder sb = new StringBuilder(row.column.getName());
		sb.append(" ");
		sb.append(operatorComboBox.getSelectedItem());
		sb.append(" ");
		if (valueRadioButton.isSelected()) {
			if (valueTextField2.isEnabled()) {
			   sb.append(valueTextField.getText());
			   sb.append(" AND ");
			   sb.append(valueTextField2.getText());
			} else {
			   sb.append(valueTextField.getText());
			}
		} else {
			if (paramComboBox.isEnabled()) {
				sb.append(ParameterConstants.START_PARAM);
				sb.append(paramComboBox.getSelectedItem());
				sb.append(ParameterConstants.END_PARAM);
				sb.append(" AND ");
				sb.append(ParameterConstants.START_PARAM);
				sb.append(paramComboBox2.getSelectedItem());
				sb.append(ParameterConstants.END_PARAM);
			} else {
				sb.append(ParameterConstants.START_PARAM);
				sb.append(paramComboBox.getSelectedItem());
				sb.append(ParameterConstants.END_PARAM);
			}
		}
		return sb.toString();
	}

	protected String getOperator() {
		return (String)operatorComboBox.getSelectedItem();
	}

	protected String getValue() {

		if (Operator.isUnar(getOperator())) {
			return "";
		}

		if (valueRadioButton.isSelected()) {
			return valueTextField.getText();
		} else {
			return ParameterConstants.START_PARAM + paramComboBox.getSelectedItem() + ParameterConstants.END_PARAM;
		}
	}

	protected String getValue2() {

			if (Operator.isUnar(getOperator())) {
				return "";
			}

			if (valueRadioButton.isSelected()) {
				if (valueTextField2.isEnabled()) {
					return valueTextField2.getText();
				} else {
					return "";
				}
			} else {
				if (paramComboBox2.isEnabled()) {
					return ParameterConstants.START_PARAM + paramComboBox2.getSelectedItem() + ParameterConstants.END_PARAM;
				} else {
					return "";
				}
			}
		}


	protected boolean isParameter() {
		return paramRadioButton.isSelected();
	}

	protected boolean isParameter2() {
		return paramRadioButton.isSelected() && paramComboBox2.isEnabled();
	}

	private void populateOperatorComboBox(QueryParameter param) {
		String selected = (String)operatorComboBox.getSelectedItem();
		if ((param == null) || param.getSelection().equals(ParameterEditPanel.SINGLE_SELECTION)) {
			operatorComboBox.removeAllItems();
			for (int i = 0, size = Operator.operators.length; i < size; i++) {
				operatorComboBox.addItem(Operator.operators[i]);
			}
		} else {
			operatorComboBox.removeAllItems();
			for (int i = 0, size = Operator.multipleOperators.length; i < size; i++) {
				operatorComboBox.addItem(Operator.multipleOperators[i]);
			}
		}
		operatorComboBox.setSelectedItem(selected);
	}
}
