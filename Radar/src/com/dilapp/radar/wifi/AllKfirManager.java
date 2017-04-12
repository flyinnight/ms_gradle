package com.dilapp.radar.wifi;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.dilapp.radar.ble.BleHelper;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.ble.IBleHelperUiCallbacks;
import com.dilapp.radar.ble.BleHelper.BleStatus;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.skintest.ActivityWifiPassword;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.IDeviceManagerCallback.DeviceConnectStatus;
import com.dilapp.radar.wifi.WifiKfirHelper.WifiState;
import com.ov.omniwificam.Vout;

public class AllKfirManager implements IWifiKfirHelperCallback,
IBleHelperUiCallbacks,NetStatusInterface, CaptureInterface, IPasswordCallback{

	private WifiKfirHelper wifiHelper = null;
	private BleHelper bleHelper = null;
	private CameraKfirHelper cameraHelper = null;
	private static AllKfirManager mSelf = null;
	
	private Context mContext = null;
	private static Object object = new Object();
	
	private boolean needBleScan = false;
	private boolean skinTestRunning = false;
	private long mLastEndTime = 0;
	
	private boolean isBleConfirming = false;
	
	public static enum NET_UI_STATUS{                          //通知Ui 的状态
		IDLE,          
		BLE_DISCONNECTED,         
		WIFI_CONNECTED,WIFI_DISCONNECTED,
		DEVICE_IDLE,DEVICE_SUCCESS,DEVICE_ERROR
	};
	
	private NET_UI_STATUS mUIStatus = NET_UI_STATUS.IDLE;
	private NET_UI_STATUS mLastUIStatus = NET_UI_STATUS.IDLE;
	
	public enum WIFI_MODE{
		IDLE,STA,AP
	}
	
	private WIFI_MODE mWifiMode = WIFI_MODE.IDLE;
	
	//callback
	private List<WeakReference<IAllKfirHelperCallback>> mInfoHelperCallbackList = new CopyOnWriteArrayList<WeakReference<IAllKfirHelperCallback>>(); /*new ArrayList<IAllKfirHelperCallback>();*/
	private IDeviceManagerCallback deviceCallback;
//	private static final Object mCallbakcLock = new Object();
	
	private int mDeviceMode = 0; //0:IDLE  1:STA 2:AP
	private int mDeviceLinkStatus = 0;// 0:IDLE 1:READY 2:CONNECTED 3:CONNECTING
	private int mDevicePowerStatus = 0;// 0 : IDLE 1:LOWP 2:CHARGING
	private String mDeviceSSID = null;
	private String mDeviceIP = null;
	private int mDeviceErrorCode = 0; //0 :NO 1:NOTEXIST 2:ERRORPWD 3:TIMEOUT
	private int mBatteryLevel = -1;
	private int mBatteryStatus = 0;//0: NORMAL 1 : WARNING 2 : OFF
	
	private int mDeviceChannelStatus = 0;
	
	private DeviceConnectStatus mDeviceStatus = DeviceConnectStatus.idel;
	
	private static final int MESSAGE_OPEN_BLE                = 0x000004;
	private static final int MESSAGE_OPEN_WIFI               = 0x000005;
	private static final int MESSAGE_CONNECT_DEVICE          = 0x000006;
	private static final int MESSAGE_RESTORE_STATUS          = 0x000007;
	private static final int MESSAGE_RESCAN_BLE              = 0x000008;
	private static final int MESSAGE_RE_CONNECT_BLE          = 0x000009;
	private static final int MESSAGE_RE_CONNECT_WIFI         = 0x000010;
	
	private static final int MESSAGE_NOTIFY_DEVICE_LINK 		= 0x000011;
	private static final int MESSAGE_NOTIFY_DEVICE_WIFI		= 0x000012;
	private static final int MESSAGE_NOTIFY_DEVICE_FOUND		= 0x000013;
	private static final int MESSAGE_NOTIFY_DEVICE_CONNECT	= 0x000014;
	private static final int MESSAGE_NOTIFY_DEVICE_CONFIRM	= 0x000015;
	
	private static final int MESSAGE_UI_STATUS_NOTIFY		= 0x000016;
	private static final int MESSAGE_CHECK_BLE_TIME			= 0x000017;
	
	private static final int MESSAGE_CHECK_AP_WIFI_STATUS		= 0x000018;
	
	private static final long WIFI_RECONNECT_DELAY = 1000;
	private static final long BLE_RECONNECT_DELAY = 1000;
	
	//API START
	public static  AllKfirManager getInstance(Context context) {
		synchronized (object) {
			if (mSelf == null) {
				mSelf = new AllKfirManager(context);
			}
		}
		return mSelf;

	}
	
	/**
	 * 设置回调
	 * @param allInfoHelperCallback
	 */
	public void registerAllInfoCallback(IAllKfirHelperCallback allInfoHelperCallback){
		if(allInfoHelperCallback != null){
			if(!weakContains(allInfoHelperCallback)){
				WeakReference<IAllKfirHelperCallback> reference = new WeakReference<IAllKfirHelperCallback>(allInfoHelperCallback);
				mInfoHelperCallbackList.add(reference);
			}
		}
	/*	synchronized (mCallbakcLock) {
			if(allInfoHelperCallback != null){
				if(!mInfoHelperCallbackList.contains(allInfoHelperCallback)){
					mInfoHelperCallbackList.add(allInfoHelperCallback);
					allInfoHelperCallback.allInfoStatusChange(mUIStatus);
				}
			}
		}*/
		init();
	}
	
	public void unRegisterAllInfoCallback(IAllKfirHelperCallback allInfoHelperCallback){
		if (allInfoHelperCallback != null) {
			Iterator<WeakReference<IAllKfirHelperCallback>> it = mInfoHelperCallbackList.iterator();
			while(it.hasNext()){
				WeakReference<IAllKfirHelperCallback> reference = it.next();
				IAllKfirHelperCallback callback = reference.get();
				if(allInfoHelperCallback == callback){
					mInfoHelperCallbackList.remove(reference);
					break;
				}
			}
		}
/*		synchronized (mCallbakcLock) {
			if (mInfoHelperCallbackList.contains(allInfoHelperCallback)) {
				mInfoHelperCallbackList.remove(allInfoHelperCallback);
			}
		}*/
	}

	private boolean weakContains(IAllKfirHelperCallback allInfoHelperCallback){
		for(WeakReference<IAllKfirHelperCallback> weakReference:mInfoHelperCallbackList){
			IAllKfirHelperCallback callback = weakReference.get();
			if(allInfoHelperCallback == callback){
				return true;
			}
		}
		return false;
	}
	
	public void setDeviceMagagerCallback(IDeviceManagerCallback deviceFoundCallback) {
		this.deviceCallback = deviceFoundCallback;
		if(this.deviceCallback != null){
			this.deviceCallback.onDeviceLightStatus(mDeviceLinkStatus, mDevicePowerStatus);
			this.deviceCallback.onDeviceWifiStatus(mDeviceMode, mDeviceSSID, mDeviceIP, mDeviceErrorCode);
		}
	}
	
	public IDeviceManagerCallback getDeviceMagagerCallback(){
		return this.deviceCallback;
	}
	
	/**
	 * 关闭应用的时候调用
	 */
	public void onDestory(){
//		mSelf.setAllInfoCallback(null);
//		synchronized (mCallbakcLock) {
			mInfoHelperCallbackList.clear();
//		}
		wifiHelper.removeWifiHelperUiCallback(this);
		if(cameraHelper != null){
		  cameraHelper.setCaptureInterfaceCallback(null);
		}
		bleHelper.setUiCallbacks(null);
		wifiHelper = null;
		cameraHelper = null;
		bleHelper = null;
	}
	
	public NET_UI_STATUS getUIStatus(){
		return this.mUIStatus;
	}
	
	public void registerBroadcast(Context context){
		if(wifiHelper == null){
			wifiHelper = WifiKfirHelper.getInstance(context);
		}
		if(bleHelper == null){
			bleHelper = BleHelper.getInstance(context);
		}
		
		wifiHelper.registerBroadcast();
		bleHelper.registerBrocast();
	}
	
	/**
	 * 取消广播
	 * @param context
	 */
	public void unRegisterBroadcast(Context context){
		wifiHelper.unRegisterBroadcast();
		bleHelper.unRegisterBrocast();
	}
	
	//BLE START
	public void startScanBleDevice(){
		Slog.i("startScanBleDevice");
		bleHelper.openBt();
		boolean result = bleHelper.startScanning();
		if(!result){
			needBleScan = true;
			mHandler.removeMessages(MESSAGE_RESCAN_BLE);
			mHandler.sendEmptyMessageDelayed(MESSAGE_RESCAN_BLE, BLE_RECONNECT_DELAY * 5);
		}else{
			needBleScan = false;
			mHandler.removeMessages(MESSAGE_RESCAN_BLE);
		}
	}
	
	public void stopScanBleDevice(){
		Slog.i("stopScanBleDevice");
		needBleScan = false;
		mHandler.removeMessages(MESSAGE_RESCAN_BLE);
		bleHelper.stopScanning();
	}
	
	public boolean isBleConnected(){
		if(BleUtils.BLE_DEBUG) return true;
		return (bleHelper.getBleStatus() == BleStatus.connected);
	}
	
	public boolean connectBleDevice(String deviceAddress){
		boolean result = bleHelper.connect(deviceAddress);
		Slog.e("connectBleDevice result :"+result +" now ble status : "+bleHelper.getBleStatus());
		if(!result){
			Message msg = new Message();
			msg.obj  = deviceAddress;
			msg.what = MESSAGE_RE_CONNECT_BLE;
			mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
			mHandler.sendMessageDelayed(msg, (int)(BLE_RECONNECT_DELAY));
		}else{
				//获取 ssid 
			mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
		}
		return result;
	}
		
	public void disConnectBleDevice(){
		Slog.i("disConnectBleDevice...");
		mDeviceMode = 0;
		mDeviceSSID = null;
		mDeviceIP = null;
		mDeviceErrorCode = 0;
		mDeviceLinkStatus = 0;
		mDevicePowerStatus = 0;
		mBatteryLevel = -1;
		saveBatteryStatus(0);
		bleHelper.disconnect();
		bleHelper.endBleKeep();
	}
	
	/**
	 * 用于APP 启动时调用
	 * @param context
	 */
	public void startSkinBle(boolean getinfo){
//			skinTestRunning=  true;
		bleHelper.startBleKeep();
		BleStatus mBleStatus = bleHelper.getBleStatus();
		if(mBleStatus == BleStatus.connected && bleHelper.isInTestMode()){
			wifiHelper.checkWifiState();
//			if(mDeviceLinkStatus <= 0){
				if(getinfo){
					bleHelper.startGetDeviceStatus();
					bleHelper.startGetEnvParams();
				}
//				bleHelper.startGetWifiStatus();
//				bleHelper.startPresetWifiMode(wifiHelper.isPreferAP() ? 1 : 0);
//			}
		}
	}
	
	public void startBleConfirm(){
		if(!SharePreCacheHelper.getPairStatus(mContext)){
			isBleConfirming = true;
			bleHelper.startConnRequest();
		}else{
			isBleConfirming = false;
		}
	}
	
	public void endBleConfirm(){
		isBleConfirming = false;
		if(!SharePreCacheHelper.getPairStatus(mContext)){
			disConnectBleDevice();
		}
	}
	
	public void startCheckTime(){
		if(bleHelper != null){
			bleHelper.startSetTimeCheck();
		}
	}
	
	public void startGetBatteryLevel(){
		if(bleHelper != null){
			bleHelper.startGetBatteryLevel();
		}
	}
	
	public int getDevicePowerStatus(){
		return mDevicePowerStatus;
	}
	
	public void startGetEnvParamWhenFree(){
		if(bleHelper != null){
			bleHelper.startGetEnvParamsWhenFree();
		}
	}
	//BLE END
	
	//WIFI START
	public void startWifiConnect(){
		if(!skinTestRunning){
			Slog.e("Skin test is not start and ignore!!!");
			mWifiMode = WIFI_MODE.IDLE;
			return;
		}
		if(mWifiMode != WIFI_MODE.IDLE){
			Slog.d("startWifiConnect has Start and only handleWifiConnect()!");
			handleWifiConnect();
			return;
		}
		
		mHandler.removeMessages(MESSAGE_RE_CONNECT_WIFI);
		wifiHelper.checkWifiState();
//		WifiState mStatus = wifiHelper.getWifiState();
//		String mcurSSID = wifiHelper.getCurrSSID();
		boolean isPreAP = wifiHelper.isPreferAP();
		if(mDeviceLinkStatus != 0 && mDeviceMode == 1){
			mWifiMode = WIFI_MODE.STA;
		}else if(mDeviceLinkStatus != 0 && mDeviceMode == 2){
			mWifiMode = WIFI_MODE.AP;
		}else if(isPreAP){
			mWifiMode = WIFI_MODE.AP;
		}else{
			if(mDeviceMode == BleUtils.DEVICE_MODE_AP 
					&& !TextUtils.isEmpty(mDeviceSSID)
					&& mDeviceSSID.equals(wifiHelper.getCurrSSID())){
				mWifiMode = WIFI_MODE.AP;
				Slog.d("prefer STA, But Wifi is already AP");
			}else{
				mWifiMode = WIFI_MODE.STA;
			}
		}
		if(Content.AP_DEBUG_MODE){
			mWifiMode = WIFI_MODE.AP;
		}
		
		if(BleUtils.BLE_DEBUG){
			mDeviceMode = BleUtils.DEVICE_MODE_AP;
			mDeviceSSID = "lenovo_test";
		}
		Slog.i("startWifiConnect  wifi prefer : "+mWifiMode);
		handleWifiConnect();
	}
	
	public void stopWifiConnect(){
		Slog.i("stopping wifi connection");
		mWifiMode = WIFI_MODE.IDLE;
		String apSSID = null;
		if(mDeviceSSID != null && mDeviceMode ==2){
			apSSID = mDeviceSSID;
		}else{
			apSSID = SharePreCacheHelper.getDefaultSSid(mContext);
		}
		wifiHelper.resetPreNet(apSSID);
	}
	
	/**
	 * 判定当前是否需要输入Wi-Fi密码
	 * @return
	 */
	public boolean needCheckWifiPassword(){
		boolean result = false;
		String currSSID = wifiHelper.getCurrSSID();
		String currBSSID = wifiHelper.getCurrBSSID();
		Slog.d("needCheckWifiPassword : "+currSSID+"  "+currBSSID);
		if(TextUtils.isEmpty(currSSID)){
			return result;
		}
		if(mDeviceMode == BleUtils.DEVICE_MODE_AP 
				&& !TextUtils.isEmpty(mDeviceSSID)
				&& mDeviceSSID.equals(currSSID)){
			return result;
		}
		LocalWifi mlocal = SharePreCacheHelper.checkSSid(mContext, currBSSID, currSSID);
		if(mlocal == null){
			result = true;
		}
		return result;
	}
	
	public boolean isWifiReadyForTrans(){
		boolean result = false;
		String currSSID = wifiHelper.getCurrSSID();
		if(!TextUtils.isEmpty(mDeviceSSID) && mDeviceSSID.equals(currSSID)
				&& !("0.0.0.0".equals(mDeviceIP))
				&& !TextUtils.isEmpty(mDeviceIP)){
			result = true;
		}
		Slog.d("isWifiReadyForTrans : currSSID :"+currSSID+" targetSSID : "+mDeviceSSID +" mDeviceIP: "+mDeviceIP+" result : "+result);
		return result;
	}
	
	public void removeAPSSID(){
		if(wifiHelper != null){
			wifiHelper.removeAPSSID();
		}
	}
	//WIFI END
	
	//CAMERA START
	public void initCameraHelper(Context context){
		
		if (cameraHelper == null) {
			cameraHelper = CameraKfirHelper.getInstance(context);
			cameraHelper.setNetStatusInterfaceCallback(this);
			cameraHelper.setCaptureInterfaceCallback(this);
		}
	}
		
	/**
	 * 初始化设备(必须做在前面)
	 * @param context
	 * @param glSurfaceView
	 */
//	public void initDevice(Context context, GLSurfaceView glSurfaceView,Vout mVout) {
//		if (cameraHelper == null) {
//			cameraHelper = CameraKfirHelper.getInstance(context);
//			cameraHelper.setNetStatusInterfaceCallback(this);
//			cameraHelper.setCaptureInterfaceCallback(this);
//		}
////		cameraHelper.initDevice();
//		if(glSurfaceView != null && mVout != null){
//			cameraHelper.initView(glSurfaceView,mVout, mContext);
//		}
//	}
	
	public void setLedControl(int value){
		if(cameraHelper != null){
			cameraHelper.setLedControl(value);
		}
	}
		
	/**
	 * 初始化数据通道
	 */
	public void startDecoding(GLSurfaceView glSurfaceView,Vout mVout) {
		handleWifiConnect();
		cameraHelper.startSkinWork(glSurfaceView, mVout);
	}

	/**
	 * 关闭数据通道
	 */
	public void stopDecoding() {
		cameraHelper.endSkinWork();
	}

	public void startVideo() {
		cameraHelper.startVideo();
	}

	public void closeVideo() {
		cameraHelper.endVideo();
	}

	public void startCamera() {
		cameraHelper.startCamera();
	}

//	public void closeCamera() {
//		cameraHelper.endCamera();
//	}

	/**
	 * 清除关闭数据通道的计时器
	 */
	public void clearTimerCloseData() {

		cameraHelper.clearTimerCloseData();

	}
	
	/**
	 * 开始计时器,并关闭数据通道(时间到了)
	 */
	public void uiStartTimerCloseData(){
	  cameraHelper.uiStartTimerCloseData();
	}
	/**
	 * 终止计时器,并关闭数据通道
	 */
	public  void uiEndTimerCloseData(){
		cameraHelper.uiEndTimerCloseData();
	}
	
	/**
	 * 获取设备的连状态  
	 *  @return >= 0 success ;< 0 failed  
	 */
	public boolean  isCameraConnected(){
		if (cameraHelper == null) {
			cameraHelper = CameraKfirHelper.getInstance(mContext);
			cameraHelper.setNetStatusInterfaceCallback(this);
			cameraHelper.setCaptureInterfaceCallback(this);
		}
	 return cameraHelper.isCameraConnected();
	}
	//CAMERA END
	
	/**
	 * 开始测试
	 * @param context
	 */
	public void startSkinTest(){
//		if(skinTestRunning){
//			Slog.e("aready has started skin test!!");
//			return;
//		}
		Slog.d("startSkinTest !!!!!!!!!");
		skinTestRunning=  true;
		bleHelper.openBt();
		bleHelper.startBleKeep();
		startWifiConnect();
//		if(cameraHelper != null){
//			cameraHelper.startSkinWork();
//		}
//		CameraHelper.setSkinRunningTest(true);
	}
	
	public void ignoreSTAAction(){
		if(skinTestRunning){
			if(mWifiMode == WIFI_MODE.STA){
//			if(mDeviceMode != 1 && mWifiMode == WIFI_MODE.STA){
				Slog.e("ignore STA and change to AP mode !!!!");
				mWifiMode = WIFI_MODE.AP;
				handleWifiConnect();
			}
		}else{
			mWifiMode = WIFI_MODE.IDLE;
		}
	}
	
	public void endSkinTest(){
		mLastEndTime = System.currentTimeMillis();
		skinTestRunning=  false;
		stopWifiConnect();
		if(cameraHelper != null){
			cameraHelper.endSkinWork();
		}
		handleUiNotify();
//		CameraHelper.setSkinRunningTest(false);
	}
	
	public boolean isOnSkinTest(){
		return skinTestRunning;
	}
	
	public String getDeviceIP(){
		return mDeviceIP;
	}
	
	public int getBatteryLevel(){
		return mBatteryLevel;
	}
		
	
	//API END
	
	/***************************private*******************************/
	private AllKfirManager(Context context){
		this.mContext = context.getApplicationContext();
		init();
	}
	
	private void init() {

		wifiHelper = WifiKfirHelper.getInstance(mContext);
		wifiHelper.addWifiHelperUiCallback(this);
		bleHelper = BleHelper.getInstance(mContext);
		bleHelper.setUiCallbacks(this);
		
		//.so库没加载好不能实例化
		//cameraHelper = CameraHelper.getInstance(mContext);
		//cameraHelper.setNetStatusInterfaceCallback(this);

	}
	
	private void handleUiNotify(){
		Slog.d("handleUiNotify : " + mUIStatus + "   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		synchronized (mCallbakcLock) {
			if(mInfoHelperCallbackList != null){
				mLastUIStatus = mUIStatus;
				for(WeakReference<IAllKfirHelperCallback> reference : mInfoHelperCallbackList){
					IAllKfirHelperCallback callback = reference.get();
					if(callback != null){
						callback.allInfoStatusChange(mUIStatus);
					}
				}
			}
//		}
	}
	
	private void handleWifiConnect(){
		mHandler.removeMessages(MESSAGE_RE_CONNECT_WIFI);
		//{Leeon added
		if(isWifiReadyForTrans()) {
			Slog.i("already connected, no need to reconnect");
			return;
		}
		//}
		if(mWifiMode == WIFI_MODE.IDLE){
			Slog.e("WIFI_MODE == IDLE Not need handle wifi connect!");
		}else if(mWifiMode == WIFI_MODE.AP){
			if(wifiHelper.wifiIsOpen()){
				if(mDeviceMode != BleUtils.DEVICE_MODE_AP || TextUtils.isEmpty(mDeviceSSID)){
					if(mDeviceLinkStatus != 0){
						Slog.e("Device is Not In IDLE and ignore send AP Cmd !!!    "+mDeviceLinkStatus);
					}else{
						Slog.d("Device AP mode in not ready and start AP first!");
						bleHelper.startWifiByAP();
					}
				}else{
					Slog.d("connecting to ssid:" + mDeviceSSID);
					wifiHelper.connectAP(mDeviceSSID);
				}
			}else{
				Slog.d("open wifi first!");
				wifiHelper.openWifi();
			}
			
		}else if(mWifiMode == WIFI_MODE.STA){
			if(wifiHelper.wifiIsOpen()){
				checkSTAReady(false);
			}else{
				Slog.d("open wifi first!");
				wifiHelper.openWifi();
			}
			
		}
	}
	
	private LocalWifi checkSSid() {
		String ssid = wifiHelper.getCurrSSID();
		String mac = wifiHelper.getCurrBSSID();
		return  SharePreCacheHelper.checkSSid(mContext, mac, ssid);
	}
	
	private void checkSTAReady(boolean forcesend){
		if(mWifiMode == WIFI_MODE.STA){
			LocalWifi localWifi = checkSSid();
			if(localWifi != null ){//&& mDeviceErrorCode != BleUtils.ERROR_CODE_ERRORPWD){
				String ssid = wifiHelper.getCurrSSID();
				String pwd = localWifi.wifiPassword;
				//ble send data TODO
				if(mDeviceMode == BleUtils.DEVICE_MODE_IDLE){
					if(mDeviceLinkStatus != 0 && !forcesend){
						Slog.e("Device is Not In IDLE and ignore send STA Cmd !!!    "+mDeviceLinkStatus);
					}else{
						bleHelper.startWifiBySTA(ssid, pwd);
					}
				}else if(mDeviceMode == BleUtils.DEVICE_MODE_STA && !TextUtils.isEmpty(mDeviceSSID) && !mDeviceSSID.equals(ssid)){
					Slog.e("Device in STA But Phone has Lost and reconnect : "+mDeviceSSID+"  !!!!!!");
					wifiHelper.connectAP(mDeviceSSID);
				}else if(forcesend){
					bleHelper.startWifiBySTA(ssid, pwd);
				}else{
					Slog.d("checkSTAReady Device is already STA : "+ssid);
				}
			}else{
				String sSSID = SharePreCacheHelper.getDefaultSSid(mContext);
				String cSSID = wifiHelper.getCurrSSID();
				if(TextUtils.isEmpty(cSSID)){
					Slog.e("Can not checkSTAReady  currSSID is NULL!!!!!!");
					return;
				}else if(cSSID.equals(sSSID)){
					Slog.e("Can not checkSTAReady currSSID is AP SSID!!!!!!");
					return;
				}
				if(wifiHelper.getWifiState() == WifiState.connected_open){
					bleHelper.startWifiBySTA(cSSID, null);
				}else{
					Intent intent =  new Intent(mContext, ActivityWifiPassword.class);
					intent.putExtra("from_device_confirm", false);
					if(mDeviceErrorCode == BleUtils.ERROR_CODE_ERRORPWD){
						intent.putExtra("error", true);
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				    mContext.startActivity(intent);
				}
			}
		}else{
			Slog.e("Error checkSTAReady : mWifiMode : "+mWifiMode);
		}
	}
	
	private void saveBatteryStatus(int status){
//		if(mBatteryStatus != status){
			mBatteryStatus = status;
			SharePreCacheHelper.setBatteryStatus(mContext, mBatteryStatus);
			Intent intent = new Intent(BleUtils.ACTION_BATTERY_CHANGED);
			intent.putExtra(BleUtils.KEY_BATTERY_STATUS, mBatteryStatus);
			mContext.sendBroadcast(intent);
//		}
	}
	
	private void notifyUIStatus(){
		printStatus();
		BleStatus bleStatus = bleHelper.getBleStatus();
//		WifiState wifiStatus = wifiHelper.getWifiState();
		if(mDeviceChannelStatus == 1){
			mUIStatus = NET_UI_STATUS.DEVICE_SUCCESS;
		}else if(bleStatus != BleStatus.connected && !BleUtils.BLE_DEBUG){
			mUIStatus = NET_UI_STATUS.BLE_DISCONNECTED;
//			if(cameraHelper != null){
//				cameraHelper.notifyWifiReady(false);
//			}
		}else if(!isWifiReadyForTrans()){
//				(wifiStatus != WifiState.connected_pw || wifiStatus != WifiState.connected_open)){
			mUIStatus = NET_UI_STATUS.WIFI_DISCONNECTED;
			if(cameraHelper != null){
				cameraHelper.notifyWifiReady(false);
			}
		}else{
			switch(mDeviceChannelStatus){
			case 0:
				if(mUIStatus == NET_UI_STATUS.WIFI_DISCONNECTED){
					mUIStatus = NET_UI_STATUS.WIFI_CONNECTED;
				}else{
					mUIStatus = NET_UI_STATUS.DEVICE_IDLE;
				}
				if(cameraHelper != null){
					cameraHelper.notifyWifiReady(true);
				}
				break;
			case -1:
				mUIStatus = NET_UI_STATUS.DEVICE_ERROR;
				break;
			case 1:
				mUIStatus = NET_UI_STATUS.DEVICE_SUCCESS;
				break;
			}
		}
		if(mInfoHelperCallbackList != null){
			mHandler.sendEmptyMessage(MESSAGE_UI_STATUS_NOTIFY);
		}
		
//		synchronized (mCallbakcLock) {
//			if(mInfoHelperCallbackList != null && mLastUIStatus != mUIStatus){
//				mLastUIStatus = mUIStatus;
//				for(IAllKfirHelperCallback callback : mInfoHelperCallbackList){
//					if(callback != null){
//						callback.allInfoStatusChange(mUIStatus);
//					}
//				}
//			}
//		}
		
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) { 
			case MESSAGE_RE_CONNECT_WIFI://重连WI-FI
				mHandler.removeMessages(MESSAGE_RE_CONNECT_WIFI);
				if(mWifiMode != WIFI_MODE.IDLE){
					handleWifiConnect();
				}
				break; 
			case MESSAGE_OPEN_WIFI:                            //重新打开wifi
//				if(open_wifi_count < MAXCOUNT ){
//					handler.removeMessages(MESSAGE_OPNE_WIFI);
//					wifiHelper.openWifi();
//					handler_open_wifi = true;
//					open_wifi_count ++;
//				}else{
//					handler.removeMessages(MESSAGE_OPNE_WIFI);
//					open_wifi_count = 0;
//				}
				
				break;
				
			case MESSAGE_CONNECT_DEVICE:                    //cameraHelper 自己干了
				
				break;
				
			case MESSAGE_OPEN_BLE:                           //重新打开蓝牙
//				if(open_ble_count < MAXBLECOUNT  ){
//					handler.removeMessages(MESSAGE_OPEN_BLE);
//					bleHelper.openBt();
//					open_ble_count ++;
//				}else{
//					handler.removeMessages(MESSAGE_OPEN_BLE);
//					open_ble_count = 0;
//				}
				
				break;
				
			case MESSAGE_RESCAN_BLE:
				Slog.e(" -----allinfomanager MESSAGE_RESCAN_BLE-----");
				mHandler.removeMessages(MESSAGE_RESCAN_BLE);
				startScanBleDevice();
		        break;
			case MESSAGE_RE_CONNECT_BLE:
				Slog.d(" ------allinfomanager MESSAGE_RE_CONNECT_BLE----");
				//Toast.makeText(mContext, "连接失败", 0).show();
				String deviceAddress = SharePreCacheHelper.getBleMacAddress(mContext);//(String) msg.obj;
				mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
				if(!bleHelper.isInTestMode()){
					Slog.e("BLE in not in test mode and ignore re connect!!!");
					break;
				}
				boolean result = false;
				if(bleHelper.isBtEnabled() && SharePreCacheHelper.getPairStatus(mContext)){
					if(!TextUtils.isEmpty(deviceAddress)){
						result = bleHelper.connect(deviceAddress);
					}
					if(!result){
						Message tempMsg = new Message();
						  tempMsg.what  = MESSAGE_RE_CONNECT_BLE;
					      tempMsg.obj = deviceAddress;
					      mHandler.sendMessageDelayed(tempMsg, BLE_RECONNECT_DELAY);
					}else{
						mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
					}
				}
				break;
			case MESSAGE_NOTIFY_DEVICE_LINK:
				if(deviceCallback != null){
					deviceCallback.onDeviceLightStatus(mDeviceLinkStatus, mDevicePowerStatus);
				}
				break;
			case MESSAGE_NOTIFY_DEVICE_WIFI:
				if(deviceCallback != null){
					deviceCallback.onDeviceWifiStatus(mDeviceMode, mDeviceSSID, mDeviceIP, mDeviceErrorCode);
				}
				break;
			case MESSAGE_NOTIFY_DEVICE_FOUND://maybe not called in UI thread
//				if(deviceCallback != null){
//					deviceCallback.uiDeviceFound(device, rssi, record);
//				}
				break;
			case MESSAGE_NOTIFY_DEVICE_CONFIRM:
				if(deviceCallback != null){
					boolean isconfirm = msg.arg1 > 0 ? true : false;
					BluetoothDevice device = (BluetoothDevice) msg.obj;
					deviceCallback.deviceConfirmed(device, isconfirm);
				}
				break;
			case MESSAGE_NOTIFY_DEVICE_CONNECT:
				if(deviceCallback != null){
					deviceCallback.deviceConnectStatus(mDeviceStatus, (BluetoothDevice)msg.obj);
				}
				break;
			case MESSAGE_UI_STATUS_NOTIFY:
				handleUiNotify();
				break;
			case MESSAGE_CHECK_BLE_TIME:
				if(mDeviceChannelStatus == 1){
					if(bleHelper != null){
						bleHelper.startSetTimeCheck();
					}
					mHandler.removeMessages(MESSAGE_CHECK_BLE_TIME);
					mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_BLE_TIME, 5000);
				}else{
					mHandler.removeMessages(MESSAGE_CHECK_BLE_TIME);
				}
				break;
			case MESSAGE_CHECK_AP_WIFI_STATUS:
				if(mWifiMode == WIFI_MODE.AP && mDeviceMode == 2 && mDeviceSSID != null){
					String currSSID = wifiHelper.getCurrSSID();
					Slog.w("MESSAGE_CHECK_AP_WIFI_STATUS curr: "+currSSID+"  target : "+mDeviceSSID);
					if(currSSID != null && currSSID.equals(mDeviceSSID)){
//						Toast.makeText(mContext, "AP 连接成功!!!!", Toast.LENGTH_SHORT).show();
						notifyUIStatus();
					}else if(currSSID == null){
						mHandler.removeMessages(MESSAGE_CHECK_AP_WIFI_STATUS);
						mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_AP_WIFI_STATUS, 500);
					}
				}
				break;
				
			default:
				break;
			}
			
		}
		
	};
	
	/***********************回调开始****************************/
	@Override
	public void onCaptureStatus(int mId, int status) {
		// TODO Auto-generated method stub
		Slog.d("onCaptureStatus : " + mId + "  " + status);
//		synchronized (mCallbakcLock) {
			if(mInfoHelperCallbackList != null){
				mLastUIStatus = mUIStatus;
				for(WeakReference<IAllKfirHelperCallback> reference : mInfoHelperCallbackList){
					IAllKfirHelperCallback callback = reference.get();
					if(callback != null){
						callback.photosStatus(mId,status);
					}
				}
			}
//		}
	}

	@Override
	public void onNetStatusChanged(int status) {
		// TODO Auto-generated method stub
		Slog.d("onNetStatusChanged : "+status);
		mDeviceChannelStatus = status;
		if(mDeviceChannelStatus == 1){
			mHandler.removeMessages(MESSAGE_CHECK_BLE_TIME);
			mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_BLE_TIME, 1000);
		}else{
			mHandler.removeMessages(MESSAGE_CHECK_BLE_TIME);
		}
		notifyUIStatus();
	}

	@Override
	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
		// TODO Auto-generated method stub
		Slog.d("uiDeviceFound : " + device.getName());
		if(deviceCallback != null ){
			deviceCallback.uiDeviceFound(device, rssi, record);
		}
	}

	@Override
	public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
		// TODO Auto-generated method stub
		Slog.d("uiDeviceConnected : " + device.getName() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		mDeviceStatus = DeviceConnectStatus.success;
		if(deviceCallback != null){
			mHandler.obtainMessage(MESSAGE_NOTIFY_DEVICE_CONNECT, device).sendToTarget();
//			deviceCallback.deviceConnectStatus(DeviceConnectStatus.success, device);
		}
		boolean isPaired = SharePreCacheHelper.getPairStatus(mContext);
		if(!isPaired){
//			bleHelper.startConnRequest();
//			if(!isBleConfirming){
//				disConnectBleDevice();
//			}
		}else{
			bleHelper.startGetDeviceStatus();
			bleHelper.startGetWifiStatus();
			bleHelper.startGetEnvParams();
			handleWifiConnect();
		}
		
		mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
		notifyUIStatus();
	}

	@Override
	public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
		// TODO Auto-generated method stub
		Slog.i("uiDeviceDisconnected...");
		mDeviceStatus = DeviceConnectStatus.failed;
		if(deviceCallback != null){
			Slog.e("~~~~~uiDeviceDisconnected~~~~~ : "+device.getName());
			mHandler.obtainMessage(MESSAGE_NOTIFY_DEVICE_CONNECT, device).sendToTarget();
//			deviceCallback.deviceConnectStatus(DeviceConnectStatus.failed, device);
		}
		mDeviceMode = 0;
		mDeviceSSID = null;
		mDeviceIP = null;
		mDeviceErrorCode = 0;
		mDeviceLinkStatus = 0;
		mDevicePowerStatus = 0;
		mBatteryLevel = -1;
		saveBatteryStatus(0);
		if(SharePreCacheHelper.getPairStatus(mContext)){
			mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
			mHandler.sendEmptyMessageDelayed(MESSAGE_RE_CONNECT_BLE, BLE_RECONNECT_DELAY*3);
		}
		notifyUIStatus();
//		mHandler.removeMessages(MESSAGE_RE_CONNECT_BLE);
//		Message msg = new Message();
//		msg.obj = device.getAddress();
//		msg.what = MESSAGE_RE_CONNECT_BLE;
//		mHandler.sendMessageDelayed(msg, BLE_RECONNECT_DELAY);
	}

	@Override
	public void uiDeviceConfirm(BluetoothGatt gatt, BluetoothDevice device,
			boolean confirm) {
		// TODO Auto-generated method stub
		Slog.d("uiDeviceConfirm :"+device.getAddress());
		String address = device.getAddress();
		String sadd = SharePreCacheHelper.getBleMacAddress(mContext);
		if(sadd != null && sadd.equals(address)){
			Slog.d("uiDeviceConfirm  : " + sadd + "  " + confirm);
//			if(confirm){
//				SharePreCacheHelper.savePairStatus(mContext, true);
//				bleHelper.startGetDeviceStatus();
//				bleHelper.startGetWifiStatus();
//			}else{
//				SharePreCacheHelper.savePairStatus(mContext, false);
//				bleHelper.disconnect();
////				bleHelper.close();
//			}
			if(deviceCallback != null){
				int flag = confirm ? 1:0;
				mHandler.obtainMessage(MESSAGE_NOTIFY_DEVICE_CONFIRM, flag, 0, device).sendToTarget();
//				deviceCallback.deviceConfirmed(device, confirm);
			}
			
		}else{
			Slog.e("uiDeviceConfirm failed : "+address+"  "+sadd +"  " +confirm);
		}
	}

	@Override
	public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device,
			List<BluetoothGattService> services) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bleEnable() {
		// TODO Auto-generated method stub
		Slog.d("bleEnable");
		String macAddress  = SharePreCacheHelper.getBleMacAddress(mContext);
		boolean paired = SharePreCacheHelper.getPairStatus(mContext);
		if(paired && !TextUtils.isEmpty(macAddress) && !bleHelper.getBleStatus().equals(BleStatus.connected)){   //connect ble 
			Slog.d("--bleEnable  connectBleDevice--");
			connectBleDevice(macAddress);
		}
		
		notifyUIStatus();
		
		if(needBleScan){
			startScanBleDevice();
		}
	}

	@Override
	public void bleDisable() {
		// TODO Auto-generated method stub
		notifyUIStatus();
	}

	@Override
	public void bleEnabling() {
		// TODO Auto-generated method stub
		notifyUIStatus();
	}

	@Override
	public void onWifiStatusResult(int mode, String ssid, String ip,
			int errorcode) {
		// TODO Auto-generated method stub
		Slog.e("onWifiStatusResult !!!!!: "+mode+"  "+ssid+"  "+ip+"  "+errorcode);
		if(mWifiMode != WIFI_MODE.IDLE){
			if((mode == 1 && mWifiMode == WIFI_MODE.AP) 
					||(mode == 2 && mWifiMode == WIFI_MODE.STA)){
				Slog.e("DEVICE mode changed and force to mode : "+mode);
				if(mode == 1){
					mWifiMode = WIFI_MODE.STA;
				}else if(mode == 2){
					mWifiMode = WIFI_MODE.AP;
				}
			}
		}
		mDeviceMode = mode;
		mDeviceSSID = ssid;
		if(mDeviceLinkStatus == BleUtils.DEVICE_LINK_IDLE
				&& mDeviceMode > 0){
			if(TextUtils.isEmpty(mDeviceSSID)){
				mDeviceLinkStatus = BleUtils.DEVICE_LINK_CONNECTING;
			}else{
				mDeviceLinkStatus = BleUtils.DEVICE_LINK_CONNECTED;
			}
		}
		if(mDeviceMode == 2 && !TextUtils.isEmpty(mDeviceSSID) 
				&& SharePreCacheHelper.getPairStatus(mContext)){
			SharePreCacheHelper.saveDefaultSSid(mContext, mDeviceSSID);
		}
		mDeviceIP = ip;
		mDeviceErrorCode = errorcode;
		if(mDeviceErrorCode == BleUtils.ERROR_CODE_ERRORPWD && mWifiMode == WIFI_MODE.STA){
			mDeviceSSID = null;
			Slog.e("ERROR_CODE_ERROR_PWD!!!!");
			Intent intent =  new Intent(mContext, ActivityWifiPassword.class);
			intent.putExtra("from_device_confirm", false);
			if(mDeviceErrorCode == BleUtils.ERROR_CODE_ERRORPWD){
				intent.putExtra("error", true);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    mContext.startActivity(intent);
		}else if(mDeviceErrorCode == BleUtils.ERROR_CODE_ERRORFAILED && mWifiMode == WIFI_MODE.STA){
			mDeviceSSID = null;
			Slog.e("ERROR_CODE_ERROR_FAILED!!!!!");
			mWifiMode = WIFI_MODE.AP;
			bleHelper.startWifiByAP();
		}else{
			handleWifiConnect();
		}
		mHandler.sendEmptyMessage(MESSAGE_NOTIFY_DEVICE_WIFI);
		notifyUIStatus();
	}

	@Override
	public void onDeviceStatusResult(int net_status, int power_status) {
		// TODO Auto-generated method stub
		Slog.d("onDeviceStatusResult : "+net_status+"  "+power_status);
		boolean needClose = false;
		if(mDeviceLinkStatus != net_status){
			Slog.d("Device Link Status changed and start get wifistatus");
			bleHelper.startGetWifiStatus();
		}else if(net_status == BleUtils.DEVICE_LINK_CONNECTED
				&& mDeviceMode == 0){
			Slog.d("Device Link is ready but mDeviceMode is 0 and start get wifistatus");
			bleHelper.startGetWifiStatus();
		}
		
		if(mDeviceLinkStatus != BleUtils.DEVICE_LINK_IDLE
				&& net_status == BleUtils.DEVICE_LINK_IDLE){
			needClose = true;
		}
		if(mDevicePowerStatus != BleUtils.DEVICE_POWER_LOWP
				&& power_status == BleUtils.DEVICE_POWER_LOWP){
			needClose = true;
			bleHelper.startGetBatteryLevel();
		}else if(mDevicePowerStatus != BleUtils.DEVICE_POWER_CHARGE
				&& power_status == BleUtils.DEVICE_POWER_CHARGE){
			bleHelper.startGetBatteryLevel();
		}else if(mDevicePowerStatus != BleUtils.DEVICE_POWER_IDLE
				&& power_status == BleUtils.DEVICE_POWER_IDLE){
			bleHelper.startGetBatteryLevel();
		}else if(mBatteryLevel < 0){
			bleHelper.startGetBatteryLevel();
		}
		mDeviceLinkStatus = net_status;
		mDevicePowerStatus = power_status;
		
		if(needClose){
			endSkinTest();
			mDeviceMode = 0;
			mDeviceSSID = null;
			mDeviceIP = null;
			mDeviceErrorCode = 0;
			Slog.w("End Skin Test by Device Status Changed : LINK TO IDLE or POWER TO LOW!");
		}
		mHandler.sendEmptyMessage(MESSAGE_NOTIFY_DEVICE_LINK);
	}

	@Override
	public void wifiDisabled() {
		// TODO Auto-generated method stub
		Slog.d("wifiDisabled!!!!!!!");
		notifyUIStatus();
//		handleWifiConnect();
	}

	@Override
	public void wifiEnable() {
		// TODO Auto-generated method stub
		Slog.d("wifiEnable!!!!!!!");
		notifyUIStatus();
		handleWifiConnect();
	}

	@Override
	public void wifiEnableding() {
		// TODO Auto-generated method stub
		Slog.d("wifiEnableding!!!!!!!");
		notifyUIStatus();
	}

	@Override
	public void sendSSidAndPassword(String ssid, String passWord) {
		// TODO Auto-generated method stub
		checkSTAReady(true);
	}

	@Override
	public void onWifiConnectSuccess(String ssid) {
		// TODO Auto-generated method stub
		Slog.d("onWifiConnectSuccess!!!!!!!,ssid:" + ssid);
		notifyUIStatus();
		handleWifiConnect();
	}

	@Override
	public void onWifiDisconnected() {
		// TODO Auto-generated method stub
		Slog.d("onWifiDisconnected!!!!!!!");
		notifyUIStatus();
		mHandler.removeMessages(MESSAGE_RE_CONNECT_WIFI);
		mHandler.sendEmptyMessageDelayed(MESSAGE_RE_CONNECT_WIFI, WIFI_RECONNECT_DELAY);
	}

	@Override
	public void onWifiConnecting() {
		// TODO Auto-generated method stub
		Slog.d("onWifiConnecting!!!!!!!");
		mHandler.removeMessages(MESSAGE_CHECK_AP_WIFI_STATUS);
		mHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_AP_WIFI_STATUS, 500);
	}

	@Override
	public void onPhotoCmdFromDevice() {
		// TODO Auto-generated method stub
		if(mDeviceChannelStatus == 1){
			startCamera();
		}
	}

	//{Leeon added
	public void printStatus() {
//		String status = "AllKfirManager{" +
//				"mDeviceMode=" + mDeviceMode +
//				", mDeviceLinkStatus=" + mDeviceLinkStatus +
//				", mDevicePowerStatus=" + mDevicePowerStatus +
//				", mDeviceSSID='" + mDeviceSSID + '\'' +
//				", mDeviceIP='" + mDeviceIP + '\'' +
//				", mDeviceErrorCode=" + mDeviceErrorCode +
//				", mUIStatus=" + mUIStatus +
//				", mLastUIStatus=" + mLastUIStatus +
//				", needBleScan=" + needBleScan +
//				", skinTestRunning=" + skinTestRunning +
//				", mWifiMode=" + mWifiMode +
//				", mDeviceChannelStatus=" + mDeviceChannelStatus +
//				", mDeviceStatus=" + mDeviceStatus +
//				",isBleConnected=" + isBleConnected() +
//				//",isCameraConnected=" + isCameraConnected() +
//				",isWifiReadyForTrans＝" + isWifiReadyForTrans() +
//				",bleStatus:" + BleHelper.getInstance(mContext).getBleStatus() +
//				",wifiState:" + WifiKfirHelper.getInstance(mContext).getWifiState() +
//				'}';
//		Slog.i(status);
	}
	//}

	@Override
	public void onBatteryChanged(int level) {
		// TODO Auto-generated method stub
		mBatteryLevel = level;
		int tmpStatus = 0;//NOR
		if(level > BleUtils.BATTERY_LEVEL_WARNING){
			tmpStatus = 0;//NOR
		}else if(level <= BleUtils.BATTERY_LEVEL_WARNING && level > BleUtils.BATTERY_LEVEL_LOW){
			tmpStatus = 1;//WARNING
		}else{
			tmpStatus = 2;//LOW
		}
		saveBatteryStatus(tmpStatus);
		mHandler.sendEmptyMessage(MESSAGE_NOTIFY_DEVICE_LINK);
	}
	
	private int mEnvParams = 0;
	
	/**
	 * 
	 * @return 0xFF0000 temp 0x00FF00 rh, 0x0000FF uv
	 */
	public int getEnvParam(){
		if(isBleConnected()){
			return mEnvParams;
		}
		return 0;
	}

	@Override
	public void onEnvParamsResult(byte temp, byte rh, byte uv) {
		// TODO Auto-generated method stub
		mEnvParams = 0;
		mEnvParams = (mEnvParams + (temp & 0xFF));
		mEnvParams = ((mEnvParams << 8) + (rh & 0xFF));
		mEnvParams = ((mEnvParams << 8) + (uv & 0xFF));
		Intent intent = new Intent(BleUtils.ACTION_GET_ENV_PARAM);
		intent.putExtra(BleUtils.KEY_ENV_PARAM, mEnvParams);
		mContext.sendBroadcast(intent);
	}

}
