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
package ro.nextreports.designer.querybuilder.table;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

/**
 * @author Decebal Suiu
 */
public class TableDNDRecognizer extends MouseInputAdapter {

    private boolean recognized;
    private boolean dragged;
    private Point pressedPoint;
    
    public boolean isDragged() {
        return dragged;
    }

    public void mousePressed(MouseEvent ev) {
        pressedPoint = ev.getPoint();
    }

    public void mouseDragged(MouseEvent ev) {
        Point p = ev.getPoint();
        if (!recognized
                && ev.isShiftDown()
                && ((Math.abs(pressedPoint.x - p.x) > 5) || (Math
                        .abs(pressedPoint.y - p.y) > 5))) {
            dragged = true;
            recognized = true;
            JComponent c = (JComponent) ev.getSource();
            TransferHandler th = c.getTransferHandler();
            if (th != null) {
                th.exportAsDrag(c, ev, ev.isAltDown() ? 
                        DnDConstants.ACTION_COPY : DnDConstants.ACTION_MOVE);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        recognized = false;
        dragged = false;
        pressedPoint = null;
    }
    
}
