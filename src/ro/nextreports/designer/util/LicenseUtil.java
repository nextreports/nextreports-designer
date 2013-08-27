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

/*
import com.asf.license.License;
import com.asf.license.LicenseManager;
import com.asf.license.LicenseNotFoundException;
import com.asf.license.LicenseException;
*/

import javax.swing.*;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Aug 13, 2008
 * Time: 11:48:23 AM
 */
public class LicenseUtil {

    public static final String TRIAL = "Trial";
    public static final String PERSONAL = "Personal";
    public static final String COMMERCIAL = "Commercial";

    public static boolean maxReportsReached() {
        if (FormLoader.getInstance().getReportCount() > Globals.getReports()) {
            JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("license.reports", Globals.getReports()));
            return true;
        } else {
            return false;
        }
    }

    public static boolean allowToAddAnotherReport() {
        if (FormLoader.getInstance().getReportCount() >= Globals.getReports()) {
            JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("license.reports", Globals.getReports()));
            return false;
        } else {
            return true;
        }
    }

    public static boolean maxDataSourcesReached() {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();        
        if (manager.getDataSources().size() > Globals.getDataSources()) {
            JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("license.datasources", Globals.getDataSources()));
            return true;
        } else {
            return false;
        }
    }

    public static boolean allowToAddAnotherDataSource() {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        if (manager.getDataSources().size() >= Globals.getDataSources()) {
            JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("license.datasources", Globals.getDataSources()));
            return false;
        } else {
            return true;
        }
    }

    public static String getEdition() {
        String result = "";
        /*
        try {
            License lic = LicenseManager.getInstance().getLicense();
            String edition = lic.getFeature("edition");
            int days = lic.getDaysTillExpire();
            if (LicenseUtil.TRIAL.equals(edition) || LicenseUtil.PERSONAL.equals(edition)) {
                result = " (" + edition + ")";
            }
        } catch (Exception e) {
            //nothing to do
        }
        */
        
        return result;
    }
}
