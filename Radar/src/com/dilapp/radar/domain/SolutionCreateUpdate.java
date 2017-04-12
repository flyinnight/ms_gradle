package com.dilapp.radar.domain;


import java.util.List;



import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;

/**
 * 
 * @author john
 * 护肤方案发布和修改相关
 *
 */
public abstract class SolutionCreateUpdate {

	public static final String SOLUTION_RELEASE_END = "dilapp.radar.solution.release.end";
	public static final int SOLUTION_RELEASE_SENDING = 1; //发送中，未返回最终结果
	public static final int SOLUTION_RELEASE_FAILED = 2; //发送失败
	public static final int SOLUTION_RELEASE_SUCCESS = 3; //发送成功
	
	// 上传护肤方案封面图片(封面限定一张图片)
	public abstract void solutionUplCoverImgAsync(String imgs, BaseCall<CoverImgResp> call);
	// 上传护肤方案正文图片
	public abstract void solutionUplTextImgAsync(List<String> imgs, BaseCall<TextImgResp> call);
	// 创建护肤方案
	public abstract void solutionCreateAsync(SolutionCreateReq bean, BaseCall<MSolutionResp> call);
	// 修改护肤方案
	public abstract void solutionUpdateAsync(SolutionUpdateReq bean, BaseCall<MSolutionResp> call);

	// 删除未发布成功/未更新成功的护肤方案
	public abstract void solutionDeleteLocalItemAsync(long localSolutionId, BaseCall<BaseResp> call);
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的护肤方案
	public abstract void solutionDeleteAllLocalDataAsync(BaseCall<BaseResp> call);
	
	
	public static class CoverImgResp extends BaseResp {
        private String coverImgUrl;
        private String coverThumbImgUrl;
        
        public String getCoverImgUrl() {
            return coverImgUrl;
        }
        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getCoverThumbImgUrl() {
            return coverThumbImgUrl;
        }
        public void setCoverThumbImgUrl(String coverThumbImgUrl) {
            this.coverThumbImgUrl = coverThumbImgUrl;
        }
	}
	
	public static class TextImgResp extends BaseResp {
        private List<String> textImgUrl;
        
        public List<String> getTextImgUrl() {
            return textImgUrl;
        }
        public void setTextImgUrl(List<String> textImgUrl) {
            this.textImgUrl = textImgUrl;
        }
	}
	

	public static class SolutionCreateReq extends BaseReq {
		
		private long localSolutionId;  //后台发送时，UI无需赋值
		private String[] effect;
		private String[] part;
		private String title;
		private String introduction;
		private String content;
		//private String coverLocalUrl;  //封面图片本地存储路径，需UI填写
		private String coverUrl;  //后台发送时，内容需变更
		private String coverThumbUrl;  //后台发送时，UI无需赋值
		private int useCycle;
		
		public long getLocalSolutionId() {
			return localSolutionId;
		}
		public void setLocalSolutionId(long localSolutionId) {
			this.localSolutionId = localSolutionId;
		}
		
		public String[] getEffect() {
			return effect;
		}
		public void setEffect(String[] effect) {
			this.effect = effect;
		}

		public String[] getPart() {
			return part;
		}
		public void setPart(String[] part) {
			this.part = part;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		public String getIntroduction() {
			return introduction;
		}
		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		/*public String getCoverLocalUrl() {
			return coverLocalUrl;
		}
		public void setCoverLocalUrl(String coverLocalUrl) {
			this.coverLocalUrl = coverLocalUrl;
		}*/
		
		public String getCoverUrl() {
			return coverUrl;
		}
		public void setCoverUrl(String coverUrl) {
			this.coverUrl = coverUrl;
		}

		public String getCoverThumbUrl() {
			return coverThumbUrl;
		}
		public void setCoverThumbUrl(String coverThumbUrl) {
			this.coverThumbUrl = coverThumbUrl;
		}

		public int getUseCycle() {
			return useCycle;
		}
		public void setUseCycle(int useCycle) {
			this.useCycle = useCycle;
		}
	}
	
	public static class SolutionUpdateReq extends SolutionCreateReq {
		private long solutionId;
		
		public long getSolutionId() {
			return solutionId;
		}
		public void setSolutionId(long solutionId) {
			this.solutionId = solutionId;
		}
	}
	
	
	public static class SolutionCreateServerReq extends BaseReq {
		
		private String effect;
		private String part;
		private String title;
		private String introduction;
		private String content;
		private String coverUrl;
		private String coverThumbnailUrl;
		private int cycle;
		
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
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		public String getIntroduction() {
			return introduction;
		}
		public void setIntroduction(String introduction) {
			this.introduction = introduction;
		}
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		public String getCoverUrl() {
			return coverUrl;
		}
		public void setCoverUrl(String coverUrl) {
			this.coverUrl = coverUrl;
		}

		public String getCoverThumbUrl() {
			return coverThumbnailUrl;
		}
		public void setCoverThumbUrl(String coverThumbnailUrl) {
			this.coverThumbnailUrl = coverThumbnailUrl;
		}

		public int getUseCycle() {
			return cycle;
		}
		public void setUseCycle(int cycle) {
			this.cycle = cycle;
		}
	}
	
	public static class SolutionUpdateServerReq extends SolutionCreateServerReq {

		private long id;
		
		public long getSolutionId() {
			return id;
		}
		public void setSolutionId(long id) {
			this.id = id;
		}
	}
}
