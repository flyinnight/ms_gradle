package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.GetPostList.*;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.*;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionCommentScore;
import com.dilapp.radar.domain.SolutionCommentScore.*;
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.impl.PostReleaseCallBackAsyncImpl;
import com.dilapp.radar.domain.impl.SolutionCommentScoreImpl;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.UmengUtils;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/7/8.
 */
public class ActivityPostComment extends ActivityPostEditBase {

    private TitleView mTitle;

    private long postID;
    private long topicID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_comment);
        Intent data = getIntent();

        postID = data.getLongExtra(Constants.EXTRA_SEND_COMMENT_PARENT_POST_ID, 0);
        topicID = data.getLongExtra(Constants.EXTRA_SEND_COMMENT_TOPIC_ID, 0);

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftText(R.string.cancel, this);
        mTitle.setCenterText(R.string.send_comment_title, null);
        mTitle.setRightText(R.string.send_comment_release, this);

        initView(savedInstanceState);
    }

    @Override
    public void onClick(View weight) {
        super.onClick(weight);

        switch (weight.getId()) {
            case TitleView.ID_LEFT: {
                setEmojicoVisiable(false);
                setSoftKeyboardVisiable(false);
                finish();
                break;
            }
            case TitleView.ID_RIGHT: {
                if (TopicHelper.isNotEmpty(mTextBuilder)) {
                    if (postID != 0) {
                        setEmojicoVisiable(false);
                        setSoftKeyboardVisiable(false);
                        // AsyncTask<?, ?, ?> empty = null;
                        // showWaitingDialog(empty);
                        // requestReleasePost();
                        startReleasePost();
                        // Toast.makeText(this, mTextBuilder.getString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    boolean twoFirst = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && twoFirst) {
            EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
            v.setHint(R.string.topic_please_input_text);
            et_title.requestFocus();
            twoFirst = false;
        }
    }

    private void startReleasePost() {
        showWaitingDialog((AsyncTask) null);
        List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
        // 将帖子中图片抽取出来
        List<String> thumbs = TopicHelper.describeItemContent2Strings(images);
        if (Constants.COMPRESS_POST_IMAGE) {
            thumbs = TopicHelper.compress(thumbs);
        }
        int flag = getIntent().getIntExtra(
                Constants.EXTRA_SEND_COMMENT_FLAG,
                Constants.EXTRA_SEND_COMMENT_FLAG_POST);
        switch (flag) {
        case Constants.EXTRA_SEND_COMMENT_FLAG_PLAN: {
            requestReleasePlan(mTextBuilder.getString(), thumbs);
            break;
        }
        case Constants.EXTRA_SEND_COMMENT_FLAG_POST:
        default: {
            requestReleasePost(mTextBuilder.getString(), thumbs);
            break;
        }
        }
    }

   /*
    @Deprecated
   private void requestUploadImage(final List<BBSDescribeItem> images, int thumbSize) {
        List<String> imageUrls = TopicHelper.describeItemContent2Strings(images);
        String log = "";
        if(imageUrls != null) {
            for (String str : imageUrls) {
                log += str + ", ";
            }
        }
        Log.i("III_logic", "paths " + log);
        if(imageUrls != null && imageUrls.size() == 0) {
            List<String> thumbs = new ArrayList<String>();
            if(imageUrls != null && imageUrls.size() != thumbSize) {
                List<BBSDescribeItem> imgs = TopicHelper.findImages(mTextBuilder);
                for (int i = 0; i < imgs.size(); i++) {
                    thumbs.add(imgs.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                }
            }

            TopicHelper.trimBBSTextBuilder(mTextBuilder);
            requestReleasePost(mTextBuilder.getString(), thumbs);
            Log.i("III_logic", "帖子中没有图片，或已上传成功");
            return;
        }
        BaseCall<MPostImgResp> node = new BaseCall<MPostImgResp>() {
            @Override
            public void call(MPostImgResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    if (resp.getPostImgURL() != null && resp.getPostImgURL().size() == images.size()) {
                        TopicHelper.setStrings2BBSDescribeItemContent(resp.getPostImgURL(), images, "");
                        TopicHelper.trimBBSTextBuilder(mTextBuilder);

                        List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
                        List<String> thumbs = new ArrayList<String>(images.size());
                        for (int i = 0; i < images.size(); i++) {
                            thumbs.add(images.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                        }
                        requestReleasePost(mTextBuilder.getString(), thumbs);
                        Log.i("III_logic", "图片上传OK");
                    } else {

                        Log.i("III_logic", "图片上传有问题");
                        dimessWaitingDialog();
                    }
                } else {
                    Log.i("III_logic", "图片上传失败 " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(), R.string.topic_update_image_failure, Toast.LENGTH_SHORT).show();
                    dimessWaitingDialog();
                }
            }
        };
        addCallback(node);
        prc.uploadPostImgAsync(imageUrls, node);
    }*/

    private void requestReleasePost(String content, List<String> imgs) {
        final Context context = getApplicationContext();
        PostReleaseReq req = new PostReleaseReq();
        req.setPostLevel(1);
        req.setPostContent(content);
        req.setParentId(postID);
        req.setTopicId(topicID);
        req.setPostTitle("");
        req.setThumbURL(imgs);
        d("III", "p " + postID + ", t " + topicID + ", content " + content);
        UmengUtils.onEventPostReply(this, "" + topicID);
        BaseCall<MPostResp> node = new BaseCall<MPostResp>() {
            @Override
            public void call(MPostResp resp) {

                if (resp != null && resp.isRequestSuccess()) {
                    Intent data = new Intent();
                    data.putExtra(Constants.EXTRA_SEND_COMMENT_RESULT, (resp));
                    setResult(RESULT_OK, data);
                    finish();
                    Toast.makeText(context, R.string.send_comment_success, Toast.LENGTH_SHORT).show();
                    android.util.Log.i("III", "帖子上传成功");
                } else {
                    android.util.Log.i("III", "msg-> " + resp.getMessage());
                    Toast.makeText(context, R.string.send_comment_failure, Toast.LENGTH_SHORT).show();
                }
                dimessWaitingDialog();
            }
        };
        addCallback(node);
        PostReleaseCallBack prc = new PostReleaseImgUploadWrapper(new PostReleaseCallBackAsyncImpl(this));
        prc.createPostAsync(req, node);
    }

    private void requestReleasePlan(String content, List<String> imgs) {
        final Context context = getApplicationContext();
        CreatCommentReq req = new CreatCommentReq();
        req.setSolutionId(postID);
        req.setContent(content);
        req.setPicUrl(imgs);
        BaseCall<MSolutionResp> call = new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    Intent data = new Intent();
                    data.putExtra(Constants.EXTRA_SEND_COMMENT_RESULT, (resp));
                    setResult(RESULT_OK, data);
                    finish();
                    Toast.makeText(context, R.string.send_comment_success, Toast.LENGTH_SHORT).show();
                    d("III", "护肤方案评论上传成功");
                } else {
                    d("III", "msg-> " + resp.getMessage());
                    Toast.makeText(context, R.string.send_comment_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(call);
        SolutionCommentScore scs = new SolutionCSSyncWrapper(new SolutionCommentScoreImpl(this));
        scs.solutionCreatCommentsAsync(req, call);
    }
}
