/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dilapp.radar.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.Login;
import com.dilapp.radar.domain.Login.LoginReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.found.FragmentFound;
import com.dilapp.radar.ui.mine.FragmentMine;
import com.dilapp.radar.ui.skintest.FragmentTest;
import com.dilapp.radar.ui.topic.FragmentTopic;
import com.dilapp.radar.update.UpdateTestDataImpl;
import com.dilapp.radar.util.EMChatUtils;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.ov.omniwificam.OVBroadcast;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * This demonstrates how you can implement switching between the tabs of a
 * TabHost through fragments, using FragmentTabHost.
 */
public class ActivityTabs extends BaseFragmentActivity implements
		OnTabChangeListener {
	private FragmentTabHost mTabHost;
	private String mPreTabId;
	private boolean isOnPause = false;

	private static final long ENV_PARAM_TIME = 5000;
	private static final int MSG_GET_ENV_PARAM = 1;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_GET_ENV_PARAM:
				if (!isOnPause) {
					AllKfirManager.getInstance(ActivityTabs.this)
							.startGetEnvParamWhenFree();
					mHandler.removeMessages(MSG_GET_ENV_PARAM);
					mHandler.sendEmptyMessageDelayed(MSG_GET_ENV_PARAM,
							ENV_PARAM_TIME);
				}
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		d("III", "ActivityTabs onActivityResult req " + requestCode + ", res " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);

		UmengUpdateAgent.setUpdateCheckConfig(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		// SharePreCacheHelper.setAppUpdateFlag(this, false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					Slog.d("有更新");
					SharePreCacheHelper.setAppUpdateFlag(ActivityTabs.this,
							true);
					UmengUpdateAgent.showUpdateDialog(ActivityTabs.this,
							updateInfo);
					break;
				case UpdateStatus.No: // has no update
					SharePreCacheHelper.setAppUpdateFlag(ActivityTabs.this,
							false);
					Slog.d("没有更新");
					// Toast.makeText(mContext, "没有更新",
					// Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.NoneWifi: // none wifi
					Slog.d("没有WI-FI");
					// Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
					// Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.Timeout: // time out
					Slog.d("超时");
					// Toast.makeText(mContext, "超时",
					// Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
		// EMChatUtils.startLogin(SharePreCacheHelper.getUserID(this));

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent/* , R.id.realtabcontent */);
		mTabHost.setOnTabChangedListener(this);

		// View[] indicato rViews = getIndicatorViews();
		FragmentInfo[] infos = getFragmentInfos();
		for (int i = 0; i < infos.length; i++) {
			FragmentInfo info = infos[i];
			mTabHost.addTab(
					mTabHost.newTabSpec(info.tag).setIndicator(
							getIndicatorView(info.iconRes, info.textRes)),
					info.clazz, info.args);
		}

		test();
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		Intent intentService = new Intent(this, OVBroadcast.class);
		intentService.putExtra("alertmode", "0");
		startService(intentService);
	}

	// add by kfir
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean haspaired = SharePreCacheHelper.getPairStatus(this);
			if (haspaired) {
				AllKfirManager.getInstance(this).endSkinTest();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public void setCurrentTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	@Override
	public void onTabChanged(String tabId) {
		if ("skintest".equals(tabId)) {

		} else if (!"skintest".equals(tabId) && "skintest".equals(mPreTabId)) {
			Context context = getApplicationContext();
			// WifiHelper.getInstance(context).endSkinTest(context);
			// AllKfirManager.getInstance(context).endSkinTest();
		}
		mPreTabId = tabId;
	}

	@Override
	public void onBackPressed() {
		if ("topic".equals(mTabHost.getCurrentTabTag())) {
			super.onBackPressed();
		} else {
			mTabHost.setCurrentTabByTag("topic");
		}
	}

	private void test() {
		if (!BuildConfig.DEBUG) {
			return;
		}
		mTabHost.setCurrentTab(0);

		// startLogin();
		testExportAssets();
	}

	private void startLogin() {
		Login l = ReqFactory.buildInterface(getApplicationContext(),
				Login.class);
		// SharePreCacheHelper.setUserID(this, "lenovo");
		// SharePreCacheHelper.setPassword(this, "lenovo");
		String mUserID = SharePreCacheHelper.getUserName(this);
		String mCurrPwd = SharePreCacheHelper.getPassword(this);
		// SharePreCacheHelper.setUserName(this, null);
		LoginReq bean = new LoginReq();
		bean.setUsername(mUserID);
		bean.setPwd(mCurrPwd);
		l.loginAsync(bean, null);
		// try {
		// l.loginAsync(bean, new BaseCall<Login.LoginResp>() {
		// @Override
		// public void call(LoginResp obj) {
		// if (obj != null && obj.isRequestSuccess()) {
		// Toast.makeText(getApplicationContext(),
		// obj.getMessage(), Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		// } catch (Exception e) {
		// Toast.makeText(getApplicationContext(), "服务还未开启!",
		// Toast.LENGTH_SHORT).show();
		// }
	}

	private void testExportAssets() {
		final String test_rgb = "rgb.jpg";
		final String test_pl = "pl.jpg";
		File dir = getCacheDir();

		File testRgb = new File(dir, test_rgb);
		File testPl = new File(dir, test_pl);
		if (testRgb.exists() && testPl.exists()) {
			Log.i("III", test_rgb + " and " + test_pl + " exists!");
			return;
		}

		AssetManager am = getAssets();
		try {
			int len = 0;
			byte[] buf = new byte[1024 * 5];
			InputStream ris = am.open("test/" + test_rgb);
			OutputStream ros = new FileOutputStream(testRgb);
			while ((len = ris.read(buf)) != -1) {
				ros.write(buf, 0, len);
			}
			ris.close();
			ros.close();
			Log.i("III", "rgb.jpg writed!");

			InputStream pis = am.open("test/" + test_pl);
			OutputStream pos = new FileOutputStream(testPl);
			while ((len = pis.read(buf)) != -1) {
				pos.write(buf, 0, len);
			}
			pis.close();
			pos.close();
			Log.i("III", "pl.jpg writed!");

			// am.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isOnPause = false;
		mHandler.removeMessages(MSG_GET_ENV_PARAM);
		mHandler.sendEmptyMessageDelayed(MSG_GET_ENV_PARAM, ENV_PARAM_TIME);
		UpdateTestDataImpl.getInstance(this).startCheckSend();
		if (ReleaseUtils.DEBUG_REMOTE_MODE) {
			String userId = SharePreCacheHelper.getUserID(this);
			if (!TextUtils.isEmpty(userId))
				EMChatUtils.startLogin(this);
		} else {
			EMChatUtils.startLogin(this);
		}

		/*
		 * String currID = SharePreCacheHelper.getUserID(this); String currPwd =
		 * SharePreCacheHelper.getPassword(this); if(TextUtils.isEmpty(currID)
		 * || TextUtils.isEmpty(currPwd)){ finish(); return; }
		 */

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnPause = true;
		mHandler.removeMessages(MSG_GET_ENV_PARAM);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Slog.w("stop OVBroadcast!!!!");
		// stopService(new Intent(this, OVBroadcast.class));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	private View getIndicatorView(int iconRes, int textRes) {
		// final int tabLength = 4;
		// int[] iconRes = new int[] { R.drawable.ico_test,
		// R.drawable.ico_found,
		// R.drawable.ico_circle, R.drawable.ico_mine };
		// int[] textRes = new int[] { R.string.main_test, R.string.main_found,
		// R.string.main_circle, R.string.main_mine };
		// View[] indicators = new View[tabLength];

		LayoutInflater inflater = getLayoutInflater();

		// for (int i = 0; i < indicators.length; i++) {
		View indicator = inflater.inflate(R.layout.layout_tabs_indicator, null);
		ImageView icon = (ImageView) indicator.findViewById(android.R.id.icon1);
		TextView text = (TextView) indicator.findViewById(android.R.id.text1);
		icon.setImageResource(iconRes);
		text.setText(textRes);

		// indicators[i] = indicator;
		// }

		return indicator;
	}

	private FragmentInfo[] getFragmentInfos() {
		FragmentInfo[] infos = new FragmentInfo[4];

		infos[0] = new FragmentInfo("enter", R.drawable.btn_topic,
				R.string.main_enter, FragmentTopic.class, null);
		infos[1] = new FragmentInfo("book", R.drawable.btn_test,
				R.string.main_book, FragmentTest.class, null);
		// infos[1] = new FragmentInfo("skintest", R.drawable.btn_test,
		// R.string.main_test, SharePreCacheHelper.getBleConnectStatus(this) ?
		// FragmentTestSkin.class
		// : FragmentConnDevice.class, null);
		infos[2] = new FragmentInfo("topic", R.drawable.btn_found,
				R.string.main_topic, FragmentFound.class, null);
		// infos[2] = new FragmentInfo("found", R.drawable.btn_found,
		// R.string.main_found, FragmentDeveloping.class, null);
		infos[3] = new FragmentInfo("mine", R.drawable.btn_mine,
				R.string.main_mine, FragmentMine.class, null);
//		infos[4] = new FragmentInfo("develop", R.drawable.btn_mine,
//				R.string.main_develop, FragmentDeveloping.class, null);

		// if (Constants.PREVIEW) {
		// Bundle b0 = new Bundle();
		// b0.putString("titleText", getString(R.string.main_topic));
		// infos[0] = new FragmentInfo("topic", R.drawable.btn_topic,
		// R.string.main_topic, FragmentDeveloping.class, b0);
		// Bundle b2 = new Bundle();
		// b2.putString("titleText", getString(R.string.main_found));
		// infos[2] = new FragmentInfo("found", R.drawable.btn_found,
		// R.string.main_found, FragmentDeveloping.class, b2);
		// Bundle b3 = new Bundle();
		// b3.putString("titleText", getString(R.string.me));
		// infos[3] = new FragmentInfo("mine", R.drawable.btn_mine,
		// R.string.main_mine, FragmentDeveloping.class, b3);
		// }
		return infos;
	}

	class FragmentInfo {
		String tag;
		int iconRes;
		int textRes;
		Class<? extends Fragment> clazz;
		Bundle args;

		public FragmentInfo(String tag, int iconRes, int textRes,
				Class<? extends Fragment> clazz, Bundle args) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.textRes = textRes;
			this.clazz = clazz;
			this.args = args;
		}

	}
}
