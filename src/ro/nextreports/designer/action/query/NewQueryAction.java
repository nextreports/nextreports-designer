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
package ro.nextreports.designer.action.query;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.querybuilder.SQLViewPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 1:56:32 PM
 */
public class NewQueryAction extends AbstractAction {

    private boolean done = false;

    public NewQueryAction() {
        this(true);
    }

    public NewQueryAction(boolean fullName) {
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("new.query"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("query.name"));
        }
        Icon icon = ImageUtil.getImageIcon("query_new");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("query.new.mnemonic",  new Integer('Q')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("query.new.accelerator", "control Q")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("new.query"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("new.query"));
    }

    public void actionPerformed(ActionEvent e) {
    	
    	if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}
    	
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();

        if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("new.query"))) {
            return;
        }

        Globals.setReportLoaded(false);
        Globals.setChartLoaded(false);
        builderPanel.newQuery();
        Globals.getMainMenuBar().newQueryActionUpdate();
        Globals.getMainToolBar().newQueryActionUpdate();
        Globals.setOriginalSql(SQLViewPanel.DEFAULT_QUERY);
        Globals.setCurrentReportAbsolutePath(null);
        Globals.setCurrentChartAbsolutePath(null);
        Globals.setInitialQuery("");
        
        done = true;
    }

    public boolean executed() {
        return done;
    }

}
