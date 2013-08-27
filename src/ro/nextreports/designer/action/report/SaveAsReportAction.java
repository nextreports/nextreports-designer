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

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 2:02:53 PM
 */
public class SaveAsReportAction extends AbstractAction {

    private String name;
    private String path;

    public SaveAsReportAction() {
        putValue(Action.NAME, I18NSupport.getString("save.as.report"));
        Icon icon = ImageUtil.getImageIcon("report_save_as");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,  KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.as.report.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.as.report.desc"));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void actionPerformed(ActionEvent e) {

        if (!LicenseUtil.allowToAddAnotherReport()) {
            return;
        }

        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (builderPanel.isCleaned()) {
            return;
        }

        DefaultGridModel gridModel = (DefaultGridModel)Globals.getReportGrid().getModel();
        if ((gridModel.getRowCount() == 0) && (gridModel.getColumnCount() == 0)) {
            Show.info(I18NSupport.getString("report.isEmpty"));
            return;
        }

        //NextReportsUtil.reloadReportIfNecessary();

        if (MessageUtil.showReconnect()) {
            return;
        }

        name = FormSaver.getInstance().save((String)this.getValue(Action.NAME), true);
        if (name != null) {
            builderPanel.addReport(name, Globals.getCurrentReportAbsolutePath());
            Globals.setCurrentReportName(name);
            //Globals.setCurrentReportAbsolutePath(Globals.getCurrentFile().getAbsolutePath());
            Globals.setCurrentQueryName(name);
        }
    }
}
