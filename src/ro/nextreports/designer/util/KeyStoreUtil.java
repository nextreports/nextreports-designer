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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.wizpublish.PublishLoginWizardPanel;


public class KeyStoreUtil {
	
	public static final String KEYSTORE_FILE = Globals.USER_DATA_DIR + File.separator + "jssecacerts";
    public static final String KEYSTORE_PASS = "next";
    private static final Log LOG = LogFactory.getLog(PublishLoginWizardPanel.class);
	
	public static void setKeystore() {
		File file = new File(KEYSTORE_FILE);
		if (!file.exists()) {   
			OutputStream out = null;;
			try {
				KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
				ks.load(null, KEYSTORE_PASS.toCharArray());
				out = new FileOutputStream(KEYSTORE_FILE);
				ks.store(out, KEYSTORE_PASS.toCharArray());				
			} catch (Exception e) {
				LOG.error("Could not create keystore file : " + KEYSTORE_FILE,	e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}
		}
		System.setProperty("javax.net.ssl.trustStore", KEYSTORE_FILE);
	}

}
