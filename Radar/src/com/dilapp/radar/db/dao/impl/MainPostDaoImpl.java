package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.MainPostDao;


public class MainPostDaoImpl implements MainPostDao {
	private static final String TAG = "MainPostDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "main_post_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SENDSTATE = "sendState";
	private static final String KEY_LOCALPOSTID = "localPostId";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_LISTUPDATETIME = "listUpdateTime";
	private static final String KEY_POSTITEMBEAN = "postItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_MAIN_POST_LISTS_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_SENDSTATE + " integer , " + KEY_LOCALPOSTID + " integer , " + KEY_UPDATETIME
			+ " integer , " + KEY_LISTUPDATETIME + " integer , " + KEY_POSTITEMBEAN + " text );";

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
	public MainPostDaoImpl(Context ctx) {
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
	
	//存储一条post
	@Override
	public synchronized long savePostItem(int sendState, long localPostId, long updateTime, long listUpdateTime, String postBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_LOCALPOSTID, localPostId);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_LISTUPDATETIME, listUpdateTime);
			initialValues.put(KEY_POSTITEMBEAN, postBean);

			ins = mDb.insert(DATABASE_TABLE, null, initialValues);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新贴子发送状态
	@Override
	public synchronized long updatePostStateItem(int sendState, long localPostId, long updateTime, String postBean) {
		//根据localPostId查找
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);

			String[] args = {Long.toString(localPostId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//图片发送成功，更新post内容
	@Override
	public synchronized long updatePostImgItem(int sendState, long localPostId, long updateTime, String postBean) {
		//根据localPostId查找
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_LOCALPOSTID, localPostId);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_POSTITEMBEAN, postBean);

			String[] args = {Long.toString(localPostId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新一条post
	@Override
	public synchronized long updatePostItem(int sendState, long localPostId, long updateTime, String postBean) {
		//根据localPostId查找，然后将localPostId置为0
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_LOCALPOSTID, 0);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_POSTITEMBEAN, postBean);

			String[] args = {Long.toString(localPostId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除一条post
	@Override
	public synchronized boolean deletePostItemById(long localPostId) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(localPostId)};
			ins = mDb.delete(DATABASE_TABLE, KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins > 0;
	}

	//删除所有本地待发贴子
	@Override
	public synchronized long deleteLocalPosts(long localPostId) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(localPostId)};
			ins = mDb.delete(DATABASE_TABLE, KEY_LOCALPOSTID + "!=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//第一次开机更新所有存储的本地posts状态
	@Override
	public synchronized long updateAllLOcalPostState(int sendState, long localPostId, long updateTime, String postBean) {
		//根据localPostId查找
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);

			String[] args = {Long.toString(0)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_LOCALPOSTID + "!=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取所有存储posts asc desc
	@Override
	public synchronized Cursor getAllMainPosts() {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SENDSTATE, KEY_LOCALPOSTID, KEY_LISTUPDATETIME, KEY_POSTITEMBEAN}, null,
					null, null, null, KEY_UPDATETIME + " desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
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