package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author john 精选帖子相关
 */
public abstract class PostCollection {
	
	//上传精选帖子图片
	public abstract void uploadCollectionImgAsync(List<String> imgs, BaseCall<UploadCollectionImgResp> call);
	//更新精选帖子
	public abstract void editPostCollectionAsync(EditCollectionReq bean, BaseCall<BaseResp> call);
	//删除精选帖子
	public abstract void deletePostCollectionAsync(DeleteCollectionReq bean, BaseCall<BaseResp> call);

	//获取精选帖子列表
	public abstract void getPostCollectionListByTypeAsync(int pageNo, BaseCall<GetPostCollectionListResp> call, int type);
	
	
	public static class EditCollectionReq extends BaseReq {
		private long topicId;
		private long postId;
		private long solutionId;  //护肤方案Id，其与postId/topicId是互斥的
		private String picUrl;
		private String slogan;
		
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
		
		public String getpicUrl() {
			return picUrl;
		}
		public void setpicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		
		public String getSlogan() {
			return slogan;
		}
		public void setSlogan(String slogan) {
			this.slogan = slogan;
		}
	}
	
	public static class DeleteCollectionReq extends BaseReq {
		private long topicId;
		private long solutionId;  //护肤方案Id，其与topicId是互斥的
		
		public long getTopicId() {
			return topicId;
		}
		public void setTopicId(long topicId) {
			this.topicId = topicId;
		}
		
		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
	}
	

	public static class UploadCollectionImgResp extends BaseResp {
		private String picUrl;

		public String getPicUrl() {
			return picUrl;
		}

		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
	}
	
	public static class GetPostCollectionListResp extends BaseResp {
		private int totalPage;
		private int pageNo;
		private List<PostCollectionResp> datas;
		
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
		
		public List<PostCollectionResp> getDatas() {
			return datas;
		}
		public void setDatas(List<PostCollectionResp> datas) {
			this.datas = datas;
		}
	}
	
    public static class PostCollectionResp implements Serializable {
    	
        private long topicId;
        private long postId;
        private long solutionId;  //护肤方案Id，其与postId/topicId是互斥的
        private String solutionTitle;  //护肤方案Title，其与topicTitle是互斥的
        private String topicTitle;
        private String slogan;
        private String picUrl;
        private long updateTime;
        private long postUpdateTime;

		public PostCollectionResp() {
		}

		public PostCollectionResp(String picUrl) {
			this.picUrl = picUrl;
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
		
        public String getSolutionTitle() {
            return solutionTitle;
        }
        public void setSolutionTitle(String solutionTitle) {
            this.solutionTitle = solutionTitle;
        }
        
        public String getTopicTitle() {
            return topicTitle;
        }
        public void setTopicTitle(String topicTitle) {
            this.topicTitle = topicTitle;
        }
        
		public String getpicUrl() {
			return picUrl;
		}
		public void setpicUrl(String picUrl) {
			this.picUrl = picUrl;
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

}
