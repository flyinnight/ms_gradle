package com.dilapp.radar.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.util.LogUtil;
import com.dilapp.radar.util.Slog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.Protocol;
import android.text.TextUtils;
import android.util.Log;

public class WifiKfirHelper {
	
	public static final int SECURITY_NONE = 0;  
    public static final int SECURITY_WEP = 1;  
    public static final int SECURITY_PSK = 2;  
    public static final int SECURITY_EAP = 3; 
    
    private static final int AP_RSSI = -50;
	
	private static WifiKfirHelper mSelf;
	
	private Context mContext;
//	private WifiManager mWifiManager;
//	private WifiInfo mWifiInfo;
	private List<WifiConfiguration> mWifiConfigList;
//	private WifiConfiguration mApConfig;
	
	private List<IWifiKfirHelperCallback> iWifiHelperUiCallbackList = new CopyOnWriteArrayList<IWifiKfirHelperCallback>();
	
	private boolean isPreferAp = false;     //当模式的倾向
	private boolean isRunning = true;  //是否正在测试
	
	private int mPreNetType = -1;  //切换前的网络
	private int mPreWifiEnable = -1; //-1 idle 0 disable 1 enable;
	private int mPreNetId = -1;		//切换前WI-FI 的ID（如果有的话）
	
	private String defaultApSSID = "";
	private String defaultApPWD = "lenovo1234";
	
	private static final int MAX_RECONNECT = 3;
	
	private static WifiState wifiState = WifiState.none;
	
	public enum WifiState{                          //wifi state
        none,enabled,disabled,enableding,disabling,
        connected_error,
        connected_pw,connected_open,
    };
	//API START
    public static WifiKfirHelper getInstance(Context context){
	    	 if(mSelf == null){
	    		 mSelf = new WifiKfirHelper(context);
	    	 }
	    	 return mSelf;
    }
     
    public void addWifiHelperUiCallback(IWifiKfirHelperCallback iWifiHelperUiCallback) {
// 		this.iWifiHelperUiCallback = iWifiHelperUiCallback;
		if(!iWifiHelperUiCallbackList.contains(iWifiHelperUiCallback)){
			iWifiHelperUiCallbackList.add(iWifiHelperUiCallback);
		}
 	}

	public void removeWifiHelperUiCallback(IWifiKfirHelperCallback iWifiHelperUiCallback) {
		iWifiHelperUiCallbackList.remove(iWifiHelperUiCallback);
	}
    
    public void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.setPriority(1000);
		mContext.registerReceiver(receiver, filter);

	}

	public void unRegisterBroadcast() {
		mContext.unregisterReceiver(receiver);
		//clearLastSSid();
	}
     
    public void checkWifiState(){
    		int tempSecurity = getCurrWifiSecurity();
   		if(tempSecurity == 0){       // open 
   			wifiState = WifiState.connected_open;
   			isPreferAp = false;
//   			if(mWifiManager != null){
   				WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
   				WifiInfo info = mWifiManager.getConnectionInfo();
   				int rssi = info.getRssi();
   				if(rssi < AP_RSSI){
   					Slog.e("rssi : "+rssi+" < "+AP_RSSI+" and set prefer AP!!!");
   					isPreferAp = true;
   				}else{
   					isPreferAp = false;
   				}
//   			}
   		}else if(tempSecurity > 0){  // pwd
   			wifiState = WifiState.connected_pw;
//   			if(!wifiState.equals(WifiState.ap_connected)){
//   				wifiState = WifiState.connected_pw;
//   			}
   			isPreferAp = false;
//   			if(mWifiManager != null){
   			WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
   				WifiInfo info = mWifiManager.getConnectionInfo();
   				int rssi = info.getRssi();
   				if(rssi < AP_RSSI){
   					Slog.e("rssi : "+rssi+" < "+AP_RSSI+" and set prefer AP!!!");
   					isPreferAp = true;
   				}else{
   					isPreferAp = false;
   				}
//   			}
   		}else if(tempSecurity < 0){   //wifi 没连接上(可能是open,或者是disabled);
   			WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
   		  int tempState = mWifiManager.getWifiState();  //获取wifi AP状态
               switch (tempState) {
   			case WifiManager.WIFI_STATE_DISABLED:
   				wifiState = WifiState.disabled;
   				break;
   			case WifiManager.WIFI_STATE_ENABLED:
   				Slog.d("checkWifiState WIFI_STATE_ENABLED");
   				wifiState = WifiState.enabled;
   				break;
   			case WifiManager.WIFI_STATE_ENABLING:
   				wifiState = WifiState.enableding;
   				break;

   			default:
   				break;
   			}
               isPreferAp = true;
   		}
//   		String ssid  = getCurrSSID() == null ?" ": getCurrSSID().replace("\"", "") ;
//   		defaultApSSID = SharePreCacheHelper.getDefaultSSid(mContext);
//   		if(defaultApSSID.equals(ssid)){
//   			wifiState = WifiState.ap_connected;
//   		}
   		
    }
    
    public String getCurrSSID() {
// 	   if(mWifiManager == null) return null;
    	WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
 	   WifiInfo info = mWifiManager.getConnectionInfo();
 	   if(info == null) return null;
 	   String ssid = info.getSSID();
// 	   Slog.d("getCurrSSID : "+ssid+"  netid : "+info.getNetworkId()+" ip : "+info.getIpAddress());
 	  if(info.getNetworkId() < 0){
		   Slog.e("invalued ssid : "+ssid+ " has no netID");
		   return null;
	   }
 	   if(ssid != null && ssid.equals("<unknown ssid>")){
 		   ssid = null;
 	   }
 	   if(ssid != null){
 		   ssid = ssid.replace("\"", "");
 	   }
// 	   Slog.d("API getCurrSSID : "+ssid);
// 	   Slog.d("API getCurrSSID other param :"+info.getNetworkId() + " "+info.getIpAddress()+"  "+info.getBSSID());
    	   return ssid;
    }
    
    public String getCurrBSSID(){
//    	if(mWifiManager == null) return null;
    	WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
  	   WifiInfo info = mWifiManager.getConnectionInfo();
  	   if(info == null) return null;
  	   Slog.d("getCurrBSSID : "+info.getBSSID());
  	   return info.getBSSID();
    }
    
    public String getCurrIP(){
//    		if(mWifiManager == null) return null;
    	WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    		WifiInfo info = mWifiManager.getConnectionInfo();
    		if(info == null) return null;
    		String ip = intToIp(info.getIpAddress());
    		return ip;
    }
    
    public WifiState getWifiState(){
  	  Slog.d(" wifiHelper wifistate  is " +wifiState );
  	  return wifiState;
    }
    
    public int getNetworkIdBySSID(String ssid){
    		int result = -1;
    		if(TextUtils.isEmpty(ssid)) return result;
    		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    		List<WifiConfiguration> wifiConfigList = mWifiManager
				.getConfiguredNetworks();
		if (wifiConfigList != null) {
			for (WifiConfiguration wifiConfiguration : wifiConfigList) {
				// 配置过的SSID
				String configSSid = wifiConfiguration.SSID;
				configSSid = configSSid.replace("\"", "");
				if (ssid.equals(configSSid)) {
					result = wifiConfiguration.networkId;
					break;
				}
			}
		} else {
			result = -1;
		}
		return result;
    }
    
    public boolean isPreferAP(){
    		return this.isPreferAp;
    }
    
    public boolean wifiIsOpen(){
    	WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if(mWifiManager != null){
			return mWifiManager.isWifiEnabled();
		}else{
			return false;
		}
	}
    
    public void openWifi() {
    	WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    	int wstatus = mWifiManager.getWifiState();
		if (wstatus != WifiManager.WIFI_STATE_ENABLED 
				&& wstatus != WifiManager.WIFI_STATE_ENABLING) {
//		if (!mWifiManager.isWifiEnabled()) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
					mWifiManager.setWifiEnabled(true);
				}
			}).start();
		}
	}
    
    public void connectAP(String apssid){
			Slog.i("connect ap:" + apssid);
    		if(TextUtils.isEmpty(apssid)){
    			Slog.e("Error connectAP : appsid is Empty!!");
    			return;
    		}
    		String cussid = getCurrSSID();//getCurrSSIDNotCheck();
    		if(cussid != null && cussid.equals(apssid)){
    			Slog.w("Curr has connect AP now : "+cussid);
    			return;
    		}
    		int netID = getNetworkIdBySSID(apssid);
    		if(mPreNetType == -1){
    			mPreNetType = getCurrNetType();
    		}
    		if(mPreWifiEnable == -1){
    			WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    			int wstatus = mWifiManager.getWifiState();
    			if (wstatus == WifiManager.WIFI_STATE_ENABLED 
    					|| wstatus == WifiManager.WIFI_STATE_ENABLING) {
    				mPreWifiEnable = 1;
    			}else{
    				mPreWifiEnable = 0;
    			}
    		}
    		
    		if(mPreNetId == -1){
    			mPreNetId = getNetworkId();
    		}
		Slog.i("connecting ap, mPreNetType:" + mPreNetType + ",mPreNetId:" + mPreNetId);
    		if(mPreNetId == netID){
    			mPreNetId = -1;
    		}
    		if(netID != -1){
				Slog.i("connecting by netId:" + netID);
    			connectWifiByNetID(netID);
    		}else{
				Slog.i("fetching netId...");
				WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    			WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
    			if(mWifiInfo == null){
    				Slog.e("Can not get WifiInfo");
    				wifiState = WifiState.connected_error;
    				return;
    			}
    			if(!apssid.equals(getCurrSSID())){
    				WifiConfiguration mApConfig = getExistConfigBySSID(apssid);
    				if(mApConfig == null){
    					mApConfig = createApConfigInfo(apssid, defaultApPWD, 3);
    				}
    				int wcgID = mWifiManager.addNetwork(mApConfig);
    				if(wcgID == -1){
    					Slog.e("Add ApConfig Error!");
        				wifiState = WifiState.connected_error;
    				}else{
    					connectWifiByNetID(wcgID);
    				}
    			}
    		}
    }
    
    public void resetPreNet(String preAPSSID){
		Slog.d("resetPreNet -- WIFI!,mPreNetType:" + mPreNetType + ",mPreNetId:" + mPreNetId);
		if(mPreNetType == -1){
			Slog.d("Not need reset!!!!");
			mPreNetType = -1;
			mPreNetId = -1;
			String currSSID = getCurrSSIDNotCheck();
			if(preAPSSID != null && currSSID != null && currSSID.equals(preAPSSID)){
				Slog.d("and close AP !!!");
				enableAllIDButAP();
//				disconnectWifi(getNetworkId());
				if(mPreWifiEnable == 0){
					closeWifi();
				}
			}
			mPreWifiEnable = -1;
			return;
		}
		switch(mPreNetType){
		case ConnectivityManager.TYPE_WIFI:
			enableAllIDButAP();
			if(mPreWifiEnable == 0){
				closeWifi();
			}
//			if(mPreNetId >= 0){
//				Slog.d("start reconect preNetId : "+mPreNetId);
//				mWifiManager.enableNetwork(mPreNetId, true);
//			}else{
//				disconnectWifi(getNetworkId());
//				if(mPreWifiEnable == 0){
//					closeWifi();
//				}
//			}
			break;
		case ConnectivityManager.TYPE_MOBILE:
			enableAllIDButAP();
//			disconnectWifi(getNetworkId());
			closeWifi();
			break;
		case -1:
			break;
		default:
//			disconnectWifi(getNetworkId());
//			closeWifi();
			break;
		}
		mPreNetType = -1;
		mPreNetId = -1;
		mPreWifiEnable = -1;
	}
    
    public void removeAPSSID(){
    		String sSSID = SharePreCacheHelper.getDefaultSSid(mContext);
    		if(!TextUtils.isEmpty(sSSID)){
    			WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    			List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
    			if(existingConfigs != null){
    				for (WifiConfiguration existingConfig : existingConfigs) {
    					if (existingConfig.SSID.equals("\"" + sSSID + "\"")) {
    						mWifiManager.removeNetwork(existingConfig.networkId);
    						break;
    					}
    				}
    			}
    		}
    		SharePreCacheHelper.clearDefaultSSID(mContext);
    }
     
	//API END
	
	/********************private********************************/
	
	private WifiKfirHelper(Context context){
		this.mContext = context.getApplicationContext();
//		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		mWifiInfo = mWifiManager.getConnectionInfo();
		defaultApSSID = SharePreCacheHelper.getDefaultSSid(mContext);
//		mApConfig = createApConfigInfo(defaultApSSID, defaultApPWD, 3);
//		WifiConfiguration tempConfig = this.getExistConfigBySSID(defaultApSSID);
//		if (tempConfig == null) {
//			mWifiManager.addNetwork(mApConfig);
//		}
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		boolean result  = mWifiManager.isWifiEnabled();
		if(result){
			wifiState = WifiState.enabled;
		}else{
			wifiState = WifiState.disabled;
		}
	}
	
	private String getCurrSSIDNotCheck(){
//		if(mWifiManager == null) return null;
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	 	   WifiInfo info = mWifiManager.getConnectionInfo();
	 	   if(info == null) return null;
	 	   String ssid = info.getSSID();
	 	   if(ssid != null && ssid.equals("<unknown ssid>")){
	 		   ssid = null;
	 	   }
	 	   if(ssid != null){
	 		   ssid = ssid.replace("\"", "");
	 	   }
	    	   return ssid;
	}
	
	// 指定配置好的网络进行连接
 	private void connectWifiByNetID(int networkId) {
// 		if(mWifiManager != null ){
 			WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
 		    boolean result =  mWifiManager.enableNetwork(networkId, true);
 		    if(!result){
 				Slog.e(" connectApByNetWorkId  connect is error");
// 				handler.removeMessages(AP_RECONNECT);
// 		    		handler.sendEmptyMessage(AP_RECONNECT);
 				/*if(iWifiHelperUiCallback != null ){
 					iWifiHelperUiCallback.onWifiDisconnected();
 				}*/
				for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
					callback.onWifiDisconnected();
				}
 		    }else{
	 		    	Slog.e(" connectApByNetWorkId  connect is success");
	 		    	wifiState = WifiState.connected_pw;
	 		    	/*if(iWifiHelperUiCallback != null ){
	 					iWifiHelperUiCallback.onWifiConnecting();
	 			}*/
				for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
					callback.onWifiConnecting();
				}
 		    }
// 		}		
 	}
	
	/**
	 * connect wifi
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	private WifiConfiguration createApConfigInfo(String SSID, String Password,int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
//		WifiConfiguration tempConfig = this.getExistConfigBySSID(SSID);
//		if (tempConfig != null) {
//			mWifiManager.removeNetwork(tempConfig.networkId);
//		}
		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}else if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}else if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			//config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			//config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			//config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			//config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		//	config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			//config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.priority = 10;
			config.allowedProtocols.set(Protocol.WPA);
			config.allowedProtocols.set(Protocol.RSN);
		}
		// 接受ip数据包，配置指定的wifi配置对象
		new StaticIpSet(mContext).confingStaticIp(config);
		
		return config;
	}
	
	private int getCurrWifiSecurity(){
		String currentSSid = getCurrSSID();
//	   WifiInfo info = mWifiManager.getConnectionInfo();  
//	    //当前连接SSID  
//	   String currentSSid =info.getSSID();  
//       currentSSid = currentSSid.replace("\"", "");  
//       Slog.i("currentSSid  : "+currentSSid);
       if(TextUtils.isEmpty(currentSSid)) return -1;
      // 得到配置好的网络连接  
       WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
      List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();  
      if(wifiConfigList != null){
	      for (WifiConfiguration wifiConfiguration : wifiConfigList) {  
	          //配置过的SSID  
	          String configSSid = wifiConfiguration.SSID;  
	          configSSid = configSSid.replace("\"", ""); 
	          //比较networkId，防止配置网络保存相同的SSID  
	          //
	          if (currentSSid.equals(configSSid) && getNetworkId() == wifiConfiguration.networkId) {
	              return getSecurityByConfig(wifiConfiguration);
	          }
	      }
     }
      return -1;
    }
	
	private static int getSecurityByConfig(WifiConfiguration config) {  
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {  
            return SECURITY_PSK;  
        }  
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {  
            return SECURITY_EAP;  
        }  
        if(config.allowedKeyManagement.get(KeyMgmt.NONE)){
        	return SECURITY_NONE;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;  
    }
	
	private WifiConfiguration getExistConfigBySSID(String SSID) {
		long start = System.currentTimeMillis();
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
	   if(existingConfigs != null){
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
	  }
		return null;
	}
	
	private void enableAllIDButAP(){
		String mAPSSID = SharePreCacheHelper.getDefaultSSid(mContext);
		if(TextUtils.isEmpty(mAPSSID)){
			mAPSSID = "";
		}
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		   if(existingConfigs != null){
			for (WifiConfiguration existingConfig : existingConfigs) {
				if (existingConfig.SSID.equals("\"" + mAPSSID + "\"")) {
					mWifiManager.disableNetwork(existingConfig.networkId);
				}else{
					mWifiManager.enableNetwork(existingConfig.networkId, false);
				}
			}
		  }
	}
	
	// 得到连接的ID
	private int getNetworkId() {
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if(mWifiManager == null) return -1;
		WifiInfo info = mWifiManager.getConnectionInfo();
		if(info == null) return -1;
		return info.getNetworkId();
	}
	
	private int getCurrNetType(){
		int netType = -1;
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	    if (networkInfo == null) {
	        return netType;
	    }
	    netType = networkInfo.getType();
	    return netType;
    }
	
	// 断开指定ID的网络
	private void disconnectWifi(int netId) {
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if(mWifiManager == null || netId == -1) return;
		mWifiManager.disableNetwork(netId);
//		mWifiManager.disconnect();
	}
	
	/**
	 * close wifi
	 */
	private void closeWifi() {
		WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		int wstatus = mWifiManager.getWifiState();
		if (wstatus == WifiManager.WIFI_STATE_ENABLED 
				|| wstatus == WifiManager.WIFI_STATE_ENABLING) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
					mWifiManager.setWifiEnabled(false);
				}
			}).start();
		}
	}
	
	private String intToIp(int paramIntip) {
		return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
   }
	
	
	
	/*************************状态监听************************************/
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		if(isRunning){                            //在测试模块
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager cm = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isConnected()&& info.getState() == NetworkInfo.State.CONNECTED
						&& info.getType() == ConnectivityManager.TYPE_WIFI) {
					/*if(iWifiHelperUiCallback != null){
						iWifiHelperUiCallback.onWifiConnectSuccess(getCurrSSID());
					}*/
					for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
						callback.onWifiConnectSuccess(getCurrSSID());
					}
				} else {// wifi 断开了 TODO
					/*if(iWifiHelperUiCallback != null){
						iWifiHelperUiCallback.onWifiDisconnected();
					}*/
					for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
						callback.onWifiDisconnected();
					}
			  }// wifi打开或关闭
				checkWifiState();
			} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) { 
				int wifiStateNum = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiStateNum) {
				case WifiManager.WIFI_STATE_DISABLED:
					wifiState  = WifiState.disabled;
					Slog.d("WIFI_STATE_DISABLED");
					/*if(iWifiHelperUiCallback != null ){
						 iWifiHelperUiCallback.wifiDisabled();
					 }*/
					for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
						callback.wifiDisabled();
					}
					break;
				case WifiManager.WIFI_STATE_DISABLING:
					Slog.d("WIFI_STATE_DISABLING");
					if(/*iWifiHelperUiCallback != null && */wifiState != WifiState.disabling){
						wifiState  = WifiState.disabling;
//						iWifiHelperUiCallback.wifiDisabled();
						for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
							callback.wifiDisabled();
						}
					}
					wifiState = WifiState.disabling;
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					Slog.d("WIFI_STATE_ENABLING");
					 
					 /*if(iWifiHelperUiCallback != null ){
						 iWifiHelperUiCallback.wifiEnableding();
					 }*/
					for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
						callback.wifiEnableding();
					}
					 wifiState = WifiState.enableding;
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					Slog.d("WIFI_STATE_ENABLED");
					/*if(iWifiHelperUiCallback != null ){
						wifiState = WifiState.enabled;
						iWifiHelperUiCallback.wifiEnable();
					}*/
					for(IWifiKfirHelperCallback callback:iWifiHelperUiCallbackList){
						wifiState = WifiState.enabled;
						callback.wifiEnable();
					}
					wifiState = WifiState.enabled;
					break;
				}
			}
		}//END isRunning
	  }	
	};
}
