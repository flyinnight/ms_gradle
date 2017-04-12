package com.dilapp.radar.ui.admin;

import java.io.Serializable;

public class TopItemParcel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int type = 0; //0 :top 1: banner
	private long topicId;
	private long postId;
	private long solutionId;
	private String cover;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTopicId() {
		return topicId;
	}
	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}
	public long getPostId() {
		return postId;
	}
	public void setPostId(long postId) {
		this.postId = postId;
	}

	public long getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(long solutionId) {
		this.solutionId = solutionId;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}
}
