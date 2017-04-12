package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.d;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.chat.ChatActivity2;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowUser;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetUserRelation;
import com.dilapp.radar.domain.GetUserRelation.RelationList;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.UpdateGetUser;
import com.dilapp.radar.domain.UpdateGetUser.*;
import com.dilapp.radar.domain.UserPostTopicList;
import com.dilapp.radar.domain.UserPostTopicList.UserPostTopicReq;
import com.dilapp.radar.domain.UserPostTopicList.UserTopicResp;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabsPager;
import com.dilapp.radar.ui.comm.FragmentTabsPager.TabsPagerInfo;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.ui.found.MyJoinTopicAdapter;
import com.dilapp.radar.ui.mine.ActivityFocusonFans.FocusFansAdapter;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.ui.topic.PostAdapter;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * 他人主页面
 */
public class ActivityOthers extends BaseFragmentActivity implements
		OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView>,
		OnItemClickListener, FocusOnChangedListener {

	private Context mContext;
	private TitleView mTitle;
	private TextView tv_nickName;
	private TextView tv_sex;
	private TextView tv_level;
	private TextView tv_chat;
	// private TextView tv_focus_num;
	// private TextView tv_fans_num;
	private ImageView iv_head;

	private FragmentTabsPager mFragmentTabsPager;

	private PullToRefreshListView ptr_posts;
	private PullToRefreshListView ptr_topics;
	private PullToRefreshListView ptr_fans;
	private PullToRefreshListView ptr_follow;

	private PostAdapter postAdapter;
	private MyJoinTopicAdapter topicAdapter;
	private FocusFansAdapter fansAdapter;
	private FocusFansAdapter follwAdapter;

	private EmptyView ev_posts;
	private EmptyView ev_topics;
	private EmptyView ev_fans;
	private EmptyView ev_follow;

	private String userId;
	private String emUserId;// 环信聊天传的ID
	private GetUserResp user;// 后台请求下来的所有数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_others);

		mContext = this;
		userId = getIntent()
				.getStringExtra(Constants.EXTRA_USER_OTHERS_USER_ID);
		emUserId = getIntent().getStringExtra("EMUserId");
		user = (GetUserResp) getIntent().getSerializableExtra(
				Constants.EXTRA_USER_OTHERS_USER_CONTENT);
		d("III", "userID " + userId);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_nickName = (TextView) findViewById(R.id.tv_nickname);
		tv_sex = (TextView) findViewById(R.id.tv_gender);
		tv_level = (TextView) findViewById(R.id.tv_level);
		tv_chat = (TextView) findViewById(R.id.tv_chat);
		// tv_focus_num = (TextView) findViewById(R.id.tv_focus_num);
		// tv_fans_num = (TextView) findViewById(R.id.tv_fans_num);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(mContext, title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		if (user != null) {
			setUIFromData(user);
			if (TextUtils.isEmpty(emUserId))
				emUserId = user.getEMUserId();
		}

		tv_chat.setOnClickListener(this);
		ptr_posts = MineViewHelper.getDefaultListView(this);
		ptr_topics = MineViewHelper.getDefaultListView(this);
		ptr_fans = MineViewHelper.getDefaultListView(this);
		ptr_follow = MineViewHelper.getDefaultListView(this);

		ptr_posts.setOnRefreshListener(this);
		ptr_topics.setOnRefreshListener(this);
		ptr_fans.setOnRefreshListener(this);
		ptr_follow.setOnRefreshListener(this);

		ptr_posts.setAdapter(postAdapter = new PostAdapter(this,
				getLayoutInflater()));
		ptr_topics.setAdapter(topicAdapter = new MyJoinTopicAdapter(this));
		ptr_fans.setAdapter(fansAdapter = new FocusFansAdapter(this,
				ActivityFocusonFans.MODE_FANS));
		ptr_follow.setAdapter(follwAdapter = new FocusFansAdapter(this,
				ActivityFocusonFans.MODE_FOCUSON));

		fansAdapter.setFocusOnChangedListener(this);
		follwAdapter.setFocusOnChangedListener(this);

		postAdapter.setItemViewClickable(false);
		ptr_posts.setOnItemClickListener(this);
		ptr_topics.setOnItemClickListener(this);
		ptr_fans.setOnItemClickListener(fansAdapter);
		ptr_follow.setOnItemClickListener(follwAdapter);

		mFragmentTabsPager = (FragmentTabsPager) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_tabs_pager);
		mFragmentTabsPager.setTabsPagerInfos(genTabsPagerInfos());

		getOthersInfo(userId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case R.id.tv_follow: {
			if (user == null) {
				break;
			}
			requestFocus(user, !user.isFollowsUser(), v, (TextView) v);
			break;
		}
		case R.id.tv_chat:
			if (!TextUtils.isEmpty(emUserId)) {
				Intent intent = new Intent(mContext, ChatActivity2.class);
				intent.putExtra("userId", userId);
				intent.putExtra("EMuserID", emUserId);
				intent.putExtra("othersPortrait", user.getPortrait());
				intent.putExtra("userName", user.getName());
				startActivity(intent);
			} else {
				DialogUtils.promptInfoDialog(mContext,
						"该用户没有环信ID,不能私信,请注册新用户使用此功能");
			}
			Log.i("ActivityOthers", "userId:" + userId + "--EMuserID:"
					+ emUserId);
			break;
		default:
			break;
		}
	}

	private void setUIFromData(GetUserResp data) {
		if (data != null) {
			if (!TextUtils.isEmpty(data.getPortrait())) {
				TopicHelper.setImageFromUrl(
						TopicHelper.wrappeImagePath(data.getPortrait()),
						iv_head);
			} else {
				iv_head.setImageResource(R.drawable.img_default_head);
			}
			mTitle.setCenterText(data.getName(), null);
			if (!TextUtils.isEmpty(data.getName()))
				tv_nickName.setText(data.getName());
			else
				tv_nickName.setText(getResources().getString(R.string.unknown));
			tv_sex.setText(Constants.getGenderString(this, data.getGender()));
			tv_level.setText("LV" + data.getLevel());
			((TextView) findViewById(R.id.tv_follow)).setText(data
					.isFollowsUser() ? R.string.detail_followed
					: R.string.detail_follow);
			// tv_focus_num.setText(user.getFollowCount() +
			// "");
			// tv_fans_num
			// .setText(user.getFollowedCount() + "");
		}
	}

	private void requestPosts(int page, String userId, final boolean isRefresh,
			int type) {

		Object tag = ptr_posts.getTag(R.id.tv_tag);
		int totalPage = tag != null ? (Integer) tag : -1;
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			ptr_posts.onRefreshComplete();
			d("III_request", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(mContext, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			ptr_posts.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			return;
		}
		UserPostTopicList mDetail = ReqFactory.buildInterface(mContext,
				UserPostTopicList.class);
		UserPostTopicReq request_parm = new UserPostTopicReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(userId);
		d("III", "page " + page + ", refresh " + isRefresh);
		BaseCall<UserPostTopicList.UserPostResp> node = new BaseCall<UserPostTopicList.UserPostResp>() {
			@Override
			public void call(UserPostTopicList.UserPostResp resp) {
				ptr_posts.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					d("III", "普通贴请求成功 "
							+ (resp.getDatas() != null ? resp.getDatas().size()
									: -1));
					if (isRefresh) {
						postAdapter.getList().clear();
						postAdapter.notifyDataSetChanged();
						// ll_posts.setTag(true);
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {
						ptr_posts
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						// ll_posts.setTag(false);
					} else {
						ptr_posts.setMode(PullToRefreshBase.Mode.BOTH);
					}
					// mListView.setMode(PullToRefreshBase.Mode.DISABLED);
					ptr_posts.setTag(R.id.tv_tag, resp.getTotalPage());
					ptr_posts.setTag(resp.getPageNo() + 1);
					d("III",
							"page " + resp.getPageNo() + ", totalPage "
									+ resp.getTotalPage());
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						postAdapter.getList().addAll(
								wrappePosts(resp.getDatas()));
						postAdapter.notifyDataSetChanged();

						if (ev_posts.getVisibility() != View.GONE) {
							ev_posts.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_posts.getVisibility() != View.VISIBLE) {
							ev_posts.setVisibility(View.VISIBLE);
						}
					}
					// final int size = resp.getDatas() != null ?
					// resp.getDatas().size() : 0;
					// for (int i = 0; i < size; i++) {
					// adapter.addItem(resp.getDatas().get(i));
					// }

				} else {
					Toast.makeText(mContext, "没有拿到数据", Toast.LENGTH_SHORT)
							.show();
					d("III", "请求帖子失败 "
							+ (resp != null ? resp.getMessage() : null));
				}
				// vg_waiting.setVisibility(View.GONE);
			}
		};
		addCallback(node);
		mDetail.getUserCreatPostByTypeAsync(request_parm, node, type);
	}

	private void requestTopics(int page, final String userId,
			final boolean isRefresh, int type) {

		Object tag = ptr_topics.getTag(R.id.tv_tag);
		int totalPage = tag != null ? (Integer) tag : -1;
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			ptr_topics.onRefreshComplete();
			d("III_request", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(mContext, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			ptr_topics.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			return;
		}

		UserPostTopicList mDetail = ReqFactory.buildInterface(mContext,
				UserPostTopicList.class);
		UserPostTopicReq request_parm = new UserPostTopicReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(userId);
		d("III", "page " + page + ", refresh " + isRefresh + ", userId "
				+ userId);
		BaseCall<UserTopicResp> node = new BaseCall<UserTopicResp>() {
			@Override
			public void call(UserTopicResp resp) {
				ptr_topics.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					d("III", userId
							+ " 话题请求成功 "
							+ (resp.getDatas() != null ? resp.getDatas().size()
									: -1));
					if (isRefresh) {
						topicAdapter.getData().clear();
						topicAdapter.notifyDataSetChanged();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {
						ptr_topics
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {
						ptr_topics.setMode(PullToRefreshBase.Mode.BOTH);
					}
					ptr_topics.setTag(R.id.tv_tag, resp.getTotalPage());
					ptr_topics.setTag(resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						topicAdapter.getData().addAll(resp.getDatas());
						topicAdapter.notifyDataSetChanged();

						if (ev_topics.getVisibility() != View.GONE) {
							ev_topics.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_topics.getVisibility() != View.VISIBLE) {
							ev_topics.setVisibility(View.VISIBLE);
						}
					}
					// final int size = resp.getDatas() != null ?
					// resp.getDatas().size() : 0;
					// for (int i = 0; i < size; i++) {
					// adapter.addItem(resp.getDatas().get(i));
					// }

				} else {
					Toast.makeText(mContext, userId + " 没有拿到数据",
							Toast.LENGTH_SHORT).show();
					d("III",
							userId + " 请求话题失败 "
									+ (resp != null ? resp.getMessage() : null));
				}
				// vg_waiting.setVisibility(View.GONE);
			}
		};
		addCallback(node);
		mDetail.getUserCreatTopicByTypeAsync(request_parm, node, type);
	}

	private void requestFanses(int page, String userId,
			final boolean isRefresh, int type) {

		Object tag = ptr_fans.getTag(R.id.tv_tag);
		int totalPage = tag != null ? (Integer) tag : -1;
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			ptr_fans.onRefreshComplete();
			d("III_request", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(mContext, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			ptr_fans.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			return;
		}

		GetUserRelation mDetail = ReqFactory.buildInterface(mContext,
				GetUserRelation.class);
		GetUserRelation.getUserListReq request_parm = new GetUserRelation.getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(userId);
		d("III", "page " + page + ", refresh " + isRefresh + " fans");
		BaseCall<GetUserRelation.getUserRelationResp> node = new BaseCall<GetUserRelation.getUserRelationResp>() {
			@Override
			public void call(GetUserRelation.getUserRelationResp resp) {
				ptr_fans.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					d("III", "粉丝请求成功 "
							+ (resp.getDatas() != null ? resp.getDatas().size()
									: -1) + ", " + resp.getPageNo() + "/"
							+ resp.getTotalPage());
					if (isRefresh) {
						fansAdapter.getList().clear();
						fansAdapter.notifyDataSetChanged();
						// ll_posts.setTag(true);
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {
						ptr_fans.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {
						ptr_fans.setMode(PullToRefreshBase.Mode.BOTH);
					}
					// mListView.setMode(PullToRefreshBase.Mode.DISABLED);
					ptr_fans.setTag(R.id.tv_tag, resp.getTotalPage());
					ptr_fans.setTag(resp.getPageNo() + 1);

					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						fansAdapter.getList().addAll(resp.getDatas());
						fansAdapter.notifyDataSetChanged();

						if (ev_fans.getVisibility() != View.GONE) {
							ev_fans.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_fans.getVisibility() != View.VISIBLE) {
							ev_fans.setVisibility(View.VISIBLE);
						}
					}
					// final int size = resp.getDatas() != null ?
					// resp.getDatas().size() : 0;
					// for (int i = 0; i < size; i++) {
					// adapter.addItem(resp.getDatas().get(i));
					// }

				} else {
					Toast.makeText(mContext, "没有拿到粉丝", Toast.LENGTH_SHORT)
							.show();
					d("III", "请求粉丝失败 "
							+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(node);
		mDetail.getUserFansListByTypeAsync(request_parm, node, type);
	}

	private void requestFocuses(int page, String userId,
			final boolean isRefresh, int type) {
		Object tag = ptr_follow.getTag(R.id.tv_tag);
		int totalPage = tag != null ? (Integer) tag : -1;
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			ptr_follow.onRefreshComplete();
			d("III_request", "关注当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(mContext, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			ptr_follow.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
			return;
		}

		GetUserRelation mDetail = ReqFactory.buildInterface(mContext,
				GetUserRelation.class);
		GetUserRelation.getUserListReq request_parm = new GetUserRelation.getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(userId);
		d("III", "page " + page + ", refresh " + isRefresh + " focus");
		BaseCall<GetUserRelation.getUserRelationResp> node = new BaseCall<GetUserRelation.getUserRelationResp>() {
			@Override
			public void call(GetUserRelation.getUserRelationResp resp) {
				ptr_follow.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					d("III", "关注请求成功 "
							+ (resp.getDatas() != null ? resp.getDatas().size()
									: -1));
					if (isRefresh) {
						follwAdapter.getList().clear();
						follwAdapter.notifyDataSetChanged();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {
						ptr_follow
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {
						ptr_follow.setMode(PullToRefreshBase.Mode.BOTH);
					}
					ptr_follow.setTag(R.id.tv_tag, resp.getTotalPage());
					ptr_follow.setTag(resp.getPageNo() + 1);
					d("III",
							"page " + resp.getPageNo() + ", totalPage "
									+ resp.getTotalPage());
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						follwAdapter.getList().addAll(resp.getDatas());
						follwAdapter.notifyDataSetChanged();

						if (ev_follow.getVisibility() != View.GONE) {
							ev_follow.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_follow.getVisibility() != View.VISIBLE) {
							ev_follow.setVisibility(View.VISIBLE);
						}
					}
					// final int size = resp.getDatas() != null ?
					// resp.getDatas().size() : 0;
					// for (int i = 0; i < size; i++) {
					// adapter.addItem(resp.getDatas().get(i));
					// }

				} else {
					// Toast.makeText(mContext, "没有拿到关注" ,
					// Toast.LENGTH_SHORT).show();
					d("III", "请求关注失败 "
							+ (resp != null ? resp.getMessage() : null));
				}
				// vg_waiting.setVisibility(View.GONE);
			}
		};
		addCallback(node);
		mDetail.getUserFollowsListByTypeAsync(request_parm, node, type);
	}

	/**
	 * 请求关注用户
	 * 
	 * @param data
	 *            数据
	 * @param isFocus
	 *            关注
	 * @param click
	 *            触发该事件的控件
	 * @param text
	 *            关注成功需要改变的控件
	 */
	private void requestFocus(final RelationList data, final boolean isFocus,
			final View click, final TextView text) {
		if (data == null || data.getUserId() == null
				|| "".equals(data.getUserId().trim())) {
			d("III", "没有UserID，不能请求关注。");
			return;
		}
		FollowUser fu = ReqFactory.buildInterface(this, FollowUser.class);
		FollowUser.FollowUserReq req = new FollowUser.FollowUserReq();
		req.setUserId(data.getUserId());
		req.setFollow(isFocus);
		if (click != null) {
			click.setClickable(false);
		}
		d("III_request", "关注 userId " + data.getUserId() + ", follow "
				+ isFocus);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (click != null) {
					click.setClickable(true);
				}
				if (resp != null && resp.isRequestSuccess()) {
					data.setFollowsUser(isFocus);
					if (text != null) {
						text.setText(isFocus ? R.string.detail_followed
								: R.string.detail_follow);
					}
					int index = follwAdapter.getList().indexOf(data);
					if (index >= 0 && index < follwAdapter.getList().size()) {
						follwAdapter.getList().get(index)
								.setFollowsUser(isFocus);
						follwAdapter.notifyDataSetChanged();
					}
					index = fansAdapter.getList().indexOf(data);
					if (index >= 0 && index < fansAdapter.getList().size()) {
						fansAdapter.getList().get(index)
								.setFollowsUser(isFocus);
						fansAdapter.notifyDataSetChanged();
					}

					d("III_data", "关注操作成功 focus " + isFocus + " ");
				} else {
					d("III_data", "关注操作失败 focus " + isFocus + " "
							+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(node);
		fu.followUserAsync(req, node);
	}

	/**
	 * 请求关注用户
	 * 
	 * @param data
	 *            数据
	 * @param isFocus
	 *            关注
	 * @param click
	 *            触发该事件的控件
	 * @param text
	 *            关注成功需要改变的控件
	 */
	private void requestFocus(final GetUserResp data, final boolean isFocus,
			final View click, final TextView text) {
		if (data == null || data.getUserId() == null
				|| "".equals(data.getUserId().trim())) {
			d("III", "没有UserID，不能请求关注。");
			return;
		}
		FollowUser fu = ReqFactory.buildInterface(this, FollowUser.class);
		FollowUser.FollowUserReq req = new FollowUser.FollowUserReq();
		req.setUserId(data.getUserId());
		req.setFollow(isFocus);
		if (click != null) {
			click.setClickable(false);
		}
		d("III_request", "关注 userId " + data.getUserId() + ", follow "
				+ isFocus);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				if (click != null) {
					click.setClickable(true);
				}
				if (resp != null && resp.isRequestSuccess()) {
					data.setFollowsUser(isFocus);
					text.setText(isFocus ? R.string.detail_followed
							: R.string.detail_follow);
					d("III_data", "关注操作成功 focus " + isFocus + " ");
				} else {
					d("III_data", "关注操作失败 focus " + isFocus + " "
							+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(node);
		fu.followUserAsync(req, node);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (ptr_posts == refreshView) {
			requestPosts(1, userId, true, GetPostList.GET_DATA_SERVER);
		} else if (ptr_topics == refreshView) {
			requestTopics(1, userId, true, GetPostList.GET_DATA_SERVER);
		} else if (ptr_fans == refreshView) {
			requestFanses(1, userId, true, GetPostList.GET_DATA_SERVER);
		} else if (ptr_follow == refreshView) {
			requestFocuses(1, userId, true, GetPostList.GET_DATA_SERVER);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		Object tag = refreshView.getTag();
		int page = tag == null ? 1 : Integer.parseInt((String) tag);
		if (ptr_posts == refreshView) {
			requestPosts(page, userId, false, GetPostList.GET_DATA_SERVER);
		} else if (ptr_topics == refreshView) {
			requestTopics(page, userId, false, GetPostList.GET_DATA_SERVER);
		} else if (ptr_fans == refreshView) {
			requestFanses(page, userId, false, GetPostList.GET_DATA_SERVER);
		} else if (ptr_follow == refreshView) {
			requestFocuses(page, userId, false, GetPostList.GET_DATA_SERVER);
		}
	}

	private List<MPostResp> wrappePosts(List<MPostResp> posts) {

		if (posts == null || user == null) {
			return posts;
		}
		for (GetPostList.MPostResp post : posts) {
			post.setUserId(user.getUserId());
			post.setUserName(user.getName());
			post.setUserHeadIcon(user.getPortrait());
			post.setGender(user.getGender());
			post.setLevel(user.getLevel());
			post.setFollowsUser(user.isFollowsUser());
		}
		return posts;
	}

	private List<TabsPagerInfo> genTabsPagerInfos() {
		View p = MineViewHelper.wrapStateView(this, ptr_posts);
		ev_posts = (EmptyView) p.findViewById(R.id.ev_empty);
		ev_posts.setText(R.string.empty_data);

		View t = MineViewHelper.wrapStateView(this, ptr_topics);
		ev_topics = (EmptyView) t.findViewById(R.id.ev_empty);
		ev_topics.setText(R.string.empty_data);

		View fa = MineViewHelper.wrapStateView(this, ptr_fans);
		ev_fans = (EmptyView) fa.findViewById(R.id.ev_empty);
		ev_fans.setText(R.string.empty_data);

		View fo = MineViewHelper.wrapStateView(this, ptr_follow);
		ev_follow = (EmptyView) fo.findViewById(R.id.ev_empty);
		ev_follow.setText(R.string.empty_data);

		List<TabsPagerInfo> list = new ArrayList<TabsPagerInfo>(4);
		list.add(new TabsPagerInfo(0, R.string.others_speak, p));
		list.add(new TabsPagerInfo(0, R.string.found_title, t));
		list.add(new TabsPagerInfo(0, R.string.mine_fans, fa));
		list.add(new TabsPagerInfo(0, R.string.detail_follow, fo));
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		position--;
		d("III", "parent " + parent.getClass().getName() + ", view "
				+ view.getClass().getName());
		if (ptr_posts.getRefreshableView() == parent) {
			MPostResp item = postAdapter.getItem(position);
			Intent intent = new Intent(mContext, ActivityPostDetail.class);
			intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
			startActivity(intent);
		} else if (ptr_topics.getRefreshableView() == parent) {
			MTopicResp item = topicAdapter.getItem(position);
			d("III",
					"topic position " + position + ", "
							+ JsonUtils.toJson(item));
			Intent intent = new Intent(mContext, ActivityTopicDetail.class);
			intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getTopicId());
			startActivity(intent);
		} else if (ptr_fans.getRefreshableView() == parent) {
		} else if (ptr_follow.getRefreshableView() == parent) {
		}
	}

	@Override
	public void onFocusOnChanged(Button button, RelationList data, int position) {
		requestFocus(data, !data.isFollowsUser(), null, null);
	}

	/**
	 * 获取他人信息
	 */
	private void getOthersInfo(final String userId) {
		UpdateGetUser mDetail = ReqFactory.buildInterface(mContext,
				UpdateGetUser.class);
		GetUserReq request_parm = new GetUserReq();
		request_parm.setUserId(userId);
		BaseCall<GetUserResp> node = new BaseCall<GetUserResp>() {
			@Override
			public void call(GetUserResp resp) {
				if (resp != null && resp.isRequestSuccess()) {
					d("ActivityFocusonFans", "resp:" + resp);
					user = resp;

					requestPosts(1, userId, true, GetPostList.GET_DATA_LOCAL);
					requestTopics(1, userId, true, GetPostList.GET_DATA_LOCAL);
					requestFanses(1, userId, true, GetPostList.GET_DATA_SERVER);
					requestFocuses(1, userId, true, GetPostList.GET_DATA_SERVER);
					setUIFromData(resp);
				} else {
					Toast.makeText(getApplicationContext(), "failed",
							Toast.LENGTH_SHORT).show();
					d("III_request",
							"获取用户信息失败msg ->"
									+ (resp != null ? resp.getMessage() : null));
				}
			}
		};
		addCallback(node);
		mDetail.getUserAsync(request_parm, node);
	}

}
