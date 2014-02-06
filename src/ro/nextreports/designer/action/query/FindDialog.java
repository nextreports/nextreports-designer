package ro.nextreports.designer.action.query;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

public class FindDialog extends BaseDialog {

    private FindPanel findPanel;

    public FindDialog(FindPanel findPanel) {
        super(findPanel, I18NSupport.getString("validate.replace"));
        this.findPanel = findPanel;
    }

    protected boolean ok() {
        String oldText = findPanel.getOldText();
        String newText = findPanel.getNewText();
        if ("".equals(oldText.trim()) || "".equals(newText.trim())) {
        	return false;
        }              
        return true;
    }
}
