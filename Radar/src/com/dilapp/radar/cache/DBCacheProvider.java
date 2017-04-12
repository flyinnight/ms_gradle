package com.dilapp.radar.cache;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.dilapp.radar.db.DBHelper;
/**
 * 
 * @author hj
 * @time 2015-03-16
 *
 */
public class DBCacheProvider extends ContentProvider {
	
	private final static String AUTHORITY = "com.dilapp.radar.cache.DBCacheProvider";
	private final static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	// match index
	private final static int POSTLIST_INDEX = 1;
	private final static int POSTTYPE_INDEX = 2;
	private final static int DETAILPOST_INDEX = 3;
	private final static int ANSWER_INDEX = 4;
	private final static int POSTANDDETAIL_INDEX = 5;
	
	// table names
	private final static String POSTLISTTABLE = "postlist";
	private final static String POSTTYPETABLE = "posttype";
	private final static String DEATILPOSTTABLE = "postsdetail";
	private final static String ANSWERTABLE = "answer";
	private final static String POSTANDDETAILTABLE = "postanddetailtable";
	
	//postlist Table  columns
	public static final String POSTS_COLUMN_ID  = "_id";
	public static final String POSTS_COLUMN_TYPE_ID  = "type_id";
	public static final String POSTS_COLUMN_DETAIL_ID  = "postdetail_id";
	
	//detailPost Table columns
	public static final String DETAIL_COLUMNS_ID = "_id";
	public static final String DETAIL_COLUMNS_DETAIL_ID = "postdetail_id";
	public static final String DETAIL_COLUMNS_ITEM = "item_json";
	
	//answer Table columns
	public static final String ANSWER_COLUMNS_ID = "_id";
	public static final String ANSWER_COLUMNS_USER_ID = "user_id";
	public static final String ANSWER_COLUMNS_FLOOR_ID = "floor_id";
	public static final String ANSWER_COLUMNS_DETAIL_ID = "postdetail_id";
	public static final String ANSWER_COLUMNS_ITEM = "item_json";
	
	//postsType Table columns
	public static final String POSTSTYPE_COLUMN_ID  = "_id";
	public static final String POSTSTYPE_COLUMN_TYPE_ID  = "type_id";
	public static final String POSTSTYPE_COLUMN_TYPE_NAME  = "type_name";

	//Match Uri
	public static final Uri POSTlISTURI = Uri.parse("content://"+ AUTHORITY+"/"+POSTLISTTABLE);
	public static final Uri POSTTYPESURI = Uri.parse("content://"+ AUTHORITY+"/"+POSTTYPETABLE);
	public static final Uri DETAILPOSTURI = Uri.parse("content://"+ AUTHORITY+"/"+DEATILPOSTTABLE);
	public static final Uri ANSWERURI = Uri.parse("content://"+ AUTHORITY+"/"+ANSWERTABLE);
	public static final Uri POSTANDDETAILURI = Uri.parse("content://"+ AUTHORITY+"/"+POSTANDDETAILTABLE);
	
	// ProjectionMap 
	private static final Map<String,String> mPostListProjectionMap = new HashMap<String, String>();
	private static final Map<String,String> mPostTypeProjectionMap = new HashMap<String, String>();
	private static final Map<String,String> mDetailPostProjectionMap = new HashMap<String, String>();
	private static final Map<String,String> mAnswerProjectionMap = new HashMap<String, String>();
	
	static{
		matcher.addURI(AUTHORITY, POSTLISTTABLE, POSTLIST_INDEX);
		matcher.addURI(AUTHORITY, POSTTYPETABLE, POSTTYPE_INDEX);
		matcher.addURI(AUTHORITY, DEATILPOSTTABLE, DETAILPOST_INDEX);
		matcher.addURI(AUTHORITY, ANSWERTABLE, ANSWER_INDEX);
		matcher.addURI(AUTHORITY, "postanddetailuri", POSTANDDETAIL_INDEX);
	}
	
	static{
		mPostTypeProjectionMap.put(POSTSTYPE_COLUMN_ID, POSTS_COLUMN_ID);
		mPostTypeProjectionMap.put(POSTSTYPE_COLUMN_TYPE_NAME, POSTSTYPE_COLUMN_TYPE_NAME);
		
		mPostListProjectionMap.put(POSTS_COLUMN_ID, POSTS_COLUMN_ID);
		mPostListProjectionMap.put(POSTS_COLUMN_TYPE_ID, POSTS_COLUMN_TYPE_ID);
		mPostListProjectionMap.put(POSTS_COLUMN_DETAIL_ID, POSTS_COLUMN_DETAIL_ID);
		
		mDetailPostProjectionMap.put(DETAIL_COLUMNS_ID, DETAIL_COLUMNS_ID);
		mDetailPostProjectionMap.put(DETAIL_COLUMNS_ITEM, DETAIL_COLUMNS_ITEM);
		mDetailPostProjectionMap.put(DETAIL_COLUMNS_DETAIL_ID, DETAIL_COLUMNS_DETAIL_ID);
		
		mAnswerProjectionMap.put(ANSWER_COLUMNS_ID, ANSWER_COLUMNS_ID);
		mAnswerProjectionMap.put(ANSWER_COLUMNS_FLOOR_ID, ANSWER_COLUMNS_FLOOR_ID);
		mAnswerProjectionMap.put(ANSWER_COLUMNS_USER_ID, ANSWER_COLUMNS_USER_ID);
		mAnswerProjectionMap.put(ANSWER_COLUMNS_ITEM, ANSWER_COLUMNS_ITEM);
		mAnswerProjectionMap.put(ANSWER_COLUMNS_DETAIL_ID, ANSWER_COLUMNS_DETAIL_ID);
		
	}
	
	private SQLiteOpenHelper SQlHelper;
	
	@Override
	public boolean onCreate() {
		SQlHelper = DBHelper.getInstance(getContext());
		return false;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(values == null) return null;
		int code = matcher.match(uri);
		SQLiteDatabase db  = SQlHelper.getWritableDatabase();
		long id ;
		Uri result_uri = null;
		switch (code) {
		case POSTLIST_INDEX:
			 id = db.insert(POSTLISTTABLE, POSTS_COLUMN_ID, values);
			 result_uri = ContentUris.withAppendedId(uri, id);
			 Log.i("hj","uri "+ uri);
			break;
		case POSTTYPE_INDEX:
			 id = db.insert(POSTTYPETABLE,null, values);
			 result_uri = ContentUris.withAppendedId(uri, id);
			break;
		case DETAILPOST_INDEX:
			id = db.insert(DEATILPOSTTABLE, null, values);
			Log.i("hj", "DETAILPOST_INDEX "+values.get(DBCacheProvider.DETAIL_COLUMNS_ITEM));
			break;
		case ANSWER_INDEX:
			id = db.insert(ANSWERTABLE, null, values);
			break;
		default:
			break;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return result_uri;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int code = matcher.match(uri);
		SQLiteDatabase db  = SQlHelper.getWritableDatabase();
		int id = -1;
		switch (code) {
		case POSTLIST_INDEX:
			id = db.delete(POSTLISTTABLE, where, whereArgs);
			break;
		case POSTTYPE_INDEX:
			id = db.delete(POSTTYPETABLE, where, whereArgs);
			break;
		case DETAILPOST_INDEX:
			id  = db.delete(DEATILPOSTTABLE, where, whereArgs);
			break;
		case ANSWER_INDEX:
			id=  db.delete(ANSWERTABLE, where, whereArgs);
			break;
		default:
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);	
		return id;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int code = matcher.match(uri);
		SQLiteDatabase db  = SQlHelper.getWritableDatabase();
		int id = -1;
		switch (code) {
		case POSTLIST_INDEX:
			id = db.update(POSTLISTTABLE, values, selection, selectionArgs);
			break;
		case POSTTYPE_INDEX:
			id = db.update(POSTTYPETABLE, values, selection, selectionArgs);
			break;
		case DETAILPOST_INDEX:
			id = db.update(DEATILPOSTTABLE, values, selection, selectionArgs);
			break;
		case ANSWER_INDEX:
			id =  db.update(ANSWERTABLE, values, selection, selectionArgs);
			break;
		default:
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);	
		return id;
	}
	
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		boolean flag = true;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int code = matcher.match(uri);
	   Cursor result = null;
		switch (code) {
		case POSTLIST_INDEX:
			qb.setTables(POSTLISTTABLE);
			qb.setProjectionMap(mPostListProjectionMap);
			break;
		case POSTTYPE_INDEX:
			qb.setTables(POSTTYPETABLE);
			qb.setProjectionMap(mPostTypeProjectionMap);
			break;
		case DETAILPOST_INDEX:
		   qb.setTables(DEATILPOSTTABLE);
		   qb.setProjectionMap(mDetailPostProjectionMap);
		   break;
		case ANSWER_INDEX:
			qb.setTables(ANSWERTABLE);
			qb.setProjectionMap(mAnswerProjectionMap);
			break;
		case POSTANDDETAIL_INDEX:
			flag = false;
			String sql = "select postsdetail.[_id],postlist.[postdetail_id] ,item_json " +
				         	"from postlist ,postsdetail " +
			 "where  postlist.[postdetail_id] = postsdetail.[postdetail_id]  and " +selection;
			SQLiteDatabase db  = SQlHelper.getReadableDatabase();
			result = db.rawQuery(sql, null);
			
			break;
		default:
			throw new IllegalArgumentException("no uri match " + uri);
			
		}
		if(flag){
		  SQLiteDatabase db  = SQlHelper.getReadableDatabase();
		  result = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		  result.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return result;
	}
	
	
	@Override
	public String getType(Uri arg0) {
		return null;
	}
	

}
