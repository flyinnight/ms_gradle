package com.dilapp.radar.domain;


/**
 * 
 * @author john
 * 用户对帖子点赞 
 * 用户反感帖子
 */
public abstract class LikeDislikePost {
	public abstract void likePostAsync(LikeDislikePostReq bean,
			BaseCall<BaseResp> call);
	
	public abstract void dislikePostAsync(LikeDislikePostReq bean,
			BaseCall<BaseResp> call);

	public static class LikeDislikePostReq extends BaseReq {
		public long postId;
		public boolean like;
		public boolean dislike;

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}

		public boolean getLike() {
			return like;
		}

		public void setLike(boolean like) {
			this.like = like;
		}

		public boolean getDislike() {
			return dislike;
		}

		public void setDislike(boolean dislike) {
			this.dislike = dislike;
		}
	}

}
