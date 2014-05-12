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
package ro.nextreports.designer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.action.datasource.DataSourceConnectAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.ui.wizard.Wizard;
import ro.nextreports.designer.ui.wizard.WizardListener;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.wizpublish.PublishLoginWizardPanel;
import ro.nextreports.designer.wizpublish.PublishWizard;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.server.api.client.ChartMetaData;
import ro.nextreports.server.api.client.DataSourceMetaData;
import ro.nextreports.server.api.client.FileMetaData;
import ro.nextreports.server.api.client.ReportMetaData;
import ro.nextreports.server.api.client.WebServiceException;
import com.thoughtworks.xstream.XStream;

/**
 * @author Mihai Dinca-Panaitescu
 */
public class NextReportsServerRequest implements WizardListener {
	
	private static final Log LOG = LogFactory.getLog(NextReportsServerRequest.class);
	private JDialog dialog;
    private PublishLoginWizardPanel loginPanel;
    
    public NextReportsServerRequest() {
    	
    	try {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					String message = I18NSupport.getString("download");
					dialog = new JDialog(Globals.getMainFrame(), message, true);

					loginPanel = new PublishLoginWizardPanel(null) {
						public boolean hasNext() {
							return false;
						}

						public boolean canFinish() {
							return true;
						}

						public boolean validateFinish(java.util.List<String> messages) {
							boolean ok = validateNext(messages);
							return ok;
						}
					};

					Wizard wizard = new Wizard(loginPanel);
					wizard.getContext().setAttribute(PublishWizard.MAIN_FRAME, dialog);
					wizard.addWizardListener(NextReportsServerRequest.this);

					dialog.setContentPane(wizard);
					dialog.setSize(400, 340);
					dialog.setLocationRelativeTo(null);
					dialog.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							super.windowClosing(e);
						}
					});
					dialog.setVisible(true);

				}
				
			});
		} catch (Exception e) {			
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		} 
    	
    }
    
    // After server request (open report / chart from Edit action on server)
    //   save data source
    //   connect to data source
    //   download report / chart
    //   open report/ chart
    public void wizardFinished(Wizard wizard) {
		dialog.dispose();
		
		try {	
			ReportMetaData _rmd = null;
			ChartMetaData _cmd = null;
			DataSourceMetaData dsmd = null;
			final boolean isReport = Globals.getServerPath().contains("reports");
			if (isReport) {
				_rmd = loginPanel.getClient().getReport(Globals.getServerPath());
				dsmd = loginPanel.getClient().getDataSource(_rmd.getDataSourcePath());
			} else {
				_cmd = loginPanel.getClient().getChart(Globals.getServerPath());
				dsmd = loginPanel.getClient().getDataSource(_cmd.getDataSourcePath());
			}
			final ReportMetaData rmd = _rmd;
			final ChartMetaData cmd = _cmd;						
			
			Globals.setWebService(loginPanel.getClient());
			Globals.setServerReportMetaData(rmd);
			Globals.setServerChartMetaData(cmd);
			Globals.setServerDSMetaData(dsmd);
			String dsName; 
			if (isReport) {
				dsName = getName(rmd.getDataSourcePath());
			} else {
				dsName = getName(cmd.getDataSourcePath());
			}			
			String serverIp = getServerIp(Globals.getServerUrl());			
			
			// create a data source with name: dsName@serverIp
			DataSource ds = new DataSource();
			ds.setName(dsName + "@" + serverIp);
			ds.setDriver(dsmd.getDriver());
			ds.setUser(dsmd.getUsername() == null ? "" : dsmd.getUsername());
			ds.setPassword(dsmd.getPassword() == null ? "" : dsmd.getPassword());
			ds.setUrl(dsmd.getUrl());
			ds.setType(dsmd.getVendor());	
			ds.setProperties(dsmd.getProperties());
									
			try {
				DefaultDataSourceManager.getInstance().addDataSource(ds);				
				DefaultDataSourceManager.getInstance().save();
				
				// User interface				
				Globals.getMainFrame().getQueryBuilderPanel().addDataSource(ds.getName());
				
			} catch (NonUniqueException e) {
				// data source found, use this 
				// @todo may not be correct if we have two data sources with the same name (in different folder) on the server
				LOG.info("Edit from server: data source '" + ds.getName() + "' exists.");				
			}
			
			final String baseName = getName(Globals.getServerPath());
			String _name = null;
			if (isReport) {
				_name = baseName + FormSaver.REPORT_FULL_EXTENSION;
			} else {
				_name = baseName + ChartUtil.CHART_FULL_EXTENSION;
			}
			final String name = _name;
			final DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
        	tree.selectNode(ds.getName(), DBObject.DATABASE);
        	DataSourceConnectAction ca= new DataSourceConnectAction(tree, tree.getSelectionPath()) {
        		
        		// NON-EDT
        	    protected void afterJob() {
        	    	// save report        	
                	XStream xstream = XStreamFactory.createXStream();
                	Report report = null;
                	Chart chart = null;
                	String destinationPath = null;
                	String type = null;
                	try {
	                	if (isReport) {
	                		report = (Report) xstream.fromXML(new String(rmd.getMainFile().getFileContent(),"UTF-8"));
	                		destinationPath = FileReportPersistence.getReportsAbsolutePath();
	                		type = I18NSupport.getString("report");
	                	} else {
	                		chart = (Chart) xstream.fromXML(new String(cmd.getMainFile().getFileContent(),"UTF-8"));
	                		destinationPath = FileReportPersistence.getChartsAbsolutePath();
	                		type = I18NSupport.getString("chart");
	                	}    
                	} catch (UnsupportedEncodingException ex) {
                		ex.printStackTrace();
						LOG.error(ex.getMessage(), ex);
                	}
        			new File(destinationPath).mkdirs();		
        			File entityFile = new File(destinationPath, name);
        			boolean overwrite = false;
        			if (entityFile.exists()) {
        				int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                                I18NSupport.getString("save.entity.exists",  type, name), 
                                "", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            overwrite = true;                           
                        } 
        			} else {
        				overwrite = true;
        			}
					if (overwrite) {
						if (isReport) {
							boolean ok = FormSaver.getInstance().save(entityFile, report);
							if (ok) {
								// save images
								List<FileMetaData> list = rmd.getImages();
								if (list != null) {
									for (FileMetaData image : list) {
										try {
											FileUtil.createFile(destinationPath	+ File.separator + image.getFileName(),	image.getFileContent());
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											LOG.error(e.getMessage(), e);
										}
									}
								}
								
								// save template
								FileMetaData fmd = rmd.getTemplate();
								if (fmd != null) {
									try {
										FileUtil.createFile(destinationPath	+ File.separator + fmd.getFileName(),	fmd.getFileContent());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										LOG.error(e.getMessage(), e);
									}
								}
							}
						} else {
							boolean ok = ChartUtil.save(entityFile, chart);
						}
					}
        	    }
        	    
        	    // EDT
        	    protected void afterCreate() {
        	    	if (isReport) {
        	    		Globals.getMainFrame().openSystemReport(tree, baseName);
        	    	} else {
        	    		Globals.getMainFrame().openSystemChart(tree, baseName);
        	    	}
        	    }
        	};
        	ca.actionPerformed(null);        	     
        	
		} catch (WebServiceException e) {			
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public void wizardCancelled(Wizard wizard) {
		Globals.resetServerFile();
		dialog.dispose();
	}

	@Override
	public void wizardPanelChanged(Wizard wizard) {				
	}
	
	private String getName(String path) {
		int lastPathSeparatorIndex = path.lastIndexOf("/");
		if (lastPathSeparatorIndex == -1) {
			return null;
		}
		return path.substring(lastPathSeparatorIndex + 1);
	}
	
	private String getServerIp(String url) {
		String protocol = "http://";
		int index = url.indexOf(protocol);
		if (index == -1) {
			protocol = "https://";
			index = url.indexOf(protocol);
			if (index == -1) {
				return "";
			}
		}
		String s = url.substring(index + protocol.length());
		index = s.indexOf(":");
		if (index != -1) {
			return s.substring(0, index);
		} else {
			index = s.indexOf("/");
			if (index != -1) {
				return s.substring(0, index);
			}
		}
		return s;
	}

}
