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
package ro.nextreports.designer.querybuilder;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.querybuilder.sql.JoinType;

/**
 * @author Decebal Suiu
 */
public class JoinPropertiesDialog extends BaseDialog {

    public JoinPropertiesDialog(JoinPropertiesPanel joinPanel) {
        super(joinPanel, I18NSupport.getString("join.properties"), true);
    }

    @Override
    protected boolean ok() {
        JoinPropertiesPanel joinPanel = (JoinPropertiesPanel) basePanel;
        JoinLine joinLine = joinPanel.getJoinLine();            
        joinLine.getJoinCriteria().setOperator(joinPanel.getOperator());
        if (joinPanel.isOuterJoin()) {
           if (joinPanel.isLeftJoin()) {
               joinLine.getJoinCriteria().setJoinType(JoinType.LEFT_OUTER_JOIN);
           } else {
               joinLine.getJoinCriteria().setJoinType(JoinType.RIGHT_OUTER_JOIN);
           }
        } else {
            joinLine.getJoinCriteria().setJoinType(JoinType.INNER_JOIN);
        }

        return true;
    }

}
