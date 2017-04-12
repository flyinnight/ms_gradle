package com.dilapp.radar.ui.skintest;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.imageanalysis.ImageProcess;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.skintest.CameraVideoHelper.*;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.view.AnimationListenerAdapter;
import com.dilapp.radar.wifi.*;


import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * startActivity(ActivityTakingSkin.class);需要以下参数，里面没做空处理，不给别怪我
 * #{@linkplain Constants#EXTRA_CHOOSE_PART_IS_TEST} boolean 想给就给
 * 这个参数是在没有视频硬件的情况下使用的
 * true为使用应用自带的皮肤图片测试，不调用拍照接口
 * false为正常情况，自然是需要硬件支持啦！
 * <p/>
 * #{@linkplain Constants#EXTRA_SKIN_TAKING_TEXT_INFO} String 必须要给
 * 部位的名称，我的标题就用到了这个值。比如 “额头皮肤图像”，当然，你只需要给我“额头”就行了
 * <p/>
 * #{@linkplain Constants#EXTRA_SKIN_TAKING_CHOOSED_PART} enum(int) 必须要给
 * 取值范围在下
 * #{@linkplain Constants#PART_FOREHEAD}, #{@linkplain Constants#PART_EYE}
 * #{@linkplain Constants#PART_NOSE}, #{@linkplain Constants#PART_CHEEK}
 * #{@linkplain Constants#PART_HAND}
 * 选择的部位的编码，值的意思就不废话了，我又不是写帮助文档的
 * <p/>
 * #{@linkplain Constants#EXTRA_SKIN_TAKING_RESULT_BUTTON_TEXTS} String[] 想给就给
 * 这个值在#{@link ActivityTakingConfirm}要用到。
 * #{@link ActivityTakingConfirm}的右下方的按钮的文字，不给就显示默认值“查看结果”
 * 问：为什么会是数组呢？
 * 答：业务需要，你可能需要在一条逻辑中多次测试皮肤，但是每次“确认拍摄图像”界面的
 * 按钮文字又需要不一样的。所以在这里按照顺序设置文字即可。
 * <p/>
 * #{@linkplain Constants#EXTRA_SKIN_TAKING_RESULT_ACTIVITIES} String[]({ Class.getName() }) 必须要给
 * 点击“查看结果”会发生什么呢？由你来决定！
 * 和楼上的那个逻辑差不多，按钮点击后所开启的Activity的全名，
 * 跳过去的时候我会把测试结果告诉你哦,
 * Key = #{@link Constants#EXTRA_TAKING_RESULT(int))
 * 还有你选择的部位
    * Key = #{@linkplain Constants#EXTRA_SKIN_TAKING_CHOOSED_PART}
    */
    public class ActivityTakingSkin extends BaseActivity implements
            View.OnClickListener, OnTakeResultListener,
            OnPreTakeStateChangedListener {

    private final static int REQ_CONFIRM_TAKING = 10;
    private TitleView mTitle;
    private PromptInfoView mPrompt;
    private ViewGroup vg_container;
    private View vg_skin_video;
    private ImageView iv_image;
    private ImageView iv_logo;
    private ImageView iv_crop;
    private ImageView iv_cover_rotate;
    private View v_door_lt;
    private View v_door_rb;
    private View btn_taking;
    private GLSurfaceView sv_video;
    private View v_crop_background;
    private Animation animRotate;
    private CameraVideoHelper mVideoHelper;
    private int infoId = -1;
    private boolean isTest;

    //add by kfir
    private boolean isOnConnNotify = false;
    private TextView mNotifyLayout;//connecting notify layout
    // private ImageView mNotifyImage;//connecting notify image
    private AnimationDrawable mNotifyDrawable;
    private TitleNotify mTitleNotify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_skin);

        Context context = getApplicationContext();

        Intent data = getIntent();
        String titleText = data.getStringExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO);

        isTest = data.getBooleanExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, false);
        int currIndex = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CURRENT_INDEX, 0);
        int[] logos = data.getIntArrayExtra(Constants.EXTRA_SKIN_TAKING_LOGOS);

        mPrompt = new PromptInfoView(this, findViewById(R.id.vg_infos));
        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(getString(R.string.taking_title, titleText), null);
        mTitle.setLeftIcon(R.drawable.btn_close_white, this);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

        mTitleNotify = new TitleNotify(this, mTitle);
        mTitleNotify.setLowPowerDialog(this, true, new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mTitleNotify.setNotifyType(TitleNotify.NOTIFY_BATTERY_LOW
                | TitleNotify.NOTIFY_BATTERY_WARNING
                | TitleNotify.NOTIFY_BLE_ERROR
                | TitleNotify.NOTIFY_WIFI_ERROR, -1, this);

        vg_container = findViewById_(R.id.vg_container);
        vg_skin_video = findViewById_(R.id.vg_skin_video);
        iv_image = findViewById_(R.id.iv_image);
        iv_logo = findViewById_(R.id.iv_logo);
        iv_crop = findViewById_(R.id.iv_crop);
        iv_cover_rotate = findViewById_(R.id.iv_cover_rotate);
        v_door_lt = findViewById_(R.id.v_door_lt);
        v_door_rb = findViewById_(R.id.v_door_rb);
        animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_taking_skin_cover_rotate);
        final Drawable rotate = iv_cover_rotate.getDrawable();
        d("III_view", "drawable " + iv_cover_rotate.getDrawable().getClass().getName());
            // ((AnimationDrawable) rotate).start();
        btn_taking = findViewById_(R.id.btn_taking);
        sv_video = findViewById_(R.id.sv_video);
        vg_skin_video.setVisibility(View.INVISIBLE);
        v_crop_background = vg_container;// findViewById_(R.id.v_crop_background);
        v_crop_background.setVisibility(View.VISIBLE);
        v_crop_background.setBackground(ZBackgroundHelper.getDrawable(this, ZBackgroundHelper.TYPE_BLACK_BLUR));
        v_crop_background.setDrawingCacheEnabled(true);
        v_crop_background.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				// 这段代码是兼容GLSurfaceView的圆形。
				v_crop_background.getViewTreeObserver().removeOnPreDrawListener(this);
				v_crop_background.buildDrawingCache();
                View target = vg_skin_video;//获取需要覆盖的控件，根据需求可更改

                // 获取被覆盖控件的相对于屏幕的 x y 值
				int[] location = new int[2];
                target.getLocationOnScreen(location);
                float centerX = location[0] + target.getMeasuredWidth() / 2f;
                float centerY = location[1] + target.getMeasuredHeight() / 2f;
                float radius = sv_video.getMeasuredHeight() / 2f;

//                d("III_view", "location " + location[0] + "," + location[1] +
//                        ",  width " + target.getMeasuredWidth() +
//                        ", height " + target.getMeasuredHeight());

                // 获取背景源(即 从它身上扣图)
	            Bitmap bitmap = v_crop_background.getDrawingCache();

	            Canvas canvas = new Canvas(bitmap);
	            Paint paint = new Paint();
                // 记得，是以清除模式画的颜色哦，会把下方的颜色剪了扔掉,想象成一张纸吧
                // 否则就像你在白色上话一个50%透明黑，还是看到的下方有点白色
	            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	            paint.setStyle(Paint.Style.FILL);// 我要画的是实心圆
	            paint.setColor(Color.TRANSPARENT);// 并且是透明的
	            canvas.drawCircle(centerX, centerY, radius , paint);// 中间剪一块扔掉

	            Bitmap b2 = Bitmap.createBitmap(bitmap, location[0], location[1],
                        target.getMeasuredWidth(), target.getMeasuredHeight());
	            iv_crop.setImageBitmap(b2);
	            v_crop_background.setDrawingCacheEnabled(false);
	            sv_video.setVisibility(View.VISIBLE);
	            vg_skin_video.setVisibility(View.VISIBLE);
	            vg_skin_video.startAnimation(
                        AnimationUtils.loadAnimation(ActivityTakingSkin.this, android.R.anim.fade_in));

	            bitmap.recycle();
				return true;
			}
		});

        mNotifyLayout = findViewById_(R.id.connect_notify_layout);
        mNotifyDrawable = (AnimationDrawable) mNotifyLayout.getCompoundDrawables()[2];
        // mNotifyImage = (ImageView) findViewById(R.id.connect_notify_image);

        mVideoHelper = new CameraVideoHelper(context, isTest ? null : sv_video, false);
        mVideoHelper.onCreate(savedInstanceState);
        mVideoHelper.setOnTakeResultListener(this);
        mVideoHelper.setOnPreTakeStateChangedListener(this);

        if (logos != null && currIndex >= 0 && currIndex < logos.length) {
            iv_logo.setVisibility(View.VISIBLE);
            iv_logo.setImageResource(logos[currIndex]);
        } else {
            iv_logo.setVisibility(View.GONE);
        }
        ViewCompat.setTransitionName(iv_logo, "share:logo");
        test();
    }

    private void test() {
        // 测试用的代码
        if (!isTest) {
            return;
        }
        String titleText = getIntent().getStringExtra(
                Constants.EXTRA_SKIN_TAKING_TEXT_INFO);

        sv_video.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
            }

            @Override
            public void onDrawFrame(GL10 gl) {
            }
        });
        mVideoHelper.setOnTakeResultListener(null);
        mVideoHelper.setOnPreTakeStateChangedListener(null);
        mTitle.setCenterText(getString(R.string.taking_title, titleText), null);
//        mTitle.setCenterText(getString(R.string.taking_title, titleText),
//                new View.OnClickListener() {
//                    boolean tmp;
//
//                    @Override
//                    public void onClick(View v) {
////                        if (tmp) {
////                            mPrompt.setInfoText(null, null);
////                        } else {
////                            mPrompt.setInfoText("异常错误", null);
////                        }
////                        tmp = !tmp;
//
//                        boolean run = isRotateRunning();
//                        if (run) {
//                            openDoorIm();
//                            stopRotateCover();
//                        } else {
//                            shutDoor();
//                            startRotateCover();
//                        }
//                    }
//                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case TitleView.ID_LEFT:
                vg_skin_video.setVisibility(View.INVISIBLE);
                vg_skin_video.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                setResult(RESULT_CANCELED);
                ActivityCompat.finishAfterTransition(this);
                break;
            case TitleView.ID_RIGHT:
                startActivity(new Intent(this, ActivityDeviceInfo.class));
                break;
            case R.id.v_cover:
                Log.i("III", "Click Cover!");
                break;
            case PromptInfoView.ID_INFOS: {
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra("infoId", infoId);
                startActivity(intent);
                break;
            }
            case R.id.btn_taking: {// 拍照测试
                if (isTest) {
                    btn_taking.setClickable(false);
                    final String test_rgb = "rgb.jpg";
                    final String test_pl = "pl.jpg";
                    new AsyncTask<String, Object, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            return ImageProcess.getInstance().checkImg(params[0]);
                        }

                        protected void onPostExecute(String result) {
                            startNextActivity(
                                    getCacheDir().getAbsolutePath() + "/" + test_rgb,
                                    getCacheDir().getAbsolutePath() + "/" + test_pl,
                                    Constants.SKIN_STRING2INTEGER(result));
                            btn_taking.setClickable(true);
                        }
                    }.execute(getCacheDir().getAbsolutePath() + "/" + test_rgb);
                } else {
                    if (mVideoHelper.isTakeable()) {
                        mVideoHelper.takeVideo();
                    } else {
                        Toast.makeText(this, R.string.test_device_noready, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        vg_skin_video.setVisibility(View.INVISIBLE);
        vg_skin_video.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CONFIRM_TAKING) {
            switch (resultCode) {
                case RESULT_OK:
                    setResult(resultCode);
                    finish();
                    break;
                case RESULT_CANCELED:
                    setResult(resultCode);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onStateChanged(int state) {
    		Slog.d("onStateChanged : "+state);
	    	if(!AllKfirManager.getInstance(this).isOnSkinTest()){
	    		Slog.e("Skin Test has End !!!!");
	    		setResult(RESULT_CANCELED);
	         finish();
	         return;
	    }
        if (state == CameraVideoHelper.OnPreTakeStateChangedListener.BLE_DISCONNECTED
                || state == CameraVideoHelper.OnPreTakeStateChangedListener.WIFI_DISCONNECTED) {
            if (isOnConnNotify) {
                return;
            }
        }

        if (mNotifyDrawable != null && mNotifyDrawable.isRunning()) {
            mNotifyDrawable.stop();
        }
        mNotifyLayout.setVisibility(View.GONE);
        iv_image.setVisibility(View.GONE);
        if (state == CameraVideoHelper.OnPreTakeStateChangedListener.BLE_DISCONNECTED) {
            infoId = 0;
            setCurrentUIState(false, getString(R.string.test_ble_dis_msg));
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.WIFI_DISCONNECTED) {
            infoId = 2;
            setCurrentUIState(false, getString(R.string.test_wifi_dis_msg));
            if(isRotateRunning()){
            	stopRotateCover();
            }
            if(v_door_lt.getVisibility() != View.VISIBLE){
        		shutDoor();
            }
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.WIFI_CONNECTED) {
            infoId = -1;
            setCurrentUIState(false, null);
            iv_image.setVisibility(View.VISIBLE);
            if(!isRotateRunning()){
            		startRotateCover();
            }
            if(v_door_lt.getVisibility() != View.VISIBLE){
            		shutDoor();
            }
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_INITNG) {
            infoId = -1;
            setCurrentUIState(false, null);
            iv_image.setVisibility(View.VISIBLE);
            if(!isRotateRunning()){
        			startRotateCover();
            }
            if(v_door_lt.getVisibility() != View.VISIBLE){
        		shutDoor();
            }
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_SUCCESS) {
            infoId = -1;
            setCurrentUIState(true, null);
            iv_image.setVisibility(View.GONE);
            if(isRotateRunning()){
    			stopRotateCover();
            }
            if(v_door_lt.getVisibility() == View.VISIBLE){
        		openDoorDelay(100);
            }
        }
    }

    @Override
    public void onTakeResult(boolean success) {

        if (success) {
            new AsyncTask<String, Object, String>() {
                @Override
                protected String doInBackground(String... params) {
                    return ImageProcess.getInstance().checkImg(params[0]);
                }

                protected void onPostExecute(String result) {
                    startNextActivity(
                            Content.RGB_PATH,
                            Content.PL_PATH,
                            Constants.SKIN_STRING2INTEGER(result));
                    btn_taking.setClickable(true);
                }
            }.execute(Content.RGB_PATH);
        } else {
            Toast.makeText(this, "拍照错误", Toast.LENGTH_SHORT).show();
            btn_taking.setClickable(true);
        }
    }

    private static final int MSG_CONN_NOTIFY_TIMEOUT = 1;
    private static final int MSG_DOOR_OPEN_DELAY = 2;
    private static final int MSG_DOOR_SHUT_DELAY = 3;
    private Handler mHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case MSG_CONN_NOTIFY_TIMEOUT:
                    Slog.d("msg conn notify timeout");
                    dispatchState(false);
                    break;
                case MSG_DOOR_OPEN_DELAY:
                		openDoorIm();
                		break;
                case MSG_DOOR_SHUT_DELAY:
                		shutDoor();
                		break;
            }
        }

    };

    private void startNextActivity(String rgb, String pl, int ckSkin) {
        Intent intent = new Intent(this, ActivityTakingConfirm.class);
        intent.putExtras(getIntent().getExtras());
        Log.i("III", "extras.size " + getIntent().getExtras().size());
        intent.putExtra(Constants.EXTRA_CONFIRM_TAKING_EPIDERMIS_PATH, rgb);
        intent.putExtra(Constants.EXTRA_CONFIRM_TAKING_GENUINE_PATH, pl);
        intent.putExtra(Constants.EXTRA_CONFIRM_TAKING_IS_SKIN_IMG, ckSkin);
        startActivityForResult(intent, REQ_CONFIRM_TAKING);
        btn_taking.setClickable(true);
    }

    private void dispatchState(boolean isconnectnotify) {
        AllKfirManager.getInstance(this).printStatus();
        isOnConnNotify = isconnectnotify;
        if (isconnectnotify) {
            infoId = -1;
            setCurrentUIState(false, null);
            if (!mVideoHelper.isBleCorrect() || !mVideoHelper.isWifiCorrect()) {
                mNotifyLayout.setVisibility(View.VISIBLE);
                if (mNotifyDrawable != null && !mNotifyDrawable.isRunning()) {
                    mNotifyDrawable.start();
                }
                iv_image.setVisibility(View.GONE);
                btn_taking.setEnabled(false);
                if(isRotateRunning()){
        			stopRotateCover();
                }
	            if(v_door_lt.getVisibility() != View.VISIBLE){
	        		shutDoor();
	            }
            } else {
                if (mNotifyDrawable != null && mNotifyDrawable.isRunning()) {
                    mNotifyDrawable.stop();
                    mNotifyDrawable = null;
                }
                mNotifyLayout.setVisibility(View.GONE);
                if (AllKfirManager.getInstance(this).isCameraConnected()) {
                    Slog.i("camera connected??");
                    iv_image.setVisibility(View.GONE);
                    btn_taking.setEnabled(true);
                    if(isRotateRunning()){
            			stopRotateCover();
                    }
	    	            if(v_door_lt.getVisibility() == View.VISIBLE){
	    	            	openDoorDelay(100);
//	    	            	mHander.sendEmptyMessageDelayed(MSG_DOOR_OPEN_DELAY, 500);
	    	            }
                } else {
                    Slog.e("camera not connected??");
                    iv_image.setVisibility(View.VISIBLE);
                    btn_taking.setEnabled(false);
                    if(!isRotateRunning()){
            			startRotateCover();
                    }
	    	            if(v_door_lt.getVisibility() != View.VISIBLE){
	    	        		shutDoor();
	    	            }
                }
            }
        } else {
            iv_image.setVisibility(View.GONE);
            if (!mVideoHelper.isBleCorrect()) {
                Slog.e("ble not connected");
                infoId = 0;
                setCurrentUIState(false, getString(R.string.test_ble_dis_msg));
            } else if (!mVideoHelper.isWifiCorrect()) {
                Slog.e("wifi not correct");
                infoId = 2;
                setCurrentUIState(false, getString(R.string.test_wifi_dis_msg));
            } else {
                infoId = -1;
                if (AllKfirManager.getInstance(this).isCameraConnected()) {
                    iv_image.setVisibility(View.GONE);
                    setCurrentUIState(true, null);
                    if(isRotateRunning()){
            			stopRotateCover();
                    }
	    	            if(v_door_lt.getVisibility() == View.VISIBLE){
	    	        		openDoorDelay(100);
	    	            }
                } else {
                    iv_image.setVisibility(View.VISIBLE);
                    setCurrentUIState(false, null);
                    if(!isRotateRunning()){
            			startRotateCover();
                    }
	    	            if(v_door_lt.getVisibility() != View.VISIBLE){
	    	        		shutDoor();
	    	            }
                }
            }
        }
    }

    private void setCurrentUIState(boolean state, String msg) {
        if (isTest) {
            state = true;
            msg = null;
        }
        // btn_forehead.setEnabled(state);
        // btn_eye_left.setEnabled(state);
        // btn_eye_right.setEnabled(state);
        // btn_nose.setEnabled(state);
        // btn_cheek.setEnabled(state);
        // btn_hand.setEnabled(state);
        btn_taking.setEnabled(state);
        mPrompt.setInfoText(msg, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVideoHelper.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTitleNotify.onResume();
        sv_video.setKeepScreenOn(true);
        if (isTest) return;
        //{Leeon--reconnect wifi if disconnected
        if (/*mVideoHelper.isBleCorrect() && */!mVideoHelper.isWifiCorrect()) {//TODO handle ble disconnect
            Slog.i("wifi disconnected, reconnecting...");
            dispatchState(true);
            if(!AllKfirManager.getInstance(this).isOnSkinTest()){
            		Slog.e("Skin Test has End !!!!");
            		setResult(RESULT_CANCELED);
                 finish();
            }else{
            		mVideoHelper.onResume();
            		AllKfirManager.getInstance(this).registerAllInfoCallback(helperCallback);
                AllKfirManager.getInstance(this).startWifiConnect();
            }
        } else {
            doVideoResume();
        }
        //}
    }

    private void doVideoResume() {
        Slog.i("do video resume");
        mVideoHelper.onResume();
        isOnConnNotify = true;
        dispatchState(true);
        mHander.removeMessages(MSG_CONN_NOTIFY_TIMEOUT);
        mHander.sendEmptyMessageDelayed(MSG_CONN_NOTIFY_TIMEOUT, 3000);
    }

    IAllKfirHelperCallback helperCallback = new IAllKfirHelperCallback() {
        @Override
        public void allInfoStatusChange(AllKfirManager.NET_UI_STATUS status) {
            if (AllKfirManager.NET_UI_STATUS.WIFI_CONNECTED == status) {
                Slog.i("wifi connected to radar device, now do video resume");
//                AllKfirManager.getInstance(ActivityTakingSkin.this).unRegisterAllInfoCallback(this);
                doVideoResume();
            }
        }

        @Override
        public void photosStatus(int mId, int status) {

        }
    };

	/*IWifiKfirHelperCallback callback = new WifiKfirHelperCallbackAdapter(){
        public void onWifiConnectSuccess(String ssid) {
			Slog.i("wifi connected to " + ssid + ", try do video resume");
			if(AllKfirManager.getInstance(ActivityTakingSkin.this).isWifiReadyForTrans()){
				Slog.i("wifi connected to radar device, now do video resume");
				WifiKfirHelper.getInstance(ActivityTakingSkin.this).removeWifiHelperUiCallback(this);
				doVideoResume();
			}else {
				Slog.i("wifi connect is no correct, waiting...");
			}
		}
	};*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mVideoHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        sv_video.setKeepScreenOn(false);
        mVideoHelper.onPause();
        if(mTitleNotify != null){
    			mTitleNotify.onPause();
    		}
        isOnConnNotify = false;
        mHander.removeMessages(MSG_CONN_NOTIFY_TIMEOUT);
        mHander.removeMessages(MSG_DOOR_OPEN_DELAY);
        mHander.removeMessages(MSG_DOOR_SHUT_DELAY);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // sw_switch.setChecked(false);
        mVideoHelper.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sv_video = null;
        mVideoHelper.onDestroy();
        mTitleNotify.onDestroy();
        super.onDestroy();
        AllKfirManager.getInstance(this).unRegisterAllInfoCallback(helperCallback);
    }

    private int du = 800;
    private Animation doorInLT, doorInRB, doorOutLT, doorOutRB;
    
    private boolean doorMoving = false;
    private boolean doorNeedOpen = false;

    private synchronized void ensureDoorAnimation() {
        if (doorInLT == null) {
            TranslateAnimation animLT =
                    new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -1f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -1f);
            animLT.setFillAfter(true);
            animLT.setFillEnabled(true);
            animLT.setInterpolator(new LinearInterpolator());
            animLT.setDuration(du);
            animLT.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                		doorMoving = false;
                    v_door_lt.setVisibility(View.GONE);
                    if(!doorNeedOpen){
                    	shutDoor();
                    }
                }
            });
            doorInLT = animLT;
        }
        if (doorInRB == null) {
            TranslateAnimation animRB =
                    new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1f);
            animRB.setFillAfter(true);
            animRB.setFillEnabled(true);
            animRB.setInterpolator(new LinearInterpolator());
            animRB.setDuration(du);
            animRB.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    v_door_rb.setVisibility(View.GONE);
                }
            });
            doorInRB = animRB;
        }
        if (doorOutLT == null) {
            TranslateAnimation animLT =
                    new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, -1f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -1f,
                            Animation.RELATIVE_TO_SELF, 0f);
            animLT.setFillAfter(true);
            animLT.setFillEnabled(true);
            animLT.setInterpolator(new LinearInterpolator());
            animLT.setDuration(du);
            animLT.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                		doorMoving = false;
                    if(doorNeedOpen){
                    	openDoorIm();
                    }
                }
            });
            doorOutLT = animLT;
        }
        if (doorOutRB == null) {
            Animation animRB =
                    new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 0f);
            animRB.setFillAfter(true);
            animRB.setFillEnabled(true);
            animRB.setInterpolator(new LinearInterpolator());
            animRB.setDuration(du);
            doorOutRB = animRB;
        }
    }
    private void openDoorDelay(long delay) {
    	Slog.e("openDoor");
    		mHander.removeMessages(MSG_DOOR_OPEN_DELAY);
        mHander.removeMessages(MSG_DOOR_SHUT_DELAY);
    		mHander.sendEmptyMessageDelayed(MSG_DOOR_OPEN_DELAY, delay);
    }
    
    private void openDoorIm(){
    		ensureDoorAnimation();
        mHander.removeMessages(MSG_DOOR_OPEN_DELAY);
        mHander.removeMessages(MSG_DOOR_SHUT_DELAY);
        doorNeedOpen = true;
        if(doorMoving){
        	return;
        }
        Animation animLT = doorInLT;
        Animation animRB = doorInRB;
        v_door_lt.startAnimation(animLT);
        v_door_rb.startAnimation(animRB);
        doorMoving = true;
    }

    public void shutDoor() {
    	Slog.e("shutDoor");
        ensureDoorAnimation();
        mHander.removeMessages(MSG_DOOR_OPEN_DELAY);
        mHander.removeMessages(MSG_DOOR_SHUT_DELAY);
        doorNeedOpen = false;
        if(doorMoving){
        	return;
        }
        Animation animLT = doorOutLT;
        Animation animRB = doorOutRB;
        v_door_lt.setVisibility(View.VISIBLE);
        v_door_rb.setVisibility(View.VISIBLE);
        v_door_lt.startAnimation(animLT);
        v_door_rb.startAnimation(animRB);
        doorMoving = true;
    }

    boolean isRunning;
    private void startRotateCover() {
        //if (!animRotate.hasStarted()) {
        iv_cover_rotate.startAnimation(animRotate);
        // animRotate.start();
        isRunning = true;
        //}
    }

    private boolean isRotateRunning() {
        return isRunning;
    }

    private void stopRotateCover() {
        //if (animRotate.hasStarted()) {
        // animRotate.cancel();
        animRotate.cancel();
        iv_cover_rotate.clearAnimation();
        isRunning = false;
        // iv_cover_rotate.setAnimation(null);
            // animRotate.reset();
        //}
    }

    public static Bitmap drawableToBitmap(Drawable drawable) { // 取 drawable 的长宽 3
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();// 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config); // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h); // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
