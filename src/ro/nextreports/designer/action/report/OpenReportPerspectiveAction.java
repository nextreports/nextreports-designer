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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;


/**
 * @author Decebal Suiu
 */
public class OpenReportPerspectiveAction extends AbstractAction {

    public OpenReportPerspectiveAction() {
        putValue(Action.NAME, I18NSupport.getString("report.perspective"));
        Icon icon = ImageUtil.getImageIcon("report_perspective");
        putValue(Action.SMALL_ICON, icon);       
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("report.perspective.desc"));
    }

    public void actionPerformed(ActionEvent ev) {
    	// see global action from NextReports class
    	if (!Globals.isReportLoaded()) {
    		// ignore
    		return;
    	}
                    	
        //NextReportsUtil.reloadReportIfNecessary();

        WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.REPORT_WORKSPACE);
    }

}
