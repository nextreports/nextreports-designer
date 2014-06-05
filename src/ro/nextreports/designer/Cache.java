package ro.nextreports.designer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.nextreports.engine.util.NameType;
import ro.nextreports.server.api.client.Md5PasswordEncoder;

public class Cache {
	
	private static Map<String, List<NameType>> columnsCache = new HashMap<String, List<NameType>>();
	
	public static String getColumnsKey(String s) {
    	Md5PasswordEncoder encoder = new Md5PasswordEncoder();
    	return encoder.encode(s);
    }
		
	public static List<NameType> getColumns(String key) {
		return columnsCache.get(key);
	}
	
	public static void setColumns(String key, List<NameType> columns) {
		columnsCache.clear();
		columnsCache.put(key, columns);
	}


}
