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
package ro.nextreports.designer.wizrep;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ro.nextreports.designer.action.datasource.AddDataSourceAction;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceRenderer;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 9, 2008
 * Time: 2:50:28 PM
 */
public class DataSourceWizardPanel extends WizardPanel {

    private JButton addDataSourceButton;
    private JComboBox dataSourceCombo;
    private Dimension dim = new Dimension(200, 20);
    private Dimension buttonDim = new Dimension(20, 20);

    public DataSourceWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",2, 5)  + I18NSupport.getString("wizard.panel.datasource.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.datasource.subtitle"));
        init();
        WizardUtil.disconnect();
    }

    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
        
        JDialog mainDialog = (JDialog)context.getAttribute(WizardConstants.MAIN_FRAME);
        AddDataSourceAction action = new AddDataSourceAction(mainDialog, false) {
            public void afterSaveAction() {
                DataSource ds = getAddedDataSource();
                populateDataSources(ds);
            }
        };
        addDataSourceButton.setAction(action);
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {
//        System.out.println("Validate next : " +dataSourceCombo.getSelectedIndex());
        if (dataSourceCombo.getSelectedIndex() == 0) {
            messages.add(I18NSupport.getString("wizard.panel.datasource.select"));
            return false;
        }
        context.setAttribute(WizardConstants.DATA_SOURCE, (DataSource)dataSourceCombo.getSelectedItem());
        boolean result = WizardUtil.connect(((DataSource)dataSourceCombo.getSelectedItem()).getName());
        if (!result) {
            messages.add(I18NSupport.getString("wizard.panel.datasource.error"));
        }
        return result;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return new QueryWizardPanel();
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
    public boolean validateFinish(List<String> messages) {
        return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }
	
    private void populateDataSources(DataSource selectedDataSource) {
        dataSourceCombo.removeAllItems();
        List<DataSource> sources = DefaultDataSourceManager.getInstance().getDataSources();
        Collections.sort(sources, new Comparator<DataSource>() {
            public int compare(DataSource o1, DataSource o2) {
                return Collator.getInstance().compare(o1.getName(), o2.getName());
            }
        });
        DataSource defaultDataSource = new DataSource();
        defaultDataSource.setName("-- " + I18NSupport.getString("source.dialog.select") + " --");
        dataSourceCombo.addItem(defaultDataSource);
        for (DataSource source : sources)  {
            dataSourceCombo.addItem(source);
        }
        if (selectedDataSource != null) {
            dataSourceCombo.setSelectedItem(selectedDataSource);
        }
    }

    private void init() {
        setLayout(new BorderLayout());

        dataSourceCombo = new JComboBox();
        dataSourceCombo.setPreferredSize(dim);
        dataSourceCombo.setRenderer(new DataSourceRenderer());
        populateDataSources(null);

        addDataSourceButton = new JButton();
        addDataSourceButton.setPreferredSize(buttonDim);
        addDataSourceButton.setMaximumSize(buttonDim);
        addDataSourceButton.setMinimumSize(buttonDim);

        JPanel dsPanel = new JPanel(new GridBagLayout());
        dsPanel.add(new JLabel(I18NSupport.getString("wizard.panel.datasource.label")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(dataSourceCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        dsPanel.add(addDataSourceButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        dsPanel.add(new JLabel(""), new GridBagConstraints(3, 0, 1, 2, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(dsPanel, BorderLayout.CENTER);
    }

    public String getWaitingMessage() {
        return I18NSupport.getString("wizard.panel.datasource.connect", ((DataSource)dataSourceCombo.getSelectedItem()).getName());
    }
}

