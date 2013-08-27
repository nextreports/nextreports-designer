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
package ro.nextreports.designer.wizrestore;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.designer.util.file.BackupFilter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 10-Mar-2009
 * Time: 13:08:46
 */
public class RestoreArchieveSelectionWizardPanel extends WizardPanel {

    private JButton selButton;
    private JTextField nameTextField;
    private Dimension dim = new Dimension(200, 20);
    private Dimension buttonDim = new Dimension(20, 20);
    private String path;

    public RestoreArchieveSelectionWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.restore.panel.start.select.title"));
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
        if (path == null) {
            messages.add(I18NSupport.getString("wizard.restore.panel.start.select.title.error"));
        }
        return path != null;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
        Thread executorThread = new Thread(new Runnable() {

            public void run() {

                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("restore"));
                if (path != null) {

                    if (!path.endsWith("." + BackupFilter.BACKUP_EXTENSION)) {
                        return;
                    }

                    activator.start();
                    FileUtil.deleteDir(new File(Globals.USER_DATA_DIR + "/output"));
                    new File(Globals.USER_DATA_DIR +  "/datasource.xml").delete();
                    //new File("driverpath.txt").delete();

                    FileUtil.unzip(path, Globals.USER_DATA_DIR);

                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                Globals.getMainFrame().getQueryBuilderPanel().refreshTreeOnRestore();
                                Show.info(I18NSupport.getString("restore.success"));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (activator != null) {
                    activator.stop();
                }

            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }

    private void init() {
        setLayout(new BorderLayout());

        nameTextField = new JTextField();
        nameTextField.setPreferredSize(dim);
        nameTextField.setEditable(false);

        selButton = new JButton();
        selButton.setPreferredSize(buttonDim);
        selButton.setMaximumSize(buttonDim);
        selButton.setMinimumSize(buttonDim);
        selButton.setIcon(ImageUtil.getImageIcon("folder"));
        selButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle(I18NSupport.getString("restore.long.desc"));
                fc.setAcceptAllFileFilterUsed(false);
                fc.addChoosableFileFilter(new BackupFilter());
                int returnVal = fc.showOpenDialog((JDialog) context.getAttribute(RestoreWizard.MAIN_FRAME));                

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f != null) {
                        path = f.getAbsolutePath();
                        if (!path.endsWith("." + BackupFilter.BACKUP_EXTENSION)) {
                            path = null;
                        } else {
                            nameTextField.setText(f.getName());
                        }
                    }
                }
            }
        });

        JPanel dsPanel = new JPanel(new GridBagLayout());
        dsPanel.add(new JLabel(I18NSupport.getString("wizard.restore.panel.start.select.title.label")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(selButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        dsPanel.add(new JLabel(""), new GridBagConstraints(3, 0, 1, 2, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(dsPanel, BorderLayout.CENTER);
    }

}


