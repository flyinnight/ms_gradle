package com.dilapp.radar.db;

import android.database.Cursor;
import android.util.Log;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.UserDaoImpl;
import com.dilapp.radar.domain.Login.LoginResp;
import com.dilapp.radar.domain.server.User;
import com.dilapp.radar.util.GsonUtil;

public class AccountHelper {

	public static void saveToken(String token) {
		UserDaoImpl dbUtil = new UserDaoImpl(RadarApplication.getInstance());
		dbUtil.saveUserInfo(token);
	}

	public static String getFirst() {
		UserDaoImpl dbUtil = new UserDaoImpl(RadarApplication.getInstance());
		User user = new User();
		try {
			Cursor cur = dbUtil.getFirst();
			if (cur.moveToFirst()) {
				user.setToken(cur.getString(cur.getColumnIndex("token")));
				user.setUserId(cur.getString(cur.getColumnIndex("userId")));
			}
			cur.close();
			dbUtil.close();
		} catch (Exception e) {
			e.printStackTrace();
			return GsonUtil.getGson().toJson(user).toString();
		}
		return GsonUtil.getGson().toJson(user).toString();
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 */
	public static boolean updateUser(String userString) {
		User user = GsonUtil.getGson().fromJson(userString, User.class);
		UserDaoImpl dbUtil = new UserDaoImpl(RadarApplication.getInstance());
		boolean ifSuc = dbUtil.updateUser(user);
		return ifSuc;
	}
}
