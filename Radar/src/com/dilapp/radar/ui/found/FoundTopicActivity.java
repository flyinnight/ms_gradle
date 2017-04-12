package com.dilapp.radar.ui.found;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.view.CycleViewPager;
import com.dilapp.radar.view.CycleViewPager.ImageCycleViewListener;
import com.dilapp.radar.view.ViewFactory;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * 话题大全主页面
 * 
 * @author Administrator
 * 
 */
public class FoundTopicActivity extends IndicatorFragmentActivity implements
		OnClickListener {
	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENT_THREE = 2;
	private TitleView mTitle;
	private List<ImageView> views = new ArrayList<ImageView>();
	private List<ADInfo> infos = new ArrayList<ADInfo>();
	private CycleViewPager cycleViewPager;
	private TitlePageIndicator mIndicator;
	private ViewPagerCompat viewPager;
	private String[] imageUrls = {
			"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
			"http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
			"http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
			"http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
			"http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg" };
	private TextView et_search = null;
	private final int SEARCH_REQUEST_CODE = 190;
	private LinearLayout rl_search = null;
	public final int REQUEST_CODE = 133;
	float y;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init_view();
		// et_search.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(FoundTopicActivity.this,
		// SearchTopicActivity.class);
		// startActivityForResult(intent, SEARCH_REQUEST_CODE);
		// overridePendingTransition(R.anim.activity_open, 0);
		// }
		// });
	}

	private void init_view() {
		View vg_title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.all_topic, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		et_search = (TextView) findViewById(R.id.et_search_topic);
		rl_search = (LinearLayout) findViewById(R.id.rl_search);
		et_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				y = et_search.getY();
				TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
						-160);
				animation.setDuration(100);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Intent intent = new Intent(FoundTopicActivity.this,
								SearchTopicActivity.class);
						startActivityForResult(intent, SEARCH_REQUEST_CODE);
						overridePendingTransition(R.anim.animation_search_one,
								R.anim.animation_search_two);
					}
				});
				rl_search.startAnimation(animation);
			}
		});
		configImageLoader();
		initialize();
	}

	@SuppressWarnings("unused")
	private void initialize() {
		cycleViewPager = (CycleViewPager) getFragmentManager()
				.findFragmentById(R.id.fragment_cycle_viewpager_content);

		for (int i = 0; i < imageUrls.length; i++) {
			ADInfo info = new ADInfo();
			info.setUrl(imageUrls[i]);
			info.setContent("图片-->" + i);
			infos.add(info);
		}

		// 将最后一个ImageView添加进来
		views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1)
				.getUrl()));
		for (int i = 0; i < infos.size(); i++) {
			views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
		}
		// 将第一个ImageView添加进来
		views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));

		// 设置循环，在调用setData方法前调用
		cycleViewPager.setCycle(true);

		// 在加载数据前设置是否循环
		cycleViewPager.setData(views, infos, mAdCycleViewListener);
		// 设置轮播
		cycleViewPager.setWheel(true);

		// 设置轮播时间，默认5000ms
		cycleViewPager.setTime(2000);
		// 设置圆点指示图标组居中显示，默认靠右
		cycleViewPager.setIndicatorCenter();
//
//		ViewPagerCompat pager = (ViewPagerCompat) findViewById(R.id.pager);
//		pager.setAdapter(new FoundTabAdapter(getSupportFragmentManager()));
//
//		mIndicator = (TitlePageIndicator) findViewById(R.id.pagerindicator);
//		mIndicator.setViewPager(pager);
//
//		final float density = getResources().getDisplayMetrics().density;

	}

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.all_topic),
				AllTopicFragment.class));
		tabs.add(new TabInfo(FRAGMENT_TWO, getString(R.string.topic_manger),
				TopicMangerFragment.class));
		return FRAGMENT_ONE;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case TitleView.ID_RIGHT:
			break;
		default:
			break;
		}

	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {
			if (cycleViewPager.isCycle()) {
				position = position - 1;
				Toast.makeText(FoundTopicActivity.this,
						"position-->" + info.getContent(), Toast.LENGTH_SHORT)
						.show();
			}

		}

	};

	/**
	 * 配置ImageLoder
	 */
	private void configImageLoader() {
		// 初始化ImageLoader
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_launcher) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == REQUEST_CODE) {
			TranslateAnimation animation = new TranslateAnimation(0, 0, -160, 0);
			animation.setDuration(500);
			animation.setFillAfter(true);
			rl_search.startAnimation(animation);
		}
	}
}
