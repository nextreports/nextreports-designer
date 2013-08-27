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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.ReleaseInfo;
import ro.nextreports.designer.ReleaseInfoAdapter;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jul 26, 2006
 * Time: 12:07:43 PM
 */
public class SplashScreen extends JWindow {

    private static final byte TOP_LEFT = 1;
    private static final byte TOP_CENTER = 2;
    private static final byte TOP_RIGHT = 3;
    private static final byte MIDDLE_LEFT = 4;
    private static final byte MIDDLE_CENTER = 5;
    private static final byte MIDDLE_RIGHT = 6;
    private static final byte BOTTOM_LEFT = 7;
    private static final byte BOTTOM_CENTER = 8;
    private static final byte BOTTOM_RIGHT = 9;

    private int scrollPosition;
    private int barWidth;
    private int barHeight = 10;
    private int progress = 0;
    private final static Color COLOR = new Color(177, 195, 225);

    private BufferedImage image;
	private Image offscreenImage;
	private Graphics2D offscreenGraphics;
    private Rectangle splashBounds;
	
    public SplashScreen(String imageName) {
        this(imageName, 0);
    }

    public SplashScreen(String imageName, int waitTime) {
        this.scrollPosition = BOTTOM_CENTER;
        try {
            image = ImageUtil.getImage(imageName);
            this.setSize(image.getWidth(), image.getHeight());
            splashBounds = new Rectangle(getSize());
        } catch (Exception e) {
        	e.printStackTrace();
            image = null;
            return;
        }

        addMouseListener(new MouseAdapter() {
        	
        	@Override
            public void mousePressed(MouseEvent e) {
                setVisible(false);
                dispose();
            }
            
        });

        setLocationRelativeTo(null);
        setVisible(true);

        if (waitTime > 0) {
            final Runnable closerRunner = new Runnable() {
            	
                public void run() {
                    setVisible(false);
                    dispose();
                }
                
            };

            final int pause = waitTime;
            Runnable waitRunner = new Runnable() {
            	
                public void run() {
                    try {
                        Thread.sleep(pause);
                        SwingUtilities.invokeAndWait(closerRunner);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            };

            Thread splashThread = new Thread(waitRunner, "SplashThread");
            splashThread.start();
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        if (offscreenImage == null) {
            offscreenImage = createImage(image.getWidth(), image.getHeight());
            offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
        }

        // Draw background image
        if (image != null) {
        	offscreenGraphics.drawImage(image, 0, 0, null);
        }
        
        String version = "Version " + ReleaseInfoAdapter.getVersionNumber();
        if ((version != null) && (version.length() > 0)) {        	
            Font font = Font.decode("Arial-Bold-20");           
            int x = 310;
            int y = 160;
            offscreenGraphics.setFont(font);
            offscreenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            offscreenGraphics.setColor(Color.BLACK);
            offscreenGraphics.drawString(version, x, y);            
        }
        
        String copyright = "\u00A9 " + ReleaseInfo.getCopyright();
        int x = 290;
        int y = 105;
        Font font = Font.decode("Arial-Bold-10");
        offscreenGraphics.setFont(font);
        offscreenGraphics.drawString(copyright, x, y);

        offscreenGraphics.drawRect(0, 0, splashBounds.width - 1, splashBounds.height - 1);
        drawSplash(offscreenGraphics, I18NSupport.getString("init"));
        g2.drawImage(offscreenImage, 0, 0, this);
    }

    public void updateSplash(final int i) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progress = i;
                repaint();
            }
        });            
    }

    private void drawSplash(Graphics2D splashGraphics, String status) {
        barWidth = splashBounds.width*50/100;

        int x = getX(scrollPosition);
        int y = getY(scrollPosition);

        // draw new string
        splashGraphics.setPaintMode();
        Font font = Font.decode("Arial-Bold-16");
        splashGraphics.setFont(font);
        splashGraphics.setColor(Color.BLACK);
        splashGraphics.drawString(status, x, y);

        // draw scroll bar rectangle
        splashGraphics.setColor(Color.BLACK);
        splashGraphics.drawRect(x, y+10, barWidth + 2, barHeight);

        // fill scroll bar rectangle with the 2 colors
        splashGraphics.setColor(COLOR);
        int width = progress*barWidth/100;
        splashGraphics.fillRect(x+1, y+11, width + 1, barHeight-1);
        splashGraphics.setColor(Color.WHITE);
        splashGraphics.fillRect(x+1 + width + 1, y+11, barWidth - width, barHeight-1);
    }

    private int getX(int type) {
        switch (type) {
            case TOP_LEFT :
            case MIDDLE_LEFT:
            case BOTTOM_LEFT:
                return 10;
            case TOP_CENTER:
            case MIDDLE_CENTER:
            case BOTTOM_CENTER:
                return (splashBounds.width - barWidth) / 2;
            case TOP_RIGHT :
            case MIDDLE_RIGHT:
            case BOTTOM_RIGHT:
                return splashBounds.width - barWidth - 10;
            default:
                return 10;
        }
    }

    private int getY(int type) {
        switch (type) {
            case TOP_LEFT :
            case TOP_CENTER:
            case TOP_RIGHT :
                return 20;
            case MIDDLE_LEFT:
            case MIDDLE_CENTER:
            case MIDDLE_RIGHT:
                return (splashBounds.height - barHeight) / 2;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
                return splashBounds.height - barHeight - 66;
            default:
                return 20;
        }
    }
    
}
