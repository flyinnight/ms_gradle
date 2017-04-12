package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.PostListDao;


public class PostListDaoImpl implements PostListDao {
	private static final String TAG = "PostListDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "post_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_POSTTYPE = "postType";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_POSTITEMBEAN = "postItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_POST_LISTS_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_POSTTYPE + " integer , " + KEY_UPDATETIME + " integer , " + KEY_POSTITEMBEAN + " text );";

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
	public PostListDaoImpl(Context ctx) {
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
	
	//存储某种类型的帖子列表
	@Override
	public synchronized long savePostListByType(int postType, long updateTime, String postBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_POSTTYPE, postType);
			initialValues.put(KEY_UPDATETIME, updateTime);
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
	
	//更新某种类型的帖子列表
	@Override
	public synchronized long updatePostListByType(int postType, long updateTime, String postBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_POSTTYPE, postType);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_POSTITEMBEAN, postBean);

			String[] args = {Integer.toString(postType)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_POSTTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取某种类型的帖子列表
	@Override
	public synchronized Cursor getPostListByType(int postType) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(postType)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_POSTTYPE, KEY_UPDATETIME, KEY_POSTITEMBEAN}, 
					KEY_POSTTYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//删除某种类型的帖子列表
	@Override
	public synchronized long deletePostListByType(int postType) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(postType)};
			ins = mDb.delete(DATABASE_TABLE, KEY_POSTTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除所有帖子列表
	@Override
	public synchronized long deleteAllPostList() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_POSTTYPE + "!=?", args);
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