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
package ro.nextreports.designer.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.wizpublish.JcrBrowserTree;


public class JcrBrowserTreeUtil {
	
	public static JPanel createSelectionPanel(final JcrBrowserTree jcrBrowserTree, final byte type) {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BorderLayout());                
        jcrBrowserTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
                //selection();
                if (e.isPopupTrigger() || (e.getClickCount() == 2)) {
                    final TreePath selPath = jcrBrowserTree.getPathForLocation(e.getX(), e.getY());
                    if (selPath == null) {
                        return;
                    }
                    jcrBrowserTree.setSelectionPath(selPath);

                    DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
                    if (selectedNode.getDBObject().isFolder()) {
                        return;
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(jcrBrowserTree);
        scroll.setPreferredSize(new Dimension(200, 200));
        selectionPanel.add(scroll, BorderLayout.CENTER);

        return selectionPanel;
    }

}
