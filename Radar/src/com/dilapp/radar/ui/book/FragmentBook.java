package com.dilapp.radar.ui.book;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowupPostTopic;
import com.dilapp.radar.domain.FollowupPostTopic.FollowupTopicReq;
import com.dilapp.radar.domain.MyTopicCallBack;
import com.dilapp.radar.domain.MyTopicCallBack.HasFollowTopicResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.topic.FragmentPosts;
import com.dilapp.radar.ui.topic.FragmentTopicChoice;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;

import java.util.List;

/**
 * Created by husj1 on 2015/7/4.
 */
public class FragmentBook extends BaseFragment implements View.OnClickListener {

    // private TitleView mTitle;
    private FragmentTopicChoice mFChoice;
    private Fragment mFPostList;
    private View vg_loading;
    private View vg_error;
    private View vg_content;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFChoice = new FragmentTopicChoice();
        Bundle args = new Bundle();
        args.putBoolean(FragmentTopicChoice.ARG_IS_TITLE, true);
        args.putBoolean(FragmentTopicChoice.ARG_IS_FIXED, true);
        args.putBoolean(FragmentTopicChoice.ARG_IS_MULTI, true);
        args.putBoolean(FragmentTopicChoice.ARG_IS_SEARCH, false);
        args.putString(FragmentTopicChoice.ARG_TEXT_PROMPT, getString(R.string.choice_topic_selected));
        mFChoice.setArguments(args);
        mFChoice.setOnClickListener(this);

        mFPostList = new FragmentPosts();
//        mFPostList = new FragmentTopicPost();
        // mFPostList.setOnClickListener(this);
    }

    @Override
    public void onCreateView(/*LayoutInflater inflater, */ViewGroup container, Bundle savedInstanceState) {
        // Log.i("III", "onCreateView Diff-------");
        super.onCreateView(/*inflater, */container, savedInstanceState);
        setContentView(R.layout.fragment_topic);
        setCacheView(true);

        // mTitle = new TitleView(mContext, findViewById(R.id.vg_toolbar));
        vg_loading = findViewById(R.id.vg_loading);
        vg_error = findViewById(R.id.vg_error);
        vg_error.setOnClickListener(this);
        // vg_error.bringToFront();
        vg_content = findViewById(R.id.fragment_container);
        requestMyFocusedTopics();
        // showChoiceTopic();
        test();
    }

    private void test() {
        if(!BuildConfig.DEBUG) {
            return;
        }
        // mTitle.setCenterText();
    }

    public void toggle() {

        if(mFChoice.isVisible()) {
            showPostList();
        } else {
            showChoiceTopic();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 貌似ChildFragment主动回调不到onActivityResult
        if(mFPostList.isVisible()) {
            mFPostList.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean haspaired = SharePreCacheHelper.getPairStatus(getActivity());
        if(haspaired && !ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
        		AllKfirManager.getInstance(getActivity()).endSkinTest();
        }
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	private int clickCount;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                if(mFPostList.isVisible()) {
                }
                break;
            }
            case TitleView.ID_RIGHT: {

                if(mFPostList.isVisible()) {
                    /*if(clickCount == 3) {
                        showChoiceTopic();
                        clickCount = 0;
                        return;
                    }
                    clickCount++;
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            clickCount = 0;
                        }
                    }.sendEmptyMessageDelayed(0, 1000);*/
                } else {
                    List<MTopicResp> list = mFChoice.getCheckeds();
                    if(list == null || list.size() == 0) {
                        Toast.makeText(mContext, R.string.topic_please_choice_topics, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    AsyncTask empty = null;
                    showWaitingDialog(empty);
                    requestFocusTopics(0, list);
                    // Log.i("III_logic", "select size " + mFChoice.getSelected().size());
                }
//                Intent intent = new Intent(mContext, SearchTopicActivity.class);
//                startActivityForResult(intent, 20);
                break;
            }
            case TitleView.ID_CENTER: {
//                Intent intent = new Intent(mContext, ActivityTopicDetail.class);
//                startActivity(intent);
                break;
            }
            case R.id.vg_error: {
                vg_error.setVisibility(View.GONE);
                requestMyFocusedTopics();
                break;
            }
        }
    }

    private void requestMyFocusedTopics() {
        vg_loading.setVisibility(View.VISIBLE);
        vg_error.setVisibility(View.GONE);
        vg_content.setVisibility(View.GONE);
        
        MyTopicCallBack mt = ReqFactory.buildInterface(mContext, MyTopicCallBack.class);
        Log.i("III_logic", "requestMyFocusedTopics");
        BaseCall<HasFollowTopicResp> node = new BaseCall<HasFollowTopicResp>() {
            @Override
            public void call(HasFollowTopicResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (!resp.getHasFollow()) {
                        showChoiceTopic();
                        Log.i("III_logic", "没有关注的话题，显示关注话题");
                        Slog.f("Filelog: requestMyFocusedTopics: " + resp);
                    } else {
                        showPostList();
                        Log.i("III_logic", "有关注的话题，显示帖子列表");
                    }
                } else {
                    showError();
                    Log.i("III_logic", "好像错误了 " + (resp != null ? resp.getMessage() : null));
                }
            }
        };
        addCallback(node);
        mt.hasFollowTopicAsync(node);
    }

    /**
     * 好傻好傻的办法，只能这样
     * @param curr 递归到当前的索引
     * @param list 数据列表
     */
    private void requestFocusTopics(final int curr, final List<MTopicResp> list) {
        if(list == null || list.size() == 0) {
            Toast.makeText(mContext, R.string.topic_please_choice_topics, Toast.LENGTH_SHORT).show();
            return;
        }
        MTopicResp data = list.get(curr);
        FollowupPostTopic fpt = ReqFactory.buildInterface(mContext, FollowupPostTopic.class);
        FollowupTopicReq req = new FollowupTopicReq();
        req.setTopicId(data.getTopicId());
        req.setFollowup(true);
        Log.i("III_logic", "topic id " + data.getTopicId());
        BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (curr + 1 == list.size()) {
                        showPostList();
                        Toast.makeText(mContext, R.string.topic_focus_success, Toast.LENGTH_SHORT).show();
                        dimessWaitingDialog();
                    } else {
                        requestFocusTopics(curr + 1, list);
                    }
                } else {
                    Log.i("III_data", "当前位置 " + curr + " " + (resp != null ? resp.getMessage() : "null"));
                    Toast.makeText(mContext, R.string.topic_focus_failure, Toast.LENGTH_SHORT).show();
                    dimessWaitingDialog();
                }
            }
        };
        addCallback(node);
        fpt.followupTopicAsync(req, node);
    }

    private void showChoiceTopic() {
        if (vg_loading.getVisibility() != View.GONE) {
            vg_loading.setVisibility(View.GONE);
        }
        if(vg_error.getVisibility() != View.GONE) {
            vg_error.setVisibility(View.GONE);
        }

        if(vg_content.getVisibility() != View.VISIBLE) {
            vg_content.setVisibility(View.VISIBLE);
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(mFPostList.isAdded()) {
            ft.hide(mFPostList);
        }
        if(mFChoice.isAdded()) {
            ft.show(mFChoice);
        } else {
            ft.add(R.id.fragment_container, mFChoice, "mFChoice");
        }
        ft.commitAllowingStateLoss();
        /*mTitle.setCenterText(R.string.choice_title, null);
        mTitle.setRightIcon(null, null);
        mTitle.setRightText(R.string.finsh, this);
        mTitle.setLeftText(null, this);*/
    }

    private void showPostList() {
        if (vg_loading.getVisibility() != View.GONE) {
            vg_loading.setVisibility(View.GONE);
        }
        if(vg_error.getVisibility() != View.GONE) {
            vg_error.setVisibility(View.GONE);
        }
        if(vg_content.getVisibility() != View.VISIBLE) {
            vg_content.setVisibility(View.VISIBLE);
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(mFChoice.isAdded()) {
            ft.hide(mFChoice);
        }
        if(mFPostList.isAdded()) {
            ft.show(mFPostList);
        } else {
            ft.add(R.id.fragment_container, mFPostList, "mFPostList");
        }
        ft.commitAllowingStateLoss();
        /*mTitle.setCenterText(R.string.main_topic, this);
        mTitle.setRightText(null, null);
        mTitle.setRightIcon(R.drawable.btn_topic_search, this);
        mTitle.setLeftIcon(R.drawable.topic_release_iv, this);*/
    }

    private void showError() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(mFChoice.isAdded()) {
            ft.hide(mFChoice);
        }
        if(mFPostList.isAdded()) {
            ft.hide(mFPostList);
        }
        if(vg_error.getVisibility() != View.VISIBLE) {
            vg_error.setVisibility(View.VISIBLE);
        }

        if (vg_loading.getVisibility() != View.GONE) {
            vg_loading.setVisibility(View.GONE);
        }
    }
}
