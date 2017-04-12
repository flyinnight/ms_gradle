/*********************************************************************/
/*  文件名  Login.java    　                                            */
/*  程序名  抽象登录域                    						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

import java.util.List;
import java.util.Map;

public abstract class Login {

	public abstract void loginAsync(LoginReq bean, BaseCall<LoginResp> call);

	public static class LoginReq extends BaseReq {
		private String username;
		private String pwd;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

	}

	public static class LoginResp extends BaseResp {
		private String userId;
		private int point;
		private int level;
		private String levelName;
		private String roles;
		//private boolean createTopic;
		//private List<Long> forbidden;  //topicId列表
		//private List<Map<String, List<String>>> actionMap;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

		public int getPoint() {
			return point;
		}
		public void setPoint(int point) {
			this.point = point;
		}

		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		
		public String getLevelName() {
			return levelName;
		}
		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}
		
		public String getRoles() {
			return roles;
		}
		public void setRoles(String roles) {
			this.roles = roles;
		}
		
		/*public boolean getCreateTopic() {
			return createTopic;
		}
		public void setCreateTopic(boolean createTopic) {
			this.createTopic = createTopic;
		}
		
		public List<Long> getForbidden() {
			return forbidden;
		}
		public void setForbidden(List<Long> forbidden) {
			this.forbidden = forbidden;
		}
		
		public List<Map<String, List<String>>> getActionMap() {
			return actionMap;
		}
		public void setActionMap(List<Map<String, List<String>>> actionMap) {
			this.actionMap = actionMap;
		}*/
	}
}