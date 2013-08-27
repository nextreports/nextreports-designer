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
package ro.nextreports.designer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.util.I18NSupport;


public class HistoryColorChooserPanel extends AbstractColorChooserPanel {
	
	private Dimension btnDim = new Dimension(20, 20);
	private int no = 10;
	private JButton[][] buttons = new JButton[no][no];

	@Override
	protected void buildChooser() {		
		setLayout(new GridBagLayout());
		
		JPanel standardPanel = new JPanel();
        standardPanel.setLayout(new GridLayout(no, no));
        for (int i=0; i<no; i++) {
        	for (int j=0; j<no; j++) {
        		buttons[i][j] = makeColorButton("");
        		standardPanel.add(buttons[i][j]);
        	}	
        }
        add(standardPanel, new GridBagConstraints(0, 0, 4, 4, 1.0, 1.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		
	}
	
	@Override
	public void updateChooser() {				
	}
	
	@Override
	public String getDisplayName() {
		return I18NSupport.getString("colorchooser.history.name");
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}
	
	private JButton makeColorButton(String name) {
        JButton button = new JButton(name);
        button.setPreferredSize(btnDim);
        button.setMinimumSize(btnDim);
        button.setMaximumSize(btnDim);        
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createRaisedBevelBorder()));        
        button.setBackground(Color.GRAY);
        button.setEnabled(false);        
        return button;
    }
		
	public void addColorToHistory(Color color) {
		if ((color == null) || colorFound(color)) {
			return;
		}
		
		// all buttons are moved to right and the color is added at (0,0)
		JButton last = buttons[no-1][no-1];
		for (int i=no-1; i>=0; i--) {
        	for (int j=no-1; j>=0; j--) { 
        		if ((i == 0) && (j == 0)) {
        			buttons[0][0].setBackground(last.getBackground());
        		} else if (j==0) {
        			buttons[i][j].setBackground(buttons[i-1][no-1].getBackground());
        			if (buttons[i-1][no-1].isEnabled()) {
        				activateButton(buttons[i][j]);        				
        			}
        		} else {
        			buttons[i][j].setBackground(buttons[i][j-1].getBackground());
        			if (buttons[i][j-1].isEnabled()) {
        				activateButton(buttons[i][j]); 
        			}
        		} 
        	}
		}			
		buttons[0][0].setBackground(color);		
		buttons[0][0].setToolTipText(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
		if (!buttons[0][0].isEnabled()) {
			activateButton(buttons[0][0]);
		}			        
	}
	
	Action setColorAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton) evt.getSource();
            getColorSelectionModel().setSelectedColor(button.getBackground());
            ExtendedColorChooser.addToHistory = false;
        }
    };
    
    private void activateButton(JButton button) {
    	button.setEnabled(true);
		button.setAction(setColorAction);
		Color color = button.getBackground();
		button.setToolTipText(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
    }
    
    public boolean colorFound(Color color) {
    	if (buttons[0][0] == null) {
    		buildChooser();
    	}
    	for (int i=0; i<no; i++) {
    		for (int j=0; j<no; j++) {
    			if (buttons[i][j].getBackground().equals(color)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

}
