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
package ro.nextreports.designer.action.query;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBProcedure;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.tree.DBNodeExpander;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import java.awt.event.ActionEvent;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Apr-2009
// Time: 12:04:12

//
public class ValidateProceduresAction extends AbstractAction {

    public static String VALID_PROPERTY = "VALID_PROCEDURE";
    private DBObject procObject;

    public ValidateProceduresAction() {
        this(null);
    }

    public ValidateProceduresAction(DBObject procObject) {
        String text;
        if (procObject == null) {
            text = I18NSupport.getString("procedure.validation");
        } else {
            text = I18NSupport.getString("procedure.validation.single");
        }
        putValue(Action.NAME, text);
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("procedure_valid"));       
        putValue(Action.SHORT_DESCRIPTION, text);
        putValue(Action.LONG_DESCRIPTION, text);
        this.procObject = procObject;
    }

    public void actionPerformed(ActionEvent e) {

        Thread executorThread = new Thread(new Runnable() {

            public void run() {

                UIActivator activator = new UIActivator(Globals.getMainFrame(),
                        I18NSupport.getString("procedure.validation"));
                activator.start();
                
                if (procObject != null) {
                    try {
                        DBProcedure proc = new DBProcedure(procObject.getSchemaName(),
                                procObject.getCatalog(),
                                procObject.getName(), 0);
                        if (Globals.getDBViewer().isValidProcedure(proc)) {
                            procObject.putProperty(VALID_PROPERTY, true);
                        } else {
                            Show.info(I18NSupport.getString("procedure.invalid"));
                        }
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }

                // validate all procedures
                } else {
                    try {
                        DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();
                        DBBrowserNode node = tree.searchNode(DBNodeExpander.PROCEDURES);
                        if (node.getChildCount() == 0) {
                            tree.startExpandingTree(node, false, null);
                        }
                        for (int i = 0, size = node.getChildCount(); i < size; i++) {
                            DBBrowserNode child = (DBBrowserNode) node.getChildAt(i);
                            DBObject object = child.getDBObject();
                            DBProcedure proc = new DBProcedure(object.getSchemaName(),
                                    object.getCatalog(),
                                    object.getName(), 0);
                            if (Globals.getDBViewer().isValidProcedure(proc)) {
                                object.putProperty(VALID_PROPERTY, true);
                            }
                        }
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }
                }
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }
}
