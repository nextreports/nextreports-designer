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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 8, 2006
 * Time: 2:19:13 PM
 */
public abstract class UpDownTableKeyAdapter extends KeyAdapter {

    private JTable table;

    public UpDownTableKeyAdapter(JTable table) {
        super();
        this.table = table;
    }

    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_DOWN)) {
            int row = table.getSelectedRow();
            if (row >= 0) {

                if ((e.getKeyCode() == KeyEvent.VK_DOWN) && (row < table.getRowCount() - 1)) {
                    row = row + 1;
                }
                if ((e.getKeyCode() == KeyEvent.VK_UP) && (row > 0)) {
                    row = row - 1;
                }
            }
            if ((row == -1) && (table.getRowCount() > 0)) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    row = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    row = 1;
                }
            }

            action(row);
        }
    }

    public abstract void action(int row);
}
