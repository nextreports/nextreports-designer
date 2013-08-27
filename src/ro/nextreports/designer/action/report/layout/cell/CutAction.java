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
package ro.nextreports.designer.action.report.layout.cell;


import java.awt.event.ActionEvent;

import javax.swing.*;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ShortcutsUtil;

/**
 * @author Decebal Suiu
 */
public class CutAction extends AbstractAction {

    public CutAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("cut.cell.action.name"));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.cut.accelerator", "control X")));
    }

    public void actionPerformed(ActionEvent event) {
    	CopyAction copyAction = new CopyAction();
        copyAction.actionPerformed(null);
        if (copyAction.isOk()) {
            new ClearCellAction(false).actionPerformed(null);
        }
    }
    
}
