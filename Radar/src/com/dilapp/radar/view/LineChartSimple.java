package com.dilapp.radar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;

import java.util.Random;

/**
 * Created by wangxing on 2015/4/28.
 * 简易图表
 */
public class LineChartSimple extends HorizontalScrollView {
    private final static String TAG = SpiderWebView.class.getSimpleName();
    private final static boolean LOG = true;

    public static final int CMD_WEB = 1;

    private Context mContext;
    private int mWidth;
    private int mHeight;

    private Paint pLine;
    private Paint pDate;
    private Paint pDotValue;
    private Paint pLineValue;

    private int command = CMD_WEB;

    private int[] testValues;

    public LineChartSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        testValues = testValues(7, 100);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = getWidth();
                mHeight = getHeight();

                setUpPaint();
                l("w " + mWidth + ", h " + mHeight + ", mw " + getMeasuredWidth() + ", mh " + getMeasuredHeight());
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void setUpPaint() {
        float density = mContext.getResources().getDisplayMetrics().density;
        pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        pLine.setColor(Color.parseColor("#33cccccc"));
        pLine.setStyle(Paint.Style.STROKE);
        pLine.setStrokeWidth(.5f * density + .5f);

        pDate = new Paint(Paint.ANTI_ALIAS_FLAG);
        pDate.setTextAlign(Paint.Align.CENTER);
        pDate.setColor(Color.parseColor("#999999"));
        pDate.setTextSize(13f * density + .5f);

        pDotValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        // pDotValue.setShader(new LinearGradient());
        //pDotValue.setColor(Color.RED);
        pDotValue.setStyle(Paint.Style.FILL);

        pLineValue = new Paint(Paint.ANTI_ALIAS_FLAG);
        pLineValue.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(1800, 720);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        switch (command) {
            case CMD_WEB:
                drawWeb(canvas);
                drawData(canvas);
                // command = 0;
                break;
        }
    }

    /**
     * 画网格背景
     *
     * @param canvas
     */
    private void drawWeb(Canvas canvas) {
        int lineCount = 9;
        float lineHeight = (mHeight - pDate.getTextSize()) / (lineCount + 1);
        for (int i = 0; i < lineCount; i++) {
            canvas.drawLine(0, i * lineHeight, mWidth, i * lineHeight + -(pLine.getStrokeWidth() / 2), pLine);
        }
        for (int i = 0; i < 7; i++) {
            canvas.drawText("" + (3 + i), (mWidth / 8) * (i + 1), (lineCount - 1) * lineHeight + pDate.getTextSize()
                    , pDate);
        }
    }

    private void drawData(Canvas canvas) {
        float density = mContext.getResources().getDisplayMetrics().density;

        int lineCount = 9;
        float lineHeight = (mHeight - pDate.getTextSize()) / (lineCount + 1);
        float bottom = lineHeight * (lineCount - 1);

        float radius = 3.3f * density + .5f;
        Path path = new Path();
        path.moveTo(0, bottom);
        for (int i = 0; i < 7; i++) {
            float x = mWidth / 8 * (i + 1);
            float y = bottom - testValues[i] / 100f * bottom - radius / 2 + pLine.getStrokeWidth() / 2;
            path.lineTo(x, y);
//            canvas.drawText("" + (3 + i), (mWidth / 8) * (i + 1), (lineCount - 1) * lineHeight + pDate.getTextSize()
//                    , pDate);
        }
        path.lineTo(mWidth, bottom);
        path.close();
        pLineValue.setShader(new LinearGradient(0, 0, 0, bottom, Color.parseColor("#4CFC6868"), Color.parseColor("#4CFFDCDC"), Shader.TileMode.CLAMP));
        canvas.drawPath(path, pLineValue);

        for (int i = 0; i < 7; i++) {
            float x = mWidth / 8 * (i + 1);
            float y = bottom - testValues[i] / 100f * bottom - radius / 2 + pLine.getStrokeWidth() / 2;
            pDotValue.setShader(new LinearGradient(x, y + radius / 2, x, y - radius / 2, Color.parseColor("#FC6868"), Color.parseColor("#FFDCDC"), Shader.TileMode.CLAMP));
            canvas.drawCircle(x, y, radius, pDotValue);
//            canvas.drawText("" + (3 + i), (mWidth / 8) * (i + 1), (lineCount - 1) * lineHeight + pDate.getTextSize()
//                    , pDate);
        }
    }

    private int[] testValues(int len, int max) {
        Random ran = new Random();
        int[] values = new int[len];
        for(int i = 0; i < values.length; i++) {
            values[i] = ran.nextInt(max);
        }
        return values;
    }

    private static void l(String msg) {
        if (LOG)
            Log.d(TAG, msg);
    }
}
