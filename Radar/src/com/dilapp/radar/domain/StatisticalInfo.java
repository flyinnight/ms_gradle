package com.dilapp.radar.domain;



/***
 * 服务器统计的数据信息
 * @author john
 *
 */
public abstract class StatisticalInfo {
	
	//日新帖数目统计
	public abstract void totalPostCountAsync(DailyCountReq bean, BaseCall<DailyCountResp> call);
	
	//日新话题数目
	public abstract void newTopicCountAsync(DailyCountReq bean, BaseCall<DailyCountResp> call);
	
	//新设备激活
	public abstract void activatedDeviceAsync(String sn, BaseCall<BaseResp> call);
	
	//日新设备激活数
	public abstract void totalActivatedDeviceNumAsync(DailyCountReq bean, BaseCall<DailyCountResp> call);
	
	//日回复数目
	public abstract void totalReplyCountAsync(DailyCountReq bean, BaseCall<DailyCountResp> call);
	
	
	public static class DailyCountReq extends BaseReq {
		private long from;
		private long to;
		
		public long getFrom() {
			return from;
		}
		public void setFrom(long from) {
			this.from = from;
		}
		
		public long getTo() {
			return to;
		}
		public void setTo(long to) {
			this.to = to;
		}
	}
	
	public static class DailyCountResp extends BaseResp {
		private int count;
		
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
	}
	
}
