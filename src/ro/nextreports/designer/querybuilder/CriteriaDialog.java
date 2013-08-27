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
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.querybuilder.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Mar 31, 2006
 * Time: 11:43:51 AM
 */
public class CriteriaDialog extends BaseDialog {

    private CriteriaPanel criteriaPanel;
    private MatchCriteria criteria;
    private boolean or;

    public CriteriaDialog(CriteriaPanel criteriaPanel) {
        this(criteriaPanel, false);
    }

    public CriteriaDialog(CriteriaPanel criteriaPanel, boolean or) {
        super(criteriaPanel, criteriaPanel.getTitle(), true);
        this.criteriaPanel = criteriaPanel;
        this.or = or;
    }


    protected boolean ok() {

        if (criteriaPanel.added()) {
            String value = criteriaPanel.getValue();
            if (value.equals("") && !Operator.isUnar(criteriaPanel.getOperator())) {
                Show.info(I18NSupport.getString("criteria.dialog.enter.first"));
                return false;
            }
            String value2 = criteriaPanel.getValue2();
            if (value2.equals("") && Operator.isDoubleValue(criteriaPanel.getOperator())) {
                Show.info(I18NSupport.getString("criteria.dialog.enter.second"));
                return false;
            }
            criteria = new MatchCriteria(criteriaPanel.getColumn(), criteriaPanel.getOperator(), value);
            criteria.setParameter(criteriaPanel.isParameter());
            if (Operator.isDoubleValue(criteriaPanel.getOperator())) {
                criteria.setValue2(value2);
                criteria.setParameter2(criteriaPanel.isParameter2());
            }
            if (or) {
                criteriaPanel.getSelectQuery().addOrCriteria(criteria, 0);
            } else {
                criteriaPanel.getSelectQuery().addCriteria(criteria);
            }
        } else {
            if (or) {
                criteria = criteriaPanel.getSelectQuery().getOrMatchCriteria(criteriaPanel.getColumn(), 0);
            } else {
                criteria = criteriaPanel.getSelectQuery().getMatchCriteria(criteriaPanel.getColumn());
            }
            criteria.setOperator(criteriaPanel.getOperator());
            criteria.setValue(criteriaPanel.getValue());
            criteria.setParameter(criteriaPanel.isParameter());
            if (Operator.isDoubleValue(criteriaPanel.getOperator())) {
                criteria.setValue2(criteriaPanel.getValue2());
                criteria.setParameter2(criteriaPanel.isParameter2());
            }
        }

        return true;

    }

    public MatchCriteria getCriteria() {
        return criteria;
    }

}
