package com.dilapp.radar.ui.skintest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.IDeviceManagerCallback;

public class ActivityDeviceConfirm extends Activity implements OnClickListener,
		IDeviceManagerCallback {
	// ui
	// private LinearLayout btn_back;
	private Context context;
	private TextView btn_noanswer;
	private ImageView img_body;
	private ImageView finish_imageView;
	private ImageView round_imageView;
	private TitleView mTitle;
	// private TextView tv_center;

	private AnimationDrawable confimAnimation;
	private AllKfirManager allInfoManager;
	private Animation scaleAnimation;
	private Animation scaleAnimation2;
	// private static final boolean DEBUG = false;
	private boolean isFinished = false;

	private static final int MSG_START_CONFIRM_ANIM = 1;
	private static final int MSG_CONFIRM_TIME_OUT = 2;
	private static final int MSG_CONFIRM_OK = 3;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_START_CONFIRM_ANIM:
				if (confimAnimation == null) {
					confimAnimation = (AnimationDrawable) img_body
							.getBackground();
				}
				confimAnimation.start();
				break;
			case MSG_CONFIRM_TIME_OUT:
				if (BleUtils.BLE_DEBUG) {
					SharePreCacheHelper.savePairStatus(
							ActivityDeviceConfirm.this, true);
				}
				allInfoManager.endBleConfirm();
				if (!isFinished) {
					checkWifiStaus();
				}
				break;
			case MSG_CONFIRM_OK:
				if (!isFinished) {
					checkWifiStaus();
				}
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_confirm);
		ZBackgroundHelper.setBackgroundForActivity(this,
				ZBackgroundHelper.TYPE_BLACK_BLUR);
		isFinished = false;
		initView();
		initEvent();

		init();
		img_body.setBackgroundResource(R.anim.device_confirm_anim);
		confimAnimation = (AnimationDrawable) img_body.getBackground();
		mHandler.sendEmptyMessageDelayed(MSG_START_CONFIRM_ANIM, 800);

		long timeout = BleUtils.BLE_DEBUG ? 1000 : 45 * 1000;
		mHandler.sendEmptyMessageDelayed(MSG_CONFIRM_TIME_OUT, timeout);
	}

	private void init() {
		allInfoManager = AllKfirManager.getInstance(this);
		allInfoManager.setDeviceMagagerCallback(this);
		allInfoManager.startBleConfirm();
	}

	private void initEvent() {
		// btn_back.setOnClickListener(this);
		btn_noanswer.setOnClickListener(this);

	}

	private void initView() {
		context = this;
		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.activity_deviceConfirm_center_title, null);
		mTitle.setLeftIcon(R.drawable.btn_back_white, this);
		mTitle.setBackgroundColor(getResources().getColor(
				R.color.test_title_color));
		// mTitle.setRightIcon(R.drawable.btn_share, this);
		// btn_back = (LinearLayout) this.findViewById(R.id.title_left);
		btn_noanswer = (TextView) this.findViewById(R.id.noanswer);
		img_body = (ImageView) this.findViewById(R.id.bodyImage);
		finish_imageView = (ImageView) findViewById(R.id.finish_imageView);
		round_imageView = (ImageView) findViewById(R.id.round_imageView);
		// tv_center = (TextView) this.findViewById(R.id.title_center);
		// tv_center.setText(getString(R.string.activity_deviceConfirm_center_title));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			isFinished = true;
			mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
			mHandler.removeMessages(MSG_START_CONFIRM_ANIM);
			mHandler.removeMessages(MSG_CONFIRM_OK);
			finish();
			break;
		case R.id.noanswer:
			Intent intent = new Intent(this, HelpActivity.class);
			intent.putExtra("infoId", 1);
			startActivity(intent);
			// finish();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isFinished = true;
			mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
			mHandler.removeMessages(MSG_START_CONFIRM_ANIM);
			mHandler.removeMessages(MSG_CONFIRM_OK);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void checkWifiStaus() {
		if (SharePreCacheHelper.getPairStatus(this)
				&& allInfoManager.needCheckWifiPassword()) {
			Intent intent = new Intent(this, ActivityWifiPassword.class);
			intent.putExtra("from_device_confirm", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		} else if (SharePreCacheHelper.getPairStatus(this)) {
			Intent intent = new Intent(this, ActivityBindConfirm.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);
		}
		if (!SharePreCacheHelper.getPairStatus(this)) {
			allInfoManager.disConnectBleDevice();
		}
		mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
		isFinished = true;
		finish();
	}

	// private LocalWifi checkSSid() {
	// String ssid = allInfoManager.getSSID();
	// String mac = allInfoManager.getMacAddress();
	// return SharePreCacheHelper.checkSSid(this, mac, ssid);
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		allInfoManager.endBleConfirm();
		if (allInfoManager.getDeviceMagagerCallback() == this) {
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
		String targetAddress = SharePreCacheHelper.getBleMacAddress(this);
		Slog.d("deviceConnectStatus : target : " + targetAddress + " curr : "
				+ device.getAddress() + " " + deviceConnectStatus);

		if (deviceConnectStatus == DeviceConnectStatus.failed
				&& targetAddress != null
				&& targetAddress.equals(device.getAddress())) {
			Slog.e("target has disconnected !");
			isFinished = true;
			mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
			if (!SharePreCacheHelper.getPairStatus(this)) {
				allInfoManager.disConnectBleDevice();
			}
			finish();
		}
	}

	@Override
	public void deviceConfirmed(BluetoothDevice device, boolean isconfirm) {
		// TODO Auto-generated method stub
		String targetAddress = SharePreCacheHelper.getBleMacAddress(this);
		Slog.d("deviceConfirmed : target : " + targetAddress + " curr : "
				+ device.getAddress() + " " + isconfirm);
		if (isconfirm && targetAddress != null
				&& targetAddress.equals(device.getAddress())) {
			SharePreCacheHelper.savePairStatus(this, true);
			SharePreCacheHelper.saveBleName(this, device.getName());
			mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
			mHandler.removeMessages(MSG_START_CONFIRM_ANIM);
			mHandler.sendEmptyMessageDelayed(MSG_CONFIRM_OK, 800);
			// img_body.setBackgroundResource(R.drawable.device_connect_ok);
			finish_imageView.setImageResource(R.drawable.device_connect_ok2);
			round_imageView.setImageResource(R.drawable.device_connect_ok3);
			scaleAnimation = AnimationUtils.loadAnimation(context,
					R.anim.scale_anim);
			scaleAnimation2 = AnimationUtils.loadAnimation(context,
					R.anim.scale_anim2);
			finish_imageView.startAnimation(scaleAnimation);
			round_imageView.startAnimation(scaleAnimation2);
			if (confimAnimation != null) {
				confimAnimation.stop();
				confimAnimation = null;
			}
			allInfoManager.endBleConfirm();
			// checkWifiStaus();
		} else if (!isconfirm && targetAddress != null
				&& targetAddress.equals(device.getAddress())) {
			isFinished = true;
			if (!SharePreCacheHelper.getPairStatus(this)) {
				allInfoManager.disConnectBleDevice();
			}
			mHandler.removeMessages(MSG_CONFIRM_TIME_OUT);
			finish();
		}
	}

	@Override
	public void onDeviceLightStatus(int linkstatus, int powerstatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeviceWifiStatus(int mode, String ssid, String ip, int error) {
		// TODO Auto-generated method stub

	}

}
