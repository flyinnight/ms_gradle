package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;

/**
 * 
 * @author john
 * 删除帖子
 * 删除话题
 */
public abstract class DeletePostTopic {
	//删除帖子
	public abstract void deletePostAsync(DeletePostReq bean,
			BaseCall<BaseResp> call);
	//删除话题
	public abstract void deleteTopicAsync(DeleteTopicReq bean,
			BaseCall<BaseResp> call);
	
	public static class DeletePostReq extends BaseReq {
		public long postId;
		public int postLevel;

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}

		public int getPostLevel() {
			return postLevel;
		}

		public void setPostLevel(int postLevel) {
			this.postLevel = postLevel;
		}		

	}
	
	public static class DeleteTopicReq extends BaseReq {
		public long topicId;

		public long getTopicId() {
			return topicId;
		}

		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}

	}

}
