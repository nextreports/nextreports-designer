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
package ro.nextreports.designer.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 16-Oct-2009
// Time: 13:40:38

//
public class SteppedComboBox extends JComboBox {

    private int popupWidth;

    public SteppedComboBox() {
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }

    public SteppedComboBox(ComboBoxModel aModel) {
        super(aModel);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }

    public SteppedComboBox(final Object[] items) {
        super(items);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }

    public SteppedComboBox(Vector items) {
        super(items);
        setUI(new SteppedComboBoxUI());
        popupWidth = 0;
    }


    public void setPopupWidth(int width) {
        popupWidth = width;
    }

    public Dimension getPopupSize() {
        Dimension size = getSize();
        if (popupWidth < 1) popupWidth = size.width;
        return new Dimension(popupWidth, size.height);
    }
}

