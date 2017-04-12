package com.dilapp.radar.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.dilapp.radar.R;

public class BaseDialog extends Dialog {

    protected Activity mActivity;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseDialog(Activity acitvity) {
        this(acitvity, R.style.BaseDialog);
    }

    public BaseDialog(Activity acitvity, int theme) {
        super(acitvity, theme);
        this.mActivity = acitvity;
        this.mContext = acitvity.getApplicationContext();
        this.mInflater = LayoutInflater.from(mContext);
    }

    public <T> T findViewById_(int id) {
        return (T) super.findViewById(id);
    }

    public void setWidthFullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = mActivity.getWindowManager();
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.width = wm.getDefaultDisplay().getWidth();
        } else {
            lp.width = wm.getDefaultDisplay().getHeight();
        }
        window.setAttributes(lp);
    }
    
    public void setFullScreen(){
    		Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = mActivity.getWindowManager();
        if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.width = wm.getDefaultDisplay().getWidth();
            lp.height = wm.getDefaultDisplay().getHeight();
        } else {
            lp.width = wm.getDefaultDisplay().getHeight();
            lp.height = wm.getDefaultDisplay().getWidth();
        }
        window.setAttributes(lp);
    }

    public void setG() {

    }
}
