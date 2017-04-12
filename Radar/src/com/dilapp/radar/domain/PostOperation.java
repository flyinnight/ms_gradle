package com.dilapp.radar.domain;


/**
 * 
 * @author john
 * 移帖
 * 置顶
 */
public abstract class PostOperation {
	//移帖
	public abstract void movePostAsync(MovePostReq bean, BaseCall<BaseResp> call);
	//置顶
	public abstract void topPostAsync(TopPostReq bean, BaseCall<BaseResp> call);
	//收藏
	public abstract void storeupPostAsync(StoreupPostReq bean, BaseCall<BaseResp> call);

	public static class MovePostReq extends BaseReq {
		private long postId;
		private long fromTopicId;
		private long toTopicId;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}

		public long getFromTopicId() {
			return fromTopicId;
		}
		public void setFromTopicId(long fromTopicId) {
			this.fromTopicId = fromTopicId;
		}
		
		public long getToTopicId() {
			return toTopicId;
		}
		public void setToTopicId(long toTopicId) {
			this.toTopicId = toTopicId;
		}
	}
	
	public static class TopPostReq extends BaseReq {
		private long postId;
		private boolean topFlag;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public boolean getTopFlag() {
			return topFlag;
		}
		public void setTopFlag(boolean topFlag) {
			this.topFlag = topFlag;
		}
	}

	public static class StoreupPostReq extends BaseReq {
		private long postId;
		private boolean storeup;
		
		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}

		public boolean getStoreUp() {
			return storeup;
		}
		public void setStoreUp(boolean storeup) {
			this.storeup = storeup;
		}
	}
}
