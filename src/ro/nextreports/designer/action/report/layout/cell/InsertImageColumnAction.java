package ro.nextreports.designer.action.report.layout.cell;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.util.ObjectCloner;

public class InsertImageColumnAction extends AbstractAction {
	
	private static final String DEFAULT_TEXT = "?";
	
	public InsertImageColumnAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("insert.db.image.column.action.name"));
    }
	
	public void actionPerformed(ActionEvent event) {
    	ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
		int row = selectionModel.getSelectedCell().getRow();
        int column = selectionModel.getSelectedCell().getColumn();
        
        BandElement element = new ImageColumnBandElement(DEFAULT_TEXT);
        BandUtil.copySettings(grid.getBandElement(selectionModel.getSelectedCell()), element);
        
        grid.putClientProperty("layoutBeforeInsert", ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout()));
        BandUtil.insertElement(element, row, column);
        
        grid.editCellAt(row, column, event);

    }

}
