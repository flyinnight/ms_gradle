package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.CreateTopic.TopicReleaseReq;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListResp;

/**
 * 
 * @author john
 *	预置话题相关
 */
public abstract class  PresetTopic {

	//上传图片
	public abstract void uploadTopicImgAsync(List<String> imgs, BaseCall<TopicReleaseResp> call);
	//增加预置话题信息
	public abstract void createPresetTopicAsync(TopicReleaseReq bean, BaseCall<TopicReleaseResp> call);
	//返回预制话题列表
	public abstract void getPresetTopicListByTypeAsync(BaseReq bean, BaseCall<TopicListResp> call, int type);
}
