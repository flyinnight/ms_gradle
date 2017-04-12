package com.dilapp.radar.ui.skintest;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.dilapp.radar.R;
import com.dilapp.radar.widget.BaseDialog;

public class LowPowerDialog extends BaseDialog{
	
	private Button mBtnClose;
	private android.view.View.OnClickListener mCloseListener;

	public LowPowerDialog(Activity acitvity) {
		super(acitvity, R.style.BottomDialog);
		// TODO Auto-generated constructor stub
		initView();
		initDialog();
	}
	
	private void initView() {
        setContentView(R.layout.dialog_low_power);
        
        mBtnClose = findViewById_(R.id.dialog_close);
        
        mBtnClose.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mCloseListener != null){
					mCloseListener.onClick(v);
				}
				dismiss();
			}
		});
    }

    private void initDialog() {
//        setWidthFullScreen();
    		setFullScreen();
//        Window window = getWindow();
//        window.setGravity(Gravity.TOP);
        setCanceledOnTouchOutside(true);
    }
    
    public void setButtonsOnClickListener(
    		android.view.View.OnClickListener close){
    		mCloseListener = close;
    }

}
