package com.dilapp.radar.ui.mine;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowUser;
import com.dilapp.radar.domain.FollowUser.FollowUserReq;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetUserRelation;
import com.dilapp.radar.domain.GetUserRelation.RelationList;
import com.dilapp.radar.domain.GetUserRelation.getUserListReq;
import com.dilapp.radar.domain.GetUserRelation.getUserRelationResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateGetUser.GetUserResp;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabsPager;
import com.dilapp.radar.ui.comm.FragmentTabsPager.TabsPagerInfo;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ActivityFocusonFans extends BaseFragmentActivity implements
		OnClickListener, OnRefreshListener2<ListView>, FocusOnChangedListener {
	private static final String TAG = "ActivityFocusonFans";
	public static final int MODE_FOCUSON = 0;
	public static final int MODE_FANS = 1;
	private TitleView mTitle;
	private FragmentTabsPager mFragmentTabsPager;
	private FocusFansAdapter mFocusAdapter;
	private FocusFansAdapter mFansAdapter;
	private PullToRefreshListView mMyFocusList;
	private PullToRefreshListView mMyFansList;
	private EmptyView ev_focus;
	private EmptyView ev_fans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_focuson_fans);

		Context context = getApplicationContext();
		// int slop = ViewConfiguration.get(context).getScaledTouchSlop();
		// Log.i("III", "slop " + slop);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(getApplicationContext(), title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setCenterText(R.string.focusfans_title_focus, null);

		mFocusAdapter = new FocusFansAdapter(this, MODE_FOCUSON);
		mFansAdapter = new FocusFansAdapter(this, MODE_FANS);

		mFocusAdapter.setFocusOnChangedListener(this);
		mFansAdapter.setFocusOnChangedListener(this);

		mMyFocusList = MineViewHelper.getDefaultListView(this);
		mMyFansList = MineViewHelper.getDefaultListView(this);

		mMyFocusList.setOnRefreshListener(this);
		mMyFansList.setOnRefreshListener(this);

		mMyFocusList.setAdapter(mFocusAdapter);
		mMyFansList.setAdapter(mFansAdapter);

		mMyFocusList.setOnItemClickListener(mFocusAdapter);
		mMyFansList.setOnItemClickListener(mFansAdapter);
		// mMyFocusList
		// .setAdapter(new FocusFansAdapter(context, MODE_FOCUSON, 20));
		// mMyFansList.setAdapter(new FocusFansAdapter(context, MODE_FANS, 10));

		mFragmentTabsPager = (FragmentTabsPager) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_tabs_pager);
		mFragmentTabsPager.setTabsPagerInfos(genTabsPagerInfos());

		int page = getIntent().getIntExtra(Constants.EXTRA_FOCUS_FANS_PAGE, 0);
		d("III", "page " + page);
		mFragmentTabsPager.setCurrentItem(page);

		requestMyFollowList(1, true, GetPostList.GET_DATA_SERVER);
		requestMyFansList(1, true, GetPostList.GET_DATA_SERVER);
	}

	private void test() {
		if (!BuildConfig.DEBUG)
			return;
		mTitle.setCenterText(R.string.focusfans_title_focus,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						RecyclerView list = null;
						if (mFragmentTabsPager.getCurrentItem() == 0) {
							// list = mMyFocusList;
						} else {
							// list = mMyFansList;
						}
						java.util.Random r = new java.util.Random();
						int index = r.nextInt(list.getAdapter().getItemCount());
						Toast.makeText(getApplicationContext(), "" + index,
								Toast.LENGTH_SHORT).show();
						LayoutManager layoutManager = list.getLayoutManager();
						layoutManager.scrollToPosition(index);
					}
				});
	}

	/**
	 * 我的关注
	 */
	private void requestMyFollowList(int page, final boolean clear, int type) {

		int totalPage = mMyFocusList.getTag() == null ? -1 : Integer
				.parseInt(mMyFocusList.getTag().toString());
		if (totalPage != -1 && page > totalPage && !clear) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		GetUserRelation mDetail = ReqFactory.buildInterface(this,
				GetUserRelation.class);
		getUserListReq request_parm = new getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(SharePreCacheHelper.getUserID(this));
		d("III_request", "focus__ page " + page + ", total " + totalPage);
		BaseCall<getUserRelationResp> node = new BaseCall<getUserRelationResp>() {
			@Override
			public void call(getUserRelationResp resp) {
				mMyFocusList.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (clear) {
						mFocusAdapter.getList().clear();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						mMyFocusList
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						mMyFocusList.setMode(PullToRefreshBase.Mode.BOTH);
					}
					mMyFocusList.setTag(resp.getTotalPage());
					mMyFocusList.setTag(R.id.tv_tag, resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						mFocusAdapter.getList().addAll(resp.getDatas());
						d("III_request", "查询成功 " + resp.getPageNo() + ", "
								+ resp.getTotalPage() + ", "
								+ resp.getDatas().size());
						if (ev_focus.getVisibility() != View.GONE) {
							ev_focus.setVisibility(View.GONE);
						}
					} else if (clear) {
						if (ev_focus.getVisibility() != View.VISIBLE) {
							ev_focus.setVisibility(View.VISIBLE);
						}
					}
					mFocusAdapter.notifyDataSetChanged();
				} else {
					w("III_request",
							"查询失败 "
									+ (resp != null ? resp.getMessage()
											: "null"));
					Toast.makeText(getApplicationContext(),
							R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		addCallback(node);
		mDetail.getUserFollowsListByTypeAsync(request_parm, node, type);
	}

	/**
	 * 我的关注
	 */
	private void requestMyFansList(int page, final boolean isRefresh, int type) {

		int totalPage = mMyFansList.getTag() == null ? -1 : Integer
				.parseInt(mMyFansList.getTag().toString());
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		GetUserRelation mDetail = ReqFactory.buildInterface(this,
				GetUserRelation.class);
		getUserListReq request_parm = new getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(SharePreCacheHelper.getUserID(this));
		d("III_request", "fans__ page " + page + ", total " + totalPage);
		BaseCall<getUserRelationResp> node = new BaseCall<getUserRelationResp>() {
			@Override
			public void call(getUserRelationResp resp) {
				mMyFansList.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (isRefresh) {
						mFansAdapter.getList().clear();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						mMyFansList
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						mMyFansList.setMode(PullToRefreshBase.Mode.BOTH);
					}
					mMyFansList.setTag(resp.getTotalPage());
					mMyFansList.setTag(R.id.tv_tag, resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						mFansAdapter.getList().addAll(resp.getDatas());
						d("III_request", "查询成功 " + resp.getPageNo() + ", "
								+ resp.getTotalPage() + ", "
								+ resp.getDatas().size());
						if (ev_fans.getVisibility() != View.GONE) {
							ev_fans.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_fans.getVisibility() != View.VISIBLE) {
							ev_fans.setVisibility(View.VISIBLE);
						}
					}
					mFansAdapter.notifyDataSetChanged();
				} else {
					w("III_request",
							"查询失败 "
									+ (resp != null ? resp.getMessage()
											: "null"));
					Toast.makeText(getApplicationContext(),
							R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		addCallback(node);
		mDetail.getUserFansListByTypeAsync(request_parm, node, type);
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
		FollowUserReq req = new FollowUserReq();
		req.setUserId(data.getUserId());
		req.setFollow(isFocus);
		if (click != null) {
			click.setClickable(false);
		}
		d("III_logic", "关注 userId " + data.getUserId() + ", follow " + isFocus);
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

					List<RelationList> list = mFansAdapter.getList();
					int index = list.indexOf(data);
					if (list.contains(data)) {
						if (list.get(index) != data) {
							list.set(index, data);
						}
						mFansAdapter.notifyDataSetChanged();
					}
					data.setFansCount(data.getFansCount() + (isFocus ? 1 : -1));
					if (isFocus) {
						mFocusAdapter.getList().add(data);
					} else {
						mFocusAdapter.getList().remove(data);
						if (mFocusAdapter.getList().size() == 0) {
							ev_focus.setVisibility(View.VISIBLE);
						}
					}
					mFocusAdapter.notifyDataSetChanged();
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("page", mFragmentTabsPager.getCurrentItem());
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

	// @Override
	// public void onPageSelected(int position) {
	// if(position == 0) {
	// mTitle.setCenterText(R.string.focusfans_title_focus, null);
	// } else {
	// mTitle.setCenterText(R.string.focusfans_title_fans, null);
	// }
	// }

	private List<TabsPagerInfo> genTabsPagerInfos() {

		View fo = MineViewHelper.wrapStateView(this, mMyFocusList);
		ev_focus = (EmptyView) fo.findViewById(R.id.ev_empty);
		ev_focus.setText(R.string.empty_data);

		View fa = MineViewHelper.wrapStateView(this, mMyFansList);
		ev_fans = (EmptyView) fa.findViewById(R.id.ev_empty);
		ev_fans.setText(R.string.empty_data);

		List<TabsPagerInfo> infos = new ArrayList<TabsPagerInfo>(2);
		infos.add(new TabsPagerInfo(0, R.string.focusfans_my_focuson, fo));
		infos.add(new TabsPagerInfo(0, R.string.focusfans_my_fans, fa));
		return infos;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView == mMyFocusList) {
			d("III_refresh", " focus");
			requestMyFollowList(1, true, GetPostList.GET_DATA_SERVER);
		} else {
			d("III_refresh", " fans");
			requestMyFansList(1, true, GetPostList.GET_DATA_SERVER);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int page = refreshView.getTag() != null ? Integer.parseInt(refreshView
				.getTag().toString()) : 0;
		if (refreshView == mMyFocusList) {
			d("III_load", " focus");
			requestMyFollowList(page, false, GetPostList.GET_DATA_SERVER);
		} else {
			d("III_load", " fans");
			requestMyFansList(page, false, GetPostList.GET_DATA_SERVER);
		}
	}

	@Override
	public void onFocusOnChanged(Button button, RelationList data, int position) {
		requestFocus(data, !data.isFollowsUser(), button, null);
	}

	public static class FocusFansAdapter extends BaseAdapter implements
			OnClickListener, OnItemClickListener {

		private Activity context;
		private int mode = MODE_FOCUSON;
		private FocusOnChangedListener listener;
		private List<RelationList> list;

		public FocusFansAdapter(Activity context, int mode) {
			this(context, mode, new ArrayList<RelationList>(0));
		}

		public FocusFansAdapter(Activity context, int mode,
				List<RelationList> list) {
			this.context = context;
			this.mode = mode;
			this.list = list;
		}

		public List<RelationList> getList() {
			return list;
		}

		public void setList(List<RelationList> list) {
			this.list = list;
		}

		public void setFocusOnChangedListener(FocusOnChangedListener listener) {
			this.listener = listener;
		}

		@Override
		public int getCount() {
			return list != null ? list.size() : 0;
		}

		@Override
		public RelationList getItem(int position) {
			return list != null && position >= 0 & position < list.size() ? list
					.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// Log.i("III", "onBindViewHolder p " + position);
			final RelationList item = getItem(position);
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_focuson_fans, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (!TextUtils.isEmpty(item.getPortrait())) {
				ImageLoader.getInstance().displayImage(
						TopicHelper.wrappeImagePath(item.getPortrait()),
						holder.iv_head);
			} else {
				holder.iv_head.setImageResource(R.drawable.img_default_head);
			}
			Log.i(TAG, "头像：" + item.getPortrait());
			if (TextUtils.isEmpty(item.getName()))
				holder.tv_nickname.setText(item.getUserId());
			else
				holder.tv_nickname.setText(item.getName());
			holder.tv_sex.setText(Constants.getGenderString(context,
					item.getGender()));
			holder.tv_level.setText(context.getString(R.string.fans_level,
					item.getLevel() + ""));
			holder.tv_focus.setText(context.getString(
					R.string.fans_focus_number, "" + item.getFollowsCount()));
			holder.tv_fans.setText(context.getString(R.string.fans_fans_number,
					item.getFansCount() + ""));

			if (!item.getUserId()
					.equals(SharePreCacheHelper.getUserID(context))) {
				holder.btn_focus.setVisibility(View.VISIBLE);
				holder.btn_focus.setTag(item);
				holder.btn_focus.setTag(R.id.btn_focus, position);
				holder.btn_focus
						.setText(item.isFollowsUser() ? R.string.detail_followed
								: R.string.detail_follow);
				holder.btn_focus.setOnClickListener(this);
			} else {
				holder.btn_focus.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			position--;
			d("III_click", "position " + position);
			RelationList item = getItem(position);
			if (SharePreCacheHelper.getUserID(context).equals(item.getUserId())) {
				return;
			}
			GetUserResp content = new GetUserResp();
			content.setUserId(item.getUserId());
			content.setPortrait(item.getPortrait());
			content.setName(item.getName());
			content.setGender(item.getGender());
			content.setLevel(item.getLevel());
			content.setLevelName(item.getLevelName());
			content.setSkinQuality(item.getSkin());
			content.setFollowsUser(item.isFollowsUser());
			switch (mode) {
			case MODE_FOCUSON: {
				// content.setFollowsUser(true);
				Intent intent = new Intent(context, ActivityOthers.class);
				intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_ID,
						item.getUserId());
				intent.putExtra("EMUserId", item.getEMUserId());
				intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_CONTENT,
						content);
				context.startActivity(intent);
				break;
			}
			case MODE_FANS: {
				Intent intent = new Intent(context, ActivityOthers.class);
				intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_ID,
						item.getUserId());
				intent.putExtra("EMUserId", item.getEMUserId());
				intent.putExtra(Constants.EXTRA_USER_OTHERS_USER_CONTENT,
						content);
				context.startActivity(intent);
				break;
			}
			default:
				break;
			}
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_focus: {
				if (listener != null) {
					Button btn = (Button) v;
					RelationList item = (RelationList) btn.getTag();
					// boolean isChecked = item.isFollowsUser();
					int position = (Integer) btn.getTag(R.id.btn_focus);
					listener.onFocusOnChanged(btn, item, position);
					// btn.setText(isChecked ? R.string.detail_followed :
					// R.string.detail_follow);
				}
				break;
			}
			}
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private ImageView iv_head;
		private TextView tv_nickname;
		private TextView tv_sex;
		private TextView tv_level;
		private TextView tv_focus;
		private TextView tv_fans;
		private Button btn_focus;

		public ViewHolder(View itemView) {
			super(itemView);
			iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
			tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
			tv_sex = (TextView) itemView.findViewById(R.id.tv_gender);
			tv_level = (TextView) itemView.findViewById(R.id.tv_level);
			tv_focus = (TextView) itemView.findViewById(R.id.tv_focus);
			tv_fans = (TextView) itemView.findViewById(R.id.tv_fans);
			btn_focus = (Button) itemView.findViewById(R.id.btn_focus);
		}

	}
}

interface FocusOnChangedListener {

	void onFocusOnChanged(Button button, RelationList data, int position);
}
