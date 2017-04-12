package com.dilapp.radar.ui.mine;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.util.CollectionUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MineParticipatedFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
	private ArrayList<MTopicResp> alldata = new ArrayList<MTopicResp>();
	private final int REQUEST_SUCCESS = 1421;
	private final int REQUEST_FAILURE = 1422;
	private MyParticipateAdapter adapter = null;
	private PullToRefreshListView pullListView;
	private ListView listView;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private long topicID;
	private int currPage = 1;
	private int totalPage = -1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				if (!CollectionUtil.isEmpty(alldata)) {
					adapter.setData(alldata);
					adapter.notifyDataSetChanged();
				} else {
					defult_layout.setVisibility(View.VISIBLE);
					tv_defult.setText("您还没有参与任何话题");
				}
				break;
			case REQUEST_FAILURE:
				defult_layout.setVisibility(View.VISIBLE);
				tv_defult.setText("您还没有参与任何话题");
				break;
			default:
				break;
			}
		}
	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.all_topic_fragment);
		setCacheView(true);
		initView();
		return getContentView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getParticipateList(currPage, true, GetPostList.GET_DATA_LOCAL);
	}

	private void initView() {
		pullListView = findViewById(R.id.pullListView);
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		listView = pullListView.getRefreshableView();
		alldata = new ArrayList<MTopicResp>();
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(this);
		adapter = new MyParticipateAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	// 我参与话题的list
	private void getParticipateList(int page, final boolean isRefresh, int type) {
		MyTopicCallBack mDetail = ReqFactory.buildInterface(getActivity(),
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
						alldata.clear();
					currPage = resp.getPageNo() + 1;
					alldata.addAll(resp.getDatas());
					mHandler.sendEmptyMessage(REQUEST_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(REQUEST_FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getMyFollowTopicByTypeAsync(request_parm, node, type);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getParticipateList(1, true, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getParticipateList(currPage++, false, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		topicID = alldata.get(position - 1).getTopicId();
		Intent intent1 = new Intent(getActivity(), ActivityTopicDetail.class);
		intent1.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
		startActivity(intent1);
	}
}
