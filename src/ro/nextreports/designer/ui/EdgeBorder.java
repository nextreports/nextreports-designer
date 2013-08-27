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

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;

import javax.swing.border.Border;
import javax.swing.SwingConstants;

/**
 * @author Decebal Suiu
 */
public class EdgeBorder implements Border, SwingConstants {

    public static final int RAISED = 1;
    public static final int LOWERED = 2;

    protected int edge = NORTH;
    protected int lift = LOWERED;

    public EdgeBorder() {
        this(NORTH);
    }

    public EdgeBorder(int edge) {
        this.edge = edge;
    }

    public Insets getBorderInsets(Component component) {
        switch (edge) {
            case SOUTH:
                return new Insets(0, 0, 2, 0);
            case EAST:
                return new Insets(0, 2, 0, 0);
            case WEST:
                return new Insets(0, 0, 0, 2);
            default:
                return new Insets(2, 0, 0, 0);
        }
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component component, Graphics g, int x, int y,
                            int w, int h) {
        if (lift == RAISED) {
            g.setColor(component.getBackground().brighter());
        } else {
            g.setColor(component.getBackground().darker());
        }

        switch (edge) {
            case SOUTH:
                g.drawLine(x, y + h - 2, w, y + h - 2);
                break;
            case EAST:
                g.drawLine(x + w - 2, y, x + w - 2, y + h);
                break;
            case WEST:
                g.drawLine(x + 1, y, x + 1, y + h);
                break;
            default:
                g.drawLine(x, y, x + w, y);
        }

        if (lift == RAISED) {
            g.setColor(component.getBackground().darker());
        } else {
            g.setColor(component.getBackground().brighter());
        }

        switch (edge) {
            case SOUTH:
                g.drawLine(x, y + h - 1, w, y + h - 1);
                break;
            case EAST:
                g.drawLine(x + w - 1, y, x + w - 1, y + h);
                break;
            case WEST:
                g.drawLine(x + 1, y, x + 1, y + h);
                break;
            default:
                g.drawLine(x, y + 1, x + w, y + 1);
        }
    }

}
