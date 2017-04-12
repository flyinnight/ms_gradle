/*********************************************************************/
/*  文件名  DailyTestSkin.java    　                                    */
/*  程序名  抽象每日测试域                      						     			     */
/*  版本履历   2015/5/6  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

public abstract class DailyTestSkin {

	public final static int NOT_UPLOAD_SERVER = 0;  // 测试数据未上传服务器
	public final static int UPLOADED_SERVER = 1;  // 测试数据已成功上传服务器
	
	/**
	 * 日常测试
	 * 添加单条测试数据
	 * 
	 * @param bean
	 * @param call
	 */
	public abstract void dailyTestSkinAsync(TestSkinReq bean, BaseCall<TestSkinResp> call);

	public static class TestSkinReq extends BaseReq {

		/**
		 * the part to{@linkplain AnalyzeType#FOREHEAD}
		 * {@linkplain AnalyzeType#CHEEK} {@linkplain AnalyzeType#EYE}
		 * {@linkplain AnalyzeType#NOSE} {@linkplain AnalyzeType#HAND}
		 * the type to{@linkplain AnalyzeType#DAILY} {@linkplain AnalyzeType#SKIN}
		 * {@linkplain AnalyzeType#SPECIAL}
		 * {@linkplain AnalyzeType#SKIN_PRODUCTS}
		 */

		private int type;
		private int part;
		private int water;
		private int oil;
		private int eastic;
		private int whitening;
		private int sensitive;
		private int pore;
		private int skinAge;
		
		private String rid;// record id; 根据测试的时间生成，并赋值
		private String uid;// user id;
		private int subtype;
		private long analyzeTime;
		private String analyzePlace;
		private String analyzeClimate;		
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

		public long getAnalyzeTime() {
			return analyzeTime;
		}
		public void setAnalyzeTime(long analyzeTime) {
			this.analyzeTime = analyzeTime;
		}

		public int getSubtype() {
			return subtype;
		}
		public void setSubtype(int subtype) {
			this.subtype = subtype;
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
		
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}

		public int getPart() {
			return part;
		}
		public void setPart(int part) {
			this.part = part;
		}

		public int getWater() {
			return water;
		}
		public void setWater(int water) {
			this.water = water;
		}

		public int getOil() {
			return oil;
		}
		public void setOil(int oil) {
			this.oil = oil;
		}

		public int getEastic() {
			return eastic;
		}
		public void setElastic(int eastic) {
			this.eastic = eastic;
		}

		public int getWhitening() {
			return whitening;
		}
		public void setWhitening(int whitening) {
			this.whitening = whitening;
		}

		public int getSensitive() {
			return sensitive;
		}
		public void setSensitive(int sensitive) {
			this.sensitive = sensitive;
		}

		public int getPore() {
			return pore;
		}
		public void setPore(int pore) {
			this.pore = pore;
		}

		public int getSkinAge() {
			return skinAge;
		}
		public void setSkinAge(int skinAge) {
			this.skinAge = skinAge;
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

	public static class TestSkinResp extends BaseResp {

		private int waterAvg;
		private int waterLast;
		private int oilAvg;
		private int oilLast;
		private int elasticAvg;
		private int elasticLast;
		private int whiteningAvg;
		private int whiteningLast;
		private int sensitiveAvg;
		private int sensitiveLast;
		private int poreAvg;
		private int poreLast;

		private int waterMax;
		private int oilMax;
		private int elasticMax;
		private int whiteningMax;
		private int sensitiveMax;
		private int poreMax;

		public int getWaterAvg() {
			return waterAvg;
		}

		public void setWaterAvg(int waterAvg) {
			this.waterAvg = waterAvg;
		}

		public int getWaterLast() {
			return waterLast;
		}

		public void setWaterLast(int waterLast) {
			this.waterLast = waterLast;
		}

		public int getOilAvg() {
			return oilAvg;
		}

		public void setOilAvg(int oilAvg) {
			this.oilAvg = oilAvg;
		}

		public int getOilLast() {
			return oilLast;
		}

		public void setOilLast(int oilLast) {
			this.oilLast = oilLast;
		}

		public int getElasticAvg() {
			return elasticAvg;
		}

		public void setElasticAvg(int elasticAvg) {
			this.elasticAvg = elasticAvg;
		}

		public int getElasticLast() {
			return elasticLast;
		}

		public void setElasticLast(int elasticLast) {
			this.elasticLast = elasticLast;
		}

		public int getWhiteningAvg() {
			return whiteningAvg;
		}

		public void setWhiteningAvg(int whiteningAvg) {
			this.whiteningAvg = whiteningAvg;
		}

		public int getWhiteningLast() {
			return whiteningLast;
		}

		public void setWhiteningLast(int whiteningLast) {
			this.whiteningLast = whiteningLast;
		}

		public int getSensitiveAvg() {
			return sensitiveAvg;
		}

		public void setSensitiveAvg(int sensitiveAvg) {
			this.sensitiveAvg = sensitiveAvg;
		}

		public int getSensitiveLast() {
			return sensitiveLast;
		}

		public void setSensitiveLast(int sensitiveLast) {
			this.sensitiveLast = sensitiveLast;
		}

		public int getPoreAvg() {
			return poreAvg;
		}

		public void setPoreAvg(int poreAvg) {
			this.poreAvg = poreAvg;
		}

		public int getPoreLast() {
			return poreLast;
		}

		public void setPoreLast(int poreLast) {
			this.poreLast = poreLast;
		}

		public int getWaterMax() {
			return waterMax;
		}

		public void setWaterMax(int waterMax) {
			this.waterMax = waterMax;
		}

		public int getOilMax() {
			return oilMax;
		}

		public void setOilMax(int oilMax) {
			this.oilMax = oilMax;
		}

		public int getElasticMax() {
			return elasticMax;
		}

		public void setElasticMax(int elasticMax) {
			this.elasticMax = elasticMax;
		}

		public int getWhiteningMax() {
			return whiteningMax;
		}

		public void setWhiteningMax(int whiteningMax) {
			this.whiteningMax = whiteningMax;
		}

		public int getSensitiveMax() {
			return sensitiveMax;
		}

		public void setSensitiveMax(int sensitiveMax) {
			this.sensitiveMax = sensitiveMax;
		}

		public int getPoreMax() {
			return poreMax;
		}

		public void setPoreMax(int poreMax) {
			this.poreMax = poreMax;
		}

	}
}
