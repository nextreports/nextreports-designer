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
package ro.nextreports.designer.template.report;


import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 8, 2008
 * Time: 2:28:26 PM
 */
public class ChooserTemplatePreviewPanel extends JPanel implements PropertyChangeListener {

    private TemplatePreviewPanel panel;

    public ChooserTemplatePreviewPanel(File file) {
        panel = new TemplatePreviewPanel(file);
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 0));
        add(new JLabel(I18NSupport.getString("apply.template.preview")), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        panel.propertyChange(evt);
    }
}
