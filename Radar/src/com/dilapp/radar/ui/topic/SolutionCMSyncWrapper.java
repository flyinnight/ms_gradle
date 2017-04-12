package com.dilapp.radar.ui.topic;

import android.util.SparseArray;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionDetailData.*;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.ContextState.*;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;
import static com.dilapp.radar.textbuilder.utils.L.w;

/**
 * Created by husj1 on 2015/10/16.
 */
public class SolutionCMSyncWrapper extends SolutionCreateUpdate {

/*
    private State uploadCover = new State() {
        @Override
        public void handle(ContextState context, Object... params) {

        }
    };
    private ContextState sctx = new ContextState(states[0]);*/
    private SolutionCreateUpdate mSCU;

    public SolutionCMSyncWrapper(SolutionCreateUpdate scu) {
        this.mSCU = scu;
    }

    @Override
    public void solutionUplCoverImgAsync(String imgs, BaseCall<CoverImgResp> call) {
        mSCU.solutionUplCoverImgAsync(imgs, call);
    }

    @Override
    public void solutionUplTextImgAsync(List<String> imgs, BaseCall<TextImgResp> call) {
        mSCU.solutionUplTextImgAsync(imgs, call);
    }

    @Override
    public void solutionCreateAsync(final SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {
        uploadCover(false, bean, call);
        // mSCU.solutionCreateAsync(bean, call);
    }

    @Override
    public void solutionUpdateAsync(SolutionUpdateReq bean, BaseCall<MSolutionResp> call) {
        uploadCover(true, bean, call);
        // mSCU.solutionUpdateAsync(bean, call);
    }

    @Override
    public void solutionDeleteLocalItemAsync(long localSolutionId, BaseCall<BaseResp> call) {
        mSCU.solutionDeleteLocalItemAsync(localSolutionId, call);
    }

    @Override
    public void solutionDeleteAllLocalDataAsync(BaseCall<BaseResp> call) {
        mSCU.solutionDeleteAllLocalDataAsync(call);
    }

    private void uploadCover(final boolean isUpdate, final SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {

        if (TopicHelper.isImagePath(bean.getCoverUrl()) == TopicHelper.PATH_LOCAL_SDCARD) {
            d("III_requesting", "ready upload cover, cover is " + bean.getCoverUrl());
            solutionUplCoverImgAsync(bean.getCoverUrl(), new BaseCall<CoverImgResp>() {
                @Override
                public void call(CoverImgResp resp) {
                    if (resp != null && resp.isRequestSuccess() &&
                            resp.getCoverImgUrl() != null &&
                            resp.getCoverThumbImgUrl() != null &&
                            !"".equals(resp.getCoverImgUrl().trim()) &&
                            !"".equals(resp.getCoverThumbImgUrl().trim())) {
                        bean.setCoverUrl(resp.getCoverImgUrl());
                        bean.setCoverThumbUrl(resp.getCoverThumbImgUrl());
                        d("III_requesting", "upload cover success. cover is "
                                + resp.getCoverImgUrl() + ", thumb is "
                                + resp.getCoverThumbImgUrl());
                        uploadImageInContent(isUpdate, bean, call);
                    } else {
                        String log = resp != null ? resp.getStatus() : null;
                        log += ", " + (resp != null ? resp.getCoverImgUrl() : null);
                        log += ", " + (resp != null ? resp.getCoverThumbImgUrl() : null);
                        d("III_requesting", "upload cover failure. " + log);
                        callFailure(call, resp, "upload cover failure in the application");
                    }
                }
            });
        } else {
            d("III_requesting", "Don't need upload cover. cover is " + bean.getCoverUrl());
            uploadImageInContent(isUpdate, bean, call);
        }
    }

    private void uploadImageInContent(final boolean isUpdate, final SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {
        d("III_requesting", "ready upload images in content");
        final BBSTextBuilder build = new BBSTextBuilderImpl(bean.getContent());
        if (build.getBBSDescribe() == null || build.getBBSDescribe().size() == 0) {
            // 没有图片，直接发布
            d("III_requesting", "content is not image.");
            requestPlan(isUpdate, bean, call);
        } else {
            final List<String> images = new ArrayList<String>();
            final SparseArray<String> imagesSparse = new SparseArray<String>();

            String log = "";
            // 将图片冲帖子内容中抽取出来
            for (int i = 0; i < build.size(); i++) {
                BBSDescribeItem item = build.get(i);
                if (item.getType() == TopicHelper.TYPE_PLAN_STEP) {
                    String image = (String) item.getParam("image_01");
                    if (TopicHelper.isImagePath(image) == TopicHelper.PATH_LOCAL_SDCARD) {
                        images.add(image);
                        imagesSparse.put(i, image);
                        log += i + ":" + image + ", ";
                    }
                }
            }

            if (imagesSparse.size() == 0) {
                // 没有图片，直接发布
                d("III_requesting", "images in content is upload finished.");
                requestPlan(isUpdate, bean, call);
            } else {
                if (Constants.COMPRESS_POST_IMAGE) {
                    List<String> compress = TopicHelper.compress(images);
                    if (compress != null && compress.size() == images.size()) {
                        images.clear();
                        images.addAll(compress);
                    }
                }
                d("III_requesting", "upload images: " + log);
                // 上传图片
                solutionUplTextImgAsync(images, new BaseCall<TextImgResp>() {
                    @Override
                    public void call(TextImgResp resp) {
                        if (resp != null
                                && resp.isRequestSuccess()
                                && resp.getTextImgUrl() != null
                                && resp.getTextImgUrl().size() == images.size()) {

                            String log = "";
                            // 上传没有任何问题
                            for (int i = 0; i < imagesSparse.size(); i++) {
                                // 这里是为了减少不必要的循环，imagesSparse里面已经排序好保存内容中需要上传图片的索引
                                String s = resp.getTextImgUrl().get(i);
                                build.get(imagesSparse.keyAt(i)).putParam("image_01", s);
                                log += i + ":" + s + ", ";
                            }
                            bean.setContent(build.getString());
                            d("III_requesting", "Upload images are finished, It's " + log);
                            requestPlan(isUpdate, bean, call);
                        } else {
                            d("III_requesting", "upload images failure");
                        /*if (resp.getTextImgUrl().size() != images.size()) {
                            w("III_requesting", "upload images have a question TextImageSize "
                                    + images.size() + ", RespImageSize " + resp.getTextImgUrl().size());
                        }*/
                            callFailure(call, resp, "upload images failure in the application");
                        }
                    }
                });
            }

        }
    }

    private void requestPlan(boolean isUpdate, final SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {
        if (isUpdate) {
           mSCU.solutionUpdateAsync((SolutionUpdateReq) bean, call);
        } else {
            mSCU.solutionCreateAsync(bean, call);
        }
    }

    private void callFailure(BaseCall<MSolutionResp> call, BaseResp resp, String msg) {

        if (call != null && !call.cancel) {
            MSolutionResp r = new MSolutionResp();

            if (resp != null) {
                r.setStatusCode(resp.getStatusCode());
                r.setStatus(resp.getStatus());
                r.setMessage(resp.getMessage());
            } else {
                r.setStatus("FAILED");
                r.setMessage(msg);
            }
            call.call(r);
            call.cancel = false;
        }
    }
}
