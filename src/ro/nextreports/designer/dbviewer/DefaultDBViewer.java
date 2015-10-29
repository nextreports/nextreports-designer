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
package ro.nextreports.designer.dbviewer;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.DialectException;
import ro.nextreports.engine.querybuilder.sql.dialect.OracleDialect;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.ProcUtil;
import ro.nextreports.engine.util.ReportUtil;

import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryParameter;

import java.sql.*;
import java.util.*;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.dbviewer.common.*;
import ro.nextreports.designer.util.I18NSupport;

public class DefaultDBViewer implements DBViewer {

    public static final String NO_SCHEMA_NAME = "%";

    private static final Log LOG = LogFactory.getLog(DefaultDBViewer.class);

    public DBInfo getDBInfo(String schemaName, int mask) throws NextSqlException {
        Connection con;
        try {
            con = Globals.getConnection();
        } catch (Exception e) {
            throw new NextSqlException("Could not retrieve connection.");
        }
        return getDBInfo(schemaName, mask, con);
    }

    public DBInfo getDBInfo(int mask) throws NextSqlException {
        Connection con;
        try {
            con = Globals.getConnection();
        } catch (Exception e) {
            throw new NextSqlException("Could not retrieve connection.", e);
        }
        return getDBInfo(mask, con);
    }

    public DBInfo getDBInfo(int mask, Connection con) throws NextSqlException {

        String schemaName;

		try {
			schemaName = con.getMetaData().getUserName();

			/*
			 * As of version 7 there was no equivalent to the concept of
			 * "schema" in SQL Server. For DatabaseMetaData functions that
			 * include SCHEMA_NAME drivers usually return the user name that
			 * owns the table.
			 * 
			 * So for all databases in which schema name is different from the
			 * user name, we use the schema name as "%"
			 */

			boolean foundSchema = false;
			try {
				ResultSet schemas = con.getMetaData().getSchemas();
				while (schemas.next()) {
					String sch = schemas.getString("TABLE_SCHEM");
					if (schemaName.equals(sch)) {
						foundSchema = true;
						break;
					}
				}
			} catch (SQLException ex) {
				LOG.error(ex.getMessage(), ex);
				ex.printStackTrace();
			}

			if (!foundSchema) {
				schemaName = "%";
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
			throw new NextSqlException("Could not retrieve connection.", e);
		}

		return getDBInfo(schemaName, mask, con);
    }


    public DBInfo getDBInfo(String schemaName, int mask, Connection con) throws NextSqlException {

        String info = "";
        List<String> keywords = new ArrayList<String>();
        List<DBTable> tables = new ArrayList<DBTable>();
        List<DBProcedure> procedures = new ArrayList<DBProcedure>();
        Dialect dialect;


        try {
            dialect = DialectUtil.getDialect(con);
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new NextSqlException("Could not get Dialect.", ex);
        }


        try {
            DatabaseMetaData dbmd = con.getMetaData();

            if ((mask & DBInfo.INFO) == DBInfo.INFO) {
                StringBuffer sb = new StringBuffer();
                sb.append(I18NSupport.getString("database.product")).append(dbmd.getDatabaseProductName()).append("\r\n");
                sb.append(I18NSupport.getString("database.product.version")).append(dbmd.getDatabaseProductVersion()).append("\r\n");
                sb.append(I18NSupport.getString("database.driver.name")).append(dbmd.getDriverName()).append("\r\n");
                sb.append(I18NSupport.getString("database.driver.version")).append(dbmd.getDriverVersion()).append("\r\n");
                info = sb.toString();
            }

            if ((mask & DBInfo.SUPPORTED_KEYWORDS) == DBInfo.SUPPORTED_KEYWORDS) {
                StringTokenizer st = new StringTokenizer(dbmd.getSQLKeywords(), ",");
                while (st.hasMoreTokens()) {
                    keywords.add(st.nextToken());
                }
            }

            // Get a ResultSet that contains all of the tables in this database
            // We specify a table_type of "TABLE" to prevent seeing system tables,
            // views and so forth
            boolean tableMask = ((mask & DBInfo.TABLES) == DBInfo.TABLES);
            boolean viewMask = ((mask & DBInfo.VIEWS) == DBInfo.VIEWS);
            if (tableMask || viewMask) {
                String[] tableTypes;
                if (tableMask && viewMask) {
                    tableTypes = new String[]{"TABLE", "VIEW"};
                } else if (tableMask) {
                    tableTypes = new String[]{"TABLE"};
                } else {
                    tableTypes = new String[]{"VIEW"};
                }
                
                String pattern = tableMask ? Globals.getTableNamePattern() : Globals.getViewNamePattern();
                ResultSet allTables = dbmd.getTables(null, schemaName, pattern, tableTypes);
                try {
                    while (allTables.next()) {
                        String table_name = allTables.getString("TABLE_NAME");
                        String table_type = allTables.getString("TABLE_TYPE");

                        // discard recycle bin tables
                        String ignoreTablePrefix = dialect.getRecycleBinTablePrefix();
                        if ((table_name == null) ||
                                ((ignoreTablePrefix != null) && table_name.startsWith(ignoreTablePrefix))) {
                            continue;
                        }

                        if ((mask & DBInfo.INDEXES) == DBInfo.INDEXES) {
                            ResultSet indexList = null;
                            try {
                                // Get a list of all the indexes for this table
                                indexList = dbmd.getIndexInfo(null, schemaName, table_name, false, false);
                                List<DBIndex> indexes = new ArrayList<DBIndex>();
                                while (indexList.next()) {
                                    String index_name = indexList.getString("INDEX_NAME");
                                    String column_name = indexList.getString("COLUMN_NAME");
                                    if (!index_name.equals("null")) {
                                        DBIndex index = new DBIndex(index_name, column_name);
                                        indexes.add(index);
                                    }
                                }
                                DBTable table = new DBTable(schemaName, table_name, table_type, indexes);
                                tables.add(table);

                            } catch (SQLException e) {
                                throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
                            } finally {
                                closeResultSet(indexList);
                            }

                        } else {
                            DBTable table = new DBTable(schemaName, table_name, table_type);
                            tables.add(table);
                        }
                    }
                } catch (SQLException e) {
                    throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
                } finally {
                    closeResultSet(allTables);
                }

            }

            boolean procedureMask = ((mask & DBInfo.PROCEDURES) == DBInfo.PROCEDURES);
            if (procedureMask) {
            	String pattern = Globals.getProcedureNamePattern();
            	if (pattern == null) {
            		pattern = "%";
            	}
                ResultSet rs = dbmd.getProcedures(null, schemaName, pattern);
                try {
                    while (rs.next()) {
                        String spName = rs.getString("PROCEDURE_NAME");
                        int spType = rs.getInt("PROCEDURE_TYPE");
                        String catalog = rs.getString("PROCEDURE_CAT");
//                        System.out.println("Stored Procedure Name: " + spName);
//                        if (spType == DatabaseMetaData.procedureReturnsResult) {
//                            System.out.println("procedure Returns Result");
//                        } else if (spType == DatabaseMetaData.procedureNoResult) {
//                            System.out.println("procedure No Result");
//                        } else {
//                            System.out.println("procedure Result unknown");
//                        }
                        procedures.add(new DBProcedure(schemaName, catalog, spName, spType));
                    }
                } catch (SQLException e) {
                    throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
                } finally {
                    closeResultSet(rs);
                }
            }

        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
        }

        return new DBInfo(info, tables, procedures, keywords);
    }

    public List<DBColumn> getColumns(String schema, String table) throws NextSqlException, MalformedTableNameException {

        Connection con;
        List<DBColumn> columns = new ArrayList<DBColumn>();
        String schemaName;
        String escapedTableName;
        try {
            con = Globals.getConnection();
            if (schema == null) {
                schemaName = Globals.getConnection().getMetaData().getUserName();
            } else {
                schemaName = schema;
            }

            Dialect dialect = Globals.getDialect();
            if (dialect.isKeyWord(table)) {
                escapedTableName = dialect.getEscapedKeyWord(table);
            } else {
                escapedTableName = table;
            }

        } catch (Exception e) {
            throw new NextSqlException("Could not retrieve connection.", e);
        }

        ResultSet rs = null;
        Statement stmt = null;
        List<String> keyColumns = new ArrayList<String>();
        try {
            // primary keys
            DatabaseMetaData dbmd = con.getMetaData();
            rs = dbmd.getPrimaryKeys(null, schemaName, table);
            while (rs.next()) {
                keyColumns.add(rs.getString("COLUMN_NAME"));
            }
            closeResultSet(rs);

            // foreign keys
            rs = dbmd.getImportedKeys(null, schemaName, table);
            List<String> foreignColumns = new ArrayList<String>();
            HashMap<String, DBForeignColumnInfo> fkMap = new HashMap<String, DBForeignColumnInfo>();
            while (rs.next()) {
                String fkSchema = rs.getString("FKTABLE_SCHEM");
                String fkTable = rs.getString("FKTABLE_NAME");
                String fkColumn = rs.getString("FKCOLUMN_NAME");
                String pkSchema = rs.getString("PKTABLE_SCHEM"); 
                String pkTable = rs.getString("PKTABLE_NAME");
                String pkColumn = rs.getString("PKCOLUMN_NAME");
                DBForeignColumnInfo fkInfo = new DBForeignColumnInfo(fkSchema, fkTable, fkColumn,
                        pkSchema, pkTable, pkColumn);
                //System.out.println("fkInfo :  " + fkInfo);
                foreignColumns.add(fkColumn);
                fkMap.put(fkColumn, fkInfo);
            }
            closeResultSet(rs);
            
            // column names with index
            rs = dbmd.getIndexInfo(null, schemaName, table, false, true); 
            List<String> indexes = new ArrayList<String>();
            while (rs.next()) {
            	String indexName = rs.getString(9);
            	if (indexName != null) {
            		indexes.add(indexName);
            	}
            }
            closeResultSet(rs);

            DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource(); 
            String header = "";
            stmt = con.createStatement();
            try {
                // workaround if a table name contains spaces
                if (escapedTableName.indexOf(" ") != -1) {
                    escapedTableName = "\"" + escapedTableName + "\"";
                }
                String prefix = "";
                if (!NO_SCHEMA_NAME.equals(schemaName)) {
                    prefix = schemaName;
                }
                if (prefix.indexOf(" ") != -1) {
                    prefix = "\"" + prefix + "\"";
                }
                if (!"".equals(prefix)) {
                    prefix = prefix + ".";
                }
                                               
                if (ds.getDriver().equals(CSVDialect.DRIVER_CLASS)) {
                	header = (String)ds.getProperties().get("headerline");  
                	if (header == null) {
                		header = "";
                	}
                }
                if (header.isEmpty()) {
                	String s = "SELECT * FROM " + prefix + escapedTableName + " WHERE 1 = 0";
                	LOG.info("getColumns[ "+ s + "]");                
                    rs = stmt.executeQuery(s);
                } 
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new MalformedTableNameException(e);
            }
            
			if (header.isEmpty()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int col = 1; col <= columnCount; col++) {
					String name = rsmd.getColumnLabel(col);
					int length = rsmd.getColumnDisplaySize(col);
					int precision = rsmd.getPrecision(col);
					int scale = rsmd.getScale(col);
					boolean isPrimaryKey = false;
					boolean isForeignKey = false;
					boolean isIndex= false;
					if (keyColumns.contains(name)) {
						isPrimaryKey = true;
					}
					DBForeignColumnInfo fkInfo = null;
					if (foreignColumns.contains(name)) {
						isForeignKey = true;
						fkInfo = fkMap.get(name);
					}
					if (indexes.contains(name)) {
						isIndex = true;
					}
					DBColumn column = new DBColumn(schemaName, table, name, rsmd.getColumnTypeName(col), isPrimaryKey,
							isForeignKey, isIndex, fkInfo, length, precision, scale);
					columns.add(column);
				}
			} else {
				String columnTypes = (String)ds.getProperties().get("columnTypes");  
				String[] names = header.split(",");
				String[] types = new String[names.length];
				for (int i=0; i<types.length; i++) {
					types[i] = "String";
				}
				if ((columnTypes != null) && !columnTypes.isEmpty()) {					
					types = columnTypes.split(",");
				}
				for (int i=0; i<names.length; i++) {
					DBColumn column = new DBColumn(schemaName, table, names[i], types[i], false, false, false, null, 20, 0, 0);
					columns.add(column);
				}
				
			}
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return columns;
    }

    public List<DBProcedureColumn> getProcedureColumns(String schema, String catalog, String procedure) throws NextSqlException {
        Connection con;
        List<DBProcedureColumn> columns = new ArrayList<DBProcedureColumn>();
        String schemaName;

        try {
            con = Globals.getConnection();
            if (schema == null) {
                schemaName = Globals.getConnection().getMetaData().getUserName();
            } else {
                schemaName = schema;
            }
        } catch (Exception e) {
            throw new NextSqlException("Could not retrieve connection.", e);
        }

        ResultSet rs = null;
        Statement stmt = null;
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            rs = dbmd.getProcedureColumns(catalog, schema, procedure, null);
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                int returnType = rs.getShort("COLUMN_TYPE");
                String retType;
                if (DatabaseMetaData.procedureColumnIn == returnType) {
                    retType = ProcUtil.IN;
                } else if (DatabaseMetaData.procedureColumnOut == returnType) {
                    retType = ProcUtil.OUT;
                } else if (DatabaseMetaData.procedureColumnInOut == returnType) {
                    retType = ProcUtil.INOUT;
                } else if (DatabaseMetaData.procedureColumnReturn == returnType) {
                    retType = ProcUtil.VAL;
                } else {
                    retType = ProcUtil.OTHER;
                }
                String dataType = rs.getString("TYPE_NAME");
                int length = rs.getInt("LENGTH");
                int precision = rs.getInt("PRECISION");
                int scale = rs.getInt("SCALE");
                DBProcedureColumn col = new DBProcedureColumn(schema, procedure, name, retType, dataType,
                        length, precision, scale);
                columns.add(col);
            }
            return columns;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public boolean isValidProcedure(DBProcedure proc) {
        try {
            List<DBProcedureColumn> columns = getProcedureColumns(proc.getSchema(), proc.getCatalog(), proc.getName());
            return isValidProcedure(columns);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidProcedure(List<DBProcedureColumn> columns) {
        try {
            Dialect dialect = Globals.getDialect();
            int out = 0;
            for (DBProcedureColumn col : columns) {
                if (ProcUtil.IN.equals(col.getReturnType()) || ProcUtil.VAL.equals(col.getReturnType())) {

                } else if (ProcUtil.OUT.equals(col.getReturnType())) {
                    if (dialect instanceof OracleDialect) {
                        if (ProcUtil.REF_CURSOR.equals(col.getDataType())) {
                            out++;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if ((out != 1) && (dialect instanceof OracleDialect)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<IdName> getColumnValues(String schema, String table, String columnName, String shownColumnName, byte orderBy) throws NextSqlException {
        return getColumnValues(Globals.getConnection(), schema, table, columnName, shownColumnName, orderBy);
    }

    public List<IdName> getColumnValues(Connection con, String schema, String table, String columnName, String shownColumnName, byte orderBy) throws NextSqlException {
        List<IdName> values = new ArrayList<IdName>();        
        try {
            values = ParameterUtil.getColumnValues(con, schema, table, columnName, shownColumnName, orderBy);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
        } catch (DialectException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("Dialect Exception: " + e.getMessage(), e);
        }
        return values;
    }
    
    public List<IdName> getValues(String select, boolean sort, byte orderBy) throws NextSqlException, InvalidSqlException {
        return getValues(Globals.getConnection(), select, sort, orderBy);
    }

    public List<IdName> getValues(Connection con, String select, boolean sort, byte orderBy) throws NextSqlException, InvalidSqlException {

        List<IdName> values = new ArrayList<IdName>();        
        try {
            values = ParameterUtil.getSelectValues(con, select, sort, orderBy);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("SQL Exception: " + e.getMessage(), e);
        } catch (DialectException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("Dialect Exception: " + e.getMessage(), e);
        }
        return values;
    }

    public ArrayList<Serializable> getDefaultSourceValues(Connection con, QueryParameter qp) throws NextSqlException {
        ArrayList<Serializable> result = new ArrayList<Serializable>();
        try {
           result = ParameterUtil.getDefaultSourceValues(con, qp);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("Exception: " + e.getMessage(), e);
        } 
        return result;
    }


    public DBColumn getColumn(String schema, String tableName, String columnName) throws NextSqlException {
        List<DBColumn> columns = null;
        try {
            columns = getColumns(schema, tableName);
        } catch (MalformedTableNameException e) {
            return null;
        }
        for (DBColumn column : columns) {
            if (column.getTable().equals(tableName) && column.getName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public String getUserSchema() throws NextSqlException {
        try {
            if (Globals.getConnection() == null) {
                return null;
            }
            return Globals.getConnection().getMetaData().getUserName();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("Could not retrieve schema name.", e);
        }
    }

    public String getUserSchema(Connection connection) throws NextSqlException {
        try {
            return connection.getMetaData().getUserName();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
            throw new NextSqlException("Could not retrieve schema name.", e);
        }
    }

    public List<String> getSchemas() throws NextSqlException {
        return getSchemas(Globals.getConnection());
    }

    public List<String> getSchemas(Connection connection) throws NextSqlException {
        List<String> schemas = new ArrayList<String>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getSchemas();
            while (rs.next()) {
                String schemaName = rs.getString("TABLE_SCHEM");
                schemas.add(schemaName);
            }
            if (schemas.size() == 0) {
                schemas.add(NO_SCHEMA_NAME);
            }
            return schemas;
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
        	e.printStackTrace();
            //throw new NextSqlException("Could not retrieve schema names.", e);
        	schemas.add("%");
        	return schemas;
        } finally {
            closeResultSet(rs);
        }
    }

    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    public DBColumn getPrimaryKeyColumn(DBColumn foreignKeyColumn) throws NextSqlException {
        DBColumn column = null;
        DBInfo info = getDBInfo(foreignKeyColumn.getSchema(), DBInfo.TABLES);
        List<DBTable> tables = info.getTables();
        for (DBTable table : tables) {

            List<DBColumn> columns = null;
            try {
                columns = getColumns(foreignKeyColumn.getSchema(), table.getName());
            } catch (MalformedTableNameException e) {
                // table with special characters
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
                continue;
            }
            for (DBColumn col : columns) {
                if (col.isForeignKey()) {
                    DBForeignColumnInfo fkInfo = col.getFkInfo();
                    if (fkInfo.getFkTable().equals(foreignKeyColumn.getTable()) &&
                            fkInfo.getFkColumn().equals(foreignKeyColumn.getName())) {
                        column = new DBColumn(foreignKeyColumn.getSchema(), fkInfo.getPkTable(), fkInfo.getPkColumn(), foreignKeyColumn.getType(),
                                true, false, false, fkInfo, foreignKeyColumn.getLength(), foreignKeyColumn.getPrecision(), foreignKeyColumn.getScale());
                        return column;
                    }
                }
            }
        }
        return column;
    }

    // here we get foreign keys just from the tables from the same schema !!!
    // getDBInfo(null, DBInfo.TABLES) may take too long (so let it be like this from now)
    public List<DBColumn> getForeignKeyColumns(DBColumn primaryKeyColumn) throws NextSqlException {
        List<DBColumn> list = new ArrayList<DBColumn>();
        DBInfo info = getDBInfo(primaryKeyColumn.getSchema(), DBInfo.TABLES);
        List<DBTable> tables = info.getTables();
        for (DBTable table : tables) {
            List<DBColumn> columns = null;
            try {
                columns = getColumns(table.getSchema(), table.getName());
            } catch (MalformedTableNameException e) {
                // table with special characters
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
                continue;
            }
            for (DBColumn col : columns) {
                if (col.isForeignKey()) {
                    DBForeignColumnInfo fkInfo = col.getFkInfo();
                    if (fkInfo.getPkTable().equals(primaryKeyColumn.getTable()) &&
                            fkInfo.getPkColumn().equals(primaryKeyColumn.getName())) {
                        DBColumn column = new DBColumn(primaryKeyColumn.getSchema(), fkInfo.getFkTable(), fkInfo.getFkColumn(), primaryKeyColumn.getType(),
                                true, false, false, fkInfo, primaryKeyColumn.getLength(), primaryKeyColumn.getPrecision(), primaryKeyColumn.getScale());
                        list.add(column);
                    }
                }
            }
        }
        return list;
    }
    
    public String isValidSql(Report report) {
    	return ReportUtil.isValidSqlWithMessage(Globals.getConnection(), report);    	    	
    }               

}
