package com.dilapp.radar.ui.skintest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleDeviceListAdapter;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.IDeviceManagerCallback;

public class ActivityScanBle extends BaseActivity implements OnClickListener,IDeviceManagerCallback {
    //view
//	private LinearLayout btn_back;
//	private TextView tv_center;
	private Button btn_test;
	private ListView listView;
	private ImageView waitprogressbar;
	private AnimationDrawable mSearchWaitAnim;
	private LinearLayout scanFailedLayout;
	private Button btn_reScan;
	private TextView tiltle;
	private TextView bottom_title;
	private TextView center_title;
	private LinearLayout mNotFoundLayout;
	
	private RelativeLayout mLinkWaitCover;
	private ImageView mLinkWaitImg;
	private AnimationDrawable  mLinkWaitAnim;
	private TitleView mTitle;
	
	private AllKfirManager allInfoManager = null;
	
	private BleDeviceListAdapter bleDeviceListAdapter;
	private List<BluetoothDevice> mLeDevices ;
	//message
	private static final int MESSAGE_FIND_DEVICE                     = 0x000001;
	private static final int MESSAGE_DEVICE_CONNECT_SUCCESS          = 0x000002;
	private static final int MESSAGE_DEVICE_CONNECT_OVER_TIME        = 0x000003;
	private static final int MESSAGE_STOP_SCAN                       = 0x000004;
	private static final int MESSAGE_START_SCAN                      = 0x000005;
	private static final int MESSAGE_SCAN_OVVER_TIME                 = 0x000006;
	
	private static long connect_start_time = 0; 
	private static boolean connect_success = false;
	private static boolean scan_success = false;
	
	private static final int CONNECT_OVER_TIME = 10*1000;
	private static final String DEFAULTBLENAME  = BleUtils.BLE_NAME_START; //radar
	private static final int SCAN_OVER_TIME =20*1000;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FIND_DEVICE:
				if(bleDeviceListAdapter == null){
					bleDeviceListAdapter = new BleDeviceListAdapter(ActivityScanBle.this,mLeDevices);
					listView.setAdapter(bleDeviceListAdapter);
				}else{
					bleDeviceListAdapter.notifyDataSetChanged();
				}
				break;
				
			case MESSAGE_DEVICE_CONNECT_SUCCESS:
				waitprogressbar.setVisibility(View.GONE);
				mSearchWaitAnim.stop();
//				Toast.makeText(ActivityScanBle.this, msg.obj+"连接成功", 1).show();
//				SharePreCacheHelper.saveBleConnectStatus(ActivityScanBle.this, true);
//				SharePreCacheHelper.savePairStatus(ActivityScanBle.this, true);
				SharePreCacheHelper.saveBleConnectStatus(ActivityScanBle.this, true);
//				SharePreCacheHelper.saveBleMacAddress(ActivityScanBle.this, deviceAddress);
				boolean needConfirm = false;
				if(SharePreCacheHelper.getPairStatus(ActivityScanBle.this)){
					Slog.e("ble has paired and no need check!");
//					String savedAddress = SharePreCacheHelper.getBleMacAddress(ActivityScanBle.this);
//					if(savedAddress == null || !savedAddress.equals(deviceAddress)){
//						SharePreCacheHelper.saveBleMacAddress(ActivityScanBle.this, deviceAddress);
//						SharePreCacheHelper.savePairStatus(ActivityScanBle.this, false);
//						needConfirm = true;
//					}
				}else{
					SharePreCacheHelper.saveBleMacAddress(ActivityScanBle.this, deviceAddress);
					needConfirm = true;
				}
				if(needConfirm){
					Intent intent  = new Intent(ActivityScanBle.this,ActivityDeviceConfirm.class);
				    startActivity(intent);
				}
				finish();
				break;
			case MESSAGE_DEVICE_CONNECT_OVER_TIME:     //连接超时
				if(!connect_success){
					//
//					Toast.makeText(ActivityScanBle.this,"连接失败,请检查设备", Toast.LENGTH_SHORT).show();
					if(!SharePreCacheHelper.getPairStatus(ActivityScanBle.this)){
						allInfoManager.disConnectBleDevice();
					}
					mLinkWaitCover.setVisibility(View.GONE);
					mLinkWaitAnim.stop();
					showBottom(true);
				}
				
				break;
			case MESSAGE_STOP_SCAN:
				stopScan();
				showBottom(true);
				break;
				
			case MESSAGE_SCAN_OVVER_TIME:    //
				//TODO
				if(!scan_success){
				 handler.removeMessages(MESSAGE_SCAN_OVVER_TIME);
//				 center_title.setVisibility(View.VISIBLE);
				 
				}
				 stopScan();
				 showBottom(true);
				break;
			case MESSAGE_START_SCAN:
				waitprogressbar.setVisibility(View.VISIBLE);	
				mSearchWaitAnim.start();
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_ble);

		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);

        mTitle = new TitleView(this, findViewById(R.id.titleLayput));
        mTitle.setCenterText(R.string.bluetooth, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));
//        mTitle.setRightIcon(R.drawable.btn_share, this);
        
		findView();
		
		init();
		
		allInfoManager.setDeviceMagagerCallback(this);
		startScan();
		
	}
	String deviceAddress = null;
	private void init() {
		allInfoManager = AllKfirManager.getInstance(this);
		mLeDevices = new ArrayList<BluetoothDevice>();
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				waitprogressbar.setVisibility(View.VISIBLE);
				mSearchWaitAnim.start();
                connect_success = false;
                deviceAddress =  mLeDevices.get(position).getAddress();
				allInfoManager.connectBleDevice(deviceAddress);
				stopScan();
//				SharePreCacheHelper.saveBleMacAddress(ActivityScanBle.this, "ovt_default_ap");
				mLinkWaitCover.setVisibility(View.VISIBLE);
				mLinkWaitAnim.start();
				showBottom(false);
				handler.removeMessages(MESSAGE_DEVICE_CONNECT_OVER_TIME);
				handler.sendEmptyMessageDelayed(MESSAGE_DEVICE_CONNECT_OVER_TIME, CONNECT_OVER_TIME);
			
			}
		});
		
		
	}

	private void findView() {

//		btn_back = (LinearLayout) this.findViewById(R.id.title_left);
//		btn_back.setOnClickListener(this);
//		tv_center = (TextView) this.findViewById(R.id.title_center);
//		tv_center.setText(getString(R.string.bluetooth));
		
		btn_test =  (Button) this.findViewById(R.id.test);
		btn_test.setOnClickListener(this);
		listView = (ListView) this.findViewById(R.id.listview);
		waitprogressbar = (ImageView) this.findViewById(R.id.waitprogressbar);
		mSearchWaitAnim = (AnimationDrawable) waitprogressbar.getDrawable();
		scanFailedLayout = (LinearLayout) this.findViewById(R.id.scanfailedlayout);
		btn_reScan = (Button) this.findViewById(R.id.rescan);
		btn_reScan.setOnClickListener(this);
		tiltle  = (TextView) this.findViewById(R.id.tiltle);
		bottom_title = (TextView) this.findViewById(R.id.bottom_title);
		mNotFoundLayout = (LinearLayout) this.findViewById(R.id.not_find_layout);
		bottom_title.setOnClickListener(this);
		center_title  =  (TextView) this.findViewById(R.id.center_title);
		
		mLinkWaitCover = (RelativeLayout) this.findViewById(R.id.link_wait_cover);
		mLinkWaitCover.setOnClickListener(this);
		mLinkWaitCover.setVisibility(View.GONE);
		mLinkWaitImg = (ImageView) this.findViewById(R.id.link_wait);
		mLinkWaitImg.setImageResource(R.anim.ble_connect_waiting);
		mLinkWaitAnim = (AnimationDrawable) mLinkWaitImg.getDrawable();
	}
	
	private void startScan(){
//		 scanFailedLayout.setVisibility(View.GONE);
//		 bottom_title.setVisibility(View.GONE);
		mNotFoundLayout.setVisibility(View.GONE);
//		 btn_reScan.setVisibility(View.GONE);
		 showBottom(false);
		 
		 if(bleDeviceListAdapter != null){
			 mLeDevices.clear();
			 bleDeviceListAdapter.notifyDataSetChanged();
		 }
		 
		 tiltle.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
//			    allInfoManager.openBle();
			    
				scan();
			}
		}).start();
	}
	
   private void scan(){
	   allInfoManager.startScanBleDevice();
//	   if(allInfoManager.bleIsOpen()){
//		   Log.i("hj", "bleIsOpen scan");
//		   allInfoManager.scanBleDevice();
//	   }else{
//		   allInfoManager.setsStartScan(true);
//	   }
	  // allInfoManager.scanBleDevice();
	   handler.removeMessages(MESSAGE_START_SCAN);
	   handler.sendEmptyMessage(MESSAGE_START_SCAN);
	   
	   handler.removeMessages(MESSAGE_SCAN_OVVER_TIME);
	   handler.sendEmptyMessageDelayed(MESSAGE_SCAN_OVVER_TIME,SCAN_OVER_TIME);
   }
   
   
   private void stopScan(){
	   handler.removeMessages(MESSAGE_SCAN_OVVER_TIME);
//	    scanFailedLayout.setVisibility(View.VISIBLE);
//		bottom_title.setVisibility(View.VISIBLE);
//		btn_reScan.setVisibility(View.VISIBLE);
		allInfoManager.stopScanBleDevice();
//		allInfoManager.setsStartScan(false);
		waitprogressbar.setVisibility(View.GONE);
		mSearchWaitAnim.stop();
   }
   
   private void showBottom(boolean show){
	   if(show){
		   scanFailedLayout.setVisibility(View.VISIBLE);
		   mNotFoundLayout.setVisibility(View.VISIBLE);
			btn_reScan.setVisibility(View.VISIBLE);
	   }else{
		   scanFailedLayout.setVisibility(View.GONE);
		   mNotFoundLayout.setVisibility(View.GONE);
		   btn_reScan.setVisibility(View.GONE);
	   }
   }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			 finish();
		     //overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			break;
			
		case R.id.test:
			SharePreCacheHelper.saveBleConnectStatus(ActivityScanBle.this, true);
			SharePreCacheHelper.savePairStatus(ActivityScanBle.this, true);
			 overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			finish();
			break;
		case R.id.rescan:
			
			startScan();
			
			break;
		case R.id.bottom_title:
			Intent intent  = new Intent(this,HelpActivity.class);
			intent.putExtra("infoId",0);
			startActivity(intent);
			

		default:
			break;
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			  finish();
			//  overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			break;

		default:
			break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		allInfoManager.setDeviceMagagerCallback(this);
//		startScan();
	}

	@Override
	protected void onPause() {
		//allInfoManager.stopScanBleDevice();
		super.onPause();
//		stopScan();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		stopScan();
		showBottom(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(allInfoManager.getDeviceMagagerCallback() == this){
			allInfoManager.setDeviceMagagerCallback(null);
		}
	}

	
   /**********************************回调**********************************/
	@Override
	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
		Log.i("hj", "ui uiDeviceFound device :"+device.getName());
		if(!mLeDevices.contains(device) && device.getName() != null && device.getName().startsWith(DEFAULTBLENAME)){
			mLeDevices.add(device);
			scan_success = true;
			Log.i("hj", "found success");
			handler.removeMessages(MESSAGE_FIND_DEVICE);
			handler.sendEmptyMessage(MESSAGE_FIND_DEVICE);
			tiltle.setVisibility(View.VISIBLE);
			
		}
	
		
		
	}

	@Override
	public void deviceConnectStatus(DeviceConnectStatus deviceConnectStatus,
			BluetoothDevice device) {
		
		if(deviceConnectStatus.equals(DeviceConnectStatus.success)){
			if(deviceAddress != null && deviceAddress.equals(device.getAddress())){
				Message msg = new Message();
				msg.obj = device.getName();
				msg.what = MESSAGE_DEVICE_CONNECT_SUCCESS;
				handler.sendMessage(msg);
				connect_success = true;
				handler.removeMessages(MESSAGE_DEVICE_CONNECT_OVER_TIME);
				mLinkWaitCover.setVisibility(View.GONE);
				mLinkWaitAnim.stop();
			}else{
				Slog.e("It is not the last ble connect and ignore  last : "+deviceAddress+" this : "+device.getAddress());
			}
		}
		
		
		if(deviceConnectStatus.equals(DeviceConnectStatus.failed)){
			//Toast.makeText(ActivityScanBle.this, "连接失败了!!", 0).show();
			if(deviceAddress != null && deviceAddress.equals(device.getAddress())){
//				Toast.makeText(ActivityScanBle.this, "连接 : "+device.getName()+" 失败了!!", Toast.LENGTH_SHORT).show();
				if(!SharePreCacheHelper.getPairStatus(this)){
					allInfoManager.disConnectBleDevice();
				}
			}
		}
		
		
	}

	@Override
	public void deviceConfirmed(BluetoothDevice device, boolean isconfirm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceLightStatus(int linkstatus, int powerstatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceWifiStatus(int mode, String ssid, String ip, int error) {
		// TODO Auto-generated method stub
		
	}
	
/*****************************回调结束**************************************/	
	

}
