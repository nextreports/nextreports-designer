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
package ro.nextreports.designer.action.chart;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.MessageUtil;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 18-Dec-2009
 * Time: 11:54:45
 */
public class SaveAsChartAction extends AbstractAction {

    private String name;
    private String path;

    public SaveAsChartAction() {
        putValue(Action.NAME, I18NSupport.getString("save.as.chart"));
        Icon icon = ImageUtil.getImageIcon("chart_save_as");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('V'));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.as.chart.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.as.chart.desc"));
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

        if (ChartUtil.chartUndefined()) {
            return;
        }

        if (MessageUtil.showReconnect()) {
            return;
        }

        name =  ChartUtil.saveChart((String) this.getValue(Action.NAME), true);
        if (name != null) {
            builderPanel.addChart(name, Globals.getCurrentChartAbsolutePath());
            Globals.setCurrentChartName(name);            
            Globals.setCurrentQueryName(name);
        }
    }
}
