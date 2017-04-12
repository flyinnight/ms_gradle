package com.dilapp.radar.widget;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dilapp.radar.R;

public class WaitingDialog extends BaseDialog {

	private TextView text1;

	public WaitingDialog(Activity acitvity) {
		super(acitvity);
		initView();
		initDialog();
	}

	public WaitingDialog(Activity activity, int theme) {
		super(activity, theme);
		initView();
		initDialog();
	}

	private void initView() {
		setContentView(R.layout.dialog_waiting);
		ImageView anim = findViewById_(android.R.id.icon1);
		text1 = findViewById_(android.R.id.text1);
		// 这里需要手动启动一下动画，否则在有的手机不会自动启动
		((AnimationDrawable) anim.getBackground()).start();
	}

	private void initDialog() {
		setCanceledOnTouchOutside(false);
	}

	public void setText(CharSequence text) {
		text1.setText(text);
	}

	public void setText(int res) {
		text1.setText(res);
	}
}
