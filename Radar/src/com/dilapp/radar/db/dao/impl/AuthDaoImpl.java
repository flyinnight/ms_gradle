package com.dilapp.radar.db.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.authDao;
import com.dilapp.radar.domain.server.User;

public class AuthDaoImpl implements authDao {
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "auth";
	/**
	 * Table columns
	 */
	private static final String KEY_ID = "_id";
	private static final String KEY_ROLE = "role";
	private static final String KEY_ACTION = "action";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_USER_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_ROLE + " text , " + KEY_ACTION + " text);";
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
	public AuthDaoImpl(Context ctx) {
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

	/**
	 * his method is used to create/insert new record User record.
	 * 
	 * @param String
	 * @return long
	 */
	@Override
	public synchronized void saveAuth(String role, String action) {
		try {
			if (!mDb.isOpen()) {
				mDb = mDbHelper.getWritableDatabase();
			}
			dbBegin();
			ContentValues values = new ContentValues();
			values.put(KEY_ROLE, role);
			values.put(KEY_ACTION, action);
			mDb.insert(DATABASE_TABLE, null, values);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
	}

	/**
	 * This method will delete User record.
	 * 
	 * @return return boolean
	 */
	// @Override
	// public synchronized boolean deleteUserAll() {
	// boolean deleAll = mDb.delete(DATABASE_TABLE, null, null) > 0;
	// dbSuccess();
	// return deleAll;
	// }

	// @Override
	// public synchronized boolean updateUser(User bean) {
	// ContentValues args = new ContentValues();
	// args.put(KEY_USERID, bean.getUserId());
	// args.put(KEY_LEVEL, bean.getLevel());
	// args.put(KEY_POINT, bean.getPoint());
	// args.put(KEY_CREATETOPIC, bean.getCreateTopic());
	// args.put(KEY_TOKEN, bean.getToken());
	// return mDb.update(DATABASE_TABLE, args, KEY_TOKEN + "=" +
	// bean.getToken(), null) > 0;
	// }

	// @Override
	// public synchronized Cursor getFirst() {
	// Cursor c = null;
	// try {
	// if (!mDb.isOpen()) {
	// mDb = mDbHelper.getWritableDatabase();
	// }
	// dbBegin();
	// c = mDb.rawQuery("SELECT token,userId FROM USER  LIMIT 0,1;", null);
	// dbSuccess();
	// } catch (Exception e) {
	// } finally {
	// // dbClose();
	// }
	// return c;
	// }

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
