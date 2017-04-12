package com.dilapp.radar.domain;



/**
 * 
 * @author john
 *	护肤方案操作相关
 */
public abstract class SolutionOperate {

	// 删除护肤方案
	public abstract void solutionDeleteAsync(long solutionId, BaseCall<BaseResp> call);
	// 收藏护肤方案
	public abstract void solutionStoreupAsync(StoreupReq bean, BaseCall<BaseResp> call);
	// 使用护肤方案
	public abstract void solutionUseAsync(UseReq bean, BaseCall<BaseResp> call);

	
	public static class StoreupReq extends BaseReq {
		private long solutionId;
		private boolean isStoreup;

		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public boolean getIsStoreup() {
			return isStoreup;
		}
		public void setIsStoreup(boolean isStoreup) {
			this.isStoreup = isStoreup;
		}
	}
	
	public static class UseReq extends BaseReq {
		private long solutionId;
		private boolean isUse;

		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public boolean getIsUse() {
			return isUse;
		}
		public void setIsUse(boolean isUse) {
			this.isUse = isUse;
		}
	}
	
}
