package com.dilapp.radar.ui.skintest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.Content;

public class ActivityMicroscope extends BaseActivity implements View.OnClickListener, CameraVideoHelper.OnTakeResultListener, CameraVideoHelper.OnPreTakeStateChangedListener {

    private Animation show = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private Animation hide = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private TitleView mTitle;

    private GLSurfaceView sv_video;

    private CameraVideoHelper mVideoHelper;

    private View v_epidermis;
    private View v_genuine;
    private ViewGroup vg_option;
    private ImageView iv_image;
    private ImageView iv_progress;
    private View ibtn_taking;

    private CustomDialog mOptionDialog;

    private Bitmap mBitmap;
    private int mCurrType = -1;
    private int infoId;

    private boolean isOnPause = false;

    private TitleNotify mTitleNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microscope);
        Context context = getApplicationContext();

        show.setDuration(200);
        show.setInterpolator(new LinearInterpolator());
        hide.setDuration(200);
        hide.setInterpolator(new LinearInterpolator());

        View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        // mTitle.setCenterText(R.string.microscope_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        mTitle.setBackgroundColor(getResources().getColor(R.color.test_microscope_cover));//(Color.TRANSPARENT);

        mTitleNotify = new TitleNotify(this, mTitle);
        mTitleNotify.setLowPowerDialog(this, true, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        mTitleNotify.setNotifyType(TitleNotify.NOTIFY_BATTERY_LOW
                | TitleNotify.NOTIFY_BATTERY_WARNING
                | TitleNotify.NOTIFY_BLE_ERROR
                | TitleNotify.NOTIFY_WIFI_ERROR, -1, this);
        // mTitle.setRightIcon(R.drawable.btn_share, this);
        sv_video = findViewById_(R.id.sv_video);
        /*sv_video.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            }
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
            }
            @Override
            public void onDrawFrame(GL10 gl) {
            }
        });*/
        vg_option = findViewById_(R.id.vg_option);
        separationOption();
        mOptionDialog = new CustomDialog(this, vg_option);
        mOptionDialog.setCanceledOnTouchOutside(false);
        mOptionDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (!isOnPause) {
                    mVideoHelper.openVideo();
                }
            }
        });
        mOptionDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                iv_image.setVisibility(View.GONE);
                iv_image.setImageBitmap(null);
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                if (!isOnPause) {
                    mVideoHelper.openVideo();
                }
            }
        });

        v_epidermis = findViewById_(R.id.v_epidermis);
        v_genuine = findViewById_(R.id.v_genuine);
        iv_image = findViewById_(R.id.iv_image);
        iv_progress = findViewById_(R.id.iv_progress);
        ibtn_taking = findViewById_(R.id.ibtn_taking);

        ((AnimationDrawable) iv_progress.getDrawable()).start();

//        mVideoHelper = new CameraVideoHelper(context, null);
        mVideoHelper = new CameraVideoHelper(context, sv_video, true);
        mVideoHelper.setOnTakeResultListener(this);
        mVideoHelper.setOnPreTakeStateChangedListener(this);
        mVideoHelper.onCreate(savedInstanceState);
        // mCurrLED = mVideoHelper.getLED();
        setImageCategory(CameraVideoHelper.LED_RGB);
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
            case R.id.vg_epidermis:
                if (mVideoHelper.isTakeable()) {
                    mVideoHelper.setLED(CameraVideoHelper.LED_RGB);
                    setImageCategory(CameraVideoHelper.LED_RGB);
                } else {
                    Toast.makeText(this, R.string.test_device_noready, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vg_genuine:
                if (mVideoHelper.isTakeable()) {
                    mVideoHelper.setLED(CameraVideoHelper.LED_PL);
                    setImageCategory(CameraVideoHelper.LED_PL);
                } else {
                    Toast.makeText(this, R.string.test_device_noready, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ibtn_taking:

                if (mVideoHelper.isTakeable()) {
                    mVideoHelper.takeVideo();
                    ibtn_taking.setClickable(false);
                } else {
                    Toast.makeText(this, R.string.test_device_noready, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                iv_image.setVisibility(View.GONE);
                iv_image.setImageBitmap(null);
                mOptionDialog.dismiss();
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                break;
            case R.id.btn_save:
                String fname = "radar" + System.currentTimeMillis() + ".png";
                File file = new File("sdcard/Pictures", fname);
                CompressFormat format = Bitmap.CompressFormat.PNG;
                int quality = 100;
                OutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                    mBitmap.compress(format, quality, stream);
                    stream.flush();
                    stream.close();
                    Toast.makeText(this, "save to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
//					MediaStore.Images.Media.insertImage(getContentResolver(),
//							file.getAbsolutePath(), fname, null);
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                } catch (Exception e) {
                    Slog.e("Save Error! ", e);
                }
                iv_image.setVisibility(View.GONE);
                iv_image.setImageBitmap(null);
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                mOptionDialog.dismiss();
                break;
            default:
                break;
        }
    }

    private void setImageCategory(int type) {
        if (mCurrType == type) {
            return;
        }
        mCurrType = type;
        if (type == CameraVideoHelper.LED_RGB) {
            if (v_epidermis.getVisibility() != View.VISIBLE) {
                v_epidermis.startAnimation(show);
                v_epidermis.setVisibility(View.VISIBLE);
            }
            if (v_genuine.getVisibility() != View.INVISIBLE) {
                v_genuine.startAnimation(hide);
                v_genuine.setVisibility(View.INVISIBLE);
            }
        } else if (type == CameraVideoHelper.LED_PL) {
            if (v_epidermis.getVisibility() != View.INVISIBLE) {
                v_epidermis.startAnimation(hide);
                v_epidermis.setVisibility(View.INVISIBLE);
            }

            if (v_genuine.getVisibility() != View.VISIBLE) {
                v_genuine.startAnimation(show);
                v_genuine.setVisibility(View.VISIBLE);
            }
        }
    }

    private void separationOption() {
        ViewGroup parent = (ViewGroup) vg_option.getParent();
        if (parent != null) {
            parent.removeView(vg_option);
        }
    }

    private void setCurrentUIState(boolean state, String msg) {
//        btn_forehead.setEnabled(state);
//        btn_eye_left.setEnabled(state);
//        btn_eye_right.setEnabled(state);
//        btn_nose.setEnabled(state);
//        btn_cheek.setEnabled(state);
//        btn_hand.setEnabled(state);
//        mPrompt.setInfoText(msg, this);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    @Override
    public void onStateChanged(int state) {
        android.util.Log.i("III", "state " + state);
        if (!AllKfirManager.getInstance(this).isOnSkinTest()) {
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
            setImageCategory(mVideoHelper.getLED());
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_SUCCESS) {
            iv_progress.setVisibility(View.GONE);
        } else if (state == CameraVideoHelper.OnPreTakeStateChangedListener.DEVICE_INITNG) {
            infoId = -1;
            setCurrentUIState(true, null);
            setImageCategory(mVideoHelper.getLED());
        }
    }

    @Override
    public void onTakeResult(boolean success) {
        if (success) {
            iv_image.setVisibility(View.VISIBLE);
            new Handler() {
                public void handleMessage(android.os.Message msg) {
                    String path = mVideoHelper.getLED() == CameraVideoHelper.LED_PL
                            ? Content.PL_PATH : Content.RGB_PATH;
                    mBitmap = rotateBitmap(BitmapFactory.decodeFile(path), -90);
                    iv_image.setImageBitmap(mBitmap);
                    mOptionDialog.show();
                    ibtn_taking.setClickable(true);
                    mVideoHelper.setLEDInternal(CameraVideoHelper.LED_RGB);
                    mVideoHelper.closeVideo();
                    setImageCategory(CameraVideoHelper.LED_RGB);
                }
            }.sendEmptyMessageDelayed(0, 300);
        } else {
            Toast.makeText(this, "拍照错误", Toast.LENGTH_SHORT).show();
            ibtn_taking.setClickable(true);
            mVideoHelper.setLEDInternal(CameraVideoHelper.LED_RGB);
            if (!isOnPause) {
                mVideoHelper.openVideo();
            }
            setImageCategory(CameraVideoHelper.LED_RGB);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVideoHelper.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        mVideoHelper.onResume();
        sv_video.setKeepScreenOn(true);
        if (mTitleNotify != null) {
            mTitleNotify.onResume();
        }
        if (AllKfirManager.getInstance(this).isCameraConnected()) {
            iv_progress.setVisibility(View.GONE);
        } else {
            iv_progress.setVisibility(View.VISIBLE);
        }
        // sv_video.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mVideoHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        // sv_video.onPause();
        isOnPause = true;
        sv_video.setKeepScreenOn(false);
        mVideoHelper.onPause();

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
        mVideoHelper.onDestroy();
        if (mTitleNotify != null) {
            mTitleNotify.onDestroy();
        }
        super.onDestroy();
    }

    private class CustomDialog extends Dialog {

        private Activity mActivity;
        private Context mContext;

        public CustomDialog(Activity activity, View content) {
            super(activity, R.style.RadarAnimationInLeftOutDownDialog);
            this.mActivity = activity;
            this.mContext = activity.getApplicationContext();
            setContentView(content);
            initDialog();
        }


        private void initDialog() {
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            WindowManager wm = mActivity.getWindowManager();
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                lp.width = wm.getDefaultDisplay().getWidth();
            } else {
                lp.width = wm.getDefaultDisplay().getHeight();
            }
            window.setGravity(Gravity.BOTTOM);
            window.setAttributes(lp);
            setCanceledOnTouchOutside(true);
        }
    }
}
