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

import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.ParameterValueSelectionDialog;
import ro.nextreports.designer.querybuilder.ParameterValueSelectionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import ro.nextreports.engine.queryexec.QueryParameter;

/**
 * User: mihai.panaitescu
 * Date: 17-Jun-2010
 * Time: 14:22:38
 */
public class ListAddPanel extends JPanel {

    private Dimension btnDim = new Dimension(20, 20);
    private Dimension scrDim = new Dimension(200, 150);
    private JScrollPane scrSrc = new JScrollPane();

    private QueryParameter parameter;
    private JXList valuesList;
    private DefaultListModel valuesModel;
    private JButton addButton;
    private JButton removeButton;

    public ListAddPanel(QueryParameter parameter) {
        this.parameter = parameter;
        init();
    }

    private void init() {

        setLayout(new GridBagLayout());

        valuesList = new JXList();
        valuesModel = new DefaultListModel();
        JScrollPane scroll = new JScrollPane();
        scroll.setPreferredSize(scrDim);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().add(valuesList, null);
        valuesList.setModel(valuesModel);
        addButton = new JButton(ImageUtil.getImageIcon("add"));
        addButton.setToolTipText(I18NSupport.getString("parameter.value.add"));
        addButton.setPreferredSize(btnDim);
        addButton.setMinimumSize(btnDim);
        addButton.setMaximumSize(btnDim);
        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                ParameterValueSelectionPanel panel = new ParameterValueSelectionPanel(parameter.getValueClassName());
                ParameterValueSelectionDialog dialog = new ParameterValueSelectionDialog(panel);

                dialog.pack();
                Show.centrateComponent(Globals.getMainFrame(), dialog);
                dialog.setVisible(true);
                if (dialog.okPressed()) {
                    List<Serializable> values = dialog.getValues();
                    boolean add = false;
                    for (Serializable value : values) {
                        if (!valuesModel.contains(value)) {
                            valuesModel.addElement(value);
                            add = true;
                        }
                    }
                    if (!add && (values.size() > 0)) {
                        Show.info(I18NSupport.getString("parameter.value.exists"));
                    } else {
                    	onAdd();
                    }
                }
            }

        });

        removeButton = new JButton(ImageUtil.getImageIcon("clear"));
        removeButton.setToolTipText(I18NSupport.getString("parameter.default.remove"));
        removeButton.setPreferredSize(btnDim);
        removeButton.setMinimumSize(btnDim);
        removeButton.setMaximumSize(btnDim);
        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int[] indices = valuesList.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    valuesModel.removeElementAt(indices[i]);
                }
                onRemove();
            }

        });

        add(scroll,
                new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        add(addButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 0), 0, 0));
        add(removeButton,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(0, 2, 5, 0), 0, 0));

    }

    public List getElements() {
        return Collections.list(valuesModel.elements());
    }

    public void setElements(List elements) {
        valuesModel.removeAllElements();
        for (Object element : elements) {
            valuesModel.addElement(element);
        }
    }
    
    protected void onAdd() {
	}  

    protected void onRemove() {
	}
}
