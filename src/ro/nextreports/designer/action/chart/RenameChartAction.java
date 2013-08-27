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

import ro.nextreports.engine.util.StringUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * User: mihai.panaitescu
 * Date: 17-Dec-2009
 * Time: 16:37:36
 */
public class RenameChartAction extends AbstractAction {

    private DBBrowserTree tree;
    private DBBrowserNode selectedNode;

    public RenameChartAction(DBBrowserTree tree, DBBrowserNode selectedNode) {
        putValue(Action.NAME, I18NSupport.getString("rename.chart"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("chart_rename"));
        putValue(Action.MNEMONIC_KEY, new Integer('R'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("rename.chart"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("rename.chart"));
        this.tree = tree;
        this.selectedNode = selectedNode;
    }

    public void actionPerformed(ActionEvent ev) {
        String name = selectedNode.getDBObject().getName();

        String newName = JOptionPane.showInputDialog(Globals.getMainFrame(),
                I18NSupport.getString("rename.chart.ask", name), name);

        // cancel
        if (newName == null) {
            return;
        }

        if (newName.trim().equals("")) {
            Show.error(I18NSupport.getString("chart.empty.name"));
            return;
        }

        if (!StringUtil.isFileName(newName)) {
            Show.error(I18NSupport.getString("name.invalid"));
            return;
        }

        boolean save = ChartUtil.renameChart(name, newName, selectedNode.getDBObject().getParentPath());
        if (!save) {
            Show.error(I18NSupport.getString("could.not.save.chart"));
        } else {
            tree.renameNode(selectedNode, newName, selectedNode.getDBObject().getParentPath() + File.separator +
                newName + ChartUtil.CHART_FULL_EXTENSION);
        }
    }

}
