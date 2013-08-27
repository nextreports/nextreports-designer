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
package ro.nextreports.designer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.sql.Connection;

import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.exporter.util.variable.Variable;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.exporter.ResultExporter;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.JexlException;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 18, 2008
 * Time: 2:42:33 PM
 */
public class ReportLayoutUtil {

    public static List<NameType> getAllColumnsForReport(Report report) throws Exception {
        return getAllColumnsForReport(report, DefaultDataSourceManager.getInstance().getConnectedDataSource());
    }

    public static List<NameType> getAllColumnsForReport(Report report, DataSource ds) throws Exception {

        String sql;
        if (report == null) {
            QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
            builderPanel.refreshSql();
            sql = builderPanel.getUserSql();
        } else {
            if (report.getSql() != null) {
                sql = report.getSql();
            } else {
                sql = report.getQuery().toString();
            }
        }
        return getAllColumnsForSql(report, sql, ds);
    }

    public static List<String> getAllColumnNamesForReport(Report report) throws Exception {
        return getAllColumnNamesForReport(report, DefaultDataSourceManager.getInstance().getConnectedDataSource());
    }

    public static List<String> getAllColumnNamesForReport(Report report, DataSource ds) throws Exception {

        String sql;
        if (report == null) {
            QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
            builderPanel.refreshSql();
            sql = builderPanel.getUserSql();
        } else {
            if (report.getSql() != null) {
                sql = report.getSql();
            } else {
                sql = report.getQuery().toString();
            }
        }
        return getAllColumnNamesForSql(report, sql, ds);
    }
    
    public static Set<String> getAllColumnNamesUsedByGroupes(ReportLayout layout) {    	
    	Set<String> columnNames = new HashSet<String>();
    	List<ReportGroup> groups = layout.getGroups();
        if ((groups != null) && (groups.size() > 0)) {           
            for (ReportGroup group : groups) {
                Integer index = Integer.parseInt(group.getName());
                columnNames.add(group.getColumn());                
            }            
        }
        return columnNames;
    }

    public static List<String> getAllColumnNamesForSql(Report report, String sql) throws Exception {
        return getAllColumnNamesForSql(report, sql, DefaultDataSourceManager.getInstance().getConnectedDataSource());
    }

    public static List<String> getAllColumnNamesForSql(Report report, String sql, DataSource dataSource) throws Exception {
        List<NameType> columns = getAllColumnsForSql(report, sql, dataSource);
        List<String> columnNames = new ArrayList<String>();
        for (NameType nt : columns) {
            columnNames.add(nt.getName());
        }
        return columnNames;
    }

    public static List<NameType> getAllColumnsForSql(Report report, String sql, DataSource dataSource) throws Exception {
        List<NameType> columns = new ArrayList<NameType>();
        Connection con = null;
        try {
            con = Globals.createTempConnection(dataSource);
            QueryUtil qu = new QueryUtil(con, Globals.getDialect());
            // get parameters definition from system
            Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
            if (report == null) {
                ParameterManager paramManager = ParameterManager.getInstance();
                List<String> paramNames = paramManager.getParameterNames();
                for (String paramName : paramNames) {
                    QueryParameter param = paramManager.getParameter(paramName);
                    if (param == null) {
                        throw new Exception(I18NSupport.getString("parameter.undefined", paramName));
                    }
                    params.put(paramName, param);
                }
            } else {
                for (QueryParameter param : report.getParameters()) {
                    params.put(param.getName(), param);
                }
            }
            columns = qu.getColumns(sql, params);
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return columns;
    }

    public static List<String> getAllColumnTypesForReport(String sql) throws Exception {
        List<String> columnTypes = new ArrayList<String>();
        Connection con = null;
        try {
            con = Globals.createTempConnection(
                    DefaultDataSourceManager.getInstance().getConnectedDataSource());
            QueryUtil qu = new QueryUtil(con, Globals.getDialect());
            // get parameters definition from system
            Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
            ParameterManager paramManager = ParameterManager.getInstance();
            List<String> paramNames = paramManager.getParameterNames();
            for (String paramName : paramNames) {
                QueryParameter param = paramManager.getParameter(paramName);
                if (param == null) {
                    throw new Exception(I18NSupport.getString("parameter.undefined", paramName));
                }
                params.put(paramName, param);
            }
            columnTypes = qu.getColumnTypes(sql, params);
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return columnTypes;
    }

    public static List<NameType> getAllColumnsForReport(String sql) throws Exception {
        List<NameType> columnTypes = new ArrayList<NameType>();
        Connection con = null;
        try {
            con = Globals.createTempConnection(
                    DefaultDataSourceManager.getInstance().getConnectedDataSource());
            QueryUtil qu = new QueryUtil(con, Globals.getDialect());
            // get parameters definition from system
            Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
            ParameterManager paramManager = ParameterManager.getInstance();
            List<String> paramNames = paramManager.getParameterNames();
            for (String paramName : paramNames) {
                QueryParameter param = paramManager.getParameter(paramName);
                if (param == null) {
                    throw new Exception(I18NSupport.getString("parameter.undefined", paramName));
                }
                params.put(paramName, param);
            }
            columnTypes = qu.getColumns(sql, params);
        } finally {
            if (con != null) {
                con.close();
            }
        }

        return columnTypes;
    }

    public static List<String> getColumnNames(List<NameType> columns) {
        List<String> names = new ArrayList<String>();
        if (columns != null) {
            for (NameType nt : columns) {
                names.add(nt.getName());
            }
        }
        return names;
    }

    public static String getColumnTypeForReportColumn(String sql, String column) throws Exception {
        Connection con = null;
        try {
            con = Globals.createTempConnection(
                    DefaultDataSourceManager.getInstance().getConnectedDataSource());
            QueryUtil qu = new QueryUtil(con, Globals.getDialect());
            // get parameters definition from system
            Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
            ParameterManager paramManager = ParameterManager.getInstance();
            List<String> paramNames = paramManager.getParameterNames();
            for (String paramName : paramNames) {
                QueryParameter param = paramManager.getParameter(paramName);
                if (param == null) {
                    throw new Exception(I18NSupport.getString("parameter.undefined", paramName));
                }
                params.put(paramName, param);
            }
            return qu.getColumnType(sql, params, column);
        } finally {
            if (con != null) {
                con.close();
            }
        }        
    }

    public static void setCurrentGroupIndex(ReportLayout layout) {
        List<ReportGroup> groups = layout.getGroups();
        if ((groups == null) || (groups.size() == 0)) {
            GroupIndexGenerator.resetCurrentIndex();
        } else {
            int lastIndex = 0;
            for (ReportGroup group : groups) {
                Integer index = Integer.parseInt(group.getName());
                if (index > lastIndex) {
                    lastIndex = index;
                }
            }
            GroupIndexGenerator.setCurrentIndex(lastIndex);
        }
    }   

    public static void resizeColumn(ReportGrid grid, int column, int size) {
        grid.setColumnWidth(column, size);
        int total = 0;
        List<Integer> columnsWidth = new ArrayList<Integer>();
        for (int i = 0, n = grid.getColumnCount(); i < n; i++) {
            total += grid.getColumnWidth(i);
            columnsWidth.add(grid.getColumnWidth(i));
        }
        LayoutHelper.getReportLayout().setColumnsWidth(columnsWidth);
    }

    public static void updateColumnWidth(ReportGrid grid) {
        List<Integer> columnsWidth = new ArrayList<Integer>();
        for (int i = 0, n = Globals.getReportGrid().getColumnCount(); i < n; i++) {
            columnsWidth.add(Globals.getReportGrid().getColumnWidth(i));
        }
        LayoutHelper.getReportLayout().setColumnsWidth(columnsWidth);
    }

     public static String getColumnType(String column) {
        String type = "java.lang.Double";
        String sql = Globals.getMainFrame().getQueryBuilderPanel().getUserSql();
        if (column != null) {
            try {
                type = getColumnTypeForReportColumn(sql, column);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    public static String getExpressionType(String expression)  {
        Object value = null;
        try {
            value = testExpression(expression);
        } catch (JexlException ex) {
            ex.printStackTrace();
            Show.error(ex);
        }

        String type = "java.lang.Double";
        if ((value instanceof String) || (value instanceof Boolean) || (value instanceof Date) )  {
            type = value.getClass().getCanonicalName();
        }
        return type;
    }

    public static boolean isValidExpression(String expression) {
        Object value = null;
        try {
            value = testExpression(expression);
        } catch (JexlException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isValidBooleanExpression(String expression) {
        Object value = null;
        try {
            value = testExpression(expression);
            if  ( !(value instanceof Boolean) ) {
                return false;
            }
        } catch (JexlException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private static Object testExpression(String expression) throws JexlException {
        Object value = null;

        JexlEngine jexl = new JexlEngine();
        Expression e = jexl.createExpression(expression);
        // create context with all variables, parameters and columns
        // make sure to replace spaces in column names (as in designer expression evaluator)
        JexlContext checkContext = new MapContext();

        // default values for variables
        for (Variable variable : VariableFactory.getVariables()) {
            Object obj = "test";
            if (variable.getName().equals(Variable.DATE_VARIABLE)) {
                obj = new Date();
            } else if ((variable.getName().equals(Variable.ROW_VARIABLE)) ||
                       (variable.getName().equals(Variable.GROUP_ROW_VARIABLE)) ) {
                obj = new Integer(1);
            }
            checkContext.set("$V_" + variable.getName(), obj);
        }
        // default values for parameters
        for (QueryParameter parameter : ParameterManager.getInstance().getParameters()) {
            Object obj = new Integer(1);
            if ("java.lang.String".equals(parameter.getValueClassName())) {
                obj = "test";
            } else if ("java.lang.Boolean".equals(parameter.getValueClassName())) {
                obj = Boolean.TRUE;
            } else if ("java.util.Date".equals(parameter.getValueClassName())) {
                obj = new Date();
            }
            checkContext.set("$P_" + parameter.getName(), obj);
        }

        // default values for columns
        try {
            List<NameType> columns = getAllColumnsForReport(Globals.getMainFrame().getQueryBuilderPanel().getUserSql());
            for (NameType nt : columns) {
                String columnName = nt.getName();
                String col = columnName.replaceAll("\\s", ResultExporter.SPACE_REPLACEMENT);
                Object obj = new Integer(1);
                if ("java.lang.String".equals(nt.getType())) {
                    obj = "test";
                } else if ("java.util.Date".equals(nt.getType())) {
                    obj = new Date();
                } else if ("java.lang.Boolean".equals(nt.getType())) {
                    obj = Boolean.TRUE;
                }
                checkContext.set("$C_" + col, obj);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        // default values for functions
        for (String function : LayoutHelper.getReportLayout().getFunctions()) {        	
        	checkContext.set("$F_" + function, new Integer(1));
        }

        value = e.evaluate(checkContext);

        return value;
    }

}
