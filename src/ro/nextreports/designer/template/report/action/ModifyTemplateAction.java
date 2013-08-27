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
package ro.nextreports.designer.template.report.action;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.template.report.CreateTemplatePanel;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 9, 2008
 * Time: 11:14:10 AM
 */
public class ModifyTemplateAction extends AbstractAction {

    public ModifyTemplateAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("modify.template"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("template_modify"));
        putValue(Action.MNEMONIC_KEY, new Integer('M'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M,
//                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("modify.template"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("modify.template"));
    }

    public void actionPerformed(ActionEvent ev) {

        final CreateTemplatePanel panel = new CreateTemplatePanel(false);
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("modify.template"), true) {
            protected boolean ok() {
                if (panel.getFile() == null) {
                    Show.info(I18NSupport.getString("modify.template.select.message"));
                    return false;
                }
                
                File file = panel.getFile();
                String path = file.getAbsolutePath();
                try {
                    TemplateManager.saveTemplate(panel.getTemplate(), path);
                } catch (Exception e) {
                    Show.error(e);
                }
                // stay in template dialog
                return false;
            }
        };
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);               
    }

}
