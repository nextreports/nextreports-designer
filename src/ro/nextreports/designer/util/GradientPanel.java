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

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 5, 2006
 * Time: 4:06:54 PM
 */
public class GradientPanel extends JPanel {

    private Color color1;
    private Color color2;

    public GradientPanel() {
        this(Color.blue, Color.green);
    }

    public GradientPanel(Color c1, Color c2) {
        super();
        this.color1 = c1;
        this.color2 = c2;
    }

    public void setColor1(Color c1) {
        this.color1 = c1;
        repaint();
    }

    public void setColor2(Color c2) {
        this.color2 = c2;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        GradientPaint gradient = new GradientPaint(0, 0, color1, w, h, color2, true);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);
    }

}
