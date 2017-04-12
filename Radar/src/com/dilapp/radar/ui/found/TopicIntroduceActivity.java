package com.dilapp.radar.ui.found;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostReq;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.impl.GetPostListImpl;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * @warnning:此页面有两个路口,"话题列表"和"我创建的话题" 需要判断是从那个页面跳转过来的,然后对右上角的菜单显示或者隐藏
 * @author john
 */
public class TopicIntroduceActivity extends BaseActivity implements
		OnClickListener {
	private TitleView mTitle;
	private ListView lv_topic_list = null;
	private ArrayList<MPostResp> alldata = null;
	private TopicForPostAdapter mAdapter = null;
	private String[] thumbURL = {
			"http://content.52pk.com/files/100623/2230_102437_1_lit.jpg",
			"http://content.52pk.com/files/100623/2230_102437_1_lit.jpg",
			"http://content.52pk.com/files/100623/2230_102437_1_lit.jpg",
			"http://content.52pk.com/files/100623/2230_102437_1_lit.jpg" };
	private final int TOPIC_DETAIL_REQUEST_CODE = 123;
	private MTopicResp mTopicResp;
	private final int SUCCESS = 21;
	private final int FAILURE = 22;
	private ImageView topic_icon;
	private TextView tv_topic;
	private TextView tv_join_count;
	private TextView tv_create_time;
	private TextView tv_content;
	private TextView tv_isjoin;
	private TextView tv_speak;
	private DisplayImageOptions options;

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				mAdapter.notifyDataSetChanged();
				break;
			case FAILURE:
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_introduce);
		View vg_title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, vg_title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		topic_icon = (ImageView) findViewById(R.id.topic_icon);
		tv_topic = (TextView) findViewById(R.id.tv_topic);
		tv_join_count = (TextView) findViewById(R.id.tv_join_count);
		tv_create_time = (TextView) findViewById(R.id.tv_create_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_isjoin = (TextView) findViewById(R.id.tv_isjoin);
		tv_speak = (TextView) findViewById(R.id.tv_speak);

		if (!getIntent().getBooleanExtra("fromlist", false)) {
			mTitle.setRightIcon(R.drawable.edit_post_bg, this);
		}
		initView();
		mTopicResp = (MTopicResp) getIntent().getSerializableExtra("topic");
		if (mTopicResp != null) {
			mTitle.setCenterText(mTopicResp.getTopictitle(), null);
			loadData(mTopicResp);
			requestFortopicTag(mTopicResp.getTopicId());
		}
	}

	private void loadData(MTopicResp resp) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				// 正在加载的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)
				// URL请求失败
				.showImageOnFail(R.drawable.ic_launcher)
				// 图片加载失败
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(5)).build();
		ImageLoader.getInstance().displayImage(resp.getTopicimg()[0],
				topic_icon, options, null);
		String title = resp.getTopictitle();
		if (!StringUtils.isEmpty(title)) {
			tv_topic.setText(title);
		}
		int join_count = resp.getRegen();
		if (!StringUtils.isEmpty(join_count)) {
			tv_join_count.setText(Integer.toString(join_count));
		}
		long create_time = resp.getReleasetime();
		if (!StringUtils.isEmpty(create_time)) {
			tv_join_count.setText(Long.toString(create_time));
		}
		String content = resp.getContent();
		if (!StringUtils.isEmpty(content)) {
			tv_content.setText(content);
		}
		// String isjoin=resp.getReleasetime();
		// if (StringUtils.isEmpty(create_time)) {
		// tv_join_count.setText(create_time);
		// }
		// TODO:发言总数 好像现在没有这个数据
		int speak = resp.getRegen();
		if (!StringUtils.isEmpty(speak)) {
			tv_speak.setText(Integer.toString(speak));
		}
	}

	private void initView() {
		lv_topic_list = (ListView) findViewById(R.id.lv_post_list);
		alldata = new ArrayList<MPostResp>();
		mAdapter = new TopicForPostAdapter(TopicIntroduceActivity.this, alldata);
		lv_topic_list.setAdapter(mAdapter);
		lv_topic_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				/*Intent intent = new Intent(TopicIntroduceActivity.this,
						TopicDetailsActivity.class);
				TextView tv_title = (TextView) arg1
						.findViewById(R.id.tv_topic_title);
				intent.putExtra("title", tv_title.getText().toString());
				intent.putExtra("postid", alldata.get(arg2).getId());
				startActivityForResult(intent, TOPIC_DETAIL_REQUEST_CODE);*/
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			this.finish();
			break;
		default:
			break;
		}
	}

	// private void getdata() {
	// for (int i = 0; i < 10; i++) {
	// PostResp mPost = new PostResp();
	// mPost.setUserName("张三");
	// mPost.setGender("女");
	// mPost.setPostLevel("12lv");
	// mPost.setPostTitle("菊花的功效");
	// mPost.setUpdateTime("创建时间:2014/12/12");
	// mPost.setFollowsUpNum("1000");
	// mPost.setReport("300");
	// mPost.setThumbURL(thumbURL);
	// alldata.add(mPost);
	// }
	// }

	/**
	 * 查询某一个话题下面的帖子 上方分组框查询
	 */
	private void requestFortopicTag(long topicid) {
		Object obj = ReqFactory.buildInterface(this, GetPostList.class);
		GetPostListImpl mDetail = (GetPostListImpl) obj;
		MPostReq request_parm = new MPostReq();
		request_parm.setPageNo(1);
		request_parm.setTopicId(topicid);
		BaseCall<TopicPostListResp> node = new BaseCall<TopicPostListResp>() {
			@Override
			public void call(TopicPostListResp resp) {
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						alldata.clear();
						alldata.addAll(resp.getPostLists());
						mhandler.sendEmptyMessage(SUCCESS);
					}
				} else {
					mhandler.sendEmptyMessage(FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getPostsOfOneTopicByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

}
