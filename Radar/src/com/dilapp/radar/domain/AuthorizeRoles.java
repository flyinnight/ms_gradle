package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author john 权限相关
 */
public abstract class AuthorizeRoles {
	
	//授权(取消)其它用户管理员权限
	public abstract void authorizeUserTopicPermissionAsync(AuthorizeTopicReq bean, BaseCall<AuthorizeTopicResp> call);
	//获取用户动态权限
	public abstract void updateRolesAsync(UpdateRolesReq bean, BaseCall<UpdateRolesResp> call);
	//从主页进入帖子时获得角色权限
	public abstract void updatePostRolesAsync(UpdateRolesReq bean, BaseCall<UpdateRolesResp> call);
	
	
	public static class AuthorizeTopicReq extends BaseReq {
		private long topicId;
		private List<String> toUserIdList;
		private boolean grant;
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public List<String> getToUserIdList() {
			return toUserIdList;
		}
		public void setToUserIdList(List<String> toUserIdList) {
			this.toUserIdList = toUserIdList;
		}
		
		public boolean getGrant() {
			return grant;
		}
		public void setGrant(boolean grant) {
			this.grant = grant;
		}
	}
	
	public static class UpdateRolesReq extends BaseReq {
		private long topicId;
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
	}
	
	
	public static class AuthorizeTopicResp extends BaseResp {
		private List<String> successList;
		private List<String> failedList;
		
		public List<String> getSuccessList() {
			return successList;
		}
		public void setSuccessList(List<String> successList) {
			this.successList = successList;
		}
		
		public List<String> getFailedList() {
			return failedList;
		}
		public void setFailedList(List<String> failedList) {
			this.failedList = failedList;
		}
	}
	
	public static class UpdateRolesResp extends BaseResp {
		private String userId;
		private List<String> roles;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public List<String> getRoles() {
			return roles;
		}
		public void setRoles(List<String> roles) {
			this.roles = roles;
		}
	}

}
