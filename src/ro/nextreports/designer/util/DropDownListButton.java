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


import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;

import ro.nextreports.designer.ui.MagicButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ContainerEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * User: mihai.panaitescu
 * Date: 07-Jan-2010
 * Time: 13:07:10
 */
public class DropDownListButton extends MagicButton implements
        ChangeListener, PopupMenuListener, ActionListener,
        PropertyChangeListener {

	private final JButton mainButton = this;
	private final JButton arrowButton = new JButton(ImageUtil.getImageIcon("dropdown"));

	private boolean popupVisible = false;
	private JPopupMenu popupMenu;

    public DropDownListButton() {
		mainButton.getModel().addChangeListener(this);
		arrowButton.getModel().addChangeListener(this);
		arrowButton.addActionListener(this);
		mainButton.addPropertyChangeListener("enabled", this); // NOI18N
		getPopupMenu().addContainerListener(new MenuContainerListener());
    }

    public DropDownListButton(String text) {
        this();
        setText(text);
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
        int position = 0;
        if (mainButton.getWidth() + arrowButton.getWidth() > popup.getPreferredSize().getWidth()) {
            position = mainButton.getWidth() + arrowButton.getWidth() - (int)popup.getPreferredSize().getWidth();
        }
        popup.show(mainButton, position, mainButton.getHeight());
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

	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
		}

		return popupMenu;
	}

    public void setItems(List<String> items) {
        List<Boolean> marked = new ArrayList<Boolean>();
        for (String item : items) {
            marked.add(Boolean.FALSE);
        }
        setItems(items, marked);
    }

    public void setItems(List<String> items, List<Boolean> marked) {
        if ((items == null) || (marked == null) || (items.size() != marked.size())) {
            throw new IllegalArgumentException("items and marked must have the same size");
        }
        popupMenu = null;
        for (int i=0, size=items.size(); i<size; i++) {
            ItemAction action = new ItemAction(items.get(i), marked.get(i));
            JMenuItem mi = getPopupMenu().add(action);
            if (action.getMarked()) {
                mi.setForeground(Color.LIGHT_GRAY);
            }
        }
    }


    class ItemAction extends AbstractAction {

        private String item;
        private Boolean marked;

        public ItemAction(String item, Boolean marked) {
            this.item = item;
            this.marked = marked;
            putValue(NAME, item);
        }

        public void actionPerformed(ActionEvent e) {
            mainButton.setText(getText(item));
            afterSelection(item, marked);
        }

        public Boolean getMarked() {
            return marked;
        }
    }

    protected String getText(String item) {
        return item;
    }

    protected void afterSelection(String item, Boolean marked) {
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
			AbstractButton source = (AbstractButton) e.getSource();
			mainButton.setAction(source.getAction());
		}

    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this);
        panel.add(arrowButton);
        return panel;
    }

    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
        arrowButton.setMinimumSize(new Dimension((int)arrowButton.getPreferredSize().getWidth(), (int)minimumSize.getHeight()));
    }

    public void setMaximumSize(Dimension maximumSize) {
        super.setMaximumSize(maximumSize);
        arrowButton.setMaximumSize(new Dimension((int)arrowButton.getPreferredSize().getWidth(), (int)maximumSize.getHeight()));
    }

    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        arrowButton.setMaximumSize(new Dimension((int)arrowButton.getPreferredSize().getWidth(), (int)preferredSize.getHeight()));
    }

    public void setArrowTooltip(String tooltip) {
        arrowButton.setToolTipText(tooltip);
    }
}
