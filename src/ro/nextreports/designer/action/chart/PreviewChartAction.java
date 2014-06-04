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
package ro.nextreports.designer.action.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.chart.ChartWebServer;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.i18n.action.I18nManager;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.EngineProperties;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartRunner;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.i18n.I18nLanguage;
import ro.nextreports.engine.i18n.I18nUtil;

/**
 * User: mihai.panaitescu
 * Date: 18-Dec-2009
 * Time: 16:42:54
 */
// open-flash-chart.swf from zip chart-webroot.zip is the downloaded from here :
//     http://ofc2dz.com/OFC2/downloads/ofc2Downloads.html  version OFC2Patches-DZ-Ichor.zip
//     The other patched version OFC2Patches-DZ-Hyperion.zip shows horizontal bar (with more Y columns)
//     as a stack instead more bars one besides other   
//
//  JOFC Patch 2 -> 23.09.2011 
//             - delay & cascade for AnimatedElement.OnShow
//             - String 'area' for constructor AreaLineChart (instead of 'area_line')
public class PreviewChartAction extends AbstractAction {
	
	private static final Log LOG = LogFactory.getLog(PreviewChartAction.class);

    private Chart chart;
    private Thread executorThread;
    private boolean loaded;
    private List<QueryParameter> oldParameters;
    private boolean stop = false; 
    private String chartRunnerType;
    private byte chartGraphicType;
    private boolean supported = true;

    public PreviewChartAction(String chartRunnerType, byte chartGraphicType) {
    	String image = "chart_preview_flash";
    	if (ChartRunner.IMAGE_FORMAT.equals(chartRunnerType)) {
    		image = "chart_preview_image";
    	} else if (ChartRunner.HTML5_TYPE == chartGraphicType) {
    		image = "chart_preview_html5";
    	}
        Icon icon = ImageUtil.getImageIcon(image);
        putValue(Action.SMALL_ICON, icon);
        String descKey = "preview.chart.flash";
        if (ChartRunner.IMAGE_FORMAT.equals(chartRunnerType)) {
        	descKey = "preview.chart.image";
        } else if (ChartRunner.HTML5_TYPE == chartGraphicType) {
        	descKey = "preview.chart.html5";
    	}
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString(descKey));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString(descKey));
        this.chartRunnerType = chartRunnerType;
        this.chartGraphicType = chartGraphicType;
        loaded = true;                
    }

    // called from Tree (chart not loaded in designer)
    public PreviewChartAction(String chartRunnerType,  byte chartGraphicType, String name) {
        this(chartRunnerType, chartGraphicType);
        putValue(Action.NAME, name);
        loaded = false;
    }

    public void setChart(Chart chart) {
    	if (ChartType.hasNoFlashSupport(chart.getType().getType())) {
    		supported = false;
    	}
        this.chart = chart;
        if (!loaded) {
            oldParameters = ParameterManager.getInstance().getParameters();
            ParameterManager.getInstance().setParameters(chart.getReport().getParameters());            
        }
        I18nManager.getInstance().setKeys(chart.getI18nkeys());
        I18nManager.getInstance().setLanguages(chart.getLanguages());
    }        

    public boolean isSupported() {
		return supported;
	}

	public void actionPerformed(ActionEvent event) {

        executorThread = new Thread(new Runnable() {

            public void run() {

                if (ChartUtil.chartUndefined(chart)) {
                    return;
                }

                ParametersBean pBean = NextReportsUtil.selectParameters(chart.getReport(), null);
                if (pBean == null) {
                    return;
                }

                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("preview.chart.execute"));
                activator.start(new PreviewStopAction());

                ChartWebServer webServer = ChartWebServer.getInstance();
                String webRoot = webServer.getWebRoot();

                ChartRunner runner = new ChartRunner();
                runner.setFormat(chartRunnerType);
                runner.setGraphicType(chartGraphicType);
                runner.setChart(chart);                
                runner.setQueryTimeout(Globals.getQueryTimeout());
                runner.setParameterValues(pBean.getParamValues());
                I18nLanguage language = I18nUtil.getDefaultLanguage(chart);
                if (language != null) {                	
                	runner.setLanguage(language.getName());
                }	
                if (ChartRunner.IMAGE_FORMAT.equals(runner.getFormat())) {
                	runner.setImagePath(Globals.USER_DATA_DIR + "/reports");
                }
                try {         
                	DataSource runDS = Globals.getChartLayoutPanel().getRunDataSource();
                	boolean csv = runDS.getDriver().equals(CSVDialect.DRIVER_CLASS); 
                	runner.setConnection(Globals.createTempConnection(runDS), csv);
                    if (ChartRunner.IMAGE_FORMAT.equals(runner.getFormat())) {
                    	runner.run();
                    	JDialog dialog = new JDialog(Globals.getMainFrame(), "");
                    	dialog.setBackground(Color.WHITE);
                    	dialog.setLayout(new BorderLayout());
                    	ShowImagePanel panel = new ShowImagePanel(runner.getChartImageAbsolutePath());                  
                        dialog.add(panel, BorderLayout.CENTER);
                        dialog.pack();     
                        dialog.setResizable(false);
                        Show.centrateComponent(Globals.getMainFrame(), dialog);
                        dialog.setVisible(true);                    	
					} else {
						String jsonFile = "data.json";
						if (ChartRunner.HTML5_TYPE == runner.getGraphicType()) {
							jsonFile = "data-html5.json";
						}
						OutputStream outputStream = new FileOutputStream(webRoot + File.separatorChar + jsonFile);
	                    boolean result = runner.run(outputStream);
	                    outputStream.close();
						if (result) {
							if (!webServer.isStarted()) {
								webServer.start();
							}
							if (ChartRunner.HTML5_TYPE == runner.getGraphicType()) {
								FileUtil.openUrl("http://localhost:" + Globals.getChartWebServerPort() + "/chart-html5.html", PreviewChartAction.class);
							} else {
								FileUtil.openUrl("http://localhost:" + Globals.getChartWebServerPort() + "/chart.html?ofc=data.json", PreviewChartAction.class);
							}
						}
                    }

                } catch (NoDataFoundException e) {
                    Show.info(I18NSupport.getString("run.nodata"));
                } catch (InterruptedException e) {
                    Show.dispose();  // close a possible previous dialog message
                    Show.info(I18NSupport.getString("preview.cancel"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Show.error(e);
                } finally {
                    stop = false;
                    if (activator != null) {
                        activator.stop();
                        activator = null;
                    }

                    // restore old parameters if chart was runned from tree
                    if (oldParameters != null) {
                        ParameterManager.getInstance().setParameters(oldParameters);
                    }
                }
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.setPriority(EngineProperties.getRunPriority());
        executorThread.start();
    }
    
    private class PreviewStopAction extends AbstractAction {

            public PreviewStopAction() {
                super();
                putValue(Action.NAME, I18NSupport.getString("preview.stop.action.name"));
                putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("stop_execution"));
                putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("preview.stop.action.desc"));
                putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("preview.stop.action.desc"));
                putValue(Action.MNEMONIC_KEY, new Integer('S'));
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
            }

            public void actionPerformed(ActionEvent e) {
                if  (stop) {
                    Show.disposableInfo(I18NSupport.getString("stop.wait.second"));
                    return;
                } else {
                    Show.disposableInfo(I18NSupport.getString("stop.wait"));
                }
                if (executorThread != null) {
                    stop = true;
                    executorThread.interrupt();
                }
            }
        }
    
	public class ShowImagePanel extends JPanel {
		
		private BufferedImage image;
		
		public ShowImagePanel(String imagePath) {
			try {								
				image = ImageIO.read(new File(imagePath));
			} catch (IOException ie) {
				System.out.println("Error:" + ie.getMessage());
			}
		}

		public void paint(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(image.getWidth(), image.getHeight());
		}
		
		
				
	}


}
