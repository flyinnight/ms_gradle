package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.BannerCollectionDao;


public class BannerCollectionDaoImpl implements BannerCollectionDao {
	private static final String TAG = "BannerCollectionDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "banner_collection_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_TYPE = "type";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_CONTENTBEAN = "contentBean";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_BANNER_COLLECTION_LIST_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_TYPE + " integer , "
			+ KEY_UPDATETIME + " integer , " + KEY_CONTENTBEAN + " text );";

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
	public BannerCollectionDaoImpl(Context ctx) {
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
	
	//存储一条banner或精选帖
	@Override
	public synchronized long saveBannerCollectionItem(int type, long updateTime, String contentBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TYPE, type);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_CONTENTBEAN, contentBean);

			ins = mDb.insert(DATABASE_TABLE, null, initialValues);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新一条banner或精选帖 Timestamp
	@Override
	public synchronized long updateBannerCollectionTime(int type, long updateTime) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TYPE, type);
			initialValues.put(KEY_UPDATETIME, updateTime);

			String[] args = {Integer.toString(type)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_TYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除一条banner或精选帖
	@Override
	public synchronized long deleteBannerCollectionItem(int type) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(type)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取一条banner或精选帖
	@Override
	public synchronized Cursor getBannerCollectionItem(int type) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(type)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_TYPE, KEY_UPDATETIME, KEY_CONTENTBEAN}, 
					KEY_TYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//删除所有banner或精选帖
	@Override
	public synchronized long deleteAllBannerCollectionLists() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_TYPE + "!=?", args);
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