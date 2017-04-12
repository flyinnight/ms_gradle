package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface TopicDetailDao {
	//存储一条话题详情
	public long saveTopicDetailItem(long topicId, int type, long updateTime, String topicBean);
	//更新一条话题详情
	public long updateTopicDetailItem(long topicId, int type, long updateTime, String topicBean);
	//删除一条话题详情
	public long deleteTopicDetailItem(long topicId, int type);
	//获取一条话题详情
	public Cursor getTopicDetailItem(long topicId, int type);
	//获取所有话题详情列表
	public Cursor getAllTopicDetailLists(int type);
	//删除所有话题详情列表
	public long deleteAllTopicDetailLists();
	
}
