package com.dilapp.radar.domain.server;

public class User {
	private String point;// 积分
	private String level;
	private String token;
	private String createTopic;// 是否创建话题
	private String userId;

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCreateTopic() {
		return createTopic;
	}

	public void setCreateTopic(String createTopic) {
		this.createTopic = createTopic;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
