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

import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
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
// Time: 10:27:11
//
public class AddFolderAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;
    private byte type;

    public AddFolderAction(DBBrowserTree tree, DBBrowserNode selectedNode, byte type) {
        putValue(Action.NAME, I18NSupport.getString("folder.add"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("folder.add"));
        putValue(Action.MNEMONIC_KEY, new Integer('A'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("folder.add"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("folder.add"));
        this.tree = tree;
        this.selectedNode = selectedNode;
        this.type = type;
    }

    public void actionPerformed(ActionEvent ev) {
        String folder = JOptionPane.showInputDialog(I18NSupport.getString("folder.add.name"), "");
        if ((folder != null) && !folder.trim().equals("")) {
            if (!StringUtil.isFileName(folder)) {
                Show.error(I18NSupport.getString("folder.invalid"));
                return;
            }
            String path = selectedNode.getDBObject().getAbsolutePath();
            String absolutePath;
            boolean onRoot;
            if (path == null) {
                onRoot = true;
                String root;
                if (type == DBObject.FOLDER_QUERY) {
                    root = FileReportPersistence.getQueriesRelativePath();
                } else if (type == DBObject.FOLDER_REPORT) {
                    root = FileReportPersistence.getReportsRelativePath();
                } else {
                    root = FileReportPersistence.getChartsRelativePath();
                }
                root = new File(root).getAbsolutePath();
                absolutePath = root + File.separator + folder;
            } else {
                onRoot = false;
                absolutePath = path + File.separator + folder;
            }
            File newFile = new File(absolutePath);
            if (newFile.exists()) {
                Show.info(I18NSupport.getString("folder.add.exists", folder));
            } else {
                boolean done = newFile.mkdirs();
                if (done) {
                    tree.addFolder(folder, absolutePath, type, onRoot);
                } else {
                    Show.error(I18NSupport.getString("write.error", path));
                }
            }
        }
    }
}
