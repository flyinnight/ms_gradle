package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.*;

/**
 * 帖子详情请求接口
 * 进入主贴，分页获取从帖列表
 * 读取跟帖的回复
 * 
 * @author john
 *
 */
public abstract class PostDetailsCallBack {
	
	//获取主贴详情
	public abstract void getPostDetailsByTypeAsync(MPostDetailReq bean, BaseCall<MPostDetailResp> call, int type);

	//读取跟帖的回复
	public abstract void getReplyByTypeAsync(MPostDetailReq bean, BaseCall<MReplyResp> call, int type);
	

	public static class MPostDetailReq extends BaseReq {
		private long postId;
		private int pageNo;
		// 用于比较本地缓存和服务器的时间戳，以便更新本地缓存
		private long updateTime;

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
		
        public long getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
	}

	/**
	 * 帖子详情返回Bean
	 *
	 * @author john
	 *
	 */

	public static class MPostDetailResp extends BaseResp {
		private int totalPage;
		private int pageNo;

		// 从帖列表:   获取第一分页时，第一个返回的post是主帖信息，其余是跟帖信息
		private List<MFollowPostResp> mResp;

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

		public List<MFollowPostResp> getResp() {
			return mResp;
		}

		public void setResp(List<MFollowPostResp> mResp) {
			this.mResp = mResp;
		}
	}

	public static class PostDetailRespLocal implements Serializable {
		private long updateTime;
		private MPostDetailResp mDetailResp;

		public long getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}

		public MPostDetailResp getDetailResp() {
			return mDetailResp;
		}
		public void setDetailResp(MPostDetailResp mDetailResp) {
			this.mDetailResp = mDetailResp;
		}
	}
	
	/**
	 * 从贴详情Bean
	 *
	 * @author john
	 *
	 */
	public static class MFollowPostResp extends GetPostList.MPostResp {
		private boolean isMain;  //是否主贴
		
		// 评论内容
		private List<MPostResp> comment;

		public boolean isMain() {
			return isMain;
		}

		public void setMain(boolean isMain) {
			this.isMain = isMain;
		}

		public List<MPostResp> getComment() {
			return comment;
		}

		public void setComment(List<MPostResp> comment) {
			this.comment = comment;
		}

	}
	
	/**
	 * 从贴的回复内容Bean
	 *
	 * @author john
	 *
	 */
	public static class MReplyResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		// 评论内容
		private List<MPostResp> comment;

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

		public List<MPostResp> getComment() {
			return comment;
		}

		public void setComment(List<MPostResp> comment) {
			this.comment = comment;
		}

	}

	public static class DeleteLocalPostReq implements Serializable {
		private long postId;
		private long localPostId;

		public long getPostId() {
			return postId;
		}

		public void setPostId(long postId) {
			this.postId = postId;
		}

		public long getLocalPostId() {
			return localPostId;
		}

		public void setLocalPostId(long localPostId) {
			this.localPostId = localPostId;
		}
	}
}
