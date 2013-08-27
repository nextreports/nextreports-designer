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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import ro.nextreports.engine.ReleaseInfoAdapter;

import java.util.List;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 4:12:01 PM
 */
public class DefaultSchemaManager implements SchemaManager {

    public static final String SCHEMAS_FILE = "schemas.xml";
    private static DefaultSchemaManager instance;
    private static List<PersistedSchema> schemas;

    private static Log LOG = LogFactory.getLog(DefaultSchemaManager.class);

    public static DefaultSchemaManager getInstance() {
        if (instance == null) {
            instance = new DefaultSchemaManager();
        }
        return instance;
    }

    protected static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("schema", PersistedSchema.class);
        xstream.alias("schemas", PersistedSchemas.class);
        xstream.addImplicitCollection(PersistedSchemas.class, "list");
        xstream.aliasField("names", PersistedSchema.class, "schemas");
        xstream.useAttributeFor(PersistedSchemas.class, "version");   
        return xstream;
    }

    public List<String> getPersistedSchemas(String dbName) {
        List<PersistedSchema> schemas = getPersistedSchemas();
        for (PersistedSchema schema : schemas) {
            if (schema.getDbName().equals(dbName)) {
                return schema.getSchemas();
            }
        }
        return null;
    }
    
    public List<PersistedSchema> getPersistedSchemas() {
        if (schemas == null) {
            List<PersistedSchema> result = new ArrayList<PersistedSchema>();
            XStream xstream = createXStream();
            FileInputStream fis = null;
            InputStreamReader reader = null;
            try {
                fis = new FileInputStream(Globals.USER_DATA_DIR + "/" + SCHEMAS_FILE);
                reader = new InputStreamReader(fis, "UTF-8");
                PersistedSchemas ps = (PersistedSchemas)xstream.fromXML(reader);
                if (ps.getList() != null) {
                    result = ps.getList();
                }
            } catch (FileNotFoundException e1) {
                // nothing to do -> flist is empty
            } catch (Exception e1) {
                e1.printStackTrace();
                LOG.error(e1.getMessage(), e1);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            schemas = result;
        }
        return schemas;
    }

    public boolean isVisible(String dbName, String schema) {
        List<String> schemas = getPersistedSchemas(dbName);
        if (schemas == null) {
            return false;
        }
        for (String s : schemas) {
            if (s.equals(schema)) {
                return true;
            }
        }
        return false;
    }

    public boolean save(List<PersistedSchema> schemas) {
        XStream xstream = createXStream();
        FileOutputStream fos = null;
        boolean ok = true;
        try {
            File file = new File(Globals.USER_DATA_DIR + "/" + SCHEMAS_FILE);
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                try {
                   ok  = file.createNewFile();
                    // cannot create file
                    if (!ok) {
                        Show.error(I18NSupport.getString("schema.save.error", file));
                        return false;
                    }
                } catch (IOException e) {
                    Show.error(I18NSupport.getString("schema.save.error", file));
                    return false;
                }
            }
            PersistedSchemas ps = new PersistedSchemas(new ArrayList<PersistedSchema>(schemas), ReleaseInfoAdapter.getVersionNumber());
            xstream.toXML(ps, fos);
            fos.flush();
            DefaultSchemaManager.schemas = schemas;
            return true;       
        } catch (Exception e1) {
            e1.printStackTrace();
            LOG.error(e1.getMessage(), e1);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
