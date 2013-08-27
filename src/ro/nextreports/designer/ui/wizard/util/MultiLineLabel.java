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
package ro.nextreports.designer.ui.wizard.util;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.StringTokenizer;

import javax.swing.JTextArea;

/**
 * A component for displaying multiple line text labels.
 *
 * @author Decebal Suiu
 */
class MultiLineLabel extends JTextArea {

    /**
     * Default columns.
     */
    private final static int DEFAULT_COLUMNS = 80;

    /**
     * Number of columns(characters) for a line.
     */
    private int columns = DEFAULT_COLUMNS;

    public MultiLineLabel() {
        this("", DEFAULT_COLUMNS);
    }

    public MultiLineLabel(int columns) {
        this("", columns);
    }

    public MultiLineLabel(String label) {
        this(label, DEFAULT_COLUMNS);
    }

    public MultiLineLabel(String label, int columns) {
        this.columns = columns;

        setEditable(false);
        setOpaque(false);
        setBorder(null);

        setText(wrap(label));
    }

    @Override
    public void setText(String text) {
        super.setText(wrap(text));
    }

    @Override
    public Dimension getPreferredSize() {
        String text = wrap(getText());
        int width = getLongestLineWidth(text);
        int height = super.getPreferredSize().height;

        return new Dimension(width, height);
    }

    private String wrap(String text) {
        if (text == null) {
            return text;
        }

        return (WordWrap.wrap(text, columns));
    }

    private int getLongestLineWidth(String wrappedText) {
        int length = 0;
        int maxLength = 0;

        FontMetrics metric = getFontMetrics(getFont());
        StringTokenizer st = new StringTokenizer(wrappedText, "\n");
        while (st.hasMoreTokens()) {
            length = metric.stringWidth(st.nextToken());
            if (length > maxLength) {
                maxLength = length;
            }
        }

        return maxLength;
    }

}
