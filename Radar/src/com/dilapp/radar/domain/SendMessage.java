package com.dilapp.radar.domain;

import java.util.List;


/**
 * 
 * @author john
 * 
 */
public abstract class SendMessage {
	
	//群发系统消息给用户
	public abstract void multiSendMsgtoUserAsync(SendMsgReq bean, BaseCall<BaseResp> call);


	public static class SendMsgReq extends BaseReq {
		List<String> userIds;
		String content;

		public List<String> getUserIds() {
			return userIds;
		}
		public void setUserIds(List<String> userIds) {
			this.userIds = userIds;
		}

		public String getMsgContent() {
			return content;
		}
		public void setMsgContent(String content) {
			this.content = content;
		}
	}

	
}
