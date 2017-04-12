package com.dilapp.radar.ui.found;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.CreateTopic;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseReq;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.AndroidBugsSolution;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.UmengUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ActivityTopicEdit extends BaseFragmentActivity implements View.OnClickListener {

    private static int REQ_IMAGE = 20;

    private TitleView mTitle;
    private EditText et_title;
    private EditText et_introduce;
    private ImageView iv_image;
    private TextView tv_image;

    private boolean isModify;
    private MTopicResp update;

    private CreateTopic createTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_edit);
        AndroidBugsSolution.assistActivity(this, null);

        createTopic = ReqFactory.buildInterface(this, CreateTopic.class);

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftIcon(R.drawable.btn_back, this);
        mTitle.setCenterText(R.string.found_create_topic_title, null);
        mTitle.setRightText(R.string.finish, this);

        et_title = findViewById_(R.id.et_title);
        et_introduce = findViewById_(R.id.et_introduce);
        iv_image = findViewById_(R.id.iv_image);
        tv_image = findViewById_(R.id.tv_image);

        // 是否是修改模式
        Intent data = getIntent();
        if (data.getBooleanExtra(Constants.EXTRA_TOPIC_EDIT_IS_MODIFY, false)) {
            isModify = true;
            mTitle.setCenterText(R.string.found_modify_topic_title, null);
            update = (MTopicResp) data.getSerializableExtra(Constants.EXTRA_TOPIC_EDIT_CONTENT);
            if (update == null || update.getTopicId() < 1) {
                w("III", "修改模式，但是没有要修改对象");
                finish();
            } else if (savedInstanceState == null) {// 反正下面都要覆盖，这里就不浪费操作了
                setUIFromData(update.getTopictitle(), update.getContent(), update.getTopicimg()[0]);
            }
        }

        if (savedInstanceState != null) {
            // 现场恢复，防止内存不足时Activity被销毁
            String title = savedInstanceState.getString("title");
            String intro = savedInstanceState.getString("intro");
            String path = savedInstanceState.getString("path");
            setUIFromData(title, intro, path);

        }
        Selection.setSelection(et_title.getText(), et_title.getText().length());
    }

    private void setUIFromData(String title, String intro, String path) {
        et_title.setText(title);
        et_introduce.setText(intro);
        iv_image.setTag(path);
        int flag = TopicHelper.isImagePath(path);
        if (path != null && flag != TopicHelper.PATH_UNKNOWN) {
            tv_image.setText(R.string.topic_replce_image);
            if (flag == TopicHelper.PATH_LOCAL_SDCARD) {
                ImageLoader.getInstance().displayImage("file://" + path, iv_image);
            } else {
                ImageLoader.getInstance().displayImage(
                        TopicHelper.wrappeImagePath(path),
                        iv_image);
            }
        }
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
                String title = et_title.getText().toString();
                String intro = et_introduce.getText().toString();
                String path = (String) iv_image.getTag();
                if ("".equals(title.trim())) {
                    Toast.makeText(this, R.string.topic_please_input_title, Toast.LENGTH_SHORT).show();
                    break;
                }
                if ("".equals(intro.trim())) {
                    Toast.makeText(this, R.string.topic_please_input_content, Toast.LENGTH_SHORT).show();
                    break;
                }
                if (path == null || "".equals(path.trim())) {
                    Toast.makeText(this, R.string.topic_please_choice_image, Toast.LENGTH_SHORT).show();
                    break;
                }
                dispatchCreateTopic(title, intro, path);
                break;
            }
            case R.id.vg_update_topic: {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, REQ_IMAGE);
                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_IMAGE && resultCode == RESULT_OK) {
            tv_image.setText(R.string.topic_replce_image);
            String path = PathUtils.getPath(getApplication(), data.getData());
            d("III_onActivityResult", "path=" + path);
            ImageLoader.getInstance().displayImage("file://" + path, iv_image);
            iv_image.setTag(path);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", et_title.getText().toString());
        outState.putString("intro", et_introduce.getText().toString());
        outState.putString("path", (String) iv_image.getTag());
    }

    private void dispatchCreateTopic(String title, String intro, String path) {

        showWaitingDialog((AsyncTask) null);
        if (TopicHelper.isImagePath(path) == TopicHelper.PATH_LOCAL_SDCARD) {
            // 如果是本地图片，就先上传
            requestUploadImage(path, title, intro);
        } else if (TopicHelper.isImagePath(path) != TopicHelper.PATH_LOCAL_SDCARD) {

            // 否则就直接新建帖子
            requestOptionTopic(title, intro, path);
        }
    }

    private void requestUploadImage(String path, final String title, final String intro) {

        d("III_request", "path " + path);
        List<String> temp = new ArrayList<String>(1);
        temp.add(path);
        BaseCall<TopicReleaseResp> call = new BaseCall<TopicReleaseResp>() {
            @Override
            public void call(TopicReleaseResp resp) {
                if (resp != null &&
                        resp.isRequestSuccess() &&
                        TopicHelper.isImagePath(resp.getTopicImgUrl()) != TopicHelper.PATH_LOCAL_SDCARD &&
                        TopicHelper.isImagePath(resp.getTopicImgUrl()) != TopicHelper.PATH_UNKNOWN) {

                    iv_image.setTag(resp.getTopicImgUrl());
                    requestOptionTopic(title, intro, resp.getTopicImgUrl());
                    d("III_request", "图片上传成功");
                } else {
                    d("III_request", "图片上传失败 " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(), R.string.topic_create_failure, Toast.LENGTH_SHORT).show();
                    dimessWaitingDialog();
                }
            }
        };
        addCallback(call);
        createTopic.uploadTopicImgAsync(temp, call);
    }

    /**
     * 包含修改与新建
     *
     * @param title
     * @param intro
     * @param url
     */
    private void requestOptionTopic(String title, String intro, String url) {

        d("III_request", "url " + url);
        UmengUtils.onEventTopicCreated(this, title);
        BaseCall<TopicReleaseResp> call = new BaseCall<TopicReleaseResp>() {
            @Override
            public void call(TopicReleaseResp resp) {
                dimessWaitingDialog();
                if (resp != null && resp.isRequestSuccess()) {
                    setResult(RESULT_OK);
                    finish();
                    Toast.makeText(getApplicationContext(), R.string.topic_create_success, Toast.LENGTH_SHORT).show();
                    d("III_request", "话题操作成功");
                } else {
                    d("III_request", "话题操作失败 " + (resp != null ? resp.getMessage() : null));
                    Toast.makeText(getApplicationContext(), R.string.topic_create_failure, Toast.LENGTH_SHORT).show();
                }
            }
        };
        addCallback(call);
        TopicReleaseReq trr = new TopicReleaseReq();
        trr.setTopicTitle(title);
        trr.setTopicDes(intro);
        trr.setTopicImgUrl(url);
        if (isModify) {
            trr.setTopicId(update.getTopicId());
            createTopic.updateTopicAsync(trr, call);
        } else {
            createTopic.createTopicAsync(trr, call);
        }

    }

}
