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
package ro.nextreports.designer.ui.wizard.demo;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.ui.wizard.WizardPanel;


/**
 * An implementation of the base class used for implementing a panel that is
 * displayed in a Wizard. Shows how to implement a choose file wizard panel.
 *
 * @author Decebal Suiu
 */
public class ChooseFileWizardPanel extends WizardPanel {

	private JTextField fileTextField;

    public ChooseFileWizardPanel() {
        super();
        banner.setTitle("Choose File");
        initComponents();
    }

	/**
     * Called when the panel is set.
     */
    public void onDisplay() {
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages
     *            a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {
    	return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return null;
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return true;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages
     *            a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List<String> messages) {
        return true;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

    private void initComponents() {
    	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    	panel.add(new JLabel("File"));
    	panel.add(Box.createHorizontalStrut(6));
    	fileTextField = new JTextField(20);
    	fileTextField.setMaximumSize(fileTextField.getPreferredSize());
    	panel.add(fileTextField);
    	panel.add(Box.createHorizontalStrut(6));
    	JButton browseButton = new JButton("...");
    	browseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				browse();
			}

    	});
    	panel.add(browseButton);
    	add(panel);
	}

    private void browse() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            fileTextField.setText(selectedFile.getPath());
        }
    }

}
