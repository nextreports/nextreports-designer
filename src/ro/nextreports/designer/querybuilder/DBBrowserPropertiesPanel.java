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
package ro.nextreports.designer.querybuilder;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Decebal Suiu
 */
public class DBBrowserPropertiesPanel extends JPanel {

    public DBBrowserPropertiesPanel() {
        super();
        initUI();
    }

    private void initUI() {
        // create the layout
        double[] columns = {
                TableLayoutConstants.PREFERRED,
                TableLayoutConstants.FILL,
        };
        double[] rows = {
                TableLayoutConstants.PREFERRED,
                TableLayoutConstants.PREFERRED
        };
        TableLayout layout = new TableLayout(columns, rows);
        layout.setHGap(6);
        layout.setVGap(6);
        this.setLayout(layout);

        this.add(new JCheckBox("Show all schemas into object tree"), "0, 0, 1, 0");
        this.add(new JLabel("Object Filter"), "0, 1");
        this.add(new JTextField(25), "1, 1");
    }

}
