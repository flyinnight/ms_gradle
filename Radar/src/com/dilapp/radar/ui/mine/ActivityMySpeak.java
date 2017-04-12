package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.i;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.CommentedPostListResp;
import com.dilapp.radar.domain.GetPostList.CommentedPostReq;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.MyPostList;
import com.dilapp.radar.domain.MyPostList.MyCreatPostReq;
import com.dilapp.radar.domain.MyPostList.MyCreatPostResp;
import com.dilapp.radar.domain.MyPostList.MyStorePostReq;
import com.dilapp.radar.domain.MyPostList.MyStorePostResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabsPager;
import com.dilapp.radar.ui.comm.FragmentTabsPager.TabsPagerInfo;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ActivityMySpeak extends BaseFragmentActivity implements
		OnClickListener {
	private final int SPEAK_SUCCESS = 0123;
	private final int SPEAK_FAILURE = 0124;
	private final int COLLECT_SUCCESS = 0125;
	private final int COLLECT_FAILURE = 0126;
	private final int COMMENT_SUCCESS = 0127;
	private final int COMMENT_FAILURE = 0121;
	private final int SPEAK_RELEASE = 0141;
	private final int SPEAK_COLLECT = 0142;
	private final int SPEAK_COMMENT = 0143;

	private Context context;
	private TitleView mTitle;
	private FragmentTabsPager mFragmentTabsPager;
	private MySpeakAdapter mAdapter;
	private MyCommentAdapter mCommentAdapter;

	private PullToRefreshListView mReleaseList;// 发布
	private PullToRefreshListView mCollectList;// 收藏
	private PullToRefreshListView mPartakeList;// 参与
	private MyCreatPostResp mPostList = null;// 后台请求下来的所有数据
	private MyStorePostResp mStorePostList = null;// 后台请求下来的所有数据
	private CommentedPostListResp mCommentPostList = null;// 后台请求下来的所有数据

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SPEAK_SUCCESS:
				// mReleaseList.setDividerHeight(20);
				mReleaseList.setAdapter(mAdapter);
				break;
			case SPEAK_FAILURE:
				break;
			case COLLECT_SUCCESS:
				mCollectList.setAdapter(mAdapter);
				break;
			case COLLECT_FAILURE:
				break;
			case COMMENT_SUCCESS:
				mPartakeList.setAdapter(mCommentAdapter);
				break;
			case COMMENT_FAILURE:
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
		mTitle.setCenterText(R.string.speak_title, null);
		mReleaseList = MineViewHelper.getDefaultListView(context);
		mCollectList = MineViewHelper.getDefaultListView(context);
		mPartakeList = MineViewHelper.getDefaultListView(context);

		mReleaseList.setOnItemClickListener(new MySpeakOnClickListener(
				SPEAK_RELEASE));
		mCollectList.setOnItemClickListener(new MySpeakOnClickListener(
				SPEAK_COLLECT));
		mPartakeList.setOnItemClickListener(new MySpeakOnClickListener(
				SPEAK_COMMENT));

		mFragmentTabsPager = new FragmentTabsPager();
		mFragmentTabsPager.setTabsPagerInfos(genTabsPagerInfos());
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, mFragmentTabsPager,
						"fragmentTabsPagerRecycler").commit();

		// mReleaseList.setAdapter(new SpeakTopicMessageAdapter(context,
		// MineViewHelper.genBeans(1)));
		// mCollectList.setAdapter(new SpeakTopicMessageAdapter(context,
		// MineViewHelper.genBeans(5)));
		// mPartakeList.setAdapter(new SpeakTopicMessageAdapter(context,
		// MineViewHelper.genBeans(15)));

		getMySpeakList();
		getMyCollectList();
		getMyCommentList(GetPostList.GET_DATA_LOCAL);
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

	private List<TabsPagerInfo> genTabsPagerInfos() {
		List<TabsPagerInfo> list = new ArrayList<TabsPagerInfo>(3);
		list.add(new TabsPagerInfo(0, R.string.speak_release, mReleaseList));
		list.add(new TabsPagerInfo(0, R.string.speak_collect, mCollectList));
		list.add(new TabsPagerInfo(0, R.string.speak_partake, mPartakeList));
		return list;
	}

	/**
	 * 我的发布
	 */
	private void getMySpeakList() {
		MyPostList mDetail = ReqFactory.buildInterface(context,
				MyPostList.class);
		MyCreatPostReq request_parm = new MyCreatPostReq();
		request_parm.setPageNo(1);
		BaseCall<MyCreatPostResp> node = new BaseCall<MyCreatPostResp>() {
			@Override
			public void call(MyCreatPostResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMySpeak", "resp:" + resp);
					mPostList = (MyCreatPostResp) resp;
					mHandler.sendEmptyMessage(SPEAK_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(SPEAK_FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getMyCreatPostByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	/**
	 * 我的收藏
	 */
	private void getMyCollectList() {
		MyPostList mDetail = ReqFactory.buildInterface(context,
				MyPostList.class);
		MyStorePostReq request_parm = new MyStorePostReq();
		request_parm.setPageNo(1);
		BaseCall<MyStorePostResp> node = new BaseCall<MyStorePostResp>() {
			@Override
			public void call(MyStorePostResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMySpeak", "resp:" + resp);
					mStorePostList = (MyStorePostResp) resp;
					mHandler.sendEmptyMessage(COLLECT_SUCCESS);
				} else {

				}
			}
		};
		addCallback(node);
		mDetail.getMyStorePostByTypeAsync(request_parm, node, GetPostList.GET_DATA_SERVER);
	}

	/**
	 * 我的评论
	 */
	private void getMyCommentList(int type) {
		GetPostList mDetail = ReqFactory.buildInterface(context,
				GetPostList.class);
		CommentedPostReq request_parm = new CommentedPostReq();
		request_parm.setPageNo(1);
		BaseCall<CommentedPostListResp> node = new BaseCall<CommentedPostListResp>() {
			@Override
			public void call(CommentedPostListResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					i("ActivityMySpeak", "resp:" + resp);
					mCommentPostList = (CommentedPostListResp) resp;
					mHandler.sendEmptyMessage(COMMENT_SUCCESS);
				} else {

				}
			}
		};
		addCallback(node);
		mDetail.getCommentedPostByTypeAsync(request_parm, node, type);
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
			case SPEAK_RELEASE:
				i("ActivityMySpeak", "position:" + position + "--"
						+ mPostList.getDatas().get(position).getId());
				Intent intent = new Intent(context, ActivityPostDetail.class);
				MPostResp item = new MPostResp();
				item.setId(mPostList.getDatas().get(position).getId());
				intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
				startActivity(intent);
				break;
			case SPEAK_COLLECT:
				i("ActivityMySpeak", "position:" + position + "=="
						+ mStorePostList.getDatas().get(position).getId());
				Intent intent1 = new Intent(context, ActivityPostDetail.class);
				MPostResp item1 = new MPostResp();
				item1.setId(mStorePostList.getDatas().get(position).getId());
				intent1.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item1);
				startActivity(intent1);
				break;
			case SPEAK_COMMENT:
				i("ActivityMySpeak", "position:"
						+ position
						+ "=="
						+ mCommentPostList.getPostLists().get(position)
								.getPid());
				Intent intent2 = new Intent(context, ActivityPostDetail.class);
				MPostResp item2 = new MPostResp();
				item2.setId(mCommentPostList.getParenPostLists().get(position)
						.getId());
				intent2.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item2);
				startActivity(intent2);
				break;
			default:
				break;
			}
		}
	}
}
