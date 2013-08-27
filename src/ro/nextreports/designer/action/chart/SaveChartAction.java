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
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.wizpublish.WebServiceResult;
import ro.nextreports.designer.wizpublish.WebServiceUtil;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 16-Dec-2009
 * Time: 16:49:11
 */
public class SaveChartAction extends AbstractAction {

    private boolean cancel = false;

    public SaveChartAction() {
        putValue(Action.NAME, I18NSupport.getString("save.chart"));
        Icon icon = ImageUtil.getImageIcon("chart_save");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('S'));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.chart"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.chart"));
    }

    public void actionPerformed(ActionEvent e) {

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
        
        boolean saveToServer = false;        
        if (Globals.getServerPath() != null) {
        	int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    I18NSupport.getString("save.entity.server",  I18NSupport.getString("chart")), 
                    "", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
            	saveToServer = true;                           
            } 
        }

        String name = ChartUtil.saveChart((String) this.getValue(Action.NAME), false);
        if (name != null) {
            String path = Globals.getCurrentChartAbsolutePath();
            builderPanel.addChart(name, path);
            Globals.setCurrentChartName(name);
            Globals.setCurrentQueryName(name);
            Globals.getReportUndoManager().discardAllEdits();
            
            if (saveToServer) {
            	WebServiceResult result = WebServiceUtil.publishChart(Globals.getWebService(), 
            			Globals.getServerPath(), 
            			Globals.getServerDSMetaData().getPath(),             			
            			Globals.getServerChartMetaData().getDescription(), 
            			path);
            	if (result.isError()) {
					Show.error(Globals.getMainFrame(), result.getMessage());
				} else {
					Show.info(Globals.getMainFrame(), result.getMessage());
				}  
            }
            
        } else {
            cancel = true;
        }
    }

    public boolean isCancel() {
        return cancel;
    }
}
