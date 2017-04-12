package com.dilapp.radar.ui.topic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.Banner.BannerResp;
import com.dilapp.radar.domain.Banner.GetBannerListResp;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MFollowTopicPostReq;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.PostCollection;
import com.dilapp.radar.domain.PostCollection.GetPostCollectionListResp;
import com.dilapp.radar.domain.PostCollection.PostCollectionResp;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionDetailData.*;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.FragmentTabs;
import com.dilapp.radar.ui.comm.FragmentTabs.*;
import com.dilapp.radar.view.CustomScrollView;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.dilapp.radar.view.PullToRefreshCustomScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import cn.lightsky.infiniteindicator.InfiniteIndicatorLayout;
import cn.lightsky.infiniteindicator.slideview.BaseSliderView;
import cn.lightsky.infiniteindicator.slideview.BaseSliderView.OnSliderClickListener;
import cn.lightsky.infiniteindicator.slideview.BaseSliderView.ScaleType;
import cn.lightsky.infiniteindicator.slideview.DefaultSliderView;

import static com.dilapp.radar.textbuilder.utils.L.*;


/**
 * Created by husj1 on 2015/7/27.
 */
public class FragmentPosts extends BaseFragment implements OnClickListener, OnSliderClickListener, PullToRefreshBase.OnRefreshListener2<CustomScrollView>, AdapterView.OnItemClickListener, PostAdapter.PostAdapterListener, CustomScrollView.OnScrollChangedListener, TabHost.OnTabChangeListener {

    public static final int REQ_POST_DETAIL = 50;
    public static final int REQ_POST_RELEASE = 10;

    DisplayImageOptions options;

    private TitleView mTitle;

    private PullToRefreshCustomScrollView ptr_scroll;
    private InfiniteIndicatorLayout iil_banner;
    private LinearLayoutForListView ll_highlights;
    private LinearLayoutForListView ll_posts;
    private HighlightsAdapter mHighlightsAdapter;
    private PostAdapter mPostAdapter;
    private FragmentTabs mTabsPager;
    private View vg_tabs;
    private ViewGroup vg_float;
    private ViewGroup vg_floating;

    private int currPage = 1;
    private int totalPage = -1;
    private boolean isRefreshBanner;
    private boolean isRefreshHighlights;
    private boolean isRefreshPostList;
    private boolean isLoadable;
    private String preTabId;
    private SparseArray<Integer> saveScollY;// 保存2个列表滚动的值
    //    private ListView lv_highlights;
//    private HighlightsAdapter lv_highlights_adapter;
    private boolean isReceiver = false;
    private BroadcastReceiver postReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!PostReleaseCallBack.MAINPOST_RELEASE_END.equals(intent.getAction())) return;

            MPostResp resp = (MPostResp) intent.getSerializableExtra("RespData");
            int index = -1;
            List<MPostResp> list = mPostAdapter.getList();
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                MPostResp post = list.get(i);
                if (post.getLocalPostId() != 0 && post.getLocalPostId() == resp.getLocalPostId()) {
                    index = i;
                    break;
                }
            }
            d("III", "index " + index + ", receiver " + JsonUtils.toJson(resp));
            if (index == -1) {
                return;
            }
            if (resp.getSendState() == PostReleaseCallBack.POST_RELEASE_SENDSUCCESS) {
                resp.setLocalPostId(0);
                list.set(index, resp);
            } else {
                list.get(index).setSendState(resp.getSendState());
            }
            mPostAdapter.notifyItemRangeChanged(index);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabsPager = new FragmentTabs();
        saveScollY = new SparseArray<Integer>(2);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_bbs_default)
                        // 正在加载的图片
                .showImageForEmptyUri(R.drawable.img_bbs_default)
                        // URL请求失败
                .showImageOnFail(R.drawable.img_bbs_default)
                        // 图片加载失败
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                        // .displayer(new RoundedBitmapDisplayer(mContext.getResources().getDimensionPixelSize(R.dimen.topic_main_radius)))
                .displayer(new FadeInBitmapDisplayer(200))
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }

    @Override
    protected void onCreateView(ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(container, savedInstanceState);
        setContentView(R.layout.fragment_posts);
        setCacheView(true);

        mTitle = new TitleView(mContext, findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(R.string.main_topic, this);
        mTitle.setRightIcon(R.drawable.btn_topic_search, this);
        mTitle.setLeftIcon(R.drawable.btn_release, this);

        ptr_scroll = findViewById(R.id.ptr_scroll);
        ptr_scroll.getRefreshableView().setOnScrollChangedListener(this);
        iil_banner = findViewById(R.id.iil_banner);
        iil_banner.setInterval(5000);
        vg_float = findViewById(R.id.vg_float);
        vg_floating = findViewById(R.id.vg_floating);

        ll_highlights = findViewById(R.id.ll_highlights);
        ll_highlights.setAdapter(mHighlightsAdapter = new HighlightsAdapter(new ArrayList<PostCollectionResp>(3)));
        ll_highlights.setOnItemClickListener(mHighlightsAdapter);

        mPostAdapter = new PostAdapter(mContext, mInflater, 0);
        mPostAdapter.setPostAdapterListener(this);
        ll_posts = findViewById(R.id.ll_posts);
        ll_posts.setAdapter(mPostAdapter);
        ll_posts.setOnItemClickListener(this);
        ptr_scroll.setOnRefreshListener(this);
        ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptr_scroll.demo();

        getChildFragmentManager().beginTransaction().add(R.id.fragment_container_child, mTabsPager).commit();
        List<TabsPagerInfo> pagers = new ArrayList<TabsPagerInfo>(2);
        pagers.add(new TabsPagerInfo(0, R.string.topic_this_week, ll_highlights));
        pagers.add(new TabsPagerInfo(0, R.string.topic_recently_update, ll_posts));
        mTabsPager.setTabsPagerInfos(pagers);
        mTabsPager.setCacheView(true);

        requestBanner(true, GetPostList.GET_DATA_LOCAL);
        requestHighlights(true, GetPostList.GET_DATA_LOCAL);
        requestPostList(currPage++, true, GetPostList.GET_DATA_LOCAL);
        // PagerTabStrip
        IntentFilter filter = new IntentFilter();
        filter.addAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
        mContext.registerReceiver(postReceiver, filter);
        isReceiver = true;
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                vg_tabs = findViewById(R.id.vg_tabs);
                mTabsPager.getTabHost().setOnTabChangedListener(FragmentPosts.this);
                ((ViewGroup) vg_tabs.getParent()).removeView(vg_tabs);
                vg_float.setMinimumHeight(vg_tabs.getMeasuredHeight());
                vg_float.addView(vg_tabs);
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                Intent jump_release = new Intent(mContext, ActivityPostEditPre.class);
                startActivityForResult(jump_release, REQ_POST_RELEASE);
                break;
            }
            case TitleView.ID_RIGHT: {
                startActivity(new Intent(mContext, ActivityPostSearch.class));
                break;
            }
        }
    }

    @Override
    public void onChildViewClick(View v, Object item, final int position) {

        final MPostResp post = (MPostResp) item;
        switch (v.getId()) {
            case R.id.tv_sending: {// 本地帖重发
                d("III", "resend position " + position + ", obj " + JsonUtils.toJson(post));
                PostReleaseCallBack prc = ReqFactory.buildInterface(mContext, PostReleaseCallBack.class);
                PostReleaseReq req = new PostReleaseReq();
                req.setLocalCreateTime(post.getCreateTime());
                req.setToUserId(post.getToUserId());
                req.setSkin(post.getSkinQuality());
                req.setSelectedToSolution(post.isSelectedToSolution());
                req.setThumbURL(post.getThumbURL());
                req.setPostLevel(post.getPostLevel());
                req.setPart(post.getPart());
                req.setEffect(post.getEffect());
                req.setTopicTitle(post.getTopicTitle());
                req.setPostId(post.getId());
                req.setTopicId(post.getTopicId());
                req.setPostTitle(post.getPostTitle());
                req.setPostContent(post.getPostContent());
                req.setLocalPostId(post.getLocalPostId());
                d("III", "重新发送 localId" + post.getLocalPostId());
                BaseCall<MPostResp> call = new BaseCall<MPostResp>() {
                    @Override
                    public void call(MPostResp resp) {
                        if (resp != null && resp.isRequestSuccess()) {
                            post.setSendState(resp.getSendState());
                            // mPostAdapter.getList().set(positon, post);
                            mPostAdapter.notifyItemRangeChanged(position);
                        }
                    }
                };
                addCallback(call);
                prc.createPostAsync(req, call);
                break;
            }
            case R.id.tv_delete: {// 本地帖删除
                d("III", "delete position " + position + ", obj " + JsonUtils.toJson(post));
                PostReleaseCallBack prc = ReqFactory.buildInterface(mContext, PostReleaseCallBack.class);
                PostReleaseReq req = new PostReleaseReq();
                req.setLocalPostId(post.getLocalPostId());
                req.setPostLevel(post.getPostLevel());
                BaseCall<MPostResp> call = new BaseCall<MPostResp>() {
                    @Override
                    public void call(MPostResp resp) {
                        if (resp != null && resp.isRequestSuccess()) {
                            mPostAdapter.getList().remove(position);
                            mPostAdapter.notifyItemRangeRemoved(position);
                        }
                    }
                };
                addCallback(call);
                prc.deleteLocalPostAsync(req, call);
                break;
            }
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        BannerResp data = (BannerResp) slider.getBundle().getSerializable("data");
        if (data == null) {
            return;
        }
        if (data.getPostId() != 0) {
            MPostResp item = new MPostResp();
            item.setId(data.getPostId());
            item.setTopicId(data.getTopicId());
            Intent intent = new Intent(mContext, ActivityPostDetail.class);
            // 貌似ChildFragment主动回调不到onActivityResult
            // id传到详情界面
            // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
            intent.putExtra(Constants.EXTRA_POST_DETAIL_BANNER, data.getPriority());
            intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
            startActivityForResult(intent, REQ_POST_DETAIL);
        } else if (data.getSolutionId() != 0) {
            MSolutionResp item = new MSolutionResp();
            item.setSolutionId(data.getSolutionId());
            Intent intent = new Intent(mContext, ActivityCarePlanDetail.class);
            // 貌似ChildFragment主动回调不到onActivityResult
            // id传到详情界面
            // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
            intent.putExtra(Constants.EXTRA_POST_DETAIL_BANNER, data.getPriority());
            intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
            startActivityForResult(intent, REQ_POST_DETAIL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        iil_banner.startAutoScroll();
    }

    @Override
    public void onPause() {
        iil_banner.stopAutoScroll();
        super.onPause();
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getParentFragment().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        d("III", "request " + requestCode + ", result " + resultCode);
        if (requestCode == REQ_POST_DETAIL) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                if (data != null && data.getIntExtra(Constants.EXTRA_POST_DETAIL_BANNER, 0) > 0) {
                    requestBanner(true, GetPostList.GET_DATA_SERVER);
                } else if (data != null && data.getBooleanExtra(Constants.EXTRA_POST_DETAIL_HIGHLIGHTS, false)) {
                    requestHighlights(true, GetPostList.GET_DATA_SERVER);
                } else {
                    requestPostList(1, true, GetPostList.GET_DATA_SERVER);
                }
            }
        } else if (requestCode == REQ_POST_RELEASE && resultCode == Activity.RESULT_OK) {
            ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            ptr_scroll.demo();
            requestPostList(1, true, GetPostList.GET_DATA_SERVER);
        }
        /*
        List<Fragment> list = getChildFragmentManager().getFragments();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isVisible()) {
                list.get(i).onActivityResult(requestCode, resultCode, data);
            }
        }*/
    }


    @Override
    public void onStop() {
        ptr_scroll.onRefreshComplete();
        super.onStop();
    }

    private void addBanner(BannerResp b) {
        if (b == null || b.getBannerUrl() == null || b.getBannerUrl().size() == 0) {
            return;
        }
        String relative = b.getBannerUrl().get(0);
        String url = TopicHelper.wrappeImagePath(relative);
        d("III_url", "banner id " + b.getPostId() + ", url " + url);
        DefaultSliderView textSliderView = new DefaultSliderView(mContext);
        textSliderView.image(url)
                .setScaleType(ScaleType.Fit)
                .setOnSliderClickListener(this)
                .isShowErrorView(true);
        textSliderView.getBundle().putSerializable("data", b);
        iil_banner.addSlider(textSliderView);
    }

    private void requestPostList(int page, final boolean isRefresh, int type) {

        if (totalPage != -1 && page > totalPage && !isRefresh) {
            isRefreshPostList = true;
            if (isRefreshBanner && isRefreshHighlights && isRefreshPostList) {
                ptr_scroll.onRefreshComplete();
            }
            d("III_logic", "当前页 " + page + ", 总页数 " + totalPage + ", 最后一页了，无法加载");
            Toast.makeText(mContext, R.string.detail_data_finish, Toast.LENGTH_SHORT).show();
            ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            return;
        }

        isRefreshPostList = false;
        GetPostList mDetail = ReqFactory.buildInterface(mContext, GetPostList.class);
        MFollowTopicPostReq request_parm = new MFollowTopicPostReq();
        request_parm.setPageNo(page);
        d("III", "page " + page + ", refresh " + isRefresh);
        BaseCall<TopicPostListResp> node = new BaseCall<TopicPostListResp>() {
            @Override
            public void call(TopicPostListResp resp) {
                isRefreshPostList = true;
                if (isRefreshBanner && isRefreshHighlights && isRefreshPostList) {
                    ptr_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    d("III", "普通贴请求成功");
                    if (isRefresh) {
                        mPostAdapter.list.clear();
                        mPostAdapter.notifyDataSetChanged();
                        ll_posts.setTag(true);
                    }
                    if (resp.getTotalPage() <= resp.getPageNo()) {
                        ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        ll_posts.setTag(false);
                    } else if (isLoadable) {
                        ptr_scroll.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    // mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    d("III", "page " + resp.getPageNo() + ", totalPage " + resp.getTotalPage());
                    final int size = resp.getPostLists() != null ? resp.getPostLists().size() : 0;
                    for (int i = 0; i < size; i++) {
                        mPostAdapter.addItem(resp.getPostLists().get(i));
                    }
                    // mPostAdapter.list.addAll(resp);
                    // mPostAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(mContext, "没有拿到数据", Toast.LENGTH_SHORT).show();
                }
                // vg_waiting.setVisibility(View.GONE);
            }
        };
        addCallback(node);
        mDetail.getPostsOfFollowTopicByTypeAsync(request_parm, node, type);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<CustomScrollView> refreshView) {
        //if (iil_banner.getSliderCount() == 0) {
        requestBanner(true, GetPostList.GET_DATA_SERVER);
        // }
        requestHighlights(true, GetPostList.GET_DATA_SERVER);
        requestPostList(1, true, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<CustomScrollView> refreshView) {
        requestPostList(currPage++, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // Log.i("III", "posi:" + position);
        MPostResp item = mPostAdapter.getItem(position);
        if (item == null) {
            return;
        }
        d("III", "localPostId " + item.getLocalPostId() + ", postId " + item.getId());
        Intent intent = new Intent(mContext, ActivityPostDetail.class);
        // 貌似ChildFragment主动回调不到onActivityResult
        // id传到详情界面
        // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
        getParentFragment().startActivityForResult(intent, REQ_POST_DETAIL);

    }

    private void requestBanner(final boolean isRefresh, int type) {
        Banner b = ReqFactory.buildInterface(mContext, Banner.class);

        isRefreshBanner = false;
        iil_banner.stopAutoScroll();
        d("III", "请求Banner");
        BaseCall<GetBannerListResp> node = new BaseCall<GetBannerListResp>() {
            @Override
            public void call(GetBannerListResp resp) {
                isRefreshBanner = true;
                if (isRefreshBanner && isRefreshHighlights && isRefreshPostList) {
                    ptr_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    if (isRefresh) {
                        iil_banner.removeAllSlider();
                    }
                    if (resp.getDatas() != null) {
                        final int size = resp.getDatas().size();
                        for (int i = 0; i < size; i++) {
                            BannerResp b = resp.getDatas().get(i);
                            if (b == null) continue;
                            addBanner(b);
                        }
                        iil_banner.notifyDataChange();
                        iil_banner.startAutoScroll();
                        iil_banner.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Center_Bottom);
                    }

                    d("III", "Banner请求成功 " + (resp.getDatas() != null ? JsonUtils.toJson(resp.getDatas()) : 0));
                } else {
                    w("III", "Banner请求失败 " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(node);
        b.getBannerListByTypeAsync(new BaseReq(), node, type);
    }

    private void requestHighlights(final boolean isRefresh, int type) {
        PostCollection pc = ReqFactory.buildInterface(mContext, PostCollection.class);
        d("III", "请求精华帖");
        isRefreshHighlights = false;
        BaseCall<GetPostCollectionListResp> node = new BaseCall<GetPostCollectionListResp>() {
            @Override
            public void call(GetPostCollectionListResp resp) {

                isRefreshHighlights = true;
                if (isRefreshBanner && isRefreshHighlights && isRefreshPostList) {
                    ptr_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    if (resp.getDatas() == null || resp.getDatas().size() == 0) {
                        d("III", "没有精华帖");
                        return;
                    }
                    if (isRefresh) {
                        mHighlightsAdapter.list.clear();
                        mHighlightsAdapter.notifyDataSetChanged();
                        ll_posts.setTag(false);
                    }
                    for (int i = 0; i < resp.getDatas().size(); i++) {
                        mHighlightsAdapter.addItem(resp.getDatas().get(i));
                    }
//                    if (ll_highlights instanceof AbsListView) {
//                        mHighlightsAdapter.notifyDataSetChanged();
//                    }

                    d("III", "精华帖请求成功" + (resp.getDatas().size()));
                } else {
                    d("III", "精华帖请求失败 " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(node);
        pc.getPostCollectionListByTypeAsync(1, node, type);
    }

    @Override
    public void onDestroy() {
        if (isReceiver && postReceiver != null) {
            mContext.unregisterReceiver(postReceiver);
            isReceiver = false;
        }
        super.onDestroy();
    }

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {

        // d("III_scroll", "y " + y + ", vg_float.getTop " + vg_float.getTag());
        if (y >= vg_float.getTop()) {
            if (vg_tabs.getParent() != vg_floating) {
                vg_float.removeView(vg_tabs);
                vg_floating.addView(vg_tabs);
                if (vg_floating.getVisibility() != View.VISIBLE) {
                    vg_floating.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (vg_tabs.getParent() != vg_float) {
                vg_floating.removeView(vg_tabs);
                vg_float.addView(vg_tabs);
                if (vg_floating.getVisibility() == View.VISIBLE) {
                    vg_floating.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void scrollBottom() {

    }

    @Override
    public void onTabChanged(String tabId) {
        int index = Integer.parseInt(tabId);
        int preIndex = Integer.parseInt(preTabId != null ? preTabId : "0");
        changeScrollY(index, preIndex);
        changeRefreshMode(index, preIndex);
        preTabId = tabId;
    }

    private void changeScrollY(int curr, int prev) {
        int currY = ptr_scroll.getRefreshableView().getScrollY();
        int top = vg_float.getTop();

        // 恢复并记录列表的滚动位置
        saveScollY.put(prev, currY);
        Integer y = saveScollY.get(curr);// 获取上次记录的滚动值
        if ((y == null) || (currY >= top && y < top)) {
            y = top;
        }
//        d("III_view", "savedIndex " + preIndex +
//                " savedY " + ptr_scroll.getRefreshableView().getScrollY() + " || " +
//                " scrollIndex " + tabId +
//                " scrollY " + y);

        if (currY >= top && y >= top) {
            ptr_scroll.getRefreshableView().scrollTo(0, y);
        }
    }

    private void changeRefreshMode(int curr, int prev) {
        // 处理列表可否刷新操作
        Object tag = mTabsPager.getTabHost().getCurrentView().getTag();
        isLoadable = tag == null ? false : Boolean.parseBoolean(tag.toString());
        //d("III_view", "tag " + tag + ", isLoadable " + isLoadable);
        Mode mode = (isLoadable ? Mode.BOTH : Mode.PULL_FROM_START);
        ptr_scroll.setMode(mode);
    }

    class HighlightsAdapter extends LinearLayoutForListView.LinearLayoutForListViewAdapter implements AdapterView.OnItemClickListener {

        List<PostCollectionResp> list;

        HighlightsAdapter(List<PostCollectionResp> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public PostCollectionResp getItem(int position) {
            return list != null && position < list.size() && position >= 0 ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return list != null && position < list.size() && position >= 0 ? list.get(position).getPostId() : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            HighlightsHolder vh = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_post_highlights, null);
                convertView.setTag(vh = new HighlightsHolder(convertView));
            } else {
                vh = (HighlightsHolder) convertView.getTag();
            }

            PostCollectionResp item = getItem(position);
            d("III", "highlights topicID " + item.getTopicId() + " url " + (item != null ? item.getpicUrl() : null));
            if (item == null || item.getpicUrl() == null || "".equals(item.getpicUrl().trim())) {
                return convertView;
            }
            String url = TopicHelper.wrappeImagePath(item.getpicUrl());
            String topic = getString(R.string.topic_prefix,
                    item.getSolutionId() != 0 ? getString(R.string.found_skin_plan) : item.getTopicTitle());
            String ad = item.getSlogan() != null && !"".equals(item.getSlogan().trim()) ? item.getSlogan() : "unknown";
            // d("III", "highlights2 url " + url);
//            ImageLoader.getInstance().displayImage(url, vh.iv_image, options);
//            com.dilapp.radar.util.ViewUtils.measureView(vh.iv_image);

            Spannable s = new SpannableString(topic + "| " + ad);
            ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.test_primary));
            s.setSpan(span, 0, topic.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.tv_text.setText(s);
            ImageLoader.getInstance().displayImage(url, vh.iv_image, options/*new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }
                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }
                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        ImageView iv = (ImageView) view;
                        int[] wh = TopicHelper.getCurrWidthAndHeight(bitmap.getWidth(), bitmap.getHeight(),
                                DensityUtils.dip2px(mContext, 346.6f));
                        iv.setMinimumWidth(wh[0]);
                        iv.setMaxWidth(wh[0]);
                        iv.setMinimumHeight(wh[1]);
                        iv.setMaxHeight(wh[1]);
                    }
                }
                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            }*/);
            /*final ImageView iv = vh.iv_image;
            new AsyncTask<String,Object, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    int width = Integer.parseInt(params[1]);
                    Log.i("III", "width " + width);
                    return TopicHelper.getResizeBitmapForNet(mContext, url, width);
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    iv.setImageBitmap(bitmap);
                }
            }.execute(url, DensityUtils.dip2px(mContext, 346.6f) + "");*/

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            d("III", "posi:" + position + ", id " + id);
            PostCollectionResp data = getItem(position);
            if (data == null) {
                return;
            }

            if (data.getPostId() != 0) {
                MPostResp item = new MPostResp();
                item.setTopicId(data.getTopicId());
                item.setId(data.getPostId());
                Intent intent = new Intent(mContext, ActivityPostDetail.class);
                // 貌似ChildFragment主动回调不到onActivityResult
                // id传到详情界面
                // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
                intent.putExtra(Constants.EXTRA_POST_DETAIL_HIGHLIGHTS, true);
                intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
                startActivityForResult(intent, REQ_POST_DETAIL);
            } else if (data.getSolutionId() != 0) {
                MSolutionResp item = new MSolutionResp();
                item.setTitle(data.getSolutionTitle());
                item.setSolutionId(data.getSolutionId());
                Intent intent = new Intent(mContext, ActivityCarePlanDetail.class);
                // 貌似ChildFragment主动回调不到onActivityResult
                // id传到详情界面
                // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
                intent.putExtra(Constants.EXTRA_POST_DETAIL_HIGHLIGHTS, true);
                intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
                startActivityForResult(intent, REQ_POST_DETAIL);
            }
        }

        @Override
        public void addItem(Object item) {
            list.add((PostCollectionResp) item);
            super.addItem(item);
        }
    }

    class HighlightsHolder {
        ImageView iv_image;
        TextView tv_text;

        public HighlightsHolder(View itemView) {
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}
