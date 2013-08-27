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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

import ro.nextreports.engine.band.Border;

/**
 * @author alexandru.parvulescu
 * 
 */
public class CustomLineBorder extends AbstractBorder {
	private Border border;

	public CustomLineBorder(Border border) {
		this.border = border;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		boolean hasLeft = false;
		boolean hasTop = false;
		boolean hasBottom = false;
		boolean hasRight = false;

		g.setColor(Color.BLACK);
		if (border != null) {
			if (border.getTop() > 0) {
				hasTop = true;
			}
			if (border.getRight() > 0) {
				hasRight = true;
			}
			if (border.getLeft() > 0) {
				hasLeft = true;
			}
			if (border.getBottom() > 0) {
				hasBottom = true;
			}
		}
		if (hasLeft) {
			if (border.getLeft() >= 1) {
				g.setColor(border.getLeftColor());
				g.drawLine(x + 1, y + 1, x + 1, y - 2 + height);
				if (border.getLeft() >= 2) {
					g.drawLine(x + 1 + 1, y + 1, x + 1 + 1, y - 2 + height);
					if (border.getLeft() >= 3) {
						g.drawLine(x + 1 + 2, y + 1, x + 1 + 2, y - 2 + height);
					}
				}
			}
		}
		if (hasTop) {
			if (border.getTop() >= 1) {
				g.setColor(border.getTopColor());
				g.drawLine(x + 1, y + 1, x - 1 + width, y + 1);
				if (border.getTop() >= 2) {
					g.drawLine(x + 1, y + 1 + 1, x - 1 + width, y + 1 + 1);
					if (border.getTop() >= 3) {
						g.drawLine(x + 1, y + 1 + 2, x - 1 + width, y + 1 + 2);
					}
				}
			}
		}
		if (hasRight) {
			if (border.getRight() >= 1) {
				g.setColor(border.getRightColor());
				g.drawLine(x - 1 + width, y + 1, x - 1 + width, y - 2 + height);
				if (border.getRight() >= 2) {
					g.drawLine(x - 1 + width - 1, y + 1, x - 1 + width - 1, y - 2 + height);
					if (border.getRight() >= 3) {
						g.drawLine(x - 1 + width - 2, y + 1, x - 1 + width - 2, y - 2 + height);
					}
				}
			}
		}
		if (hasBottom) {
			if (border.getBottom() >= 1) {
				g.setColor(border.getBottomColor());
				g.drawLine(x + 1, y - 1 + height, x - 1 + width, y - 1 + height);
				if (border.getBottom() >= 2) {
					g.drawLine(x + 1, y - 1 + height - 1, x - 1 + width, y - 1 + height - 1);
					if (border.getBottom() >= 3) {
						g.drawLine(x + 1, y - 1 + height - 2, x - 1 + width, y - 1 + height - 2);
					}
				}
			}
		}
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(3, 3, 3, 3);
	}

	public boolean isBorderOpaque() {
		return false;
	}

}
