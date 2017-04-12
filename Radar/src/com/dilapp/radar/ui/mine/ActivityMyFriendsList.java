package com.dilapp.radar.ui.mine;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.chat.ChatActivity2;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetUserRelation;
import com.dilapp.radar.domain.GetUserRelation.RelationList;
import com.dilapp.radar.domain.GetUserRelation.getUserListReq;
import com.dilapp.radar.domain.GetUserRelation.getUserRelationResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.CollectionUtil;
import com.dilapp.radar.util.DialogUtils;

public class ActivityMyFriendsList extends BaseFragmentActivity implements
		OnClickListener, OnItemClickListener {
	private final int REQUEST_SUCCESS = 1420;
	private final int REQUEST_FAILURE = 1421;
	private final int REQUEST_TEMPLIST = 1422;
	private Context mContext;
	private TitleView mTitle;
	private ListView listView;
	private MyFriendsListAdapter adapter;
	private String userId;
	private String emUserId;// 环信聊天传的ID
	private String portraitUrl;
	private String nickName;
	private ArrayList<RelationList> followList = new ArrayList<RelationList>();
	private ArrayList<RelationList> fansList = new ArrayList<RelationList>();
	private ArrayList<RelationList> lists = new ArrayList<RelationList>();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				requestMyFansList(1, GetPostList.GET_DATA_SERVER);
				break;
			case REQUEST_FAILURE:
				break;
			case REQUEST_TEMPLIST:
				for (RelationList list : followList) {
					if (!lists.contains(list)) {
						lists.add(list);
					}
				}
				for (RelationList list : fansList) {
					if (!lists.contains(list)) {
						lists.add(list);
					}
				}
				if (!CollectionUtil.isEmpty(lists)) {
					adapter.setData(lists);
					adapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
		init_view();
	}

	private void init_view() {
		mContext = this;
		View vg_title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, vg_title);
		mTitle.setCenterText(R.string.friends_list, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		requestMyFollowList(1, GetPostList.GET_DATA_SERVER);

		adapter = new MyFriendsListAdapter(mContext);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			setResult(RESULT_OK);
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 我的关注
	 */
	private void requestMyFollowList(int page, int type) {
		GetUserRelation mDetail = ReqFactory.buildInterface(this,
				GetUserRelation.class);
		getUserListReq request_parm = new getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(SharePreCacheHelper.getUserID(this));
		BaseCall<getUserRelationResp> node = new BaseCall<getUserRelationResp>() {
			@Override
			public void call(getUserRelationResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					followList.addAll(resp.getDatas());
					mHandler.sendEmptyMessage(REQUEST_SUCCESS);
				} else {
					
				}
			}
		};
		addCallback(node);
		mDetail.getUserFollowsListByTypeAsync(request_parm, node, type);
	}

	/**
	 * 我的关注
	 */
	private void requestMyFansList(int page, int type) {

		GetUserRelation mDetail = ReqFactory.buildInterface(this,
				GetUserRelation.class);
		getUserListReq request_parm = new getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(SharePreCacheHelper.getUserID(this));
		BaseCall<getUserRelationResp> node = new BaseCall<getUserRelationResp>() {
			@Override
			public void call(getUserRelationResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					fansList.addAll(resp.getDatas());
					mHandler.sendEmptyMessage(REQUEST_TEMPLIST);
				} else {
					mHandler.sendEmptyMessage(REQUEST_FAILURE);
				}
			}
		};
		addCallback(node);
		mDetail.getUserFansListByTypeAsync(request_parm, node, type);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		emUserId = lists.get(position).getEMUserId();
		userId = lists.get(position).getUserId();
		nickName = lists.get(position).getName();
		portraitUrl = lists.get(position).getPortrait();
		if (!TextUtils.isEmpty(emUserId)) {
			Intent intent = new Intent(mContext, ChatActivity2.class);
			intent.putExtra("userId", userId);
			intent.putExtra("EMuserID", emUserId);
			intent.putExtra("othersPortrait", portraitUrl);
			intent.putExtra("userName", nickName);
			startActivity(intent);
		} else {
			DialogUtils
					.promptInfoDialog(mContext, "该用户没有环信ID,不能私信,请注册新用户使用此功能");
		}
		Log.i("ActivityMyFriendsList", "EMUserId:" + emUserId + ",UserId:"
				+ userId);
	}

}
