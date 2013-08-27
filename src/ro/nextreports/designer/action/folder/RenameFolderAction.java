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

import ro.nextreports.engine.util.StringUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 07-Apr-2009
// Time: 11:28:46

//
public class RenameFolderAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public RenameFolderAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("folder.rename"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("folder.rename"));
        putValue(Action.MNEMONIC_KEY, new Integer('R'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
                KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("folder.rename"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("folder.rename"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String oldName = selectedNode.getDBObject().getName();
        String newName = JOptionPane.showInputDialog(I18NSupport.getString("folder.rename.name"), oldName);
        if ((newName != null) && !newName.trim().equals("")) {
            if (!StringUtil.isFileName(newName)) {
                Show.error(I18NSupport.getString("folder.invalid"));
                return;
            }
            String absolutePath = selectedNode.getDBObject().getAbsolutePath();
            String newPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator)) +
                    File.separator + newName;

            File newFile =  new File(newPath);
            if (newFile.exists()) {
                Show.info(I18NSupport.getString("folder.add.exists", newName));
            } else {
                new File(absolutePath).renameTo(newFile);
                // if a query/report from this folder is loaded, adjust the current path in Globals
                if ((Globals.getCurrentQueryAbsolutePath() != null) &&
                    Globals.getCurrentQueryAbsolutePath().startsWith(absolutePath)) {
                    Globals.setCurrentQueryAbsolutePath(newPath + File.separator +
                    Globals.getCurrentQueryAbsolutePath().substring(absolutePath.length()+1));
                } else if ((Globals.getCurrentReportAbsolutePath() != null) &&
                    Globals.getCurrentReportAbsolutePath().startsWith(absolutePath)) {
                    Globals.setCurrentReportAbsolutePath(newPath + File.separator +
                    Globals.getCurrentReportAbsolutePath().substring(absolutePath.length()+1));
                } else if ((Globals.getCurrentChartAbsolutePath() != null) &&
                    Globals.getCurrentChartAbsolutePath().startsWith(absolutePath)) {
                    Globals.setCurrentChartAbsolutePath(newPath + File.separator +
                    Globals.getCurrentChartAbsolutePath().substring(absolutePath.length()+1));
                }
                tree.renameFolder(oldName, newName, absolutePath, newPath, selectedNode.getDBObject().getType());
            }
        }
    }
}
