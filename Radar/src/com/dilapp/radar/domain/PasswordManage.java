package com.dilapp.radar.domain;


/**
 * 
 * @author john
 * 移帖
 * 置顶
 */
public abstract class PasswordManage {
	//用户修改密码
	public abstract void changePwdAsync(ChangePwdReq bean, BaseCall<BaseResp> call);
	//忘记密码（手机找回-1）
	public abstract void retrievePwdByPhoneAsync(String phoneNo, BaseCall<BaseResp> call);
	//重置密码（手机找回-2）
	public abstract void resetPwdByPhoneAsync(ResetPwdPhoneReq bean, BaseCall<BaseResp> call);
	//忘记密码（邮箱找回-1）
	public abstract void retrievePwdByEmailAsync(String email, BaseCall<BaseResp> call);
	//重置密码（邮箱找回-2）
	//public abstract void resetPwdByEmailAsync(ResetPwdEmailReq bean, BaseCall<BaseResp> call);
	
	
	public static class ChangePwdReq extends BaseReq {
		private String oldPwd;
		private String newPwd;

		public String getOldPwd() {
			return oldPwd;
		}
		public void setOldPwd(String oldPwd) {
			this.oldPwd = oldPwd;
		}

		public String getNewPwd() {
			return newPwd;
		}
		public void setNewPwd(String newPwd) {
			this.newPwd = newPwd;
		}
	}
	
	public static class ResetPwdPhoneReq extends BaseReq {
		private String phoneNo;
		private String newPwd;
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

		public String getNewPwd() {
			return newPwd;
		}
		public void setNewPwd(String newPwd) {
			this.newPwd = newPwd;
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
	
/*	public static class ResetPwdEmailReq extends BaseReq {
		private String email;
		private String newPwd;

		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}

		public String getNewPwd() {
			return newPwd;
		}
		public void setNewPwd(String newPwd) {
			this.newPwd = newPwd;
		}
	}*/
}
