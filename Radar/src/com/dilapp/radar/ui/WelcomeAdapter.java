package com.dilapp.radar.ui;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeAdapter extends PagerAdapter{
	
	 private List<View> mListViews;
	 
	 public WelcomeAdapter(List<View> views){
		 this.mListViews = views;
	 }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return  mListViews.size();//返回页卡的数量
	}
	
	@Override  
    public void destroyItem(ViewGroup container, int position, Object object)   {     
        container.removeView(mListViews.get(position));//删除页卡  
    }
	
	@Override  
    public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡         
         container.addView(mListViews.get(position));//添加页卡  
         return mListViews.get(position);
    }

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;//官方提示这样写
	}

}
