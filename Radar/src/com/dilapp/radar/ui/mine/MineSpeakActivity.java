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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;

/**
 * 我的发言 （发布，收藏，评论）
 * 
 * @author Administrator
 * 
 */
public class MineSpeakActivity extends BaseFragmentActivity implements
		OnClickListener {

	private Context context;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mList;
	private ImageView mTabLine;
	private TextView tv_release, tv_collect, tv_comment;
	private TitleView mTitle;
	private int mScreen1_3;
	private int mCurrentPageIndex;
	private Display display;
	private SpeakReleasedFragment releaseFragment;
	private SpeakCollectFragment collectFragment;
	private SpeakCommentFragment commentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_speak);
		init_view();
		init_TabLine();
	}

	private void init_TabLine() {
		mTabLine = (ImageView) findViewById(R.id.tab_line);
		display = getWindow().getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		mScreen1_3 = dm.widthPixels / 3;
		LayoutParams lp = mTabLine.getLayoutParams();
		lp.width = mScreen1_3;
		mTabLine.setLayoutParams(lp);
	}

	private void init_view() {
		context = this;
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		View vg_title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.speak_title, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		tv_release = (TextView) findViewById(R.id.tv_release);
		tv_collect = (TextView) findViewById(R.id.tv_collect);
		tv_comment = (TextView) findViewById(R.id.tv_comment);
		tv_release.setText(R.string.speak_release);
		tv_collect.setText(R.string.speak_collect);
		tv_comment.setText(R.string.speak_partake);
		tv_release.setOnClickListener(new MyOnClickListener(0));
		tv_collect.setOnClickListener(new MyOnClickListener(1));
		tv_comment.setOnClickListener(new MyOnClickListener(2));
		mList = new ArrayList<Fragment>();
		releaseFragment = new SpeakReleasedFragment();
		collectFragment = new SpeakCollectFragment();
		commentFragment = new SpeakCommentFragment();

		mList.add(releaseFragment);
		mList.add(collectFragment);
		mList.add(commentFragment);
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mList.size();
			}

			@Override
			public Fragment getItem(int index) {
				return mList.get(index);
			}

			@Override
			public Fragment instantiateItem(ViewGroup container, int position) {
				Fragment fragment = (Fragment) super.instantiateItem(container,
						position);
				getSupportFragmentManager().beginTransaction().show(fragment).commit();
				return fragment;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// super.destroyItem(container, position, object);
				Fragment fragment = mList.get(position);
				getSupportFragmentManager().beginTransaction().hide(fragment).commit();
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
					lp.leftMargin = (int) (positionOffset * mScreen1_3 + mCurrentPageIndex
							* mScreen1_3);

				} else if (mCurrentPageIndex == 1 && position == 0) {// 1-->0
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1)
							* mScreen1_3);

				} else if (mCurrentPageIndex == 1 && position == 1) {// 1-->2
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + positionOffset
							* mScreen1_3);
				} else if (mCurrentPageIndex == 2 && position == 1) {// 2-->1
					lp.leftMargin = (int) (mCurrentPageIndex * mScreen1_3 + (positionOffset - 1)
							* mScreen1_3);
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
