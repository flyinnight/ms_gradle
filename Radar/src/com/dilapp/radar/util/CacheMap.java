package com.dilapp.radar.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheMap {

	
	private static ArrayList<HashMap<Integer, Object>> list = new ArrayList<HashMap<Integer, Object>>();
	

	public static void putDate(ArrayList<HashMap<Integer, Object>> values) {

		list = values;
	}

	public static ArrayList<HashMap<Integer, Object>> getDate() {
		
		return list;
	}
	
}
