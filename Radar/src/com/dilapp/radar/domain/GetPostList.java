package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;


/**
 * 某个话题下的帖子列表
 *
 * @author john
 */
public abstract class GetPostList {

	public final static int GET_DATA_SERVER = 0;  // 从服务器获取数据
	public final static int GET_DATA_LOCAL = 1;  // 从本地获取数据
	
	public final static int UPDATE_SPAN_TIME = 600000;  // 间隔固定时间更新一次本地数据(毫秒ms)
	
	public static final int POST_LIST_RECOMMEND_BYTEST = 1;   //根据测试结果推荐的贴子列表
	public static final int POST_LIST_BY_SEND = 2;   //自己发布的帖子列表
	public static final int POST_LIST_BY_STORE = 3;   //自己收藏的帖子列表
	public static final int POST_LIST_BY_COMMENT = 4;   //自己的评论列表
	public static final int POST_LIST_BY_COMMENT_PARENT = 5;   //自己的评论所属的原帖列表
	public static final int POST_LIST_BY_REPLY = 6;   //读取从帖的回复(回复大于3条时单独接口)
	public static final int POST_LIST_BY_USER_SEND = 7;   //他人发布的帖子列表
	
    // 获得某个话题的帖子列表
    /**
     * 获得某个话题的帖子列表
     * @param bean
     * @param call
     * @param type  区分本地数据网络数据
     * @return
     */
    public abstract void getPostsOfOneTopicByTypeAsync(MPostReq bean, BaseCall<TopicPostListResp> call, int type);

    // 主页显示用户关注话题的帖子列表
    public abstract void getPostsOfFollowTopicByTypeAsync(MFollowTopicPostReq bean, BaseCall<TopicPostListResp> call, int type);
    
    // 获取用户评论过的帖子以及原帖
    public abstract void getCommentedPostByTypeAsync(CommentedPostReq bean, BaseCall<CommentedPostListResp> call, int type);
    
	// 根据测试结果推荐贴子
	public abstract void recommendPostsByTestAsync(PostsTestReq bean, BaseCall<TopicPostListResp> call);

	
	// 请求参数
	public static class PostsTestReq extends BaseReq {
		// 查询条件
		private String[] postParam;
		int startNo;

		public String[] getPostParam() {
			return postParam;
		}
		public void setPostParam(String[] postParam) {
			this.postParam = postParam;
		}
		
		public int getStartNo() {
			return startNo;
		}
		public void setStartNo(int startNo) {
			this.startNo = startNo;
		}
	}
	

    public static class MFollowTopicPostReq extends BaseReq {
        // 分页
        private int pageNo;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }
    }

    public static class MPostReq extends BaseReq {

        private long topicId;
        // 分页
        private int pageNo;

        public long getTopicId() {
            return topicId;
        }

        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

    }
    
    public static class CommentedPostReq extends BaseReq {
        // 分页
        private int pageNo;

        public int getPageNo() {
            return pageNo;
        }
        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }
    }

    
    //某话题下帖子列表resp
    public static class TopicPostListResp extends BaseResp {
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
    
    //存储主页帖子列表
    public static class MainPostListSave implements Serializable {
        private long updateTime;
		private List<MPostResp> postLists;

        public long getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

		public List<MPostResp> getPostLists() {
			return postLists;
		}
		public void setPostLists(List<MPostResp> postLists) {
			this.postLists = postLists;
		}
    }
    
    //用户评论过的帖子以及原帖resp
    public static class CommentedPostListResp extends BaseResp {
        private int totalPage;
        private int pageNo;
		private List<MPostResp> postLists;
		private List<MPostResp> parentpostLists;

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
		
		public List<MPostResp> getParenPostLists() {
			return parentpostLists;
		}
		public void setParenPostLists(List<MPostResp> parentpostLists) {
			this.parentpostLists = parentpostLists;
		}
    }
    
    //存储本地的帖子列表
    public static class PostListSave implements Serializable  {
        private int type;
        private long updateTime;
		private List<MPostResp> postList;

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
        
		public List<MPostResp> getPostList() {
			return postList;
		}
		public void setPostList(List<MPostResp> postList) {
			this.postList = postList;
		}
    }
    
    //存储本地的话题详情信息
    public static class TopicDetailSave implements Serializable  {
    	private int type;
    	private long topicId;
    	private long updateTime;
    	private MTopicResp topicContent;
		private TopicPostListResp detailList;

		public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        
		public long getTopicId() {
            return topicId;
        }
        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }

        public long getUpdateTime() {
            return updateTime;
        }
        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
        
        public MTopicResp getTopicContent() {
			return topicContent;
		}
		public void setTopicContent(MTopicResp topicContent) {
			this.topicContent = topicContent;
		}
		
		public TopicPostListResp getPostList() {
			return detailList;
		}
		public void setPostList(TopicPostListResp detailList) {
			this.detailList = detailList;
		}
    }
    
    //获取本地的话题详情信息
    public static class TopicDetailGet implements Serializable  {
    	private int type;
    	private long topicId;

		public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        
		public long getTopicId() {
            return topicId;
        }
        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }
    }
    
    public static class MPostResp extends BaseResp {
        private String userHeadIcon;
        private long topicId;
        private String topicTitle;
        //postId
        private long id;
        // post's local Id 发送成功前，预览及本地存储之用
     	private long localPostId;
        // 贴子发送状态，预览及本地存储之用
     	private int sendState;
        //post's parent Id
        private long pid;
        private int postLevel;
        private String userId;
        private String userName;
        private String EMUserId;  //环信userid
        private String toUserId;
        private String toUserName;
        private String postTitle;
        private String postContent;
        private int followsUpNum;
        private int storeupNum;
        private boolean selectedToSolution;
        private String effect;
        private String part;
        private boolean report;
        private boolean onTop;
        private List<String> thumbURL;
        private int like;
        private int dislike;
        private long updateTime;
        private long createTime;
        private String qq;
        private String email;
        private String wechat;
        private String blog;
        private int gender;
        private String desc;
        private String occupation;
        //private String portrait;
        private String skinQuality;
        private String location;

        private int level;
        private String levelName;
        private String birthday;

        // 回复量 回复总数
        private int totalFollows;
        // 浏览数量/围观数量
        private int postViewCount;
        private boolean isStoreUp;  //是否收藏
        private boolean isLike;  //是否点赞
        private boolean isFollowsUser;// 是否关注了该用户

        public String getLevelName() {
            return levelName;
        }

        public void setLevelName(String levelName) {
            this.levelName = levelName;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public long getTopicId() {
            return topicId;
        }

        public void setTopicId(long topicId) {
            this.topicId = topicId;
        }

        public String getTopicTitle() {
            return topicTitle;
        }

        public void setTopicTitle(String topicTitle) {
            this.topicTitle = topicTitle;
        }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        
        public long getLocalPostId() {
			return localPostId;
		}
		public void setLocalPostId(long localPostId) {
			this.localPostId = localPostId;
		}
		
		public int getSendState() {
			return sendState;
		}
		public void setSendState(int sendState) {
			this.sendState = sendState;
		}
		
        public long getPid() {
            return pid;
        }
        public void setPid(long pid) {
            this.pid = pid;
        }

        public int getPostLevel() {
            return postLevel;
        }

        public void setPostLevel(int postLevel) {
            this.postLevel = postLevel;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

		public String getEMUserId() {
			return EMUserId;
		}
		public void setEMUserId(String EMUserId) {
			this.EMUserId = EMUserId;
		}
		
        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public String getToUserName() {
            return toUserName;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public String getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(String postTitle) {
            this.postTitle = postTitle;
        }

        public String getPostContent() {
            return postContent;
        }

        public void setPostContent(String postContent) {
            this.postContent = postContent;
        }

        public int getFollowsUpNum() {
            return followsUpNum;
        }

        public void setFollowsUpNum(int followsUpNum) {
            this.followsUpNum = followsUpNum;
        }

        public int getStoreupNum() {
            return storeupNum;
        }

        public void setStoreupNum(int storeupNum) {
            this.storeupNum = storeupNum;
        }

        public boolean isSelectedToSolution() {
            return selectedToSolution;
        }

        public void setSelectedToSolution(boolean selectedToSolution) {
            this.selectedToSolution = selectedToSolution;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public String getPart() {
            return part;
        }

        public void setPart(String part) {
            this.part = part;
        }

        public boolean getReport() {
            return report;
        }

        public void setReport(boolean report) {
            this.report = report;
        }

        public boolean getOnTop() {
            return onTop;
        }

        public void setOnTop(boolean onTop) {
            this.onTop = onTop;
        }

        public List<String> getThumbURL() {
            return thumbURL;
        }

        public void setThumbURL(List<String> thumbURL) {
            this.thumbURL = thumbURL;
        }
        
        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getDislike() {
            return dislike;
        }

        public void setDislike(int dislike) {
            this.dislike = dislike;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
        
        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
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

        public int isGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
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

        public String getSkinQuality() {
            return skinQuality;
        }

        public void setSkinQuality(String skinQuality) {
            this.skinQuality = skinQuality;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getUserHeadIcon() {
            return userHeadIcon;
        }

        public void setUserHeadIcon(String userHeadIcon) {
            this.userHeadIcon = userHeadIcon;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getPostViewCount() {
            return postViewCount;
        }

        public void setPostViewCount(int postViewCount) {
            this.postViewCount = postViewCount;
        }

        public int getTotalFollows() {
            return totalFollows;
        }

        public void setTotalFollows(int totalFollows) {
            this.totalFollows = totalFollows;
        }

        public boolean isStoreUp() {
            return isStoreUp;
        }

        public void setStoreUp(boolean isStoreUp) {
            this.isStoreUp = isStoreUp;
        }

        public boolean isLike() {
            return isLike;
        }

        public void setLike(boolean isLike) {
            this.isLike = isLike;
        }

        public boolean isFollowsUser() {
            return isFollowsUser;
        }

        public void setFollowsUser(boolean isFollowsUser) {
            this.isFollowsUser = isFollowsUser;
        }
        
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}

        @Override
        public boolean equals(Object o) {
            return o instanceof MPostResp && ((MPostResp) o).id == id;
        }
    }


}
