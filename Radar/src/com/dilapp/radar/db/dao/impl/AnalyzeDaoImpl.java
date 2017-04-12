package com.dilapp.radar.db.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dilapp.radar.db.DBHelper;
import com.dilapp.radar.db.dao.AnalyzeDao;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;

public class AnalyzeDaoImpl implements AnalyzeDao {
	private static final String TAG = "AnalyzeDaoImpl";
	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "facial_analyze";
	/**
	 * Table columns
	 */
	private static final String KEY_ROWID = "_id";
	private static final String KEY_RECORDID = "recordId";
	private static final String KEY_USERID = "userId";
	private static final String KEY_TYPE = "type";
	private static final String KEY_SUBTYPE = "subtype";
	private static final String KEY_ANALYZEPART = "analyzePart";
	private static final String KEY_ANALYZETIME = "analyzeTime";
	private static final String KEY_ANALYZEPLACE = "analyzePlace";
	private static final String KEY_ANALYZECLIMATE = "analyzeClimate";

	private static final String KEY_PARAM1VALUE = "param1Value";
	private static final String KEY_PARAM1STANDARD = "param1Standard";
	private static final String KEY_PARAM1RESULT = "param1Result";
	private static final String KEY_PARAM2VALUE = "param2Value";
	private static final String KEY_PARAM2STANDARD = "param2Standard";
	private static final String KEY_PARAM2RESULT = "param2Result";
	private static final String KEY_PARAM3VALUE = "param3Value";
	private static final String KEY_PARAM3STANDARD = "param3Standard";
	private static final String KEY_PARAM3RESULT = "param3Result";
	private static final String KEY_PARAM4VALUE = "param4Value";
	private static final String KEY_PARAM4STANDARD = "param4Standard";
	private static final String KEY_PARAM4RESULT = "param4Result";
	private static final String KEY_PARAM5VALUE = "param5Value";
	private static final String KEY_PARAM5STANDARD = "param5Standard";
	private static final String KEY_PARAM5RESULT = "param5Result";
	private static final String KEY_PARAM6VALUE = "param6Value";
	private static final String KEY_PARAM6STANDARD = "param6Standard";
	private static final String KEY_PARAM6RESULT = "param6Result";
	private static final String KEY_PARAM7VALUE = "param7Value";
	private static final String KEY_PARAM7STANDARD = "param7Standard";
	private static final String KEY_PARAM7RESULT = "param7Result";

	private static final String KEY_COSMETIC_ID = "cosmetic_id";
	private static final String KEY_SCHEMA_ID = "schema_id";
	private static final String KEY_LABEL_ID = "label_id";
	/**
	 * 是否已经上传服务器 OK:1 NG:0
	 */
	private static final String KEY_IFCLOUD = "ifCloud";

	/**
	 * Database creation sql statement
	 */
	public static final String CREATE_FACIAL_ANALYZE_TABLE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_RECORDID + " text , " + KEY_USERID + " text , " + KEY_TYPE + " text , " + KEY_SUBTYPE
			+ " text , " + KEY_ANALYZEPART + " text , " + KEY_ANALYZETIME + " text , " + KEY_ANALYZEPLACE + " text , "
			+ KEY_ANALYZECLIMATE + " text, " + KEY_PARAM1VALUE + " text , " + KEY_PARAM1STANDARD + " text , "
			+ KEY_PARAM1RESULT + " text , " + KEY_PARAM2VALUE + " text , " + KEY_PARAM2STANDARD + " text , "
			+ KEY_PARAM2RESULT + " text , " + KEY_PARAM3VALUE + " text , " + KEY_PARAM3STANDARD + " text , "
			+ KEY_PARAM3RESULT + " text , " + KEY_PARAM4VALUE + " text , " + KEY_PARAM4STANDARD + " text , "
			+ KEY_PARAM4RESULT + " text , " + KEY_PARAM5VALUE + " text , " + KEY_PARAM5STANDARD + " text , "
			+ KEY_PARAM5RESULT + " text , " + KEY_PARAM6VALUE + " text , " + KEY_PARAM6STANDARD + " text , "
			+ KEY_PARAM6RESULT + " text , " + KEY_PARAM7VALUE + " text , " + KEY_PARAM7STANDARD + " text , "
			+ KEY_PARAM7RESULT + " text , " + KEY_COSMETIC_ID + " text , " + KEY_SCHEMA_ID + " text , "
			+ KEY_LABEL_ID + " text , " + KEY_IFCLOUD + " text );";

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
	// Log.i(TAG, "Creating DataBase: " + CREATE_FACIAL_ANALYZE_TABLE);
	// // db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
	// db.execSQL(CREATE_FACIAL_ANALYZE_TABLE);
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
	public AnalyzeDaoImpl(Context ctx) {
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
	// public synchronized AnalyzeDaoImpl open() throws SQLException {
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

	public synchronized void mDbclose() {
		mDb.close();
	}
	
	/**
	 * This method is used to create/insert new record Analyze record.
	 * 
	 * @param FacialAnalyzeBean
	 * @return long
	 */
	@Override
	public synchronized long saveAnalyze(FacialAnalyzeBean bean, int state) {
		long ins = 0;
		// synchronized (mDbHelper) {
		// }
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			
			initialValues.put(KEY_RECORDID, bean.getRid());
			initialValues.put(KEY_USERID, bean.getUid());
			initialValues.put(KEY_TYPE, bean.getType());
			initialValues.put(KEY_SUBTYPE, bean.getSubtype());
			initialValues.put(KEY_ANALYZEPART, bean.getAnalyzePart());
			initialValues.put(KEY_ANALYZETIME, bean.getAnalyzeTime());
			initialValues.put(KEY_ANALYZEPLACE, bean.getAnalyzePlace());
			initialValues.put(KEY_ANALYZECLIMATE, bean.getAnalyzeClimate());
			initialValues.put(KEY_PARAM1VALUE, bean.getParam1Value());
			initialValues.put(KEY_PARAM1RESULT, bean.getParam1Result());
			initialValues.put(KEY_PARAM1STANDARD, bean.getParam1Standard());
			initialValues.put(KEY_PARAM2VALUE, bean.getParam2Value());
			initialValues.put(KEY_PARAM2RESULT, bean.getParam2Result());
			initialValues.put(KEY_PARAM2STANDARD, bean.getParam2Standard());
			initialValues.put(KEY_PARAM3VALUE, bean.getParam3Value());
			initialValues.put(KEY_PARAM3RESULT, bean.getParam3Result());
			initialValues.put(KEY_PARAM3STANDARD, bean.getParam3Standard());
			initialValues.put(KEY_PARAM4VALUE, bean.getParam4Value());
			initialValues.put(KEY_PARAM4RESULT, bean.getParam4Result());
			initialValues.put(KEY_PARAM4STANDARD, bean.getParam4Standard());
			initialValues.put(KEY_PARAM5VALUE, bean.getParam5Value());
			initialValues.put(KEY_PARAM5RESULT, bean.getParam5Result());
			initialValues.put(KEY_PARAM5STANDARD, bean.getParam5Standard());
			initialValues.put(KEY_PARAM6VALUE, bean.getParam6Value());
			initialValues.put(KEY_PARAM6RESULT, bean.getParam6Result());
			initialValues.put(KEY_PARAM6STANDARD, bean.getParam6Standard());
			initialValues.put(KEY_PARAM7VALUE, bean.getParam7Value());
			initialValues.put(KEY_PARAM7RESULT, bean.getParam7Result());
			initialValues.put(KEY_PARAM7STANDARD, bean.getParam7Standard());
			initialValues.put(KEY_COSMETIC_ID, bean.getCosmeticID());
			initialValues.put(KEY_SCHEMA_ID, bean.getSchemaID());
			initialValues.put(KEY_LABEL_ID, bean.getLabelID());
			initialValues.put(KEY_IFCLOUD, state);
			ins = mDb.insert(DATABASE_TABLE, null, initialValues);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}


	@Override
	public synchronized long updateRecordState(String recordId, int state) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_IFCLOUD, state);
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_RECORDID + "=" + recordId, null);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins;
	}
	
	/**
	 * This method will delete Analyze record.
	 * 
	 * @param rowId
	 * @return boolean
	 */
	@Override
	public synchronized boolean deleteAnalyzeById(String recordId) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ins = mDb.delete(DATABASE_TABLE, KEY_RECORDID + "=" + recordId, null);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		return ins > 0;
	}

	/**
	 * This method will return Cursor holding all the Analyze records.
	 * 
	 * @return Cursor
	 */
	@Override
	public synchronized Cursor fetchAllAnalyzeData() {
		Cursor cur = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			cur = mDb.query(DATABASE_TABLE, new String[] { KEY_RECORDID, KEY_TYPE, KEY_ANALYZEPART, KEY_PARAM1RESULT, KEY_PARAM2RESULT, KEY_PARAM3RESULT, KEY_PARAM4RESULT, KEY_PARAM5RESULT, KEY_PARAM6RESULT, KEY_PARAM7RESULT }, 
					null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cur;
	}

	/**
	 * This method will return Cursor holding the specific Analyze record.
	 * 
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	@Override
	public synchronized Cursor fetchAnalyzeDataById(String recordId) throws SQLException {
		Cursor mCursor = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_RECORDID, KEY_TYPE, KEY_ANALYZEPART, KEY_PARAM1RESULT, KEY_PARAM2RESULT, KEY_PARAM3RESULT, KEY_PARAM4RESULT, KEY_PARAM5RESULT, KEY_PARAM6RESULT, KEY_PARAM7RESULT }, 
					KEY_RECORDID + "=" + recordId, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCursor;
	}

	/**
	 * This method will update Analyze record.
	 * 
	 * @param FacialAnalyzeBean
	 * @return boolean
	 */
	@Override
	public synchronized boolean updateAnalyzeDataById(FacialAnalyzeBean bean) {
		long ins = 0;

		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		dbBegin();
		try {
			ContentValues initialValues = new ContentValues();
			
			initialValues.put(KEY_RECORDID, bean.getRid());
			initialValues.put(KEY_USERID, bean.getUid());
			initialValues.put(KEY_TYPE, bean.getType());
			initialValues.put(KEY_SUBTYPE, bean.getSubtype());
			initialValues.put(KEY_ANALYZEPART, bean.getAnalyzePart());
			initialValues.put(KEY_ANALYZETIME, bean.getAnalyzeTime());
			initialValues.put(KEY_ANALYZEPLACE, bean.getAnalyzePlace());
			initialValues.put(KEY_ANALYZECLIMATE, bean.getAnalyzeClimate());
			initialValues.put(KEY_PARAM1VALUE, bean.getParam1Value());
			initialValues.put(KEY_PARAM1RESULT, bean.getParam1Result());
			initialValues.put(KEY_PARAM1STANDARD, bean.getParam1Standard());
			initialValues.put(KEY_PARAM2VALUE, bean.getParam2Value());
			initialValues.put(KEY_PARAM2RESULT, bean.getParam2Result());
			initialValues.put(KEY_PARAM2STANDARD, bean.getParam2Standard());
			initialValues.put(KEY_PARAM3VALUE, bean.getParam3Value());
			initialValues.put(KEY_PARAM3RESULT, bean.getParam3Result());
			initialValues.put(KEY_PARAM3STANDARD, bean.getParam3Standard());
			initialValues.put(KEY_PARAM4VALUE, bean.getParam4Value());
			initialValues.put(KEY_PARAM4RESULT, bean.getParam4Result());
			initialValues.put(KEY_PARAM4STANDARD, bean.getParam4Standard());
			initialValues.put(KEY_PARAM5VALUE, bean.getParam5Value());
			initialValues.put(KEY_PARAM5RESULT, bean.getParam5Result());
			initialValues.put(KEY_PARAM5STANDARD, bean.getParam5Standard());
			initialValues.put(KEY_PARAM6VALUE, bean.getParam6Value());
			initialValues.put(KEY_PARAM6RESULT, bean.getParam6Result());
			initialValues.put(KEY_PARAM6STANDARD, bean.getParam6Standard());
			initialValues.put(KEY_PARAM7VALUE, bean.getParam7Value());
			initialValues.put(KEY_PARAM7RESULT, bean.getParam7Result());
			initialValues.put(KEY_PARAM7STANDARD, bean.getParam7Standard());
			initialValues.put(KEY_COSMETIC_ID, bean.getCosmeticID());
			initialValues.put(KEY_SCHEMA_ID, bean.getSchemaID());
			initialValues.put(KEY_LABEL_ID, bean.getLabelID());
			
			ins = mDb.update(DATABASE_TABLE, initialValues, KEY_RECORDID + "=" + bean.getRid(), null);
			dbSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbClose();
		}
		
		return ins > 0;
	}

	@Override
	public synchronized Cursor fetchLastIdByType(String type, String part, String uid) {
		// SELECT *,FROM_UNIXTIME(`你的字段`, '%Y-%m-%d') as userdate FROM `你的表名`
		// order by `你的字段` desc
		Cursor curType = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			curType = mDb.rawQuery("select * from facial_analyze where type =? and analyzePart =? and userId=? ORDER BY analyzeTime DESC LIMIT 0,1;",
					new String[] { type, part, uid });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curType;
	}

	@Override
	public synchronized Cursor fetchAnalyzeDataByTypePart(String type, String part, String uid, String startTime, String endTime) {
		// SELECT *,FROM_UNIXTIME(`你的字段`, '%Y-%m-%d') as userdate FROM `你的表名`
		// order by `你的字段` desc
		Cursor curType = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			curType = mDb.rawQuery("select * from facial_analyze where type =? and analyzePart =? and userId=? and analyzeTime>? and analyzeTime<? ORDER BY analyzeTime DESC;",
					new String[] { type, part, uid, startTime, endTime });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curType;
	}

	
	@Override
	public synchronized Cursor fetchAllStupidAnalyzeData(int state) {
		Cursor mCursor = null;
		
		if (!mDb.isOpen()) {
			mDb = mDbHelper.getWritableDatabase();
		}
		try {
			mCursor = mDb.rawQuery("select * from facial_analyze where ifCloud =?;", new String[] { Integer.toString(state) });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCursor;
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