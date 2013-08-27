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
package ro.nextreports.designer.ui.vista;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 20, 2009
 * Time: 10:19:58 AM
 */
public class VistaDialogContent {

    private List<VistaButton> buttons;
    private String text;
    private String description;

    public VistaDialogContent(List<VistaButton> buttons, String text) {
        this(buttons, text, null);
    }

    public VistaDialogContent(List<VistaButton> buttons, String text, String description) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null!");
        }
        this.buttons = buttons;
        this.text = text;
        this.description = description;
    }

    public List<VistaButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<VistaButton> buttons) {
        this.buttons = buttons;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
