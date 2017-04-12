package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.SolutionDataDao;


public class SolutionDataDaoImpl implements SolutionDataDao {
	private static final String TAG = "SolutionDataDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "solution_data";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SOLUTIONTYPE = "solutionType";
	private static final String KEY_SOLUTIONID = "solutionId";
	private static final String KEY_LOCALSOLUTIONID = "localSolutionId";
	private static final String KEY_SENDSTATE = "sendState";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_SOLUTIONTAG = "solutionTag";
	private static final String KEY_SOLUTIONITEMBEAN = "solutionItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_SOLUTION_DATA_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, " 
			+ KEY_SOLUTIONTYPE + " integer , " + KEY_SOLUTIONID + " integer , " + KEY_LOCALSOLUTIONID + " integer , " 
			+ KEY_SENDSTATE + " integer , " + KEY_UPDATETIME + " integer , " + KEY_SOLUTIONTAG + " text , " + KEY_SOLUTIONITEMBEAN + " text );";

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
	public SolutionDataDaoImpl(Context ctx) {
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
	
	//存储某种类型的护肤方案数据
	@Override
	public synchronized long saveSolutionDataByType(int solutionType, long solutionId, long localSolutionId, int sendState, long updateTime, String tag, String solutionBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SOLUTIONTYPE, solutionType);
			initialValues.put(KEY_SOLUTIONID, solutionId);
			initialValues.put(KEY_LOCALSOLUTIONID, localSolutionId);
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_SOLUTIONTAG, tag);
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
	
	//更新某条护肤方案发送状态
	@Override
	public synchronized long updateSolutionStateItemByType(int solutionType, long localSolutionId, int sendState){
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);

			String[] args = {Integer.toString(solutionType), Long.toString(localSolutionId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_SOLUTIONTYPE + "=?" + " and " + KEY_LOCALSOLUTIONID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新某种类型的护肤方案发送状态
	@Override
	public synchronized long updateSolutionStateByType(int solutionType, int sendState){
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);

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
	
	//(图片发送成功)更新某条护肤方案内容
	@Override
	public synchronized long updateSolutionDataItemByType(int solutionType, long localSolutionId, String solutionBean){
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SOLUTIONITEMBEAN, solutionBean);

			String[] args = {Integer.toString(solutionType), Long.toString(localSolutionId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_SOLUTIONTYPE + "=?" + " and " + KEY_LOCALSOLUTIONID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//更新某种类型的护肤方案的Timestamp
	@Override
	public synchronized long updateSolutionTimestampByType(int solutionType, long updateTime){
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_UPDATETIME, updateTime);

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
	
	//获取某条护肤方案数据
	@Override
	public synchronized Cursor getSolutionDataItemByType(int solutionType, long solutionId, long localSolutionId, String tag) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(solutionType), Long.toString(solutionId), Long.toString(localSolutionId), tag};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SOLUTIONTYPE, KEY_SOLUTIONID, KEY_LOCALSOLUTIONID, KEY_SENDSTATE, KEY_UPDATETIME, KEY_SOLUTIONTAG, KEY_SOLUTIONITEMBEAN}, 
					KEY_SOLUTIONTYPE + "=?" + " and " + KEY_SOLUTIONID + "=?" + " and " + KEY_LOCALSOLUTIONID + "=?" + " and " + KEY_SOLUTIONTAG + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//获取某种类型的护肤方案数据
	@Override
	public synchronized Cursor getSolutionDataByType(int solutionType) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Integer.toString(solutionType)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SOLUTIONTYPE, KEY_SOLUTIONID, KEY_LOCALSOLUTIONID, KEY_SENDSTATE, KEY_UPDATETIME, KEY_SOLUTIONITEMBEAN}, 
					KEY_SOLUTIONTYPE + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	//删除某条护肤方案数据
	@Override
	public synchronized long deleteSolutionDataItemByType(int solutionType, long solutionId, long localSolutionId, String tag) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Integer.toString(solutionType), Long.toString(solutionId), Long.toString(localSolutionId), tag};
			ins = mDb.delete(DATABASE_TABLE, KEY_SOLUTIONTYPE + "=?" + " and " + KEY_SOLUTIONID + "=?" + " and " + KEY_LOCALSOLUTIONID + "=?" + " and " + KEY_SOLUTIONTAG + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除某种类型的护肤方案数据
	@Override
	public synchronized long deleteSolutionDataByType(int solutionType) {
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
	
	//删除所有护肤方案数据
	@Override
	public synchronized long deleteAllSolutionData() {
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