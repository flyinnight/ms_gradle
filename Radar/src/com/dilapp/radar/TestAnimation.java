package com.dilapp.radar;

import java.io.File;

import com.dilapp.radar.ble.BleDeviceSearchAnimationFrameLayout;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.wifi.CameraKfirHelper;
import com.dilapp.radar.wifi.CaptureInterface;
import com.dilapp.radar.wifi.IWifiHelperUiCallback;
import com.dilapp.radar.wifi.IWifiKfirHelperCallback;
import com.dilapp.radar.wifi.WifiKfirHelper;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestAnimation extends Activity implements IWifiKfirHelperCallback,CaptureInterface {

	private Button startAnimation;
	private BleDeviceSearchAnimationFrameLayout animationFrameLayout;
	private Button openwifi;
	private Button openCamera;
	
	private WifiKfirHelper wifiHelper;
	private CameraKfirHelper cameraHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_animation);
		startAnimation = (Button) this.findViewById(R.id.startAnimation);
		animationFrameLayout = (BleDeviceSearchAnimationFrameLayout) this.findViewById(R.id.animationLayout);
		wifiHelper = WifiKfirHelper.getInstance(this);
		cameraHelper = CameraKfirHelper.getInstance(this);
		startAnimation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				animationFrameLayout.startAnimation();
			}
		});
		openwifi = (Button) this.findViewById(R.id.openwifi);
		openwifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				wifiHelper.connectWifiAp();
			}
		});
		
		openCamera = (Button) this.findViewById(R.id.openCamera);
		openCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				cameraHelper.startCamera();
			}
		});
	}
	private int netWorkId = -1;
	private int state = -1;
	
	@Override
	protected void onResume() {
		super.onResume();
		//cameraHelper.startLoadLibraryService(this);
		wifiHelper.openWifi();
		wifiHelper.registerBroadcast();
		wifiHelper.addWifiHelperUiCallback(this);
		
//		state = wifiHelper.getNetworkType(this);
		//getNetWordId();
//		netWorkId = wifiHelper.getNetworkId();
//		cameraHelper.initDevice();
		//cameraHelper.initCameraToPhoto();
		cameraHelper.setCaptureInterfaceCallback(this);
		//Toast.makeText(this, "sd card is " + getSDTotalSize(), 1).show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//wifiHelper.restoredNetState(state,netWorkId);
		wifiHelper.unRegisterBroadcast();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		wifiHelper.removeWifiHelperUiCallback(this);
	}
	

//	@Override
//	public void connectApSuccess(String ip) {
//		
//		/*Intent intent = new Intent(this,ActivityTabs.class);
//		startActivity(intent);
//		finish();*/
//	}

//	@Override
//	public void connectWifiSuccess(String ssid, String ip) {
//		
//	}
//
//	@Override
//	public void wifiDisconnected() {
//		
//	}
//
//	@Override
//	public void apDisConnected() {
//		
//	}

	@Override
	public void wifiDisabled() {
		
	}

	@Override
	public void wifiEnable() {
		
	}

	 private long getSDTotalSize() {  
	        File path = Environment.getExternalStorageDirectory();  
	        StatFs stat = new StatFs(path.getPath());  
	        long blockSize = stat.getBlockSize();  
	        long totalBlocks = stat.getBlockCount();  
	        //return Formatter.formatFileSize(this, blockSize * totalBlocks);  
	        return blockSize * totalBlocks;  
	    }

	@Override
	public void wifiEnableding() {
		
	}

	@Override
	public void onCaptureStatus(int mId, int status) {
		
		Log.i("hj", "------- "+ mId + "--"+status);
		
	}

//	@Override
//	public void deviceConnected() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void deviceDisConnected() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void apConnecting() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void apConnectError() {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onWifiConnectSuccess(String ssid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWifiDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWifiConnecting() {
		// TODO Auto-generated method stub
		
	}  

}
