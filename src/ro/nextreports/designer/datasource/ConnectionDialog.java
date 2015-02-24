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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.exception.ModificationException;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBInfo;
import ro.nextreports.designer.dbviewer.common.DBViewer;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SwingUtil;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 16, 2006
 * Time: 2:00:04 PM
 */
public class ConnectionDialog extends JDialog implements ActionListener, ItemListener {

    private Dimension dim = new Dimension(510, 440);
    private Dimension dim2 = new Dimension(510, 225);
    private Dimension buttonDim = new Dimension(20, 20);

    private final String STATUS = I18NSupport.getString("connection.dialog.status");
    private final String DEFAULT_DRIVER_TYPE = "Oracle";
    private Color backColor;

    private List<DriverTemplate> templates;

    Connection mConnection;
    JTextField mDriver, mURL, mUser, mName;
    JLabel urlFormatLabel;
    JPasswordField mPassword;
    JTextArea txaStatus;
    JScrollPane scr;
    JComboBox types;
    JButton urlButton;
    JCheckBox auto;
    String defaultPort = null;
    private DataSource oldDataSource;
    private boolean viewOnly = false;
    private boolean added = false;
    private boolean modified = false;
    public String oldName = "";
    public String newName = "";    

    public DataSource addedDataSource;   
    private Properties p = new Properties();

    private static final Log LOG = LogFactory.getLog(ConnectionDialog.class);   

    public ConnectionDialog(JFrame owner, String title, DataSource oldDataSource, boolean viewOnly) {
        super(owner, title, true);
        init(oldDataSource, viewOnly);
    }

    public ConnectionDialog(JDialog owner, String title, DataSource oldDataSource, boolean viewOnly) {
        super(owner, title, true);
        init(oldDataSource, viewOnly);
    }
    
    private void readDriverTemplates() {

        XStream xstream = new XStream(new DomDriver());
        xstream.alias("driver", DriverTemplate.class);
        xstream.alias("drivers", DriverTemplates.class);
        xstream.addImplicitCollection(DriverTemplates.class, "list");
        xstream.useAttributeFor(DriverTemplates.class, "version");

        InputStream is = null;
        try {            
            is = getClass().getResourceAsStream("/driver_template.xml");
            DriverTemplates dt = (DriverTemplates) xstream.fromXML(is);
            templates = dt.getList();
        } catch (Exception e1) {
            LOG.error(e1.getMessage(), e1);
            e1.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    LOG.error(e1.getMessage(), e1);
                    e1.printStackTrace();
                }
            }
        }
    }

    private void init(DataSource oldDataSource, boolean viewOnly) {
        this.oldDataSource = oldDataSource;
        this.viewOnly = viewOnly;

        readDriverTemplates();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                if (mConnection != null) {
                    try {
                        //System.out.println(">> Window closed -> disconnected.");
                        mConnection.close();
                        txaStatus.setText(I18NSupport.getString("connection.dialog.disconnected"));
                        mConnection = null;
                        //Globals.setConnection(mConnection);
                    } catch (SQLException e) {
                        txaStatus.setText(e.toString());
                    } catch (Exception e) {
                        // any other exception
                        mConnection = null;
                    }
                }
            }
        });

        create();
    }


    private void create() {
        this.getContentPane().setLayout(new GridBagLayout());
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });

        JPanel p = new JPanel(new GridBagLayout());
        backColor = p.getBackground();

        mName = new JTextField();
        mDriver = new JTextField();
        //mDriver.setBackground(backColor);
        mURL = new JTextField();
        //mURL.setBackground(backColor);
        mUser = new JTextField();
        //mUser.setBackground(backColor);
        mPassword = new JPasswordField();
        mPassword.setEchoChar('*');
        //mPassword.setBackground(backColor);
        txaStatus = new JTextArea();
        txaStatus.setEditable(false);
        txaStatus.setBackground(backColor);
        scr = new JScrollPane(txaStatus);
        scr.setSize(220, 50);
        scr.setBorder(new TitledBorder(STATUS));
        types = new JComboBox();
        types.addItemListener(this);

        urlFormatLabel = new JLabel();
        Font font = urlFormatLabel.getFont().deriveFont(Font.BOLD, 10);
        urlFormatLabel.setFont(font);

        urlButton = new JButton();
        urlButton.setPreferredSize(buttonDim);
        urlButton.setMinimumSize(buttonDim);
        urlButton.setMaximumSize(buttonDim);
        urlButton.setIcon(ImageUtil.getImageIcon("url_edit"));
        urlButton.setToolTipText(I18NSupport.getString("connection.dialog.tags.tooltip"));
        urlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editUrl();
            }
        });

        for (DriverTemplate template : templates) {
            types.addItem(template.getType());
        }
        types.setSelectedItem(DEFAULT_DRIVER_TYPE);

        auto = new JCheckBox(I18NSupport.getString("connection.dialog.auto"));
        
        if (viewOnly) {
            mName.setEditable(false);
            types.setEnabled(false);
            mDriver.setEditable(false);
            mURL.setEditable(false);
            mUser.setEditable(false);
            mPassword.setEditable(false);
            urlButton.setEnabled(false);
        }

        p.add(new JLabel(I18NSupport.getString("connection.dialog.name")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(mName, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JLabel(I18NSupport.getString("connection.dialog.type")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(types, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JLabel(I18NSupport.getString("connection.dialog.driver")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(mDriver, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JLabel(I18NSupport.getString("connection.dialog.url")), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(mURL, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(urlButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        if (!viewOnly) {
            p.add(urlFormatLabel, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 5), 0, 0));
        }

        p.add(new JLabel(I18NSupport.getString("connection.dialog.user")), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(mUser, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        p.add(new JLabel(I18NSupport.getString("connection.dialog.password")), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        p.add(mPassword, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
		if (!viewOnly) {
			p.add(auto, new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
					new Insets(5, 5, 5, 5), 0, 0));
		}

        JButton okBtn, disBtn, drvBtn, addBtn, propBtn;
        okBtn = new JButton(I18NSupport.getString("connection.dialog.test"), ImageUtil.getImageIcon("database_connect"));
        okBtn.setMnemonic('T');
        okBtn.setActionCommand("ConnectOk");
        okBtn.setToolTipText(I18NSupport.getString("connection.dialog.test.tooltip"));
        okBtn.addActionListener(this);
        disBtn = new JButton(I18NSupport.getString("connection.dialog.disconnect"), ImageUtil.getImageIcon("database"));
        disBtn.setMnemonic('D');
        disBtn.setActionCommand("Disconnect");
        disBtn.setToolTipText(I18NSupport.getString("connection.dialog.disconnect"));
        disBtn.addActionListener(this);
        drvBtn = new JButton(I18NSupport.getString("connection.dialog.add.driver"), ImageUtil.getImageIcon("add_driver"));
        drvBtn.setMnemonic('A');
        drvBtn.setActionCommand("AddDriver");
        drvBtn.setToolTipText(I18NSupport.getString("connection.dialog.add.driver.tooltip"));
        drvBtn.addActionListener(this);
        propBtn = new JButton(I18NSupport.getString("connection.dialog.properties"), ImageUtil.getImageIcon("properties"));
        propBtn.setMnemonic('P');
        propBtn.setActionCommand("Properties");
        propBtn.setToolTipText(I18NSupport.getString("connection.dialog.properties.tooltip"));
        propBtn.addActionListener(this);
        addBtn = new JButton(ImageUtil.getImageIcon("database_export"));
        if (oldDataSource == null) {
            addBtn.setText(I18NSupport.getString("connection.dialog.save"));
            addBtn.setToolTipText(I18NSupport.getString("connection.dialog.save.tooltip"));
            addBtn.setMnemonic('S');
        } else {
            addBtn.setText(I18NSupport.getString("connection.dialog.modify"));
            addBtn.setToolTipText(I18NSupport.getString("connection.dialog.modify.tooltip"));
            addBtn.setMnemonic('M');
        }
        addBtn.setActionCommand("AddDataSource");
        addBtn.addActionListener(this);
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(propBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        btnPanel.add(okBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        //btnPanel.add(disBtn);
        //btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        btnPanel.add(addBtn);
//        btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
//        btnPanel.add(drvBtn);
        SwingUtil.equalizeButtonSizes(btnPanel);

        if (!viewOnly) {
            p.add(btnPanel, new GridBagConstraints(0, 8, 3, 1, 1.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 5), 0, 0));
            p.add(scr, new GridBagConstraints(0, 9, 3, 1, 1.0, 1.0,
                    GridBagConstraints.EAST, GridBagConstraints.BOTH,
                    new Insets(5, 5, 5, 5), 0, 0));
        }


        this.getContentPane().add(p, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        if (oldDataSource != null) {
            mName.setText(oldDataSource.getName());
            types.setSelectedItem(oldDataSource.getType());
            mDriver.setText(oldDataSource.getDriver());
            mURL.setText(oldDataSource.getUrl());
            mUser.setText(oldDataSource.getUser());
            mPassword.setText(oldDataSource.getPassword());
        }
    }   

    public Connection getConnection() {
        return mConnection;
    }

    public void actionPerformed(ActionEvent ev) {
        String s = ev.getActionCommand();
        if (s.equals("ConnectOk")) {

            Thread executorThread = new Thread(new Runnable() {

                public void run() {

                    UIActivator activator = new UIActivator(ConnectionDialog.this, I18NSupport.getString("connect.to", mName.getText()));
                    activator.start();

                    try {
                        DataSource ds = new DataSource();
                        ds.setName(mName.getText());
                        Object type = types.getSelectedItem();
                        if (type != null) {
                            ds.setType((String) type);
                        }
                        ds.setDriver(mDriver.getText());
                        ds.setPassword(String.valueOf(mPassword.getPassword()));
                        ds.setUrl(mURL.getText());
                        ds.setUser(mUser.getText());
                        ds.setProperties(p);
                        mConnection = Globals.createTempConnection(ds);

                        DBViewer v = new DefaultDBViewer();
                        final DBInfo info = v.getDBInfo(DBInfo.INFO, mConnection);
                        final String success = I18NSupport.getString("connection.dialog.succes");

                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                txaStatus.setText(success + "\r\n" + info.getInfo());
                            }
                        });

                        // do the disconnect here too
                        if (mConnection != null) {
                            try {
                                mConnection.close();
                                //txaStatus.setText("Disconnected.");
                                mConnection = null;
                            } catch (final SQLException e) {
                                SwingUtilities.invokeAndWait(new Runnable() {
                                    public void run() {
                                        txaStatus.setText(e.toString());
                                    }
                                });
                            } catch (Exception ex) {
                                LOG.error(ex.getMessage(), ex);
                                ex.printStackTrace();
                                mConnection = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.error(e.getMessage(), e);
                        txaStatus.setText(e.toString());
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }

                }
            }, "NEXT : " + getClass().getSimpleName());
            executorThread.start();

            /*
        } else if (s.equals("AddDriver")) {

            final JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(new JarFilter());
            String path = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.JAR_PATH_KEY);
            if (path != null) {
                chooser.setSelectedFile(new File(path));
            }
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.JAR_PATH_KEY,
                        file.getAbsolutePath());
                try {
                    DriverPath.addEntry(file.getAbsolutePath());
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    ex.printStackTrace();
                }
                                   
                // TODO remove this option (put all drivers in jdbc-drivers folder)
				try {
					LauncherClassLoader launcherClassLoader = (LauncherClassLoader) ConnectionDialog.class.getClassLoader();
					if (file.isDirectory()) {
						launcherClassLoader.loadJars(file);
					} else {
						launcherClassLoader.loadJar(file);
					}
				} catch (Exception ex) {
					Show.error(ex);
				}                
            }
*/
        } else if (s.equals("Disconnect")) {
            if (mConnection != null) {
                try {
                    mConnection.close();
                    txaStatus.setText(I18NSupport.getString("connection.dialog.disconnected"));
                    mConnection = null;
                    //Globals.setConnection(mConnection);
                } catch (SQLException e) {
                    txaStatus.setText(e.toString());
                    mConnection = null;
                }
            }
            
        } else if (s.equals("Properties")) {
        	                	
        	if (!mDriver.getText().equals(CSVDialect.DRIVER_CLASS)) {
        		Show.info(I18NSupport.getString("connection.dialog.noproperties"));        		
        		return;
        	}
        	        	        	
        	if ((p == null) || p.isEmpty()) { 
        		if (oldDataSource != null) {
            		p = oldDataSource.getProperties();
            	}
            	if ((p == null) || p.isEmpty()) { 
            		p = getDefaultCsvProperties();
            	}        		
        	}
        	
        	final DataSourcePropertyPanel panel = new DataSourcePropertyPanel(p);
        	BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("connection.dialog.properties"), true) {
                 protected boolean ok() {
                	 Properties local = panel.getLocalProperties();
                	 for (Object key : local.keySet()) {
                		 p.put(key, p.get(key));
                	 }
                     return true;
                 }
             };
             dialog.setPreferredSize(new Dimension(300, 200));
             dialog.setLocationRelativeTo(Globals.getMainFrame());
             dialog.setVisible(true);
        	                
        } else if (s.equals("AddDataSource")) {
            addedDataSource = null;
            DataSource ds = new DataSource();

            if (mName.getText().trim().equals("")) {
                Show.info(I18NSupport.getString("connection.dialog.name.enter"));
                return;
            }

            ds.setName(mName.getText().trim());
            ds.setDriver(mDriver.getText());
            ds.setType((String) types.getSelectedItem());
            ds.setUrl(mURL.getText());
            ds.setPassword(String.valueOf(mPassword.getPassword()));
            ds.setUser(mUser.getText());
            ds.setStatus(DataSourceType.DISCONNECTED);
            ds.setProperties(p);           
            
            //System.out.println(ds);
            DataSourceManager manager = DefaultDataSourceManager.getInstance();
            try {
                if (oldDataSource == null) {
                    added = true;
                    newName = ds.getName();
                    manager.addDataSource(ds);
                    Globals.getMainFrame().getQueryBuilderPanel().addDataSource(ds.getName());
                    addedDataSource = ds;
                } else {
                    try {
                        System.out.println(">> ds.getName()=" + ds.getName() + " oldDataSource.getName()=" + oldDataSource.getName());
                        oldName = oldDataSource.getName();
                        newName = ds.getName();
                        manager.modifyDataSource(oldDataSource, ds);
                        if (!ds.getName().equals(oldDataSource.getName())) {
                            Globals.getMainFrame().getQueryBuilderPanel().modifyDataSource(oldName, newName);
                        }
                        modified = true;
                    } catch (ModificationException e) {
                        Show.info(e.getMessage());
                    }
                }
                manager.save();
                this.dispose();
                afterSave();
            } catch (NonUniqueException e) {
                Show.info(I18NSupport.getString("connection.dialog.exists", ds.getName()));
            }
        }
    }
    
	private Properties getDefaultCsvProperties() {
		Properties p = new Properties();
		p.put("separator", ",");
		p.put("fileExtension", ".csv");
		p.put("suppressHeaders", Boolean.FALSE);
		p.put("headerline", "");
		p.put("columnTypes", "");
		return p;
	}

    public void afterSave() {
    }

    public void itemStateChanged(ItemEvent e) {
        String s = (String) e.getItem();
        for (DriverTemplate template : templates) {
            if (s.equals(template.getType())) {
                mDriver.setText(template.getClassName());
                mURL.setText(template.getUrlTemplate());
                urlFormatLabel.setText(template.getUrlTemplate());
                defaultPort = template.getDefaultPort();
            }
        }
    }


    private List<String> getTags(String url) {
        List<String> tags = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = url.length(); i < size; i++) {
            char c = url.charAt(i);
            if (c == '<') {
                sb = new StringBuilder();
            } else if (c == '>') {
                tags.add(sb.toString());
            } else {
                sb.append(c);
            }
        }
        return tags;
    }

    public List<String> getTagsValues(String url, String urlFormat) {
        List<String> values = new ArrayList<String>();
        if ("".equals(url.trim())) {
            return values;
        }

        String[] parts = urlFormat.split("<[^<>]*>");        
        int lastIndex;
        String temp = url;
        for (int i = 0, size = parts.length; i < size; i++) {
            String part = parts[i];
            lastIndex = temp.indexOf(part) + part.length();
            int to;
            if (i < size - 1) {
                to = lastIndex + temp.substring(lastIndex).indexOf(parts[i + 1]);
            } else {
                to = temp.length();
            }

            //System.out.println("firstindex=" + firstIndex + "  lastIndex=" + lastIndex + "  to=" + to);
            if (lastIndex < to) {
                values.add(temp.substring(lastIndex, to));
                temp = temp.substring(lastIndex);
                //System.out.println("temp=" + temp);
            }
        }
        return values;
    }

    public String setTagValues(List<String> values, String urlFormat) {
        String[] parts = urlFormat.split("<[^<>]*>");
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = parts.length; i < size; i++) {
            sb.append(parts[i]);
            if (i < values.size()) {
                sb.append(values.get(i));
            }
        }
        return sb.toString();
    }

    public boolean wasAdded() {
        return added;
    }

    public boolean wasModified() {
        return modified;
    }

    public String getOldName() {
        return oldName;
    }

    public String getName() {
        return newName;
    }


    public void resize() {
        Dimension d = getSize();
        Dimension min = getMinimumSize();
        if (d.width < min.width) {
            d.width = min.width;
        }
        if (d.height < min.height) {
            d.height = min.height;
        }
        setSize(d);
    }

    public Dimension getPreferredSize() {
        if (viewOnly) {
            return dim2;
        } else {
            return dim;
        }
    }

    public Dimension getMinimumSize() {
        if (viewOnly) {
            return dim2;
        } else {
            return dim;
        }
    }

    public DataSource getAddedDataSource() {
        return addedDataSource;
    }

    private void editUrl() {
        String urlFormat = urlFormatLabel.getText();
        String url = mURL.getText();

        List<String> tags = getTags(urlFormat);
        if (tags.size() == 0) {
            return;
        }
        List<String> tagsValues = getTagsValues(url, urlFormat);

        boolean tns = "Oracle - Tns Name".equals(types.getSelectedItem());
        boolean server = "NextReports Server".equals(types.getSelectedItem());

        TagsDTO dto = new TagsDTO();
        dto.setTags(tags);
        dto.setTagsValues(tagsValues);
        dto.setDefaultPort(defaultPort);
        dto.setTns(tns);
        dto.setServer(server);
        if (server) {
        	String user = mUser.getText();
        	String password = new String(mPassword.getPassword());
        	if (user.trim().equals("")) {
        		Show.info(I18NSupport.getString("server.select.user"));
        		return;
        	}
        	if (password.trim().equals("")) {
        		Show.info(I18NSupport.getString("server.select.password"));
        		return;
        	}
        	dto.setUser(user);
        	dto.setPassword(password);
        }
        
        TagsPanel panel = new TagsPanel(dto);
        TagsDialog dlg = new TagsDialog(panel);
        dlg.pack();
        dlg.setResizable(false);
        Show.centrateComponent(Globals.getMainFrame(), dlg);
        dlg.setVisible(true);
        if (dlg.okPressed()) {
            mURL.setText(setTagValues(dlg.getTagsValues(), urlFormat));
        }

    }
    
    public boolean getAutoConnect() {
    	return auto.isSelected();
    }

}


