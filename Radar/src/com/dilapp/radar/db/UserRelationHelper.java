package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.UserRelationDaoImpl;
import com.dilapp.radar.domain.GetUserRelation.UserRelationGetLocal;
import com.dilapp.radar.domain.GetUserRelation.UserRelationSave;
import com.dilapp.radar.util.GsonUtil;


public class UserRelationHelper {

	// 存储一条用户关系列表
	public static long saveUserRelationItem(String beanString) {
		UserRelationSave bean = GsonUtil.getGson().fromJson(beanString, UserRelationSave.class);

		int type = 0;
		String userId = null;
		long updateTime = 0;
		long repId = 0;
		type = bean.getType();
		userId = bean.getUserId();
		updateTime= bean.getUpdateTime();
		
		//如果已经存储的话，先删除本地数据，避免重复存储
		UserRelationDaoImpl dbUtil = new UserRelationDaoImpl(RadarApplication.getInstance());
		dbUtil.deleteUserRelationItem(userId, type);
		
		try {
			UserRelationDaoImpl dbUtil1 = new UserRelationDaoImpl(RadarApplication.getInstance());
			repId = dbUtil1.saveUserRelationItem(userId, type, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新一条用户关系 time
	public static long updateUserRelationTime(String beanString) {
		UserRelationSave bean = GsonUtil.getGson().fromJson(beanString, UserRelationSave.class);
		UserRelationDaoImpl dbUtil = new UserRelationDaoImpl(RadarApplication.getInstance());
		
		int type = 0;
		String userId = null;
		long updateTime = 0;
		long repId = 0;
		type = bean.getType();
		userId = bean.getUserId();
		updateTime= bean.getUpdateTime();

		try {
			repId = dbUtil.updateUserRelationTime(userId, type, updateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 删除一条用户关系列表
	public static long deleteUserRelationItem(String beanString) {
		UserRelationGetLocal bean = GsonUtil.getGson().fromJson(beanString, UserRelationGetLocal.class);
		UserRelationDaoImpl dbUtil = new UserRelationDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		int type = 0;
		String userId = null;
		type = bean.getType();
		userId = bean.getUserId();
		
		try {
			repId = dbUtil.deleteUserRelationItem(userId, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	// 获取一条用户关系列表
	public static String getUserRelationItem(String beanString) {
		UserRelationDaoImpl dbUtil = new UserRelationDaoImpl(RadarApplication.getInstance());
		UserRelationGetLocal bean = GsonUtil.getGson().fromJson(beanString, UserRelationGetLocal.class);
		
		int type = 0;
		String userId = null;
		type = bean.getType();
		userId = bean.getUserId();
		
		UserRelationSave detailContent = null;
		
		Cursor curDetail = dbUtil.getUserRelationItem(userId, type);
		if (curDetail != null && (curDetail.moveToFirst())) {
			detailContent = analyzeContent(userId, type, curDetail);
			curDetail.close();
		}
		dbUtil.mDbclose();

		if (detailContent == null) {
			detailContent = new UserRelationSave();
		}

		return GsonUtil.getGson().toJson(detailContent);
	}
	
	public static UserRelationSave analyzeContent(String Id, int type, Cursor cur) {
		UserRelationSave resp = null;
		
		String userId = cur.getString(cur.getColumnIndex("userId"));
		int type1 = cur.getInt(cur.getColumnIndex("type"));
		String itemJson = cur.getString(cur.getColumnIndex("contentBean"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		if ((userId.equals(Id)) && (type1 == type)){
			resp = GsonUtil.getGson().fromJson(itemJson, UserRelationSave.class);
			resp.setUpdateTime(updateTime);
			return resp;
		}
		
		return null;
	}

}
