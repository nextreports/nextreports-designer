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
package ro.nextreports.designer.wizimport;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 16-Mar-2009
 * Time: 14:01:09
 */
public class ImportWizard implements WizardListener {

    public static final String MAIN_FRAME = "MAIN_FRAME";


    private JDialog dialog;

    public ImportWizard() {
        dialog = new JDialog(Globals.getMainFrame(), I18NSupport.getString("import"), true);
        //dialog.setIconImage(ImageUtil.getImage("wizard.png"));
        Wizard wizard = new Wizard(new ImportStartWizardPanel());
        wizard.getContext().setAttribute(MAIN_FRAME, dialog);
        wizard.addWizardListener(this);
        dialog.setContentPane(wizard);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
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
        dialog.dispose();
    }

    /**
     * Called when the wizard is cancelled.
     *
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard) {
        dialog.dispose();
    }

    /**
     * Called when a new panel has been displayed in the wizard.
     *
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard) {
    }

}


