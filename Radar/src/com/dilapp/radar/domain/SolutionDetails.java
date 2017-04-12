package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.SolutionComment.Solutioncomment;

/**
 * 获取护肤方案及其评论
 * 
 * @author john
 * 
 */
public abstract class SolutionDetails {

	// 获取护肤方案及其评论
	public abstract void getSkinSolutionAndCommentsAsync(
			SolutionDetailReq bean, BaseCall<SolutionDetailResp> call);

	/**
	 * 护肤方案详情Bean
	 * 
	 * @author john
	 * 
	 */
	public static class SolutionDetailReq extends BaseReq {
		private long postId;

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}
	}

	/**
	 * 获取护肤方案及其评论Resp
	 * 
	 * @author john
	 * 
	 */
	public static class SolutionDetailResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		// 护肤方案内容
		private SolutionResp mSolutionResp;
		// 评论内容
		private List<Solutioncomment> comment;

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

		public SolutionResp getSolutionResp() {
			return mSolutionResp;
		}

		public void setSolutionResp(SolutionResp mSolutionResp) {
			this.mSolutionResp = mSolutionResp;
		}

		public List<Solutioncomment> getComment() {
			return comment;
		}

		public void setComment(List<Solutioncomment> comment) {
			this.comment = comment;
		}
	}

	/**
	 * 护肤方案帖子内容
	 * 
	 * @author john
	 * 
	 */
	public static class SolutionResp  implements Serializable {
		private double scores;
        private long postId;
        private long parentId;
        private long topicId;
        private String topicTitle;
        private int postLevel;
        private String userId;
        private String userName;
        private String portrait;
        private String postTitle;
        private String postContent;
        private int followsUpNum;
        private int storeupNum;
        private String effect;
        private String part;
        private String skin;
        private boolean report;
        private boolean onTop;
        private int favorite;
        private int disfavorite;
        private long updateTime;

		// 部分返回list 部分返回长字符串
		private List<String> thumbURL;
		// 供SolutionDetails solutionRankAsync使用
		private String[] thumbString;
		private boolean isSolution;
		private boolean inUsed;

		public double getScores() {
			return scores;
		}

		public void setScores(double scores) {
			this.scores = scores;
		}

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}

		public long getParentId() {
			return parentId;
		}

		public void setParentId(long parentId) {
			this.parentId = parentId;
		}

		public long getTopicId() {
			return topicId;
		}
        
        public String getPortrait() {
            return portrait;
        }
        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}

		public String getTopicTitle() {
			return topicTitle;
		}
		public void setTopicTitle(String topicTitle) {
			this.topicTitle = topicTitle;
		}
		
		public int getPostLevel() {
			return postLevel;
		}

		public void setPostLevel(int postLevel) {
			this.postLevel = postLevel;
		}

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

		public String getPostTitle() {
			return postTitle;
		}

		public void setPostTitle(String postTitle) {
			this.postTitle = postTitle;
		}

		public String getPostContent() {
			return postContent;
		}

		public void setPostContent(String postContent) {
			this.postContent = postContent;
		}

		public int getFollowsUpNum() {
			return followsUpNum;
		}

		public void setFollowsUpNum(int followsUpNum) {
			this.followsUpNum = followsUpNum;
		}

		public int getStoreupNum() {
			return storeupNum;
		}

		public void setStoreupNum(int storeupNum) {
			this.storeupNum = storeupNum;
		}

		public String getEffect() {
			return effect;
		}

		public void setEffect(String effect) {
			this.effect = effect;
		}

		public String getPart() {
			return part;
		}

		public void setPart(String part) {
			this.part = part;
		}

		public String getSkin() {
			return skin;
		}

		public void setSkin(String skin) {
			this.skin = skin;
		}

		public boolean getReport() {
			return report;
		}

		public void setReport(boolean report) {
			this.report = report;
		}

		public boolean getOnTop() {
			return onTop;
		}

		public void setOnTop(boolean onTop) {
			this.onTop = onTop;
		}

		public int getFavorite() {
			return favorite;
		}

		public void setFavorite(int favorite) {
			this.favorite = favorite;
		}

		public int getDisfavorite() {
			return disfavorite;
		}

		public void setDisfavorite(int disfavorite) {
			this.disfavorite = disfavorite;
		}

		public long getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}

		public List<String> getThumbURL() {
			return thumbURL;
		}

		public void setThumbURL(List<String> thumbURL) {
			this.thumbURL = thumbURL;
		}

		public String[] getThumbString() {
			return thumbString;
		}

		public void setThumbString(String[] thumbString) {
			this.thumbString = thumbString;
		}

		public boolean getIsSolution() {
			return isSolution;
		}

		public void setIsSolution(boolean isSolution) {
			this.isSolution = isSolution;
		}

		public boolean getInUsed() {
			return inUsed;
		}

		public void setInUsed(boolean inUsed) {
			this.inUsed = inUsed;
		}
	}

}
