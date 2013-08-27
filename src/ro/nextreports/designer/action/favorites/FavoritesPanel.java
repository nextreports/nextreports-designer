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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Favorites Panel
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 15.05.2013
 */
public class FavoritesPanel extends JPanel {

    private JList list;
    private Dimension dim = new Dimension(300, 200);

    public FavoritesPanel() {

        setLayout(new GridBagLayout());
        DefaultListModel model = new DefaultListModel();
        List<FavoriteEntry> favorites = FavoritesUtil.loadFavorites();
        for (FavoriteEntry fav : favorites) {
            model.addElement(fav);
        }

        list = new JList() {
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                FavoriteEntry fav = (FavoriteEntry)getModel().getElementAt(index);
                return fav.getPath();
            }
        };

        list.setModel(model);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setCellRenderer(new FavoriteRenderer());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(dim);

        add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    }

    public List<FavoriteEntry> getSelectedFavorites() {
        List<FavoriteEntry> result = new ArrayList<FavoriteEntry>();
        Object[] selected = list.getSelectedValues();
        for (Object sel : selected) {
            result.add((FavoriteEntry)sel);
        }
        return result;
    }

    class FavoriteRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list,
                Object value,   // value to display
                int index,      // cell index
                boolean iss,    // is the cell selected
                boolean chf)    // the list and the cell have the focus
        {
            super.getListCellRendererComponent(list, value, index, iss, chf);
            FavoriteEntry fav = (FavoriteEntry)value;
            setText(fav.getName());
            return this;
        }
    }

}
