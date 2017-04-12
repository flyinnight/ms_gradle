package com.dilapp.radar.ui.skintest;

import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleDialogUtil;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.IDeviceManagerCallback;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityDeviceInfo extends Activity implements OnClickListener,IDeviceManagerCallback  {
    //view
	private Button btn_clear_deviceInfo;
	private Button btn_clear_wifipwd;
//	private TextView tv_center;
//	private LinearLayout back;
	private TitleView mTitle;
	
//	private TextView mMode;
//	private TextView mWifiName;
//	private TextView mWifiIp;
//	private TextView mLinkStatus;
//	private TextView mPowerStatus;
//	
//	private TextView mName;
//	private TextView mBleStatus;
	
	private TextView mDeviceName;
	private ImageView mPowerIcon;
	private TextView mPowerStatus;
	private ImageView mBtRightIcon;
	private TextView mBtStatus;
	private TextView mWifiStatus;
	private TextView mPhoneStatus;
	private LinearLayout mBtRightLayout;
	private LinearLayout mWifiRightLayout;
	private ImageView mWifiRightIcon;
	
	private boolean isOnPause = false;
	
	private AllKfirManager allInfoManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_info);
		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
		
		initView();
		allInfoManager = AllKfirManager.getInstance(ActivityDeviceInfo.this);
		mPhoneStatus.setText(new Build().MODEL);
		
	}

	private void initView() {
		View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.activity_device_info_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

		btn_clear_deviceInfo = (Button) this.findViewById(R.id.clearInfo);
		btn_clear_deviceInfo.setOnClickListener(this);
		btn_clear_wifipwd = (Button) this.findViewById(R.id.clearWifipwd);
		btn_clear_wifipwd.setOnClickListener(this);
//		tv_center = (TextView) this.findViewById(R.id.title_center);
//		tv_center.setText(getString(R.string.activity_device_info_title));
//		back = (LinearLayout) this.findViewById(R.id.title_left);
//		back.setOnClickListener(this);
		mDeviceName = (TextView) this.findViewById(R.id.device_name);
		mPowerIcon = (ImageView) this.findViewById(R.id.power_icon);
		mPowerStatus = (TextView) this.findViewById(R.id.power_status);
		
		mBtRightIcon = (ImageView) this.findViewById(R.id.bt_right_icon);
		mBtStatus = (TextView) this.findViewById(R.id.bt_right_status);
		mBtRightLayout = (LinearLayout) this.findViewById(R.id.bt_layout);
		mWifiRightLayout = (LinearLayout) this.findViewById(R.id.wifi_layout);
		mWifiRightIcon = (ImageView) this.findViewById(R.id.wifi_right_icon);
		
		mWifiStatus = (TextView) this.findViewById(R.id.wifi_right_status);
		
		mPhoneStatus = (TextView) this.findViewById(R.id.phone_right_status);
		
		
//		mMode = (TextView) this.findViewById(R.id.mode);
//		mWifiName = (TextView) this.findViewById(R.id.wifiname);
//		mWifiIp = (TextView) this.findViewById(R.id.wifiaddress);
//		mLinkStatus = (TextView) this.findViewById(R.id.link_status);
//		mPowerStatus = (TextView) this.findViewById(R.id.power_status);
		
//		mName = (TextView) this.findViewById(R.id.device_name);
//		mBleStatus = (TextView) this.findViewById(R.id.ble_status);
	
	}

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.clearInfo:
			showClearDialog();
			//解除绑定的时候 连接上的情况(断开连接) TODO
			break;
		case R.id.vg_left:
			finish();
			break;
		case R.id.clearWifipwd:
			Toast.makeText(getApplicationContext(), "执行了一次WI-FI密码清空操作", Toast.LENGTH_SHORT).show();
			SharePreCacheHelper.clearWifiList(this);
			break;
		case R.id.bt_layout:
			Intent btintent  = new Intent(this,HelpActivity.class);
			btintent.putExtra("infoId",0);
			startActivity(btintent);
			break;
		case R.id.wifi_layout:
			Intent wifiintent  = new Intent(this,HelpActivity.class);
			wifiintent.putExtra("infoId",2);
			startActivity(wifiintent);
			break;

		default:
			break;
		}
	}

	
	private void showClearDialog() {
		OnClickListener clearListerner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharePreCacheHelper.savePairStatus(ActivityDeviceInfo.this,	false);
					
				// String macAddress =
				// SharePreCacheHelper.getBleMacAddress(ActivityDeviceInfo.this);
				allInfoManager.endSkinTest();
				allInfoManager.disConnectBleDevice();
				allInfoManager.removeAPSSID();
				SharePreCacheHelper.clearBleMacAddress(ActivityDeviceInfo.this);
				SharePreCacheHelper.saveBleConnectStatus(ActivityDeviceInfo.this, false);
				SharePreCacheHelper.saveBleName(ActivityDeviceInfo.this, "");
//				SharePreCacheHelper.clearDefaultSSID(ActivityDeviceInfo.this);
				BleDialogUtil.dismissClearDeviceDialog();
				ActivityDeviceInfo.this.finish();
			}
		};
		OnClickListener cancelListerner = new OnClickListener() {

			@Override
			public void onClick(View v) {

				BleDialogUtil.dismissClearDeviceDialog();
			}
		};

		BleDialogUtil.showclearDeviceInfo(ActivityDeviceInfo.this,clearListerner, cancelListerner);
				
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isOnPause = false;
		allInfoManager.setDeviceMagagerCallback(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isOnPause = true;
		if(allInfoManager.getDeviceMagagerCallback() == this){
			allInfoManager.setDeviceMagagerCallback(null);
		}
	}

	@Override
	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceConnectStatus(DeviceConnectStatus deviceConnectStatus,
			BluetoothDevice device) {
		// TODO Auto-generated method stub
		Slog.w("deviceConnectStatus : "+deviceConnectStatus.toString()+" "+isOnPause);
		if(deviceConnectStatus != DeviceConnectStatus.success && !isOnPause){
			this.onDeviceLightStatus(0, 0);
			this.onDeviceWifiStatus(0, null, null, 0);
		}
	}

	@Override
	public void deviceConfirmed(BluetoothDevice device, boolean isconfirm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceLightStatus(int linkstatus, int powerstatus) {
		// TODO Auto-generated method stub
//		String linkStatus = getString(R.string.link_status) + " : ";
//		String powerStatus = getString(R.string.power_status)+" : ";
//		switch(linkstatus){
//		case BleUtils.DEVICE_LINK_IDLE:
//			linkStatus += "IDLE";
//			break;
//		case BleUtils.DEVICE_LINK_READY:
//			linkStatus += "READY";
//			break;
//		case BleUtils.DEVICE_LINK_CONNECTING:
//			linkStatus += "CONNECTION";
//			break;
//		case BleUtils.DEVICE_LINK_CONNECTED:
//			linkStatus += "CONNECTED";
//			break;
//		default:
//			linkStatus +="UNKNOWN";
//			break;
//		}
		
//		switch(powerstatus){
//		case BleUtils.DEVICE_POWER_IDLE:
//			powerStatus += "IDLE";
//			break;
//		case BleUtils.DEVICE_POWER_LOWP:
//			powerStatus += "LOW_POWER";
//			break;
//		case BleUtils.DEVICE_POWER_CHARGE:
//			powerStatus += "CAHRGING";
//			break;
//		default:
//			linkStatus +="UNKNOWN";
//			break;
//		}
//		mLinkStatus.setText(linkStatus);
		boolean isLinked = allInfoManager.isBleConnected();//SharePreCacheHelper.getBleConnectStatus(this);
		int fShare = SharePreCacheHelper.getbatteryStatus(this);
		int fDevice = allInfoManager.getDevicePowerStatus();
		mPowerIcon.setVisibility(View.VISIBLE);
		if(!isLinked){
			mPowerIcon.setVisibility(View.GONE);
			mPowerStatus.setText(R.string.device_ofline);
		}else if(fDevice == 1){
			mPowerIcon.setImageResource(R.drawable.device_low_power);
			mPowerStatus.setText(R.string.activity_device_info_power_low);
		}else if(fDevice == 2){
			mPowerIcon.setImageResource(R.drawable.device_charging);
			mPowerStatus.setText(R.string.activity_device_info_power_charging);
		}else{
			switch(fShare){
			case 0:
				
				int level = allInfoManager.getBatteryLevel();
				mPowerStatus.setText(""+level+"%");
				if(level == 100){
					mPowerIcon.setImageResource(R.drawable.device_full_power);
				}else if(level < 0){
					mPowerIcon.setVisibility(View.GONE);
					mPowerStatus.setText(R.string.get_battery_level);
				}else{
					mPowerIcon.setImageResource(R.drawable.device_power);
				}
//				mPowerStatus.setText(R.string.activity_device_info_power_normal);
				break;
			case 1:
				mPowerIcon.setImageResource(R.drawable.device_low_power);
				mPowerStatus.setText(R.string.activity_device_info_power_warning);
				break;
			case 2:
				mPowerIcon.setImageResource(R.drawable.device_low_power);
				mPowerStatus.setText(R.string.activity_device_info_power_low);
				break;
			}
		}
//		mPowerStatus.setText(powerStatus);
		
		String mSName = SharePreCacheHelper.getBleName(this);
		if(!TextUtils.isEmpty(mSName)){
			mDeviceName.setText(mSName);
		}else{
			mDeviceName.setText(R.string.unknown);
		}
		
		if(isLinked){
			mBtRightLayout.setOnClickListener(null);
			mBtRightIcon.setVisibility(View.GONE);
			mBtStatus.setText(R.string.activity_device_info_right_linked);
		}else{
			mBtRightLayout.setOnClickListener(this);
			mBtRightIcon.setVisibility(View.VISIBLE);
			mBtStatus.setText(R.string.activity_device_info_right_unlink);
		}
		
	}

	@Override
	public void onDeviceWifiStatus(int mode, String ssid, String ip, int error) {
		// TODO Auto-generated method stub
//		String smode = "Mode : ";
		
		
		boolean isConnected = allInfoManager.isWifiReadyForTrans();
		mWifiRightLayout.setOnClickListener(null);
		mWifiRightIcon.setVisibility(View.GONE);
		if(isConnected){
			mWifiStatus.setText(getString(R.string.activity_device_info_right_link_to, ssid));
		}else{
			switch(mode){
			case BleUtils.DEVICE_MODE_IDLE:
				mWifiRightLayout.setOnClickListener(this);
				mWifiRightIcon.setVisibility(View.VISIBLE);
				mWifiStatus.setText(R.string.activity_device_info_right_unlink);
				break;
			case BleUtils.DEVICE_MODE_STA:
				mWifiStatus.setText(getString(R.string.activity_device_info_right_trylink_to, ssid)+"(STA)");
				break;
			case BleUtils.DEVICE_MODE_AP:
				mWifiStatus.setText(getString(R.string.activity_device_info_right_trylink_to, ssid)+"(AP)");
				break;
			default:
				mWifiRightLayout.setOnClickListener(this);
				mWifiRightIcon.setVisibility(View.VISIBLE);
				mWifiStatus.setText(R.string.activity_device_info_right_unlink);
				break;
			}
		}
//		mMode.setText(smode);
//		mWifiName.setText("WIFI SSID : "+ssid);
//		mWifiIp.setText("WIFI IP : "+ip);
	}

}
