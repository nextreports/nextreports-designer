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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.swing.*;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.querybuilder.sql.ParameterConstants;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.Table;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.persistence.TablePersistentObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.ParametersPanel;
import ro.nextreports.designer.querybuilder.QueryBrowserPanel;
import ro.nextreports.designer.ui.list.CheckListBox;
import ro.nextreports.designer.ui.sqleditor.EditorPanel;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.TableUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 9, 2008
 * Time: 3:36:11 PM
 */
public class QueryWizardPanel extends WizardPanel {

    private EditorPanel editor;
    private JLabel queryLabel;
    private QueryBrowserPanel queryPanel;
    private JLabel sqlLabel;
    private ParametersPanel parametersPanel;
    private EasySelectColumnsPanel easyPanel;
    private Dimension buttonDim = new Dimension(20, 20);
    private Dimension dim = new Dimension(150, 20);
    private Dimension comboDim = new Dimension(200, 20);
    private JRadioButton selectionRB = new JRadioButton(I18NSupport.getString("wizard.panel.query.selection"));
    private JRadioButton queryRB = new JRadioButton(I18NSupport.getString("wizard.panel.query.query"));
    private JRadioButton editRB = new JRadioButton(I18NSupport.getString("wizard.panel.query.edit"));
    private JLabel emptyLabel = new JLabel("");

    private static final Log LOG = LogFactory.getLog(QueryWizardPanel.class);

    public QueryWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",3,5) + I18NSupport.getString("wizard.panel.query.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.query.subtitle", 
                ParameterConstants.START_PARAM, ParameterConstants.END_PARAM ));
        WizardUtil.resetParameters();
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
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {

        String sql = editor.getText();
        if ("".equals(sql.trim())) {

            List<DBColumn> list = easyPanel.getSelectedColumns();
            if (list.size() == 0) {
                if (selectionRB.isSelected()) {
                    messages.add(I18NSupport.getString("wizard.panel.query.validate.column"));
                } else if (queryRB.isSelected()) {
                    messages.add(I18NSupport.getString("wizard.panel.query.validate.query"));
                } else {
                    messages.add(I18NSupport.getString("wizard.panel.query.validate.enter"));
                }
                return false;
            } else {
                SelectQuery selectQuery = new SelectQuery();
                for (DBColumn col : list) {
                    Table table = new Table(col.getTable());
                    table.setSchemaName(easyPanel.getSchemaName());
                    try {
                        Dialect dialect = DialectUtil.getDialect(Globals.getConnection());
                        table.setDialect(dialect);
                        selectQuery.setDialect(dialect);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        LOG.error(e.getMessage(), e);
                    }
                    Column column = new Column(table, col.getName(), TableUtil.getJavaTypeForColumn(col));
                    DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
                    if (ds.getDriver().equals(CSVDialect.DRIVER_CLASS)) {
                    	column.setUseTableName(false);                   
                    }
                    column.setOutput(true);
                    column.setfKey(col.isForeignKey());
                    column.setpKey(col.isPrimaryKey());
                    selectQuery.addColumn(column);
                    sql = selectQuery.toString();
                }                
            }
        }
        ParameterManager paramManager = ParameterManager.getInstance();
		Map<String, QueryParameter> parametersMap = paramManager.getParametersMap();
        try {
            checkSqlHasParametersDefined(sql, parametersMap);
        } catch (Exception e) {
            messages.add(e.getMessage());
            return false;
        }
                   
        context.setAttribute(WizardConstants.QUERY, new Query(sql));
        return true;
    }

    private List<TablePersistentObject> getTables(List<DBColumn> list) {
        List<TablePersistentObject> tables = new ArrayList<TablePersistentObject>();
        int x = 100;
        int y = 100;
        for (DBColumn col : list){
            TablePersistentObject tpo = new TablePersistentObject();
            Table table = new Table(col.getTable());
            table.setSchemaName(easyPanel.getSchemaName());
            try {
                table.setDialect(DialectUtil.getDialect(Globals.getConnection()));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                LOG.error(e.getMessage(), e);
            }
            tpo.setTable(table);
            tpo.setPoint(new Point(x + 250, 100));
            tpo.setDim(CheckListBox.tableDim);
            tables.add(tpo);
        }
        return tables;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        String entity = (String)context.getAttribute(WizardConstants.ENTITY);
        Integer reportType = (Integer)context.getAttribute(WizardConstants.REPORT_TYPE);
        if (WizardConstants.ENTITY_REPORT.equals(entity)) {
        	if (reportType.equals(ResultExporter.ALARM_TYPE) || reportType.equals(ResultExporter.INDICATOR_TYPE)) {
        		return new SelectOneColumnWizardPanel();
        	} else	if (reportType.equals(ResultExporter.DISPLAY_TYPE)) {
        		return new SelectTwoColumnsWizardPanel();
        	} else {
        		return new SelectColumnsWizardPanel();
        	}
        } else {
            return new SelectChartColumnsWizardPanel();
        }
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

    private void init() {
        setLayout(new BorderLayout());

        ButtonGroup bg = new ButtonGroup();
        bg.add(selectionRB);
        bg.add(queryRB);
        bg.add(editRB);
        selectionRB.setSelected(true);

        selectionRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selection();
            }
        });
        queryRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selection();
            }
        });
        editRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selection();
            }
        });

        editor = new EditorPanel();
        parametersPanel = new ParametersPanel();
        parametersPanel.setPreferredSize(new Dimension(120, 200));

        queryPanel = new QueryBrowserPanel() {
            protected void selection() {
                String name = queryPanel.getSelectedFilePath();
                if (queryPanel.querySelected()) {
                    ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                            Globals.getReportPersistenceType());
                    Report report = repPersist.loadReport(name);
                    context.setAttribute(WizardConstants.LOAD_REPORT, report);
                    String sql = report.getSql();
                    if (sql == null) {
                        sql = report.getQuery().toString();
                    }
                    editor.setText(sql);
                    parametersPanel.set(report.getParameters());
                } else {
                    context.setAttribute(WizardConstants.LOAD_REPORT, null);
                    editor.setText("");
                    parametersPanel.set(new ArrayList<QueryParameter>());
                }
            }
        };

        sqlLabel = new JLabel("<html><b>Sql</b></html>");

        easyPanel = new EasySelectColumnsPanel();

        JPanel qPanel = new JPanel(new GridBagLayout());

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        radioPanel.add(selectionRB);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(queryRB);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(editRB);

        qPanel.add(radioPanel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));

        queryLabel = new JLabel(I18NSupport.getString("query.name"));
        qPanel.add(queryLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));
        qPanel.add(queryPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 0, 0), 0, 0));


        qPanel.add(sqlLabel, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));
        qPanel.add(editor, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        qPanel.add(parametersPanel, new GridBagConstraints(3, 2, 1, 2, 0.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 5, 5, 5), 0, 0));
        qPanel.add(emptyLabel, new GridBagConstraints(0, 4, 4, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        qPanel.add(easyPanel, new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        selection();

        add(qPanel, BorderLayout.CENTER);
    }

    private void selection() {
        if (selectionRB.isSelected()) {
            editor.setText("");
            queryPanel.clearSelection();
            parametersPanel.set(new ArrayList<QueryParameter>());
            easyPanel.setVisible(true);
        } else {
            easyPanel.setVisible(false);
        }
        if (queryRB.isSelected()) {
            editor.setText("");
            easyPanel.resetSelectedColumns();
            queryLabel.setVisible(true);
            queryPanel.setVisible(true);
            emptyLabel.setVisible(true);
        } else {
            queryLabel.setVisible(false);
            queryPanel.setVisible(false);
            emptyLabel.setVisible(false);
        }
        if (editRB.isSelected()) {            
            queryPanel.clearSelection();
            editor.setText("");
            parametersPanel.set(new ArrayList<QueryParameter>());
            easyPanel.resetSelectedColumns();
            sqlLabel.setVisible(true);
            editor.setVisible(true);
            parametersPanel.setVisible(true);
        } else {
            sqlLabel.setVisible(false);
            editor.setVisible(false);
            parametersPanel.setVisible(false);
        }
    }

    public void checkSqlHasParametersDefined(String sql, Map<String,QueryParameter> params)
            throws Exception {
        // create the query object
        Query query = new Query(sql);

        // get parameter names
        String[] paramNames = query.getParameterNames();

        // execute query if no parameters
        if (paramNames.length == 0) {
            return;
        }

        for (String name : paramNames) {
             QueryParameter param = params.get(name);
                if (param == null) {
                    throw new Exception(I18NSupport.getString("run.parameter.not.defined", name));
                }
        }
    }


}

