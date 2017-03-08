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
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.server.JcrBrowserTreeUtil;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.designer.util.file.ChartFilter;
import ro.nextreports.designer.util.file.ReportFilter;
import ro.nextreports.designer.wizrep.WizardConstants;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;
import ro.nextreports.server.api.client.EntityConstants;
import ro.nextreports.server.api.client.WebServiceClient;

//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 15:17:07

//
public class PublishFileWizardPanel extends WizardPanel {

	private static Log LOG = LogFactory.getLog(WizardPanel.class);

	private JLabel nameLabel;
	private JTextField nameTextField;
	private JLabel pathLabel;
	private JTextField pathTextField;
	private JLabel sourceLabel;
	private JTextField dataSourceTextField;
	private JButton selectDataSourceButton;
	private JLabel descriptionLabel;
	private JTextArea descriptionArea;
	private JScrollPane scrArea;
	private Dimension buttonDim = new Dimension(20, 20);
	private boolean overwrite = false;

	private static String REP_SEPARATOR = ",";
	private JcrBrowserTree jcrBrowserTree = null;

	public PublishFileWizardPanel() {
		super();
		banner.setTitle(I18NSupport.getString("wizard.publish.file"));
		// banner.setSubtitle(I18NSupport.getString("wizard.panel.datasource.subtitle"));
		init();
	}

	private void init() {

		setLayout(new GridBagLayout());

		nameTextField = new JTextField();
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		pathTextField.setBackground(Color.WHITE);
		dataSourceTextField = new JTextField();
		dataSourceTextField.setEditable(false);
		dataSourceTextField.setBackground(Color.WHITE);
		descriptionArea = new JTextArea(5, 20);
		scrArea = new JScrollPane(descriptionArea);
		selectDataSourceButton = new JButton(ImageUtil.getImageIcon("database"));
		selectDataSourceButton.setPreferredSize(buttonDim);
		selectDataSourceButton.setMaximumSize(buttonDim);
		selectDataSourceButton.setMinimumSize(buttonDim);
		selectDataSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSelectionDialog(DBObject.DATABASE);
			}
		});

		JButton selectPathButton = new JButton(ImageUtil.getImageIcon("folder"));
		selectPathButton.setPreferredSize(buttonDim);
		selectPathButton.setMaximumSize(buttonDim);
		selectPathButton.setMinimumSize(buttonDim);
		selectPathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String entity = (String) context.getAttribute(WizardConstants.ENTITY);
				byte type;
				if (WizardConstants.ENTITY_REPORT.equals(entity)) {
					type = DBObject.REPORTS_GROUP;
				} else {
					type = DBObject.CHARTS_GROUP;
				}
				createSelectionDialog(type);
			}
		});

		add(nameLabel = new JLabel(I18NSupport.getString("wizard.publish.file.name")), new GridBagConstraints(0, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
		add(nameTextField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
		add(pathLabel = new JLabel(I18NSupport.getString("wizard.publish.file.path")), new GridBagConstraints(0, 1, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
		add(pathTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
		add(selectPathButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
		add(sourceLabel = new JLabel(I18NSupport.getString("wizard.publish.file.source")), new GridBagConstraints(0, 2,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
		add(dataSourceTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
		add(selectDataSourceButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 5, 5, 0), 0, 0));
		add(descriptionLabel = new JLabel(I18NSupport.getString("wizard.publish.file.description")),
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		add(scrArea, new GridBagConstraints(1, 3, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 0), 0, 0));

	}

	private void createSelectionDialog(final byte type) {
		WebServiceClient client = (WebServiceClient) context.getAttribute(PublishWizard.CLIENT);
		boolean download = (Boolean) context.getAttribute(PublishWizard.DOWNLOAD);
		jcrBrowserTree = new JcrBrowserTree(type, client, download);
		JPanel selectionPanel = JcrBrowserTreeUtil.createSelectionPanel(jcrBrowserTree, type);

		String message = "";
		if ((type == DBObject.REPORTS_GROUP) || (type == DBObject.CHARTS_GROUP)) {
			message = I18NSupport.getString("wizard.publish.file.path.select");
		} else if (type == DBObject.DATABASE) {
			message = I18NSupport.getString("wizard.publish.file.source");
		}
		JDialog dialog = new BaseDialog(selectionPanel, message, true) {
			protected boolean ok() {
				return selection(jcrBrowserTree, type);
			}
		};
		dialog.pack();
		Show.centrateComponent((JDialog) context.getAttribute(PublishWizard.MAIN_FRAME), dialog);
		dialog.setVisible(true);
	}

	private String getAllObjectUnderObjectFolder(DBBrowserNode selectedNode, byte objType) {
		String path = "";

		// get reports under this, and add them all
		// problem if the folder is not expanded, it does not see the children
		if (DBObject.REPORTS == objType && selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT
				&& selectedNode.getChildCount() == 0) {
			jcrBrowserTree.startExpandingTree(selectedNode, true, null);
		} else if (DBObject.CHARTS == objType
				&& (selectedNode.getDBObject().getType() == DBObject.FOLDER_CHART
						|| selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT)
				&& selectedNode.getChildCount() == 0) {
			jcrBrowserTree.startExpandingTree(selectedNode, true, null);
		}

		for (int x = 0; x < selectedNode.getChildCount(); x++) {
			final DBBrowserNode rep = (DBBrowserNode) selectedNode.getChildAt(x);
			String xpath = rep.getDBObject().getAbsolutePath();
			if (DBObject.REPORTS == objType && rep.getDBObject().getType() == DBObject.FOLDER_REPORT) {
				String text = getAllObjectUnderObjectFolder(rep, objType);
				if (text.length() > 1) {
					if (path.length() > 1) {
						path = path + REP_SEPARATOR + text;
					} else {
						path += text;
					}
				}

			} else if (DBObject.CHARTS == objType && (rep.getDBObject().getType() == DBObject.FOLDER_CHART
					|| rep.getDBObject().getType() == DBObject.FOLDER_REPORT)) {
				String text = getAllObjectUnderObjectFolder(rep, objType);
				if (text.length() > 1) {
					if (path.length() > 1) {
						path = path + REP_SEPARATOR + text;
					} else {
						path += text;
					}
				}
			} else if (rep.getDBObject().getType() == objType) {
				if (path.length() > 1) {
					path += REP_SEPARATOR + xpath;
				} else {
					path += xpath;
				}
			}
		}
		return path;
	}

	private boolean selection(JcrBrowserTree jcrBrowserTree, byte type) {
		boolean download = (Boolean) context.getAttribute(PublishWizard.DOWNLOAD);
		TreePath[] treePaths = jcrBrowserTree.getSelectionPaths();
		if (treePaths == null) {
			return false;
		}
		boolean downloadFoundOneOneReport = false;
		byte mtype = -1;
		if (type == DBObject.REPORTS_GROUP) {
			mtype = DBObject.REPORTS;
		} else if (type == DBObject.CHARTS_GROUP) {
			mtype = DBObject.CHARTS;
		}
		pathTextField.setText("");
		for (TreePath treePath : treePaths) {
			final DBBrowserNode selectedNode = (DBBrowserNode) treePath.getLastPathComponent();
			if ((type == DBObject.REPORTS_GROUP) || (type == DBObject.CHARTS_GROUP)) {
				String path = selectedNode.getDBObject().getAbsolutePath();
				if (download) {
					// multiple selection is allowed and we need a least a
					// report in all the folders
					if (selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT) {
						String text = getAllObjectUnderObjectFolder(selectedNode, mtype);
						if (text != null && text.length() > 1) {
							String oText = pathTextField.getText() != null ? pathTextField.getText() : "";
							if (oText.length() > 1) {
								pathTextField.setText(oText + REP_SEPARATOR + text);
							} else {
								pathTextField.setText(text);
							}
							downloadFoundOneOneReport = true;
						}
					} else if (selectedNode.getDBObject().getType() == DBObject.REPORTS_GROUP) {
						// pathTextField.setText(JcrNodeExpander.REPORTS_ROOT);

						String text = getAllObjectUnderObjectFolder(selectedNode, mtype);
						if (text != null && text.length() > 1) {
							String oText = pathTextField.getText() != null ? pathTextField.getText() : "";
							if (oText.length() > 1) {
								pathTextField.setText(oText + REP_SEPARATOR + text);
							} else {
								pathTextField.setText(text);
							}
							downloadFoundOneOneReport = true;
						}

					} else if (selectedNode.getDBObject().getType() == DBObject.CHARTS_GROUP) {
						// pathTextField.setText(JcrNodeExpander.CHARTS_ROOT);

						String text = getAllObjectUnderObjectFolder(selectedNode, mtype);
						if (text != null && text.length() > 1) {
							String oText = pathTextField.getText() != null ? pathTextField.getText() : "";
							if (oText.length() > 1) {
								pathTextField.setText(oText + REP_SEPARATOR + text);
							} else {
								pathTextField.setText(text);
							}
							downloadFoundOneOneReport = true;
						}
					} else if ((selectedNode.getDBObject().getType() == DBObject.REPORTS)
							|| (selectedNode.getDBObject().getType() == DBObject.CHARTS)) {
						String text = pathTextField.getText() != null ? pathTextField.getText() : "";
						if (text.length() > 1) {
							pathTextField.setText(text + REP_SEPARATOR + path);
						} else {
							pathTextField.setText(path);
						}
						downloadFoundOneOneReport = true;
					}
				} else {
					if (selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT) {
						pathTextField.setText(path);
						overwrite = false;
					} else if (selectedNode.getDBObject().getType() == DBObject.REPORTS_GROUP) {
						pathTextField.setText(JcrNodeExpander.REPORTS_ROOT);
						overwrite = false;
					} else if (selectedNode.getDBObject().getType() == DBObject.CHARTS_GROUP) {
						pathTextField.setText(JcrNodeExpander.CHARTS_ROOT);
						overwrite = false;
					} else if ((selectedNode.getDBObject().getType() == DBObject.REPORTS)
							|| (selectedNode.getDBObject().getType() == DBObject.CHARTS)) {
						// report
						pathTextField.setText(path.substring(0, path.lastIndexOf("/")));
						nameTextField.setText(path.substring(path.lastIndexOf("/") + 1));
						overwrite = true;
					}
				}
			} else if (type == DBObject.DATABASE) {
				if (selectedNode.getDBObject().getType() != DBObject.DATASOURCE) {
					return false;
				}
				dataSourceTextField.setText(selectedNode.getDBObject().getAbsolutePath());
			}
		}
		if (download && !downloadFoundOneOneReport) {
			String entity = (String) context.getAttribute(WizardConstants.ENTITY);
			String name;
			if (WizardConstants.ENTITY_REPORT.equals(entity)) {
				name = I18NSupport.getString("report");
			} else {
				name = I18NSupport.getString("chart");
			}
			Show.info(SwingUtilities.getWindowAncestor(jcrBrowserTree),
					I18NSupport.getString("download.name.select", name));
			return false;
		}

		return true;
	}

	/**
	 * Called when the panel is set.
	 */
	public void onDisplay() {

		if (context.getAttribute(PublishBulkWizard.LIST) != null) {
			nameLabel.setVisible(false);
			nameTextField.setVisible(false);
			// for bulk publish we won't keep info about where the files are
			// uploaded

			// String entity = (String)
			// context.getAttribute(WizardConstants.ENTITY);
			// if (WizardConstants.ENTITY_CHART.equals(entity)) {
			// }

		} else {

			boolean download = (Boolean) context.getAttribute(PublishWizard.DOWNLOAD);
			if (download) {
				pathLabel.setText(I18NSupport.getString("download.path"));
				nameLabel.setVisible(false);
				nameTextField.setVisible(false);
				sourceLabel.setVisible(false);
				dataSourceTextField.setVisible(false);
				selectDataSourceButton.setVisible(false);
				descriptionLabel.setVisible(false);
				scrArea.setVisible(false);
			} else {
				// we use a hashcode to prevent "Key too long" error
				String load = ReporterPreferencesManager.getInstance().loadParameter(
						String.valueOf((ReporterPreferencesManager.NEXT_REPORT_SPATH + getFileName()).hashCode()));
				if (load != null) {
					String[] s = load.split(PublishLoginWizardPanel.DELIM);
					if (s.length >= 3) {
						nameTextField.setText(s[0]);
						pathTextField.setText(s[1]);
						dataSourceTextField.setText(s[2]);
					}
				} else {
					String reportPath = (String) context.getAttribute(PublishWizard.REPORT_PATH);
					String name = new File(reportPath).getName();
					String entity = (String) context.getAttribute(WizardConstants.ENTITY);
					String extension;
					if (WizardConstants.ENTITY_REPORT.equals(entity)) {
						extension = ReportFilter.REPORT_EXTENSION;
					} else {
						extension = ChartFilter.CHART_EXTENSION;
					}
					nameTextField.setText(name.substring(0, name.indexOf(extension)));
				}
			}
		}
	}

	/**
	 * Is there be a next panel?
	 *
	 * @return true if there is a panel to move to next
	 */
	public boolean hasNext() {
		return false;
	}

	public boolean validateNext(List<String> messages) {
		return false;
	}

	/**
	 * Get the next panel to go to.
	 */
	public WizardPanel getNextPanel() {
		return null;
	}

	/**
	 * Can this panel finish the wizard?
	 *
	 * @return true if this panel can finish the wizard.
	 */
	public boolean canFinish() {
		return true;
	}

	/**
	 * Called to validate the panel before finishing the wizard. Should return
	 * false if canFinish returns false.
	 *
	 * @param messages
	 *            a List of messages to be displayed.
	 * @return true if it is valid for this wizard to finish.
	 */
	public boolean validateFinish(java.util.List<String> messages) {

		boolean download = (Boolean) context.getAttribute(PublishWizard.DOWNLOAD);

		if (!download && nameTextField.getText().trim().equals("")
				&& (context.getAttribute(PublishBulkWizard.LIST) == null)) {
			messages.add(I18NSupport.getString("wizard.publish.file.name.error"));
			return false;
		}

		if (!download && !StringUtil.isFileName(nameTextField.getText())
				&& (context.getAttribute(PublishBulkWizard.LIST) == null)) {
			messages.add(I18NSupport.getString("name.invalid"));
			return false;
		}

		if (pathTextField.getText().trim().equals("")) {
			messages.add(I18NSupport.getString("wizard.publish.file.path.error"));
			return false;
		}

		if (!download && dataSourceTextField.getText().trim().equals("")) {
			messages.add(I18NSupport.getString("wizard.publish.file.source.error"));
			return false;
		}

		// a special report (table, alarm, indicator) cannot be published if not
		// all parameters have default values
		if (!download) {
			String entity = (String) context.getAttribute(WizardConstants.ENTITY);
			if (WizardConstants.ENTITY_REPORT.equals(entity)) {
				String reportPath = (String) context.getAttribute(PublishWizard.REPORT_PATH);
				if (reportPath != null) {
					boolean ok = defaultValuesOk(reportPath, messages);
					if (!ok) {
						return false;
					}
				}
			}
		}

		JDialog parent = (JDialog) context.getAttribute(PublishWizard.MAIN_FRAME);
		WebServiceClient client = (WebServiceClient) context.getAttribute(PublishWizard.CLIENT);
		try {
			if (!download) {
				if (context.getAttribute(PublishBulkWizard.LIST) != null) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) context.getAttribute(PublishBulkWizard.LIST);
					StringBuilder sb = new StringBuilder();
					for (String path : list) {
						if (!path.endsWith(".chart")) {
							boolean ok = defaultValuesOk(path, messages);
							if (!ok) {
								return false;
							}
						}
						String name = path.substring(path.lastIndexOf(File.separator) + 1);
						int found = exists(client, pathTextField.getText(), name);
						if (found != EntityConstants.ENTITY_NOT_FOUND) {
							sb.append(name).append(" : ");
							sb.append(I18NSupport.getString("wizard.publish.entity.found"));
							sb.append("\n");
						}
					}
					if (sb.length() > 0) {
						if (!overwriteBulk(parent, sb.toString())) {
							return false;
						}
					}
				} else {
					int found = exists(client, pathTextField.getText(), nameTextField.getText());
					if (found != EntityConstants.ENTITY_NOT_FOUND) {
						if ((found == EntityConstants.REPORT_FOUND) || (found == EntityConstants.CHART_FOUND)) {
							if (!overwrite(parent, nameTextField.getText(), found)) {
								return false;
							}
						} else {
							Show.info(I18NSupport.getString("wizard.publish.entity.found"));
							return false;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Show.error(parent, e.getMessage());
			return false;
		}

		if (!download && (context.getAttribute(PublishBulkWizard.LIST) == null)) {
			ReporterPreferencesManager.getInstance().storeParameter(
					String.valueOf((ReporterPreferencesManager.NEXT_REPORT_SPATH + getFileName()).hashCode()),
					nameTextField.getText() + PublishLoginWizardPanel.DELIM + pathTextField.getText()
							+ PublishLoginWizardPanel.DELIM + dataSourceTextField.getText());
		}

		return true;
	}

	private boolean defaultValuesOk(String reportPath, List<String> messages) {
		File reportFile = new File(reportPath);
		try {
			Report report = ReportUtil.loadConvertedReport(new FileInputStream(reportFile));
			if (report.getLayout().getReportType() == ResultExporter.DEFAULT_TYPE) {
				return true;
			}
			if (!ParameterUtil.allParametersHaveDefaults(ParameterUtil.getUsedNotHiddenParametersMap(report))) {
				messages.add(I18NSupport.getString("parameter.default.restriction"));
				return false;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return true;
	}

	public void onFinish() {
		Thread executorThread = new Thread(new Runnable() {

			public void run() {
				JDialog parent = (JDialog) context.getAttribute(PublishWizard.MAIN_FRAME);
				boolean download = (Boolean) context.getAttribute(PublishWizard.DOWNLOAD);
				String message = "";
				if (download) {
					message = I18NSupport.getString("download");
				} else {
					message = I18NSupport.getString("publish");
				}
				UIActivator activator = new UIActivator(Globals.getMainFrame(), message);
				if (context.getAttribute(PublishBulkWizard.LIST) != null) {
					activator = new UIActivator(Globals.getMainFrame(), message,
							((List) context.getAttribute(PublishBulkWizard.LIST)).size());
				}
				activator.start();
				WebServiceClient client = (WebServiceClient) context.getAttribute(PublishWizard.CLIENT);
				String entity = (String) context.getAttribute(WizardConstants.ENTITY);
				if (download) {
					try {

						if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("download.name"))) {
							return;
						}
						if (WizardConstants.ENTITY_REPORT.equals(entity)) {
							// get report from server
							for (String reportPath : pathTextField.getText().split(",")) {
								DownloadHelper.downloadReport(reportPath, client, activator);
								// ReportMetaData reportMetaData =
								// client.getReport(reportPath);
								// XStream xstream =
								// XStreamFactory.createXStream();
								// Report report = (Report) xstream
								// .fromXML(new
								// String(reportMetaData.getMainFile().getFileContent(),
								// "UTF-8"));
								//
								// if (activator != null) {
								// activator.stop();
								// activator = null;
								// }
								//
								// byte status =
								// ReportUtil.isValidReportVersion(report);
								// if (ReportUtil.REPORT_INVALID_OLDER ==
								// status) {
								// Show.error(I18NSupport.getString("report.version.invalid.older"));
								// return;
								// } else if (ReportUtil.REPORT_INVALID_NEWER ==
								// status) {
								// Show.error(I18NSupport.getString("report.version.invalid.newer",
								// ReleaseInfoAdapter.getVersionNumber()));
								// return;
								// }
								//
								// // save the report
								// String existingPath =
								// Globals.getCurrentReportAbsolutePath();
								//
								// // check if the path exists, if not create
								// the
								// // report automatically
								// // extract path and report name
								//
								// String repPath = reportMetaData.getPath();
								// repPath = repPath.replaceFirst("/reports",
								// "");
								// String repName = null;
								// try {
								// repName =
								// repPath.substring(repPath.lastIndexOf("/") +
								// 1);
								// repPath = repPath.substring(0,
								// repPath.lastIndexOf("/"));
								// } catch (Exception e) {
								// repName =
								// repPath.substring(repPath.lastIndexOf("\\"));
								// repPath = repPath.substring(0,
								// repPath.lastIndexOf("/"));
								// }
								//
								// File px = new
								// File(FileReportPersistence.getReportsRelativePath());
								// if (!px.exists()) {
								// px.mkdirs();
								// }
								//
								// repPath =
								// FileReportPersistence.getReportsRelativePath()
								// + File.separator + repPath;
								// File rx = new File(repPath);
								//
								// String name = null;
								// // if the path exists ask for a new name for
								// the
								// // report
								// if (!rx.exists()) {
								// rx.mkdirs();
								// }
								//
								// File mxxFile = new File(rx.getAbsolutePath()
								// + File.separator + repName);
								//
								// if (!mxxFile.exists()) {
								// Globals.setCurrentReportAbsolutePath(rx.getAbsolutePath()
								// + File.separator + repName
								// + FormSaver.REPORT_EXTENSION_SEPARATOR +
								// FormSaver.REPORT_EXTENSION);
								// report.setName(repName +
								// FormSaver.REPORT_EXTENSION_SEPARATOR
								// + FormSaver.REPORT_EXTENSION);
								// name =
								// FormSaver.getInstance().save(I18NSupport.getString("save.report"),
								// false,
								// report);
								// } else {
								// report.setName(repName);
								// name =
								// FormSaver.getInstance().save(I18NSupport.getString("save.report"),
								// true,
								// report);
								// }
								//
								// if (name != null) {
								// String path =
								// Globals.getCurrentReportAbsolutePath();
								//
								// // this is buggy , adding report directly to
								// // folder on automatic, rather have refresh
								// // datasouce all together
								//
								// //
								// Globals.getMainFrame().getQueryBuilderPanel().addReport(name,
								// // path);
								//
								// Globals.getReportUndoManager().discardAllEdits();
								//
								// // save images
								// List<FileMetaData> list =
								// reportMetaData.getImages();
								// if (list != null) {
								// String prefix = path.substring(0,
								// path.lastIndexOf(File.separator));
								// for (FileMetaData image : list) {
								// FileUtil.createFile(prefix + File.separator +
								// image.getFileName(),
								// image.getFileContent());
								// }
								// }
								//
								// // save template
								// FileMetaData fmd =
								// reportMetaData.getTemplate();
								// if (fmd != null) {
								// String prefix = path.substring(0,
								// path.lastIndexOf(File.separator));
								// FileUtil.createFile(prefix + File.separator +
								// fmd.getFileName(),
								// fmd.getFileContent());
								// }
								// }
								//
								// Globals.setCurrentReportAbsolutePath(existingPath);
							} // end loop

							// refresh whole tree after an import
							Globals.getMainFrame().getQueryBuilderPanel().refreshTreeOnRestore();
						} else {
							for (String chartPath : pathTextField.getText().split(",")) {
								DownloadHelper.downloadChart(chartPath, client, activator);
							}

							// // get chart from server
							// ChartMetaData chartMetaData =
							// client.getChart(pathTextField.getText());
							// XStream xstream = XStreamFactory.createXStream();
							// Chart chart = (Chart) xstream
							// .fromXML(new
							// String(chartMetaData.getMainFile().getFileContent(),
							// "UTF-8"));
							//
							// if (activator != null) {
							// activator.stop();
							// activator = null;
							// }
							//
							// byte status =
							// NextChartUtil.isValidChartVersion(chart);
							// if (NextChartUtil.CHART_INVALID_NEWER == status)
							// {
							// Show.error(I18NSupport.getString("chart.version.invalid.newer",
							// ReleaseInfoAdapter.getVersionNumber()));
							// return;
							// }
							//
							// // save the chart
							// String existingPath =
							// Globals.getCurrentChartAbsolutePath();
							// String name =
							// ChartUtil.saveChart(I18NSupport.getString("save.report"),
							// true, chart);
							// if (name != null) {
							// String path =
							// Globals.getCurrentChartAbsolutePath();
							// Globals.getMainFrame().getQueryBuilderPanel().addChart(name,
							// path);
							// Globals.getReportUndoManager().discardAllEdits();
							// }
							// Globals.setCurrentChartAbsolutePath(existingPath);

							// refresh whole tree after an import
							Globals.getMainFrame().getQueryBuilderPanel().refreshTreeOnRestore();
						}

					} catch (Exception e) {
						e.printStackTrace();
						String name;
						if (WizardConstants.ENTITY_REPORT.equals(entity)) {
							name = I18NSupport.getString("report.name");
						} else {
							name = I18NSupport.getString("chart.name");
						}
						Show.error(parent, I18NSupport.getString("download.name.error", name), e);
					} finally {
						if (activator != null) {
							activator.stop();
						}
					}
				} else {

					if (WizardConstants.ENTITY_REPORT.equals(entity)) {
						try {
							if (context.getAttribute(PublishBulkWizard.LIST) == null) {
								String reportPath = (String) context.getAttribute(PublishWizard.REPORT_PATH);
								WebServiceResult result = WebServiceUtil.publishReport(client,
										pathTextField.getText() + "/" + nameTextField.getText(),
										dataSourceTextField.getText(), descriptionArea.getText(), reportPath);
								if (result.isError()) {
									Show.error(parent, result.getMessage());
								} else {
									Show.info(parent, result.getMessage());
								}
							} else {
								@SuppressWarnings("unchecked")
								List<String> list = (List<String>) context.getAttribute(PublishBulkWizard.LIST);
								StringBuilder sbInfo = new StringBuilder();
								StringBuilder sbError = new StringBuilder();
								for (String path : list) {
									String name = path.substring(path.lastIndexOf(File.separator) + 1,
											path.indexOf(FormSaver.REPORT_FULL_EXTENSION));
									WebServiceResult result = WebServiceUtil.publishReport(client,
											pathTextField.getText() + "/" + name, dataSourceTextField.getText(),
											descriptionArea.getText(), path);
									if (result.isError()) {
										sbError.append(name + " : " + result.getMessage()).append("\n");
									} else {
										sbInfo.append(name + " : " + result.getMessage()).append("\n");
									}
									activator.updateProgress();
								}
								if (sbError.length() > 0) {
									Show.error(parent, sbInfo.toString() + "\n" + sbError.toString());
								} else {
									Show.info(parent, sbInfo.toString());
								}
							}
						} finally {
							if (activator != null) {
								activator.stop();
							}
						}
					} else {

						try {
							if (context.getAttribute(PublishBulkWizard.LIST) == null) {
								String reportPath = (String) context.getAttribute(PublishWizard.REPORT_PATH);
								WebServiceResult result = WebServiceUtil.publishChart(client,
										pathTextField.getText() + "/" + nameTextField.getText(),
										dataSourceTextField.getText(), descriptionArea.getText(), reportPath);
								if (result.isError()) {
									Show.error(parent, result.getMessage());
								} else {
									Show.info(parent, result.getMessage());
								}
							} else {
								@SuppressWarnings("unchecked")
								List<String> list = (List<String>) context.getAttribute(PublishBulkWizard.LIST);
								StringBuilder sbInfo = new StringBuilder();
								StringBuilder sbError = new StringBuilder();
								for (String path : list) {
									String name = path.substring(path.lastIndexOf(File.separator) + 1,
											path.indexOf(ChartUtil.CHART_FULL_EXTENSION));
									WebServiceResult result = WebServiceUtil.publishChart(client,
											pathTextField.getText() + "/" + name, dataSourceTextField.getText(),
											descriptionArea.getText(), path);
									if (result.isError()) {
										sbError.append(name + " : " + result.getMessage()).append("\n");
									} else {
										sbInfo.append(name + " : " + result.getMessage()).append("\n");
									}
									activator.updateProgress();
								}
								if (sbError.length() > 0) {
									Show.error(parent, sbInfo.toString() + "\n" + sbError.toString());
								} else {
									Show.info(parent, sbInfo.toString());
								}
							}
						} finally {
							if (activator != null) {
								activator.stop();
							}
						}
					}
				}
			}
		}, "NEXT : " + getClass().getSimpleName());
		executorThread.start();
	}

	private int exists(WebServiceClient client, String entityPath, String entityName) throws Exception {
		if (overwrite) {
			return EntityConstants.REPORT_FOUND;
		}
		String path = entityPath + "/" + entityName;
		return client.entityExists(path);
	}

	private boolean overwrite(JDialog parent, String name, int foundType) {
		Object[] options = { I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no") };
		String type;
		if (foundType == EntityConstants.REPORT_FOUND) {
			type = I18NSupport.getString("report");
		} else {
			type = I18NSupport.getString("chart");
		}
		int option = JOptionPane.showOptionDialog(parent, I18NSupport.getString("wizard.publish.overwrite", type, name),
				I18NSupport.getString("report.util.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);

		return (option == JOptionPane.YES_OPTION);
	}

	private boolean overwriteBulk(JDialog parent, String message) {
		Object[] options = { I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no") };
		int option = JOptionPane.showOptionDialog(parent,
				message + "\n" + I18NSupport.getString("wizard.publish.entities.overwrite"),
				I18NSupport.getString("report.util.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);

		return (option == JOptionPane.YES_OPTION);
	}

	private String getFileName() {
		String reportPath = (String) context.getAttribute(PublishWizard.REPORT_PATH);
		File reportFile = new File(reportPath);
		return reportFile.getName();
	}

}
