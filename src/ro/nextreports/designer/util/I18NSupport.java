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


import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXDatePicker;

import ro.nextreports.designer.action.tools.LanguageAction;

/**
 * Created by IntelliJ IDEA. User: mihai.panaitescu Date: Aug 27, 2008 Time:
 * 4:15:35 PM
 */
public class I18NSupport {
	
	private static final Log LOG = LogFactory.getLog(I18NSupport.class);

	private static ResourceBundle resBundle = PropertyResourceBundle.getBundle("i18n/next-ui", new Locale(
			LanguageAction.LANGUAGE_ENGLISH, LanguageAction.COUNTRY_ENGLISH));

	public static String getString(String propertyName, Object... params) {
		String value = resBundle.getString(propertyName);
		if (params.length > 0) {
			return MessageFormat.format(value, params);
		} else {
			return value;
		}
	}

	public static void changeLocale(Locale locale) {
		resBundle = PropertyResourceBundle.getBundle("i18n/next-ui", locale);
		Locale.setDefault(locale);
		// any third-party components with internationalization must change
		// locale
		JXDatePicker.setDefaultLocale(locale);
	}

	// get i18n files added by user
	// must have both language and country like next-ui_<lang>_<country>.properties
	public static List<String> getUserI18NFiles() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		final String dir = "i18n/";
		final String bundlename = "next-ui";		
		URL url = loader.getResource(dir);				
		
		List<String> result = new ArrayList<String>();
		File root = new File(url.getFile());
		
		// run from java ide (i18n folder is found in classpath)
		if (root.exists()) {
			File[] files = root.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches("^" + bundlename + "(_\\w{2}(_\\w{2})?)?\\.properties$");
				}
			});
			for (File file : files) {
				result.add(file.getName());
			}
			
		// run from jar	(i18n folder in inside jar)
		} else {
			// find the jar where i18n folder is located
			CodeSource src = I18NSupport.class.getProtectionDomain().getCodeSource();			
			if (src != null) {
				URL jar = src.getLocation();
				try {
					// look for all i18n files with both language and country specified
					// (existing i18n files have only the language)
					ZipInputStream zip = new ZipInputStream(jar.openStream());
					ZipEntry entry = zip.getNextEntry();
					while (entry != null) {						
						if (entry.getName().startsWith(dir + bundlename)) {							
							if (entry.getName().split("_").length == 3) {
								result.add(entry.getName().substring(dir.length()));
								LOG.info("I18NSupport found a new i18n file: " + entry.getName().substring(dir.length()));
							}
						}
						zip.closeEntry();
						entry = zip.getNextEntry();
					}
					zip.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					LOG.error(ex.getMessage(), ex);
				}
			}			
		}		
		return result;				
	}
}
