package ro.nextreports.designer.i18n.action;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.LocaleUtil;
import ro.nextreports.designer.config.Country;
import ro.nextreports.designer.config.CountryRenderer;
import ro.nextreports.engine.i18n.I18nLanguage;

public class LanguageSelectionPanel extends JPanel {

	private JComboBox languagesCombo;
	private JCheckBox defaultCheck;

	private Dimension dim = new Dimension(200, 22);

	public LanguageSelectionPanel() {

		languagesCombo = new JComboBox();
		languagesCombo.setMinimumSize(dim);
		languagesCombo.setPreferredSize(dim);
		for (Country c : LocaleUtil.getCountries()) {
			languagesCombo.addItem(c);
		}
		languagesCombo.setSelectedItem(LocaleUtil.getCountry(Globals.getConfigLocale()));
		languagesCombo.setRenderer(new CountryRenderer());
		
		defaultCheck = new JCheckBox(I18NSupport.getString("languages.default"));

		setLayout(new GridBagLayout());

		add(new JLabel(I18NSupport.getString("languages.selection")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(languagesCombo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 5, 5), 0, 0));
		add(defaultCheck, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		
	}

	public I18nLanguage getLanguage() {
		Country c = (Country) languagesCombo.getSelectedItem();
		I18nLanguage language = new I18nLanguage(c.getLanguage()+ "_" + c.getCode(), defaultCheck.isSelected());
		return language;
	}
	
	public void setLanguage(I18nLanguage language) {
		Country country = getCountry(language);
		if (country != null) {
			languagesCombo.setSelectedItem(country);
		}
		defaultCheck.setSelected(language.isDefault());
	}		
	
	public Country getCountry(I18nLanguage language) {
		for (Country c : LocaleUtil.getCountries()) {
			if (language.getName().equals(c.getLanguage()+"_"+c.getCode())) {
				return c;
			}
		}
		return null;
	}
	
}
