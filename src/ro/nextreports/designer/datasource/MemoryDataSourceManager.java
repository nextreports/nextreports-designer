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

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.Show;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 13, 2008
 * Time: 11:50:00 AM
 */
public class MemoryDataSourceManager extends DefaultDataSourceManager {

    private static final Log LOG = LogFactory.getLog(MemoryDataSourceManager.class);

    // for memory data source there is no persistence
    public boolean save() {
        return true;
    }

    // for memory data source there is no import
    public List<DataSource> load(String file) {
        return new ArrayList<DataSource>();
    }

    // for memory data source there is no export
    public boolean save(String file, List<DataSource> sources) {
        return true;
    }

    // for memory data sources are loaded from a string parameter of the application
    public void load() {
        XStream xstream = createXStream();
        try {
            DataSources ds =  (DataSources)xstream.fromXML(datasourcesXml);
            sources = ds.getList();

            if (sources.size() > Globals.getDataSources()) {
                int toDelete = sources.size() - Globals.getDataSources();
                for (int i = 0; i < toDelete; i++) {
                    sources.remove(0);
                }
            }
            decryptSourcePasswords();

            for (DataSource s : sources) {
                (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + s.getName() + File.separator + FileReportPersistence.QUERIES_FOLDER)).mkdirs();
                (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + s.getName() + File.separator + FileReportPersistence.REPORTS_FOLDER)).mkdirs();
                (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + s.getName() + File.separator + FileReportPersistence.CHARTS_FOLDER)).mkdirs();
            }

        } catch (XStreamException e) {
        	LOG.error("datasourcesXml = '" + datasourcesXml + "'");
            LOG.error(e.getMessage(), e);
            Show.error(e);
        } catch (Exception ex) {
            LOG.error("datasourcesXml = '" + datasourcesXml + "'");
            LOG.error(ex.getMessage(), ex);
            Show.error(ex);
        }
    }

}
