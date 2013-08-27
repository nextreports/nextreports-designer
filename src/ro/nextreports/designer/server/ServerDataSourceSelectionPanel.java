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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.KeyStoreUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.wizpublish.JcrBrowserTree;

import ro.nextreports.server.api.client.Md5PasswordEncoder;
import ro.nextreports.server.api.client.WebServiceClient;

public class ServerDataSourceSelectionPanel extends JPanel {
	
	// same as in StorageConstants on the next server
	private String DATASOURCES_FOLDER_NAME = "dataSources";
	
	private String user;
	private String password;
	private String serverUrl;
		
	private JDialog parent;
	private JTextField dataSourceField;
	private Dimension buttonDim = new Dimension(20, 20);
	
	public ServerDataSourceSelectionPanel() {		
		init();
	}
			
	public void setParent(JDialog parent) {
		this.parent = parent;
	}
	
	public String getDataSource() {
		return dataSourceField.getText();
	}
	
	public void setDataSource(String dataSource) {
		dataSourceField.setText(dataSource);
	}		
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	private void init() {
		dataSourceField = new JTextField();
		JButton dsButton = new JButton(ImageUtil.getImageIcon("database"));
		dsButton.setPreferredSize(buttonDim);
		dsButton.setMaximumSize(buttonDim);
		dsButton.setMinimumSize(buttonDim);
		dsButton.setToolTipText(I18NSupport.getString("wizard.panel.start.datasource.title"));
		dsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {      
            	if (selection()) {
            		selectDataSourceDialog();
            	}
            }
        });
		
		setLayout(new GridBagLayout());
        
        add(dataSourceField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(dsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 0), 0, 0));
	}
	
	private void selectDataSourceDialog() {
		KeyStoreUtil.setKeystore();
		WebServiceClient client = new WebServiceClient();  
        // for https
        client.setKeystoreFile(KeyStoreUtil.KEYSTORE_FILE);
        client.setKeyStorePass(KeyStoreUtil.KEYSTORE_PASS);        
		if (!serverUrl.endsWith("api")) {
			if (!serverUrl.endsWith("/")) {
				serverUrl = serverUrl + "/";
			}
			serverUrl = serverUrl + "api";
		}
        client.setServer(serverUrl);
        client.setUsername(user);
        client.setPassword(password);
        client.setPasswordEncoder(new Md5PasswordEncoder());              
        
        boolean authorized = false;
        try {
            authorized = client.isAuthorized();
            if (authorized) {            	
                final JcrBrowserTree jcrBrowserTree = new JcrBrowserTree(DBObject.DATABASE, client);
                JPanel selectionPanel = JcrBrowserTreeUtil.createSelectionPanel(jcrBrowserTree, DBObject.DATABASE);
                    
                JDialog dialog = new BaseDialog(selectionPanel, 
                		I18NSupport.getString("wizard.panel.start.datasource.title"), true) {
                    protected boolean ok() {
                        return selection(jcrBrowserTree);
                    }
                };
                dialog.pack();
                Show.centrateComponent(parent, dialog);
                dialog.setVisible(true);
            } else {
            	 Show.error(parent, I18NSupport.getString("wizard.publish.connection.login.error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Show.error(parent, I18NSupport.getString("wizard.publish.connection.error"), e);            
        }
	}
	
	private boolean selection(JcrBrowserTree jcrBrowserTree) {
		TreePath treePath = jcrBrowserTree.getSelectionPath();
		if (treePath == null) {
			return false;
		}
		final DBBrowserNode selectedNode = (DBBrowserNode) treePath
				.getLastPathComponent();

		if (selectedNode.getDBObject().getType() != DBObject.DATASOURCE) {
			return false;
		}
		String absolutePath = selectedNode.getDBObject().getAbsolutePath();
		String path = absolutePath.substring(DATASOURCES_FOLDER_NAME.length()+2);
		dataSourceField.setText(path);

		return true;

	}
	
	public boolean selection() {
		return true;
	}

}
