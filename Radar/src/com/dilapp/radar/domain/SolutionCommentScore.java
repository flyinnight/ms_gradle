package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.SolutionCreateUpdate.CoverImgResp;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;


/**
 * 
 * @author john
 *	护肤方案评论和评分相关
 */

public abstract class SolutionCommentScore {

	// 上传评论图片
	public abstract void solutionUplCommentImgAsync(List<String> imgs, BaseCall<CommentImgResp> call);
	// 添加评论
	public abstract void solutionCreatCommentsAsync(CreatCommentReq bean, BaseCall<MSolutionResp> call);
	// 创建/修改评分
	public abstract void solutionUpdateScoreAsync(UpdateScoreReq bean, BaseCall<BaseResp> call);
	// 获取评分
	public abstract void solutionGetScoreAsync(long solutionId, BaseCall<GetScoreResp> call);
	// 给评论点赞/取消点赞
	public abstract void solutionLikeCommentAsync(LikeCommentReq bean, BaseCall<BaseResp> call);
	// 删除评论
	public abstract void solutionDeleteCommentAsync(long commentId, BaseCall<BaseResp> call);

	
	public static class CommentImgResp extends BaseResp {
        private List<String> commentImgUrl;
        
        public List<String> getCommentImgUrl() {
            return commentImgUrl;
        }
        public void setCommentImgUrl(List<String> commentImgUrl) {
            this.commentImgUrl = commentImgUrl;
        }
	}
	
	public static class CreatCommentReq extends BaseReq {
		private long solutionId;
		private long parentCommId;  //parent Comment Id 一级评论不用赋值, 二级评论需要赋值
		private String content;
		private String toUserId;  //被评论人id 一级评论不用赋值
		private List<String> picUrl;  //上传的图片的网络地址

		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public long getParentCommId() {
			return parentCommId;
		}
		public void setParentCommId(long parentCommId) {
			this.parentCommId = parentCommId;
		}
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		public String getToUserId() {
			return toUserId;
		}
		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}
		
		public List<String> getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(List<String> picUrl) {
			this.picUrl = picUrl;
		}
	}
	
	public static class UpdateScoreReq extends BaseReq {
		private long solutionId;
		private int score;

		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
	}

	public static class GetScoreResp extends BaseResp {
		private int score;

		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
	}

	public static class LikeCommentReq extends BaseReq {
		private long commentId;
		private boolean isLike;

		public long getCommentId() {
			return commentId;
		}
		public void setCommentId(long commentId) {
			this.commentId = commentId;
		}
		
		public boolean getIsLike() {
			return isLike;
		}
		public void setIsLike(boolean isLike) {
			this.isLike = isLike;
		}
	}
	
}
