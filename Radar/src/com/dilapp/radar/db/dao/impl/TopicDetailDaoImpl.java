package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.TopicDetailDao;


public class TopicDetailDaoImpl implements TopicDetailDao {
	private static final String TAG = "TopicDetailDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "topic_detail_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_TOPICID = "topicId";
	private static final String KEY_TOPICTYPE = "topicType";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_TOPICITEMBEAN = "topicItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_TOPIC_DETAIL_LIST_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_TOPICID + " integer , " + KEY_TOPICTYPE + " integer , "
			+ KEY_UPDATETIME + " integer , " + KEY_TOPICITEMBEAN + " text );";

	/**
	 * Context
	 */
	private final Context mCtx = null;
	private SQLiteOpenHelper mDbHelper;
	private SQLiteDatabase mDb;


	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx
	 *            the Context within which to work
	 */
	public TopicDetailDaoImpl(Context ctx) {
		// this.mCtx = ctx;
		SQLiteOpenHelper mDbHelper = DBHelper.getInstance(ctx);
		mDb = mDbHelper.getWritableDatabase();
	}

	/**
	 * This method is used for closing the connection.
	 */
	public synchronized void close() {
		mDb.endTransaction();
		mDb.close();
		mDbHelper.close();
	}

	public synchronized void mDbclose() {
		mDb.close();
	}
	
	//存储一条话题详情
	@Override
	public synchronized long saveTopicDetailItem(long topicId, int type, long updateTime, String topicBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TOPICID, topicId);
			initialValues.put(KEY_TOPICTYPE, type);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_TOPICITEMBEAN, topicBean);

			ins = mDb.insert(DATABASE_TABLE, null, initialValues);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新一条话题详情
	@Override
	public synchronized long updateTopicDetailItem(long topicId, int type, long updateTime, String topicBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TOPICID, topicId);
			initialValues.put(KEY_TOPICTYPE, type);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_TOPICITEMBEAN, topicBean);

			String[] args = {Long.toString(topicId), Integer.toString(type)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_TOPICID + "=?" + " and " + KEY_TOPICTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除一条话题详情
	@Override
	public synchronized long deleteTopicDetailItem(long topicId, int type) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(topicId), Integer.toString(type)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TOPICID + "=?" + " and " + KEY_TOPICTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取一条话题详情
	@Override
	public synchronized Cursor getTopicDetailItem(long topicId, int type) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Long.toString(topicId), Integer.toString(type)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_TOPICID, KEY_TOPICTYPE, KEY_UPDATETIME, KEY_TOPICITEMBEAN}, 
					KEY_TOPICID + "=?" + " and " + KEY_TOPICTYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//获取所有话题详情内容/列表   asc desc
	@Override
	public synchronized Cursor getAllTopicDetailLists(int type) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(type)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_TOPICID, KEY_TOPICTYPE, KEY_UPDATETIME, KEY_TOPICITEMBEAN}, 
					KEY_TOPICTYPE + "=?", args, null, null,  KEY_UPDATETIME + " desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//删除所有话题详情内容和列表
	@Override
	public synchronized long deleteAllTopicDetailLists() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TOPICID + "!=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}


	private void dbSuccess() {
		mDb.setTransactionSuccessful();
	}

	private void dbClose() {
		mDb.endTransaction();
		mDb.close();
	}

	private void dbBegin() {
		mDb.beginTransaction();
	}

}