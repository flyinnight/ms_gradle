package com.dilapp.radar.view;

import com.dilapp.radar.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBar extends RelativeLayout {
	private ImageView leftButton, rightButton;
	private TextView titleTextView;
	private OnLeftAndRightClickListener listener;// 监听点击事件

	// 设置监听器
	public void setOnLeftAndRightClickListener(
			OnLeftAndRightClickListener listener) {
		this.listener = listener;
	}

	// 设置左边按钮的可见性
	public void setLeftButtonVisibility(boolean flag) {
		if (flag)
			leftButton.setVisibility(VISIBLE);
		else
			leftButton.setVisibility(INVISIBLE);
	}

	// 设置右边按钮的可见性
	public void setRightButtonVisibility(boolean flag) {
		if (flag)
			rightButton.setVisibility(VISIBLE);
		else
			rightButton.setVisibility(INVISIBLE);
	}

	// 按钮点击接口
	public interface OnLeftAndRightClickListener {
		public void onLeftButtonClick();

		public void onRightButtonClick();
	}

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.activity_title, this);
		leftButton = (ImageView) findViewById(R.id.iv_left_back);
		rightButton = (ImageView) findViewById(R.id.iv_right_back);
		titleTextView = (TextView) findViewById(R.id.tv_title);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onLeftButtonClick();// 点击回调

			}
		});
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onRightButtonClick();// 点击回调
			}
		});
		// 获得自定义属性并赋值
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TopBar);
		int leftBtnBackground = typedArray.getResourceId(
				R.styleable.TopBar_leftBackground, R.drawable.btn_back);
		int rightBtnBackground = typedArray.getResourceId(
				R.styleable.TopBar_rightBackground, R.drawable.btn_back);
		String titleText = typedArray.getString(R.styleable.TopBar_titleText);
		float titleTextSize = typedArray.getDimension(
				R.styleable.TopBar_titleTextSize, 18);
		int titleTextColor = typedArray.getColor(
				R.styleable.TopBar_titleTextColor, R.color.whilte);
		typedArray.recycle();// 释放资源

		leftButton.setBackgroundResource(leftBtnBackground);
		rightButton.setBackgroundResource(rightBtnBackground);
		titleTextView.setText(titleText);
		titleTextView.setTextSize(titleTextSize);
		titleTextView.setTextColor(titleTextColor);
	}
}
