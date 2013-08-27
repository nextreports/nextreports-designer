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
package ro.nextreports.designer.template.chart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.template.ChartTemplate;
import com.thoughtworks.xstream.XStream;

public class TemplateManager {
	
	private static final Log LOG = LogFactory.getLog(TemplateManager.class);
	
	public static void saveTemplate(ChartTemplate template, String path) throws Exception {
        XStream xstream = XStreamFactory.createChartTemplateXStream();        
        Writer writer = new FileWriter(path);
        xstream.toXML(template, writer);
        writer.close();
    }
	
	public static ChartTemplate getTemplate(Chart chart) {
		ChartTemplate template = new ChartTemplate();
		template.setVersion(ReleaseInfoAdapter.getVersionNumber());
        template.setBackground(chart.getBackground());
        template.setForegrounds(chart.getForegrounds());
        template.setTitleColor(chart.getTitle().getColor());
        template.setxAxisColor(chart.getxAxisColor());
        template.setyAxisColor(chart.getyAxisColor());
        template.setxLabelColor(chart.getXColor());
        template.setyLabelColor(chart.getYColor());
        template.setxLegendColor(chart.getXLegend().getColor());
        template.setyLegendColor(chart.getYLegend().getColor());
        template.setxGridColor(chart.getXGridColor());
        template.setyGridColor(chart.getYGridColor());
        return template;
    }
	
	public static ChartTemplate loadTemplate(File file) {
        if (file == null) {
            return null;
        }        
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            XStream xstream = XStreamFactory.createChartTemplateXStream();
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "UTF-8");
            return (ChartTemplate) xstream.fromXML(reader);
        } catch (Exception e1) {
            LOG.error(e1.getMessage(), e1);
            e1.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    LOG.error(e1.getMessage(), e1);
                    e1.printStackTrace();
                }
            }
        }
    }
	
	public static void applyGeneralTemplate(Chart chart, ChartTemplate template) {
		chart.setBackground(template.getBackground());
		chart.setForegrounds(template.getForegrounds());
		chart.getTitle().setColor(template.getTitleColor());
		chart.setxAxisColor(template.getxAxisColor());
		chart.setyAxisColor(template.getyAxisColor());
		chart.setXColor(template.getxLabelColor());
		chart.setYColor(template.getyLabelColor());
		chart.setXGridColor(template.getxGridColor());
		chart.setYGridColor(template.getyGridColor());
		chart.getXLegend().setColor(template.getxLegendColor());
		chart.getYLegend().setColor(template.getyLegendColor());		
		Globals.getMainFrame().getQueryBuilderPanel().loadChart(chart);
	}


}
