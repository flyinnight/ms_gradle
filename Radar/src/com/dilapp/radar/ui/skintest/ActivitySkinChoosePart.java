package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.LinearRadioGroup;
import com.dilapp.radar.wifi.AllKfirManager;

import java.util.HashMap;

/**
 * Created by husj1 on 2015/6/8.
 */
public class ActivitySkinChoosePart extends BaseActivity implements View.OnClickListener, CameraVideoHelper.OnPreTakeStateChangedListener {

    // 测试的部位有多少个
    private static final int[] PARTS_ID = Constants.SKIN_TEST_PARTS;
    private static final String CHOOSED_PARTS_NAME = "Choosed Parts Key__.";
    private static final String CHOOSED_INDEX_NAME = "Choosed Index Key__-";
    private static final int REQ_TEST_SKIN = 2;
    private TitleView mTitle;
    private PromptInfoView mPrompt;

    private TextView tv_msg;
//    private LinearRadioGroup rg_skin;
//    private RadioButton btn_forehead;
//    private RadioButton btn_cheek_left;
//    private RadioButton btn_cheek_right;
    private ImageView iv_logo;
    private Button btn_test;

    private CameraVideoHelper mCameraHelp;

    private int infoId = -1;
    private boolean isTest;
    private int prevPart;// 上一个测试的部位
    
    private TitleNotify mTitleNotify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_choose_part);
        Context context = getApplicationContext();
        ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);

        mCameraHelp = new CameraVideoHelper(context, null, false);
        mCameraHelp.setOnPreTakeStateChangedListener(this);

        Intent data = getIntent();
        prevPart = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, 0);
        isTest = data.getBooleanExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, false);

        View title = findViewById(R.id.vg_toolbar);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.skin_title, null);
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

        View prompt = findViewById(R.id.vg_infos);
        mPrompt = new PromptInfoView(this, prompt);

        tv_msg = findViewById_(R.id.tv_msg);
        // TODO old
//        rg_skin = findViewById_(R.id.rg_skin);
//        rg_skin.setVisibility(View.VISIBLE);
//        rg_skin.setDisableChnage(false);

//        btn_forehead = (RadioButton) rg_skin.findViewById(R.id.btn_forehead);
//        btn_cheek_left = (RadioButton) rg_skin.findViewById(R.id.btn_cheek_left);
//        btn_cheek_right = (RadioButton) rg_skin.findViewById(R.id.btn_cheek_right);
//        btn_forehead.setOnClickListener(this);
//        btn_cheek_left.setOnClickListener(this);
//        btn_cheek_right.setOnClickListener(this);
        iv_logo = findViewById_(R.id.iv_logo);
        btn_test = (Button) getWindow().getDecorView().findViewWithTag("btn_forehead");

        int index = 0;
        int[] parts = data.getIntArrayExtra(CHOOSED_PARTS_NAME);
        if (parts == null || parts.length == 0) {
            parts = new int[PARTS_ID.length];
            data.putExtra(CHOOSED_INDEX_NAME, index);
            data.putExtra(CHOOSED_PARTS_NAME, parts);
        } else {
            index = data.getIntExtra(CHOOSED_INDEX_NAME, 0);
            data.putExtra(CHOOSED_INDEX_NAME, ++index);
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] != 0) {
                    if (parts[i] == Constants.PART_CHEEK) {
                        setCheckedAndDisable(R.id.btn_cheek_left);
                        setCheckedAndDisable(R.id.btn_cheek_right);
                        continue;
                    }
                    setCheckedAndDisable(getViewId(parts[i]));
                }
            }
        }
        String[] nums = getResources().getStringArray(R.array.chinese_number);
        tv_msg.setText(getString(R.string.skin_please_choose_part, nums[1 + index]));

        if (prevPart != 0) {
            // rg_skin.setBackgroundResource(R.drawable.img_skin_cheek);
            btn_test.setId(R.id.btn_cheek_left);
            setResult(RESULT_FIRST_USER);
            startActivity(Constants.PART_CHEEK, getString(R.string.normal_cheek));
            // finish();
            // return;
        }
        test();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchState();
        mCameraHelp.onResume();
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
        mCameraHelp.onPause();
        if(mTitleNotify != null){
			mTitleNotify.onPause();
		}
    }

    private void test() {
        if (!isTest) {
            return;
        }
        Toast.makeText(this, "您正在进行测试。", Toast.LENGTH_SHORT).show();
        mTitle.setCenterText(R.string.skin_title, new View.OnClickListener() {
            boolean tmp;

            @Override
            public void onClick(View v) {
                if (tmp) {
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
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_INITNG) {
            infoId = -1;
            setCurrentUIState(true, null);
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_SUCCESS) {
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
            case R.id.btn_cheek_left:
            case R.id.btn_cheek_right:
                startActivity(Constants.PART_CHEEK, getString(R.string.normal_cheek));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQ_TEST_SKIN == requestCode) {
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

    private void startActivity(int id, String title) {
        Intent data = getIntent();
        Intent intent = new Intent(this, ActivityTakingSkin.class);
        intent.putExtras(data.getExtras());
        int currIndex = data.getIntExtra(CHOOSED_INDEX_NAME, 0);
        int[] parts = data.getIntArrayExtra(CHOOSED_PARTS_NAME);
        parts[currIndex] = id;
        Bundle bundle = null;
        if (prevPart == 0) {

            intent.putExtra(Constants.EXTRA_SKIN_TAKING_RESULT_BUTTON_TEXTS,
                    new String[]{getString(R.string.test_next)});
            intent.putExtra(Constants.EXTRA_SKIN_TAKING_RESULT_ACTIVITIES,
                    new String[]{ActivitySkinChoosePart.class.getName(),
                            ActivitySkinResult.class.getName()});
            intent.putExtra(Constants.EXTRA_SKIN_TAKING_LOGOS,
                    new int[]{R.drawable.img_taking_forehead, R.drawable.img_taking_cheek});


            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    // Now we provide a list of Pair items which contain the view we can transitioning
                    // from, and the name of the view it is transitioning to, in the launched activity
                    new Pair<View, String>(iv_logo,
                            "share:logo")).toBundle();
        }
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO, title);
        intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, id);
        ActivityCompat.startActivityForResult(this, intent, REQ_TEST_SKIN, bundle);

        // startActivityForResult(intent, REQ_TEST_SKIN);
        if (prevPart == 0) {
            // TODO old
            // rg_skin.clearCheck();
        }
    }

    private void setCheckedAndDisable(int id) {
        // TODO old
//        RadioButton rb = (RadioButton) rg_skin.findViewById(id);
//        rb.setChecked(true);
//        rb.setEnabled(false);
    }

    private int getViewId(int partId) {
        int id = 0;
        switch (partId) {
            case Constants.PART_FOREHEAD:
                id = R.id.btn_forehead;
                // case Constants.PART_CHEEK:
                break;
            default:
                id = 0;
        }
        return id;
    }

    private void dispatchState() {
        if (!mCameraHelp.isBleCorrect()) {
            infoId = 0;
            setCurrentUIState(false, getString(R.string.test_ble_dis_msg));
        } else if (!mCameraHelp.isWifiCorrect()) {
            infoId = 2;
            setCurrentUIState(false, getString(R.string.test_wifi_dis_msg));
        } else {
            infoId = -1;
            setCurrentUIState(true, null);
        }
    }

    private void setCurrentUIState(boolean state, String msg) {
        if (isTest) {
            state = true;
            msg = null;
        }
        state = true;
        // TODO old
//        btn_forehead.setEnabled(state);
//        btn_cheek_left.setEnabled(state);
//        btn_cheek_right.setEnabled(state);

        int[] parts = getIntent().getIntArrayExtra(CHOOSED_PARTS_NAME);
        for (int i = 0; i < parts.length; i++) {
//        	Slog.e("Parts :           "+Integer.toHexString(parts[i]));
            if (parts[i] == 0) {
//                continue;
            } else if (parts[i] == Constants.PART_CHEEK) {
                // TODO old
//                findViewById(R.id.btn_cheek_left).setEnabled(false);
//                findViewById(R.id.btn_cheek_right).setEnabled(false);
            } else if (parts[i] == Constants.PART_FOREHEAD) {
//            	Slog.e("into  : PART_FOREHEAD");
                // TODO old
                // btn_forehead.setEnabled(false);
            }
        }
        mPrompt.setInfoText(null, this);
//        mPrompt.setInfoText(msg, this);
    }

    @Override
    protected void onDestroy() {
        // TODO old
//        ViewUtils.releaseBackground(rg_skin);
//        ViewUtils.releaseBackground(btn_forehead);
//        ViewUtils.releaseBackground(btn_cheek_left);
//        ViewUtils.releaseBackground(btn_cheek_right);
        mCameraHelp.onDestroy();
        if(mTitleNotify != null){
			mTitleNotify.onDestroy();
        }
        super.onDestroy();
    }


    class SortValue implements Comparable<SortValue> {
        String name;
        int value;

        public SortValue(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int compareTo(SortValue another) {
            if (another == null) {
                return -1;
            }
            return this.value < another.value ? -1 : 1;
        }
    }
}
