package com.dilapp.radar.domain;

import java.util.List;

/**
 * 
 * @author john
 *	创建话题
 */
public abstract class  CreateTopic {

	//上传图片
	public abstract void uploadTopicImgAsync(List<String> imgs,
			BaseCall<TopicReleaseResp> call);
	//创建话题信息
	public abstract void createTopicAsync(TopicReleaseReq bean, BaseCall<TopicReleaseResp> call);
	//更新话题信息
	public abstract void updateTopicAsync(TopicReleaseReq bean, BaseCall<TopicReleaseResp> call);

	/**
	 * 发布话题 Bean
	 * 
	 * @author john
	 */
	public static class TopicReleaseReq extends BaseReq {
		// 标题
		private String topicTitle;
		// 内容
		private String topicDes;
		// url
		private String topicImgUrl;

		private long topicId;

		public long getTopicId() {
			return topicId;
		}

		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public String getTopicTitle() {
			return topicTitle;
		}

		public void setTopicTitle(String topicTitle) {
			this.topicTitle = topicTitle;
		}

		public String getTopicDes() {
			return topicDes;
		}

		public void setTopicDes(String topicDes) {
			this.topicDes = topicDes;
		}

		public String getTopicImgUrl() {
			return topicImgUrl;
		}

		public void setTopicImgUrl(String topicImgUrl) {
			this.topicImgUrl = topicImgUrl;
		}

	}

	/**
	 * 发布话题 成功与否
	 * 
	 * @author john
	 */
	public static class TopicReleaseResp extends BaseResp {
		private String topicImgUrl;
		private String roles;
		private long topicId;
		private String userId;

		public String getTopicImgUrl() {
			return topicImgUrl;
		}

		public void setTopicImgUrl(String topicImgUrl) {
			this.topicImgUrl = topicImgUrl;
		}
		
		public String getRoles() {
			return roles;
		}

		public void setRoles(String roles) {
			this.roles = roles;
		}
		
		public long getTopicId() {
			return topicId;
		}

		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
	}
}
