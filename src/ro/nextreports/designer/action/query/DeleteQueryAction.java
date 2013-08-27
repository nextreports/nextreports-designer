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

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 13, 2006
 * Time: 3:46:56 PM
 */
public class DeleteQueryAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DeleteQueryAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("delete.query"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("query_delete"));
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("delete.query.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("delete.query.desc"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();

        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                I18NSupport.getString("delete.query.ask", name), "", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                Globals.getReportPersistenceType());

        repPersist.deleteReport(selectedNode.getDBObject().getAbsolutePath());
        tree.removeNode(selectedNode);
    }
}
