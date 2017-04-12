package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface BannerCollectionDao {
	//存储一条banner或精选帖
	public long saveBannerCollectionItem(int type, long updateTime, String contentBean);
	//更新一条banner或精选帖 Timestamp
	public long updateBannerCollectionTime(int type, long updateTime);
	//删除一条banner或精选帖
	public long deleteBannerCollectionItem(int type);
	//获取一条banner或精选帖
	public Cursor getBannerCollectionItem(int type);
	//删除所有banner或精选帖
	public long deleteAllBannerCollectionLists();
	
}
