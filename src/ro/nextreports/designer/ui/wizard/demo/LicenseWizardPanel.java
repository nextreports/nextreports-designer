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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import ro.nextreports.designer.ui.wizard.WizardPanel;


/**
 * An implementation of the base class used for implementing a panel that is
 * displayed in a Wizard. Shows some sample license data.
 *
 * @author Decebal Suiu
 */
public class LicenseWizardPanel extends WizardPanel {

    private String license = "If you agree to this license, please "
            + "choose 'I agree'.\n"
            + "If you don't agree, then you won't be able to continue.";

    private JCheckBox accept;

    private final WizardPanel chooseFile = new ChooseFileWizardPanel();

    public LicenseWizardPanel() {
        super();
        banner.setTitle("License");
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
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {
        if (!accept.isSelected()) {
            messages.add("Please accept the license to continue.");
            return false;
        }

        return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return chooseFile;
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return false;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List<String> messages) {
        return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

	private void initComponents() {
		setLayout(new BorderLayout());

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setText(license);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
        JPanel agreePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        agreePanel.add(accept = new JCheckBox("I agree"));
        add(agreePanel, BorderLayout.SOUTH);
	}

}
