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
package ro.nextreports.designer;

import java.util.ArrayList;
import java.util.List;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.condition.BandElementCondition;
import ro.nextreports.engine.condition.ConditionalExpression;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.util.IndicatorData;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 11, 2008
 * Time: 12:19:57 PM
 */
public class ReportLayoutFactory {

    public static ReportLayout create(List<String> columnNames, String title) {

        ReportLayout reportLayout = new ReportLayout();
        reportLayout.setReportType(ResultExporter.DEFAULT_TYPE);
        int size = columnNames.size();
//        System.out.println("size=" + size);

        Band headerBand = reportLayout.getHeaderBand();
        List<BandElement> titleRow = new ArrayList<BandElement>();
        BandElement titleElement = new BandElement(title);
        titleElement.setColSpan(size);
        titleElement.setHorizontalAlign(BandElement.CENTER);
        titleElement.setVerticalAlign(BandElement.MIDDLE);
        titleElement.setPadding(new Padding(1,1,1,1));
        titleRow.add(titleElement);
        for (int i = 0; i < size - 1; i++) {
            titleRow.add(null);
        }
        List<List<BandElement>> headerElements = new ArrayList<List<BandElement>>();
        headerElements.add(titleRow);

        Band detailBand = reportLayout.getDetailBand();
        List<BandElement> headerNamesRow = new ArrayList<BandElement>(size);
        List<BandElement> headerFieldsRow = new ArrayList<BandElement>(size);
        for (String column : columnNames) {
        	BandElement he = new BandElement(column);
        	he.setPadding(new Padding(1,1,1,1));
            headerNamesRow.add(he);
            BandElement ce = new ColumnBandElement(column);
            ce.setPadding(new Padding(1,1,1,1));
            headerFieldsRow.add(ce);
        }
        List<List<BandElement>> detailElements = new ArrayList<List<BandElement>>();
        headerElements.add(headerNamesRow);
        detailElements.add(headerFieldsRow);
        
        Band footerBand = reportLayout.getFooterBand();
        List<List<BandElement>> footerElements = new ArrayList<List<BandElement>>();
        List<BandElement> footerRow = new ArrayList<BandElement>();
        for (int i = 0; i < size; i++) {
        	footerRow.add(new BandElement(""));
        }
        footerElements.add(footerRow);

        headerBand.setElements(headerElements);
        detailBand.setElements(detailElements);
        footerBand.setElements(footerElements);               

        return reportLayout;
    }
    
    public static ReportLayout createTable(List<String> columnNames) {
    	ReportLayout reportLayout = new ReportLayout();
    	reportLayout.setReportType(ResultExporter.TABLE_TYPE);
        int size = columnNames.size();

        Band headerBand = reportLayout.getHeaderBand();       
        List<List<BandElement>> headerElements = new ArrayList<List<BandElement>>();        

        Band detailBand = reportLayout.getDetailBand();
        List<BandElement> headerNamesRow = new ArrayList<BandElement>(size);
        List<BandElement> headerFieldsRow = new ArrayList<BandElement>(size);
        
        for (String column : columnNames) {
        	BandElement he = new BandElement(column);
        	he.setPadding(new Padding(1,1,1,1));
            headerNamesRow.add(he);
            BandElement ce = new ColumnBandElement(column);
            ce.setPadding(new Padding(1,1,1,1));
            headerFieldsRow.add(ce);
        }
        List<List<BandElement>> detailElements = new ArrayList<List<BandElement>>();
        headerElements.add(headerNamesRow);
        detailElements.add(headerFieldsRow);
               
        headerBand.setElements(headerElements);
        detailBand.setElements(detailElements);                    

        return reportLayout;
    }
    
    public static ReportLayout createAlarm(String column, FormattingConditions formattingConditions, List<String> messages) {
    	ReportLayout reportLayout = new ReportLayout();
    	reportLayout.setReportType(ResultExporter.ALARM_TYPE);
        
        Band detailBand = reportLayout.getDetailBand();
        List<BandElement> row = new ArrayList<BandElement>(2);
        
        BandElement ce = new ColumnBandElement(column);
        ce.setPadding(new Padding(1,1,1,1));
        ce.setFormattingConditions(formattingConditions);
        row.add(ce);
                
        BandElement ee = new ExpressionBandElement("Expr", getAlarmExpressionText(column, formattingConditions, messages));
        ee.setPadding(new Padding(1,1,1,1));        
        row.add(ee);
                
        List<List<BandElement>> detailElements = new ArrayList<List<BandElement>>();        
        detailElements.add(row);               
        detailBand.setElements(detailElements);                    

        return reportLayout;
    }
    
	public static ReportLayout createIndicator(String column, IndicatorData data) {
		ReportLayout reportLayout = new ReportLayout();
		reportLayout.setReportType(ResultExporter.INDICATOR_TYPE);

		Band headerBand = reportLayout.getHeaderBand();
		List<List<BandElement>> headerElements = new ArrayList<List<BandElement>>();
		List<BandElement> firstHeaderRow = new ArrayList<BandElement>(3);
		List<BandElement> secondHeaderRow = new ArrayList<BandElement>(3);

		firstHeaderRow.add(new BandElement(data.getTitle()));
		firstHeaderRow.add(new BandElement(data.getDescription()));
		firstHeaderRow.add(new BandElement(data.getUnit()));
		
		secondHeaderRow.add(new BandElement(String.valueOf(data.getMin())));
		secondHeaderRow.add(new BandElement(String.valueOf(data.getMax())));
		secondHeaderRow.add(new BandElement(String.valueOf(data.isShowMinMax())));
		
		headerElements.add(firstHeaderRow);
		headerElements.add(secondHeaderRow);
		
		headerBand.setElements(headerElements);
		
		Band detailBand = reportLayout.getDetailBand();
        List<BandElement> row = new ArrayList<BandElement>(1);
        
        BandElement ce = new ColumnBandElement(column);
        ce.setPadding(new Padding(1,1,1,1));     
        ce.setForeground(data.getColor());
        row.add(ce);
        
        List<List<BandElement>> detailElements = new ArrayList<List<BandElement>>();        
        detailElements.add(row);               
        detailBand.setElements(detailElements);   

		return reportLayout;
	}
    
    private static String getAlarmExpressionText(String column, FormattingConditions formattingConditions, List<String> messages) {
    	StringBuilder sb = new StringBuilder();
    	int index = 0;
    	List<BandElementCondition> conds = formattingConditions.getConditions();
    	int size = conds.size();
    	for (BandElementCondition bec : conds) {
    		if (index > 0) {
    			appendSpaces(sb, index-1);
    			sb.append("else { \n"); 
    		}
    		if (index < size-1) {
    			appendSpaces(sb, index);
    			sb.append("if ( $C_").append(column).append(" ");    		
    			ConditionalExpression ce = bec.getExpression();
    			boolean interval = ce.getOperator().equals("[]");
    			if (interval) {
    				sb.append("<=");
    			} else {
    				sb.append(ce.getOperator());
    			}
    			sb.append(" ");
    			if (interval) {
    				sb.append(ce.getRightOperand2());
    			} else {
    				sb.append(ce.getRightOperand());
    			}
    			sb.append(") {\n");
    		}
    		appendSpaces(sb, index+2);
    		sb.append("\"");
    		String m = messages.get(index);
    		int colIndex = m.indexOf("$C_" + column);
    		int length = ("$C_" + column).length();
    		if (colIndex == -1) {
    			sb.append(messages.get(index));
    		} else {
    			sb.append(m.substring(0, colIndex));
    			sb.append("\" + $C_");
    			sb.append(column);
    			sb.append(" + \"");
    			sb.append(m.substring(colIndex + length));
    		}
    		sb.append("\" }\n");
    		
    		index++;
    	}
    	index = index-2;
    	for (int i=size-2; i>-0; i--) {
    		appendSpaces(sb, index);
    		index = index-2;
    		sb.append("}\n");
    	}
    	
    	return sb.toString();
    }
    
    private static void appendSpaces(StringBuilder sb , int no) {
    	for (int i=0; i<no; i++) {
    		sb.append(" ");
    	}
    }
}
