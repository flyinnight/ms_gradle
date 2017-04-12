/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dilapp.radar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;

import com.dilapp.radar.R;

// TODO: Auto-generated Javadoc
/**
 * 
 * Copyright (c) 2012 All rights reserved 名称：AbSlidingButton.java 描述：滑动按钮
 * 
 * @author zhaoqp
 * @date：2013-11-14 上午11:31:21
 * @version v1.0
 */
public class SlidingButton extends CheckBox {
	private final static int SHAKE = 5;// 点击防抖
	private Bitmap bg_on, bg_off, slipper_btn;
//	private int[] bg_on_res;
//	private int[] bg_off_res;

	private Context context;
	/**
	 * 按下时的x和当前的x
	 */
	private float downX, nowX;

	/**
	 * 记录用户是否在滑动
	 */
	private boolean onSlip = false;

	/**
	 * 当前的状态
	 */
	// private boolean nowStatus = false;

	private boolean entry;

	public SlidingButton(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public SlidingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public void init() {
//		bg_on_res = new int[] { R.drawable.btn_switch_off_01,
//				R.drawable.btn_switch_off_02, R.drawable.btn_switch_off_03,
//				R.drawable.btn_switch_off_04, R.drawable.btn_switch_off_05,
//				R.drawable.btn_switch_off_06, R.drawable.btn_switch_off_07,
//				R.drawable.btn_switch_off_08, R.drawable.btn_switch_off_09,
//				R.drawable.btn_switch_off_10 };
//		bg_off_res = new int[] { R.drawable.btn_switch_on_01,
//				R.drawable.btn_switch_on_02, R.drawable.btn_switch_on_03,
//				R.drawable.btn_switch_on_04, R.drawable.btn_switch_on_05,
//				R.drawable.btn_switch_on_06, R.drawable.btn_switch_on_07,
//				R.drawable.btn_switch_on_08, R.drawable.btn_switch_on_09,
//				R.drawable.btn_switch_on_10 };
		// 载入图片资源
		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.img_switch_on);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.img_switch_off);
		slipper_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.img_switch_track_dark);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(bg_on.getWidth(), bg_on.getHeight());
	}

	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = 0;
		int end = bg_on.getWidth() - slipper_btn.getWidth();

		if (onSlip) {// 是否是在滑动状态,
			if (nowX >= bg_on.getWidth()) {// 是否划出指定范围,不能让滑块跑到外头,必须做这个判断
				x = bg_on.getWidth() - slipper_btn.getWidth() / 2;// 减去滑块1/2的长度
			} else {
				x = nowX - slipper_btn.getWidth() / 2;
			}
		} else {
			if (isChecked()) {// 根据当前的状态设置滑块的x值
				x = bg_on.getWidth() - slipper_btn.getWidth();
			} else {
				x = 0;
			}
		}

		// 对滑块滑动进行异常处理，不能让滑块出界
		if (x < 0) {
			x = 0;
		} else if (x > end) {
			x = end;
		}

		// 根据nowX设置背景，开或者关状态
		Bitmap bg = null;
		// if (nowX < (bg_on.getWidth() / 2)) {
		// bg = bg_off;
		// // canvas.drawBitmap(, matrix, paint);// 画出关闭时的背景
		// } else {
		// bg = bg_on;
		// }
		int width = end;
		if (isChecked()) {
//			int s = (int) (x / width / bg_off_res.length * 100);
//			int index = s < 0 ? 0
//					: s >= bg_off_res.length ? bg_off_res.length - 1 : s;
			// bg = BitmapUtils.readBitmap(context, bg_off_res[index], 0);
			bg = bg_on;
		} else {
//			int s = (int) (x / width / bg_on_res.length * 100);
//			int index = s < 0 ? 0
//					: s >= bg_on_res.length ? bg_on_res.length - 1 : s;
			// bg = BitmapUtils.readBitmap(context, bg_on_res[index], 0);
			bg = bg_off;

		}

		canvas.drawBitmap(bg, matrix, paint);// 画出打开时的背景
		// 画出滑块
		canvas.drawBitmap(slipper_btn, x, 0, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}
		// setEnabled(enabled);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (event.getX() > bg_off.getWidth()
					|| event.getY() > bg_off.getHeight()) {
				return false;
			} else {
				onSlip = true;
				downX = event.getX();
				nowX = downX;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			nowX = event.getX();
			if (Math.abs(downX - nowX) > SHAKE && !entry) {
				entry = true;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			onSlip = false;
			// boolean old = isChecked();
			boolean nowStatus = isChecked();
			if (Math.abs(downX - event.getX()) < SHAKE && !entry) {
				nowStatus = !nowStatus;
			} else if (event.getX() >= (bg_on.getWidth() / 2)) {
				nowStatus = true;
				nowX = bg_on.getWidth() - slipper_btn.getWidth();
			} else {
				nowStatus = false;
				nowX = 0;
			}
			setChecked(nowStatus);
			entry = false;
			break;
		}
		}
		// 刷新界面
		invalidate();
		return true;
	}

	/**
	 * 设置滑动开关的初始状态，供外部调用
	 * 
	 * @param checked
	 */
	@Override
	public void setChecked(boolean checked) {
		if (checked) {
			nowX = bg_off.getWidth();
		} else {
			nowX = 0;
		}
		super.setChecked(checked);
	}

}
