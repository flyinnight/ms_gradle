package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;


/**
 * 
 * @author john
 * 话题分组相关
 */

public abstract class TopicGroups {
	//创建用户话题分组
	public abstract void createTopicGroupsAsync(CreateGroupsReq bean,
			BaseCall<CreateUpdateGroupsResp> call);
	//更新用户话题分组
	public abstract void updateTopicGroupsAsync(UpdateGroupsReq bean,
			BaseCall<CreateUpdateGroupsResp> call);
	//显示用户话题分组
	public abstract void getTopicGroupsAsync(BaseReq bean,
			BaseCall<GetGroupsResp> call);
	//点击话题分组进入帖子列表
	public abstract void getUserTopicGroupPostFlowAsync(GetGroupsPostReq bean,
			BaseCall<GetGroupsPostResp> call);
	
	//创建用户话题分组req
	public static class CreateGroupsReq extends BaseReq {
		private List<CreateGroups> createGroups;

		public List<CreateGroups> getCreateGroups() {
			return createGroups;
		}
		public void setCreateGroups(List<CreateGroups> createGroups) {
			this.createGroups = createGroups;
		}
	}

	public static class CreateGroups implements Serializable {
		private String groupName;
		private List<TopicIds> topics;

		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public List<TopicIds> getTopics() {
			return topics;
		}
		public void setTopics(List<TopicIds> topics) {
			this.topics = topics;
		}
	}
	
	public static class TopicIds implements Serializable {
		private long topicId;
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
	}
	
	//更新用户话题分组req
	public static class UpdateGroupsReq extends BaseReq {
		private List<UpdateGroups> updateGroups;

		public List<UpdateGroups> getUpdateGroups() {
			return updateGroups;
		}
		public void setUpdateGroups(List<UpdateGroups> updateGroups) {
			this.updateGroups = updateGroups;
		}
	}
	
	public static class UpdateGroups implements Serializable {
		private long groupId;
		private String groupName;
		private List<TopicIds> topics;

		public long getGroupId() {
			return groupId;
		}
		public void setGroupId(long groupId) {
			this.groupId = groupId;
		}
		
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public List<TopicIds> getTopics() {
			return topics;
		}
		public void setTopics(List<TopicIds> topics) {
			this.topics = topics;
		}
	}
	
	//点击话题分组进入帖子列表req
	public static class GetGroupsPostReq extends BaseReq {
		public long groupId;
		public int pageNo;

		public long getGroupId() {
			return groupId;
		}
		public void setGroupId(long groupId) {
			this.groupId = groupId;
		}

		public int getPageNo() {
			return pageNo;
		}
		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}
	}
	
	
	//创建/更新用户话题分组resp
	public static class CreateUpdateGroupsResp extends BaseResp {
		private List<GroupsList> groups;

		public List<GroupsList> getUpTopicGroups() {
			return groups;
		}
		public void setUpTopicGroups(List<GroupsList> groups) {
			this.groups = groups;
		}
	}

	public static class GroupsList implements Serializable {
		private long groupId;
		private String groupName;

		public long getGroupId() {
			return groupId;
		}
		public void setGroupId(long groupId) {
			this.groupId = groupId;
		}
		
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
	}
	
	//显示用户话题分组resp
	public static class GetGroupsResp extends BaseResp {
		private List<GetGroupsList> groups;

		public List<GetGroupsList> getUpTopicGroups() {
			return groups;
		}
		public void setUpTopicGroups(List<GetGroupsList> groups) {
			this.groups = groups;
		}
	}
	
	public static class GetGroupsList implements Serializable {
		private long groupId;
		private String groupName;
		private List<Integer> topicIds;

		public long getGroupId() {
			return groupId;
		}
		public void setGroupId(long groupId) {
			this.groupId = groupId;
		}
		
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public List<Integer> getTopicIds() {
			return topicIds;
		}
		public void setTopicIds(List<Integer> topicIds) {
			this.topicIds = topicIds;
		}
	}
	
	//点击话题分组进入帖子列表resp
    public static class GetGroupsPostResp extends BaseResp {
        private int totalPage;
        private int pageNo;
        private List<MPostResp> postLists;

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

		public List<MPostResp> getPostLists() {
			return postLists;
		}
		public void setPostLists(List<MPostResp> postLists) {
			this.postLists = postLists;
		}
    }
}
