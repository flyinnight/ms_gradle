package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.MPostResp;

/**
 * 1.预置话题列表请求接口2.自动推荐话题列表请求接口3.请求分组项接口 4.分组查询接口
 * 
 * @author john
 */
public abstract class TopicListCallBack {

	public static final int TOPIC_LIST_ALL = 1;   //话题大全
	public static final int TOPIC_LIST_BY_SEND = 2;   //自己发布的话题列表
	public static final int TOPIC_LIST_BY_FOLLOW = 3;   //自己关注的话题列表
	public static final int TOPIC_LIST_BY_RECOMMEND_AUTO = 4;   //自动推荐话题列表
	public static final int TOPIC_LIST_BY_RECOMMEND_TEST = 5;   //根据测试结果推荐话题列表
	public static final int TOPIC_LIST_BY_TOPFAVOUR = 6;   //用户最关注话题列表
	
	public static final int TOPIC_DETAIL_CONTENT = 7;   //单个话题/话题详情:话题描述
	public static final int TOPIC_DETAIL_POSTLIST = 8;   //单个话题/话题详情:帖子列表
	
	public static final int TOPIC_LIST_PRESET = 9;   //预置话题列表
	public static final int TOPIC_LIST_BY_USER_SEND = 10;   //他人发布的话题列表

	// 自动推荐话题列表请求
	public abstract void getRecommendTopicListByTypeAsync(BaseReq bean, BaseCall<TopicListResp> call, int type);
	
	// 根据测试结果推荐话题(暂未使用)
	public abstract void getRecommendTopicListOfTestAsync(TopicsTestReq bean, BaseCall<TopicListResp> call);

	// 获取单个话题/话题详情
	public abstract void getTopicDetailByTypeAsync(TopicDetailReq bean, BaseCall<MTopicResp> call, int type);
	
	// 用户最关注话题列表
	public abstract void getTopFavourTopicListByTypeAsync(BaseReq bean, BaseCall<TopicListResp> call, int type);
	
	
	// 请求参数
	public static class TopicsTestReq extends BaseReq {
		// 查询条件
		private String[] topicParam;

		public String[] getTopicParam() {
			return topicParam;
		}

		public void setTopicParam(String[] topicParam) {
			this.topicParam = topicParam;
		}
	}
	
	// 请求参数
	public static class TopicDetailReq extends BaseReq {
		private long topicId;

        public long getTopicId() {
            return topicId;
        }
        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }
	}
	
	public static class TopicListResp extends BaseResp {

		private List<MTopicResp> datas;

		public List<MTopicResp> getDatas() {
			return datas;
		}

		public void setDatas(List<MTopicResp> datas) {
			this.datas = datas;
		}
	}

    //存储本地的话题列表
    public static class TopicListSave implements Serializable  {
        private int type;
        private long updateTime;
		private List<MTopicResp> topicList;

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

		public List<MTopicResp> getTopicList() {
			return topicList;
		}
		public void setTopicList(List<MTopicResp> topicList) {
			this.topicList = topicList;
		}
    }

	/**
	 * 详情返回Bean
	 *
	 * @author john
	 */
	public static class MTopicResp extends BaseResp {
		// 用户名
		private String username;
		// 用户性别
		private boolean usergender;
		// 用户level
		private int userlevel;
		// 话题标题
		private String topictitle;
		//话题图片
		private String topicURL;
		// 话题图片url列表
		private String[] topicimg;
		// 发布时间
		private long releasetime;
		// 访问量
		private int visits;
		// 回复量/贴子数量
		private int regen;
		//
		private long topicId;
		//目前用的是content
		//private String topicDes;
		//
		private String userId;
		//
		private boolean followup;
		//
		private int followsUpNum;

		// 话题内容 TODO:建议后面换成帖子对象
		private String content;

		public String getTopicURL() {
			return topicURL;
		}

		public void setTopicURL(String topicURL) {
			this.topicURL = topicURL;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public boolean getUsergender() {
			return usergender;
		}

		public void setUsergender(boolean usergender) {
			this.usergender = usergender;
		}

		public int getUserlevel() {
			return userlevel;
		}

		public void setUserlevel(int userlevel) {
			this.userlevel = userlevel;
		}

		public String getTopictitle() {
			return topictitle;
		}

		public void setTopictitle(String topictitle) {
			this.topictitle = topictitle;
		}

		public String[] getTopicimg() {
			return topicimg;
		}

		public void setTopicimg(String[] topicimg) {
			this.topicimg = topicimg;
		}
		
		public long getReleasetime() {
			return releasetime;
		}

		public void setReleasetime(long releasetime) {
			this.releasetime = releasetime;
		}

		public int getVisits() {
			return visits;
		}

		public void setVisits(int visits) {
			this.visits = visits;
		}

		public long getTopicId() {
			return topicId;
		}

		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public boolean getFollowup() {
			return followup;
		}

		public void setFollowup(boolean followup) {
			this.followup = followup;
		}

		public int getFollowsUpNum() {
			return followsUpNum;
		}

		public void setFollowsUpNum(int followsUpNum) {
			this.followsUpNum = followsUpNum;
		}

		public int getRegen() {
			return regen;
		}

		public void setRegen(int regen) {
			this.regen = regen;
		}

	}
}
