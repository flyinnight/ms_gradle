package com.handmark.pulltorefresh.library.internal;

import com.dilapp.radar.widget.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @author wuwenjie
 * @date 2015/1/8
 * @desc 帧动画加载布局
 */
public class TweenAnimLoadingLayout extends LoadingLayout {

    private AnimationDrawable animationDrawable;

    public TweenAnimLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        // 初始化

        mHeaderImage.setImageResource(R.drawable.img_refresh);
        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
        animationDrawable.stop();
    }

    // 默认图片
    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.img_refresh;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {
        // NO-OP
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
        // NO-OP
    }

    // 下拉以刷新
    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    // 正在刷新时回调
    @Override
    protected void refreshingImpl() {
        // 播放帧动画
        if(!animationDrawable.isRunning())
            animationDrawable.start();
    }

    // 释放以刷新
    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    // 重新设置
    @Override
    protected void resetImpl() {
        mHeaderImage.setVisibility(View.VISIBLE);
        // mHeaderImage.clearAnimation();
        ((AnimationDrawable)mHeaderImage.getDrawable()).stop();
        // animationDrawable.stop();
    }

}
