package com.dilapp.radar.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dilapp.radar.R;
import com.dilapp.radar.widget.WaitingDialog;

public class ActivityHelper {

    private final static String TAG = ActivityHelper.class.getName();
    private final static boolean DEBUG = true;

    /**
     * 对于不需要写布局文件的UI
     * @param context
     * @return
     */
    public static ViewGroup getFragmentContainer(Context context) {
        FrameLayout fl = new FrameLayout(context);
        fl.setId(R.id.fragment_container);
        return fl;
    }

    private Activity mActivity;

    private WaitingDialog mWaitingDialog;

    public ActivityHelper(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /*private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mActivity.setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.actionbar_bg);
            SystemBarConfig config = tintManager.getConfig();
            //listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
        }
    }*/

    /**
     * 显示等待对话框
     *
     * @param task 你的异步任务，如果用户点击Back键，可以取消正在执行的异步任务.没有可以为null
     */
    public void showWaitingDialog(final AsyncTask<?, ?, ?> task) {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(mActivity);
        }
        if (mWaitingDialog.isShowing()) {
            return;
        }
        if (task != null) {
            mWaitingDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    l("user cancel this task");
                    if (!task.isCancelled()) {
                        task.cancel(true);
                    }
                }
            });
        }
        mWaitingDialog.show();
    }

    /**
     * 显示等待对话框
     *
     * @param contextState 你的状态模式，如果用户点击Back键，并且当前正在执行的State实现了{@link StopStateable},将会调用
     *                     {@link StopStateable#stop()}
     */
    public void showWaitingDialog(final ContextState contextState) {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(mActivity);
        }
        if (mWaitingDialog.isShowing()) {
            return;
        }
        if (contextState != null) {
            mWaitingDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    l("user cancel this task");
                    if (contextState.getState() instanceof StopStateable) {
                        ((StopStateable) contextState.getState()).stop();
                    }
                }
            });
        }
        mWaitingDialog.show();
    }

    /**
     * 设置等待的文本
     *
     * @param text
     */
    public void setWaitingText(CharSequence text) {
        if (mWaitingDialog != null) {
            mWaitingDialog.setText(text);
        } else {
            w("WaitingDialog is null! setText(" + text + ") fail!");
        }
    }

    /**
     * 关闭等待对话框
     */
    public void dimessWaitingDialog() {
        if (mWaitingDialog == null) {
            return;
        }
        if (!mWaitingDialog.isShowing()) {
            return;
        }
        mWaitingDialog.setOnCancelListener(null);
        mWaitingDialog.dismiss();
    }

    public interface StopStateable {
        void stop();
    }

    protected void l(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    protected void w(String msg) {
        Log.w(TAG, msg);
    }
}
