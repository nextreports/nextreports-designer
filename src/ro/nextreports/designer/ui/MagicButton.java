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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;

/**
 * @author Decebal Suiu
 */
public class MagicButton extends JButton implements MouseListener {

    public MagicButton() {
        super();
        buttonInit();
    }

    public MagicButton(Icon icon) {
        super(icon);
        buttonInit();
    }

    public MagicButton(String text) {
        super(text);
        buttonInit();
    }

    public MagicButton(String text, Icon icon) {
        super(text, icon);
        buttonInit();
    }

    public MagicButton(Action action) {
        super(action);
        buttonInit();
    }

    private void buttonInit() {
//        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
//        this.setMargin(new Insets(2, 2, 2, 2));

        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (isEnabled()) {
            this.setBorderPainted(true);
//            this.setFocusPainted(true);
        }
    }

    public void mouseExited(MouseEvent e) {
        this.setBorderPainted(false);
        this.setFocusPainted(false);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

}


