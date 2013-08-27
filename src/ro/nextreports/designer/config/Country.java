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
package ro.nextreports.designer.config;

public class Country {
	
	private String iso;
	private String code;
	private String name;
	private String language;

	public Country(String iso, String code, String name, String language) {
		this.iso = iso;
		this.code = code;
		this.name = name;
		this.language = language;
	}		

	public String getIso() {
		return iso;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public String toString() {
		return name.toUpperCase() + " (" + iso + ") : " + language + "," + code;
	}
	
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Country that = (Country) o;

        if (iso != null ? !iso.equals(that.iso) : that.iso != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = (iso != null ? iso.hashCode() : 0);
        result = 29 * result + (code != null ? code.hashCode() : 0);
        result = 29 * result + (name != null ? name.hashCode() : 0);
        result = 29 * result + (language != null ? language.hashCode() : 0);       
        return result;
    }


}
