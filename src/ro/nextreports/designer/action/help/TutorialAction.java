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
package ro.nextreports.designer.action.help;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.jdesktop.jdic.desktop.Desktop;
import org.jdesktop.jdic.desktop.DesktopException;

import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 24, 2006
 * Time: 6:09:05 PM
 */
public class TutorialAction  extends AbstractAction  {

    private String file;
    public static final String TUTORIAL_DIR = "tutorial";
    public static final String TUTORIAL_FILE_EXT = ".html";

    public TutorialAction(String file) {
        putValue(Action.NAME, file.substring(0, file.lastIndexOf(".")));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("help"));
        putValue(Action.MNEMONIC_KEY, new Integer(file.charAt(0)));
        //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X,
        //        KeyEvent.CTRL_DOWN_MASK));
        putValue(Action.SHORT_DESCRIPTION, file);
        putValue(Action.LONG_DESCRIPTION, file);
        this.file = file;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Desktop.open(new File(TUTORIAL_DIR + File.separator + file));
        } catch (DesktopException e1) {
            Show.error(e1);
        }

    }
}
