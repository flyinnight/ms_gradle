package com.dilapp.radar.domain;

/**
 * 
 * @author john
 * （取消）关注用户
 */
public abstract class FollowUser {
	public abstract void followUserAsync(FollowUserReq bean,
			BaseCall<BaseResp> call);
	
	public static class FollowUserReq extends BaseReq {
		public String userId;
		public boolean follow;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public boolean getFollow() {
			return follow;
		}

		public void setFollow(boolean follow) {
			this.follow = follow;
		}

	}

}
