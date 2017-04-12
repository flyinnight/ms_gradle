package com.dilapp.radar.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
/**
 * 
 * @author hj
 * @time 2015-03-12
 *
 */
public class DBCacheHelper {

    /**
     *   insert or update postList and postDetail
     * @param context
     * @param item_json
     * @param type_id
     * @param detailpost_id
     */
	public static void insertOrUpdateToPostList(Context context,String json,int type_id,int detailpost_id){
		Cursor cursor = null;
		Cursor mCursor = null;
		try {
			
			//context.getContentResolver().delete(CatchProvider.POSTlISTURI, CatchProvider.POSTS_COLUMN_DETAIL_ID +" = "+detailpost_id, null);
			String where = DBCacheProvider.POSTS_COLUMN_DETAIL_ID+" = "+detailpost_id +
					" and "+DBCacheProvider.POSTS_COLUMN_TYPE_ID+" = "+type_id;
			cursor = context.getContentResolver().query(DBCacheProvider.POSTlISTURI, mPostListProjection, where, null, null);
			ContentValues values  = null;
			if(cursor.moveToNext()){
				
			}else{
				values = new ContentValues();
				values.put(DBCacheProvider.POSTS_COLUMN_TYPE_ID, type_id);
				values.put(DBCacheProvider.POSTS_COLUMN_DETAIL_ID, detailpost_id);
				context.getContentResolver().insert(DBCacheProvider.POSTlISTURI, values);
			}
			where = DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID + " = " + detailpost_id;
		    mCursor = context.getContentResolver().query(DBCacheProvider.DETAILPOSTURI, mDetailPostProjrction, where, null, null);
			if(mCursor.moveToNext()){
				values = new ContentValues();
			    values.put(DBCacheProvider.DETAIL_COLUMNS_ITEM,json);
			    where = DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID + " = "+detailpost_id;
			    context.getContentResolver().update(DBCacheProvider.DETAILPOSTURI, values, where, null);
			}else{
				 values = new ContentValues();
				 values.put(DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID, detailpost_id);
				 values.put(DBCacheProvider.DETAIL_COLUMNS_ITEM, json);
				 context.getContentResolver().insert(DBCacheProvider.DETAILPOSTURI, values);		
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper insertOrUpdateToPostList is error");
		}finally{
			cursor.close();
			mCursor.close();
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param id
	 */
	public static void deletePostList(Context context,int id){
		
		try {
			context.getContentResolver().delete(DBCacheProvider.POSTlISTURI, DBCacheProvider.POSTS_COLUMN_ID +" = "+id, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper deletePostList is error");
		}
	}
	
   /**
    * 
    * @param context
    * @param type_id
    */
	public static void deletePostListOfType(Context context,int type_id){
		
		try {
			context.getContentResolver().delete(DBCacheProvider.POSTlISTURI, DBCacheProvider.POSTS_COLUMN_TYPE_ID +" = "+type_id, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper deletePostListOfType is error");
		}
		
	}
	
	private static String []mPostListProjection = new  String[]{
									DBCacheProvider.POSTS_COLUMN_ID,
									DBCacheProvider.POSTSTYPE_COLUMN_TYPE_ID,
									DBCacheProvider.POSTS_COLUMN_DETAIL_ID
									};
	
	
	
    /**
     * 
     * @param context
     * @param typeId
     * @return  _id,detailpost_id,item_json
     */
	public static Cursor queryPostList(Context context,int typeId){
		
		Cursor cursor = null;
		try {
			//String selection = CatchProvider.POSTS_COLUMN_TYPE_ID + " = "+typeId;
			//cursor = context.getContentResolver().query(CatchProvider.POSTlISTURI, mPostListProjection, selection, null, null);
		    String selection = DBCacheProvider.POSTS_COLUMN_TYPE_ID + " = "+ typeId;
			cursor = context.getContentResolver().query(DBCacheProvider.POSTANDDETAILURI, mDetailPostProjrction, selection, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper queryPostand detail is error");
		}
		return cursor;
	}
	
	 /**
	  * 
	  * @param context
	  * @param detail_id
	  * @param json
	  */
	public static void updateToDetailPost(Context context,int detail_id,String json){
		
		try {
			ContentValues values = new ContentValues();
			values.put(DBCacheProvider.DETAIL_COLUMNS_ITEM, json);
			String where = DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID  + " = " +detail_id;
			context.getContentResolver().update(DBCacheProvider.DETAILPOSTURI, values, where, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper UpdateToDetailPost is error");
		}
		
	}
    /**
     * delete postlist and postsdetail
     * @param context
     * @param detail_id
     */
	public static void deleteToDetailPost(Context context,int detail_id){
	    try {
			String where = DBCacheProvider.DETAIL_COLUMNS_ID + " = "+ detail_id;
			context.getContentResolver().delete(DBCacheProvider.DETAILPOSTURI, where, null);
			where = DBCacheProvider.POSTS_COLUMN_DETAIL_ID + " = "+ detail_id;
			context.getContentResolver().delete(DBCacheProvider.POSTlISTURI, where, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj","CatchHelper deleteToDetailPost is error");
		}
		
	}
	
	
	private static  String[] mDetailPostProjrction = new String[] {
		DBCacheProvider.DETAIL_COLUMNS_ID,
        DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID,
        DBCacheProvider.DETAIL_COLUMNS_ITEM
      };
	
	private static Cursor getDatailPost(Context context ,String json){
		Cursor cursor = null;;
		try {
			String selection = DBCacheProvider.DETAIL_COLUMNS_ITEM + " = "+ json;
			cursor = context.getContentResolver().query(DBCacheProvider.DETAILPOSTURI, mDetailPostProjrction, selection, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper getDatailPost is error");
		}
		return cursor;
		
	}
	
   /**
    * 
    * @param context
    * @param detailpostId
    * @return  _id,detailpost_id,item_json
    */
	public static Cursor getDetailPost(Context context ,int detailpostId){
		Cursor mCursor = null;
		try {
			String selection = DBCacheProvider.DETAIL_COLUMNS_DETAIL_ID + " = "+ detailpostId;
			mCursor = context.getContentResolver().query(DBCacheProvider.DETAILPOSTURI, mDetailPostProjrction, selection, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper getDetailPost is error");
		}
		return mCursor;
		
	}
	
    /**
     * 
     * @param context
     * @param detailPostId
     * @param user_id
     * @param floor_id
     * @param json
     */
	public static void insertOrUpdateToAnswer(Context context,int detailPostId,int user_id,int floor_id,String json){
		
		try {
			ContentValues values = new ContentValues();
			values.put(DBCacheProvider.ANSWER_COLUMNS_DETAIL_ID, detailPostId);
			values.put(DBCacheProvider.ANSWER_COLUMNS_USER_ID, user_id);
			values.put(DBCacheProvider.ANSWER_COLUMNS_FLOOR_ID, floor_id);
			values.put(DBCacheProvider.ANSWER_COLUMNS_ITEM, json);
			deleteToAnswer(context,detailPostId,user_id,floor_id);
			context.getContentResolver().insert(DBCacheProvider.ANSWERURI, values);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper insertOrUpdateToAnswer is error");
		}
		
	}
	
     /**
      * 
      * @param context
      * @param detailPostId
      * @param user_id
      * @param floor_id
      */
	public static void deleteToAnswer(Context context,int detailPostId,int user_id,int floor_id){
		try {
			String where = DBCacheProvider.ANSWER_COLUMNS_DETAIL_ID + " = "+detailPostId +
					" and "+DBCacheProvider.ANSWER_COLUMNS_USER_ID +" = "+user_id +
					" and "+ DBCacheProvider.ANSWER_COLUMNS_FLOOR_ID +" = "+floor_id;
			context.getContentResolver().delete(DBCacheProvider.ANSWERURI, where, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper deleteToAnswer is error");
		}
	}
	
	
	private static String [] mAnswerProjection = new String[]{
			DBCacheProvider.ANSWER_COLUMNS_ID,
			DBCacheProvider.ANSWER_COLUMNS_DETAIL_ID,
			DBCacheProvider.ANSWER_COLUMNS_USER_ID,
			DBCacheProvider.ANSWER_COLUMNS_FLOOR_ID,
			DBCacheProvider.ANSWER_COLUMNS_ITEM
	};
    
	/**
	 * 
	 * @param context
	 * @param detailPostId
	 * @return _id,detailpost_id,user_id,floor_id,item_json
	 */
	public static Cursor queryAnswer(Context context,int detailPostId){
		Cursor cursor = null;
		try {
			String where = DBCacheProvider.ANSWER_COLUMNS_DETAIL_ID +" = "+detailPostId;
			cursor = context.getContentResolver().query(DBCacheProvider.ANSWERURI, mAnswerProjection, where, null, DBCacheProvider.ANSWER_COLUMNS_FLOOR_ID+ " ASC ");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper queryAnswer is error");		
		}
		
		return cursor;
	}
	
	 /**
	  *  delete all tables data
	  * @param context
	  */
	public static void deleteCatch(Context context){
		
		try {
			context.getContentResolver().delete(DBCacheProvider.POSTlISTURI,	null, null);
		} catch (Exception e) {
			Log.e("hj", "CatchHelper deleteCatch  delete postlist error");
			e.printStackTrace();
		}
		try {
			context.getContentResolver().delete(DBCacheProvider.DETAILPOSTURI,	null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper deleteCatch  delete detail post is error");
		}
		try {
			context.getContentResolver().delete(DBCacheProvider.ANSWERURI, null,	null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hj", "CatchHelper deleteCatch  delete answer is error");
		}
	  
		
		
		
	}
	
	
	
	
	
}
