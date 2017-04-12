package com.dilapp.radar.ui.skintest;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.SerializableUtil;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.FrameRadioGroup;
import com.dilapp.radar.wifi.AllKfirManager;

/**
 * Created by husj1 on 2015/6/8.
 */
public class ActivityTasteChoosePart extends BaseActivity implements View.OnClickListener, CameraVideoHelper.OnPreTakeStateChangedListener {

    private final static int REQ_TEST_SKIN = 10;
    private final static int REQ_TEST_SKIN_AFTER = 20;
    private TitleView mTitle;
    private PromptInfoView mPrompt;

    private TextView tv_msg;
    private FrameRadioGroup rg_taste;
    private RadioButton btn_forehead;
    private RadioButton btn_eye_left;
    private RadioButton btn_eye_right;
    private RadioButton btn_nose;
    private RadioButton btn_cheek;
    private RadioButton btn_hand;
    private Button btn_after;

    private CameraVideoHelper mVideoHelper;

    private int infoId = -1;
    private int prevPart;// prevPart != 0 代表之前已经测试过
    private int tempPart;
    private boolean isTest;
    
    private TitleNotify mTitleNotify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_parts);
        Context context = getApplicationContext();
        ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
        
        Intent data = getIntent();

        prevPart = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, 0);
        isTest = data.getBooleanExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, false);

        mVideoHelper = new CameraVideoHelper(context, null,false);
        mVideoHelper.setOnPreTakeStateChangedListener(this);

        View title = findViewById(R.id.vg_toolbar);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.taste_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));
        
        mTitleNotify = new TitleNotify(this, mTitle);
        mTitleNotify.setLowPowerDialog(this, true, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            		if (prevPart != 0) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    saveTempResult();
                }
                finish();
            }
        });
		mTitleNotify.setNotifyType(TitleNotify.NOTIFY_BATTERY_WARNING | TitleNotify.NOTIFY_BATTERY_LOW, -1, this);

        View prompt = findViewById(R.id.vg_infos);
        mPrompt = new PromptInfoView(this, prompt);

        tv_msg = findViewById_(R.id.tv_msg);
        rg_taste = findViewById_(R.id.rg_daily);
        rg_taste.setVisibility(View.VISIBLE);

        btn_forehead = (RadioButton) rg_taste.findViewById(R.id.btn_forehead);
        btn_eye_left = (RadioButton) rg_taste.findViewById(R.id.btn_eye_left);
        btn_eye_right = (RadioButton) rg_taste.findViewById(R.id.btn_eye_right);
        btn_nose = (RadioButton) rg_taste.findViewById(R.id.btn_nose);
        btn_cheek = (RadioButton) rg_taste.findViewById(R.id.btn_cheek);
        btn_hand = (RadioButton) rg_taste.findViewById(R.id.btn_hand);
        btn_after = findViewById_(R.id.btn_after);

        if (prevPart == 0) {

            btn_forehead.setOnClickListener(this);
            btn_eye_left.setOnClickListener(this);
            btn_eye_right.setOnClickListener(this);
            btn_nose.setOnClickListener(this);
            btn_cheek.setOnClickListener(this);
            btn_hand.setOnClickListener(this);

        } else {
            tv_msg.setVisibility(View.GONE);

            findViewById(R.id.vg_taste_extra).setVisibility(View.VISIBLE);

            RadioButton[] btns = new RadioButton[]{btn_forehead, btn_eye_left, btn_eye_right,
                    btn_nose, btn_cheek, btn_hand,};
            int[] arr = getViewId(prevPart);
            for (RadioButton btn : btns) {
                for (int v: arr) {
                    if (v == btn.getId()) {
                        btn.setChecked(true);
                    }
                }
                btn.setEnabled(false);
            }
        }
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
    			if (prevPart != 0) {
    				Intent data = new Intent();
    				setResult(RESULT_OK, data);
    				saveTempResult();
             }
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
        if(!isTest) {
            return;
        }
        if(prevPart == 0) Toast.makeText(this, "您正在进行测试。", Toast.LENGTH_SHORT).show();
        mTitle.setCenterText(R.string.taste_title, new View.OnClickListener() {
            boolean tmp;
            @Override
            public void onClick(View v) {
                if(tmp) {
                    mPrompt.setInfoText(null, null);
                } else {
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
	    		if (prevPart != 0) {
                 Intent data = new Intent();
                 setResult(RESULT_OK, data);
                 saveTempResult();
             }
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
        }else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_INITNG){
        		infoId = -1;
            setCurrentUIState(true, null);
        }else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_SUCCESS){
        		infoId = -1;
            setCurrentUIState(true, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                if (prevPart != 0) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    saveTempResult();
                }
                finish();
                break;
            case TitleView.ID_RIGHT:
                startActivity(new Intent(this, ActivityDeviceInfo.class));
                break;
            case R.id.btn_after: {
//                Intent intent = new Intent(this, ActivityTakingSkin.class);
//                intent.putExtras(getIntent().getExtras());
//                startActivityForResult(intent, REQ_TEST_SKIN_AFTER);
                startActivity(prevPart, getString(prevPart), REQ_TEST_SKIN_AFTER);
                break;
            }
            case R.id.btn_retest: {
            	String path = Constants.PRODUCT_TEST_TEMP_RESULT_PATH(this);
            	File file = new File(path);
            	if(file.exists()){
            		file.delete();
            	}
            	if(getIntent().getBooleanExtra("first", false)) {
            		Intent intent = new Intent(this, this.getClass());
            		intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, isTest);
            		startActivity(intent);
            		finish();
            	} else {
	                setResult(RESULT_CANCELED);
	                finish();
            	}
                break;
            }
            case PromptInfoView.ID_INFOS: {
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra("infoId", infoId);
                startActivity(intent);
            }
                break;
            case R.id.btn_forehead:
                startActivity(Constants.PART_FOREHEAD, getString(R.string.normal_forehead), REQ_TEST_SKIN);
                break;
            case R.id.btn_eye_left:
            case R.id.btn_eye_right:
            case R.id.btn_eye:
                startActivity(Constants.PART_EYE, getString(R.string.normal_eye), REQ_TEST_SKIN);
                break;
            case R.id.btn_nose:
                startActivity(Constants.PART_NOSE, getString(R.string.normal_nose), REQ_TEST_SKIN);
                break;
            case R.id.btn_cheek_left:
            case R.id.btn_cheek_right:
            case R.id.btn_cheek:
                startActivity(Constants.PART_CHEEK, getString(R.string.normal_cheek), REQ_TEST_SKIN);
                break;
            case R.id.btn_hand:
                startActivity(Constants.PART_HAND, getString(R.string.normal_hand), REQ_TEST_SKIN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TEST_SKIN) {
            switch (resultCode) {
                case RESULT_OK:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == REQ_TEST_SKIN_AFTER) {
            switch (resultCode) {
                case RESULT_OK:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (prevPart != 0) {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            saveTempResult();
        }
        super.onBackPressed();
    }

    // 测试皮肤
    private void startActivity(int id, String title, int req) {
        tempPart = id;
        Intent intent = new Intent(this, ActivityTakingSkin.class);
        intent.putExtras(getIntent().getExtras());
        // intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, isTest);
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO, title);
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, id);
        //if (prevPart == 0) {
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_RESULT_BUTTON_TEXTS,
                new String[]{getString(R.string.test_next)});
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_RESULT_ACTIVITIES,
                new String[]{ActivityTasteChoosePart.class.getName(), ActivityTasteResult.class.getName()});
        //}
        startActivityForResult(intent, req);
        if(prevPart == 0) {
        	rg_taste.clearCheck();
        }
    }

    // 主动检测蓝牙，歪饭状态
    private void dispatchState() {
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

    private void setCurrentUIState(boolean state, String msg) {
    	if(isTest) {
    		state = true;
    		msg = null;
    	}
    	state = true;
        if(prevPart == 0) {
            btn_forehead.setEnabled(state);
            btn_eye_left.setEnabled(state);
            btn_nose.setEnabled(state);
            btn_cheek.setEnabled(state);
            btn_hand.setEnabled(state);
        }
        btn_after.setEnabled(state);
        mPrompt.setInfoText(null, this);
//        mPrompt.setInfoText(msg, this);
    }
    
    // 根据PartID获取ViewID，就是那些部位按钮
    private int[] getViewId(int partId) {
        switch (partId) {
            case Constants.PART_FOREHEAD:
                return new int[]{R.id.btn_forehead};
            case Constants.PART_EYE:
                return new int[]{R.id.btn_eye, R.id.btn_eye_left, R.id.btn_eye_right};
            case Constants.PART_NOSE:
                return new int[]{R.id.btn_nose};
            case Constants.PART_CHEEK:
                return new int[]{R.id.btn_cheek, R.id.btn_cheek_left, R.id.btn_cheek_right};
            case Constants.PART_HAND:
                return new int[]{R.id.btn_hand};
            default:
                return new int[0];
        }
    }
    
//    private int getStringId(int partId) {
//        switch (partId) {
//        case Constants.PART_FOREHEAD:
//            return R.string.normal_forehead;
//        case Constants.PART_EYE:
//            return R.string.normal_eye;
//        case Constants.PART_NOSE:
//            return R.string.normal_nose;
//        case Constants.PART_CHEEK:
//            return R.string.normal_cheek;
//        case Constants.PART_HAND:
//            return R.string.normal_hand;
//        default:
//            return 0;
//        }
//    }

    private void saveTempResult() {
    	if(prevPart == 0) {
    		return;
    	}
    	TestSkinReq result = (TestSkinReq) getIntent().getSerializableExtra(Constants.EXTRA_TAKING_RESULT(prevPart));
    	if(result == null) {
    		return;
    	}
    	String filepath = Constants.PRODUCT_TEST_TEMP_RESULT_PATH(this);
    	File file = new File(filepath);
    	if(file.isFile()) {
    		file.delete();
    	}
    	SerializableUtil.writeSerializableObject(filepath, result);
    }

    @Override
    protected void onDestroy() {
        ViewUtils.releaseBackground(rg_taste);
        ViewUtils.releaseBackground(btn_forehead);
        ViewUtils.releaseBackground(btn_eye_left);
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
