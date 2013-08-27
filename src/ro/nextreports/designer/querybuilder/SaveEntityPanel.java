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

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 8, 2006
 * Time: 5:10:42 PM
 */
public class SaveEntityPanel extends JPanel {

    private JTextField nameTextField;
    private DBBrowserTree dbBrowserTree;
    private JButton newFolderButton;
    private Dimension buttonDim = new Dimension(20,20);
    private Dimension scrDim = new Dimension(300, 200);
    private String folderPath;
    private byte type;    

    public SaveEntityPanel(String entityName, byte dbObjectType) {
        type = dbObjectType;
        JLabel nameLabel = new JLabel(entityName);
        nameTextField = new JTextField();
        
        newFolderButton = new JButton();
        newFolderButton.setIcon(ImageUtil.getImageIcon("folder.add"));
        newFolderButton.setToolTipText(I18NSupport.getString("folder.add"));
        newFolderButton.setMinimumSize(buttonDim);
        newFolderButton.setMaximumSize(buttonDim);
        newFolderButton.setPreferredSize(buttonDim);
        newFolderButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				TreePath path = dbBrowserTree.getSelectionPath();
				boolean root = true;
				DBBrowserNode selectedNode = null;
				if (path != null) {
					selectedNode = (DBBrowserNode) path.getLastPathComponent();
					if (selectedNode.getDBObject().isFolder()) {
						root = false;
						folderPath = selectedNode.getDBObject().getAbsolutePath();						
					} else {
						selectedNode = null;
					}
				}
				if (root) {										
					folderPath = dbBrowserTree.getRootAbsolutePath(type);					
				}				
				String newName = JOptionPane.showInputDialog(I18NSupport.getString("folder.add.name"));				
				if (newName != null) {							
				    new File(folderPath + File.separator + newName).mkdirs();							
				}
				dbBrowserTree.refreshParentNode(selectedNode);
			}
        	
        });

        // ignore double click listener for tree (which opens the query)
        // and create our own listener (which just selects the path)
        dbBrowserTree = new DBBrowserTree(dbObjectType, false);
        dbBrowserTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
                final TreePath selPath = dbBrowserTree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null) {
                    return;
                }

                dbBrowserTree.setSelectionPath(selPath);
                final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();                
                if (selectedNode.getDBObject().isFolder()) {
                    folderPath = selectedNode.getDBObject().getAbsolutePath();
                    nameTextField.setText("");
                } else {
                    if ((selectedNode.getDBObject().getType() == DBObject.QUERIES) ||
                        (selectedNode.getDBObject().getType() == DBObject.REPORTS) ||
                        (selectedNode.getDBObject().getType() == DBObject.CHARTS)   ) {
                        folderPath = selectedNode.getDBObject().getParentPath();
                        nameTextField.setText(selectedNode.getDBObject().getName());
                    } else {
                        nameTextField.setText("");
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        scroll.setPreferredSize(scrDim);

        setLayout(new GridBagLayout());

        add(newFolderButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(scroll, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(nameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 0, 0, 5), 0, 0));
        add(nameTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
    }

    public String getName() {
        String name = nameTextField.getText();
        if (name.trim().equals("")) {
            name = null;
        }
        return name;
    }

    public String getFolderPath() {
        if (folderPath == null) {
            if (type == DBObject.QUERIES_GROUP) {
                return new File(FileReportPersistence.getQueriesRelativePath()).getAbsolutePath();
            } else if (type == DBObject.REPORTS_GROUP){
                return new File(FileReportPersistence.getReportsRelativePath()).getAbsolutePath();
            } else {
                return new File(FileReportPersistence.getChartsRelativePath()).getAbsolutePath();
            }
        }
        return folderPath;
    }

    public void requestFocus() {
        nameTextField.requestFocus();
    }

    class NodeCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                DBBrowserNode node = (DBBrowserNode) value;
                value = node.getDBObject().getName();
                list.setToolTipText(value.toString());
            }

            return comp;
        }

    }

    public boolean findEntity(String name, String path) {
        String[] names = new File(path).list();
        for (String n : names) {
            if (type == DBObject.QUERIES_GROUP) {
                if (n.equalsIgnoreCase(name + FileReportPersistence.REPORT_FULL_EXTENSION)) {
                    return true;
                }
            } else if (type == DBObject.REPORTS_GROUP) {
                if (n.equalsIgnoreCase(name + FormSaver.REPORT_FULL_EXTENSION)) {
                    return true;
                }
            } else {
                if (n.equalsIgnoreCase(name + ChartUtil.CHART_FULL_EXTENSION)) {
                    return true;
                }
            }
        }
        return false;
    }
}
