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
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 27-Aug-2009
// Time: 16:22:37

//

import ro.nextreports.engine.exporter.ResultExporter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.tools.SettingsAction;

/* Rule.java is used by ScrollDemo.java. */

public class Rule extends JComponent {
    
    public static final int INCH = Toolkit.getDefaultToolkit().getScreenResolution();
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int SIZE = 25;

    public int orientation;
    public boolean isMetric;
    private int increment;
    private int units;

    public Rule(int o, boolean m) {
        orientation = o;
        isMetric = m;
        setIncrementAndUnits();
        // change unit with double click (modify settings file)
        addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String oldR = Globals.getRulerUnit();
					String newR;
					if (oldR.equals(Globals.UNIT_CM)) {
						newR = Globals.UNIT_IN;
					} else {
						newR = Globals.UNIT_CM;
					}
					Map<String, String> map = new HashMap<String, String>();
					map.put("ruler.unit", newR);
					SettingsAction.modify(map);
				}
			}        	
        });
    }

    public void setIsMetric(boolean isMetric) {
        this.isMetric = isMetric;
        setIncrementAndUnits();
        repaint();
    }

    private void setIncrementAndUnits() {
        if (isMetric) {
            units = (int) ((double) INCH / (double) 2.54); // dots per centimeter
            increment = units;
        } else {
            units = INCH;
            increment = units / 2;
        }
    }

    public boolean isMetric() {
        return this.isMetric;
    }

    public int getIncrement() {
        return increment;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, SIZE));
    }

    protected void paintComponent(Graphics g) {
        Rectangle drawHere = g.getClipBounds();

        // Fill clipping area with dirty brown/orange.
        g.setColor(new Color(200, 220, 220));
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Do the ruler labels in a small font that's black.
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(Color.black);

        // Some vars we need.
        int end = 0;
        int start = 0;
        int tickLength = 0;
        String text = null;

        // Use clipping bounds to calculate first and last tick locations.
        if (orientation == HORIZONTAL) {
            start = (drawHere.x / increment) * increment;
            end = (((drawHere.x + drawHere.width) / increment) + 1)
                    * increment;
        } else {
            start = (drawHere.y / increment) * increment;
            end = (((drawHere.y + drawHere.height) / increment) + 1)
                    * increment;
        }

        // Make a special case of 0 to display the number
        // within the rule and draw a units label.
        if (start == 0) {
            text = Integer.toString(0) + (isMetric ? " cm" : " in");
            tickLength = 6;
            if (orientation == HORIZONTAL) {
                g.drawLine(0, SIZE - 1, 0, SIZE - tickLength - 1);
                g.drawString(text, 2, SIZE - tickLength - 6);
            } else {
                g.drawLine(SIZE - 1, 0, SIZE - tickLength - 1, 0);
                g.drawString(text, 9, 10);
            }
            text = null;
            start = increment;
        }

        // ticks and labels
        for (int i = start; i < end; i += increment) {
            if (i % units == 0) {
                tickLength = 6;
                text = Integer.toString(i / units);
            } else {
                tickLength = 3;
                text = null;
            }

            if (tickLength != 0) {
                if (orientation == HORIZONTAL) {
                    g.drawLine(i, SIZE - 1, i, SIZE - tickLength - 1);
                    if (text != null)
                        g.drawString(text, i - 3, SIZE - tickLength - 6);
                } else {
                    g.drawLine(SIZE - 1, i, SIZE - tickLength - 1, i);
                    if (text != null)
                        g.drawString(text, 9, i + 5);
                }
            }
        }

        g.drawLine(0, SIZE, drawHere.width, SIZE);

        g.setColor(Color.RED);
        g.drawLine(ResultExporter.A4_PORTRAIT_PIXELS , 0, ResultExporter.A4_PORTRAIT_PIXELS, SIZE);
        g.drawLine(ResultExporter.A4_LANDSCAPE_PIXELS , 0, ResultExporter.A4_LANDSCAPE_PIXELS, SIZE);
    }
}
