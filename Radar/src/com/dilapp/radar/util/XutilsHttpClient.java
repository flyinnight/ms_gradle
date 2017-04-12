/*********************************************************************/
/*  文件名  XutilsHttpClient.java    　                                 */
/*  程序名  获取HttpUtils                     						     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.util;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;

class XutilsHttpClient {

	private static HttpUtils client;

	/**
	 * @param context
	 * 
	 * @return HttpUtils对象实例
	 */
	public synchronized static HttpUtils getInstence(Context context) {
		if (client == null) {
			// time out
			client = new HttpUtils(1000 * 20);
			client.configSoTimeout(1000 * 20);
			client.configResponseTextCharset("UTF-8");
			// save server(Session)Cookie
			PreferencesCookieStore cookieStore = new PreferencesCookieStore(context);
			// clean cookie
			cookieStore.clear();
			client.configCookieStore(cookieStore);
		}
		return client;
	}

}