package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;



/**
 * 
 * @author john
 * 获取发言人关系列表
 */
public abstract class GetUserRelation {
	
	public static final int USER_RELATION_BY_FOLLOWS = 1;   //关注列表
	public static final int USER_RELATION_BY_FANS = 2;   //粉丝列表
	
	
	//获取自己和他人的关注列表
	public abstract void getUserFollowsListByTypeAsync(getUserListReq bean, BaseCall<getUserRelationResp> call, int type);
	//获取自己和他人的粉丝列表
	public abstract void getUserFansListByTypeAsync(getUserListReq bean, BaseCall<getUserRelationResp> call, int type);
	

	public static class getUserRelationReq extends BaseReq {
		public int pageNo;
		//true获取关注列表，false获取粉丝列表
		public boolean follow;
		
		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}

		public boolean getFollow() {
			return follow;
		}

		public void setFollow(boolean follow) {
			this.follow = follow;
		}
	}
	
	public static class getUserListReq extends BaseReq {
		public String userId;
		public int pageNo;
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}

	public static class getUserRelationResp extends BaseResp {
		// 总页数
		private int allPages;
		// 当前页
		private int currPage;
		//关系列表
		private List<RelationList> mRelationList;

		public int getTotalPage() {
			return allPages;
		}

		public void setTotalPage(int allPages) {
			this.allPages = allPages;
		}
		
		public int getPageNo() {
			return currPage;
		}

		public void setPageNo(int currPage) {
			this.currPage = currPage;
		}
		
		public List<RelationList> getDatas() {
			return mRelationList;
		}

		public void setDatas(List<RelationList> mRelationList) {
			this.mRelationList = mRelationList;
		}
	}
	
	public static class RelationList {
		// user id
		private String userId;
		// 昵称
		private String name;
		private String EMUserId;  //环信userid
		// 头像地址
		private String portrait;
		// 肤质
		private int skin;
		// 等级
		private int level;
		private int gender;
		private String levelName;
		private String location;

		private int followsCount;
		private int fansCount;
		private boolean isFollowsUser;// 是否关注了该用户
		
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
		
		public String getPortrait() {
			return portrait;
		}
		public void setPortrait(String portrait) {
			this.portrait = portrait;
		}

		public int getSkin() {
			return skin;
		}
		public void setSkin(int skin) {
			this.skin = skin;
		}

		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
		
		public int getGender() {
			return gender;
		}
		public void setGender(int gender) {
			this.gender = gender;
		}
		
		public String getLevelName() {
			return levelName;
		}
		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}
		
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		
		public int getFollowsCount() {
			return followsCount;
		}
		public void setFollowsCount(int followsCount) {
			this.followsCount = followsCount;
		}
		
		public int getFansCount() {
			return fansCount;
		}
		public void setFansCount(int fansCount) {
			this.fansCount = fansCount;
		}
		
		public boolean isFollowsUser() {
            return isFollowsUser;
        }
        public void setFollowsUser(boolean isFollowsUser) {
            this.isFollowsUser = isFollowsUser;
        }

		@Override// 不要删掉
		public boolean equals(Object o) {
			return o instanceof RelationList &&
					((RelationList)o).userId.equals(userId);
		}
	}
	
    //存储本地的用户关系数据
    public static class UserRelationSave implements Serializable  {
    	private String userId;
    	private int type;
    	private long updateTime;
    	private getUserRelationResp userResp;

		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

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
        
        public getUserRelationResp getUserResp() {
			return userResp;
		}
		public void setUserResp(getUserRelationResp userResp) {
			this.userResp = userResp;
		}
    }
    
    //获取本地的用户关系数据
    public static class UserRelationGetLocal implements Serializable  {
    	private String userId;
    	private int type;

		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

		public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
    }
}
