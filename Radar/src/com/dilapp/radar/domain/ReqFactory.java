/*********************************************************************/
/*  文件名  ReqFactory.java    　                                       */
/*  程序名  ui/server工厂             						     				 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

import android.content.Context;

import com.dilapp.radar.domain.impl.*;

public class ReqFactory {

	@SuppressWarnings("unchecked")
	public static <T> T buildInterface(Context context, Class<T> clazz) {
		// 登录
		if (clazz == Login.class) {
			return (T) new LoginImpl(context);
		}
		// 注册
		if (clazz == Register.class) {
			return (T) new RegisterImpl(context);
		}
		// 日常/肤质
		if (clazz == DailyTestSkin.class) {
			return (T) new DailyTestSkinImpl(context);
		}
		// 测试历史记录
		if (clazz == HistoricalRecords.class) {
			return (T) new HistoricalRecordsImpl(context);
		}
		// 护肤品
		if (clazz == ProductsTestSkin.class) {
			return (T) new ProductsTestSkinImpl(context);
		}
		// 话题列表
		if (clazz == TopicListCallBack.class) {
			return (T) new TopicListCallBackImpl(context);
		}
		// 发布话题
		if (clazz == CreateTopic.class) {
			return (T) new CreateTopicImpl(context);
		}
		// 发布帖子
		if (clazz == PostReleaseCallBack.class) {
			return (T) new PostReleaseCallBackImpl(context);
		}
		// 用户话题列表
		if (clazz == MyTopicCallBack.class) {
			return (T) new MyTopicCallBackImpl(context);
		}
		// 所有话题列表
		if (clazz == FoundAllTopic.class) {
			return (T) new FoundAllTopicImpl(context);
		}
		// 分页获取帖子列表
		if (clazz == GetPostList.class) {
			return (T) new GetPostListImpl(context);
		}
		// 进入主贴，分页获取从帖列表
		if (clazz == PostDetailsCallBack.class) {
			return (T) new PostDetailsCallBackImpl(context);
		}
		// 精选帖子相关
		if (clazz == PostCollection.class) {
			return (T) new PostCollectionImpl(context);
		}
		// 用户删除帖子话题
		if (clazz == DeletePostTopic.class) {
			return (T) new DeletePostTopicImpl(context);
		}
		// 用户关注帖子话题
		if (clazz == FollowupPostTopic.class) {
			return (T) new FollowupPostTopicImpl(context);
		}
		// 新增帖子浏览数目
		if (clazz == AddPostViewCount.class) {
			return (T) new AddPostViewCountImpl(context);
		}
		// 扫本地测试库，上传数据
		if (clazz == Scanning.class) {
			return (T) new ScanningImpl(context);
		}
		// 护肤方案列表相关
		if (clazz == SolutionList.class) {
			return (T) new SolutionListImpl(context);
		}
		// 查询返回话题/帖子列表
		if (clazz == SearchCallBack.class) {
			return (T) new SearchCallBackImpl(context);
		}
		// 用户点赞/反感帖子
		if (clazz == LikeDislikePost.class) {
			return (T) new LikeDislikePostImpl(context);
		}
		// 获取发言人关系列表
		if (clazz == GetUserRelation.class) {
			return (T) new GetUserRelationImpl(context);
		}
		// （取消）关注用户
		if (clazz == FollowUser.class) {
			return (T) new FollowUserImpl(context);
		}
		// 更新获取发言人
		if (clazz == UpdateGetUser.class) {
			return (T) new UpdateGetUserImpl(context);
		}
		// 获取自己发布/收藏的帖子列表
		if (clazz == MyPostList.class) {
			return (T) new MyPostListImpl(context);
		}
		// 保存测试图片数据
		if (clazz == SaveTestPic.class) {
			return (T) new SaveTestPicImpl(context);
		}
		// 更新版本
		if (clazz == UpdateVersion.class) {
			return (T) new UpdateVersionImpl(context);
		}
		// 护肤方案收藏应用相关
		if (clazz == SolutionCollectApply.class) {
			return (T) new SolutionCollectApplyImpl(context);
		}
		// 护肤方案的评论相关
		if (clazz == SolutionComment.class) {
			return (T) new SolutionCommentImpl(context);
		}
		// 获取护肤方案及其评论
		if (clazz == SolutionDetails.class) {
			return (T) new SolutionDetailsImpl(context);
		}
		// 移帖/置顶
		if (clazz == PostOperation.class) {
			return (T) new PostOperationImpl(context);
		}
		// 预置话题相关
		if (clazz == PresetTopic.class) {
			return (T) new PresetTopicImpl(context);
		}
		// Banner相关
		if (clazz == Banner.class) {
			return (T) new BannerImpl(context);
		}
		// 话题分组相关
		if (clazz == TopicGroups.class) {
			return (T) new TopicGroupsImpl(context);
		}
		// 权限相关
		if (clazz == AuthorizeRoles.class) {
			return (T) new AuthorizeRolesImpl(context);
		}
		// 密码相关
		if (clazz == PasswordManage.class) {
			return (T) new PasswordManageImpl(context);
		}
		// 电话email绑定相关
		if (clazz == PhoneEmailManage.class) {
			return (T) new PhoneEmailManageImpl(context);
		}
		// 服务器统计的数据信息相关
		if (clazz == StatisticalInfo.class) {
			return (T) new StatisticalInfoImpl(context);
		}
		// 获取他人所发帖子和话题
		if (clazz == UserPostTopicList.class) {
			return (T) new UserPostTopicListImpl(context);
		}
		// 发送系统消息相关
		if (clazz == SendMessage.class) {
			return (T) new SendMessageImpl(context);
		}
		// 护肤方案评论和评分相关
		if (clazz == SolutionCommentScore.class) {
			return (T) new SolutionCommentScoreImpl(context);
		}
		// 护肤方案发布和修改相关
		if (clazz == SolutionCreateUpdate.class) {
			//return (T) new SolutionCreateUpdateAsyncImpl(context);
			return (T) new SolutionCreateUpdateImpl(context);
		}
		// 护肤方案操作相关
		if (clazz == SolutionOperate.class) {
			return (T) new SolutionOperateImpl(context);
		}
		// 护肤方案详情相关
		if (clazz == SolutionDetailData.class) {
			return (T) new SolutionDetailDataImpl(context);
		}
		// 护肤方案及其评论列表相关
		if (clazz == SolutionListData.class) {
			return (T) new SolutionListDataImpl(context);
		}
		return null;
	}
}
