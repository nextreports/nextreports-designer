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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.PaperSize;

public class CustomSizePanel extends JPanel {
	
	private PaperSize paperSize;
	
	private JComboBox unitCombo;
	private JTextField widthText;
	private JTextField heightText;
	
	public CustomSizePanel() {
		super();
		init();
	}
	
	private void init() {
		
		setLayout(new GridBagLayout());
		
		unitCombo = new JComboBox();
		unitCombo.addItem(PaperSize.UNIT_CM);
		unitCombo.addItem(PaperSize.UNIT_IN);		
		
		widthText = new JTextField(10);
		heightText = new JTextField(10);												
		
		add(new JLabel(I18NSupport.getString("paper.size.unit")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(unitCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
		
		add(new JLabel(I18NSupport.getString("paper.size.width")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(widthText, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
		
		add(new JLabel(I18NSupport.getString("paper.size.height")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(heightText, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
										
	}
	
	public PaperSize getPaperSize() {
		if (paperSize == null) {
			paperSize = PaperSize.A4;
		}
		return paperSize;
	}
	
	public void setPaperSize(PaperSize paperSize) {
		this.paperSize = paperSize;
		updatePaperSize();
	}

    private void updatePaperSize() {
    	unitCombo.setSelectedItem(paperSize.getUnit());
    	widthText.setText(String.valueOf(paperSize.getWidth()));
    	heightText.setText(String.valueOf(paperSize.getHeight()));
    }
    
    public PaperSize getFinalPaperSize() {
    	String unit = (String)unitCombo.getSelectedItem();
    	float width = Float.parseFloat(widthText.getText());
    	float height = Float.parseFloat(heightText.getText());
    	return new PaperSize(unit, width, height);
    }

}
