package ro.nextreports.designer.i18n.action;

import java.util.List;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

public class KeySelectionDialog extends BaseDialog {

    private KeySelectionPanel panel;    
    private boolean okPressed = false;

    public KeySelectionDialog(KeySelectionPanel panel, boolean edit) {
        super(panel, I18NSupport.getString("languages.keys.add"), true);
        if (edit) {
        	setTitle(I18NSupport.getString("languages.keys.edit"));
        }
        this.panel = panel;
    }

    protected boolean ok() {
        
        if ("".equals(panel.getKey().trim())) {        
            Show.info(I18NSupport.getString("languages.keys.selection.key.invalid"));
            return false;
        }
        
        if (panel.showValueField()) {
        	if ("".equals(panel.getValue().trim())) {
        		Show.info(I18NSupport.getString("languages.keys.selection.value.invalid"));
                return false;
        	}
        }
        
        okPressed = true;
        return true;
    }
    
    public String getKey() {
    	return panel.getKey();
    }
    
    public boolean isAll() {
    	return panel.isAll();
    }
    
    public List<String> getKeys() {
    	return panel.getKeys();
    }
    
    public String getValue() {
    	return panel.getValue();
    }
         
    public boolean okPressed() {
        return okPressed;
    }
}

