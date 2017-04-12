package com.dilapp.radar.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.SolutionDetailData.*;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionListData.*;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabsPager;
import com.dilapp.radar.ui.comm.FragmentTabsPager.TabsPagerInfo;
import com.dilapp.radar.ui.topic.ActivityCarePlanDetail;
import com.dilapp.radar.ui.topic.PlanAdapter;
import com.dilapp.radar.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

public class ActivityMyCarePlan extends BaseFragmentActivity implements
		OnClickListener, OnRefreshListener2<ListView>, OnItemClickListener {
	private static final String TAG = "ActivityFocusonFans";
	private static final int REQ_CARE_PLAN_DETAIL = 2000;
	private TitleView mTitle;
	private FragmentTabsPager mFragmentTabsPager;
	private PlanAdapter mUseingAdapter;
	private PlanAdapter mCollectAdapter;
	private PlanAdapter mReleaseAdapter;
	private PullToRefreshListView mUseing;
	private PullToRefreshListView mCollect;
	private PullToRefreshListView mRelease;
	private EmptyView ev_useing;
	private EmptyView ev_collect;
	private EmptyView ev_release;

	private SolutionDetailData sdd;
	private SolutionListData sld;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_focuson_fans);

		sdd = ReqFactory.buildInterface(this, SolutionDetailData.class);
		sld = ReqFactory.buildInterface(this, SolutionListData.class);
		Context context = getApplicationContext();
		// int slop = ViewConfiguration.get(context).getScaledTouchSlop();
		// Log.i("III", "slop " + slop);
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(getApplicationContext(), title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setCenterText(R.string.mine_my_care_plan, null);

		mUseingAdapter = new PlanAdapter(this, getLayoutInflater());
		mCollectAdapter = new PlanAdapter(this, getLayoutInflater());
		mReleaseAdapter = new PlanAdapter(this, getLayoutInflater());

		mUseing = MineViewHelper.getDefaultListView(this);
		mCollect = MineViewHelper.getDefaultListView(this);
		mRelease = MineViewHelper.getDefaultListView(this);

		mUseing.setOnRefreshListener(this);
		mCollect.setOnRefreshListener(this);
		mRelease.setOnRefreshListener(this);

		mUseing.setAdapter(mUseingAdapter);
		mCollect.setAdapter(mCollectAdapter);
		mRelease.setAdapter(mReleaseAdapter);

		mUseing.setOnItemClickListener(this);
		mCollect.setOnItemClickListener(this);
		mRelease.setOnItemClickListener(this);

		mFragmentTabsPager = (FragmentTabsPager) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_tabs_pager);
		mFragmentTabsPager.setTabsPagerInfos(genTabsPagerInfos());

		int page = getIntent().getIntExtra(Constants.EXTRA_FOCUS_FANS_PAGE, 0);
		d("III", "page " + page);
		mFragmentTabsPager.setCurrentItem(page);

		requestUseingList(1, true, GetPostList.GET_DATA_LOCAL);
		requestCollectList(1, true, GetPostList.GET_DATA_LOCAL);
		requestReleaseList(1, true, GetPostList.GET_DATA_LOCAL);
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
						} else {
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
	 * 真在使用
	 */
	private void requestUseingList(int page, final boolean clear, int type) {

		int totalPage = mUseing.getTag() == null ? -1 : Integer
				.parseInt(mUseing.getTag().toString());
		if (totalPage != -1 && page > totalPage && !clear) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		/*getUserListReq request_parm = new getUserListReq();
		request_parm.setPageNo(page);
		request_parm.setUserId(SharePreCacheHelper.getUserID(this));*/
		d("III_request", "useing__ page " + page + ", total " + totalPage);
		BaseCall<MSolutionResp> node = new BaseCall<MSolutionResp>() {
			@Override
			public void call(MSolutionResp resp) {
				mUseing.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (clear) {
						mUseingAdapter.getList().clear();
					}
					mUseing.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					mUseing.setTag(1);
					mUseing.setTag(R.id.tv_tag, 1);
					mUseingAdapter.getList().add(resp);
					mUseingAdapter.notifyDataSetChanged();
					/*if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						mUseing.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						mUseing.setMode(PullToRefreshBase.Mode.BOTH);
					}
					mUseing.setTag(resp.getTotalPage());
					mUseing.setTag(R.id.tv_tag, resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						mUseingAdapter.getList().addAll(resp.getDatas());
						d("III_request", "查询成功 " + resp.getPageNo() + ", "
								+ resp.getTotalPage() + ", "
								+ resp.getDatas().size());
						if (ev_useing.getVisibility() != View.GONE) {
							ev_useing.setVisibility(View.GONE);
						}
					} else if (clear) {
						if (ev_useing.getVisibility() != View.VISIBLE) {
							ev_useing.setVisibility(View.VISIBLE);
						}
					}
					mUseingAdapter.notifyDataSetChanged();*/
				} else {
					if (ev_useing.getVisibility() != View.VISIBLE) {
						ev_useing.setVisibility(View.VISIBLE);
					}
					w("III_request",
							"查询失败 "
									+ (resp != null ? resp.getMessage()
									: "null"));
					/*Toast.makeText(getApplicationContext(),
							R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
							.show();*/
				}
			}
		};
		addCallback(node);
		sdd.getSolutionInUsedDataAsync(/*request_parm, */node/*, type*/);
	}

	/**
	 * 我的收藏
	 */
	private void requestCollectList(int page, final boolean isRefresh, int type) {

		int totalPage = mCollect.getTag() == null ? -1 : Integer
				.parseInt(mCollect.getTag().toString());
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		SolutionListStoreupReq request_parm = new SolutionListStoreupReq();
		request_parm.setPageNo(page);
		d("III_request", "collect__ page " + page + ", total " + totalPage);
		BaseCall<MSolutionListResp> node = new BaseCall<MSolutionListResp>() {
			@Override
			public void call(MSolutionListResp resp) {
				mCollect.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (isRefresh) {
						mCollectAdapter.getList().clear();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						mCollect
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						mCollect.setMode(PullToRefreshBase.Mode.BOTH);
					}
					mCollect.setTag(resp.getTotalPage());
					mCollect.setTag(R.id.tv_tag, resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						mCollectAdapter.getList().addAll(resp.getDatas());
						d("III_request", "查询成功 " + resp.getPageNo() + ", "
								+ resp.getTotalPage() + ", "
								+ resp.getDatas().size());
						if (ev_collect.getVisibility() != View.GONE) {
							ev_collect.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_collect.getVisibility() != View.VISIBLE) {
							ev_collect.setVisibility(View.VISIBLE);
						}
					}
					mCollectAdapter.notifyDataSetChanged();
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
		sld.getSolutionListStoreupByTypeAsync(request_parm, node, type);
	}

	/**
	 * 我的发布
	 */
	private void requestReleaseList(int page, final boolean isRefresh, int type) {

		int totalPage = mRelease.getTag() == null ? -1 : Integer
				.parseInt(mRelease.getTag().toString());
		if (totalPage != -1 && page > totalPage && !isRefresh) {
			d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
					+ ", 最后一页了，无法加载");
			Toast.makeText(this, R.string.detail_data_finish,
					Toast.LENGTH_SHORT).show();
			return;
		}
		SolutionListCreateReq request_parm = new SolutionListCreateReq();
		request_parm.setPageNo(page);
		d("III_request", "release__ page " + page + ", total " + totalPage);
		BaseCall<MSolutionListResp> node = new BaseCall<MSolutionListResp>() {
			@Override
			public void call(MSolutionListResp resp) {
				mRelease.onRefreshComplete();
				if (resp != null && resp.isRequestSuccess()) {
					if (isRefresh) {
						mReleaseAdapter.getList().clear();
					}
					if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
						mRelease
								.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					} else {// 还有数据可拉
						mRelease.setMode(PullToRefreshBase.Mode.BOTH);
					}
					mRelease.setTag(resp.getTotalPage());
					mRelease.setTag(R.id.tv_tag, resp.getPageNo() + 1);
					if (resp.getDatas() != null && resp.getDatas().size() > 0) {
						mReleaseAdapter.getList().addAll(resp.getDatas());
						d("III_request", "查询成功 " + resp.getPageNo() + ", "
								+ resp.getTotalPage() + ", "
								+ resp.getDatas().size());
						if (ev_release.getVisibility() != View.GONE) {
							ev_release.setVisibility(View.GONE);
						}
					} else if (isRefresh) {
						if (ev_release.getVisibility() != View.VISIBLE) {
							ev_release.setVisibility(View.VISIBLE);
						}
					}
					mReleaseAdapter.notifyDataSetChanged();
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
		sld.getSolutionListCreateByTypeAsync(request_parm, node, type);
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

		View u = MineViewHelper.wrapStateView(this, mUseing);
		ev_useing = (EmptyView) u.findViewById(R.id.ev_empty);
		ev_useing.setText(R.string.empty_data);

		View c = MineViewHelper.wrapStateView(this, mCollect);
		ev_collect = (EmptyView) c.findViewById(R.id.ev_empty);
		ev_collect.setText(R.string.empty_data);

		View r = MineViewHelper.wrapStateView(this, mRelease);
		ev_release = (EmptyView) r.findViewById(R.id.ev_empty);
		ev_release.setText(R.string.empty_data);

		// TODO
		List<TabsPagerInfo> infos = new ArrayList<TabsPagerInfo>(3);
		infos.add(new TabsPagerInfo(0, R.string.plan_useing, u));
		infos.add(new TabsPagerInfo(0, R.string.speak_collect, c));
		infos.add(new TabsPagerInfo(0, R.string.speak_release, r));
		return infos;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView == mUseing) {
			d("III_refresh", " useing");
			requestUseingList(1, true, GetPostList.GET_DATA_SERVER);
		} else if (refreshView == mCollect) {
			d("III_refresh", " collect");
			requestCollectList(1, true, GetPostList.GET_DATA_SERVER);
		} else {
			d("III_refresh", " release");
			requestReleaseList(1, true, GetPostList.GET_DATA_SERVER);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int page = refreshView.getTag() != null ? Integer.parseInt(refreshView
				.getTag().toString()) : 0;
		if (refreshView == mUseing) {
			d("III_load", " focus");
			requestUseingList(page, false, GetPostList.GET_DATA_SERVER);
		} else if (refreshView == mCollect) {
			d("III_load", " fans");
			requestCollectList(page, false, GetPostList.GET_DATA_SERVER);
		} else {
			d("III_refresh", " release");
			requestReleaseList(page, false, GetPostList.GET_DATA_SERVER);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// position++;
		MSolutionResp item = (MSolutionResp) parent.getAdapter().getItem(position);

		d("III", "item " + JsonUtils.toJson(item) + ", positon " + position);
		if (item != null) {
			Intent intent = new Intent(this, ActivityCarePlanDetail.class);
			intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
			startActivityForResult(intent, REQ_CARE_PLAN_DETAIL);
		}
	}

}

