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
package ro.nextreports.designer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.wizpublish.PublishBulkWizard;


public class PublishBulkChartAction  extends AbstractAction {

    private boolean cancel = false;

    public PublishBulkChartAction () {
        putValue(Action.NAME, I18NSupport.getString("publish"));
        Icon icon = ImageUtil.getImageIcon("publish");
        putValue(Action.SMALL_ICON, icon);
        putValue(MNEMONIC_KEY, ShortcutsUtil.getMnemonic("publish.mnemonic",  new Integer('U')));        
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("publish"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("publish"));
    }

    public void actionPerformed(ActionEvent e) {
        new PublishBulkWizard(DBObject.CHARTS_GROUP); 
    }

}
