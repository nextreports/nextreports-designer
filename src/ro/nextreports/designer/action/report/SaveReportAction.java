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
import ro.nextreports.designer.action.query.SaveAsQueryAction;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.wizpublish.WebServiceResult;
import ro.nextreports.designer.wizpublish.WebServiceUtil;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 2:01:27 PM
 */
public class SaveReportAction extends SaveAsQueryAction {

    private boolean cancel = false;
    private boolean forced = false;
    
    public SaveReportAction() {
    	this(false);
    }

    public SaveReportAction(boolean forced) {
        putValue(Action.NAME, I18NSupport.getString("save.report"));
        Icon icon = ImageUtil.getImageIcon("report_save");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("report.save.mnemonic",  new Integer('T')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("report.save.accelerator", "control T")));        
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.report"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.report"));
        this.forced = forced;
    }

    public void actionPerformed(ActionEvent e) {
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (builderPanel.isCleaned()) {
            return;
        }

        DefaultGridModel gridModel = (DefaultGridModel) Globals.getReportGrid().getModel();
        if ((gridModel.getRowCount() == 0) && (gridModel.getColumnCount() == 0)) {
            Show.info(I18NSupport.getString("report.isEmpty"));
            return;
        }
        
        if (!forced && MessageUtil.showReconnect()) {
            return;
        }
        
        boolean saveToServer = false;
        if (Globals.getServerPath() != null) {
        	int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    I18NSupport.getString("save.entity.server",  I18NSupport.getString("report")), 
                    "", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
            	saveToServer = true;                           
            } 
        }

        String name = FormSaver.getInstance().save((String) this.getValue(Action.NAME), false);        
        if (name != null) {        	        	
            String path = Globals.getCurrentReportAbsolutePath();
            // forced is used when connection is really down, so we cannot update UI
            if (!forced) {
            	builderPanel.addReport(name, path);
            }
            Globals.setCurrentReportName(name);
            Globals.setCurrentQueryName(name);
            Globals.getReportUndoManager().discardAllEdits();
            
            if (saveToServer) {
            	WebServiceResult result = WebServiceUtil.publishReport(Globals.getWebService(), 
            			Globals.getServerPath(), 
            			Globals.getServerDSMetaData().getPath(),             			 
            			Globals.getServerReportMetaData().getDescription(), 
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
