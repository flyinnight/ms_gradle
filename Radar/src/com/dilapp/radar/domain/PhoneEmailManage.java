package com.dilapp.radar.domain;


/**
 * 
 * @author john
 * 移帖
 * 置顶
 */
public abstract class PhoneEmailManage {
	//验证用户邮箱
	public abstract void emailVerifyAsync(BaseCall<BaseResp> call);
	//用户绑定或修改手机号
	public abstract void bindPhoneNoAsync(BindPhoneReq bean, BaseCall<BaseResp> call);
	//用户绑定或修改邮箱
	public abstract void bindEmailAsync(String email, BaseCall<BaseResp> call);

	
	public static class BindPhoneReq extends BaseReq {
		private String phoneNo;
		//短信验证码
		private String verifyCode;
		//区号 国家码 86
		public String regionCode;

		public String getPhoneNo() {
			return phoneNo;
		}
		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}
		
		public String getVerifyCode() {
			return verifyCode;
		}
		public void setVerifyCode(String verifyCode) {
			this.verifyCode = verifyCode;
		}
		
		public String getRegionCode() {
			return regionCode;
		}
		public void setRegionCode(String regionCode) {
			this.regionCode = regionCode;
		}
	}
}
