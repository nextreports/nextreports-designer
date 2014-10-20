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
package ro.nextreports.designer.datasource;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 7, 2008
 * Time: 1:40:12 PM
 */
public class DataSourcePanel extends JPanel {

    private JList list;
    private Dimension dim = new Dimension(300, 200);

    public DataSourcePanel() {

        setLayout(new GridBagLayout());
        DefaultListModel model = new DefaultListModel();
        List<DataSource> sources = DefaultDataSourceManager.getInstance().getDataSources();
        for (DataSource source : sources) {
            model.addElement(source);
        }

        list = new JList() {
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                DataSource ds = (DataSource)getModel().getElementAt(index);
                return ds.getUrl();
            }
        };

        list.setModel(model);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setCellRenderer(new ListRenderer());

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(dim);

        add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    }

    public List<DataSource> getSelectedDataSources() {
        List<DataSource> result = new ArrayList<DataSource>();
        List selected = list.getSelectedValuesList();
        for (Object sel : selected) {
            result.add((DataSource)sel);
        }
        return result;
    }

    class ListRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list,
                Object value,   // value to display
                int index,      // cell index
                boolean iss,    // is the cell selected
                boolean chf)    // the list and the cell have the focus
        {
            super.getListCellRendererComponent(list, value, index, iss, chf);
            DataSource ds = (DataSource)value;
            setText(ds.getName());
            return this;
        }
    }

}
