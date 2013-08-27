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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import ro.nextreports.designer.action.report.layout.export.ExportAction;
import ro.nextreports.designer.util.ClassPathUtil;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;


/**
 * @author Decebal Suiu
 */
public class Launcher {		

    public static void main(String[] args) {
    	
    	try {
			deployUserData();
		} catch (IOException e) {			
			e.printStackTrace();			
			return;
		}
    	
    	//System.setProperty("nextreports.user.data", ".");
    	System.setProperty("nextreports.user.data", System.getProperty("user.home") + "/.nextreports-" + ReleaseInfo.getVersion());
    	System.setProperty("log4j.configuration", "file:" + System.getProperty("nextreports.user.data") + "/config/log4j.properties");
        System.setProperty("nextreports.log", System.getProperty("nextreports.user.data") + "/logs/nextreports.log");
        System.setProperty("derby.stream.error.file", System.getProperty("nextreports.user.data") + "/logs/derby.log");        
        System.setProperty("spy.log", System.getProperty("nextreports.user.data") + "/logs/jdbc-spy.log");                
    	
    	String user = null;
    	String serverUrl = null;
    	String path = null;
        try {
        	if (args == null) {
        		System.out.println("NextReports passed arguments: null");
        	} else {
        		System.out.println("NextReports passed arguments: " + Arrays.asList(args) );        		
        		if (args.length == 1) {
        			String protocol = "nextreports://";
        			String pSign = "?";        			        			        			
        			String param = args[0];
        			int signIndex = param.indexOf(pSign);
        			if (param.startsWith(protocol) && (signIndex != -1)) {
        				serverUrl = param.substring(protocol.length(), signIndex);
        				String params = param.substring(signIndex + pSign.length());        				
        				user=getParameterValue("user=", params);
        				path=getParameterValue("ref=", params);
        				Globals.setServerUrl(serverUrl);
        				Globals.setServerUser(user);        				
        				path = path.replaceAll("%20", " ");
        				Globals.setServerPath(path);
        			}
        		}
        	}	
        	
            // load all jars from lib directory
            ClassPathUtil.addJars("lib");

            // add to classpath the folder where the report images will be copied
            new File(ExportAction.REPORTS_DIR).mkdirs();
            ClassPathUtil.addClasses(ExportAction.REPORTS_DIR);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }               
        
        

        // create the next reports
        final NextReports nextReports = NextReports.getInstance();

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {

        	@Override
            public void run() {
                nextReports.shutdown();
            }

        });
        
        // print some release info in log
        nextReports.printReleaseInfo();

        // init and start the next reports
        nextReports.init();
		nextReports.start();

         if (!nextReports.checkWrite()) {
            Show.error(I18NSupport.getString("write.error.full", Globals.USER_DATA_DIR));
        }         
    }     
    
    // parameters  "key1=val1&key2=val2"
    private static String getParameterValue(String parameterKey, String parameters) {
		String pAnd = "&";
		int index = parameters.indexOf(parameterKey);
		if (index == -1) {
			// not found
			return "";
		} else {			
			String s = parameters.substring(index + parameterKey.length());
			int andIndex = s.indexOf(pAnd);
			if (andIndex == -1) {
				// parameter is the last one in parameters string
				return s;
			} else {
				// parameter is followed by another parameter
				return s.substring(0, andIndex);
			}
		}
	}
    
    // /f means to delete without confirmation
    private static final void deleteRegistry(String location){
        try {           
            Process process = Runtime.getRuntime().exec("reg delete \"" + location + "\" /f");
            process.waitFor();            
        } catch (Exception e) {}
    }
            
    private static void deployUserData() throws IOException {    	
    	
    	// try to delete old nextreports url protocol (versions 5.1 and 5.2) from HKCU
    	// to allow new HKLM new protocol to be found
    	deleteRegistry("HKCU\\SOFTWARE\\CLASSES\\nextreports");
    	
    	String archiveName = "nextreports-designer-data-" + ReleaseInfo.getVersion();
    	String data_root = System.getProperty("user.home") + "/." + "nextreports-" + ReleaseInfo.getVersion();
		File dataRoot = new File(data_root);
		if (dataRoot.exists() && dataRoot.isDirectory()) {
			return;
		}				
		
		// create and populate the webroot folder
		dataRoot.mkdirs();
		
		
        InputStream input = Launcher.class.getResourceAsStream("/" + archiveName + ".zip");
        if (input == null) {
            // cannot restore the workspace
        	System.err.println("Resource '/" + archiveName + "' not found." );                 
            throw new IOException("Resource '/" + archiveName + "' not found." );
        }        
        
        // deployment
        System.out.println("Deployment mode - copy from jar (/" + archiveName + ".zip" + ")");
        ZipInputStream zipInputStream = new ZipInputStream(input);
        FileUtil.unzip(zipInputStream, data_root);
        
        // replace user home in Derby demo database path inside datasource.xml
        replaceUserHome(dataRoot + "/datasource.xml");
        
	}
    
    private static void replaceUserHome(String dataSourceFilePath) throws IOException {
    	String s = FileUtil.readFileAsString(dataSourceFilePath);
    	s = s.replaceAll(Pattern.quote("${user.home}"), Matcher.quoteReplacement(System.getProperty("user.home") + "/.nextreports-" + ReleaseInfo.getVersion()));
    	new File(dataSourceFilePath).delete();
    	FileUtil.createFile(dataSourceFilePath, s.getBytes());    	
    }

}
