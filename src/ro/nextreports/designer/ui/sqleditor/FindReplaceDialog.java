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
package ro.nextreports.designer.ui.sqleditor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import ro.nextreports.designer.ui.EqualsLayout;
import ro.nextreports.designer.ui.HistoryComboBox;
import ro.nextreports.designer.util.I18NSupport;


/**
 * @author Decebal Suiu
 */
public class FindReplaceDialog extends JDialog {
	
	private final JTextComponent target;
	
	private JCheckBox caseCheck;
	private JRadioButton allButton;
	private JRadioButton selectedLinesButton;
	private JRadioButton forwardButton;
	private JRadioButton backwardButton;
	private JCheckBox wholeWordCheck;
	private JButton findButton;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private HistoryComboBox findCombo;
	private HistoryComboBox replaceCombo;
	private JCheckBox wrapCheck;
	
	private int lastFoundIndex = -1;
    private MatchResult lastMatchResult;
    private String lastRegex;
	private Pattern pattern;
	private Segment segment = new Segment();

	public FindReplaceDialog(Frame parent, JTextComponent target) {
		super(parent, I18NSupport.getString("sqleditor.findReplaceDialog.title"), false);
		this.target = target;
		initComponents();
	}
	
	public FindReplaceDialog(Dialog parent, JTextComponent target) {
		super(parent, I18NSupport.getString("sqleditor.findReplaceDialog.title"), false);
		this.target = target;
		initComponents();
	}

	private void initComponents() {
		JPanel panel = new JPanel(new BorderLayout());
		findCombo = new HistoryComboBox();
		replaceCombo = new HistoryComboBox();

		// create inputs
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        inputPanel.add(new JLabel(I18NSupport.getString("sqleditor.findReplaceDialog.find")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 0), 0, 0));
        inputPanel.add(findCombo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 1), 0, 0));
        inputPanel.add(new JLabel(I18NSupport.getString("sqleditor.findReplaceDialog.replaceWith")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 1, 0), 0, 0));
        inputPanel.add(replaceCombo, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 1, 1), 0, 0));

        // create direction panel
		ButtonGroup directionGroup = new ButtonGroup();
		forwardButton = new JRadioButton(I18NSupport.getString("sqleditor.findReplaceDialog.forward"), true);
		forwardButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		directionGroup.add(forwardButton);
		backwardButton = new JRadioButton(I18NSupport.getString("sqleditor.findReplaceDialog.backward"));
		backwardButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		directionGroup.add(backwardButton);

		JPanel directionPanel = new JPanel(new GridBagLayout());
		directionPanel.setBorder(BorderFactory.createTitledBorder(I18NSupport.getString("sqleditor.findReplaceDialog.direction")));
        directionPanel.add(forwardButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));
        directionPanel.add(new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 1, 5), 0, 0));
        directionPanel.add(backwardButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));

        // create scope panel
		ButtonGroup scopeGroup = new ButtonGroup();
		allButton = new JRadioButton(I18NSupport.getString("sqleditor.findReplaceDialog.all"), true);
		allButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		selectedLinesButton = new JRadioButton(I18NSupport.getString("sqleditor.findReplaceDialog.selectedLines"));
		selectedLinesButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		scopeGroup.add(allButton);
		scopeGroup.add(selectedLinesButton);

        // TODO doesn't work yet
		selectedLinesButton.setEnabled(false);
		
		JPanel scopePanel = new JPanel(new GridBagLayout());
		scopePanel.setBorder(BorderFactory.createTitledBorder(I18NSupport.getString("sqleditor.findReplaceDialog.scope")));
        scopePanel.add(allButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));
        scopePanel.add(new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 1, 5), 0, 0));
        scopePanel.add(selectedLinesButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));

        // create options
		caseCheck = new JCheckBox(I18NSupport.getString("sqleditor.findReplaceDialog.caseSensitive"));
		caseCheck.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		wholeWordCheck = new JCheckBox(I18NSupport.getString("sqleditor.findReplaceDialog.wholeWord"));
		wholeWordCheck.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		wrapCheck = new JCheckBox(I18NSupport.getString("sqleditor.findReplaceDialog.wrapSearch"));
		wrapCheck.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JPanel optionsPanel = new JPanel(new GridBagLayout());
		optionsPanel.setBorder(BorderFactory.createTitledBorder(I18NSupport.getString("sqleditor.findReplaceDialog.options")));
        optionsPanel.add(caseCheck, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));
        optionsPanel.add(new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 1, 5), 0, 0));
        // TODO doesn't work yet
        /*
        optionsPanel.add(wholeWordCheck, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));
        optionsPanel.add(wrapCheck, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 1, 5), 0, 0));
         */

        JPanel radios = new JPanel(new GridBagLayout());
        radios.add(directionPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        radios.add(scopePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));


        // create panel with options
		JPanel options = new JPanel(new GridBagLayout());
		options.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        options.add(radios, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        options.add(optionsPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        // create buttons
		JPanel buttonsPanel = new JPanel(new EqualsLayout(5));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
		findButton = new JButton(new FindAction());
		buttonsPanel.add(findButton);
		replaceButton = new JButton(new ReplaceAction());
		buttonsPanel.add(replaceButton);
		replaceAllButton = new JButton(new ReplaceAllAction());
		buttonsPanel.add(replaceAllButton);
		buttonsPanel.add(new JButton(new CloseAction()));

		panel.add(inputPanel, BorderLayout.NORTH);
		panel.add(options, BorderLayout.CENTER);
		panel.add(buttonsPanel, BorderLayout.SOUTH);

		add(panel);
		
		replaceCombo.setEnabled(target.isEditable());
		replaceAllButton.setEnabled(target.isEditable());
		replaceButton.setEnabled(target.isEditable());

//		findCombo.getEditor().selectAll();
//		findCombo.requestFocus();
	}
	
	private boolean doFind() {
		return doFind(target.getSelectionEnd());
	}
	
	private boolean doFind(int startIndex) {
		boolean found = search(getPattern(), startIndex, backwardButton.isSelected());
		if (found) {
			String item = (String) findCombo.getSelectedItem();
			findCombo.add(item);
		}
		
		return found;
	}
	
	private boolean doReplace() {
		boolean replaced = doReplace(target.getSelectionStart());
		if (replaced) {
			String item = (String) replaceCombo.getSelectedItem();
			replaceCombo.add(item);
		}
		
		return replaced;
	}
	
	private boolean doReplace(int startIndex) {
		String findString = (String) findCombo.getSelectedItem();
		String selectedText = target.getSelectedText();
		
		if ((selectedText != null) && (selectedText.length() > 0) && selectedText.equals(findString)) {
			String replaceString = (String) replaceCombo.getSelectedItem();
			try {
				target.getDocument().remove(startIndex, findString.length());
				target.getDocument().insertString(startIndex, replaceString, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
			return search(getPattern(), startIndex, backwardButton.isSelected());
		}
		
		return false;
	}
	
	private int doReplaceAll() {
		String findString = (String) findCombo.getSelectedItem();

		if (findString == null || findString.length() == 0) {
			return 0;
		}

		String replaceString = (String) replaceCombo.getSelectedItem();		
		int counter = 0;
		int startIndex = 0; 
		while (doFind(startIndex)) {
			try {
				target.getDocument().remove(lastFoundIndex, findString.length());
				target.getDocument().insertString(lastFoundIndex, replaceString, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			counter++;
			startIndex = lastFoundIndex + replaceString.length();
		}
		
		return counter;
	}

	private boolean search(Pattern pattern, int startIndex, boolean backwards) {
		int textLength = target.getDocument().getLength();
        if ((textLength == 0) || ((startIndex > -1) && (textLength < startIndex))) {
            updateStateAfterNotFound();
            return false;
        }

        int start = startIndex;
        if ((startIndex >= 0) && (startIndex == lastFoundIndex)) {
        	if (foundExtendedMatch(pattern, start)) {
        		return true;
        	} else {
        		start++;
        	}
        }
        
        int length = 0;
        if (backwards) {
            start = 0;
            if (startIndex < 0) {
                length = textLength - 1; // end of text
            } else if (startIndex > 0) {
                length = startIndex - 1;
            }
        } else {
            if (start < 0) {
                start = 0; // begin of text
            }
            length = textLength - start;
        }
        
//        System.out.println("start = " + start);
//        System.out.println("length = " + length);
        try {
            target.getDocument().getText(start, length, segment);
        } catch (BadLocationException e) {
        	e.printStackTrace();
        }

        Matcher matcher = pattern.matcher(segment.toString());
        MatchResult matchResult = getMatchResult(matcher, !backwards);
        if (matchResult != null) {
            updateStateAfterFound(matchResult, start);
            return true;
        } else {
            updateStateAfterNotFound();
            return false;
        }
	}
	
    /**
     * Search from same startIndex as the previous search. 
     * Checks if the match is different from the last (either 
     * extended/reduced) at the same position. Returns true
     * if the current match result represents a different match 
     * than the last, false if no match or the same.
     */
    private boolean foundExtendedMatch(Pattern pattern, int start) {
        if (pattern.pattern().equals(lastRegex)) {
            return false;
        }
        
        int length = target.getDocument().getLength() - start;
        try {
            target.getDocument().getText(start, length, segment);
        } catch (BadLocationException e) {
        	e.printStackTrace();
        }
        
        Matcher matcher = pattern.matcher(segment.toString());
        MatchResult matchResult = getMatchResult(matcher, true);
        if (matchResult != null) {
            if ((matchResult.start() == 0) &&  (!lastMatchResult.group().equals(matchResult.group()))) {
                updateStateAfterFound(matchResult, start);
                return true;
            } 
        }
        
        return false;
    }

    private MatchResult getMatchResult(Matcher matcher, boolean onlyFirst) {
        MatchResult matchResult = null;
        while (matcher.find()) {
            matchResult = matcher.toMatchResult();
            if (onlyFirst) {
            	break;
            }
        }
        
        return matchResult;
    }

    private int updateStateAfterFound(MatchResult matchResult, int offset) {
        int end = matchResult.end() + offset;
        int found = matchResult.start() + offset; 
        
        target.select(found, end);
        target.getCaret().setSelectionVisible(true);
        
        // update state variables
        lastFoundIndex = found;
        lastMatchResult = matchResult;
        lastRegex = ((Matcher) lastMatchResult).pattern().pattern();
        
        return found;
    }

    private void updateStateAfterNotFound() {
        lastMatchResult = null;
        lastRegex = null;
        lastFoundIndex = -1;
    }
	
	private Pattern getPattern() {
	    String searchString = findCombo.getSelectedItem().toString();
	    if (searchString.length() == 0) {
	    	return null;
	    }        

        if ((pattern == null) || !pattern.pattern().equals(searchString) || (pattern.flags() != getPatternFlag())) {
            // use Pattern.LITERAL so that metacharacters or escape sequences in the
            // searchString will be given no special meaning.
            pattern = Pattern.compile(searchString, getPatternFlag() | Pattern.LITERAL);
        }
        
        return pattern;
	}
	
	private int getPatternFlag() {
		return caseCheck.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
	}
	
	private class FindAction extends AbstractAction {
		
		private static final long serialVersionUID = -8920424035964381915L;

		public FindAction() {
			super(I18NSupport.getString("sqleditor.findReplaceDialog.action.find"));
		}

		public void actionPerformed(ActionEvent event) {
			if (!doFind()) {
				if (!wrapCheck.isSelected()) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(FindReplaceDialog.this,
                            I18NSupport.getString("sqleditor.findReplaceDialog.value.not.found"));
				}
			}
		}
		
	}

	private class ReplaceAction extends AbstractAction {
		
		private static final long serialVersionUID = 6520019012311957603L;

		public ReplaceAction() {
			super(I18NSupport.getString("sqleditor.findReplaceDialog.action.replace"));
		}

		public void actionPerformed(ActionEvent event) {
			if (!doReplace()) {
				if (!wrapCheck.isSelected()) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(FindReplaceDialog.this,
                            I18NSupport.getString("sqleditor.findReplaceDialog.value.not.found"));
				}
			}
			/*
			if (target.getSelectedText() == null) {
				return;
			}

			String value = replaceCombo.getSelectedItem().toString();
			int ix = target.getSelectionStart();
			target.select(ix + value.length(), ix);

			for (int c = 0; c < replaceCombo.getItemCount(); c++) {
				if (replaceCombo.getItemAt(c).equals(value)) {
					replaceCombo.removeItem(c);
					break;
				}
			}

			replaceCombo.insertItemAt(value, 0);
			*/
		}
		
	}

	private class ReplaceAllAction extends AbstractAction {
		
		private static final long serialVersionUID = -2154781306570849689L;

		public ReplaceAllAction() {
			super(I18NSupport.getString("sqleditor.findReplaceDialog.action.replaceall"));
		}

		public void actionPerformed(ActionEvent event) {
			int counter = doReplaceAll();
			String message;
			if (counter == 0) {
				Toolkit.getDefaultToolkit().beep();
				message = I18NSupport.getString("sqleditor.findReplaceDialog.value.not.found");
			} else {
				message = I18NSupport.getString("sqleditor.findReplaceDialog.replacements", counter);
			}
			JOptionPane.showMessageDialog(FindReplaceDialog.this, message);                                        
			/*
			int caretPosition = target.getCaretPosition();
			String text = target.getText();

			if (findCombo.getSelectedItem() == null) {
				return;
			}
			
			String value = findCombo.getSelectedItem().toString();
			if ((value.length() == 0) || (text.length() == 0)) {
				return;
			}
			
			String newValue = replaceCombo.getSelectedItem().toString();
			*/
		}
		
	}

	private class CloseAction extends AbstractAction {
		
		private static final long serialVersionUID = 6227267005012383534L;

		public CloseAction() {
			super(I18NSupport.getString("sqleditor.findReplaceDialog.action.close"));
		}

		public void actionPerformed(ActionEvent event) {
			setVisible(false);
		}
		
	}

 }
