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

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.template.ReportTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.io.File;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 7, 2008
 * Time: 4:38:22 PM
 */
public class TemplatePreviewPanel extends JPanel implements PropertyChangeListener {

    private ReportTemplate reportTemplate;
    private int width = 150;
    private int height = 170;
    private int maxPad = 100;
    private int maxFontSize=20;
    private Dimension dim = new Dimension(width, height);
    private int rows = 7;
    private int columns = 3;

    private String TITLE = "Title";
    private String COLUMN = "Col";
    private String TEXT = "text";
    private String FOOTER = "foo";
    private Color gridColor = new Color(227, 226, 226);

    //@todo padding for header ??
    public TemplatePreviewPanel(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
        setPreferredSize(dim);
    }

    public TemplatePreviewPanel(File file) {
        if (file == null) {
            reportTemplate = null;
        } else {
            reportTemplate = TemplateManager.loadTemplate(file);
        }
        setPreferredSize(dim);        
    }

    public TemplatePreviewPanel() {
        reportTemplate = null;
        setPreferredSize(dim);     
    }

    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();                
        Color foreground = g2.getColor();

        if (reportTemplate == null) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
        }

        
        int rowHeight = (h - 2) / rows;
        //System.out.println("rowHeight=" + rowHeight);       

        if (reportTemplate  == null) {
            return;
        }

        // backgrounds
        BandElement titleBand = reportTemplate.getTitleBand();
        Padding titlePadding = titleBand.getPadding();
        if (titlePadding == null) {
            titlePadding = new Padding(0,0,0,0);
            titleBand.setPadding(titlePadding);
        }
        if (titleBand.getBackground() == null) {
            titleBand.setBackground(Color.WHITE);
        }
        if (titleBand.getForeground() == null) {
            titleBand.setForeground(Color.BLACK);
        }

        int titleTop = titlePadding.getTop();

        //System.out.println("top=" + titleTop);
        if (titleTop > maxPad) {
            titleTop = maxPad;
        }
        titleTop = height * titleTop / maxPad / 10;
        int titleBottom = titlePadding.getBottom();

        //System.out.println("bot=" + titleBottom);
        if (titleBottom > maxPad) {
            titleBottom = maxPad;
        }
        titleBottom = height * titleBottom / maxPad / 10;
        //System.out.println("top=" + titleTop + "  bot=" + titleBottom);
        int titleDelta = titleTop + titleBottom;
        g2.setColor(titleBand.getBackground());
        g2.fillRect(1, 1, w - 2, rowHeight + titleDelta + 1);

        BandElement headerBand = reportTemplate.getHeaderBand();
        g2.setColor(headerBand.getBackground());
        g2.fillRect(1, 2 + rowHeight + titleDelta, w - 2, 2 + rowHeight + titleDelta);

        BandElement detailBand = reportTemplate.getDetailBand();
        g2.setColor(detailBand.getBackground());
        g2.fillRect(1, 2 + 2 * rowHeight + titleDelta, w - 2, h - rowHeight + titleDelta - 3);
        
        BandElement footerBand = reportTemplate.getFooterBand();
        g2.setColor(footerBand.getBackground());
        g2.fillRect(1, 2 + h -  rowHeight + titleDelta - 3, w - 2, h - rowHeight - titleDelta - 3);

        //// column grid
        g2.setColor(gridColor);
        for (int i = 1; i < columns; i++) {
            g2.drawLine(i * w / columns, rowHeight + titleDelta + 2, i * w / columns, h - 2);
        }
        // horizonal lines for rows
        for (int i = 1; i < rows; i++) {
            g2.drawLine(2, i * rowHeight + titleDelta + 2, w - 3, i * rowHeight + titleDelta + 2);
        }

        //////title text
        FontRenderContext frc = g2.getFontRenderContext();
        //System.out.println("titleFont=" + titleBand.getFont());
        Font titleFont = titleBand.getFont();
        if (titleFont == null) {
            titleFont = (Font) UIManager.getDefaults().get("Label.font");
            titleBand.setFont(titleFont);
        }
        if (titleFont.getSize() > maxFontSize) {
            titleFont = titleFont.deriveFont(titleFont.getStyle(), maxFontSize);
        }
        TextLayout titleLayout = new TextLayout(TITLE, titleFont, frc);
        float titleHeight = titleLayout.getAscent() + titleLayout.getDescent();
        int titleWidth = g2.getFontMetrics(titleFont).stringWidth(TITLE);

        int align = titleBand.getHorizontalAlign();
        int x0;
        // left
        if (align == 2) {
            x0 = 5;
            // right
        } else if (align == 4) {
            x0 = w - 4 - titleWidth - 5;
            // center
        } else {
            x0 = (w - 4 - titleWidth) / 2;
        }
        int y0 = (int) ((rowHeight + titleHeight) / 2 + titleTop);

        AffineTransform at = AffineTransform.getTranslateInstance(x0, y0);
        Shape outline = titleLayout.getOutline(at);
        g2.setColor(titleBand.getForeground());
        g2.fill(outline);

        // title border
        g2.setColor(foreground);
        Border border = titleBand.getBorder();
        if (border == null) {
           border = new Border(0,0,0,0);
           titleBand.setBorder(border);
        }
        drawBorder(g2, w, rowHeight, titleDelta, border, 1);

        //// header text
        drawText(g2, headerBand, frc, w, rowHeight, titleDelta, 3, COLUMN);

        // header border
        g2.setColor(foreground);
        Border borderH = headerBand.getBorder();
        if (borderH == null) {
           borderH = new Border(0,0,0,0);
           headerBand.setBorder(borderH); 
        }
        drawBorder(g2, w, rowHeight + 1, titleDelta, borderH, 2);

        // detail text
        drawText(g2, detailBand, frc, w, rowHeight, titleDelta, 5, TEXT);
        
        // detail border
        g2.setColor(foreground);
        Border borderD = detailBand.getBorder();
        if (borderD == null) {
           borderD = new Border(0,0,0,0);
           detailBand.setBorder(borderD); 
        }
        drawBorder(g2, w, rowHeight + 1, titleDelta, borderD, 3);
        
        // footer text
        drawText(g2, footerBand, frc, w, rowHeight, titleDelta, 13, FOOTER);
        
        // footer border
        g2.setColor(foreground);
        Border borderF = footerBand.getBorder();
        if (borderF == null) {
           borderF = new Border(0,0,0,0);
           footerBand.setBorder(borderF); 
        }
        drawBorder(g2, w, rowHeight , titleDelta+3, borderF, 7);
        
        // panel border
        g2.setColor(gridColor.darker());
        g2.drawLine(0, 0, w - 1, 0);
        g2.drawLine(0, 0, 0, h - 1);
        g2.drawLine(0, h - 1, w - 1, h - 1);
        g2.drawLine(w - 1, 0, w - 1, h - 1);

    }

    public static void main(String[] args) {

        ReportTemplate template = TemplateManager.loadTemplate(new File("E:\\test14.ntempl"));
        TemplatePreviewPanel panel = new TemplatePreviewPanel(template);

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new GridBagLayout());
        frame.getContentPane().add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(400, 400);
        frame.setTitle("Test");
        //frameanimation.pack();
        frame.setVisible(true);
    }

    private void drawBorder(Graphics2D g2, int w, int rowHeight, int titleDelta, Border border, int row) {
        int delta = 0;
        if (row > 1) {
            delta = titleDelta;
        } else {
            titleDelta+=2;
        }

        int bottom = border.getBottom();
        // thin
        if (bottom > 0) {
        	g2.setColor(border.getBottomColor());
            g2.drawLine(1, row * rowHeight + titleDelta, w - 1, row * rowHeight + titleDelta);
            // medium
            if (bottom > 1) {
                g2.drawLine(1, row * rowHeight + titleDelta + 1, w - 1, row * rowHeight + titleDelta + 1);
            }
            // thick
            if (bottom > 2) {
                g2.drawLine(1, row * rowHeight + titleDelta + 2, w - 1, row * rowHeight + titleDelta + 2);
            }
        }
        int top = border.getTop();
        // thin
        if (top > 0) {
        	g2.setColor(border.getTopColor());
            g2.drawLine(1, 1 + (row - 1) * rowHeight + delta, w - 1, 1 + (row - 1) * rowHeight + delta);
            // medium
            if (top > 1) {
                g2.drawLine(1, 2 + (row - 1) * rowHeight + delta, w - 1, 2 + (row - 1) * rowHeight + delta);
            }
            // thick
            if (top > 2) {
                g2.drawLine(1, 3 + (row - 1) * rowHeight + delta, w - 1, 3 + (row - 1) * rowHeight + delta);
            }
        }

        int left = border.getLeft();
        // thin
        if (left > 0) {
        	g2.setColor(border.getLeftColor());
            g2.drawLine(1, 1 + (row - 1) * rowHeight + delta, 1, row * rowHeight - 1 + titleDelta);
            // medium
            if (left > 1) {
                g2.drawLine(2, 1 + (row - 1) * rowHeight + delta, 2, row * rowHeight - 1 + titleDelta);
            }
            // thick
            if (left > 2) {
                g2.drawLine(3, 1 + (row - 1) * rowHeight + delta, 3, row * rowHeight - 1 + titleDelta);
            }
        }

        int right = border.getRight();        
        // thin
        if (right > 0) {
        	g2.setColor(border.getRightColor());
            g2.drawLine(w - 2, 1 + (row - 1) * rowHeight + delta, w - 2, row * rowHeight - 1 + titleDelta);
            // medium
            if (right > 1) {
                g2.drawLine(w - 3, 1 + (row - 1) * rowHeight + delta, w - 3, row * rowHeight - 1 + titleDelta);
            }
            // thick
            if (right > 2) {
                g2.drawLine(w - 4, 1 + (row - 1) * rowHeight + delta, w - 4, row * rowHeight - 1 + titleDelta);
            }
        }

    }

    private void drawText(Graphics2D g2, BandElement band, FontRenderContext frc,
                          int w, int rowHeight, int titleDelta, int rowPos, String text) {

        TextLayout detailLayout = new TextLayout(text, band.getFont(), frc);
        float detailHeight = detailLayout.getAscent() + detailLayout.getDescent();
        int detailWidth = g2.getFontMetrics(band.getFont()).stringWidth(text);
        for (int i = 0; i < columns; i++) {

            int alignH = band.getHorizontalAlign();
            //System.out.println("alignH=" + alignH);
            int x0H;
            // left
            if (alignH == 2) {
                x0H = i * w / columns + 5;
                // right
            } else if (alignH == 4) {
                x0H = (i + 1) * w / columns - detailWidth - 5;
                // center
            } else {
                x0H = i * w / columns + (w / columns - detailWidth) / 2;
            }
            int y0H = titleDelta + (int) ((rowPos * rowHeight + detailHeight) / 2);

            //System.out.println("x0H=" + x0H + "  y0H=" + y0H);
            AffineTransform atH = AffineTransform.getTranslateInstance(x0H, y0H);
            Shape outlineH = detailLayout.getOutline(atH);
            g2.setColor(band.getForeground());
            g2.fill(outlineH);

        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {        
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
            File newFile = (File) evt.getNewValue();
            if (newFile != null) {
                String path = newFile.getAbsolutePath();
                if (canPreview(path)) {
                    reportTemplate = TemplateManager.loadTemplate(new File(path));
                } else {
                    reportTemplate = null;
                }
            }
            this.repaint();
        }
    }

    private boolean canPreview(String path) {
        String ignoreCasePath = path.toLowerCase();
        return (ignoreCasePath.endsWith(TemplateFileFilter.TEMPLATE_FILE_EXT));
    }

    public void setReportTemplate(ReportTemplate reportTemplate) {
        this.reportTemplate = reportTemplate;
        repaint();
    }

    public void setFileTemplate(File file) {
        if (file == null) {
            reportTemplate = null;
        } else {
            reportTemplate = TemplateManager.loadTemplate(file);
        }
        repaint();
    }
}
