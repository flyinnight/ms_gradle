package com.dilapp.radar.ui.topic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.FoundAllTopic;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicReq;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PresetTopic;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListResp;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.comm.ActivityInputHistory;
import com.dilapp.radar.util.HttpConstant;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 使用FragmentTopicChoice需要注意以下几个参数
 * <p/>
 * 参数名称                            类型   默认值  参数解释
 * FragmentTopicChoice.ARG_IS_MULTI    boolean false   是否多选
 * FragmentTopicChoice.ARG_IS_FIXED    boolean false   是否只显示预置的Topics
 * FragmentTopicChoice.ARG_IS_SEARCH   boolean true    是否支持搜索
 * FragmentTopicChoice.ARG_TEXT_PROMPT String  ""      文本提示
 */
public class FragmentTopicChoice extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<GridView> {

    public final static String ARG_IS_TITLE = "is_title--::";
    public final static String ARG_IS_MULTI = "is_multi--::";
    public final static String ARG_IS_FIXED = "is_fixed--::";
    public final static String ARG_IS_SEARCH = "is_search--::";
    public final static String ARG_TEXT_PROMPT = "text_prompt--::";

    private final static int REQ_SEARCH_TOPIC = 10;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true)
            .showImageOnLoading(R.drawable.img_bbs_default)
            .showImageOnFail(R.drawable.img_bbs_default)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(200))
            .build();

    private boolean isTitle;// 是否显示标题
    private boolean isMulti;// 是否多选
    private boolean isFixed;// 是否只显示预置的Topics
    private boolean isSerach;// 是否支持搜索
    private String textPrompt;// 文本提示
    private OnCheckedListener checkedListener;
    private OnClickListener clickListener;

    private TitleView mTitle;
    private View vg_progress;
    private View vg_error;
    private View vg_search;
    private TextView tv_title;
    private PullToRefreshGridView prgv_grid;
    private GridView gv_topic;
    private TopicAdapter adapter;

    private int currPage = 1;
    private int totalPage = -1;

    @Override
    protected void onCreateView(ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(container, savedInstanceState);
        setContentView(R.layout.fragment_topic_choice);
        setCacheView(true);

        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        isTitle = args.getBoolean(ARG_IS_TITLE, false);
        isMulti = args.getBoolean(ARG_IS_MULTI, false);
        isFixed = args.getBoolean(ARG_IS_FIXED, false);
        isSerach = args.getBoolean(ARG_IS_SEARCH, true);
        textPrompt = args.getString(ARG_TEXT_PROMPT);

        mTitle = new TitleView(mContext, findViewById(TitleView.ID_TITLE));
        mTitle.setCenterText(R.string.choice_title, null);
        mTitle.setRightText(R.string.finish, this);

        vg_progress = findViewById(R.id.vg_progress);
        vg_error = findViewById(R.id.vg_error);
        vg_search = findViewById(R.id.vg_search);
        tv_title = findViewById(R.id.tv_title);
        prgv_grid = findViewById(R.id.prgv_grid);
        gv_topic = prgv_grid.getRefreshableView();
        adapter = new TopicAdapter();
        gv_topic.setAdapter(adapter);
        gv_topic.setOnItemClickListener(adapter);
        vg_error.setOnClickListener(this);
        vg_search.setOnClickListener(this);

        mTitle.setVisibility(isTitle ? View.VISIBLE : View.GONE);
        gv_topic.setChoiceMode(isMulti ? AbsListView.CHOICE_MODE_MULTIPLE : AbsListView.CHOICE_MODE_SINGLE);
        vg_search.setVisibility(isSerach ? View.VISIBLE : View.GONE);
        if (textPrompt != null) {
            tv_title.setText(textPrompt);
        }
        if (isFixed) {// 预置列表是一次性拉下来的，所以不需要刷新
            prgv_grid.setMode(PullToRefreshBase.Mode.DISABLED);
        } else {
            prgv_grid.setOnRefreshListener(this);
            prgv_grid.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        dispatchRequestData(GetPostList.GET_DATA_SERVER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_RIGHT: {
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
                break;
            }
            case R.id.vg_error: {
                vg_error.setVisibility(View.GONE);
                vg_progress.setVisibility(View.VISIBLE);
                dispatchRequestData(GetPostList.GET_DATA_SERVER);
                break;
            }
            case R.id.vg_search: {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        // Now we provide a list of Pair items which contain the view we can transitioning
                        // from, and the name of the view it is transitioning to, in the launched activity
                        new Pair<View, String>(v, "share:et_taste_name"));
                Intent intent = new Intent(mContext, ActivityInputHistory.class);
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_NAME, "topic");
                intent.putExtra(Constants.EXTRA_INPUT_HISTORY_HINT, mContext.getString(R.string.choice_please_input_topic));
                ActivityCompat.startActivityForResult(getActivity(), intent, REQ_SEARCH_TOPIC, activityOptions.toBundle());
                break;
            }
        }
    }

    /**
     * 获取当前选择的项
     *
     * @return
     */
    public List<MTopicResp> getCheckeds() {
        long[] ids = gv_topic.getCheckedItemIds();

        String idsStr = "";
        List<MTopicResp> checkeds = new LinkedList<MTopicResp>();
        for (int i = 0; i < ids.length; i++) {
            int index = (int) ids[i];
            idsStr += index + ",";
            checkeds.add(adapter.list.get(index));
        }
        // Log.i("III_logic", "ids " + idsStr);
        return checkeds;
    }

    /**
     * 选择回调，暂时仅对单选有效
     *
     * @param checkedListener
     */
    public void setOnCheckedListener(OnCheckedListener checkedListener) {
        this.checkedListener = checkedListener;
    }

    public OnClickListener getOnClickListener() {
        return clickListener;
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SEARCH_TOPIC) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    private void dispatchRequestData(int type) {
        if (isFixed) {
            requestFixedTopics(type);
        } else {
            requestAllTopics(currPage, true, type);
        }
    }

    private void requestAllTopics(int page, final boolean isRefresh, int type) {

        if (totalPage != -1 && page > totalPage && !isRefresh) {
            Log.i("III_logic", "当前页 " + page + ", 总页数 " + totalPage + ", 最后一页了，无法加载");
            Toast.makeText(mContext, R.string.detail_data_finish, Toast.LENGTH_SHORT).show();
            return;
        }
        FoundAllTopic fat = ReqFactory.buildInterface(mContext, FoundAllTopic.class);
        AllTopicReq req = new AllTopicReq();
        req.setPageNo(page);
        BaseCall<AllTopicResp> node = new BaseCall<AllTopicResp>() {
            @Override
            public void call(AllTopicResp resp) {
                if (resp != null && resp.isRequestSuccess()) {

                    if (resp.getTotalPage() <= resp.getPageNo()) {
                        prgv_grid.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        prgv_grid.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                    if (isRefresh) {
                        adapter.list.clear();
                    }
                    totalPage = resp.getTotalPage();
                    currPage = resp.getPageNo() + 1;
                    adapter.list.addAll(resp.getTopicResp());
                    adapter.notifyDataSetChanged();
                    Log.i("III_logic", "数据加载成功 " + resp.getPageNo() + "/" + resp.getTotalPage());
                    prgv_grid.setVisibility(View.VISIBLE);

                    if (resp.getTopicResp() == null || resp.getTopicResp().size() == 0) {
                        Log.i("III_logic", "服务器上没有任何数据");
                    }
                } else {
                    Log.i("III_logic", "数据加载失败");
                    Toast.makeText(mContext, R.string.choice_request_failure, Toast.LENGTH_SHORT).show();
                    if (totalPage == -1) {
                        vg_error.setVisibility(View.VISIBLE);
                    }
                }
                vg_progress.setVisibility(View.GONE);
                prgv_grid.onRefreshComplete();
            }
        };
        addCallback(node);
        fat.getAllTopicByTypeAsync(req, node, type);
    }

    private void requestFixedTopics(int type) {
        Log.i("III_logic", "获取预置Topics");
        PresetTopic pt = ReqFactory.buildInterface(mContext, PresetTopic.class);

        // TopicListCallBack tlc = ReqFactory.buildInterface(mContext, TopicListCallBack.class);

        BaseReq req = new BaseReq();
        BaseCall<TopicListResp> node = new BaseCall<TopicListResp>() {
            @Override
            public void call(TopicListResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (resp.getDatas() != null && resp.getDatas().size() > 0) {
                        adapter.list.addAll(resp.getDatas());
                        adapter.notifyDataSetChanged();
                        Log.i("III_data", "获取数据成功");
                    } else {
                        Log.i("III_data", "服务器没有数据");
                    }
                    prgv_grid.setVisibility(View.VISIBLE);
                } else {
                    Log.i("III_data", "获取数据失败 " + (resp != null ? resp.getMessage() : null));
                    vg_error.setVisibility(View.VISIBLE);
                }
                vg_progress.setVisibility(View.GONE);
            }
        };
        addCallback(node);
        pt.getPresetTopicListByTypeAsync(req, node, type);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        if (!isFixed) {
            requestAllTopics(1, true, GetPostList.GET_DATA_SERVER);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        if (!isFixed) {
            requestAllTopics(currPage++, false, GetPostList.GET_DATA_SERVER);
        }
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.list.clear();
        }
        this.checkedListener = null;
        super.onDestroy();
    }

    class TopicAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private List<MTopicResp> list = new ArrayList<MTopicResp>(0);

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public MTopicResp getItem(int position) {
            return list != null && position < list.size() ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_topic_choice_grid, null);
                convertView.setTag(vh = new ViewHolder(convertView));
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            MTopicResp item = getItem(position);
            if (item == null) {
                return convertView;
            }
            String text = item.getTopictitle() == null ? "unknown" : item.getTopictitle();
            String image = item.getTopicimg() == null || item.getTopicimg().length == 0 ? null : HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP + item.getTopicimg()[0];
            //android.util.Log.i("III_data", "image url " + image);
            vh.tv_text.setText(text);
            if (image != null) ImageLoader.getInstance().displayImage(image, vh.iv_image, options);
            return convertView;
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isMulti) {
                if (checkedListener != null) {
                    checkedListener.onChecked(getItem(position));
                }

            }
            android.util.Log.i("III_logic", "item click " + position + " " + gv_topic.getCheckedItemIds().length);
        }
    }

    class ViewHolder {
        TextView tv_text;
        ImageView iv_image;

        ViewHolder(View parent) {
            tv_text = (TextView) parent.findViewById(R.id.tv_text);
            iv_image = (ImageView) parent.findViewById(R.id.iv_image);
        }
    }

    public interface OnCheckedListener {
        void onChecked(MTopicResp data);
    }
}
