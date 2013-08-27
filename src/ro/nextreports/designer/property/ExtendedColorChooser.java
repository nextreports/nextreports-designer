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
package ro.nextreports.designer.property;


import ro.nextreports.engine.template.ChartTemplate;
import ro.nextreports.engine.template.ReportTemplate;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import ro.nextreports.designer.ui.ExcelColorChooserPanel;
import ro.nextreports.designer.ui.HistoryColorChooserPanel;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Locale;
import java.util.List;

/**
 * User: mihai.panaitescu
 * Date: 14-May-2010
 * Time: 13:01:33
 */
public class ExtendedColorChooser {
	
	// global chooser to keep track of recent selected colors
	private static JColorChooser globalChooser;
	private static HistoryColorChooserPanel historyPanel = new HistoryColorChooserPanel();
	public static boolean addToHistory = true;

    public static Color showDialog(Component parent, String title, Color initialColor) {
        
		if (globalChooser == null) {			
			if (initialColor != null) {
				globalChooser = new JColorChooser(initialColor);
			} else {
				globalChooser = new JColorChooser();
			}
			addChooserPanels(globalChooser);			
		} else {			
			if (initialColor != null) {
				globalChooser.setColor(initialColor);
			}
		}
		globalChooser.setLocale(Locale.getDefault());
       
        ColorTracker ok = new ColorTracker(globalChooser);
        JDialog dialog = JColorChooser.createDialog(parent, I18NSupport.getString("color.dialog.title"), true, globalChooser, ok, null);
        dialog.setVisible(true);
        return ok.getColor();
    }

    private static class ColorTracker implements ActionListener, Serializable {
        JColorChooser chooser;
        Color color;

        public ColorTracker(JColorChooser c) {
            chooser = c;
        }

        public void actionPerformed(ActionEvent e) {
            color = chooser.getColor();
            if (addToHistory) {
            	historyPanel.addColorToHistory(color);
            } else {
            	addToHistory = true;
            }
        }

        public Color getColor() {
            return color;
        }
    }
    
    private static void addChooserPanels(JColorChooser chooser) {
    	AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
        AbstractColorChooserPanel[] newPanels = new AbstractColorChooserPanel[oldPanels.length+2];        
        newPanels[0] =  new ExcelColorChooserPanel();
        newPanels[newPanels.length-1] = historyPanel; 
        System.arraycopy(oldPanels, 0, newPanels, 1, oldPanels.length);
        chooser.setChooserPanels(newPanels);
    }        
    
    public static void loadColorsFromChartTemplate(ChartTemplate template) {
    	    	    	    	
    	List<Color> foregrounds = template.getForegrounds();
    	if (foregrounds != null) {
    		for (Color color : foregrounds) {
    			historyPanel.addColorToHistory(color);
    		}
    	}
    	 
    	historyPanel.addColorToHistory(template.getBackground());
    	historyPanel.addColorToHistory(template.getTitleColor());
    	historyPanel.addColorToHistory(template.getxAxisColor());
    	historyPanel.addColorToHistory(template.getxGridColor());
    	historyPanel.addColorToHistory(template.getxLabelColor());
    	historyPanel.addColorToHistory(template.getxLegendColor());
    	historyPanel.addColorToHistory(template.getyAxisColor());
    	historyPanel.addColorToHistory(template.getyGridColor());
    	historyPanel.addColorToHistory(template.getyLabelColor());
    	historyPanel.addColorToHistory(template.getyLegendColor());    	    	
    }
    
    public static void loadColorsFromReportTemplate(ReportTemplate template) {    	
    	historyPanel.addColorToHistory(template.getTitleBand().getForeground());
    	historyPanel.addColorToHistory(template.getTitleBand().getBackground());
    	historyPanel.addColorToHistory(template.getHeaderBand().getForeground());
    	historyPanel.addColorToHistory(template.getHeaderBand().getBackground());
    	historyPanel.addColorToHistory(template.getDetailBand().getForeground());
    	historyPanel.addColorToHistory(template.getDetailBand().getBackground());
    	historyPanel.addColorToHistory(template.getFooterBand().getForeground());
    	historyPanel.addColorToHistory(template.getFooterBand().getBackground());
    }
    	

}
