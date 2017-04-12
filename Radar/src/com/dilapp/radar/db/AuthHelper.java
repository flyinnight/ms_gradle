package com.dilapp.radar.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.AuthDaoImpl;

public class AuthHelper {
	// 登录后所有权限映射入库
	public static void saveAuthForList(String stringList) {
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(stringList);
			AuthDaoImpl dbUtil = new AuthDaoImpl(RadarApplication.getInstance());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				dbUtil.saveAuth(jsonObj.getString("role"), jsonObj.getString("action"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
