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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXList;

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
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.designer.wizrep.WizardConstants;
import ro.nextreports.server.api.client.WebServiceClient;

public class DownloadListWizardPanel extends WizardPanel {

	private static Log LOG = LogFactory.getLog(DownloadListWizardPanel.class);

	private Dimension btnDim = new Dimension(20, 20);
	private Dimension scrDim = new Dimension(400, 150);
	private Dimension scrTreeDim = new Dimension(250, 200);
	private JXList list = new JXList();
	private DefaultListModel listModel = new DefaultListModel();
	private JScrollPane scrList = new JScrollPane();
	private JButton btnAdd = new JButton();
	private JButton btnRem = new JButton();
	private JcrBrowserTree jcrBrowserTree;

	public DownloadListWizardPanel() {
		super();
		banner.setTitle(I18NSupport.getString("download"));
		init();
	}

	private void init() {
		setLayout(new GridBagLayout());

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

		add(scrList, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));

		add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));
	}

	public void onDisplay() {
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
	public boolean validateFinish(List<String> messages) {
		if (listModel.size() <= 0) {
			messages.add(I18NSupport.getString("wizard.publish.entities.select.error"));
			return false;
		}
		String destinationPath = (String) context.getAttribute(DownloadBulkWizard.DESTINATION);
		String entity = (String) context.getAttribute(WizardConstants.ENTITY);
		String extension;
		if (WizardConstants.ENTITY_REPORT.equals(entity)) {
			extension = FormSaver.REPORT_FULL_EXTENSION;
		} else {
			extension = ChartUtil.CHART_FULL_EXTENSION;
		}
		StringBuilder sb = new StringBuilder();
		for (Object path : Collections.list(listModel.elements())) {
			String serverPath = (String) path;
			String name = getName(serverPath);
			if (new File(destinationPath + File.separator + name + extension).exists()) {
				sb.append(name).append(" : ");
				sb.append(I18NSupport.getString("wizard.publish.entity.found"));
				sb.append("\n");
			}
		}
		if (sb.length() > 0) {
			JDialog parent = (JDialog) context.getAttribute(PublishWizard.MAIN_FRAME);
			if (!overwriteBulk(parent, sb.toString())) {
				return false;
			}
		}
		return true;
	}

	private void add() {
		String entity = (String) context.getAttribute(WizardConstants.ENTITY);
		byte type;
		if (WizardConstants.ENTITY_REPORT.equals(entity)) {
			type = DBObject.REPORTS_GROUP;
		} else {
			type = DBObject.CHARTS_GROUP;
		}
		createSelectionDialog(type);
	}

	private void remove() {
		for (Object obj : list.getSelectedValues()) {
			listModel.removeElement(obj);
		}
	}

	private void createSelectionDialog(final byte type) {
		WebServiceClient client = (WebServiceClient) context.getAttribute(PublishWizard.CLIENT);
		jcrBrowserTree = new JcrBrowserTree(type, client);
		jcrBrowserTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		JPanel selectionPanel = JcrBrowserTreeUtil.createSelectionPanel(jcrBrowserTree, type);

		JDialog dialog = new BaseDialog(selectionPanel, I18NSupport.getString("wizard.publish.file.path.select"),
				true) {
			protected boolean ok() {
				return selection(jcrBrowserTree, type);
			}
		};
		dialog.pack();
		Show.centrateComponent((JDialog) context.getAttribute(PublishWizard.MAIN_FRAME), dialog);
		dialog.setVisible(true);
	}

	private void addAllObjectsUnderFolder(DBBrowserNode selectedNode, byte objType) {
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
			if (DBObject.REPORTS == objType && rep.getDBObject().getType() == DBObject.FOLDER_REPORT) {
				addAllObjectsUnderFolder(rep, objType);
			} else if (DBObject.CHARTS == objType && (rep.getDBObject().getType() == DBObject.FOLDER_CHART
					|| rep.getDBObject().getType() == DBObject.FOLDER_REPORT)) {
				addAllObjectsUnderFolder(rep, objType);
			} else if (rep.getDBObject().getType() == objType) {
				if (!listModel.contains(rep.getDBObject().getAbsolutePath())) {
					listModel.addElement(rep.getDBObject().getAbsolutePath());
				}
			}
		}
	}

	private boolean selection(JcrBrowserTree jcrBrowserTree, byte type) {
		TreePath[] paths = jcrBrowserTree.getSelectionPaths();
		if (paths == null) {
			return false;
		}
		byte mtype = -1;
		if (type == DBObject.REPORTS_GROUP) {
			mtype = DBObject.REPORTS;
		} else if (type == DBObject.CHARTS_GROUP) {
			mtype = DBObject.CHARTS;
		}
		for (TreePath selPath : paths) {
			DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
			if ((selectedNode.getDBObject().getType() == DBObject.REPORTS)
					|| (selectedNode.getDBObject().getType() == DBObject.CHARTS)) {
				if (!listModel.contains(selectedNode.getDBObject().getAbsolutePath())) {
					listModel.addElement(selectedNode.getDBObject().getAbsolutePath());
				}
			} else if (selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT
					|| selectedNode.getDBObject().getType() == DBObject.FOLDER_CHART
					|| selectedNode.getDBObject().getType() == DBObject.REPORTS_GROUP
					|| selectedNode.getDBObject().getType() == DBObject.CHARTS_GROUP) {
				addAllObjectsUnderFolder(selectedNode, mtype);
			}
		}
		if (listModel.size() <= 0) {
			return false;
		}

		return true;
	}

	public void onFinish() {

		Thread executorThread = new Thread(new Runnable() {

			public void run() {

				JDialog parent = (JDialog) context.getAttribute(PublishWizard.MAIN_FRAME);
//				String destinationPath = (String) context.getAttribute(DownloadBulkWizard.DESTINATION);
				UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("download"),
						listModel.size());
				activator.start();
				WebServiceClient client = (WebServiceClient) context.getAttribute(PublishWizard.CLIENT);
				String entity = (String) context.getAttribute(WizardConstants.ENTITY);
				try {
					StringBuilder sbError = new StringBuilder();
					StringBuilder sbInfo = new StringBuilder();

					if (WizardConstants.ENTITY_REPORT.equals(entity)) {
						for (String path : (List<String>) Collections.list(listModel.elements())) {
							String serverPath = (String) path;
							String name = getName(serverPath) + FormSaver.REPORT_FULL_EXTENSION;
							try {
								DownloadHelper.downloadReport(path, client, activator);

								sbInfo.append(name).append(" : ").append(I18NSupport.getString("downloaded"))
										.append("\n");

								// ReportMetaData reportMetaData =
								// client.getReport(serverPath);
								// XStream xstream =
								// XStreamFactory.createXStream();
								// Report report = (Report) xstream
								// .fromXML(new
								// String(reportMetaData.getMainFile().getFileContent(),
								// "UTF-8"));
								//
								// boolean ok = FormSaver.getInstance().save(new
								// File(destinationPath, name), report);
								// sbInfo.append(name).append(" :
								// ").append(I18NSupport.getString("downloaded"))
								// .append("\n");
								// if (ok) {
								// // save images
								// List<FileMetaData> list =
								// reportMetaData.getImages();
								// if (list != null) {
								// for (FileMetaData image : list) {
								// FileUtil.createFile(destinationPath +
								// File.separator + image.getFileName(),
								// image.getFileContent());
								// }
								// }
								//
								// // save template
								// FileMetaData fmd =
								// reportMetaData.getTemplate();
								// if (fmd != null) {
								// FileUtil.createFile(destinationPath +
								// File.separator + fmd.getFileName(),
								// fmd.getFileContent());
								// }
								// }
							} catch (Exception e) {
								sbError.append(name).append(" : ").append(e.getMessage()).append("\n");
							}
							activator.updateProgress();
						}
						Globals.getMainFrame().getQueryBuilderPanel().refreshTreeOnRestore();
					} else {
						for (Object path : Collections.list(listModel.elements())) {
							String serverPath = (String) path;
							String name = getName(serverPath) + ChartUtil.CHART_FULL_EXTENSION;
							try {
								// ChartMetaData chartMetaData =
								// client.getChart(serverPath);
								// XStream xstream =
								// XStreamFactory.createXStream();
								// Chart chart = (Chart) xstream
								// .fromXML(new
								// String(chartMetaData.getMainFile().getFileContent(),
								// "UTF-8"));
								// ChartUtil.save(new File(destinationPath,
								// name), chart);

								DownloadHelper.downloadChart((String)path, client, activator);

								sbInfo.append(name).append(" : ").append(I18NSupport.getString("downloaded"))
										.append("\n");
							} catch (Exception e) {
								sbError.append(name).append(" : ").append(e.getMessage()).append("\n");
							}
							// activator.updateProgress();
						}
						Globals.getMainFrame().getQueryBuilderPanel().refreshTreeOnRestore();
					}

					if (sbError.length() > 0) {
						Show.error(parent, sbInfo.toString() + "\n" + sbError.toString());
					} else {
						Show.info(parent, sbInfo.toString());
					}

				} finally {
					if (activator != null) {
						activator.stop();
						activator = null;
					}
				}
			}
		}, "NEXT : " + getClass().getSimpleName());
		executorThread.start();
	}

	private String getName(String serverPath) {
		int separatorIndex = serverPath.lastIndexOf("/") + 1;
		if (separatorIndex == 0) {
			separatorIndex = serverPath.lastIndexOf("\\") + 1;
		}
		return serverPath.substring(separatorIndex);
	}

	private boolean overwriteBulk(JDialog parent, String message) {
		Object[] options = { I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no") };
		int option = JOptionPane.showOptionDialog(parent,
				message + "\n" + I18NSupport.getString("wizard.publish.entities.overwrite"),
				I18NSupport.getString("report.util.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);

		return (option == JOptionPane.YES_OPTION);
	}

}
