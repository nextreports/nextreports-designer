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

import java.util.List;

import ro.nextreports.designer.ui.wizard.MessageWizardPanel;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 9, 2008
 * Time: 2:21:02 PM
 */
public class StartWizardPanel extends MessageWizardPanel {

    private String welcome = "<html><b>" +
            I18NSupport.getString("wizard.panel.start.easy") +
            "</b><br>" +
            "<ul><li><b>1.</b>&nbsp;" +
            I18NSupport.getString("wizard.panel.start.entity.title") +
            "</li><li><b>2.</b>&nbsp;" +
            I18NSupport.getString("wizard.panel.start.datasource.title") +
            "</li><li><b>3.</b>&nbsp;" +
            I18NSupport.getString("wizard.panel.start.query.title") +
            "</li><li><b>4.</b>&nbsp;" +
            I18NSupport.getString("wizard.panel.start.columns.title") +
            "</li><li><b>5.</b>&nbsp;" +
            I18NSupport.getString("wizard.panel.start.template.title") +
            "</li></ul>" +
            I18NSupport.getString("wizard.panel.start.info") +
            "<br><br>" +
            I18NSupport.getString("wizard.panel.start.next") +            
            "</html>";


    public StartWizardPanel() {
        super();
        setHtmlMessage(welcome, "text/html");
        banner.setTitle(I18NSupport.getString("wizard.panel.start.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.start.subtitle"));
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
        return new EntityWizardPanel();
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
     * @param messages
     *            a List of messages to be displayed.
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

}
