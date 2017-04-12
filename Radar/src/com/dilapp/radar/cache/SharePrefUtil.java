package com.dilapp.radar.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 
 * @author hj
 * @time 2015-03-17
 * 
 */
public class SharePrefUtil {
	private static String tag = SharePrefUtil.class.getSimpleName();
	public final static String SP_NAME = "config";

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_MULTI_PROCESS);
		sp.edit().putBoolean(key, value).commit();
	}

	/**
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_MULTI_PROCESS);
		sp.edit().putString(key, value).commit();

	}

	public static void clear(Context context) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().clear().commit();
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveLong(Context context, String key, long value) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putLong(key, value).commit();
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveInt(Context context, String key, int value) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(key, value).commit();
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveFloat(Context context, String key, float value) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putFloat(key, value).commit();
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_MULTI_PROCESS);
		return sp.getString(key, defValue);
	}

	/**
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_MULTI_PROCESS);
		return sp.getInt(key, defValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String key, long defValue) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getLong(key, defValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static float getFloat(Context context, String key, float defValue) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getFloat(key, defValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		// if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_MULTI_PROCESS);
				
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 将对象进行base64编码后保存到SharePref中
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public static void saveObj(Context context, String key, Object object) {
         SharedPreferences	sp = context.getSharedPreferences(SP_NAME, 0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			// 将对象的转为base64码
			String objBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
			sp.edit().putString(key,objBase64).commit();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将SharePref中经过base64编码的对象读取出来
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static Object getObj(Context context, String key) {
//		if (sp == null)
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		String objBase64 = sp.getString(key, null);
		if (TextUtils.isEmpty(objBase64))
			return null;

		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decodeBase64(objBase64.getBytes());
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);

		ObjectInputStream ois;
		Object obj = null;
		try {
			ois = new ObjectInputStream(bais);
			obj = (Object) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void remove(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().remove(name).commit();
	}
}
