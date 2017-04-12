package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.GetPostList.*;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.textbuilder.utils.L;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.found.ActivityTopicDetail;
import com.dilapp.radar.view.OverScrollView;
import com.dilapp.radar.viewbuilder.BBSViewBuilder;
import com.dilapp.radar.viewbuilder.BBSViewGetter;
import com.dilapp.radar.viewbuilder.impl.BBSViewBuilderImpl;
import com.lenovo.text.bbsbuild.BBSViewGetterImpl;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/8/13.
 */
public class ActivityPostPreview extends BaseActivity implements View.OnClickListener, OverScrollView.OnScrollChangedListener {

    private TitleView mTitle;
    private BBSViewBuilder mBBSViewBuilder;
    private BBSTextBuilder mBBSTextBuilder;
    private BBSViewGetter mBBSViewGetter;

    private Animation inToTop, outToTop;

    private ViewGroup vg_content;
    private OverScrollView osv_scroll;
    private View btn_to_top;
    private TextView tv_topic;
    private TextView tv_title;
    private ViewGroup post_container;

    private MPostResp mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_preview);
        Context context = getApplicationContext();
        mPost = (MPostResp) getIntent().getSerializableExtra(Constants.EXTRA_POST_DETAIL_CONTENT);
        if (mPost == null) {
            d("III", "没有可预览的帖子");
            finish();
            return;
        }

        inToTop = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        outToTop = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        vg_content = findViewById_(R.id.vg_content);
        osv_scroll = findViewById_(R.id.osv_scroll);
        tv_topic = findViewById_(R.id.tv_topic);
        tv_title = findViewById_(R.id.tv_title);
        btn_to_top = findViewById_(R.id.btn_to_top);
        post_container = findViewById_(R.id.post_container);
        osv_scroll.setOnScrollChangedListener(this);

        mBBSViewGetter = new BBSViewGetterImpl(this, getLayoutInflater());
        mBBSTextBuilder = new BBSTextBuilderImpl("[]");
        mBBSViewBuilder = new BBSViewBuilderImpl(this, BBSViewBuilder.MODE_NORMAL, mBBSTextBuilder, mBBSViewGetter);
        post_container.addView(mBBSViewBuilder.getContainer());
    }

    private boolean first = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (first) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth() - getResources().getDimensionPixelSize(R.dimen.topic_detail_padding_l_r) * 1;
            ((BBSViewGetterImpl) mBBSViewGetter).setParentWidth(width);
            // MPostListResp resp = (MPostListResp) getIntent().getSerializableExtra(Constants.EXTRA_POST_DETAIL_CONTENT);
             setUIFromData(mPost, 0);
            first = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                finish();
                break;
            }
            case R.id.tv_topic: {
                Intent intent = new Intent(this, ActivityTopicDetail.class);
                intent.putExtra(Constants.EXTRA_TOPIC_DETAIL_ID, mPost.getTopicId());
                startActivity(intent);
                break;
            }
        }
    }


    /**
     * 设置用户信息以及主贴内容
     *
     * @param data   数据
     * @param status 暂时没卵用
     */
    private void setUIFromData(MPostResp data, int status) {
        if (data == null) {
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }

        d("III_logic", "setUIFromData " + data.getId() + " " + data.getPostTitle());
        String title = data.getPostTitle() == null ? "unknown" : data.getPostTitle();
        String topic = getString(R.string.detail_topic, data.getTopicTitle() == null ? "unknown" : data.getTopicTitle());
        String tv_send = getString(R.string.topic_sending);
        // String postTitle = (data.getTopicTitle() != null ? "[" + data.getTopicTitle() + "]" : "") + title;
        String nickname = data.getUserName() == null ? "unknown" : data.getUserName();
        String gender = data.isGender() == 2 ? getString(R.string.woman) : data.isGender() == 1 ? getString(R.string.man) : getString(R.string.secret);
        String level = "lv " + data.getLevel();
        String comment = getString(R.string.detail_what_total_reply, data.getTotalFollows() + "");
        boolean noShowTopic = true;// TopicHelper.isSpecialTopic(data.getTopicId());

//        if (btn_focus.getVisibility() == View.VISIBLE)
//            btn_focus.setVisibility(data.getUserId().equals(SharePreCacheHelper.getUserID(this)) ? View.INVISIBLE : View.VISIBLE);
//        btn_focus.setText(data.isFollowsUser() ? R.string.detail_followed : R.string.detail_follow);
//        btn_focus.setTag(data);
        mTitle.setCenterText(title, null);
        tv_topic.setText(tv_send);
        tv_title.setText(title);
//        tv_nickname.setText(nickname);
//        tv_datetime.setText(TopicHelper.getTopicDateString(this, System.currentTimeMillis(), data.getCreateTime()));
//        tv_gender.setText(gender);
//        tv_level.setText(level);
//        tv_like.setText(data.getLike() + "");
//        tv_reply.setText(data.getTotalFollows() + "");
        // mTitle.setCenterText(tv_title.getText().toString(), null);
//        tv_total_comment.setText(comment);
//        tv_is_collection.getCompoundDrawables()[0].setLevel(data.isStoreUp() ? 1 : 0);
//        tv_is_like.getCompoundDrawables()[0].setLevel(data.isLike() ? 1 : 0);
//        iv_header.setTag(data.getUserId());
//        iv_header.setOnClickListener(this);
        //TopicHelper.setImageFromUrl((data.getUserHeadIcon().startsWith("http") ? "" : HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP) + data.getUserHeadIcon(), iv_header);
//        ImageLoader.getInstance().displayImage(TopicHelper.wrappeImagePath(data.getUserHeadIcon()), iv_header, options);
//        vg_like.setTag(data);
//        vg_like.setTag(R.id.vg_like, tv_is_like.getCompoundDrawables()[0]);
//        vg_like.setTag(R.id.btn_agree, tv_like);

        // d("III", "postContent:" + data.getPostContent());
        if (!mBBSTextBuilder.getString().equals(data.getPostContent())) {
            mBBSTextBuilder.setString(data.getPostContent());
            mBBSViewBuilder.notifyTextBuilderChanged();
            d("III", "not equals " + data.getPostContent());
        } else {
            d("III", "post content equals");
        }
        // post_container.removeAllViews();
        // if (mBBSViewBuilder.getContainer() != null) {
        // post_container.addView(mBBSViewBuilder.getContainer());
        // }
        tv_topic.setCompoundDrawables(null, null, null, null);
        tv_topic.setVisibility(noShowTopic ? View.GONE : View.VISIBLE);
//        vg_option.setVisibility(View.VISIBLE);
//        if (vg_loading.getVisibility() != View.GONE) {
//            vg_loading.setVisibility(View.GONE);
//            vg_loading.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
//        }
//        if (osv_scroll.getVisibility() != View.VISIBLE) {
//            osv_scroll.setVisibility(View.VISIBLE);
//            osv_scroll.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
//        }
        // osv_scroll.getRefreshableView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onScrollChanged(int x, int y, int oldx, int oldy) {
        if (y > vg_content.getHeight()) {
            if (btn_to_top.getVisibility() != View.VISIBLE) {
                btn_to_top.setVisibility(View.VISIBLE);
                btn_to_top.startAnimation(inToTop);
            }
        } else {
            if (btn_to_top.getVisibility() != View.GONE) {
                btn_to_top.setVisibility(View.GONE);
                btn_to_top.startAnimation(outToTop);
            }
        }
    }

    @Override
    public void scrollBottom() {

    }
}
