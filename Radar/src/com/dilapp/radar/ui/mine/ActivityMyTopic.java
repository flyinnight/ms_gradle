package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.i;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.MyTopicCallBack;
import com.dilapp.radar.domain.MyTopicCallBack.MMyFollowTopicResp;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicReq;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabsPager;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.ui.comm.FragmentTabsPager.TabsPagerInfo;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ActivityMyTopic extends BaseFragmentActivity implements
		OnClickListener {
	private final int MY_RELEASE_SUCCESS = 0151;
	private final int MY_RELEASE_FAILURE = 0152;
	private final int MY_PARTICIPATE_SUCCESS = 0153;
	private final int MY_PARTICIPATE_FAILURE = 0154;
	private final int MY_RELEASE = 0155;
	private final int MY_PARTICIPATE = 0156;
	private Context context;
	private TitleView mTitle;
	private FragmentTabsPager mFragmentTabsPager;

	private MyTopicAdapter mAdapter;
	private MyParticipateAdapter mParAdapter;
	private PullToRefreshListView mReleaseList;// 发布
	private PullToRefreshListView mParticipateList;// 参与
	private MMyTopicResp mMyTopicList = null;// 后台请求下来的所有数据
	private MMyFollowTopicResp mMyParticipateList = null;// 后台请求下来的所有数据
	private long topicID;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MY_RELEASE_SUCCESS:
				mReleaseList.setAdapter(mAdapter);
				break;
			case MY_RELEASE_FAILURE:
				break;
			case MY_PARTICIPATE_SUCCESS:
				mParticipateList.setAdapter(mParAdapter);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_focuson_fans);

		init_view();
	}

	private void init_view() {
		context = this;
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(context, title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setCenterText(R.string.topic_title, null);

		mReleaseList = MineViewHelper.getDefaultListView(context);
		mParticipateList = MineViewHelper.getDefaultListView(context);

		mReleaseList.setOnItemClickListener(new MySpeakOnClickListener(
				MY_RELEASE));
		mParticipateList.setOnItemClickListener(new MySpeakOnClickListener(
				MY_PARTICIPATE));
		mFragmentTabsPager = new FragmentTabsPager();
		mFragmentTabsPager.setTabsPagerInfos(genTabsPagerInfos());
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, mFragmentTabsPager,
						"fragmentTabsPagerRecycler").commit();

		getReleaseList();
		getParticipateList();
	}

	// 我发布话题的list
	private void getReleaseList() {
		MyTopicCallBack mDetail = ReqFactory.buildInterface(context,
				MyTopicCallBack.class);
		MMyTopicReq request_parm = new MMyTopicReq();
		request_parm.setPageNo(1);
		BaseCall<MMyTopicResp> node = new BaseCall<MMyTopicResp>() {
			@Override
			public void call(MMyTopicResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMyTopic", "resp:" + resp);
					mMyTopicList = (MMyTopicResp) resp;
					mHandler.sendEmptyMessage(MY_RELEASE_SUCCESS);
				} else {

				}
			}
		};
		addCallback(node);
		mDetail.getMyCreateTopicByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	// 我参与话题的list
	private void getParticipateList() {
		MyTopicCallBack mDetail = ReqFactory.buildInterface(context,
				MyTopicCallBack.class);
		MMyTopicReq request_parm = new MMyTopicReq();
		request_parm.setPageNo(1);
		BaseCall<MMyFollowTopicResp> node = new BaseCall<MMyFollowTopicResp>() {
			@Override
			public void call(MMyFollowTopicResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMyTopic", "resp:" + resp);
					mMyParticipateList = (MMyFollowTopicResp) resp;
					mHandler.sendEmptyMessage(MY_PARTICIPATE_SUCCESS);
				} else {

				}
			}
		};
		addCallback(node);
		mDetail.getMyFollowTopicByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
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

	class MySpeakOnClickListener implements OnItemClickListener {
		private int index;

		public MySpeakOnClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (index) {
			case MY_RELEASE:
				i("ActivityMySpeak", "position:" + position + "--"
						+ mMyTopicList.getDatas().get(position).getTopicId());
				topicID = mMyTopicList.getDatas().get(position).getTopicId();
				Intent intent = new Intent(context, ActivityTopicDetail.class);
				intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
				startActivity(intent);
				break;
			case MY_PARTICIPATE:
				i("ActivityMySpeak", "TopicId:"
						+ mMyParticipateList.getDatas().get(position)
								.getTopicId());
				topicID = mMyParticipateList.getDatas().get(position)
						.getTopicId();
				Intent intent1 = new Intent(context, ActivityTopicDetail.class);
				intent1.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
				startActivity(intent1);
				break;
			default:
				break;
			}
		}
	}

	private List<TabsPagerInfo> genTabsPagerInfos() {
		List<TabsPagerInfo> list = new ArrayList<TabsPagerInfo>();
		list.add(new TabsPagerInfo(0, R.string.speak_release, mReleaseList));
		list.add(new TabsPagerInfo(0, R.string.speak_participate,
				mParticipateList));
		return list;
	}
}
