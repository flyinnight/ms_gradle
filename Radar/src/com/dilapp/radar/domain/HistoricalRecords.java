/*********************************************************************/
/*  文件名  HistoricalRecords.java    　                                */
/*  程序名  抽象历史记录域                      						     			     */
/*  版本履历   2015/5/6  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

import java.io.Serializable;
import java.util.List;


import com.dilapp.radar.domain.server.FacialAnalyzeBean;

public abstract class HistoricalRecords {

	//根据条件查询服务器存储的测试数据
	public abstract void historicalRecordsAsync(HistoricalReq bean, BaseCall<MHistoricalResp> call);
	
	//根据条件查询服务器上平均测试数据
	public abstract void queryAverageTestDataAsync(AverageDataReq bean, BaseCall<MAverageResp> call);
	
	//根据条件查询本地测试数据
	public abstract void queryLocalRecordsAsync(HistoricalReq bean, BaseCall<MHistoricalResp> call);

	
	public static class HistoricalReq extends BaseReq {
		/**
		 * the analyzePart to{@linkplain AnalyzeType#FOREHEAD}
		 * {@linkplain AnalyzeType#CHEEK} {@linkplain AnalyzeType#EYE}
		 * {@linkplain AnalyzeType#NOSE} {@linkplain AnalyzeType#HAND} the type
		 * to{@linkplain AnalyzeType#DAILY} {@linkplain AnalyzeType#SKIN}
		 * {@linkplain AnalyzeType#SPECIAL}
		 * {@linkplain AnalyzeType#SKIN_PRODUCTS}
		 */
		private int type;
		private int analyzePart;
		//private String analyzeParam;
		private long startTime;
		private long endTime;
		private long pageTime;

		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

		public int getAnalyzePart() {
			return analyzePart;
		}
		public void setAnalyzePart(int analyzePart) {
			this.analyzePart = analyzePart;
		}

		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		public long getPageTime() {
			return pageTime;
		}
		public void setPageTime(long pageTime) {
			this.pageTime = pageTime;
		}

	}

	public static class AverageDataReq extends BaseReq {
		/**
		 *the type
		 * to{@linkplain AnalyzeType#DAILY} {@linkplain AnalyzeType#SKIN}
		 * {@linkplain AnalyzeType#SPECIAL}
		 * {@linkplain AnalyzeType#SKIN_PRODUCTS}
		 */
		private int type;
		private int queryType;  //查询类型 1—小时  2—天  3—月
		private long startTime;
		private long endTime;

		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

		public int getQueryType() {
			return queryType;
		}
		public void setQueryType(int queryType) {
			this.queryType = queryType;
		}

		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
	}
	
	public static class HistoricalResp extends BaseResp {
		private List<FacialAnalyzeBean> value;

		public List<FacialAnalyzeBean> getValue() {
			return value;
		}

		public void setValue(List<FacialAnalyzeBean> value) {
			this.value = value;
		}
	}
	
	public static class LocalTestDataReq extends BaseReq {

		private String type;
		private String analyzePart;
		private String startTime;
		private String endTime;
		private String userId;

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

		public String getAnalyzePart() {
			return analyzePart;
		}
		public void setAnalyzePart(String analyzePart) {
			this.analyzePart = analyzePart;
		}

		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}

	}
	
	public static class MHistoricalResp extends BaseResp {
		private List<FacialAnalyzeResp> value;

		public List<FacialAnalyzeResp> getValue() {
			return value;
		}

		public void setValue(List<FacialAnalyzeResp> value) {
			this.value = value;
		}
	}
	
	public static class MAverageResp extends BaseResp {
		private AverageData foreheadValue;
		private AverageData cheekValue;
		private AverageData eyeValue;
		private AverageData noseValue;
		private AverageData handValue;

		public AverageData getForeheadValue() {
			return foreheadValue;
		}
		public void setForeheadValue(AverageData foreheadValue) {
			this.foreheadValue = foreheadValue;
		}
		
		public AverageData getCheekValue() {
			return cheekValue;
		}
		public void setCheekValue(AverageData cheekValue) {
			this.cheekValue = cheekValue;
		}
		
		public AverageData getEyeValue() {
			return eyeValue;
		}
		public void setEyeValue(AverageData eyeValue) {
			this.eyeValue = eyeValue;
		}
		
		public AverageData getNoseValue() {
			return noseValue;
		}
		public void setNoseValue(AverageData noseValue) {
			this.noseValue = noseValue;
		}
		
		public AverageData getHandValue() {
			return handValue;
		}
		public void setHandValue(AverageData handValue) {
			this.handValue = handValue;
		}
	}
	
	public static class AverageData implements Serializable {
		private String uid; // user id;
		private int type;
		private int analyzePart;
		private List<AverageResult> result;

		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		
		public int getAnalyzePart() {
			return analyzePart;
		}
		public void setAnalyzePart(int analyzePart) {
			this.analyzePart = analyzePart;
		}

		public List<AverageResult> getValue() {
			return result;
		}
		public void setValue(List<AverageResult> result) {
			this.result = result;
		}
	}
	
	public static class AverageResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private long analyzeTime;
		private int waterResult;
		private int oilResult;
		private int elasticResult;
		private int sensitiveResult;
		private int whiteningResult;
		private int poreResult;
		private int skinAgeResult;

		public long getAnalyzeTime() {
			return analyzeTime;
		}
		public void setAnalyzeTime(long analyzeTime) {
			this.analyzeTime = analyzeTime;
		}
		
		public int getWaterResult() {
			return waterResult;
		}
		public void setWaterResult(int waterResult) {
			this.waterResult = waterResult;
		}

		public int getOilResult() {
			return oilResult;
		}
		public void setOilResult(int oilResult) {
			this.oilResult = oilResult;
		}

		public int getElasticResult() {
			return elasticResult;
		}
		public void setElasticResult(int elasticResult) {
			this.elasticResult = elasticResult;
		}

		public int getSensitiveResult() {
			return sensitiveResult;
		}
		public void setSensitiveResult(int sensitiveResult) {
			this.sensitiveResult = sensitiveResult;
		}

		public int getWhiteningResult() {
			return whiteningResult;
		}
		public void setWhiteningResult(int whiteningResult) {
			this.whiteningResult = whiteningResult;
		}

		public int getPoreResult() {
			return poreResult;
		}
		public void setPoreResult(int poreResult) {
			this.poreResult = poreResult;
		}

		public int getSkinAgeResult() {
			return skinAgeResult;
		}
		public void setSkinAgeResult(int skinAgeResult) {
			this.skinAgeResult = skinAgeResult;
		}
	}
	
	public static class FacialAnalyzeResp implements Serializable {
		private String rid;// record id;
		private String uid;// user id;
		private int type;// analyze type
		private int subtype;
		private int analyzePart;
		private long analyzeTime;
		private String analyzePlace;
		private String analyzeClimate;

		private int waterResult;
		private int oilResult;
		private int elasticResult;
		private int sensitiveResult;
		private int whiteningResult;
		private int poreResult;
		private int skinAgeResult;

		private int waterStandard;
		private int oilStandard;
		private int elasticStandard;
		private int sensitiveStandard;
		private int whiteningStandard;
		private int poreStandard;
		private int skinAgeStandard;
		
		public String cosmeticID;
		public String schemaID;
		public String labelID;

		public String getRid() {
			return rid;
		}
		public void setRid(String rid) {
			this.rid = rid;
		}

		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}

		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

		public long getAnalyzeTime() {
			return analyzeTime;
		}
		public void setAnalyzeTime(long analyzeTime) {
			this.analyzeTime = analyzeTime;
		}

		public int getWaterStandard() {
			return waterStandard;
		}
		public void setWaterStandard(int waterStandard) {
			this.waterStandard = waterStandard;
		}

		public int getWaterResult() {
			return waterResult;
		}
		public void setWaterResult(int waterResult) {
			this.waterResult = waterResult;
		}

		public int getSubtype() {
			return subtype;
		}
		public void setSubtype(int subtype) {
			this.subtype = subtype;
		}

		public int getAnalyzePart() {
			return analyzePart;
		}
		public void setAnalyzePart(int analyzePart) {
			this.analyzePart = analyzePart;
		}

		public String getAnalyzePlace() {
			return analyzePlace;
		}
		public void setAnalyzePlace(String analyzePlace) {
			this.analyzePlace = analyzePlace;
		}

		public String getAnalyzeClimate() {
			return analyzeClimate;
		}
		public void setAnalyzeClimate(String analyzeClimate) {
			this.analyzeClimate = analyzeClimate;
		}

		public int getOilStandard() {
			return oilStandard;
		}
		public void setOilStandard(int oilStandard) {
			this.oilStandard = oilStandard;
		}

		public int getOilResult() {
			return oilResult;
		}
		public void setOilResult(int oilResult) {
			this.oilResult = oilResult;
		}

		public int getElasticStandard() {
			return elasticStandard;
		}
		public void setElasticStandard(int elasticStandard) {
			this.elasticStandard = elasticStandard;
		}

		public int getElasticResult() {
			return elasticResult;
		}
		public void setElasticResult(int elasticResult) {
			this.elasticResult = elasticResult;
		}

		public int getSensitiveStandard() {
			return sensitiveStandard;
		}
		public void setSensitiveStandard(int sensitiveStandard) {
			this.sensitiveStandard = sensitiveStandard;
		}

		public int getSensitiveResult() {
			return sensitiveResult;
		}
		public void setSensitiveResult(int sensitiveResult) {
			this.sensitiveResult = sensitiveResult;
		}
		
		public int getWhiteningStandard() {
			return whiteningStandard;
		}
		public void setWhiteningStandard(int whiteningStandard) {
			this.whiteningStandard = whiteningStandard;
		}

		public int getWhiteningResult() {
			return whiteningResult;
		}
		public void setWhiteningResult(int whiteningResult) {
			this.whiteningResult = whiteningResult;
		}
		
		public int getPoreStandard() {
			return poreStandard;
		}
		public void setPoreStandard(int poreStandard) {
			this.poreStandard = poreStandard;
		}

		public int getPoreResult() {
			return poreResult;
		}
		public void setPoreResult(int poreResult) {
			this.poreResult = poreResult;
		}

		public int getSkinAgeStandard() {
			return skinAgeStandard;
		}
		public void setSkinAgeStandard(int skinAgeStandard) {
			this.skinAgeStandard = skinAgeStandard;
		}

		public int getSkinAgeResult() {
			return skinAgeResult;
		}
		public void setSkinAgeResult(int skinAgeResult) {
			this.skinAgeResult = skinAgeResult;
		}

		public String getCosmeticID() {
			return cosmeticID;
		}
		public void setCosmeticID(String cosmeticID) {
			this.cosmeticID = cosmeticID;
		}

		public String getSchemaID() {
			return schemaID;
		}
		public void setSchemaID(String schemaID) {
			this.schemaID = schemaID;
		}

		public String getLabelID() {
			return labelID;
		}
		public void setLabelID(String labelID) {
			this.labelID = labelID;
		}
	}
}
