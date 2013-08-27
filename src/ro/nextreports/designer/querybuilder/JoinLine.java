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

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.WeakReference;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.JoinCriteria;

/**
 * @author Decebal Suiu
 * @author Mihai Dinca-Panaitescu
 */
public class JoinLine extends JComponent {

    private WeakReference<DBTableInternalFrame> firstIFrame;
    private int firstSelectedRow;
    private WeakReference<DBTableInternalFrame> secondIFrame;
    private int secondSelectedRow;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private JButton button = new JButton();
    private JoinCriteria joinCriteria;
    private Column firstColumn;
    private Column secondColumn;

    public JoinLine(final DBTableInternalFrame firstIFrame, int firstSelectedRow,
                    final DBTableInternalFrame secondIFrame, int secondSelectedRow) {
        this.firstIFrame = new WeakReference<DBTableInternalFrame>(firstIFrame);
        this.firstSelectedRow = firstSelectedRow;
        this.secondIFrame = new WeakReference<DBTableInternalFrame>(secondIFrame);
        this.secondSelectedRow = secondSelectedRow;
        this.firstColumn = firstIFrame.getColumn(firstSelectedRow);
        this.secondColumn = secondIFrame.getColumn(secondSelectedRow);
        // to be able to see the drawn line no matter how bigger the desktop is
        setSize(new Dimension(7000, 7000));
        button.setFocusPainted(false);
        add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JoinPropertiesPanel joinPanel = new JoinPropertiesPanel(JoinLine.this);
                JDialog dlg = new JoinPropertiesDialog(joinPanel);
                dlg.pack();
                dlg.setResizable(false);
                Show.centrateComponent(Globals.getMainFrame(), dlg);
                dlg.setVisible(true);
            }

        });

        // mouse right click event for removing a join
        button.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
               if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                   Point mousePoint = e.getPoint();
                   JPopupMenu popupMenu = new JPopupMenu();
                   JMenuItem menuItem = new JMenuItem(I18NSupport.getString("designer.remove.join"));
                   menuItem.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                           JoinLine line = JoinLine.this;
                           firstIFrame.getDesktopPane().removeJoinLine(line);
                       }

                   });
                   popupMenu.add(menuItem);
                   popupMenu.show(e.getComponent(), mousePoint.x, mousePoint.y);
               }
            }
        });
    }

    private void computeLineCoords() {
        DBTableInternalFrame leftIFrame = getFramesInXOrder()[0];
        int leftSelectedRow = getSelectedRowsInXOrder()[0];
        x1 = leftIFrame.getBounds().x + leftIFrame.getBounds().width;
        y1 = leftIFrame.getBounds().y + leftIFrame.getJoinY(leftSelectedRow);
        //System.out.println("(x1,y1)=" + x1 + "," + y1 + "  joinY=" +leftIFrame.getJoinY(leftSelectedRow) + " row=" + leftSelectedRow);

        DBTableInternalFrame rightIFrame = getFramesInXOrder()[1];
        int rightSelectedRow = getSelectedRowsInXOrder()[1];
        x2 = rightIFrame.getBounds().x;
        y2 = rightIFrame.getBounds().y + rightIFrame.getJoinY(rightSelectedRow);
        //System.out.println("(x2,y2)=" + x2 + "," + y2 + "  joinY=" +rightIFrame.getJoinY(rightSelectedRow) + " row=" + rightSelectedRow);
    }

    /**
     * Verifica daca <code>iFrame</code> participa in <code>join</code>
     *
     * @param iFrame
     *            o fereastra interna
     * @return <code>true</code> daca <code>iFrame</code> participa in
     *         <code>join</code>, <code>false</code> altfel
     */
    public boolean joinsInternalFrame(JInternalFrame iFrame) {
        if (firstIFrame.get() == iFrame) {
            return true;
        }

        if (secondIFrame.get() == iFrame) {
            return true;
        }

        return false;
    }

    public DBTableInternalFrame getFirstIFrame() {
        return firstIFrame.get();
    }

    public int getFirstSelectedRow() {
        return firstSelectedRow;
    }

    public DBTableInternalFrame getSecondIFrame() {
        return secondIFrame.get();
    }

    public int getSecondSelectedRow() {
        return secondSelectedRow;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        computeLineCoords();

        if (secondIFrame.get() == getFramesInXOrder()[0]) {
            int[] xPoints = { x1, x1 + 4, x1 + 4 };
            int[] yPoints = { y1, y1 - 3, y1 + 4 };
            g2d.fillPolygon(xPoints, yPoints, 3);
        }

        g2d.drawLine(x1, y1, x1 + 7, y1);
        g2d.drawLine(x1 + 7, y1, x2 - 7, y2);
        g2d.drawLine(x2 - 7, y2, x2, y2);

        if (secondIFrame.get() == getFramesInXOrder()[1]) {
            int[] xPoints = { x2 - 4, x2, x2 - 4 };
            int[] yPoints = { y2 - 3, y2, y2 + 4 };
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    protected void paintChildren(Graphics g) {
        button.setBounds((((Math.max(x1, x2) - Math.min(x1, x2)) / 2) + Math
                        .min(x1, x2)) - 5, (((Math.max(y1, y2) - Math.min(y1,
                        y2)) / 2) + Math.min(y1, y2)) - 5, 10, 10);
        super.paintChildren(g);
    }

    public void removeFromParent() {
        ((DBTablesDesktopPane) getParent()).removeJoinLine(this);
    }

    private DBTableInternalFrame[] getFramesInXOrder() {
        DBTableInternalFrame[] frames = new DBTableInternalFrame[2];

        // todo must be 'x + width' ?
        if (firstIFrame.get().getBounds().x < secondIFrame.get().getBounds().x) {
            frames[0] = firstIFrame.get();
            frames[1] = secondIFrame.get();
        } else {
            frames[0] = secondIFrame.get();
            frames[1] = firstIFrame.get();
        }

        return frames;
    }

    private int[] getSelectedRowsInXOrder() {
        int[] rows = new int[2];

        // todo must be 'x + width' ?
        if (firstIFrame.get().getBounds().x < secondIFrame.get().getBounds().x) {
            rows[0] = firstSelectedRow;
            rows[1] = secondSelectedRow;
        } else {
            rows[0] = secondSelectedRow;
            rows[1] = firstSelectedRow;
        }

        return rows;
    }

    public JoinCriteria getJoinCriteria() {
        return joinCriteria;
    }

    public void setJoinCriteria(JoinCriteria joinCriteria) {
        this.joinCriteria = joinCriteria;
    }

    public Column getFirstColumn() {
        return firstColumn;
    }

    public Column getSecondColumn() {
        return secondColumn;
    }

}
