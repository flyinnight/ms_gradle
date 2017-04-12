package com.dilapp.radar.ui.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.*;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.impl.GetPostListImpl;
import com.dilapp.radar.textbuilder.utils.L;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.VerticalImageSpan;
import com.dilapp.radar.util.ViewUtils;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.dilapp.radar.view.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/7/14.
 */
public class FragmentPostList extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {

    public static final int REQ_POST_DETAIL = 50;

    private View vg_waiting;
    private PullToRefreshListView mListView;
    private PostAdapter mAdapter;
    private final int REQUEST_RELEASE_CODE = 400;

    private List<String> displayedImages = Collections
            .synchronizedList(new LinkedList<String>());

    private int currPage = 1;
    private int totalPage = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PostAdapter(mContext, mInflater, 0);
    }

    @Override
    public void onCreateView(/*LayoutInflater inflater, */ViewGroup container, Bundle savedInstanceState) {
        // Log.i("III", "onCreateView Diff-------");
        super.onCreateView(/*inflater, */container, savedInstanceState);
        setContentView(R.layout.fragment_post_list);
        setCacheView(true);

        //if(!isCacheView()) {

        vg_waiting = findViewById(R.id.vg_waiting);

        mListView = findViewById(R.id.lv_topic);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(this);
        requestPostList(currPage++, true, GetPostList.GET_DATA_LOCAL);
        //}
        //return getContentView();
        test();
    }

    private void test() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        // mTitle.setCenterText();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_POST_DETAIL) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                requestPostList(1, true, GetPostList.GET_DATA_SERVER);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                Intent jump_release = new Intent(mContext, ActivityPostEditPre.class);
                startActivityForResult(jump_release, 10);
                break;
            }
            case TitleView.ID_RIGHT: {
//                Intent intent = new Intent(mContext, SearchTopicActivity.class);
//                startActivityForResult(intent, 20);
                break;
            }
            case TitleView.ID_CENTER: {
//                Intent intent = new Intent(mContext, ActivityTopicDetail.class);
//                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onStop() {
        mListView.onRefreshComplete();
        super.onStop();
    }

    private void requestPostList(int page, final boolean isRefresh, int type) {

        if (totalPage != -1 && page > totalPage && !isRefresh) {
            Log.i("III_logic", "当前页 " + page + ", 总页数 " + totalPage + ", 最后一页了，无法加载");
            Toast.makeText(mContext, R.string.detail_data_finish, Toast.LENGTH_SHORT).show();
            return;
        }

        Object obj = ReqFactory
                .buildInterface(getActivity(), GetPostList.class);
        GetPostListImpl mDetail = (GetPostListImpl) obj;
        GetPostList.MFollowTopicPostReq request_parm = new GetPostList.MFollowTopicPostReq();
        request_parm.setPageNo(page);

        BaseCall<TopicPostListResp> node = new BaseCall<TopicPostListResp>() {
            @Override
            public void call(TopicPostListResp resp) {
                mListView.onRefreshComplete();
                if (resp != null && resp.isRequestSuccess()) {

                    if (isRefresh) {
                        mAdapter.list.clear();
                    }
                    List<MPostResp> posts = resp.getPostLists();
                    if (resp.getTotalPage() <= resp.getPageNo()) {
                        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    // mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    mAdapter.list.addAll(resp.getPostLists());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "没有拿到数据", Toast.LENGTH_SHORT).show();
                }
                vg_waiting.setVisibility(View.GONE);
            }
        };
        addCallback(node);
        mDetail.getPostsOfFollowTopicByTypeAsync(request_parm, node, type);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPostList(1, true, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPostList(currPage++, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Log.i("III", "posi:" + position);
        MPostResp item = mAdapter.getItem(position - 1);
        if (item == null) {
            return;
        }
        Intent intent = new Intent(mContext, ActivityPostDetail.class);
        // 貌似ChildFragment主动回调不到onActivityResult
        // id传到详情界面
        // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
        getParentFragment().startActivityForResult(intent, REQ_POST_DETAIL);

    }


}