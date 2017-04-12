package com.dilapp.radar.ui.share;

import java.util.ArrayList;
import java.util.List;

import com.dilapp.radar.R;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.ActivityPostEditPre;
import com.dilapp.radar.ui.topic.PresetPostModel;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.UmengUtils;
import com.dilapp.radar.wifi.Content;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class ShareBoard extends PopupWindow implements OnClickListener{
	
	private UMSocialService mController = UMServiceFactory.getUMSocialService(UmengUtils.DESCRIPTOR);
    private Activity mActivity;
    
    public ShareBoard(Activity activity){
    		super(activity);
        this.mActivity = activity;
        initView(activity);
    }
    
    private void initView(Context context){
    		View rootView = LayoutInflater.from(context).inflate(R.layout.custom_board, null);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.sina).setOnClickListener(this);
        rootView.findViewById(R.id.sradar).setOnClickListener(this);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
        switch (id) {
            case R.id.wechat:
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.sina:
                performShare(SHARE_MEDIA.SINA);
                break;
            case R.id.sradar:
//            		Slog.e("Click sradar!!!!");
            		performShareRadar();
//                performShare(SHARE_MEDIA.QZONE);
                break;
            default:
                break;
        }
	}
	
	private void performShareRadar(){
		Slog.d("performShareRadar");
		PresetPostModel mModel = new PresetPostModel();
		List<BBSDescribeItem> mItems = new ArrayList<BBSDescribeItem>();
		mModel.setTitle("快来看看我的测试结果吧");
		BBSDescribeItem item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK);
		item.setContent("结果蛛网图:");
		mItems.add(item);
		item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_IMAGE_LINK);
		item.setContent(PathUtils.SHARE_IMAGE_PATH);
		mItems.add(item);
		item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK);
		item.setContent("皮肤拍照（普通光）:");
		mItems.add(item);
		item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_IMAGE_LINK);
		item.setContent(Content.RGB_PATH);
		mItems.add(item);
		item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_TEXT_EMOJI_LINK);
		item.setContent("皮肤拍照（偏振光）:");
		mItems.add(item);
		item = new BBSDescribeItem();
		item.setType(BBSDescribeItem.TYPE_IMAGE_LINK);
		item.setContent(Content.PL_PATH);
		mItems.add(item);
		mModel.setList(mItems);
		
		Intent intent = new Intent(mActivity, ActivityPostEditPre.class);
		intent.putExtra(Constants.EXTRA_EDIT_POST_PRESET_CONTENT, mModel);
		mActivity.startActivity(intent);
		dismiss();
		
	}
	
	private void performShare(SHARE_MEDIA platform) {
        mController.postShare(mActivity, platform, new SnsPostListener() {

            @Override
            public void onStart() {

            }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                    String showText = platform.toString();
                    if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                        showText += "平台分享成功";
                    } else {
                        showText += "平台分享失败";
                    }
                    Slog.d("ShreBoard  : "+showText);
//                Toast.makeText(mActivity, showText, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

}
