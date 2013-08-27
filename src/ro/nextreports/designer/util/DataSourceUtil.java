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
package ro.nextreports.designer.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSources;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Decebal Suiu
 */
public class DataSourceUtil {

	public static void validateDataSourcesXml(String xml) throws Exception {
        XStream xstream = createXStream();
        try {
            DataSources dataSources = (DataSources) xstream.fromXML(xml);
            System.out.println("Found " + dataSources.getList().size() + " data sources");
            for (DataSource dataSource : dataSources.getList()) {
            	System.out.println(dataSource);
            }
        } catch (Throwable t) {
        	throw new Exception("Invalid data sources xml file", t);
        }
	}
	
	public static void validateDataSourcesFile(String file) throws Exception {
		validateDataSourcesXml(readTextFile(file));
	}
	
    public static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.alias("datasource", DataSource.class);
        xstream.alias("datasources", DataSources.class);
        xstream.addImplicitCollection(DataSources.class, "list");
        xstream.useAttributeFor(DataSources.class, "version");
        
        return xstream;
    }
    
    private static String readTextFile(String file) throws IOException {
    	StringBuffer buffer = new StringBuffer(1024);
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    			
    	char[] chars = new char[1024];
    	while((reader.read(chars)) > -1) {
    		buffer.append(String.valueOf(chars));	
    	}

    	reader.close();

    	return buffer.toString();
    }
    
    /*
    public static void main(String[] args) {
    	String xml = "<datasources><datasource><name>DMS_DIRECTSALES</name><type>Oracle</type><driver>oracle.jdbc.driver.OracleDriver</driver><url>jdbc:oracle:thin:@bari:1526:NPU2</url><user>DMS_DIRECTSALES</user><password>KVtWZ2xVZjpPVyQ=</password></datasource></datasources>";
    	try {
			DataSourceUtil.validateDataSourcesXml(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    */
	
}
