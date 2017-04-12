package com.dilapp.radar.ui.skintest;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.skintest.BatteryHelper.BatteryListener;
import com.dilapp.radar.ui.skintest.BatteryHelper.STATUS;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.AllKfirManager.NET_UI_STATUS;
import com.dilapp.radar.wifi.IAllKfirHelperCallback;

public class TitleNotify implements IAllKfirHelperCallback, BatteryListener{
	
	private Context mContext;
	private TitleView mTitleView;
	private int mPosition = 2;
	private int mReqType = 0;
	private int mCurrType = 0;
	private long mAnimSpeed = 300;
	private OnClickListener mClickListener;
	private int mDefRes = -1;
	
	private AllKfirManager mManager;
	private BatteryHelper mBatteryHelper;
	private LowPowerDialog mLowPowerDialog;
	
	private boolean isOnPause = true;
	private boolean isShowIcon = false;
	private boolean isShowLowPower = false;
	private boolean hasShowDialog = false;
	
	public static final int NOTIFY_BLE_ERROR = 		0x000001;
	public static final int NOTIFY_WIFI_ERROR = 		0x000010;
	public static final int NOTIFY_BATTERY_WARNING = 0x000100;
	public static final int NOTIFY_BATTERY_LOW = 	0x001000;
	
	public TitleNotify(Context context, TitleView titleView){
		this.mContext = context;
		this.mTitleView = titleView;
		this.mPosition = 2;
		mManager = AllKfirManager.getInstance(mContext);
		mBatteryHelper = BatteryHelper.getInstance(mContext);
	}
	/**
	 * 
	 * @param context
	 * @param titleView
	 * @param positon 0: left 1: center 2:right
	 */
	public TitleNotify(Context context, TitleView titleView, int position){
		this.mContext = context;
		this.mTitleView = titleView;
		this.mPosition = position;
		mManager = AllKfirManager.getInstance(mContext);
		mBatteryHelper = BatteryHelper.getInstance(mContext);
	}
	
	/**
	 * 
	 * @param type 类型{@link NOTIFY_BLE_ERROR}
	 * @param def_res notify 之前的 Icon
	 * @param listener
	 */
	public void setNotifyType(int req_type,int def_res, OnClickListener listener){
		this.mReqType = req_type;
		mClickListener = listener;
		mDefRes = def_res;
		if(mDefRes > 0){
			setIcon(mDefRes);
		}
		mHandler.removeMessages(MSG_FLASH_NOTIFY);
		mHandler.sendEmptyMessageDelayed(MSG_FLASH_NOTIFY, mAnimSpeed);
	}
	
	public void setAnimSpeed(long speed){
		this.mAnimSpeed = speed;
	}
	
	public void setLowPowerDialog(Activity activity, boolean show, OnClickListener close){
		this.isShowLowPower = show;
		if(isShowLowPower){
			if(mLowPowerDialog == null){
				mLowPowerDialog = new LowPowerDialog(activity);
				mLowPowerDialog.setButtonsOnClickListener(close);
			}
		}else{
			if(mLowPowerDialog != null){
				if(mLowPowerDialog.isShowing()){
					mLowPowerDialog.cancel();
				}
				mLowPowerDialog = null;
			}
		}
		
	}
	
	public boolean isOnNotify(){
		return (mCurrType > 0);
	}
	
	public void onResume(){
		isOnPause = false;
		mManager.registerAllInfoCallback(this);
		mBatteryHelper.onResume(this);
		mHandler.removeMessages(MSG_FLASH_NOTIFY);
		mHandler.sendEmptyMessageDelayed(MSG_FLASH_NOTIFY, mAnimSpeed);
	}
	
	public void onPause(){
		isOnPause = true;
		isShowIcon = false;
		mBatteryHelper.onPause();
		mManager.unRegisterAllInfoCallback(this);
		mHandler.removeMessages(MSG_FLASH_NOTIFY);
	}
	
	public void onDestroy(){
		mManager = null;
		mBatteryHelper = null;
	}
	
	/********************/
	
	private void handleNotify(){
		if(mManager == null || mBatteryHelper == null) return;
		boolean bleEnable = mManager.isBleConnected();
		boolean wifiEnable = mManager.isWifiReadyForTrans();
		STATUS mbstatus = mBatteryHelper.getBatteryStatus();
		mCurrType = 0;
		if(!bleEnable && (mReqType & NOTIFY_BLE_ERROR) > 0){
			mCurrType |= NOTIFY_BLE_ERROR;
		}
		if(!wifiEnable && (mReqType & NOTIFY_WIFI_ERROR) > 0){
			mCurrType |= NOTIFY_WIFI_ERROR;
		}
		if(mbstatus == STATUS.WARNING && (mReqType & NOTIFY_BATTERY_WARNING) > 0){
			mCurrType |= NOTIFY_BATTERY_WARNING;
		}
		if(mbstatus == STATUS.LOW && (mReqType & NOTIFY_BATTERY_LOW) > 0){
			mCurrType |= NOTIFY_BATTERY_LOW;
		}
		
		if((mCurrType & NOTIFY_BLE_ERROR) > 0){
			setNotifyByTypeId(NOTIFY_BLE_ERROR);
		}else if((mCurrType & NOTIFY_WIFI_ERROR) > 0){
			setNotifyByTypeId(NOTIFY_WIFI_ERROR);
		}else if((mCurrType & NOTIFY_BATTERY_WARNING) > 0){
			setNotifyByTypeId(NOTIFY_BATTERY_WARNING);
		}else if((mCurrType & NOTIFY_BATTERY_LOW) > 0){
			setNotifyByTypeId(NOTIFY_BATTERY_LOW);
		}else{
			setNotifyByTypeId(0);
		}
		
		if(mManager.getDevicePowerStatus() == 1){
			onBatteryStatusChanged(STATUS.LOW);
		}
	}
	
	private void setNotifyByTypeId(int typeid){
		switch(typeid){
		case NOTIFY_BLE_ERROR:
			setIcon(R.drawable.notify_bt);
			break;
		case NOTIFY_WIFI_ERROR:
			setIcon(R.drawable.notify_wifi);
			break;
		case NOTIFY_BATTERY_WARNING:
			setIcon(R.drawable.notify_power);
			break;
		case NOTIFY_BATTERY_LOW:
			setIcon(R.drawable.notify_power);
			break;
		default:
			setIcon(0);
			break;
		}
	}
	
	private void setIcon(int resid){
		OnClickListener listener = null;
		if(isShowIcon){
			resid = -1;
			isShowIcon = false;
		}else{
			isShowIcon = true;
		}
		if(mReqType <= 0 || mCurrType <= 0){
			resid = mDefRes;
		}
		if(mDefRes <= 0 && mCurrType <= 0){
			listener = null;
		}else{
			listener = mClickListener;
		}
		
		switch(mPosition){
		case 0:
			mTitleView.setLeftIcon(resid, listener);
			break;
		case 1:
			mTitleView.setCenterIcon(resid, listener);
			break;
		case 2:
			mTitleView.setRightIcon(resid, listener);
			break;
		}
	}
	
	private static final int MSG_FLASH_NOTIFY = 1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_FLASH_NOTIFY:
				if(!isOnPause){
					handleNotify();
					if(mCurrType > 0){
						mHandler.removeMessages(MSG_FLASH_NOTIFY);
						mHandler.sendEmptyMessageDelayed(MSG_FLASH_NOTIFY, mAnimSpeed);
					}
				}else{
					isShowIcon = false;
				}
				break;
			}
		}
		
	};

	@Override
	public void allInfoStatusChange(NET_UI_STATUS status) {
		// TODO Auto-generated method stub
		mHandler.removeMessages(MSG_FLASH_NOTIFY);
		mHandler.sendEmptyMessageDelayed(MSG_FLASH_NOTIFY, mAnimSpeed);
	}
	@Override
	public void photosStatus(int mId, int status) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onBatteryStatusChanged(STATUS status) {
		// TODO Auto-generated method stub
		if(STATUS.LOW == status){
			if(isShowLowPower && !hasShowDialog && mLowPowerDialog != null && !mLowPowerDialog.isShowing()){
				mLowPowerDialog.show();
				hasShowDialog = true;
			}
		}else{
			if(isShowLowPower && mLowPowerDialog != null && mLowPowerDialog.isShowing()){
				mLowPowerDialog.cancel();
			}
		}
		mHandler.removeMessages(MSG_FLASH_NOTIFY);
		mHandler.sendEmptyMessageDelayed(MSG_FLASH_NOTIFY, mAnimSpeed);
	}

}
