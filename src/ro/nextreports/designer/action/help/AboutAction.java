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
package ro.nextreports.designer.action.help;


import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReleaseInfo;
import ro.nextreports.designer.ReleaseInfoAdapter;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

/**
 * @author Mihai Dinca-Panaitescu
 */
public class AboutAction extends AbstractAction {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    private String VERSION = I18NSupport.getString("version");
    private String VERSION_NO = ReleaseInfoAdapter.getVersion();
    private String COPYRIGHT = "\u00A9" + ReleaseInfo.getCopyright();
    private String DEVELOPER = "NextReports";
    private String BUILD = I18NSupport.getString("build");
    private String BUILD_DATE = sdf.format(ReleaseInfo.getBuildDate());
    private String SITE = I18NSupport.getString("site");
    private String SITE_SMALL_VALUE = " " + ReleaseInfo.getHome();
    private String SITE_VALUE = "http://" + ReleaseInfo.getHome();

    private final Color gradientColor = new Color(102, 153, 204);
    private final Color textColor = new Color(51, 102, 153);
    
    private boolean showCredits = true;        
    private JComponent panel;
    private JDialog dlg;

    private String[] team = {"Decebal \u015Euiu",
            "Mihai Dinc\u0103-Panaitescu"
    };

    public AboutAction() {
        putValue(Action.NAME, I18NSupport.getString("about.next.reports"));
        Icon icon = ImageUtil.getImageIcon("about");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, new Integer('A'));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("about"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("about.next.reports"));        
    }

    public void actionPerformed(ActionEvent e) {

    	panel = createPanel();
    	
        final JPanel allPanel = new JPanel();
        final JPanel creditsPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gradient = new GradientPaint(0, 0, gradientColor, w, h, Color.WHITE, true);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, w, h);
                g2.setColor(textColor);
                g2.drawLine(0, 0, 0, h - 1);
                g2.drawLine(w - 1, 0, w - 1, h - 1);
                g2.drawLine(0, h - 1, w - 1, h - 1);
            }
        };
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.X_AXIS));
        final Component comp = Box.createRigidArea(new Dimension(17, 35));
        creditsPanel.add(comp);
        final JButton btnCredits = new JButton(I18NSupport.getString("about.credits"));
        creditsPanel.add(btnCredits);
        creditsPanel.add(Box.createGlue());

        allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));        
        allPanel.add(panel);
        if (showCredits) {
            allPanel.add(creditsPanel);
        }

        dlg = new JDialog(Globals.getMainFrame(), I18NSupport.getString("about.title"), true);
        dlg.add(allPanel);
        dlg.setUndecorated(true);
        dlg.pack();
        dlg.setResizable(false);
        Show.centrateComponent(Globals.getMainFrame(), dlg);
        dlg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                dlg.dispose();
            }
        });        

        btnCredits.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                creditsPanel.remove(comp);
                creditsPanel.remove(btnCredits);
                creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
                creditsPanel.add(Box.createRigidArea(new Dimension(17, 5)));
                creditsPanel.add(new JLabel("<HTML><u><font color=\"#0000A0\">" +
                        I18NSupport.getString("development.team") + "</font></u></HTML>"));
                for (int i = 0, size = team.length; i < size; i++) {
                    creditsPanel.add(Box.createRigidArea(new Dimension(17, 4)));
                    creditsPanel.add(new JLabel("<HTML><font color=\"#0000A0\">" + team[i] + "</font></HTML>"));
                }
                creditsPanel.add(Box.createRigidArea(new Dimension(17, 5)));
                creditsPanel.revalidate();
                dlg.pack();
            }
        });
        dlg.setVisible(true);
    }

    protected MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent me) {
            dlg.dispose();
        }
    };

            
    private JComponent createPanel() {
    	System.setProperty("awt.useSystemAAFontSettings", "on");
    	final JEditorPane editorPane = new JEditorPane();    	
    	HTMLEditorKit kit = new HTMLEditorKit();
    	editorPane.setEditorKit(kit);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(new Font("Arial", Font.PLAIN, 12));
        editorPane.setPreferredSize(new Dimension(350, 180));
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setBackground(new Color(234, 241, 248));        
        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(".firstCol {margin-left: 25px; }");
        styleSheet.addRule(".secondCol {color: blue; }");
        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);
        editorPane.setText(
                "<html>" +
                "<body>" +
                "<table border='0px' BGCOLOR=\"#EAF1F8\">" +
                "<tr><td colspan=2>" +
                "<img src='" + ImageUtil.getImageURL("logo").toExternalForm() + "'>" +
                "</td></tr>" +
                "<tr><td class=\"firstCol\"><b>" + VERSION + "</b></td><td class=\"secondCol\">" + 
                	VERSION_NO + 
                "</td></tr>" +
                "<tr><td class=\"firstCol\"><b>" + BUILD + "</b></td><td class=\"secondCol\">" + 
                	ReleaseInfo.getBuildNumber() + " (" + BUILD_DATE + ")" + 
                "</td></tr>" +
                "<tr><td class=\"firstCol\"><b>" + SITE + "</b></td><td class=\"secondCol\">"+ 
                	"<a href=\"" + SITE_VALUE + "\">" + SITE_SMALL_VALUE + 
                "</a></td></tr>" +
                "<tr><td class=\"firstCol\"><b>" + COPYRIGHT + "</b></td><td class=\"secondCol\">" + 
                	DEVELOPER + 
                "</td></tr>" +
                "</table>" +
                "</body>" +
                "</html>"
        );

        // Add Hyperlink listener to process hyperlinks
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            editorPane.setToolTipText(e.getURL().toExternalForm());
                        }
                    });
                } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {                            
                            SwingUtilities.getWindowAncestor(editorPane).setCursor(Cursor.getDefaultCursor());
                            editorPane.setToolTipText(null);
                        }
                    });
                } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {       
                	    FileUtil.openUrl(e.getURL().toString(), AboutAction.class);                       
                }
            }
        });        
        editorPane.addMouseListener(mouseListener);
        JScrollPane sp = new JScrollPane(editorPane);       
        return sp;
    }
    
    
}
