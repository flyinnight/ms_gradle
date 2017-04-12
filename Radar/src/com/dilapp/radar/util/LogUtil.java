package com.dilapp.radar.util;

import android.util.Log;

public class LogUtil {
	
	private static boolean flag = true;
	
	private static String  Tag = "hj";
	
	public static void showInfo(String msg){
		if(flag){
		  Log.i(Tag, msg);
		}
	}
	
	public static void showError(String msg){
	   if(flag){
		  Log.e(Tag, msg);
		}
	}
}
