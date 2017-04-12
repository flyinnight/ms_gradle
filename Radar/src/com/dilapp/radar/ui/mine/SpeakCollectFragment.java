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
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.MyPostList;
import com.dilapp.radar.domain.MyPostList.MyStorePostReq;
import com.dilapp.radar.domain.MyPostList.MyStorePostResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.util.CollectionUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 我的发言---收藏
 * 
 * @author Administrator
 * 
 */
public class SpeakCollectFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
	private ArrayList<MPostResp> alldata = new ArrayList<MPostResp>();
	private final int REQUEST_SUCCESS = 1221;
	private final int REQUEST_FAILURE = 1222;
	private MySpeakAdapter adapter = null;
	private PullToRefreshListView pullListView;
	private ListView listView;
	private RelativeLayout defult_layout;
	private long topicId, postId;
	private TextView tv_defult;
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
					tv_defult.setText("您还没有任何收藏");
				}
				break;
			case REQUEST_FAILURE:
				defult_layout.setVisibility(View.VISIBLE);
				tv_defult.setText("您还没有任何收藏");
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
		getMyCollectList(currPage, true, GetPostList.GET_DATA_LOCAL);
	}

	private void initView() {
		pullListView = findViewById(R.id.pullListView);
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		listView = pullListView.getRefreshableView();
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(this);
		adapter = new MySpeakAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	/**
	 * 我的收藏
	 */
	private void getMyCollectList(int page, final boolean isRefresh, int type) {
		MyPostList mDetail = ReqFactory.buildInterface(getActivity(),
				MyPostList.class);
		MyStorePostReq request_parm = new MyStorePostReq();
		request_parm.setPageNo(page);
		BaseCall<MyStorePostResp> node = new BaseCall<MyStorePostResp>() {
			@Override
			public void call(MyStorePostResp resp) {
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
				} else {
					mHandler.sendEmptyMessage(REQUEST_FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getMyStorePostByTypeAsync(request_parm, node, type);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyCollectList(1, true, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyCollectList(currPage++, false, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		postId = alldata.get(position - 1).getId();
		if (postId != 0) {
			Intent intent1 = new Intent(getActivity(), ActivityPostDetail.class);
			MPostResp item1 = new MPostResp();
			item1.setId(postId);
			intent1.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item1);
			startActivity(intent1);
		}
	}
}
