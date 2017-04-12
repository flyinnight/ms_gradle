package com.dilapp.radar.ui.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;

/**
 * 我的话题 （发布，参与）
 * 
 * @author Administrator
 * 
 */
public class MineTopicActivity extends BaseFragmentActivity implements
		OnClickListener {

	private Context context;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mList;
	private MineReleasedFragment releasedFragment;
	private MineParticipatedFragment participatedFragment;
	private ImageView mTabLine;
	private TextView tv_topicList, tv_topicManage;
	private TitleView mTitle;
	private int mScreen1_2;
	private int mCurrentPageIndex;
	private Display display;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_all);
		init_view();
		init_TabLine();
	}

	private void init_TabLine() {
		mTabLine = (ImageView) findViewById(R.id.tab_line);
		display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		mScreen1_2 = dm.widthPixels / 2;
		LayoutParams lp = mTabLine.getLayoutParams();
		lp.width = mScreen1_2;
		mTabLine.setLayoutParams(lp);
	}

	private void init_view() {
		context = this;
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		View vg_title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.mine_my_start_topic, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		tv_topicList = (TextView) findViewById(R.id.tv_topicList);
		tv_topicManage = (TextView) findViewById(R.id.tv_topicManage);
		tv_topicList.setText(R.string.speak_release);
		tv_topicManage.setText(R.string.speak_participate);
		tv_topicList.setOnClickListener(new MyOnClickListener(0));
		tv_topicManage.setOnClickListener(new MyOnClickListener(1));
		mList = new ArrayList<Fragment>();
		releasedFragment = new MineReleasedFragment();
		participatedFragment = new MineParticipatedFragment();

		mList.add(releasedFragment);
		mList.add(participatedFragment);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mList.size();
			}

			@Override
			public Fragment getItem(int index) {
				return mList.get(index);
			}
		};
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCurrentPageIndex = position;
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionPx) {

				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine
						.getLayoutParams();
				if (mCurrentPageIndex == 0 && position == 0) {// 0-->1
					lp.leftMargin = (int) (positionOffset * mScreen1_2 + mCurrentPageIndex
							* mScreen1_2);

				} else if (mCurrentPageIndex == 1 && position == 0) {// 1-->0
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_2 + (positionOffset - 1)
							* mScreen1_2);

				} else if (mCurrentPageIndex == 1 && position == 1) {// 1-->2
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_2 + positionOffset
							* mScreen1_2);
				} else if (mCurrentPageIndex == 2 && position == 1) {// 2-->1
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_2 + (positionOffset - 1)
							* mScreen1_2);
				}
				mTabLine.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		default:
			break;
		}
	}

	private class MyOnClickListener implements OnClickListener {

		private int index;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	}
}
