package com.dilapp.radar.domain;


/**
 * 
 * @author john
 *	创建话题
 */
public abstract class  UpdateVersion {

	//获取最近版本
	public abstract void getLatestVersionAsync(UpdateVersionReq bean, BaseCall<UpdateVersionResp> call);

	/**
	 * 获取最近版本Bean
	 * 
	 * @author john
	 */
	public static class UpdateVersionReq extends BaseReq {
		// 应用名称（不带版本号）
		private String appName;

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}
		
	}

	/**
	 * 获取最近版本
	 * 
	 * @author john
	 */
	public class UpdateVersionResp extends BaseResp {
		private String version;
		private String url;
		
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}

	}
}
