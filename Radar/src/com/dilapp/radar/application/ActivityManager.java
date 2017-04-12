package com.dilapp.radar.application;

import java.lang.ref.WeakReference;
import java.util.Stack;
import android.app.Activity;
import android.content.Context;

public class ActivityManager {
	
	/***寄存整个应用Activity**/
	private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();
	
	private static ActivityManager activityManager = null;
	
	private static Context mContext = null;
	private static Object object = new Object();
	private static RadarApplication mApplication;
	
	
    private ActivityManager(){ }
    
	
	public static ActivityManager getActivityManagerIntance(Context context){
		synchronized (object) {
			if(activityManager == null){
			   mContext = context;
				activityManager = new ActivityManager();
				mApplication = (RadarApplication) context.getApplicationContext();
			}
		}
		return activityManager;
	}
	
	
	
	/**
	 * 将Activity压入Application栈
	 * @param task 将要压入栈的Activity对象
	 */
	public  void pushTask(Activity context) {
		WeakReference<Activity> task = new WeakReference<Activity>(context);
		activitys.push(task);
	}

	
	/**
	 * 将传入的Activity对象从栈中移除
	 * @param task
	 */
	public  void removeTask(Activity context) {
		
		WeakReference<Activity> task = new WeakReference<Activity>(context);
		activitys.remove(task);
	}

	/**
	 * 根据指定位置从栈中移除Activity
	 * @param taskIndex Activity栈索引
	 */
	public  void removeTask(int taskIndex) {
		if (activitys.size() > taskIndex)
			activitys.remove(taskIndex);
	}

	/**
	 * 将栈中Activity移除至栈顶
	 */
	public  void removeToTop() {
		int end = activitys.size();
		int start = 1;
		for (int i = end - 1; i >= start; i--) {
			if (!activitys.get(i).get().isFinishing()) {     
				activitys.get(i).get().finish(); 
		    }
		}
	}

	/**
	 * 移除全部（用于整个应用退出）
	 */
	public  void removeAll() {
		//finish所有的Activity
		for (WeakReference<Activity> task : activitys) {
			if (!task.get().isFinishing()) {     
				task.get().finish(); 
		    }  
		}
		//调用Application 中资源回收的方法
		mApplication.onDestory();
		activityManager = null;
	}
	
	
	
	

}
