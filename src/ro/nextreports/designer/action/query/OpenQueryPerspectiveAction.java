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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;


/**
 * @author Decebal Suiu
 */
public class OpenQueryPerspectiveAction extends AbstractAction {

    public OpenQueryPerspectiveAction() {
        putValue(Action.NAME, I18NSupport.getString("query.perspective"));
        Icon icon = ImageUtil.getImageIcon("query_perspective");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("perspective.query.open.accelerator", "control 1")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("query.perpsective.desc") +
                " (" +  ShortcutsUtil.getShortcut("perspective.query.open.accelerator.display", "Ctrl 1")+  ")");
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("query.perpsective.desc"));        
    }

    public void actionPerformed(ActionEvent e) {
        WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.QUERY_WORKSPACE);
    }

}
