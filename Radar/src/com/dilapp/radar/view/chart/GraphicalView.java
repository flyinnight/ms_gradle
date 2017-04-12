package com.dilapp.radar.view.chart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.dilapp.radar.view.chart.RadarChartView.Points;

import android.view.View;

/**
 * Created by husj1 on 2015/10/8.
 */
public class GraphicalView extends View {

    private final static String TEXT_MEASURE = "字";
    int mStart;
    RadarChartStyle mStyle;
    RadarChartAdapter mAdapter;
    private Points mPoints;

    private Rect mRound;
    private Bitmap mDrawing;
    private Canvas mDrawboard;
    private PointF mFrontTemp;

    private Rect titleBound = new Rect();
    private Rect secondTitleBound = new Rect();
    private Rect pointTextBound = new Rect();
    private boolean isMeasure;

    public GraphicalView(Context context) {
        this(context, null);
    }

    public GraphicalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphicalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public Points getPoints() {
        return mPoints;
    }

    public void setPoints(Points points) {
        this.mPoints = points;
        if (isMeasure && mDrawboard != null) {
            if ((mDrawing == null || mDrawing.isRecycled())) {
                int w = getMeasuredWidth(), h = getMeasuredHeight();
                d("setPoints " + w + "x" + h);
                mDrawboard.setBitmap(mDrawing = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888));
            }
            drawingBoard();
            postInvalidate();
            // invalidate();
        }
    }

    void recycle() {
        mPoints = null;
        if (mDrawing != null && !mDrawing.isRecycled()) {
            mDrawing.recycle();
        }
        if (mDrawing != null) {
            mDrawing = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w, h;
        setMeasuredDimension(
                w = RadarChartStyle.measure(widthMeasureSpec, mStyle.graphicalWidth),
                h = RadarChartStyle.measure(heightMeasureSpec, mStyle.graphicalHeight)
        );
        isMeasure = true;
        if (mDrawing == null || mDrawboard == null) {
            mRound = new Rect(0, 0, w, mStyle.graphicalChartHeight);
            mDrawing = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mDrawboard = new Canvas(mDrawing);
            drawingBoard();
        }
        d("onMeasure " + w + "x" + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawing != null && !mDrawing.isRecycled()) {
            canvas.drawBitmap(mDrawing, 0, 0, null);
            d("onDraw draw");
        } else {
            d("onDraw not draw");
        }
    }

    void drawingBoard() {
        if (!isMeasure) return;

        Canvas canvas = mDrawboard;

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // 计算垂直每个单元的宽度
        canvas.drawRect(0, 0, mStyle.graphicalWidth, mStyle.graphicalChartHeight, mStyle.mBackground);
        float splitWidth = (float) mStyle.graphicalWidth / mStyle.graphicalSplitCount;
        float location = splitWidth / 2f;
        d("splitWidth " + splitWidth + ", location " + location + ", height " + canvas.getHeight());

        for (int i = mStyle.graphicalSplitCount - 1; i >= 0; i--) {
            float currX = location + splitWidth * i;
            canvas.drawLine(
                    currX, 0, currX, mStyle.graphicalChartHeight,
                    mStyle.mBackgroundVerticalLine);
            // mStyle.mBackgroundVerticalLine.
        }
        drawTitles(canvas);
        drawLines(canvas);
    }

    private void drawTitles(Canvas canvas) {

        float splitWidth = (float) mStyle.graphicalWidth / mStyle.graphicalSplitCount;
        float location = splitWidth / 2f;
        int titleHeight = mStyle.graphicalHeight - mRound.height();
        final int maxDis = RadarChartStyle.dip2px(getContext(), 6);
        final float disPc = 0.2f;
        int index = -1;
        for (int i = mStyle.graphicalSplitCount - 1; i >= 0; i--) {
            index++;
            float currX = location + splitWidth * i;

            if (titleHeight > 0 && mAdapter != null) {
                String title = mAdapter.getTitle(index + mStart);
                String secondTitle = mAdapter.getSecondTitle(index + mStart);

                if (title != null) {// 这个“字”是用来测量文本宽高的，并没有其他意思
                    mStyle.mTitlePaint.getTextBounds(TEXT_MEASURE, 0, 1, titleBound);
                }

                if (secondTitle != null) {
                    mStyle.mSecondTitlePaint.getTextBounds(TEXT_MEASURE, 0, 1, secondTitleBound);
                }

                int dis = (int) ((titleHeight - titleBound.height() - secondTitleBound.height()) * disPc);
                dis = dis > maxDis ? maxDis : dis;
                if (titleHeight >= titleBound.height() + secondTitleBound.height()) {
                    // 正 副标题同时存在。
                    int h = titleBound.height() + secondTitleBound.height();
                    int startx = (titleHeight - h - dis) / 2;
                    int tx = mRound.height() + startx + titleBound.height();
                    int stx = tx + dis + secondTitleBound.height();
                    if (title != null) {
                        canvas.drawText(title, currX, tx, mStyle.mTitlePaint);
                    }
                    if (secondTitle != null) {
                        canvas.drawText(secondTitle, currX, stx, mStyle.mSecondTitlePaint);
                    }
                    d("th " + titleBound.height() + ", sth " + secondTitleBound.height() + ", h " + titleHeight + ", dis " + dis);
                } else if (title != null && titleHeight >= titleBound.height()) {
                    // 只存在正标题
                    int startx = (titleHeight - titleBound.height()) / 2;
                    int tx = mRound.height() + startx + titleBound.height();
                    canvas.drawText(title, currX, tx, mStyle.mTitlePaint);
                }
            }
        }
    }

    private void drawLines(Canvas canvas) {

        if (mPoints == null) {
            return;
        }
        float splitWidth = (float) mStyle.graphicalWidth / mStyle.graphicalSplitCount;
        float location = splitWidth / 2f;
        if (mPoints.mDatas == null) {
            if (mPoints.mDataBehinds != null && mPoints.mDataFronts != null) {

            }
            return;
        }
        for (int i = 0; i < mPoints.mDatas.length; i++) {
            if (mPoints.mDatas[i] == null || mPoints.mDatas[i].length == 0) {
                if (mPoints.mDataFronts != null && mPoints.mDataBehinds != null) {
                    float x = location + splitWidth * (mStyle.graphicalSplitCount - 1 + mPoints.mFrontSub[i]);
                    float y = value2y(mAdapter.getPointY(mPoints.mDataFronts[i]));
                    float toX = location + splitWidth * -mPoints.mBehindSub[i];
                    float toY = value2y(mAdapter.getPointY(mPoints.mDataBehinds[i]));
                    canvas.drawLine(x, y, toX, toY, mStyle.mLine);
                }
                continue;
            }

            if (mPoints.mDataFronts != null && i < mPoints.mDataFronts.length && mPoints.mDataFronts[i] != null) {
                // 看上一个GraphicalView有没有
                float x = location + splitWidth * (mStyle.graphicalSplitCount - 1 + mPoints.mFrontSub[i]);
                float y = value2y(mAdapter.getPointY(mPoints.mDataFronts[i]));
                mFrontTemp = new PointF(x, y);
            }

            int len = mStyle.graphicalSplitCount - mPoints.mDatas[i].length;
            if (len < 0) len = 0;
            int index = -1;
            for (int j = mStyle.graphicalSplitCount - 1; j >= len; j--) {
                index++;
                float x = location + splitWidth * j;

                Object o = mPoints.mDatas[i][index];
                if (o == null) continue;

                int value = mAdapter.getPointY(o);
                if (value < 0) value = 0;
                else if (value > 100) value = 100;

                float y = value2y(value);

                if (mFrontTemp != null) {
                    // 左边为前，右边为后
                    // 当前点连接后面一个点的线
                    canvas.drawLine(x, y, mFrontTemp.x, mFrontTemp.y, mStyle.mLine);
                }

                canvas.drawCircle(x, y, mStyle.chartForegroundPointRadius, mStyle.mPoint);

                // 画点上的文字
                String pointText = mAdapter.getPointText(o);
                if (pointText != null) {
                    int dist = RadarChartStyle.dip2px(getContext(), 7f);
                    int padding = RadarChartStyle.dip2px(getContext(), 7f);
                    mStyle.mPointText.getTextBounds(pointText, 0, pointText.length(), pointTextBound);

                    float ptx, pty;
                    if (y - mStyle.chartForegroundPointRadius / 2f - dist - pointTextBound.height() - padding >= 0) {
                        pty = y - mStyle.chartForegroundPointRadius / 2f - dist;
                    } else {
                        pty = y + mStyle.chartForegroundPointRadius / 2f + dist + pointTextBound.height();
                    }
                    if (x - pointTextBound.width() / 2f - padding < 0) {
                        ptx = padding + pointTextBound.width() / 2f;
                    } else if (x + pointTextBound.width() / 2f + padding > mStyle.graphicalWidth) {
                        ptx = mStyle.graphicalWidth - padding - pointTextBound.width() / 2f;
                    } else {
                        ptx = x;
                    }
                    canvas.drawText(pointText, ptx, pty, mStyle.mPointText);
                }


                if (mFrontTemp == null) mFrontTemp = new PointF();
                mFrontTemp.set(x, y);
            }

            if (mPoints.mDataBehinds != null && i < mPoints.mDataBehinds.length && mPoints.mDataBehinds[i] != null && mFrontTemp != null) {
                // 一般在最左边的一个，要连接到上一个GraphicalView最右边一条线
                float x = location + splitWidth * (float) -mPoints.mBehindSub[i];
                float y = value2y(mAdapter.getPointY(mPoints.mDataBehinds[i]));
                // d("mDrawboard " + canvas + " mFrontTemp " + mFrontTemp + " mLine " + mStyle.mLine);
                canvas.drawLine(x, y, mFrontTemp.x, mFrontTemp.y, mStyle.mLine);
            }

            mFrontTemp = null;
        }
    }

    private float value2y(int value) {
        int linesAreaHeight = mRound.height() -
                mStyle.graphicalRulerPaddingTop -
                mStyle.graphicalRulerPaddingBottom;
        return linesAreaHeight - linesAreaHeight / 100f * (float) value + mStyle.graphicalRulerPaddingBottom;
    }

    private void d(String msg) {
        if (false) {
            com.dilapp.radar.textbuilder.utils.L.d("III_Graphical", msg);
        }
    }
}
