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
package ro.nextreports.designer.template.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.InputStreamReader;
import java.util.List;
import java.awt.*;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.RowElement;
import ro.nextreports.engine.util.xstream.FontConverter;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 22, 2007
 * Time: 5:48:32 PM
 */
public class TemplateManager {

    private static final Log LOG = LogFactory.getLog(TemplateManager.class);

    public static void saveTemplate(ReportTemplate template, String path) throws Exception {

        XStream xstream = XStreamFactory.createTemplateXStream();
        xstream.registerConverter(new FontConverter());
        Writer writer = new FileWriter(path);
        xstream.toXML(template, writer);
        writer.close();
    }

    public static ReportTemplate getTemplate(ReportLayout layout) {

        ReportTemplate template = new ReportTemplate();
        BandElement title = layout.getHeaderBand().getElements().get(0).getElements().get(0);
        template.setTitleBand(title);

        BandElement header = layout.getDetailBand().getRow(0).get(0);
        template.setHeaderBand(header);

        BandElement detail = layout.getDetailBand().getRow(1).get(0);
        template.setDetailBand(detail);
                
        BandElement footer = layout.getFooterBand().getRow(0).get(0);
        template.setFooterBand(footer);

        return template;
    }

    public static ReportTemplate loadTemplate(File file) {
        if (file == null) {
            return null;
        }
//        System.out.println("***path="+file.getAbsolutePath());
        FileInputStream fis = null;
        InputStreamReader reader = null;
        try {
            XStream xstream = XStreamFactory.createTemplateXStream();
            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, "UTF-8");
            return (ReportTemplate) xstream.fromXML(reader);
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

    public static void restoreBandElement(BandElement current, BandElement from) {
        if (current == null) {
            return;
        }
        current.setBackground(from.getBackground());
        current.setBorder(from.getBorder());
        current.setFont(from.getFont());
        current.setForeground(from.getForeground());
        current.setHorizontalAlign(from.getHorizontalAlign());
        current.setVerticalAlign(from.getVerticalAlign());
        current.setPadding(from.getPadding());
    }    

    // try to apply a basic template on any report
    public static void applyGeneralTemplate(ReportLayout layout, ReportTemplate template) {

        boolean dirty =false;
        Band headerBand = layout.getHeaderBand();
        if (headerBand != null) {
            List<RowElement> headerRows = headerBand.getElements();
            // title
            if (headerRows.size() > 0) {                    
                RowElement titleElements = headerRows.get(0);
                for (BandElement titleElement : titleElements.getElements()) {
                    dirty=true;
                    TemplateManager.restoreBandElement(titleElement, template.getTitleBand());
                }
            }
            // header
            if (headerRows.size() > 1) {
                RowElement headerElems = headerRows.get(1);
                for (BandElement headerElem : headerElems.getElements()) {
                    dirty=true;
                    TemplateManager.restoreBandElement(headerElem, template.getHeaderBand());
                }
            }
        }

        // detail
        Band detailBand = layout.getDetailBand();
        if (detailBand !=  null) {
            List<RowElement> detailRows = detailBand.getElements();
            if (detailRows.size() > 0) {
                List<BandElement> detailElems = detailBand.getRow(0);
                for (BandElement detailElem : detailElems) {
                    dirty=true;
                    TemplateManager.restoreBandElement(detailElem, template.getDetailBand());
                }
            }
        }
        
        // footer
        Band footerBand = layout.getFooterBand();
        if (footerBand !=  null) {
            List<RowElement> footerRows = footerBand.getElements();
            if (footerRows.size() > 0) {
                List<BandElement> footerElems = footerBand.getRow(0);
                for (BandElement footerElem : footerElems) {
                    dirty=true;
                    TemplateManager.restoreBandElement(footerElem, template.getFooterBand());
                }
            }
        }


        if (dirty) {
            Globals.getMainFrame().getQueryBuilderPanel().loadReport(LayoutHelper.getReportLayout());
        }
    }

    public static ReportTemplate createDefaultReportTemplate() {
        ReportTemplate template = new ReportTemplate();
        template.setVersion(ReleaseInfoAdapter.getVersionNumber());
        BandElement title = new BandElement("Title");
        title.setBackground(Color.WHITE);
        title.setForeground(Color.BLACK);
        Font font = (Font) UIManager.getDefaults().get("Panel.font");
        title.setFont(font);
        title.setPadding(new Padding(0, 0, 0, 0));
        Border border  = new Border();
        border.setLeftColor(Color.BLACK);
        border.setRightColor(Color.BLACK);
        border.setTopColor(Color.BLACK);
        border.setBottomColor(Color.BLACK);
        title.setBorder(border);
        template.setTitleBand(title);

        BandElement header = new BandElement("Header");
        TemplateManager.restoreBandElement(header, title);
        template.setHeaderBand(header);

        BandElement detail = new BandElement("Detail");
        TemplateManager.restoreBandElement(detail, title);
        template.setDetailBand(detail);
        
        BandElement footer = new BandElement("Footer");
        TemplateManager.restoreBandElement(footer, title);
        template.setFooterBand(footer);

        return template;
    }

     public static ReportTemplate getGeneralTemplate(ReportLayout layout) {
        ReportTemplate template = createDefaultReportTemplate();

        Band headerBand = layout.getHeaderBand();
        if (headerBand != null) {
            List<RowElement> headerRows = headerBand.getElements();
            // title
            if (headerRows.size() > 0) {
                RowElement titleElements = headerRows.get(0);
                if (titleElements != null) {
                    for (BandElement titleElem : titleElements.getElements()) {
                        if (titleElem != null) {
                            template.setTitleBand(titleElem);
                            break;
                        }
                    }
                }
            }
            // header
            if (headerRows.size() > 1) {
                RowElement headerElems = headerRows.get(1);
                if (headerElems != null) {
                    for (BandElement headerElem : headerElems.getElements()) {
                        if (headerElem != null) {
                            template.setHeaderBand(headerElem);
                        }
                    }
                }
            }
        }

        // detail
        Band detailBand = layout.getDetailBand();
        if (detailBand !=  null) {
            List<RowElement> detailRows = detailBand.getElements();
            if (detailRows.size() > 0) {
                List<BandElement> detailElems = detailBand.getRow(0);
                if (detailElems != null) {
                    for (BandElement detailElem : detailElems) {
                        if (detailElem != null) {
                            template.setDetailBand(detailElem);
                            break;
                        }
                    }
                }
            }
        }
        
        // footer
        Band footerBand = layout.getFooterBand();
        if (footerBand !=  null) {
            List<RowElement> footerRows = footerBand.getElements();
            if (footerRows.size() > 0) {
                List<BandElement> footerElems = footerBand.getRow(0);
                if (footerElems != null) {
                    for (BandElement footerElem : footerElems) {
                        if (footerElem != null) {
                            template.setFooterBand(footerElem);
                            break;
                        }
                    }
                }
            }
        }

        return template;
     }

    public static void main(String[] args) {
        ReportTemplate template = loadTemplate(new File("E:\\Public\\next-reports\\templates\\Caribbean.ntempl"));
        System.out.println(template);
    }

}
