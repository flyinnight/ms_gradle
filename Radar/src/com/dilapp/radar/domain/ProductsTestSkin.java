package com.dilapp.radar.domain;

import java.util.List;

import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.HistoricalRecords.HistoricalReq;
import com.dilapp.radar.domain.HistoricalRecords.MHistoricalResp;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;

public abstract class ProductsTestSkin {
	//护肤品测试
	//插入多条测试数据
	public abstract void productsTestSkinAsync(ProductsTestSkinReq bean, BaseCall<BaseResp> call);

	public abstract void getLastProductsTestSkin(HistoricalReq bean, BaseCall<MHistoricalResp> call);

	
	public static class ProductsTestSkinReq extends BaseReq {

		private static final long serialVersionUID = 1L;

		// 护肤品测试之前的数据
		private TestSkinReq before;
		// 护肤品测试之后的数据
		private TestSkinReq after;

		public TestSkinReq getBefore() {
			return before;
		}
		public void setBefore(TestSkinReq before) {
			this.before = before;
		}

		public TestSkinReq getAfter() {
			return after;
		}
		public void setAfter(TestSkinReq after) {
			this.after = after;
		}
	}

	public static class SaveReordReq extends BaseResp {
		
		private int ifCloud;
		private List<FacialAnalyzeBean> value;

		public int getIfCloud() {
			return ifCloud;
		}
		public void setIfCloud(int ifCloud) {
			this.ifCloud = ifCloud;
		}
		
		public List<FacialAnalyzeBean> getValue() {
			return value;
		}
		public void setValue(List<FacialAnalyzeBean> value) {
			this.value = value;
		}
	}
}
