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
package ro.nextreports.designer;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.jdesktop.swingx.JXPanel;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.util.ReportUtil;

public class SelectExpressionPanel extends JXPanel {
	
	private JComboBox combo;

	public SelectExpressionPanel(List<ExpressionBean> list) {
		
		JLabel label = new JLabel(I18NSupport.getString("expression.insert"));
		
		combo = new JComboBox();
		combo.setRenderer(new BasicComboBoxRenderer() {
			 public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
					if (-1 < index) {
						list.setToolTipText(value.toString());
					}
				} else {
					setForeground(list.getForeground());
					setBackground(list.getBackground());					
				}
				setFont(list.getFont());
				setText((value == null) ? "" : ((BandElement)value).getText());
				return this;
			}
		});		
		for (ExpressionBean bean : list) {
			combo.addItem(bean.getBandElement());
		}	
		
		setLayout(new GridBagLayout());
		
		add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
		add(combo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	public String getExpression() {
		return ((ExpressionBandElement)combo.getSelectedItem()).getExpression();
	}
}
