package com.dilapp.radar.ui.found;

import static com.dilapp.radar.textbuilder.utils.L.d;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DeletePostTopic;
import com.dilapp.radar.domain.DeletePostTopic.*;
import com.dilapp.radar.domain.FollowupPostTopic;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostReq;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicDetailReq;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.topic.ActivityPostDetail;
import com.dilapp.radar.ui.topic.PostAdapter;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.view.CustomScrollView;
import com.dilapp.radar.view.LinearLayoutForListView;
import com.dilapp.radar.view.PullToRefreshCustomScrollView;
import com.dilapp.radar.widget.ButtonsDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by husj1 on 2015/7/31.
 */
public class ActivityTopicDetail extends BaseFragmentActivity implements View.OnClickListener, OnRefreshListener2<CustomScrollView>, AdapterView.OnItemClickListener, CustomScrollView.OnScrollChangedListener {

    public static final int REQ_TOPIC_MODIFY = 30;
    public static final int REQ_POST_DETAIL = 50;
    private Context mContext;
    private TitleView mTitle;

    private View vg_loading;
    private View vg_error;
    private PullToRefreshCustomScrollView ptr_scroll;
    private ImageView iv_image;
    private TextView tv_topic_title;
    private TextView tv_topic_content;
    private TextView tv_join;
    private TextView tv_create_time;
    private Button btn_join;
    private TextView tv_post_number;
    private LinearLayoutForListView lv_post;
    private Button btn_delete;
    private Button btn_modify;

    private ViewGroup vg_float;
    private ViewGroup vg_floating;

    private ButtonsDialog mOptionDialog;
    private ButtonsDialog mDeleteDialog;

    private PostAdapter mPostAdapter;

    private long topicId;
    private MTopicResp topic;

    private int currPage = 1;
    private int totalPage = -1;
    private boolean reqTopic;
    private boolean reqPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        topicId = getIntent().getLongExtra(Constants.EXTRA_TOPIC_DETAIL_ID, 0);
        mPostAdapter = new PostAdapter(this, getLayoutInflater());

        mContext = getApplicationContext();
        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        vg_loading = findViewById_(R.id.vg_loading);
        vg_error = findViewById(R.id.vg_error);
        ptr_scroll = findViewById_(R.id.ptr_scroll);
        iv_image = findViewById_(R.id.iv_image);
        tv_topic_title = findViewById_(R.id.tv_topic_title);
        tv_topic_content = findViewById_(R.id.tv_topic_content);
        tv_join = findViewById_(R.id.tv_join);
        tv_create_time = findViewById_(R.id.tv_create_time);
        btn_join = findViewById_(R.id.btn_join);
        tv_post_number = findViewById_(R.id.tv_post_number);
        lv_post = findViewById_(R.id.lv_post);
        ptr_scroll.setOnRefreshListener(this);
        lv_post.setAdapter(mPostAdapter);
        lv_post.setOnItemClickListener(this);
        vg_float = findViewById_(R.id.vg_float);
        vg_floating = findViewById_(R.id.vg_floating);
        ptr_scroll.getRefreshableView().setOnScrollChangedListener(this);
        btn_delete = findViewById_(R.id.btn_delete);
        btn_modify = findViewById_(R.id.btn_modify);
        // ((AnimationDrawable)((ImageView)vg_loading).getDrawable()).start();

        View deleteView = findViewById(R.id.vg_delete);
        ((ViewGroup) deleteView.getParent()).removeView(deleteView);
        deleteView.setVisibility(View.VISIBLE);
        mOptionDialog = new ButtonsDialog(this);
        mOptionDialog.setContentView(deleteView);
        mOptionDialog.setWidthFullScreen();

        View enterView = findViewById(R.id.vg_delete_enter);
        ((ViewGroup) enterView.getParent()).removeView(enterView);
        enterView.setVisibility(View.VISIBLE);
        mDeleteDialog = new ButtonsDialog(this);
        mDeleteDialog.setContentView(enterView);
        mDeleteDialog.setWidthFullScreen();

        requestTopicDetail(topicId, GetPostList.GET_DATA_LOCAL);
        requestPostList(currPage, topicId, true, GetPostList.GET_DATA_LOCAL);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                finish();
                break;
            }
            case TitleView.ID_RIGHT: {
                mOptionDialog.show();
                break;
            }
            case R.id.btn_join: {
                if(topic == null || topic.getTopicId() == 0) {
                    Toast.makeText(this, R.string.found_detail_topic_invalid, Toast.LENGTH_SHORT).show();
                    break;
                }
                requestFocusTopics(topic.getTopicId(), !topic.getFollowup(), v, tv_join);
                break;
            }
            case R.id.vg_error: {
                requestTopicDetail(topicId, GetPostList.GET_DATA_SERVER);
                requestPostList(currPage, topicId, true, GetPostList.GET_DATA_SERVER);
                vg_loading.setVisibility(View.VISIBLE);
                vg_error.setVisibility(View.GONE);
                ptr_scroll.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_modify: {
                Intent intent = new Intent(this, ActivityTopicEdit.class);
                intent.putExtra(Constants.EXTRA_TOPIC_EDIT_IS_MODIFY, true);
                intent.putExtra(Constants.EXTRA_TOPIC_EDIT_CONTENT, topic);
                startActivityForResult(intent, REQ_TOPIC_MODIFY);
                mOptionDialog.dismiss();
                break;
            }
            case R.id.btn_delete: {
                mOptionDialog.dismiss();
                mDeleteDialog.show();
                break;
            }
            case R.id.btn_delete_enter: {
                mDeleteDialog.dismiss();
                showWaitingDialog((AsyncTask) null);
                requestTopicDelete(topic.getTopicId());
                break;
            }
            case R.id.btn_cancel: {
                mOptionDialog.dismiss();
                break;
            }
            case R.id.btn_cancel_delete: {
                mDeleteDialog.dismiss();
                break;
            }
        }
    }

    private void setUIFromData(MTopicResp data) {

        if (data == null) {
            return;
        }

        DateFormat fmt = new SimpleDateFormat(getString(R.string.found_detail_create_fmt));
        String imgUrl = (data.getTopicimg()[0].startsWith("http") ? "" : HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP) + data.getTopicimg()[0];
        String title = data.getTopictitle() != null ? data.getTopictitle() : "unknown";
        String joinNumber = getString(R.string.found_detail_what_join, "" + data.getFollowsUpNum());
        String time = getString(R.string.found_detail_create, fmt.format(new Date(data.getReleasetime())));
        String content = data.getContent() != null ? data.getContent() : "";
        String joinBtn = getString(data.getFollowup() ? R.string.found_detail_unjoin : R.string.found_detail_join);
        String postNumber = getString(R.string.found_detail_post_number, data.getRegen() + "");

        ImageLoader.getInstance().displayImage(imgUrl, iv_image);
        mTitle.setCenterText(title, null);
        tv_topic_title.setText(title);
        tv_join.setText(joinNumber);
        tv_create_time.setText(time);
        tv_topic_content.setText(content);
        btn_join.setText(joinBtn);
        tv_post_number.setText(postNumber);

        if (Permissions.canTopicDelete(this, data) ||
                Permissions.canTopicModify(this, data)) {
            mTitle.setRightIcon(R.drawable.btn_more, this);

            if (!Permissions.canTopicDelete(this, data)) {
                btn_delete.setVisibility(View.GONE);
            }
            if (!Permissions.canTopicModify(this, data)) {
                btn_modify.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*if(requestCode == REQ_POST_DETAIL) {
            if(resultCode == Activity.RESULT_FIRST_USER) {
                requestPostList(1, true);
            }
        }
        List<Fragment> list = getChildFragmentManager().getFragments();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).isVisible()) {
                list.get(i).onActivityResult(requestCode, resultCode, data);
            }
        }*/
        if (REQ_TOPIC_MODIFY == requestCode && resultCode == RESULT_OK) {
            ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            ptr_scroll.demo();
            requestTopicDetail(topicId, GetPostList.GET_DATA_SERVER);
            requestPostList(1, topicId, true, GetPostList.GET_DATA_SERVER);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // Log.i("III", "posi:" + position);
        MPostResp item = mPostAdapter.getItem(position);
        if (item == null) {
            return;
        }
        Intent intent = new Intent(mContext, ActivityPostDetail.class);
        // 貌似ChildFragment主动回调不到onActivityResult
        // id传到详情界面
        // intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, item.getId());
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
        startActivityForResult(intent, REQ_POST_DETAIL);

    }

    /**
     * 获取话题详情
     * @param topicId
     */
    private void requestTopicDetail(long topicId, int type) {

        TopicListCallBack tl = ReqFactory.buildInterface(this, TopicListCallBack.class);
        TopicDetailReq req = new TopicDetailReq();
        req.setTopicId(topicId);
        d("III", "获取话题详情 " + topicId);
        reqTopic = false;
        BaseCall<MTopicResp> node = new BaseCall<MTopicResp>() {
            @Override
            public void call(MTopicResp resp) {
                reqTopic = true;
                if (reqTopic && reqPosts) {
                    ptr_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    topic = resp;
                    d("III", "获取话题成功 " + resp.getTopictitle());
                    setUIFromData(resp);
                    vg_loading.setVisibility(View.GONE);
                    vg_error.setVisibility(View.GONE);
                    ptr_scroll.setVisibility(View.VISIBLE);
                } else {
                    d("III", "获取话题失败 " + (resp != null ? resp.getMessage() : null));
                    vg_loading.setVisibility(View.GONE);
                    vg_error.setVisibility(View.VISIBLE);
                    ptr_scroll.setVisibility(View.GONE);
                }
            }
        };
        addCallback(node);
        tl.getTopicDetailByTypeAsync(req, node, type);
    }

    /**
     * 获取帖子列表
     * @param page
     * @param topicId
     * @param isRefresh
     */
    private void requestPostList(int page, long topicId, final boolean isRefresh, int type) {

        if (totalPage != -1 && page > totalPage && !isRefresh) {
            Log.i("III_logic", "当前页 " + page + ", 总页数 " + totalPage + ", 最后一页了，无法加载");
            Toast.makeText(mContext, R.string.detail_data_finish, Toast.LENGTH_SHORT).show();
            return;
        }

        GetPostList mDetail = ReqFactory.buildInterface(mContext, GetPostList.class);
        MPostReq req = new MPostReq();
        req.setTopicId(topicId);
        req.setPageNo(page);
        Log.i("III_logic", "获取话题帖子列表 page " + page + ", topicId " + topicId);
        reqPosts = false;
        BaseCall<TopicPostListResp> node = new BaseCall<TopicPostListResp>() {
            @Override
            public void call(TopicPostListResp resp) {
                reqPosts = true;
                if (reqTopic && reqPosts) {
                    ptr_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    if (resp.getPostLists() == null) {
                        return;
                    }
                    if (isRefresh) {
                        if (mPostAdapter.getList() != null) {
                            mPostAdapter.getList().clear();
                        }
                        mPostAdapter.notifyDataSetChanged();
                    }
                    if (resp.getTotalPage() <= resp.getPageNo()) {
                        ptr_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        ptr_scroll.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    // mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    for (int i = 0; i < resp.getPostLists().size(); i++) {
                        mPostAdapter.addItem(resp.getPostLists().get(i));
                    }
                    d("III", "获取帖子列表成功 ");
                    // mPostAdapter.list.addAll(resp);
                    // mPostAdapter.notifyDataSetChanged();
                } else {
                    d("III", "获取帖子列表失败 " + (resp != null ? resp.getMessage() : null));
                }
                // vg_waiting.setVisibility(View.GONE);
            }
        };
        addCallback(node);
        mDetail.getPostsOfOneTopicByTypeAsync(req, node, type);
    }

    /**
     * 关注话题
     * @param topicID
     * @param isFocus
     * @param clickView
     */
    private void requestFocusTopics(long topicID, final boolean isFocus, final View clickView, final TextView numView) {
        if(topicID == 0) {
            Toast.makeText(mContext, R.string.topic_please_choice_topics, Toast.LENGTH_SHORT).show();
            return;
        }
        clickView.setClickable(false);
        FollowupPostTopic fpt = ReqFactory.buildInterface(mContext, FollowupPostTopic.class);
        FollowupPostTopic.FollowupTopicReq req = new FollowupPostTopic.FollowupTopicReq();
        req.setTopicId(topicID);
        req.setFollowup(isFocus);
        d("III_logic", "topic id " + topicID + ", focus " + isFocus);
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    topic.setFollowsUpNum(topic.getFollowsUpNum() + (isFocus ? 1 : -1));
                    Toast.makeText(mContext, isFocus ?
                            R.string.found_detail_focus_success :
                            R.string.found_detail_cancel_success, Toast.LENGTH_SHORT).show();
                    topic.setFollowup(isFocus);
                    btn_join.setText(!isFocus ? R.string.found_detail_join : R.string.found_detail_unjoin);
                    tv_join.setText(getString(R.string.found_detail_what_join, "" + topic.getFollowsUpNum()));
                    d("III", "话题关系操作成功 " + isFocus + " ");
                } else {
                    Toast.makeText(mContext, isFocus ?
                            R.string.found_detail_focus_failure :
                            R.string.found_detail_focus_failure, Toast.LENGTH_SHORT).show();
                    d("III", "话题关系操作失败 " + isFocus + " " + (resp != null ? resp.getMessage() : null));
                }
                dimessWaitingDialog();
                clickView.setClickable(true);
            }
        };
        addCallback(node);
        fpt.followupTopicAsync(req, node);
    }

    private void requestTopicDelete(long topicId) {
        d("III", "delete topic " + topicId);
        DeletePostTopic dpt = ReqFactory.buildInterface(this, DeletePostTopic.class);
        DeleteTopicReq req = new DeleteTopicReq();
        req.setTopicId(topicId);
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    setResult(RESULT_FIRST_USER);
                    finish();
                    d("III", "删除成功");
                } else {
                    d("III", "删除失败 " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(), R.string.detail_delete_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(call);
        dpt.deleteTopicAsync(req, call);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<CustomScrollView> refreshView) {
        requestTopicDetail(topicId, GetPostList.GET_DATA_SERVER);
        requestPostList(1, topicId, true, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<CustomScrollView> refreshView) {
        requestPostList(currPage++, topicId, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        // d("Scroll", "top " + vg_float.getTop());
        if(y >= vg_float.getTop()) {
            if (tv_post_number.getParent() != vg_floating) {
                vg_float.removeView(tv_post_number);
                vg_floating.addView(tv_post_number);
                if (vg_floating.getVisibility() != View.VISIBLE) {
                    vg_floating.setVisibility(View.VISIBLE);
                }
            }
        }else{
            if (tv_post_number.getParent() != vg_float) {
                vg_floating.removeView(tv_post_number);
                vg_float.addView(tv_post_number);
                if (vg_floating.getVisibility() == View.VISIBLE) {
                    vg_floating.setVisibility(View.GONE);
                }
//                vg_float.getLayoutParams().height = vg_floating.getMeasuredHeight();
//                d("III", "measured height " + vg_float.getMeasuredHeight());
            }
        }
    }

    @Override
    public void scrollBottom() {

    }
}
