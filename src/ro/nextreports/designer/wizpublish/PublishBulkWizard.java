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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.wizrep.WizardConstants;


public class PublishBulkWizard implements WizardListener {
   
    public static final String LIST = "LIST";
    
    private JDialog dialog;  

    public PublishBulkWizard(byte dbObjectType) {
        String message = I18NSupport.getString("publish");        
        dialog = new JDialog(Globals.getMainFrame(), message, true);
        
        //final PublishLoginWizardPanel loginPanel = new PublishLoginWizardPanel(entityPath);
        
        SelectEntityWizardPanel selectPanel = new SelectEntityWizardPanel(dbObjectType);
        Wizard wizard = new Wizard(selectPanel);
        

        wizard.getContext().setAttribute(PublishWizard.MAIN_FRAME, dialog);
        wizard.getContext().setAttribute(PublishWizard.DOWNLOAD, false);
        String entity = WizardConstants.ENTITY_REPORT;
        if (DBObject.CHARTS_GROUP == dbObjectType) {
        	entity = WizardConstants.ENTITY_CHART;
        }
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
