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
package ro.nextreports.designer.datasource;

import ro.nextreports.engine.util.StringUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.server.Server;
import ro.nextreports.designer.server.ServerDataSourceSelectionPanel;
import ro.nextreports.designer.server.ServerSelectionPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TnsNameParser;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 11:09:02 AM
 */
public class TagsPanel extends JPanel {

    private Dimension textDim = new Dimension(150, 20);
    private Dimension serverDim = new Dimension(350, 22);
    
    private List<JComponent> tagTexts;
    private TagsDTO dto;
    private List<String> dataSources = new ArrayList<String>();

    private String PORT_TAG = "port";

    public TagsPanel(TagsDTO dto) {
        this.dto = dto;
        if (dto.isTns()) {
            dataSources = TnsNameParser.getTnsDataSources(Globals.getOracleClientPath());
            if (dataSources.size() == 0) {
                dto.setTns(false);
            }
        }        
        tagTexts = new ArrayList<JComponent>();
        init();
    }


    private void init() {
        setLayout(new GridBagLayout());

        for (int i=0, size=dto.getTags().size(); i<size; i++) {
            String tag = dto.getTags().get(i);
            add(new JLabel(StringUtil.capitalize(tag)), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));
            JComponent comp;
            if (dto.isTns()) {
                comp = new JComboBox();
                comp.setPreferredSize(textDim);
            } else if (dto.isServer()) {
            	if (i == 0) {
            		comp = new ServerSelectionPanel() {
            			public void selection() {                     				
            				ServerSelectionPanel serverPanel = (ServerSelectionPanel)tagTexts.get(0);
            				if (tagTexts.size() == 2) {
            					ServerDataSourceSelectionPanel dsPanel = (ServerDataSourceSelectionPanel)tagTexts.get(1);
            					Server server = serverPanel.getServer();            					
            					if (server != null) {
            						dsPanel.setServerUrl(server.getUrl());            					
            					}
            				}
            			}
            		};
            	} else {
            		ServerDataSourceSelectionPanel sp = new ServerDataSourceSelectionPanel() {
            			public boolean selection() {
							ServerDataSourceSelectionPanel dsPanel = (ServerDataSourceSelectionPanel) tagTexts.get(1);
							if (dsPanel.getServerUrl() == null) {
								Show.info(I18NSupport.getString("server.select"));
								return false;
							}
							return true;
            			}
            		};
            		sp.setUser(dto.getUser());
            		sp.setPassword(dto.getPassword());            		
            		comp = sp;
            	}
                comp.setPreferredSize(serverDim);
            } else {
                comp = new JTextField();
                comp.setPreferredSize(textDim);
            }
            
            tagTexts.add(comp);

            String value = dto.getTagsValues().get(i);
            if (dto.isTns()) {
            	JComboBox combo = (JComboBox)comp;
                for (String s : dataSources) {
                    combo.addItem(s);
                }
                if (!value.startsWith("<") || !value.endsWith(">")) {
                    combo.setSelectedItem(value);
                }
            } else if (dto.isServer()) {
            	if (i == 0) {
	            	ServerSelectionPanel panel = (ServerSelectionPanel)comp;
	            	if (!value.startsWith("<") || !value.endsWith(">")) {
	                    panel.setServer(value);
	                }
            	} else {
            		ServerDataSourceSelectionPanel panel = (ServerDataSourceSelectionPanel)comp;
                	if (!value.startsWith("<") || !value.endsWith(">")) {
                        panel.setDataSource(value);
                    }
            	}
            } else {
                JTextField text = (JTextField)comp;
                if (!value.startsWith("<") || !value.endsWith(">")) {
                    text.setText(value);
                } else {
                    if (tag.equals(PORT_TAG) && (dto.getDefaultPort() != null)) {
                        text.setText(dto.getDefaultPort());
                    }
                }
            }

            if (tag.equals(PORT_TAG) && (dto.getDefaultPort() != null)) {
                add(new JLabel("(" + dto.getDefaultPort() + ")"), new GridBagConstraints(2, i, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(5, 0, 5, 5), 0, 0));
            }
            add(comp, new GridBagConstraints(1, i, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 0, 5, 5), 0, 0));
        }
        
        if (dto.isServer()) {
        	ServerSelectionPanel serverPanel = (ServerSelectionPanel)tagTexts.get(0);
        	Server server = serverPanel.getServer();
        	if (server != null) {
        		ServerDataSourceSelectionPanel dsPanel = (ServerDataSourceSelectionPanel) tagTexts.get(1);
        		dsPanel.setServerUrl(server.getUrl());
        	}        	
        }
    }

    public List<String> getTagsValues() {
        List<String> result = new ArrayList<String>();
        for (JComponent comp : tagTexts) {
            String text;
            if (comp instanceof JTextField) {
                text = ((JTextField)comp).getText();
            } else if  (comp instanceof ServerSelectionPanel){
                text = ((ServerSelectionPanel)comp).getServer().getUrl();
            } else if  (comp instanceof ServerDataSourceSelectionPanel){
                text = ((ServerDataSourceSelectionPanel)comp).getDataSource();
            } else {
                text = (String)((JComboBox)comp).getSelectedItem();
            }
            result.add(text);
        }
        return result;
    }

    public List<String> getTags() {
        return dto.getTags();
    }
}
