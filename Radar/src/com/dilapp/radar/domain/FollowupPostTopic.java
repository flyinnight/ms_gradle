package com.dilapp.radar.domain;

/**
 * 
 * @author john
 * 关注帖子
 * 关注话题
 */
public abstract class FollowupPostTopic {
	//关注帖子
	public abstract void followupPostAsync(FollowupPostReq bean,
			BaseCall<BaseResp> call);
	//关注话题
	public abstract void followupTopicAsync(FollowupTopicReq bean,
			BaseCall<BaseResp> call);
	
	public static class FollowupPostReq extends BaseReq {
		public long postId;
		public boolean followup;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public boolean getFollowup() {
			return followup;
		}
		public void setFollowup(boolean followup) {
			this.followup = followup;
		}		

	}
	
	
	public static class FollowupTopicReq extends BaseReq {
		public long topicId;
		public boolean followup;

		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public boolean getFollowup() {
			return followup;
		}
		public void setFollowup(boolean followup) {
			this.followup = followup;
		}		

	}

}
