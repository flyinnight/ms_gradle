package com.dilapp.radar.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.db.dao.impl.MainPostDaoImpl;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.MainPostListSave;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;
import com.dilapp.radar.domain.PostReleaseCallBack.UpdatePostSendingState;
import com.dilapp.radar.util.GsonUtil;


public class MainPostHelper {

	// 贴子发送前，存储至本地主页贴子列表
	public static long savePostReleaseItem(String beanString) {
		PostReleaseReq bean = GsonUtil.getGson().fromJson(beanString, PostReleaseReq.class);
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.savePostItem(PostReleaseCallBack.POST_RELEASE_SENDING, bean.getLocalPostId(), bean.getLocalCreateTime(), bean.getLocalCreateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新贴子发送状态
	public static long updatePostSendState(String beanString) {
		UpdatePostSendingState bean = GsonUtil.getGson().fromJson(beanString, UpdatePostSendingState.class);
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostStateItem(bean.getSendState(), bean.getLocalPostId(), bean.getLocalCreateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 更新所有本地帖子发送状态
	public static long updateAllLocalPostSendState(String beanString) {
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updateAllLOcalPostState(PostReleaseCallBack.POST_RELEASE_SENDFAILED, 0, 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 图片发送成功，更新帖子内容
	public static long updatePostImgContent(String beanString) {
		PostReleaseReq bean = GsonUtil.getGson().fromJson(beanString, PostReleaseReq.class);
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostImgItem(PostReleaseCallBack.POST_RELEASE_SENDING, bean.getLocalPostId(), bean.getLocalCreateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 贴子发送成功，更新主页贴子列表数据为网络返回数据
	public static long updatePostReleaseItem(String beanString) {
		MPostResp bean = GsonUtil.getGson().fromJson(beanString, MPostResp.class);
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostItem(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS, bean.getLocalPostId(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 用户取消发送贴子，从主页贴子列表删除数据
	public static boolean deletePostReleaseItem(String beanString) {
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		
		long localPostId = Long.parseLong(beanString);
		return dbUtil.deletePostItemById(localPostId);
	}

	// 除所有服务器端主页贴子列表数据
	public static boolean deleteMainPostRemote(String beanString) {
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		
		return dbUtil.deletePostItemById(0);
	}
	
	// 获取本地存储的主页贴子列表
	public static String getAllMainPostLists(String beanString) {
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		Cursor cur = dbUtil.getAllMainPosts();
		long updateTime = 0;
		int pageNum = Integer.parseInt(beanString);
		
		List<MPostResp> postLists = new ArrayList<MPostResp>();
		if (cur.moveToFirst()) {
			MPostResp postItem = analyzeBeanGetLocal(pageNum, cur);
			if (postItem != null) {
				postLists.add(postItem);
			}
			while (cur.moveToNext()) {
				postItem = analyzeBeanGetLocal(pageNum, cur);
				if (postItem != null) {
					postLists.add(postItem);
				}
			}
		}
		
		if (cur.moveToFirst()) {
			MPostResp postItem = analyzeBeanGetRemote(pageNum, cur);
			if (postItem != null) {
				postLists.add(postItem);
			}
			while (cur.moveToNext()) {
				postItem = analyzeBeanGetRemote(pageNum, cur);
				if (postItem != null) {
					postLists.add(postItem);
				}
			}
		}
		
		if (cur.moveToFirst()) {
			updateTime = getListUpdateTime(cur);
			if (updateTime == 0) {
				while (cur.moveToNext()) {
					updateTime = getListUpdateTime(cur);
					if (updateTime != 0) {
						break;
					}
				}
			}
		}
		
		cur.close();
		dbUtil.mDbclose();
		MainPostListSave beanSave = new MainPostListSave();
		beanSave.setUpdateTime(updateTime);
		beanSave.setPostLists(postLists);
		return GsonUtil.getGson().toJson(beanSave);
	}

	// 网络获取到新主页贴子后，更新本地主页贴子列表
	public static String updateMainPostLists(String content) {
		MainPostListSave bean = GsonUtil.getGson().fromJson(content, MainPostListSave.class);
		//先删除原来存储的服务器上posts
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		dbUtil.deletePostItemById(0);
		//存储新的服务器上posts
		
		List<MPostResp> resList = bean.getPostLists();
		for(int i = 0; i < resList.size(); i++) {
			MainPostDaoImpl dbUtil1 = new MainPostDaoImpl(RadarApplication.getInstance());
			dbUtil1.savePostItem(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS, resList.get(i).getLocalPostId(), resList.get(i).getUpdateTime(), bean.getUpdateTime(), GsonUtil.getGson().toJson(resList.get(i)));
		}
		
		BaseResp res = new BaseResp();
		res.setStatus("SUCCESS");
		return GsonUtil.getGson().toJson(res);
	}
	
	//退出登录等操作后，删除所有本地待发贴子
	public static long deleteAllLocalPosts(String beanString) {
		MainPostDaoImpl dbUtil = new MainPostDaoImpl(RadarApplication.getInstance());
		
		return dbUtil.deleteLocalPosts(0);
	}

	public static MPostResp analyzeBeanGetLocal(int pageNum, Cursor cur) {
		MPostResp resp = null;
		
		long localPostId = cur.getLong(cur.getColumnIndex("localPostId"));
		String postItemJson = cur.getString(cur.getColumnIndex("postItemJson"));

		//如果localPostId不等于0,需转换
		if(localPostId != 0 && (pageNum == 1)) {
			resp = new MPostResp();
			PostReleaseReq bean = GsonUtil.getGson().fromJson(postItemJson, PostReleaseReq.class);
				
			resp.setId(bean.getPostId());
			resp.setLocalPostId(bean.getLocalPostId());
			resp.setSendState(cur.getInt(cur.getColumnIndex("sendState")));
			resp.setPid(bean.getParentId());
			resp.setTopicId(bean.getTopicId());
			resp.setTopicTitle(bean.getTopicTitle());
			resp.setPostLevel(bean.getPostLevel());
			resp.setToUserId(bean.getToUserId());
			resp.setPostTitle(bean.getPostTitle());
			resp.setPostContent(bean.getPostContent());
			resp.setSelectedToSolution(bean.getSelectedToSolution());
			resp.setEffect(bean.getEffect());
			resp.setPart(bean.getPart());
			resp.setThumbURL(bean.getThumbURL());
			resp.setUpdateTime(bean.getLocalCreateTime());
			resp.setSkinQuality(bean.getSkin());
			//resp.setPageNo(1);
			//resp.setTotalPage(1);
			resp.setUserId(SharePreCacheHelper.getUserID(RadarApplication.getInstance()));
			resp.setUserName(SharePreCacheHelper.getNickName(RadarApplication.getInstance()));
			resp.setLevel(SharePreCacheHelper.getLevel(RadarApplication.getInstance()));
			resp.setGender(SharePreCacheHelper.getGender(RadarApplication.getInstance()));
			resp.setUserHeadIcon(SharePreCacheHelper.getUserIconUrl(RadarApplication.getInstance()));
		}
		
		return resp;
	}
	
	public static MPostResp analyzeBeanGetRemote(int pageNum, Cursor cur) {
		MPostResp resp = null;
		
		long localPostId = cur.getLong(cur.getColumnIndex("localPostId"));
		String postItemJson = cur.getString(cur.getColumnIndex("postItemJson"));

		if (localPostId == 0){
			resp = GsonUtil.getGson().fromJson(postItemJson, MPostResp.class);
			resp.setSendState(cur.getInt(cur.getColumnIndex("sendState")));
			resp.setLocalPostId(localPostId);
		}
		
		return resp;
	}
	
	public static long getListUpdateTime(Cursor cur) {
		long localPostId = cur.getLong(cur.getColumnIndex("localPostId"));
		long updateTime = 0;

		if (localPostId == 0){
			updateTime = cur.getLong(cur.getColumnIndex("listUpdateTime"));
		}
		
		return updateTime;
	}
}
