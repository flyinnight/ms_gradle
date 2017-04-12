package com.dilapp.radar.domain;

import java.util.List;


/**
 * 
 * @author john
 *	护肤方案收藏应用相关
 */
public abstract class SolutionCollectApply {

	//用户收藏帖子为自己护肤方案
	public abstract void storeupPostAsSolutionAsync(StoreupSolutionReq bean, BaseCall<BaseResp> call);
	//修改一个帖子是否为护肤方案
	public abstract void changePostToSolutionAsync(ChangePostSolutionReq bean, BaseCall<BaseResp> call);
	//选定为应用的护肤方案
	public abstract void selectedSolutionAsync(SelectedSolutionReq bean, BaseCall<SelectedSolutionResp> call);

	/**
	 * 用户收藏帖子为自己护肤方案 Bean
	 * 
	 * @author john
	 */
	public static class StoreupSolutionReq extends BaseReq {

		private long postId;
		private boolean selectedSolution;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public boolean getSelectedSolution() {
			return selectedSolution;
		}
		public void setSelectedSolution(boolean selectedSolution) {
			this.selectedSolution = selectedSolution;
		}
	}

	/**
	 * 修改一个帖子是否为护肤方案 Bean
	 * 
	 * @author john
	 */
	public static class ChangePostSolutionReq extends BaseReq {

		private long postId;
		private String part;
		private String effect;
		private String skin;
		private boolean setPlan;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public String getPart() {
			return part;
		}
		public void setPart(String part) {
			this.part = part;
		}
		
		public String getEffect() {
			return effect;
		}
		public void setEffect(String effect) {
			this.effect = effect;
		}
		
		public String getSkin() {
			return skin;
		}
		public void setSkin(String skin) {
			this.skin = skin;
		}
		
		public boolean getSetPlan() {
			return setPlan;
		}
		public void setSetPlan(boolean setPlan) {
			this.setPlan = setPlan;
		}
	}

	/**
	 * 选定为应用的护肤方案 Bean
	 * 
	 * @author john
	 */
	public static class SelectedSolutionReq extends BaseReq {

		private long postId;
		private boolean selected;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public boolean getSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
	}
	
	
	/**
	 * 选定为应用的护肤方案 Resp
	 * 
	 * @author john
	 */
	public class SelectedSolutionResp extends BaseResp {

		private long postId;

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
	}
}
