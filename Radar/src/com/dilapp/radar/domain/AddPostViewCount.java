package com.dilapp.radar.domain;


/**
 * 
 * @author tony 新增帖子的浏览数目
 *
 */
public abstract class AddPostViewCount {
	public abstract void addPostViewCountAsync(AddPostViewCountReq bean,
			BaseCall<BaseResp> call);

	public static class AddPostViewCountReq extends BaseReq {
		public long postId;
		//新增浏览数目
		public long viewCount;

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}

		public long getViewCount() {
			return viewCount;
		}

		public void setViewCount(long viewCount) {
			this.viewCount = viewCount;
		}		

	}

}
