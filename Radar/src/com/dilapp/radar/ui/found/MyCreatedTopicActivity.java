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
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicReq;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.impl.MyTopicCallBackImpl;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.CollectionUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MyCreatedTopicActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener,
		PullToRefreshBase.OnRefreshListener2<ListView> {
	private Context mContext = null;
	private final int SUCCESS = 111;
	private final int FAILARE = 123;
	private TitleView mTitle;
	private ListView listView = null;
	private RelativeLayout defult_layout;
	private TextView tv_defult;
	private PullToRefreshListView pullListView;
	private MyCreatedTopicAdapter mAdapter = null;
	private MMyTopicResp mMyTopicList = null;// 后台请求下来的所有数据
	private long topicID;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				if (!CollectionUtil.isEmpty(mMyTopicList.getDatas())) {
					mAdapter.setData(mMyTopicList.getDatas());
					mAdapter.notifyDataSetChanged();
				} else {
					defult_layout.setVisibility(View.VISIBLE);
					tv_defult.setText("您还没有创建任何话题");
				}
				break;
			case FAILARE:
				defult_layout.setVisibility(View.VISIBLE);
				tv_defult.setText("您还没有创建任何话题");
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
		// requestMyCreatedTopic();
		getCreateTopic();
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

	private void initView() {
		mContext = this;
		View vg_title = findViewById(TitleView.ID_TITLE);
		pullListView = (PullToRefreshListView) findViewById(R.id.pullListView);
		listView = pullListView.getRefreshableView();
		defult_layout = (RelativeLayout) findViewById(R.id.defult_layout);
		tv_defult = (TextView) findViewById(R.id.tv_defult);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.my_create_topic, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		listView.setOnItemClickListener(this);
		pullListView.setOnRefreshListener(this);
		pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		mAdapter = new MyCreatedTopicAdapter(mContext);
		listView.setAdapter(mAdapter);
		// listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
		// listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

	}

	// 我创建话题的list
	private void getCreateTopic() {
		MyTopicCallBack mDetail = ReqFactory.buildInterface(mContext,
				MyTopicCallBack.class);
		MMyTopicReq request_parm = new MMyTopicReq();
		request_parm.setPageNo(1);
		BaseCall<MMyTopicResp> node = new BaseCall<MMyTopicResp>() {
			@Override
			public void call(MMyTopicResp resp) {
				pullListView.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					mMyTopicList = (MMyTopicResp) resp;
					mHandler.sendEmptyMessage(SUCCESS);
				} else {
					mHandler.sendEmptyMessage(FAILARE);
				}
				Log.i("ActivityMyTopic", "resp:" + resp);
			}
		};
		addCallback(node);
		mDetail.getMyCreateTopicByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	/**
	 * 作废，不用
	 */
	private void requestMyCreatedTopic() {
		Object obj = ReqFactory.buildInterface(mContext, MyTopicCallBack.class);
		MyTopicCallBackImpl mDetail = (MyTopicCallBackImpl) obj;
		MMyTopicReq bean = new MMyTopicReq();
		bean.setPageNo(1);
		BaseCall<MMyTopicResp> node = new BaseCall<MMyTopicResp>() {
			@Override
			public void call(MMyTopicResp resp) {
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						ArrayList<MTopicResp> topicResp = (ArrayList<MTopicResp>) resp
								.getDatas();
						mHandler.sendEmptyMessage(SUCCESS);
					} else {
						mHandler.sendEmptyMessage(FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.getMyCreateTopicByTypeAsync(bean, node, GetPostList.GET_DATA_SERVER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		topicID = mMyTopicList.getDatas().get(position - 1).getTopicId();
		Intent intent = new Intent(mContext, ActivityTopicDetail.class);
		intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, topicID);
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCreateTopic();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCreateTopic();
	}
}
