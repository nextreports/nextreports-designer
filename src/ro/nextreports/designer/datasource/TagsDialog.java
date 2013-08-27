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

import ro.nextreports.engine.util.StringUtil;

import java.util.List;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 11:20:00 AM
 */
public class TagsDialog  extends BaseDialog {

    public TagsDialog(TagsPanel tagsPanel) {
        super(tagsPanel, I18NSupport.getString("connection.dialog.tags.title"));
    }

    public List<String> getTagsValues() {
        return ((TagsPanel)basePanel).getTagsValues();
    }

    public List<String> getTags() {
        return ((TagsPanel)basePanel).getTags();
    }

    protected boolean ok() {
        List<String> tags = getTags();
        List<String> tagsValues = getTagsValues();
        for  (int i=0, size=tagsValues.size(); i<size; i++) {
            if ("".equals(tagsValues.get(i).trim())) {
                Show.info(I18NSupport.getString("connection.dialog.tags.enter", StringUtil.capitalize(tags.get(i))));
                return false;
            }
        }                       
        return true;
    }

}
