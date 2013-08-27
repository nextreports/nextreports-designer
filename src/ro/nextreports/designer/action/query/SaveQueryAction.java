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
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 13, 2006
 * Time: 11:42:45 AM
 */
public class SaveQueryAction extends SaveAsQueryAction {

    private boolean cancel = false;

    public SaveQueryAction() {
        putValue(NAME, I18NSupport.getString("save.query"));
        Icon icon = ImageUtil.getImageIcon("query_save");
        putValue(SMALL_ICON, icon);
        putValue(MNEMONIC_KEY, ShortcutsUtil.getMnemonic("query.save.mnemonic",  new Integer('B')));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("query.save.accelerator", "control B")));
        putValue(SHORT_DESCRIPTION, I18NSupport.getString("save.query"));
        putValue(LONG_DESCRIPTION, I18NSupport.getString("save.query"));
    }

    public void actionPerformed(ActionEvent e) {

        // test to see if a query created with designer was modified in editor
        QueryBuilderPanel panel = Globals.getMainFrame().getQueryBuilderPanel();
        if (panel.hasDesigner() && panel.queryWasModified(true)) {
            Object[] options = {I18NSupport.getString("optionpanel.yes"), I18NSupport.getString("optionpanel.no")};
            String m1 = I18NSupport.getString("querybuilder.change.select");
            String m2 = I18NSupport.getString("querybuilder.change.lost");
            int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
                    "<HTML>" + m1 + "<BR>" + m2 + "</HTML>", I18NSupport.getString("querybuilder.confirm"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            if (option != JOptionPane.YES_OPTION) {
                cancel = true;
                return;
            }
        }

        //System.out.println(" >> query = " + Globals.getCurrentQueryName());
        super.setName(Globals.getCurrentQueryName());
        if (Globals.getCurrentQueryAbsolutePath() != null) {
            super.setPath(Globals.getCurrentQueryAbsolutePath());
        }
        super.actionPerformed(e);
    }

    public boolean isCancel() {
        return cancel;
    }
}
