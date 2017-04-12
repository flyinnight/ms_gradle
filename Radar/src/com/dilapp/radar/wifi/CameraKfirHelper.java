package com.dilapp.radar.wifi;

import java.io.File;
import java.util.Collections;
import java.util.Currency;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.util.IPUtils;
import com.dilapp.radar.util.Slog;
import com.ov.omniwificam.OVWIFICamJNI;
import com.ov.omniwificam.Vout;
import com.ov.omniwificam.util.CameraDevInfo;

public class CameraKfirHelper implements NetStatusInterface,CaptureInterface{


	private static CameraKfirHelper mSelf;
	private static final int CAMERA_CHANNEL = 0;
	
	private Context mContext;
	private RadarApplication app;
	private OVWIFICamJNI OVJNI = null;
	private CameraDevInfo fdeviceInfo = null;
	private  CameraDevInfo deviceInfo = null;
	
	private GLSurfaceView mGLSurfaceView;
	private Vout mVout;
	
	private int mCurrNetStatus = 0;
	private long macAddress = -1;
	private boolean hasInWork = false;
	private boolean hasInit = false;
	private boolean hasNativeStartCamera = false;
	private boolean isWifiReady = false;
	private boolean videoNeedOpen = true;
	private boolean hasSetVideoOn = false;
	private boolean hasSetCapture = false;
	private boolean captureNeedOn = false;
	
	private boolean allinfo_done = false;
	
	private boolean stopingCamera = false;
	private long stopingCameraTime = 0;
	
	private long mLastCoolTime = 0;
	private static final long COOL_TIME = 1 * 1000;
	
//	private ICameraHelperCallback iCameraHelperCallback;    // link status
	private CaptureInterface captureInterfaceCallback;   //image callback
	
	private NetStatusInterface netStatusInterfaceCallback;    //camer status
	
//	public enum DeviceStatus{idel,failed,success};
//	private DeviceStatus deviceStatus = DeviceStatus.idel;   //设备的状态

	//API START
	public synchronized static CameraKfirHelper getInstance(Context context){
		if(mSelf == null){
			mSelf = new CameraKfirHelper(context);
		}
		return mSelf;
	}
	
	public void setCaptureInterfaceCallback(
			CaptureInterface captureInterfaceCallback) {
		this.captureInterfaceCallback = captureInterfaceCallback;
		//OVJNI.nativeSetCaptureCallback(this);
		
	}

	public void setNetStatusInterfaceCallback(
			NetStatusInterface netStatusInterfaceCallback) {
		this.netStatusInterfaceCallback = netStatusInterfaceCallback;
//		OVJNI.nativeSetCaptureCallback(this);
	}
	
	public synchronized void startSkinWork(GLSurfaceView glSurfaceView,Vout vout){
		Slog.d("startSkinWork!!!!");
		if(hasInWork){
			if(glSurfaceView != null && glSurfaceView == mGLSurfaceView){
				Slog.e("startSkinWork has called11111!!!");
				return;
			}else if(glSurfaceView != null){
				mGLSurfaceView = glSurfaceView;
				mVout = vout;
				if(hasInit){
					Slog.e("startSkinWork has init and restart!!!!!");
//					exitDevice();
					initViewInternal(false);
					OVJNI.initVout(deviceInfo.GLView, deviceInfo.Vout, CAMERA_CHANNEL);
					startVideo();
					return;
				}
			}else{
				Slog.e("startSkinWork has called22222!!!");
				return;
			}
		}
		hasInWork = true;
		mGLSurfaceView = glSurfaceView;
		mVout = vout;
		isWifiReady = AllKfirManager.getInstance(mContext).isWifiReadyForTrans();
//		mHandler.removeMessages(MESSAGE_START_WORK);
//		mHandler.removeMessages(MESSAGE_END_WORK);
		mHandler.removeMessages(MESSAGE_CHECKCONNECT);
		mHandler.removeMessages(MESSAGE_CLOSE_NET);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_START_WORK, 50);
		if(isWifiReady){
			initDevice();
		}else{
			Slog.e("MESSAGE_START_WORK : wait for wifi ready");
		}
	}
	
	public synchronized void endSkinWork(){
		Slog.e("endSkinwork!!!!");
		hasInWork = false;
		mGLSurfaceView = null;
		mVout = null;
//		mHandler.removeMessages(MESSAGE_START_WORK);
//		mHandler.removeMessages(MESSAGE_END_WORK);
		mHandler.removeMessages(MESSAGE_CHECKCONNECT);
		mHandler.removeMessages(MESSAGE_CLOSE_NET);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_END_WORK, 50);
		exitDevice();
	}
	
	public void notifyWifiReady(boolean ready){
		isWifiReady = ready;
		Slog.d("notifyWifiReady : wifiReady : "+ready+" inWork : "+hasInWork);
		if(hasInWork){
			mHandler.removeMessages(MESSAGE_CHECKCONNECT);
			mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 50);
		}
	}
	
	/**
	 * 
	 * @param value 0 normal 1 PL
	 */
	public void setLedControl(int value){
//		Slog.e("setLedControl start : "+value);
		if(OVJNI != null && mCurrNetStatus == 1){
			OVJNI.nativeSetLEDCtrl(CAMERA_CHANNEL, value);
			Slog.d("setLedControl : "+value);
		}
	}
	
	/**
	 * Call after init Device
	 * @param glSurfaceView
	 * @param mVout
	 * @param context
	 */
	private void initViewInternal(boolean initdefault){
//		if(hasInit){
//			Slog.e("has Init and can not initView");
//		}
		if(deviceInfo.Vout != null){
			deviceInfo.Vout.setJNI(null);
		}
		deviceInfo.Vout = mVout;
		deviceInfo.Vout.setJNI(OVJNI);
		
		//init GLSurfaceView
//		if(deviceInfo.GLView != null){
//			deviceInfo.GLView.setOnTouchListener(null);
//		}
		deviceInfo.GLView = null; 
		deviceInfo.GLView = mGLSurfaceView;
//		deviceInfo.GLView.setOnTouchListener(null);//(new MyOnTouchListener());
		
//		 deviceInfo.GLView.setRenderer(deviceInfo.Vout);
//		 deviceInfo.GLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		deviceInfo.Vout.dispmode = 1;//(deviceInfo.Vout.dispmode + 1); //display mode
//		OVJNI.initVout(deviceInfo.GLView, deviceInfo.Vout, CAMERA_CHANNEL);
		if(initdefault){
			deviceInfo.initDefaultSetting();
		}
	}
	
	public boolean isCameraConnected(){
		boolean result = false;
		if(mCurrNetStatus == 1){
			result = true;
		}
		return result;
	}
	
	public synchronized void startVideo(){
		if(hasInWork && !hasInit){
			initDevice();
		}else if(!hasSetVideoOn){
			if(mCurrNetStatus == 1 && !captureNeedOn){
				Slog.d("start startVideo!!!");
				OVJNI.nativeSetVideo(CAMERA_CHANNEL);
				hasSetVideoOn = true;
			}else{
				mHandler.removeMessages(MESSAGE_RESTART_VIDEO);
				mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_VIDEO, 1000);
			}
		}else if(hasSetCapture){
			if(mCurrNetStatus == 1 && !captureNeedOn){
				Slog.d("start nativeRestoreVideo!!!");
				OVJNI.nativeRestoreVideo(CAMERA_CHANNEL);
				hasSetCapture = false;
				hasSetVideoOn = true;
			}
		}
		videoNeedOpen = true;
	}
	
	public void endVideo(){
		if(mCurrNetStatus == 1 && !captureNeedOn && hasSetVideoOn){
			Slog.d("start endVideo");
			OVJNI.nativeCloseVideo(CAMERA_CHANNEL);
			hasSetVideoOn = false;
			hasSetCapture = false;
		}
		videoNeedOpen = false;
	}
	
	public void startCamera(){
		if(captureNeedOn){
			Slog.e("startCamera has worked ignore!!!");
			return;
		}
		checkFile(Content.RGB_PATH,Content.PL_PATH);
		try {
			OVJNI.nativeCaptureDone(CAMERA_CHANNEL);
			OVJNI.nativeSetCapture(CAMERA_CHANNEL,Content.RGB_PATH,Content.RGB_PATH.length());
			captureNeedOn = true;
			hasSetCapture = true;
			mHandler.removeMessages(MESSAGE_CAPTURE_TIMEOUT);
			mHandler.sendEmptyMessageDelayed(MESSAGE_CAPTURE_TIMEOUT, CAPTURE_TIMER);
		} catch (Throwable e) {
			Slog.i("startCamera Error : ",e);
//			e.printStackTrace();
		}
	}
	
	public void uiStartTimerCloseData( ){
		
    	Slog.i("ui StartTimerClose");
    	mHandler.removeMessages(MESSAGE_CLOSE_NET);
    	mHandler.sendEmptyMessageDelayed(MESSAGE_CLOSE_NET, UI_CLOSE_TIMER);
    	
	}
	
	public void uiEndTimerCloseData( ){
		Slog.i(" ui end TimerClose");
		mHandler.removeMessages(MESSAGE_CLOSE_NET);
		mHandler.sendEmptyMessage(MESSAGE_CLOSE_NET);
	}
	
	public void clearTimerCloseData(){
		mHandler.removeMessages(MESSAGE_CLOSE_NET);
	}
	//API END
	
	
	/*********************private ****************/
	
	private static final int MESSAGE_CHECKCONNECT = 0x1001;
	private static final int MESSAGE_CLOSE_NET = 0x1002;
//	private static final int MESSAGE_START_WORK = 0x1004;
//	private static final int MESSAGE_END_WORK = 0x1005;
	private static final int MESSAGE_CAPTURE_TIMEOUT = 0x1006;
	private static final int MESSAGE_RESTART_VIDEO = 0x1007;
	private static final int MSG_CHANNEL_FAILED_WAIT = 0x1008;
	private static final int MSG_STOP_CAMERA_DONE = 0x1009;
	
	private static final int UI_CLOSE_TIMER = 20*1000;
	private static final int CAPTURE_TIMER = 6 * 1000;
	
//	private static final int MAX_RECONNECT_COUNT = 8; 
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MESSAGE_CHECKCONNECT:
				if(hasInWork && isWifiReady){
					if(!hasInit){
						initDevice();
					}else{
						if (OVJNI.nativelinklost2(CAMERA_CHANNEL) == 1 && hasNativeStartCamera == true){
							Slog.d("check connect nativeExit2 33333");
							handleNativeExit();
//							OVJNI.nativeExit2(CAMERA_CHANNEL);
//							hasInit = false;
							hasNativeStartCamera = false;
							initDevice();
						}else{
							if(hasNativeStartCamera){
								updateCamSta();
							}
							mHandler.removeMessages(MESSAGE_CHECKCONNECT);
							mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
						}
					}
				}else{
					Slog.w("Ignore MESSAGE_CHECKCONNECT : work: "+hasInWork+" wifi: "+isWifiReady);
				}
				break;
			case MESSAGE_CLOSE_NET:
				endSkinWork();
//				hasInWork = false;
				allinfo_done = false;
//				exitDevice();
				break;
//			case MESSAGE_START_WORK:
//				if(isWifiReady){
//					initDevice();
//				}else{
//					Slog.e("MESSAGE_START_WORK : wait for wifi ready");
//				}
//				break;
//			case MESSAGE_END_WORK:
//				exitDevice();
//				break;
			case MESSAGE_CAPTURE_TIMEOUT:
				captureNeedOn = false;
				if(videoNeedOpen && mCurrNetStatus == 1){
					startVideo();
				}
				break;
			case MESSAGE_RESTART_VIDEO:
				startVideo();
				break;
			case MSG_CHANNEL_FAILED_WAIT:
				exitDevice();
				break;
			case MSG_STOP_CAMERA_DONE:
				if(stopingCamera){
					if(OVJNI.nativeStopCameraDone2(CAMERA_CHANNEL) <= 0){
						if((System.currentTimeMillis() - stopingCameraTime) > 2000){
							Slog.e("stop camera action time and force native exit!!!!!!!!");
							stopingCamera = false;
							Slog.w("start navite exit 44444");
							handleNativeExit();
						}else{
							mLastCoolTime = System.currentTimeMillis();
							mHandler.removeMessages(MSG_STOP_CAMERA_DONE);
							mHandler.sendEmptyMessageDelayed(MSG_STOP_CAMERA_DONE, 100);
						}
					}else{
						Slog.w("MSG_STOP_CAMERA_DONE !!!!!!!");
						stopingCamera = false;
						Slog.w("start Exit native  1111111!!!!!!!");
						handleNativeExit();
					}
				}
				break;
			}
		}
		
	};
	
	private CameraKfirHelper(Context context){
		this.mContext = context.getApplicationContext();
		init();
	}
	
	private void handleNativeExit(){
		allinfo_done = false;
		if(hasInit){
			if(stopingCamera){
				Slog.e("reject native exit2 by stopping camera and wait for a moment!!!");
				mHandler.removeMessages(MSG_STOP_CAMERA_DONE);
				mHandler.sendEmptyMessageDelayed(MSG_STOP_CAMERA_DONE, 100);
			}else{
				Slog.d("handleNativeExit nativeExit2");
				OVJNI.nativeExit2(CAMERA_CHANNEL);
				mLastCoolTime = System.currentTimeMillis();
				hasInit = false;
				mHandler.removeMessages(MESSAGE_CHECKCONNECT);
				mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
			}
			hasSetVideoOn = false;
			videoNeedOpen = true;
			hasSetCapture = false;
		}
	}
	
	private synchronized void exitDevice(){
		mCurrNetStatus = -1;
//		mLastCoolTime = System.currentTimeMillis();
		if(hasInit){
			mLastCoolTime = System.currentTimeMillis();
			try {
				if (hasNativeStartCamera == true) {
					if(OVJNI.nativelinklost2(CAMERA_CHANNEL) == 1){
						Slog.w("start native exit2  55555555!!!!!!!!!");
						handleNativeExit();
					}else{
						Slog.w("start nativeStopCamera2!!!");
						OVJNI.nativeStopCamera2(CAMERA_CHANNEL);
						stopingCamera = true;
						stopingCameraTime = System.currentTimeMillis();
						mHandler.removeMessages(MSG_STOP_CAMERA_DONE);
						mHandler.sendEmptyMessageDelayed(MSG_STOP_CAMERA_DONE, 100);
					}
					hasNativeStartCamera = false;
					mLastCoolTime = System.currentTimeMillis();
					
//					Slog.w("start navite Exit2   00000 !!!!!!!!!");
					
				}else{
					Slog.d("endDecoding nativeExit2 2222");
					handleNativeExit();
				}
				//OVJNI.nativeStopCamera2(CAMERA_CHANNEL);
				
			}catch (Throwable e) {
				Slog.e("Camera helper ending is error",e);
//				e.printStackTrace();
			}
		}
	}
	
	private synchronized void initDevice(){
		int result = 0;
		if(hasInWork && !hasInit){
			if((System.currentTimeMillis() - mLastCoolTime) < COOL_TIME){
				Slog.e("restart too fast and wait for a moment!!!");
				mHandler.removeMessages(MESSAGE_CHECKCONNECT);
				mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, System.currentTimeMillis() - mLastCoolTime);
				return;
			}
			mLastCoolTime = System.currentTimeMillis();
			initCameraDevice();
			result = findCameraDevice();
			if(result > 0){
				Slog.i("-- startDecoding "+intToIp(deviceInfo.ip));
				if(deviceInfo.ip == 0){
					//changeDeviceStatus(DeviceStatus.failed);
					Slog.e("-- error Decoding ipaddr empty and retry!!!");
					// TODO UI 
//					result = - 1;
					mHandler.removeMessages(MESSAGE_CHECKCONNECT);
					mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
				}else{
					initViewInternal(true);
//					deviceInfo.initDefaultSetting();
					OVJNI.initVout(deviceInfo.GLView, deviceInfo.Vout, CAMERA_CHANNEL);
					OVJNI.sendDevSetting(deviceInfo, CAMERA_CHANNEL);
					hasInit = true;
					int tmp = OVJNI.nativeStartByMac(deviceInfo.mac, CAMERA_CHANNEL);
					if(tmp != 0){
						hasNativeStartCamera = false;
						Slog.e("nativeStartByMac failed!!!!!");
						exitDevice();
					}else{
						hasNativeStartCamera = true;
						Slog.e("nativeStartByMac Success!!!!!");
						mHandler.removeMessages(MESSAGE_CHECKCONNECT);
						mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 500);
					}
				}
			}else{
				Slog.e("findCameraDevice Error and retry!!!");
				mHandler.removeMessages(MESSAGE_CHECKCONNECT);
				mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
			}
		}else{
			Slog.e("Can not init Device : hasInWork : "+hasInWork+"  hasInit : "+hasInit);
		}
	}
	
	private int findCameraDevice() {
		int result = 0;
		if(fdeviceInfo == null){
			fdeviceInfo = new CameraDevInfo();
		}
		OVJNI.nativeCheckCameraOnline();
		OVJNI.nativeCameraUpdated();
		int cameraCnt = OVJNI.nativeGetCameraCnt();
//		Slog.i("camers num is " +cameraCnt);
		String deviceIP = AllKfirManager.getInstance(mContext).getDeviceIP();
		if(cameraCnt > 0 && !TextUtils.isEmpty(deviceIP) && !"0.0.0.0".equals(deviceIP)){
			Slog.d("start find camera for ip == "+deviceIP);
			macAddress = -1;
			for(int i=0;i < cameraCnt;i++){
				initCameraDevice();
				OVJNI.nativeGetCameraInfo(fdeviceInfo);
				String infoIP = IPUtils.intToIp(fdeviceInfo.ip);
				Slog.d("find camera : "+infoIP);
				if(deviceIP.equals(infoIP)){
					macAddress = fdeviceInfo.mac;  //mac
					Slog.i("find device : mac : " +macAddress+" IP :"+infoIP);
					break;
				}
			}
			if(macAddress <= 0){
				Slog.e("Can not find camera for ip == "+deviceIP);
				result = 0;
				return result;
			}
			deviceInfo = new CameraDevInfo();
			int cnt = OVJNI.nativeGetCameraInfoByMac(deviceInfo, macAddress);
			if(cnt == 0){
				Slog.e("Cannot get device info !!!");
				result = -1;
			}else{
				if(deviceInfo.info_ready == 0){
					OVJNI.nativeRetrieveCameraData(deviceInfo.mac, null, null);
					OVJNI.nativeGetCameraInfoByMac(deviceInfo, deviceInfo.mac);
				}
				if(deviceInfo.info_ready == 0){
					Slog.e("info_ready == 0 and Failed");
					result = -1;
				}else{
					result =1;
				}
			}
//		    result = loginDevice();
		}else{
			Slog.e("getCameraDevice Failed device num is 0");
			result = 0;
			//TODO callback
		}
		return result;
	}
	
	private void init() {
		app = (RadarApplication) mContext.getApplicationContext();
		OVJNI = app.getJNI();
		initCameraDevice(); 
		OVJNI.nativeSetNetStatusCallback(this);
		OVJNI.nativeSetCaptureCallback(this);
		
	}
	
	private void initCameraDevice() {
		fdeviceInfo = new CameraDevInfo();
		fdeviceInfo.online = 0;
		fdeviceInfo.avol = 0;
		fdeviceInfo.devicename = null;
		fdeviceInfo.ipAddr = null;
		fdeviceInfo.mac = 0;
		fdeviceInfo.ip = 0;
		fdeviceInfo.port = 0;
		fdeviceInfo.remote = 0;
		fdeviceInfo.newcarctrl = 0;
		fdeviceInfo.camera_record_en = 0;
		fdeviceInfo.type = 0;
		fdeviceInfo.status = 0;
		fdeviceInfo.IsOnRender = false;
		fdeviceInfo.RenderIdx = -1;
		fdeviceInfo.selected = false;
		
		//deviceStatus = DeviceStatus.none;
		//changeDeviceStatus(DeviceStatus.idel);
	}
	
	private void checkFile(String rgbFileName, String plFileName) {
		File file = new File(rgbFileName);
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(file.exists()){
			file.delete();
		}
		file = new File(plFileName);
		dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		if(file.exists()){
			file.delete();
		}
	}
	
	private String intToIp(int paramIntip) {
        return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
    }
	
	/**update camera status*/
	private void updateCamSta(){
		//check allinfo
		if(! allinfo_done){
			if(OVJNI.nativeAllinfoDone2(0) != 0){
				//framerate
				int framerate_cnt = OVJNI.nativeAllinfoGetCnt(0, 1);
				if(framerate_cnt > 0){
					int i;
					deviceInfo.frmrateList.clear();
					for(i=0; i < framerate_cnt; i++){
						int v = OVJNI.nativeAllinfoGetList(0, 1, i);
						deviceInfo.frmrateList.add(v);
						if(v == deviceInfo.def_framerate){
							deviceInfo.frmrate_index = i;
						}
					}
					
					//if be in descending order, then sort in ascending order
					if(framerate_cnt > 1){
						if(deviceInfo.frmrateList.get(0) > deviceInfo.frmrateList.get(1)){
							Collections.sort(deviceInfo.frmrateList);
							for(i=0; i < framerate_cnt; i++){
								if(deviceInfo.frmrateList.get(i) == deviceInfo.def_framerate){
									deviceInfo.frmrate_index = i;
								}
							}
						}						
					}
				}else{
					deviceInfo.frmrateList_InitDef();
				}

				//resolution
				int res_cnt = OVJNI.nativeAllinfoGetCnt(0, 0);
				if(res_cnt > 0){
					int i;
					deviceInfo.resList.clear();
					for(i=0; i < res_cnt; i++){
						int v = OVJNI.nativeAllinfoGetList(0, 0, i);
						deviceInfo.resList.add(v);
						if(v == ((deviceInfo.def_width << 16) | deviceInfo.def_height ) ){
							deviceInfo.res_index = i;
						}
					}
					
					//if be in descending order, then sort in ascending order
					if(res_cnt > 1){
						if(deviceInfo.resList.get(0) > deviceInfo.resList.get(1)){
							Collections.sort(deviceInfo.resList);
							for(i=0; i < res_cnt; i++){
								if(deviceInfo.resList.get(i) == ((deviceInfo.def_width << 16) | deviceInfo.def_height )){
									deviceInfo.res_index = i;
								}
							}
						}						
					}
				}else{
					deviceInfo.resList_InitDef();
				}

				//bitrate
				int bitrate_cnt = OVJNI.nativeAllinfoGetCnt(0, 2);
				if(bitrate_cnt > 0){
					deviceInfo.def_bitrate = OVJNI.nativeAllinfoGetDef(0, 2);//get default bitrate
					int i;
					deviceInfo.bitrateList.clear();
					for(i=0; i < bitrate_cnt; i++){
						int v = OVJNI.nativeAllinfoGetList(0, 2, i);
						deviceInfo.bitrateList.add(v);
						if(v == deviceInfo.def_bitrate){
							deviceInfo.bitrate_index = i;
						}
					}
					
					//if be in descending order, then sort in ascending order
					if(bitrate_cnt > 1){
						if(deviceInfo.bitrateList.get(0) > deviceInfo.bitrateList.get(1)){
							Collections.sort(deviceInfo.bitrateList);
							for(i=0; i < bitrate_cnt; i++){
								if(deviceInfo.bitrateList.get(i) == deviceInfo.def_bitrate){
									deviceInfo.bitrate_index = i;
								}
							}
						}						
					}
				}else{
					deviceInfo.bitrateList_InitDef();
				}

				//flipmirror
				int fm = OVJNI.nativeAllinfoGetEffect(0, 32); //32 is SNR_EFFECT_FLIP_MIR
				if (fm!=0) {
					deviceInfo.flipmirror_min = (fm >> 24) & 0xff;
					deviceInfo.flipmirror_max = (fm >> 16) & 0xff;									
				}
				Slog.d(String.format("flipmirror:%d-%d", deviceInfo.flipmirror_min, deviceInfo.flipmirror_max));

				//get the all info list
				allinfo_done = true;
			}
		}

		//check string
		if(OVJNI.nativeStringChanged2(0) == 0)
			return;
		
		String str = OVJNI.nativeGetString2(0);
		if(str != null && str.length() > 0){
			Slog.d("Camera Status : "+str+"  currStatus : "+mCurrNetStatus);
		}
	}
	
	/*********************CALL BACK ****************/
	@Override
	public void onCaptureStatus(int mId, int status) {
		// TODO Auto-generated method stub
		if(status != 0 && (Content.ONE_CAPTURE ||(!Content.ONE_CAPTURE && mId > 0))){
			captureNeedOn = false;
			if(videoNeedOpen && mCurrNetStatus == 1 && status != 1){
				startVideo();
			}
		}
		if(captureInterfaceCallback !=null){
			captureInterfaceCallback.onCaptureStatus(mId,status);
		}
	}

	@Override
	public void onNetStatusChanged(int status) {
		// TODO Auto-generated method stub
		Slog.i("onNetStatusChanged : "+status +"  inwork : "+hasInWork);
//		mCurrNetStatus = status;
		switch(status){
		case 0://IDLE
			mHandler.removeMessages(MESSAGE_CHECKCONNECT);
			mHandler.removeMessages(MSG_CHANNEL_FAILED_WAIT);
			mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
			break;
		case 1://SUCCESS
			mHandler.removeMessages(MESSAGE_CHECKCONNECT);
			mHandler.removeMessages(MSG_CHANNEL_FAILED_WAIT);
			mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
			Slog.e("ready to restart video : "+mCurrNetStatus+" "+videoNeedOpen+" "+captureNeedOn);
//			if(mCurrNetStatus != 1){
				if(videoNeedOpen){
//				if(videoNeedOpen && !captureNeedOn){
					Slog.d("restart Video");
					mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_VIDEO, 10);
//					startVideo();
				}
//			}
			break;
		case -1://FAILED
			mHandler.removeMessages(MESSAGE_CHECKCONNECT);
			mHandler.removeMessages(MSG_CHANNEL_FAILED_WAIT);
			mHandler.sendEmptyMessageDelayed(MSG_CHANNEL_FAILED_WAIT, 1000);
//			exitDevice();
//			mHandler.removeMessages(MESSAGE_CHECKCONNECT);
//			mHandler.sendEmptyMessageDelayed(MESSAGE_CHECKCONNECT, 1000);
			break;
		}
		mCurrNetStatus = status;
		if (netStatusInterfaceCallback != null) {
			netStatusInterfaceCallback.onNetStatusChanged(status);
		}
		
	}

}
