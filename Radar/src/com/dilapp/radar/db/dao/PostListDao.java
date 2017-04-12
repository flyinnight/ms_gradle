package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface PostListDao {
	//存储某种类型的帖子列表
	public long savePostListByType(int postType, long updateTime, String postBean);
	//更新某种类型的帖子列表
	public long updatePostListByType(int postType, long updateTime, String postBean);
	//获取某种类型的帖子列表
	public Cursor getPostListByType(int postType);
	//删除某种类型的帖子列表
	public long deletePostListByType(int postType);
	//删除所有帖子列表
	public long deleteAllPostList();
}
