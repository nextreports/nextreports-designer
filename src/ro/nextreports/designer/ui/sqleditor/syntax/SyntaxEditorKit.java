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
package ro.nextreports.designer.ui.sqleditor.syntax;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.PlainDocument;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * To use the package, just set the EditorKit of the EditorPane to a new 
 * instance of this class. You need to pass a proper lexer to the class.
 * 
 * @author Decebal Suiu
 */
public class SyntaxEditorKit extends DefaultEditorKit implements ViewFactory {

	/** Undo action. */
    public static final String undoAction = "undo";
    
    /** Redo action. */
    public static final String redoAction = "redo";
    
    /** Indent action. */
    public static final String indentAction = "indent";
    
    /** Unindent action. */
    public static final String unindentAction = "unindent";
    
	/** Smart indent action. */
    public static final String smartIndentAction = "smart-indent";
    
	private static final long serialVersionUID = 3971907941600240991L;
	
    private Lexer lexer;
    
    /**
     * Create a new Kit for the given language 
     */
    public SyntaxEditorKit(Lexer lexer) {
        super();
        this.lexer = lexer;
    }

    @Override
    public ViewFactory getViewFactory() {
        return this;
    }

    public View create(Element element) {
        return new SyntaxView(element);
    }

    /**
     * This is called by Swing to create a Document for the JEditorPane document
     * This may be called before you actually get a reference to the control.
     * We use it here to create a properl lexer and pass it to the 
     * SyntaxDcument we return.
     * @return
     */
    @Override
    public Document createDefaultDocument() {
        return new SyntaxDocument(lexer);
    }
    
    @Override
	public void install(JEditorPane target) {
		super.install(target);
		// add key bindings
		Keymap keymap = target.getKeymap();
		JTextComponent.loadKeymap(keymap, getKeyBindings(), target.getActions());	    
	}

    /**
     * Get actions associated with this kit. getCustomActions() is called
     * to get basic list.
     */
    public final Action[] getActions() {
    	List<Action> actions = new ArrayList<Action>();
    	actions.addAll(Arrays.asList(super.getActions()));
    	actions.addAll(Arrays.asList(getDefaultActions()));
    	actions.addAll(Arrays.asList(getCustomActions()));
    	    	
    	return actions.toArray(new Action[actions.size()]);
    }
    
    protected Action[] getCustomActions() {
    	return new Action[0];
    }
    
    private Action[] getDefaultActions() {
    	return new Action[] {
    			new UndoAction(),
    			new RedoAction(),
    			new IndentAction(), 
    			new UnindentAction()
    	};
    }

    protected JTextComponent.KeyBinding[] getKeyBindings() {
    	List<JTextComponent.KeyBinding> keyBindings = new ArrayList<JTextComponent.KeyBinding>();
    	keyBindings.addAll(Arrays.asList(getDefaultKeyBindings()));
    	keyBindings.addAll(Arrays.asList(getCustomKeyBindings()));
    	
    	return keyBindings.toArray(new JTextComponent.KeyBinding[keyBindings.size()]);
    }
    
    protected JTextComponent.KeyBinding[] getCustomKeyBindings() {
    	return new JTextComponent.KeyBinding[0];
    }

    private JTextComponent.KeyBinding[] getDefaultKeyBindings() {
    	return new JTextComponent.KeyBinding[] {
    			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke("control Z"), undoAction),
    			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke("control Y"), redoAction),
    			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke("TAB"), indentAction),
    			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke("shift TAB"), unindentAction)
    	};
    }
 
    /**
     * A Pair action inserts a pair of characters (left and right) around the
     * current selection, and then places the caret between them.
     */
    public static class PairAction extends TextAction {

		private static final long serialVersionUID = 512435731659762179L;
		
		protected String left;
        protected String right;

        public PairAction(String actionName, String left, String right) {
            super(actionName);
            this.left = left;
            this.right = right;
        }

        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            if (target != null) {
                String selected = target.getSelectedText();
                if (selected != null) {
                    target.replaceSelection(left + selected + right);
                } else {
                    target.replaceSelection(left + right);
                }
                target.setCaretPosition(target.getCaretPosition() - 1);
            }
        }
        
    }

    /**
     * This action performs SmartIndentation each time VK_ENTER is pressed
     * SmartIndentation is inserting the same amount of spaces as
     * the line above. May not be too smart, but good enough.
     */
    public static class SmartIndentAction extends TextAction {

		private static final long serialVersionUID = -4884630822418253474L;

		public SmartIndentAction() {
            super(smartIndentAction);
        }

        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            if (target != null) {
                String line = SyntaxUtil.getLine(target);
                /**
                 * Perform Smart Indentation:  pos must be on a line: this method will
                 * use the previous lines indentation (number of spaces before any non-space
                 * character or end of line) and return that as the prefix.
                 */
                String indent = "";
                if (line != null && line.length() > 0) {
                	int i = 0;
                	while (i < line.length() && line.charAt(i) == ' ') {
                		i++;
                	}

                	indent = line.substring(0, i);
                }

                target.replaceSelection("\n" + indent);
            }
        }
        
    }

    /**
     * IndentAction is used to replace Tabs with spaces. If there is selected 
     * text, then the lines spanning the selection will be shifted
     * right by one tab-width space  character.
     */
    public static class IndentAction extends DefaultEditorKit.InsertTabAction {

		private static final long serialVersionUID = 182313664992032966L;

		public IndentAction() {
            super();
            putValue(Action.NAME, indentAction);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            if (target != null) {
                String selected = target.getSelectedText();
                if (selected == null) {
                    PlainDocument pDoc = (PlainDocument) target.getDocument();
                    Integer tabStop = (Integer) pDoc.getProperty(PlainDocument.tabSizeAttribute);
                    // insert needed number of tabs:
                    int lineStart = pDoc.getParagraphElement(target.getCaretPosition()).getStartOffset();
                    // column 
                    int column = target.getCaretPosition() - lineStart;
                    int needed = tabStop - (column % tabStop);
                    target.replaceSelection(SyntaxUtil.SPACES.substring(0, needed));
                } else {
                    String[] lines = SyntaxUtil.getSelectedLines(target);
                    int start = target.getSelectionStart();
                    StringBuilder sb = new StringBuilder();
                    for (String line : lines) {
                        sb.append('\t');
                        sb.append(line);
                        sb.append('\n');
                    }
                    target.replaceSelection(sb.toString());
                    target.select(start, start + sb.length());
                }
            }
        }
        
    }

    /**
     * This is usually mapped to Shift-TAB to unindent the selection.  The 
     * current line, or the selected lines are un-indented by the tabstop of the
     * document.
     */
    public static class UnindentAction extends TextAction {

		private static final long serialVersionUID = -4364953875980816216L;

		public UnindentAction() {
            super(unindentAction);
        }

        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            Integer tabStop = (Integer) target.getDocument().getProperty(PlainDocument.tabSizeAttribute);
            String indent = SyntaxUtil.SPACES.substring(0, tabStop);
            if (target != null) {
                String[] lines = SyntaxUtil.getSelectedLines(target);
                int start = target.getSelectionStart();
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    if (line.startsWith(indent)) {
                        sb.append(line.substring(indent.length()));
                    } else if (line.startsWith("\t")) {
                        sb.append(line.substring(1));
                    } else {
                        sb.append(line);
                    }
                    sb.append('\n');
                }
                target.replaceSelection(sb.toString());
                target.select(start, start + sb.length());
            }
        }
        
    }

    /**
     * Undo action.
     */
    public static class UndoAction extends TextAction {

		private static final long serialVersionUID = -1872733699834542335L;

		public UndoAction() {
            super(undoAction);
        }

        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            if (target != null) {
                if (target.getDocument() instanceof SyntaxDocument) {
                    SyntaxDocument document = (SyntaxDocument) target.getDocument();
                    document.doUndo();
                }
            }
        }
        
    }

    /**
     * Redo action.
     */
    public static class RedoAction extends TextAction {

		private static final long serialVersionUID = 5474351789865934556L;

		public RedoAction() {
            super(redoAction);
        }

        public void actionPerformed(ActionEvent event) {
            JTextComponent target = getTextComponent(event);
            if (target != null) {
                if (target.getDocument() instanceof SyntaxDocument) {
                    SyntaxDocument document = (SyntaxDocument) target.getDocument();
                    document.doRedo();
                }
            }
        }
        
    }

}
