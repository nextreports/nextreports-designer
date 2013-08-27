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
package ro.nextreports.designer.wizimport;

import ro.nextreports.engine.util.ReportUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.ui.HTMLDialog;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.MergeProperties;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 16-Mar-2009
 * Time: 14:13:02
 */
public class ImportPathSelectionWizardPanel extends WizardPanel {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
    private JButton selButton;
    private JTextField nameTextField;
    private JCheckBox propCheck;
    private Dimension dim = new Dimension(200, 20);
    private Dimension buttonDim = new Dimension(20, 20);
    private String path;

    private static final Log LOG = LogFactory.getLog(ImportPathSelectionWizardPanel.class);

    public ImportPathSelectionWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.import.panel.start.select.title"));
        //banner.setSubtitle(I18NSupport.getString("wizard.panel.datasource.subtitle"));
        init();
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
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(java.util.List<String> messages) {
        boolean result = true;
        if (path == null) {
            messages.add(I18NSupport.getString("wizard.import.panel.start.select.title.error"));
            result = false;
        }
        String currentPath;
        try {
            currentPath = new File(Globals.USER_DATA_DIR).getCanonicalPath();
        } catch (IOException e) {
            currentPath = Globals.USER_DATA_DIR;
        }
        if (currentPath.equals(path)) {
            messages.add(I18NSupport.getString("wizard.import.panel.start.select.title.error"));
            result = false;
        } else {
            String dataPath = path + File.separator + DefaultDataSourceManager.DATASOURCES_FILE;
            if (!new File(dataPath).exists()) {
                messages.add(I18NSupport.getString("wizard.import.panel.start.invalid.error"));
                result = false;
            } else {
                String version = DefaultDataSourceManager.getInstance().getVersion(dataPath);
                if (ReportUtil.isOlderUnsupportedVersion(version)) {
                    messages.add(I18NSupport.getString("wizard.import.panel.start.invalid.version.error"));
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
        Thread executorThread = new Thread(new Runnable() {

            public void run() {
                final List<String> sources = new ArrayList<String>();
                final Map<String, List<String>> queries = new HashMap<String, List<String>>();
                final Map<String, List<String>> reports = new HashMap<String, List<String>>();
                final Map<String, List<String>> charts = new HashMap<String, List<String>>();
                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("import"));
                if (path != null) {
                    activator.start();

                    String currentPath = Globals.USER_DATA_DIR;
                    Map<String, String> newFolders = new HashMap<String, String>();

                    // override properties from next-reports.properties
                    // do this first because we need tnsnames (if any) to connect to imported data sources
                    if (propCheck.isSelected()) {
                        String oldPropertiesPath = path + File.separator + "config" + File.separator + "next-reports.properties";
                        String newPropertiesPath = currentPath + File.separator + "config" + File.separator + "next-reports.properties";
                        MergeProperties mp = new MergeProperties();
                        File currentFile = new File(newPropertiesPath);
                        mp.setFile(currentFile);
                        mp.setImportFile(new File(oldPropertiesPath));
                        mp.setDestinationFile(currentFile);
                        try {
                            mp.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // must reset config to reload next-reports properties
                        Globals.resetConfig();
                         // properties that are read from System.properties (must do a System.setProperty)
                        Globals.setOracleClientPath();
                    }
                                        
                    // see import data source
                    String dataPath = path + File.separator + DefaultDataSourceManager.DATASOURCES_FILE;
                    List<DataSource> list = DefaultDataSourceManager.getInstance().load(dataPath);
                    String importVersion = DefaultDataSourceManager.getInstance().getVersion(dataPath);
                    LOG.info("# Import files from version : " + importVersion);
                    DataSourceManager manager = DefaultDataSourceManager.getInstance();

                    for (DataSource ds : list) {
                        if (!LicenseUtil.allowToAddAnotherDataSource()) {
                            return;
                        }
                        // to avoid data source with the same name
                        if (manager.getDataSource(ds.getName()) != null) {
                            String newName = ds.getName() + "_" + sdf.format(new Date());
                            newFolders.put(ds.getName(), newName);
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + newName + File.separator + FileReportPersistence.QUERIES_FOLDER).mkdirs();
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + newName + File.separator + FileReportPersistence.REPORTS_FOLDER).mkdirs();
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + newName + File.separator + FileReportPersistence.CHARTS_FOLDER).mkdirs();
                            ds.setName(newName);
                        } else {
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + ds.getName() + File.separator + FileReportPersistence.QUERIES_FOLDER).mkdirs();
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + ds.getName() + File.separator + FileReportPersistence.REPORTS_FOLDER).mkdirs();
                            new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR +
                                    File.separator + ds.getName() + File.separator + FileReportPersistence.CHARTS_FOLDER).mkdirs();
                        }
                        try {                        	
                            sources.add(ds.getName());                            
                            queries.put(ds.getName(), new ArrayList<String>());
                            reports.put(ds.getName(), new ArrayList<String>());
                            charts.put(ds.getName(), new ArrayList<String>());
                            manager.addDataSource(ds);
                        } catch (NonUniqueException e) {
                            e.printStackTrace();
                        }
                        Globals.getMainFrame().getQueryBuilderPanel().addDataSource(ds.getName());
                        boolean ok = SchemaManagerUtil.addDefaultSchemaAsVisible(ds);
                        if (!ok) {
                            LOG.warn("Could not add default schema for data source : " + ds.getName());                            
                        }
                    }
                    manager.save();

                    // copy all output files (.query and .report)
                    File file = new File(path + File.separator + FileReportPersistence.OUTPUT_DIR);

                    // all data sources folders
                    List<File> files = FileUtil.listFiles(file, null, false);
                    for (File f : files) {
                        LOG.info("# Copy data source : " + f.getAbsolutePath());
                        String name = f.getName();
                        if (name.equals(FileReportPersistence.OUTPUT_DIR)) {
                            continue;
                        }
                        String dsName;
                        if (newFolders.containsKey(name)) {
                            dsName = newFolders.get(name);
                        } else {
                            dsName = name;
                        }
                        File dest = new File(currentPath + File.separator + FileReportPersistence.OUTPUT_DIR + File.separator + dsName);
                        File qFolder = new File(dest.getAbsolutePath() + File.separator + FileReportPersistence.QUERIES_FOLDER);
                        File rFolder = new File(dest.getAbsolutePath() + File.separator + FileReportPersistence.REPORTS_FOLDER);
                        File cFolder = new File(dest.getAbsolutePath() + File.separator + FileReportPersistence.CHARTS_FOLDER);
                        dest.mkdirs();
                        qFolder.mkdirs();
                        rFolder.mkdirs();
                        cFolder.mkdirs();
                        try {
                            // no QUERIES and REPORTS folders in data sources folders for versions 2.0 and 2.1
                            if (importVersion.equals("2.0") || importVersion.equals("2.1")) {
                                List<File> all = FileUtil.listFiles(f, null, false);
                                for (File aFile : all) {                                    
                                    String fileName = aFile.getName();
                                    if (fileName.endsWith(".query")) {
                                        FileUtil.copy(aFile, new File(qFolder.getAbsolutePath() +  File.separator + fileName));
                                        LOG.info("# Copy query : " + aFile.getAbsolutePath());
                                        if (queries.get(dsName) == null) {
                                        	queries.put(dsName, new ArrayList<String>());
                                        }
                                        queries.get(dsName).add(fileName);
                                    } else if (fileName.endsWith(".report")) {
                                        FileUtil.copy(aFile, new File(rFolder.getAbsolutePath() +  File.separator + fileName));
                                        LOG.info("# Copy report : " + aFile.getAbsolutePath());
                                        if (reports.get(dsName) == null) {
                                        	reports.put(dsName, new ArrayList<String>());
                                        }
                                        reports.get(dsName).add(fileName);
                                    }
                                }
                            } else {
                                FileUtil.copyDirToDir(f, dest);
                                LOG.info("# Copy all files from : " + f.getAbsolutePath());
                                List<File> all = FileUtil.listFiles(f, null, true);                                
                                for (File aFile : all) {
                                    String fileName = aFile.getName();                                    
                                    if (fileName.endsWith(".query")) {
                                    	if (queries.get(dsName) == null) {
                                        	queries.put(dsName, new ArrayList<String>());
                                        }
                                        queries.get(dsName).add(fileName);
                                    } else if (fileName.endsWith(".report")) {
                                    	if (reports.get(dsName) == null) {
                                        	reports.put(dsName, new ArrayList<String>());
                                        }
                                        reports.get(dsName).add(fileName);
                                    } else if (fileName.endsWith(".chart")) {
                                    	if (charts.get(dsName) == null) {
                                    		charts.put(dsName, new ArrayList<String>());
                                        }
                                        charts.get(dsName).add(fileName);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (activator != null) {
                    activator.stop();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HTMLDialog dialog = new HTMLDialog(Globals.getMainFrame(),
                                I18NSupport.getString("import.files"),
                                getStatistics(sources, queries, reports, charts));
                        Show.centrateComponent(Globals.getMainFrame(), dialog);
                        dialog.setVisible(true);
                    }
                });
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }


    private String getStatistics(List<String> sources, Map<String, List<String>> queries,
                                 Map<String, List<String>> reports, Map<String, List<String>> charts) {
        StringBuilder sb = new StringBuilder("<HTML><ul>");
        for (String s : sources) {
            sb.append("<li><b>").append(s).append("</b>");
            List<String> qNames = queries.get(s);
            if (qNames.size() > 0) {
                sb.append("<br><b>");
                sb.append(I18NSupport.getString("node.queries"));
                sb.append("<br></b>");
                for (String q : qNames) {
                    sb.append("&nbsp;-&nbsp;");
                    sb.append(q);
                    sb.append("<br>");
                }
            }
            List<String> rNames = reports.get(s);
            if (rNames.size() > 0) {
                sb.append("<br><b>");
                sb.append(I18NSupport.getString("node.reports"));
                sb.append("<br></b>");
                for (String r : rNames) {
                    sb.append("&nbsp;-&nbsp;");
                    sb.append(r);
                    sb.append("<br>");
                }
            }
            List<String> cNames = charts.get(s);
            if (cNames.size() > 0) {
                sb.append("<br><b>");
                sb.append(I18NSupport.getString("node.charts"));
                sb.append("<br></b>");
                for (String c : cNames) {
                    sb.append("&nbsp;-&nbsp;");
                    sb.append(c);
                    sb.append("<br>");
                }
            }
            sb.append("<br>");
        }
        sb.append("</ul></HTML>");
        return sb.toString();
    }

    private void init() {
        setLayout(new BorderLayout());

        nameTextField = new JTextField();
        nameTextField.setPreferredSize(dim);
        nameTextField.setEditable(false);

        propCheck = new JCheckBox(I18NSupport.getString("wizard.import.panel.start.select.title.prop"), true);

        selButton = new JButton();
        selButton.setPreferredSize(buttonDim);
        selButton.setMaximumSize(buttonDim);
        selButton.setMinimumSize(buttonDim);
        selButton.setIcon(ImageUtil.getImageIcon("folder"));
        selButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(I18NSupport.getString("import.long.desc"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showSaveDialog((JDialog) context.getAttribute(ImportWizard.MAIN_FRAME));

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f != null) {
                        nameTextField.setText(f.getName());
                        path = f.getAbsolutePath();
                    }
                }
            }
        });

        JPanel dsPanel = new JPanel(new GridBagLayout());
        dsPanel.add(new JLabel(I18NSupport.getString("wizard.import.panel.start.select.title.label")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(selButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        dsPanel.add(propCheck, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
         dsPanel.add(new JLabel(""), new GridBagConstraints(3, 1, 1, 2, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(dsPanel, BorderLayout.CENTER);
    }

}


