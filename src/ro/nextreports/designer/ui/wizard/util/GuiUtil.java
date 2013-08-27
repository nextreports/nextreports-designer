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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.List;

import javax.swing.*;

/**
 * @author Decebal Suiu
 */
public class GuiUtil {

    public static void showMessages(Component parent, List<String> messages) {

        if ((messages == null) || (messages.size() == 0)){
            return;
        }

        Window window = SwingUtilities.windowForComponent(parent);

        StringBuilder sb = new StringBuilder();
        if (messages != null) {
            for (int i = 0, size = messages.size(); i < size; i++) {
                sb.append(messages.get(i));
                if (i < size - 1) {
                    sb.append("\r\n");
                }
            }
        }
        JOptionPane.showMessageDialog(window, sb.toString());
    }

}
