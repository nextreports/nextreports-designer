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

import ro.nextreports.designer.persistence.FileReportPersistence;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 13, 2006
 * Time: 10:54:04 AM
 */
public class QueryBrowserPanel extends JPanel {

    private DBBrowserTree dbBrowserTree;
    private Dimension scrDim = new Dimension(300, 200);
    private boolean doubleClick = false;
    private QueryBrowserDialog parent;

    public QueryBrowserPanel() {
        setLayout(new BorderLayout());
        // ignore double click listener for tree (which opens the query)
        // and create our own listener (which just selects the path)
        dbBrowserTree = new DBBrowserTree(DBObject.QUERIES_GROUP, false);
        dbBrowserTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
                selection();
                if (e.isPopupTrigger() || (e.getClickCount() == 2)) {
                    final TreePath selPath = dbBrowserTree.getPathForLocation(e.getX(), e.getY());
                    if (selPath == null) {
                        return;
                    }
                    dbBrowserTree.setSelectionPath(selPath);

                    DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
                    if (selectedNode.getDBObject().isFolder()) {
                        return;
                    }

                    doubleClick = true;
                    if (parent != null) {
                        parent.dispose();
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        scroll.setPreferredSize(scrDim);        
        add(scroll, BorderLayout.CENTER);
    }

    protected void selection() {
    }

    public String getSelectedName() {
        TreePath selPath = dbBrowserTree.getSelectionPath();
        if (selPath == null) {
            return null;
        }
        DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();

        if ((selectedNode != null) && (selectedNode.getDBObject().getType() == DBObject.QUERIES)) {
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

        if ((selectedNode != null) && (selectedNode.getDBObject().getType() == DBObject.QUERIES)) {
            return selectedNode.getDBObject().getAbsolutePath();
        }
        return null;
    }

    public boolean querySelected() {
        String path = getSelectedFilePath();
        return (path != null) && path.endsWith(FileReportPersistence.REPORT_EXTENSION_SEPARATOR+FileReportPersistence.REPORT_EXTENSION);
    }

    public void clearSelection() {
        dbBrowserTree.clearSelection();
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }

    public void setParent(QueryBrowserDialog parent) {
        this.parent = parent;
    }
        
}
