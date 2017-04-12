package com.dilapp.radar.db.dao.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.PostDetailDao;


public class PostDetailDaoImpl implements PostDetailDao {
	private static final String TAG = "PostDetailDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "post_detail_lists";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SENDSTATE = "sendState";
	private static final String KEY_POSTID = "postId";
	private static final String KEY_LOCALPOSTID = "localPostId";
	private static final String KEY_UPDATETIME = "updateTime";
	private static final String KEY_POSTITEMBEAN = "postItemJson";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_POST_DETAIL_LISTS_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_SENDSTATE + " integer , " + KEY_POSTID + " integer , " + KEY_LOCALPOSTID + " integer , "
			+ KEY_UPDATETIME + " integer , " + KEY_POSTITEMBEAN + " text );";

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
	public PostDetailDaoImpl(Context ctx) {
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
	
	//存储一条待发从贴/存储一条主贴详情
	@Override
	public synchronized long savePostItem(int sendState, long postId, long localPostId, long updateTime, String postBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_POSTID, postId);
			initialValues.put(KEY_LOCALPOSTID, localPostId);
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
	
	//(图片发送成功)更新一条待发从贴内容/更新一条主贴详情
	@Override
	public synchronized long updatePostItem(int sendState, long postId, long localPostId, long updateTime, String postBean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_POSTID, postId);
			initialValues.put(KEY_LOCALPOSTID, localPostId);
			initialValues.put(KEY_UPDATETIME, updateTime);
			initialValues.put(KEY_POSTITEMBEAN, postBean);

			String[] args = {Long.toString(postId), Long.toString(localPostId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_POSTID + "=?" + " and " + KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//从贴发送成功，更新从贴内容为网络数据
	@Override
	public synchronized long updateSendSuccessPostItem(int sendState, long postId, long localPostId, long updateTime, String postBean) {
		//根据localPostId查找，然后将localPostId置为0
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_SENDSTATE, sendState);
			initialValues.put(KEY_POSTID, postId);
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
	
	//更新一条待发从贴状态
	@Override
	public synchronized long updateSendingPostStateItem(int sendState, long localPostId) {
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
	
	//第一次开机更新所有待发从贴为初始状态
	@Override
	public synchronized long restoreAllSendingPostsState(int sendState) {
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
	
	//删除一条待发从贴/删除一条主贴详情
	@Override
	public synchronized long deletePostItem(long postId, long localPostId) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(postId), Long.toString(localPostId)};
			ins = mDb.delete(DATABASE_TABLE, KEY_POSTID + "=?" + " and " + KEY_LOCALPOSTID + "=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	//删除所有待发从贴
	@Override
	public synchronized long deleteAllSendingPosts() {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			String[] args = {Long.toString(0)};
			ins = mDb.delete(DATABASE_TABLE, KEY_LOCALPOSTID + "!=?", args);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}

	//获取所有待发从贴列表   asc desc
	@Override
	public synchronized Cursor getAllSendingPosts() {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Long.toString(0)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SENDSTATE, KEY_POSTID, KEY_LOCALPOSTID, KEY_POSTITEMBEAN}, 
					KEY_LOCALPOSTID + "!=?", args, null, null, KEY_UPDATETIME + " desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	//获取所有主贴详情列表   asc desc
	@Override
	public synchronized Cursor getPostDetailLists() {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Long.toString(0)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SENDSTATE, KEY_POSTID, KEY_LOCALPOSTID, KEY_POSTITEMBEAN}, 
					KEY_LOCALPOSTID + "=?", args, null, null, KEY_UPDATETIME + " desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}
	
	//获取一条主贴详情
	@Override
	public synchronized Cursor getPostDetailItem(long postId) {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			String[] args = {Long.toString(postId)};
			cur = mDb.query(DATABASE_TABLE, new String[] {KEY_SENDSTATE, KEY_POSTID, KEY_LOCALPOSTID, KEY_UPDATETIME, KEY_POSTITEMBEAN}, 
					KEY_POSTID + "=?", args, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	//更新一条主贴详情的Timestamp
	@Override
	public synchronized long updatePostDetailTimestamp(long postId, long updateTime) {
		//根据postId查找
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_POSTID, postId);
			initialValues.put(KEY_UPDATETIME, updateTime);

			String[] args = {Long.toString(postId)};
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_POSTID + "=?", args);
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