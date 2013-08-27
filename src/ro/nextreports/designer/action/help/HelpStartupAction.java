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
package ro.nextreports.designer.action.help;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.NextReports;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 20, 2009
 * Time: 12:06:26 PM
 */
public class HelpStartupAction extends AbstractAction {

    public HelpStartupAction() {
        putValue(Action.NAME, I18NSupport.getString("menu.startup"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("start.help"));
        //putValue(Action.MNEMONIC_KEY, new Integer('S'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("quickstart.accelerator", "F3")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("menu.startup"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("menu.startup"));
    }

    public void actionPerformed(ActionEvent e) {
        NextReports.showStartDialog(false);
    }
}
