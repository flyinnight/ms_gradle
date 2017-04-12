package com.dilapp.radar.view;

import java.util.ArrayList;
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
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.dilapp.radar.R;

/**
 * Created by wangxing on 2015/4/23.
 */
public class SpiderWebViewSimple extends View {
    private final static String TAG = SpiderWebView.class.getSimpleName();
    private final static boolean LOG = true;

    public final static SpiderWebType[] TYPES = new SpiderWebType[]{
            new SpiderWebType("水份", Color.parseColor("#000000"),
                    Color.parseColor("#2ac1e0")),
            new SpiderWebType("Q弹", Color.parseColor("#000000"),
                    Color.parseColor("#d467c3")),
            new SpiderWebType("美白", Color.parseColor("#000000"),
                    Color.parseColor("#e5657e")),
            new SpiderWebType("毛孔", Color.parseColor("#000000"),
                    Color.parseColor("#bc9c7d")),
            new SpiderWebType("敏感", Color.parseColor("#000000"),
                    Color.parseColor("#73c87a")),
            new SpiderWebType("油份", Color.parseColor("#000000"),
                    Color.parseColor("#e78568")),
            // new SpiderWebType("油份", Color.parseColor("#000000"),
            // Color.parseColor("#F88043")),
    };

    /**
     * 画场景命令
     */
    private final static int DRAW_SCENE = 10;
    /**
     * 画值的命令
     */
    private final static int DRAW_VALUE = 20;

    /**
     * 画详情的文本
     */
    public final static int DRAW_DETAIL = 30;

    private SpiderWebType[] mTypes = TYPES;
    private String mTopText;// 内圆的文字
    private String[] mDetailTexts;// 文字详情数组
    private int mMin;// 所给值的最小值
    private int mMax;// 所给值的最大值
    private List<int[]> mValues;// 值
    private List<Paint> mStyles;// 样式
    private String mTip;
    private Paint mTipPaint;
    private Rect mTipRound = new Rect();
    private String mVal;
    private Paint mValPaint;
    private Rect mValRound = new Rect();
    private String mEval;
    private Paint mEvalPaint;
    private Rect mEvalRound = new Rect();
    private int mProgress;// 用来做动画的
    private Interpolator mInterpolator = new DecelerateInterpolator();

    private int rRadius;// 背景色圆的半径
    private int rWebStrokeWidth;// 网的宽度
    private int rWebRadius;// 蜘蛛网小圆的半径
    private int rWebCircleEdge;// 蜘蛛网小圆距离边缘的距离
    private int rTextEdgeDistance;// 圆外边的文本距离圆的边缘的距离
    private int rArcHeight;// 弧的高度
    private int rStrokeRadius;// 虚线的半径
    private int rInnerCircleEdgeDistance;// 圆的顶部距离边缘的距离
    private int rInnerCircleDetailDistance;// 内圆下方距离详情文本的距离
    private int rDetailLineSpacing;// 详情文本的行间距
    private Bitmap rInnerBg;

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
    private Paint pCircleStroke;
    private Paint pInnerBg;
    private Paint pInnerTextBig;
    private Paint pInnerTextSmall;

    private Resources res;
    private boolean isRealy;
    private boolean isLayout;
    private boolean isCreated;
    private boolean isSetValue;

    // 临时参数
    private List<float[]> rulerses;
    private List<Paint> rulersPaints;
    private int command = DRAW_DETAIL;

    public SpiderWebViewSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 手动禁止硬件加速，否则阴影画不出来。
        // (不知道为什么，我明明没有开启硬件加速，阴影就是画不出来)
        // (必须要手动关了之后才行)
        // 问题就是，我没开，但是要关
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // setZOrderOnTop(true);
        // getHolder().setFormat(PixelFormat.TRANSLUCENT);
        res = context.getResources();

        rRadius = res.getDimensionPixelSize(R.dimen.spiderWeb_circle_radius);
        rWebRadius = res
                .getDimensionPixelSize(R.dimen.spiderWeb_web_circle_radius);
        rWebCircleEdge = res
                .getDimensionPixelSize(R.dimen.spiderWeb_web_circle_apart_edge);
        rWebStrokeWidth = res
                .getDimensionPixelSize(R.dimen.spiderWeb_web_width);
        rTextEdgeDistance = res
                .getDimensionPixelSize(R.dimen.spiderWeb_edge_text_distance);
        rArcHeight = res
                .getDimensionPixelSize(R.dimen.spiderWeb_value_arc_height);
        rStrokeRadius = res.getDimensionPixelSize(R.dimen.spiderWeb_stroke_radius);
        rInnerCircleDetailDistance = res.getDimensionPixelSize(R.dimen.test_skin_innerCiecle_detail_distance);
        rInnerCircleEdgeDistance = res.getDimensionPixelSize(R.dimen.test_skin_innerCircle_edge_distance);
        rDetailLineSpacing = res.getDimensionPixelSize(R.dimen.test_skin_detail_line_spacing);
        rInnerBg = ((BitmapDrawable) res.getDrawable(R.drawable.bg_skin_type_result)).getBitmap();

        // getHolder().addCallback(this);

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
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
                        isCreated = true;
                        if (isCreated) {
                            isRealy = true;
                            realy();
                        }
                        l("layout finish, w is " + mViewWidth + ", h is "
                                + mViewHeight + ", r is " + rRadius);
                        if (Build.VERSION.SDK_INT < 16) {
                            getViewTreeObserver().removeGlobalOnLayoutListener(
                                    this);
                        } else {
                            getViewTreeObserver().removeOnGlobalLayoutListener(
                                    this);
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
            mTypes[i].setValue(ran.nextInt(100) + "");
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
        pWebCircle.setColor(res.getColor(R.color.spiderWeb_web_circle_color));

        pTypeText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pTypeText.setColor(res.getColor(R.color.spiderWeb_type_text_color));
        pTypeText.setTextSize(res
                .getDimensionPixelSize(R.dimen.spiderWeb_type_text_size));
        pTypeText.setTextAlign(Paint.Align.CENTER);

        pValueText = new Paint(Paint.ANTI_ALIAS_FLAG);
        pValueText.setTextAlign(Paint.Align.CENTER);
        pValueText.setTextSize(res
                .getDimensionPixelSize(R.dimen.spiderWeb_value_text_size));

        pCircleStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        pCircleStroke.setStyle(Style.STROKE);
        pCircleStroke.setColor(res.getColor(R.color.spiderWeb_circle_stroke_color));
        pCircleStroke.setStrokeWidth(res.getDimensionPixelSize(R.dimen.spiderWeb_stroke_width));
        pCircleStroke.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 1));

        pInnerBg = new Paint();

        pInnerTextBig = new Paint(Paint.ANTI_ALIAS_FLAG);
        pInnerTextBig.setTextAlign(Align.CENTER);
        pInnerTextBig.setColor(res.getColor(R.color.spiderWebSimpl_innerCircle_text_color));
        pInnerTextBig.setTextSize(res.getDimensionPixelSize(R.dimen.test_skin_innerCircle_text_size));

        pInnerTextSmall = new Paint(Paint.ANTI_ALIAS_FLAG);
        pInnerTextSmall.setTextAlign(Align.CENTER);
        pInnerTextSmall.setColor(res.getColor(R.color.spiderWebSimpl_detail_text_color));
        pInnerTextSmall.setTextSize(res.getDimensionPixelSize(R.dimen.test_skin_detail_text_size));
    }

    void realy() {
        l("realy go! isSetValue " + isSetValue);
        invalidate();
        if (isSetValue) {
            setProgress(mProgress);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        clear(canvas);
        switch (command) {
            case DRAW_SCENE:
                drawScene(canvas, true, true);
                break;
            case DRAW_VALUE:
                drawScene(canvas, true, true);
                // drawValue(canvas, rulers, rulersPaint);
                break;
            case DRAW_DETAIL:
                drawScene(canvas, false, false);
                drawSceneDetail(canvas);
                break;

            default:
                break;
        }
    }

    private void drawScene(Canvas canvas, boolean drawText, boolean drawWeb) {
        // 画背景圆，大圆
        canvas.drawCircle(mCenterX, mCenterY, rRadius, pCircle);
        canvas.drawCircle(mCenterX, mCenterY, rStrokeRadius, pCircleStroke);
        // testDraw(canvas);
        drawSceneWeb(canvas, drawWeb);
        if (drawText) {
            drawSceneText(canvas);
        }
    }

    private void drawSceneText(Canvas canvas) {
        int lineCount = mTypes == null ? 0 : mTypes.length;
        float density = getContext().getResources().getDisplayMetrics().density;
        int textDist = (int) (5 * density + 0.5f);
        float ruler = rRadius + rTextEdgeDistance;
        float[] textRulers = new float[lineCount];
        for (int i = 0; i < textRulers.length; i++) {
            textRulers[i] = ruler;
        }
        float degrees = 360f / lineCount;
        PointF[] points = new PointF[textRulers.length];
        rulers2points(mCenterX, mCenterY, textRulers, points);
        for (int i = 0; i < points.length; i++) {
            SpiderWebType type = mTypes[i];
            if (type == null) {
                continue;
            }
            pTypeText.setColor(mTypes[i].getNameColor());
            pValueText.setColor(mTypes[i].getValueColor());

            String text = null;
            Rect typeRound = new Rect();
            text = mTypes[i].getName();
            pTypeText.getTextBounds(text, 0, text.length(), typeRound);

            Rect valueRound = new Rect();
            text = mTypes[i].getValue() + "";
            pValueText.getTextBounds(text, 0, text.length(), valueRound);

            final float valueXOffset = 0;// typeRound.width() / 2
            float nameX = points[i].x;
            float nameY = points[i].y - valueRound.height() - textDist;
            float valueX = points[i].x;
            float valueY = points[i].y;
            float currDegress = degrees * i;

            Paint.Align typeAlign = pTypeText.getTextAlign();
            Paint.Align valueAlign = pValueText.getTextAlign();

            // 360°判断无死角
            // TODO 每个方向的对齐方式都不同，还有BUG
            if (currDegress == 0 || currDegress == 360) {
                // 正上方

            } else if (currDegress == 180) {
                // 正下方
                final int offsetY = -1;
                valueX = points[i].x;
                valueY = points[i].y + valueRound.height() + offsetY;
                nameX = points[i].x;
                nameY = valueY + typeRound.height() + textDist + offsetY;
            } else if (currDegress == 90) {
                // 正右方
                pTypeText.setTextAlign(Align.LEFT);
                pValueText.setTextAlign(Align.LEFT);

                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x + valueXOffset;
                valueY = points[i].y + valueRound.height();
            } else if (currDegress == 270) {
                // 正左方
                pTypeText.setTextAlign(Align.RIGHT);
                pValueText.setTextAlign(Align.RIGHT);
                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x - valueXOffset;
                valueY = points[i].y + valueRound.height();
            } else if (currDegress > 0 && currDegress < 90) {
                // 右上方
                pTypeText.setTextAlign(Align.LEFT);
                pValueText.setTextAlign(Align.LEFT);
                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x + valueXOffset;
                valueY = points[i].y + valueRound.height();
            } else if (currDegress > 90 && currDegress < 180) {
                // 右下方
                pTypeText.setTextAlign(Align.LEFT);
                pValueText.setTextAlign(Align.LEFT);
                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x + valueXOffset;
                valueY = points[i].y + valueRound.height();
            } else if (currDegress > 180 && currDegress < 270) {
                // 左下方
                pTypeText.setTextAlign(Align.RIGHT);
                pValueText.setTextAlign(Align.RIGHT);

                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x + valueXOffset;
                valueY = points[i].y + valueRound.height();
            } else if (currDegress > 270 && currDegress < 360) {
                // 正上方
                pTypeText.setTextAlign(Align.RIGHT);
                pValueText.setTextAlign(Align.RIGHT);

                nameX = points[i].x;
                nameY = points[i].y - textDist;

                valueX = points[i].x - valueXOffset;
                valueY = points[i].y + valueRound.height();
            }

            canvas.drawText(mTypes[i].getName() + "", nameX, nameY, pTypeText);
            canvas.drawText(mTypes[i].getValue() + "", valueX, valueY,
                    pValueText);

            pTypeText.setTextAlign(typeAlign);
            pValueText.setTextAlign(valueAlign);
        }
    }

    private void drawSceneWeb(Canvas canvas, boolean drawWeb) {
        int lineCount = mTypes == null ? 0 : mTypes.length;

        float degrees = 360f / lineCount;
        float rulerLength = mRulerLength;// perimeter / directionSize;

        float innerDegrees = 180f / lineCount;
        canvas.save();
        for (int i = 0; i < lineCount; i++) {
            float webCircleX = (float) mCenterX;
            float webCircleY = (float) mCenterY - rulerLength;// rRadius +
            // rWebCircleEdge
            // + rWebRadius;

            if (drawWeb) {
                // 画虚线 我发现SurfaceView的Canvas和普通控件的Canvas画虚线画出来的效果不同
                // 普通控件在设置过Paint的Effect值后通过drawLine画不出虚线
                // 而SurfaceView可以，所以做个判断
                if ("android.view.Surface.CompatibleCanvas".equals(canvas
                        .getClass().getName())) {
                    canvas.drawLine(mCenterX, mCenterY, webCircleX, webCircleY,
                            pWeb);
                } else {
                    Path path = new Path();
                    path.moveTo(mCenterX, mCenterY);
                    path.lineTo(webCircleX, webCircleY);
                    canvas.drawPath(path, pWeb);
                }
            }
            canvas.drawCircle(webCircleX, webCircleY, rWebRadius, pWebCircle);// 画小白圆

            // if (drawWeb) {
            // canvas.rotate(innerDegrees, webCircleX, webCircleY);// 转过去
            // canvas.drawLine(webCircleX, webCircleY, webCircleX + cosine,
            // webCircleY, pWebEdge);// 画实线
            // canvas.rotate(-innerDegrees, webCircleX, webCircleY);// 再转过来
            // }

            canvas.rotate(degrees, mCenterX, mCenterY);
        }
        canvas.restore();
    }

    private void testDraw(Canvas canvas) {

        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(Color.BLUE);
        paint1.setStyle(Style.STROKE);
        paint1.setStrokeWidth(3);

        float[] rulers1 = new float[mTypes.length];
        Random ran = new Random();
        // int temp1 = ran.nextInt(mRulerLength);
        for (int i = 0; i < rulers1.length; i++) {
            rulers1[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers1, paint1);

        float[] rulers2 = new float[mTypes.length];
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.GREEN);
        paint2.setStyle(Style.STROKE);
        paint2.setStrokeWidth(3);
        for (int i = 0; i < rulers2.length; i++) {
            rulers2[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers2/* new float[]{ 60f, 70f } */, paint2);

        float[] rulers3 = new float[mTypes.length];
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(Color.YELLOW);
        paint3.setStyle(Style.STROKE);
        paint3.setStrokeWidth(3);
        for (int i = 0; i < rulers3.length; i++) {
            rulers3[i] = ran.nextInt(mRulerLength);
        }
        drawValue(canvas, rulers3, paint3);
    }

    @Deprecated
    private void drawValueOld(Canvas canvas, float[] rulers, Paint paint) {
        if (rulers == null) {
            return;
        }
        // TODO drawValue
        int lineCount = rulers.length;
        float degrees = 360f / lineCount;

        float x = mCenterX;
        float y = mCenterY - rulers[0];
        Path p = new Path();
        p.moveTo(x, y);

        // float arcX = mCenterX;
        // float arcY = mCenterY - rulers[0] + rArcHeight;
        for (int i = 0; i < rulers.length; i++) {
            // 算线的宽度，题目是这样的
            // 已知三角形的2条边与1夹角，求另外一条边的宽度
            float rotate = degrees * i;
            float rulerA = rulers[(i)];
            float rulerB = rulers[(i + 1 == rulers.length ? 0 : i + 1)];
            // 这个就是所谓的另外一条边的宽度了
            float rulerC = (float) (Math.sqrt(Math.pow(rulerA, 2)
                    + Math.pow(rulerB, 2) - 2f * rulerA * rulerB
                    * Math.cos(Math.toRadians(degrees))));

            // 现在不是只知道一个角么，这里是算另外一个角的度数的
            float[] edge = new float[]{rulerA, rulerB, rulerC};
            double arccosine = (Math.pow(edge[2], 2) + Math.pow(edge[0], 2) - Math
                    .pow(edge[1], 2)) / (2 * edge[2] * edge[0]);
            arccosine = (Math.toDegrees(Math.acos(arccosine < -1.0 ? -1.0
                    : arccosine > 1.0 ? 1.0 : arccosine)));

            // 因为要使用Path的原因(不能画一条线旋转画布一下)，这里需要算另外一个三角形，题目是这样的
            // 已知直角三角形90°角的对角边宽度，和另外一个角的度数，求其他2条边
            double A = 90f;// 直角
            double B = 90f - (90f + rotate) + arccosine;// 需要旋转下度数再计算
            double C = 90f - B;

            // 获取直角三角形的3条边长
            double rulerA1 = rulerC;
            double v1 = rulerA1 / Math.sin(Math.toRadians(A));
            float rulerB1 = (float) (v1 * Math.sin(Math.toRadians(B)));
            float rulerC1 = (float) (v1 * Math.sin(Math.toRadians(C)));
            // l("勾 " + rulerB1 + ", 股 " + rulerC1 + ", 弦 " + rulerA1 + ", △ A "
            // + A + ", B " + B + ", C " + C);

            x += rulerB1;
            y += rulerC1;
            // 三角三边已出

            // arc: 为了算弧度，现在需要来一个影子，就是小一圈的三角形
            // 只能想到这么笨的方法了
            // float arcHeight = rulerC * 0.1f;// rArcHeight;
            // l("arcHeight " + arcHeight + ", ");
            // float arcRulerA = rulerA - arcHeight;
            // float arcRulerB = rulerB - arcHeight;
            // float arcRulerC = (float) (Math.sqrt(Math.pow(arcRulerA, 2)
            // + Math.pow(arcRulerB, 2) - 2f * arcRulerA * arcRulerB
            // * Math.cos(Math.toRadians(degrees))));
            // float[] arcEdge = new float[] { arcRulerA, arcRulerB, arcRulerC
            // };
            // double arcArccosine = (Math.pow(arcEdge[2], 2)
            // + Math.pow(arcEdge[0], 2) - Math.pow(arcEdge[1], 2))
            // / (2 * arcEdge[2] * arcEdge[0]);
            // arcArccosine = (Math.toDegrees(Math.acos(arcArccosine < -1.0 ?
            // -1.0
            // : arcArccosine > 1.0 ? 1.0 : arcArccosine)));
            // double arcA = 90f;// 直角拉
            // double arcB = 90f - (90f + rotate) + arcArccosine;// 需要旋转下度数再计算
            // double arcC = 90f - arcB;
            // double arcRulerA1 = arcRulerC;
            // double arcV1 = arcRulerA1 / Math.sin(Math.toRadians(arcA));
            // float arcRulerB1 = (float) (arcV1 *
            // Math.sin(Math.toRadians(arcB)));
            // float arcRulerC1 = (float) (arcV1 *
            // Math.sin(Math.toRadians(arcC)));

            // Paint pa = new Paint(Paint.ANTI_ALIAS_FLAG);
            // pa.setStyle(Style.FILL);
            // pa.setColor(Color.BLUE);
            // canvas.drawCircle(x + rulerB1 / 2f, y + rulerC1 / 2f, 6, pa);

            // Paint arcPa = new Paint(pa);
            // arcPa.setColor(Color.GREEN);
            // canvas.drawCircle(arcX + arcRulerB1 / 2f, arcY + arcRulerC1 / 2f,
            // 6, arcPa);

            // p.quadTo(arcX + arcRulerB1 / 2f, arcY + arcRulerC1 / 2f, x, y);
            p.lineTo(x, y);

            // arcX += arcRulerB1;
            // arcY += arcRulerC1;
        }
        p.close();
        float webTop = mCenterY - rulers[0];
        int ys = rulers.length / 2;
        float webDown = mCenterY
                + (rulers.length % 2 == 0 ? rulers[ys] : Math.max(rulers[ys],
                rulers[1 + ys]));

        SpiderWebPaint.wrapPaintLinearGradient(paint, mCenterX, webDown,
                mCenterX, webTop);
        canvas.drawPath(p, paint);
    }

    private void drawValue(Canvas canvas, float[] rulers, Paint paint) {
        if (rulers == null) {
            return;
        }
        Path p = new Path();
        PointF[] ps = new PointF[rulers.length];
        rulers2points(mCenterX, mCenterY, rulers, ps);
        p.moveTo(ps[0].x, ps[0].y);
        for (int i = 1; i < ps.length; i++) {
            p.lineTo(ps[i].x, ps[i].y);
        }
        p.close();
        float webTop = mCenterY - rulers[0];
        int ys = rulers.length / 2;
        float webDown = mCenterY
                + (rulers.length % 2 == 0 ? rulers[ys] : Math.max(rulers[ys],
                rulers[1 + ys]));

        SpiderWebPaint.wrapPaintLinearGradient(paint, mCenterX, webDown,
                mCenterX, webTop);
        canvas.drawPath(p, paint);
    }

    public void drawResultText(Canvas canvas, String tip, Paint tipPaint,
                               String val, Paint valPaint, String evaluation, Paint evalPaint) {
        canvas.translate(mCenterX, mCenterY);
        final float distanceDp = dp2px(getContext(), 5);
        float valX = 0;
        float valY = 0;
        if (val != null) {
            valY = mValRound.height() / 2;
            valX = 0;
            // l("val height " + (round.bottom - round.top) + ", centerY: " +
            // mCenterY);
            canvas.drawText(val, valX, valY, valPaint);
        }
        if (tip != null) {
            float tipX = 0;
            float tipY = -(mValRound.height() / 2 + distanceDp);
            canvas.drawText(tip, tipX, tipY, tipPaint);
        }
        if (evaluation != null) {
            float evaluationX = 0;
            float evaluationY = mValRound.height() / 2 + distanceDp
                    + mEvalRound.height();
            canvas.drawText(evaluation, evaluationX, evaluationY, evalPaint);
        }
        canvas.restore();
    }

    private void drawSceneDetail(Canvas canvas) {

        float density = getContext().getResources().getDisplayMetrics().density;
        int textDist = (int) (2 * density + 0.5f);

        Rect bit = new Rect();
        Rect rect = new Rect();
        int left = mCenterX - rInnerBg.getWidth() / 2;
        int top = mCenterY - rRadius + rInnerCircleEdgeDistance;
        bit.set(left, top, left + rInnerBg.getWidth(), top + rInnerBg.getHeight());
        canvas.drawBitmap(rInnerBg, bit.left, bit.top, pInnerBg);
        l("rInnerBg.getWidth() " + rInnerBg.getWidth() + ", rInnerBg.getHeight() " + rInnerBg.getHeight());
        if(mTopText != null) {
            String[] texts = clipText(mTopText, 4);
            for (int i = 0; i < (texts.length > 2 ? 2 : texts.length); i++) {
                pInnerTextBig.getTextBounds(texts[i], 0, texts[i].length(), rect);
                int offset = textDist / 2;
                if(i == 0) {
                    offset = -offset;
                }
                canvas.drawText(texts[i], bit.centerX(), bit.centerY() + i * rect.height() + offset - (1.5f * density + 0.5f), pInnerTextBig);
            }
        }

        float detailY = bit.centerY() + rInnerBg.getHeight() / 2 + rInnerCircleDetailDistance + pInnerTextSmall.getTextSize();
        for (int i = 0; i < mDetailTexts.length; i++) {
            canvas.drawText(mDetailTexts[i], mCenterX, detailY + (pInnerTextSmall.getTextSize() + rDetailLineSpacing) * i, pInnerTextSmall);
        }
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
            ObjectAnimator anim = ObjectAnimator.ofInt(this, "progress", 100)
                    .setDuration(duration);
            anim.setInterpolator(mInterpolator);
            anim.start();
        }
    }

    private void notifyDrawValues(int progress) {
        if (mValues == null) {
            return;
        }
        l("drawValues size " + mValues.size() + ", progress is " + progress);
        // List<float[]> rulerses = new ArrayList<float[]>(mValues.size());
        // List<Paint> rulerPaints = new ArrayList<Paint>(mStyles.size());
        if (rulerses == null || rulerses.size() == 0) {
            rulerses = new ArrayList<float[]>(mValues.size());
            for (int i = 0; i < mValues.size(); i++) {
                int[] values = mValues.get(i);
                if (values == null) {
                    continue;
                }
                rulerses.add(new float[values.length]);
            }
        }
        if (rulersPaints == null || rulersPaints.size() == 0) {
            rulersPaints = new ArrayList<Paint>(mStyles);
        }
        for (int i = 0; i < mValues.size(); i++) {
            int[] values = mValues.get(i);
            if (values == null) {
                continue;
            }

            float[] rulers = rulerses.get(i);
            values2rulers(values, mMin, mMax, rulers);
            rulers2progressRulers(rulers, progress);
            // rulerses.add(rulers);
            // Paint paint = getIndexElseLast(i, mStyles);
            // if (i < 1 || rulersPaints.get(i - 1) != paint) {
            // rulersPaints.add(paint);
            // }
        }
        // this.rulerses = rulerses;
        // this.rulersPaints = rulersPaints;
        command = DRAW_VALUE;
        invalidate();
    }

    @Deprecated
    private void notifyDrawValuesOld(int progress) {
        if (mValues == null) {
            return;
        }
        l("drawValues size " + mValues.size() + ", progress is " + progress);
        List<float[]> rulerses = new ArrayList<float[]>(mValues.size());
        List<Paint> rulerPaints = new ArrayList<Paint>(mStyles.size());
        for (int i = 0; i < mValues.size(); i++) {
            int[] values = mValues.get(i);
            if (values == null) {
                continue;
            }

            Paint paint = getIndexElseLast(i, mStyles);
            float[] rulers = new float[values.length];
            for (int j = 0; j < values.length; j++) {
                int value = values[j] < mMin ? mMin : values[j] > mMax ? mMax
                        : values[j];
                float ruler = mRulerLength * (value / (float) mMax);
                rulers[j] = progressRulers(progress, ruler);
                l("values[" + j + "] = " + value + ", " + "rulers[" + j
                        + "] = " + ruler + "-" + rulers[j] + ", rulerLength = "
                        + mRulerLength);
            }
            rulerses.add(rulers);
            if (i < 1 || rulerPaints.get(i - 1) != paint) {
                rulerPaints.add(paint);
            }
        }
        this.rulerses = rulerses;
        this.rulersPaints = rulerPaints;
        command = DRAW_VALUE;
        invalidate();
    }

    private String[] clipText(String str, int byteLen) {
        float l = str.getBytes().length / (float) byteLen;
        String[] texts = new String[(int) (l % 10 == 0 ? l : l + 1)];
        int index = 0;
        texts[index] = "";
        for (int i = 0; i < mTopText.length(); i++) {
            if (texts[index].getBytes().length >= byteLen) {
                if (index == texts.length) {
                    break;
                }
                index++;
                texts[index] = "";
            }
            texts[index] += mTopText.charAt(i);
        }
        return texts;
    }

    // 用于动画效果，将尺度按照当前动画的进度重新转换
    private void rulers2progressRulers(float[] rulers, int progress) {
        for (int j = 0; j < rulers.length; j++) {
            rulers[j] = progressRulers(progress, rulers[j]);
        }
    }

    // 将 外部的值转换为控件内部需要的尺度
    private void values2rulers(int[] values, int min, int max, float[] rulers) {
        for (int j = 0; j < values.length; j++) {
            int value = values[j] < min ? min : values[j] > max ? max
                    : values[j];
            float ruler = mRulerLength * (value / (float) max);
            rulers[j] = ruler;
        }
    }

    // 将尺度值自动旋转，算出围绕centerX, centerY坐标的位置
    private void rulers2points(float centerX, float centerY, float[] rulers,
                               PointF[] points) {

        float degrees = 360f / rulers.length;
        float x = centerX;
        float y = centerY - rulers[0];
        for (int i = 0; i < rulers.length; i++) {
            if (points[i] == null) {
                points[i] = new PointF();
            }
            points[i].set(x, y);
            // 算线的宽度，题目是这样的
            // 已知三角形的2条边与1夹角，求另外一条边的宽度
            float rotate = degrees * i;
            float rulerA = rulers[(i)];
            float rulerB = rulers[(i + 1 == rulers.length ? 0 : i + 1)];
            // 这个就是所谓的另外一条边的宽度了
            float rulerC = (float) (Math.sqrt(Math.pow(rulerA, 2)
                    + Math.pow(rulerB, 2) - 2f * rulerA * rulerB
                    * Math.cos(Math.toRadians(degrees))));

            // 现在不是只知道一个角么，这里是算另外一个角的度数的
            float[] edge = new float[]{rulerA, rulerB, rulerC};
            double arccosine = (Math.pow(edge[2], 2) + Math.pow(edge[0], 2) - Math
                    .pow(edge[1], 2)) / (2 * edge[2] * edge[0]);
            arccosine = (Math.toDegrees(Math.acos(arccosine < -1.0 ? -1.0
                    : arccosine > 1.0 ? 1.0 : arccosine)));

            // 因为要使用Path的原因(不能画一条线旋转画布一下)，这里需要算另外一个三角形，题目是这样的
            // 已知直角三角形90°角的对角边宽度，和另外一个角的度数，求其他2条边
            double A = 90f;// 直角
            double B = 90f - (90f + rotate) + arccosine;// 需要旋转下度数再计算
            double C = 90f - B;

            // 获取直角三角形的3条边长
            double rulerA1 = rulerC;
            double v1 = rulerA1 / Math.sin(Math.toRadians(A));
            float rulerB1 = (float) (v1 * Math.sin(Math.toRadians(B)));
            float rulerC1 = (float) (v1 * Math.sin(Math.toRadians(C)));

            x += rulerB1;
            y += rulerC1;
        }
    }

    /**
     * 获取List中的数据，如果list.size() <= index, 将取List的最后一个
     *
     * @param index
     * @param list
     * @param <T>
     * @return
     */
    private <T> T getIndexElseLast(int index, List<T> list) {
        T obj = null;
        if (index >= list.size()) {
            obj = list.get(list.size() - 1);
        } else {
            obj = list.get(index);
        }
        return obj;
    }

    // 转换一下
    private float progressRulers(int progress, float ruler) {
        // for (int i = 0; i < rulers.length; i++) {
        // int value = rulers[j] < mMin ? mMin : values[j] > mMax ? mMax :
        // values[j];
        // * (value / (float) mMax);
        // }
        // 少了下面这个 f ,动画效果就出不来了
        if (ruler <= 0) { // 如果为0的话，会出现图表不显示的问题
            ruler = 0.01f;
        }
        return ruler * (progress / 100f);
    }

    private void clear(Canvas canvas) {
        clear(canvas, new Rect(0, 0, mViewWidth, mViewHeight));
    }

    private void clear(Canvas canvas, Rect rect) {
        Bitmap buffer = Bitmap.createBitmap(rect.right, rect.bottom,
                Bitmap.Config.ARGB_4444);
        buffer.eraseColor(Color.TRANSPARENT);
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

    public String getTopText() {
        return mTopText;
    }

    public void setTopText(String topText) {
        this.mTopText = topText;
    }

    public String[] getDetailTexts() {
        return this.mDetailTexts;
    }

    public void setDetailTexts(String[] detail) {
        this.mDetailTexts = detail;
    }

    public List<int[]> getValues() {
        return mValues;
    }

    /**
     * 值都是0-100之间
     *
     * @param values
     * @param styles 至少需要一个，且可以只有一个
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

    public String getTip() {
        return mTip;
    }

    public void setTip(String tip) {
        this.mTip = tip;
        changeResultTextAndPaint(mTip, mTipPaint, mTipRound);
    }

    public Paint getTipPaint() {
        return mTipPaint;
    }

    public void setTipPaint(Paint tipPaint) {
        this.mTipPaint = tipPaint;
        changeResultTextAndPaint(mTip, mTipPaint, mTipRound);
    }

    public String getVal() {
        return mVal;
    }

    public void setVal(String val) {
        this.mVal = val;
        changeResultTextAndPaint(mVal, mValPaint, mValRound);
    }

    public Paint getValPaint() {
        return mValPaint;
    }

    public void setValPaint(Paint valPaint) {
        this.mValPaint = valPaint;
        changeResultTextAndPaint(mVal, mValPaint, mValRound);
    }

    public String getEval() {
        return mEval;
    }

    public void setEval(String eval) {
        this.mEval = eval;
        changeResultTextAndPaint(mEval, mEvalPaint, mEvalRound);
    }

    public Paint getEvalPaint() {
        return mEvalPaint;
    }

    public void setEvalPaint(Paint evalPaint) {
        this.mEvalPaint = evalPaint;
        changeResultTextAndPaint(mEval, mEvalPaint, mEvalRound);
    }

    public void setResultText(String tip, Paint tipPaint, String val,
                              Paint valPaint, String eval, Paint evalPaint) {
        this.mTip = tip;
        this.mTipPaint = tipPaint;
        changeResultTextAndPaint(mTip, mTipPaint, mTipRound);
        this.mVal = val;
        this.mValPaint = valPaint;
        changeResultTextAndPaint(mVal, mValPaint, mValRound);
        this.mEval = eval;
        this.mEvalPaint = evalPaint;
        changeResultTextAndPaint(mEval, mEvalPaint, mEvalRound);
    }

    private void changeResultTextAndPaint(String text, Paint paint, Rect round) {
        if (round == null) {
            return;
        }
        if (text == null || paint == null) {
            round.set(0, 0, 0, 0);
        } else {
            paint.getTextBounds(text, 0, text.length(), round);
        }
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
        // if (progress < 0) {
        // progress = 0;
        // } else if (progress > 100) {
        // progress = 100;
        // }
        this.mProgress = progress;

        if (!isRealy) {
            isSetValue = true;
            return;
        }
        notifyDrawValues(mProgress);
    }

    private float calcTextHeight(float textSize) {
        return (textSize * 0.85f);
    }

    public int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public float px2dp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }

    /**
     * 设置渐变色
     */
    public static class SpiderWebPaint extends Paint {
        public static Paint wrapPaintLinearGradient(Paint paint, float x1,
                                                    float y1, float x2, float y2) {
            if (paint instanceof SpiderWebPaint) {
                SpiderWebPaint p = (SpiderWebPaint) paint;
                p.setShader(new LinearGradient(x1, y1, x2, y2, p.startColor,
                        p.endColor, Shader.TileMode.CLAMP));
            }
            return paint;
        }

        private int[] colors;
        private int startColor;
        private int endColor;

        public SpiderWebPaint(int flags) {
            super(flags);
        }

        public SpiderWebPaint(SpiderWebPaint paint) {
            super(paint);
            this.colors = paint.colors;
            this.startColor = paint.startColor;
            this.endColor = paint.endColor;
        }

        public int[] getColors() {
            return colors;
        }

        public void setColors(int[] colors) {
            this.colors = colors;
        }

        public int getStartColor() {
            return startColor;
        }

        public void setStartColor(int startColor) {
            this.startColor = startColor;
        }

        public int getEndColor() {
            return endColor;
        }

        public void setEndColor(int endColor) {
            this.endColor = endColor;
        }

        public void setStartAndEndColor(int startColor, int endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }
    }

    public static class SpiderWebType {
        public final static int MIN = 0;
        public final static int MAX = 100;
        private String name;
        private int nameColor;
        private String value;
        private int valueColor;

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getValueColor() {
            return valueColor;
        }

        public void setValueColor(int valueColor) {
            this.valueColor = valueColor;
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

