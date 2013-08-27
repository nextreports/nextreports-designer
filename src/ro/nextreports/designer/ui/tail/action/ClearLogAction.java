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
package ro.nextreports.designer.ui.tail.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 2, 2006
 * Time: 10:26:03 AM
 */
public class ClearLogAction  extends AbstractAction  {

    private JTextArea textArea;

    public ClearLogAction(JTextArea textArea) {
        this.textArea = textArea;

        putValue(Action.NAME, I18NSupport.getString("clear.log"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("clear_log"));
        putValue(Action.MNEMONIC_KEY, new Integer('C'));
//        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
//                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("clear.log"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("clear.log"));
    }

    public void actionPerformed(ActionEvent e) {
        textArea.setText("");
    }
}
