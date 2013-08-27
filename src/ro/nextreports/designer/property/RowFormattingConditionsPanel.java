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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.condition.RowFormattingConditions;
import ro.nextreports.engine.util.ReportUtil;

public class RowFormattingConditionsPanel extends JPanel {
	
	private Dimension dim = new Dimension(20,20);	
	private FormattingConditionsPanel condPanel;
	private JTextField expName;
	private RowFormattingConditions rfc;
	private Component parent;
	private  List<Integer> rows;
	
	public RowFormattingConditionsPanel(RowFormattingConditions rfc,  List<Integer> rows) {		
		this.rows = rows;
		if (rfc == null) {
			rfc = new RowFormattingConditions("");
		}
		this.rfc = rfc;
		initUI();		
	}
	
	private void initUI() {
		setLayout(new GridBagLayout());
		
		JLabel label = new JLabel(I18NSupport.getString("condition.expression"));
		expName = new JTextField();
		expName.setEnabled(false);
		JButton expButton = new JButton(ImageUtil.getImageIcon("add"));		
		expButton.setMinimumSize(dim);
		expButton.setMaximumSize(dim);
		expButton.setPreferredSize(dim);
		expButton.setToolTipText(I18NSupport.getString("expression.add"));
		expButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String bandName = BandUtil.getBand(LayoutHelper.getReportLayout(), rows.get(0)).getName();
		        boolean isStaticBand = !(bandName.equals(ReportLayout.DETAIL_BAND_NAME) ||
		                        bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ||
		                        bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX));
		        boolean isFooterBand = bandName.equals(ReportLayout.FOOTER_BAND_NAME) ||                
		                bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX);
				String exp = ExpressionChooser.showDialog(parent, I18NSupport.getString("condition.expression"), expName.getText(), false, isStaticBand, isFooterBand, bandName);
		        if (exp != null) {
		        	expName.setText(exp);
		        	String type =  ReportLayoutUtil.getExpressionType(exp);
		        	condPanel.setType(type);
		        }
			}
			
		});
		String bandName = BandUtil.getBand(LayoutHelper.getReportLayout(), rows.get(0)).getName();
		final List<ExpressionBean> list = ReportUtil.getExpressions(LayoutHelper.getReportLayout(), bandName);
		JButton selectExpButton = new JButton(ImageUtil.getImageIcon("expression"));		
		selectExpButton.setMinimumSize(dim);
		selectExpButton.setMaximumSize(dim);
		selectExpButton.setPreferredSize(dim);
		selectExpButton.setToolTipText(I18NSupport.getString("expression.select"));
		if (list.size() == 0) {
			selectExpButton.setEnabled(false);
		} else {
			selectExpButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					String exp = ExpressionChooser.showSelectDialog(parent, I18NSupport.getString("condition.expression"), list);
					if (exp != null) {
						expName.setText(exp);
						String type = ReportLayoutUtil.getExpressionType(exp);
						condPanel.setType(type);
					}
				}

			});
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(label);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(expName);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(expButton);
		panel.add(Box.createHorizontalStrut(2));
		panel.add(selectExpButton);
		
		condPanel = new FormattingConditionsPanel("java.lang.Boolean");
		
		expName.setText(rfc.getExpressionText());
		condPanel.setRenderConditions(rfc);		
		condPanel.setType(ReportLayoutUtil.getExpressionType(rfc.getExpressionText()));
		
		add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		add(condPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
		
	}

	public String getExpressionText() {
		return expName.getText();
	}

	public void setExpressionText(String expressionText) {
		expName.setText(expressionText);
	}

	public FormattingConditionsPanel getCondPanel() {
		return condPanel;
	}
	
	public RowFormattingConditions getConditions() {
		FormattingConditions fc = condPanel.getFinalRenderConditions();
		RowFormattingConditions rc = new RowFormattingConditions(getExpressionText());
		rc.set(fc.getConditions());
		return rc;
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}
		

}
