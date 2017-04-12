package com.dilapp.radar.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import static com.dilapp.radar.textbuilder.utils.L.*;

import com.dilapp.radar.domain.BaseCallNode;
import com.dilapp.radar.ui.ActivityHelper.StopStateable;
import com.dilapp.radar.util.UmengUtils;

public class BaseFragmentActivity extends FragmentActivity {

	private final static String TAG = BaseFragmentActivity.class.getSimpleName();
	private final static boolean DEBUG = false;

	private ActivityHelper mHelpActivity;
	private BaseCallbackManager mCallbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelpActivity = new ActivityHelper(this);
		mCallbackManager = new BaseCallbackManager();
		if(DEBUG) d(TAG, "onCreate\t\t" + getClass().getSimpleName());
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(DEBUG) d(TAG,  "onStart\t\t" + getClass().getSimpleName());
	}

	public <T extends View> T findViewById_(int id) {
		return (T) findViewById(id);
	}
	
	public void addCallback(BaseCallNode node){
		mCallbackManager.addCallbace(node);
	}

	/**
	 * 将onClick事件交给子Fragment处理
	 * @param weight
	 */
	public void onClick(View weight) {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if(fragments == null) {
			return;
		}
		for (Fragment f : fragments) {
			if (f == null) {
				continue;
			}
			if (f.isVisible() && f.isInLayout()) {
				try {
					Method m = f.getClass().getMethod("onClick", View.class);
					if(DEBUG) d(TAG, "class " + f.getClass().getSimpleName());
					m.invoke(f, weight);
				} catch (NoSuchMethodException e) {
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
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

	@Override
	protected void onResume() {
		super.onResume();
		UmengUtils.onResume(this);
		if(DEBUG) d(TAG, "onResume\t\t" + getClass().getSimpleName());
	}

	@Override
	protected void onPause() {
		UmengUtils.onPause(this);
		if(DEBUG) d(TAG, "onPause\t\t" + getClass().getSimpleName());

		super.onPause();
	}

	@Override
	protected void onStop() {
		if(DEBUG) d(TAG, "onStop\t\t" + getClass().getSimpleName());
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if(DEBUG) d(TAG, "onDestroy\t\t" + getClass().getSimpleName());
		super.onDestroy();
		mCallbackManager.clearCallback();
	}

	/*@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		l("onSaveInstanceState\t" + getClass().getSimpleName());
	}*/

	@Override
	protected void onRestart() {
		super.onRestart();
		if(DEBUG) d(TAG, "onRestart\t\t" + getClass().getSimpleName());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(DEBUG) d(TAG, "onNewIntent\t\t" + getClass().getSimpleName());
	}
}
