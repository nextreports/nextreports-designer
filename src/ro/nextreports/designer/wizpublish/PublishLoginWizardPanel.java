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

import ro.nextreports.server.api.client.WebServiceClient;
import ro.nextreports.server.api.client.Md5PasswordEncoder;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.server.Server;
import ro.nextreports.designer.server.ServerHandler;
import ro.nextreports.designer.server.ServerSelectionPanel;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.KeyStoreUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;

import java.util.List;
import java.awt.*;

//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 14:34:50
public class PublishLoginWizardPanel extends WizardPanel {
		
    private static final Log LOG = LogFactory.getLog(PublishLoginWizardPanel.class); 
    
    private ServerSelectionPanel serverPanel;
    private JTextField userTextField;
    private JPasswordField passField;
    private JCheckBox rememberCheckBox;

    public static final String DELIM  = "nextaabbaanext";

    private WebServiceClient client;
    private String reportPath;

    public PublishLoginWizardPanel(String reportPath) {
        super();
        banner.setTitle(I18NSupport.getString("wizard.publish.login"));
        //banner.setSubtitle(I18NSupport.getString("wizard.panel.datasource.subtitle"));
        
        KeyStoreUtil.setKeystore();
        
        client = new WebServiceClient();  
        // for https
        client.setKeystoreFile(KeyStoreUtil.KEYSTORE_FILE);
        client.setKeyStorePass(KeyStoreUtil.KEYSTORE_PASS);
        
        this.reportPath = reportPath;
        init();
    }

    private void init() {

        userTextField = new JTextField();
        
        passField = new JPasswordField();
        rememberCheckBox = new JCheckBox(I18NSupport.getString("wizard.publish.login.remember"));

        ServerHandler serverHandler = new ServerHandler();
        List<Server> servers = serverHandler.getServers();
        String lastServer = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.LAST_SERVER);        
        if (lastServer != null) {
            serverHandler.adjustServers(lastServer);
            autoSetCredentials(lastServer);
        }
                
        serverPanel = new ServerSelectionPanel(serverHandler) {
        	
        	@Override
        	public void selection() {
        		Server server = getServer();
        		if (server != null) {
        			autoSetCredentials(server.getName());
        		}
        	}
        };    
        
        // edit from server (url protocol contains user name)
        if (Globals.getServerUser() != null) {
        	userTextField.setText(Globals.getServerUser());
        	userTextField.setEnabled(false);
        	
        	serverPanel.setServerRequest(Globals.getServerUrl());
        }

        setLayout(new GridBagLayout());
        add(new JLabel(I18NSupport.getString("wizard.publish.login.server")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(serverPanel, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));        
        add(new JLabel(I18NSupport.getString("wizard.publish.login.user")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(userTextField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(I18NSupport.getString("wizard.publish.login.password")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(passField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(rememberCheckBox, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));


        add(new JLabel(""), new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
    }    
    
    private void autoSetCredentials(String serverName) {
    	String remember = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.REMEMBER_AUTH);
        if ((remember != null) && Boolean.parseBoolean(remember)) {
            rememberCheckBox.setSelected(true);
            String auth =  ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.SERVER_AUTH  + serverName);
            //System.out.println("key="+(ReporterPreferencesManager.SERVER_AUTH  + lastServer));
            //System.out.println("auth="+auth);
            if (auth != null) {
                int index = auth.indexOf(DELIM);
                if (index != -1) {
                	String savedUser = auth.substring(0, index);
                	if (Globals.getServerUser() != null) {
                		// edit in designer
                		if (Globals.getServerUser().equals(savedUser)) {
                			userTextField.setText(savedUser);
                    		passField.setText(auth.substring(index + DELIM.length()));
                		} else {
                			// other user is saved : we do not have the password
                			userTextField.setText(Globals.getServerUser());
                		}
                		
                	} else {
                		// simple publish
                		userTextField.setText(savedUser);
                		passField.setText(auth.substring(index + DELIM.length()));
                	}
                }
            }
        }
    }


    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
    	serverPanel.setParent((JDialog) context.getAttribute(PublishWizard.MAIN_FRAME));
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    public boolean validateNext(List<String> messages) {
        if (serverPanel.getServer() == null) {
            messages.add(I18NSupport.getString("wizard.publish.login.server.error"));
            return false;
        }

        if (userTextField.getText().trim().equals("")) {
            messages.add(I18NSupport.getString("wizard.publish.login.user.error"));
            return false;
        }

        if (passField.getPassword().length == 0) {
            messages.add(I18NSupport.getString("wizard.publish.login.password.error"));
            return false;
        }

        Server server = serverPanel.getServer();
        String url = server.getUrl();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + "api";
        client.setServer(url);
        client.setUsername(userTextField.getText());
        client.setPassword(new String(passField.getPassword()));
        client.setPasswordEncoder(new Md5PasswordEncoder());

        JDialog parent = (JDialog) context.getAttribute(PublishWizard.MAIN_FRAME);
        boolean authorized = false;
        try {
            authorized = client.isAuthorized();            
        } catch (Exception e) {
            e.printStackTrace();
            Show.error(parent, I18NSupport.getString("wizard.publish.connection.error"), e);
            return false;
        }

        if (!authorized) {
            Show.error(parent, I18NSupport.getString("wizard.publish.connection.login.error"));
            return false;
        }

        context.setAttribute(PublishWizard.CLIENT, client);
        context.setAttribute(PublishWizard.REPORT_PATH, reportPath);

        ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.LAST_SERVER, server.getName());
        boolean remember = rememberCheckBox.isSelected();
        ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.REMEMBER_AUTH, String.valueOf(remember));
        if (remember) {            
            ReporterPreferencesManager.getInstance().storeParameter(
                    ReporterPreferencesManager.SERVER_AUTH + server.getName() ,
                    userTextField.getText() + DELIM + new String(passField.getPassword()));
        }

        return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return new PublishFileWizardPanel();
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

    public void onFinish() {
    }

	public WebServiceClient getClient() {
		return client;
	}        
       
}
