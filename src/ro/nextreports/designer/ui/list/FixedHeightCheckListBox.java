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
package ro.nextreports.designer.ui.list;

import java.awt.Font;
import java.awt.Graphics;

/**
 * @author Decebal Suiu
 */
public class FixedHeightCheckListBox extends CheckListBox {

    private boolean firstPaint = true;
    private int cellHeight = -1;

    public FixedHeightCheckListBox() {
        super();
    }

    public FixedHeightCheckListBox(int cellHeight) {
        super();
        this.cellHeight = cellHeight;
    }

    @Override
    public void setFont(Font font) {
        firstPaint = true;
        super.setFont(font);
    }

    @Override
    public void paint(Graphics g) {
        if (firstPaint) {
            calculateFixedHeight(g);
            //Setting the fixed height will generate another paint request,
            //no need to complete this one
            return;
        }

        super.paint (g);
    }

    @Override
    public int getFixedCellHeight() {
        return cellHeight;
    }

    private void calculateFixedHeight(Graphics g) {
        if (cellHeight == -1) {
            g.setFont(getFont());
            setFixedCellHeight(g.getFontMetrics().getHeight());
        } else {
            setFixedCellHeight(cellHeight);
        }
        firstPaint = false;
    }

}
