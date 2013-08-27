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

import java.util.List;

import javax.swing.JOptionPane;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;


public class FavoritesDialog extends BaseDialog {

    private FavoritesPanel favPanel;

    public FavoritesDialog(FavoritesPanel favPanel) {
        super(favPanel, I18NSupport.getString("favorites.remove.desc"));
        this.favPanel = favPanel;
        setOkText(I18NSupport.getString("listselectionpanel.remove"));
        setCloseText(I18NSupport.getString("optionpanel.cancel"));
    }

    protected boolean ok() {

        List<FavoriteEntry> list = favPanel.getSelectedFavorites();

        if (list.size() == 0) {
            Show.info(I18NSupport.getString("favorites.remove.select"));
            return false;
        }

        Object[] options = {I18NSupport.getString("optionpanel.yes"), I18NSupport.getString("optionpanel.no")};
        int option = JOptionPane.showOptionDialog(this,
                I18NSupport.getString("favorites.remove.confirm"),
                I18NSupport.getString("report.util.confirm"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);

        if (option == JOptionPane.YES_OPTION) {
            List<FavoriteEntry> favorites = FavoritesUtil.loadFavorites();
            favorites.removeAll(list);
            FavoritesUtil.saveFavorites(favorites);
        } else {
            return false;
        }

        return true;
    }
}
