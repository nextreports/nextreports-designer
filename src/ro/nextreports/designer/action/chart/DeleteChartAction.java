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
package ro.nextreports.designer.action.chart;


import javax.swing.*;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 17-Dec-2009
 * Time: 16:28:20
 */
public class DeleteChartAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public DeleteChartAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("delete.chart"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("chart_delete"));
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("delete.chart.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("delete.chart.desc"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();

        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                I18NSupport.getString("delete.chart.ask", name), "", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        FormSaver.getInstance().deleteReport(selectedNode.getDBObject().getAbsolutePath());        
        tree.removeNode(selectedNode);
    }

}
