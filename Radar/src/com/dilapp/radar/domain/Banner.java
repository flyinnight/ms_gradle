package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;

import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.PostCollection.GetPostCollectionListResp;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;


/**
 *
 * @author john 精选帖子相关
 */
public abstract class Banner {

	public static final int LIST_BY_BANNER = 1;   //Banner列表
	public static final int LIST_BY_COLLECTION = 2;   //精选帖子列表
	
	//上传banner图片
	public abstract void uploadBannerImgAsync(List<String> imgs, BaseCall<UploadBannerImgResp> call);
	//添加banner
	public abstract void createBannerAsync(CreateBannerReq bean, BaseCall<BaseResp> call);
	//删除banner
	public abstract void deleteBannerAsync(DeleteBannerReq bean, BaseCall<BaseResp> call);
	//banner顺序
	public abstract void updateBannerPriorityAsync(BannerPriorityReq bean, BaseCall<BaseResp> call);

	//获取banner列表
	public abstract void getBannerListByTypeAsync(BaseReq bean, BaseCall<GetBannerListResp> call, int type);
	

	public static class CreateBannerReq extends BaseReq {
		private long topicId;
		private long postId;
		private long solutionId;  //护肤方案Id，其与postId/topicId是互斥的
		private String bannerURL;
		private String slogan;
		private int priority;

		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}
		
		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}

		public String getBannerUrl() {
			return bannerURL;
		}
		public void setBannerUrl(String bannerURL) {
			this.bannerURL = bannerURL;
		}

		public String getSlogan() {
			return slogan;
		}
		public void setSlogan(String slogan) {
			this.slogan = slogan;
		}

		public int getPriority() {
			return priority;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public static class DeleteBannerReq extends BaseReq {
		private int priority;

		public int getPriority() {
			return priority;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}
	}

	public static class BannerPriorityReq extends BaseReq {
		private int priority1;
		private int priority2;

		public int getPriority1() {
			return priority1;
		}
		public void setPriority1(int priority1) {
			this.priority1 = priority1;
		}

		public int getPriority2() {
			return priority2;
		}
		public void setPriority2(int priority2) {
			this.priority2 = priority2;
		}
	}

	public static class UploadBannerImgResp extends BaseResp {
		private List<String> bannerImgURL;

		public List<String> getBannerImgURL() {
			return bannerImgURL;
		}

		public void setBannerImgURL(List<String> bannerImgURL) {
			this.bannerImgURL = bannerImgURL;
		}
	}

	public static class GetBannerListResp extends BaseResp {

		private List<BannerResp> datas;

		public List<BannerResp> getDatas() {
			return datas;
		}
		public void setDatas(List<BannerResp> datas) {
			this.datas = datas;
		}
	}

	public static class BannerResp implements Serializable {

		private long topicId;
		private long postId;
		private long solutionId;  //护肤方案Id，其与postId/topicId是互斥的
		private String slogan;
		private List<String> bannerUrl;
		private long updateTime;
		private int priority;
		private long postUpdateTime;

		public int getPriority() {
			return priority;
		}
		public void setPriority(int priority) {
			this.priority = priority;
		}

		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}

		public long getPostId() {
			return postId;
		}
		public void setPostId(long postId) {
			this.postId = postId;
		}

		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
		
		public List<String> getBannerUrl() {
			return bannerUrl;
		}
		public void setBannerUrl(List<String> bannerUrl) {
			this.bannerUrl = bannerUrl;
		}

		public String getSlogan() {
			return slogan;
		}
		public void setSlogan(String slogan) {
			this.slogan = slogan;
		}

		public long getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(long updateTime) {
			this.updateTime = updateTime;
		}
		
		public long getPostUpdateTime() {
            return postUpdateTime;
        }
        public void setPostUpdateTime(long postUpdateTime) {
            this.postUpdateTime = postUpdateTime;
        }
	}

    //存储本地的banner或者精选
    public static class BannerCollectionSave implements Serializable  {
    	private int type;
    	private long updateTime;
    	private GetBannerListResp bannerResp;
		private GetPostCollectionListResp collectionResp;

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
        
        public GetBannerListResp getBannerContent() {
			return bannerResp;
		}
		public void setBannerContent(GetBannerListResp bannerResp) {
			this.bannerResp = bannerResp;
		}
		
		public GetPostCollectionListResp getCollectionList() {
			return collectionResp;
		}
		public void setCollectionList(GetPostCollectionListResp collectionResp) {
			this.collectionResp = collectionResp;
		}
    }
}
