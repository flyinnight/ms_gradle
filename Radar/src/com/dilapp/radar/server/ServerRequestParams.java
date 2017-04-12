/*********************************************************************/
/*  文件名  ServerRequestParams.java    　                              */
/*  程序名  Transport entity                     						 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

import java.util.Map;

import com.dilapp.radar.domain.AnalyzeType;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerRequestParams implements Parcelable {

	private String requestUrl;
	// parameter is less than 5
	private Map<String, ?> requestParam;
	// parameter is more than 5
	private String requestEntity;
	private String token;
	/**
	 * This variable defines some HTTP States 
	 * {@link XUtilsHelper#UPLOAD_FILE}
	 */
	private int status;

	public static Parcelable.Creator<ServerRequestParams> getCreator() {
		return CREATOR;
	}

	public static final Parcelable.Creator<ServerRequestParams> CREATOR = new Parcelable.Creator<ServerRequestParams>() {

		@Override
		public ServerRequestParams createFromParcel(Parcel in) {
			return new ServerRequestParams(in);
		}

		@Override
		public ServerRequestParams[] newArray(int size) {
			return new ServerRequestParams[size];
		}
	};

	public ServerRequestParams() {
	}

	public ServerRequestParams(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel readIn) {
		requestUrl = readIn.readString();
		requestParam = readIn.readHashMap(Map.class.getClassLoader());
		requestEntity = readIn.readString();
		token = readIn.readString();
		status = readIn.readInt();
	}

	@Override
	public void writeToParcel(Parcel writeDest, int flags) {
		writeDest.writeString(requestUrl);
		writeDest.writeMap(requestParam);
		writeDest.writeString(requestEntity);
		writeDest.writeString(token);
		writeDest.writeInt(status);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
	}

	public Map<String, ?> getRequestParam() {
		return requestParam;
	}

	public void setRequestParam(Map<String, Object> requestParam) {
		this.requestParam = requestParam;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
