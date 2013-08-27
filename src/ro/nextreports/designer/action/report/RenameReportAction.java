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
package ro.nextreports.designer.action.report;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 4:01:39 PM
 */
public class RenameReportAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public RenameReportAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("rename.report"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("report_rename"));
        putValue(Action.MNEMONIC_KEY, new Integer('M'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("rename.report"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("rename.report"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();

        String newName = JOptionPane.showInputDialog(Globals.getMainFrame(),
                I18NSupport.getString("rename.report.ask", name), name);

        // cancel
        if (newName == null) {
            return;
        }

        if (newName.trim().equals("")) {
            Show.error(I18NSupport.getString("report.empty.name"));
            return;
        }

        if (!StringUtil.isFileName(newName)) {
            Show.error(I18NSupport.getString("name.invalid"));
            return;
        }

        boolean save = FormSaver.getInstance().renameReport(name, newName, selectedNode.getDBObject().getParentPath());
        if (!save) {
            Show.error(I18NSupport.getString("could.not.save.report"));
        } else {
            tree.renameNode(selectedNode, newName, selectedNode.getDBObject().getParentPath() + File.separator +
                newName + FormSaver.REPORT_EXTENSION_SEPARATOR + FormSaver.REPORT_EXTENSION);
        }
    }

}
