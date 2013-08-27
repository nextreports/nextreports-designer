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
package ro.nextreports.designer.action.undo;

import ro.nextreports.engine.ReportLayout;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.action.report.layout.ClearLayoutAction;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 04-Mar-2009
 * Time: 13:58:43
 */
public class LayoutEdit extends AbstractUndoableEdit {

    private ReportLayout oldLayout;
    private ReportLayout newLayout;
    private String name;

    public LayoutEdit(ReportLayout oldLayout, ReportLayout newLayout, String name) {
        this.oldLayout = oldLayout;
        this.newLayout = newLayout;
        this.name = name;
    }

    @Override
	public String getPresentationName() {
        if (name == null) {
            return "";
        }
        return name;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

        if (oldLayout == null) {
            new ClearLayoutAction(true).actionPerformed(null);
        } else {
            ReportLayoutUtil.setCurrentGroupIndex(oldLayout);
            Globals.getMainFrame().getQueryBuilderPanel().loadReport(oldLayout);
        }
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

        if (newLayout == null) {
            new ClearLayoutAction(true).actionPerformed(null);
        } else {
            ReportLayoutUtil.setCurrentGroupIndex(newLayout);
            Globals.getMainFrame().getQueryBuilderPanel().loadReport(newLayout);
        }        
	}
}
