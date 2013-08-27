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
package ro.nextreports.designer.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import ro.nextreports.designer.config.Country;


public class LocaleUtil {
	
	public static List<Country> getCountries() {
		List<Country> countries = new ArrayList<Country>();
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			String iso = "NA";
			try {
				iso = locale.getISO3Country();
			} catch (MissingResourceException ex) {				
			}
			String code = locale.getCountry();
			String name = locale.getDisplayCountry();
			String language = locale.getLanguage();

			if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {				
				Country c = new Country(iso, code, name, language);
				if (!countries.contains(c)) {
					countries.add(c);
				}	
			}
		}

		Collections.sort(countries, new Comparator<Country>() {
			@Override
			public int compare(Country c1, Country c2) {
				 return Collator.getInstance().compare(c1.getName(), c2.getName());
			}
			
		});	
		return countries;
	}
	
	public static Country getCountry(Locale locale) {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		for (Country c : getCountries()) {
			if (locale.getCountry().equals(c.getCode()) && locale.getLanguage().equals(c.getLanguage())) {
				return c;
			}
		}
		return null;
	}

}
