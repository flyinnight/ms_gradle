package com.dilapp.radar.view.chart;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by husj1 on 2015/10/8.
 */
public class RadarChartStyle {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int measure(int measureSpec, int def) {
        int result;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the height
            result = def;
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    final int screenWidth;

    // 整个图表的高度
    int width;
    int height;

    // 绘画图表的宽度
    int graphicalWidth;
    int graphicalHeight;
    int graphicalChartHeight;
    int graphicalSplitCount;
    int graphicalRulerPaddingTop;// 数据点距离图表上方的距离
    int graphicalRulerPaddingBottom;// 数据点距离图表下方的距离
    int graphicalMaxOnScreen;// 在屏幕上最多能显示多少个

    // 刻度的宽度
    int rulerWidth; // = dip2px(context, 15);

    // 图表背景色
    int chartBackgroundColor;// = 0x99ea4661;

    // 图表的背景线颜色
    int chartBackgroundLineColor;

    // 图表的背景线宽度
    int chartBackgroundLineWidth;

    // 图表的前景线颜色
    int chartForegroundLineColor;

    // 图表的前景线宽度
    int chartForegroundLineWidth;

    // 图表的点的颜色
    int chartForegroundPointColor;

    // 图表的点的半径
    int chartForegroundPointRadius;

    // 图表的阴影的半径
    int chartForegroundPointShadowRadius;

    // 图表的点的外边距(就是文字离点的距离)
    int chartForegroundPointMargin;

    // 图表的文字的颜色
    int chartForegroundTextColor;

    // 图表的文字的大小
    int chartForegroundTextSize;

    int titleColor;
    int titleSize;

    int secondTitleColor;
    int secondTitleSize;


    //---------------------------------------------
    Rect mRound;//
    Rect mRuleRound;
    Paint mBackgroundVerticalLine;
    Paint mTitlePaint;
    Paint mSecondTitlePaint;
    Paint mBackground;

    Paint mPoint;
    Paint mPointText;
    Paint mLine;

    public RadarChartStyle(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();

        // 设置默认值
        width = dip2px(context, 360f);
        height = dip2px(context, 280f);

//        graphicalWidth = dip2px(context, 320f);
        graphicalWidth = dip2px(context, 373.3f);
        graphicalHeight = height;// dip2px(context, 233.33f);// 700px
        graphicalChartHeight = dip2px(context, 233.33f);
        graphicalSplitCount = 7;
        graphicalRulerPaddingTop = dip2px(context, 10f);
        graphicalRulerPaddingBottom = dip2px(context, 10f);
        graphicalMaxOnScreen = screenWidth / graphicalWidth + 2;

        rulerWidth = dip2px(context, 15f);
        chartBackgroundColor = 0x99ea4661;
        chartBackgroundLineColor = 0xffb77380;
        chartBackgroundLineWidth = dip2px(context, 1f);
        chartForegroundLineColor = 0xffffffff;
        chartForegroundLineWidth = dip2px(context, 3f);
        chartForegroundPointColor = 0xffffffff;
        chartForegroundPointRadius = dip2px(context, 3.33f);
        chartForegroundPointShadowRadius = dip2px(context, 3.33f);
        chartForegroundPointMargin = dip2px(context, 6f);
        chartForegroundTextColor = 0xffffffff;
        chartForegroundTextSize = dip2px(context, 12f);

        titleColor = 0xffffffff;
        titleSize = dip2px(context, 10.6f);
        secondTitleColor = 0xffea4661;
        secondTitleSize = dip2px(context, 10);
        // com.dilapp.radar.textbuilder.utils.L.d("III_style", "sw " + screenWidth + ", ts " + graphicalMaxOnScreen);
    }

    public RadarChartStyle build() {
        mRound = new Rect(0, 0, width, height);
        mRuleRound = new Rect(0, 0, rulerWidth, graphicalHeight);

        mBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackground.setStyle(Paint.Style.FILL);
        mBackground.setColor(chartBackgroundColor);

        mBackgroundVerticalLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundVerticalLine.setStyle(Paint.Style.FILL_AND_STROKE);
        mBackgroundVerticalLine.setColor(chartBackgroundLineColor);
        mBackgroundVerticalLine.setStrokeWidth(chartBackgroundLineWidth);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(titleColor);
        mTitlePaint.setTextSize(titleSize);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);

        mSecondTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSecondTitlePaint.setColor(secondTitleColor);
        mSecondTitlePaint.setTextSize(secondTitleSize);
        mSecondTitlePaint.setTextAlign(Paint.Align.CENTER);

        mPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPoint.setColor(chartForegroundPointColor);
        mPoint.setStyle(Paint.Style.FILL);
        mPoint.setShadowLayer(chartForegroundPointShadowRadius, 0, 0, chartForegroundPointColor);

        mPointText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointText.setColor(chartForegroundTextColor);
        mPointText.setTextSize(chartForegroundTextSize);
        mPointText.setTextAlign(Paint.Align.CENTER);

        mLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine.setColor(chartForegroundLineColor);
        mLine.setStrokeWidth(chartForegroundLineWidth);
        return this;
    }
}
