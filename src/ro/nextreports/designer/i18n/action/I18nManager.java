package ro.nextreports.designer.i18n.action;

import java.util.ArrayList;
import java.util.List;

import ro.nextreports.engine.i18n.I18nLanguage;

public class I18nManager {
	
	private static I18nManager instance = new I18nManager();
	
	private List<String> keys = new ArrayList<String>();
	private List<I18nLanguage> languages = new ArrayList<I18nLanguage>();
	private I18nLanguage currentLanguage;
	
	private I18nManager() {		
	}
	
	public static I18nManager getInstance() {
		return instance;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	
	public void addKeys(List<String> keys) {
		for (String key : keys) {
			if (!this.keys.contains(key)) {
				this.keys.add(key);
			}
		}
	}

	public List<I18nLanguage> getLanguages() {
		return languages;
	}

	public void setLanguages(List<I18nLanguage> languages) {
		this.languages = languages;
	}
	
	public void addLanguage(I18nLanguage language) {
		languages.add(language);
	}

	public I18nLanguage getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(I18nLanguage currentLanguage) {
		this.currentLanguage = currentLanguage;
	}	
	
	public void clear() {
		currentLanguage = null;
		keys.clear();
		languages.clear();
	}

}
