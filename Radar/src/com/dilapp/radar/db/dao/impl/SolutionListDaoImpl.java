package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.SolutionListDao;


public class SolutionListDaoImpl implements SolutionListDao {
	private static final String TAG = "SolutionListDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "solution_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SOLUTIONTYPE = "solutionType";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_SOLUTIONITEMBEAN = "solutionItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_SOLUTION_LISTS_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_SOLUTIONTYPE + " integer , " + KEY_UPDATETIME + " integer , " + KEY_SOLUTIONITEMBEAN + " text );";

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
	public SolutionListDaoImpl(Context ctx) {
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
	
	//存储某种类型的护肤方案列表
	@Override
	public synchronized long saveSolutionListByType(int solutionType, long updateTime, String solutionBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SOLUTIONTYPE, solutionType);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_SOLUTIONITEMBEAN, solutionBean);

			ins = mDb.insert(DATABASE_TABLE, null, initialValues);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新某种类型的护肤方案列表
	@Override
	public synchronized long updateSolutionListByType(int solutionType, long updateTime, String solutionBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SOLUTIONTYPE, solutionType);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_SOLUTIONITEMBEAN, solutionBean);

			String[] args = {Integer.toString(solutionType)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_SOLUTIONTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//获取某种类型的护肤方案列表
	@Override
	public synchronized Cursor getSolutionListByType(int solutionType) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(solutionType)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SOLUTIONTYPE, KEY_UPDATETIME, KEY_SOLUTIONITEMBEAN}, 
					KEY_SOLUTIONTYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	//删除某种类型的护肤方案列表
	@Override
	public synchronized long deleteSolutionListByType(int solutionType) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(solutionType)};
			ins = mDb.delete(DATABASE_TABLE, KEY_SOLUTIONTYPE + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除所有护肤方案列表
	@Override
	public synchronized long deleteAllSolutionList() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_SOLUTIONTYPE + "!=?", args);
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