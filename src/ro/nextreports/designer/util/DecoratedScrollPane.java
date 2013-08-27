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

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 7, 2006
 * Time: 1:18:09 PM
 */
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 7, 2006
 * Time: 1:18:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedScrollPane extends JComponent {


    private static final double MAX_SIZE = 200;
    private JScrollPane theScrollPane;
    private JComponent theComponent;
    private JPopupMenu thePopupMenu;
    private JButton theButton;
    private BufferedImage theImage;
    private Rectangle theStartRectangle;
    private Rectangle theRectangle;
    private Point theStartPoint;
    private double theScale;

    private DecoratedScrollPane(JScrollPane aScrollPane) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        theScrollPane = aScrollPane;
        theComponent = (JComponent) theScrollPane.getViewport().getView();
        theImage = null;
        theStartRectangle = null;
        theRectangle = null;
        theStartPoint = null;
        theScale = 0.0;
        theButton = new JButton(new AbstractAction("", ImageUtil.getImageIcon("view")) {
            public void actionPerformed(ActionEvent e) {
                display();
            }
        });
        theButton.setToolTipText(I18NSupport.getString("designer.scroll"));
        theScrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, theButton);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        MouseInputListener mil = new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                theStartPoint = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                if (theStartPoint != null) {
                    Point newPoint = e.getPoint();
                    int deltaX = (int) ((newPoint.x - theStartPoint.x) / theScale);
                    int deltaY = (int) ((newPoint.y - theStartPoint.y) / theScale);
                    scroll(deltaX, deltaY);
                }
                theStartPoint = null;
                theStartRectangle = theRectangle;
            }

            public void mouseDragged(MouseEvent e) {
                if (theStartPoint == null) return;
                Point newPoint = e.getPoint();
                moveRectangle(newPoint.x - theStartPoint.x, newPoint.y - theStartPoint.y);
            }
        };
        addMouseListener(mil);
        addMouseMotionListener(mil);
        thePopupMenu = new JPopupMenu();
        thePopupMenu.setLayout(new BorderLayout());
        thePopupMenu.add(this, BorderLayout.CENTER);
        thePopupMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public static void decorate(JScrollPane aScrollPane) {
        new DecoratedScrollPane(aScrollPane);
    }

    protected void paintComponent(Graphics g1D) {
        if (theImage == null || theRectangle == null) return;
        Graphics2D g = (Graphics2D) g1D;
        Insets insets = getInsets();
        int xOffset = insets.left;
        int yOffset = insets.top;
        int availableWidth = getWidth() - insets.left - insets.right;
        int availableHeight = getHeight() - insets.top - insets.bottom;
        g.drawImage(theImage, xOffset, yOffset, null);
        Color tmpColor = g.getColor();
        Area area = new Area(new Rectangle(xOffset, yOffset, availableWidth, availableHeight));
        area.subtract(new Area(theRectangle));
        g.setColor(new Color(255, 255, 255, 128));
        g.fill(area);
        g.setColor(Color.BLACK);
        g.draw(theRectangle);
        g.setColor(tmpColor);
    }

    public Dimension getPreferredSize() {
        if (theImage == null || theRectangle == null) return new Dimension();
        Insets insets = getInsets();
        return new Dimension(theImage.getWidth(null) + insets.left + insets.right,
                theImage.getHeight(null) + insets.top + insets.bottom);
    }

    private void display() {
        double compWidth = theComponent.getWidth();
        double compHeight = theComponent.getHeight();
        double scaleX = MAX_SIZE / compWidth;
        double scaleY = MAX_SIZE / compHeight;
        theScale = Math.min(scaleX, scaleY);

        theImage = new BufferedImage((int) (theComponent.getWidth() * theScale),
                (int) (theComponent.getHeight() * theScale),
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = theImage.createGraphics();
        g.scale(theScale, theScale);
        theComponent.paint(g);

        theStartRectangle = theComponent.getVisibleRect();
        Insets insets = getInsets();
        theStartRectangle.x = (int) (theScale * theStartRectangle.x + insets.left);
        theStartRectangle.y = (int) (theScale * theStartRectangle.y + insets.right);
        theStartRectangle.width *= theScale;
        theStartRectangle.height *= theScale;
        theRectangle = theStartRectangle;

        Dimension pref = thePopupMenu.getPreferredSize();

        thePopupMenu.show(theButton,
                (theButton.getWidth() - pref.width) / 2,
                (theButton.getHeight() - pref.height) / 2);

        try {
            Robot robot = new Robot();
            Point centerPoint = new Point(theRectangle.x + theRectangle.width / 2,
                    theRectangle.y + theRectangle.height / 2);
            SwingUtilities.convertPointToScreen(centerPoint, this);
            robot.mouseMove(centerPoint.x, centerPoint.y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void moveRectangle(int aDeltaX, int aDeltaY) {
        if (theStartRectangle == null) return;
        Insets insets = getInsets();
        Rectangle newRect = new Rectangle(theStartRectangle);
        newRect.x += aDeltaX;
        newRect.y += aDeltaY;
        newRect.x = Math.min(Math.max(newRect.x, insets.left), getWidth() - insets.right - newRect.width);
        newRect.y = Math.min(Math.max(newRect.y, insets.right), getHeight() - insets.bottom - newRect.height);
        Rectangle clip = new Rectangle();
        Rectangle.union(theRectangle, newRect, clip);
        clip.grow(2, 2);
        theRectangle = newRect;
        paintImmediately(clip);
    }

    private void scroll(int aDeltaX, int aDeltaY) {
        JComponent component = (JComponent) theScrollPane.getViewport().getView();
        Rectangle rect = component.getVisibleRect();
        rect.x += aDeltaX;
        rect.y += aDeltaY;
        component.scrollRectToVisible(rect);
        thePopupMenu.setVisible(false);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame(DecoratedScrollPane.class.getName());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    URL imageURL = new URL("http://www.aboutstonehenge.info/images/education/stonehenge-wallpaper-1.jpg");
                    JLabel label = new JLabel(new ImageIcon(ImageIO.read(imageURL)));
                    JScrollPane scrollPane = new JScrollPane(label);
                    new DecoratedScrollPane(scrollPane);
                    frame.setContentPane(scrollPane);
                    frame.pack();
                    frame.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

