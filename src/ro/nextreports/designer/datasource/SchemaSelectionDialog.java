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
package ro.nextreports.designer.datasource;


import java.util.List;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 3:53:36 PM
 */
public class SchemaSelectionDialog extends BaseDialog {

    public SchemaSelectionDialog(SchemaSelectionPanel basePanel) {
        super(basePanel, I18NSupport.getString("schema.selection.title"));
    }

    @SuppressWarnings("unchecked")
    public List<String> getVisibleSchemas() {
        return ((SchemaSelectionPanel)basePanel).getVisibleSchemas();
    }

    protected boolean ok() {
        List<String> visibleSchemas = getVisibleSchemas();
        System.out.println(visibleSchemas);
        if (visibleSchemas.size() == 0) {
            Show.info(I18NSupport.getString("schema.selection.enter"));
            return false;
        }        
        return true;
    }

}
