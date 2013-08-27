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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.TreeUtil;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 9, 2008
 * Time: 2:28:15 PM
 */
public class RepWizard implements WizardListener {

    private JDialog dialog;

    public RepWizard() {
        dialog = new JDialog(Globals.getMainFrame(), I18NSupport.getString("wizard.action.name"), true);
        //dialog.setIconImage(ImageUtil.getImage("wizard.png"));
        Wizard wizard = new Wizard(new StartWizardPanel());
        wizard.getContext().setAttribute(WizardConstants.MAIN_FRAME, dialog);
        wizard.addWizardListener(this);
        dialog.setContentPane(wizard);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(null);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                WizardUtil.disconnect();
            }
        });
        dialog.setVisible(true);
    }

    /**
     * Called when the wizard finishes.
     *
     * @param wizard the wizard that finished.
     */
    public void wizardFinished(Wizard wizard) {
//        System.out.println("wizard finished");
        TreeUtil.expandConnectedDataSource();
        dialog.dispose();
    }

    /**
     * Called when the wizard is cancelled.
     *
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard) {
//        System.out.println("wizard cancelled");
        dialog.dispose();
        WizardUtil.disconnect();
    }

    /**
     * Called when a new panel has been displayed in the wizard.
     *
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard) {
//        System.out.println("wizard new panel");
    }

}

