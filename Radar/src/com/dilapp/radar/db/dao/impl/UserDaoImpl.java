package com.dilapp.radar.db.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.UserDao;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.domain.server.User;

public class UserDaoImpl implements UserDao {
	private static final String TAG = "UserDaoImpl";
	// /**
	// * Database Name
	// */
	// private static final String DATABASE_NAME = "user_database";
	// /**
	// * Database Version
	// */
	// private static final int DATABASE_VERSION = 1;
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "user";
	/**
	 * Table columns
	 */
	private static final String KEY_ID = "_id";
	private static final String KEY_USERID = "userId";
	private static final String KEY_LEVEL = "level";
	private static final String KEY_POINT = "point";
	private static final String KEY_CREATETOPIC = "createTopic";
	private static final String KEY_TOKEN = "token";
	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_USER_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_USERID + " text , " + KEY_LEVEL + " text," + KEY_POINT
			+ " text," + KEY_CREATETOPIC + " text," + KEY_TOKEN + " text);";
	/**
	 * Context
	 */
	private final Context mCtx = null;
	private SQLiteOpenHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Inner private class. Database Helper class for creating and updating
	 * database.
	 */
	// private static class DatabaseHelper extends SQLiteOpenHelper {
	// DatabaseHelper(Context context) {
	// super(context, DATABASE_NAME, null, DATABASE_VERSION);
	// }
	//
	// /**
	// * onCreate method is called for the 1st time when database doesn't
	// * exists.
	// */
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// Log.i(TAG, "Creating DataBase: " + CREATE_USER_TABLE);
	// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
	// db.execSQL(CREATE_USER_TABLE);
	// }
	//
	// /**
	// * onUpgrade method is called when database version changes.
	// */
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// if (oldVersion < newVersion) {
	// onCreate(db);
	// }
	// Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
	// newVersion);
	// }
	// }

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx
	 *            the Context within which to work
	 */
	public UserDaoImpl(Context ctx) {
		// this.mCtx = ctx;
		SQLiteOpenHelper mDbHelper = DBHelper.getInstance(ctx);
		mDb = mDbHelper.getWritableDatabase();
	}

	/**
	 * This method is used for creating/opening connection
	 * 
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	// public synchronized UserDaoImpl open() throws SQLException {
	// // mDbHelper = new DatabaseHelper(mCtx);
	// mDbHelper = DBHelper.getInstance(mCtx);
	// mDb = mDbHelper.getWritableDatabase();
	// mDb.beginTransaction();
	// return this;
	// }

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
	public synchronized void saveUserInfo(String content) {
		try {
			if (!mDb.isOpen()) {
				mDb = mDbHelper.getWritableDatabase();
			}
			dbBegin();
			mDb.execSQL("DELETE FROM USER;");
			mDb.execSQL("INSERT INTO USER ('token') VALUES ('" + content + "');");
			// ContentValues values = new ContentValues();
			// values.put("", value);
			// mDb.insert(DATABASE_TABLE, null, values);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
	}

	/**
	 * 
	 * Delete one
	 * 
	 * @param rowId
	 * @return boolean
	 */
	@Override
	public synchronized boolean deleteUserId(long rowId) {
		boolean deleId = mDb.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
		dbSuccess();
		return deleId;
	}

	/**
	 * This method will delete User record.
	 * 
	 * @return return boolean
	 */
	@Override
	public synchronized boolean deleteUserAll() {
		boolean deleAll = mDb.delete(DATABASE_TABLE, null, null) > 0;
		dbSuccess();
		return deleAll;
	}

	@Override
	public synchronized boolean updateUser(User bean) {
		ContentValues args = new ContentValues();
		args.put(KEY_USERID, bean.getUserId());
		args.put(KEY_LEVEL, bean.getLevel());
		args.put(KEY_POINT, bean.getPoint());
		args.put(KEY_CREATETOPIC, bean.getCreateTopic());
		args.put(KEY_TOKEN, bean.getToken());
		return mDb.update(DATABASE_TABLE, args, KEY_TOKEN + "=" + bean.getToken(), null) > 0;
	}

	@Override
	public synchronized Cursor getFirst() {
		Cursor c = null;
		try {
			if (!mDb.isOpen()) {
				mDb = mDbHelper.getWritableDatabase();
			}
			dbBegin();
			c = mDb.rawQuery("SELECT * FROM USER  LIMIT 0,1;", null);
			dbSuccess();
		} catch (Exception e) {
		} finally {
			// dbClose();
		}
		return c;
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
