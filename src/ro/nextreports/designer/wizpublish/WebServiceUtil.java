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
package ro.nextreports.designer.wizpublish;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.server.api.client.ChartMetaData;
import ro.nextreports.server.api.client.ErrorCodes;
import ro.nextreports.server.api.client.FileMetaData;
import ro.nextreports.server.api.client.ReportMetaData;
import ro.nextreports.server.api.client.WebServiceClient;
import ro.nextreports.server.api.client.WebServiceException;

public class WebServiceUtil {
	
	private static Log LOG = LogFactory.getLog(WebServiceUtil.class);
	
	public static WebServiceResult publishReport(WebServiceClient client, 
			String serverPath, String serverDataSourcePath,	String description, 
			String localPath) {
		
    	ReportMetaData reportMetaData = new ReportMetaData();
        reportMetaData.setPath(serverPath);
        reportMetaData.setDescription(description);
        reportMetaData.setDataSourcePath(serverDataSourcePath);                
        
        File reportFile = new File(localPath);
        try {
            Report report = ReportUtil.loadConvertedReport(new FileInputStream(reportFile));
            reportMetaData.setSpecialType(report.getLayout().getReportType());
            List<String> images = ReportUtil.getStaticImages(report);
            String prefix = new File(localPath).getParentFile().getAbsolutePath();
            List<FileMetaData> list = new ArrayList<FileMetaData>();
            for (String image : images) {
                FileMetaData fmd = new FileMetaData();
                fmd.setFile(new File(prefix + File.separator + image));
                list.add(fmd);
            }
            reportMetaData.setImages(list);
            
            String template = report.getLayout().getTemplateName();
            if ((template != null) && !"".equals(template.trim())) {
            	FileMetaData fmd = new FileMetaData();
            	fmd.setFile(new File(prefix + File.separator + template));
            	reportMetaData.setTemplate(fmd);
            }            
            
            // report meta data contains the xml which may be not deserializable on the server if local version is greater than server one
            // so we must have a separate method to test version before doing publishReport(reportMetaData)!
            byte status = client.getVersionStatus(report.getVersion());
            boolean verified = false;
            String message = "";
            if (status == ErrorCodes.OLD_REPORT_VERSION) {
                verified = true;
                message = I18NSupport.getString("wizard.publish.older", I18NSupport.getString("report"));                    
            } else if (status == ErrorCodes.NEW_REPORT_VERSION) {
                verified = true;
                message = I18NSupport.getString("wizard.publish.newer", I18NSupport.getString("report"));
            } 
            if (verified) {
            	LOG.error("Publish report " + report.getName() + " version " + report.getVersion() + "different from server!");
            	return new WebServiceResult(true, message);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }

        try {        	        	        	
            reportMetaData.setFile(reportFile);
            client.publishReport(reportMetaData);
            return new WebServiceResult(false, I18NSupport.getString("wizard.publish.success", I18NSupport.getString("report.name")));            
        } catch (Exception e) {
            e.printStackTrace();
            boolean verified = false;
            String message = "";
            if (e instanceof WebServiceException) {            	
                int status = ((WebServiceException) e).getClientResponse().getStatus();
                if (status == ErrorCodes.OLD_REPORT_VERSION) {
                    verified = true;
                    message = I18NSupport.getString("wizard.publish.older", I18NSupport.getString("report"));                    
                } else if (status == ErrorCodes.NEW_REPORT_VERSION) {
                    verified = true;
                    message = I18NSupport.getString("wizard.publish.newer", I18NSupport.getString("report"));
                } else if (status == ErrorCodes.PATH_NOT_FOUND) {
                    verified = true;
                    message = I18NSupport.getString("wizard.publish.path.notfound");
                } else if (status == ErrorCodes.REPORT_PATH_NOT_FOUND) {
                    verified = true;
                    message = I18NSupport.getString("wizard.publish.path.report.notfound");
                } else if (status == ErrorCodes.DATASOURCE_PATH_NOT_FOUND) {
                    verified = true;
                    message = I18NSupport.getString("wizard.publish.path.datasource.notfound");
                }
            }
            if (!verified) {
            	return new WebServiceResult(true, I18NSupport.getString("wizard.publish.error", I18NSupport.getString("report.name")) + " : " + e.getMessage());                
            } else {
            	return new WebServiceResult(true, message);
            }
        }         
    }
	
	public static WebServiceResult publishChart(WebServiceClient client, 
				String serverPath, String serverDataSourcePath, 
				String description, String localPath) {
		 
	    	ChartMetaData chartMetaData = new ChartMetaData();	        
	        chartMetaData.setPath(serverPath);
	        chartMetaData.setDescription(description);
	        chartMetaData.setDataSourcePath(serverDataSourcePath);

	        File reportFile = new File(localPath);
	        
			try {
				Chart chart = ChartUtil.loadChart(new FileInputStream(reportFile));
				byte status = client.getVersionStatus(chart.getVersion());
				boolean verified = false;
				String message = "";
				if (status == ErrorCodes.OLD_CHART_VERSION) {
					verified = true;
					message = I18NSupport.getString("wizard.publish.older", I18NSupport.getString("chart"));
				} else if (status == ErrorCodes.NEW_CHART_VERSION) {
					verified = true;
					message = I18NSupport.getString("wizard.publish.newer", I18NSupport.getString("chart"));
				}
				if (verified) {
					LOG.error("Publish chart " + chart.getName() + " version " + chart.getVersion() + " different from server!");
					return new WebServiceResult(true, message);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
			}

	        try {
	            chartMetaData.setFile(reportFile);
	            client.publishChart(chartMetaData);
	            return new WebServiceResult(false, I18NSupport.getString("wizard.publish.success", I18NSupport.getString("chart.name")));
	        } catch (Exception e) {
	            e.printStackTrace();
	            boolean verified = false;
	            String message = "";
	            if (e instanceof WebServiceException) {
	                int status = ((WebServiceException) e).getClientResponse().getStatus();
	                if (status == ErrorCodes.OLD_CHART_VERSION) {
	                    verified = true;
	                    message = I18NSupport.getString("wizard.publish.older", I18NSupport.getString("chart"));
	                } else if (status == ErrorCodes.NEW_CHART_VERSION) {
	                    verified = true;
	                    message = I18NSupport.getString("wizard.publish.newer", I18NSupport.getString("chart"));
	                } else if (status == ErrorCodes.PATH_NOT_FOUND) {
	                    verified = true;
	                    message = I18NSupport.getString("wizard.publish.path.notfound");
	                } else if (status == ErrorCodes.CHART_PATH_NOT_FOUND) {
	                    verified = true;
	                    message = I18NSupport.getString("wizard.publish.path.chart.notfound");
	                } else if (status == ErrorCodes.DATASOURCE_PATH_NOT_FOUND) {
	                    verified = true;
	                    message = I18NSupport.getString("wizard.publish.path.datasource.notfound");
	                }
	            }
	            if (!verified) {
	            	return new WebServiceResult(true, I18NSupport.getString("wizard.publish.error", I18NSupport.getString("chart.name")) + " : " + e.getMessage());
	            } else {
	            	return new WebServiceResult(true, message);
	            }
	        } 
	    }

}
