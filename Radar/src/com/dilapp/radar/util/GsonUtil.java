/*********************************************************************/
/*  文件名  HttpConstant.java    　                                 	 */
/*  程序名  GsonUtil                     						     	 */
/*  版本履历   2015/5/7  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
	private static Gson gson;

	private GsonUtil() {

	}

	/**
	 * This is a gosn bug. This method has solved the bug. in different machines
	 * will produce different date formats. For example, IOS and Android.
	 * 
	 * @return gosn
	 */
	public static Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z")
					.create();
		}
		return gson;
	}

	/**
	 * 对象转换成json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	/**
	 * json字符串转成对象
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param type
	 *            需要转换的对象类型
	 * @return 对象
	 */
	public static <T> T fromJson(String str, Class<T> type) {
		Gson gson = new Gson();
		return gson.fromJson(str, type);
	}

}
