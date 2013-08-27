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


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 20, 2008
 * Time: 10:56:25 AM
 */
public class SchemaManagerUtil {

    private static Log LOG = LogFactory.getLog(SchemaManagerUtil.class);

    public static boolean addDefaultSchemaAsVisible(DataSource ds) {
    	if (ds == null) {
    		return true;
    	}
        DefaultSchemaManager schemaManager = DefaultSchemaManager.getInstance();
        List<PersistedSchema> all = schemaManager.getPersistedSchemas();
        Connection connection = null;
        try {
            connection = Globals.createTempConnection(ds);
            List<String> allSchemas = Globals.getDBViewer().getSchemas(connection);
            String schema;
            if ((allSchemas.size()==1) && allSchemas.get(0).equals(DefaultDBViewer.NO_SCHEMA_NAME)) {
                schema = DefaultDBViewer.NO_SCHEMA_NAME;
            } else {
                schema = Globals.getDBViewer().getUserSchema(connection);
                // there is no schema for current user
                if (!allSchemas.contains(schema)) {
                    schema = null;                    
                }
            }
            if ((schema != null) && !"".equals(schema) && !"%".equals(schema)) {
                PersistedSchema ps = new PersistedSchema();
                ps.setDbName(ds.getName());
                List<String> schemas = new ArrayList<String>();
                schemas.add(schema);
                ps.setSchemas(schemas);
                all.add(ps);
                schemaManager.save(all);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void deleteVisibleSchemas(DataSource ds) {
        DefaultSchemaManager schemaManager = DefaultSchemaManager.getInstance();
        List<PersistedSchema> all = schemaManager.getPersistedSchemas();
        boolean found = false;
        for (Iterator it = all.iterator(); it.hasNext();) {
            PersistedSchema ps = (PersistedSchema) it.next();
            if (ps.getDbName().equals(ds.getName())) {
                it.remove();
                found = true;
                break;
            }
        }
        if (found) {
            schemaManager.save(all);
        }
    }

    public static void renameVisibleSchemas(String oldDataSourceName, String newDataSourceName) {
        DefaultSchemaManager schemaManager = DefaultSchemaManager.getInstance();
        List<PersistedSchema> all = schemaManager.getPersistedSchemas();
        boolean found = false;
        for (PersistedSchema ps : all) {
            if (ps.getDbName().equals(oldDataSourceName)) {
                ps.setDbName(newDataSourceName);
                found = true;
                break;
            }
        }
        if (found) {
            schemaManager.save(all);
        }
    }

    public static void updateVisibleSchemas(String dataSourceName, List<String> visibleSchemas) {
        DefaultSchemaManager manager = DefaultSchemaManager.getInstance();
        PersistedSchema ps = new PersistedSchema();
        ps.setDbName(dataSourceName);
        ps.setSchemas(visibleSchemas);
        List<PersistedSchema> all = manager.getPersistedSchemas();
        boolean found = false;
        for (PersistedSchema schema : all) {
            if (schema.getDbName().equals(dataSourceName)) {
                found = true;
                schema.setSchemas(ps.getSchemas());
            }
        }
        if (!found) {
            all.add(ps);
        }
        manager.save(all);
    }

}
