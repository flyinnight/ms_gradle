package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.AddPostViewCount;
import com.dilapp.radar.domain.AddPostViewCount.AddPostViewCountReq;
import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.Banner.DeleteBannerReq;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PostCollection;
import com.dilapp.radar.domain.PostCollection.DeleteCollectionReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionCommentScore;
import com.dilapp.radar.domain.SolutionCommentScore.CreatCommentReq;
import com.dilapp.radar.domain.SolutionCommentScore.LikeCommentReq;
import com.dilapp.radar.domain.SolutionCommentScore.UpdateScoreReq;
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionListData.Sol2ndCommentListResp;
import com.dilapp.radar.domain.SolutionListData.SolCommentList2ndReq;
import com.dilapp.radar.domain.SolutionListData.SolCommentListReq;
import com.dilapp.radar.domain.SolutionListData.SolCommentListResp;
import com.dilapp.radar.domain.SolutionListData.SolCommentResp;
import com.dilapp.radar.domain.SolutionOperate;
import com.dilapp.radar.domain.SolutionOperate.StoreupReq;
import com.dilapp.radar.domain.SolutionOperate.UseReq;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.admin.ActivityEditTopModel;
import com.dilapp.radar.ui.admin.TopItemParcel;
import com.dilapp.radar.util.DensityUtils;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MathUtils;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.view.CustomScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

/**
 * Created by husj1 on 2015/7/6.
 */
public class ActivityCarePlanDetail extends ActivityPostBase implements ActivityPostBase.PostBaseAdapter {
    private final static boolean LIKE_SUCCESS_TOAST = false;
    private final static boolean COLL_SUCCESS_TOAST = false;
    private TitleView mTitle;
    private ImageView iv_cover;
    private TextView tv_use;
    private TextView tv_coll;
    private TextView tv_score;
    private GridLayout vg_tags;
    private View v_cover_layer;
    private View v_cover_layer_title;
    private ViewGroup vg_eval_score;
    private TextView tv_curr_score;
    private Button btn_use;

    private boolean setDetail;
    private boolean requestDetail;
    private boolean requestComments;
    private SolutionDetailData sdd;
    private SolutionListData sld;
    private SolutionOperate sop;
    private SolutionCommentScore scs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan_detail);
        setConverter(this);
        setCommentFlag(Constants.EXTRA_SEND_COMMENT_FLAG_PLAN);
        initView();
        mBBSViewBuilder.setDividerDrawable(null);
        mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        tv_use = tv_like;
        tv_coll = tv_reply;
        iv_cover = findViewById_(R.id.iv_cover);
        tv_score = findViewById_(R.id.tv_score);
        vg_tags = findViewById_(R.id.vg_tags);
        v_cover_layer = findViewById_(R.id.v_cover_layer);
        v_cover_layer_title = findViewById_(R.id.v_cover_layer_title);
        vg_eval_score = findViewById_(R.id.vg_eval_score);
        tv_curr_score = findViewById_(R.id.tv_curr_score);
        btn_use = findViewById_(R.id.btn_use);

        sdd = ReqFactory.buildInterface(this, SolutionDetailData.class);
        sld = ReqFactory.buildInterface(this, SolutionListData.class);
        sop = ReqFactory.buildInterface(this, SolutionOperate.class);
        scs = ReqFactory.buildInterface(this, SolutionCommentScore.class);

        if (postMain.getId() == 0 && postMain.getLocalId() != 0) {
            // setUIFromData(postMain, 0);
        } else {
            requestPlanDetail(postID, GetPostList.GET_DATA_LOCAL);
            requestComments(postID, currPage, true, GetPostList.GET_DATA_LOCAL);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case TitleView.ID_RIGHT:
                onClickOption(v);
                break;
            case R.id.vg_collection: {
                if ((v.getTag() == null)) {
                    d("III_logic", "没有Post, 不能收藏操作");
                    break;
                }
                PostBaseEntity data = (PostBaseEntity) v.getTag();
                requestCollection(data, !data.isColl(),
                        (Drawable) v.getTag(R.id.vg_collection), v);
                break;
            }
            case R.id.btn_use: {
                if (postMain.isUse()) {
                    requestUsePlan(postMain, false, v);
                } else {
                    requestUsePlan(postMain, true, v);
                }
                break;
            }
            case R.id.btn_agree:
            case R.id.vg_like: {
                if ((v.getTag() == null)) {
                    d("III_logic", "没有Post, 不能点赞");
                    break;
                }
                PostBaseEntity data = (PostBaseEntity) v.getTag();
            /*
             * if (data.isLike()) { Toast.makeText(this, R.string.detail_liked,
			 * Toast.LENGTH_SHORT).show(); break; }
			 */
                requestLike(data, !data.isLike(),
                        (Drawable) v.getTag(R.id.vg_like), v,
                        (TextView) v.getTag(R.id.btn_agree));
                break;
            }
            case R.id.vg_reply_more: {
                PostBaseEntity parent = (PostBaseEntity) v.getTag();
                int page = Integer.parseInt(v.getTag(R.id.tv_more).toString());
                ViewGroup container = (ViewGroup) v.getTag(R.id.vg_reply_more);
                if (parent == null) {
                    w("III", "叫你爸过来领儿子。");
                    break;
                }
                requestReplyList(page + 1, parent.getCommentId(), v, v, container, GetPostList.GET_DATA_SERVER);
                break;
            }
            case R.id.btn_send: {
                PostBaseEntity data = (PostBaseEntity) v.getTag();
                ViewGroup container = (ViewGroup) v.getTag(R.id.btn_send);
                if (data == null || container == null) {
                    d("III_error", "回复错误，快看逻辑 你是不是删了一些代码呀？");
                    return;
                }
                String content = et_message.getText().toString().trim();
                if (content.equals("")) {
                    Toast.makeText(this, R.string.detail_input_content,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Toast.makeText(this, "功能暂未完善", Toast.LENGTH_SHORT).show();
                requestReply(content, data, container);
                break;
            }
            case R.id.btn_edit: {
                mOptionDialog.dismiss();
                Intent intent = new Intent(this, ActivityCarePlanEdit.class);
                intent.putExtra(Constants.EXTRA_EDIT_POST_IS_MODIFY, true);
                intent.putExtra(Constants.EXTRA_EDIT_POST_MODIFY_POST, mAdapter.to(postMain));
                startActivityForResult(intent, REQ_EDIT_POST);
                break;
            }
            case R.id.btn_delete: {
                mOptionDialog.dismiss();
                PostBaseEntity data = (PostBaseEntity) v.getTag();
                if (data == null) {
                    d("III_error", "没有数据你让我怎么删？");
                    return;
                }
                if (data.getPostLevel() == 0) {
                    mDeleteDialog.show();
                } else {
                    View removeView = (View) btn_delete.getTag(R.id.btn_delete);
                    requestDeleteCommentAndReply(data.getCommentId(), data.getPostLevel(), removeView);
                }
                break;
            }
            case R.id.btn_delete_enter: {
                mDeleteDialog.dismiss();
                PostBaseEntity data = (PostBaseEntity) btn_delete.getTag();
                requestDeletePlan(data.getId());
                break;
            }
            // add by kfir
            case R.id.btn_top: {
                mOptionDialog.dismiss();
                boolean top = v.getTag() instanceof Boolean ? Boolean
                        .parseBoolean(v.getTag().toString()) : false;
                if (top) {
                    requestTopRemove(postID, v);
                } else {
                    dispatchActivity(0);
                }
                break;
            }
            case R.id.btn_banner: {
                mOptionDialog.dismiss();
                int priority = v.getTag() instanceof Integer ? Integer.parseInt(v
                        .getTag().toString()) : 0;
                if (priority > 0) {
                    requestBannerRemove(priority, v);
                } else {
                    dispatchActivity(1);
                }
                break;
            }
            case R.id.btn_one:
            case R.id.btn_two:
            case R.id.btn_three:
            case R.id.btn_four:
            case R.id.btn_five: {
                int score = Integer.parseInt(v.getTag().toString());
                requestEvaluationScore(postMain.getId(), score);
                break;
            }
            case R.id.btn_next: {
                dimessEvaluation();
                break;
            }
        }
    }

    private void dispatchActivity(int type) {
        TopItemParcel topParcel = new TopItemParcel();
        topParcel.setType(type);
        topParcel.setSolutionId(postID);
        topParcel.setCover(postMain.getCover());
        Intent topIntent = new Intent(this, ActivityEditTopModel.class);
        topIntent.putExtra(Constants.EXTRA_EDIT_TOP_CONTENT, topParcel);
        startActivity(topIntent);
    }

    @Override
    protected void onBindView(int index, BBSDescribeItem item, View itemView) {
        super.onBindView(index, item, itemView);
        if (item.getType() == TopicHelper.TYPE_PLAN_STEP) {
            ((TextView) itemView.findViewById(R.id.tv_number))
                    .setText(getString(R.string.plan_step_num, (index + 1) + ""));
        }
    }

	/*@Override
    public boolean onLongClick(View v) {
		return super.onLongClick(v);
	}*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_EDIT_POST: {
                if (resultCode == RESULT_OK) {
                    requestPlanDetail(postID, GetPostList.GET_DATA_SERVER);
                }
                break;
            }
        }
    }

    private void showEvaluation() {
        if (v_cover_layer.getVisibility() != View.VISIBLE) {
            v_cover_layer.setVisibility(View.VISIBLE);
            v_cover_layer.startAnimation(fadeIn);
        }
        if (v_cover_layer_title.getVisibility() != View.VISIBLE) {
            v_cover_layer_title.setVisibility(View.VISIBLE);
            v_cover_layer_title.startAnimation(fadeIn);
        }
        if (vg_eval_score.getVisibility() != View.VISIBLE) {
            vg_eval_score.setVisibility(View.VISIBLE);
            vg_eval_score.startAnimation(fromBottonIn);
        }
    }

    private void dimessEvaluation() {
        if (v_cover_layer.getVisibility() != View.GONE) {
            v_cover_layer.setVisibility(View.GONE);
            v_cover_layer.startAnimation(fadeOut);
        }
        if (v_cover_layer_title.getVisibility() != View.GONE) {
            v_cover_layer_title.setVisibility(View.GONE);
            v_cover_layer_title.startAnimation(fadeOut);
        }
        if (vg_eval_score.getVisibility() != View.GONE) {
            vg_eval_score.setVisibility(View.GONE);
            vg_eval_score.startAnimation(fromBottomOut);
        }
    }

    private void requestPlanDetail(final long planId, int type) {
        if (planId == 0) {
            osv_scroll.onRefreshComplete();
            d("III_request", " planId 为0，无法执行");
            return;
        }
        setDetail = false;
        requestDetail = false;
        BaseCall<MSolutionResp> call = new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                requestDetail = true;
                if (requestDetail && requestComments) {
                    osv_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {
                    setDetail = true;
                    int count = postMain.getCommentCount();
                    postMain = mAdapter.from(resp);
                    if (setDetail && requestDetail && requestComments) {
                        d("III_request", "list count " + count);
                        postMain.setCommentCount(count);
                        setUIFromData(postMain, 0);
                    }
                } else {
                    w("III_data", "详情查询失败 "
                            + (resp != null ? resp.getMessage() : "null"));
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        addCallback(call);
        sdd.getSolutionDetailDataAsync(planId, call/*, type*/);
    }

    /**
     * 请求数据
     *
     * @param postId 帖子ID
     * @param page   第几页
     * @param clear  是否清楚老数据
     */
    private void requestComments(final long postId, int page, final boolean clear, int type) {
        if (postId == 0) {
            d("III_logic", " postId 为0，无法执行");
            return;
        }
        if (totalPage != -1 && page > totalPage && !clear) {
            d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
                    + ", 最后一页了，无法加载");
            Toast.makeText(this, R.string.detail_data_finish,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        setDetail = false;
        requestComments = false;
        SolCommentListReq bean = new SolCommentListReq();
        bean.setPageNo(page);
        bean.setSolutionId(postId);
        d("III_logic", " load datas page " + page + " postId " + postId
                + ", token " + HttpConstant.TOKEN);
        BaseCall<SolCommentListResp> node = new BaseCall<SolCommentListResp>() {
            @Override
            public void call(SolCommentListResp resp) {
                requestComments = true;
                if (requestComments && requestDetail) {
                    osv_scroll.onRefreshComplete();
                }
                if (resp != null && resp.isRequestSuccess()) {

                    if (clear) {
                        resetDatas();
                    }
                    d("III_request", "list count " + resp.getTotalCount());
                    postMain.setCommentCount(resp.getTotalCount());

                    if (setDetail && requestDetail && requestComments) {
                        setUIFromData(postMain, 0);
                    }

                    if (resp.getTotalPage() <= resp.getPageNo()) {// 数据拉完了
                        vg_comment_end.setVisibility(View.VISIBLE);
                        osv_scroll.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {// 还有数据可拉
                        vg_comment_end.setVisibility(View.GONE);
                        osv_scroll.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    if (resp.getDatas() != null && resp.getDatas().size() > 0) {
                        // datas.addAll(resp.getResp());
                        d("III_data",
                                "查询成功 " + resp.getPageNo() + ", "
                                        + resp.getTotalPage() + ", "
                                        + resp.getDatas().size());
                        for (int i = 0; i < resp.getDatas().size(); i++) {
                            SolCommentResp b = resp.getDatas().get(i);
                            addComment(vg_comments, false, mAdapter.from(b));
                        }
                    } else {
                        d("III_data", "查询成功 但是没有数据，艹");
                    }
                    // 一条评论都没有
                    if (resp.getPageNo() <= 1
                            && (resp.getDatas() == null || resp.getDatas().size() <= 0)) {
                        vg_comment_end.setVisibility(View.GONE);
                        tv_sofa.setVisibility(View.VISIBLE);
                    } else {
                        tv_sofa.setVisibility(View.GONE);
                    }
                    /*if (!addBrowseCount) {
                        requestAddBrowseCount(postId);
                        addBrowseCount = true;
                    }*/
                } else {
                    w("III_data", "列表查询失败 "
                            + (resp != null ? resp.getMessage() : "null"));
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        addCallback(node);
        sld.getSolutionCommentListByTypeAsync(bean, node, type);
        // inter.getPostDetailsByTypeAsync(bean, node, type);
    }

    /**
     * 请求回复帖子
     *
     * @param content   回复的内容
     * @param data      回复的对象
     * @param container 回复成功后,添加回复的容器
     */
    private void requestReply(final String content, final PostBaseEntity data,
                              final ViewGroup container) {
        final Context context = getApplicationContext();
        // 准备请求参数
        CreatCommentReq req = new CreatCommentReq();
        req.setContent(content);
        req.setParentCommId(data.getCommentId());
        req.setSolutionId(postMain.getId());
        /*if (data.getPostLevel() == 1) {
        } else */
        if (data.getPostLevel() == 2) {
            req.setParentCommId(data.getCommentParentId());
            req.setToUserId(data.getUserId());
            // req.setToUserName(data.getUserName());
        }
        d("III_data", "id " + req.getParentCommId() + ", toUserId " + data.getUserId());
        UmengUtils.onEventPostReply(this, "" + data.getTopicId());
        BaseCall<MSolutionResp> node = new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    resp.setContent(content);
                    resp.setNickName(SharePreCacheHelper.getNickName(context));
                    if (data.getPostLevel() == 2) {
                        resp.setToUserId(data.getUserId());
                        resp.setToNickName(data.getNickname());
                    }
                    if (data.getPostLevel() == 1) {
                        List<PostBaseEntity> comms = data.getComments();
                        if (comms == null) {
                            comms = new LinkedList<PostBaseEntity>();
                            data.setComments(comms);
                        }
                        comms.add(mAdapter.from(resp));
                    }

                    // 数据拿到后清空按钮上的数据
                    et_message.setText("");
                    btn_send.setTag(null);
                    btn_send.setTag(R.id.btn_send, null);
                    hideReply();// 隐藏回复栏
                    addReply(mAdapter.from(resp), false, 0, container);
                    // 这个0要改掉
                    Toast.makeText(context, R.string.detail_reply_success,
                            Toast.LENGTH_SHORT).show();
                } else {
                    d("III_data", "msg->"
                            + ((resp != null) ? resp.getMessage() : "null"));
                    Toast.makeText(context, R.string.detail_reply_failure,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(node);

        scs.solutionCreatCommentsAsync(req, node);
    }

    /**
     * 请求收藏
     *
     * @param data     需要收藏的帖子
     * @param isColl   true为收藏，false为取消收藏
     * @param drawable 需要改变样式的Drawable
     * @param view     触发该事件的View，最好给我，否则用户会重复点，我可以去重复哦
     */
    private void requestCollection(final PostBaseEntity data, final boolean isColl,
                                   final Drawable drawable, final View view) {
        StoreupReq req = new StoreupReq();
        req.setIsStoreup(isColl);
        req.setSolutionId(data.getId());
        d("III_logic", "收藏 planId " + data.getId() + ", is " + isColl);
        if (view != null) {
            view.setClickable(false);
        }
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (drawable != null) {
                        drawable.setLevel(isColl ? 1 : 0);
                    }
                    data.setColl(isColl);

                    if (COLL_SUCCESS_TOAST) {
                        Toast.makeText(
                                getApplicationContext(),
                                isColl ? R.string.detail_collection_success
                                        : R.string.detail_discollection_success,
                                Toast.LENGTH_SHORT).show();
                    }
                    // 发送广播更新UI数据
                    Intent intent = new Intent(Constants.FOUND_TOPIC_COLLECT);
                    context.sendBroadcast(intent);
                    /*if (data.isSelectedToSolution()) {
						requestCollectionPlan(data.getId(), isColl);
					}*/
                } else {
                    if (drawable != null) {
                        drawable.setLevel(!isColl ? 1 : 0);
                    }
                    // data.setStoreUp(!isColl);
                    w("III_data", "收藏失败 "
                            + (resp != null ? resp.getMessage() : "null"));
                    Toast.makeText(
                            getApplicationContext(),
                            isColl ? R.string.detail_collection_failure
                                    : R.string.detail_discollection_failure,
                            Toast.LENGTH_SHORT).show();
                }

                if (view != null) {
                    view.setClickable(true);
                }
            }
        };
        addCallback(node);
        sop.solutionStoreupAsync(req, node);
        // cp.storeupPostAsync(req, node);
    }

    /**
     * 请求点赞
     *
     * @param data     请求点赞帖子
     * @param isLike   点赞，一般为true
     * @param drawable 需要改变样式的Drawable
     * @param view     触发该事件的View，最好给我，否则用户会重复点，我可以去重复哦
     * @param tv       需要显示点赞数据的TextView，我可以在点赞成功后帮你+1哦
     */
    private void requestLike(final PostBaseEntity data, final boolean isLike,
                             final Drawable drawable, final View view, final TextView tv) {

        LikeCommentReq req = new LikeCommentReq();
        req.setCommentId(data.getCommentId());
        req.setIsLike(isLike);
        d("III_logic", "点赞 postId " + data.getId() + ", like " + isLike);

        if (view != null) {
            view.setClickable(false);
        }
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (drawable != null) {
                        drawable.setLevel(isLike ? 1 : 0);
                    }
                    if (tv != null) {
                        try {
                            tv.setText(""
                                    + (Integer
                                    .parseInt(tv.getText().toString()) + (isLike ? 1
                                    : -1)));
                        } catch (Exception e) {
                        }
                    }
                    data.setLikeCount(data.getLikeCount() + (isLike ? 1 : -1));
                    data.setLike(isLike);
                    if (isLike) {
                        view.startAnimation(AnimationUtils.loadAnimation(
                                getApplicationContext(), R.anim.like_post));
                    }
                    // post.setLike(isColl);
                    if (LIKE_SUCCESS_TOAST) {
                        Toast.makeText(
                                getApplicationContext(),
                                isLike ? R.string.detail_like_success
                                        : R.string.detail_cancel_success,
                                Toast.LENGTH_SHORT).show();
                    }
                    // 点赞成功的话，按钮就不让点了
                } else {
                    if (drawable != null) {
                        drawable.setLevel(!isLike ? 1 : 0);
                    }
                    // post.setLike(!isColl);
                    w("III_data", "操作失败 "
                            + (resp != null ? resp.getMessage() : "null"));
                    Toast.makeText(
                            getApplicationContext(),
                            isLike ? R.string.detail_like_failure
                                    : R.string.detail_cancel_failure,
                            Toast.LENGTH_SHORT).show();
                }
                if (view != null) {
                    view.setClickable(true);
                }

            }
        };
        addCallback(node);
        scs.solutionLikeCommentAsync(req, node);
    }

    private void requestDeletePlan(final long planId) {
        showWaitingDialog((AsyncTask) null);
        d("III_logic", "delete plan id " + planId);
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    d("III_logic", "删除成功");
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_delete_success, Toast.LENGTH_SHORT)
                            .show();
                    Intent data = new Intent();
                    data.putExtra(Constants.RESULT_POST_DETAIL_DELETE_ID, planId);
                    setResult(RESULT_FIRST_USER, data);
                    finish();
                } else {
                    d("III_logic", "删除失败 msg-> " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_delete_failure, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        addCallback(node);
        sop.solutionDeleteAsync(planId, node);
    }

    private void requestDeleteCommentAndReply(long id, final int postLevel,
                                              final View removeView) {
        showWaitingDialog((AsyncTask) null);
        d("III_logic", "delete comment or reply id " + id);
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    if (removeView != null && removeView.getParent() != null) {
                        ((ViewGroup) removeView.getParent())
                                .removeView(removeView);
                    }
                    if (postLevel == 1) {
                        // 总评论数 - 1
                        postMain.setCommentCount(postMain.getCommentCount() - 1);
                        tv_total_comment.setText(
                                getString(R.string.detail_what_total_reply,
                                        postMain.getCommentCount() + ""));
                        tv_reply.setText(postMain.getCommentCount() + "");

                        if (vg_comments.getChildCount() == 0) {
                            if (tv_sofa.getVisibility() != View.VISIBLE) {
                                tv_sofa.setVisibility(View.VISIBLE);
                            }
                            if (vg_comment_end.getVisibility() != View.GONE) {
                                vg_comment_end.setVisibility(View.GONE);
                            }
                        }
                    }
                    d("III_reqest", "删除成功");
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_delete_success, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    d("III_reqest", "删除失败 msg->" + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_delete_failure, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        addCallback(call);
        scs.solutionDeleteCommentAsync(id, call);
    }

    private void requestAddBrowseCount(long postId) {
        AddPostViewCount apvc = ReqFactory.buildInterface(this,
                AddPostViewCount.class);
        AddPostViewCountReq req = new AddPostViewCountReq();
        req.setPostId(postId);
        req.setViewCount(1);
        apvc.addPostViewCountAsync(req, null);
    }

    /**
     * 请求回复列表
     *
     * @param page      第几页
     * @param post      父贴的ID
     * @param click     触发此请求的按钮
     * @param more      “显示更多”控件
     * @param container 容器
     */
    private void requestReplyList(int page, long post, final View click,
                                  final View more, final ViewGroup container, int type) {
        if (post == 0) {
            d("III_logic", " postId 为0，无法执行");
            return;
        }
        int totalPage = Integer.parseInt(more.getTag(R.id.vg_loading)
                .toString());
        if (totalPage != -1 && page > totalPage) {
            d("III_logic", "当前页 " + page + ", 总页数 " + totalPage
                    + ", 最后一页了，无法加载");
            Toast.makeText(this, R.string.detail_data_finish,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        SolCommentList2ndReq req = new SolCommentList2ndReq();
        req.setPageNo(page);
        req.setCommentId(post);

        // 显示加载
        more.findViewById(R.id.vg_loading).setVisibility(View.VISIBLE);
        more.findViewById(R.id.tv_more).setVisibility(View.GONE);
        if (click != null) {
            click.setClickable(false);
        }
        d("III", "replys " + post + ", page " + page);
        BaseCall<Sol2ndCommentListResp> node = new BaseCall<Sol2ndCommentListResp>() {
            @Override
            public void call(Sol2ndCommentListResp resp) {
                more.findViewById(R.id.vg_loading).setVisibility(View.GONE);
                more.findViewById(R.id.tv_more).setVisibility(View.VISIBLE);
                if (click != null) {
                    click.setClickable(true);
                }
                if (resp != null && resp.isRequestSuccess()) {
                    if (more.getParent() != null) {
                        ((ViewGroup) more.getParent()).removeView(more);
                    }
                    if (resp.getPageNo() == 1) {// 当前页为 1，把View清楚一遍
                        container.removeAllViews();
                    }
                    more.setTag(R.id.tv_more, resp.getPageNo());// 当前的页数
                    more.setTag(R.id.vg_loading, resp.getTotalPage());// 总页数
                    // more
                    d("III", "获取列表成功 "
                            + (resp.getDatas() != null ? resp.getDatas()
                            .size() : 0) + ", " + resp.getPageNo()
                            + "/" + resp.getTotalPage());
                    if (resp.getDatas() != null) {
                        final int size = resp.getDatas().size();
                        for (int i = 0; i < size; i++) {
                            MSolutionResp reply = resp.getDatas().get(i);
                            if (reply == null)
                                continue;
                            addReply(mAdapter.from(reply), true, -1, container);
                        }
                        if (resp.getPageNo() < resp.getTotalPage()) {
                            // 不是最后一页
                            container.addView(more);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.detail_get_data_filure, Toast.LENGTH_SHORT)
                            .show();
                    d("III", "获取列表失败");
                }
            }
        };
        addCallback(node);

        sld.getSolution2ndCommentListAsync(req, node);
    }

    private void requestEvaluationScore(long planId, int score) {
        UpdateScoreReq req = new UpdateScoreReq();
        req.setSolutionId(planId);
        req.setScore(score);
        showWaitingDialog((AsyncTask) null);
        d("III_request", "评分 " + planId + " -> " + score);
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    dimessEvaluation();

                } else {
                    d("III_request", "评分失败 msg->" + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        scs.solutionUpdateScoreAsync(req, call);
    }

    private void requestUsePlan(final PostBaseEntity data, final boolean isUse, final View click) {

        Object o = click.getTag(R.id.btn_add);
        if (click != null && o != null && Boolean.parseBoolean(o.toString())) {
            d("III_request", "正在请求，请稍后..");
            return;
        }

        if (click != null) {
            click.setTag(R.id.btn_add, true);
        }
        UseReq req = new UseReq();
        req.setSolutionId(data.getId());
        req.setIsUse(isUse);
        d("III_request", "use plan id " + req.getSolutionId() + ", use " + req.getIsUse());
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (click != null) {
                    click.setTag(R.id.btn_add, false);
                }
                if (resp != null && resp.isRequestSuccess()) {
                    data.setUse(isUse);
                    btn_use.setText(isUse ? R.string.used : R.string.start_use);
                    if (!isUse) {
                        showEvaluation();
                    }
                    d("III_request", "request use success. " + isUse);
                } else {
                    d("III_request", "request use failre.msg->" + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(), "Use failed! Check network please.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(call);
        showWaitingDialog((AsyncTask) null);
        sop.solutionUseAsync(req, call);
    }

    private void requestTopRemove(long planId, final View top) {
        if (planId <= 0) {
            w("III_request", "planId " + planId + ", 你这是几个意思?");
            return;
        }

        showWaitingDialog((AsyncTask) null);
        PostCollection pc = ReqFactory.buildInterface(this,
                PostCollection.class);

        DeleteCollectionReq dcr = new DeleteCollectionReq();
        dcr.setSolutionId(planId);
        d("III_request", "移除精选贴 planId " + planId);
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    if (top != null) {
                        top.setTag(false);
                        if (top instanceof TextView) {
                            ((TextView) top).setText(R.string.edit_post_top);
                        }
                    }
                    setResult(RESULT_FIRST_USER, getIntent());
                    finish();
                    Toast.makeText(ActivityCarePlanDetail.this,
                            R.string.edit_post_remove_success,
                            Toast.LENGTH_SHORT).show();
                    d("III_request", "移除精选成功");
                } else {
                    Toast.makeText(
                            ActivityCarePlanDetail.this,
                            getString(R.string.edit_post_remove_failure)
                                    + " msg "
                                    + (resp != null ? resp.getMessage() : null),
                            Toast.LENGTH_SHORT).show();
                    w("III_request",
                            "移除精选失败 "
                                    + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        pc.deletePostCollectionAsync(dcr, call);
    }

    private void requestBannerRemove(final int priority, final View banner) {
        if (priority <= 0) {
            w("III_request", "priority " + priority + ", 你这是几个意思?");
            return;
        }
        Banner b = ReqFactory.buildInterface(this, Banner.class);
        DeleteBannerReq dbr = new DeleteBannerReq();
        dbr.setPriority(priority);
        d("III_request", "移除Banner priority " + priority);
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (banner != null) {
                        banner.setTag(0);
                        if (banner instanceof TextView) {
                            ((TextView) banner)
                                    .setText(R.string.edit_post_banner);
                        }
                    }
                    setResult(RESULT_FIRST_USER, getIntent());
                    finish();
                    d("III_request", "Banner 移除成功");
                    Toast.makeText(
                            ActivityCarePlanDetail.this,
                            getString(R.string.edit_post_remove_success)
                                    + "priority " + priority,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            ActivityCarePlanDetail.this,
                            getString(R.string.edit_post_remove_failure)
                                    + " msg "
                                    + (resp != null ? resp.getMessage() : null),
                            Toast.LENGTH_SHORT).show();
                    w("III_request",
                            "Banner 移除失败 "
                                    + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        b.deleteBannerAsync(dbr, call);

    }

    /**
     * 设置用户信息以及主贴内容
     *
     * @param data   数据
     * @param status 暂时没卵用
     */
    @Override
    protected void setUIFromData(PostBaseEntity data, int status) {
        if (data == null) {
            vg_loading.setVisibility(View.GONE);
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }
        super.setUIFromData((data), status);


        String title = data.getTitle() == null ? "unknown" : data.getTitle();
        mTitle.setCenterText(title, null);
        if (isPreview()) {
            mTitle.setRightIcon(null, this);
        } else if (mAdapter.canDelete(mAdapter.to(data))) {
            mTitle.setRightIcon(R.drawable.btn_more, this);
        }
        int pathType = TopicHelper.isImagePath(data.getCover());
        if (pathType != TopicHelper.PATH_UNKNOWN) {
            if (pathType == TopicHelper.PATH_LOCAL_SDCARD) {
                ImageLoader.getInstance().displayImage(
                        ("file://" + data.getCover()), iv_cover, options);
            } else {
                ImageLoader.getInstance().displayImage(
                        TopicHelper.wrappeImagePath(data.getCover()), iv_cover, options);
            }
        }
        tv_score.setText(data.getScore() + "");
        tv_curr_score.setText(data.getScore() + "");
        int start = 0;
        int total = (data.getEffects() != null ? data.getEffects().length : 0) +
                (data.getParts() != null ? data.getParts().length : 0);
        vg_tags.removeAllViews();
        addTags(Constants.PLAN_EFFECTS, data.getEffects(), vg_tags, start, total);
        start = (data.getEffects() != null ? data.getEffects().length : 0);
        addTags(Constants.PLAN_PARTS, data.getParts(), vg_tags, start, total);

        btn_use.setText(data.isUse() ? R.string.used : R.string.start_use);

        String used = context.getString(R.string.plan_used, data.getUseCount() + "");
        String coll = context.getString(R.string.plan_coll, data.getCollCount() + "");
        tv_use.setText(used);
        tv_coll.setText(coll);
    }

    private void addTags(SparseArray<String> maps, String[] arrays, GridLayout vg, int start, int total) {

        String log = "";
        if (arrays != null) {
            LayoutInflater inflater = getLayoutInflater();
            int col = vg.getColumnCount();
            for (int i = 0; i < arrays.length; i++) {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                //d("III", "right: (" + i + " + " + start + " + 1) % " + col + " = " + ((i + start + 1) % col));
                if ((i + start + 1) % col != 0) {
                    params.rightMargin = DensityUtils.dip2px(this, 8);
                }
                //d("III", "bottom: " + total + " - " + total + " % " + col + " = " + (total - total % col));
                if (total - total % col >= i + start + 1) {
                    params.bottomMargin = DensityUtils.dip2px(this, 8);
                }
                if (arrays[i] != null && !"".equals(arrays[i].trim())) {
                    View view = inflater.inflate(R.layout.item_plan_detail_tags, null);
                    vg.addView(view);
                    view.setLayoutParams(params);
                    TextView tv_tag = (TextView) view.findViewById(R.id.tv_tag);
                    int index = -1;
                    for (int j = 0; j < maps.size(); j++) {
                        if (arrays[i].equals(maps.valueAt(j))) {
                            index = /*Constants.PLAN_PARTS.keyAt(*/j;
                            break;
                        }
                    }
                    if (index != -1) {
                        tv_tag.setText(maps.keyAt(index));
                    }
                    log += arrays[i] + ", ";
                    // log += parts[i] + ", ";
                }
            }
        }
        d("III", "arrays ->" + log);
    }

    @Override
    public void onPullDownToRefresh(
            PullToRefreshBase<CustomScrollView> refreshView) {
        d("III_logic", "下拉触发，开始流程,代号： down");
        requestPlanDetail(postID, GetPostList.GET_DATA_SERVER);
        requestComments(postID, 1, true, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onPullUpToRefresh(
            PullToRefreshBase<CustomScrollView> refreshView) {
        d("III_logic", "上拉触发，开始流程,代号： up");
        requestPlanDetail(postID, GetPostList.GET_DATA_SERVER);
        requestComments(postID, currPage, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public PostBaseEntity from(Serializable o) {
        MSolutionResp resp = (MSolutionResp) o;
        if (resp == null) return null;

        PostBaseEntity entity = new PostBaseEntity();
        entity.setId(resp.getSolutionId());
        entity.setLocalId(resp.getLocalSolutionId());
        entity.setCommentId(resp.getCommentId());
        entity.setCommentParentId(resp.getParentCommId());
        entity.setUserId(resp.getUserId());
        // entity.setEmUserId(resp.getEMUserId());
        entity.setGender(resp.getGender());
        entity.setUserLevel(resp.getLevel());
        entity.setHeadUrl(resp.getPortrait());
        entity.setNickname(resp.getNickName());
        // entity.setFocusUser(resp.isFollowsUser());
        entity.setToUserId(resp.getToUserId());
        entity.setToNickname(resp.getToNickName());
        entity.setCover(resp.getCoverImgUrl());
        entity.setTitle(resp.getTitle());
        entity.setIntroduction(resp.getIntroduction());
        entity.setContent(resp.getContent());
        // entity.setTopicTitle(resp.getTopicTitle());
        // entity.setPostLevel(resp.getPostLevel());
        entity.setUseCount(resp.getUsedCount());
        entity.setLikeCount(resp.getLikeCount());
        entity.setCollCount(resp.getStoreUpCount());
        // entity.setBrowseCount(resp.getPostViewCount());
        // entity.setCommentCount(resp.getTotalFollows());
        // entity.setTopicId(resp.getTopicId());
        entity.setCreateTime(resp.getCreateTime());
        entity.setUpdateTime(resp.getUpdateTime());
        entity.setUse(resp.getInUse());
        entity.setColl(resp.getIsStoreup());
        entity.setLike(resp.getIsLike());
        entity.setScore((float) MathUtils.round(resp.getScore(), 1));
        entity.setEffects(resp.getEffect());
        entity.setParts(resp.getPart());

        if (resp instanceof SolCommentResp) {
            SolCommentResp fresp = (SolCommentResp) resp;
            List<MSolutionResp> comms = fresp.getFollowComments();
            if (fresp.getHasMore()) {
                entity.setCommentCount(comms.size() + 1);
            }
            if (comms != null && comms.size() > 0) {
                List<PostBaseEntity> entites = new ArrayList<PostBaseEntity>(comms.size());
                for (int i = 0; i < comms.size(); i++) {
                    entites.add(from(comms.get(i)));
                }
                entity.setComments(entites);
            }
        }
        return entity;
    }

    @Override
    public Serializable to(PostBaseEntity entity) {

        if (entity == null) return null;
        MSolutionResp resp = entity.getComments() != null ? new SolCommentResp() : new MSolutionResp();
        resp.setSolutionId(entity.getId());
        resp.setLocalSolutionId(entity.getLocalId());
        resp.setCommentId(entity.getCommentId());
        resp.setParentCommId(entity.getCommentParentId());
        resp.setUserId(entity.getUserId());
        // resp.setEMUserId(entity.getEmUserId());
        resp.setGender(entity.getGender());
        resp.setLevel(entity.getUserLevel());
        resp.setPortrait(entity.getHeadUrl());
        resp.setNickName(entity.getNickname());
        // resp.setFollowsUser(entity.isFocusUser());
        resp.setToUserId(entity.getToUserId());
        resp.setToNickName(entity.getToNickname());
        resp.setCoverImgUrl(entity.getCover());
        resp.setTitle(entity.getTitle());
        resp.setIntroduction(entity.getIntroduction());
        resp.setContent(entity.getContent());
        // resp.setTopicTitle(entity.getTopicTitle());
        // resp.setPostLevel(entity.getPostLevel());
        resp.setUsedCount(entity.getUseCount());
        resp.setLikeCount(entity.getLikeCount());
        resp.setStoreUpCount(entity.getCollCount());
        // resp.setPostViewCount(entity.getBrowseCount());
        // resp.setTotalFollows(entity.getCommentCount());
        // resp.setTopicId(entity.getTopicId());
        resp.setCreateTime(entity.getCreateTime());
        resp.setUpdateTime(entity.getUpdateTime());
        resp.setInUse(entity.isUse());
        resp.setIsStoreup(entity.isColl());
        resp.setIsLike(entity.isLike());
        resp.setScore(entity.getScore());
        resp.setEffect(entity.getEffects());
        resp.setPart(entity.getParts());

        if (resp instanceof SolCommentResp) {
            SolCommentResp fresp = (SolCommentResp) resp;
            List<PostBaseEntity> entities = entity.getComments();
            if (entities != null && entities.size() > 0) {
                if (entities.size() > 3) {
                    fresp.setHasMore(true);
                }
                List<MSolutionResp> posts = new ArrayList<MSolutionResp>(entities.size());
                for (int i = 0; i < entities.size(); i++) {
                    posts.add((MSolutionResp) to(entities.get(i)));
                }
                fresp.setFollowComments(posts);
            }
        }
        return resp;
    }

    @Override
    public boolean canDelete(Serializable o) {
        return Permissions.canPlanDelete(this, (MSolutionResp) o);
    }

    @Override
    public boolean canModify(Serializable o) {
        return Permissions.canPlanModify(this, (MSolutionResp) o);
    }
}
