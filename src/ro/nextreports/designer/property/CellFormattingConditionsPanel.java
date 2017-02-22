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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.util.ReportUtil;

public class CellFormattingConditionsPanel extends JPanel {
	
	private Dimension dim = new Dimension(20,20);	
	private FormattingConditionsPanel condPanel;
	private JTextField expName;
	private JRadioButton currentButton;
	private JRadioButton otherButton;
	private JButton expButton;
	private JButton selectExpButton;	
	private Component parent;	
	private String type;
	private String bandName;
	
	public CellFormattingConditionsPanel(String type,  String bandName) {						
		this.type = type;
		this.bandName = bandName;
		initUI();		
	}
	
	private void initUI() {
		setLayout(new GridBagLayout());
		
		currentButton = new JRadioButton(I18NSupport.getString("condition.use.current.value"));
        otherButton = new JRadioButton(I18NSupport.getString("condition.use.other.value"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(currentButton);
        bg.add(otherButton);
        currentButton.setSelected(true);   
               
        currentButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				expName.setEnabled(!currentButton.isSelected());	
				expButton.setEnabled(!currentButton.isSelected());	
				selectExpButton.setEnabled(!currentButton.isSelected());	
			}        	
        });
        otherButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				expName.setEnabled(otherButton.isSelected());
				expButton.setEnabled(otherButton.isSelected());
				selectExpButton.setEnabled(otherButton.isSelected());
			}        	
        });
		
		JLabel label = new JLabel(I18NSupport.getString("condition.expression"));
		expName = new JTextField();
		expName.setEnabled(false);
		// when expression is modified directly from text field, the type can change
		// so we always test on focus lost for the new type
		expName.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {				
				// nothing to do
			}

			@Override
			public void focusLost(FocusEvent e) {
				String type = ReportLayoutUtil.getExpressionType(expName.getText());
				condPanel.setType(type);				
			}			
		});
		
		expButton = new JButton(ImageUtil.getImageIcon("add"));		
		expButton.setMinimumSize(dim);
		expButton.setMaximumSize(dim);
		expButton.setPreferredSize(dim);
		expButton.setToolTipText(I18NSupport.getString("expression.add"));
		expButton.setEnabled(false);
		expButton.addActionListener(new ActionListener() {			

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isStaticBand = !(bandName.equals(ReportLayout.DETAIL_BAND_NAME)
						|| bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) || bandName
						.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX));
				boolean isFooterBand = bandName.equals(ReportLayout.FOOTER_BAND_NAME)
						|| bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX);
				String exp = ExpressionChooser.showDialog(parent, I18NSupport.getString("condition.expression"),
						expName.getText(), false, isStaticBand, isFooterBand, bandName);
				if (exp != null) {
					expName.setText(exp);
					String type = ReportLayoutUtil.getExpressionType(exp);
					condPanel.setType(type);
				}

			}
			
		});		
		final List<ExpressionBean> list = ReportUtil.getExpressions(LayoutHelper.getReportLayout(), bandName);
		selectExpButton = new JButton(ImageUtil.getImageIcon("expression"));		
		selectExpButton.setMinimumSize(dim);
		selectExpButton.setMaximumSize(dim);
		selectExpButton.setPreferredSize(dim);
		selectExpButton.setToolTipText(I18NSupport.getString("expression.select"));
		selectExpButton.setEnabled(false);
		if (list.size() == 0) {
			selectExpButton.setEnabled(false);
		} else {
			selectExpButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String exp = ExpressionChooser.showSelectDialog(parent, I18NSupport.getString("condition.expression"), list);
					if (exp != null) {
						expName.setText(exp);
						String type = ReportLayoutUtil.getExpressionType(exp);
						condPanel.setType(type);
					}
				}

			});
		}
								
		JPanel typePanel = new JPanel();
		typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
		typePanel.add(currentButton);
		typePanel.add(Box.createHorizontalStrut(5));
		typePanel.add(otherButton);		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(label);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(expName);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(expButton);
		panel.add(Box.createHorizontalStrut(2));
		panel.add(selectExpButton);
		
		condPanel = new FormattingConditionsPanel(type);		
				
		add(typePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		add(condPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
		
	}

	public String getExpressionText() {		
		return expName.getText();
	}

	public void setExpressionText(String expressionText) {
		if (expressionText != null) {
			expName.setText(expressionText);
			otherButton.setSelected(true);
		} else {
			currentButton.setSelected(true);
		}
	}

	public FormattingConditionsPanel getCondPanel() {
		return condPanel;
	}
	
	public FormattingConditions getConditions() {
		FormattingConditions fc = condPanel.getFinalRenderConditions();
		FormattingConditions cc = new FormattingConditions();
		String exp = getExpressionText();
		if ((exp != null) && !exp.equals("")) {
			cc.setCellExpressionText(exp);
		}
		cc.set(fc.getConditions());
		return cc;
	}
	
	public void setRenderConditions(FormattingConditions conditions) {
        if (conditions != null) {
            condPanel.setRenderConditions(conditions);
            setExpressionText(conditions.getCellExpressionText());
        }
    }
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
		

}
