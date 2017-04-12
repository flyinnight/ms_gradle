package com.dilapp.radar.application;

import android.content.Context;

public class AppSharedPreferences extends AbsSharedPreferences {

	private final static String SP_NAME = "app";// SharedPreferences

	public AppSharedPreferences(Context context) {
		super(context, SP_NAME, 0);
	}

	/**
	 * UserID
	 *
	 * @return
	 */
	public String getUserID() {
		return getString("app:user_id");
	}

	/**
	 * UserID
	 *
	 * @param userid
	 */
	public void setUserID(String userid) {
		setString("app:user_id", userid);
	}

	/**
	 * UserName
	 *
	 * @return
	 */
	public String getUserName() {
		return getString("app:uesrname");
	}

	/**
	 * UserName
	 *
	 * @param username
	 */
	public void setUserName(String username) {
		setString("app:uesrname", username);
	}

	/**
	 * Password
	 *
	 * @return Password
	 */
	public String getPassword() {
		return getString("app:password");
	}

	/**
	 * Password
	 *
	 * @param password
	 */
	public void setPassword(String password) {
		setString("app:password", password);
	}

	/**
	 * UserIconUrl
	 *
	 * @return UserIconUrl
	 */
	public String getUserIconUrl() {
		return getString("app:user_icon_url");
	}

	/**
	 * UserIconUrl
	 *
	 * @param userIconUrl
	 */
	public void setUserIconUrl(String userIconUrl) {
		setString("app:user_icon_url", userIconUrl);
	}

}
