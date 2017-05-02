package com.dilapp.radar.ui;

import org.apache.commons.net.ftp.parser.MLSxEntryParser;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.Login;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.Login.LoginReq;
import com.dilapp.radar.domain.Login.LoginResp;
import com.dilapp.radar.location.LocationManager;
import com.dilapp.radar.util.EMChatUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class WelcomePre extends BaseActivity{
	
	private RelativeLayout mCover;
	private boolean isOnPause = false;
	
	private static final long MIN_WAIT_TIME = 800;
	private long mStartTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_pre_layout);
		mCover = (RelativeLayout) findViewById(R.id.welcome_cover);
		
//		Intent intent = new Intent(this, WelcomeLogin.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//		startActivity(intent);
		
//		finish();
//		checkStatus();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isOnPause = false;
		LocationManager.getInstance(this).start();
		AllKfirManager.getInstance(this).startSkinBle(true);
		
//		AllKfirManager.getInstance(this).startGetEnvParams();
		checkStatus();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnPause = true;
		mHandler.removeMessages(MSG_TABS);
	}
	
	private void checkStatus(){
		String mCurrID = SharePreCacheHelper.getUserName(this);
		String mCurrPwd = SharePreCacheHelper.getPassword(this);
		if(TextUtils.isEmpty(mCurrID) || TextUtils.isEmpty(mCurrPwd)){
			mCover.setVisibility(View.GONE);
			Intent intent = new Intent(this, WelcomeLogin.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
			finish();
		}else{
			mStartTime = System.currentTimeMillis();
			mCover.setVisibility(View.VISIBLE);
			//startLogin();
			mHandler.removeMessages(MSG_TABS);
			mHandler.sendEmptyMessageDelayed(MSG_TABS, 5000); //时间需加长，3秒在移动网络下有时也失败
		}
	}

	private static final int MSG_TABS = 1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(!isOnPause){
				Intent intent = new Intent(WelcomePre.this, ActivityTabs.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
				finish();
			}
		}
		
	};
	
	private void startLogin() {
        Login l = ReqFactory.buildInterface(getApplicationContext(),
                Login.class);
        String mUserID = SharePreCacheHelper.getUserName(this);
        String mCurrPwd = SharePreCacheHelper.getPassword(this);
        LoginReq bean = new LoginReq();
        bean.setUsername(mUserID);
        bean.setPwd(mCurrPwd);
        BaseCall<LoginResp> node = new BaseCall<Login.LoginResp>(){

			@Override
			public void call(LoginResp resp) {
				// TODO Auto-generated method stub
				if(resp.isRequestSuccess()){
					
				}else{
					Slog.e("Logian Failed !!! : "+resp.getMessage());
				}
				mHandler.removeMessages(MSG_TABS);
				long time = MIN_WAIT_TIME - (System.currentTimeMillis() - mStartTime);
				time = time < 0 ? 1 : time;
				mHandler.sendEmptyMessageDelayed(MSG_TABS, time);
//				mHandler.sendEmptyMessage(MSG_TABS);
			}
        		
        };
        addCallback(node);
        l.loginAsync(bean, node);
    }

}
