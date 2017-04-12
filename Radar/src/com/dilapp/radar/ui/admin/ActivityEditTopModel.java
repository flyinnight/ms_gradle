package com.dilapp.radar.ui.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.Banner.CreateBannerReq;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PostCollection;
import com.dilapp.radar.domain.Banner.UploadBannerImgResp;
import com.dilapp.radar.domain.PostCollection.EditCollectionReq;
import com.dilapp.radar.domain.PostCollection.UploadCollectionImgResp;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.ABImageProcess;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityEditTopModel extends BaseActivity implements OnClickListener, ImageLoadingListener {

    private TopItemParcel mParcel = null;
    private TitleView mTitle;

    private static int REQ_IMAGE = 20;

    private TextView mTopTitle;
    private TextView mTopicId;
    private TextView mPostId;
    private TextView mPlanId;
    private EditText mAdvEdit;
    private EditText mPriorityEdit;
    private LinearLayout mPriorityLayout;
    private ImageView mBannerImage;
    private Button mBtnSend;

    private String mAdvWords;
    private String mBannerPath;
    private int mPriority = 1;

    private PostCollection mCollection;
    private Banner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_top_layout);

        mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
        mTitle.setLeftText(R.string.cancel, this);

        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopicId = (TextView) findViewById(R.id.top_topicid);
        mPostId = (TextView) findViewById(R.id.top_postid);
        mPlanId = (TextView) findViewById(R.id.top_planid);
        mAdvEdit = (EditText) findViewById(R.id.adv_input);
        mPriorityEdit = (EditText) findViewById(R.id.priority_input);
        mPriorityLayout = (LinearLayout) findViewById(R.id.priority_layout);
        mBannerImage = (ImageView) findViewById(R.id.banner_image_input);
        mBannerImage.setOnClickListener(this);
        mBtnSend = (Button) findViewById(R.id.confirm_top);
        mBtnSend.setOnClickListener(this);

        mCollection = ReqFactory.buildInterface(this, PostCollection.class);
        mBanner = ReqFactory.buildInterface(this, Banner.class);

        addCallback(mCollectionImgResp);
        addCallback(mBannerImgResp);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        resumeParcel();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void resumeParcel() {
        mParcel = (TopItemParcel) getIntent().getSerializableExtra(Constants.EXTRA_EDIT_TOP_CONTENT);
        if (mParcel == null) {
            Slog.e("Can not find TopItemParcel and finish()!!!!!");
            finish();
            return;
        }

        if (mParcel.getType() == 0) {
            mTitle.setCenterText(R.string.top_post_title, null);
            mTopTitle.setText(R.string.edit_post_top);
            mPriorityLayout.setVisibility(View.GONE);
        } else {
            mTitle.setCenterText(R.string.top_banner_title, null);
            mTopTitle.setText(R.string.edit_post_banner);
            mPriorityLayout.setVisibility(View.VISIBLE);
        }
        mTopicId.setText("TOPIC_ID : " + mParcel.getTopicId());
        mPostId.setText("POST_ID : " + mParcel.getPostId());
        mPlanId.setText("PLAN_ID : " + mParcel.getSolutionId());

        if (mParcel.getSolutionId() != 0 &&
                mParcel.getTopicId() == 0 &&
                mParcel.getPostId() == 0) {
            int pathType = TopicHelper.isImagePath(mParcel.getCover());
            if (pathType == TopicHelper.PATH_LOCAL_SDCARD) {
                ImageLoader.getInstance().displayImage(
                        "file://" + mParcel.getCover(),
                        mBannerImage, this);
            } else if (pathType != TopicHelper.PATH_UNKNOWN) {
                ImageLoader.getInstance().displayImage(
                        TopicHelper.wrappeImagePath(mParcel.getCover()),
                        mBannerImage, this);
            }
        }
    }

    private void handleSendStart() {
        if (mParcel == null) {
            Slog.e("Can not send by Parcel is NULL!!!");
            return;
        }
        if (TextUtils.isEmpty(mBannerPath)) {
            Slog.e("Can not send by BannerPath is NULL!!!!!");
        }

        String sPriority = mPriorityEdit.getText().toString();
        if (!TextUtils.isEmpty(sPriority)) {
            mPriority = Integer.parseInt(sPriority);
        }
        mAdvWords = mAdvEdit.getText().toString();

        if (mParcel.getType() == 0) {
            if (TopicHelper.isImagePath(mBannerPath) == TopicHelper.PATH_LOCAL_SDCARD) {
                List<String> imgList = new ArrayList<String>();
                imgList.add(mBannerPath);
                mCollection.uploadCollectionImgAsync(imgList, mCollectionImgResp);
            } else {
                UploadCollectionImgResp resp = new UploadCollectionImgResp();
                resp.setStatus("SUCCESS");
                resp.setMessage("local callback!");
                resp.setPicUrl(mBannerPath);
                mCollectionImgResp.call(resp);
            }
        } else {
            List<String> imgList = new ArrayList<String>();
            imgList.add(mBannerPath);
            if (TopicHelper.isImagePath(mBannerPath) == TopicHelper.PATH_LOCAL_SDCARD) {
                mBanner.uploadBannerImgAsync(imgList, mBannerImgResp);
            } else {
                UploadBannerImgResp resp = new UploadBannerImgResp();
                resp.setStatus("SUCCESS");
                resp.setMessage("local callback!");
                resp.setBannerImgURL(imgList);
                mBannerImgResp.call(resp);
            }
        }
    }

    private BaseCall<UploadCollectionImgResp> mCollectionImgResp = new BaseCall<PostCollection.UploadCollectionImgResp>() {

        @Override
        public void call(UploadCollectionImgResp resp) {
            // TODO Auto-generated method stub
            if (resp.isRequestSuccess()) {
                String surl = resp.getPicUrl();
                EditCollectionReq mReq = new EditCollectionReq();
                mReq.setpicUrl(surl);
                if (mParcel.getTopicId() != 0 && mParcel.getPostId() != 0) {
                    mReq.setTopicId(mParcel.getTopicId());
                    mReq.setPostId(mParcel.getPostId());
                } else if (mParcel.getSolutionId() != 0) {
                    mReq.setSolutionId(mParcel.getSolutionId());
                }
                if (!TextUtils.isEmpty(mAdvWords)) {
                    mReq.setSlogan(mAdvWords);
                }
                Slog.i("start editPostCollectionAsync : " + surl + "  " + mParcel.getTopicId() + "  " + mParcel.getPostId() + " " + mAdvWords);
                BaseCall<BaseResp> node = new BaseCall<BaseResp>() {

                    @Override
                    public void call(BaseResp resp) {
                        // TODO Auto-generated method stub
                        if (!resp.isRequestSuccess()) {
                            Slog.e("editPostCollectionAsync Resp  Failed!!!");
                            Toast.makeText(ActivityEditTopModel.this, "上传失败!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Slog.e("editPostCollectionAsync Resp  SUCCESS!!!");
                            Toast.makeText(ActivityEditTopModel.this, "上传成功!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                };
                addCallback(node);
                mCollection.editPostCollectionAsync(mReq, node);
            } else {
                Slog.e("UploadCollectionImgResp  Failed!!!");
                Toast.makeText(ActivityEditTopModel.this, "上传失败!!!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BaseCall<UploadBannerImgResp> mBannerImgResp = new BaseCall<Banner.UploadBannerImgResp>() {

        @Override
        public void call(UploadBannerImgResp resp) {
            // TODO Auto-generated method stub
            if (resp.isRequestSuccess()) {
                String surl = resp.getBannerImgURL().get(0);
                CreateBannerReq mReq = new CreateBannerReq();
                mReq.setBannerUrl(surl);
                if (mParcel.getTopicId() != 0 && mParcel.getPostId() != 0) {
                    mReq.setTopicId(mParcel.getTopicId());
                    mReq.setPostId(mParcel.getPostId());
                } else if (mParcel.getSolutionId() != 0) {
                    mReq.setSolutionId(mParcel.getSolutionId());
                }
                mReq.setPriority(mPriority);
                if (!TextUtils.isEmpty(mAdvWords)) {
                    mReq.setSlogan(mAdvWords);
                }
                Slog.i("start editPostCollectionAsync : " + surl + "  " + mParcel.getTopicId() + "  " + mParcel.getPostId() + " " + mAdvWords);
                BaseCall<BaseResp> node = new BaseCall<BaseResp>() {

                    @Override
                    public void call(BaseResp resp) {
                        // TODO Auto-generated method stub
                        if (!resp.isRequestSuccess()) {
                            Slog.e("createBannerAsync Resp  Failed!!!");
                            Toast.makeText(ActivityEditTopModel.this, "上传失败!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Slog.e("createBannerAsync Resp  SUCCESS!!!");
                            Toast.makeText(ActivityEditTopModel.this, "上传成功!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                };
                addCallback(node);
                mBanner.createBannerAsync(mReq, node);
            } else {
                Slog.e("UploadBannerImgResp  Failed!!!");
                Toast.makeText(ActivityEditTopModel.this, "上传失败!!!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_IMAGE && resultCode == RESULT_OK) {
            mBannerPath = PathUtils.getPath(getApplication(), data.getData());
            if (!TextUtils.isEmpty(mBannerPath)) {
                mBannerImage.setImageBitmap(ABImageProcess.getSmallBitmap(mBannerPath, 400, 200));
//				 mBannerImage.setImageURI(Uri.fromFile(new File(mBannerPath)));
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                finish();
                break;
            case R.id.banner_image_input:
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, REQ_IMAGE);
                break;
            case R.id.confirm_top:
                handleSendStart();
                break;
        }
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        mBannerPath = mParcel.getCover();
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }
}
