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
package ro.nextreports.designer.action.report;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import com.thoughtworks.xstream.XStream;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.sqleditor.Editor;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.file.ChartFilter;
import ro.nextreports.designer.util.file.NextFileFilter;
import ro.nextreports.designer.util.file.QueryFilter;
import ro.nextreports.designer.util.file.ReportFilter;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 16, 2007
 * Time: 11:37:56 AM
 */
public class ViewReportSqlAction extends AbstractAction {

    private static final Log LOG = LogFactory.getLog(ViewReportSqlAction.class);

    public ViewReportSqlAction() {
        putValue(Action.NAME, I18NSupport.getString("view.sql"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("report_view"));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("view.sql"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("view.sql"));
    }

    public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(I18NSupport.getString("select.next.reports.file"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new NextFileFilter());
        int returnVal = fc.showOpenDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (f != null) {

                String sql = null;
                String entityName = null;
                Object entity = null;
                FileInputStream fis = null;
                entityName = f.getName();
                try {
                    XStream xstream = XStreamFactory.createXStream();
                    fis = new FileInputStream(f);
                    entity = xstream.fromXML(fis);


                } catch (Exception ex) {
                    Show.error(ex);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                if (entityName.endsWith(QueryFilter.QUERY_EXTENSION) ||
                    entityName.endsWith(ReportFilter.REPORT_EXTENSION)) {

                    Report report = (Report) entity;
                    if (report.getSql() != null) {
                        sql = report.getSql();
                    } else if (report.getQuery() != null) {
                        SelectQuery query = report.getQuery();
                        try {
                            query.setDialect(DialectUtil.getDialect(Globals.getConnection()));
                        } catch (Exception ex) {
                            ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            LOG.error(ex.getMessage(), ex);
                        }
                        sql = query.toString();
                    }

                } else if (entityName.endsWith(ChartFilter.CHART_EXTENSION)) {
                    Chart chart = (Chart) entity;
                    if (chart.getReport().getSql() != null) {
                        sql = chart.getReport().getSql();
                    } else if (chart.getReport().getQuery() != null) {
                        SelectQuery query = chart.getReport().getQuery();
                        try {
                            query.setDialect(DialectUtil.getDialect(Globals.getConnection()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            LOG.error(ex.getMessage(), ex);
                        }
                        sql = query.toString();
                    }
                }

                Editor editor = new Editor();
                editor.setText(sql);
                editor.setPreferredSize(new Dimension(400, 400));
                JFrame frame = new JFrame(I18NSupport.getString("view.sql.info", entityName));
                frame.setIconImage(ImageUtil.getImageIcon("report_view").getImage());
                frame.setLayout(new GridBagLayout());
                frame.add(editor, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
                frame.pack();
                Show.centrateComponent(Globals.getMainFrame(), frame);
                frame.setVisible(true);

            }
        }


    }
}
