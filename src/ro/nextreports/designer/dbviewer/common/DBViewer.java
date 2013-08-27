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
package ro.nextreports.designer.dbviewer.common;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.queryexec.IdName;
import ro.nextreports.engine.queryexec.QueryParameter;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 4, 2006
 * Time: 3:05:20 PM
 */
public interface DBViewer {

    public DBInfo getDBInfo(String schemaName, int mask) throws NextSqlException;

    public DBInfo getDBInfo(int mask) throws NextSqlException;

	public DBInfo getDBInfo(int mask, Connection con) throws NextSqlException;

	public List<DBColumn> getColumns(String schema, String table) throws NextSqlException, MalformedTableNameException;

    public List<DBProcedureColumn> getProcedureColumns(String schema, String catalog, String procedure) throws NextSqlException;

    public boolean isValidProcedure(DBProcedure proc);

    public boolean isValidProcedure(List<DBProcedureColumn> columns);
    
    public boolean isValidSql(Report report);       

    public DBColumn getPrimaryKeyColumn(DBColumn foreignKeyColumn) throws NextSqlException;

	public List<DBColumn> getForeignKeyColumns(DBColumn primaryKeyColumn) throws NextSqlException;

	public List<IdName> getColumnValues(String schema, String table, String columnName, String shownColumnName, byte orderBy)  throws NextSqlException;

	public List<IdName> getValues(String select, boolean sort, byte orderBy) throws NextSqlException, InvalidSqlException;

    public List<IdName> getColumnValues(Connection connection, String schema, String table, String columnName, String shownColumnName, byte orderBy)  throws NextSqlException;

	public List<IdName> getValues(Connection connection, String select, boolean sort, byte orderBy) throws NextSqlException, InvalidSqlException;

    public ArrayList<Serializable> getDefaultSourceValues(Connection con, QueryParameter qp) throws NextSqlException;

    public DBColumn getColumn(String schema, String tableName, String columnName) throws NextSqlException;

	public String getUserSchema() throws NextSqlException;

    public String getUserSchema(Connection connection) throws NextSqlException;

    public List<String> getSchemas() throws NextSqlException;

    public List<String> getSchemas(Connection connection) throws NextSqlException;
}
