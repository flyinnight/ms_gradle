package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author john
 *	护肤方案收藏应用相关
 */
public abstract class SolutionComment {

	// 护肤方案评论列表
	public abstract void getSkinSolutionCommentsAsync(GetSolutionCommentReq bean, BaseCall<GetSolutionCommentResp> call);
	// 针对护肤方案发表评论
	public abstract void postSkinSolutionCommentAsync(PostSolutionCommentReq bean, BaseCall<PostSolutionCommentResp> call);
	// 删除护肤方案的评论
	public abstract void deleteSkinSolutionCommentAsync(DeleteSolutionCommentReq bean, BaseCall<BaseResp> call);

	/**
	 * 护肤方案评论列表 Bean
	 * 
	 * @author john
	 */
	public static class GetSolutionCommentReq extends BaseReq {

		private long postId;
		private int pageNo;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}

	/**
	 * 护肤方案评论列表Resp
	 * 
	 * @author john
	 */
	public class GetSolutionCommentResp extends BaseResp {

		private double scores;
		private int totalPage;
		private int pageNo;
		List<Solutioncomment> comments;
		
		public double getScores() {
			return scores;
		}
		public void setScores(double scores) {
			this.scores = scores;
		}
		
		public int getTotalPage() {
			return totalPage;
		}
		public void setTotalPage(int totalPage) {
			this.totalPage = totalPage;
		}
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public List<Solutioncomment> getComments() {
			return comments;
		}
		public void setComments(List<Solutioncomment> comments) {
			this.comments = comments;
		}
	}
	
	public static class Solutioncomment implements Serializable {

	    private String userId;
	    private String userName;
	    private int score;
	    private String comments;
	    private long updateTime;
	    
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}

		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}

		public long getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}
	}

	
	/**
	 * 针对护肤方案发表评论 Bean
	 * 
	 * @author john
	 */
	public static class PostSolutionCommentReq extends BaseReq {

		private long postId;
		private int score;
		private String comment;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
	}

	/**
	 * 针对护肤方案发表评论 Resp
	 * 
	 * @author john
	 */
	public class PostSolutionCommentResp extends BaseResp {

		private double scores;
		private long commentId;

		public double getScores() {
			return scores;
		}
		public void setScores(double scores) {
			this.scores = scores;
		}
		
		public long getCommentId() {
			return commentId;
		}
		public void setCommentId(long commentId) {
			this.commentId = commentId;
		}
	}
	
	
	/**
	 * 删除护肤方案的评论Bean
	 * 
	 * @author john
	 */
	public static class DeleteSolutionCommentReq extends BaseReq {

		private long commentId;
		
		public long getCommentId() {
			return commentId;
		}
		public void setCommentId(long commentId) {
			this.commentId = commentId;
		}
	}
	
}
