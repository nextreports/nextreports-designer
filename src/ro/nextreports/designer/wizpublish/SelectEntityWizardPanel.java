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
package ro.nextreports.designer.wizpublish;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.converter.ConverterUtil;

public class SelectEntityWizardPanel extends WizardPanel {
	
	private static Log LOG = LogFactory.getLog(SelectEntityWizardPanel.class);
	
	private byte type;
	private Dimension btnDim = new Dimension(20, 20);
    private Dimension scrDim = new Dimension(400, 150);
    private Dimension scrTreeDim = new Dimension(250, 200);
	private JXList list = new JXList();
	private DefaultListModel listModel = new DefaultListModel();
	private JScrollPane scrList = new JScrollPane();
	private JButton btnAdd = new JButton();
	private JButton btnRem = new JButton();
	
	public SelectEntityWizardPanel(byte dbObjectType) {
		type = dbObjectType;	
        jbInit();
    }

    private void jbInit() {
		this.setLayout(new GridBagLayout());
		banner.setTitle(I18NSupport.getString("wizard.publish.entities.select"));
		
		btnAdd.setIcon(ImageUtil.getImageIcon("add"));
		btnAdd.setPreferredSize(btnDim);
		btnAdd.setMinimumSize(btnDim);
		btnAdd.setMaximumSize(btnDim);
		btnAdd.setToolTipText(I18NSupport.getString("listselectionpanel.add"));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add();
			}
		});
		
		btnRem.setIcon(ImageUtil.getImageIcon("delete"));
		btnRem.setPreferredSize(btnDim);
		btnRem.setMinimumSize(btnDim);
		btnRem.setMaximumSize(btnDim);
		btnRem.setToolTipText(I18NSupport.getString("listselectionpanel.remove"));
		btnRem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					remove();
				}
			}
		});
		
		scrList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrList.setMinimumSize(scrDim);
		scrList.setPreferredSize(scrDim);
		scrList.getViewport().add(list, null);
		list.setModel(listModel);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		btnPanel.add(Box.createGlue());
        btnPanel.add(btnAdd);
		btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));        
		btnPanel.add(btnRem);        
        btnPanel.add(Box.createGlue());
        
        add(scrList, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));
        
		add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
    }	
    
    private void add() {
    	// ignore double click listener for tree (which opens the query)
        // and create our own listener (which just selects the path)
    	final DBBrowserTree dbBrowserTree = new DBBrowserTree(type, false);
    	dbBrowserTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        dbBrowserTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
            	TreePath[] paths = dbBrowserTree.getSelectionPaths();
                if (paths == null) {
                    return;
                }
                dbBrowserTree.setSelectionPaths(paths);				
            }
        });
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        scroll.setPreferredSize(scrTreeDim);
        
        JPanel panel = new JPanel();
        panel.add(scroll);
                               
        JDialog dialog = new BaseDialog(panel, I18NSupport.getString("wizard.publish.entities.select"), true) {
        	 protected boolean ok() {
        		 TreePath[] paths = dbBrowserTree.getSelectionPaths();
                 if (paths == null) {
                     return false;
                 }                 
 				for (TreePath selPath : paths) {					
 					final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();	 					
 					if (!selectedNode.getDBObject().isFolder()) {
 						String path = selectedNode.getDBObject().getAbsolutePath();
 						if (!listModel.contains(path)) {
 						    // convert xml if needed before add to list
							if (selectedNode.getDBObject().getType() == DBObject.REPORTS) {
								byte result = ConverterUtil.convertIfNeeded(path);
								if (result != ConverterUtil.TYPE_CONVERSION_EXCEPTION) {
									listModel.addElement(path);
								}
							} else {
								listModel.addElement(path);
							}
 						}	
 					}  					
 				}
        	    return true;
        	 }
        };
    	dialog.setBackground(Color.WHITE);    	
        dialog.pack();             
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);                        	
    }
    
    private void remove() {
    	for (Object obj : list.getSelectedValues()) {
    		listModel.removeElement(obj);
    	}	    	
    }
    
    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(java.util.List<String> messages) {
    	if (listModel.size() <= 0) {
    		messages.add(I18NSupport.getString("wizard.publish.entities.select.error"));
    		return false;
    	}
    	context.setAttribute(PublishBulkWizard.LIST, Collections.list(listModel.elements()));
    	return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return new PublishLoginWizardPanel(null);
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return false;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(java.util.List<String> messages) {
        return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

}
