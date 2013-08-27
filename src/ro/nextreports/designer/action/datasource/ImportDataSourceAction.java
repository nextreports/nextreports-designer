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
package ro.nextreports.designer.action.datasource;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.SchemaManagerUtil;
import ro.nextreports.designer.datasource.exception.NonUniqueException;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.designer.util.file.DataSourceFilter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 7, 2008
 * Time: 2:39:17 PM
 */
public class ImportDataSourceAction extends AbstractAction {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

    public ImportDataSourceAction() {
        this(true);
    }

    public ImportDataSourceAction(boolean fullName) {
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("import.data.source"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("import.data.source.small"));
        }
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_import"));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("import.data.source.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("import.data.source.desc"));
    }

    public void actionPerformed(ActionEvent ev) {
        if (!LicenseUtil.allowToAddAnotherDataSource()) {
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(I18NSupport.getString("datasource.import.dialog.load"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new DataSourceFilter());
        int returnVal = fc.showOpenDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File f = fc.getSelectedFile();
            if (f != null) {

                Thread executorThread = new Thread(new Runnable() {

                    public void run() {

                        UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("import.data.source"));
                        activator.start();

                        try {
                            List<DataSource> list = DefaultDataSourceManager.getInstance().load(f.getAbsolutePath());
                            DataSourceManager manager = DefaultDataSourceManager.getInstance();

                            for (DataSource ds : list) {
                                if (!LicenseUtil.allowToAddAnotherDataSource()) {
                                    return;
                                }
                                // to avoid data source with the same name
                                if (manager.getDataSource(ds.getName()) != null) {
                                    ds.setName(ds.getName() + "_" + sdf.format(new Date()));
                                }
                                try {
                                    manager.addDataSource(ds);
                                } catch (NonUniqueException e) {
                                    e.printStackTrace();
                                }
                                Globals.getMainFrame().getQueryBuilderPanel().addDataSource(ds.getName());
                                manager.save();
                                SchemaManagerUtil.addDefaultSchemaAsVisible(ds);
                            }
                            if (list.size() > 0) {
                                Show.info(I18NSupport.getString("import.data.source.success"));
                            }
                        } finally {
                            if (activator != null) {
                                activator.stop();
                            }
                        }

                    }
                }, "NEXT : " + getClass().getSimpleName());
                executorThread.start();

            }
        }

    }

}
