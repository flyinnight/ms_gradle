package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.BannerCollectionDaoImpl;
import com.dilapp.radar.domain.Banner.BannerCollectionSave;
import com.dilapp.radar.util.GsonUtil;


public class BannerCollectionHelper {

	// 存储一条banner或精选帖
	public static long saveBannerCollectionItem(String beanString) {
		BannerCollectionSave bean = GsonUtil.getGson().fromJson(beanString, BannerCollectionSave.class);

		int type = 0;
		long updateTime = 0;
		long repId = 0;
		type = bean.getType();
		updateTime= bean.getUpdateTime();
		
		//如果已经存储的话，先删除本地数据，避免重复存储
		BannerCollectionDaoImpl dbUtil = new BannerCollectionDaoImpl(RadarApplication.getInstance());
		dbUtil.deleteBannerCollectionItem(type);

		try {
			BannerCollectionDaoImpl dbUtil2 = new BannerCollectionDaoImpl(RadarApplication.getInstance());
			repId = dbUtil2.saveBannerCollectionItem(type, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新一条banner或精选帖 Timestamp
	public static long updateBannerCollectionTime(String beanString) {
		BannerCollectionSave bean = GsonUtil.getGson().fromJson(beanString, BannerCollectionSave.class);
		BannerCollectionDaoImpl dbUtil = new BannerCollectionDaoImpl(RadarApplication.getInstance());
		
		int type = 0;
		long updateTime = 0;
		long repId = 0;
		
		type = bean.getType();
		updateTime= bean.getUpdateTime();

		try {
			repId = dbUtil.updateBannerCollectionTime(type, updateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 删除一条banner或精选帖
	public static long deleteBannerCollectionItem(String beanString) {
		Integer type = Integer.parseInt(beanString);
		BannerCollectionDaoImpl dbUtil = new BannerCollectionDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteBannerCollectionItem(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	// 获取一条banner或精选帖
	public static String getBannerCollectionItem(String beanString) {
		Integer type = Integer.parseInt(beanString);
		BannerCollectionDaoImpl dbUtil = new BannerCollectionDaoImpl(RadarApplication.getInstance());
		BannerCollectionSave Content = null;
		
		Cursor curContent = dbUtil.getBannerCollectionItem(type);
		if (curContent != null && (curContent.moveToFirst())) {
			Content = analyzeBannerConnection(type, curContent);
			curContent.close();
		}
		dbUtil.mDbclose();

		if (Content == null) {
			Content = new BannerCollectionSave();
		}
		
		return GsonUtil.getGson().toJson(Content);
	}
	
	public static BannerCollectionSave analyzeBannerConnection(int type, Cursor cur) {
		BannerCollectionSave resp = null;
		
		int type1 = cur.getInt(cur.getColumnIndex("type"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));
		String postItemJson = cur.getString(cur.getColumnIndex("contentBean"));

		if (type1 == type){
			resp = GsonUtil.getGson().fromJson(postItemJson, BannerCollectionSave.class);
			resp.setUpdateTime(updateTime);
			return resp;
		}
		
		return null;
	}

}
