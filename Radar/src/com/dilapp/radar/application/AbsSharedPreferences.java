package com.dilapp.radar.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AbsSharedPreferences {

	// private Context mContext;
	private Editor mEditor;
	private SharedPreferences mPref;

	public AbsSharedPreferences(Context context, String name, int mode) {
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		// this.mContext = context;
		this.mPref = sp;
		this.mEditor = mPref.edit();

	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int def) {
		return mPref.getInt(key, def);
	}

	public void setInt(String key, int val) {
		mEditor.putInt(key, val);
		mEditor.commit();
	}

	public long getLong(String key) {
		return getLong(key, 0l);
	}

	public long getLong(String key, long def) {
		return mPref.getLong(key, def);
	}

	public void setLong(String key, long val) {
		mEditor.putLong(key, val);
		mEditor.commit();
	}
	
	public float getFloat(String key) {
		return getFloat(key, 0f);
	}
	
	public float getFloat(String key, float def) {
		return mPref.getFloat(key, def);
	}
	
	public void setFloat(String key, float val) {
		mEditor.putFloat(key, val);
		mEditor.commit();
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String def) {
		return mPref.getString(key, def);
	}

	public void setString(String key, String val) {
		mEditor.putString(key, val);
		mEditor.commit();
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean def) {
		return mPref.getBoolean(key, def);
	}

	public void setBoolean(String key, boolean val) {
		mEditor.putBoolean(key, val);
		mEditor.commit();
	}

}