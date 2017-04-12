package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.i;

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
import com.dilapp.radar.domain.GetPostList.CommentedPostListResp;
import com.dilapp.radar.domain.GetPostList.CommentedPostReq;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 我的发言---评论
 * 
 * @author Administrator
 * 
 */

public class SpeakCommentFragment extends BaseFragment implements
		OnItemClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {

	private ArrayList<MPostResp> postData = new ArrayList<MPostResp>();
	private ArrayList<MPostResp> parentData = new ArrayList<MPostResp>();
	private final int REQUEST_SUCCESS = 1230;
	private final int REQUEST_FAILURE = 1231;
	private MyCommentAdapter adapter = null;
	private PullToRefreshListView pullListView;
	private ListView listView;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private int currPage = 1;
	private int totalPage = -1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				if (postData.size() != 0 && parentData.size() != 0) {
					adapter.setData(parentData, postData);
					adapter.notifyDataSetChanged();
				} else {
					defult_layout.setVisibility(View.VISIBLE);
					tv_defult.setText("您还没有任何评论");
				}
				break;
			case REQUEST_FAILURE:
				defult_layout.setVisibility(View.VISIBLE);
				tv_defult.setText("您还没有任何评论");
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
		getMyCommentList(currPage, true, GetPostList.GET_DATA_LOCAL);
	}

	private void initView() {
		pullListView = findViewById(R.id.pullListView);
		defult_layout = findViewById(R.id.defult_layout);
		tv_defult = findViewById(R.id.tv_defult);
		listView = pullListView.getRefreshableView();
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(this);
		adapter = new MyCommentAdapter(getActivity());
		listView.setAdapter(adapter);
	}

	/**
	 * 我的评论
	 */
	private void getMyCommentList(int page, final boolean isRefresh, int type) {
		GetPostList mDetail = ReqFactory.buildInterface(getActivity(),
				GetPostList.class);
		CommentedPostReq request_parm = new CommentedPostReq();
		request_parm.setPageNo(1);
		BaseCall<CommentedPostListResp> node = new BaseCall<CommentedPostListResp>() {
			@Override
			public void call(CommentedPostListResp resp) {
				pullListView.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMySpeak", "resp:" + resp);

					totalPage = resp.getTotalPage();
					if (totalPage <= resp.getPageNo()) {
						pullListView
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {
						pullListView.setMode(PullToRefreshBase.Mode.BOTH);
					}
					if (isRefresh) {
						postData.clear();
						parentData.clear();
					}
					currPage = resp.getPageNo() + 1;
					postData.addAll(resp.getPostLists());
					parentData.addAll(resp.getParenPostLists());
					mHandler.sendEmptyMessage(REQUEST_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(REQUEST_FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getCommentedPostByTypeAsync(request_parm, node, type);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyCommentList(1, true, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyCommentList(currPage++, false, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent2 = new Intent(getActivity(), ActivityPostDetail.class);
		MPostResp item2 = new MPostResp();
		item2.setId(parentData.get(position - 1).getId());
		intent2.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item2);
		startActivity(intent2);
	}
}
