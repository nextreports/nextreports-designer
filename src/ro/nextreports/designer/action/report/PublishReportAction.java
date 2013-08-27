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
package ro.nextreports.designer.action.report;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.wizpublish.PublishWizard;
import ro.nextreports.designer.wizrep.WizardConstants;

import java.awt.event.ActionEvent;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 14:11:06

//
public class PublishReportAction extends AbstractAction {

    private String reportPath;
    private boolean publishCurrent = false;

    public PublishReportAction() {
        this(Globals.getCurrentReportAbsolutePath());  // may be null if report was not saved!
        publishCurrent = true;
    }

    public PublishReportAction(String reportPath) {
        putValue(Action.NAME, I18NSupport.getString("publish.name"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("publish"));
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("publish.mnemonic",  new Integer('U')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("publish.accelerator", "control U")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("publish.name.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("publish.name.desc"));
        this.reportPath = reportPath;
        publishCurrent = false;
    }

    public void actionPerformed(ActionEvent ev) {
        if (publishCurrent) {
            if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("publish.name"))) {
                return;
            }
            reportPath = Globals.getCurrentReportAbsolutePath();
        }
        new PublishWizard(reportPath, WizardConstants.ENTITY_REPORT);
    }
}
