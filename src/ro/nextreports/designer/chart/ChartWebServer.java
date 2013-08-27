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
package ro.nextreports.designer.chart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.FileUtil;

import winstone.Launcher;


/**
 * See http://winstone.sourceforge.net for more details about winstone.
 *
 * @author Decebal Suiu
 */
public class ChartWebServer {

	private static final Log LOG = LogFactory.getLog(ChartWebServer.class);
	private static final String WEB_ROOT = Globals.USER_DATA_DIR + "/chart-webroot";
	
	private static ChartWebServer singleton;
	
	private boolean started;
    private Launcher launcher;

    private ChartWebServer() {
	}
	
	public static ChartWebServer getInstance() {
		if (singleton == null) {
			singleton = new ChartWebServer();
		}
		
		try {
			singleton.init();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
		
		return singleton;
	}
	
	public void start() {
		if (started) {
			LOG.error("Chart web server already started");
			return;
		}
		
		int port = Globals.getChartWebServerPort();
		
		// set the winstone arguments
		Map<String, String> winstoneArguments = new HashMap<String, String>();
		winstoneArguments.put("webroot", WEB_ROOT);
		winstoneArguments.put("httpPort", String.valueOf(port));
		winstoneArguments.put("ajp13Port", "-1");
		winstoneArguments.put("httpListenAddress", "127.0.0.1");

		// run the winstone
		try {
//			Launcher.initLogger(winstoneArguments);
			launcher = new Launcher(winstoneArguments);
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return;
		}
		
		started = true;
		LOG.info("Chart web server started on port " + port + " (accept connections only from localhost)");
	}

    public void stop() {
        launcher.shutdown();
        started = false;
        LOG.info("Chart web server stopped.");
    }

    public void restart() {
        stop();
        start();
    }

    public String getWebRoot() {
		return WEB_ROOT;
	}

	public boolean isStarted() {
		return started;
	}

	private void init() throws IOException {
		File webRoot = new File(WEB_ROOT);
		if (webRoot.exists() && webRoot.isDirectory()) {
			return;
		}
		
		// create and populate the webroot folder
		webRoot.mkdirs();
		
        InputStream input = ChartWebServer.class.getResourceAsStream("/chart-webroot.zip");
        if (input == null) {
            // cannot restore the workspace
        	LOG.error("Resource '/chart-webroot' not found." );
            return;
        }
        
        // deployment
        LOG.debug("Deployment mode - copy from jar (/chart-webroot.zip)");
        ZipInputStream zipInputStream = new ZipInputStream(input);
        FileUtil.unzip(zipInputStream, WEB_ROOT);
        
        if (!new File(WEB_ROOT, "chart.html").exists()) {        	
        	// development (idea or eclipse - no jar)
        	LOG.warn("Development mode - copy from zip (src/chart-webroot.zip)");
        	FileUtil.unzip("src/chart-webroot.zip", WEB_ROOT);
        }
	}
	
}
