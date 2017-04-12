package com.dilapp.radar.util;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

public class UmengUtils {
	
	public static final String EVENT_CREATE_POST = "create_post";
	public static final String EVENT_CREATE_TEST = "create_test";
	public static final String EVENT_CREATE_TOPIC = "create_topic";
	public static final String EVENT_REPLY_POST = "reply_post";
	public static final String EVENT_DEVICE_ACTIVE = "device_active";
	
	public static final String EVENT_ERROR_CRASH = "error_crash";
	public static final String EVENT_ERROR_W	= "error_w";
	
	
	public static final String TYPE_TEST_DAILY = "daily_test";
	public static final String TYPE_TEST_SKIN = "skin_test";
	public static final String TYPE_TEST_PRODUCT = "product_test";
	public static final String TYPE_TEST_SPECIAL = "special_test";
	
	public static final String WEIXIN_APPID = "wx59aa30a4079507e1";
	public static final String WEIXIN_SECRET = "d03b0196e715990ed2210471b77f5821";
	public static final String DESCRIPTOR = "com.umeng.share";
	
	/**
	 * used in Activity.onResume
	 * @param context
	 */
	public static void onResume(Context context){
		MobclickAgent.onResume(context);
	}
	
	/**
	 * used in activity.onPause
	 * @param context
	 */
	public static void onPause(Context context){
		MobclickAgent.onPause(context);
	}
	
	/**
	 * used in Fragment.onResume
	 * @param pagename
	 */
	public static void onPageStart(String pagename){
		MobclickAgent.onPageStart(pagename);
	}
	
	/**
	 * used in Fragment.onPause
	 * @param pagename
	 */
	public static void onPageEnd(String pagename){
		MobclickAgent.onPageEnd(pagename);
	}
	
	public static void onEvent(Context context, String event){
		 HashMap<String,String> map = new HashMap<String,String>();
		 map.put("type","Default");
		 MobclickAgent.onEvent(context, event, map);
//		MobclickAgent.onEvent(context, event);
	}
	
	/**
	 * 统计回帖
	 * @param context
	 * @param topicName
	 */
	public static void onEventPostReply(Context context, String topicid){
		HashMap<String,String> map = new HashMap<String,String>();
		if(TextUtils.isEmpty(topicid)){
			topicid = "Default";
		}
		 map.put("topic_id",topicid);
		 MobclickAgent.onEvent(context, EVENT_REPLY_POST, map);
	}
	
	/**
	 * 统计新建话题
	 * @param context
	 * @param topicName 话题名称
	 */
	public static void onEventTopicCreated(Context context, String topicName){
		HashMap<String,String> map = new HashMap<String,String>();
		if(TextUtils.isEmpty(topicName)){
			topicName = "Default";
		}
		 map.put("topic",topicName);
		 MobclickAgent.onEvent(context, EVENT_CREATE_TOPIC, map);
	}
	
	/**
	 * 统计新建的帖子
	 * @param context
	 * @param topicName 帖子所在话题的名称
	 */
	public static void onEventPostCreated(Context context, String topicName){
		HashMap<String,String> map = new HashMap<String,String>();
		 map.put("topic",topicName);
		 MobclickAgent.onEvent(context, EVENT_CREATE_POST, map);
	}
	
	/**
	 * 为肤质测试定制了接口
	 * @param context
	 * @param testtype  TYPE_TEST_*
	 */
	public static void onEventSkinTest(Context context, String testtype){
		HashMap<String,String> map = new HashMap<String,String>();
		if(TextUtils.isEmpty(testtype)){
			testtype = "Default";
		}
		 map.put("type",testtype);
		 MobclickAgent.onEvent(context, EVENT_CREATE_TEST, map);
	}
	
	/**
	 * map 为当前事件的属性和取值（Key-Value键值对）。
	 
	 * @param context
	 * @param event
	 * @param map
	 * example:
	 * HashMap<String,String> map = new HashMap<String,String>();
	 * map.put("type","default");
	 * MobclickAgent.onEvent(mContext, "event", map);
	 */
	public static void onEvent(Context context, String event, HashMap<String, String> map){
		MobclickAgent.onEvent(context, event, map);
	}
	
	/**
	 * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用
	 * @param context
	 */
	public static void onKillProcess(Context context){
		MobclickAgent.onKillProcess(context);
	}
	
	/**
	 * 测试模式，会在logcat 中打印信息。tag : MobclickAgent
	 * @param flag
	 */
	public static void setDebugMode(boolean flag){
		MobclickAgent.setDebugMode(flag);
	}

}
