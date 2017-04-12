/*********************************************************************/
/*  文件名  HttpConstant.java    　                                 	 */
/*  程序名  Http常量                     						     					 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.util;

public final class HttpConstant {

	public static String TOKEN = null;
	public static final boolean DEBUG_VERSION = ReleaseUtils.DEBUG_REMOTE_MODE;

	private static final String DEBUG_USER_HOST_IP = "http://10.4.65.4:8081/";
	private static final String RELEASE_USER_HOST_IP = "http://121.41.79.23:8081/";

	private static final String DEBUG_RADAR_HOST_IP = "http://10.4.65.4:80/";
	private static final String RELEASE_RADAR_HOST_IP = "http://121.41.79.23:80/";

	private static final String DEBUG_FTP_IP = "http://114.215.181.127:8083/";
	private static final String RELEASE_FTP_IP = "http://114.215.181.127:8083/";
	// user系统测试环境
	private static final String TEST_USER_HOST_IP = "http://10.4.64.247:8081/";
	// radar系统测试环境
	private static final String TEST_RADAR_HOST_IP = "http://10.4.64.248:9091/";
	// 亚莉环境ip
	private static final String YL_HOST_IP = "http://10.4.64.119:8080/";
	// 正式环境
	public static final String OFFICIAL_USER_HOST_IP = DEBUG_VERSION ? DEBUG_USER_HOST_IP
			: RELEASE_USER_HOST_IP;// "http://121.41.79.23:8081/";
	// radar系统正式环境
	public static final String OFFICIAL_RADAR_HOST_IP = DEBUG_VERSION ? DEBUG_RADAR_HOST_IP
			: RELEASE_RADAR_HOST_IP;// "http://121.41.79.23:9091/";
	// radar正式环境下载图片地址
	public static final String OFFICIAL_RADAR_DOWNLOAD_IMG_IP = OFFICIAL_RADAR_HOST_IP + "radar/file/downloader/";
	// ftp下载版本环境
	public static final String FTP_GET_VER_IP = DEBUG_VERSION ? DEBUG_FTP_IP
			: RELEASE_FTP_IP;// "http://114.215.181.127:8083/";
	// 进一步了解radar链接
	public static final String GET_RADAR_INFO_MORE = RELEASE_RADAR_HOST_IP+"radar";
	// user注册接口
	private static final String USER_REG = "user/register";
	// user登录接口
	private static final String USER_LOGIN = "user/login";
	// radar注册接口
	private static final String RADAR_REG = "radar/user/registerUser";
	// radar登录接口
	private static final String RADAR_LOGIN = "radar/user/login";
	// 添加测试数据接口
	private final static String SAVE_RESULT = "radar/analyze/save";
	// 插入多条测试数据接口
	private final static String SAVE_MULTY_RESULTS = "radar/analyze/saveResults";
	// 根据条件查询测试数据接口
	private final static String QUERY_TEST_RECORDS = "radar/analyze/queryByTypeAndPart";
	//根据条件查询服务器上平均测试数据
	private final static String QUERY_CHART_DATA = "radar/analyze/queryChartData";
	// 获取预置话题列表
	private final static String FIXED_TOPIC_LIST = "radar/fixedTopicList";
	// 上传话题图片
	private final static String UPLOAD_TOPIC_IMG = "radar/uploadTopicImg";
	// 上传话题信息
	private final static String CREATE_TOPIC = "radar/createTopic";
	// 更新话题信息
	private final static String UPDATE_TOPIC = "radar/updateTopic";
	// 用户话题列表
	private final static String USER_TOPICLIST = "radar/userTopicList";
	// 获取偏好内容列表
	private final static String USER_FAVOUR_CONTENTLIST = "radar/user/getFavourContentList";
	// 所有话题列表
	private final static String TOPICLIST = "radar/topicList";
	// 自动推荐话题列表
	private final static String RECOMMEND_TOPICLIST = "radar/getRecommendTopics";
	// 根据测试结果推荐话题
	private final static String RECOMMEND_TOPIC_TEST = "radar/recommendTopic";
	// 根据测试结果推荐贴子
	private final static String RECOMMEND_POST_TEST = "radar/recommendPost";
	// 上传帖子图片
	private final static String UPLOAD_POST_IMG = "radar/uploadPostImg";
	// 发布帖子信息
	private final static String CREATE_POST = "radar/createPost";
	// 更新帖子信息
	private final static String UPDATE_POST = "radar/updatePost";
	// 分页获取帖子列表
	private final static String POST_LIST = "radar/postList";
	// 用户护肤方案列表
	private final static String GET_SKIN_SOLUTION_LIST = "radar/getSkinSolutionList";
	// 根据部位显示护肤方案列表
	private final static String GET_SKIN_SOLUTION_LIST_PART = "radar/getSkinSolutionListByPart";
	// 根据功效显示护肤方案列表
	private final static String GET_SKIN_SOLUTION_LIST_EFFECT = "radar/getSkinSolutionListByEffect";
	// 护肤方案排行榜
	private final static String GET_SKIN_SOLUTION_RANK = "radar/getSkinSolutionRank";
	// 查询返回话题列表
	private final static String SEARCH_RETURN_TOPIC = "radar/searchReturnTopic";
	// 查询返回帖子列表
	private final static String SEARCH_RETURN_POST = "radar/searchReturnPost";
	// 主页显示用户关注话题的帖子列表
	private final static String GET_POSTS_OF_FOLLOW_TOPIC = "radar/getPostsOfFollowTopic";
	// 进入主贴，分页获取从帖列表
	private final static String READ_MAIN_POST = "radar/readMainPost";
	// 读取跟帖的回复
	private final static String READ_REPLY = "radar/readReply";
	// 收藏帖子
	private final static String STOREUP_POST = "radar/storeupPost";
	// 删除帖子
	private final static String DELETE_POST = "radar/deletePost";
	// 删除话题
	private final static String DELETE_TOPIC = "radar/deleteTopic";
	// 关注帖子
	private final static String FOLLOWUP_POST = "radar/followupPost";
	// 关注话题
	private final static String FOLLOWUP_TOPIC = "radar/followupTopic";
	// 点赞帖子
	private final static String LIKE_POST = "radar/likePost";
	// 反感帖子
	private final static String DISLIKE_POST = "radar/dislikePost";
	// 新增帖子的浏览数目
	private final static String ADD_POST_VIEW_COUNT = "radar/addPostViewCount";
	// 获取发言人关系列表
	private final static String GET_USER_RELATION = "radar/user/getUserRelation";
	// 获取发言人关系列表
	private final static String FOLLOW_USER = "radar/user/followUser";
	// 创建用户话题分组
	private final static String CREATE_TOPIC_GROUPS = "radar/createTopicGroups";
	// 更新用户话题分组
	private final static String UPDATE_TOPIC_GROUPS = "radar/updateTopicGroups";
	// 显示用户话题分组
	private final static String GET_TOPIC_GROUPS = "radar/getTopicGroups";
	// 点击话题分组进入帖子列表
	private final static String GET_TOPIC_GROUPS_POST = "radar/getUserTopicGroupPostFlow";
	// 上传用户头像
	private final static String UPLOAD_PORTRAIN = "radar/user/uploadPortrait";
	// 更新用户
	private final static String UPDATE_USER = "radar/user/updateUser";
	// 获取发言人
	private final static String GEE_USER = "radar/user/getUser";
	// 根据环信Id获取发言人信息
	private final static String GEE_USER_BY_EMID = "radar/user/getUsersByMsgIdList";
	// 获取自己所发帖子的列表
	private final static String GET_MY_CREAT_POST = "radar/getMainPostsSentByUser";
	// 上传测试图片
	private final static String UPLOAD_FACIAL_PIC = "radar/uploadFacialPic";
	// 保存测试图片数据
	private final static String SAVE_FACIAL_PIC = "radar/saveFacialPic";
	// 获取最近版本
	private final static String GET_LATEST_VER = "AppFTP/getLatestVersion";
	// 用户收藏帖子为自己护肤方案
	private final static String STOREUP_POST_SOLUTION = "radar/storeupPostAsSolution";
	// 修改一个帖子是否为护肤方案
	private final static String CHANGE_POST_SOLUTION = "radar/changePostToSolution";
	// 选定为应用的护肤方案
	private final static String SELECTED_SOLUTION = "radar/selectedSolution";
	// 护肤方案评论列表
	private final static String SOLUTION_COMMENT_LIST = "radar/getSkinSolutionComments";
	// 针对护肤方案发表评论
	private final static String CREATE_SOLUTION_COMMENT = "radar/postSkinSolutionComment";
	// 删除护肤方案的评论
	private final static String DELETE_SOLUTION_COMMENT = "radar/deleteSkinSolutionComment";
	// 获取护肤方案及其评论
	private final static String GET_SOLUTION_COMMENT = "radar/getSkinSolutionAndComments";
	// 移帖
	private final static String MOVE_POST = "radar/movePost";
	// 置顶
	private final static String TOP_POST = "radar/topPost";
	// 获取单个话题
	private final static String GET_TOPIC = "radar/getTopic";
	// 上传预置话题信息
	private final static String CREATE_PRESET_TOPIC = "radar/createPresetTopic";
	// 返回预制话题列表
	private final static String GET_PRESET_TOPIC = "radar/getPresetTopicList";
	// 上传精选帖子图片
	private final static String UPLOAD_COLLECTION_IMG = "radar/uploadPostCollectionImg";
	// 更新精选帖子
	private final static String EDIT_POST_COLLECTION = "radar/editPostCollection";
	// 更新精选护肤方案
	private final static String EDIT_SOLUTION_COLLECTION = "radar/solution/editSelection";
	// 删除精选帖子
	private final static String DELETE_POST_COLLECTION = "radar/deletePostCollection";
	// 删除精选护肤方案
	private final static String DELETE_SOLUTION_COLLECTION = "radar/solution/deleteSelected";
	// 获取精选帖子列表
	private final static String GET_POST_COLLECTION = "radar/getPostCollectionList";
	// 上传banner图片
	private final static String UPLOAD_BANNER_IMG = "radar/uploadBannerImg";
	// 添加banner
	private final static String CREATE_BANNER = "radar/createBanner";
	// 删除banner
	private final static String DELETE_BANNER = "radar/deleteBanner";
	// banner顺序
	private final static String UPDATE_BANNER_PRIORITY = "radar/updateBannerPriority";
	// 获取banner列表
	private final static String GET_BANNER_LIST = "radar/getBannerList";
	// 用户最关注话题列表
	private final static String TOP_FAVOUR_TOPIC_LIST = "radar/topFavourTopicList";
	// 获取用户评论过的帖子以及原帖
	private final static String GET_COMMENTED_POST = "radar/getCommentedPost";
	// 授权(取消)其它用户管理员权限
	private final static String AUTHORIZE_USER_TOPIC_PERMISSION = "radar/authorizeUserTopicPermission";
	// 获取用户动态权限
	private final static String UPDATE_ROLES = "radar/user/updateRoles";
	// 从主页进入帖子时获得角色权限
	private final static String UPDATE_POST_ROLES = "radar/updatePostRoles";
	// 用户修改密码
	private final static String CHANGE_PASSWORD = "user/changePwd";
	// 忘记密码（手机找回-1）
	private final static String RETRIEVE_PWD_PHONE = "user/retrievePwdByPhone";
	// 重置密码（手机找回-2）
	private final static String RESET_PWD_PHONE = "user/resetPwdByPhone";
	// 忘记密码（邮箱找回-1）
	private final static String RETRIEVE_PWD_EMAIL = "user/retrievePwdByEmail";
	// 验证用户邮箱
	private final static String EMAIL_VERIFY = "user/emailVerify";
	// 用户绑定或修改手机号
	private final static String BIND_PHONENUM = "user/bindPhoneNo";
	// 用户绑定或修改邮箱
	private final static String BIND_EMAIL = "user/bindEmail";
	// 日新帖数目统计
	private final static String POST_COUNT = "radar/totalPostCount";
	// 日新话题数目
	private final static String TOPIC_COUNT = "radar/newTopicCount";
	// 新设备激活
	private final static String ACTIVATE_DEVICE = "radar/activatedDevice";
	// 日新设备激活数
	private final static String ACTIVATE_DEVICE_NUM = "radar/totalActivatedDeviceNum";
	// 日回复数目
	private final static String REPLY_COUNT = "radar/totalReplyCount";
	//他人关注列表
	private final static String USER_FOLLOW_LIST = "radar/user/getFollowList";
	//他人粉丝列表
	private final static String USER_FOLLOWED_LIST = "radar/user/getFollowedList";
	//他人发布的帖子
	private final static String USER_POST_CREATE = "radar/getPostSentByUser";
	//他人发布的话题
	private final static String USER_TOPIC_CREATE = "radar/getTopicSentByUser";
	//群发系统消息
	private final static String SEND_MSG_USER = "user/sendMsgtoUser";
	//护肤方案添加评论
	private final static String SOLUTION_CREATE_COMMENT = "radar/solution/createComment";
	//护肤方案修改评分
	private final static String SOLUTION_UPDATE_SCORE = "radar/solution/updateScore";
	//护肤方案获取评分
	private final static String SOLUTION_GET_SCORE = "radar/solution/getScore";
	//给护肤方案的评论点赞/取消点赞
	private final static String SOLUTION_LIKE_COMMENT = "radar/solution/likeComment";
	//删除护肤方案的评论
	private final static String SOLUTION_DELETE_COMMENT = "radar/solution/deleteComment";
	//上传护肤方案图片
	private final static String SOLUTION_UPLOAD_IMG = "radar/file/upload/images";
	//创建护肤方案
	private final static String SOLUTION_CREATE = "radar/solution/create";
	//修改护肤方案
	private final static String SOLUTION_UPDATE = "radar/solution/update";
	//删除护肤方案
	private final static String SOLUTION_DELETE = "radar/solution/delete";
	//收藏护肤方案
	private final static String SOLUTION_STOREUP = "radar/solution/storeup";
	//使用护肤方案
	private final static String SOLUTION_USE = "radar/solution/useSolution";
	//获取护肤方案详情
	private final static String SOLUTION_GET_DETAIL = "radar/solution/get";
	//获取正在使用的护肤方案
	private final static String SOLUTION_GET_INUSED = "radar/solution/getInUsed";
	//获取护肤方案列表（大全）
	private final static String SOLUTION_GET_LIST = "radar/solution/getList";
	//获取用户收藏的护肤方案列表
	private final static String SOLUTION_GET_STOREUP = "radar/solution/getStoreupList";
	//获取用户发布的护肤方案列表
	private final static String SOLUTION_GET_CREATEL = "radar/solution/getMySolutionList";
	//获取护肤方案评论列表
	private final static String SOLUTION_GET_COMMENT = "radar/solution/getCommentList";
	//获取护肤方案二级评论列表
	private final static String SOLUTION_GET_SECONDCOMMENT = "radar/solution/getSecondaryCommentList";
	//判断登录用户是否有关注的话题
	private final static String HAS_FOLLOW_TOPIC = "radar/hasFollowTopic";
	
	// 化妆品测试取得最后一条
	public final static String PRODUCT_GET_LAST = "product_last.action";
	// 更新测试数据上传状态
	public final static String SKIN_TEST_UPDATE_STATE = "skintest_update_state.action";
	// 保存测试数据列表
	public final static String PRODUCT_TEST_WIRTE_LISTS = "product_test_write_lists.action";
	// 根据条件查询测试数据
	public final static String QUERY_HISTORY_RECORD = "query_by_type_part.action";
	// 扫Analyze数据库，返回未上传数据
	public final static String SCANNING_ANALYZE = "scanning_analyze.action";

	// 插入本地user
	public final static String LOGIN_WIRTE_ONE = "login_wirte_one.action";
	// 获取第一条用户信息
	public final static String USER_GET_FIRST = "user_get_first.action";
	// 权限入库
	public final static String AUTH_WRITE = "auth_write.action";

	// 贴子发送前，存储至本地主页贴子列表
	public final static String POST_RELEASE_INSERT_ONE = "post_release_insert_one.action";
	// 更新贴子发送状态
	public final static String POST_RELEASE_UPDATE_STATE_ONE = "post_release_update_state_one.action";
	// 更新所有本地帖子发送状态
	public final static String POST_RELEASE_UPDATE_STATE_ALL = "post_release_update_state_all.action";
	// 图片发送成功，更新帖子内容
	public final static String POST_RELEASE_UPDATEIMG_ONE = "post_release_updateimg_one.action";
	// 贴子发送成功，更新主页贴子列表数据
	public final static String POST_RELEASE_UPDATE_ONE = "post_release_update_one.action";
	// 用户取消发送贴子，从主页贴子列表删除数据
	public final static String POST_RELEASE_DELETE_ONE = "post_release_delete_one.action";
	// 获取本地存储的主页贴子列表
	public final static String GET_MAIN_POST_LIST_LOCAL = "get_main_post_list_local.action";
	// 网络获取到新主页贴子后，更新本地主页贴子列表
	public final static String UPDATE_MAIN_POST_LIST_LOCAL = "update_main_post_list_local.action";
	// 删除所有服务器端主页贴子列表数据
	public final static String MAIN_POST_DELETE_REMOTE = "main_post_delete_remote.action";

	// 从贴发送前，存储一条待发从贴
	public final static String POST_DETAIL_INSERT_SENDING_ONE = "post_detail_insert_sending_one.action";
	// 存储一条主贴详情/更新一条主贴详情
	public final static String POST_DETAIL_INSERT_UPDATE_DETAIL_ONE = "post_detail_insert_update_detail_one.action";
	// 更新一条待发从贴状态
	public final static String POST_DETAIL_UPDATE_SENDING_STATE = "post_detail_update_sending_state.action";
	// 第一次开机更新所有待发从贴为初始状态
	public final static String POST_DETAIL_RESTORE_STATE_ALL = "post_detail_restore_state_all.action";
	// (图片发送成功)更新从贴内容
	public final static String POST_DETAIL_UPDATEIMG_ONE = "post_detail_updateimg_one.action";
	// 从贴发送成功，更新从贴内容为网络数据
	public final static String POST_DETAIL_UPDATE_SENDING_ONE = "post_detail_update_sending_one.action";
	// 删除一条待发从贴/删除一条主贴详情
	public final static String POST_DETAIL_DELETE_ONE = "post_detail_delete_one.action";
	// 获取一条主贴详情
	public final static String POST_DETAIL_GET_DETAIL_ONE = "post_detail_get_detail_one.action";
	// 更新一条主贴详情的Timestamp
	public final static String POST_DETAIL_UPDATE_TIMESTAMP = "post_detail_update_timestamp.action";

	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的贴子
	public final static String DELETE_ALL_LOCAL_SENDING_POST = "delete_all_local_sending_post.action";

	// 存储某种类型的帖子列表
	public final static String POST_LIST_SAVE_ONE_BYTYPE = "post_list_save_one_bytype.action";
	// 更新某种类型的帖子列表
	public final static String POST_LIST_UPDATE_ONE_BYTYPE = "post_list_update_one_bytype.action";
	// 获取某种类型的帖子列表
	public final static String POST_LIST_GET_ONE_BYTYPE = "post_list_get_one_bytype.action";
	// 删除某种类型的帖子列表
	public final static String POST_LIST_DELETE_ONE_BYTYPE = "post_list_delete_one_bytype.action";
	
	// 存储某种类型的护肤方案列表
	public final static String SOLUTION_LIST_SAVE_ONE_BYTYPE = "solution_list_save_one_bytype.action";
	// 更新某种类型的护肤方案列表
	public final static String SOLUTION_LIST_UPDATE_ONE_BYTYPE = "solution_list_update_one_bytype.action";
	// 获取某种类型的护肤方案列表
	public final static String SOLUTION_LIST_GET_ONE_BYTYPE = "solution_list_get_one_bytype.action";
	// 删除某种类型的护肤方案列表
	public final static String SOLUTION_LIST_DELETE_ONE_BYTYPE = "solution_list_delete_one_bytype.action";
	
	// 存储某种类型的护肤方案数据
	public final static String SOLUTION_DATA_SAVE_ONE_BYTYPE = "solution_data_save_one_bytype.action";
	// 更新某条护肤方案发送状态
	public final static String SOLUTION_STATE_ITEM_UPDATE_BYTYPE = "solution_state_item_update_bytype.action";
	// 更新某种类型的护肤方案发送状态
	public final static String SOLUTION_STATE_UPDATE_BYTYPE = "solution_state_update_bytype.action";
	// (图片发送成功)更新某条护肤方案内容
	public final static String SOLUTION_DATA_ITEM_UPDATE_BYTYPE = "solution_data_item_update_bytype.action";
	// 更新某种类型的护肤方案的Timestamp
	public final static String SOLUTION_TIMESTAMP_UPDATE_BYTYPE = "solution_timestamp_update_bytype.action";
	// 获取某种类型的护肤方案数据
	public final static String SOLUTION_DATA_GET_ONE_BYTYPE = "solution_data_get_one_bytype.action";
	// 删除某条护肤方案数据
	public final static String SOLUTION_DATA_DELETE_ONE_BYTYPE = "solution_data_delete_one_bytype.action";
	// 删除某种类型的护肤方案数据
	public final static String SOLUTION_DATA_DELETE_BYTYPE = "solution_data_delete_bytype.action";
	
	
	// 存储某种类型的话题列表
	public final static String TOPIC_LIST_SAVE_ONE_BYTYPE = "topic_list_save_one_bytype.action";
	// 更新某种类型的话题列表
	public final static String TOPIC_LIST_UPDATE_ONE_BYTYPE = "topic_list_update_one_bytype.action";
	// 获取某种类型的话题列表
	public final static String TOPIC_LIST_GET_ONE_BYTYPE = "topic_list_get_one_bytype.action";
	// 删除某种类型的话题列表
	public final static String TOPIC_LIST_DELETE_ONE_BYTYPE = "topic_list_delete_one_bytype.action";
	
	// 存储一条话题详情(本地只支持第一页)
	public final static String TOPIC_DETAIL_SAVE_ONE = "topic_detail_save_one.action";
	// 更新一条话题详情
	public final static String TOPIC_DETAIL_UPDATE_ONE = "topic_detail_update_one.action";
	// 获取一条话题详情(本地只支持第一页)
	public final static String TOPIC_DETAIL_GET_ONE = "topic_detail_get_one.action";
	// 删除一条话题详情
	public final static String TOPIC_DETAIL_DELETE_ONE = "topic_detail_delete_one.action";
	
	// 存储一条banner或精选帖
	public final static String BANNER_COLLECTION_SAVE_ONE = "banner_collection_save_one.action";
	// 更新一条banner或精选帖 时间戳
	public final static String BANNER_COLLECTION_UPDATE_TIME = "banner_collection_update_time.action";
	// 获取一条banner或精选帖
	public final static String BANNER_COLLECTION_GET_ONE = "banner_collection_get_one.action";
	// 删除一条banner或精选帖
	public final static String BANNER_COLLECTION_DELETE_ONE = "banner_collection_delete_one.action";
	
	// 存储用户关系列表
	public final static String USER_RELATION_SAVE_ONE = "user_relation_save_one.action";
	// 更新用户关系 时间戳
	public final static String USER_RELATION_UPDATE_TIME = "user_relation_update_time.action";
	// 获取用户关系列表
	public final static String USER_RELATION_GET_ONE = "user_relation_get_one.action";
	// 删除用户关系列表
	public final static String USER_RELATION_DELETE_ONE = "user_relation_delete_one.action";
	
	// private static User user;

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	/*
	 * public static User getUserInfo() { user = new User();
	 * DBFactory.buildManager(USER_GET_FIRST, null, new HttpCallback() {
	 * 
	 * @Override public void onServerMessage(String result) { user =
	 * GsonUtil.getGson().fromJson(result, User.class); } }); return user; }
	 */

	/**
	 * user注册url
	 */
	public static String getRegUrl(String json) {
		return OFFICIAL_USER_HOST_IP + USER_REG;
	}

	/**
	 * user登录url
	 */
	public static String getLoginUrl(String json) {
		return OFFICIAL_USER_HOST_IP + USER_LOGIN;
	}

	/**
	 * radar注册url
	 */
	public static String getRadarRegUrl(String json) {
		return OFFICIAL_RADAR_HOST_IP + RADAR_REG;
	}

	/**
	 * radar登录url
	 */
	public static String getRadarLoginUrl(String json) {
		return OFFICIAL_RADAR_HOST_IP + RADAR_LOGIN;
	}

	/**
	 * 日常测试url
	 */
	public static String getDailyTestSkinUrl(String json) {
		return OFFICIAL_RADAR_HOST_IP + SAVE_RESULT;
	}

	/**
	 * 化妆品测试url
	 */
	public static String getProductsTestUrl(String json) {
		return OFFICIAL_RADAR_HOST_IP + SAVE_MULTY_RESULTS;
	}

	/**
	 * 测试历史数据url
	 */
	public static String getHistoricalRecordsUrl(String json) {
		return OFFICIAL_RADAR_HOST_IP + QUERY_TEST_RECORDS;
	}

	/**
	 * 根据条件查询服务器上平均测试数据url
	 */
	public static String queryChartData(String json) {
		return OFFICIAL_RADAR_HOST_IP + QUERY_CHART_DATA;
	}
	
	/**
	 * 获取预置话题列表url
	 */
	public static String getFixedTopicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + FIXED_TOPIC_LIST;
	}

	/**
	 * 上传话题图片url
	 */
	public static String uploadTopicImg(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_TOPIC_IMG;
	}

	/**
	 * 创建话题url
	 */
	public static String createTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_TOPIC;
	}

	/**
	 * 更新话题url
	 */
	public static String updateTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_TOPIC;
	}

	/**
	 * 所有话题列表url
	 */
	public static String topicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + TOPICLIST;
	}

	/**
	 * 我的创建话题列表url
	 */
	public static String userTopicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_TOPICLIST;
	}

	/**
	 * 获取偏好内容列表url
	 */
	public static String userFollowTopicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_FAVOUR_CONTENTLIST;
	}

	/**
	 * 自动推荐话题列表url
	 */
	public static String getRecommendTopics(String json) {
		return OFFICIAL_RADAR_HOST_IP + RECOMMEND_TOPICLIST;
	}

	/**
	 * 根据测试结果推荐话题url
	 */
	public static String recommendTopicsTest(String json) {
		return OFFICIAL_RADAR_HOST_IP + RECOMMEND_TOPIC_TEST;
	}

	/**
	 * 根据测试结果推荐贴子url
	 */
	public static String recommendPostTest(String json) {
		return OFFICIAL_RADAR_HOST_IP + RECOMMEND_POST_TEST;
	}

	/**
	 * 上传帖子图片url
	 */
	public static String uploadPostImg(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_POST_IMG;
	}

	/**
	 * 创建帖子url
	 */
	public static String createPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_POST;
	}

	/**
	 * 更新帖子url
	 */
	public static String updatePost(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_POST;
	}

	/**
	 * 分页获取帖子列表url
	 */
	public static String postList(String json) {
		return OFFICIAL_RADAR_HOST_IP + POST_LIST;
	}

	/**
	 * 用户护肤方案列表url
	 */
	public static String getSkinSolutionList(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_SKIN_SOLUTION_LIST;
	}

	/**
	 * 根据部位显示护肤方案列表url
	 */
	public static String getSkinSolutionListByPart(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_SKIN_SOLUTION_LIST_PART;
	}

	/**
	 * 根据功效显示护肤方案列表url
	 */
	public static String getSkinSolutionListByEffect(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_SKIN_SOLUTION_LIST_EFFECT;
	}

	/**
	 * 护肤方案排行榜url
	 */
	public static String getSkinSolutionRank(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_SKIN_SOLUTION_RANK;
	}

	/**
	 * 查询返回话题列表url
	 */
	public static String searchReturnTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + SEARCH_RETURN_TOPIC;
	}

	/**
	 * 查询返回帖子列表url
	 */
	public static String searchReturnPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + SEARCH_RETURN_POST;
	}

	/**
	 * 主页显示用户关注话题的帖子列表列表url
	 */
	public static String getPostsOfFollowTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_POSTS_OF_FOLLOW_TOPIC;
	}

	/**
	 * 进入主贴，分页获取从帖列表url
	 */
	public static String readMainPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + READ_MAIN_POST;
	}

	/**
	 * 读取跟帖的回复url
	 */
	public static String readReply(String json) {
		return OFFICIAL_RADAR_HOST_IP + READ_REPLY;
	}

	/**
	 * 收藏帖子url
	 */
	public static String storeupPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + STOREUP_POST;
	}

	/**
	 * 删除帖子url
	 */
	public static String deletePost(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_POST;
	}

	/**
	 * 删除话题url
	 */
	public static String deleteTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_TOPIC;
	}

	/**
	 * 关注帖子url
	 */
	public static String followupPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + FOLLOWUP_POST;
	}

	/**
	 * 关注话题url
	 */
	public static String followupTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + FOLLOWUP_TOPIC;
	}

	/**
	 * 点赞帖子url
	 */
	public static String likePost(String json) {
		return OFFICIAL_RADAR_HOST_IP + LIKE_POST;
	}

	/**
	 * 反感帖子url
	 */
	public static String dislikePost(String json) {
		return OFFICIAL_RADAR_HOST_IP + DISLIKE_POST;
	}

	/**
	 * 新增帖子的浏览数目url
	 */
	public static String addPostViewCount(String json) {
		return OFFICIAL_RADAR_HOST_IP + ADD_POST_VIEW_COUNT;
	}

	/**
	 * 获取发言人关系列表url
	 */
	public static String getUserRelation(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_USER_RELATION;
	}

	/**
	 * （取消）关注用户url
	 */
	public static String followUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + FOLLOW_USER;
	}

	/**
	 * 创建用户话题分组url
	 */
	public static String createTopicGroups(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_TOPIC_GROUPS;
	}

	/**
	 * 更新用户话题分组url
	 */
	public static String updateTopicGroups(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_TOPIC_GROUPS;
	}

	/**
	 * 显示用户话题分组url
	 */
	public static String getTopicGroups(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_TOPIC_GROUPS;
	}

	/**
	 * 点击话题分组进入帖子列表url
	 */
	public static String getUserTopicGroupPostFlow(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_TOPIC_GROUPS_POST;
	}

	/**
	 * 上传用户头像url
	 */
	public static String uploadPortrait(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_PORTRAIN;
	}

	/**
	 * 更新用户url
	 */
	public static String updateUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_USER;
	}

	/**
	 * 获取用户url
	 */
	public static String getUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + GEE_USER;
	}

	/**
	 * 根据环信Id获取发言人信息url
	 */
	public static String getUsersByMsgIdList(String json) {
		return OFFICIAL_RADAR_HOST_IP + GEE_USER_BY_EMID;
	}
	
	/**
	 * 获取自己所发帖子的列表url
	 */
	public static String getMainPostsSentByUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_MY_CREAT_POST;
	}

	/**
	 * 上传测试图片url
	 */
	public static String uploadFacialPic(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_FACIAL_PIC;
	}

	/**
	 * 保存测试图片数据url
	 */
	public static String saveFacialPic(String json) {
		return OFFICIAL_RADAR_HOST_IP + SAVE_FACIAL_PIC;
	}

	/**
	 * 获取最近版本url
	 */
	public static String getLatestVersion(String json) {
		return FTP_GET_VER_IP + GET_LATEST_VER;
	}

	/**
	 * 用户收藏帖子为自己护肤方案url
	 */
	public static String storeupPostAsSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + STOREUP_POST_SOLUTION;
	}

	/**
	 * 修改一个帖子是否为护肤方案url
	 */
	public static String changePostToSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + CHANGE_POST_SOLUTION;
	}

	/**
	 * 选定为应用的护肤方案url
	 */
	public static String selectedSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SELECTED_SOLUTION;
	}

	/**
	 * 护肤方案评论列表url
	 */
	public static String getSkinSolutionComments(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_COMMENT_LIST;
	}

	/**
	 * 针对护肤方案发表评论url
	 */
	public static String postSkinSolutionComment(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_SOLUTION_COMMENT;
	}

	/**
	 * 删除护肤方案的评论url
	 */
	public static String deleteSkinSolutionComment(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_SOLUTION_COMMENT;
	}

	/**
	 * 获取护肤方案及其评论url
	 */
	public static String getSkinSolutionAndComments(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_SOLUTION_COMMENT;
	}

	/**
	 * 移帖url
	 */
	public static String movePost(String json) {
		return OFFICIAL_RADAR_HOST_IP + MOVE_POST;
	}

	/**
	 * 置顶url
	 */
	public static String topPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + TOP_POST;
	}

	/**
	 * 获取单个话题url
	 */
	public static String getTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_TOPIC;
	}

	/**
	 * 增加预置话题信息url
	 */
	public static String createPresetTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_PRESET_TOPIC;
	}

	/**
	 * 返回预制话题列表url
	 */
	public static String getPresetTopicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_PRESET_TOPIC;
	}

	/**
	 * 上传精选帖子图片url
	 */
	public static String uploadSelectedImg(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_COLLECTION_IMG;
	}

	/**
	 * 更新精选帖子url
	 */
	public static String editPostCollection(String json) {
		return OFFICIAL_RADAR_HOST_IP + EDIT_POST_COLLECTION;
	}
	
	/**
	 * 更新精选护肤方案url
	 */
	public static String editSolutionCollection(String json) {
		return OFFICIAL_RADAR_HOST_IP + EDIT_SOLUTION_COLLECTION;
	}

	/**
	 * 删除精选帖子url
	 */
	public static String deletePostCollection(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_POST_COLLECTION;
	}

	/**
	 * 删除精选护肤方案url
	 */
	public static String deleteSolutionCollection(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_SOLUTION_COLLECTION;
	}
	
	/**
	 * 获取精选帖子列表url
	 */
	public static String getPostCollectionList(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_POST_COLLECTION;
	}

	/**
	 * 上传banner图片url
	 */
	public static String uploadBannerImg(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPLOAD_BANNER_IMG;
	}

	/**
	 * 添加banner url
	 */
	public static String createBanner(String json) {
		return OFFICIAL_RADAR_HOST_IP + CREATE_BANNER;
	}

	/**
	 * 删除banner url
	 */
	public static String deleteBanner(String json) {
		return OFFICIAL_RADAR_HOST_IP + DELETE_BANNER;
	}

	/**
	 * banner顺序url
	 */
	public static String updateBannerPriority(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_BANNER_PRIORITY;
	}

	/**
	 * 获取banner列表url
	 */
	public static String getBannerList(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_BANNER_LIST;
	}

	/**
	 * 用户最关注话题列表url
	 */
	public static String topFavourTopicList(String json) {
		return OFFICIAL_RADAR_HOST_IP + TOP_FAVOUR_TOPIC_LIST;
	}

	/**
	 * 获取用户评论过的帖子以及原帖url
	 */
	public static String getCommentedPost(String json) {
		return OFFICIAL_RADAR_HOST_IP + GET_COMMENTED_POST;
	}

	/**
	 * 授权(取消)其它用户管理员权限url
	 */
	public static String authorizeUserTopicPermission(String json) {
		return OFFICIAL_RADAR_HOST_IP + AUTHORIZE_USER_TOPIC_PERMISSION;
	}

	/**
	 * 获取用户动态权限url
	 */
	public static String updateRoles(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_ROLES;
	}

	/**
	 * 从主页进入帖子时获得角色权限url
	 */
	public static String updatePostRoles(String json) {
		return OFFICIAL_RADAR_HOST_IP + UPDATE_POST_ROLES;
	}

	/**
	 * 用户修改密码url
	 */
	public static String changePwd(String json) {
		return OFFICIAL_USER_HOST_IP + CHANGE_PASSWORD;
	}

	/**
	 * 忘记密码（手机找回-1）url
	 */
	public static String retrievePwdByPhone(String json) {
		return OFFICIAL_USER_HOST_IP + RETRIEVE_PWD_PHONE;
	}

	/**
	 * 重置密码（手机找回-2）url
	 */
	public static String resetPwdByPhone(String json) {
		return OFFICIAL_USER_HOST_IP + RESET_PWD_PHONE;
	}

	/**
	 * 忘记密码（邮箱找回-1）url
	 */
	public static String retrievePwdByEmail(String json) {
		return OFFICIAL_USER_HOST_IP + RETRIEVE_PWD_EMAIL;
	}

	/**
	 * 验证用户邮箱url
	 */
	public static String emailVerify(String json) {
		return OFFICIAL_USER_HOST_IP + EMAIL_VERIFY;
	}

	/**
	 * 用户绑定或修改手机号url
	 */
	public static String bindPhoneNo(String json) {
		return OFFICIAL_USER_HOST_IP + BIND_PHONENUM;
	}

	/**
	 * 用户绑定或修改邮箱url
	 */
	public static String bindEmail(String json) {
		return OFFICIAL_USER_HOST_IP + BIND_EMAIL;
	}
	
	/**
	 * 日新帖数目统计url
	 */
	public static String totalPostCount(String json) {
		return OFFICIAL_RADAR_HOST_IP + POST_COUNT;
	}
	
	/**
	 * 日新话题数目url
	 */
	public static String newTopicCount(String json) {
		return OFFICIAL_RADAR_HOST_IP + TOPIC_COUNT;
	}
	
	/**
	 * 新设备激活url
	 */
	public static String activatedDevice(String json) {
		return OFFICIAL_RADAR_HOST_IP + ACTIVATE_DEVICE;
	}
	
	/**
	 * 日新设备激活数url
	 */
	public static String totalActivatedDeviceNum(String json) {
		return OFFICIAL_RADAR_HOST_IP + ACTIVATE_DEVICE_NUM;
	}
	
	/**
	 * 日回复数目url
	 */
	public static String totalReplyCount(String json) {
		return OFFICIAL_RADAR_HOST_IP + REPLY_COUNT;
	}
	
	/**
	 * 他人关注列表url
	 */
	public static String getFollowList(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_FOLLOW_LIST;
	}
	
	/**
	 * 他人粉丝列表url
	 */
	public static String getFollowedList(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_FOLLOWED_LIST;
	}
	
	/**
	 * 他人发布的帖子url
	 */
	public static String getPostSentByUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_POST_CREATE;
	}
	
	/**
	 * 他人发布的话题url
	 */
	public static String getTopicSentByUser(String json) {
		return OFFICIAL_RADAR_HOST_IP + USER_TOPIC_CREATE;
	}
	
	/**
	 * 群发系统消息url
	 */
	public static String sendMsgtoUser(String json) {
		return OFFICIAL_USER_HOST_IP + SEND_MSG_USER;
	}
	
	/**
	 * 护肤方案添加评论url
	 */
	public static String createComment(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_CREATE_COMMENT;
	}
	
	/**
	 * 护肤方案修改评分url
	 */
	public static String updateScore(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_UPDATE_SCORE;
	}
	
	/**
	 * 护肤方案获取评分url
	 */
	public static String getScore(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_SCORE;
	}
	
	/**
	 * 给评论点赞/取消点赞url
	 */
	public static String likeComment(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_LIKE_COMMENT;
	}
	
	/**
	 * 删除评论url
	 */
	public static String deleteComment(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_DELETE_COMMENT;
	}
	
	/**
	 * 上传护肤方案图片url
	 */
	public static String uploadImg(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_UPLOAD_IMG;
	}
	
	/**
	 * 创建护肤方案url
	 */
	public static String createSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_CREATE;
	}
	
	/**
	 * 修改护肤方案url
	 */
	public static String updateSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_UPDATE;
	}
	
	/**
	 * 删除护肤方案url
	 */
	public static String deleteSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_DELETE;
	}
	
	/**
	 * 收藏护肤方案url
	 */
	public static String storeupSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_STOREUP;
	}
	
	/**
	 * 使用护肤方案url
	 */
	public static String useSolution(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_USE;
	}
	
	/**
	 * 获取护肤方案详情url
	 */
	public static String solutionDetail(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_DETAIL;
	}
	
	/**
	 * 获取正在使用的护肤方案url
	 */
	public static String solutionInUsed(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_INUSED;
	}
	
	/**
	 * 获取护肤方案列表（大全）url
	 */
	public static String getSolitionList(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_LIST;
	}
	
	/**
	 * 获取用户收藏的护肤方案列表url
	 */
	public static String getSolStoreupList(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_STOREUP;
	}
	
	/**
	 * 获取用户发布的护肤方案列表url
	 */
	public static String getSolCreateList(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_CREATEL;
	}
	
	/**
	 * 获取护肤方案评论列表url
	 */
	public static String getSolCommentList(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_COMMENT;
	}
	
	/**
	 * 获取护肤方案二级评论列表url
	 */
	public static String get2ndSolCommentList(String json) {
		return OFFICIAL_RADAR_HOST_IP + SOLUTION_GET_SECONDCOMMENT;
	}
	
	/**
	 * 判断登录用户是否有关注的话题url
	 */
	public static String hasFollowTopic(String json) {
		return OFFICIAL_RADAR_HOST_IP + HAS_FOLLOW_TOPIC;
	}

}
