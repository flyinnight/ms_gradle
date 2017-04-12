package com.dilapp.radar.ui.skintest;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircieDoubleView extends View {

    private Rect mRound;// 整个控件的矩形区域
    private int mSpacing;// 外圆与内圆的距离

    private int mBigStrokeWidth;
    private int mBigColor;
    private int mBigBackground;
    private int mSmallStrokeWidth;
    private int mSmallColor;
    private int mSmallBackground;
    private int mTextSize;
    private int mTextColor;

    private Paint mBigBg;
    private Paint mBigValue;
    private Paint mSmallBg;
    private Paint mSmallValue;
    private Paint mText;

    private int mMin = 0;
    private int mMax = 100;

    private int vBigValue = 60;
    private int vSmallValue = 40;
    private String vText = "水分";

    private int mProgress = 100;
    
    private Rect tmp = new Rect();// 用于绘画用的

    public CircieDoubleView(Context context) {
        this(context, null);
    }

    public CircieDoubleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 该控件如果设置为wrap_content的话，可绘区域默认为100dp(可绘区域不包括padding)
        mRound = new Rect(0, 0, dip2px(100), dip2px(100));// 控件可绘画的矩形区域
        mSpacing = dip2px(6f);// 外圆与内圆的距离
        mBigStrokeWidth = dip2px(6.6f);
        mBigColor = 0xffe94661;
        mBigBackground = 0x4de94661;
        mSmallStrokeWidth = dip2px(6.6f);
        mSmallColor = 0xff898989;
        mSmallBackground = 0x4d898989;
        mTextSize = dip2px(18);
        mTextColor = 0xff464646;

        setUpPaint();
    }

    private void setUpPaint() {

        mBigBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigBg.setColor(mBigBackground);
        mBigBg.setStyle(Paint.Style.STROKE);
        mBigBg.setStrokeWidth(mBigStrokeWidth);

        mBigValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigValue.setColor(mBigColor);
        mBigValue.setStyle(Paint.Style.STROKE);
        mBigValue.setStrokeWidth(mBigStrokeWidth);

        mSmallBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallBg.setColor(mSmallBackground);
        mSmallBg.setStyle(Paint.Style.STROKE);
        mSmallBg.setStrokeWidth(mSmallStrokeWidth);

        mSmallValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallValue.setColor(mSmallColor);
        mSmallValue.setStyle(Paint.Style.STROKE);
        mSmallValue.setStrokeWidth(mSmallStrokeWidth);

        mText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mText.setColor(mTextColor);
        mText.setTextSize(mTextSize);
        mText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float width = mRound.width();
        final float height = mRound.height();
        final float centerX = mRound.exactCenterX();
        final float centerY = mRound.exactCenterY();

        float bigRadius = (Math.min(width, height) - mBigBg.getStrokeWidth()) / 2f;
        canvas.drawCircle(centerX, centerY, bigRadius, mBigBg);
        RectF bigOval = calcArc(centerX, centerY, bigRadius);
        canvas.drawArc(bigOval, 90f, 360f * ((float) vBigValue / (mMax - mMin)), false, mBigValue);

        float smallRadius = bigRadius - ((mBigBg.getStrokeWidth() + mSmallBg.getStrokeWidth()) / 2f) - mSpacing;
        canvas.drawCircle(centerX, centerY, smallRadius, mSmallBg);
        RectF smallOval = calcArc(centerX, centerY, smallRadius);
        canvas.drawArc(smallOval, 90f, 360f * ((float) vSmallValue / (mMax - mMin)), false, mSmallValue);

        if (vText != null) {
            Rect txt = tmp;
            float tx = 0, ty = 0;
            mText.getTextBounds(vText, 0, vText.length(), txt);
            if (mText.getTextAlign() == Paint.Align.CENTER) {
                tx = centerX;
                ty = centerY + txt.height() / 2f - 1;
            } else if (mText.getTextAlign() == Paint.Align.LEFT) {
                tx = centerX - txt.width() / 2f;
                ty = centerY + txt.height() / 2f - 1;
            } else {
                tx = centerX + txt.width() / 2f;
                ty = centerY + txt.height() / 2f - 1;
            }
            canvas.drawText(vText, tx, ty, mText);
        }

    }

    private RectF calcArc(float centerX, float centerY, float radius) {
        RectF rect = new RectF();
        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        return rect;
    }

    private float offsetProgress() {

        return 0;
    }

    public void show(int mis) {
        mProgress = 0;
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "progress", 100).setDuration(mis);
        anim.start();
    }

    public int getSmallValue() {
        return vSmallValue;
    }

    public void setSmallValue(int smallValue) {
        this.vSmallValue = smallValue;
        invalidate();
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        this.mMin = min;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public int getBigValue() {
        return vBigValue;
    }

    public void setBigValue(int bigValue) {
        this.vBigValue = bigValue;
        invalidate();
    }

    public String getText() {
        return vText;
    }

    public void setText(String text) {
        this.vText = text;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRound.set(0, 0, measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        setMeasuredDimension(mRound.width(), mRound.height());
        mRound.left += getPaddingLeft();
        mRound.top += getPaddingTop();
        mRound.right -= getPaddingRight();
        mRound.bottom -= getPaddingBottom();
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the height
        	result = mRound.width() + getPaddingLeft() + getPaddingRight();
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the height
            result = mRound.height() + getPaddingTop() + getPaddingBottom();
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int dip2px( float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
