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
package ro.nextreports.designer;


import javax.swing.*;

import ro.nextreports.designer.grid.GridHeaderRenderer;
import ro.nextreports.designer.grid.JGrid;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 13, 2008
 * Time: 4:10:30 PM
 */
public class ReportGridHeaderRowRenderer extends GridHeaderRenderer {

    public Component getRendererComponent(int row, int column, Object value,
                                          boolean isSelected, boolean hasFocus, JGrid grid) {
        super.getRendererComponent(row, column, value, isSelected, hasFocus, grid);
        setIcon(BandUtil.getIcon(row));        
        if (grid.getSelectionModel().isRowSelected(row))  {
            setBackground(SELECTION_COLOR);
        } else {
            setBackground(UIManager.getColor("TableHeader.background"));
        }
        return this;
    }

}
