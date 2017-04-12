package com.dilapp.radar.view;

import java.util.List;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.dilapp.radar.R;

/**
 * Created by wangxing on 2015/4/23.
 */
public class SpiderWebViewSurface extends SurfaceView implements SurfaceHolder.Callback {
    private final static String TAG = SpiderWebViewSurface.class.getSimpleName();
    private final static boolean LOG = true;

    public final static SpiderWebType[] TYPES = new SpiderWebType[]{
            new SpiderWebType("水份", Color.parseColor("#000000"), Color.parseColor("#019DED")),
            new SpiderWebType("Q弹", Color.parseColor("#000000"), Color.parseColor("#D566C4")),
            new SpiderWebType("毛孔", Color.parseColor("#000000"), Color.parseColor("#A67C52")),
            new SpiderWebType("美白", Color.parseColor("#000000"), Color.parseColor("#FE728D")),
            new SpiderWebType("敏感", Color.parseColor("#000000"), Color.parseColor("#FF0018")),
            new SpiderWebType("油份", Color.parseColor("#000000"), Color.parseColor("#F88043")),
            new SpiderWebType("油份", Color.parseColor("#000000"), Color.parseColor("#F88043")),
    };

    private SpiderWebType[] mTypes = TYPES;
    private int mMin;// 所给值的最小值
    private int mMax;// 所给值的最大值
    private List<int[]> mValues;// 值
    private List<Paint> mStyles;// 样式
    private int mProgress;// 用来做动画的
    private Interpolator mInterpolator = new DecelerateInterpolator();

    private int rRadius;// 背景色圆的半径
    private int rWebStrokeWidth;// 网的宽度
    private int rWebRadius;// 蜘蛛网小圆的半径
    private int rWebCircleEdge;// 蜘蛛网小圆距离边缘的距离
    private int rTextEdgeDistance;// 圆外边的文本距离圆的边缘的距离
    private int rArcHeight;// 弧的高度

    private int mViewWidth;
    private int mViewHeight;

    private int mCenterX;
    private int mCenterY;
    private int mRulerLength;

    private Paint pCircle;
    private Paint pWeb;
    private Paint pWebCircle;
    private Paint pWebEdge;
    private Paint pTypeText;
    private Paint pValueText;

    private Resources res;
    private boolean isRealy;
    private boolean isLayout;
    private boolean isCreated;
    private boolean isSetValue;

    public SpiderWebViewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        res = context.getResources();

        rRadius = res.getDimensionPixelSize(R.dimen.spiderWeb_circle_radius);
        rWebRadius = res.getDimensionPixelSize(R.dimen.spiderWeb_web_circle_radius);
        rWebCircleEdge = res.getDimensionPixelSize(R.dimen.spiderWeb_web_circle_apart_edge);
        rWebStrokeWidth = res.getDimensionPixelSize(R.dimen.spiderWeb_web_width);
        rTextEdgeDistance = res.getDimensionPixelSize(R.dimen.spiderWeb_edge_text_distance);
        rArcHeight = res.getDimensionPixelSize(R.dimen.spiderWeb_value_arc_height);

        getHolder().addCallback(this);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                mViewWidth = getMeasuredWidth();
                mViewHeight = getMeasuredHeight();

                mCenterX = mViewWidth / 2;
                mCenterY = mViewHeight / 2;

                mRulerLength = rRadius - rWebRadius - rWebCircleEdge;

                setUpPaint();
                // test();

                isLayout = true;
                if (isCreated) {
                    isRealy = true;
                    realy();
                }
                l("layout finish, w is " + mViewWidth + ", h is " + mViewHeight + ", r is " + rRadius);
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void test() {
        if (mTypes == null) {
            return;
        }
        Random ran = new Random();
        for (int i = 0; i < mTypes.length; i++) {
            mTypes[i].setValue(ran.nextInt(100));
        }
    }

    private void setUpPaint() {
        pCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        pCircle.setColor(res.getColor(R.color.spiderWeb_circle_color));
        pCircle.setStyle(Style.FILL);
        pCircle.setShadowLayer(
                res.getDimensionPixelSize(R.dimen.spiderWeb_circle_shadow_radius),
                0, 0, res.getColor(R.color.spiderWeb_circle_shadow_color));

        pWeb = new Paint(Paint.ANTI_ALIAS_FLAG);
        pWeb.setColor(res.getColor(R.color.spiderWeb_web_color));
        pWeb.setStyle(Style.STROKE);
        pWeb.setStrokeWidth(rWebStrokeWidth);
        pWeb.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 1));

        pWebEdge = new Paint(pWeb);
        pWebEdge.setPathEffect(null);

        pWebCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        pWebCircle.setColor(res.getColor(R.color.spiderWeb_web_color));

        pTypeText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pTypeText.setColor(res.getColor(R.color.spiderWeb_type_text_color));
        pTypeText.setTextSize(res.getDimensionPixelSize(R.dimen.spiderWeb_type_text_size));
        pTypeText.setTextAlign(Paint.Align.CENTER);

        pValueText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pValueText.setTextAlign(Paint.Align.CENTER);
        pValueText.setTextSize(res.getDimensionPixelSize(R.dimen.spiderWeb_value_text_size));
    }

    void realy() {
        l("realy go! isSetValue " + isSetValue);
        Canvas canvas = getHolder().lockCanvas();
        clear(canvas);
        drawScene(canvas, false);
        getHolder().unlockCanvasAndPost(canvas);
        if (isSetValue) {
            setProgress(mProgress);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        l("create!");
        isCreated = true;
        if (isLayout) {
            isRealy = true;
            realy();
        }
        //清屏过程

//        Paint clearPaint = new Paint();
//        clearPaint.setAntiAlias(true);
//        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        Canvas canvas = holder.lockCanvas();
//        canvas.drawRect(0, 0, mViewWidth, mViewHeight, clearPaint);
//        canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
//        canvas.drawBitmap(mDrawingView.mBitmap, 0, 0, mDrawingView.mBitmapPaint);
//        mDrawingView.mSurfaceHolder.unlockCanvasAndPost(canvas);
//        holder.unlockCanvasAndPost(canvas);

//        Canvas canvas = holder.lockCanvas();
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        holder.unlockCanvasAndPost(canvas);
        // run();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isCreated = false;
        isRealy = false;
    }

    private void drawScene(Canvas canvas, boolean onlyLine) {
        // 画背景圆，大圆
        canvas.drawCircle(mCenterX, mCenterY, rRadius, pCircle);
        // testDraw(canvas);
        int lineCount = mTypes.length;

        float degrees = 360f / lineCount;
        float rulerLength = mRulerLength;// perimeter / directionSize;

        float cosine = (float) (Math.sqrt(Math.pow(rulerLength, 2) + Math.pow(rulerLength, 2) - 2f * rulerLength * rulerLength * Math.cos(Math.toRadians(degrees))));

        // l("cosine is " + cosine + ", Math.pow(lineW, 2) is " + Math.pow(rulerLength, 2) + ", degrees is " + degrees + ", Math.cos(degrees) is " + Math.cos(Math.toRadians(degrees)));
        // cosine /= cosine;
        float innerDegrees = 180f / lineCount;
        l("" + canvas.getClass().getName());
        canvas.save();
        for (int i = 0; i < lineCount; i++) {
            float webCircleX = (float) mCenterX;
            float webCircleY = (float) mCenterY - rulerLength;//rRadius + rWebCircleEdge + rWebRadius;

            // 画虚线 我发现SurfaceView的Canvas和普通控件的Canvas画虚线画出来的效果不同
            // 普通控件在设置过Paint的Effect值后通过drawLine画不出虚线
            // 而SurfaceView可以，所以做个判断
            if("android.view.Surface.CompatibleCanvas".equals(canvas.getClass().getName())) {
            	 canvas.drawLine(mCenterX, mCenterY, webCircleX, webCircleY, pWeb);
            } else {
	            Path path = new Path();
	            path.moveTo(mCenterX, mCenterY);
	            path.lineTo(webCircleX, webCircleY);
	            canvas.drawPath(path, pWeb);
            }
            canvas.drawCircle(webCircleX, webCircleY, rWebRadius, pWebCircle);// 画小白圆

            canvas.rotate(innerDegrees, webCircleX, webCircleY);// 转过去
            canvas.drawLine(webCircleX, webCircleY, webCircleX + cosine, webCircleY, pWebEdge);// 画实线
            canvas.rotate(-innerDegrees, webCircleX, webCircleY);// 再转过来

            if (!onlyLine) {
                float valueX = mCenterX;
                float valueY = mCenterY - rRadius - rTextEdgeDistance;
                pValueText.setColor(mTypes[i].getValueColor());
                canvas.rotate(-(degrees * i), valueX, valueY);// 转过去
                canvas.drawText(mTypes[i].getValue() + "", valueX, valueY, pValueText);
                canvas.rotate((degrees * i), valueX, valueY);// 转过去

                float nameX = mCenterX;
                float nameY = mCenterY - rRadius - rTextEdgeDistance - pValueText.getTextSize();
                pTypeText.setColor(mTypes[i].getNameColor());
                canvas.rotate(-(degrees * i), nameX, nameY);// 转过去
                canvas.drawText(mTypes[i].getName() + "", nameX, nameY, pTypeText);
                canvas.rotate((degrees * i), nameX, nameY);// 再转过来
            }

            canvas.rotate(degrees, mCenterX, mCenterY);
        }
        canvas.restore();
    }


    private void testDraw(Canvas canvas) {
        float[] rulers1 = new float[mTypes.length];
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(Color.BLUE);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(3);
        Random ran = new Random();
        // int temp1 = ran.nextInt(mRulerLength);
        for (int i = 0; i < rulers1.length; i++) {
            rulers1[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers1, paint1);

        float[] rulers2 = new float[mTypes.length];
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(3);
        for (int i = 0; i < rulers2.length; i++) {
            rulers2[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers2/*new float[]{ 60f, 70f }*/, paint2);

        float[] rulers3 = new float[mTypes.length];
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(Color.YELLOW);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(3);
        for (int i = 0; i < rulers3.length; i++) {
            rulers3[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers3, paint3);
    }

    private void drawValue(Canvas canvas, float[] rulers, Paint paint) {
        if (rulers == null) {
            return;
        }
        clear(canvas);
        // TODO drawValue
        drawScene(canvas, false);
        int lineCount = mTypes.length;
        float degrees = 360f / lineCount;
        canvas.save();
        for (int i = 0; i < rulers.length; i++) {
            float rulerA = rulers[(i)];
            float rulerB = rulers[(i + 1 == rulers.length ? 0 : i + 1)];
            float webCircleX = (float) mCenterX;
            float webCircleY = (float) mCenterY - rulerA;

            float rulerC = (float) (Math.sqrt(Math.pow(rulerA, 2) + Math.pow(rulerB, 2) - 2f * rulerA * rulerB * Math.cos(Math.toRadians(degrees))));

            float[] edge = new float[]{rulerA, rulerB, rulerC};
            //Arrays.sort(edge);

            double arccosine = (Math.pow(edge[2], 2) + Math.pow(edge[0], 2) - Math.pow(edge[1], 2)) / (2 * edge[2] * edge[0]);
            arccosine = (Math.toDegrees(Math.acos(arccosine < -1.0 ? -1.0 : arccosine > 1.0 ? 1.0 : arccosine)));
            if (arccosine >= 180f - degrees)
                arccosine = 180f - degrees - arccosine;
            float innerDegrees = 90f - (float) arccosine;//180f / lineCount;// + 60f - (float) arccosine;// 180f - degrees - (float) arccosine;
            canvas.rotate(innerDegrees, webCircleX, webCircleY);// 转过去
            canvas.drawLine(webCircleX, webCircleY, webCircleX + rulerC, webCircleY, paint);// 画实线
//            RectF rect = new RectF();
//            rect.left = webCircleX;
//            rect.top = webCircleY;
//            rect.right = webCircleX + rulerC;
//            rect.bottom = webCircleY + rArcHeight;
//            canvas.drawArc(rect, 3, 86, false, paint);
            canvas.rotate(-innerDegrees, webCircleX, webCircleY);// 再转过来
            l("三角形的3个角分别为：" + degrees + ", " + arccosine + ", " + (180 - degrees - arccosine));

            //l("a is " + rulerA + ", b is " + rulerB + ", cosine is " + rulerC + ", degrees is " + degrees + ", arccosine is " + arccosine);
            canvas.rotate(degrees, mCenterX, mCenterY);
        }
        canvas.restore();
    }

    /**
     * 直接显示数据
     */
    public void show() {
        setProgress(100);
    }

    /**
     * 这是一个动画
     *
     * @param duration 数据显示完成需要的时间，单位 毫秒
     */
    public void show(final int duration) {
        if (Build.VERSION.SDK_INT < 11) {
            setProgress(100);
        } else {
            mProgress = 0;
            ObjectAnimator anim = ObjectAnimator.ofInt(this, "progress", 100).setDuration(duration);
            anim.setInterpolator(mInterpolator);
            anim.start();
        }
    }

    private void notifyDrawValues(int progress) {
        if (mValues == null) {
            return;
        }
        l("drawValues size " + mValues.size() + ", progress is " + progress);
        for (int i = 0; i < mValues.size(); i++) {
            int[] values = mValues.get(i);
            if (values == null) {
                continue;
            }

            Paint paint = null;
            if (i >= mStyles.size()) {
                paint = mStyles.get(mStyles.size() - 1);
            } else {
                paint = mStyles.get(i);
            }
            float[] rulers = new float[values.length];
            for (int j = 0; j < values.length; j++) {
                int value = values[j] < mMin ? mMin : values[j] > mMax ? mMax : values[j];
                float ruler = mRulerLength * (value / (float) mMax);
                rulers[j] = progressRulers(progress, ruler);
                l("values[" + j + "] = " + value + ", " + "rulers[" + j + "] = " + ruler + "-" + rulers[j] + ", rulerLength = " + mRulerLength);
            }
            Canvas canvas = getHolder().lockCanvas();
            drawValue(canvas, rulers, paint);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    // 转换一下
    private float progressRulers(int progress, float ruler) {
        //for (int i = 0; i < rulers.length; i++) {
        // int value = rulers[j] < mMin ? mMin : values[j] > mMax ? mMax : values[j];
        // * (value / (float) mMax);
        //}
        // 少了下面这个 f ,动画效果就出不来了
        return ruler * (progress / 100f);
    }

    private void clear(Canvas canvas) {
        clear(canvas, new Rect(0, 0, mViewWidth, mViewHeight));
    }

    private void clear(Canvas canvas, Rect rect) {
        Bitmap buffer = Bitmap.createBitmap(rect.right, rect.bottom, Bitmap.Config.ARGB_4444);buffer.eraseColor(Color.TRANSPARENT);
        canvas.drawBitmap(buffer, 0, 0, null);
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public SpiderWebType[] getTypes() {
        return mTypes;
    }

    public void setTypes(SpiderWebType[] mTypes) {
        this.mTypes = mTypes;
    }

    public List<int[]> getValues() {
        return mValues;
    }

    /**
     * 值都是0-100之间
     *
     * @param values
     */
    public void setValues(List<int[]> values, List<Paint> styles) {
        this.mValues = values;
        this.mStyles = styles;
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

    public void setMinAndMax(int min, int max) {
        this.mMin = min;
        this.mMax = max;
    }

    // 这个方法去掉的话会报错
    int getProgress() {
        return mProgress;
    }

    /**
     * 在调用这个方法前请设置好{@link #setValues(java.util.List, java.util.List)}
     *
     * @param progress
     */
    void setProgress(int progress) {
        // 做了这个边界判断的话，OvershootInterpolator的效果就不出来了
        // 所以我还是不做这个判断了吧
        // 反正这个方法又不给外部用
//        if (progress < 0) {
//            progress = 0;
//        } else if (progress > 100) {
//            progress = 100;
//        }
        this.mProgress = progress;

        if (!isRealy) {
            isSetValue = true;
            return;
        }
        notifyDrawValues(mProgress);
    }

    public static class SpiderWebType {
        public final static int MIN = 0;
        public final static int MAX = 100;
        private String name;
        private int nameColor;
        private int valueColor;

        private int value;
        private int min = MIN;
        private int max = MAX;

        private Paint rulerStyle;

        public SpiderWebType() {
        }

        public SpiderWebType(String name, int nameColor, int valueColor) {
            this.name = name;
            this.nameColor = nameColor;
            this.valueColor = valueColor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNameColor() {
            return nameColor;
        }

        public void setNameColor(int nameColor) {
            this.nameColor = nameColor;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getValueColor() {
            return valueColor;
        }

        public void setValueColor(int valueColor) {
            this.valueColor = valueColor;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public Paint getRulerStyle() {
            return rulerStyle;
        }

        public void setRulerStyle(Paint rulerStyle) {
            this.rulerStyle = rulerStyle;
        }
    }

    private static void l(String msg) {
        if (LOG)
            Log.d(TAG, msg);
    }
}

