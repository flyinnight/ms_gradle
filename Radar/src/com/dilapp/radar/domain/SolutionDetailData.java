package com.dilapp.radar.domain;


import java.util.List;



/**
 * 护肤方案详情相关
 * 
 * @author john
 * 
 */
public abstract class SolutionDetailData {

	// 获取护肤方案详情
	public abstract void getSolutionDetailDataAsync(long solutionId, BaseCall<MSolutionResp> call);
	// 获取正在使用的护肤方案
	public abstract void getSolutionInUsedDataAsync(BaseCall<MSolutionResp> call);

	
	public static class MSolutionResp extends BaseResp {
		
		private long solutionId;
		private long localSolutionId;  //本地未发送成功的护肤方案Id
     	private int sendState;  //发送状态，预览及本地存储之用
		private long commentId;
		private long parentCommId;  //parent Comment Id
		private String[] effect;
		private String[] part;
		private String title;
		private String introduction;
		private String content;
		private String coverImgUrl;
		private String coverThumbImgUrl; //封面缩略图地址
		private List<String> textImgUrl;
		private int useCycle;
		private double score;
		private int myScore;  //自己的评分
		private int usedCount;
		private int storeUpCount;
		private long createTime;
		private long updateTime;
		private long startTime;  //开始使用护肤方案的时间
		private String userId;
		private int gender;
		private int level;
		private String nickName;
		private String toUserId;  //被评论人id
		private String toNickName;  //被评论文昵称
		private String portrait;
		private boolean isStoreup;
		private boolean inUse;
		private int rank;
		private int likeCount;
		private boolean isLike;
		
		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public long getLocalSolutionId() {
			return localSolutionId;
		}
		public void setLocalSolutionId(long localSolutionId) {
			this.localSolutionId = localSolutionId;
		}
		
		public int getSendState() {
			return sendState;
		}
		public void setSendState(int sendState) {
			this.sendState = sendState;
		}
		
		public long getCommentId() {
			return commentId;
		}
		public void setCommentId(long commentId) {
			this.commentId = commentId;
		}
		
		public long getParentCommId() {
			return parentCommId;
		}
		public void setParentCommId(long parentCommId) {
			this.parentCommId = parentCommId;
		}
		
		public String[] getEffect() {
			return effect;
		}
		public void setEffect(String[] effect) {
			this.effect = effect;
		}

		public String[] getPart() {
			return part;
		}
		public void setPart(String[] part) {
			this.part = part;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		public String getIntroduction() {
			return introduction;
		}
		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		public String getCoverImgUrl() {
			return coverImgUrl;
		}
		public void setCoverImgUrl(String coverImgUrl) {
			this.coverImgUrl = coverImgUrl;
		}
		
		public String getCoverThumbImgUrl() {
			return coverThumbImgUrl;
		}
		public void setCoverThumbImgUrl(String coverThumbImgUrl) {
			this.coverThumbImgUrl = coverThumbImgUrl;
		}

		public List<String> getTextImgUrl() {
			return textImgUrl;
		}
		public void settextImgUrl(List<String> textImgUrl) {
			this.textImgUrl = textImgUrl;
		}

		public int getUseCycle() {
			return useCycle;
		}
		public void setUseCycle(int useCycle) {
			this.useCycle = useCycle;
		}
		
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		
		public int getMyScore() {
			return myScore;
		}
		public void setMyScore(int myScore) {
			this.myScore = myScore;
		}
		
		public int getUsedCount() {
			return usedCount;
		}
		public void setUsedCount(int usedCount) {
			this.usedCount = usedCount;
		}
		
		public int getStoreUpCount() {
			return storeUpCount;
		}
		public void setStoreUpCount(int storeUpCount) {
			this.storeUpCount = storeUpCount;
		}
		
		public long getCreateTime() {
			return createTime;
		}
		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}
		
		public long getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}
		
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

		public int getGender() {
			return gender;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		
		public String getToUserId() {
			return toUserId;
		}
		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}

		public String getToNickName() {
			return toNickName;
		}
		public void setToNickName(String toNickName) {
			this.toNickName = toNickName;
		}
		
		public String getPortrait() {
			return portrait;
		}
		public void setPortrait(String content) {
			this.portrait = content;
		}
		
		public boolean getIsStoreup() {
			return isStoreup;
		}
		public void setIsStoreup(boolean isStoreup) {
			this.isStoreup = isStoreup;
		}
		
		public boolean getInUse() {
			return inUse;
		}
		public void setInUse(boolean inUse) {
			this.inUse = inUse;
		}
		
		public int getRank() {
			return rank;
		}
		public void setRank(int rank) {
			this.rank = rank;
		}
		
		public int getLikeCount() {
			return likeCount;
		}
		public void setLikeCount(int likeCount) {
			this.likeCount = likeCount;
		}
		
		public boolean getIsLike() {
			return isLike;
		}
		public void setIsLike(boolean isLike) {
			this.isLike = isLike;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof MSolutionResp && ((MSolutionResp) o).solutionId == this.solutionId;
		}
	}

}
