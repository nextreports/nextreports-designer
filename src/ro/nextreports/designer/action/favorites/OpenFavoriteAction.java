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
package ro.nextreports.designer.action.favorites;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.datasource.DataSourceConnectAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


public class OpenFavoriteAction extends AbstractAction {	

	private FavoriteEntry fav;

	public OpenFavoriteAction(FavoriteEntry fav) {
		putValue(Action.NAME, fav.getName());
		String image = fav.getType().equals(FavoritesUtil.FAV_REPORT) ? "report" : "chart";
		putValue(Action.SMALL_ICON, ImageUtil.getImageIcon(image));		
		putValue(Action.SHORT_DESCRIPTION, fav.getPath());
		putValue(Action.LONG_DESCRIPTION, fav.getPath());
		this.fav = fav;
	}

	public void actionPerformed(ActionEvent e) {						
								
		if (!favoriteExists(fav)) {
			return;
		}
		
		final String relativePath = getRelativePath(fav);

		final DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
		tree.selectNode(fav.getDataSource(), DBObject.DATABASE);
		DataSourceConnectAction ca = new DataSourceConnectAction(tree, tree.getSelectionPath()) {
			 // EDT
    	    protected void afterCreate() {
    	    	if  (fav.getType().equals(FavoritesUtil.FAV_REPORT)) {    				
    				Globals.getMainFrame().openSystemReport(fav.getName(), relativePath);
    			} else {			    				
    				Globals.getMainFrame().openSystemChart(fav.getName(), relativePath);
    			}	
    	    }
		};
		ca.actionPerformed(null);						
	}
	
	private String getRelativePath(FavoriteEntry fav) {
		String path = fav.getPath();
		String relativePath = null;
		if (!path.endsWith("Reports") && !path.endsWith("Charts")) {
			int index = path.indexOf("Charts");
			if (index != -1) {
				relativePath = path.substring(index + 7);
			} else {
				index = path.indexOf("Reports");
				if (index != -1) {
					relativePath = path.substring(index + 8);
				}
			}
		}
		return relativePath;
	}		
	
	private boolean favoriteExists(FavoriteEntry fav) {
		File file;
		if  (fav.getType().equals(FavoritesUtil.FAV_REPORT)) {
			file = new File(fav.getPath() + File.separator + fav.getName() + FormSaver.REPORT_FULL_EXTENSION);
		} else {
			file = new File(fav.getPath() + File.separator + fav.getName() + ChartUtil.CHART_FULL_EXTENSION);;
		}		
		if (!file.exists()) {
			Show.error(I18NSupport.getString("favorites.notfound"));
			return false;
		}
		return true;
	}

}
