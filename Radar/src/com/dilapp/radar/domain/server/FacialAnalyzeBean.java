/*********************************************************************/
/*  文件名  FacialAnalyzeBean.java    　                                */
/*  程序名  测试模块图表返回bean                     						 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain.server;

import java.io.Serializable;

/**
 * Created by Leeon on 2015/4/13.
 */
public class FacialAnalyzeBean implements Serializable {
	private String rid;// record id;
	private String uid;// user id;
	private int type;// analyze type
	private int subtype;
	private int analyzePart;
	private long analyzeTime;
	private String analyzePlace;
	private String analyzeClimate;

	private int param1Value;
	private int param1Standard;
	private String param1Result;
	private int param2Value;
	private int param2Standard;
	private String param2Result;
	private int param3Value;
	private int param3Standard;
	private String param3Result;
	private int param4Value;
	private int param4Standard;
	private String param4Result;
	private int param5Value;
	private int param5Standard;
	private String param5Result;
	private int param6Value;
	private int param6Standard;
	private String param6Result;
	private int param7Value;
	private int param7Standard;
	private String param7Result;

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

	public int getParam1Value() {
		return param1Value;
	}

	public void setParam1Value(int param1Value) {
		this.param1Value = param1Value;
	}

	public int getParam1Standard() {
		return param1Standard;
	}

	public void setParam1Standard(int param1Standard) {
		this.param1Standard = param1Standard;
	}

	public String getParam1Result() {
		return param1Result;
	}

	public void setParam1Result(String param1Result) {
		this.param1Result = param1Result;
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

	public int getParam2Value() {
		return param2Value;
	}

	public void setParam2Value(int param2Value) {
		this.param2Value = param2Value;
	}

	public int getParam2Standard() {
		return param2Standard;
	}

	public void setParam2Standard(int param2Standard) {
		this.param2Standard = param2Standard;
	}

	public String getParam2Result() {
		return param2Result;
	}

	public void setParam2Result(String param2Result) {
		this.param2Result = param2Result;
	}

	public int getParam3Value() {
		return param3Value;
	}

	public void setParam3Value(int param3Value) {
		this.param3Value = param3Value;
	}

	public int getParam3Standard() {
		return param3Standard;
	}

	public void setParam3Standard(int param3Standard) {
		this.param3Standard = param3Standard;
	}

	public String getParam3Result() {
		return param3Result;
	}

	public void setParam3Result(String param3Result) {
		this.param3Result = param3Result;
	}

	public int getParam4Value() {
		return param4Value;
	}

	public void setParam4Value(int param4Value) {
		this.param4Value = param4Value;
	}

	public int getParam4Standard() {
		return param4Standard;
	}

	public void setParam4Standard(int param4Standard) {
		this.param4Standard = param4Standard;
	}

	public String getParam4Result() {
		return param4Result;
	}

	public void setParam4Result(String param4Result) {
		this.param4Result = param4Result;
	}

	public int getParam5Value() {
		return param5Value;
	}

	public void setParam5Value(int param5Value) {
		this.param5Value = param5Value;
	}

	public int getParam5Standard() {
		return param5Standard;
	}

	public void setParam5Standard(int param5Standard) {
		this.param5Standard = param5Standard;
	}

	public String getParam5Result() {
		return param5Result;
	}

	public void setParam5Result(String param5Result) {
		this.param5Result = param5Result;
	}

	public int getParam6Value() {
		return param6Value;
	}

	public void setParam6Value(int param6Value) {
		this.param6Value = param6Value;
	}

	public int getParam6Standard() {
		return param6Standard;
	}

	public void setParam6Standard(int param6Standard) {
		this.param6Standard = param6Standard;
	}

	public String getParam6Result() {
		return param6Result;
	}

	public void setParam6Result(String param6Result) {
		this.param6Result = param6Result;
	}

	public int getParam7Value() {
		return param7Value;
	}

	public void setParam7Value(int param7Value) {
		this.param7Value = param7Value;
	}

	public int getParam7Standard() {
		return param7Standard;
	}

	public void setParam7Standard(int param7Standard) {
		this.param7Standard = param7Standard;
	}

	public String getParam7Result() {
		return param7Result;
	}

	public void setParam7Result(String param7Result) {
		this.param7Result = param7Result;
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
