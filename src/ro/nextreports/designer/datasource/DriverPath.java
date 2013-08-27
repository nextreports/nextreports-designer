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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.util.ClassPathUtil;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 22, 2006
 * Time: 3:48:05 PM
 */
public class DriverPath {

	private static final Log LOG = LogFactory.getLog(DriverPath.class);
	
    private static File getFile() throws IOException {
        File file = new File("driverpath.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


    private static boolean findEntry(File file, String entry) {

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = input.readLine()) != null) {
                if (line.equals(entry)) {
                    LOG.debug("found entry : " + entry);
                    return true;
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                LOG.error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    public static void addEntry(String entry) throws IOException {
        File file = getFile();
        if (findEntry(file, entry)) {
            return;
        }
        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter(file, true));
            output.write(entry);
            output.write(System.getProperty("line.separator"));
        } finally {
            //flush and close both "output" and its underlying FileWriter
            if (output != null) {
                output.close();
            }
        }
    }

    public static void loadDrivers() throws IOException {
        try {
            ClassPathUtil.addJars(new File("jdbc-drivers"));
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        }
    }

}
