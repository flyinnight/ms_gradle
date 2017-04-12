package com.dilapp.radar.ui.topic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList.*;
import com.dilapp.radar.domain.PostOperation;
import com.dilapp.radar.domain.PostOperation.*;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.*;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.*;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.utils.JsonUtils;

import static com.dilapp.radar.textbuilder.utils.L.*;

import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MD5;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.widget.PromptDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by husj1 on 2015/7/15.
 */
public class ActivityPostEdit extends ActivityPostEditBase implements View.OnLongClickListener, ImageLoadingListener {

    public static final String BROADCAST_FINISH = "finish edit post";
    /*private static int REQ_IMAGE = 20;
    private static int REQ_IMAGE_REPLACE = 100;
    private static int SOFT_KEYBOARD_DELAYED_TIME = 80;*/

    private TitleView mTitle;
    private TextView tv_topic;
    private TextView tv_skin_plan;

    private boolean isModify;
    private MPostResp mPost;

    private PromptDialog mPrompt;
    private PopupWindow mPlanPopup;
    private PostReleaseCallBack prc;
    private long oldTopicID;
    private int replaceIndex = -1;
    private File netImageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        /*AndroidBugsSolution.assistActivity(this, this);*/
        Context context = getApplicationContext();
        Intent data = getIntent();

        isModify = data.getBooleanExtra(Constants.EXTRA_EDIT_POST_IS_MODIFY, false);
        if (isModify) {
            mPost = (MPostResp) data.getSerializableExtra(Constants.EXTRA_EDIT_POST_MODIFY_POST);
            if (mPost == null) {
                Log.i("III_logic", "请选择 Post " + Constants.EXTRA_EDIT_POST_MODIFY_POST + "=" + MPostResp.class.getSimpleName());
                finish();
                return;
            }
            if (mPost.getTopicTitle() == null) mPost.setTopicTitle("");
        } else {
            mPost = new MPostResp();
            MTopicResp topic = (MTopicResp) data.getSerializableExtra(Constants.EXTRA_EDIT_POST_TOPIC);
            if (topic == null) {
                Log.i("III_logic", "请选择 Topic " + Constants.EXTRA_EDIT_POST_TOPIC + "=" + MTopicResp.class.getSimpleName());
                finish();
                return;
            }
            mPost.setTopicId(topic.getTopicId());
            mPost.setTopicTitle(topic.getTopictitle());
        }

        /*imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);*/
        prc = ReqFactory.buildInterface(this, PostReleaseCallBack.class);
        netImageDir = new File(getCacheDir().getPath() + File.separatorChar + "temp");
        if (!netImageDir.isDirectory()) {
            netImageDir.mkdirs();
        }

        mTitle = new TitleView(context, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftText(R.string.cancel, this);
        mTitle.setCenterText(isModify ? R.string.release_post_edit_title : R.string.release_post_title, null);
        mTitle.setRightText(R.string.send_comment_release, this);

        tv_topic = findViewById_(R.id.tv_topic);
        tv_topic.setText(mPost.getTopicTitle());

        tv_skin_plan = findViewById_(R.id.tv_skin_plan);

        View planView = getLayoutInflater().inflate(R.layout.popup_care_skin_plan, null);
        planView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        planView.findViewById(R.id.tv_edit).setOnClickListener(this);
        mPlanPopup = new PopupWindow(this);
        mPlanPopup.setContentView(planView);
        mPlanPopup.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPlanPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPlanPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPlanPopup.setFocusable(true);
        mPlanPopup.setOutsideTouchable(true);
        mPlanPopup.setAnimationStyle(R.style.SkinCarePlanPopupWindowAnim);

        mPrompt = new PromptDialog(this);
        mPrompt.setCanceledOnTouchOutside(false);
        mPrompt.setConfirmId(R.id.dialog_btn_ok);
        mPrompt.setCancelId(R.id.dialog_btn_cancel);
        mPrompt.setCancelOnClickListener(this);
        mPrompt.setConfirmOnClickListener(this);
        mPrompt.setMessage(R.string.edit_unfinish_confirm);

        findViewById(R.id.btn_add).setOnLongClickListener(this);

        initView(savedInstanceState);

        if (savedInstanceState != null) {
            MPostResp post = (MPostResp) savedInstanceState.getSerializable("PostArgs");
            if (post != null) {
                mPost.setTopicId(post.getTopicId());
                mPost.setTopicTitle(post.getTopicTitle());
                mPost.setSelectedToSolution(post.isSelectedToSolution());
                mPost.setPart(post.getPart());
                mPost.setEffect(post.getEffect());
            }
        }

        if (isModify) {
            et_title.setText(mPost.getPostTitle());
            tv_skin_plan.getCompoundDrawables()[0].setLevel(mPost.isSelectedToSolution() ? 1 : 0);
            Log.i("III", "Post effect " + mPost.getEffect());
        }
    }

    private boolean firstA = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (firstA) {

            EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
            v.setHint(R.string.topic_please_input_text);
            et_title.requestFocus();
            /*post_container.addView(mViewBuilder.getContainer());
            // Log.i("III", "container width " + post_container.getMeasuredWidth());
            ((BBSViewGetterImpl) mViewGetter).setParentWidth(
                    post_container.getMeasuredWidth() -
                            (post_container.getPaddingLeft() + post_container.getPaddingRight()));
            PresetPostModel model = (PresetPostModel) getIntent().getSerializableExtra(Constants.EXTRA_EDIT_POST_PRESET_CONTENT);
            presetPost(model);*/
            if (isModify && sis == null) {// 需要做个sis变量为null的判断
                // 否则修改模式下，按Home键，如果内存不足，一回来，你会发现你编辑的东西都变回去了
                mTextBuilder.setString(mPost.getPostContent());
                // mTextBuilder.getBBSDescribe();
                mViewBuilder.notifyTextBuilderChanged();
            }
            initBindView();
            /*EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
            if (!isModify) v.setHint(R.string.topic_please_input_text);
            et_title.requestFocus();
            first = false;*/
            firstA = false;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case TitleView.ID_LEFT: {
                if (et_title.getText().toString().trim().equals("") &&
                        !TopicHelper.isNotEmpty(mTextBuilder)) {
                    finishThis();
                } else {
                    mPrompt.show();
                }
                break;
            }
            case TitleView.ID_CENTER: {
                mOption.show();
                break;
            }
            case TitleView.ID_RIGHT: {
                if (et_title.getText().toString().trim().equals("")) {
                    et_title.requestFocus();
                    setSoftKeyboardVisiable(true);
                    Toast.makeText(this, R.string.topic_please_input_title, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TopicHelper.isNotEmpty(mTextBuilder)) {
                    startReleasePost();
                } else {
                    Toast.makeText(this, R.string.topic_please_input_content, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.dialog_btn_cancel: {
                mPrompt.dismiss();
                break;
            }
            case R.id.dialog_btn_ok: {
                mPrompt.dismiss();
                finishThis();
                break;
            }
            case R.id.tv_topic: {
                Intent intent = new Intent(this, ActivityPostEditPre.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//
                intent.putExtra(Constants.EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT, true);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
                break;
            }
            case R.id.tv_skin_plan: {
                if (tv_skin_plan.getCompoundDrawables()[0].getLevel() == 0) {
                    Intent intent = new Intent(this, ActivityPostChoicePlan.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//
                    intent.putExtra(Constants.EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT, true);
                    startActivity(intent);
                } else {
                    // PopupWindowCompat.showAsDropDown(mPlanPopup, v, 0, -v.getMeasuredHeight(), Gravity.TOP);
                    // mPlanPopup.showAsDropDown(v, 0, 0, Gravity.TOP);
                    // setSoftKeyboardVisiable(false);
                    // setEmojicoVisiable(false);
                    float density = getResources().getDisplayMetrics().density;
                    int[] local = new int[2];
                    v.getLocationOnScreen(local);
                    mPlanPopup.showAtLocation(v, Gravity.NO_GRAVITY, 0, local[1] - (int) (v.getHeight() * 1.85f));
                }
                break;
            }
            case R.id.tv_cancel: {
//                if (mPostRR != null) {
//                    mPostRR = null;
//                }
                mPost.setSelectedToSolution(false);
                tv_skin_plan.getCompoundDrawables()[0].setLevel(0);
                mPlanPopup.dismiss();
                break;
            }
            case R.id.tv_edit: {
                Intent intent = new Intent(this, ActivityPostChoicePlan.class);
                if (isModify && v.getTag() == null) {
                    Log.i("III", "PRR effect " + mPost.getEffect());
                    PostReleaseReq prr = new PostReleaseReq();
                    prr.setEffect(mPost.getEffect());
                    prr.setSkin(mPost.getSkinQuality());
                    prr.setPart(mPost.getPart());
                    intent.putExtra(Constants.EXTRA_EDIT_POST_PLAN_CONTENT, prr);
                    v.setTag("");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//
                intent.putExtra(Constants.EXTRA_EDIT_POST_PRE_REORDER_TO_FRONT, true);
                startActivity(intent);
                mPlanPopup.dismiss();
                break;
            }
            case R.id.btn_cancel_path: {
                replaceIndex = -1;
                break;
            }
            case R.id.btn_confirm_path: {
                String url = mNetImage.getUrl();
                if ("".equals(url)) {
                    break;
                }
                d("III", "urls->" + url);
                String[] split = url.split("\\|\\|");
                for (int i = 0; i < split.length; i++) {
                    url = split[i];
                    ImageLoader.getInstance().loadImage(url, this);
                }
                // mNetImage.setUrl("");
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
        case R.id.btn_add: {
            mNetImage.show();
            return true;
        }
        case R.id.btn_replace:
            // 测试功能，替换成网络图片
            mOption.dismiss();
            mNetImage.show();
            replaceIndex = v.getTag() instanceof Integer ? Integer.parseInt(v.getTag().toString()) : -1;
            return true;
        }
        return super.onLongClick(v);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 重新选择了话题
        MTopicResp topic = (MTopicResp) intent.getSerializableExtra(Constants.EXTRA_EDIT_POST_TOPIC);
        if (topic != null) {
            // mTopic = topic;
            tv_topic.setText(topic.getTopictitle());
            d("III", "to topic id " + topic.getTopicId());
            // if (isModify) {
            oldTopicID = mPost.getTopicId();
            mPost.setTopicId(topic.getTopicId());
            mPost.setTopicTitle(topic.getTopictitle());
            // }
        }
        // 在这里做Plan的判断

        if (intent.getBooleanExtra(Constants.EXTRA_EDIT_POST_IS_PLAN, false)) {
            tv_skin_plan.getCompoundDrawables()[0].setLevel(1);
            PostReleaseReq prr = (PostReleaseReq) intent.getSerializableExtra(Constants.EXTRA_EDIT_POST_PLAN_CONTENT);
            d("III", "effect " + prr.getEffect() + ", part " + prr.getPart());
            mPost.setEffect(prr.getEffect());
            mPost.setPart(prr.getPart());
            mPost.setSelectedToSolution(true);
        }
    }

    private void finishThis() {
        Intent finish = new Intent();
        finish.setAction(BROADCAST_FINISH);
        sendBroadcast(finish);
        finish();
    }

    private void saveBitmap(String path, Bitmap bitmap) {
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("PostArgs", mPost);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finishThis();
        super.onBackPressed();
    }

    @Override
    public void onKeyboardChanged(int state) {
        super.onKeyboardChanged(state);
        mPlanPopup.dismiss();
    }

    private void startReleasePost() {
        showWaitingDialog((AsyncTask) null);
        List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
        // 将帖子中图片抽取出来
        List<String> thumbs = TopicHelper.describeItemContent2Strings(images);
        if (Constants.COMPRESS_POST_IMAGE) {
            thumbs = TopicHelper.compress(thumbs);
        }
        dispatchReleasePost(thumbs);
    }

    private void dispatchReleasePost(List<String> thumbs) {
        // 将空内容去掉，比如没有文字的文本框
        TopicHelper.trimBBSTextBuilder(mTextBuilder);
        if (isModify) {
            if (oldTopicID != 0 && oldTopicID != mPost.getTopicId()) {
                requestMovePost(mPost.getId(), oldTopicID, mPost.getTopicId(), thumbs);
            } else {
                requestUpdatePost(mTextBuilder.getString(), thumbs);
            }
        } else {
            requestReleasePost(mTextBuilder.getString(), thumbs);
        }
    }

    @Deprecated
    private void startReleasePostSync() {
        showWaitingDialog((AsyncTask) null);
        final List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
        int imageSize = images.size();
        // 图片已经上传的话就不需要上传了
        for (int i = 0; i < images.size(); i++) {
            // 如果图片不是本地的就不要上传了
            // 注意，不是本地的，和是服务器的有区别
            // 不是本地的代表是服务器的相对地址或者绝对地址还有可能是错误的地址
            if (TopicHelper.isImagePath(images.get(i).getContent().toString()) != TopicHelper.PATH_LOCAL_SDCARD) {
                images.remove(i--);
            }
        }
        requestUploadImage(images, imageSize);
    }

    private void requestUploadImage(final List<BBSDescribeItem> images, int thumbSize) {
        List<String> imageUrls = TopicHelper.describeItemContent2Strings(images);
        String log = "";// 打印LOG用的，把路径全都拼起来打印
        if (imageUrls != null) {
            for (String str : imageUrls) {
                log += str + ", ";
            }
        }
        d("III_logic", "paths " + log);
        // 代表图片都已上传过了
        if (imageUrls != null && imageUrls.size() == 0) {
            // 一般是编辑的时候用到的，这个情况代表图片全都不是本地的，一般情况来说，也就是图片都是服务器的
            List<String> thumbs = new ArrayList<String>();
            if (imageUrls.size() != thumbSize) {
                // 这个代表
                List<BBSDescribeItem> imgs = TopicHelper.findImages(mTextBuilder);
                for (int i = 0; i < imgs.size(); i++) {
                    // 将服务的绝对地址改为相对地址
                    thumbs.add(imgs.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                }
                // 缩略图的地址
            }
            dispatchReleasePost(thumbs);
            d("III_logic", "帖子中没有图片，或已上传成功");
            return;
        }
        BaseCall<MPostImgResp> node = new BaseCall<MPostImgResp>() {
            @Override
            public void call(MPostImgResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    // 第一步，判断服务器给的图片地址和本地上传的图片数量一样多
                    if (resp.getPostImgURL() != null && resp.getPostImgURL().size() == images.size()) {

                        // 将帖子中SDCard地址全部换成服务器的地址
                        TopicHelper.setStrings2BBSDescribeItemContent(resp.getPostImgURL(), images, ""/*HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP*/);

                        List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
                        List<String> thumbs = new ArrayList<String>(images.size());
                        for (int i = 0; i < images.size(); i++) {
                            thumbs.add(images.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                        }
                        dispatchReleasePost(thumbs);
                        Log.i("III_logic", "图片上传OK");
                    } else {

                        Log.i("III_logic", "图片上传有问题");
                        dimessWaitingDialog();
                    }
                } else {
                    Log.i("III_logic", "图片上传失败 " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(ActivityPostEdit.this, R.string.topic_update_image_failure, Toast.LENGTH_SHORT).show();
                    dimessWaitingDialog();
                }
            }
        };
        addCallback(node);
        prc.uploadPostImgAsync(imageUrls, node);
    }

    private void requestReleasePost(String content, List<String> imgs) {

        final Context context = getApplicationContext();
        PostReleaseReq req = new PostReleaseReq();
        req.setPostTitle(et_title.getText().toString());
        req.setTopicTitle(mPost.getTopicTitle());
        req.setTopicId(mPost.getTopicId());
        req.setPostContent(content);
        req.setPostLevel(0);
        req.setThumbURL(imgs);
        //add by kfir
        UmengUtils.onEventPostCreated(this, mPost.getTopicTitle());
        Log.i("III_logic", "--> " + JsonUtils.toJson(req));
        BaseCall<MPostResp> node = new BaseCall<MPostResp>() {
            @Override
            public void call(MPostResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    Intent data = new Intent();
                    // data.putExtra(Constants.EXTRA_SEND_COMMENT_RESULT, (resp));
                    setResult(RESULT_OK, data);
                    finishThis();
                    Toast.makeText(context, R.string.send_comment_async, Toast.LENGTH_SHORT).show();
                    android.util.Log.i("III", "帖子上传成功");
                } else {
                    android.util.Log.i("III", "release post msg-> " + resp.getMessage());
                    Toast.makeText(context, R.string.send_comment_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(node);
        prc.createPostAsync(req, node);
    }

    private void requestUpdatePost(String content, List<String> imgs) {
        final Context context = getApplicationContext();
        PostReleaseReq req = new PostReleaseReq();
        req.setPostId(mPost.getId());
        req.setPostContent(content);
        req.setThumbURL(imgs);
        req.setPostTitle(et_title.getText().toString());
        req.setPostLevel(0);
        req.setTopicId(mPost.getTopicId());
        Log.i("III_logic", "--> " + JsonUtils.toJson(req));
        BaseCall<MPostResp> node = new BaseCall<MPostResp>() {
            @Override
            public void call(MPostResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    Intent finish = new Intent();
                    finish.setAction(BROADCAST_FINISH);
                    sendBroadcast(finish);
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(context, R.string.send_comment_success, Toast.LENGTH_SHORT).show();
                    d("III", "帖子编辑成功");
                } else {
                    d("III", "modify post msg-> " + resp.getMessage());
                    Toast.makeText(context, R.string.send_comment_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(node);
        prc.updatePostAsync(req, node);
    }


    private void requestMovePost(long postId, long fromTopicId, long toTopicId, final List<String> thumbs) {
        if (postId == 0 || fromTopicId == 0 || toTopicId == 0) {
            return;
        }
        final Context context = getApplicationContext();
        MovePostReq req = new MovePostReq();
        req.setPostId(postId);
        req.setFromTopicId(fromTopicId);
        req.setToTopicId(toTopicId);
        d("III_request", "requestMovePost: " + JsonUtils.toJson(req));
        BaseCall<BaseResp> call = new BaseCall<BaseResp>() {
            @Override
            public void call(BaseResp resp) {

                if (resp != null && resp.isRequestSuccess()) {
                    d("III_reqest", "move success");
                    requestUpdatePost(mTextBuilder.getString(), thumbs);
                } else {
                    dimessWaitingDialog();
                    d("III", "move post msg-> " + resp.getMessage());
                    Toast.makeText(context, R.string.send_comment_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        PostOperation po = ReqFactory.buildInterface(this, PostOperation.class);
        addCallback(call);
        po.movePostAsync(req, call);
    }
    @Override
    public void onLoadingStarted(String s, View view) {
        showWaitingDialog((AsyncTask) null);
        d("III", "started->" + s);
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        dimessWaitingDialog();
        d("III", "failed->" + s);
        Toast.makeText(ActivityPostEdit.this, "找不到该图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        dimessWaitingDialog();
        mNetImage.dismiss();
        mNetImage.setUrl("");
        String path = netImageDir.getAbsolutePath() + File.separatorChar + MD5.getMD5(s) + ".png";
        saveBitmap(path, bitmap);
        // new File(path).deleteOnExit();
        d("III", "saved->" + path);
        if (replaceIndex == -1) {
            View image = mViewBuilder.appendImage(path);
            bindView(image);
            EditText v = (EditText) mViewBuilder.appendForType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK).findViewById(R.id.et_edittext);
            bindView(v);
            v.requestFocus();
        } else {
            int type = mTextBuilder.get(replaceIndex).getType();
            d("III", "index " + replaceIndex + ", path " + path);
            if (type == BBSDescribeItem.TYPE_IMAGE || type == BBSDescribeItem.TYPE_IMAGE_LINK) {
                mViewBuilder.replaceContent(replaceIndex, path);
            }
            replaceIndex = -1;
        }
        Toast.makeText(ActivityPostEdit.this, "图片已保存->" + path, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        d("III", "cancel->" + s);
        dimessWaitingDialog();
    }
}
