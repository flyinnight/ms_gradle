package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;


/***
 * 护肤方案列表相关
 * @author john
 *
 */
public abstract class SolutionList {

	public static final int SOLUTION_LIST_USER_STORE = 1;   //用户收藏的护肤方案列表
	public static final int SOLUTION_LIST_BY_PART = 2;   //根据部位显示护肤方案列表
	public static final int SOLUTION_LIST_BY_EFFECT = 3;   //根据功效显示护肤方案列表
	public static final int SOLUTION_LIST_RANK = 4;   //护肤方案排行榜
	
	
	//用户护肤方案列表
	public abstract void solutionListByTypeAsync(SolutionListReq bean, BaseCall<SolutionListResp> call, int type);
	
	//根据部位显示护肤方案列表
	public abstract void solutionListPartByTypeAsync(SolutionListPartReq bean, BaseCall<SolutionListResp> call, int type);
	
	//根据功效显示护肤方案列表
	public abstract void solutionListEffectByTypeAsync(SolutionListEffectReq bean, BaseCall<SolutionListResp> call, int type);
	
	//护肤方案排行榜
	public abstract void solutionListRankByTypeAsync(SolutionRankReq bean, BaseCall<SolutionListResp> call, int type);
	
	
	public static class SolutionListReq extends BaseReq {
		private int pageNo;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}		
	}
	
	public static class SolutionListResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		private List<SolutionResp> solutions;
		
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
		
		public List<SolutionResp> getSolutions() {
			return solutions;
		}
		public void setSolutions(List<SolutionResp> solutions) {
			this.solutions = solutions;
		}
	}
	

	public static class SolutionListPartReq extends BaseReq {
		private int pageNo;
		private String part;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public String getPart() {
			return part;
		}
		public void setPart(String part) {
			this.part = part;
		}
	}
	
	
	public static class SolutionListEffectReq extends BaseReq {
		private int pageNo;
		private String effect;
		
		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public String getEffect() {
			return effect;
		}
		public void setEffect(String effect) {
			this.effect = effect;
		}
	}
	
	
	public static class SolutionRankReq extends BaseReq {
		private int pageNo;
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}		
	}
	
    //存储本地的护肤方案列表
    public static class SolutionListSave implements Serializable  {
        private int type;
        private long updateTime;
		private List<SolutionResp> solutionList;

        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        
        public long getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

		public List<SolutionResp> getSolutionList() {
			return solutionList;
		}
		public void setSolutionList(List<SolutionResp> solutionList) {
			this.solutionList = solutionList;
		}
    }
    
}
