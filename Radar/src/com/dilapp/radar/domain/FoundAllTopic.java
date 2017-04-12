package com.dilapp.radar.domain;

import java.util.List;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;

/**
 * 话题大全接口/所有话题列表请求接口
 * 
 * @author john
 *
 */
public abstract class FoundAllTopic {

    public abstract void getAllTopicByTypeAsync(AllTopicReq bean, BaseCall<AllTopicResp> call, int type);
    
    
	public static class AllTopicReq extends BaseReq {
		// 分页
		private int pageNo;

		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}


	public static class AllTopicResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
		// 话题列表
		private List<MTopicResp> mTopicResp;

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
		
		public List<MTopicResp> getTopicResp() {
			return mTopicResp;
		}

		public void setTopicResp(List<MTopicResp> mTopicResp) {
			this.mTopicResp = mTopicResp;
		}
	}	
}
