package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.TopicDetailDaoImpl;
import com.dilapp.radar.domain.GetPostList.TopicDetailGet;
import com.dilapp.radar.domain.GetPostList.TopicDetailSave;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.util.GsonUtil;


public class TopicDetailHelper {

	// 存储一条话题详情(本地只支持第一页)
	public static long saveTopicDetailItem(String beanString) {
		TopicDetailSave bean = GsonUtil.getGson().fromJson(beanString, TopicDetailSave.class);

		int type = 0;
		long topicId = 0;
		long updateTime = 0;
		long repId = 0;
		type = bean.getType();
		topicId = bean.getTopicId();
		updateTime= bean.getUpdateTime();
		
		//如果已经存储的话，先删除本地数据，避免重复存储
		TopicDetailDaoImpl dbUtil = new TopicDetailDaoImpl(RadarApplication.getInstance());
		dbUtil.deleteTopicDetailItem(topicId, type);
		
		long topicIdFirst = 0;
		int topicSize = 0;
		TopicDetailDaoImpl dbUtil1 = new TopicDetailDaoImpl(RadarApplication.getInstance());
		Cursor curDetail = dbUtil1.getAllTopicDetailLists(type);
		if ((curDetail != null) && (curDetail.moveToFirst())) {
			topicIdFirst = curDetail.getLong(curDetail.getColumnIndex("topicId"));
			topicSize++;
			while (curDetail.moveToNext()) {
				topicSize++;
			}
			
			curDetail.close();
		}
		dbUtil1.mDbclose();
		
		if (topicSize >= 100) {
			TopicDetailDaoImpl dbUtil2 = new TopicDetailDaoImpl(RadarApplication.getInstance());
			dbUtil2.deleteTopicDetailItem(topicIdFirst, type);
		}

		try {
			TopicDetailDaoImpl dbUtil3 = new TopicDetailDaoImpl(RadarApplication.getInstance());
			repId = dbUtil3.saveTopicDetailItem(topicId, type, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新一条话题详情
	public static long updateTopicDetailItem(String beanString) {
		TopicDetailSave bean = GsonUtil.getGson().fromJson(beanString, TopicDetailSave.class);
		TopicDetailDaoImpl dbUtil = new TopicDetailDaoImpl(RadarApplication.getInstance());
		
		int type = 0;
		long topicId = 0;
		long updateTime = 0;
		long repId = 0;
		
		type = bean.getType();
		topicId = bean.getTopicId();
		updateTime= bean.getUpdateTime();

		try {
			repId = dbUtil.updateTopicDetailItem(topicId, type, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 删除一条话题详情
	public static long deleteTopicDetailItem(String beanString) {
		TopicDetailGet topicGet = GsonUtil.getGson().fromJson(beanString, TopicDetailGet.class);
		TopicDetailDaoImpl dbUtil = new TopicDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteTopicDetailItem(topicGet.getTopicId(), topicGet.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}

	// 获取一条话题详情(本地只支持第一页)
	public static String getTopicDetailItem(String beanString) {
		TopicDetailDaoImpl dbUtil = new TopicDetailDaoImpl(RadarApplication.getInstance());
		TopicDetailGet topicGet = GsonUtil.getGson().fromJson(beanString, TopicDetailGet.class);
		TopicDetailSave topicDetail = null;
		
		Cursor curDetail = dbUtil.getTopicDetailItem(topicGet.getTopicId(), topicGet.getType());
		if (curDetail != null && (curDetail.moveToFirst())) {
			if (topicGet.getType() == TopicListCallBack.TOPIC_DETAIL_CONTENT) {
				topicDetail = analyzeBeanDetailContent(topicGet.getTopicId(), topicGet.getType(), curDetail);
			} else {
				topicDetail = analyzeBeanDetailList(topicGet.getTopicId(), topicGet.getType(), curDetail);
			}
			curDetail.close();
		}
		dbUtil.mDbclose();
		
		if (topicDetail == null) {
			topicDetail = new TopicDetailSave();
		}
		
		return GsonUtil.getGson().toJson(topicDetail);
	}
	
	public static TopicDetailSave analyzeBeanDetailContent(long Id, int type, Cursor cur) {
		TopicDetailSave resp = null;
		
		long topicId = cur.getLong(cur.getColumnIndex("topicId"));
		int topictype = cur.getInt(cur.getColumnIndex("topicType"));
		String postItemJson = cur.getString(cur.getColumnIndex("topicItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		if ((topicId == Id) && (topictype == type)){
			resp = GsonUtil.getGson().fromJson(postItemJson, TopicDetailSave.class);
			resp.setUpdateTime(updateTime);
			return resp;
		}
		
		return null;
	}
	
	public static TopicDetailSave analyzeBeanDetailList(long Id, int type, Cursor cur) {
		TopicDetailSave resp = null;
		
		long topicId = cur.getLong(cur.getColumnIndex("topicId"));
		int topictype = cur.getInt(cur.getColumnIndex("topicType"));
		String postItemJson = cur.getString(cur.getColumnIndex("topicItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		if ((topicId == Id) && (topictype == type)){
			resp = GsonUtil.getGson().fromJson(postItemJson, TopicDetailSave.class);
			resp.setUpdateTime(updateTime);
			return resp;
		}
		
		return null;
	}
}
