package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/6/10.
 */
public class SeekBarDoubleView extends View {

    private Context mContext;
    private int mSpacing;
    private int mSmallRound;
    private int mBigRound;
    private int mSeekBarHeight;
    private int mSeekBarRadius;

    private int mTextSpacing;// 如果上方的文字重叠在一起，需要左右显示的距离
    private RectF mSeekBarRound = new RectF();

    private Paint mBigText;
    private Paint mSmallText;
    private Paint mSmallCirclePaint;
    private Paint mBigCirclePaint;
    private Paint mSmallStrokePaint;
    private Paint mBigStrokePaint;
    private Paint mSeekBarPaint;

    private int mMin = 0;
    private int mMax = 100;

    private int mBigValue;
    private int mSmallValue;

    public SeekBarDoubleView(Context context) {
        this(context, null);
    }

    public SeekBarDoubleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        Resources res = context.getResources();
        mSpacing = res.getDimensionPixelSize(R.dimen.default_distance_small);
        mSmallRound = res.getDimensionPixelSize(R.dimen.test_daily_avg_oval_round);
        mBigRound = res.getDimensionPixelSize(R.dimen.test_daily_my_oval_round);
        mSeekBarHeight = res.getDimensionPixelSize(R.dimen.test_daily_seek_height);
        mSeekBarRadius = res.getDimensionPixelSize(R.dimen.test_daily_seek_radius);
        mTextSpacing = res.getDimensionPixelSize(R.dimen.default_distance_small);
        setUpPaint();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect textRect = new Rect();
                mBigText.getTextBounds("0", 0, 1, textRect);
                int left = getPaddingLeft() + mBigRound / 2 + (int) mBigStrokePaint.getStrokeWidth() / 2 + 1;
                int top = getPaddingTop() + textRect.height()
                        + mSpacing// 文字与图形的距离
                        + (mBigRound / 2 - mSeekBarHeight / 2)//
                        - (int) (mBigStrokePaint.getStrokeWidth() / 2f + 0.5f);// 圆的线的距离
                int right = getMeasuredWidth() - (getPaddingRight() + mBigRound / 2 + (int) mBigStrokePaint.getStrokeWidth() / 2 + 1);
                int bottom = top + mSeekBarHeight;
                mSeekBarRound.set(left, top, right, bottom);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setUpPaint() {
        Resources res = mContext.getResources();

        mBigText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigText.setTextAlign(Paint.Align.CENTER);
        mBigText.setColor(res.getColor(R.color.test_primary));
        mBigText.setTextSize(res.getDimensionPixelSize(R.dimen.test_daily_seek_number_text));

        mSmallText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallText.setTextAlign(Paint.Align.CENTER);
        mSmallText.setColor(res.getColor(R.color.default_text_color));
        mSmallText.setTextSize(res.getDimensionPixelSize(R.dimen.test_daily_seek_number_text));

        mSeekBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeekBarPaint.setColor(res.getColor(R.color.test_primary));
        mSeekBarPaint.setStyle(Paint.Style.FILL);


        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint.setStyle(Paint.Style.FILL);
        mSmallCirclePaint.setColor(res.getColor(R.color.test_daily_seek_p_fill));

        mBigCirclePaint = mSmallCirclePaint;

        mSmallStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallStrokePaint.setStyle(Paint.Style.STROKE);
        mSmallStrokePaint.setColor(res.getColor(R.color.test_daily_seek_p_stroke));
        mSmallStrokePaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.test_daily_seek_p_stroke));

        mBigStrokePaint = mSmallStrokePaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mSeekBarRound, mSeekBarRadius, mSeekBarRadius, mSeekBarPaint);

        // 这是直径所以要除2
        float bigX = mSeekBarRound.left + mSeekBarRound.width() * ((float) mBigValue / (mMax - mMin));
        canvas.drawCircle(bigX, mSeekBarRound.centerY(), mBigRound / 2f, mBigCirclePaint);
        canvas.drawCircle(bigX, mSeekBarRound.centerY(), mBigRound / 2f, mBigStrokePaint);

        float smallX = mSeekBarRound.left + mSeekBarRound.width() * ((float) mSmallValue / (mMax - mMin));
        canvas.drawCircle(smallX, mSeekBarRound.centerY(), mSmallRound / 2f, mSmallCirclePaint);
        canvas.drawCircle(smallX, mSeekBarRound.centerY(), mSmallRound / 2f, mSmallStrokePaint);

        Rect bTextRound = new Rect();
        String bText = "" + mBigValue;
        mBigText.getTextBounds(bText, 0, bText.length(), bTextRound);
        bTextRound.offset((int) bigX, getPaddingTop());
        float bTextX = bigX;
        float bTextY = getPaddingTop() + bTextRound.height();

        Rect sTextRound = new Rect();
        String sText = "" + mSmallValue;
        mSmallText.getTextBounds(sText, 0, sText.length(), sTextRound);
        sTextRound.offset((int) smallX, getPaddingTop());
        float sTextX = smallX;
        float sTextY = getPaddingTop() + sTextRound.height();

        /*if(Rect.intersects(bTextRound, sTextRound) && mBigValue != mSmallValue) {
            Rect unionRound = new Rect();
            unionRound.set(bTextRound);
            unionRound.union(sTextRound);
            float radius = mBigRound / 2f + mBigStrokePaint.getStrokeWidth() / 2f;// 大圆的半径
            int totalWidth = bTextRound.width() + sTextRound.width() + mTextSpacing;// 文字的宽度
            unionRound.left = unionRound.centerY() - totalWidth / 2;
            unionRound.right = unionRound.centerY() + totalWidth / 2;
            if(mBigValue < mSmallValue) {
                if(mBigText.getTextAlign() == Paint.Align.CENTER) {
                    bTextX = unionRound.left + bTextRound.width() / 2f;
                } else if(mBigText.getTextAlign() == Paint.Align.LEFT) {
                    bTextX = unionRound.left;
                } else if(mBigText.getTextAlign() == Paint.Align.RIGHT) {
                    bTextX = unionRound.left + bTextRound.width();
                }
                if(mSmallText.getTextAlign() == Paint.Align.CENTER) {
                    sTextX = unionRound.right - sTextRound.width() / 2f;
                } else if(mSmallText.getTextAlign() == Paint.Align.LEFT) {
                    sTextX = unionRound.right - sTextRound.width();
                } else if(mSmallText.getTextAlign() == Paint.Align.RIGHT) {
                    sTextX = unionRound.right;
                }
            } else {
                if(mSmallText.getTextAlign() == Paint.Align.CENTER) {
                    sTextX = unionRound.left + sTextRound.width() / 2f;
                } else if(mSmallText.getTextAlign() == Paint.Align.LEFT) {
                    sTextX = unionRound.left;
                } else if(mSmallText.getTextAlign() == Paint.Align.RIGHT) {
                    sTextX = unionRound.left + sTextRound.width();
                }
                if(mBigText.getTextAlign() == Paint.Align.CENTER) {
                    bTextX = unionRound.right - bTextRound.width() / 2f;
                } else if(mBigText.getTextAlign() == Paint.Align.LEFT) {
                    bTextX = unionRound.right - bTextRound.width();
                } else if(mBigText.getTextAlign() == Paint.Align.RIGHT) {
                    bTextX = unionRound.right;
                }
            }
            float w = mBigStrokePaint.getStrokeWidth();
            mBigStrokePaint.setStrokeWidth(1);
            canvas.drawRect(unionRound, mBigStrokePaint);
            mBigStrokePaint.setStrokeWidth(w);
        }
        canvas.drawText(sText, sTextX, sTextY, mSmallText);*/
        canvas.drawText(bText, bTextX, bTextY, mBigText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    }

    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            Rect textRect = new Rect();
            mBigText.getTextBounds("0", 0, 1, textRect);
            result = getPaddingTop() + getPaddingBottom() + textRect.height() + mSpacing + mBigRound;
            // Measure the height
            // result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureLong(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        result = specSize;
        return result;
    }

    private void notifyValueChanged() {
        invalidate();
    }

    public void setValues(int small, int big) {
        setSmallValue(small);
        setBigValue(big);
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        this.mMin = min;
    }

    public int getBigValue() {
        return mBigValue;
    }

    public void setBigValue(int bigValue) {
        if(bigValue < mMin) {
            bigValue = mMin;
        } else if(bigValue > mMax) {
            bigValue = mMax;
        }
        this.mBigValue = bigValue;
        notifyValueChanged();
    }

    public int getSmallValue() {
        return mSmallValue;
    }

    public void setSmallValue(int smallValue) {
        if(smallValue < mMin) {
            smallValue = mMin;
        } else if(smallValue > mMax) {
            smallValue = mMax;
        }
        this.mSmallValue = smallValue;
        notifyValueChanged();
    }
}
