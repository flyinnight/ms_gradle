package com.dilapp.radar.ui.topic;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.PathUtils;

import java.util.ArrayList;
import java.util.List;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * 为了随时切换发帖方式(异步||同步)
 */
public class PostReleaseImgUploadWrapper extends PostReleaseCallBack {

    private PostReleaseCallBack mPRCB;

    public PostReleaseImgUploadWrapper(PostReleaseCallBack pr) {
        mPRCB = pr;
    }

    @Override
    public void createPostAsync(final PostReleaseReq bean, final BaseCall<GetPostList.MPostResp> call) {
        before(bean, call, new DisptachCallback() {
            @Override
            public void disptach(List<String> thumbs) {
                if (mPRCB != null) {
                    bean.setThumbURL(thumbs);
                    mPRCB.createPostAsync(bean, call);
                }
            }
        });
    }

    @Override
    public void updatePostAsync(final PostReleaseReq bean, final BaseCall<GetPostList.MPostResp> call) {
        before(bean, call, new DisptachCallback() {
            @Override
            public void disptach(List<String> thumbs) {
                if (mPRCB != null) {
                    bean.setThumbURL(thumbs);
                    mPRCB.updatePostAsync(bean, call);
                }
            }
        });
    }

    @Override
    public void uploadPostImgAsync(List<String> imgs, BaseCall<MPostImgResp> call) {
        if (mPRCB != null) {
            mPRCB.uploadPostImgAsync(imgs, call);
        }
    }

    @Override
    public void deleteLocalPostAsync(PostReleaseReq bean, BaseCall<GetPostList.MPostResp> call) {
        if (mPRCB != null) {
            mPRCB.deleteLocalPostAsync(bean, call);
        }

    }

    @Override
    public void deleteAllSendingPostAsync(BaseReq bean, BaseCall<BaseResp> call) {
        if (mPRCB != null) {
            mPRCB.deleteAllSendingPostAsync(bean, call);
        }
    }

    private void before(final PostReleaseReq bean, final BaseCall<GetPostList.MPostResp> call, final DisptachCallback run) {

        final BBSTextBuilder textBulder = new BBSTextBuilderImpl(bean.getPostContent());
        final List<BBSDescribeItem> images = TopicHelper.findImages(textBulder);
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

        // --------------- 华丽的分割线 ---------------

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
            if (imageUrls.size() != imageSize) {
                // 这个代表
                List<BBSDescribeItem> imgs = TopicHelper.findImages(textBulder);
                for (int i = 0; i < imgs.size(); i++) {
                    // 将服务的绝对地址改为相对地址
                    thumbs.add(imgs.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                }
                // 缩略图的地址
            }
            if (run != null) {
                run.disptach(thumbs);
            }
            d("III_logic", "帖子中没有图片，或已上传成功");
            return;
        }
        if (Constants.COMPRESS_POST_IMAGE) {
            imageUrls = TopicHelper.compress(imageUrls);
        }

        BaseCall<MPostImgResp> node = new BaseCall<MPostImgResp>() {
            @Override
            public void call(MPostImgResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    // 第一步，判断服务器给的图片地址和本地上传的图片数量一样多
                    if (resp.getPostImgURL() != null && resp.getPostImgURL().size() == images.size()) {

                        // 将帖子中SDCard地址全部换成服务器的地址
                        TopicHelper.setStrings2BBSDescribeItemContent(resp.getPostImgURL(), images, ""/*HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP*/);

                        List<BBSDescribeItem> images = TopicHelper.findImages(textBulder);
                        List<String> thumbs = new ArrayList<String>(images.size());
                        for (int i = 0; i < images.size(); i++) {
                            thumbs.add(images.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                        }
                        bean.setPostContent(textBulder.getString());
                        if (run != null) {
                            run.disptach(thumbs);
                        }
                        d("III_logic", "图片上传OK");
                    } else {
                        if (call != null && !call.cancel) {
                            call.call(copy(resp));
                        }
                        call.cancel = false;
                        // 正常情况下，不会走到这里，除非服务器有问题
                        d("III_logic", "图片上传有问题");
                        // dimessWaitingDialog();
                    }
                } else {
                    d("III_logic", "图片上传失败 " + (resp != null ? resp.getMessage() : null));
                    if (call != null && !call.cancel) {
                        call.call(copy(resp));
                    }
                    call.cancel = false;
                }
            }
        };
        // addCallback(node);
        uploadPostImgAsync(imageUrls, node);
    }

    private GetPostList.MPostResp copy(MPostImgResp resp) {
        GetPostList.MPostResp bc = null;
        if (resp != null) {
            bc = new GetPostList.MPostResp();
            bc.setStatusCode(resp.getStatusCode());
            bc.setStatus(resp.getStatus());
            bc.setMessage(resp.getMessage());
            bc.setSuccess(resp.isSuccess());
        }
        return bc;
    }

    public interface DisptachCallback {
        void disptach(List<String> thumbs);
    }
}
