package ro.nextreports.designer.i18n.action;

import java.util.List;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import ro.nextreports.engine.i18n.I18nLanguage;

public class LanguageSelectionDialog extends BaseDialog {

	private List<I18nLanguage> languages;
    private LanguageSelectionPanel panel;    
    private boolean okPressed = false;
    private boolean edit;

    public LanguageSelectionDialog(List<I18nLanguage> languages, LanguageSelectionPanel panel, boolean edit) {
        super(panel, I18NSupport.getString("languages.add"), true);
        if (edit) {
        	setTitle(I18NSupport.getString("languages.edit"));
        }
        this.panel = panel;
        this.languages = languages;
        this.edit = edit;
    }

    protected boolean ok() {
        
       I18nLanguage lang = panel.getLanguage();        
        if (lang.isDefault() && !edit) {
        	for (I18nLanguage language : languages) {
        		if (language.isDefault()) {
        			Show.info(I18NSupport.getString("languages.default.invalid"));
        			return false;
        		}
        	}
        }                
        
        okPressed = true;
        return true;
    }
    
    public I18nLanguage getLanguage() {
    	return panel.getLanguage();
    }       
   
    public boolean okPressed() {
        return okPressed;
    }
}

