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


import javax.swing.*;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.file.DataSourceFilter;

import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 7, 2008
 * Time: 1:50:58 PM
 */
public class DataSourceDialog extends BaseDialog {

    private DataSourcePanel dsPanel;

    public DataSourceDialog(DataSourcePanel dsPanel) {
        super(dsPanel, I18NSupport.getString("datasource.export.dialog.title"));
        this.dsPanel = dsPanel;
    }

    protected boolean ok() {

        List<DataSource> list = dsPanel.getSelectedDataSources();

        if (list.size() == 0) {
            Show.info(I18NSupport.getString("datasource.export.dialog.select"));
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new DataSourceFilter());
        chooser.setDialogTitle(I18NSupport.getString("datasource.export.dialog.save"));

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(DataSourceFilter.DS_EXTENSION)) {
                path += "." + DataSourceFilter.DS_EXTENSION;
            }
            if ((new File(path)).exists()) {                
                Object[] options = {I18NSupport.getString("optionpanel.yes"), I18NSupport.getString("optionpanel.no")};
                int option = JOptionPane.showOptionDialog(this,
                        I18NSupport.getString("datasource.export.dialog.file.exists"),
                        I18NSupport.getString("report.util.confirm"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);

                if (option == JOptionPane.YES_OPTION) {
                    DefaultDataSourceManager.getInstance().save(path, list);
                } else {
                    return false;
                }
            } else {
                DefaultDataSourceManager.getInstance().save(path, list);
            }
        }

        return true;
    }
}
