package com.dilapp.radar.ui;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dilapp.radar.domain.BaseCallNode;
import com.dilapp.radar.ui.ActivityHelper.StopStateable;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.widget.WaitingDialog;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class BaseFragment extends Fragment {

    private final static String TAG = BaseFragment.class.getSimpleName();
    private final static boolean LOG = false;

    // 是否缓存View的默认值
    public static boolean DEFAULT_CACHE_VIEW = false;

    private boolean isCacheView = DEFAULT_CACHE_VIEW;

    protected Context mContext;
    protected LayoutInflater mInflater;

    private View mContentView;
    protected WaitingDialog mDialog = null;
    
    private BaseCallbackManager mCallbackManager;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        if (LOG)
            d(TAG, "onAttach " + getClass().getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.mContext = getActivity().getApplicationContext();
        this.mInflater = getThemeWrapperLayoutInflater(LayoutInflater.from(mContext));
        mDialog = new WaitingDialog(getActivity());
        if (LOG)
            d(TAG, "onCreate " + getClass().getSimpleName());
        mCallbackManager = new BaseCallbackManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (LOG)
            d(TAG, "onCreateView " + getClass().getSimpleName());
        if(mContentView == null || !isCacheView) {
        	onCreateView(container, savedInstanceState);
        }
        return getContentView();
    }
    
    protected void onCreateView(ViewGroup container, Bundle savedInstanceState) {
        if (LOG)
            d(TAG, "onCreateView Custom " + getClass().getSimpleName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        if (LOG)
            d(TAG, "onActivityCreated " + getClass().getSimpleName());
    }

    @Override
    public void onStart() {

        // TODO Auto-generated method stub
        super.onStart();
        if (LOG)
            d(TAG, "onStart " + getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengUtils.onPageStart(getClass().getSimpleName());
        if (LOG)
            d(TAG, "onResume " + getClass().getSimpleName());
    }

    protected void setContentView(int resLayout) {
        //使用生成的LayoutInflater创建View
        setContentView(mInflater.inflate(resLayout, null, false));
    }

    protected void setContentView(View view) {
        if (view == null) {
            throw new NullPointerException("view is null");
        }
        if (mContentView == null || !isCacheView) {
            mContentView = view;
        } else {
            if (LOG)
                d(TAG, "Using Cache View " + getClass().getSimpleName());
        }
    }

    protected void clearContentView() {
        mContentView = null;
    }

    protected <T extends View> T findViewById(int id) {
        return (T) mContentView.findViewById(id);
    }

    protected View getContentView() {
        return mContentView;
    }

    public boolean isCacheView() {
        return isCacheView;
    }

    /**
     * 当Fragment切换到后台，是否保存该Fragment状态 请在{@link #onCreateView}方法及
     * {@link #onCreateView}以前的生命周期的方法调用 请勿在其他生命周期调用
     *
     * @param isCacheView
     */
    public void setCacheView(boolean isCacheView) {
        this.isCacheView = isCacheView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (LOG)
            d(TAG, "onSaveInstanceState " + getClass().getSimpleName());
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (LOG)
            d(TAG, "onHiddenChanged " + hidden + " "
                    + getClass().getSimpleName());
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (LOG)
            d(TAG, "onPause " + getClass().getSimpleName());
        // TODO Auto-generated method stub
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        if (LOG)
            d(TAG, "onPause " + getClass().getSimpleName());
        UmengUtils.onPageEnd(getClass().getSimpleName());
        super.onPause();
    }

    @Override
    public void onStop() {
        if (LOG)
            d(TAG, "onStop " + getClass().getSimpleName());
        // TODO Auto-generated method stub
        super.onStop();
    }
    
    public BaseCallNode addCallback(BaseCallNode node){
		mCallbackManager.addCallbace(node);
		return node;
	}

    @Override
    public void onDestroyView() {
        if (LOG)
            d(TAG, "onDestroyView " + getClass().getSimpleName());
        if (!isCacheView) {
            mContentView = null;
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            parent.removeView(mContentView);
            if (LOG)
                d(TAG, "Save Cache View " + getClass().getSimpleName());
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mContentView = null;
        mInflater = null;
        mContext = null;
        if (LOG)
            d(TAG, "onDestroy " + getClass().getSimpleName());
        // TODO Auto-generated method stub
        mCallbackManager.clearCallback();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (LOG)
            d(TAG, "onDetach " + getClass().getSimpleName());
        // TODO Auto-generated method stub
        super.onDetach();
    }

    /**
     * 显示等待对话框
     *
     * @param task 你的异步任务，如果用户点击Back键，可以取消正在执行的异步任务.没有可以为null
     */
    protected void showWaitingDialog(final AsyncTask<?, ?, ?> task) {
        ((BaseFragmentActivity) getActivity()).showWaitingDialog(task);
    }

    /**
     * 显示等待对话框
     *
     * @param contextState 你的状态模式，如果用户点击Back键，并且当前正在执行的State实现了{@link StopStateable},将会调用
     *                     {@link StopStateable#stop()}
     */
    protected void showWaitingDialog(ContextState contextState) {
        ((BaseFragmentActivity) getActivity()).showWaitingDialog(contextState);
    }

    /**
     * 设置等待的文本
     *
     * @param text
     */
    protected void setWaitingText(CharSequence text) {
        ((BaseFragmentActivity) getActivity()).setWaitingText(text);
    }

    /**
     * 关闭等待对话框
     */
    protected void dimessWaitingDialog() {
        ((BaseFragmentActivity) getActivity()).dimessWaitingDialog();
    }

    private LayoutInflater getThemeWrapperLayoutInflater(LayoutInflater inflater) {

        try {
            // 这个问题搞了我2天了，终于找到原因了，Fragment没有使用应用的主题
            // 因为Fragment默认是不能使用应用中的主题，这里需要手动设置主题
            // 这个方法是被系统隐藏了，需要使用反射
            Method method = ContextThemeWrapper.class.getDeclaredMethod("getThemeResId");
            method.setAccessible(true);
            int themeResId = (Integer) method.invoke(getActivity());
            Context ctxWithTheme = new ContextThemeWrapper(
                    getActivity().getApplicationContext(),
                    themeResId);

            //通过生成的Context创建一个LayoutInflater
            return inflater.cloneInContext(ctxWithTheme);
        } catch (Exception e) {
            e.printStackTrace();
            return inflater;
        }

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Fragment parent = getParentFragment();
        if (parent != null) {
            // d("III", "startActivityForResult " + getClass().getSimpleName() + " " + requestCode + " parent");
            parent.startActivityForResult(intent, requestCode);
        } else {
            // d("III", "startActivityForResult " + getClass().getSimpleName() + " " + requestCode);
            super.startActivityForResult(intent, requestCode);
        }
    }
}
