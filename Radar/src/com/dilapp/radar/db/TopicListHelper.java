package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.TopicListDaoImpl;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.util.GsonUtil;


public class TopicListHelper {

	// 存储某种类型的话题列表
	public static long saveTopicList(String beanString) {
		TopicListSave bean = GsonUtil.getGson().fromJson(beanString, TopicListSave.class);
		long repId = 0;
		
		//先删除本地数据，避免重复存储，同一类型的list只保存一种
		TopicListDaoImpl dbUtil = new TopicListDaoImpl(RadarApplication.getInstance());
		dbUtil.deleteTopicListByType(bean.getType());
		
		try {
			TopicListDaoImpl dbUtil1 = new TopicListDaoImpl(RadarApplication.getInstance());
			repId = dbUtil1.saveTopicListByType(bean.getType(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新某种类型的话题列表
	public static long updateTopicList(String beanString) {
		TopicListSave bean = GsonUtil.getGson().fromJson(beanString, TopicListSave.class);
		TopicListDaoImpl dbUtil = new TopicListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updateTopicListByType(bean.getType(), bean.getUpdateTime(),beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 获取某种类型的话题列表
	public static String getTopicList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		TopicListDaoImpl dbUtil = new TopicListDaoImpl(RadarApplication.getInstance());
		TopicListSave beanSave = null;

		Cursor curTopic = dbUtil.getTopicListByType(type);
		if ((curTopic != null) && (curTopic.moveToFirst())) {
			beanSave = analyzeBeanTopic(type, curTopic);
			curTopic.close();
		}
		dbUtil.mDbclose();
		
		if (beanSave == null) {
			beanSave = new TopicListSave();
		}

		return GsonUtil.getGson().toJson(beanSave);
	}

	// 删除某种类型的话题列表
	public static long deleteTopicList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		TopicListDaoImpl dbUtil = new TopicListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteTopicListByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	public static TopicListSave analyzeBeanTopic(int type, Cursor cur) {
		int typeVerify = cur.getInt(cur.getColumnIndex("topicType"));
		String topicJson = cur.getString(cur.getColumnIndex("topicItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		//判断类型是否一致
		if(type == typeVerify) {
			TopicListSave bean = GsonUtil.getGson().fromJson(topicJson, TopicListSave.class);
			bean.setUpdateTime(updateTime);
			return bean;
		}
		
		return null;
	}
	
}
