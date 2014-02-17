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


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.chart.SaveChartAction;
import ro.nextreports.designer.action.query.SaveQueryAction;
import ro.nextreports.designer.action.report.SaveReportAction;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 07-Jan-2010
 * Time: 17:24:20
 */
public class SaveAction extends AbstractAction {

    private boolean cancel = false;
    // save the entity anyway (no connection testing is made)
    private boolean forced = false;
    
    public SaveAction () {
    	this(false);
    }

    public SaveAction (boolean forced) {
        putValue(Action.NAME, I18NSupport.getString("save"));
        Icon icon = ImageUtil.getImageIcon("save");
        putValue(Action.SMALL_ICON, icon);
        putValue(MNEMONIC_KEY, ShortcutsUtil.getMnemonic("save.mnemonic",  new Integer('S')));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("save.accelerator", "control S")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save"));
        this.forced = forced;
    }

    public void actionPerformed(ActionEvent e) {
        if (Globals.isChartLoaded()){
            new SaveChartAction(forced).actionPerformed(e);
        } else if (Globals.isReportLoaded()) {
            new SaveReportAction(forced).actionPerformed(e);
        } else {
            new SaveQueryAction(forced).actionPerformed(e);
        }

    }

}
