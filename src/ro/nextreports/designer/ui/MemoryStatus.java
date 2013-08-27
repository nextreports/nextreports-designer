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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

/**
 * @author Decebal Suiu
 */
public class MemoryStatus extends JComponent implements ActionListener {

    private static final String MEMORY_TEST_STRING = "9999/9999M";
    private static final Color PROGRESS_FOREGROUND = Color.DARK_GRAY;
//    private static final Color PROGRESS_BACKGROUND = Color.LIGHT_GRAY;
//    private static final Color PROGRESS_BACKGROUND = new Color(255, 210, 150);
    private static final Color PROGRESS_BACKGROUND = new Color(165, 180, 229);
    
    private LineMetrics lineMetrics;
    private Timer timer;

    public MemoryStatus() {
//        Font font = new JLabel().getFont();
        Font font = UIManager.getFont("Label.font");
        MemoryStatus.this.setFont(font);

        FontRenderContext fontRendererContext = new FontRenderContext(null, false, false);
        Rectangle2D bounds = font.getStringBounds(MEMORY_TEST_STRING, fontRendererContext);
        Dimension dimension = new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
        setPreferredSize(dimension);
        setMaximumSize(dimension);
        lineMetrics = font.getLineMetrics(MEMORY_TEST_STRING, fontRendererContext);        
        addMouseListener(new MouseHandler());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer = new Timer(2000, this);
        timer.start();
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    public void removeNotify() {
        timer.stop();
        ToolTipManager.sharedInstance().unregisterComponent(this);
        super.removeNotify();
    }

    @Override
    public String getToolTipText() {
        Runtime runtime = Runtime.getRuntime();
        int freeMemory = (int) (runtime.freeMemory() / 1024);
        int totalMemory = (int) (runtime.totalMemory() / 1024);
        int usedMemory = (totalMemory - freeMemory);
        Integer[] args = { new Integer(usedMemory), new Integer(totalMemory) };
        return I18NSupport.getString("memory") + " " + usedMemory/1024 + "M";
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return new Point(event.getX(), -20);
    }

    public void actionPerformed(ActionEvent event) {
        MemoryStatus.this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Insets insets = new Insets(0, 0, 0, 0);
//        MemoryStatus.this.getBorder().getBorderInsets(this);

        Runtime runtime = Runtime.getRuntime();
        int freeMemory = (int) (runtime.freeMemory() / 1024);
        int totalMemory = (int) (runtime.totalMemory() / 1024);
        int usedMemory = (totalMemory - freeMemory);

        int width = MemoryStatus.this.getWidth() - insets.left - insets.right;
//        int height = MemoryStatus.this.getHeight() - insets.top - insets.bottom - 1;
        int height = MemoryStatus.this.getHeight() - insets.top - insets.bottom;

        float fraction = ((float) usedMemory) / totalMemory;

        g.setColor(PROGRESS_BACKGROUND);
        g.fillRect(insets.left, insets.top, (int) (width * fraction), height);

        String str = (usedMemory / 1024) + "/" + (totalMemory / 1024) + "M";

        FontRenderContext fontRendererContext = new FontRenderContext(null, false, false);

        Rectangle2D bounds = g.getFont().getStringBounds(str, fontRendererContext);

        Graphics g2 = g.create();
        g2.setClip(insets.left, insets.top, (int) (width * fraction), height);
        g2.setColor(PROGRESS_FOREGROUND);
        g2.drawString(str, insets.left + (int) (width - bounds.getWidth()) / 2,
                (int) (insets.top + lineMetrics.getAscent()));

        g2.dispose();
        g2 = g.create();

        g2.setClip(insets.left + (int) (width * fraction), insets.top,
                MemoryStatus.this.getWidth() - insets.left
                        - (int) (width * fraction), height);

        g2.setColor(MemoryStatus.this.getForeground());

        g2.drawString(str, insets.left + (int) (width - bounds.getWidth()) / 2,
                (int) (insets.top + lineMetrics.getAscent()));

        g2.dispose();
    }

    class MouseHandler extends MouseAdapter {

    	@Override
        public void mousePressed(MouseEvent event) {
            if (event.getClickCount() == 2) {
                Memory.showMemoryDialog((JFrame) SwingUtilities.getWindowAncestor(MemoryStatus.this));
                repaint();
            }
        }

    }

}
