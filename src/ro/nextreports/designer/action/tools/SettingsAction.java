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
package ro.nextreports.designer.action.tools;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartWebServer;
import ro.nextreports.designer.config.SettingsDialog;
import ro.nextreports.designer.config.SettingsException;
import ro.nextreports.designer.config.SettingsPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MergeProperties;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 16-Jun-2009
// Time: 11:48:04

//
public class SettingsAction extends AbstractAction {
	
    public SettingsAction() {
        putValue(Action.NAME, I18NSupport.getString("settings.action"));
        Icon icon = ImageUtil.getImageIcon("settings");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('S'));
        //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("settings.action"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("settings.action"));
    }      

    public void actionPerformed(ActionEvent e) {
        SettingsPanel panel = new SettingsPanel();
        SettingsDialog dialog = new SettingsDialog(panel);
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            modify(createPropertiesMap(panel));
        }
    }

    private Map<String, String> createPropertiesMap(SettingsPanel panel) {
        Map<String, String> map = new HashMap<String, String>();
        try {
        	map.put("connection.timeout", String.valueOf(panel.getConTimeout()));
            map.put("query.timeout", String.valueOf(panel.getTimeout()));
            map.put("chart.webserver.port", String.valueOf(panel.getChartPort()));
            map.put("max.rows.checked", String.valueOf(panel.getMaxRowsChecked()));
            map.put("accessibility.html", String.valueOf(panel.getHTMLAccessibility()));
            map.put("oracle.net.tns_admin", panel.getOracleClientPath());
            map.put("font.directories", panel.getFontDirectories());
            map.put("A4.warning", String.valueOf(panel.getA4Warning()));
            map.put("csv.delimiter", String.valueOf(panel.getCsvDelimiter()));
            map.put("ruler.isVisible", String.valueOf(panel.isRulerVisible()));
            map.put("ruler.unit", String.valueOf(panel.getRulerUnit()));
            map.put("locale", String.valueOf(panel.getLocaleLanguageCountry()));
        } catch (SettingsException ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public static void modify(Map<String, String> map) {
    	String currentPath = Globals.USER_DATA_DIR;
        String newPropertiesPath = currentPath + File.separator + "config" + File.separator + "next-reports.properties";
        MergeProperties mp = new MergeProperties();
        File currentFile = new File(newPropertiesPath);
        mp.setFile(currentFile);
        mp.setDestinationFile(currentFile);        
        try {        	
            mp.execute(map);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // must reset config to reload next-reports properties
        Globals.resetConfig();
        // properties that are read from System.properties (must do a System.setProperty)
        Globals.setOracleClientPath();
        // recreate properies panel (depends on html accessibilty)
        Globals.getReportDesignerPanel().recreatePropertiesPanel();
        // to show/hide ruler
        Globals.getReportLayoutPanel().refresh();
        // set locale
        Globals.setLocale();
        // change max rows 
        Globals.getMainFrame().getQueryBuilderPanel().enableMaxCheck();
        // restart chart web server (port may be modified)
        ChartWebServer webServer = ChartWebServer.getInstance();
        if (webServer.isStarted()) {
            webServer.restart();
        }
    }               

}
