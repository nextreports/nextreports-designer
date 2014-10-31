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

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.DownloadBulkChartAction;
import ro.nextreports.designer.action.DownloadBulkReportAction;
import ro.nextreports.designer.action.NamePatternAction;
import ro.nextreports.designer.action.PublishBulkChartAction;
import ro.nextreports.designer.action.PublishBulkReportAction;
import ro.nextreports.designer.action.chart.DeleteChartAction;
import ro.nextreports.designer.action.chart.ExportChartAction;
import ro.nextreports.designer.action.chart.ImportChartAction;
import ro.nextreports.designer.action.chart.NewChartFromQueryAction;
import ro.nextreports.designer.action.chart.OpenChartAction;
import ro.nextreports.designer.action.chart.PreviewChartAction;
import ro.nextreports.designer.action.chart.PublishChartAction;
import ro.nextreports.designer.action.chart.RenameChartAction;
import ro.nextreports.designer.action.datasource.AddDataSourceAction;
import ro.nextreports.designer.action.datasource.DataSourceConnectAction;
import ro.nextreports.designer.action.datasource.DataSourceDeleteAction;
import ro.nextreports.designer.action.datasource.DataSourceDisconnectAction;
import ro.nextreports.designer.action.datasource.DataSourceEditAction;
import ro.nextreports.designer.action.datasource.DataSourceSchemaSelectionAction;
import ro.nextreports.designer.action.datasource.DataSourceViewInfoAction;
import ro.nextreports.designer.action.favorites.AddToFavoritesAction;
import ro.nextreports.designer.action.folder.AddFolderAction;
import ro.nextreports.designer.action.folder.DeleteFolderAction;
import ro.nextreports.designer.action.folder.RenameFolderAction;
import ro.nextreports.designer.action.query.DeleteQueryAction;
import ro.nextreports.designer.action.query.ExportQueryAction;
import ro.nextreports.designer.action.query.ImportQueryAction;
import ro.nextreports.designer.action.query.NewQueryAction;
import ro.nextreports.designer.action.query.OpenQueryAction;
import ro.nextreports.designer.action.query.RenameQueryAction;
import ro.nextreports.designer.action.query.ValidateProceduresAction;
import ro.nextreports.designer.action.query.ValidateSqlsAction;
import ro.nextreports.designer.action.query.ViewProcedureColumnsInfoAction;
import ro.nextreports.designer.action.query.ViewTableColumnsInfoAction;
import ro.nextreports.designer.action.report.DeleteReportAction;
import ro.nextreports.designer.action.report.ExportReportAction;
import ro.nextreports.designer.action.report.ImportReportAction;
import ro.nextreports.designer.action.report.NewReportFromQueryAction;
import ro.nextreports.designer.action.report.OpenReportAction;
import ro.nextreports.designer.action.report.PublishReportAction;
import ro.nextreports.designer.action.report.RenameReportAction;
import ro.nextreports.designer.action.report.layout.export.ExportToCsvAction;
import ro.nextreports.designer.action.report.layout.export.ExportToDocxAction;
import ro.nextreports.designer.action.report.layout.export.ExportToExcelAction;
import ro.nextreports.designer.action.report.layout.export.ExportToHtmlAction;
import ro.nextreports.designer.action.report.layout.export.ExportToPdfAction;
import ro.nextreports.designer.action.report.layout.export.ExportToRtfAction;
import ro.nextreports.designer.action.report.layout.export.ExportToTsvAction;
import ro.nextreports.designer.action.report.layout.export.ExportToTxtAction;
import ro.nextreports.designer.action.report.layout.export.ExportToXmlAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DataSourceType;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.NotFoundException;
import ro.nextreports.designer.dbviewer.common.DBProcedure;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.datatransfer.DBProcTransferable;
import ro.nextreports.designer.querybuilder.datatransfer.DBTableTransferable;
import ro.nextreports.designer.querybuilder.datatransfer.FileTransferable;
import ro.nextreports.designer.querybuilder.tree.DBNodeExpander;
import ro.nextreports.designer.querybuilder.tree.NodeExpander;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartRunner;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.querybuilder.sql.Table;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.ReportUtil;

/**
 * @author Decebal Suiu
 */
public class DBBrowserTree extends JXTree {

    private static final Log LOG = LogFactory.getLog(DBBrowserTree.class);
    
    private DBBrowserTreeModel model;
    private DBBrowserTree instance;
    private byte typeRoot;    

    public DBBrowserTree() {
        this(DBObject.DATASOURCE, true);
    }

    public DBBrowserTree(byte typeRoot) {
        this(typeRoot, true);
    }

    /**
     * Constructor
     *
     * @param typeRoot            is used to create the tree starting from a specific type node
     * @param registerDoubleClick register mouse double click on tree
     */
    public DBBrowserTree(byte typeRoot, boolean registerDoubleClick) {
        super();        
        this.typeRoot = typeRoot;
        populateTree(typeRoot);

        setCellRenderer(new DBBrowserTreeRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setRolloverEnabled(true);
        addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 

        DragGestureRecognizer dgr = DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_COPY_OR_MOVE, new DBBrowserTreeDragGestureListener());

        DBBrowserTreeDropListener dropListener = new DBBrowserTreeDropListener();
        addTreeSelectionListener(dropListener);
        setDropTarget(new DropTarget(this, dropListener));

        if (registerDoubleClick) {
            addMouseDoubleClickListener();
        }

        instance = this;

        // single data source autoconnect
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        List<DataSource> sources = manager.getDataSources();
        if (sources.size() == 1) {
            if (Globals.singleSourceAutoConnect()) {
                DataSource ds = manager.getConnectedDataSource();
                if (ds == null) {
                    connectToDataSource(sources.get(0).getName());
                }
            }
        } else {
        	String dataSourceName = Globals.getSystemDataSource();        	
        	DataSource ds = manager.getDataSource(dataSourceName);        	
        	if (ds != null) {
        		connectToDataSource(dataSourceName);
        	}
        }		                
    }
    
    private void connectToDataSource(String dataSourceName) {
    	try {
    		if (DefaultDataSourceManager.getInstance().getConnectedDataSource() != null) {
    			return; // already connected
    		}
    		DefaultDataSourceManager.getInstance().connect(dataSourceName);
            DBBrowserNode node = searchNode(DBNodeExpander.CONNECTIONS);            
            if (node.getChildCount() == 0) {
                return;
            }            
            DBBrowserNode selectedNode = searchNode(dataSourceName, DBObject.DATABASE);
            
            // select data source node and expand it
            TreeNode[] nodes = model.getPathToRoot(selectedNode);
            TreePath path = new TreePath(nodes);
            scrollPathToVisible(path);
            setSelectionPath(path);
            instance.fireTreeExpanded(path);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    
    private void addMouseDoubleClickListener() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                job(e, false);
            }

            private void job(MouseEvent e, boolean pressed) {
                if (e.isPopupTrigger() || (e.getClickCount() == 2)) {
                    final TreePath selPath = getPathForLocation(e.getX(), e.getY());
                    if (selPath == null) {
                        return;
                    }
                    setSelectionPath(selPath);
                    try {
                        final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();
                        if (selectedNode != null) {
                            if (selectedNode.getDBObject().getType() == DBObject.DATASOURCE) {
                                selectionDataSource(e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.QUERIES_GROUP) {
                                selectionQueryGroup(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.QUERIES) {
                                selectionQuery(selectedNode, e, pressed);
                            } else if (selectedNode.getDBObject().getType() == DBObject.REPORTS_GROUP) {
                                selectionReportGroup(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.REPORTS) {
                                selectionReport(selectedNode, e, pressed);
                            } else if (selectedNode.getDBObject().getType() == DBObject.CHARTS_GROUP) {
                                selectionChartGroup(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.CHARTS) {
                                selectionChart(selectedNode, e, pressed);
                            } else if (selectedNode.getDBObject().getType() == DBObject.DATABASE) {
                                selectionDatabase(selPath, selectedNode, e);
                            } else if ((selectedNode.getDBObject().getType() == DBObject.TABLE) ||
                                    (selectedNode.getDBObject().getType() == DBObject.VIEW)) {
                                selectionTableOrView(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.PROCEDURES) {
                                selectionProcedure(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.PROCEDURES_GROUP) {
                                selectionProcedureGroup(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.TABLES_GROUP) {
                                selectionTablesGroup(selectedNode, e);
                            } else if (selectedNode.getDBObject().getType() == DBObject.VIEWS_GROUP) {
                                selectionViewsGroup(selectedNode, e);    
                            } else if (selectedNode.getDBObject().isFolder()) {
                                selectionFolder(selectedNode, e);
                            }
                        }
                    } catch (Exception ex) {
                        Show.error(ex);
                    }
                }
            }
        });
    }

    private void selectionDataSource(MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        AddDataSourceAction addDSAction = new AddDataSourceAction();
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(addDSAction);
        popupMenu.add(menuItem);
        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }

    private void selectionQueryGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        ImportQueryAction importAction = new ImportQueryAction();
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(importAction);
        popupMenu.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem(new AddFolderAction(this, selectedNode, DBObject.FOLDER_QUERY));
        popupMenu.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
    	popupMenu.add(menuItem3);

        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }

    private void selectionQuery(DBBrowserNode selectedNode, MouseEvent e, boolean pressed) {
        OpenQueryAction openAction = new OpenQueryAction();
        openAction.setQueryName(selectedNode.getDBObject().getName());
        openAction.setQueryPath(selectedNode.getDBObject().getAbsolutePath());

        if (e.getClickCount() == 2) {
            if (pressed) {
                openAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
            }
        } else {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem(openAction);
            popupMenu.add(menuItem);

            NewReportFromQueryAction newReportQAction = new NewReportFromQueryAction();
            newReportQAction.setQueryName(selectedNode.getDBObject().getName());
            newReportQAction.setQueryPath(selectedNode.getDBObject().getAbsolutePath());
            JMenuItem menuItem3 = new JMenuItem(newReportQAction);
            popupMenu.add(menuItem3);

            NewChartFromQueryAction newChartQAction = new NewChartFromQueryAction();
            newChartQAction.setQueryName(selectedNode.getDBObject().getName());
            newChartQAction.setQueryPath(selectedNode.getDBObject().getAbsolutePath());
            JMenuItem menuItem6 = new JMenuItem(newChartQAction);
            popupMenu.add(menuItem6);

            DeleteQueryAction deleteAction = new DeleteQueryAction(instance, selectedNode);
            JMenuItem menuItem2 = new JMenuItem(deleteAction);//
            popupMenu.add(menuItem2);

            RenameQueryAction renameAction = new RenameQueryAction(instance, selectedNode);
            JMenuItem menuItem4 = new JMenuItem(renameAction);
            popupMenu.add(menuItem4);

            ExportQueryAction exportAction = new ExportQueryAction(instance, selectedNode);
            JMenuItem menuItem5 = new JMenuItem(exportAction);
            popupMenu.add(menuItem5);
            
            JMenuItem menuItem7 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem7);

            popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
        }
    }

    private void selectionReportGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        ImportReportAction importAction = new ImportReportAction();
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(importAction);
        popupMenu.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem(new AddFolderAction(this, selectedNode, DBObject.FOLDER_REPORT));
        popupMenu.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
    	popupMenu.add(menuItem3);
    	JMenuItem menuItem4 = new JMenuItem(new PublishBulkReportAction());
    	popupMenu.add(menuItem4);
    	JMenuItem menuItem5 = new JMenuItem(new DownloadBulkReportAction(FileReportPersistence.getReportsAbsolutePath()));
    	popupMenu.add(menuItem5);

        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }

    private void selectionReport(DBBrowserNode selectedNode, MouseEvent e, boolean pressed) {
        OpenReportAction openAction = new OpenReportAction();
        openAction.setReportName(selectedNode.getDBObject().getName());
        openAction.setReportPath(selectedNode.getDBObject().getAbsolutePath());

        if (e.getClickCount() == 2) {
            if (pressed) {
                openAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
            }
        } else {            
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem(openAction);
            popupMenu.add(menuItem);

            DeleteReportAction deleteAction = new DeleteReportAction(instance, selectedNode);
            JMenuItem menuItem2 = new JMenuItem(deleteAction);
            popupMenu.add(menuItem2);

            RenameReportAction renameAction = new RenameReportAction(instance, selectedNode);
            JMenuItem menuItem3 = new JMenuItem(renameAction);
            popupMenu.add(menuItem3);

            ExportReportAction exportAction = new ExportReportAction(instance, selectedNode);
            JMenuItem menuItem4 = new JMenuItem(exportAction);
            popupMenu.add(menuItem4);

            Report report = FormLoader.getInstance().load(selectedNode.getDBObject().getAbsolutePath(), false);
            JMenu runMenu = new JMenu(I18NSupport.getString("export"));
            Globals.setTreeReportAbsolutePath(selectedNode.getDBObject().getAbsolutePath());
            runMenu.add(new JMenuItem(new ExportToHtmlAction(report)));
            runMenu.add(new JMenuItem(new ExportToExcelAction(report)));
            runMenu.add(new JMenuItem(new ExportToPdfAction(report)));
            runMenu.add(new JMenuItem(new ExportToDocxAction(report)));
            runMenu.add(new JMenuItem(new ExportToRtfAction(report)));
            runMenu.add(new JMenuItem(new ExportToCsvAction(report)));
            runMenu.add(new JMenuItem(new ExportToTsvAction(report)));
            runMenu.add(new JMenuItem(new ExportToXmlAction(report)));
            runMenu.add(new JMenuItem(new ExportToTxtAction(report)));
            popupMenu.add(runMenu);

            PublishReportAction publishAction = new PublishReportAction(selectedNode.getDBObject().getAbsolutePath());
            JMenuItem menuItem5 = new JMenuItem(publishAction);
            popupMenu.add(menuItem5);
            
            JMenuItem menuItem6 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem6);
        	
        	JMenuItem menuItem7 = new JMenuItem(new AddToFavoritesAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem7);

            popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
        }
    }

    private void selectionChartGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu();
        ImportChartAction importAction = new ImportChartAction();        
        JMenuItem menuItem = new JMenuItem(importAction);
        popupMenu.add(menuItem);        
        JMenuItem menuItem2 = new JMenuItem(new AddFolderAction(this, selectedNode, DBObject.FOLDER_CHART));
        popupMenu.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
    	popupMenu.add(menuItem3);
    	JMenuItem menuItem4 = new JMenuItem(new PublishBulkChartAction());
    	popupMenu.add(menuItem4);
    	JMenuItem menuItem5 = new JMenuItem(new DownloadBulkChartAction(FileReportPersistence.getChartsAbsolutePath()));
    	popupMenu.add(menuItem5);

        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }

    private void selectionChart(DBBrowserNode selectedNode, MouseEvent e, boolean pressed) {
        OpenChartAction openAction = new OpenChartAction();
        openAction.setChartName(selectedNode.getDBObject().getName());
        openAction.setChartPath(selectedNode.getDBObject().getAbsolutePath());

        if (e.getClickCount() == 2) {
            if (pressed) {
                openAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
            }

        } else {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem(openAction);
            popupMenu.add(menuItem);

            DeleteChartAction deleteAction = new DeleteChartAction(instance, selectedNode);
            JMenuItem menuItem2 = new JMenuItem(deleteAction);
            popupMenu.add(menuItem2);

            RenameChartAction renameAction = new RenameChartAction(instance, selectedNode);
            JMenuItem menuItem3 = new JMenuItem(renameAction);
            popupMenu.add(menuItem3);

            ExportChartAction exportAction = new ExportChartAction(instance, selectedNode);
            JMenuItem menuItem4 = new JMenuItem(exportAction);
            popupMenu.add(menuItem4);

            Chart chart = ChartUtil.loadChart(selectedNode.getDBObject().getAbsolutePath());
            PreviewChartAction previewHTML5Action = new PreviewChartAction(ChartRunner.GRAPHIC_FORMAT, ChartRunner.HTML5_TYPE, I18NSupport.getString("preview.html5"));
            previewHTML5Action.setChart(chart);
            popupMenu.add(previewHTML5Action);
            PreviewChartAction previewFlashAction = new PreviewChartAction(ChartRunner.GRAPHIC_FORMAT, ChartRunner.FLASH_TYPE, I18NSupport.getString("preview.flash"));
            previewFlashAction.setChart(chart);
            popupMenu.add(previewFlashAction);
            previewFlashAction.setEnabled(!ChartType.hasNoFlashSupport(chart.getType().getType()));            
            PreviewChartAction previewImageAction = new PreviewChartAction(ChartRunner.IMAGE_FORMAT, ChartRunner.NO_TYPE, I18NSupport.getString("preview.image"));
            previewImageAction.setChart(chart);
            popupMenu.add(previewImageAction);

            PublishChartAction publishAction = new PublishChartAction(selectedNode.getDBObject().getAbsolutePath());
            JMenuItem menuItem5 = new JMenuItem(publishAction);
            popupMenu.add(menuItem5);
            
            JMenuItem menuItem6 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem6);
        	
        	JMenuItem menuItem7 = new JMenuItem(new AddToFavoritesAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem7);

            popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
        }
    }

    private void selectionDatabase(TreePath selPath, DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        boolean connected = false;
        String name = selectedNode.getDBObject().getName();
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        DataSource ds = manager.getDataSource(name);
        if (ds.getStatus() == DataSourceType.CONNECTED) {
            connected = true;
        } else {
            connected = false;
        }

        // try to create source directory (may not exists if we copy datasource.xml)
        (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + name + File.separator + FileReportPersistence.QUERIES_FOLDER)).mkdirs();
        (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + name + File.separator + FileReportPersistence.REPORTS_FOLDER)).mkdirs();
        (new File(FileReportPersistence.CONNECTIONS_DIR + File.separator + name + File.separator + FileReportPersistence.CHARTS_FOLDER)).mkdirs();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(new DataSourceConnectAction(instance, selPath));
        popupMenu.add(menuItem);
        if (connected) {
            menuItem.setEnabled(false);
        } else {
            menuItem.setEnabled(true);
        }

        JMenuItem menuItem2 = new JMenuItem(new DataSourceDisconnectAction(instance, selectedNode));
        popupMenu.add(menuItem2);
        if (connected) {
            menuItem2.setEnabled(true);
        } else {
            menuItem2.setEnabled(false);
        }

        JMenuItem menuItem5 = new JMenuItem(new DataSourceViewInfoAction(selectedNode));
        popupMenu.add(menuItem5);

        JMenuItem menuItem3 = new JMenuItem(new DataSourceEditAction(instance, selectedNode));
        popupMenu.add(menuItem3);
        if (connected) {
            menuItem3.setEnabled(false);
        } else {
            menuItem3.setEnabled(true);
        }

        JMenuItem menuItem4 = new JMenuItem(new DataSourceDeleteAction(instance, selectedNode));
        popupMenu.add(menuItem4);
        if (connected) {
            menuItem4.setEnabled(false);
        } else {
            menuItem4.setEnabled(true);
        }

        if (!DefaultDataSourceManager.memoryDataSources()) {
            JMenuItem menuItem6 = new JMenuItem(new DataSourceSchemaSelectionAction(instance, selectedNode));
            popupMenu.add(menuItem6);
        }

        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }

    private void selectionTableOrView(DBBrowserNode selectedNode, MouseEvent e) {
        boolean isTable = true;
        if (selectedNode.getDBObject().getType() == DBObject.VIEW) {
            isTable = false;
        }
        ViewTableColumnsInfoAction infoAction = new ViewTableColumnsInfoAction(selectedNode.getDBObject(), isTable);
        if (e.getClickCount() == 2) {
            infoAction.actionPerformed(null);
        } else {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem(infoAction);
            popupMenu.add(menuItem);
            popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
        }
    }

    private void selectionProcedure(DBBrowserNode selectedNode, MouseEvent e) {

        ViewProcedureColumnsInfoAction infoAction = new ViewProcedureColumnsInfoAction(selectedNode.getDBObject());
        if (e.getClickCount() == 2) {
            infoAction.actionPerformed(null);
        } else {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem(infoAction);
            popupMenu.add(menuItem);
            JMenuItem menuItem2 = new JMenuItem(new ValidateProceduresAction(selectedNode.getDBObject()));
            popupMenu.add(menuItem2);
            popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
        }
    }

    private void selectionProcedureGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        ValidateProceduresAction validateAction = new ValidateProceduresAction();
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(validateAction);
        popupMenu.add(menuItem);
        NamePatternAction patternAction = new NamePatternAction(NamePatternAction.PROCEDURE_NAME_PATTERN);
        JMenuItem menuItem2 = new JMenuItem(patternAction);
        popupMenu.add(menuItem2);
        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());        
    }
    
    private void selectionTablesGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }        
        JPopupMenu popupMenu = new JPopupMenu();        
        NamePatternAction patternAction = new NamePatternAction(NamePatternAction.TABLE_NAME_PATTERN);
        JMenuItem menuItem = new JMenuItem(patternAction);
        popupMenu.add(menuItem);
        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());        
    }
    
    private void selectionViewsGroup(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }        
        JPopupMenu popupMenu = new JPopupMenu();        
        NamePatternAction patternAction = new NamePatternAction(NamePatternAction.VIEW_NAME_PATTERN);
        JMenuItem menuItem = new JMenuItem(patternAction);
        popupMenu.add(menuItem);
        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());        
    }


    private void selectionFolder(DBBrowserNode selectedNode, MouseEvent e) {
        if (e.getClickCount() == 2) {
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu();

        boolean testSql = false;
        if (selectedNode.getDBObject().getType() == DBObject.FOLDER_QUERY) {
        	testSql = true;
            JMenuItem menuItem = new JMenuItem(new ImportQueryAction(selectedNode.getDBObject().getAbsolutePath()));
            popupMenu.add(menuItem);
        } else if (selectedNode.getDBObject().getType() == DBObject.FOLDER_REPORT) {
        	testSql = true;
            JMenuItem menuItem = new JMenuItem(new ImportReportAction(selectedNode.getDBObject().getAbsolutePath()));
            popupMenu.add(menuItem);
            JMenuItem menuItem2 = new JMenuItem(new DownloadBulkReportAction(selectedNode.getDBObject().getAbsolutePath()));
            popupMenu.add(menuItem2);
        } else if (selectedNode.getDBObject().getType() == DBObject.FOLDER_CHART) {
        	testSql = true;
            JMenuItem menuItem = new JMenuItem(new ImportChartAction(selectedNode.getDBObject().getAbsolutePath()));
            popupMenu.add(menuItem);
            JMenuItem menuItem2 = new JMenuItem(new DownloadBulkChartAction(selectedNode.getDBObject().getAbsolutePath()));
            popupMenu.add(menuItem2);
        }

        JMenuItem menuItem = new JMenuItem(new AddFolderAction(this, selectedNode, selectedNode.getDBObject().getType()));
        popupMenu.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem(new RenameFolderAction(this, selectedNode));
        popupMenu.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem(new DeleteFolderAction(this, selectedNode));
        popupMenu.add(menuItem3);

        if (testSql) {
        	JMenuItem menuItem4 = new JMenuItem(new ValidateSqlsAction(selectedNode.getDBObject()));
        	popupMenu.add(menuItem4);
        }

        popupMenu.show((Component) e.getSource(), e.getX(), e.getY());
    }


    private void populateTree(byte typeRoot) {
        model = new DBBrowserTreeModel(new DBBrowserNode(new DBObject(getRootName(typeRoot), null, typeRoot)));
        setModel(model);
        DBBrowserNode root = (DBBrowserNode) model.getRoot();
        //if (typeRoot == DBObject.DATABASE ) {
        //setRootVisible(false);
        //}
        addTreeExpansionListener(new NodeExpansionListener());
        setShowsRootHandles(true);
        expandNode(root, false);
        // for datasource root node expand the group nodes (tables, views, queries, reports)
        // this is necessary when starting the applications and open a query without expanding the tree
        if (typeRoot == DBObject.DATASOURCE) {
            if (root.getChildCount() > 0) {
                expandNode((DBBrowserNode) root.getChildAt(0), false);
            }
        }
    }

    private String getRootName(byte typeRoot) {
        switch (typeRoot) {
            case DBObject.DATASOURCE:
                return DBNodeExpander.CONNECTIONS;
            case DBObject.DATABASE:
            	DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
            	if (ds == null) {
            		return "<Database name>";
            	} else {
            		return ds.getName();
            	}
            case DBObject.TABLES_GROUP:
                return DBNodeExpander.TABLES;
            case DBObject.VIEWS_GROUP:
                return DBNodeExpander.VIEWS;
            case DBObject.QUERIES_GROUP:
                return DBNodeExpander.QUERIES;
            case DBObject.REPORTS_GROUP:
                return DBNodeExpander.REPORTS;
            case DBObject.CHARTS_GROUP:
                return DBNodeExpander.CHARTS;
            case DBObject.PROCEDURES_GROUP:
                return DBNodeExpander.PROCEDURES;
            default:
                return "ROOT";
        }
    }
    
    public String getRootAbsolutePath(byte typeRoot) {    	
    	switch (typeRoot) {
    		case DBObject.REPORTS_GROUP:
    			return FileReportPersistence.getReportsAbsolutePath();
    		case DBObject.QUERIES_GROUP:
    			return FileReportPersistence.getQueriesAbsolutePath();
    		case DBObject.CHARTS_GROUP:
    			return FileReportPersistence.getChartsAbsolutePath();
    		default:
    			throw new IllegalArgumentException("Invalid typeRoot = " + typeRoot);
    	}    	
    }

    /**
     * This method takes the node string and
     * traverses the tree till it finds the node
     * matching the string. If the match is found
     * the node is returned else null is returned
     *
     * @param nodeStr node string to search for
     * @return tree node
     */
    public DBBrowserNode searchNode(String nodeStr) {
        DBBrowserNode node = null;

        Enumeration enumeration = ((DBBrowserNode) model.getRoot()).breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            node = (DBBrowserNode) enumeration.nextElement();
            if (nodeStr.equals(node.getUserObject().toString())) {
                return node;
            }
        }
        return null;
    }

    public DBBrowserNode searchNode(String nodeStr, byte type) {

        // possibly SCHEMA node was not expanded, give it a chance to load children
        try {
            String schemaName = Globals.getDBViewer().getUserSchema();
            if (schemaName != null) {
                DBBrowserNode node = searchNode(schemaName);
                if (node != null) {
                    if (node.getChildCount() == 0) {
                        startExpandingTree(node, false, null);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }

        DBBrowserNode node = searchNode(DBNodeExpander.getNodeExpanderName(type));
        // possibly node of 'type' was not expanded, give it a chance to load children
        if (node != null) {
            if (node.getChildCount() == 0) {
                startExpandingTree(node, false, null);
            }
        }

        Enumeration enumeration = ((DBBrowserNode) model.getRoot()).breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            node = (DBBrowserNode) enumeration.nextElement();
            if (nodeStr.equals(node.getUserObject().toString()) && (node.getDBObject().getType() == type)) {
                return node;
            }
        }
        return null;
    }

    public DBBrowserNode searchNode(String nodeStr, String path, byte type) {

        Map<String, Boolean> expanded = new HashMap<String, Boolean>();

        // possibly SCHEMA node was not expanded, give it a chance to load children
        try {
            String schemaName = Globals.getDBViewer().getUserSchema();
            if (schemaName != null) {
                DBBrowserNode node = searchNode(schemaName);
                if (node != null) {
                    if (node.getChildCount() == 0) {
                        startExpandingTree(node, false, null);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }

        DBBrowserNode node = searchNode(DBNodeExpander.getNodeExpanderName(type));
        // possibly node of 'type' was not expanded, give it a chance to load children
        if (node != null) {
            if (node.getChildCount() == 0) {
                startExpandingTree(node, false, null);
            }
        }

        boolean done = false;
        while (!done) {
            done = true;
            Enumeration enumeration = ((DBBrowserNode) model.getRoot()).breadthFirstEnumeration();
            while (enumeration.hasMoreElements()) {
                node = (DBBrowserNode) enumeration.nextElement();
                // try to expand folder
                if (node.getDBObject().isFolder()) {
                    //if (node.getChildCount() == 0) {
                    Boolean expand = expanded.get(node.getDBObject().getAbsolutePath());
                    if ((expand == null) || !expand.booleanValue()) {
                        done = false;
                        expanded.put(node.getDBObject().getAbsolutePath(), true);
                        startExpandingTree(node, false, null);
                    }
                }
                //System.out.println("#### path="+path);
                if (nodeStr.equals(node.getUserObject().toString()) && (node.getDBObject().getType() == type) &&
                        path.equals(node.getDBObject().getAbsolutePath())) {
                    return node;
                }
            }
        }
        return null;
    }

    public void selectNode(String name, byte type) {
        DBBrowserNode foundNode = searchNode(name, type);
        if (foundNode != null) {
            TreePath treePath = new TreePath(foundNode.getPath());
            this.setSelectionPath(treePath);
            this.scrollPathToVisible(treePath);
        }
    }

    public void selectNode(String name, String path, byte type) {
        DBBrowserNode foundNode = searchNode(name, path, type);
        //System.out.println("----------------");
        //System.out.println("name="+name + "  path="+path + " type="+type);
        //System.out.println("foundNode="+foundNode);
        if (foundNode != null) {
            TreePath treePath = new TreePath(foundNode.getPath());
            this.setSelectionPath(treePath);
            this.scrollPathToVisible(treePath);
        }
    }

    public void selectNode(DBObject object) {
        selectNode(object.getName(), object.getAbsolutePath(), object.getType());
    }

    public void loadQueries() {

        DBBrowserNode node = searchNode(DBNodeExpander.QUERIES);
        expandNode(node, false);
    }

    /**
     * This method removes the passed tree node from the tree
     * and selects appropiate node
     *
     * @param selNode node to be removed
     */
    public void removeNode(DBBrowserNode selNode) {
        if (selNode != null) {
            //get the parent of the selected node
            DBBrowserNode parent = (DBBrowserNode) (selNode.getParent());

            // if the parent is not null
            if (parent != null) {
                //get the sibling node to be selected after removing the
                //selected node
                DBBrowserNode toBeSelNode = getSibling(selNode);

                //if there are no siblings select the parent node after removing the node
                if (toBeSelNode == null) {
                    toBeSelNode = parent;
                }

                //make the node visible by scroll to it
                TreeNode[] nodes = model.getPathToRoot(toBeSelNode);
                TreePath path = new TreePath(nodes);
                scrollPathToVisible(path);
                setSelectionPath(path);

                //remove the node from the parent
                model.removeNodeFromParent(selNode);
            }
        }
    }


    public void renameNode(DBBrowserNode selNode, String name, String newAbsolutePath) {
        if (selNode != null) {
            selNode.setName(name);
            if (newAbsolutePath != null) {
                //System.out.println("--- set abs  path="+newAbsolutePath);
                selNode.getDBObject().setAbsolutePath(newAbsolutePath);
            }
            model.nodeChanged(selNode);
        }
    }

    /**
     * This method returns the previous sibling node
     * if there is no previous sibling it returns the next sibling
     * if there are no siblings it returns null
     *
     * @param selNode selected node
     * @return previous or next sibling, or parent if no sibling
     */
    private DBBrowserNode getSibling(DBBrowserNode selNode) {
        //get previous sibling
        DBBrowserNode sibling = (DBBrowserNode) selNode.getPreviousSibling();
        if (sibling == null) {
            //if previous sibling is null, get the next sibling
            sibling = (DBBrowserNode) selNode.getNextSibling();
        }
        return sibling;
    }

    public void addQuery(String name, String path) throws Exception {
        addEntity(name, path, DBObject.QUERIES);
    }

    public void addReport(String name, String path) throws Exception {
        addEntity(name, path, DBObject.REPORTS);
    }

    public void addChart(String name, String path) throws Exception {
        addEntity(name, path, DBObject.CHARTS);
    }

    private void addEntity(String name, String path, byte type) throws Exception {

        String nodeString;
        byte folderType;
        if (type == DBObject.QUERIES) {
            nodeString = DBNodeExpander.QUERIES;
            folderType = DBObject.FOLDER_QUERY;
        } else if (type == DBObject.REPORTS) {
            nodeString = DBNodeExpander.REPORTS;
            folderType = DBObject.FOLDER_REPORT;
        } else {
            nodeString = DBNodeExpander.CHARTS;
            folderType = DBObject.FOLDER_CHART;
        }

        DBBrowserNode node = searchNode(nodeString);

        // possibly node was not expanded, give it a chance to load children
        if (node.getChildCount() == 0) {
            startExpandingTree(node, false, null);
        }

        String schema = Globals.getDBViewer().getUserSchema();
        DBObject obj = new DBObject(name, schema, type);
        obj.setAbsolutePath(path);
        DBBrowserNode repNode = new DBBrowserNode(obj);
        repNode.setAllowsChildren(false);

        String _path = path.substring(0, path.lastIndexOf(File.separator));
        int index = _path.lastIndexOf(File.separator);
        String parentName = _path.substring(index + 1);        
        DBBrowserNode foundNodeP = searchNode(parentName, _path, folderType);
        DBBrowserNode foundNode = searchNode(name, path, type);
        //System.out.println("foundNodeP="+foundNodeP);
        //System.out.println("foundNode="+foundNode);
        if (foundNode == null) {
            if (foundNodeP == null) {
                // try to expand folder
                if (node.getChildCount() == 0) {
                    startExpandingTree(node, false, null);
                } else {
                    insertFile(node, repNode);
                }
                model.nodeStructureChanged(node);
            } else {
                if (foundNodeP.getChildCount() == 0) {
                    startExpandingTree(foundNodeP, false, null);
                } else {
                    insertFile(foundNodeP, repNode);
                }
                model.nodeStructureChanged(foundNodeP);
            }
        }

    }

    public void addDataSource(String name) {
        DBBrowserNode node = searchNode(DBNodeExpander.CONNECTIONS);
        
        // possibly node was not expanded, give it a chance to load children
        if (node.getChildCount() == 0) {
            startExpandingTree(node, false, null);
        }

        DBBrowserNode dsNode = new DBBrowserNode(new DBObject(name, null, DBObject.DATABASE));
        dsNode.setAllowsChildren(true);

        DBBrowserNode foundNode = searchNode(name, DBObject.DATABASE);
        if (foundNode == null) {
            TreePath path = instance.getSelectionPath();
            Enumeration<TreePath> expandedPath = instance.getExpandedDescendants(path);
            
            int size = node.getChildCount();
            int insertedIndex = size;
            for (int i=0; i<size; i++)  {
            	DBBrowserNode child = (DBBrowserNode)node.getChildAt(i);
            	if (Collator.getInstance().compare(name, child.getDBObject().getName()) < 0) {            		
            		insertedIndex = i;
            		break;
            	}
            }            
            node.insert(dsNode, insertedIndex);
            model.nodeStructureChanged(node);
            // keep expanded path
            if (expandedPath != null) {
                while (expandedPath.hasMoreElements()) {
                    instance.expandPath(expandedPath.nextElement());
                }
            }
        }

    }

    public void addFolder(String name, String absPath, byte type, boolean onRoot) {

        DBObject obj = new DBObject(name, null, type);
        obj.setAbsolutePath(absPath);
        DBBrowserNode folderNode = new DBBrowserNode(obj);
        folderNode.setAllowsChildren(true);
        //System.out.println("add folder name="+name + "  absPath="+absPath + "  type="+type);

        DBBrowserNode foundNode = searchNode(name, absPath, type);
        //System.out.println("foundNode="+foundNode);
        if (foundNode == null) {
            TreePath path = instance.getSelectionPath();
            Enumeration<TreePath> expandedPath = instance.getExpandedDescendants(path);
            DBBrowserNode node;
            if (onRoot) {
                if (type == DBObject.FOLDER_QUERY) {
                    node = searchNode(DBNodeExpander.QUERIES);
                } else if (type == DBObject.FOLDER_REPORT) {
                    node = searchNode(DBNodeExpander.REPORTS);
                } else {
                    node = searchNode(DBNodeExpander.CHARTS);
                }
            } else {
                String parentPath = absPath.substring(0, absPath.lastIndexOf(File.separator));
                String parentName = parentPath.substring(parentPath.lastIndexOf(File.separator) + 1);
                node = searchNode(parentName, parentPath, type);
            }
            insertFolder(node, folderNode);
            model.nodeStructureChanged(node);
            // keep expanded path
            if (expandedPath != null) {
                while (expandedPath.hasMoreElements()) {
                    instance.expandPath(expandedPath.nextElement());
                }
            }
        }
    }

    // insert folder node to its position (first are folders , then queries/reports, ordered by name
    private void insertFolder(DBBrowserNode parentNode, DBBrowserNode folderNode) {
        int count = parentNode.getChildCount();
        int index = 0;
        for (int i = 0; i < count; i++) {
            DBBrowserNode node = (DBBrowserNode) parentNode.getChildAt(i);
            if (node.getDBObject().isFolder()) {
                int compare = Collator.getInstance().compare(node.getDBObject().getName(), folderNode.getDBObject().getName());
                if (compare < 0) {
                    index++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        parentNode.insert(folderNode, index);
    }

    // insert file node to its position
    private void insertFile(DBBrowserNode parentNode, DBBrowserNode fileNode) {
        int count = parentNode.getChildCount();
        int index = 0;
        for (int i = 0; i < count; i++) {
            DBBrowserNode node = (DBBrowserNode) parentNode.getChildAt(i);
            if (node.getDBObject().isFolder()) {
                index++;
            } else {
                int compare = Collator.getInstance().compare(node.getDBObject().getName(), fileNode.getDBObject().getName());
                if (compare < 0) {
                    index++;
                } else {
                    break;
                }
            }
        }
        parentNode.insert(fileNode, index);
    }

    public void deleteFolder(String name, String absPath, byte type) {
        DBBrowserNode foundNode = searchNode(name, absPath, type);
        if (foundNode != null) {
            DBBrowserNode node = (DBBrowserNode) foundNode.getParent();
            foundNode.removeFromParent();
            model.nodeStructureChanged(node);
        }
    }

    public void renameFolder(String oldName, String newName, String oldAbsolutePath, String newAbsolutePath, byte type) {
        //System.out.println("oldName="+oldName + "  newName="+newName +
        //   "   oldPath="+oldAbsolutePath + "  newPath=" + newAbsolutePath);
        DBBrowserNode foundNode = searchNode(oldName, oldAbsolutePath, type);
        if (foundNode != null) {            
            foundNode.setName(newName);
            foundNode.getDBObject().setAbsolutePath(newAbsolutePath);
            model.nodeChanged(foundNode);
            // change absolute path for all children
            Enumeration enumeration = foundNode.breadthFirstEnumeration();
            while (enumeration.hasMoreElements()) {
                DBBrowserNode node = (DBBrowserNode) enumeration.nextElement();
                String nodePath = node.getDBObject().getAbsolutePath();
                String name = nodePath.substring(nodePath.lastIndexOf(File.separator) + 1);
                String parentPath = ((DBBrowserNode) node.getParent()).getDBObject().getAbsolutePath();
                if (parentPath == null) {
                    if (type == DBObject.FOLDER_QUERY) {
                        parentPath = FileReportPersistence.getQueriesAbsolutePath();
                    } else if (type == DBObject.FOLDER_REPORT) {
                        parentPath = FileReportPersistence.getReportsAbsolutePath();
                    } else {
                        parentPath = FileReportPersistence.getChartsAbsolutePath();
                    }
                }
                String path = parentPath + File.separator + name;
                //System.out.println("oldPath ="  +  node.getDBObject().getAbsolutePath() + " new path = " + path);
                node.getDBObject().setAbsolutePath(path);
            }
        }
    }

    public void modifyDataSource(String oldName, String name) {
        DBBrowserNode node = searchNode(DBNodeExpander.CONNECTIONS);

        // possibly node was not expanded, give it a chance to load children
        if (node.getChildCount() == 0) {
            startExpandingTree(node, false, null);
        }

        DBBrowserNode dsNode = new DBBrowserNode(new DBObject(name, null, DBObject.DATABASE));
        dsNode.setAllowsChildren(true);

        DBBrowserNode foundNode = searchNode(oldName);
//        System.out.println(">> found = " + foundNode);
        if (foundNode != null) {
            renameNode(foundNode, name, null);
            model.nodeStructureChanged(node);
        }

    }


    public void refreshTreeOnRestore() {

        // clear anything inside query and report panels
        NewQueryAction nq = new NewQueryAction();
        nq.actionPerformed(null);
        if (!nq.executed()) {
            return;
        }

        // disconnect data source if connected
        DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
        if (ds != null) {
            try {
                DefaultDataSourceManager.getInstance().disconnect(ds.getName());
            } catch (NotFoundException e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }

        // load connections from datasource.xml
        DefaultDataSourceManager.getInstance().load();

        populateTree(DBObject.DATASOURCE);
        model.reload();
    }

    public void refreshSchemas(String dbName) {

        DBBrowserNode node = searchNode(dbName, DBObject.DATABASE);
        if (node != null) {
            node.removeAllChildren();
            expandNode(node, false);
        }

    }
    
    public void refreshParentNode(DBBrowserNode node) {
    	if (node == null) {
    		node = searchNode(getRootName(typeRoot));
    	}
    	if (node != null) {
            node.removeAllChildren();
            expandNode(node, false);
        }    	
    }


    public void startExpandingTree(DBBrowserNode node, boolean selectNode, Map selectedPathNames) {
        expandNode(node, selectNode);
    }

    private boolean expandNode(DBBrowserNode node, boolean selectNode) {
        if (node == null) {
            throw new IllegalArgumentException("DBBrowserNode is null");
        }

        // if node hasn't already been expanded.
        if (node.getChildCount() == 0) {
            // add together the standard expanders for this node type and any
            // individual expanders that there are for the node and process them.
            final byte nodeType = node.getDBObject().getType();
            NodeExpander[] expanders = model.getExpanders(nodeType);
            new TreeLoader(node, expanders, selectNode).execute();
            return true;
        }

        return false;
    }

    class NodeExpansionListener implements TreeExpansionListener {

        public void treeExpanded(TreeExpansionEvent ev) {
            final TreePath path = ev.getPath();
            final Object parentObj = path.getLastPathComponent();
            if (parentObj instanceof DBBrowserNode) {
                startExpandingTree((DBBrowserNode) parentObj, false, null);
//                expandedPathNames.put(path.toString(), null);
            }
        }

        public void treeCollapsed(TreeExpansionEvent ev) {
//            expandedPathNames.remove(ev.getPath().toString());
        }

    }

    class TreeLoader {

        private DBBrowserNode parentNode;
        private NodeExpander[] expanders;
        private boolean selectParentNode;

        TreeLoader(DBBrowserNode parentNode, NodeExpander[] expanders,
                   boolean selectParentNode) {
            super();
            this.parentNode = parentNode;
            this.expanders = expanders;
            this.selectParentNode = selectParentNode;
        }

        void execute() {
            try {
                try {
                    loadChildren();
                } finally {
                    fireStructureChanged(parentNode);
                    if (selectParentNode) {
                        clearSelection();
                        setSelectionPath(new TreePath(parentNode.getPath()));
                    }
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }

        /**
         * This expands the parent node and shows all its children.
         */
        private void loadChildren() throws Exception {
            for (int i = 0; i < expanders.length; ++i) {
                boolean nodeTypeAllowsChildren = false;
                byte lastNodeType = -1;
                List list = expanders[i].createChildren(parentNode);
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Object nextObj = it.next();
                    if (nextObj instanceof DBBrowserNode) {
                        DBBrowserNode childNode = (DBBrowserNode) nextObj;
                        byte childNodeType = childNode.getDBObject().getType();
                        if (childNodeType != lastNodeType) {
                            lastNodeType = childNodeType;
                            if (model.getExpanders(childNodeType).length > 0) {
                                nodeTypeAllowsChildren = true;
                            } else {
                                nodeTypeAllowsChildren = false;
                            }
                        }
                        childNode.setAllowsChildren(nodeTypeAllowsChildren);
                        parentNode.add(childNode);
                    }
                }
            }
        }

        /**
         * Let the object tree model know that its structure has changed.
         */
        private void fireStructureChanged(final DBBrowserNode node) {
            DBBrowserTree.this.model.nodeStructureChanged(node);
        }

    }

    class DBBrowserTreeRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, hasFocus);

            DBBrowserNode node = (DBBrowserNode) value;
            setText((String) node.getUserObject());

            switch (node.getDBObject().getType()) {
                // datasource
                case DBObject.DATASOURCE:
                    setIcon(ImageUtil.getImageIcon("connection"));
                    break;
                    // databse
                case DBObject.DATABASE:
                    DataSource dataSource = DefaultDataSourceManager.getInstance().getConnectedDataSource();
                    if (dataSource != null && dataSource.getName().equals(node.getUserObject())) {
                        setIcon(ImageUtil.getImageIcon("database_connect"));
                    } else {
                        setIcon(ImageUtil.getImageIcon("database"));
                    }
                    break;
                    // schema
                case DBObject.SCHEMA:
                    setIcon(ImageUtil.getImageIcon("schema"));
                    break;
                    // tables group
                case DBObject.TABLES_GROUP:
                    setIcon(ImageUtil.getImageIcon("entitygroup"));
                    break;
                    // views group
                case DBObject.VIEWS_GROUP:
                    setIcon(ImageUtil.getImageIcon("entitygroup"));
                    break;
                case DBObject.PROCEDURES_GROUP:
                    setIcon(ImageUtil.getImageIcon("entitygroup"));
                    break;
                    // tabela
                case DBObject.TABLE:
                    setIcon(ImageUtil.TABLE_IMAGE_ICON);
                    break;
                    // view
                case DBObject.VIEW:
                    setIcon(ImageUtil.VIEW_IMAGE_ICON);
                    break;
                case DBObject.PROCEDURES:
                    Boolean valid = (Boolean) node.getDBObject().getProperty(ValidateProceduresAction.VALID_PROPERTY);
                    if ((valid == null) || (valid.booleanValue() == false)) {
                        setIcon(ImageUtil.PROCEDURE_IMAGE_ICON);
                    } else {
                        setIcon(ImageUtil.PROCEDURE_VALID_IMAGE_ICON);
                    }
                    break;
                    // coloana
                case DBObject.COLUMN:
                    setIcon(ImageUtil.getImageIcon("column"));
                    break;
                case DBObject.QUERIES_GROUP:
                    setIcon(ImageUtil.getImageIcon("queries"));
                    break;
                case DBObject.QUERIES:
                	Boolean validQ = (Boolean) node.getDBObject().getProperty(ValidateSqlsAction.VALID_SQL_PROPERTY);
                	if ((validQ == null) || validQ.booleanValue()) {
                		setIcon(ImageUtil.QUERY_ICON);
                    } else {
                    	setIcon(ImageUtil.QUERY_ERROR_ICON);
                    }
                    break;
                case DBObject.REPORTS_GROUP:
                    setIcon(ImageUtil.getImageIcon("reports"));
                    break;
                case DBObject.REPORTS:
                	Boolean validR = (Boolean) node.getDBObject().getProperty(ValidateSqlsAction.VALID_SQL_PROPERTY);
                	if ((validR == null) || validR.booleanValue()) {
                		setIcon(ImageUtil.REPORT_ICON);
                    } else {
                    	setIcon(ImageUtil.REPORT_ERROR_ICON);
                    }
                    break;
                case DBObject.CHARTS_GROUP:
                    setIcon(ImageUtil.getImageIcon("charts"));
                    break;
                case DBObject.CHARTS:
                	Boolean validC = (Boolean) node.getDBObject().getProperty(ValidateSqlsAction.VALID_SQL_PROPERTY);
                	if ((validC == null) || validC.booleanValue()) {
                		setIcon(ImageUtil.CHART_ICON);
                    } else {
                    	setIcon(ImageUtil.CHART_ERROR_ICON);
                    }
                    break;
                case DBObject.FOLDER_QUERY:
                case DBObject.FOLDER_REPORT:
                case DBObject.FOLDER_CHART:
                    setIcon(ImageUtil.getImageIcon("folder"));
                    break;
            }

            return this;
        }
    }

    class DBBrowserTreeDragGestureListener implements DragGestureListener {

        public void dragGestureRecognized(DragGestureEvent dge) {
            TreePath path = DBBrowserTree.this.getSelectionPath();
            if (path == null) {
                return;
            }
            DBBrowserNode selectedNode = (DBBrowserNode) path.getLastPathComponent();
            byte nodeType = selectedNode.getDBObject().getType();
            if ((nodeType == DBObject.TABLE) || (nodeType == DBObject.VIEW)) {
                Table table = new Table(selectedNode.getDBObject().getName());
                table.setSchemaName(selectedNode.getDBObject().getSchemaName());
                try {
                    table.setDialect(DialectUtil.getDialect(Globals.getConnection()));
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    LOG.error(e.getMessage(), e);
                }
                Transferable transferable = new DBTableTransferable(table);
                dge.startDrag(DragSource.DefaultMoveNoDrop, transferable,
                        new DBBrowserTreeDragSourceListener());
            } else if (nodeType == DBObject.PROCEDURES) {
                Transferable transferable = new DBProcTransferable(new DBProcedure(
                        selectedNode.getDBObject().getSchemaName(),
                        selectedNode.getDBObject().getCatalog(),
                        selectedNode.getDBObject().getName(), 0
                )
                );
                dge.startDrag(DragSource.DefaultMoveNoDrop, transferable,
                        new DBBrowserTreeDragSourceListener());

            } else if ((nodeType == DBObject.QUERIES) || (nodeType == DBObject.REPORTS) ||
                    (nodeType == DBObject.CHARTS) ||
                    (nodeType == DBObject.FOLDER_QUERY) || (nodeType == DBObject.FOLDER_REPORT) ||
                    (nodeType == DBObject.FOLDER_CHART)) {

                if ((nodeType == DBObject.REPORTS) || (nodeType == DBObject.CHARTS)) {
                    Globals.setTreeReportAbsolutePath(selectedNode.getDBObject().getAbsolutePath());
                }
                Transferable transferable = new FileTransferable(selectedNode.getDBObject());
                dge.startDrag(DragSource.DefaultMoveNoDrop, transferable,
                        new DBBrowserTreeReportDragSourceListener());
            }
        }

    }

    class DBBrowserTreeDropListener implements DropTargetListener, TreeSelectionListener {

        /**
         * Stores the selected node info
         */
        protected TreePath selectedTreePath = null;
        protected DBBrowserNode selectedNode = null;


        public void dragEnter(DropTargetDragEvent dsde) {
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //set cursor location. Needed in setCursor method
            Point cursorLocationBis = dtde.getLocation();
            TreePath destinationPath =
                    getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

            Transferable tr = dtde.getTransferable();
            //cast into appropriate data type
            try {
                DBObject object = (DBObject) tr.getTransferData(FileTransferable.DATA_FLAVOR);
                if (object == null) {
                    return;
                }
                // if destination path is okay accept drop...
                if (testDropTarget(destinationPath, selectedTreePath, object.getType()) == null) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                }
                // ...otherwise reject drop
                else {
                    dtde.rejectDrag();
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        public void dragExit(DropTargetEvent dte) {
        }

        /**
         * DropTargetListener interface method - What we do when drag is released
         */
        public void drop(DropTargetDropEvent e) {
            try {
                Transferable tr = e.getTransferable();

                //flavor not supported, reject drop
                if (!tr.isDataFlavorSupported(FileTransferable.DATA_FLAVOR)) {
                    e.rejectDrop();
                }

                //cast into appropriate data type
                DBObject object = (DBObject) tr.getTransferData(FileTransferable.DATA_FLAVOR);

                if (object == null) {
                    return;
                }

                //get new parent node
                Point loc = e.getLocation();
                TreePath destinationPath = getPathForLocation(loc.x, loc.y);

                final String msg = testDropTarget(destinationPath, selectedTreePath, object.getType());
                if (msg != null) {
                    e.rejectDrop();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(Globals.getMainFrame(), msg,
                                    "Error Dialog", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    return;
                }

                DBBrowserNode newParent = (DBBrowserNode) destinationPath.getLastPathComponent();
                String newPath = newParent.getDBObject().getAbsolutePath();

                // move to root
                if (newPath == null) {
                    if ((object.getType() == DBObject.QUERIES) || (object.getType() == DBObject.FOLDER_QUERY)) {
                        newPath = new File(FileReportPersistence.CONNECTIONS_DIR + File.separator +
                                DefaultDataSourceManager.getInstance().getConnectedDataSource().getName() +
                                File.separator + FileReportPersistence.QUERIES_FOLDER).getAbsolutePath();
                    } else if ((object.getType() == DBObject.REPORTS) || (object.getType() == DBObject.FOLDER_REPORT)) {
                        newPath = new File(FileReportPersistence.CONNECTIONS_DIR + File.separator +
                                DefaultDataSourceManager.getInstance().getConnectedDataSource().getName() +
                                File.separator + FileReportPersistence.REPORTS_FOLDER).getAbsolutePath();
                    } else if ((object.getType() == DBObject.CHARTS) || (object.getType() == DBObject.FOLDER_CHART)) {
                        newPath = new File(FileReportPersistence.CONNECTIONS_DIR + File.separator +
                                DefaultDataSourceManager.getInstance().getConnectedDataSource().getName() +
                                File.separator + FileReportPersistence.CHARTS_FOLDER).getAbsolutePath();
                    }
                }

                //get old parent node
                DBBrowserNode oldParent = (DBBrowserNode) getSelectedNode().getParent();

                int action = e.getDropAction();
                boolean copyAction = (action == DnDConstants.ACTION_COPY);

                //make new child node
                String oldAbsolutePath = getSelectedNode().getDBObject().getAbsolutePath();
                DBObject newObject = new DBObject(object.getName(), object.getSchemaName(), object.getType());
                String newAbsolutePath = newPath;
                if (object.getType() == DBObject.QUERIES) {
                    newAbsolutePath = newAbsolutePath + File.separator +
                            object.getName() + FileReportPersistence.REPORT_EXTENSION_SEPARATOR +
                            FileReportPersistence.REPORT_EXTENSION;
                } else if (object.getType() == DBObject.REPORTS) {
                    newAbsolutePath = newAbsolutePath + File.separator +
                            object.getName() + FormSaver.REPORT_FULL_EXTENSION;
                } else if (object.getType() == DBObject.CHARTS) {
                    newAbsolutePath = newAbsolutePath + File.separator +
                            object.getName() + ChartUtil.CHART_FULL_EXTENSION;
                } else {
                    //folder
                    newAbsolutePath = newAbsolutePath + File.separator + object.getName();
                }

                newObject.setAbsolutePath(newAbsolutePath);
                DBBrowserNode newChild = new DBBrowserNode(newObject);
                if ((object.getType() == DBObject.FOLDER_QUERY) || (object.getType() == DBObject.FOLDER_REPORT) ||
                        (object.getType() == DBObject.FOLDER_CHART)) {
                    newChild.setAllowsChildren(true);
                } else {
                    newChild.setAllowsChildren(false);
                }

                try {

                    boolean overwrite = false;
                    if (new File(newObject.getAbsolutePath()).exists()) {
                        String message;
                        if (object.getType() == DBObject.QUERIES) {
                            message = I18NSupport.getString("import.query.exists", object.getName());
                            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), message, "", JOptionPane.YES_NO_OPTION);
                            if (option != JOptionPane.YES_OPTION) {
                                return;
                            } else {
                                overwrite = true;
                            }
                        } else if (object.getType() == DBObject.REPORTS) {
                            message = I18NSupport.getString("import.report.exists", object.getName());
                            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), message, "", JOptionPane.YES_NO_OPTION);
                            if (option != JOptionPane.YES_OPTION) {
                                return;
                            } else {
                                overwrite = true;
                            }
                        } else if (object.getType() == DBObject.CHARTS) {
                            message = I18NSupport.getString("import.chart.exists", object.getName());
                            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(), message, "", JOptionPane.YES_NO_OPTION);
                            if (option != JOptionPane.YES_OPTION) {
                                return;
                            } else {
                                overwrite = true;
                            }
                        } else {
                            message = I18NSupport.getString("folder.add.exists", object.getName());
                            Show.info(message);
                            return;
                        }
                    }

                    boolean save = true;
                    Report report = null;
                    Chart chart = null;
                    ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                            Globals.getReportPersistenceType());
                    File newFile = new File(newAbsolutePath);
                    if (!object.isFolder()) {
                        if ((object.getType() == DBObject.REPORTS) || (object.getType() == DBObject.QUERIES)) {
                            report = ReportUtil.loadConvertedReport(new FileInputStream(object.getAbsolutePath()));
                        } else if (object.getType() == DBObject.CHARTS) {
                            chart = ChartUtil.loadChart(new FileInputStream(object.getAbsolutePath()));
                        }
                    }
                    if (object.getType() == DBObject.QUERIES) {
                        save = repPersist.saveReport(report, newObject.getAbsolutePath());
                    } else if (object.getType() == DBObject.REPORTS) {
                        save = FormSaver.getInstance().save(newFile, report);
                        try {
                            FileUtil.copyImages(report, newFile.getParentFile());
                        } catch (IOException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    } else if (object.getType() == DBObject.CHARTS) {
                        save = ChartUtil.save(newFile, chart);
                    } else {
                        newFile.mkdirs();
                        FileUtil.copyDirToDir(new File(object.getAbsolutePath()), newFile);
                    }

                    if (save) {
                        if (!copyAction) {
                            // delete is the same for query and report(delete the file)
                            if (object.isFolder()) {
                                FileUtil.deleteDir(new File(oldAbsolutePath));
                            } else {
                                repPersist.deleteReport(oldAbsolutePath);
                            }
                            oldParent.remove(getSelectedNode());

                            // if we move the current loaded query/report take care to update
                            // the path in Globals
                            if (object.getType() == DBObject.QUERIES) {
                                if (oldAbsolutePath.equals(Globals.getCurrentQueryAbsolutePath())) {
                                    Globals.setCurrentQueryAbsolutePath(newAbsolutePath);
                                }
                            } else if (object.getType() == DBObject.REPORTS) {
                                if (oldAbsolutePath.equals(Globals.getCurrentReportAbsolutePath())) {
                                    Globals.setCurrentReportAbsolutePath(newAbsolutePath);
                                }
                            } else if (object.getType() == DBObject.CHARTS) {
                                if (oldAbsolutePath.equals(Globals.getCurrentChartAbsolutePath())) {
                                    Globals.setCurrentChartAbsolutePath(newAbsolutePath);
                                }
                            } else {
                                String qPath = Globals.getCurrentQueryAbsolutePath();
                                String rPath = Globals.getCurrentReportAbsolutePath();
                                String cPath = Globals.getCurrentChartAbsolutePath();
                                if ((qPath != null) && qPath.startsWith(oldAbsolutePath)) {
                                    String newQPath = newAbsolutePath + File.separator +
                                            qPath.substring(oldAbsolutePath.length() + 1);
                                    Globals.setCurrentQueryAbsolutePath(newQPath);
                                } else if ((rPath != null) && rPath.startsWith(oldAbsolutePath)) {
                                    String newRPath = newAbsolutePath + File.separator +
                                            rPath.substring(oldAbsolutePath.length() + 1);
                                    Globals.setCurrentReportAbsolutePath(newRPath);
                                } else if ((cPath != null) && cPath.startsWith(oldAbsolutePath)) {
                                    String newCPath = newAbsolutePath + File.separator +
                                            cPath.substring(oldAbsolutePath.length() + 1);
                                    Globals.setCurrentChartAbsolutePath(newCPath);
                                }
                            }

                        }
                        boolean expand = expandNode(newParent, false);
                        // if node was not expanded (expand==true) we do not need to add the node
                        // because this is done by the nod expander (see that report was saved through
                        // saveReport previously!)
                        if (!expand) {
                            if (object.isFolder()) {
                                insertFolder(newParent, newChild);
                            } else {
                                if (!overwrite) {
                                    insertFile(newParent, newChild);
                                }
                            }
                        }
                    }

                    if (copyAction) {
                        e.acceptDrop(DnDConstants.ACTION_COPY);
                    } else {
                        e.acceptDrop(DnDConstants.ACTION_MOVE);
                    }
                }
                catch (java.lang.IllegalStateException ils) {
                    e.rejectDrop();
                }

                e.getDropTargetContext().dropComplete(true);

                //expand nodes appropriately - this probably isnt the best way...
                DefaultTreeModel model = (DefaultTreeModel) getModel();
                model.reload(oldParent);
                model.reload(newParent);
                TreePath parentPath = new TreePath(newParent.getPath());
                expandPath(parentPath);
            }
            catch (IOException io) {
                io.printStackTrace();
                e.rejectDrop();
            }
            catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
                e.rejectDrop();
            }
        }

        public DBBrowserNode getSelectedNode() {
            return selectedNode;
        }


        /**
         * Convenience method to test whether drop location is valid
         *
         * @param destination The destination path
         * @param dropper     The path for the node to be dropped
         * @param type        object data type
         * @return null if no problems, otherwise an explanation
         */
        private String testDropTarget(TreePath destination, TreePath dropper, byte type) {

            boolean destinationPathIsNull = destination == null;
            if (destinationPathIsNull) {
                return "Invalid drop location.";
            }

            DBBrowserNode node = (DBBrowserNode) destination.getLastPathComponent();
            if (!node.getAllowsChildren()) {
                return "This node does not allow children";
            }

            if (destination.equals(dropper)) {
                return "Destination cannot be same as source";
            }

            if (dropper.isDescendant(destination)) {
                return "Destination node cannot be a descendant.";
            }

            if (dropper.getParentPath().equals(destination)) {
                return "Destination node cannot be a parent.";
            }

            if ((type == DBObject.QUERIES) || (type == DBObject.FOLDER_QUERY)) {
                if ((node.getDBObject().getType() != DBObject.FOLDER_QUERY) &&
                        (node.getDBObject().getType() != DBObject.QUERIES_GROUP)) {
                    return "No query folder";
                }
            } else if ((type == DBObject.REPORTS) || (type == DBObject.FOLDER_REPORT)) {
                if ((node.getDBObject().getType() != DBObject.FOLDER_REPORT) &&
                        (node.getDBObject().getType() != DBObject.REPORTS_GROUP)) {
                    return "No report folder";
                }
            } else if ((type == DBObject.CHARTS) || (type == DBObject.FOLDER_CHART)) {
                if ((node.getDBObject().getType() != DBObject.FOLDER_CHART) &&
                        (node.getDBObject().getType() != DBObject.CHARTS_GROUP)) {
                    return "No chart folder";
                }
            } else {
                return "No folder";
            }

            return null;
        }

        /**
         * TreeSelectionListener - sets selected node
         */
        public void valueChanged(TreeSelectionEvent evt) {
            selectedTreePath = evt.getNewLeadSelectionPath();
            if (selectedTreePath == null) {
                selectedNode = null;
                return;
            }
            selectedNode = (DBBrowserNode) selectedTreePath.getLastPathComponent();
        }

    }

    class DBBrowserTreeDragSourceListener extends DragSourceAdapter {

        public void dragEnter(DragSourceDragEvent dsde) {
            if ((dsde.getDropAction() & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultLinkDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
            }
        }

        public void dragExit(DragSourceEvent dse) {
            dse.getDragSourceContext().setCursor(DragSource.DefaultLinkNoDrop);
        }

    }

    class DBBrowserTreeReportDragSourceListener extends DragSourceAdapter {

        public void dragEnter(DragSourceDragEvent dsde) {
            setCursor(dsde);
        }

        public void dragExit(DragSourceEvent dse) {
            dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }

        private void setCursor(DragSourceDragEvent dsde) {
            int action = dsde.getDropAction();
            //@todo ACTION_NONE must be removed : but somehow no action is passed here for ACTION_COPY
            if ((action == DnDConstants.ACTION_COPY) || (action == DnDConstants.ACTION_NONE)) {
                //if (action == DnDConstants.ACTION_COPY) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
            } else {
                if (action == DnDConstants.ACTION_MOVE) {
                    dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                } else {
                    dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                }
            }
        }

    }


}
