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
package ro.nextreports.designer.ui.tail;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.tail.action.ClearLogAction;
import ro.nextreports.designer.ui.tail.action.ReloadLogAction;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 30, 2007
 * Time: 4:23:51 PM
 */
public class LogPanel extends JPanel implements LogFileTailerListener {

    private static final String LOG_DIR = Globals.USER_DATA_DIR + "/logs";
    private static final String LOG = LOG_DIR + "/jdbc-spy.log";
    private final String TITLE = I18NSupport.getString("logpanel.title");
    private static LogFileTailer tailer;
    private JTextArea textArea;
    private JTextField linesTextField;

    private static final int LINES = 100;
    private Dimension dim = new Dimension(40, 20);

    public LogPanel() {
        if (tailer == null) {

            setPreferredSize(new Dimension(400, 300));
            setLayout(new BorderLayout());

            textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPanel = new JScrollPane(textArea);

            linesTextField = new JTextField();
            linesTextField.setPreferredSize(dim);
            linesTextField.setMinimumSize(dim);
            linesTextField.setMaximumSize(dim);
            linesTextField.setText(String.valueOf(LINES));

            JToolBar toolBar = new JToolBar();
            toolBar.setRollover(true);
            toolBar.add(new ClearLogAction(textArea));
            toolBar.add(new ReloadLogAction(textArea, this));

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
            topPanel.add(toolBar);
            topPanel.add(Box.createHorizontalStrut(5));
            topPanel.add(new JLabel(I18NSupport.getString("logpanel.last.lines")));
            topPanel.add(Box.createHorizontalStrut(5));
            topPanel.add(linesTextField);
            topPanel.add(Box.createHorizontalGlue());

            add(topPanel, BorderLayout.NORTH);
            add(scrollPanel, BorderLayout.CENTER);

            final File log = new File(LOG);
            if (!log.exists()) {
                try {
                    new File(LOG_DIR).mkdirs();
                    boolean created = log.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            // read existing text in log
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Cursor hourGlassCursor = new Cursor(Cursor.WAIT_CURSOR);
                    setCursor(hourGlassCursor);

                    //@todo
                    //reload(log, textArea);

                    tailer = new LogFileTailer(log, 1000, false);
                    tailer.addLogFileTailerListener(LogPanel.this);
                    tailer.setPriority(Thread.MIN_PRIORITY);

                    // very consuming !!!
                    //tailer.start();

                    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    setCursor(normalCursor);
                }
            }, "NEXT : " + getClass().getSimpleName());
            t.start();

        }
    }


    public void newLogFileLine(final String line) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.append(line);
                textArea.append("\r\n");
                textArea.setCaretPosition(textArea.getText().length());
            }
        });
    }

    public static void stop() {
        if (tailer != null) {
            tailer.stopTailing();
            tailer = null;
        }
    }

    private void reloadAll(final File log, final JTextArea textArea) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(log, "r");
                    String line = file.readLine();
                    textArea.setText("");
                    while (line != null) {
//                        try {
//                            //@todo
//                            Thread.sleep(10);
//                        } catch (InterruptedException ex) {
//                        }
                        line = file.readLine();
                        final String lin = line;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                textArea.append(lin);
                                textArea.append("\r\n");
                                textArea.setCaretPosition(textArea.getText().length());
                            }
                        });

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (file != null) {
                        try {
                            file.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }, "NEXT : Reload Log");
        t.start();
    }

    private void reload(final File log, final JTextArea textArea) {
        int lines = 0;
        boolean reloadAll = false;
        String text = linesTextField.getText().trim();
        if("".equals(text)) {
           reloadAll = true;
        } else {
            try {
                lines = Integer.parseInt(text);
                if  (lines == 0) {
                    reloadAll = true;
                }
            } catch (NumberFormatException ex) {
                lines = LINES;
            }
        }
        if (reloadAll) {
            reloadAll(log, textArea);
        } else {
            final int noLines = lines;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    final ArrayList<String> lines = FileUtil.tail(LOG, noLines);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (int i = lines.size() - 1; i >= 0; i--) {
                                textArea.append(lines.get(i));
                                textArea.append("\r\n");
                            }
                            textArea.setCaretPosition(textArea.getText().length());
                        }
                    });
                }
            }, "NEXT : Reload Log");
            t.start();
        }
    }


    public void reload(JTextArea textArea) {
        textArea.setText("");
        File log = new File(LOG);
        reload(log, textArea);
    }

}
