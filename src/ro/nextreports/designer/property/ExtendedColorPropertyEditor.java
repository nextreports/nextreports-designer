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
package ro.nextreports.designer.property;

import com.l2fprod.common.beans.editor.ColorPropertyEditor;
import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.util.ResourceManager;

import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 14-May-2010
 * Time: 13:13:00
 */
public class ExtendedColorPropertyEditor extends ColorPropertyEditor {

    protected void selectColor() {
        ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
        String title = rm.getString("ColorPropertyEditor.title");
        Color selectedColor = ExtendedColorChooser.showDialog(editor, title, (Color)getValue());

        if (selectedColor != null) {
            Color oldColor = (Color)getValue();                     
            setValue(selectedColor);
            firePropertyChange(oldColor, selectedColor);
        }
    }


}
