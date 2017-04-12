/*********************************************************************/
/*  文件名  Register.java    　                                            */
/*  程序名   更新获取发言人接口                    						     				     */
/*  版本履历   2015/5/21 修改                  刘伟    			                         */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;


import java.util.Date;
import java.util.List;

import com.dilapp.radar.domain.Register.RegRadarReq;

public abstract class UpdateGetUser {
	//上传用户头像
	public abstract void uploadPortraitAsync(List<String> imgs, BaseCall<UpdateUserResp> call);
	//更新用户
	public abstract void updateUserAsync(RegRadarReq bean, BaseCall<UpdateUserResp> call);
	//获取发言人
	public abstract void getUserAsync(GetUserReq bean, BaseCall<GetUserResp> call);
	//根据环信Id获取发言人信息(可扩展为获取多人信息)
	public abstract void getUserByEMIdAsync(String EMUserId, BaseCall<GetUserResp> call);

	public static class UpdateUserResp extends BaseResp {
		
        private String portraitURL;
        private String userId;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
        public String getPortraitURL() {
            return portraitURL;
        }
        
        public void setPortraitURL(String portraitURL) {
            this.portraitURL = portraitURL;
        }

	}
	
	public static class GetUserReq extends BaseReq {
		
		public String userId;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
	}
	
	public static class GetUserResp extends BaseResp {
		
		private long id;
		private String userId;
		private String name;  //昵称
		private String EMUserId;  //环信userid
		private int gender;
		private Date birthday;
		private String location;
		private String address;
		private String desc;
		private String occupation;
		private String portrait;
		private int skinQuality;
		private boolean preferChoseSkin;
		private boolean publicPrivacy;
		private int skinQualityCalculated;
		private int level;
		private String qq;
		private String email;
		private String wechat;
		private String blog;
		private String phone;
		private int followCount;
		private int followedCount;
		private int followTopicCount;
		private int topicCount;
		private int storedPostCount;
		private String levelName;
		private boolean isFollowsUser;// 是否关注了该用户
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getEMUserId() {
			return EMUserId;
		}
		public void setEMUserId(String EMUserId) {
			this.EMUserId = EMUserId;
		}
		
		public int getGender() {
			return gender;
		}
		public void setGender(int gender) {
			this.gender = gender;
		}
		
		public Date getBirthday() {
			return birthday;
		}
		public void setBirthday(Date birthday) {
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
		
		public int getSkinQualityCalculated() {
			return skinQualityCalculated;
		}
		public void setSkinQualityCalculated(int skinQualityCalculated) {
			this.skinQualityCalculated = skinQualityCalculated;
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
		
		public int getSkinQuality() {
			return skinQuality;
		}
		public void setSkinQuality(int skinQuality) {
			this.skinQuality = skinQuality;
		}
		
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		
		public String getQq() {
			return qq;
		}
		public void setQq(String qq) {
			this.qq = qq;
		}
		
		public String getLevelName() {
			return levelName;
		}
		public void setLevelName(String levelName) {
			this.qq = levelName;
		}
		
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getWechat() {
			return wechat;
		}
		public void setWechat(String wechat) {
			this.wechat = wechat;
		}
		
		public String getBlog() {
			return blog;
		}
		public void setBlog(String blog) {
			this.blog = blog;
		}
		
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		
		public int getFollowCount() {
			return followCount;
		}
		public void setFollowCount(int followCount) {
			this.followCount = followCount;
		}
		
		public int getFollowedCount() {
			return followedCount;
		}
		public void setFollowedCount(int followedCount) {
			this.followedCount = followedCount;
		}
		
		public int getFollowTopicCount() {
			return followTopicCount;
		}
		public void setFollowTopicCount(int followTopicCount) {
			this.followTopicCount = followTopicCount;
		}
		
		public int getTopicCount() {
			return topicCount;
		}
		public void setTopicCount(int topicCount) {
			this.topicCount = topicCount;
		}
		
		public int getStoredPostCount() {
			return storedPostCount;
		}
		public void setStoredPostCount(int storedPostCount) {
			this.storedPostCount = storedPostCount;
		}
		
		public boolean isFollowsUser() {
            return isFollowsUser;
        }
        public void setFollowsUser(boolean isFollowsUser) {
            this.isFollowsUser = isFollowsUser;
        }
	}

}
