package com.dilapp.radar.db;

import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.SolutionList;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.server.HttpCallback;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.StringUtils;

public class DBFactory {

	public static Object buildManager(String action, String content, HttpCallback call, final int callBackId) {
		if (HttpConstant.getLoginUrl(null).equals(action)) {
			AccountHelper.saveToken(content);
		}
		// 登录radar系统，更新用户信息
		if (HttpConstant.LOGIN_WIRTE_ONE.equals(action)) {
			boolean suc = AccountHelper.updateUser(content);
			if (suc == true) {
			}
		}
		// 取得用户信息第一条
		if (HttpConstant.USER_GET_FIRST.equals(action)) {
			String userString = AccountHelper.getFirst();
			if (!StringUtils.isEmpty(userString)) {
				call.onServerMessage(userString, callBackId);
			}
		}
		// 登录时权限数据入库
		if (HttpConstant.AUTH_WRITE.equals(action)) {
			AuthHelper.saveAuthForList(content);
		}
		
		//更新测试数据是否已经上传至服务器状态
		if (HttpConstant.SKIN_TEST_UPDATE_STATE.equals(action)) {
			AnalyzeHelper.updateRecordState(content);
		}
		if (HttpConstant.PRODUCT_TEST_WIRTE_LISTS.equals(action)) {
			AnalyzeHelper.saveAnalyzeForList(content);
		}
		if (HttpConstant.PRODUCT_GET_LAST.equals(action)) {
			String lastOne = AnalyzeHelper.getLastType(content);
			call.onServerMessage(lastOne, callBackId);
		}
		if (HttpConstant.QUERY_HISTORY_RECORD.equals(action)) {
			String queyRecord = AnalyzeHelper.queryHistoryRecord(content);
			call.onServerMessage(queyRecord, callBackId);
		}
		// 扫库，返回未上传
		if (HttpConstant.SCANNING_ANALYZE.equals(action)) {
			String scanningAnalyze = AnalyzeHelper.fetchAllStupidAnalyzeData();
			call.onServerMessage(scanningAnalyze, callBackId);
		}
		
		// 贴子发送前，存储至本地主页贴子列表
		if (HttpConstant.POST_RELEASE_INSERT_ONE.equals(action)) {
			MainPostHelper.savePostReleaseItem(content);
		}
		// 更新贴子发送状态
		if (HttpConstant.POST_RELEASE_UPDATE_STATE_ONE.equals(action)) {
			MainPostHelper.updatePostSendState(content);
		}
		// 更新所有本地帖子发送状态
		if (HttpConstant.POST_RELEASE_UPDATE_STATE_ALL.equals(action)) {
			MainPostHelper.updateAllLocalPostSendState(content);
		}
		// 图片发送成功，更新帖子内容
		if (HttpConstant.POST_RELEASE_UPDATEIMG_ONE.equals(action)) {
			MainPostHelper.updatePostImgContent(content);
		}
		// 贴子发送成功，更新主页贴子列表数据为网络返回数据
		if (HttpConstant.POST_RELEASE_UPDATE_ONE.equals(action)) {
			MainPostHelper.updatePostReleaseItem(content);
		}
		// 用户取消发送贴子，从主页贴子列表删除数据
		if (HttpConstant.POST_RELEASE_DELETE_ONE.equals(action)) {
			MainPostHelper.deletePostReleaseItem(content);
		}
		// 删除所有服务器端主页贴子列表数据
		if (HttpConstant.MAIN_POST_DELETE_REMOTE.equals(action)) {
			MainPostHelper.deleteMainPostRemote(content);
		}
		// 获取本地存储的主页贴子列表
		if (HttpConstant.GET_MAIN_POST_LIST_LOCAL.equals(action)) {
			String postLists = MainPostHelper.getAllMainPostLists(content);
			call.onServerMessage(postLists, callBackId);
		}
		// 网络获取到新主页贴子后，更新本地主页贴子列表
		if (HttpConstant.UPDATE_MAIN_POST_LIST_LOCAL.equals(action)) {
			String updateResult = MainPostHelper.updateMainPostLists(content);
			call.onServerMessage(updateResult, callBackId);
		}
		
		// 从贴发送前，存储一条待发从贴
		if (HttpConstant.POST_DETAIL_INSERT_SENDING_ONE.equals(action)) {
			PostDetailHelper.saveSendingPostItem(content);
		}
		// 存储一条主贴详情/更新一条主贴详情
		if (HttpConstant.POST_DETAIL_INSERT_UPDATE_DETAIL_ONE.equals(action)) {
			PostDetailHelper.savePostDetailItem(content);
		}
		// 更新一条待发从贴状态
		if (HttpConstant.POST_DETAIL_UPDATE_SENDING_STATE.equals(action)) {
			PostDetailHelper.updateSendingPostState(content);
		}
		// 第一次开机更新所有待发从贴为初始状态
		if (HttpConstant.POST_DETAIL_RESTORE_STATE_ALL.equals(action)) {
			PostDetailHelper.restoreAllSendingPostsState(content);
		}
		// (图片发送成功)更新从贴内容
		if (HttpConstant.POST_DETAIL_UPDATEIMG_ONE.equals(action)) {
			PostDetailHelper.updatePostImgContent(content);
		}
		// 从贴发送成功，更新从贴内容为网络数据
		if (HttpConstant.POST_DETAIL_UPDATE_SENDING_ONE.equals(action)) {
			PostDetailHelper.updateSendSuccessPostItem(content);
		}
		// 删除一条待发从贴/删除一条主贴详情
		if (HttpConstant.POST_DETAIL_DELETE_ONE.equals(action)) {
			PostDetailHelper.deletePostItem(content);
		}
		// 获取一条主贴详情
		if (HttpConstant.POST_DETAIL_GET_DETAIL_ONE.equals(action)) {
			String updateResult = PostDetailHelper.getPostDetailItem(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 更新一条主贴详情的Timestamp
		if (HttpConstant.POST_DETAIL_UPDATE_TIMESTAMP.equals(action)) {
			PostDetailHelper.updatePostDetailTimestamp(content);
		}
		
		// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的贴子
		// 同时删除其他和用户相关的列表信息
		if (HttpConstant.DELETE_ALL_LOCAL_SENDING_POST.equals(action)) {
			MainPostHelper.deleteAllLocalPosts(content);
			PostDetailHelper.deleteAllSendingPosts(content);
			
			PostListHelper.deletePostList(Integer.toString(GetPostList.POST_LIST_BY_SEND));
			PostListHelper.deletePostList(Integer.toString(GetPostList.POST_LIST_BY_STORE));
			PostListHelper.deletePostList(Integer.toString(GetPostList.POST_LIST_BY_COMMENT));
			PostListHelper.deletePostList(Integer.toString(GetPostList.POST_LIST_BY_COMMENT_PARENT));
			SolutionListHelper.deleteSolutionList(Integer.toString(SolutionList.SOLUTION_LIST_USER_STORE));
			TopicListHelper.deleteTopicList(Integer.toString(TopicListCallBack.TOPIC_LIST_BY_SEND));
			TopicListHelper.deleteTopicList(Integer.toString(TopicListCallBack.TOPIC_LIST_BY_FOLLOW));
		}
		
		// 存储某种类型的帖子列表
		if (HttpConstant.POST_LIST_SAVE_ONE_BYTYPE.equals(action)) {
			PostListHelper.savePostList(content);
		}
		// 更新某种类型的帖子列表
		if (HttpConstant.POST_LIST_UPDATE_ONE_BYTYPE.equals(action)) {
			PostListHelper.updatePostList(content);
		}
		// 获取某种类型的帖子列表
		if (HttpConstant.POST_LIST_GET_ONE_BYTYPE.equals(action)) {
			String updateResult = PostListHelper.getPostList(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除某种类型的帖子列表
		if (HttpConstant.POST_LIST_DELETE_ONE_BYTYPE.equals(action)) {
			PostListHelper.deletePostList(content);
		}
		
		// 存储某种类型的护肤方案列表
		if (HttpConstant.SOLUTION_LIST_SAVE_ONE_BYTYPE.equals(action)) {
			SolutionListHelper.saveSolutionList(content);
		}
		// 更新某种类型的护肤方案列表
		if (HttpConstant.SOLUTION_LIST_UPDATE_ONE_BYTYPE.equals(action)) {
			SolutionListHelper.updateSolutionList(content);
		}
		// 获取某种类型的护肤方案列表
		if (HttpConstant.SOLUTION_LIST_GET_ONE_BYTYPE.equals(action)) {
			String updateResult = SolutionListHelper.getSolutionList(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除某种类型的护肤方案列表
		if (HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE.equals(action)) {
			SolutionListHelper.deleteSolutionList(content);
		}
		
		// 存储某种类型的护肤方案data
		if (HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE.equals(action)) {
			String updateResult = SolutionDataHelper.saveSolutionData(content);
			call.onServerMessage(updateResult, callBackId);
		}
		//更新某条护肤方案发送状态
		if (HttpConstant.SOLUTION_STATE_ITEM_UPDATE_BYTYPE.equals(action)) {
			SolutionDataHelper.updateSolutionStateItemByType(content);
		}
		//更新某种类型的护肤方案发送状态
		if (HttpConstant.SOLUTION_STATE_UPDATE_BYTYPE.equals(action)) {
			SolutionDataHelper.updateSolutionStateByType(content);
		}
		//(图片发送成功)更新某条护肤方案内容
		if (HttpConstant.SOLUTION_DATA_ITEM_UPDATE_BYTYPE.equals(action)) {
			SolutionDataHelper.updateSolutionDataItemByType(content);
		}
		//更新某种类型的护肤方案发送状态
		if (HttpConstant.SOLUTION_TIMESTAMP_UPDATE_BYTYPE.equals(action)) {
			SolutionDataHelper.updateSolutionTimestampByType(content);
		}
		// 获取某种类型的护肤方案data
		if (HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE.equals(action)) {
			String updateResult = SolutionDataHelper.getSolutionData(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除某条护肤方案data
		if (HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE.equals(action)) {
			SolutionDataHelper.deleteSolutionDataItem(content);
		}
		// 删除某种类型的护肤方案data
		if (HttpConstant.SOLUTION_DATA_DELETE_BYTYPE.equals(action)) {
			SolutionDataHelper.deleteSolutionData(content);
		}
		
		// 存储某种类型的话题列表
		if (HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE.equals(action)) {
			TopicListHelper.saveTopicList(content);
		}
		// 更新某种类型的话题列表
		if (HttpConstant.TOPIC_LIST_UPDATE_ONE_BYTYPE.equals(action)) {
			TopicListHelper.updateTopicList(content);
		}
		// 获取某种类型的话题列表
		if (HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE.equals(action)) {
			String updateResult = TopicListHelper.getTopicList(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除某种类型的话题列表
		if (HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE.equals(action)) {
			TopicListHelper.deleteTopicList(content);
		}
		
		// 存储一条话题详情(本地只支持第一页)
		if (HttpConstant.TOPIC_DETAIL_SAVE_ONE.equals(action)) {
			TopicDetailHelper.saveTopicDetailItem(content);
		}
		// 更新一条话题详情
		if (HttpConstant.TOPIC_DETAIL_UPDATE_ONE.equals(action)) {
			TopicDetailHelper.updateTopicDetailItem(content);
		}
		// 获取一条话题详情(本地只支持第一页)
		if (HttpConstant.TOPIC_DETAIL_GET_ONE.equals(action)) {
			String updateResult = TopicDetailHelper.getTopicDetailItem(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除一条话题详情
		if (HttpConstant.TOPIC_DETAIL_DELETE_ONE.equals(action)) {
			TopicDetailHelper.deleteTopicDetailItem(content);
		}
		
		// 存储一条banner或精选帖
		if (HttpConstant.BANNER_COLLECTION_SAVE_ONE.equals(action)) {
			BannerCollectionHelper.saveBannerCollectionItem(content);
		}
		// 更新一条banner或精选帖 时间戳
		if (HttpConstant.BANNER_COLLECTION_UPDATE_TIME.equals(action)) {
			BannerCollectionHelper.updateBannerCollectionTime(content);
		}
		// 获取一条banner或精选帖
		if (HttpConstant.BANNER_COLLECTION_GET_ONE.equals(action)) {
			String updateResult = BannerCollectionHelper.getBannerCollectionItem(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除一条banner或精选帖
		if (HttpConstant.BANNER_COLLECTION_DELETE_ONE.equals(action)) {
			BannerCollectionHelper.deleteBannerCollectionItem(content);
		}
		
		// 存储用户关系列表
		if (HttpConstant.USER_RELATION_SAVE_ONE.equals(action)) {
			UserRelationHelper.saveUserRelationItem(content);
		}
		// 更新用户关系 时间戳
		if (HttpConstant.USER_RELATION_UPDATE_TIME.equals(action)) {
			UserRelationHelper.updateUserRelationTime(content);
		}
		// 获取用户关系列表
		if (HttpConstant.USER_RELATION_GET_ONE.equals(action)) {
			String updateResult = UserRelationHelper.getUserRelationItem(content);
			call.onServerMessage(updateResult, callBackId);
		}
		// 删除用户关系列表
		if (HttpConstant.USER_RELATION_DELETE_ONE.equals(action)) {
			UserRelationHelper.deleteUserRelationItem(content);
		}
		
		return null;
	}
}
