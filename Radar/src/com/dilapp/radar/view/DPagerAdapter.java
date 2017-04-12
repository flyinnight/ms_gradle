package com.dilapp.radar.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class DPagerAdapter extends PagerAdapter {

	private Context mContext;
	private View[] views;

	public DPagerAdapter(Context context, View[] views) {
		this.mContext = context;
		this.views = views;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views[position]);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views[position]);
		return views[position];
	}
}
