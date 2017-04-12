package com.dilapp.radar.ui.found;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.domain.SolutionList;
import com.dilapp.radar.domain.SolutionList.SolutionListResp;
import com.dilapp.radar.domain.SolutionList.SolutionRankReq;
import com.dilapp.radar.domain.impl.SolutionListImpl;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.util.CollectionUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 排行榜 以前叫护肤计划 ^ ^
 * 
 * @author john
 * 
 */
public class NursePlanFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
	private ListView mLv_topic;
	private PullToRefreshListView pullListView;
	private final int QUERY_NURSE_TAG = 110;
	private final int FAILARE = 111;
	private NursePlanAdapter mAdapter;
	private ArrayList<SolutionResp> topList = new ArrayList<SolutionResp>();
	private long topicId, postId;
	private int currPage = 1;
	private int totalPage = -1;
	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QUERY_NURSE_TAG:
				mAdapter.setData(topList);
				mAdapter.notifyDataSetChanged();
				break;
			case FAILARE:
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.nurse_skin_plan);
		setCacheView(true);
		initView();
		return getContentView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		requestTop(currPage, true);
	}

	private void initView() {
		topList = new ArrayList<SolutionResp>();
		pullListView = findViewById(R.id.pullListView);
		mLv_topic = pullListView.getRefreshableView();
		mLv_topic.setOnItemClickListener(this);
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mAdapter = new NursePlanAdapter(getActivity());
		mLv_topic.setAdapter(mAdapter);
	}

	/**
	 * 护肤计划排行榜
	 */
	private void requestTop(int pageNumber, final boolean isRefresh) {
		Object obj = ReqFactory.buildInterface(getActivity(),
				SolutionList.class);
		SolutionListImpl mDetail = (SolutionListImpl) obj;
		SolutionRankReq request_parm = new SolutionRankReq();
		request_parm.setPageNo(pageNumber);
		BaseCall<SolutionListResp> node = new BaseCall<SolutionListResp>() {
			@Override
			public void call(SolutionListResp resp) {
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						pullListView.onRefreshComplete();
						if (resp != null && resp.isRequestSuccess()) {
							totalPage = resp.getTotalPage();
							if (totalPage <= resp.getPageNo()) {
								pullListView
										.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
							} else {
								pullListView
										.setMode(PullToRefreshBase.Mode.BOTH);
							}
							if (isRefresh)
								topList.clear();
							currPage = resp.getPageNo() + 1;
							if (!CollectionUtil.isEmpty(resp.getSolutions())) {
								topList.addAll(resp.getSolutions());
								mhandler.sendEmptyMessage(QUERY_NURSE_TAG);
							}
						}
					} else {
						mhandler.sendEmptyMessage(FAILARE);
					}
					pullListView.onRefreshComplete();
				}
			}
		};
		addCallback(node);
		mDetail.solutionListRankByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(!CollectionUtil.isEmpty(topList)){
			topicId = topList.get(position - 1).getTopicId();
			postId = topList.get(position - 1).getPostId();
			Log.i("NursePlanFragment", "topicId:" + topicId + "--" + position);
			Intent intent = new Intent(getActivity(), ActivityPostDetail.class);
			MPostResp item = new MPostResp();
			item.setId(postId);
			item.setTopicId(topicId);
			intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
			startActivity(intent);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestTop(1, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestTop(currPage++, false);
	}
}
