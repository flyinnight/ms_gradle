package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface TopicListDao {
	//存储某种类型的话题列表
	public long saveTopicListByType(int topicType, long updateTime, String topicBean);
	//更新某种类型的话题列表
	public long updateTopicListByType(int topicType, long updateTime, String topicBean);
	//获取某种类型的话题列表
	public Cursor getTopicListByType(int topicType);
	//删除某种类型的话题列表
	public long deleteTopicListByType(int topicType);
	//删除所有话题列表
	public long deleteAllTopicList();
	
}
