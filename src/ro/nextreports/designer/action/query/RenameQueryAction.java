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

import ro.nextreports.engine.util.StringUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Aug 29, 2008
 * Time: 4:23:35 PM
 */
public class RenameQueryAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public RenameQueryAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("rename.query"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("query_rename"));
        putValue(Action.MNEMONIC_KEY, new Integer('Y'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                KeyEvent.SHIFT_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("rename.query"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("rename.query"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {

        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        String name = selectedNode.getDBObject().getName();
        String newName = JOptionPane.showInputDialog(Globals.getMainFrame(),
                I18NSupport.getString("rename.query.ask", name), name);

        // cancel
        if (newName == null) {
            return;
        }

        if (newName.trim().equals("")) {
            Show.error(I18NSupport.getString("query.empty.name"));
            return;
        }

        if (!StringUtil.isFileName(newName)) {
            Show.error(I18NSupport.getString("name.invalid"));
            return;
        }

        ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                Globals.getReportPersistenceType());

        boolean save = repPersist.renameReport(name, newName, selectedNode.getDBObject().getParentPath());
        if (!save) {
            Show.error(I18NSupport.getString("could.not.save.query"));
        } else {
            Globals.setCurrentQueryName(newName);
            tree.renameNode(selectedNode, newName, selectedNode.getDBObject().getParentPath() + File.separator +
                newName + FileReportPersistence.REPORT_EXTENSION_SEPARATOR + FileReportPersistence.REPORT_EXTENSION);
        }

    }

}
