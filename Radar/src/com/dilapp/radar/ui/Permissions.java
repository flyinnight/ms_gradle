package com.dilapp.radar.ui;

import android.content.Context;
import android.text.TextUtils;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.domain.GetPostList.*;
import com.dilapp.radar.domain.TopicListCallBack.*;

import java.io.Serializable;

/**
 * Created by husj1 on 2015/7/20.
 */
public class Permissions {

    /**
     * 可否发帖
     * @param context
     * @param topicId
     * @return
     */
    public static boolean canPostRelease(Context context, long topicId) {

    	String topicForbidden = SharePreCacheHelper.getTopicForbiddenList(context);
    	String sTopicId = Long.toString(topicId);
    	int i;
    	
    	if (topicForbidden != null) {
    		String[] topicForbiddenList = topicForbidden.split(",");
        	for (i=0; i<topicForbiddenList.length; i++) {
        		if (sTopicId.equals(topicForbiddenList[i])) {
        			return false;
        		}
        	}
    	}
    	
        return true;
    }

    /**
     * 可否修改帖子
     * @param context
     * @param post
     * @return
     */
    public static boolean canPostModify(Context context, MPostResp post) {
    	
		String role = SharePreCacheHelper.getUserRole(context);
		String userId = SharePreCacheHelper.getUserID(context);
		
		if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}

    	if(post != null && post.getUserId() != null && post.getUserId().equals(userId)) {
    		return true;
    	}
    	
    	return false;
    }


    /**
     * 可否删除帖子
     * @param context
     * @param post
     * @return
     */
    public static boolean canPostDelete(Context context, MPostResp post) {
    	
		String role = SharePreCacheHelper.getUserRole(context);
		String userId = SharePreCacheHelper.getUserID(context);
    	String topicOwner = SharePreCacheHelper.getTopicOwnerList(context);
    	String topicAdmin = SharePreCacheHelper.getTopicAdminList(context);
		String sTopicId = Long.toString(post.getTopicId());
		int i;
		
		if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}

    	if(post != null && post.getUserId() != null && post.getUserId().equals(userId)) {
    		return true;
    	}
    	
    	if (topicOwner != null) {
    		String[] topicOwnerList = topicOwner.split(",");
    		for (i=0; i<topicOwnerList.length; i++) {
    			if (sTopicId.equals(topicOwnerList[i])) {
    				return true;
    			}
    		}
    	}
    	
    	if (topicAdmin != null) {
    		String[] topicAdminList = topicAdmin.split(",");
    		for (i=0; i<topicAdminList.length; i++) {
    			if (sTopicId.equals(topicAdminList[i])) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    /**
     * 能否发布话题
     * @param context
     * @param topic
     * @return
     */
    public static boolean canTopicRelease(Context context, MTopicResp topic) {
    	
    	String role = SharePreCacheHelper.getUserRole(context);
    	if ((role.equalsIgnoreCase("adv_user")) || (role.equalsIgnoreCase("global_admin"))) {
    		return true;
    	}
        return false;
    }
    
    /**
     * 能否删除话题
     * @param context
     * @param topic
     * @return
     */
    public static boolean canTopicDelete(Context context, MTopicResp topic) {
    	
    	if (topic == null) {
    		return false;
    	}
    	String role = SharePreCacheHelper.getUserRole(context);
    	String topicOwner = SharePreCacheHelper.getTopicOwnerList(context);
    	String sTopicId = Long.toString(topic.getTopicId());
    	int i;

    	if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}
    	
    	if (topicOwner != null) {
    		String[] topicOwnerList = topicOwner.split(",");
    		for (i=0; i<topicOwnerList.length; i++) {
    			if (sTopicId.equals(topicOwnerList[i])) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    /**
     * 能否修改话题
     * @param context
     * @param topic
     * @return
     */
    public static boolean canTopicModify(Context context, MTopicResp topic) {
    	
    	return canTopicDelete(context, topic);
    }

    /**
     * 能否操作banner
     * @param context
     * @return
     */
    public static boolean canBannerOperate(Context context, Serializable post) {
    	String role = SharePreCacheHelper.getUserRole(context);
		boolean result = false;
		if (post instanceof MPostResp) {
			result = ((MPostResp) post).getPostLevel() == 0;
		} else if (post instanceof MSolutionResp) {
			MSolutionResp plan = (MSolutionResp) post;
			result = plan.getSolutionId() != 0 && plan.getCommentId() == 0;
		}
    	if (((role.equalsIgnoreCase("global_admin")) || (role.equalsIgnoreCase("operator")))) {
    		return true && result;
    	}

    	return false;
    }
    
    /**
     * 能否操作预置话题
     * @param context
     * @return
     */
    public static boolean canPresetTopicOperate(Context context) {
    	String role = SharePreCacheHelper.getUserRole(context);
    	
    	if ((role.equalsIgnoreCase("global_admin")) || (role.equalsIgnoreCase("operator"))) {
    		return true;
    	}

    	return false;
    }
    
    /**
     * 能否操作精选帖
     * @param context
     * @return
     */
    public static boolean canPostCollectionOperate(Context context, Serializable post) {
    	
    	return canBannerOperate(context, post);
    }
    
    /**
     * 能否上传版本
     * @param context
     * @return
     */
    public static boolean canUploadApp(Context context) {

    	return canPresetTopicOperate(context);
    }
    
    /**
     * 能否修改一个帖子为护肤方案
     * @param context
     * @param topicId
     * @return
     */
    public static boolean canChangeToSolution(Context context, long topicId) {
    	String role = SharePreCacheHelper.getUserRole(context);
    	String topicOwner = SharePreCacheHelper.getTopicOwnerList(context);
    	String topicAdmin = SharePreCacheHelper.getTopicAdminList(context);
		String sTopicId = Long.toString(topicId);
		int i;
		
		if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}
    	
		if (topicOwner != null) {
			String[] topicOwnerList = topicOwner.split(",");
			for (i=0; i<topicOwnerList.length; i++) {
				if (sTopicId.equals(topicOwnerList[i])) {
					return true;
				}
			}
		}
    	
    	if (topicAdmin != null) {
    		String[] topicAdminList = topicAdmin.split(",");
    		for (i=0; i<topicAdminList.length; i++) {
    			if (sTopicId.equals(topicAdminList[i])) {
    				return true;
    			}
    		}
    	}
		
    	return false;
    }
    
    /**
     * 能否置顶一个帖子
     * @param context
     * @param topicId
     * @return
     */
    public static boolean canOnTopPost(Context context, long topicId) {

    	return canChangeToSolution(context, topicId);
    }
    
    /**
     * 能否为某个话题设限
     * @param context
     * @param topicId
     * @return
     */
    public static boolean canForbiddenTopic(Context context, long topicId) {

    	return canChangeToSolution(context, topicId);
    }
    
    /**
     * 能否设置其他用户为topicadmin
     * @param context
     * @param topicId
     * @return
     */
    public static boolean canSetUserTopicAdmin(Context context, long topicId) {
    	String role = SharePreCacheHelper.getUserRole(context);
    	String topicOwner = SharePreCacheHelper.getTopicOwnerList(context);
		String sTopicId = Long.toString(topicId);
		int i;
		
		if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}
    	
		if (topicOwner != null) {
			String[] topicOwnerList = topicOwner.split(",");
			for (i=0; i<topicOwnerList.length; i++) {
				if (sTopicId.equals(topicOwnerList[i])) {
					return true;
				}
			}
		}
    	
    	return false;
    }
    
    /**
     * 能否移帖
     * @param context
     * @return
     */
    public static boolean canMovePost(Context context) {

    	String role = SharePreCacheHelper.getUserRole(context);
    	
    	if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * 能否设置其他用户为operator
     * @param context
     * @return
     */
    public static boolean canSetUserOperator(Context context) {

    	return canMovePost(context);
    }
    
    /**
     * 能否删除护肤方案的评论
     * @param context
     * @return
     */
    public static boolean canDeleteSolutionComment(Context context, SolutionResp solution) {
    	
		String role = SharePreCacheHelper.getUserRole(context);
		String userId = SharePreCacheHelper.getUserID(context);
		
		if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}

    	if(solution != null && solution.getUserId() != null && solution.getUserId().equals(userId)) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static boolean isAdminUser(Context context){
		
    	String role = SharePreCacheHelper.getUserRole(context);
    	if (role.equalsIgnoreCase("global_admin")) {
    		return true;
    	}
        return false;
    }

    //能否发布护肤方案（新版本）
	public static boolean canPlanRelease(Context context) {
		
		String role = SharePreCacheHelper.getUserRole(context);
		if ((role.equalsIgnoreCase("global_admin")) || (role.equalsIgnoreCase("operator"))) {
    		return true;
    	}
		
		return SharePreCacheHelper.getLevel(context) >= 1;
	}

	//能否修改护肤方案（新版本）
    public static boolean canPlanModify(Context context, MSolutionResp solution) {
    	
		String role = SharePreCacheHelper.getUserRole(context);
		String userId = SharePreCacheHelper.getUserID(context);
		
		if ((role.equalsIgnoreCase("global_admin")) || (role.equalsIgnoreCase("operator"))) {
    		return true;
    	}

    	if(solution != null && solution.getUserId() != null && solution.getUserId().equals(userId)) {
    		return true;
    	}
    	
    	return false;
    }

  //能否删除护肤方案（新版本）
    public static boolean canPlanDelete(Context context, MSolutionResp solution) {
    	
    	return canPlanModify(context, solution);
    }
}
