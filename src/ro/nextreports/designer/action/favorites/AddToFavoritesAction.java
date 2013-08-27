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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import com.thoughtworks.xstream.XStream;

/**
 * Add to favorites
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 14.05.2013
 */
public class AddToFavoritesAction extends AbstractAction {
	
	private DBObject entityObject;

	public AddToFavoritesAction(DBObject entityObject) {
		putValue(Action.NAME, I18NSupport.getString("favorites.info"));
		putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("star"));
		putValue(Action.MNEMONIC_KEY, new Integer('F'));
		putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("favorites.info"));
		putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("favorites.info"));
		this.entityObject = entityObject;
	}

	public void actionPerformed(ActionEvent e) {
		if (entityObject != null) {
			XStream xstream = FavoritesUtil.createXStream();			
			List<FavoriteEntry> favorites = FavoritesUtil.loadFavorites(xstream);
			addFavorite(favorites);
			FavoritesUtil.saveFavorites(xstream, favorites);			
		}
	}
	
	private void addFavorite(List<FavoriteEntry> favorites) {
		FavoriteEntry fav = new FavoriteEntry();
		String type = (entityObject.getType() == DBObject.REPORTS) ? FavoritesUtil.FAV_REPORT : FavoritesUtil.FAV_CHART;
		fav.setType(type);
		fav.setName(entityObject.getName());
		fav.setPath(entityObject.getParentPath());
		DataSourceManager manager = DefaultDataSourceManager.getInstance();
		DataSource ds = manager.getConnectedDataSource();
		fav.setDataSource(ds.getName());
		favorites.add(fav);
	}

}
