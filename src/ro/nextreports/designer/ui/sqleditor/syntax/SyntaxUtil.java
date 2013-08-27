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

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Decebal Suiu
 */
public class SyntaxUtil {
	
	private static final Log LOG = LogFactory.getLog(SyntaxUtil.class);
	
    // This is used internally to avoid NPE if we have no Strings
    public static String[] EMPTY_STRING_ARRAY = new String[0];
    
    // This is used to quickly create Strings of at most 16 spaces (using substring)
    public static String SPACES = "                ";

    /**
     * Return the lines that span the selection (split as an array of Strings)
     * if there is no selection then current line is returned.
     * 
     * Note that the strings returned will not contain the terminating line feeds.
     * 
     * The text component will then have the full lines set as selection.
     * 
     * @param target
     * @return String[] of lines spanning selection / or Dot
     */
    public static String[] getSelectedLines(JTextComponent target) {
        String[] lines = null;
        try {
            PlainDocument document = (PlainDocument) target.getDocument();
            int start = document.getParagraphElement(target.getSelectionStart()).getStartOffset();
            int end;
            if (target.getSelectionStart() == target.getSelectionEnd()) {
                end = document.getParagraphElement(target.getSelectionEnd()).getEndOffset();
            } else {
                // if more than one line is selected, we need to subtract one from the end
                // so that we do not select the line with the caret and no selection in it
                end = document.getParagraphElement(target.getSelectionEnd() - 1).getEndOffset();
            }
            target.select(start, end);
            lines = document.getText(start, end - start).split("\n");
            target.select(start, end);
        } catch (BadLocationException e) {
            LOG.error(e.getMessage(), e);
            lines = EMPTY_STRING_ARRAY;
        }
        
        return lines;
    }

    /**
     * Return the line of text at the document's current position.
     * 
     * @param target
     * @return
     */
    public static String getLine(JTextComponent target) {
        PlainDocument document = (PlainDocument) target.getDocument();
        return getLineAt(document, target.getCaretPosition());
    }

    /**
     * Return the line of text at the given position. The returned value may
     * be null. It will not contain the trailing new-line character.
     * 
     * @param doc
     * @param pos
     * @return
     */
    public static String getLineAt(PlainDocument doc, int pos) {
        String line = null;
        int start = doc.getParagraphElement(pos).getStartOffset();
        int end = doc.getParagraphElement(pos).getEndOffset();
        try {
            line = doc.getText(start, end - start);
            if (line != null && line.endsWith("\n")) {
                line = line.substring(0, line.length() - 1);
            }
        } catch (BadLocationException e) {
            LOG.error(e.getMessage(), e);
        }
        
        return line;
    }

}
