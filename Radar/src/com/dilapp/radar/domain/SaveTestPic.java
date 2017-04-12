package com.dilapp.radar.domain;

import java.util.List;

/**
 * 
 * @author john
 *	保存测试数据的图片
 */
public abstract class  SaveTestPic {
	//上传图片
	public abstract void uploadFacialPicAsync(List<String> imgs, BaseCall<FacialPicResp> call);
	//保存图片数据
	public abstract void saveFacialPicAsync(FacialPicReq bean, BaseCall<FacialPicResp> call);

	/**
	 * 保存测试数据图片 Bean
	 * 
	 * @author john
	 */
	public static class FacialPicReq extends BaseReq {
		// 图片url地址
		private String picUrl;
		// 测试部位
		private String part;
		// 数据采集到的时间
		private long day;
		// 是否使用化妆品
		private int isUseComestic;
		// 肤质
		private int skinQuality;

		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		
		public String getPart() {
			return part;
		}
		public void setPart(String part) {
			this.part = part;
		}
		
		public long getDay() {
			return day;
		}
		public void setDay(long day) {
			this.day = day;
		}
		
		public int getUseComestic() {
			return isUseComestic;
		}
		public void setUseComestic(int isUseComestic) {
			this.isUseComestic = isUseComestic;
		}
		
		public int getSkinQuality() {
			return skinQuality;
		}
		public void setSkinQuality(int skinQuality) {
			this.skinQuality = skinQuality;
		}
	}

	/**
	 * 保存测试数据图片 结果
	 * 
	 * @author john
	 */
	public class FacialPicResp extends BaseResp {
		private List<String> facialPicsUrl;  //存储的图片url
		private long id;  //图片数据id
		private String userName;  //用户昵称

		public List<String> getFacialPicsUrl() {
			return facialPicsUrl;
		}
		public void setFacialPicsUrl(List<String> facialPicsUrl) {
			this.facialPicsUrl = facialPicsUrl;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
}
