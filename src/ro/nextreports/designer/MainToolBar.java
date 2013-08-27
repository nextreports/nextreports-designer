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
package ro.nextreports.designer;

import javax.swing.JLabel;
import javax.swing.JToolBar;

import ro.nextreports.designer.action.BackToParentAction;
import ro.nextreports.designer.action.OpenLayoutPerspectiveAction;
import ro.nextreports.designer.action.PublishAction;
import ro.nextreports.designer.action.SaveAction;
import ro.nextreports.designer.action.query.NewQueryAction;
import ro.nextreports.designer.action.query.OpenQueryPerspectiveAction;
import ro.nextreports.designer.action.report.WizardAction;
import ro.nextreports.designer.util.SwingUtil;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 * @author Mihai Dinca-Panaitescu
 */
public class MainToolBar extends JToolBar {

    private PublishAction publishAction = new PublishAction();
    private OpenLayoutPerspectiveAction openLayoutPersAction = new OpenLayoutPerspectiveAction();
    private WizardAction wizardAction = new WizardAction(Globals.getMainFrame().getQueryBuilderPanel().getTree());
    private SaveAction saveAction = new SaveAction();
    private BackToParentAction backAction = new BackToParentAction();
    private JLabel parent = new JLabel();

    public MainToolBar() {
        putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        
        add(new NewQueryAction());
        add(saveAction);

        SwingUtil.addCustomSeparator(this);

        add(wizardAction);
        add(publishAction);
        
        SwingUtil.addCustomSeparator(this);

        add(new OpenQueryPerspectiveAction());        
        add(openLayoutPersAction = new OpenLayoutPerspectiveAction());
                
        Globals.setMainToolBar(this);
        newQueryActionUpdate();
        actionUpdate(Globals.getConnection() !=  null);
        enableLayoutPerspective(false);        
                
        SwingUtil.addCustomSeparator(this);        
        backAction = new BackToParentAction();
        backAction.setEnabled(false);
        add(backAction);        
        add(parent);
    }

    public void newReportActionUpdate() {
        publishAction.setEnabled(true);
    }

    public void newChartActionUpdate() {
        publishAction.setEnabled(true);
    }

    public void newQueryActionUpdate() {
        publishAction.setEnabled(false);
    }

    public void actionUpdate(boolean connected) {
        saveAction.setEnabled(connected);
    }

    public void enableLayoutPerspective(boolean enable) {
    	openLayoutPersAction.setEnabled(enable);
    }
    
    public void enableBackAction(boolean enable, String parentText) {
    	Globals.getMainMenuBar().enableBackAction(enable);
    	backAction.setEnabled(enable);
    	parent.setText(parentText);    	
    	repaint();
    }

}
