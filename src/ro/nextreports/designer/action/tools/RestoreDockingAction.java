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
package ro.nextreports.designer.action.tools;


import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 18, 2009
 * Time: 5:53:18 PM
 */
public class RestoreDockingAction extends AbstractAction {

	private static final Log LOG = LogFactory.getLog(RestoreDockingAction.class);
	
    public RestoreDockingAction() {
        putValue(Action.NAME, I18NSupport.getString("restore.docking.action.name"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("docking_restore"));
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("layout.reset.mnemonic",  new Integer('L')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.reset.accelerator", "control L")));        
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("restore.docking.action.name"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("restore.docking.action.name"));
    }

    public void actionPerformed(ActionEvent event) {        
        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), I18NSupport.getString("docking.restart"), "", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        // remove current workspaces
        new File(WorkspaceManager.QUERY_WORKSPACE_FILE).delete();
        new File(WorkspaceManager.REPORT_WORKSPACE_FILE).delete();

        try {
			WorkspaceManager.getInstance().restoreWorkspaces();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
    }
    
}
