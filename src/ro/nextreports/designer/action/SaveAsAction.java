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


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.chart.SaveAsChartAction;
import ro.nextreports.designer.action.query.SaveAsQueryAction;
import ro.nextreports.designer.action.report.SaveAsReportAction;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 07-Jan-2010
 * Time: 17:41:07
 */
public class SaveAsAction extends AbstractAction {

    private boolean cancel = false;

    public SaveAsAction () {
        putValue(Action.NAME, I18NSupport.getString("save.as"));
        Icon icon = ImageUtil.getImageIcon("save");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("save.as"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("save.as"));
    }

    public void actionPerformed(ActionEvent e) {
        if (Globals.isChartLoaded()){
            new SaveAsChartAction().actionPerformed(e);
        } else if (Globals.isReportLoaded()) {
            new SaveAsReportAction().actionPerformed(e);
        } else {
            new SaveAsQueryAction().actionPerformed(e);
        }
    }
}
