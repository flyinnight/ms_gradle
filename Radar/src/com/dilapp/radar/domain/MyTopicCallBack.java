package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;


/**
 *  用户话题列表
 * @author john
 */
public abstract class MyTopicCallBack {

	//我发布的话题
	public abstract void getMyCreateTopicByTypeAsync(MMyTopicReq bean, BaseCall<MMyTopicResp> call, int type);

	//我关注的话题
	public abstract void getMyFollowTopicByTypeAsync(MMyTopicReq bean, BaseCall<MMyFollowTopicResp> call, int type);
	
	//判断登录用户是否有关注的话题
	public abstract void hasFollowTopicAsync(BaseCall<HasFollowTopicResp> call);
	
	
	public static class MyTopicReq extends BaseReq {
		// 分页
		private String pageNo;

		public String getPageNo() {
			return pageNo;
		}

		public void setPageNo(String pageNo) {
			this.pageNo = pageNo;
		}
	}
		

	public static class MMyTopicReq extends BaseReq {
		// 分页
		private int pageNo;

		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	
	public static class MMyFollowTopicResp extends BaseResp {
		// 总页
		private int totalPage;
		// 分页
		private int pageNo;
		// 类型
		private String type;

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
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public List<MTopicResp> getDatas() {
			return datas;
		}

		public void setDatas(List<MTopicResp> datas) {
			this.datas = datas;
		}
	}		
	
	public static class MMyTopicResp extends BaseResp {
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
	
	public static class HasFollowTopicResp extends BaseResp {
		private boolean hasFollow;
		
		public boolean getHasFollow() {
			return hasFollow;
		}
		public void setHasFollow(boolean hasFollow) {
			this.hasFollow = hasFollow;
		}
	}
	
	
/*	public static class MyFollowTopicReq extends BaseReq {
		// 关注的话题/帖子/收藏 （该处可以写死为topic）
		private String type;	
		// 分页
		private String pageNo;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public String getPageNo() {
			return pageNo;
		}

		public void setPageNo(String pageNo) {
			this.pageNo = pageNo;
		}
	}*/
}
