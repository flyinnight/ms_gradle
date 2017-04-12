package com.dilapp.radar.ui.found;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.MyTopicCallBack;
import com.dilapp.radar.domain.MyTopicCallBack.MMyFollowTopicResp;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.CollectionUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MyJoinTopic extends BaseActivity implements OnClickListener,
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {

	private TitleView mTitle;
	private ListView lv_list = null;
	private PullToRefreshListView pullListView;
	private ArrayList<MTopicResp> mTopic = new ArrayList<MTopicResp>();
	private MyJoinTopicAdapter adapter = null;
	private Context mContext = null;
	private final int SUCCESS = 102;
	private final int FAILARE = 103;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private long topicId;
	private int currPage = 1;
	private int totalPage = -1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				if (!CollectionUtil.isEmpty(mTopic)) {
					adapter.setData(mTopic);
					adapter.notifyDataSetChanged();
				} else {
					defult_layout.setVisibility(View.VISIBLE);
					tv_defult.setText("您还没有参与任何话题");
				}
				break;
			case FAILARE:
				defult_layout.setVisibility(View.VISIBLE);
				tv_defult.setText("您还没有参与任何话题");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_join_topic);
		initView();
		requestMyJoinTopic(currPage, true);
	}

	private void initView() {
		mContext = this;
		pullListView = (PullToRefreshListView) findViewById(R.id.pullListView);
		lv_list = pullListView.getRefreshableView();
		View vg_title = findViewById(TitleView.ID_TITLE);
		defult_layout = (RelativeLayout) findViewById(R.id.defult_layout);
		tv_defult = (TextView) findViewById(R.id.tv_defult);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.my_join_topic, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		lv_list.setOnItemClickListener(this);
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		adapter = new MyJoinTopicAdapter(mContext);
		// lv_list.setDivider(new ColorDrawable(Color.TRANSPARENT));
		// lv_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
		lv_list.setAdapter(adapter);
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

	/**
	 * 我参与的话题
	 */
	private void requestMyJoinTopic(int page, final boolean isRefresh) {
		MyTopicCallBack mDetail = ReqFactory.buildInterface(mContext,
				MyTopicCallBack.class);
		MMyTopicReq request_parm = new MMyTopicReq();
		request_parm.setPageNo(page);
		BaseCall<MMyFollowTopicResp> node = new BaseCall<MMyFollowTopicResp>() {
			@Override
			public void call(MMyFollowTopicResp resp) {
				pullListView.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					totalPage = resp.getTotalPage();
					if (totalPage <= resp.getPageNo()) {
						pullListView
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {
						pullListView.setMode(PullToRefreshBase.Mode.BOTH);
					}
					if (isRefresh)
						mTopic.clear();
					currPage = resp.getPageNo() + 1;
					mTopic.addAll(resp.getDatas());
					mHandler.sendEmptyMessage(SUCCESS);
				} else {
					mHandler.sendEmptyMessage(FAILARE);
				}
				pullListView.onRefreshComplete();
			}
		};
		addCallback(node);
		mDetail.getMyFollowTopicByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		topicId = mTopic.get(position - 1).getTopicId();
		Log.i("ActivityMySpeak", "TopicId:" + topicId);
		Intent intent = new Intent(mContext, ActivityTopicDetail.class);
		intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicId);
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestMyJoinTopic(1, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestMyJoinTopic(currPage++, false);
	}
}
