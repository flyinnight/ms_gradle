package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionCreateReq;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;


/***
 * 护肤方案及其评论列表相关
 * @author john
 *
 */
public abstract class SolutionListData {
	
	public static final int SOLUTION_LIST_TYPE = 1;   //护肤方案列表（大全/分类）
	public static final int SOLUTION_LIST_STOREUP = 2;   //用户收藏的护肤方案列表
	public static final int SOLUTION_LIST_CREATE = 3;   //用户发布的护肤方案列表
	public static final int SOLUTION_LIST_COMMENT = 4;   //护肤方案评论列表
	public static final int SOLUTION_DETAIL_DATA = 5;   //护肤方案详情
	public static final int SOLUTION_INUSED_DATA = 6;   //正在使用的护肤方案
	public static final int SOLUTION_SENDING_DATA = 7;   //发送中的护肤方案
	
	//获取护肤方案列表（大全/分类）
	public abstract void getSolutionListByTypeAsync(MSolutionListReq bean, BaseCall<MSolutionListResp> call, int type);
	//获取用户收藏的护肤方案列表
	public abstract void getSolutionListStoreupByTypeAsync(SolutionListStoreupReq bean, BaseCall<MSolutionListResp> call, int type);
	//获取用户发布的护肤方案列表
	public abstract void getSolutionListCreateByTypeAsync(SolutionListCreateReq bean, BaseCall<MSolutionListResp> call, int type);
	//获取护肤方案评论列表
	public abstract void getSolutionCommentListByTypeAsync(SolCommentListReq bean, BaseCall<SolCommentListResp> call, int type);
	//读取护肤方案二级评论列表
	public abstract void getSolution2ndCommentListAsync(SolCommentList2ndReq bean, BaseCall<Sol2ndCommentListResp> call);	
	
	
	public static class MSolutionListReq extends BaseReq {
		private int pageNo;
		private String tag;  //标签，用于筛选护肤方案分类，可不填
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
	}
	
	public static class MSolutionListResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		private List<MSolutionResp> datas;
		
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
		
		public List<MSolutionResp> getDatas() {
			return datas;
		}
		public void setDatas(List<MSolutionResp> datas) {
			this.datas = datas;
		}
	}
	

	public static class SolutionListStoreupReq extends BaseReq {
		private int pageNo;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	
	
	public static class SolutionListCreateReq extends BaseReq {
		private int pageNo;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	
	
	public static class SolCommentListReq extends BaseReq {
		private int pageNo;
		private long solutionId;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
	}
	
	public static class SolCommentList2ndReq extends BaseReq {
		private int pageNo;
		private long commentId;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public long getCommentId() {
			return commentId;
		}
		public void setCommentId(long commentId) {
			this.commentId = commentId;
		}
	}
	
	public static class SolCommentListResp extends BaseResp {
		private int totalPage; //评论总页数
		private int pageNo;  //当前评论页数
		private int totalCount; //总评论数
		private List<SolCommentResp> datas;  // 评论数据

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
		
		public int getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}
		
		public List<SolCommentResp> getDatas() {
			return datas;
		}

		public void setDatas(List<SolCommentResp> datas) {
			this.datas = datas;
		}
	}
    
	public static class SolCommentResp extends MSolutionResp {
		private boolean hasMore; //是否超过三条子评论
		private List<MSolutionResp> followComments;  // 子评论内容

		public boolean getHasMore() {
			return hasMore;
		}
		public void setHasMore(boolean hasMore) {
			this.hasMore = hasMore;
		}
		
		public List<MSolutionResp> getFollowComments() {
			return followComments;
		}

		public void setFollowComments(List<MSolutionResp> followComments) {
			this.followComments = followComments;
		}
	}
	
	public static class Sol2ndCommentListResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		private List<MSolutionResp> datas;  // 评论数据

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
		
		public List<MSolutionResp> getDatas() {
			return datas;
		}

		public void setDatas(List<MSolutionResp> datas) {
			this.datas = datas;
		}
	}
	
    //存储本地的护肤方案数据
    public static class SolutionDataGetDelete implements Serializable  {
    	private int type;
    	private int pageNum;
    	private long solutionId;
    	private long localSolutionId;
    	private String tag;  //护肤方案列表分类

		public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        
		public int getPageNum() {
            return pageNum;
        }
        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }
        
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
		
		public String getTag() {
            return tag;
        }
        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    //存储本地的护肤方案数据
    public static class SolutionDataSave implements Serializable  {
    	private int type;
    	private long solutionId;
    	private long localSolutionId;
    	private int sendState;
    	private long updateTime;
    	private String tag;  //护肤方案列表分类
    	private MSolutionResp solutionDetail;  //护肤方案详情/正在使用的护肤方案
		private MSolutionListResp solutionList;  //护肤方案列表
		private SolCommentListResp commentList;  //护肤方案评论列表
		private SolutionCreateReq sendingData;  //发送中护肤方案数据

		public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        
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
        
        public long getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
        
		public String getTag() {
            return tag;
        }
        public void setTag(String tag) {
            this.tag = tag;
        }
        
        public MSolutionResp getSolutionDetail() {
			return solutionDetail;
		}
		public void setSolutionDetail(MSolutionResp solutionDetail) {
			this.solutionDetail = solutionDetail;
		}
		
		public MSolutionListResp getSolutionList() {
			return solutionList;
		}
		public void setSolutionList(MSolutionListResp solutionList) {
			this.solutionList = solutionList;
		}
		
		public SolCommentListResp getCommentList() {
			return commentList;
		}
		public void setCommentList(SolCommentListResp commentList) {
			this.commentList = commentList;
		}
		
		public SolutionCreateReq getSendingData() {
			return sendingData;
		}
		public void setSendingData(SolutionCreateReq sendingData) {
			this.sendingData = sendingData;
		}
    }
}
