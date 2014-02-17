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

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.SaveAction;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.ConnectionException;

import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.ConnectionUtil;

//
public class MessageUtil {
	
	private static final Log LOG = LogFactory.getLog(MessageUtil.class);

    public static boolean showReconnect() {
        Dialect dialect = null;
        try {
            dialect = Globals.getDialect();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // current connection is broken
        if ((dialect  == null) || !ConnectionUtil.isValidConnection(Globals.getConnection(), dialect)) {                                   
            // try to create a new connection (if a connection pool is used, current connection can expire after some time)
            try {
            	LOG.warn(".... Connection was lost. Try to recreate the database connection.");
				Globals.createConnection(DefaultDataSourceManager.getInstance().getConnectedDataSource());
			} catch (ConnectionException e) {
				LOG.warn(".... Connection recreation was not possible.");
				LOG.error(e.getMessage(), e);				
				// connection is really down																
	            int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
	            		I18NSupport.getString("connection.broken"), 
	            		I18NSupport.getString("error"), 
	            		JOptionPane.OK_CANCEL_OPTION, 
	            		JOptionPane.ERROR_MESSAGE,
	            		null, 
	            		new String[]{ I18NSupport.getString("save"), I18NSupport.getString("base.dialog.close")},
	            		I18NSupport.getString("base.dialog.close"));
	            if (option != JOptionPane.OK_OPTION) {
	            	return true;
	            } else {
	            	// forced save (we should be able to save the report anyway to not lost the work)
	            	LOG.warn(".... Forced save was called.");
	                new SaveAction(true).actionPerformed(null);
	            }
				
				return true;
			}                                                                       
        }
        return false;
    }
}
