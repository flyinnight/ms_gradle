package com.dilapp.radar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义带边框TextView
 * @author Administrator
 *
 */
public class BorderTextView extends TextView {

	private int sroke_width = 1;

	public BorderTextView(Context context) {
		super(context);
	}

	public BorderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		// 将边框设为红色
		paint.setColor(android.graphics.Color.RED);
		// 画TextView的4个边
		canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
		canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth()
				- sroke_width, this.getHeight() - sroke_width, paint);
		canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth()
				- sroke_width, this.getHeight() - sroke_width, paint);
		super.onDraw(canvas);
	}
}
