package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.PostListDaoImpl;
import com.dilapp.radar.domain.GetPostList.PostListSave;
import com.dilapp.radar.util.GsonUtil;


public class PostListHelper {

	// 存储某种类型的帖子列表
	public static long savePostList(String beanString) {
		PostListSave bean = GsonUtil.getGson().fromJson(beanString, PostListSave.class);
		long repId = 0;
		
		//先删除本地数据，避免重复存储，同一类型的list只保存一种
		PostListDaoImpl dbUtil = new PostListDaoImpl(RadarApplication.getInstance());
		dbUtil.deletePostListByType(bean.getType());
				
		try {
			PostListDaoImpl dbUtil1 = new PostListDaoImpl(RadarApplication.getInstance());
			repId = dbUtil1.savePostListByType(bean.getType(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新某种类型的帖子列表
	public static long updatePostList(String beanString) {
		PostListSave bean = GsonUtil.getGson().fromJson(beanString, PostListSave.class);
		PostListDaoImpl dbUtil = new PostListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostListByType(bean.getType(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 获取某种类型的帖子列表
	public static String getPostList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		PostListDaoImpl dbUtil = new PostListDaoImpl(RadarApplication.getInstance());

		PostListSave beanSave = null;

		Cursor curPost = dbUtil.getPostListByType(type);
		if ((curPost != null) && (curPost.moveToFirst())) {
			beanSave = analyzeBeanPost(type, curPost);
			curPost.close();
		}
		dbUtil.mDbclose();
		
		if (beanSave == null) {
			beanSave = new PostListSave();
		}

		return GsonUtil.getGson().toJson(beanSave);
	}

	// 删除某种类型的帖子列表
	public static long deletePostList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		PostListDaoImpl dbUtil = new PostListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deletePostListByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	public static PostListSave analyzeBeanPost(int type, Cursor cur) {
		int typeVerify = cur.getInt(cur.getColumnIndex("postType"));
		String postJson = cur.getString(cur.getColumnIndex("postItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		//判断类型是否一致
		if(type == typeVerify) {
			PostListSave bean = GsonUtil.getGson().fromJson(postJson, PostListSave.class);
			bean.setUpdateTime(updateTime);
			return bean;
		}
		
		return null;
	}
	
}
