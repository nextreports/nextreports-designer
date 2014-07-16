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
package ro.nextreports.designer.querybuilder;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 5:36:14 PM
 */
public class BrowserPanel extends JPanel {

    public static final byte REPORT_BROWSER = 1;
    public static final byte CHART_BROWSER = 2;

    private byte type;
    private DBBrowserTree dbBrowserTree;
    private Dimension scrDim = new Dimension(300, 200);

    public BrowserPanel(byte type) {
        this(type, true);
    }
    
    public BrowserPanel(byte type, boolean registerDoubleClick) {
        this.type=type;
        setLayout(new BorderLayout());
        if (type == REPORT_BROWSER) {
            dbBrowserTree = new DBBrowserTree(DBObject.REPORTS_GROUP, registerDoubleClick);
        } else {
           dbBrowserTree = new DBBrowserTree(DBObject.CHARTS_GROUP, registerDoubleClick);
        }
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        scroll.setPreferredSize(scrDim);
        add(scroll, BorderLayout.CENTER);
    }

    public String getSelectedName() {
        TreePath selPath = dbBrowserTree.getSelectionPath();
        if (selPath == null) {
            return null;
        }
        DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();

        if ((selectedNode != null) &&
               ( (selectedNode.getDBObject().getType() == DBObject.REPORTS) ||
                 (selectedNode.getDBObject().getType() == DBObject.CHARTS) ) ) {
            return selectedNode.getDBObject().getName();
        }
        return null;
    }

    public String getSelectedFilePath() {
        TreePath selPath = dbBrowserTree.getSelectionPath();
        if (selPath == null) {
            return null;
        }
        DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();

        if ((selectedNode != null) &&
               ( (selectedNode.getDBObject().getType() == DBObject.REPORTS) ||
                 (selectedNode.getDBObject().getType() == DBObject.CHARTS) ) ) {
            return selectedNode.getDBObject().getAbsolutePath();
        }
        return null;
    }

    public byte getType() {
        return type;
    }
}
