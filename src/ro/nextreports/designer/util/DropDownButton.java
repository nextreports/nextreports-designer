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
package ro.nextreports.designer.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author Decebal Suiu
 */
public class DropDownButton extends JButton implements
		ChangeListener, PopupMenuListener, ActionListener,
		PropertyChangeListener {

	private final JButton mainButton = this;
	private final JButton arrowButton = new JButton(ImageUtil.getImageIcon("dropdown"));

	private boolean popupVisible = false;
	private JPopupMenu popupMenu;

	public DropDownButton() {
		mainButton.getModel().addChangeListener(this);
		arrowButton.getModel().addChangeListener(this);
		arrowButton.addActionListener(this);
		mainButton.addPropertyChangeListener("enabled", this); // NOI18N
		getPopupMenu().addContainerListener(new MenuContainerListener());
	}

	public void propertyChange(PropertyChangeEvent event) {
		arrowButton.setEnabled(mainButton.isEnabled());
	}

	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == mainButton.getModel()) {
			if (popupVisible && !mainButton.getModel().isRollover()) {
				mainButton.getModel().setRollover(true);
				return;
			}
			arrowButton.getModel().setRollover(mainButton.getModel().isRollover());
			arrowButton.setSelected(mainButton.getModel().isArmed()
					&& mainButton.getModel().isPressed());
		} else {
			if (popupVisible && !arrowButton.getModel().isSelected()) {
				arrowButton.getModel().setSelected(true);
				return;
			}
			mainButton.getModel().setRollover(arrowButton.getModel().isRollover());
		}
	}

	public void actionPerformed(ActionEvent event) {
		JPopupMenu popup = getPopupMenu();
		popup.addPopupMenuListener(this);
		popup.show(mainButton, 0, mainButton.getHeight());
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
		popupVisible = true;
		mainButton.getModel().setRollover(true);
		arrowButton.getModel().setSelected(true);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
		popupVisible = false;

		mainButton.getModel().setRollover(false);
		arrowButton.getModel().setSelected(false);
		((JPopupMenu) event.getSource()).removePopupMenuListener(this);
	}

	public void popupMenuCanceled(PopupMenuEvent event) {
		popupVisible = false;
	}

	public JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
		}
		
		return popupMenu;
	}

	public JButton addToToolBar(JToolBar toolbar) {
		toolbar.add(mainButton);
		toolbar.add(arrowButton);
		return mainButton;
	}
	
    public class MenuContainerListener implements ContainerListener {
    	
    	MenuActionListener menuActionListener;
    	
    	public MenuContainerListener() {
    		menuActionListener = new MenuActionListener();
    	}
    	
        public void componentAdded(ContainerEvent e) {
            ((AbstractButton) e.getChild()).addActionListener(menuActionListener);
        }
        
        public void componentRemoved(ContainerEvent e) {
            ((AbstractButton) e.getChild()).removeActionListener(menuActionListener);
        }
        
    }
    
    private class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			getPopupMenu().insert(mainButton.getAction(), 0);
			AbstractButton source = (AbstractButton) e.getSource();
			getPopupMenu().remove(source);
			mainButton.setAction(source.getAction());
//			mainButton.setText(""); // reset text
		}
    	
    }

}
