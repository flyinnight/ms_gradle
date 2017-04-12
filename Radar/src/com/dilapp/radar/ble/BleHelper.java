package com.dilapp.radar.ble;

import java.util.List;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.cache.SharePrefUtil;
import com.dilapp.radar.util.Slog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/***
 * 
 * @author hj
 * 
 * @function ble helper
 * 
 * @time 2015/04/29
 * 
 */
public class BleHelper implements IBleCmdCallback{

	private static BluetoothManager mBluetoothManager = null;
	private static BluetoothAdapter mBluetoothAdapter = null;
	private static BluetoothDevice mBluetoothDevice = null;
	private static BluetoothGatt mBluetoothGatt = null;
	
//	private BluetoothGattService mBluetoothSelectedService = null;
//	private List<BluetoothGattService> mBluetoothGattServices = null;
	private BluetoothGattCharacteristic mWriteCharacteristic = null;
	
	private Context mContext = null;
	private IBleHelperUiCallbacks mUiCallbacks;
	private String mDeviceAddress = "";                  //当前连接的MAC地址
	private  String connectAddress = ""; //连接上的MAC 地址
	private static BleHelper bleHelper = null;
	
	private boolean CMD_READY = false;
	private static long LAST_CMD_TIME = 0;
	private static int LAST_CMD_ID = -1;
	private static long CMD_TIMEOUT = 2 * 1000;
	
	private static long LAST_WIFI_MODE_CMD = 0;
	private static final long MIN_WIFI_MODE_TIME = 1000;
	
	private PackHelper mPackHelper;
	
	public enum BleStatus{                      //ble status
		none,disabled,
		enabled,enableding,
		connecting,connected,
		disconnect
	
	};
	private static BleStatus mBleStatus = BleStatus.none;
	
//	private Handler mHandler = new Handler();
	
	private static boolean startTest = false;    //开始测试的标志位
	private static boolean openBle = false;    // 应用打开ble
	
	//API STARTED
	
	public static BleHelper getInstance(Context context){
		if(bleHelper == null){	
			bleHelper = new BleHelper(context);
			if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
				mBleStatus = BleStatus.enabled;
			}else{
				mBleStatus = BleStatus.disabled;
			}
		}
		
		return bleHelper;
		
	}
	
	/**
     * check ble hardware available
     * @return
     */
	public boolean checkBleHardwareAvailable( ) {
		final BluetoothManager manager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		if(manager == null) return false;
		final BluetoothAdapter adapter = manager.getAdapter();
		if(adapter == null) return false;
		
		boolean hasBle = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		return hasBle;
	}
	
	/**
	 * check bt is enable
	 * @return
	 */
	public boolean isBtEnabled() {
		initBleAdapter();
		if(mBluetoothAdapter != null){
			 return mBluetoothAdapter.isEnabled();
		}else{
			return false;
		}
	}
	
	/**
	 *  open bt
	 */
	public void openBt(){
		initBleAdapter();
		if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mBluetoothAdapter.enable();
					openBle = true;
				}
			}).start();
		}
		
		checkBleStatus();
//		if(mBluetoothAdapter.isEnabled()){
//			if(mBleStatus == BleStatus.none || mBleStatus == BleStatus.disabled){
//				mBleStatus = BleStatus.enabled;
//			}
//		}
	}
	
	/**
	 * start scanning for BT LE devices around
	 */
	@SuppressWarnings("deprecation")
	public boolean  startScanning( ) {
		   boolean  result  = false;
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
           result = mBluetoothAdapter.startLeScan(mDeviceFoundCallback);
		}
		return result;
		
		
	}
	
	/**
	 * stops current scanning
	 */
	@SuppressWarnings("deprecation")
	public void stopScanning( ) {
		if(mBluetoothAdapter != null){
			Log.i("hj", "blehelper stopScanning");
		   mBluetoothAdapter.stopLeScan(mDeviceFoundCallback);	
		}
	}
	
	/**
	  *  connect to the device with specified  MAC address
	  * @param deviceAddress   MAC address
	  * @return
	  */
   public boolean connect(final String deviceAddress) {
	   Slog.d("BLE start connect : "+deviceAddress);
		if (mBluetoothAdapter == null || TextUtils.isEmpty(deviceAddress)){
			Slog.e("Error connect mBluetoothAdapter null or address is Empty : "+deviceAddress);
			return false;
		}
		mDeviceAddress = deviceAddress;
		if(mBluetoothGatt != null){
			if(mBluetoothGatt.getDevice() != null
					&& mDeviceAddress.equals(mBluetoothGatt.getDevice().getAddress())
					&& mBleStatus == BleStatus.connected){
				Slog.e("Ble on contected and do not need restart! "+mBluetoothGatt.getDevice().getName());
				return true;
			}else{
				Slog.d("has another GATT and close the last one!");
				mBluetoothGatt.close();
				mBluetoothGatt = null;
				mBleStatus = BleStatus.disconnect;
				SharePreCacheHelper.saveBleConnectStatus(mContext, false);
			}
		}
//		if (mBluetoothGatt != null && mBluetoothGatt.getDevice().getAddress()
//				.equals(deviceAddress)) {
////			Log.e("hj", "blehlper device is connected");
//			boolean result = mBluetoothGatt.connect();
//			if(result){
////				if(mUiCallbacks != null){
////	            	mUiCallbacks.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);
////				}
//			}else{
////				if(mUiCallbacks != null){
////				 mUiCallbacks.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
////				}
//			}
//			return result;
//		} 
		if(mBluetoothGatt == null) {
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
			if (mBluetoothDevice == null) {
				Slog.e("Error connect : mBluetoothDevice == NULL");
				return false;
			}
			mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false,mBleCallback);
			if (mBluetoothGatt != null) {
				return mBluetoothGatt.connect();
			} else {
				Slog.e("Error connect : mBluetoothGatt == NULL!");
				return false;
			}
		}
		return true;
   } 
   
   public void disconnect() {
	   if (mBluetoothAdapter == null || mBluetoothGatt == null) {
    	   		Slog.w("BluetoothAdapter not initialized");
    	   		return;
       }
	   Slog.d("start disconnect mBluetoothGatt");
       mBluetoothGatt.disconnect();
       SharePreCacheHelper.saveBleConnectStatus(mContext, false);
   }
   
   public void registerBrocast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mContext.registerReceiver(mReceiver, filter);
	}
	
	public  void unRegisterBrocast(){
		mContext.unregisterReceiver(mReceiver);
	}
	
	public BleStatus getBleStatus(){
		return mBleStatus;
	}
	
	public void setUiCallbacks(IBleHelperUiCallbacks uiCallbacks) {
		this.mUiCallbacks = uiCallbacks;
	}
	
	/**
	 * 开始BLE常连接
	 * @param status
	 */
	public void startBleKeep(){
		boolean hasPaired = SharePreCacheHelper.getPairStatus(mContext);
		if(hasPaired){
			startTest = true;
		}else{
			Slog.e("Can not start BleKeep : haspaired == "+hasPaired);
		}
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && hasPaired){
			String macAddress = SharePreCacheHelper.getBleMacAddress(mContext);
			if(getBleStatus() != BleStatus.connected || mBluetoothGatt == null){
				if(macAddress != null && !macAddress.isEmpty()){
					connect(macAddress);
				}
			}else{
				BluetoothDevice device = mBluetoothGatt.getDevice();
				if(device == null 
						|| TextUtils.isEmpty(device.getAddress())
						|| !(device.getAddress().equals(macAddress))){
					Slog.e("Error device address "  +" target:"+macAddress);
					if(macAddress != null && !macAddress.isEmpty()){
						connect(macAddress);
					}
				}
			}
		}
	}
	
	/**
	 * 结束BLE常连接
	 */
	public void endBleKeep(){
		startTest = false;
		close();
//		if(openBle){
//			closeBt();
//		}
	}
	
	public boolean isInTestMode(){
		return startTest;
	}
	
	/**
	 * 绑定请求
	 * @return
	 */
	public void startConnRequest(){
		Slog.i("startConnRequest !!!!!!!!!!!!!");
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_connectBle, null);
		if(cmds != null && cmds.size() == 1){
			byte[] mdata = cmds.get(0);
			String mlog = "";
			for(int i=0;i<mdata.length;i++){
				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
			}
			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startConnRequest by Cmd value Error!");
		}
	}
	
	public void startGetDeviceStatus(){
		Slog.i("startGetDeviceStatus !!!!!!!!!!!!!");
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_bleStatus, null);
		if(cmds != null && cmds.size() == 1){
			byte[] mdata = cmds.get(0);
			String mlog = "";
			for(int i=0;i<mdata.length;i++){
				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
			}
			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startGetDeviceStatus by Cmd value Error!");
		}
	}
	
	public synchronized void startWifiBySTA(String ssid, String pwd){
		Slog.i("startWifiBySTA : "+ssid+"  "+pwd+"!!!!!!!!!!!!!");
		if((System.currentTimeMillis() - LAST_WIFI_MODE_CMD) < MIN_WIFI_MODE_TIME){
			Slog.e("Has Send startWifiBySTA and ignore !!!");
			return;
		}
		LAST_WIFI_MODE_CMD = System.currentTimeMillis();
		WifiCommandPacket mPacket = new WifiCommandPacket(BleContent.cmd_staCommand);
		mPacket.setSSidAndPwd(ssid, pwd);
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_staCommand, mPacket);
		if(cmds != null && cmds.size() >= 1){
			for(byte[] data : cmds){
				byte[] mdata = data;
				String mlog = "";
				for(int i=0;i<mdata.length;i++){
					mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
				}
				Slog.i(mlog);
				startWriteCmd(data);
			}
		}else{
			Slog.e("Error startWifiBySTA by Cmd value Error!");
		}
	}
	
	public void startWifiByAP(){
		Slog.i("startWifiByAP !!!!!!!!!!!!!");
		if((System.currentTimeMillis() - LAST_WIFI_MODE_CMD) < MIN_WIFI_MODE_TIME){
			Slog.e("Has Send startWifiByAP and ignore !!!");
			return;
		}
		LAST_WIFI_MODE_CMD = System.currentTimeMillis();
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_apCommand, null);
		if(cmds != null && cmds.size() == 1){
			byte[] mdata = cmds.get(0);
			String mlog = "";
			for(int i=0;i<mdata.length;i++){
				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
			}
			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startWifiByAP by Cmd value Error!");
		}
	}
	
	public void startGetWifiStatus(){
		Slog.i("startGetWifiStatus !!!!!!!!!!!!!");
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_wifiStatus, null);
		if(cmds != null && cmds.size() == 1){
			byte[] mdata = cmds.get(0);
			String mlog = "";
			for(int i=0;i<mdata.length;i++){
				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
			}
			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startGetWifiStatus by Cmd value Error!");
		}
	}
	
	public void startPresetWifiMode(int status){
		Slog.i("startGetWifiStatus !!!!!!!!!!!!!  "+status);
		WifiPreModePacket mPacket = new WifiPreModePacket(status);
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_prepareWifiMode, mPacket);
		if(cmds != null && cmds.size() == 1){
			byte[] mdata = cmds.get(0);
			String mlog = "";
			for(int i=0;i<mdata.length;i++){
				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
			}
			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startPresetWifiMode by Cmd value Error!");
		}
	}
	
	public void startSetTimeCheck(){
		Slog.i("startSetTimeCheck !!!!!!!!!!!!!!!");
		TimeCheckPacket mPacket = new TimeCheckPacket();
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_time_check, mPacket);
		if(cmds != null && cmds.size() == 1){
//			byte[] mdata = cmds.get(0);
//			String mlog = "";
//			for(int i=0;i<mdata.length;i++){
//				mlog += Integer.toHexString(mdata[i] & 0xFF)+" ";
//			}
//			Slog.i(mlog);
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startSetTimeCheck by Cmd value Error!");
		}
	}
	
	public void startGetBatteryLevel(){
		Slog.i("startGetBatteryLevel");
		BatteryReqPacket mPacket = new BatteryReqPacket();
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_get_battery_level, mPacket);
		if(cmds != null && cmds.size() == 1){
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startGetBatteryLevel by Cmd value Error!");
		}
	}
	
	public void startGetEnvParams(){
		Slog.i("startGetEnvParams");
		EnvReqPacket mPacket = new EnvReqPacket();
		List<byte[]> cmds = mPackHelper.setPacket(BleContent.cmd_get_env_params, mPacket);
		if(cmds != null && cmds.size() == 1){
			startWriteCmd(cmds.get(0));
		}else{
			Slog.e("Error startGetEnvParams by Cmd value Error!");
		}
		
	}
	
	public void startGetEnvParamsWhenFree(){
		if(LeTransformQueue.size() > 0){
			Slog.e("BLE is busy and ignore get EnvParams!!");
		}else{
			startGetEnvParams();
		}
	}
	
	//API END
	
	/****************************private************************************/
	
	private static final int MSG_RESEND_CMD = 1;
	private static final int MSG_CMD_WAIT_TIMEOUT = 2;
	
	private static final long CMD_WAIT_TIMEOUT = 3 * 1000;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_RESEND_CMD:
				handleWriteCmd();
				break;
			case MSG_CMD_WAIT_TIMEOUT:
				handleCmdTimeOut();
				break;
			}
		}
		
	};
	
	//Private START
	private BleHelper(Context context){
		this.mContext = context.getApplicationContext();
		initBleAdapter();
		mPackHelper = PackHelper.getInstance();
		mPackHelper.setCallback(this);
		//registerBrocast(mContext);
	}
	
	private void handleCmdTimeOut(){
		Slog.e("Handle Ble Cmd TimeOut NOW !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		disconnect();
		close();
	}
	
	private synchronized void startWriteCmd(byte[] value){
		if(value != null){
			LeTransformQueue.offerData(value);
		}
		if(CMD_READY || (System.currentTimeMillis() - LAST_CMD_TIME) > CMD_TIMEOUT){
			mHandler.removeMessages(MSG_RESEND_CMD);
			mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
//			handleWriteCmd();
		}else{
			Slog.e("can not write Cmd by CMD_READY == false!!!");
		}
	}
	
	private synchronized void handleWriteCmd(){
		if(LeTransformQueue.size() <= 0) {
			Slog.e("handle Write ble Cmd : Queue is Empty now!");
			return;
		}
		if(mBleStatus != BleStatus.connecting && mBleStatus != BleStatus.connected){
			Slog.e("Error handleWriteCmd BleStatus is Not connect");
			LeTransformQueue.clearData();
			CMD_READY = false;
			return;
		}
		Slog.d("start handleWriteCmd size: "+LeTransformQueue.size());
		if(CMD_READY || (System.currentTimeMillis() - LAST_CMD_TIME) > CMD_TIMEOUT){
			CMD_READY = false;
			LAST_CMD_TIME = System.currentTimeMillis();
			mHandler.removeMessages(MSG_RESEND_CMD);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Slog.d("start write ble cmd!");
					if(mBluetoothAdapter == null 
							|| mBluetoothGatt == null 
							|| mWriteCharacteristic == null){
						Slog.e("Error writeCmd by some NULL : "+mBluetoothAdapter+" : "+mBluetoothGatt+" : "+mWriteCharacteristic);
						LeTransformQueue.clearData();
						CMD_READY = true;
						mHandler.removeMessages(MSG_RESEND_CMD);
						mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 500);
						return;
					}
					try{
						if(LeTransformQueue.size() > 0 && null != mWriteCharacteristic){
							byte[] value = null;//= LeTransformQueue.pollData();
							do{
								value = LeTransformQueue.peekData();//LeTransformQueue.pollData();
								if ( value == null) {
									Slog.e("Error writeCmd by values is NULL !");
//									CMD_READY = true;
								}else{
									int cmds = ((value[1]&0xF0) >> 4);
									Slog.e("start write cmd : "+cmds+"  total : "+((value[2] & 0xF0) >> 4)+" curr : "+(value[2] & 0x0F));
									mWriteCharacteristic.setValue(value);
									boolean result = mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
									if(!result){
										Slog.e("writeCharacteristic Error : return false!");
										CMD_READY = true;
										mHandler.removeMessages(MSG_RESEND_CMD);
										mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 1000);
									}else{
//										int cmd = ((value[1] & 0xF0) >> 4);
										if(cmds == 8 || cmds == 10){//preset or check time
											Slog.w("preset or check time cmd !!!!!!!");
											CMD_READY = true;
											mHandler.removeMessages(MSG_RESEND_CMD);
											mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 500);
										}else{
											mHandler.removeMessages(MSG_RESEND_CMD);
											mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, CMD_TIMEOUT);
										}
										
										if( cmds == BleContent.cmd_bleStatus){
											mHandler.removeMessages(MSG_CMD_WAIT_TIMEOUT);
											mHandler.sendEmptyMessageDelayed(MSG_CMD_WAIT_TIMEOUT, CMD_WAIT_TIMEOUT);
										}
										LeTransformQueue.pollData();
									}
								}
							}while(value != null && (((value[2] & 0xF0) >> 4) != (value[2] & 0x0F)));
							
							
						}//END if
					}catch(Exception e){
						CMD_READY = true;
						Slog.e("Error handleWriteCmd : ",e);
						mHandler.removeMessages(MSG_RESEND_CMD);
						mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 1000);
					}
					Slog.d("handleWriteCmd Thread End : "+LeTransformQueue.size());
//					CMD_READY = true;
				}
			}).start();
		}else{
			Slog.e("handle Write ble Cmd ignore : "+CMD_READY +"  "+(System.currentTimeMillis() - LAST_CMD_TIME));
		}
	}
	
	private void initBleAdapter() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		}
		if (mBluetoothAdapter == null){
			mBluetoothAdapter = mBluetoothManager.getAdapter();
		}
		
	}
	
//	public boolean bleIsOpen(){
//		if(mBluetoothAdapter != null){
//		 return mBluetoothAdapter.isEnabled();
//		}else{
//			return false;
//		}
//	}
	
	/**
	 * close bt
	 */
	private void closeBt(){
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.disable();
			openBle = false;
		}
		mBleStatus = BleStatus.disabled;
	} 
	
	private void checkBleStatus(){
		if(mBluetoothAdapter == null){
			mBleStatus = BleStatus.disabled;
		}else if(mBluetoothAdapter != null && mBluetoothGatt == null){
			if(mBluetoothAdapter.isEnabled()){
				mBleStatus = BleStatus.enabled;
			}else{
				mBleStatus = BleStatus.disabled;
			}
		}else if(mBluetoothAdapter != null && mBluetoothGatt != null){
			if(mWriteCharacteristic != null){
				mBleStatus = BleStatus.connected;
			}else{
				mBleStatus = BleStatus.disconnect;
			}
		}
	}
    
//    private void initService(){
//    	if(mBluetoothGatt == null) return;
//    	mBluetoothSelectedService = mBluetoothGatt.getService(UUID.fromString("uuid"));
//    	BluetoothGattCharacteristic characteristic = mBluetoothSelectedService.getCharacteristic(UUID.fromString("oo"));
//    	characteristic.setValue("xx");
//    	mBluetoothGatt.writeCharacteristic(characteristic); 
//    }
   /**
    * diconnect
    */
//    private void diconnect() {
//    	if(mBluetoothGatt != null) mBluetoothGatt.disconnect();
//    	if(mUiCallbacks != null){
//    		Log.i("hj"," ble diconnect");
//    		mUiCallbacks.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
//    	}
//    }

    /***
     * close GATT client completely
     */
    private void close() {
    	Slog.w("close mBluetoothGatt!!!!");
    	if(mBluetoothGatt != null){ 
    		mBluetoothGatt.close();
    		mBleStatus = BleStatus.disconnect;
    	}
    	mBluetoothGatt = null;
    } 
    
//    private void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
//
//		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//			return;
//		}
//		mBluetoothGatt.writeCharacteristic(characteristic);
///*
//		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("xxx"));
//		descriptor.setValue("11".getBytes());*/
//		
//	}

	private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}
    
	
    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
            boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
		return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		
    }
    
//    private List<BluetoothGattService> getSupportedGattServices() {
//        if (mBluetoothGatt == null) return null;
//
//        return mBluetoothGatt.getServices();
//    }
    
    private void doFiltedService( List<BluetoothGattService> supportedGattServices) {
        if (supportedGattServices == null){
            return;
        }
        for (BluetoothGattService gattService : supportedGattServices) {
            if (KraitGattAttribute.KRAIT_DEVICE.equals(gattService.getUuid()
                    .toString())) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                        .getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                    Log.e("aker","a characteristic  "+gattCharacteristic.getUuid().toString());
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        setCharacteristicNotification( gattCharacteristic, true);
                              
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    	readCharacteristic(gattCharacteristic);
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
                            && (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)!= 0 ) {
                        mWriteCharacteristic = gattCharacteristic;
                        Slog.d("find mWriteCharacteristic and restart cmd queue!");
                        mHandler.removeMessages(MSG_RESEND_CMD);
                        mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
//                        handleWriteCmd();
                    }
                }
            }
        }
    }
    
    
    
    
	
    /*****************************************************回调****************************************/
    
    
    
	 /**
	  * defines callback for scanning results
	  */
    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	if(mUiCallbacks != null){
        		mUiCallbacks.uiDeviceFound(device, rssi, scanRecord);
        	}
        }
    };

    
    /* callbacks called for any action on particular Ble Device */
    private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//        if(startTest){
        	if (newState == BluetoothProfile.STATE_CONNECTED) {
        		SharePreCacheHelper.saveBleConnectStatus(mContext, true);
        		Slog.w("BLE STATE_CONNECTED  and reset BleStatus : Connecting!!!!!!!!!!");
//        		Toast.makeText(mContext, "BLE STATE_CONNECTED!", Toast.LENGTH_SHORT).show();
//            	mBleStatus = BleStatus.connecting;
//            	CMD_READY = true;
            	mBluetoothGatt.discoverServices();
//            	if(connectAddress != mDeviceAddress ){
//	            	  if(!TextUtils.isEmpty(mDeviceAddress)){   //save macAddress
////	            		  SharePreCacheHelper.saveBleMacAddress(mContext, mDeviceAddress);
//	            		  connectAddress = mDeviceAddress;
//	            	  }
//          	}
//            	if(mUiCallbacks != null){
//            	  mUiCallbacks.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);
//            	}
            	//TODO
            	//mBluetoothGatt.readRemoteRssi();
            	
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	Slog.w("BLE STATE_DISCONNECTED !!!!!!!!!!");
            	SharePreCacheHelper.saveBleConnectStatus(mContext, false);
//            	Toast.makeText(mContext, "BLE STATE_DISCONNECTED!", Toast.LENGTH_SHORT).show();
            	mBleStatus = BleStatus.disconnect;
            	mDeviceAddress = "";
            	connectAddress = "";
            	Slog.w("BLE STATE_DISCONNECTED and clear CMD!");
            	LeTransformQueue.clearData();
            	CMD_READY = false;
            	mWriteCharacteristic = null;
            	if(mUiCallbacks != null){
            	  mUiCallbacks.uiDeviceDisconnected(mBluetoothGatt, mBluetoothDevice);
            	}
            }
//          }	
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
	            	List<BluetoothGattService> mList = gatt.getServices();
	            	doFiltedService(mList);//add by kfir
	            	Slog.w("Services Discover SUCCESS and set to connected!!!!!!");
	            	mBleStatus = BleStatus.connected;
	            	CMD_READY = true;
	            	if(connectAddress != mDeviceAddress ){
		            	  if(!TextUtils.isEmpty(mDeviceAddress)){   //save macAddress
//		            		  SharePreCacheHelper.saveBleMacAddress(mContext, mDeviceAddress);
		            		  connectAddress = mDeviceAddress;
		            	  }
	          	}
	            	if(mUiCallbacks != null){
	            	  mUiCallbacks.uiDeviceConnected(mBluetoothGatt, mBluetoothDevice);
	            	}
            }else{
            	Slog.e("Error onServicesDiscovered : "+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status)
                                         
        {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	//TODO
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
        		Slog.d("onCharacteristicChanged : "+characteristic.getUuid());
        		mPackHelper.splitBodys(characteristic.getValue());
//        	mUiCallbacks.uiGotNotification(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic);
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        	if(status == BluetoothGatt.GATT_SUCCESS) {
        		Slog.d("onCharacteristicWrite Success!");
//        		CMD_READY = true;
//        		handleWriteCmd();
        		//mUiCallbacks.uiSuccessfulWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description);
        	}
        	else {
        		Slog.d("onCharacteristicWrite Failed!");
//        		CMD_READY = true;
//        		handleWriteCmd();
        		//mUiCallbacks.uiFailedWrite(mBluetoothGatt, mBluetoothDevice, mBluetoothSelectedService, characteristic, description + " STATUS = " + status);
        	}
        };
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        	if(status == BluetoothGatt.GATT_SUCCESS) {
//        		mUiCallbacks.uiNewRssiAvailable(mBluetoothGatt, mBluetoothDevice, rssi);
        	}
        };
    };
    
   /***************************************广播监听*******************************************************/ 

  //ble的状态
  	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
  		
  		@Override
  		public void onReceive(Context context, Intent intent) {
  		//	Log.i("hj", "ble STATE_change");
//  		if(startTest){
  			String  action  = intent.getAction();
  			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
  				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
  				switch (state) {
  				case BluetoothAdapter.STATE_TURNING_ON:
  					if(mUiCallbacks != null && mBleStatus != BleStatus.enableding){
  						mBleStatus = BleStatus.enableding;
  						mUiCallbacks.bleEnabling();
  					}
  					 mBleStatus = BleStatus.enableding;
  				break;
  				case BluetoothAdapter.STATE_ON:
  					if(mUiCallbacks != null && mBleStatus != BleStatus.enabled){
  						mBleStatus = BleStatus.enabled;
  						mUiCallbacks.bleEnable();
  					}
  				  mBleStatus = BleStatus.enabled;
  					break;
  				case BluetoothAdapter.STATE_TURNING_OFF:
  					break;
  				case BluetoothAdapter.STATE_OFF:
  					
  					if(mUiCallbacks != null && mBleStatus != BleStatus.disabled){
  						 mBleStatus = BleStatus.disabled;
  						mUiCallbacks.bleDisable( );  
  					}
  					 mBleStatus = BleStatus.disabled;
  					break;
  					
  					
  				default:
  					break;
  				 }
  			}
//  		}
  	  }
  	};
//	
//	private IBleHelperUiCallbacks getUiCallbacks() {
//		return mUiCallbacks;
//	}	    
//
//	private BluetoothManager getManager() {
//		return mBluetoothManager;
//	}
//
//	private BluetoothAdapter getAdapter() {
//		return mBluetoothAdapter;
//	}
//
//	private BluetoothDevice getDevice() {
//		return mBluetoothDevice;
//	}
//
//	private BluetoothGatt getGatt() {
//		return mBluetoothGatt;
//	}
//
//	private BluetoothGattService getCachedService() {
//		return mBluetoothSelectedService;
//	}
//
//	private List<BluetoothGattService> getCachedServices() {
//		return mBluetoothGattServices;
//	}

	@Override
	public void onConnectRequestResult(int result) {
		// TODO Auto-generated method stub
		Slog.d("onConnectRequestResult : "+result);
		CMD_READY = true;
		mHandler.removeMessages(MSG_RESEND_CMD);
		mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
		boolean confirm = (result > 0) ? true : false;
		if (mUiCallbacks != null){
			mUiCallbacks.uiDeviceConfirm(mBluetoothGatt, mBluetoothDevice, confirm);
		}
	}

	@Override
	public void onWifiStatusResult(int mode, String ssid, String ip, int errorcode,
			int cmd) {
		// TODO Auto-generated method stub
		Slog.d("onWifiStatusResult : "+mode+" "+ssid+" "+ip+" "+errorcode+" "+cmd);
		if(cmd != BleContent.cmd_wifiStatusCallBack){
			CMD_READY = true;
			mHandler.removeMessages(MSG_RESEND_CMD);
			mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
		}
		if(mUiCallbacks != null){
			mUiCallbacks.onWifiStatusResult(mode, ssid, ip, errorcode);
		}
	}

	@Override
	public void onDeviceStatusResult(int net_status, int power_status,int cmd) {
		// TODO Auto-generated method stub
		Slog.d("onDeviceStatusResult : "+net_status+" "+power_status+"  "+cmd);
		if(cmd != BleContent.cmd_bleStatusCallback){
			if(cmd != BleContent.cmd_wifiStatusCallBack){
				CMD_READY = true;
				mHandler.removeMessages(MSG_RESEND_CMD);
				mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
			}
		}
		mHandler.removeMessages(MSG_CMD_WAIT_TIMEOUT);
		if(mUiCallbacks != null){
			mUiCallbacks.onDeviceStatusResult(net_status, power_status);
		}
	}
	
	@Override
	public void onPreSetWifiModeResult(){
		Slog.d("onPreSetWifiModeResult !!!!!");
		CMD_READY = true;
		mHandler.removeMessages(MSG_RESEND_CMD);
		mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
	}

	@Override
	public void onPhotoCmdFromDevice() {
		// TODO Auto-generated method stub
		Slog.d("onPhotoCmdFromDevice !!");
		if(mUiCallbacks != null){
			mUiCallbacks.onPhotoCmdFromDevice();
		}
	}

	@Override
	public void onBatteryLevelChanged(int level, int cmd) {
		// TODO Auto-generated method stub
		Slog.i("onBatteryLevelChanged : "+level+"  "+cmd);
		if(mUiCallbacks != null){
			if(cmd != BleContent.cmd_report_battery_level){
				CMD_READY = true;
				mHandler.removeMessages(MSG_RESEND_CMD);
				mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
			}
			mUiCallbacks.onBatteryChanged(level);
		}
	}

	@Override
	public void onEnvParamsResult(byte temp, byte rh, byte uv, int cmd) {
		// TODO Auto-generated method stub
		Slog.i("onEnvParamsResult : "+temp+" "+rh+"  "+uv+"  "+cmd);
		if(mUiCallbacks != null){
			CMD_READY = true;
			mHandler.removeMessages(MSG_RESEND_CMD);
			mHandler.sendEmptyMessageDelayed(MSG_RESEND_CMD, 50);
			mUiCallbacks.onEnvParamsResult(temp, rh, uv);
		}
	}

}
