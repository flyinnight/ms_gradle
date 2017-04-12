package com.dilapp.radar.ui.topic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionCreateUpdate.*;
import com.dilapp.radar.domain.SolutionDetailData.*;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionListData.*;
import com.dilapp.radar.textbuilder.utils.JsonUtils;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.Permissions;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.topic.PostAdapter.PostAdapterListener;
import com.dilapp.radar.view.CheckableFrameLayout;
import com.dilapp.radar.ui.topic.EffectRadioGroup.OnCheckedChangeListener;
import com.dilapp.radar.view.EmptyView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class ActivityCarePlanList extends BaseActivity
        implements View.OnClickListener, OnCheckedChangeListener, OnRefreshListener2<ListView>,
        AdapterView.OnItemClickListener, PostAdapterListener {

    private static final int REQ_CARE_PLAN_RELEASE = 1000;
    private static final int REQ_CARE_PLAN_DETAIL = 2000;
    private static final String TAG_ALL = "";
    private Animation fadeIn, fadeOut;
    private TitleView mTitle;
    private EffectRadioGroup vg_effects;
    private View vg_loading;
    private EmptyView ev_empty;
    private EmptyView ev_error;
    private PullToRefreshListView ptr_plans;

    private SolutionListData sld;
    private PlanAdapter mPlanAdapter;
    private String currTag;
    private int currPage = 1;
    private int totalPage = -1;

    private boolean isReceiver = false;
    private BroadcastReceiver planReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!SolutionCreateUpdate.SOLUTION_RELEASE_END.equals(intent.getAction())) return;

            MSolutionResp resp = (MSolutionResp) intent.getSerializableExtra("RespData");
            int index = -1;
            List<MSolutionResp> list = mPlanAdapter.getList();
            final int size = list.size();
            for (int i = 0; i < size; i++) {
                MSolutionResp post = list.get(i);
                if (post.getLocalSolutionId() != 0 && post.getLocalSolutionId() == resp.getLocalSolutionId()) {
                    index = i;
                    break;
                }
            }
            d("III", "index " + index + ", receiver " + JsonUtils.toJson(resp));
            if (index == -1) {
                return;
            }
            if (resp.getSendState() == PostReleaseCallBack.POST_RELEASE_SENDSUCCESS) {
                resp.setLocalSolutionId(0);
                list.set(index, resp);
            } else {
                list.get(index).setSendState(resp.getSendState());
            }
            mPlanAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_plan_list);
        sld = ReqFactory.buildInterface(this, SolutionListData.class);
        fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        mTitle.setCenterText(R.string.found_skin_plan, null);
        if (Permissions.canPlanRelease(this)) {
            mTitle.setRightIcon(R.drawable.btn_release, this);
        }
        vg_effects = findViewById_(R.id.vg_effects);
        vg_loading = findViewById_(R.id.vg_loading);
        ev_empty = findViewById_(R.id.ev_empty);
        ev_error = findViewById_(R.id.ev_error);
        ptr_plans = findViewById_(R.id.ptr_plans);

        ev_empty.setText(R.string.empty_data);
        ev_error.setText(R.string.empty_data);
        vg_effects.setOnCheckedChangeListener(this);
        ptr_plans.setOnRefreshListener(this);
        ptr_plans.setAdapter(mPlanAdapter = new PlanAdapter(this, getLayoutInflater()));
        ptr_plans.setOnItemClickListener(this);
        mPlanAdapter.setPostAdapterListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
        registerReceiver(planReceiver, filter);
        isReceiver = true;

        initEffectViews();
    }

    private void initEffectViews() {
        EffectAdapter ea = new EffectAdapter(this, getLayoutInflater());
        int size = ea.effects.size();
        int col = size / 2 + (size % 2 == 0 ? 0 : 1);
        vg_effects.setColumnCount(col);
        d("III", "NumColumns " + (size / 2 + (size % 2 == 0 ? 0 : 1)));
        Resources res = getResources();
        int rightMargin = res.getDimensionPixelSize(R.dimen.topic_plan_effect_hdistance);
        int bottomMargin = res.getDimensionPixelSize(R.dimen.topic_plan_effect_vdistance);
        for (int i = 0; i < ea.getCount(); i++) {
            View view = ea.getView(i, null, null);
            view.setId(View.generateViewId());
            LayoutParams params = new LayoutParams();
            if ((i + 1) % col != 0) {
                params.rightMargin = rightMargin;
            }
            if (size - i > size % col) {
                params.bottomMargin = bottomMargin;
            }
            d("III", "position " + i +
                    ", right " + params.rightMargin +
                    ", bottom " + params.bottomMargin);
            vg_effects.addView(view, params);
        }
        ((CheckableFrameLayout) vg_effects.getChildAt(0)).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case TitleView.ID_RIGHT:
                Intent intent = new Intent(this, ActivityCarePlanEdit.class);
                startActivityForResult(intent, REQ_CARE_PLAN_RELEASE);
                break;
            default:
                break;
        }
    }

    private SparseArray<String> genEffects() {
        SparseArray<String> effects = Constants.PLAN_EFFECTS.clone();
        effects.put(-1, getString(R.string.plan_effect_all));
        return effects;
    }

    private void switchUI(int loadVis, int emptyVis, int errorVis, int listVis) {
        if (vg_loading.getVisibility() != loadVis) {
            vg_loading.setVisibility(loadVis);
            /*if (loadVis != View.VISIBLE) {
                vg_loading.startAnimation(fadeOut);
            } else {
                vg_loading.startAnimation(fadeIn);
            }*/
        }
        if (ev_empty.getVisibility() != emptyVis) {
            ev_empty.setVisibility(emptyVis);
            if (loadVis != View.VISIBLE) {
                // ev_empty.startAnimation(fadeOut);
            } else {
                ev_empty.startAnimation(fadeIn);
            }
        }
        if (ev_error.getVisibility() != errorVis) {
            ev_error.setVisibility(errorVis);
            if (loadVis != View.VISIBLE) {
                // ev_error.startAnimation(fadeOut);
            } else {
                ev_error.startAnimation(fadeIn);
            }
        }
        if (ptr_plans.getVisibility() != listVis) {
            ptr_plans.setVisibility(listVis);
            if (loadVis != View.VISIBLE) {
                // ptr_plans.startAnimation(fadeOut);
            } else {
                ptr_plans.startAnimation(fadeIn);
            }
        }
    }

    private void requestPlanList(String tag, int page, final boolean isRefresh, final boolean isAnim, int type) {
        if (totalPage != -1 && page > totalPage && !isRefresh) {
            ptr_plans.onRefreshComplete();
            d("III_request", "当前页 " + page + ", 总页数 " + totalPage + ", 最后一页了，无法加载");
            Toast.makeText(this, R.string.detail_data_finish, Toast.LENGTH_SHORT).show();
            ptr_plans.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            return;
        }

        if (isRefresh && isAnim) {
            switchUI(View.VISIBLE, View.GONE, View.GONE, View.GONE);
        }
        MSolutionListReq req = new MSolutionListReq();
        req.setPageNo(page);
        req.setTag(tag);
        BaseCall<MSolutionListResp> call = new BaseCall<MSolutionListResp>() {
            @Override
            public void call(MSolutionListResp resp) {
                ptr_plans.onRefreshComplete();
                if (resp != null && resp.isRequestSuccess()) {
                    d("III", "列表请求成功");
                    if (isRefresh) {
                        mPlanAdapter.getList().clear();
                        mPlanAdapter.notifyDataSetChanged();
                    }
                    if (resp.getTotalPage() <= resp.getPageNo()) {
                        ptr_plans.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        ptr_plans.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    // mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    if (resp.getDatas() != null && resp.getDatas().size() != 0) {
                        mPlanAdapter.getList().addAll(resp.getDatas());
                        mPlanAdapter.notifyDataSetChanged();
                        if (isAnim) {
                            switchUI(View.GONE, View.GONE, View.GONE, View.VISIBLE);
                        }
                    } else {
                        if (isRefresh && isAnim) {
                            switchUI(View.GONE, View.VISIBLE, View.GONE, View.GONE);
                        }
                    }

                } else {
                    if (isRefresh && isAnim) {
                        // show error
                        switchUI(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                    }
                    Toast.makeText(getApplicationContext(), "没有拿到数据", Toast.LENGTH_SHORT).show();
                    d("III_request", "msg: " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(call);
        sld.getSolutionListByTypeAsync(req, call, type);
    }

    @Override
    public void onCheckedChanged(EffectRadioGroup group, int checkedId) {
        String tag = (String) group.findViewById(checkedId).getTag(R.id.tv_tag);
        if (!tag.equals(currTag)) {
            requestPlanList(tag, 1, true, true, GetPostList.GET_DATA_SERVER);
        }
        currTag = tag;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPlanList(currTag, 1, true, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPlanList(currTag, currPage++, false, false, GetPostList.GET_DATA_SERVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CARE_PLAN_DETAIL: {
                if (resultCode == RESULT_FIRST_USER) {
                    long id = data.getLongExtra(Constants.RESULT_POST_DETAIL_DELETE_ID, 0);
                    if (id != 0) {
                        MSolutionResp solu = new MSolutionResp();
                        solu.setSolutionId(id);
                        mPlanAdapter.getList().remove(solu);
                        mPlanAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case REQ_CARE_PLAN_RELEASE: {
                if (resultCode == RESULT_OK) {
                    int id = vg_effects.getChildAt(0).getId();
                    if (vg_effects.getCheckedId() != id) {
                        vg_effects.check(id);
                    } else {
                        requestPlanList(currTag, 1, true, true, GetPostList.GET_DATA_SERVER);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onChildViewClick(View v, Object item, final int position) {

        final MSolutionResp plan = (MSolutionResp) item;
        switch (v.getId()) {
            case R.id.tv_sending: {// 本地帖重发
                d("III", "resend position " + position + ", obj " + JsonUtils.toJson(plan));
                SolutionCreateUpdate prc = ReqFactory.buildInterface(this, SolutionCreateUpdate.class);
                SolutionCreateReq req = new SolutionCreateReq();
                req.setLocalSolutionId(plan.getLocalSolutionId());
                req.setPart(plan.getPart());
                req.setEffect(plan.getEffect());
                req.setTitle(plan.getTitle());
                req.setIntroduction(plan.getIntroduction());
                req.setContent(plan.getContent());
                req.setCoverUrl(plan.getCoverImgUrl());
                req.setCoverThumbUrl(plan.getCoverThumbImgUrl());
                req.setUseCycle(plan.getUseCycle());
                d("III", "重新发送 localId" + plan.getLocalSolutionId());
                BaseCall<MSolutionResp> call = new BaseCall<MSolutionResp>() {
                    @Override
                    public void call(MSolutionResp resp) {
                        if (resp != null && resp.isRequestSuccess()) {
                            plan.setSendState(resp.getSendState());
                            // mPostAdapter.getList().set(positon, post);
                            mPlanAdapter.notifyDataSetChanged();
                        }
                    }
                };
                addCallback(call);
                prc.solutionCreateAsync(req, call);
                break;
            }
            case R.id.tv_delete: {// 本地帖删除
                d("III", "delete position " + position + ", obj " + JsonUtils.toJson(plan));
                SolutionCreateUpdate prc = ReqFactory.buildInterface(this, SolutionCreateUpdate.class);
                BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
                    @Override
                    public void call(BaseResp resp) {
                        if (resp != null && resp.isRequestSuccess()) {
                            mPlanAdapter.getList().remove(position);
                            mPlanAdapter.notifyDataSetChanged();
                        }
                    }
                };
                addCallback(call);
                prc.solutionDeleteLocalItemAsync(plan.getLocalSolutionId(), call);
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position--;
        MSolutionResp item = mPlanAdapter.getItem(position);
        if (item == null) {
            return;
        }
        if (item.getLocalSolutionId() == 0) {

        }
        Intent intent = new Intent(this, ActivityCarePlanDetail.class);
        intent.putExtra(Constants.EXTRA_POST_DETAIL_CONTENT, item);
        startActivityForResult(intent, REQ_CARE_PLAN_DETAIL);
    }

    @Override
    protected void onDestroy() {
        if (isReceiver && planReceiver != null) {
            unregisterReceiver(planReceiver);
            isReceiver = false;
        }
        super.onDestroy();
    }

    class EffectViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_effect;

        public EffectViewHolder(View itemView) {
            super(itemView);
            tv_effect = (TextView) itemView.findViewById(R.id.tv_effect);
        }
    }

    class EffectAdapter extends BaseAdapter {

        private SparseArray<String> effects = genEffects();
        private Context context;
        private LayoutInflater inflater;

        public EffectAdapter(Context context, LayoutInflater inflater) {
            this.context = context;
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return effects.size();
        }

        @Override
        public String getItem(int position) {
            return effects.valueAt(position);
        }

        @Override
        public long getItemId(int position) {
            return effects.keyAt(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EffectViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan_choice_effect, null);
                convertView.setTag(holder = new EffectViewHolder(convertView));
            } else {
                holder = (EffectViewHolder) convertView.getTag();
            }

            int id = (int) getItemId(position);
            String str = id < 0 ? getItem(position) : context.getString(id);
            String tag = id >= 0 ? getItem(position) : TAG_ALL;
            holder.tv_effect.setText(str);
            holder.tv_effect.getBackground().setLevel(position * 15 % 100);
            convertView.setTag(R.id.tv_tag, tag);
            return convertView;
        }

    }


}
