package ro.nextreports.designer.i18n.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.chart.SaveChartAction;
import ro.nextreports.designer.action.report.SaveReportAction;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.engine.i18n.I18nLanguage;

public class ManageI18NAction extends AbstractAction {
	
	public ManageI18NAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("languages.manage"));
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("languages"));                
        putValue(MNEMONIC_KEY, ShortcutsUtil.getMnemonic("i18n.mnemonic",  new Integer('I')));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("i18n.accelerator", "control I")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("languages.manage"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("languages.manage"));
    }		

	@Override
	public void actionPerformed(ActionEvent e) {
		final ManageI18nPanel panel = new ManageI18nPanel();
		//Report report = FormLoader.getInstance().load(Globals.getCurrentReportAbsolutePath());
		if (Globals.isReportLoaded()) {
			panel.setKeys(LayoutHelper.getReportLayout().getI18nkeys());
			panel.setLanguages(LayoutHelper.getReportLayout().getLanguages());
		} else if (Globals.isChartLoaded()) {
			panel.setKeys(I18nManager.getInstance().getKeys());
			panel.setLanguages(I18nManager.getInstance().getLanguages());
		}
		
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("languages.manage"), true) {

			@Override
			protected boolean ok() {
				List<String> keys = panel.getKeys();
				if (keys.size() == 0) {
					Show.info(I18NSupport.getString("languages.keys.add.invalid"));					
					return false;
				}
				
				List<I18nLanguage> languages = panel.getLanguages();
				if (languages.size() == 0) {
					Show.info(I18NSupport.getString("languages.add.invalid"));					
					return false;
				}
				
				boolean isDefault = false;
				for (I18nLanguage language : languages) {
					if (language.isDefault()) {
						isDefault = true;
						break;
					}
				}
				if (!isDefault) {
					Show.info(I18NSupport.getString("languages.default.notdefined"));					
					return false;
				}
				
				I18nManager.getInstance().setKeys(keys);
				I18nManager.getInstance().setLanguages(languages);
												
				if (Globals.isReportLoaded()) {
					LayoutHelper.getReportLayout().setI18nkeys(I18nManager.getInstance().getKeys());
					LayoutHelper.getReportLayout().setLanguages(I18nManager.getInstance().getLanguages());
					SaveReportAction action = new SaveReportAction();
					action.actionPerformed(null);
				} else if (Globals.isChartLoaded()) {										
					SaveChartAction action = new SaveChartAction();
					action.actionPerformed(null);
				}
				
				return true;
			}
        	
        };
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);                                

		
	}

}
