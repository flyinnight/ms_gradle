package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.TopicListDao;


public class TopicListDaoImpl implements TopicListDao {
	private static final String TAG = "TopicListDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "topic_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_TOPICTYPE = "topicType";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_TOPICITEMBEAN = "topicItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_TOPIC_LISTS_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_TOPICTYPE + " integer , " + KEY_UPDATETIME + " integer , " + KEY_TOPICITEMBEAN + " text );";

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
	public TopicListDaoImpl(Context ctx) {
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
	
	//存储某种类型的话题列表
	@Override
	public synchronized long saveTopicListByType(int topicType, long updateTime, String topicBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TOPICTYPE, topicType);
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
	
	//更新某种类型的话题列表
	@Override
	public synchronized long updateTopicListByType(int topicType, long updateTime, String topicBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TOPICTYPE, topicType);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_TOPICITEMBEAN, topicBean);

			String[] args = {Integer.toString(topicType)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_TOPICTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取某种类型的话题列表
	@Override
	public synchronized Cursor getTopicListByType(int topicType) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(topicType)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_TOPICTYPE, KEY_UPDATETIME, KEY_TOPICITEMBEAN}, 
					 KEY_TOPICTYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	//删除某种类型的话题列表
	@Override
	public synchronized long deleteTopicListByType(int topicType) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(topicType)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TOPICTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}

	//删除所有话题列表
	@Override
	public synchronized long deleteAllTopicList() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TOPICTYPE + "!=?", args);
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