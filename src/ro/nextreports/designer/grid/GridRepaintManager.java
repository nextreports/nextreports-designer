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
package ro.nextreports.designer.grid;

import ro.nextreports.designer.grid.event.GridModelEvent;
import ro.nextreports.designer.grid.event.GridModelListener;
import ro.nextreports.designer.grid.event.HeaderModelEvent;
import ro.nextreports.designer.grid.event.HeaderModelListener;
import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.grid.event.SelectionModelListener;
import ro.nextreports.designer.grid.event.SpanModelEvent;
import ro.nextreports.designer.grid.event.SpanModelListener;

/**
 * Handles repainting of grid.
 * 
 * @author Decebal Suiu
 */
public class GridRepaintManager implements GridModelListener,
		SelectionModelListener, HeaderModelListener, SpanModelListener {
	
	private JGrid grid = null;

	public GridRepaintManager(JGrid grid) {
		this.grid = grid;
	}
	
	// Repaint listeners. The implementation of repainting should be improved
	// and only repaint the regions which have changed.
	public void gridChanged(GridModelEvent event) {
		if (event.getType() == GridModelEvent.CELLS_UPDATED
				|| event.getType() == GridModelEvent.ROWS_UPDATED
				|| event.getType() == GridModelEvent.COLUMNS_UPDATED
				|| event.getType() == GridModelEvent.MODEL_CHANGED) {
			repaint();
		}
	}

	public void selectionChanged(SelectionModelEvent event) {
		repaint();
	}

	public void headerChanged(HeaderModelEvent event) {
		resizeAndRepaint();
	}

	public void spanChanged(SpanModelEvent event) {
		repaint();
	}
	
	protected void repaint() {
		grid.repaint();
	}

	protected void resizeAndRepaint() {
		grid.revalidate();
		grid.repaint();
	}

}
