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
package ro.nextreports.designer.wizpublish;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.wizrep.EntityWizardPanel;
import ro.nextreports.designer.wizrep.WizardConstants;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 14:32:01

//
public class PublishWizard implements WizardListener {

    public static final String MAIN_FRAME = "MAIN_FRAME";
    public static final String CLIENT = "CLIENT";
    public static final String REPORT_PATH = "REPORT_PATH";
    public static final String DOWNLOAD =  "DOWNLOAD";

    private JDialog dialog;

    public PublishWizard(String entityPath, String entity) {
        this(entityPath, entity, false);
    }

    public PublishWizard(final String entityPath, String entity, boolean download) {
        String message = "";
        if (download) {
            message = I18NSupport.getString("download");
        } else {
            message = I18NSupport.getString("publish");
        }
        dialog = new JDialog(Globals.getMainFrame(), message, true);
        final PublishLoginWizardPanel loginPanel = new PublishLoginWizardPanel(entityPath);
        EntityWizardPanel entityPanel = new EntityWizardPanel() {

            public String getTitle() {
                return I18NSupport.getString("wizard.publish.entity.select");
            }

            public String getSubtitle() {
                return I18NSupport.getString("wizard.publish.entity.choose");
            }

            public WizardPanel getNextPanel() {
                return loginPanel;
            }
        };
        Wizard wizard;
        if (entity == null) {
           wizard  = new Wizard(entityPanel);
        } else {
           wizard  = new Wizard(loginPanel);
        }

        wizard.getContext().setAttribute(MAIN_FRAME, dialog);
        wizard.getContext().setAttribute(DOWNLOAD, Boolean.valueOf(download));
        wizard.getContext().setAttribute(WizardConstants.ENTITY, entity);
        wizard.addWizardListener(this);
        dialog.setContentPane(wizard);
        dialog.setSize(400, 340);
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
