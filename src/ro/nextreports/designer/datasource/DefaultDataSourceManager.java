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

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import ro.nextreports.engine.ReleaseInfoAdapter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.exception.*;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 15, 2006
 * Time: 2:24:47 PM
 */
public class DefaultDataSourceManager implements DataSourceManager {

    protected List<DataSource> sources;
    private String version = "";
    private static DefaultDataSourceManager instance;

    // parameter passed to application : contains the xml data sources as a string
    //
    // the application must be called like this :
    // next-reports.exe -J-Dnextreports.datasources="..."
    // where the value is the xml datasources content
    private static String DATASOURCES_PARAMETER = "nextreports.datasources";
    protected static String datasourcesXml = System.getProperty(DATASOURCES_PARAMETER);

    public static final String DATASOURCES_FILE = "datasource.xml";

    private static Log LOG = LogFactory.getLog(DefaultDataSourceManager.class);

    public static DefaultDataSourceManager getInstance() {
        if (instance == null) {
            if (datasourcesXml == null) {
                instance = new DefaultDataSourceManager();
            } else {
                instance = new MemoryDataSourceManager();
            }
        }
        return instance;
    }

    protected DefaultDataSourceManager() {
        sources = new ArrayList<DataSource>();
    }

    public void addDataSource(DataSource source) throws NonUniqueException {
        DataSource found = getDataSource(source.getName());
        if (found != null) {
            throw new NonUniqueException("A data source with name " + source.getName() + " already exists!");
        }
        sources.add(source);
    }

    public void modifyDataSource(DataSource oldSource, DataSource newSource) throws ModificationException, NonUniqueException {
        if ((oldSource == null) || (newSource == null)) {
            throw new IllegalArgumentException("Source parameters cannot be null.");
        }
        if (newSource.getStatus() == DataSourceType.CONNECTED) {
            throw new IllegalArgumentException("New Data Source " + newSource.getName() + " cannot have status connected!");
        }

        if (!oldSource.getName().toLowerCase().equals(newSource.getName().toLowerCase())) {
            DataSource found = getDataSource(newSource.getName());
            if (found != null) {
                throw new NonUniqueException("A datasource with name '" + newSource.getName() + "' exists.");
            }
        }

        DataSource source = getDataSource(oldSource.getName());
        if (source != null) {
            if (source.getStatus() == DataSourceType.CONNECTED) {
                throw new ModificationException("Data Source " + source.getName() + " is connected!");
            }
            source.setName(newSource.getName());
            source.setType(newSource.getType());
            source.setDriver(newSource.getDriver());
            source.setUrl(newSource.getUrl());
            source.setUser(newSource.getUser());
            source.setPassword(newSource.getPassword());
            source.setStatus(newSource.getStatus());
            source.setProperties(newSource.getProperties());
        }
    }

    public void removeDataSource(String name) throws NotFoundException {
        DataSource sourceFound = getDataSource(name);
        if (sourceFound != null) {
            sources.remove(sourceFound);
        } else {
            throw new NotFoundException("DataSource " + name + " not found.");
        }
    }

    public List<DataSource> getDataSources() {
        return sources;
    }

    public List<DataSource> getDataSources(String driver) {
        List<DataSource> result = new ArrayList<DataSource>();
        for (DataSource ds : sources) {
            if (ds.getDriver().equals(driver)) {
                result.add(ds);
            }
        }
        return result;
    }

    public DataSource getDataSource(String name) {
        if (name == null) {
            return null;
        }
        for (DataSource source : sources) {
            if (source.getName().toLowerCase().equals(name.toLowerCase())) {
                return source;
            }
        }
        return null;
    }


    public void connect(String name) throws ConnectionException, NotFoundException {

        DataSource source = getDataSource(name);
        if (source == null) {
            throw new NotFoundException("DataSource " + name + " not found.");
        }


        Connection c = Globals.getConnection();
        if (c != null) {
            throw new ConnectionException("There is a connection active!");
        }
        Globals.createConnection(source);
        source.setStatus(DataSourceType.CONNECTED);
        Globals.getMainFrame().setStatusBarMessage("<html>" + I18NSupport.getString("datasource.active") +
                " <b>" + source.getName() + "</b></html>");
    }

    public void disconnect(String name) throws NotFoundException {

        DataSource source = getDataSource(name);
        if (source == null) {
            throw new NotFoundException("DataSource " + name + " not found.");
        }
        Connection c;
        try {
            c = Globals.getConnection();
            if (c != null) {
                c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        } finally {
            c = null;
            Globals.setConnection(c);
            Globals.clearDialect();
            source.setStatus(DataSourceType.DISCONNECTED);
            Globals.getMainFrame().setStatusBarMessage("");
        }
    }

    public DataSource getConnectedDataSource() {
        for (DataSource source : sources) {
            if (source.getStatus() == DataSourceType.CONNECTED) {
                return source;
            }
        }
        return null;
    }

    protected static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("datasource", DataSource.class);
        xstream.alias("datasources", DataSources.class);
        xstream.addImplicitCollection(DataSources.class, "list");
        xstream.useAttributeFor(DataSources.class, "version");
        return xstream;
    }

    ///////   password encryption //////////////////////////////////////////
    private static final String key = "Encrypt";
    private static final String CHARSET_NAME = "UTF-8";

    private String encrypt(String plainText) throws EncryptionException {
        try {
            String enc = encryptDecrypt(plainText, 1);
            return new BASE64Encoder().encode(enc.getBytes(CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new EncryptionException("Encryption Failed!");
        }
    }

    private String decrypt(String cipherText) throws EncryptionException {
        try {
            byte[] bytes = new BASE64Decoder().decodeBuffer(cipherText);
            cipherText = new String(bytes, 0, bytes.length, CHARSET_NAME);
            return encryptDecrypt(cipherText, -1);
        } catch (IOException e) {
            throw new EncryptionException("Decryption Failed!");
        }
    }

    private String encryptDecrypt(String sourceText, int oper) {
        int keyLength = key.length();
        int sourceLength = sourceText.length();
        char[] result = new char[sourceLength];
        for (int i = 0; i < sourceLength; i++) {
            int keyPoz = i % keyLength;
            char currentSource = sourceText.charAt(i);
            char currentKey = key.charAt(keyPoz);
            int source = (int) currentSource % 128;            //get the ASCII code of the input char
            int key = (int) currentKey % 128;                  //get the ASCII code of the key char
            int newValue = (source + (oper * key)) % 128;      //add/substract them into a new ASCII code
            if (newValue < 0) {
                newValue = 128 + newValue;                     //if the result is <0 make it positive
            }
            result[i] = (char) newValue;                       //store the coresponding char
        }
        return new String(result);
    }

    private void encryptSourcePasswords() throws EncryptionException {
        for (DataSource source : sources) {
            try {
                source.setPassword(encrypt(source.getPassword()));
            } catch (EncryptionException ex) {
                throw new EncryptionException("Encryption failed for datasource " + source.getName() + " !");
            }
        }
    }

    protected void decryptSourcePasswords() throws EncryptionException {
        decryptSourcePasswords(sources);
    }

    private void decryptSourcePasswords(List<DataSource> sources) throws EncryptionException {
        for (DataSource source : sources) {
            try {
                source.setPassword(decrypt(source.getPassword()));
            } catch (EncryptionException ex) {
                throw new EncryptionException("Decryption failed for datasource " + source.getName() + " !");
            }
        }
    }
    //////////////////////////////////////////////////////////////////////

    public boolean save() {
        return save(Globals.USER_DATA_DIR + "/" + DATASOURCES_FILE, sources);
    }

    public boolean save(String file, List<DataSource> sources) {
        XStream xstream = createXStream();
        FileOutputStream fos = null;
        try {
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                try {
                    (new File(file)).createNewFile();
                } catch (IOException e) {
                    Show.error(I18NSupport.getString("datasource.save.error", file));
                    return false;
                }
            }
            encryptSourcePasswords();
            DataSources ds = new DataSources(new ArrayList<DataSource>(sources), ReleaseInfoAdapter.getVersionNumber());
            xstream.toXML(ds, fos);
            fos.flush();
            decryptSourcePasswords();
            return true;
        } catch (EncryptionException ex) {
            Show.error(ex);
            return false;
        } catch (Exception e1) {
            e1.printStackTrace();
            Show.error(e1);
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
    
    public void load() {
        XStream xstream = createXStream();
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            fis = new FileInputStream(Globals.USER_DATA_DIR + "/" + DATASOURCES_FILE);
            reader = new InputStreamReader(fis, "UTF-8");
            DataSources ds = (DataSources) xstream.fromXML(reader);
            sources = ds.getList();
            version = ds.getVersion();
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
        } catch (FileNotFoundException e1) {
            // nothing to do -> file is created when first data source is created
        } catch (EncryptionException ex) {
            Show.error(ex);
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
    }
    
    public List<DataSource> load(String file) {

        List<DataSource> result = new ArrayList<DataSource>();
        XStream xstream = createXStream();
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "UTF-8");
            DataSources ds = (DataSources) xstream.fromXML(reader);
            result = ds.getList();
            decryptSourcePasswords(result);
        } catch (FileNotFoundException e1) {
            Show.error(e1);
        } catch (EncryptionException ex) {
            Show.error(ex);
        } catch (Exception e1) {
            Show.error(I18NSupport.getString("import.data.source.error"));
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
        return result;
    }

    public static boolean memoryDataSources() {
        return datasourcesXml != null;
    }

    public String getVersion() {
        return version;
    }

    public String getVersion(String file) {        
        XStream xstream = createXStream();
        FileInputStream fis = null;
        InputStreamReader reader = null;
        String version = "";
        try {
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "UTF-8");
            try {
                DataSources ds = (DataSources) xstream.fromXML(reader);
                version = ds.getVersion();
            } catch (ClassCastException cce) {
                // older versions do not have version field!
            }
        } catch (FileNotFoundException e1) {
            Show.error(e1);
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
        return version;
    }
}
