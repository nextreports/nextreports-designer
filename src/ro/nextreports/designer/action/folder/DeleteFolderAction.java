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
package ro.nextreports.designer.action.folder;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 07-Apr-2009
// Time: 11:15:47
//

public class DeleteFolderAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DeleteFolderAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("folder.delete"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("folder.delete"));
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("folder.delete"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("folder.delete"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {

        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                I18NSupport.getString("folder.delete.message"),
                I18NSupport.getString("folder.delete"),
                JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        String folder = selectedNode.getDBObject().getName();
        String path = selectedNode.getDBObject().getAbsolutePath();

        FileUtil.deleteDir(new File(path));
        if ((Globals.getCurrentQueryAbsolutePath() != null) &&
                Globals.getCurrentQueryAbsolutePath().startsWith(path)) {
            Globals.setCurrentQueryAbsolutePath(null);
            Globals.setCurrentQueryName(null);
        } else if ((Globals.getCurrentReportAbsolutePath() != null) &&
                Globals.getCurrentReportAbsolutePath().startsWith(path)) {
            Globals.setCurrentReportAbsolutePath(null);
            Globals.setCurrentReportName(null);
        } else if ((Globals.getCurrentChartAbsolutePath() != null) &&
                Globals.getCurrentChartAbsolutePath().startsWith(path)) {
            Globals.setCurrentChartAbsolutePath(null);
            Globals.setCurrentChartName(null);
        }

        tree.deleteFolder(folder, path, selectedNode.getDBObject().getType());

    }
}
