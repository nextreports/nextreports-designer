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
package ro.nextreports.designer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import ro.nextreports.designer.ui.GlobalHotkeyManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;


/**
 * An extension to default UndoManager which manages undo/redo actions.
 * 
 * @author Decebal Suiu
 */
public class ReportUndoManager extends UndoManager {

	private static String UNDO_TEXT = I18NSupport.getString("undo");
	private static String REDO_TEXT = I18NSupport.getString("redo");

    private Action undoAction = new UndoAction();
	private Action redoAction = new RedoAction();

    public ReportUndoManager() {
		refreshUndoRedo();

        // add undo & redo as global actions
        GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
        InputMap inputMap = hotkeyManager.getInputMap();
        ActionMap actionMap = hotkeyManager.getActionMap();

        inputMap.put((KeyStroke) undoAction.getValue(Action.ACCELERATOR_KEY), "layoutUndo");
        actionMap.put("layoutUndo", undoAction);
        
        inputMap.put((KeyStroke) redoAction.getValue(Action.ACCELERATOR_KEY), "layoutRedo");
        actionMap.put("layoutRedo", redoAction);
    }

	public Action getUndoAction() {
		return undoAction;
	}

	public Action getRedoAction() {
		return redoAction;
	}

	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean result = super.addEdit(anEdit);
		refreshUndoRedo();
		return result;
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		super.undo();
		refreshUndoRedo();
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		super.redo();
		refreshUndoRedo();
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
		super.undoableEditHappened(undoableEditEvent);
		refreshUndoRedo();
	}
	
	@Override
	public synchronized void discardAllEdits() {
		super.discardAllEdits();
		refreshUndoRedo();
	}

	public void refreshUndoRedo() {
		if (canUndo()) {
			undoAction.setEnabled(true);
			undoAction.putValue(Action.SHORT_DESCRIPTION, optionalConcat(UNDO_TEXT, editToBeUndone().getPresentationName()));
		} else {
			undoAction.setEnabled(false);
			undoAction.putValue(Action.SHORT_DESCRIPTION, UNDO_TEXT);						
		}
		
		if (canRedo()) {
			redoAction.setEnabled(true);
			redoAction.putValue(Action.SHORT_DESCRIPTION, optionalConcat(REDO_TEXT, editToBeRedone().getPresentationName()));
		} else {
			redoAction.setEnabled(false);
			redoAction.putValue(Action.SHORT_DESCRIPTION, REDO_TEXT);
		}
	}
	
	private String optionalConcat(String text, String optionalText) {
		if ((optionalText != null) && (optionalText.length() > 0)) {
			return text.concat(" ").concat(optionalText);
		}
		
		return text;
	}
	
	private class UndoAction extends AbstractAction {
		
		public UndoAction() {
			super();
			putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("undo"));
			putValue(Action.SHORT_DESCRIPTION, UNDO_TEXT);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.undo.accelerator", "control Z")));
		}

		public void actionPerformed(ActionEvent event) {
			undo();
		}

	}

	private class RedoAction extends AbstractAction {
		
		public RedoAction() {
			super();
			putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("redo"));
			putValue(Action.SHORT_DESCRIPTION, REDO_TEXT);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("layout.redo.accelerator", "control Y")));
		}

		public void actionPerformed(ActionEvent event) {
			redo();
		}

	}

    public static void changeLocale() {
        UNDO_TEXT = I18NSupport.getString("undo");
	    REDO_TEXT = I18NSupport.getString("redo");
    }

}
