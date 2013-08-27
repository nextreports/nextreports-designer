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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.property.FormattingConditionsPanel;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

public class SelectAlarmSettingsWizardPanel extends WizardPanel {
	
	private FormattingConditionsPanel panel;
	private DefaultListModel model;
	private JList list;
	private Dimension dim = new Dimension(100, 200);
	
	public SelectAlarmSettingsWizardPanel() {
		super();
		banner.setTitle(I18NSupport.getString("wizard.panel.step", 5, 5) + I18NSupport.getString("wizard.panel.alarm.title"));
		banner.setSubtitle(I18NSupport.getString("wizard.panel.alarm.subtitle"));
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
		if (panel.getFinalRenderConditions().getConditions().size() == 0) {
			messages.add(I18NSupport.getString("wizard.panel.alarm.conditions"));
			return false;
		}
		if (model.getSize() == 0) {
			messages.add(I18NSupport.getString("wizard.panel.alarm.messages"));
			return false;
		}
		if (panel.getFinalRenderConditions().getConditions().size() != model.getSize()) {
			messages.add(I18NSupport.getString("wizard.panel.alarm.error"));
			return false;
		}
		context.setAttribute(WizardConstants.ALARM_CONDITIONS, panel.getFinalRenderConditions());		 
		context.setAttribute(WizardConstants.ALARM_MESSAGES, getMessages());
		 		
		WizardUtil.openReport(context, null);        
        return true;
	}

	@Override
	public void onFinish() {		
	}
	
	private void init() {
		setLayout(new GridBagLayout());        
		
		panel = new FormattingConditionsPanel(null, I18NSupport.getString("wizard.panel.alarm.conditions.name"));

		model = new DefaultListModel();
		list = new JList();
		list.setModel(model);
		JScrollPane scrollPane = new JScrollPane(list);
	    scrollPane.setPreferredSize(dim);	 
	    	   
	    JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);        

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("add");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("wizard.panel.alarm.message.add");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                add();
            }

        });

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("edit");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("wizard.panel.alarm.message.edit");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                modify();
            }

        });

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("delete");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("wizard.panel.alarm.message.delete");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                delete();
            }

        });	   

        toolBar.add(new JLabel(I18NSupport.getString("wizard.panel.alarm.messages.name")));
        
        JLabel imageLabel = new JLabel(ImageUtil.getImageIcon("alarm_main"));
		imageLabel.setPreferredSize(new Dimension(300, 80));
                
        add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(toolBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
        add(scrollPane, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        add(imageLabel, new GridBagConstraints(1, 0, 1, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 0, 5, 5), 0, 0));
	}
	
	private void add() {
		String message = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(SelectAlarmSettingsWizardPanel.this), 
				I18NSupport.getString("wizard.panel.alarm.message.enter"), "");
        if (message == null) {
            return;
        }
        model.addElement(message);		
	}
	
	private void modify() {
		int index = list.getSelectedIndex();
		if (index == -1) {
			Show.info(SwingUtilities.getWindowAncestor(SelectAlarmSettingsWizardPanel.this), I18NSupport.getString("wizard.panel.alarm.message.select"));
            return;
		}
		String s = (String)list.getSelectedValue();
		String message = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(SelectAlarmSettingsWizardPanel.this), 
				I18NSupport.getString("wizard.panel.alarm.message.edit"), s);
        if (message == null) {
            return;
        }
        model.set(index, message);
	}
	
	private void delete() {
		String s = (String)list.getSelectedValue();
		if (s == null) {
			Show.info(SwingUtilities.getWindowAncestor(SelectAlarmSettingsWizardPanel.this), I18NSupport.getString("wizard.panel.alarm.message.select"));
            return;
		}
		int option = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(SelectAlarmSettingsWizardPanel.this), 
				I18NSupport.getString("wizard.panel.alarm.message.delete") + ": "+ s, 
				I18NSupport.getString("wizard.panel.alarm.message.delete"), JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			model.removeElement(s);
		}
	}
	
	private List<String> getMessages() {
		List<String> messages = new LinkedList<String>();
		for (int i=0, size= model.getSize(); i<size; i++ ){
			messages.add((String)model.elementAt(i));
		}
		return messages;
	}

}
