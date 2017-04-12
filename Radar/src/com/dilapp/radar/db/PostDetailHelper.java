package com.dilapp.radar.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.db.dao.impl.PostDetailDaoImpl;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.PostDetailsCallBack.DeleteLocalPostReq;
import com.dilapp.radar.domain.PostDetailsCallBack.MFollowPostResp;
import com.dilapp.radar.domain.PostDetailsCallBack.MPostDetailReq;
import com.dilapp.radar.domain.PostDetailsCallBack.MPostDetailResp;
import com.dilapp.radar.domain.PostDetailsCallBack.PostDetailRespLocal;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;
import com.dilapp.radar.domain.PostReleaseCallBack.UpdatePostSendingState;
import com.dilapp.radar.util.GsonUtil;


public class PostDetailHelper {

	// 存储一条待发从贴
	public static long saveSendingPostItem(String beanString) {
		PostReleaseReq bean = GsonUtil.getGson().fromJson(beanString, PostReleaseReq.class);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.savePostItem(PostReleaseCallBack.POST_RELEASE_SENDING, bean.getPostId(), bean.getLocalPostId(), bean.getLocalCreateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 存储一条主贴详情(本地只支持第一页)
	public static long savePostDetailItem(String beanString) {
		MPostDetailResp bean = GsonUtil.getGson().fromJson(beanString, MPostDetailResp.class);

		long postId = 0;
		long updateTime = 0;
		long repId = 0;
		for (int i = 0; i < bean.getResp().size(); i++) {
			MFollowPostResp resp = bean.getResp().get(i);
			if (resp.isMain()) {
				postId = resp.getId();
				updateTime= resp.getUpdateTime();
			}
		}
		//如果已经存储的话，先删除本地数据，避免重复存储
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		dbUtil.deletePostItem(postId, 0);
		
		long postIdFirst = 0;
		int postSize = 0;
		PostDetailDaoImpl dbUtil1 = new PostDetailDaoImpl(RadarApplication.getInstance());
		Cursor curDetail = dbUtil1.getPostDetailLists();
		if ((curDetail != null) && (curDetail.moveToFirst())) {
			postIdFirst = curDetail.getLong(1);
			postSize++;
			while (curDetail.moveToNext()) {
				postSize++;
			}
			
			curDetail.close();
		}
		dbUtil1.mDbclose();
		
		if (postSize >= 100) {
			PostDetailDaoImpl dbUtil2 = new PostDetailDaoImpl(RadarApplication.getInstance());
			dbUtil2.deletePostItem(postIdFirst, 0);
		}

		try {
			PostDetailDaoImpl dbUtil3 = new PostDetailDaoImpl(RadarApplication.getInstance());
			repId = dbUtil3.savePostItem(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS, postId, 0, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// (图片发送成功)更新一条待发从贴内容
	public static long updatePostImgContent(String beanString) {
		PostReleaseReq bean = GsonUtil.getGson().fromJson(beanString, PostReleaseReq.class);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostItem(PostReleaseCallBack.POST_RELEASE_SENDING, bean.getPostId(), bean.getLocalPostId(), bean.getLocalCreateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新一条主贴详情
	public static long updatePostDetailItem(String beanString) {
		MPostDetailResp bean = GsonUtil.getGson().fromJson(beanString, MPostDetailResp.class);
		long postId = 0;
		long updateTime = 0;
		for (int i = 0; i < bean.getResp().size(); i++) {
			MFollowPostResp resp = bean.getResp().get(i);
			if (resp.isMain()) {
				postId = resp.getId();
				updateTime= resp.getUpdateTime();
			}
		}
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostItem(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS, postId, 0, updateTime, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 从贴发送成功，更新从贴内容为网络数据
	public static long updateSendSuccessPostItem(String beanString) {
		MPostResp bean = GsonUtil.getGson().fromJson(beanString, MPostResp.class);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updateSendSuccessPostItem(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS, bean.getId(), bean.getLocalPostId(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新一条待发从贴状态
	public static long updateSendingPostState(String beanString) {
		UpdatePostSendingState bean = GsonUtil.getGson().fromJson(beanString, UpdatePostSendingState.class);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updateSendingPostStateItem(bean.getSendState(), bean.getLocalPostId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 第一次开机更新所有待发从贴为初始状态
	public static long restoreAllSendingPostsState(String beanString) {
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.restoreAllSendingPostsState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 删除一条待发从贴/删除一条主贴详情
	public static long deletePostItem(String beanString) {
		DeleteLocalPostReq bean = GsonUtil.getGson().fromJson(beanString, DeleteLocalPostReq.class);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deletePostItem(bean.getPostId(), bean.getLocalPostId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}

	// 删除所有待发从贴
	public static long deleteAllSendingPosts(String beanString) {
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteAllSendingPosts();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 获取一条主贴详情(本地只支持第一页)
	public static String getPostDetailItem(String beanString) {
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		MPostDetailReq bean = GsonUtil.getGson().fromJson(beanString, MPostDetailReq.class);
		MPostDetailResp detailResp = null;
		List<MFollowPostResp> listTotal = new ArrayList<MFollowPostResp>();
		PostDetailRespLocal resp = new PostDetailRespLocal();
		long updateTime = 0;
		
		Cursor curDetail = dbUtil.getPostDetailItem(bean.getPostId());
		if (curDetail != null && (curDetail.moveToFirst())) {
			detailResp = analyzeBeanGetDetail(bean.getPostId(), curDetail);
			updateTime = curDetail.getLong(curDetail.getColumnIndex("updateTime"));
			curDetail.close();
		}

		Cursor curSending = dbUtil.getAllSendingPosts();
		if ((curSending != null) && (curSending.moveToFirst())) {
			MFollowPostResp postItem = analyzeBeanGetSending(bean.getPostId(), bean.getPageNo(), curSending);
			if (postItem != null) {
				listTotal.add(postItem);
			}
			while (curSending.moveToNext()) {
				postItem = analyzeBeanGetSending(bean.getPostId(), bean.getPageNo(), curSending);
				if (postItem != null) {
					listTotal.add(postItem);
				}
			}
			curSending.close();
		}
		dbUtil.mDbclose();
		
		if (detailResp != null) {
			List<MFollowPostResp> listsDetail = detailResp.getResp();
			for (int k = 0; k < listsDetail.size(); k++) {
				MFollowPostResp listItem = (MFollowPostResp) listsDetail.get(k);
				listTotal.add(listItem);
			}
		}

		if (detailResp == null) {
			detailResp = new MPostDetailResp();
		}
		
		detailResp.setResp(listTotal);
		
		resp.setUpdateTime(updateTime);
		resp.setDetailResp(detailResp);
		return GsonUtil.getGson().toJson(resp);
	}

	// 更新一条主贴详情的Timestamp
	public static long updatePostDetailTimestamp(String beanString) {
		Long postId = Long.parseLong(beanString);
		PostDetailDaoImpl dbUtil = new PostDetailDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updatePostDetailTimestamp(postId, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	public static MFollowPostResp analyzeBeanGetSending(long postId, int pageNum, Cursor cur) {
		MFollowPostResp resp = null;
		
		long localPostId = cur.getLong(cur.getColumnIndex("localPostId"));
		String postItemJson = cur.getString(cur.getColumnIndex("postItemJson"));

		//如果localPostId不等于0,需转换
		if(localPostId != 0 && (pageNum == 1)) {
			PostReleaseReq bean = GsonUtil.getGson().fromJson(postItemJson, PostReleaseReq.class);
			if (bean.getParentId() == postId) {
				resp = new MFollowPostResp();
				
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
				resp.setUserId(SharePreCacheHelper.getUserID(RadarApplication.getInstance()));
				resp.setUserName(SharePreCacheHelper.getNickName(RadarApplication.getInstance()));
				resp.setLevel(SharePreCacheHelper.getLevel(RadarApplication.getInstance()));
				resp.setGender(SharePreCacheHelper.getGender(RadarApplication.getInstance()));
				resp.setUserHeadIcon(SharePreCacheHelper.getUserIconUrl(RadarApplication.getInstance()));
			}
		}
		
		return resp;
	}
	
	public static MPostDetailResp analyzeBeanGetDetail(long Id, Cursor cur) {
		MPostDetailResp resp = null;
		
		long postId = cur.getLong(cur.getColumnIndex("postId"));
		long localPostId = cur.getLong(cur.getColumnIndex("localPostId"));
		String postItemJson = cur.getString(cur.getColumnIndex("postItemJson"));

		if ((postId == Id) && (localPostId == 0)){
			resp = GsonUtil.getGson().fromJson(postItemJson, MPostDetailResp.class);
		}
		
		return resp;
	}
	
}
