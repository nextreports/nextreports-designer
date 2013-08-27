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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utilities class for favorites
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 15.05.2013
 */
public class FavoritesUtil {
	
	private static Log LOG = LogFactory.getLog(FavoritesUtil.class);
	
	private static final String FAVORITES_XML = "favorites.xml";
	
	public static final String FAV_REPORT = "report";
	public static final String FAV_CHART = "chart";
	
	public static XStream createXStream() {
		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("favorite", FavoriteEntry.class);
		return xstream;
	}
	
	public static List<FavoriteEntry> loadFavorites() {
		XStream xstream = createXStream();
		return loadFavorites(xstream);
	}
	
	@SuppressWarnings("unchecked")
	public static List<FavoriteEntry> loadFavorites(XStream xstream) {
		List<FavoriteEntry> favorites = new ArrayList<FavoriteEntry>();
		FileInputStream fis = null;
		InputStreamReader reader = null;
		try {
			fis = new FileInputStream(Globals.USER_DATA_DIR + "/" + FAVORITES_XML);
			reader = new InputStreamReader(fis, "UTF-8");
			favorites = (List<FavoriteEntry>) xstream.fromXML(reader);
		} catch (FileNotFoundException ex) {
			// nothing to do -> file is not created yet
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error(ex.getMessage(), ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		Collections.sort(favorites, new Comparator<FavoriteEntry>() {
			@Override
			public int compare(FavoriteEntry o1, FavoriteEntry o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());				
			}
		});
		return favorites;
	}
	
	public static void saveFavorites(List<FavoriteEntry> favorites) { 
		XStream xstream = createXStream();
		saveFavorites(xstream, favorites);
	}
	
	public static void saveFavorites(XStream xstream, List<FavoriteEntry> favorites) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Globals.USER_DATA_DIR + "/" + FAVORITES_XML);
			xstream.toXML(favorites, fos);
			Globals.getMainMenuBar().recreateMenuFavorites();
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
