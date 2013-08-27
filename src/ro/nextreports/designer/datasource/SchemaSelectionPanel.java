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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ListSelectionPanel;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 3:36:28 PM
 */
public class SchemaSelectionPanel extends JPanel {

    private DataSource dataSource;
    private ListSelectionPanel panel;

    private static final Log LOG = LogFactory.getLog(SchemaSelectionPanel.class);

    public SchemaSelectionPanel(DataSource dataSource) {
        this.dataSource = dataSource;

        List<String> schemas = new ArrayList<String>();
        Connection connection = null;
        String schemaName  = null;
        try {
            connection = Globals.createTempConnection(dataSource);
            schemas = Globals.getDBViewer().getSchemas(connection);
            schemaName = Globals.getDBViewer().getUserSchema(connection);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> visibleSchemas = DefaultSchemaManager.getInstance().getPersistedSchemas(dataSource.getName());
        if (visibleSchemas == null) {
            //visibleSchemas = schemas;
            visibleSchemas = new ArrayList<String>();
            boolean found = false;
            for (String schema : schemas) {
                if (schema.equals(schemaName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
               schemaName = DefaultDBViewer.NO_SCHEMA_NAME;
           }
            visibleSchemas.add(schemaName);            
        }
        List<String> nonVisibleSchemas = new ArrayList<String>();
        for (String schema : schemas) {
            if (!visibleSchemas.contains(schema)) {
                nonVisibleSchemas.add(schema);
            }
        }

        panel = new ListSelectionPanel(nonVisibleSchemas, visibleSchemas,
                I18NSupport.getString("schema.selection.schemas"),
                I18NSupport.getString("schema.selection.schemas.visible"), true, false);

        this.add(panel);
    }

    @SuppressWarnings("unchecked")
    public List<String> getVisibleSchemas() {
        return panel.getDestinationElements();
    }
}
