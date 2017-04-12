package com.dilapp.radar.ui.skintest;

import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class BatteryHelper {
	
	private static BatteryHelper mSelf;
	private static final Object LOCK = new Object();
	private Context mContext;
	private STATUS mStatus = STATUS.IDLE;
	private BatteryListener mListener;
	
	private AllKfirManager mManager;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(TextUtils.isEmpty(action)) return;
			if(BleUtils.ACTION_BATTERY_CHANGED.equals(action)){
				STATUS stmp = mStatus;
				getBatteryStatus();
				if(mListener != null && stmp != mStatus){
					mListener.onBatteryStatusChanged(mStatus);
				}
			}
		}
		
	};
	
	private IntentFilter mFilter = new IntentFilter(BleUtils.ACTION_BATTERY_CHANGED);
	private boolean hasRegisted = false;
	
	public static enum STATUS{
		IDLE,WARNING,LOW,CHARGING
	}
	
	public interface BatteryListener{
		public void onBatteryStatusChanged(STATUS status);
	}
	
	public synchronized static BatteryHelper getInstance(Context context){
		if(mSelf == null){
			mSelf = new BatteryHelper(context);
		}
		return mSelf;
	}
	
	public STATUS getBatteryStatus(){
		int fShare = SharePreCacheHelper.getbatteryStatus(mContext);
		int fDevice = mManager.getDevicePowerStatus();
		if(fDevice == 1){
			mStatus = STATUS.LOW;
		}else if(fDevice == 2){
			mStatus = STATUS.CHARGING;
		}else{
			switch(fShare){
			case 0:
				mStatus = STATUS.IDLE;
				break;
			case 1:
				mStatus = STATUS.WARNING;
				break;
			case 2:
				mStatus = STATUS.LOW;
				break;
			}
		}
		
		return mStatus;
	}
	
	private BatteryHelper(Context context){
		this.mContext = context.getApplicationContext();
		mManager = AllKfirManager.getInstance(mContext);
	}
	
	public void onResume(BatteryListener listener){
		mListener = listener;
		mManager.startGetBatteryLevel();
		synchronized (LOCK) {
			if(!hasRegisted){
				mContext.registerReceiver(mReceiver, mFilter);
				hasRegisted = true;
			}
		}
		Slog.d("BatteryHelper onResume");
	}
	
	public void onPause(){
		mListener = null;
		synchronized (LOCK) {
			if(hasRegisted){
				mContext.unregisterReceiver(mReceiver);
				hasRegisted = false;
			}
		}
		Slog.d("BatteryHelper onPause");
	}

}
