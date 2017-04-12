package com.dilapp.radar.ui.found;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.PostOperation;
import com.dilapp.radar.domain.PostOperation.StoreupPostReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionCollectApply;
import com.dilapp.radar.domain.SolutionCollectApply.SelectedSolutionReq;
import com.dilapp.radar.domain.SolutionCollectApply.SelectedSolutionResp;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.domain.SolutionList;
import com.dilapp.radar.domain.SolutionList.SolutionListReq;
import com.dilapp.radar.domain.SolutionList.SolutionListResp;
import com.dilapp.radar.domain.impl.SolutionListImpl;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.util.CollectionUtil;
import com.dilapp.radar.util.Slog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 我的收藏 以前叫我的护肤计划
 * 
 * @author john
 * 
 */
public class MyNursePlanFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
	private ListView mLv_topic;
	private PullToRefreshListView pullListView;
	private final int QUERY_TAG = 1;
	// private ArrayList<HashMap<String, Object>> mTags;
	private ArrayList<SolutionResp> alldata = new ArrayList<SolutionResp>();
	private UpdateDataReceiver updateDataReceiver;
	private int mPosition = 0;
	private MyNursePlanAdapter mAdapter;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	private final int SUCCESS = 123;
	private final int FAILARE = 122;
	private long topicId, postId;
	private int currPage = 1;
	private int totalPage = -1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				if (!CollectionUtil.isEmpty(alldata)) {
					mAdapter.setData(alldata);
					mAdapter.notifyDataSetChanged();
				} else {
					defult_layout.setVisibility(View.VISIBLE);
					tv_defult.setText("您还没有收藏任何护肤方案");
				}
				break;
			case 1231:
				long postId = (long) msg.arg1;
				// cancelCollect(postId, false);
				// setUseNursePlan(postId, true);
				Slog.i("：--postId--" + postId);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentView(R.layout.my_nurse_plan);
		setCacheView(true);
		initView();
		requestMyCollectSkinPlan(currPage, true);
		register_updateData_broadcast();
		return getContentView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initView() {
		pullListView = findViewById(R.id.pullListView);
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		mLv_topic = pullListView.getRefreshableView();
		mLv_topic.setOnItemClickListener(this);
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		alldata = new ArrayList<SolutionResp>();
		mAdapter = new MyNursePlanAdapter(getActivity(), mHandler);
		mLv_topic.setAdapter(mAdapter);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(updateDataReceiver);
		Log.i("MyNursePlanFragment", "BroadcaseReceiver："
				+ "unregisterBroadcast");
		super.onDestroy();
	}

	/**
	 * 获取我收藏的护肤方案
	 */
	private void requestMyCollectSkinPlan(int page, final boolean isRefresh) {
		Object obj = ReqFactory.buildInterface(getActivity(),
				SolutionList.class);
		SolutionListImpl mDetail = (SolutionListImpl) obj;
		SolutionListReq request_parm = new SolutionListReq();
		request_parm.setPageNo(page);
		BaseCall<SolutionListResp> node = new BaseCall<SolutionListResp>() {
			@Override
			public void call(SolutionListResp resp) {
				pullListView.onRefreshComplete();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						totalPage = resp.getTotalPage();
						if (totalPage <= resp.getPageNo()) {
							pullListView
									.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						} else {
							pullListView.setMode(PullToRefreshBase.Mode.BOTH);
						}
						if (isRefresh && alldata != null) {
							alldata.clear();
						}
						alldata.addAll(resp.getSolutions());
						mHandler.sendEmptyMessage(SUCCESS);
					} else {
						mHandler.sendEmptyMessage(FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.solutionListByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	/**
	 * 取消收藏
	 * 
	 * @param postId
	 * @param isCollect
	 */
	private void cancelCollect(final long postId, final boolean isCollect) {
		PostOperation po = ReqFactory.buildInterface(getActivity(),
				PostOperation.class);
		StoreupPostReq req = new StoreupPostReq();
		req.setPostId(postId);
		req.setStoreUp(isCollect);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					// if (!CollectionUtil.isEmpty(alldata))
					// alldata.clear();
					Slog.i("刷新数据：=====" + alldata.size());
					requestMyCollectSkinPlan(1, true);
				}
			}
		};
		addCallback(node);
		po.storeupPostAsync(req, node);
	}

	/**
	 * 设置使用或者取消为使用的护肤方案
	 * 
	 * @param postId
	 * @param selected
	 */
	private void setUseNursePlan(final long postId, final boolean selected) {
		SolutionCollectApply sca = ReqFactory.buildInterface(getActivity(),
				SolutionCollectApply.class);
		SelectedSolutionReq req = new SelectedSolutionReq();
		req.setPostId(postId);
		req.setSelected(selected);
		BaseCall<SelectedSolutionResp> node = new BaseCall<SelectedSolutionResp>() {
			@Override
			public void call(SelectedSolutionResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					Slog.i("使用成功：" + resp.getMessage());
				}
			}
		};
		addCallback(node);
		sca.selectedSolutionAsync(req, node);
	}

	private void register_updateData_broadcast() {
		updateDataReceiver = new UpdateDataReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.FOUND_TOPIC_COLLECT);
		getActivity().registerReceiver(updateDataReceiver, intentFilter);
		Log.i("MyNursePlanFragment", "BroadcaseReceiver：" + "registerBroadcast");
	}

	public class UpdateDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.FOUND_TOPIC_COLLECT)) {
				requestMyCollectSkinPlan(1, true);
			}
		}
	}

	// /**
	// * 我的护肤方案
	// */
	// private void getNursePlanList(int page, final boolean isRefresh) {
	// SolutionList mDetail = ReqFactory.buildInterface(getActivity(),
	// SolutionList.class);
	// SolutionListReq request_parm = new SolutionListReq();
	// request_parm.setPageNo(1);
	// BaseCall<SolutionListResp> node = new BaseCall<SolutionListResp>() {
	// @Override
	// public void call(SolutionListResp resp) {
	// if (resp != null && resp.isRequestSuccess()) {
	// i("ActivityMySpeak", "resp:" + resp);
	// if (isRefresh && mData.getSolutions() != null)
	// mData.getSolutions().clear();
	// mData = (SolutionListResp) resp;
	// Log.i("MyNursePlanFragment", "mData:" + mData.getMessage()
	// + "--" + mData.getStatus() + "==="
	// + mData.getSolutions().size());
	// mHandler.sendEmptyMessage(SUCCESS);
	// } else {
	//
	// }
	// }
	// };
	// addCallback(node);
	// mDetail.solutionListAsync(request_parm, node);
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!CollectionUtil.isEmpty(alldata)) {
			postId = alldata.get(position - 1).getPostId();
			topicId = alldata.get(position - 1).getTopicId();
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
		requestMyCollectSkinPlan(1, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		requestMyCollectSkinPlan(currPage++, false);
	}

}
