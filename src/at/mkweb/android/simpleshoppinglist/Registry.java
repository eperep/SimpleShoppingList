package at.mkweb.android.simpleshoppinglist;

import java.util.HashMap;

public class Registry {

	public static String DATABASE = "db";
	
	private static HashMap<String, Object> objects = new HashMap<String, Object>();
	
	public static boolean add(String key, Object o) {
		
		if(objects.containsKey(key) == false) {
			
			objects.put(key, o);
			return true;
		}
		
		return false;
	}
	
	public static Object get(String key) {
		
		if(objects.containsKey(key)) {
			
			return objects.get(key);
		}
		
		return null;
	}
	
}
