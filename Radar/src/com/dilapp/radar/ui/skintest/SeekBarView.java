package com.dilapp.radar.ui.skintest;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;

/**
 * Created by husj1 on 2015/4/22.
 */
@SuppressLint("NewApi")
public class SeekBarView {
    private final static String TAG = SeekBarView.class.getSimpleName();
    private final static boolean LOG = false;

    public final static int MIN_VALUE = 0;
    public final static int MAX_VALUE = 100;

    public static void setSeekBarViewStyle(Context context, ViewGroup container, SeekBarViewStyle style) {
        if (style == null) {
            throw new NullPointerException("style is null");
        }
        if (container == null) {
            throw new NullPointerException("container is null");
        }
        int lightTextColor = context.getResources().getColor(style.lightTextColorRes);
        ViewGroup vgCircle = (ViewGroup) container.getChildAt(0);
        ((TextView) vgCircle.getChildAt(0)).setText(style.circleTopTextRes);
        ((TextView) vgCircle.getChildAt(1)).setText(style.circleBottomTextRes);
        vgCircle.setBackgroundResource(style.circleBackgroundRes);

        ViewGroup vgIndicator = (ViewGroup) container.getChildAt(2);
        ((ImageView) vgIndicator.getChildAt(0)).setImageResource(style.indicatorSeekBarRes);
        ViewGroup vgPointerContainer = (ViewGroup) vgIndicator.getChildAt(1);
        ((ImageView) ((ViewGroup) vgPointerContainer.getChildAt(0)).getChildAt(0)).setImageResource(style.indicatorPointerRes);
        ((TextView) ((ViewGroup) vgPointerContainer.getChildAt(1)).getChildAt(1)).setTextColor(lightTextColor);
        ((TextView) vgIndicator.getChildAt(3)).setTextColor(lightTextColor);
        ((TextView) vgIndicator.getChildAt(4)).setTextColor(lightTextColor);

        int val = style.isShowDrivier ? View.VISIBLE : View.INVISIBLE;
        container.findViewById(R.id.view_line1).setVisibility(val);
        container.findViewById(R.id.view_line2).setVisibility(View.GONE);
    }

    public static class SeekBarViewStyle {
        /**
         * 左边圆圈上面的文本资源ID
         */
        public int circleTopTextRes;
        /**
         * 左边圆圈下面的文本资源ID
         */
        public int circleBottomTextRes;
        /**
         * 左边圆圈的背景色资源ID
         */
        public int circleBackgroundRes;

        /**
         * 指示器指针的图片资源ID
         */
        public int indicatorPointerRes;

        /**
         * 指示刻度的图片资源ID
         */
        public int indicatorSeekBarRes;
        /**
         * 高亮文本的颜色资源ID
         */
        public int lightTextColorRes;
        /**
         * 是否显示线条
         */
        public boolean isShowDrivier;

        /**
         * 看清楚，别搞错了
         *
         * @param circleTopTextRes
         * @param circleBottomTextRes
         * @param circleBackgroundRes
         * @param indicatorPointerRes
         * @param indicatorSeekBarRes
         * @param lightTextColorRes
         */
        public SeekBarViewStyle(int circleTopTextRes, int circleBottomTextRes, int circleBackgroundRes, int indicatorPointerRes, int indicatorSeekBarRes, int lightTextColorRes, boolean isShowDrivier) {
            this.circleTopTextRes = circleTopTextRes;
            this.circleBottomTextRes = circleBottomTextRes;
            this.circleBackgroundRes = circleBackgroundRes;
            this.indicatorPointerRes = indicatorPointerRes;
            this.indicatorSeekBarRes = indicatorSeekBarRes;
            this.lightTextColorRes = lightTextColorRes;
            this.isShowDrivier = isShowDrivier;
        }
    }

    private Context mContext;
    private ViewGroup mContainer;

    private int mId;
    private int mMin = MIN_VALUE;
    private int mMax = MAX_VALUE;
    private Interpolator mInterpolator = new DecelerateInterpolator();
    private OnSeekBarChangedListener mListener;

    private boolean mIsLayout;
    private boolean mIsSetValue;
    private boolean mIsLeft;

    private View mIndicator;        // SeekBar的指针，包括它旁边的文本
    private View mSeekBar;            // SeekBar的刻度条
    private View mIndicatorIcon;    // 指针的图形指针
    private View mIndicatorText;    // 图形指针旁边附带的文本

    private int mSeekBarWidth;            // 刻度条的宽度
    private int mIndicatorIconWidth;    // 图形指针的宽度
    private int mIndicatorTextWidth;    // 图形指针旁边文本的宽度
    private int mIndicatorDistanceWidth;// 当指针容器右边不够指针文本显示时的距离
    // private int mTextPadding;

    private int mCurrentValue;
    private boolean mEnableDrag;

    public SeekBarView(Context context, ViewGroup container, int id) {
        this.mContext = context;
        this.mContainer = container;
        this.mId = id;
        this.mIndicator = container.getChildAt(1);
        this.mSeekBar = container.getChildAt(0);
        this.mIndicatorIcon = ((ViewGroup) mIndicator).getChildAt(0);
        this.mIndicatorText = ((ViewGroup) mIndicator).getChildAt(1);

        this.mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 获取对应的数值
                mSeekBarWidth = mSeekBar.getMeasuredWidth();
                mIndicatorIconWidth = mIndicatorIcon.getMeasuredWidth();
                mIndicatorTextWidth = mIndicatorText.getMeasuredWidth();
                mIndicatorDistanceWidth = mIndicatorTextWidth - mIndicatorIconWidth / 2;// + ((ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams()).rightMargin;

                // TransitionManager.go(null);
                // ViewGroup group = ((ViewGroup) mIndicatorText);
                // mTextPadding = group.getChildAt(0).getPaddingLeft();

                // 布局完成，对应的值已准确拿到
                mIsLayout = true;
                // 如果在布局完成之前有设置过进度值的话
                if (mIsSetValue) {
                    setCurrentValue(mCurrentValue);
                }
                if (Build.VERSION.SDK_INT < 16) {
                    mContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    public void enableDrag() {

        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        final int SLOP = configuration.getScaledTouchSlop();
//        mIndicator.setOnTouchListener(new View.OnTouchListener() {
//            float downX;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        downX = event.getX();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float diffX = event.getX() - downX;
//                        if(Math.abs(diffX) > SLOP) {
//                            float diff = mSeekBarWidth / mMax;
//                            int diffVal = (int) (diffX  / diff);
//                            setCurrentValue(diffVal);
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//
//                        break;
//                }
//                return true;
//            }
//        });
        mSeekBar.setOnTouchListener(new View.OnTouchListener(){
            int oldVal;
            float downX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldVal = mCurrentValue;
                        downX = event.getX();
                        float diff = mSeekBarWidth / mMax;
                        int val = (int) (downX / diff);
                        val = val < mMin ? mMin : val > mMax ? mMax : val;
                        setCurrentValue(val);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setCurrentValue(oldVal, 2000);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 获取ID
     *
     * @return id
     */
    public int getId() {
        return this.mId;
    }

    /**
     * 设置ID
     *
     * @param id
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * 最小值, 默认为{@link #MIN_VALUE}
     *
     * @return 最小值
     */
    public int getMin() {
        return this.mMin;
    }

    /**
     * 设置最小值
     *
     * @param min
     */
    public void setMin(int min) {
        this.mMin = min;
    }

    /**
     * 最大值, 默认为{@link #MAX_VALUE}
     *
     * @return 最大值
     */
    public int getMax() {
        return this.mMax;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    /**
     * 当前的值
     *
     * @return
     */
    public int getCurrentValue() {
        return mCurrentValue;
    }

    public void setCurrentValue(int value) {
        // 边界判断
        if (value > mMax) {
            mCurrentValue = mMax;
        } else if (value < mMin) {
            mCurrentValue = mMin;
        } else {
            mCurrentValue = value;
        }
        // 转换成百分比的数值
        float percentage = (float) mCurrentValue / mMax - mMin;
        l("percentage is " + percentage);
        setPositionOfPercentage(percentage);
    }

    /**
     * API 11 以下此方法无效
     *
     * @param value
     * @param duration
     */
    public void setCurrentValue(int value, int duration) {
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }

        Interpolator interpolator = new DecelerateInterpolator();
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentValue", value).setDuration(duration);
        anim.setInterpolator(interpolator);
        anim.start();
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    private synchronized void setPositionOfPercentage(float percentage) {
        // 未布局完成，还不能设置刻度值
        if (!mIsLayout) {
            // 标记一下，在布局完成的时候会自动调用一次
            mIsSetValue = true;
            return;
        }
        //ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams();
        int distance = (int) (mSeekBarWidth * percentage);
        boolean isLeft = mSeekBarWidth - (distance + mIndicatorIconWidth) < mIndicatorDistanceWidth;
        boolean isExchange = false;
        //Scene s = null;
        //margin.leftMargin = distance;
        if (isLeft && !mIsLeft) {
            mIsLeft = true;
            isExchange = true;
            //s = new Scene((ViewGroup) mIndicator, mIndicator);

            // 调整对应的布局
            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIndicatorIcon.getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17)
                iconParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            else iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) mIndicatorText.getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17) textParams.removeRule(RelativeLayout.RIGHT_OF);
            else textParams.addRule(RelativeLayout.RIGHT_OF, 0);
            textParams.addRule(RelativeLayout.LEFT_OF, mIndicatorIcon.getId());
            l("change 1");
        } else if (!isLeft && mIsLeft) {
            mIsLeft = false;
            isExchange = true;
            //s = new Scene((ViewGroup) mIndicator, mIndicator);

            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIndicatorIcon.getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17)
                iconParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            else iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) mIndicatorText.getLayoutParams();
            if (Build.VERSION.SDK_INT >= 17) textParams.removeRule(RelativeLayout.LEFT_OF);
            else textParams.addRule(RelativeLayout.LEFT_OF, 0);
            textParams.addRule(RelativeLayout.RIGHT_OF, mIndicatorIcon.getId());
            l("change 2");
        }
        if (isLeft) {
            ((ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams()).leftMargin = 0;
            ((ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams()).rightMargin = mSeekBarWidth - distance;
        } else {
            ((ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams()).leftMargin = distance;
            ((ViewGroup.MarginLayoutParams) mIndicator.getLayoutParams()).rightMargin = 0;
        }
        if (isExchange) {
            mIndicatorText.setPadding(
                    mIndicatorText.getPaddingRight(),
                    mIndicatorText.getPaddingTop(),
                    mIndicatorText.getPaddingLeft(),
                    mIndicatorText.getPaddingBottom());
            // Scene s2 = new Scene((ViewGroup)mIndicator.getParent(), (ViewGroup) mIndicator);
            // TransitionManager.go(s2);
        }
        mIndicator.requestLayout();
//        Scene s2 = new Scene((ViewGroup)mIndicator, mIndicator);
//        TransitionManager.go(s2);
        //mIndicator.requestLayout();
        l("distance is " + distance +
                ", mSeekBarWidth is " + mSeekBarWidth +
                ", mIndicatorIconWidth is " + mIndicatorIconWidth +
                ", mIndicatorTextWidth is " + mIndicatorTextWidth +
                ", isLeft is " + isLeft +
                ", mIsLeft is " + mIsLeft);
        if (mListener != null) {
            mListener.changed(this, mCurrentValue);
        }
    }

    public void setOnSeekBarChangedListener(OnSeekBarChangedListener listener) {
        this.mListener = listener;
    }

    public interface OnSeekBarChangedListener {
        void changed(SeekBarView seek, int value);
    }

    private static void l(String msg) {
        if (LOG)
            Log.d(TAG, msg);
    }

}
