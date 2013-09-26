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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReleaseInfo;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.engine.util.ReportUtil;

/**
 * Checks if a new version of NextReports Designer was released on github
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 26.09.2013
 */
public class CheckForUpdateAction extends AbstractAction {

	private String url = "https://github.com/nextreports/nextreports-designer/releases";
	private String releaseRegex = "release-([\\d\\.]+)\\.zip";	
	private String versionNotFound = "NA";
	private String indent = "&nbsp;&nbsp;&nbsp;&nbsp;";
	private JDialog dlg;

	public CheckForUpdateAction() {
		putValue(Action.NAME, I18NSupport.getString("update.check"));
		putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("update"));
		putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("update.check"));
		putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("update.check"));
	}

	public void actionPerformed(ActionEvent event) {
		
		Thread executorThread = new Thread(new Runnable() {

			public void run() {

				UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("update.check"));
				activator.start();

				final StringBuilder sb = new StringBuilder();

				try {
					System.setProperty("java.net.useSystemProxies", "true");
					String currentVersion = ReleaseInfo.getVersion();
					String lastVersion = getLastVersion(url);

					sb.append("<HTML><b><br>").append(indent)
							.append(I18NSupport.getString("update.check.current", currentVersion)).append("<br><br>");

					if (versionNotFound.equals(lastVersion)) {
						sb.append(indent).append(I18NSupport.getString("update.check.uptodate"));
					} else {
						int status = ReportUtil.compareVersions(currentVersion, lastVersion);
						if (status < 0) {
							sb.append(indent)
									.append(I18NSupport.getString("update.check.newversion", lastVersion))
									.append("<br><br>")
									.append(indent)
									.append("<font color=\"#0000A0\"><a href=\"http://www.next-reports.com/index.php/download.html\">")
									.append(I18NSupport.getString("download.name")).append("</a></font>");
						}
					}
				} finally {
					sb.append("</b></HTML>");
					activator.stop();
				}
												
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JComponent component = createPanel(sb.toString());
						dlg = new JDialog(Globals.getMainFrame(), I18NSupport.getString("update.check"), true);
						dlg.setResizable(false);
						dlg.setBackground(new Color(234, 241, 248));
						dlg.setLayout(new GridBagLayout());
						dlg.add(component, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
								GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
						dlg.pack();
						Show.centrateComponent(Globals.getMainFrame(), dlg);
						dlg.setVisible(true);
					}
				});								
			}
		}, "NEXT : " + getClass().getSimpleName());
		executorThread.start();
	}

	private String getLastVersion(String url) {
		try {
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);

			StringBuffer sbuf = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

			try {
				String res = in.readLine();
				while ((res = in.readLine()) != null) {
					sbuf.append(res);
				}
			} finally {
				in.close();
			}

			String html = sbuf.toString();
			Pattern p = Pattern.compile(releaseRegex);
			Matcher m = p.matcher(html);
			// first found is last release
			if (m.find()) {
				String release = m.group(1);
				return release;
			}
			return versionNotFound;
		} catch (IOException ex) {
			return versionNotFound;
		}
	}
	
	protected MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent me) {
            dlg.dispose();
        }
    };
	
	private JComponent createPanel(String html) {
    	System.setProperty("awt.useSystemAAFontSettings", "on");
    	final JEditorPane editorPane = new JEditorPane();    	
    	HTMLEditorKit kit = new HTMLEditorKit();
    	editorPane.setEditorKit(kit);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(new Font("Arial", Font.PLAIN, 12));
        editorPane.setPreferredSize(new Dimension(350, 120));
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setBackground(new Color(234, 241, 248));        
       
        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);
        editorPane.setText(html);

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
