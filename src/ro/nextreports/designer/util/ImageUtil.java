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
package ro.nextreports.designer.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.util.Properties;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author Decebal Suiu
 */
public class ImageUtil {

    private static final Log LOG = LogFactory.getLog(ImageUtil.class);

    private static Properties imageNames;

    public static final ImageIcon TABLE_IMAGE_ICON;
    public static final ImageIcon VIEW_IMAGE_ICON;
    public static final ImageIcon COLUMN_IMAGE_ICON;
    public static final ImageIcon PROCEDURE_IMAGE_ICON;
    public static final ImageIcon PROCEDURE_VALID_IMAGE_ICON;
    public static final ImageIcon QUERY_ICON;
    public static final ImageIcon QUERY_ERROR_ICON;
    public static final ImageIcon REPORT_ICON;
    public static final ImageIcon REPORT_ERROR_ICON;
    public static final ImageIcon CHART_ICON;
    public static final ImageIcon CHART_ERROR_ICON;
    
    public static final String LEFT_ICON_NAME = "left";
    public static final String RIGHT_ICON_NAME = "right";
    public static final String UP_ICON_NAME = "up";
    public static final String DOWN_ICON_NAME = "down";

    private static int shadowSize = 5;
    private static float shadowOpacity = 0.5f;
    private static Color shadowColor = new Color(0x000000);

    static {
    	loadImagesNames();
    	TABLE_IMAGE_ICON = getImageIcon("table");
    	VIEW_IMAGE_ICON = getImageIcon("view");
    	COLUMN_IMAGE_ICON = getImageIcon("column");
        PROCEDURE_IMAGE_ICON = getImageIcon("procedure");
        PROCEDURE_VALID_IMAGE_ICON = getImageIcon("procedure_valid");
        QUERY_ICON = getImageIcon("query");
        QUERY_ERROR_ICON = getImageIcon("query_error");
        REPORT_ICON = getImageIcon("report");
        REPORT_ERROR_ICON = getImageIcon("report_error");
        CHART_ICON = getImageIcon("chart");
        CHART_ERROR_ICON = getImageIcon("chart_error");
    }
    
    public static ImageIcon getImageIcon(String image) {
    	return getImageIcon(image, true);
    }
    
    public static ImageIcon getImageIcon(String image, boolean logError) {
    	URL url = getImageURL(image, logError);
        if (url == null) {
        	if (logError) {
        		LOG.error("Cannot load image " + image);
        	}
            return null;
        }
        
        return new ImageIcon(url);
    }
    
    public static URL getImageURL(String image) {
    	return getImageURL(image, true);
    }

    public static URL getImageURL(String image, boolean logError) {
    	if (!imageNames.containsKey(image)) {
    		if (logError) {
    			LOG.error("Cannot find a image file for image '" + image + "'");
    		}
    		return null;
    	}

        return ImageUtil.class.getResource("/images/" + getImageName(image));
    }
    
    public static BufferedImage getImage(String image) {
    	URL url = getImageURL(image);
        if (url == null) {
            LOG.error("Cannot load image " + image);
        }

    	try {
			return ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    private static void applyShadow(BufferedImage image) {
        int dstWidth = image.getWidth();
        int dstHeight = image.getHeight();

        int left = (shadowSize - 1) >> 1;
        int right = shadowSize - left;
        int xStart = left;
        int xStop = dstWidth - right;
        int yStart = left;
        int yStop = dstHeight - right;

        int shadowRgb = shadowColor.getRGB() & 0x00FFFFFF;

        int[] aHistory = new int[shadowSize];
        int historyIdx = 0;

        int aSum;

        int[] dataBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int lastPixelOffset = right * dstWidth;
        float sumDivider = shadowOpacity / shadowSize;

        // horizontal pass

        for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y * dstWidth) {
            aSum = 0;
            historyIdx = 0;
            for (int x = 0; x < shadowSize; x++, bufferOffset++) {
                int a = dataBuffer[bufferOffset] >>> 24;
                aHistory[x] = a;
                aSum += a;
            }

            bufferOffset -= right;

            for (int x = xStart; x < xStop; x++, bufferOffset++) {
                int a = (int) (aSum * sumDivider);
                dataBuffer[bufferOffset] = a << 24 | shadowRgb;

                // substract the oldest pixel from the sum
                aSum -= aHistory[historyIdx];

                // get the lastest pixel
                a = dataBuffer[bufferOffset + right] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        // vertical pass
        for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
            aSum = 0;
            historyIdx = 0;
            for (int y = 0; y < shadowSize; y++, bufferOffset += dstWidth) {
                int a = dataBuffer[bufferOffset] >>> 24;
                aHistory[y] = a;
                aSum += a;
            }

            bufferOffset -= lastPixelOffset;

            for (int y = yStart; y < yStop; y++, bufferOffset += dstWidth) {
                int a = (int) (aSum * sumDivider);
                dataBuffer[bufferOffset] = a << 24 | shadowRgb;

                // substract the oldest pixel from the sum
                aSum -= aHistory[historyIdx];

                // get the lastest pixel
                a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
                aHistory[historyIdx] = a;
                aSum += a;

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }
    }

    private static BufferedImage prepareImage(BufferedImage image) {
        BufferedImage subject = new BufferedImage(image.getWidth() + shadowSize * 2,
                                                  image.getHeight() + shadowSize * 2,
                                                  BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = subject.createGraphics();
        g2.drawImage(image, null, shadowSize, shadowSize);
        g2.dispose();

        return subject;
    }

    private static  BufferedImage createDropShadow(BufferedImage image) {
        BufferedImage subject = prepareImage(image);
        applyShadow(subject);
        return subject;
    }

    private static Dimension computeShadowPosition(double angle, int distance) {
        double angleRadians = Math.toRadians(angle);
        int distance_x = (int) (Math.cos(angleRadians) * distance);
        int distance_y = (int) (Math.sin(angleRadians) * distance);
        return new Dimension(distance_x, distance_y);
    }

    public static void drawImage(String imageName, boolean withShadow, Graphics2D g2, int x, int y) {
        BufferedImage img = toBufferedImage(getImage(imageName));
        g2.drawImage(img, null, x, y);
        if (withShadow) {
            BufferedImage shadow = createDropShadow(img);
            if (shadow != null) {
                Dimension d = ImageUtil.computeShadowPosition(30, 5);
                g2.drawImage(shadow, x + (int) d.getWidth(), y + (int) d.getHeight(), null);
            }
        }

    }

    private static void loadImagesNames() {
        if (imageNames == null) {
            imageNames = new Properties();
            try {
                imageNames.load(ImageUtil.class.getResourceAsStream("/images.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getImageName(String imageKey) {
        return imageNames.getProperty(imageKey);
    }

}
