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
package ro.nextreports.designer.wizrep;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.exporter.util.IndicatorData;

public class SelectIndicatorSettingsWizardPanel extends WizardPanel {
	
	private JTextField titleField;
	private JTextField descField;
	private JTextField unitField;
	private JTextField minField;
	private JTextField maxField;
	private JCheckBox showMinMax;
	private JTextField colorField;
	private Dimension txtDim = new Dimension(150, 20);
	private Dimension buttonDim = new Dimension(20, 20);
	
	public SelectIndicatorSettingsWizardPanel() {
		super();
		banner.setTitle(I18NSupport.getString("wizard.panel.step", 5, 5) + I18NSupport.getString("wizard.panel.indicator.title"));
		banner.setSubtitle(I18NSupport.getString("wizard.panel.indicator.subtitle"));
		init();
	}


	@Override
	public void onDisplay() {				
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public boolean validateNext(List<String> messages) {
		return true;
	}

	@Override
	public WizardPanel getNextPanel() {		
		return null;
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean validateFinish(List<String> messages) {
		if (titleField.getText().trim().isEmpty()) {
			messages.add(I18NSupport.getString("wizard.panel.indicator.error.title"));
			return false;
		}
		int min;
		try {
			min = Integer.parseInt(minField.getText());
		} catch (NumberFormatException ex) {
			messages.add(I18NSupport.getString("wizard.panel.indicator.error.min"));
			return false;
		}
		int max;
		try {
			max = Integer.parseInt(maxField.getText());
		} catch (NumberFormatException ex) {
			messages.add(I18NSupport.getString("wizard.panel.indicator.error.max"));
			return false;
		}
		int color;
		try {
			color = Integer.parseInt(colorField.getText());
		} catch (NumberFormatException ex) {
			messages.add(I18NSupport.getString("wizard.panel.indicator.error.color"));
			return false;
		}
		
		context.setAttribute(WizardConstants.INDICATOR_DATA, getData()); 		
		WizardUtil.openReport(context, null);    
		
		return true;
	}

	@Override
	public void onFinish() {				
	}
	
	private void init() {
		setLayout(new GridBagLayout());  
		
		JLabel titleLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.title"));
		titleField = new JTextField();
		titleField.setPreferredSize(txtDim);
		titleField.setMinimumSize(txtDim);
		
		JLabel descLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.description"));
		descField = new JTextField();
		descField.setPreferredSize(txtDim);
		descField.setMinimumSize(txtDim);
		
		JLabel unitLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.unit"));
		unitField = new JTextField();
		unitField.setPreferredSize(txtDim);
		unitField.setMinimumSize(txtDim);
		
		JLabel minLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.min"));
		minField = new JTextField();
		minField.setPreferredSize(txtDim);
		minField.setMinimumSize(txtDim);
		
		JLabel maxLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.max"));
		maxField = new JTextField();
		maxField.setPreferredSize(txtDim);
		maxField.setMinimumSize(txtDim);
		
		showMinMax = new JCheckBox(I18NSupport.getString("wizard.panel.indicator.data.show"));
		
		JLabel colorLabel = new JLabel(I18NSupport.getString("wizard.panel.indicator.data.color"));
		colorField = new JTextField();
		colorField.setEditable(false);
		colorField.setPreferredSize(txtDim);
		colorField.setMinimumSize(txtDim);
		JButton colorButton = new JButton();
		colorButton.setPreferredSize(buttonDim);
		colorButton.setMinimumSize(buttonDim);
		colorButton.setMaximumSize(buttonDim);
		colorButton.setIcon(ImageUtil.getImageIcon("copy_settings"));
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = ExtendedColorChooser.showDialog(SwingUtilities.getWindowAncestor(SelectIndicatorSettingsWizardPanel.this), 
						I18NSupport.getString("color.dialog.title"), null);
				if (color != null) {
					colorField.setText(String.valueOf(color.getRGB()));	
					colorField.setBackground(color);
				}
			}			
		});		
		
		JLabel imageLabel = new JLabel(ImageUtil.getImageIcon("indicator_main"));
		imageLabel.setPreferredSize(new Dimension(280, 170));
		
		add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(titleField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(imageLabel, new GridBagConstraints(3, 0, 1, 8, 1.0, 1.0,  GridBagConstraints.CENTER, 
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		add(descLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(descField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(unitLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(unitField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(minLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(minField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(maxLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(maxField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(showMinMax, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(colorLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(colorField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		add(colorButton, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,  GridBagConstraints.WEST, 
				GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
	}
	
	public IndicatorData getData() {
		IndicatorData data = new IndicatorData();
		data.setTitle(titleField.getText());
		data.setDescription(descField.getText());
		data.setUnit(unitField.getText());
		data.setMin(Integer.parseInt(minField.getText()));
		data.setMax(Integer.parseInt(maxField.getText()));
		data.setColor(new Color(Integer.parseInt(colorField.getText())));
		return data;
	}

}
