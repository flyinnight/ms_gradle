package com.dilapp.radar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.dilapp.radar.R;

public class CircularImage extends MaskedImage {

	private final static int DEFAULT_EDGE_COLOR = 0xffffffff;
	private int mEdgeColor;
	public CircularImage(Context paramContext) {
		this(paramContext, null);
	}

	public CircularImage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircularImage(Context context, AttributeSet attrs, int paramInt) {
		super(context, attrs, paramInt);

		int edgeColor = DEFAULT_EDGE_COLOR;
		if(attrs != null) {
			TypedArray attributes = context.obtainStyledAttributes(attrs,
					R.styleable.CircularImage,
					R.attr.edgeColor, 0);
			edgeColor = attributes.getColor(R.styleable.CircularImage_edgeColor, DEFAULT_EDGE_COLOR);
			attributes.recycle();
		}
		mEdgeColor = edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.mEdgeColor = edgeColor;
	}

	public int getEdgeColor() {
		return this.mEdgeColor;
	}

	public Bitmap createMask() {
		int i = getWidth();
		int j = getHeight();
		Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
		Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint(1);
		localPaint.setColor(mEdgeColor);
		float f1 = getWidth();
		float f2 = getHeight();
		RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
		localCanvas.drawOval(localRectF, localPaint);
		return localBitmap;
	}
}
