/*********************************************************************/
/*  文件名  Register.java    　                                            */
/*  程序名   注册接口                    						     				     */
/*  版本履历   2015/5/21 修改                  刘伟    			                         */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

import java.util.Date;

public abstract class Register {

	public abstract void regAsync(RegReq bean, BaseCall<BaseResp> call);
	
	public abstract void regRadarAsync(RegRadarReq bean, BaseCall<RegRadarResp> call);

	public static class RegReq extends BaseReq {
		// Gender type
		public static final int MALE = 1;
		public static final int FEMALE = 2;
		public static final int UNKNOWN = 3; //保密
		
		public String userId;
		public String pwd;
		//Email默认0
		public String verifyCode;
		//区号 国家码 86
		public String regionCode;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPwd() {
			return pwd;
		}
		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		
		public String getVerifyCode() {
			return verifyCode;
		}
		public void setVerifyCode(String verifyCode) {
			this.verifyCode = verifyCode;
		}

		public String getRegionCode() {
			return regionCode;
		}
		public void setRegionCode(String regionCode) {
			this.regionCode = regionCode;
		}
	}
	
	public static class RegRadarReq extends BaseReq {
		
		//public Long id;
		//public String userId;
		public String name;  //昵称
		public int gender;
		public long birthday;
		public String location;
		public String address;
		public String desc;
		public String occupation;
		public String portrait;
		public int skinQuality;
		public boolean preferChoseSkin;
		public boolean publicPrivacy;
		public String qq;
		public String email;
		public String wechat;
		public String blog;
		public String phone;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public int getGender() {
			return gender;
		}
		public void setGender(int gender) {
			this.gender = gender;
		}
		
		public long getBirthday() {
			return birthday;
		}
		public void setBirthday(long birthday) {
			this.birthday = birthday;
		}
		
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		public String getOccupation() {
			return occupation;
		}
		public void setOccupation(String occupation) {
			this.occupation = occupation;
		}
		
		public String getPortrait() {
			return portrait;
		}
		public void setPortrait(String portrait) {
			this.portrait = portrait;
		}
		
		public int getSkinQuality() {
			return skinQuality;
		}
		public void setSkinQuality(int skinQuality) {
			this.skinQuality = skinQuality;
		}
		
		public boolean getPreferChoseSkin() {
			return preferChoseSkin;
		}
		public void setPreferChoseSkin(boolean preferChoseSkin) {
			this.preferChoseSkin = preferChoseSkin;
		}
		
		public boolean getPublicPrivacy() {
			return publicPrivacy;
		}
		public void setPublicPrivacy(boolean publicPrivacy) {
			this.publicPrivacy = publicPrivacy;
		}
		
		public String getQq() {
			return qq;
		}
		public void setQq(String qq) {
			this.qq = qq;
		}
		
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getBlog() {
			return blog;
		}
		public void setBlog(String blog) {
			this.blog = blog;
		}
		
		public String getWechat() {
			return wechat;
		}
		public void setWechat(String wechat) {
			this.wechat = wechat;
		}
		
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
	}

	public static class RegRadarResp extends BaseResp {
		public String userId;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
	}
}
