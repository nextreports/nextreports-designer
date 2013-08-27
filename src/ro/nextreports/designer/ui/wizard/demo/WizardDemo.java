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

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * Run this class to see the example Wizard.
 *
 * @author Decebal Suiu
 */
public class WizardDemo implements WizardListener {

    public static void main(String args[]) {
    	// set look and feel
    	PlasticLookAndFeel laf = new Plastic3DLookAndFeel();
    	PlasticLookAndFeel.setCurrentTheme(new ExperienceBlue());
    	try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

        JFrame frame = new JFrame("Wizard Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Wizard wizard = new Wizard(new WelcomeWizardPanel());
        wizard.addWizardListener(new WizardDemo());
        frame.setContentPane(wizard);
//        frame.pack();
        frame.setSize(450, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Called when the wizard finishes.
     *
     * @param wizard the wizard that finished.
     */
    public void wizardFinished(Wizard wizard) {
        System.out.println("wizard finished");
        System.exit(0);
    }

    /**
     * Called when the wizard is cancelled.
     *
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard) {
        System.out.println("wizard cancelled");
        System.exit(0);
    }

    /**
     * Called when a new panel has been displayed in the wizard.
     *
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard) {
        System.out.println("wizard new panel");
    }

}
