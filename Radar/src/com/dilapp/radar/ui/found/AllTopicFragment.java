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
import com.dilapp.radar.domain.FoundAllTopic;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicReq;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.impl.FoundAllTopicImpl;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AllTopicFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
	private ArrayList<MTopicResp> alldata;
	// private ArrayList<HashMap<String, Object>> alldata;
	private final int REQUEST_SUCCESS = 111;
	private AllTopicAdapter adapter = null;
	private final int TOPIC_INTRODUCE_REQUEST_CODE = 12;
	private PullToRefreshListView pullListView;
	private ListView listView;

	private int currPage = 1;
	private int totalPage = -1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				if (alldata.size() != 0) {
					adapter.setData(alldata);
					adapter.notifyDataSetChanged();
				}
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
		getAllTopic(currPage, true, GetPostList.GET_DATA_SERVER);
	}

	// @Override
	// protected void lazyLoad() {
	// if (!isPrepared || !isVisible) {
	// return;
	// }

	// TODO:此接口待测试
	// Object obj = ReqFactory.buildInterface(getActivity(),
	// FoundAllTopic.class);
	// FoundAllTopicImpl imp = (FoundAllTopicImpl) obj;
	// AllTopicReq req = new AllTopicReq();
	// imp.allTopicAsync(req, new BaseCall<List<TopicResp>>() {
	// @Override
	// public void call(List<TopicResp> resp) {
	// // TODO:将当前的返回的数据绑定到Adapter上
	// alldata = resp;
	// }
	// });
	// }

	private void initView() {
		pullListView = findViewById(R.id.pullListView);
		listView = pullListView.getRefreshableView();
		alldata = new ArrayList<MTopicResp>();
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(this);
		adapter = new AllTopicAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	/**
	 * 获得话题列表
	 */
	private void getAllTopic(int page, final boolean isRefresh, int type) {
		FoundAllTopic mDetail = ReqFactory.buildInterface(getActivity(),
				FoundAllTopic.class);
		AllTopicReq request_parm = new AllTopicReq();
		request_parm.setPageNo(page);
		BaseCall<AllTopicResp> node = new BaseCall<AllTopicResp>() {
			@Override
			public void call(AllTopicResp resp) {
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
						if (isRefresh)
							alldata.clear();
						currPage = resp.getPageNo() + 1;
						alldata.addAll(resp.getTopicResp());
						mHandler.sendEmptyMessage(REQUEST_SUCCESS);
						if (resp.getTopicResp() == null
								|| resp.getTopicResp().size() == 0) {
							Log.i("III_logic", "服务器上没有任何数据");
						}
					} else {
						Log.v("rinfo", "查询失败");
					}
				}
			}
		};
		addCallback(node);
		mDetail.getAllTopicByTypeAsync(request_parm, node, type);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAllTopic(1, true, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getAllTopic(currPage++, false, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), ActivityTopicDetail.class);
		// TODO:这里需要获得帖子ID传递到下个页面,下个页面通过postid请求数据
		intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID,
				alldata.get(position - 1).getTopicId());
		intent.putExtra("fromlist", true);
		startActivityForResult(intent, TOPIC_INTRODUCE_REQUEST_CODE);
	}
}
