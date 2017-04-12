package com.dilapp.radar.ui.skintest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleDialogUtil;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.update.ActivityRadarDetail;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.AllKfirManager.NET_UI_STATUS;
import com.dilapp.radar.wifi.IAllKfirHelperCallback;

public class FragmentConnDevice extends BaseFragment implements OnClickListener, IAllKfirHelperCallback {
    //UI
    private Button btn_start;
    private TextView tv_linktext;

    private AllKfirManager allInfoManager;
    private Context context = null;
    //	private boolean bleConnectedStatus = false;
    //message
    private final int CONNECT_AP_FAILED = 0x000001;
    private final int BLE_ENABLED = 0X000002;
    private final int BLE_CONNECTED = 0X000003;
    private final int DISMIS_DIALOG = 0X000004;
    //	private final int WIFI_ENABLEING           = 0X000005;
    //user click falg
    private boolean userClick = false;
    //durtion
    private final int CONNECT_WFI_DELAYTIME = 1000;
    private String defaultSSid = "";   // 上次保存的ssid

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BLE_ENABLED:        //ble open
                    String defaultAddress = getSaveBleAddress();
                    if (TextUtils.isEmpty(defaultAddress)) {
                        Log.i("hj", "defaultAddress is null");
                        Intent intent = new Intent(getActivity(), ActivityScanBle.class);
                        startActivity(intent);
                        return;
                    }
                    checkWifiStaus();
                    break;
                case BLE_CONNECTED:
//				if(allInfoManager.getMode()){  // ap mode
//					defaultSSid = getDefaultSSid();
//					if(TextUtils.isEmpty(defaultSSid)){
//						// 发送 ble 命令去获取 TODO
//					}
//				}
                    checkWifiStaus();

                    break;
                case CONNECT_AP_FAILED:

                    break;
                case DISMIS_DIALOG:
                    BleDialogUtil.dismissWaitDialog();
                    Intent intent = new Intent(getActivity(), nextActivity);
                    startActivity(intent);
                    break;
//			case WIFI_ENABLEING:
//				if(userClick){
//				 Log.e("hj", "FragmentConnDevice  connect ap userClick "+ userClick);
//				 if(!AllInfoManager.getWifiState().equals(WifiState.ap_connecting) && !AllInfoManager.getWifiState().equals(WifiState.ap_connected)){
//					 allInfoManager.connectWifiAp();
//				 }
//				
//				}
//				break;

                default:
                    break;
            }

        }

        private String getDefaultSSid() {
            return SharePreCacheHelper.getDefaultSSid(getActivity());
        }

        private String getSaveBleAddress() {
            return SharePreCacheHelper.getBleMacAddress(getActivity());

        }

    };

    @Override
    public void onCreateView(ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(container, savedInstanceState);
        setCacheView(true);
        // setContentView(R.layout.fragment_conn_device);
        setContentView(R.layout.fragment_conn_device);
        getContentView().setBackground(
                ZBackgroundHelper.getDrawable(mContext, ZBackgroundHelper.TYPE_BLACK_BLUR));
    }

    Class<? extends Activity> nextActivity = ActivityDaily.class;


    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        init();
    }


    private void init() {
        context = getActivity();
        allInfoManager = AllKfirManager.getInstance(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();

        //	allInfoManager.setAllInfoCallback(this);
//		allInfoManager.startSkinBle();
        checkView();

    }

    private void initView() {
        btn_start = findViewById(R.id.startScan);
        btn_start.setOnClickListener(this);
        tv_linktext = findViewById(R.id.linktext);
        tv_linktext.setOnClickListener(this);

        addHperLinks();


    }

    private void addHperLinks() {
       //  MyTextUtils.addUnderlineText(tv_linktext, 0, tv_linktext.getText().length());
    }


    private void checkView() {


    }

    @Override
    public void onStop() {
        super.onStop();
//		if(bleConnectedStatus){
//		  SharePreCacheHelper.saveBleConnectStatus(context, false);
//		}
        userClick = false;

    }


    long start = System.currentTimeMillis();

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.startScan:
                //allInfoManager.startSkinTest(getActivity());
                allInfoManager.startSkinBle(false);
                intent = new Intent(getActivity(), ActivityScanBle.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);

                break;
            case R.id.linktext:
                Intent it = new Intent(getActivity(), ActivityRadarDetail.class);
//			Uri uri = Uri.parse("http://121.41.79.23:80/radar/"); 
//			Intent it = new Intent(Intent.ACTION_VIEW, uri); 
                startActivity(it);
                break;
/*		case R.id.setting:
            intent = new Intent(getActivity(),ActivityDeviceInfo.class);
			startActivity(intent);
			break;
		case R.id.secondStart:   // 开始使用
			allInfoManager.startSkinTest(getActivity());     //mode
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					allInfoManager.openBle();
				    // allInfoManager.openWifi();
				}
			}).start();
		
			userClick = true;
			
			checkBleStatus();
			break;*/
            default:
                break;
        }

    }

    private void checkBleStatus() {

        handler.removeMessages(BLE_ENABLED);
        handler.sendEmptyMessage(BLE_ENABLED);
    }

    private void checkWifiStaus() {
        Intent intent = null;
        String ssid = null;
        if (allInfoManager.needCheckWifiPassword()) {
            intent = new Intent(getActivity(), ActivityWifiPassword.class);
            startActivity(intent);
        }
//		allInfoManager.checkWifiStatus();
//		switch (AllInfoManager.getWifiState()) {
//		case none:
//		case disabled:                             
//		case enabled:
//		case enableding:
//		case ap_connected:
//		case ap_connecting:        //  Ap模式 
//			intentToSkinTestActivity();
//			break;
//			
//		case connected_open:  // TODO 1.判断网速 2.发送 当前的ssid    //STA
//			intentToSkinTestActivity();
//			break;
//		case connected_pw:                                     //STA
//			// 判断 ssid 可存在
//			LocalWifi localWifi = checkSSid();
//			if(localWifi != null  ){ // 存在
//				ssid = allInfoManager.getSSID();   
//				String pwd = localWifi.wifiPassword;
//			  intentToSkinTestActivity();
//			}else{
//			    intent =  new Intent(getActivity(),ActivityWifiPassword.class);
//			    startActivity(intent);
//			}
//			break;
//	
//		default:
//			break;
//		}

    }

//	private LocalWifi checkSSid() {
//		String ssid = allInfoManager.getSSID();
//		String mac = allInfoManager.getMacAddress();
//		return  SharePreCacheHelper.checkSSid(getActivity(), mac, ssid);
//	}


    private void intentToSkinTestActivity() {
		/*
		allInfoManager.setAllInfoCallback(null);
		Intent intent = new Intent(getActivity(),nextActivity);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
		*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        context = null;
//	  if(bleConnectedStatus){
//	    SharePreCacheHelper.savePairStatus(getActivity(), false);
//	  }
    }

    @Override
    public void allInfoStatusChange(NET_UI_STATUS status) {
		/*Log.e("hj", "NotifyUIStatus  " + status);
		 switch (status) {
		 case wifi_enabling:
			 if(userClick){
			 // handler.removeMessages(WIFI_ENABLEING);
			 // handler.sendEmptyMessageDelayed(WIFI_ENABLEING, CONNECT_WFI_DELAYTIME);
			 }
			break;
		 case ble_enabled:
			 if(userClick){
				 Log.e("hj", " --------+++++allInfoStatusChange ble_enabled++++------------  ");
			    handler.removeMessages(BLE_ENABLED);
			    handler.sendEmptyMessage(BLE_ENABLED);
			 }
			break;
		case ap_connected:
			break;
		case ble_connected:
			break;
		case ble_disconnected:
			break;
		default:
			break;
		}*/

    }

    @Override
    public void photosStatus(int mId, int status) {

    }
}
