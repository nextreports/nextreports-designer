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
package ro.nextreports.designer.action.query;


import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 9, 2007
 * Time: 3:43:34 PM
 */
public class DeselectListAction extends AbstractAction {

    private JList list;

    public DeselectListAction(JList list) {
        putValue(Action.NAME, I18NSupport.getString("deselect.list"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("clear"));
        putValue(Action.MNEMONIC_KEY, new Integer('D'));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("deselect.list.short.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("deselect.list.long.desc"));
        this.list = list;
    }

    public void actionPerformed(ActionEvent ev) {
        list.clearSelection();
    }
}
        
