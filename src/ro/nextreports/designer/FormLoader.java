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

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.file.QueryFilter;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.util.converter.ConverterUtil;
import com.thoughtworks.xstream.XStream;

/**
 * @author Decebal Suiu
 */
public class FormLoader {

    private static Log LOG = LogFactory.getLog(FormLoader.class);

    //////////////////////
    // variables

    private static FormLoader instance = new FormLoader();

    //////////////////////
    // constructor

    public FormLoader() {
    }

    //////////////////////
    // business

    public static FormLoader getInstance() {
        return instance;
    }

    public void load() {
        askLoad();
    }

    public Report load(String path) {
        return load(path, true);
    }

    public Report load(String path, boolean setPath) {
    	
    	// convert xml if needed before load
        ConverterUtil.convertIfNeeded(path);
    	
        XStream xstream = XStreamFactory.createXStream();
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            fis = new FileInputStream(path);
            if (setPath) {
                Globals.setCurrentReportAbsolutePath(path);
            }
            reader = new InputStreamReader(fis, "UTF-8");
            return (Report) xstream.fromXML(reader);
        } catch (Exception e1) {
            e1.printStackTrace();
            LOG.error(e1.getMessage(), e1);
            return null;
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

    private void askLoad() {
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.showOpenDialog(Globals.getMainFrame());
        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null) {
            Globals.setCurrentReportAbsolutePath(selectedFile.getAbsolutePath());
            open(selectedFile);
        }
    }

    private Report open(File file) {
        OpenThread thread = new OpenThread(file);
//        thread.setContextClassLoader(classLoader);
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return thread.getReport();
    }

//    public List<String> getReportNames()  {
//        DataSourceManager manager = DefaultDataSourceManager.getInstance();
//        DataSource ds = manager.getConnectedDataSource();
//        List<String> result = new ArrayList<String>();
//        if (ds == null) {
//            return result;
//        }
//        File file = new File(FileReportPersistence.getConnectedDataSourceRelativePath());
//        String[] names = file.list();
//        if (names == null) {
//			return new ArrayList<String>();
//		}
//
//        for (int i=0, size=names.length; i<size; i++) {
////            System.out.println(names[i]);
//            int index = names[i].indexOf(FormSaver.REPORT_EXTENSION_SEPARATOR + FormSaver.REPORT_EXTENSION);
//            if (index != -1) {
//                if (names[i].endsWith(FormSaver.REPORT_EXTENSION_SEPARATOR + FormSaver.REPORT_EXTENSION)) {
//                    result.add(names[i].substring(0, index));
//                }
//            }
//        }
//        Collections.sort(result);
//        return result;
//    }

    public List<File> getFiles(String folderPath, String extension) {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        DataSource ds = manager.getConnectedDataSource();
        List<File> result = new ArrayList<File>();
        if (ds == null) {
            return result;
        }
        File file = new File(folderPath);
        File[] files = file.listFiles();
        if (files == null) {
            return new ArrayList<File>();
        }

        for (int i = 0, size = files.length; i < size; i++) {
//            System.out.println(names[i]);
            int index = files[i].getName().indexOf(extension);
            if (index != -1) {
                if (files[i].getName().endsWith(extension)) {
                    result.add(files[i]);
                }
            } else {
                if (file.isDirectory()) {
                    result.add(files[i]);
                }
            }
        }
        Collections.sort(result, new Comparator<File>() {
            public int compare(File o1, File o2) {
                if ((o1.isDirectory() && o2.isDirectory()) || (o1.isFile() && o2.isFile())) {
                    return Collator.getInstance().compare(o1.getName(), o2.getName());
                } else {
                    if (o1.isDirectory()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
        return result;
    }

    public int getReportCount() {
        return FileUtil.listFiles(new File(FileReportPersistence.getReportsRelativePath()),
                new QueryFilter(), true).size();


    }

    class OpenThread extends Thread {

        private File file;
        private Report report;

        public OpenThread(File file) {
            setName("NEXT : " + getClass().getSimpleName());
            this.file = file;
        }

        public void run() {
            try {
                openXStream();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        private void openXStream() throws Exception {
            XStream xstream = XStreamFactory.createXStream();
            Reader reader = new FileReader(file);
            report = (Report) xstream.fromXML(reader);
            reader.close();
        }

        public Report getReport() {
            return report;
        }

    }

}
