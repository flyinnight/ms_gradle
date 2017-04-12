package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.FrameRadioGroup;
import com.dilapp.radar.wifi.AllKfirManager;

/**
 * Created by husj1 on 2015/6/8.
 */
public class ActivityDailyChoosePart extends BaseActivity implements View.OnClickListener, CameraVideoHelper.OnPreTakeStateChangedListener {

    private final static int REQ_TES_SKIN = 10;
    private TitleView mTitle;
    private PromptInfoView mPrompt;

    private TextView tv_msg;
    private FrameRadioGroup rg_daily;
    private RadioButton btn_forehead;
    private RadioButton btn_eye_left;
    private RadioButton btn_eye_right;
    private RadioButton btn_nose;
    private RadioButton btn_cheek;
    private RadioButton btn_hand;

    private CameraVideoHelper mVideoHelper;

    private int infoId = -1;
    private boolean isTest;
    
    private TitleNotify mTitleNotify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_parts);
        Context context = getApplicationContext();
        ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);

        isTest = getIntent().getBooleanExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, false);

        mVideoHelper = new CameraVideoHelper(context, null,false);
        mVideoHelper.setOnPreTakeStateChangedListener(this);

        mPrompt = new PromptInfoView(this, findViewById(R.id.vg_infos));
        
        mTitle = new TitleView(this, findViewById(R.id.vg_toolbar));
        mTitle.setCenterText(R.string.daily_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));
        
        mTitleNotify = new TitleNotify(this, mTitle);
        mTitleNotify.setLowPowerDialog(this, true, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
		mTitleNotify.setNotifyType(TitleNotify.NOTIFY_BATTERY_WARNING | TitleNotify.NOTIFY_BATTERY_LOW, -1, this);


        tv_msg	 = findViewById_(R.id.tv_msg);
        rg_daily = findViewById_(R.id.rg_daily);
        rg_daily.setVisibility(View.VISIBLE);

        btn_forehead  = (RadioButton) rg_daily.findViewById(R.id.btn_forehead);
        btn_eye_left  = (RadioButton) rg_daily.findViewById(R.id.btn_eye_left);
        btn_eye_right = (RadioButton) rg_daily.findViewById(R.id.btn_eye_right);
        btn_nose	  = (RadioButton) rg_daily.findViewById(R.id.btn_nose);
        btn_cheek	  = (RadioButton) rg_daily.findViewById(R.id.btn_cheek);
        btn_hand	  = (RadioButton) rg_daily.findViewById(R.id.btn_hand);

        btn_forehead .setOnClickListener(this);
        btn_eye_left .setOnClickListener(this);
        btn_eye_right.setOnClickListener(this);
        btn_nose	 .setOnClickListener(this);
        btn_cheek	 .setOnClickListener(this);
        btn_hand	 .setOnClickListener(this);

        test();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchState();
        mVideoHelper.onResume();
        if(mTitleNotify != null){
    			mTitleNotify.onResume();
        }
        if(!AllKfirManager.getInstance(this).isOnSkinTest()){
	    		Slog.e("Skin Test has End !!!!");
	        finish();
        }
    }
    
    

    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mVideoHelper.onPause();
		if(mTitleNotify != null){
    			mTitleNotify.onPause();
    		}
	}

	private void test() {
        if (!isTest) {
            return;
        }
        Toast.makeText(this, "您正在进行测试。", Toast.LENGTH_SHORT).show();
        mTitle.setCenterText(R.string.daily_title, new View.OnClickListener() {
            boolean tmp;

            @Override
            public void onClick(View v) {
                if (tmp) {
                    rg_daily.setEnabled(true);
                    mPrompt.setInfoText(null, null);
                } else {
                    rg_daily.setEnabled(false);
                    mPrompt.setInfoText("异常错误", null);
                }
                tmp = !tmp;
            }
        });
    }

    @Override
    public void onStateChanged(int state) {
	    	if(!AllKfirManager.getInstance(this).isOnSkinTest()){
	    		Slog.e("Skin Test has End !!!!");
	         finish();
	         return;
	    }
        if (state == CameraVideoHelper.OnPreTakeStateChangedListener.BLE_DISCONNECTED) {
            infoId = 0;
            setCurrentUIState(false, getString(R.string.test_ble_dis_msg));
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.WIFI_DISCONNECTED) {
            infoId = 2;
            setCurrentUIState(false, getString(R.string.test_wifi_dis_msg));
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.WIFI_CONNECTED) {
            infoId = -1;
            setCurrentUIState(true, null);
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_INITNG){
        		infoId = -1;
            setCurrentUIState(true, null);
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_SUCCESS){
        		infoId = -1;
            setCurrentUIState(true, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case TitleView.ID_RIGHT:
                startActivity(new Intent(this, ActivityDeviceInfo.class));
                break;
            case PromptInfoView.ID_INFOS:
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra("infoId", infoId);
                startActivity(intent);
                break;
            case R.id.btn_forehead:
                startActivity(Constants.PART_FOREHEAD, getString(R.string.normal_forehead));
                break;
            case R.id.btn_eye_left:
            case R.id.btn_eye_right:
                startActivity(Constants.PART_EYE, getString(R.string.normal_eye));
                break;
            case R.id.btn_nose:
                startActivity(Constants.PART_NOSE, getString(R.string.normal_nose));
                break;
            case R.id.btn_cheek:
                startActivity(Constants.PART_CHEEK, getString(R.string.normal_cheek));
                break;
            case R.id.btn_hand:
                startActivity(Constants.PART_HAND, getString(R.string.normal_hand));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TES_SKIN) {
            switch (resultCode) {
//                case RESULT_CANCELED:
//                    setResult(resultCode);
//                    finish();
//                    break;
                case RESULT_OK:
                    setResult(resultCode);
                    finish();
                    break;
            }
        }
    }

    private void dispatchState() {
        AllKfirManager.getInstance(this).printStatus();
        if (!mVideoHelper.isBleCorrect()) {
            infoId = 0;
            setCurrentUIState(false, getString(R.string.test_ble_dis_msg));
        } else if (!mVideoHelper.isWifiCorrect()) {
            infoId = 2;
            setCurrentUIState(false, getString(R.string.test_wifi_dis_msg));
        } else {
            infoId = -1;
            setCurrentUIState(true, null);
        }
    }

    private void startActivity(int id, String text) {
        Intent intent = new Intent(this, ActivityTakingSkin.class);
        intent.putExtras(getIntent().getExtras());
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, id);
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_RESULT_ACTIVITIES, new String[]{ActivityDailyResult.class.getName()});
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO, text);
        startActivityForResult(intent, REQ_TES_SKIN);
        rg_daily.clearCheck();
    }

    private void setCurrentUIState(boolean state, String msg) {
		if(isTest) {
			state = true;
			msg = null;
		}
		state = true;
        btn_forehead.setEnabled(state);
        btn_eye_left.setEnabled(state);
        btn_eye_right.setEnabled(state);
        btn_nose.setEnabled(state);
        btn_cheek.setEnabled(state);
        btn_hand.setEnabled(state);
        mPrompt.setInfoText(null, this);
//        mPrompt.setInfoText(msg, this);
    }

    @Override
    protected void onDestroy() {
        ViewUtils.releaseBackground(rg_daily);
        ViewUtils.releaseBackground(btn_forehead);
        ViewUtils.releaseBackground(btn_eye_left);
        ViewUtils.releaseBackground(btn_eye_right);
        ViewUtils.releaseBackground(btn_nose);
        ViewUtils.releaseBackground(btn_cheek);
        ViewUtils.releaseBackground(btn_hand);
        mVideoHelper.onDestroy();
        if(mTitleNotify != null){
    			mTitleNotify.onDestroy();
        }
        super.onDestroy();
    }
}
