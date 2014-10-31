package ro.nextreports.designer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.NamePatternPanel;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TreeUtil;

public class NamePatternAction extends AbstractAction {
	
	public static final byte TABLE_NAME_PATTERN = 1;
	public static final byte VIEW_NAME_PATTERN = 2;
	public static final byte PROCEDURE_NAME_PATTERN = 4;

	private byte type = TABLE_NAME_PATTERN;	
	
	private static final Log LOG = LogFactory.getLog(NamePatternAction.class);
    
    public NamePatternAction(byte type) {
        putValue(Action.NAME, I18NSupport.getString("pattern.action"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("pattern"));       
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("pattern.action"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("pattern.action"));     
        this.type = type;               
    }

    public void actionPerformed(ActionEvent ev) {
    	NamePatternPanel panel = new NamePatternPanel(getPattern(type));
        BaseDialog dialog = new BaseDialog(panel, getTitle(type));
    	Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);
        if (dialog.okPressed()) {   
        	String pattern = panel.getPattern();
			switch (type) {
				case TABLE_NAME_PATTERN:
					Globals.setTableNamePattern(pattern);
					break;
				case VIEW_NAME_PATTERN:
					Globals.setViewNamePattern(pattern);
					break;
				case PROCEDURE_NAME_PATTERN:
					Globals.setProcedureNamePattern(pattern);
					break;	
				default:
					break;
			}
			try {
				TreeUtil.refreshDatabase();
			} catch (Exception e) {				
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
        }
	}
    
    private String getPattern(byte type) {
    	switch (type) {
			case TABLE_NAME_PATTERN:
				return Globals.getTableNamePattern();
			case VIEW_NAME_PATTERN:
				return Globals.getViewNamePattern();
			case PROCEDURE_NAME_PATTERN:
				return Globals.getProcedureNamePattern();	
			default:
				return null;
    	}		
	}
    	
    private String getTitle(byte type) {
    	switch (type) {
			case TABLE_NAME_PATTERN:
				return I18NSupport.getString("pattern.table");
			case VIEW_NAME_PATTERN:
				return I18NSupport.getString("pattern.view");
			case PROCEDURE_NAME_PATTERN:
				return I18NSupport.getString("pattern.procedure");	
			default:
				return "";
		}		
    }
    


}
