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
package ro.nextreports.designer.querybuilder;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.ui.list.CheckListEvent;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.ui.list.FixedHeightCheckListBox;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.querybuilder.sql.Column;

/**
 * @author Decebal Suiu
 */
public class ColumnsListBox extends FixedHeightCheckListBox {

    private DBTableInternalFrame frame;
    
	public ColumnsListBox() {
		super(18);
        this.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent event) {
                if (!SwingUtilities.isRightMouseButton(event)) {
                	return;
                }
                
                final int index = locationToIndex(event.getPoint());
                if (index == -1) {
                	return;
                }

                CheckListItem item = (CheckListItem) getModel().getElementAt(index);
                if (item == null) {
                	return;
                }

                getFrame().selectRow(index);
                final Column col = (Column) item.getObject();
                if (col.isfKey()) {
                	JPopupMenu popupMenu = new JPopupMenu();
                	JMenuItem menuItem = new JMenuItem(I18NSupport.getString("designer.auto.join"), ImageUtil.getImageIcon("link"));
                	menuItem.setMnemonic('J');
                	menuItem.setToolTipText(I18NSupport.getString("designer.auto.join"));
                	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
                	menuItem.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {
                			try {
                				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
                				Globals.getMainFrame().setCursor(hourglassCursor);

                				DBColumn c = new DBColumn(col.getTable().getSchemaName(), col.getTable().getName(), col.getName(), null, false, false, null, 0, 0, 0);
                				//System.out.println("----- start ");
                				//long start = System.currentTimeMillis();
                				DBColumn dbCol = Globals.getDBViewer().getPrimaryKeyColumn(c);
                				//System.out.println(" --> " + dbCol);
                				//long end = System.currentTimeMillis();
                				//System.out.println("----- end in " + (end - start) / 1000L + " sec");
                				int x = getFrame().getX() + getFrame().getWidth() + 100;
                				int y = getFrame().getY();
                                String schemaN = null;
                                if (dbCol.getFkInfo() != null) {
                                    schemaN = dbCol.getFkInfo().getPkSchema();
                                }
                                if (schemaN == null) {
                                    schemaN = col.getTable().getSchemaName();
                                }
                                DBTableInternalFrame frame = Globals.getMainFrame().getQueryBuilderPanel().
                				addTableToDesktop(schemaN, dbCol.getTable(),
                						tableDim, new Point(x, y));
                				int destIndex = frame.getIndex(dbCol.getTable(), dbCol.getName());
                				Globals.getMainFrame().getQueryBuilderPanel().addJoin(getFrame(), index, frame, destIndex);
                			} catch (Exception e1) {
                				Show.error(e1);
                			} finally {
                				Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                				Globals.getMainFrame().setCursor(normalCursor);
                			}
                		}

                	});
                	popupMenu.add(menuItem);
                	popupMenu.show((Component) event.getSource(), event.getX(), event.getY());
                } else if (col.ispKey()) {
                	JPopupMenu popupMenu = new JPopupMenu();
                	JMenuItem menuItem = new JMenuItem(I18NSupport.getString("designer.auto.join"), ImageUtil.getImageIcon("link"));
                	menuItem.setMnemonic('J');
                	menuItem.setToolTipText(I18NSupport.getString("designer.auto.join"));
                	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
                	menuItem.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {                                
                			try {
                				FKJoinPanel panel = new FKJoinPanel(col.getTable().getSchemaName(), col.getTable().getName(), col.getName());
                				FKJoinDialog dialog = new FKJoinDialog(panel);
                				dialog.pack();
                				dialog.setResizable(false);
                				Show.centrateComponent(Globals.getMainFrame(), dialog);
                				dialog.setVisible(true);
                				if (dialog.okPressed()) {                					
                                    ArrayList<DBColumn> dbCols = panel.getSelectedColumns();
                                    for (DBColumn dbCol : dbCols) {
                						int x = getFrame().getX() + getFrame().getWidth() + 100;
                						int y = getFrame().getY();
                						DBTableInternalFrame frame = Globals.getMainFrame().getQueryBuilderPanel().
                						addTableToDesktop(col.getTable().getSchemaName(), dbCol.getTable(),
                								tableDim, new Point(x, y));
                						int destIndex = frame.getIndex(dbCol.getTable(), dbCol.getName());
                						Globals.getMainFrame().getQueryBuilderPanel().addJoin(getFrame(), index, frame, destIndex);
                					}
                				}
                			} catch (Exception e1) {
                				e1.printStackTrace();
                			}
                		}
                	});
                	popupMenu.add(menuItem);
                	popupMenu.show((Component) event.getSource(), event.getX(), event.getY());
                }
            }
        });
	}

    public DBTableInternalFrame getFrame() {
        return frame;
    }

    public void setFrame(DBTableInternalFrame frame) {
        this.frame = frame;
    }

    public void selectColumns(List<Column> columns) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) (model.getElementAt(i));
            Column col = (Column) item.getObject();
            if (columns.contains(col)) {
                item.setSelected(true);
                Globals.getEventBus().publishAndWait(new CheckListEvent(this, item));
            }
        }
        repaint();
    }

    public void selectColumn(Column column) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) (model.getElementAt(i));
            Column col = (Column) item.getObject();
            if (column.getTable().equals(col.getTable()) && column.getName().equals(col.getName())) {
                item.setSelected(true);
                Globals.getEventBus().publishAndWait(new CheckListEvent(this, item));
                break;
            }
        }
        repaint();
    }

    public int getIndex(Column column) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) (model.getElementAt(i));
            Column col = (Column) item.getObject();
            if (column.getTable().equals(col.getTable()) && column.getName().equals(col.getName())) {
                return i;
            }
        }
        
        return -1;
    }

    public int getIndex(String tableName, String columnName) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) (model.getElementAt(i));
            Column col = (Column) item.getObject();
            if (tableName.equals(col.getTable().getName()) && columnName.equals(col.getName())) {
                return i;
            }
        }
        
        return -1;
    }

    public Column getColumn(int index) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) (model.getElementAt(index));
        return (Column) item.getObject();
    }

	@Override
	protected void onCheck(CheckListItem item) {        
    	super.onCheck(item);
        Globals.getEventBus().publishAndWait(new CheckListEvent(this, item));
	}
	
}
