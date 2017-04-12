package com.dilapp.radar.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dilapp.radar.db.dao.impl.AnalyzeDaoImpl;
import com.dilapp.radar.db.dao.impl.AuthDaoImpl;
import com.dilapp.radar.db.dao.impl.BannerCollectionDaoImpl;
import com.dilapp.radar.db.dao.impl.MainPostDaoImpl;
import com.dilapp.radar.db.dao.impl.PostDetailDaoImpl;
import com.dilapp.radar.db.dao.impl.PostListDaoImpl;
import com.dilapp.radar.db.dao.impl.SolutionDataDaoImpl;
import com.dilapp.radar.db.dao.impl.SolutionListDaoImpl;
import com.dilapp.radar.db.dao.impl.TopicDetailDaoImpl;
import com.dilapp.radar.db.dao.impl.TopicListDaoImpl;
import com.dilapp.radar.db.dao.impl.UserDaoImpl;
import com.dilapp.radar.db.dao.impl.UserRelationDaoImpl;

/**
 * 
 * @author hj
 * @time 2015-03-16
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private final String TAG = "DBHelper.class";
	private static final String DBNAME = "skin.db";
	private static final int VERSION = 21;
	private static DBHelper dbHelper;

	public static DBHelper getInstance(Context context) {
		if (dbHelper == null) {
			synchronized (DBHelper.class) { 
				if (dbHelper == null) { 
					dbHelper = new DBHelper(context);
				}
			}
		}
		return dbHelper;
	}

	public DBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		onCreatePostList(db);
		onCreatePostType(db);
		onCreatePostsDetail(db);
		onCreateAnswer(db);
		// 用户
		//onCreateUser(db);
		// 测试
		onCreateAnalyze(db);
		// 权限
		//onCreateAuth(db);
		// 主页贴子列表
		onCreateMainPostList(db);
		// 帖子详情列表
		onCreatePostDetailList(db);
		// 普通界面帖子列表
		onCreatePostContentList(db);
		// 话题列表
		onCreateTopicList(db);
		// 护肤方案列表
		onCreateSolutionList(db);
		// 话题详情列表
		onCreateTopicDetailList(db);
		// banner或精选帖
		onBannerCollectionList(db);
		// 用户关系列表
		onUserRelationList(db);
		// 护肤方案数据
		onCreateSolutionData(db);
	}

	private void onCreateAnswer(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "answer");
			String sql = "CREATE TABLE answer(_id INTEGER PRIMARY KEY," + "postdetail_id  TEXT ," + "user_id TEXT ,"
					+ "floor_id  TEXT," + "item_json   TEXT);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.e("hj", "creat table answer is erros");
		}
	}

	private void onCreatePostsDetail(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "postdetail");
			String sql = "CREATE TABLE postsdetail(_id INTEGER PRIMARY KEY," + "postdetail_id     TEXT ,"
					+ "item_json   TEXT);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.e("hj", "creat table posttype is erros");
		}
	}

	private void onCreatePostType(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "posttype");
			String sql = "CREATE TABLE posttype(_id INTEGER PRIMARY KEY," + "type_id  INTEGER," + "type_name   TEXT);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.e("hj", "creat table posttype is erros");
		}
	}

	private void onCreatePostList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "postlist");
			String sql = "CREATE TABLE postlist( _id INTEGER PRIMARY KEY," + "type_id    INTEGER,"
					+ "postdetail_id    TEXT);";
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.e("hj", "crate table postlist is errors");
		}
	}

	private void onCreateUser(SQLiteDatabase db) {
		try {
			db.execSQL(UserDaoImpl.CREATE_USER_TABLE);
		} catch (SQLException e) {
			Log.e(TAG, "create table user is errors");
		}
	}

	private void onCreateAnalyze(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "facial_analyze");
			db.execSQL(AnalyzeDaoImpl.CREATE_FACIAL_ANALYZE_TABLE);
		} catch (SQLException e) {
			Log.e(TAG, "create table facial_analyze is errors");
		}
	}

	private void onCreateAuth(SQLiteDatabase db) {
		try {
			db.execSQL(AuthDaoImpl.CREATE_USER_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create table auth is errors");
		}
	}
	
	private void onCreateMainPostList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "main_post_lists");
			db.execSQL(MainPostDaoImpl.CREATE_MAIN_POST_LISTS_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create main post list error");
		}
	}
	
	private void onCreatePostDetailList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "post_detail_lists");
			db.execSQL(PostDetailDaoImpl.CREATE_POST_DETAIL_LISTS_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create post detail list error");
		}
	}
	
	private void onCreatePostContentList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "post_lists");
			db.execSQL(PostListDaoImpl.CREATE_POST_LISTS_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create post content list error");
		}
	}
	
	private void onCreateTopicList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "topic_lists");
			db.execSQL(TopicListDaoImpl.CREATE_TOPIC_LISTS_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create topic list error");
		}
	}
	
	private void onCreateSolutionList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "solution_lists");
			db.execSQL(SolutionListDaoImpl.CREATE_SOLUTION_LISTS_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create solution list error");
		}
	}
	
	private void onCreateTopicDetailList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "topic_detail_lists");
			db.execSQL(TopicDetailDaoImpl.CREATE_TOPIC_DETAIL_LIST_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create topic detail list error");
		}
	}
	
	private void onBannerCollectionList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "banner_collection_lists");
			db.execSQL(BannerCollectionDaoImpl.CREATE_BANNER_COLLECTION_LIST_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create banner collection list error");
		}
	}
	
	private void onUserRelationList(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "user_relation_lists");
			db.execSQL(UserRelationDaoImpl.CREATE_USER_RELATION_LIST_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create user relation list error");
		}
	}

	private void onCreateSolutionData(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + "solution_data");
			db.execSQL(SolutionDataDaoImpl.CREATE_SOLUTION_DATA_TABLE);
		} catch (Exception e) {
			Log.e(TAG, "create solution data error");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion < newVersion) {
			onCreate(db);
		}

	}

}
