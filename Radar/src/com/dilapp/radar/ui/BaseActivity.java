package com.dilapp.radar.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dilapp.radar.application.ActivityManager;
import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.domain.BaseCallNode;
import com.dilapp.radar.ui.ActivityHelper.StopStateable;
import com.dilapp.radar.util.UmengUtils;

public class BaseActivity extends Activity {

	private final static String TAG = BaseActivity.class.getName();
	private final static boolean DEBUG = true;

	private ActivityHelper mHelpActivity;
	private RadarApplication mApplication;
	private ActivityManager activityManager = null;
	
	private BaseCallbackManager mCallbackManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelpActivity = new ActivityHelper(this);
		mCallbackManager = new BaseCallbackManager();
		mApplication = (RadarApplication) getApplicationContext();
		//将当前Activity压入栈
		activityManager = ActivityManager.getActivityManagerIntance(this);
		activityManager.pushTask(this);
	}

	public <T extends View> T findViewById_(int id) {
		return (T) findViewById(id);
	}
	
	public void addCallback(BaseCallNode node){
		mCallbackManager.addCallbace(node);
	}

	/**
	 * 显示等待对话框
	 * 
	 * @param task
	 *            你的异步任务，如果用户点击Back键，可以取消正在执行的异步任务.没有可以为null
	 */
	protected void showWaitingDialog(final AsyncTask<?, ?, ?> task) {
		mHelpActivity.showWaitingDialog(task);
	}

	/**
	 * 
	 * 显示等待对话框
	 * 
	 * @param contextState
	 *            你的状态模式，如果用户点击Back键，并且当前正在执行的State实现了{@link StopStateable},将会调用
	 *            {@link StopStateable#stop()}
	 */
	protected void showWaitingDialog(final ContextState contextState) {
		mHelpActivity.showWaitingDialog(contextState);
	}

	/**
	 * 设置等待的文本
	 * 
	 * @param text
	 */
	protected void setWaitingText(CharSequence text) {
		mHelpActivity.setWaitingText(text);
	}

	/**
	 * 关闭等待对话框
	 */
	protected void dimessWaitingDialog() {
		mHelpActivity.dimessWaitingDialog();
	}

	protected void i(String msg) {
		if (DEBUG)
			Log.i(TAG, msg);
	}

	protected void w(String msg) {
		Log.w(TAG, msg);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.removeTask(this);
		mCallbackManager.clearCallback();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		UmengUtils.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		UmengUtils.onPause(this);
	}
	
	
}
