package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface PostDetailDao {
	//存储一条待发从贴/存储一条主贴详情
	public long savePostItem(int sendState, long postId, long localPostId, long updateTime, String postBean);
	//(图片发送成功)更新一条待发从贴内容/更新一条主贴详情
	public long updatePostItem(int sendState, long postId, long localPostId, long updateTime, String postBean);
	//从贴发送成功，更新从贴内容为网络数据
	public long updateSendSuccessPostItem(int sendState, long postId, long localPostId, long updateTime, String postBean);
	//更新一条待发从贴状态
	public long updateSendingPostStateItem(int sendState, long localPostId);
	//第一次开机更新所有待发从贴为初始状态
	public long restoreAllSendingPostsState(int sendState);
	//删除一条待发从贴/删除一条主贴详情
	public long deletePostItem(long postId, long localPostId);
	//删除所有待发从贴
	public long deleteAllSendingPosts();
	//获取所有待发从贴列表
	public Cursor getAllSendingPosts();
	//获取所有主贴详情列表
	public Cursor getPostDetailLists();
	//获取一条主贴详情
	public Cursor getPostDetailItem(long postId);
	//更新一条主贴详情的Timestamp
	public long updatePostDetailTimestamp(long postId, long updateTime);
}
