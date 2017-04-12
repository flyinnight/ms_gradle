package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicReq;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;

/**
 * 某个话题下的帖子列表
 * 
 * @author john
 *
 */
public abstract class UserPostTopicList {
	
	// 获取他人所发帖子的列表
	public abstract void getUserCreatPostByTypeAsync(UserPostTopicReq bean, BaseCall<UserPostResp> call, int type);

	// 获取他人发布的话题列表
	public abstract void getUserCreatTopicByTypeAsync(UserPostTopicReq bean, BaseCall<UserTopicResp> call, int type);
	
	
	public static class UserPostTopicReq extends BaseReq {
		
		public String userId;
		private int pageNo;

		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	

	public static class UserPostResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
		// 帖子列表
		private List<MPostResp> datas;
		
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

		public List<MPostResp> getDatas() {
			return datas;
		}
		public void setDatas(List<MPostResp> datas) {
			this.datas = datas;
		}
	}
	
	public static class UserTopicResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
		
		private List<MTopicResp> datas;

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
		
		public List<MTopicResp> getDatas() {
			return datas;
		}

		public void setDatas(List<MTopicResp> datas) {
			this.datas = datas;
		}
	}		

}
