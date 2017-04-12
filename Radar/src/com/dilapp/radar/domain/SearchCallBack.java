package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;

/**
 * 查询返回话题列表
 * 查询返回帖子列表
 * @author john
 *
 */
public abstract class SearchCallBack {

	//查询返回话题列表
	public abstract void TopicSearchAsync(TopicSearchReq bean,
			BaseCall<TopicSearchResp> call);
	//查询返回帖子列表
	public abstract void PostSearchAsync(PostSearchReq bean,
			BaseCall<PostSearchResp> call);
	
	
	public static class TopicSearchReq extends BaseReq {
		// 任意相关词语
		private String[] topicParam;
		// 默认为1
		private int startNo;

		public String[] getTopicParam() {
			return topicParam;
		}
		public void setTopicParam(String[] topicParam) {
			this.topicParam = topicParam;
		}

		public int getStartNo() {
			return startNo;
		}
		public void setStartNo(int startNo) {
			this.startNo = startNo;
		}
	}	
	
	public static class PostSearchReq extends BaseReq {
		// 任意相关词语
		private String[] postParam;
		// 默认为1
		private int startNo;

		public String[] getPostParam() {
			return postParam;
		}

		public void setPostParam(String[] postParam) {
			this.postParam = postParam;
		}

		public int getStartNo() {
			return startNo;
		}
		public void setStartNo(int startNo) {
			this.startNo = startNo;
		}
	}

	
	public static class TopicSearchResp extends BaseResp {
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
	
	public static class PostSearchResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
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
}
