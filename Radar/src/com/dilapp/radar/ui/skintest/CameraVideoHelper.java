package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.AllKfirManager.NET_UI_STATUS;
import com.dilapp.radar.wifi.Content;
import com.dilapp.radar.wifi.IAllKfirHelperCallback;
import com.ov.omniwificam.Vout;

public class CameraVideoHelper implements IAllKfirHelperCallback {

	private final static String TAG = CameraVideoHelper.class.getSimpleName();
	private final static boolean DEBUG = true;
	// 0 Doing 1 done, 2 fail

	public final static int CAPTURE_DOING = 0;
	public final static int CAPTURE_SUCCESS = 1;
	public final static int CAPTURE_FAIL = 2;

	public final static int LED_RGB = 0;// 皮肤普通光
	public final static int LED_PL = 1;// 皮肤片正光
	
	private final static int WAIT_CAPTURE = 1;
	
	private boolean hasOneCapture = Content.ONE_CAPTURE;

	private Context mContext;
	private GLSurfaceView mSurfaceView;
	private Vout mSurfaceVout;
	private boolean mOpened = true;

	private OnTakeResultListener mTakeListener;
	private OnPreTakeStateChangedListener mPTSCListener;

	private AllKfirManager allInfoManager;

	private boolean isInited;
	private boolean isOPause = true;
	private int currLED = LED_RGB;
	
	private NET_UI_STATUS mCurrStatus = NET_UI_STATUS.IDLE;

	private boolean isTakeing;// 是否需要拍照，判断条件为，如果调用了OnCreate，代表需要拍照，否则只是想拿状态
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WAIT_CAPTURE:
				Slog.d("WHAT_CAPTURE : "+msg.arg1);
				if (mTakeListener != null && !isOPause) {
					mTakeListener.onTakeResult(msg.arg1 != 0);
				}
				break;
			default:
				break;
			}
		}
	};

	public CameraVideoHelper(Context context, GLSurfaceView surfaceView, boolean isvertical) {
		this.mContext = context;
		this.mSurfaceView = surfaceView;
		if(this.mSurfaceView != null){
			this.mSurfaceVout = new Vout(mContext);
			if(isvertical){
				this.mSurfaceVout.setOrientation(Vout.Orientation.VERTICAL);
			}
			this.mSurfaceView.setRenderer(mSurfaceVout);
			this.mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		allInfoManager = AllKfirManager.getInstance(context);
		allInfoManager.registerAllInfoCallback(this);
		allInfoManager.initCameraHelper(mContext);
	}

	private void initDevice() {
		if(allInfoManager == null) return;
		if (mSurfaceView != null) {
			try {
//				if (!isInited) {
//					allInfoManager.initDevice(mContext, mSurfaceView,mSurfaceVout);
//					Slog.e("init device CameraVideoHelper");
//				}
				allInfoManager.startDecoding(mSurfaceView, mSurfaceVout);
				isInited = true;

			} catch (UnsatisfiedLinkError e) {
			}
		}
	}

	/**
	 * 是否可以拍照
	 *
	 * @return
	 */
	public boolean isTakeable() {

		final boolean ble = isBleCorrect();
		final boolean wifi = isWifiCorrect();
		final boolean camera = isCameraCorrect();
		Slog.i("isBleCorrect " + ble + " isWifiCorrect " + wifi
				+ "  isCameraCorrect  " + camera + "isInited " + isInited);
//		return isInited && ble && wifi && camera;
//		return isInited && wifi && camera;
		return isInited && camera;
	}

	/**
	 * 蓝牙状态是否正确
	 *
	 * @return
	 */
	public boolean isBleCorrect() {
		if(allInfoManager == null) return false;
		boolean result = allInfoManager.isBleConnected();
		return result;
	}

	/**
	 * Wifi状态是否正确
	 *
	 * @return
	 */
	public boolean isWifiCorrect() {
		if(allInfoManager == null) return false;
		return allInfoManager.isWifiReadyForTrans();
		// switch (WifiHelper.getWifiState()) {
		// case ap_connected:
		// case connected_open_online:
		// case connected_pw_online:
		// return true;
		//
		// default:
		// return false;
		// }
	}

	/**
	 * 相机状态是否正确
	 *
	 * @return
	 */
	public boolean isCameraCorrect() {
		if(allInfoManager == null) return false;
		boolean result = (allInfoManager.isCameraConnected());
//		if (!result) {
//			initDevice();
//		}
		return result;
	}

	/**
	 * 打开视频
	 *
	 * @return
	 */
	public boolean openVideo() {
		if (!isTakeable() || mSurfaceView == null) {
			return false;
		}
		if(allInfoManager == null) return false;
		try {
			allInfoManager.startVideo();
			return true;
		} catch (UnsatisfiedLinkError e) {
			return false;
		}
	}

	/**
	 * 视频拍照
	 *
	 * @return
	 */
	public boolean takeVideo() {
		if(mSurfaceView == null) return false;
		if(allInfoManager == null) return false;
		allInfoManager.startCamera();
		Slog.d("start takeCapture!!");
		handler.removeMessages(WAIT_CAPTURE);
		Message msg = handler.obtainMessage();
		msg.what = WAIT_CAPTURE;
		msg.arg1 = 0;
//		msg.arg2 = 0;
		handler.sendMessageDelayed(msg, 5000);
		return true;
	}

	/**
	 * 关闭视频
	 *
	 * @return
	 */
	public boolean closeVideo() {
		if(mSurfaceView == null) return false;
		if(allInfoManager == null) return false;
		try {
			allInfoManager.closeVideo();
			return true;
		} catch (UnsatisfiedLinkError e) {
			return false;
		}
	}
	

	public void onCreate(Bundle savedInstanceState) {
		isTakeing = true;
	}

	public void onStart() {

	}

	public void onResume() {
		isOPause = false;
		// 恢复现场
		// 1.结束计时
		// 2.判断蓝牙的状态
		// 3.判断wifi的状态
		// 4.判断设备的状态
		// mCamera.clearTimerCloseData();
		if(allInfoManager == null) return;
		allInfoManager.clearTimerCloseData();
		if (mOpened && mSurfaceView != null) {
			if(!this.isInited){
				this.initDevice();
			}else{
				openVideo();
			}
		}
		if(mPTSCListener != null && !isOPause) {
			mPTSCListener.onStateChanged(convertState(mCurrStatus));
		}

	}

	public void onSaveInstanceState(Bundle outState) {
	}

	public void onPause() {
		isOPause = true;
		if(allInfoManager == null) return;
		allInfoManager.uiStartTimerCloseData();
//		if(mSurfaceView != null){
//			allInfoManager.stopDecoding();
//		}
		if(mSurfaceView != null){
			closeVideo();
		}
	}

	public void onStop() {
//		closeVideo();
	}

	public void onDestroy() {
		Slog.d("VideoHelp onDestroy!");
		isInited = false;
		if(allInfoManager != null){
			allInfoManager.unRegisterAllInfoCallback(this);
		}
		mPTSCListener = null;
		mTakeListener = null;
		// if(this.mSurfaceView != null){
		this.mSurfaceView = null;
		this.mSurfaceVout = null;
		// }
//		try {
//			isInited = false;
//			allInfoManager.endDecoding();
//			allInfoManager.clearTimerCloseData();
//
//		} catch (UnsatisfiedLinkError e) {
//			Slog.e("Error",e);
//		}
		allInfoManager = null;
		mContext = null;
	}
	
	/**
	 * 请参考 {@link #LED_RGB} {@link #LED_PL}
	 * 默认为 {@link #LED_RGB}
	 * @param led
	 */
	public void setLED(int led) {
		if(led == this.currLED) {
			return;
		}
		if(led == LED_RGB || led == LED_PL) {
			this.currLED = led;
			if(allInfoManager == null) return;
			allInfoManager.setLedControl(led);
		}
	}
	
	public void setLEDInternal(int led){
		this.currLED = led;
	}
	
	public int getLED() {
		return this.currLED;
	}

	public boolean isOpen() {
		return mOpened;
	}

	public void setOpen(boolean open) {
		this.mOpened = open;
	}

	public OnTakeResultListener getOnTakeResultListener() {
		return mTakeListener;
	}

	public void setOnTakeResultListener(OnTakeResultListener l) {
		this.mTakeListener = l;
	}

	public OnPreTakeStateChangedListener getOnPreTakeStateChangedListener() {
		return mPTSCListener;
	}

	public void setOnPreTakeStateChangedListener(OnPreTakeStateChangedListener l) {
		this.mPTSCListener = l;
	}

	public interface OnTakeResultListener {

		/**
		 * 拍照结果
		 *
		 * @param success
		 *            这张图片是否拍照成功
		 */
		void onTakeResult(boolean success);
	}

	/**
	 * 拍照之前的状态处理
	 */
	public interface OnPreTakeStateChangedListener {

		/**
		 * 蓝牙未连接
		 */
		int BLE_DISCONNECTED = NET_UI_STATUS.BLE_DISCONNECTED.ordinal();
		/**
		 * Wifi未连接
		 */
		int WIFI_DISCONNECTED = NET_UI_STATUS.WIFI_DISCONNECTED.ordinal();
		/**
		 * Wifi已连接
		 */
		int WIFI_CONNECTED = NET_UI_STATUS.WIFI_CONNECTED.ordinal();
		/**
		 * 设备初始化中
		 */
		int DEVICE_INITNG = NET_UI_STATUS.DEVICE_IDLE.ordinal()
				| NET_UI_STATUS.DEVICE_ERROR.ordinal();
		/**
		 * 设备初始化成功
		 */
		int DEVICE_SUCCESS = NET_UI_STATUS.DEVICE_SUCCESS.ordinal();

		/**
		 * 状态发生改变的回调
		 *
		 * @param state
		 */
		void onStateChanged(int state);
	}

	@Override
	public void allInfoStatusChange(NET_UI_STATUS status) { // 状态信息
		Slog.e("allInfoStatusChange  " + status);
		mCurrStatus = status;
		// if(status == NET_UI_STATUS.)
		// NET_UI_STATUS.BLE_DISCONNECTED// 提示用户蓝牙连接失败
		// NET_UI_STATUS.
		if (status == NET_UI_STATUS.WIFI_CONNECTED) {
			// Toast.makeText(mContext, " success", 1).show();
			if(!isOPause){
				initDevice();
			}
		} else if(status == NET_UI_STATUS.DEVICE_SUCCESS) {
			// 重连后，默认是RGB
			this.currLED = LED_RGB;
		}
		if(mPTSCListener != null && !isOPause) {
			mPTSCListener.onStateChanged(convertState(status));
		}else{
			Slog.e("mPTSCListener in NULL or isOPause : "+isOPause);
		}
	}

	private int convertState(NET_UI_STATUS status) {
		if(NET_UI_STATUS.BLE_DISCONNECTED == status) {
			return OnPreTakeStateChangedListener.BLE_DISCONNECTED;
		} else if(NET_UI_STATUS.WIFI_CONNECTED == status) {
			return OnPreTakeStateChangedListener.WIFI_CONNECTED;
		} else if(NET_UI_STATUS.WIFI_DISCONNECTED == status) {
			return OnPreTakeStateChangedListener.WIFI_DISCONNECTED;
		} else if(NET_UI_STATUS.DEVICE_IDLE == status || NET_UI_STATUS.DEVICE_ERROR == status) {
			return OnPreTakeStateChangedListener.DEVICE_INITNG;
		} else if(NET_UI_STATUS.DEVICE_SUCCESS == status) {
			return OnPreTakeStateChangedListener.DEVICE_SUCCESS;
		} else {
			return 0;
		}
	}

	@Override
	public void photosStatus(int index, int status) { // 图片的状态

		Slog.d("photosStatus : "+index +"  "+status);
		switch (status) {
		case CAPTURE_SUCCESS: {
			// 前面做了个超时机制，这里先移除一下，免得重复调用
			if(hasOneCapture || (!hasOneCapture && index > 0)){
				handler.removeMessages(WAIT_CAPTURE);
				Message msg = handler.obtainMessage();
				msg.what = WAIT_CAPTURE;
				msg.arg1 = 1;
//				msg.arg2 = 1;
				handler.sendMessage(msg);
			}
			break;
		}
		case CAPTURE_FAIL: {
			// 前面做了个超时机制，这里先移除一下，免得重复调用
			handler.removeMessages(WAIT_CAPTURE);
			Message msg = handler.obtainMessage();
			msg.what = WAIT_CAPTURE;
			msg.arg1 = 0;
//			msg.arg2 = 0;
			handler.sendMessage(msg);
			break;
		}
		default:
			break;
		}

	}

}
