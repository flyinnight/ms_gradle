package com.dilapp.radar.ui.skintest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.IPasswordCallback;
import com.dilapp.radar.wifi.LocalWifi;
import com.dilapp.radar.wifi.WifiKfirHelper;

public class ActivityWifiPassword extends BaseActivity implements OnClickListener {
	
	private TextView tx_ssid;
//	private LinearLayout btn_back;
	private Button tx_join;
	private EditText et_input;
	private WifiKfirHelper wifiHelper;
//	private TextView tv_center;
	private TextView tv_info;
	
	private IPasswordCallback iPasswordCallback;
	private AllKfirManager allInfoManager;
	private TitleView mTitle;
	
	private TextView mInputNext;
	private boolean isFromConfirm = false;
	private boolean isOnPause = true;
	
	
	private static final int MSG_CHECK_TIME = 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_CHECK_TIME:
				if(allInfoManager != null && !isOnPause){
					allInfoManager.startCheckTime();
					mHandler.removeMessages(MSG_CHECK_TIME);
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIME, 5*1000);
				}
				break;
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_password);

		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
		Intent intent = getIntent();
		if(intent != null){
			isFromConfirm = intent.getBooleanExtra("from_device_confirm", false);
		}
		initView();
		
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
		isOnPause = false;
		mHandler.removeMessages(MSG_CHECK_TIME);
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIME, 1000);
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnPause = true;
		mHandler.removeMessages(MSG_CHECK_TIME);
	}



	private void init() {
		wifiHelper = WifiKfirHelper.getInstance(this);
		String currSSID = wifiHelper.getCurrSSID();
		if(currSSID != null){
			tx_ssid.setText("   "+currSSID.replace("\"", ""));
		}else{
			Slog.e("Can not find currSSID and finish!!!!!!");
			finish();
			return;
		}
		allInfoManager = AllKfirManager.getInstance(ActivityWifiPassword.this);
		iPasswordCallback = allInfoManager;
		
		Intent  intent = getIntent();
		if(intent != null && intent.getBooleanExtra("error", false)){   //wifi password error
			tv_info.setText(getString(R.string.activity_wifipassword_error));
		}
	}

	private void initView() {
		View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.activity_wifipassword_center_title, null);
        if(!isFromConfirm){
        		mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        }
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));
		tx_ssid = (TextView) this.findViewById(R.id.ssid);
//		btn_back = (LinearLayout) this.findViewById(R.id.title_left);
		tx_join = (Button) this.findViewById(R.id.confim);
		et_input = (EditText) this.findViewById(R.id.password);
		et_input.addTextChangedListener(textWatcher);
		
//		btn_back.setOnClickListener(this);
		tx_join.setOnClickListener(this);
		tx_join.setClickable(false);
		tx_join.setAlpha(0.5f);
		
//		tv_center = (TextView) this.findViewById(R.id.title_center);
//		tv_center.setText(getString(R.string.activity_wifipassword_center_title));
		
		tv_info = (TextView) this.findViewById(R.id.infotitle);
		
		mInputNext = (TextView) this.findViewById(R.id.input_next);
		mInputNext.setOnClickListener(this);
	}
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent != null){
			isFromConfirm = intent.getBooleanExtra("from_device_confirm", false);
		}
//		this.setIntent(intent);
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			allInfoManager.ignoreSTAAction();
		}
		return super.onKeyUp(keyCode, event);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			allInfoManager.ignoreSTAAction();
			finish();
			break;
		case R.id.confim:
			String password = et_input.getText().toString().trim();
			//TODO 发送SSID 和 pwd;
		   if(!TextUtils.isEmpty(password)){
			   savePwd( password);
			   if(iPasswordCallback != null){
				   iPasswordCallback.sendSSidAndPassword(wifiHelper.getCurrSSID(), password);
			   }
			  //等待设备的反馈状态信息 TODO
			   if(isFromConfirm){
				   Intent intent = new Intent(ActivityWifiPassword.this, ActivityBindConfirm.class);
				   intent.putExtra("need_back", true);
				   intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				   startActivity(intent);
			   }else{
				   finish();
			   }
		    }
		    break;
		case R.id.input_next:
			if(isFromConfirm){
				   Intent intent = new Intent(ActivityWifiPassword.this, ActivityBindConfirm.class);
				   intent.putExtra("need_back", true);
				   intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				   startActivity(intent);
			   }else{
				   allInfoManager.ignoreSTAAction();
				   finish();
			   }
			break;
		default:
			break;
		}
		
	}

	private void savePwd(String password) {
		LocalWifi localWifi = new LocalWifi();
		localWifi.wifiName = wifiHelper.getCurrSSID();
		localWifi.wifiMac = wifiHelper.getCurrBSSID();
		localWifi.wifiPassword = password;
		if(localWifi.wifiName != null && localWifi.wifiMac != null){
			Slog.d("addLocalWifiList : "+localWifi.wifiName+"  "+localWifi.wifiMac);
			SharePreCacheHelper.addLocalWifiList(this,localWifi);
		}else{
			Slog.e("Error save Local wifi : "+localWifi.wifiName+"  "+localWifi.wifiMac);
		}
		
		
	}
	  private final int lenggth = 8;
	 private TextWatcher textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Editable editable = et_input.getText();  
		        int len = editable.length();  
		          
		        if(len >= lenggth)  
		        {  
		        	tx_join.setClickable(true);
		        	tx_join.setAlpha(1f);
		        }else{
		        	tx_join.setClickable(false);
		        	tx_join.setAlpha(0.5f);
		        }
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				
			}
		};

		
	protected void onDestroy() {
		wifiHelper = null;
		super.onDestroy();
		iPasswordCallback = null;
		allInfoManager = null;
	};
   

}
