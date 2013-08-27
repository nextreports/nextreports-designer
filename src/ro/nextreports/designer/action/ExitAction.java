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
package ro.nextreports.designer.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.MainFrame;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.ui.tail.LogPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;


/**
 * @author Decebal Suiu
 */
public class ExitAction extends AbstractAction {

    public ExitAction() {
        putValue(Action.NAME, I18NSupport.getString("exit"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("exit"));
        putValue(Action.MNEMONIC_KEY, new Integer('x'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X,
//                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("exit.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("exit.long.desc"));
    }

    public void actionPerformed(ActionEvent event) {
        JFrame mainFrame = Globals.getMainFrame();
        if (mainFrame != null) {
        	
        	if (NextReportsUtil.isInnerEdit()) {
        		return;
        	}

            if (!NextReportsUtil.saveYesNoCancel()) {
                return;
            }

            final ReporterPreferencesManager preferencesManager = ReporterPreferencesManager.getInstance();
            preferencesManager.storeBoundsForWindow(MainFrame.class, mainFrame.getBounds());
            mainFrame.setVisible(false);
            mainFrame.dispose();
            try {
                WorkspaceManager.getInstance().storeWorkspaces();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LogPanel.stop();
        }
        System.exit(0);
    }

}
