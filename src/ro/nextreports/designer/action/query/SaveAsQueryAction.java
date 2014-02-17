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
package ro.nextreports.designer.action.query;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.*;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.Report;

/**
 * @author Decebal Suiu
 */
public class SaveAsQueryAction extends AbstractAction {

    private String name;
    private String path;
    private boolean forced = false;
    
    public SaveAsQueryAction() {
    	this(false);
    }

    public SaveAsQueryAction(boolean forced) {
        putValue(Action.NAME, I18NSupport.getString("save.as.query"));
        Icon icon = ImageUtil.getImageIcon("query_save_as");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('U'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.as.query.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.as.query.desc"));
        this.forced = forced;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void actionPerformed(ActionEvent e) {

        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (builderPanel.isCleaned()) {
            return;
        }

        if (!forced && MessageUtil.showReconnect()) {
            return;
        }

        boolean save_as = false;
        // save as
        SaveEntityDialog dialog = null;
        if (name == null) {
            save_as = true;
            //name = JOptionPane.showInputDialog(Globals.getMainFrame(), "Enter query name");
            SaveEntityPanel savePanel = new SaveEntityPanel(I18NSupport.getString("save.as.query.name"), DBObject.QUERIES_GROUP);
            dialog = new SaveEntityDialog((String) this.getValue(Action.NAME), savePanel, I18NSupport.getString("query"), save_as);
            dialog.pack();
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);
            if (dialog.okPressed()) {
                name = savePanel.getName();
                path = savePanel.getFolderPath() + File.separator + name + FileReportPersistence.REPORT_EXTENSION_SEPARATOR +
                    FileReportPersistence.REPORT_EXTENSION;
            } else {
                return;
            }

            if (name == null) {
                return;
            }
        }

        if  (path == null) {
            path = FileReportPersistence.getQueriesAbsolutePath() +
                    File.separator + name + FileReportPersistence.REPORT_EXTENSION_SEPARATOR +
                    FileReportPersistence.REPORT_EXTENSION;
        }

        Report report = builderPanel.createReport(name);

        ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                Globals.getReportPersistenceType());

        try {
            boolean save = repPersist.saveReport(report, path);
            if (!save) {                
                Show.error(I18NSupport.getString("write.error", path));
            } else {
                if ((dialog == null) || !dialog.isOverwrite()) {
                    Globals.setCurrentQueryName(name);                    
                    Globals.setCurrentQueryAbsolutePath(path);
                    builderPanel.addQuery(name, path);
                }
            }
        } finally {
            name = null;
        }

    }

}
