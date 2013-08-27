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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


public class ServerSelectionPanel extends JPanel {
	
	private JDialog dialog;
	private JDialog parent;
	private JComboBox serverComboBox;
	private JButton serverbuttonAdd;
	private JButton serverbuttonEdit;
	private JButton serverbuttonDelete;
	private Dimension buttonDim = new Dimension(20, 20);
	private List<Server> servers;
	private ServerHandler serverHandler;
	
	public ServerSelectionPanel() {
		this(new ServerHandler());
	}
	 
	public ServerSelectionPanel(ServerHandler serverHandler) {
		this.serverHandler = serverHandler;
		init();
	}
			
	public void setParent(JDialog parent) {
		this.parent = parent;
	}
	
	public Server getServer() {
		return (Server) serverComboBox.getSelectedItem();
	}
	
	public void setServer(String url) {
		for (Server server : servers) {
			if (server.getUrl().equals(url)) {
				serverComboBox.setSelectedItem(server);				
				break;
			}
		}		
	}
	
	// edit report from server
	public void setServerRequest(String url) {
		for (Server server : servers) {
			if (server.getUrl().equals(url)) {
				serverComboBox.setSelectedItem(server);		
				disablePanel();
				return;
			}
		}		
		Server s = new Server("#NextServer#", url);		
		serverComboBox.addItem(s);
		serverComboBox.setSelectedItem(s);	
		disablePanel();
	}
	
	private void disablePanel() {
		serverComboBox.setEnabled(false);
		serverbuttonAdd.setEnabled(false);
		serverbuttonEdit.setEnabled(false);
		serverbuttonDelete.setEnabled(false);
	}

	private void init() {
				
        servers = serverHandler.getServers();        
		serverComboBox = new JComboBox(servers.toArray());
        serverComboBox.setRenderer(new ServerRenderer());
        serverComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				selection();				
			}        	
        });
        serverbuttonAdd = new JButton(ImageUtil.getImageIcon("server_add"));
        serverbuttonAdd.setPreferredSize(buttonDim);
        serverbuttonAdd.setMaximumSize(buttonDim);
        serverbuttonAdd.setMinimumSize(buttonDim);
        serverbuttonAdd.setToolTipText(I18NSupport.getString("wizard.publish.login.server.add"));
        serverbuttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {            	
            	createServerDialog(true);            	
            }
        });

        serverbuttonEdit = new JButton(ImageUtil.getImageIcon("server_edit"));
        serverbuttonEdit.setPreferredSize(buttonDim);
        serverbuttonEdit.setMaximumSize(buttonDim);
        serverbuttonEdit.setMinimumSize(buttonDim);
        serverbuttonEdit.setToolTipText(I18NSupport.getString("wizard.publish.login.server.edit"));
        serverbuttonEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createServerDialog(false);
            }
        });

        serverbuttonDelete = new JButton(ImageUtil.getImageIcon("server_delete"));
        serverbuttonDelete.setPreferredSize(buttonDim);
        serverbuttonDelete.setMaximumSize(buttonDim);
        serverbuttonDelete.setMinimumSize(buttonDim);
        serverbuttonDelete.setToolTipText(I18NSupport.getString("wizard.publish.login.server.delete"));
        serverbuttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteServer();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(serverbuttonAdd);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        buttonPanel.add(serverbuttonEdit);
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        buttonPanel.add(serverbuttonDelete);
        
        setLayout(new GridBagLayout());
        
        add(serverComboBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(buttonPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 0), 0, 0));
	}		
	
	private void createServerDialog(final boolean add) {

        final JTextField nameTextField = new JTextField();
        nameTextField.setColumns(20);
        final JTextField urlTextField = new JTextField();

        Server editServer = null;
        if (!add) {
            editServer = (Server) serverComboBox.getSelectedItem();
            if (editServer != null) {
                nameTextField.setText(editServer.getName());
                urlTextField.setText(editServer.getUrl());
            } else {
                return;
            }
        } else {
            urlTextField.setText("http://<server>:<port>/nextserver");
        }

        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new GridBagLayout());
        serverPanel.add(new JLabel(I18NSupport.getString("wizard.publish.login.server.name")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        serverPanel.add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        serverPanel.add(new JLabel(I18NSupport.getString("wizard.publish.login.server.url")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        serverPanel.add(urlTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));


        final Server fserver = editServer;
        dialog = new BaseDialog(serverPanel, I18NSupport.getString("wizard.publish.login.server"), true) {
            protected boolean ok() {
                if (nameTextField.getText().trim().equals("")) {
                    Show.info(this, I18NSupport.getString("wizard.publish.login.server.name.error"));
                    return false;
                }
                String serverName = nameTextField.getText();                
                List<Server> servers = serverHandler.getServers();
                if ((add && serverHandler.serverExists(serverName)) ||
                        (!add && !serverName.equals(fserver.getName()) && serverHandler.serverExists(serverName))) {
                    Show.info(this, I18NSupport.getString("wizard.publish.login.server.name.error.exists", serverName));
                    return false;
                }

                if (urlTextField.getText().trim().equals("")) {
                    Show.info(this, I18NSupport.getString("wizard.publish.login.server.url.error"));
                    return false;
                }

                Server server = new Server(serverName, urlTextField.getText());

                if (add) {
                    servers.add(0, server);
                } else {
                    servers.remove(fserver);
                    servers.add(0, server);
                }
                serverHandler.saveServers(servers);
                serverComboBox.removeAllItems();
                for (Server s : servers) {
                    serverComboBox.addItem(s);
                }
                return true;
            }
        };
        dialog.pack();
        Show.centrateComponent(parent, dialog);
        dialog.setVisible(true);
    }

    public void deleteServer() {
        Server server = (Server) serverComboBox.getSelectedItem();
        if (server != null) {

            int option = JOptionPane.showConfirmDialog(dialog,
                    I18NSupport.getString("wizard.publish.login.server.delete.confirm"), "",
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            
            List<Server> servers = serverHandler.getServers();
            servers.remove(server);
            serverHandler.saveServers(servers);
            serverComboBox.removeAllItems();
            for (Server s : servers) {
                serverComboBox.addItem(s);
            }
        }
    }


    public class ServerRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            if (value != null) {
                Server server = (Server) value;
                value = server.getFullName();
            }

            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    public void selection() {		
	} 
	 

}
