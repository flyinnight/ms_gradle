package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface MainPostDao {
	//存储一条post
	public long savePostItem(int sendState, long localPostId, long updateTime, long listUpdateTime, String postBean);
	//更新贴子发送状态
	public long updatePostStateItem(int sendState, long localPostId, long updateTime, String postBean);
	//图片发送成功，更新post内容
	public long updatePostImgItem(int sendState, long localPostId, long updateTime, String postBean);
	//更新一条post
	public long updatePostItem(int sendState, long localPostId, long updateTime, String postBean);
	//删除一条post
	public boolean deletePostItemById(long localPostId);
	//删除所有本地待发贴子
	public long deleteLocalPosts(long localPostId);
	//第一次开机更新所有存储的本地posts状态
	public long updateAllLOcalPostState(int sendState, long localPostId, long updateTime, String postBean);
	//获取所有存储posts
	public Cursor getAllMainPosts();
	
}
