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
package ro.nextreports.designer.querybuilder;

import ro.nextreports.engine.util.StringUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 8, 2006
 * Time: 5:12:36 PM
 */
public class SaveEntityDialog extends BaseDialog {

    private SaveEntityPanel panel;
    private boolean okPressed;
    private boolean save_as;
    private String entityName;
    private boolean overwrite;
    
    public SaveEntityDialog(String title, SaveEntityPanel panel, String entityName,  boolean save_as) {        
        super(panel, title, true);
        this.panel = panel;
        okPressed = false;
        this.save_as = save_as;
        this.entityName = entityName;
    }

    protected boolean ok() {
        okPressed = true;
        String name = panel.getName();
        String path = panel.getFolderPath();
        if (name == null) {
            Show.info(I18NSupport.getString("save.entity.ask.name", entityName));
            okPressed = false;
            return false;
        }

        if (!StringUtil.isFileName(name)) {
            Show.error(I18NSupport.getString("name.invalid"));
            okPressed = false;
            return false;
        }

        if (save_as) {

            if (panel.findEntity(name, path)) {
                int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                        I18NSupport.getString("save.entity.exists", entityName, name), "", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    overwrite = true;
                    return true;
                } else {
                    okPressed = false;
                    return false;
                }
            }

        }

        return true;
    }

    public void requestFocus() {
        panel.requestFocus();
    }

    public boolean okPressed() {
       return okPressed;
    }

    public boolean isOverwrite() {
        return overwrite;
    }
}
