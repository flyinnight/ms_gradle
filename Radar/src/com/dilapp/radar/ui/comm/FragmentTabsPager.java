package com.dilapp.radar.ui.comm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragment;
import com.viewpagerindicator.UnderlinePageIndicator;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class FragmentTabsPager extends BaseFragment {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private OnPageChangeListener mListener;
	private UnderlinePageIndicator mUnderLineIndicator;

	private List<TabsPagerInfo> mTabsPagerInfos;

	private int mTempItem;

	@Override
	public void onCreateView(/*LayoutInflater inflater, */ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(/*inflater, */container, savedInstanceState);
		setContentView(R.layout.fragment_tabs_pager);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mUnderLineIndicator = findViewById(R.id.lpi_indicator);
		mTabsAdapter = new TabsAdapter(mContext, mTabHost, mViewPager);
		mTabsAdapter.setOnPageChangeListener(mListener);
		mUnderLineIndicator.setViewPager(mViewPager);

		addTabs();

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		mViewPager.setCurrentItem(mTempItem);
		/*return getContentView();*/
	}

	private void addTabs() {
		if (mTabsPagerInfos == null || mTabsPagerInfos.size() == 0) {
			findViewById(R.id.line1).setVisibility(View.GONE);
			findViewById(R.id.line2).setVisibility(View.GONE);
		} else {
			findViewById(R.id.line1).setVisibility(View.VISIBLE);
			findViewById(R.id.line2).setVisibility(View.VISIBLE);
		}
		View[] indicatorViews = getIndicatorViews();
		for (int i = 0; i < indicatorViews.length; i++) {
			View view = mTabsPagerInfos.get(i).contentView;
			if (view == null) {
				view = new View(mContext);
			}
			mTabsAdapter
					.addTab(mTabHost.newTabSpec("" + i).setIndicator(
							indicatorViews[i]), view, null);
		}
	}

	public int getCurrentItem() {
		if (mViewPager != null) {
			return mViewPager.getCurrentItem();
		} else {
			return -1;
		}
	}

	public void setCurrentItem(int item) {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(item);
		} else {
			mTempItem = item;
		}
	}

	public void setOnPageChangeListener(OnPageChangeListener l) {
		mListener = l;
		if (mTabsAdapter != null) {
			mTabsAdapter.setOnPageChangeListener(mListener);
		}
	}

	public void setTabsPagerInfos(List<TabsPagerInfo> tabsPagerRecyclerInfos) {
		this.mTabsPagerInfos = tabsPagerRecyclerInfos;
		if (isInLayout()) {
			addTabs();
		}
	}

    public void setLinesVisibility(int line1, int line2) {
        findViewById(R.id.line1).setVisibility(line1);
        findViewById(R.id.line2).setVisibility(line2);
    }

	public TabHost getTabHost() {
		return mTabHost;
	}

	public ViewPager getViewPager() {
		return mViewPager;
	}

	public UnderlinePageIndicator getUnderLineIndicator() {
		return mUnderLineIndicator;
	}

	private View[] getIndicatorViews() {
		if (mTabsPagerInfos == null) {
			return new View[0];
		}
		View[] indicators = new View[mTabsPagerInfos.size()];
		for (int i = 0; i < indicators.length; i++) {
			TabsPagerInfo info = mTabsPagerInfos.get(i);
			if (info == null) {
				continue;
			}
			indicators[i] = buldIndicatorView(info);
		}
		return indicators;
	}

	private View buldIndicatorView(TabsPagerInfo bean) {
		View view = mInflater.inflate(R.layout.layout_indicator_view, null);
		if (bean.getIconRes() != 0) {
			ImageView icon = (ImageView) view.findViewById(android.R.id.icon1);
			icon.setImageResource(bean.getIconRes());
			icon.setVisibility(View.VISIBLE);
		} else {
			view.findViewById(android.R.id.icon1).setVisibility(View.GONE);
		}
		if (bean.getTextRes() != 0) {
			TextView text = (TextView) view.findViewById(android.R.id.text1);
			text.setText(bean.getTextRes());
			text.setVisibility(View.VISIBLE);
		} else {
			view.findViewById(android.R.id.text1).setVisibility(View.GONE);
		}
		return view;
	}

	public static class TabsPagerInfo {
		private int iconRes;
		private int textRes;
		private View contentView;

		public TabsPagerInfo() {
		}

		public TabsPagerInfo(int iconRes, int textRes, View content) {
			this.iconRes = iconRes;
			this.textRes = textRes;
			this.contentView = content;
		}

		public int getIconRes() {
			return iconRes;
		}

		public void setIconRes(int iconRes) {
			this.iconRes = iconRes;
		}

		public int getTextRes() {
			return textRes;
		}

		public void setTextRes(int textRes) {
			this.textRes = textRes;
		}

		public View getContentView() {
			return contentView;
		}

		public void setContentView(View content) {
			this.contentView = content;
		}

	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends PagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		private OnPageChangeListener mListener;

		static final class TabInfo {

			private final String tag;
			private final View view;
			private final Bundle args;

			TabInfo(String _tag, View _view, Bundle _args) {
				tag = _tag;
				view = _view;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(Context context, TabHost tabHost, ViewPager pager) {

			mContext = context;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, View view, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, view, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mTabs.get(position).view);

		}

		@Override
		public Object instantiateItem(View container, int position) {
			View v = mTabs.get(position).view;
			((ViewPager) container).addView(v);
			return v;
		}

		@Override
		public boolean isViewFromObject(View paramView, Object paramObject) {
			return paramView == paramObject;
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			if (mListener != null) {
				mListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
			if (mListener != null) {
				mListener.onPageSelected(position);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (mListener != null) {
				mListener.onPageScrollStateChanged(state);
			}
		}

		public OnPageChangeListener getOnPageChangeListener() {
			return mListener;
		}

		public void setOnPageChangeListener(OnPageChangeListener listener) {
			this.mListener = listener;
		}

	}
}
