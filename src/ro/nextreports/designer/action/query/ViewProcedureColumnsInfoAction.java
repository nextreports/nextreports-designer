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
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.DBProcedureColumnViewerPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Apr-2009
// Time: 16:18:53

//
public class ViewProcedureColumnsInfoAction extends AbstractAction {

    private DBObject procObject;

    public ViewProcedureColumnsInfoAction(DBObject tableObject) {
        putValue(Action.NAME, I18NSupport.getString("view.columns.info"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("properties"));
        putValue(Action.MNEMONIC_KEY, new Integer('I'));
        //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X,
        //        KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("view.columns.info"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("view.columns.info"));
        this.procObject = tableObject;
    }

    public void actionPerformed(ActionEvent e) {

        if (procObject != null) {
            DBProcedureColumnViewerPanel panel = new DBProcedureColumnViewerPanel(procObject);
            if (panel.isError()) {
                return;
            }
            String s = I18NSupport.getString("procedure");

            JDialog dialog = new JDialog(Globals.getMainFrame(),
                    I18NSupport.getString("view.columns.message", s, procObject.getName())
                    , true);
            dialog.add(panel);
            dialog.pack();
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);
        }

    }
}
