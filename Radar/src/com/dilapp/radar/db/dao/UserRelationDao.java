package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface UserRelationDao {
	//存储一条用户关系数据
	public long saveUserRelationItem(String userId, int type, long updateTime, String contentBean);
	//更新一条用户关系数据 Timestamp
	public long updateUserRelationTime(String userId, int type, long updateTime);
	//删除一条用户关系数据
	public long deleteUserRelationItem(String userId, int type);
	//获取一条用户关系数据
	public Cursor getUserRelationItem(String userId, int type);
	//删除所有用户关系数据
	public long deleteAllUserRelationLists();
	
}
