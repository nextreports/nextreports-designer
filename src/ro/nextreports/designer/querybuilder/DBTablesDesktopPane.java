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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.persistence.TablePersistentObject;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.JoinCriteria;

/**
 * @author Decebal Suiu
 */
public class DBTablesDesktopPane extends JDesktopPane {

    public static final Dimension PREFFERED_SIZE = new Dimension(700, 500);

    private QueryBuilderPanel queryBuilderPanel;
    private List<JoinLine> joinLines = new ArrayList<JoinLine>();

    private boolean scale;
    private Image backgroundImage = null;

    public DBTablesDesktopPane(QueryBuilderPanel queryPanel) {
        this.queryBuilderPanel = queryPanel;
        setDesktopManager(new DBTablesDesktopManager());
        setPreferredSize(PREFFERED_SIZE);
        setBackground(Color.WHITE);
        setBackgroundImage();
        //Let the user scroll by dragging to outside the window.
        //setAutoscrolls(true); //enable synthetic drag events
    }

    public void tableColumnRemoved(String tableAlias, String tableName, String columnName) {
        JInternalFrame[] iFrames = getAllFrames();
        for (int i = 0; i < iFrames.length; i++) {
            DBTableInternalFrame iFrame = (DBTableInternalFrame) iFrames[i];
            String title = iFrame.getTitle();
            if (title.equals(tableAlias + " (" + tableName + ")")) {
                iFrame.tableColumnRemoved(columnName);
            }
        }
    }

    public void allTableColumnsRemoved() {
        JInternalFrame[] iFrames = getAllFrames();
        for (int i = 0; i < iFrames.length; i++) {
            ((DBTableInternalFrame) iFrames[i]).allTableColumnsRemoved();
        }
    }

    public void addJoinLine(JoinLine joinLine) {
        joinLines.add(joinLine);
        add(joinLine/*, new Integer(0)*/);

        // source
        Column firstColumn = joinLine.getFirstIFrame().getSelectedColumn();

        // destination
        Column secondColumn = joinLine.getSecondIFrame().getSelectedColumn();

        JoinCriteria jc = queryBuilderPanel.getSelectQuery().addJoin(firstColumn, secondColumn);
        joinLine.setJoinCriteria(jc);
    }

    public void addJoinLine(JoinLine joinLine, Column firstColumn, Column secondColumn) {
        joinLines.add(joinLine);
        add(joinLine/*, new Integer(0)*/);
        JoinCriteria jc = queryBuilderPanel.getSelectQuery().addJoin(firstColumn, secondColumn);
        joinLine.setJoinCriteria(jc);
    }

    public void addJoinLineWithCriteria(JoinLine joinLine) {
        JoinCriteria jc = joinLine.getJoinCriteria();
        if (jc == null) {
            throw new IllegalArgumentException("JoinLine has a null join criteria!");
        }
        joinLines.add(joinLine);
        add(joinLine);
        queryBuilderPanel.getSelectQuery().addJoin(jc);
    }

    public Collection<JoinLine> getJoinLines() {
        return joinLines;
    }

    public Collection<JoinLine> getJoinLinesForInternalFrame(JInternalFrame iFrame) {
        List<JoinLine> joins = new ArrayList<JoinLine>();

        for (JoinLine joinLine : joinLines) {
            if (joinLine.joinsInternalFrame(iFrame)) {
                joins.add(joinLine);
            }
        }

        return joins;
    }

    public void removeJoinLines(Collection<JoinLine> joinLines) {
        this.joinLines.removeAll(joinLines);
        for (JoinLine joinLine : joinLines) {
            remove(joinLine);
            queryBuilderPanel.getDesignPanel().removeJoin(joinLine.getJoinCriteria());
        }
        repaint();
    }

    public void removeAllJoinLines() {
        for (JoinLine joinLine : joinLines) {
            remove(joinLine);
            queryBuilderPanel.getDesignPanel().removeJoin(joinLine.getJoinCriteria());
        }
        joinLines.clear();
        repaint();
    }

    public void removeJoinLine(JoinLine joinLine) {
        joinLines.remove(joinLine);
        remove(joinLine);
        queryBuilderPanel.getDesignPanel().removeJoin(joinLine.getJoinCriteria());
        repaint();
    }

    public boolean containsIFrame(String title) {
        JInternalFrame[] iframes = getAllFrames();

        for (int i = 0; i < iframes.length; i++) {
            JInternalFrame iframe = iframes[i];
            if (title.equals(iframe.getTitle())) {
                return true;
            }
        }

        return false;
    }



    private class DBTablesDesktopManager extends DefaultDesktopManager {

        public void endDraggingFrame(JComponent f) {
            super.endDraggingFrame(f);
            repaintDesktop(f);
        }

        public void endResizingFrame(JComponent f) {
            super.endResizingFrame(f);
            repaintDesktop(f);
        }

        private void repaintDesktop(JComponent f) {
            RepaintManager.currentManager(DBTablesDesktopPane.this)
                    .markCompletelyDirty(DBTablesDesktopPane.this);
            if (f instanceof DBTableInternalFrame) {
                queryBuilderPanel.scroll((DBTableInternalFrame)f);
            }
        }

    }

    QueryBuilderPanel getQuerBuilderPanel() {
        return queryBuilderPanel;
    }

    public void clear() {
        removeAllJoinLines();
        JInternalFrame[] iframes = getAllFrames();

        for (JInternalFrame iframe : iframes) {
            iframe.dispose();
            iframe = null;
        }
        iframes = null;
        setBackgroundImage();
    }

    public List<TablePersistentObject> getAllTables() {
        List<TablePersistentObject> tables = new ArrayList<TablePersistentObject>();
        JInternalFrame[] frames = getAllFrames();
        for (int i=0, size=frames.length; i<size; i++ ){
            TablePersistentObject tpo = new TablePersistentObject();
            DBTableInternalFrame iFrame = (DBTableInternalFrame)frames[i];
            tpo.setTable(iFrame.getTable());
            tpo.setPoint(iFrame.getLocation());
            tpo.setDim(iFrame.getSize());
            tables.add(tpo);
        }
        return tables;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundImage != null) {
            if (scale) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                int width = backgroundImage.getWidth(this);
                int height = backgroundImage.getHeight(this);
                int xStart = (int) (((double) (getParent().getWidth() - width)) / 2.0);
                int yStart = (int) (((double) (getParent().getHeight() - height)) / 2.0);

                g.drawImage(backgroundImage, xStart, yStart, width, height, this);
            }
            paintChildren(g);
        }
    }

    private void setBackgroundImage(Image backgroundImage, boolean scale) {
        this.backgroundImage = backgroundImage;
        this.scale = scale;
        repaint();
    }

    public void setBackgroundImage() {
        String language = Locale.getDefault().getLanguage().toLowerCase();
        ImageIcon icon = ImageUtil.getImageIcon("info_"+language, false);
        if (icon == null) {
            icon = ImageUtil.getImageIcon("info");
        }
        setBackgroundImage(icon.getImage(), false);
    }

    public void clearBackgroundImage() {
        setBackgroundImage(null, false);
    }




}
