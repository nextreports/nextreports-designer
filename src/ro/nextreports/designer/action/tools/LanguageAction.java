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
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.MainFrame;
import ro.nextreports.designer.NextReports;
import ro.nextreports.designer.ReportDesignerPanel;
import ro.nextreports.designer.ReportUndoManager;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.action.query.NewQueryAction;
import ro.nextreports.designer.chart.ChartDesignerPanel;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.NotFoundException;
import ro.nextreports.designer.querybuilder.DesignerTablePanel;
import ro.nextreports.designer.querybuilder.tree.DBNodeExpander;
import ro.nextreports.designer.ui.tail.LogPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 1, 2008
 * Time: 1:56:30 PM
 */
public class LanguageAction extends AbstractAction {
    
    public static final String LANGUAGE_PROPERTY = "language";
    public static final String COUNTRY_PROPERTY = "country";
    
    public static final String LANGUAGE_ROMANIAN = "ro";
    public static final String COUNTRY_ROMANIAN = "RO";
    public static final String PROPERTY_NAME_ROMANIAN = LANGUAGE_PROPERTY + "." + LANGUAGE_ROMANIAN + "_" + COUNTRY_ROMANIAN;
    public static final String IMAGE_ROMANIAN = "flag-" + LANGUAGE_ROMANIAN + "_" + COUNTRY_ROMANIAN;
    
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String COUNTRY_ENGLISH = "US";
    public static final String PROPERTY_NAME_ENGLISH = LANGUAGE_PROPERTY + "." + LANGUAGE_ENGLISH + "_" + COUNTRY_ENGLISH;
    public static final String IMAGE_ENGLISH = "flag-" + LANGUAGE_ENGLISH + "_" + COUNTRY_ENGLISH;
        
    public static final String LANGUAGE_FRENCH = "fr";
    public static final String COUNTRY_FRENCH = "FR";
    public static final String PROPERTY_NAME_FRENCH = LANGUAGE_PROPERTY + "." + LANGUAGE_FRENCH + "_" + COUNTRY_FRENCH;
    public static final String IMAGE_FRENCH = "flag-" + LANGUAGE_FRENCH + "_" + COUNTRY_FRENCH;
    
    public static final String LANGUAGE_ITALIAN = "it";
    public static final String COUNTRY_ITALIAN = "IT";
    public static final String PROPERTY_NAME_ITALIAN = LANGUAGE_PROPERTY + "." + LANGUAGE_ITALIAN + "_" + COUNTRY_ITALIAN;
    public static final String IMAGE_ITALIAN = "flag-" + LANGUAGE_ITALIAN + "_" + COUNTRY_ITALIAN;

    public static final List<Language> languages = new ArrayList<Language>();		
    
    static {
    	languages.add(new Language(LANGUAGE_ENGLISH, COUNTRY_ENGLISH, PROPERTY_NAME_ENGLISH, IMAGE_ENGLISH));
		languages.add(new Language(LANGUAGE_FRENCH, COUNTRY_FRENCH, PROPERTY_NAME_FRENCH, IMAGE_FRENCH));
		languages.add(new Language(LANGUAGE_ROMANIAN, COUNTRY_ROMANIAN, PROPERTY_NAME_ROMANIAN, IMAGE_ROMANIAN));
		languages.add(new Language(LANGUAGE_ITALIAN, COUNTRY_ITALIAN, PROPERTY_NAME_ITALIAN, IMAGE_ITALIAN));
		
		for (String name : I18NSupport.getUserI18NFiles()) {						
			String baseName = name.substring(0, name.indexOf(".properties"));
			String[] s = baseName.split("_");
			if (s.length == 3) {
				languages.add(new Language(s[1], s[2], LANGUAGE_PROPERTY + "." + s[1] + "_" + s[2], "flag-" + s[1] + "_" + s[2]));
			}
		}
    }

    private String language;
    private String country;
    private String image;

    public LanguageAction(String language) {
    	    	
        int languageIndex = 0;
        for  (int i=0; i<languages.size(); i++) {
            if (languages.get(i).getLanguage().equals(language)) {
                languageIndex = i;
                break;
            }
        }

        String name = I18NSupport.getString(languages.get(languageIndex).getProperty());
        this.language = languages.get(languageIndex).getLanguage();
        this.country = languages.get(languageIndex).getCountry();
        this.image = languages.get(languageIndex).getFlag();


        putValue(Action.NAME, name);
        ImageIcon icon = ImageUtil.getImageIcon(image);
        if (icon == null) {
        	icon = ImageUtil.getImageIcon("flag-na");
        }
        putValue(Action.SMALL_ICON, icon);
//        char mnemonic = language.toUpperCase().charAt(0);
//        putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));        
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyStroke.getKeyStroke(mnemonic).getKeyEventType(),
//                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, name);
        putValue(Action.LONG_DESCRIPTION, name);
    }

    public void actionPerformed(ActionEvent event) {

        NewQueryAction qa = new NewQueryAction();
        qa.actionPerformed(null);

        if (qa.executed()) {

            DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
            if (ds != null) {
                try {
                    DefaultDataSourceManager.getInstance().disconnect(ds.getName());
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
            LogPanel.stop();
            
            Locale locale = new Locale(language, country);
            I18NSupport.changeLocale(locale);
            Globals.getMainFrame().setVisible(false);
            DBNodeExpander.fetchNodeNames();
            DesignerTablePanel.fetchOrders();
            LayoutHelper.fetchDefaultReportTitle();
            ReportUndoManager.changeLocale();
            WorkspaceManager.getInstance().clear();
            ReportDesignerPanel reportDesignerPanel = new ReportDesignerPanel();
            reportDesignerPanel.initWorkspace();
            Globals.setReportDesignerPanel(reportDesignerPanel);
            ChartDesignerPanel chartDesignerPanel = new ChartDesignerPanel();
            chartDesignerPanel.initWorkspace();
            Globals.setChartDesignerPanel(chartDesignerPanel);
            MainFrame frame = NextReports.createMainFrame();
            Globals.setMainFrame(frame);
            saveLanguage();
            frame.setVisible(true);           
        }
    }

    public void saveLanguage() {
        ReporterPreferencesManager pm = ReporterPreferencesManager.getInstance();
        pm.storeParameter(LANGUAGE_PROPERTY, language);
        pm.storeParameter(COUNTRY_PROPERTY, country);
    }

    public void readLanguage() {
        ReporterPreferencesManager pm = ReporterPreferencesManager.getInstance();
        pm.setMainClass(NextReports.class);
        pm.setUserName(System.getProperty("user.name"));
        language = pm.loadParameter(LANGUAGE_PROPERTY);
        if (language == null) {
            language = LANGUAGE_ENGLISH;
        }
        country = pm.loadParameter(COUNTRY_PROPERTY);
        if (country == null) {
            country = COUNTRY_ENGLISH;
        }      
    }

    public void initLanguage() {
        readLanguage();
        Locale locale = new Locale(language, country);
        I18NSupport.changeLocale(locale);
    }
    
    public static List<Language> getLanguages() {
    	return languages;
    }


}
