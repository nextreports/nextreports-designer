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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

/**
 * @author Decebal Suiu
 */
public class EmptyComponent extends JComponent {

    ////////////////////////////
    // variables

    private Dimension dim;

    ////////////////////////////
    // constructor

    public EmptyComponent(int w, int h) {
        super();
        dim = new Dimension(w, h);
        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    }

    ////////////////////////////
    // business

    public Dimension getPreferredSize() {
        return dim;
    }

    public Dimension getMinimumSize() {
        return dim;
    }

}
