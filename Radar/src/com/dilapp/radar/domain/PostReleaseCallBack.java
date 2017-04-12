package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.*;

/**
 * 发布帖子接口
 * 
 * @author john
 *
 */
public abstract class PostReleaseCallBack {

	public static final String MAINPOST_RELEASE_END = "dilapp.radar.mainpost.release.end";
	public static final int POST_RELEASE_SENDING = 1; //发送中，未返回最终结果
	public static final int POST_RELEASE_SENDFAILED = 2; //发送失败
	public static final int POST_RELEASE_SENDSUCCESS = 3; //发送成功
	
	public abstract void uploadPostImgAsync(List<String> imgs,
			BaseCall<MPostImgResp> call);

	public abstract void createPostAsync(PostReleaseReq bean,
			BaseCall<MPostResp> call);

	public abstract void updatePostAsync(PostReleaseReq bean,
			BaseCall<MPostResp> call);

	// 删除未发布成功/未更新成功的贴子
	// bean可以只赋值postlevel和localpostid
	public abstract void deleteLocalPostAsync(PostReleaseReq bean, BaseCall<MPostResp> call);
	
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的贴子
	public abstract void deleteAllSendingPostAsync(BaseReq bean, BaseCall<BaseResp> call);
	
	
	/**
	 * 发布话题 Bean
	 * 
	 * @author john
	 */
	public static class PostReleaseReq extends BaseReq {
		// post's local Id 发送成功前，预览及本地存储之用
		private long localPostId;
		// 帖子ID 更新帖子用
		private long postId;
		// 帖子话题的ID
		private long topicId;
		private String topicTitle;
		// 标题
		private String postTitle;
		// 内容
		//private String postDes;
		// 图片的URL地址 默认四个
		private List<String> thumbURL;
		// 帖子内容
		private String postContent;
		// 帖子的主帖/上一级帖子ID
		private long parentId;
		// 决定帖子是主帖、从帖、还是从帖的回复   ---必选参数---
		private int postLevel;
		// 可选，应用场景为从帖的回复
		private String toUserId;
		// 选为护肤方案的模板
		private boolean selectedToSolution;
		// 护肤方案的类型
		private String effect;
		// 部位
		private String part;
		// 肤质
		private String skin;
		// 本地发帖时间 server层填写
		private long localCreateTime;

		public long getLocalPostId() {
			return localPostId;
		}

		public void setLocalPostId(long localPostId) {
			this.localPostId = localPostId;
		}

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public String getTopicTitle() {
			return topicTitle;
		}

		public void setTopicTitle(String topicTitle) {
			this.topicTitle = topicTitle;
		}

		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
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
		
		public long getParentId() {
			return parentId;
		}
		public void setParentId(long parentId) {
			this.parentId = parentId;
		}

		public int getPostLevel() {
			return postLevel;
		}
		public void setPostLevel(int postLevel) {
			this.postLevel = postLevel;
		}
		
		public String getToUserId() {
			return toUserId;
		}
		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}

		public boolean getSelectedToSolution() {
			return selectedToSolution;
		}
		public void setSelectedToSolution(boolean selectedToSolution) {
			this.selectedToSolution = selectedToSolution;
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

		public List<String> getThumbURL() {
			return thumbURL;
		}
		public void setThumbURL(List<String> thumbURL) {
			this.thumbURL = thumbURL;
		}

		public long getLocalCreateTime() {
			return localCreateTime;
		}

		public void setLocalCreateTime(long localCreateTime) {
			this.localCreateTime = localCreateTime;
		}

		@Override
		public String toString() {
			return "TopicReleaseReq [postContent=" + postContent + "]";
		}
	}

	// 转换数据thumbURL之用
	public static class PostReleaseReqString extends BaseReq {
		// 帖子话题的ID
		private long topicId;
		// 标题
		private String postTitle;
		// 内容
		//private String postDes;
		// 图片的URL地址 默认四个
		private String thumbURL;
		// 帖子内容
		private String postContent;
		// 帖子的主帖/上一级帖子ID
		private long parentId;
		// 决定帖子是主帖、从帖、还是从帖的回复   ---必选参数---
		private int postLevel;
		// 可选，应用场景为从帖的回复
		private String toUserId;
		// 选为护肤方案的模板
		//private boolean selectedToSolution;
		// 护肤方案的类型
		//private String effect;
		// 部位
		//private String part;
		// 肤质
		private String skin;
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
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
		
		public long getParentId() {
			return parentId;
		}
		public void setParentId(long parentId) {
			this.parentId = parentId;
		}

		public int getPostLevel() {
			return postLevel;
		}
		public void setPostLevel(int postLevel) {
			this.postLevel = postLevel;
		}
		
		public String getToUserId() {
			return toUserId;
		}
		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}

		/*public boolean getSelectedToSolution() {
			return selectedToSolution;
		}
		public void setSelectedToSolution(boolean selectedToSolution) {
			this.selectedToSolution = selectedToSolution;
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
		}*/

		public String getSkin() {
			return skin;
		}
		public void setSkin(String skin) {
			this.skin = skin;
		}

		public String getThumbURL() {
			return thumbURL;
		}
		public void setThumbURL(String thumbURL) {
			this.thumbURL = thumbURL;
		}

		@Override
		public String toString() {
			return "TopicReleaseReq [postContent=" + postContent + "]";
		}
	}
	

	// 转换数据thumbURL之用
	public static class UpdatePostReqString extends BaseReq {
		// 帖子话题的ID
		private long topicId;
		// 帖子ID 更新帖子用
		private long id;
		// 标题
		private String postTitle;
		// 内容
		//private String postDes;
		// 图片的URL地址 默认四个
		private String thumbURL;
		// 帖子内容
		private String postContent;
		// 帖子的主帖/上一级帖子ID
		private long parentId;
		// 决定帖子是主帖、从帖、还是从帖的回复   ---必选参数---
		private int postLevel;
		// 可选，应用场景为从帖的回复
		private String toUserId;
		// 选为护肤方案的模板
		//private boolean selectedToSolution;
		// 护肤方案的类型
		//private String effect;
		// 部位
		//private String part;
		// 肤质
		private String skin;
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public long getPostId() {
			return id;
		}
		public void setPostId(long id) {
			this.id = id;
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
		
		public long getParentId() {
			return parentId;
		}
		public void setParentId(long parentId) {
			this.parentId = parentId;
		}

		public int getPostLevel() {
			return postLevel;
		}
		public void setPostLevel(int postLevel) {
			this.postLevel = postLevel;
		}
		
		public String getToUserId() {
			return toUserId;
		}
		public void setToUserId(String toUserId) {
			this.toUserId = toUserId;
		}

		/*public boolean getSelectedToSolution() {
			return selectedToSolution;
		}
		public void setSelectedToSolution(boolean selectedToSolution) {
			this.selectedToSolution = selectedToSolution;
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
		}*/

		public String getSkin() {
			return skin;
		}
		public void setSkin(String skin) {
			this.skin = skin;
		}

		public String getThumbURL() {
			return thumbURL;
		}
		public void setThumbURL(String thumbURL) {
			this.thumbURL = thumbURL;
		}

		@Override
		public String toString() {
			return "TopicReleaseReq [postContent=" + postContent + "]";
		}
	}

	
	
	/**
	 * 更新本地数据库 贴子发送状态
	 * 
	 * @author john
	 */
	public static class UpdatePostSendingState implements Serializable  {
		private long localPostId;
		// 贴子发送状态
		private int sendState;
		private long localCreateTime;
		
		public long getLocalPostId() {
			return localPostId;
		}
		public void setLocalPostId(long localPostId) {
			this.localPostId = localPostId;
		}
        
		public int getSendState() {
			return sendState;
		}
		public void setSendState(int sendState) {
			this.sendState = sendState;
		}

		public long getLocalCreateTime() {
			return localCreateTime;
		}
		public void setLocalCreateTime(long localCreateTime) {
			this.localCreateTime = localCreateTime;
		}
	}
	
	
	/**
	 * 发布帖子 成功与否
	 * 
	 * @author john
	 */
	public static class MPostImgResp extends BaseResp {
		
        private List<String> postImgURL;
        
        public List<String> getPostImgURL() {
            return postImgURL;
        }
        
        public void setPostImgURL(List<String> postImgURL) {
            this.postImgURL = postImgURL;
        }

	}
}
