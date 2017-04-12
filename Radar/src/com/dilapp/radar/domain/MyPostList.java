package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;

/**
 * 某个话题下的帖子列表
 * 
 * @author john
 *
 */
public abstract class MyPostList {
	
	// 获取自己所发帖子的列表
	public abstract void getMyCreatPostByTypeAsync(MyCreatPostReq bean, BaseCall<MyCreatPostResp> call, int type);

	// 获取自己收藏帖子的列表
	public abstract void getMyStorePostByTypeAsync(MyStorePostReq bean, BaseCall<MyStorePostResp> call, int type);
	
	
	public static class MyCreatPostReq extends BaseReq {
		// 分页
		private int pageNo;
		// 一页显示多少条
		private int pageSize;

		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
	}
	
	public static class MyStorePostReq extends BaseReq {
		// 分页
		private int pageNo;

		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	
	public static class MyCreatPostResp extends BaseResp {
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
	
	public static class MyStorePostResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
		// 类型
		private String type;

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
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public List<MPostResp> getDatas() {
			return datas;
		}

		public void setDatas(List<MPostResp> datas) {
			this.datas = datas;
		}
	}		

}
