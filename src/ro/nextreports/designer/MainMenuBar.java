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

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ro.nextreports.designer.action.BackToParentAction;
import ro.nextreports.designer.action.ExitAction;
import ro.nextreports.designer.action.OpenFromServerAction;
import ro.nextreports.designer.action.OpenLayoutPerspectiveAction;
import ro.nextreports.designer.action.PublishAction;
import ro.nextreports.designer.action.SaveAction;
import ro.nextreports.designer.action.SaveAsAction;
import ro.nextreports.designer.action.chart.ImportChartAction;
import ro.nextreports.designer.action.chart.NewChartAction;
import ro.nextreports.designer.action.chart.NewChartFromQueryAction;
import ro.nextreports.designer.action.chart.OpenChartAction;
import ro.nextreports.designer.action.datasource.AddDataSourceAction;
import ro.nextreports.designer.action.datasource.ExportDataSourceAction;
import ro.nextreports.designer.action.datasource.ImportDataSourceAction;
import ro.nextreports.designer.action.favorites.FavoriteEntry;
import ro.nextreports.designer.action.favorites.FavoritesUtil;
import ro.nextreports.designer.action.favorites.ManageFavoritesAction;
import ro.nextreports.designer.action.favorites.OpenFavoriteAction;
import ro.nextreports.designer.action.help.AboutAction;
import ro.nextreports.designer.action.help.HelpManualAction;
import ro.nextreports.designer.action.help.HelpMovieAction;
import ro.nextreports.designer.action.help.HelpStartupAction;
import ro.nextreports.designer.action.query.ImportQueryAction;
import ro.nextreports.designer.action.query.NewQueryAction;
import ro.nextreports.designer.action.query.OpenQueryAction;
import ro.nextreports.designer.action.query.OpenQueryPerspectiveAction;
import ro.nextreports.designer.action.report.ImportReportAction;
import ro.nextreports.designer.action.report.NewReportAction;
import ro.nextreports.designer.action.report.NewReportFromQueryAction;
import ro.nextreports.designer.action.report.OpenReportAction;
import ro.nextreports.designer.action.report.ViewReportSqlAction;
import ro.nextreports.designer.action.report.WizardAction;
import ro.nextreports.designer.action.tools.BackupAction;
import ro.nextreports.designer.action.tools.ImportAction;
import ro.nextreports.designer.action.tools.Language;
import ro.nextreports.designer.action.tools.LanguageAction;
import ro.nextreports.designer.action.tools.RestoreAction;
import ro.nextreports.designer.action.tools.RestoreDockingAction;
import ro.nextreports.designer.action.tools.SettingsAction;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.template.report.action.ApplyTemplateAction;
import ro.nextreports.designer.template.report.action.CreateTemplateAction;
import ro.nextreports.designer.template.report.action.ExtractTemplateAction;
import ro.nextreports.designer.template.report.action.ModifyTemplateAction;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ShortcutsUtil;

import ro.nextreports.engine.exporter.ResultExporter;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.thoughtworks.xstream.XStream;

/**
 * @author Decebal Suiu
 */
public class MainMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	private AddDataSourceAction addDataSourceAction = new AddDataSourceAction(false);
    private ExportDataSourceAction exportDataSourceAction = new ExportDataSourceAction(false);
    private ImportDataSourceAction importDataSourceAction = new ImportDataSourceAction(false);
    private WizardAction wizardAction = new WizardAction(Globals.getMainFrame().getQueryBuilderPanel().getTree());
    private NewQueryAction newQueryAction = new NewQueryAction(false);
    private OpenQueryAction openQueryAction = new OpenQueryAction(false);
    private NewReportAction newReportAction = new NewReportAction(false);
    private NewReportFromQueryAction newReportFromQueryAction = new NewReportFromQueryAction(null, null, false, ResultExporter.DEFAULT_TYPE);
    private OpenReportAction openReportAction = new OpenReportAction(false);
    private OpenFromServerAction openFromServerAction = new OpenFromServerAction();
    private PublishAction publishAction = new PublishAction();
    private ExitAction exitAction = new ExitAction();
    private BackupAction backupAction = new BackupAction();
    private RestoreAction restoreAction = new RestoreAction();
    private ImportAction importAction = new ImportAction();
    private SettingsAction settingsAction = new SettingsAction();
    private ViewReportSqlAction viewReportSqlAction = new ViewReportSqlAction();
    private ImportReportAction importReportAction = new ImportReportAction(false);
    private ImportQueryAction importQueryAction = new ImportQueryAction(false);
    private ApplyTemplateAction applyTemplateAction = new ApplyTemplateAction(true);
    private ExtractTemplateAction extractTemplateAction = new ExtractTemplateAction();
    private OpenLayoutPerspectiveAction openLayoutPersAction = new OpenLayoutPerspectiveAction();
    private NewChartAction newChartAction = new NewChartAction(false);
    private OpenChartAction openChartAction = new OpenChartAction(false);
    private NewChartFromQueryAction newChartFromQueryAction = new NewChartFromQueryAction(null, false);
    private ImportChartAction importChartAction = new ImportChartAction(false);
    private SaveAction saveAction = new SaveAction();
    private SaveAsAction saveAsAction = new SaveAsAction();
    private BackToParentAction backAction = new BackToParentAction();
    private JMenu menuFavorites = new JMenu(I18NSupport.getString("menu_favorites"));

    public MainMenuBar() {
        putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        add(createFileMenu());        
        add(createViewsMenu());
        add(createToolsMenu());
        add(createHelpMenu());
        actionUpdate(Globals.getConnection() != null);
        Globals.setMainMenuBar(this);
    }

    public void newChartActionUpdate() {
        publishAction.setEnabled(true);
    }

    public void newReportActionUpdate() {
        applyTemplateAction.setEnabled(true);
        extractTemplateAction.setEnabled(true);
        publishAction.setEnabled(true);
    }

    public void newQueryActionUpdate() {
        applyTemplateAction.setEnabled(false);
        extractTemplateAction.setEnabled(false);
        publishAction.setEnabled(false);
    }

    public void actionUpdate(boolean connected) {
        openQueryAction.setEnabled(connected);
        importQueryAction.setEnabled(connected);
        newReportAction.setEnabled(connected);
        newReportFromQueryAction.setEnabled(connected);
        openReportAction.setEnabled(connected);
        importReportAction.setEnabled(connected);
        openFromServerAction.setEnabled(connected);
        newChartAction.setEnabled(connected);
        openChartAction.setEnabled(connected);
        newChartFromQueryAction.setEnabled(connected);
        importChartAction.setEnabled(connected);
        saveAction.setEnabled(connected);
        saveAsAction.setEnabled(connected);
    }

    public void enableLayoutPerspective(boolean enable) {
        openLayoutPersAction.setEnabled(enable);
    }

    private JMenu createFileMenu() {
        JMenu mnu = new JMenu(I18NSupport.getString("menu.file"));
        mnu.setMnemonic(ShortcutsUtil.getMnemonic("menu.file.mnemonic", new Integer('F')));

        JMenu mnu1 = new JMenu(I18NSupport.getString("menu_new"));
        mnu1.add(addDataSourceAction);
        mnu1.add(newQueryAction);
        mnu1.add(newReportAction);
        mnu1.add(newReportFromQueryAction);
        mnu1.add(newChartAction);
        mnu1.add(newChartFromQueryAction);               
        mnu.add(mnu1);

        JMenu mnu2 = new JMenu(I18NSupport.getString("menu_open"));
        mnu2.add(openQueryAction);
        mnu2.add(openReportAction);
        mnu2.add(openChartAction);
        mnu2.addSeparator();
        backAction.setEnabled(false);
        mnu2.add(backAction);
        mnu.add(mnu2);

        JMenu mnu3 = new JMenu(I18NSupport.getString("menu_import"));
        if (!DefaultDataSourceManager.memoryDataSources()) {
            mnu3.add(importDataSourceAction);
        }
        mnu3.add(importQueryAction);
        mnu3.add(importReportAction);
        mnu3.add(importChartAction);
        mnu.add(mnu3);


        JMenu mnu4 = new JMenu(I18NSupport.getString("menu_export"));
        if (!DefaultDataSourceManager.memoryDataSources()) {
            mnu4.add(exportDataSourceAction);
            mnu.add(mnu4);
        }
        
        mnu.addSeparator();
        menuFavorites = new JMenu(I18NSupport.getString("menu_favorites"));
        recreateMenuFavorites();
        mnu.add(menuFavorites);

        mnu.addSeparator();
        mnu.add(saveAction);
        mnu.add(saveAsAction);
        mnu.addSeparator();
        mnu.add(exitAction);
        newQueryActionUpdate();
        return mnu;
    }
    
    public void recreateMenuFavorites() {
    	menuFavorites.removeAll();
    	XStream xstream = FavoritesUtil.createXStream();			
 		List<FavoriteEntry> favorites = FavoritesUtil.loadFavorites(xstream);
 		for (FavoriteEntry fav : favorites) {
 			menuFavorites.add(new OpenFavoriteAction(fav));
 		}    
 		menuFavorites.addSeparator();
 		menuFavorites.add(new ManageFavoritesAction());
    }

    private JMenu createViewsMenu() {
        JMenu perspectiveMenu = new JMenu(I18NSupport.getString("menu.perpective"));
        perspectiveMenu.setMnemonic(ShortcutsUtil.getMnemonic("menu.perspective.mnemonic", new Integer('P')));

        JMenuItem item = new JMenuItem(new OpenQueryPerspectiveAction());
        perspectiveMenu.add(item);

        item = new JMenuItem(openLayoutPersAction);
        enableLayoutPerspective(false);
        perspectiveMenu.add(item);

        return perspectiveMenu;
    }

    private JMenu createHelpMenu() {
        JMenu mnu = new JMenu(I18NSupport.getString("menu.help"));
        mnu.setMnemonic(ShortcutsUtil.getMnemonic("menu.help.mnemonic", new Integer('H')));

        mnu.add(new HelpMovieAction());
        mnu.add(new HelpManualAction());
        mnu.add(new HelpStartupAction());
        mnu.addSeparator();
        mnu.add(new AboutAction());
        
        return mnu;
    }

    private JMenu createToolsMenu() {
        JMenu mnu = new JMenu(I18NSupport.getString("menu.tools"));
        mnu.setMnemonic(ShortcutsUtil.getMnemonic("menu.tools.mnemonic", new Integer('T')));
        mnu.add(wizardAction);                
        mnu.add(publishAction);
        mnu.add(openFromServerAction);
        mnu.add(viewReportSqlAction);
        mnu.addSeparator();

        JMenu mnu2 = new JMenu(I18NSupport.getString("menu_templates"));
        mnu2.add(new CreateTemplateAction());
        mnu2.add(new ModifyTemplateAction());
        mnu2.add(applyTemplateAction);
        mnu2.add(extractTemplateAction);
        mnu.add(mnu2);

        mnu.addSeparator();
        mnu.add(backupAction);
        mnu.add(restoreAction);
        mnu.add(importAction);
        mnu.add(settingsAction);
        mnu.addSeparator();
        mnu.add(new RestoreDockingAction());
        mnu.addSeparator();
        mnu.add(createLanguageMenu());
        return mnu;
    }

    private JMenu createLanguageMenu() {
        JMenu mnu = new JMenu(I18NSupport.getString("language"));
        mnu.setMnemonic(ShortcutsUtil.getMnemonic("menu.language.mnemonic", new Integer('L')));

        for (Language lang : LanguageAction.getLanguages()) {
        	mnu.add(new LanguageAction(lang.getLanguage()));     
        }
       
        return mnu;
    }
    
    public void enableBackAction(boolean enable) {
    	backAction.setEnabled(enable);
    }

}
