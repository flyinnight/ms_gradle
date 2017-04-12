/*********************************************************************/
/*  文件名  BaseResp.java    　                                         */
/*  程序名  返回结果基类                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

import java.io.Serializable;

public class BaseResp implements Serializable {
	private static final long serialVersionUID = 1L;
	public final static int OK = 0;// 上传服务器成功
	public final static int NET_ERROR = 1;// 没有网络
	public final static int TIME_OUT = 2;// 连接超时
	public final static int PARAM_ERROR = 3;// 参数错误
	public final static int NOT_SERVICE = 4;// 服务未开启
	public final static int RETURN_NULL = 5;// 返回空
	public final static int DATA_LOCAL = 6;// 返回的是本地数据
	public final static int INTERNAL_SERVER_ERROR = 7;// 服务器内部错误
	public final static int UNKNOWN = 8;// 其他原因
	// false:local true:net&&local
	public final static boolean DB_TAG = true;
	private String status;
	private boolean success;
	private String message;  /*服务器返回的msg消息*/
	/**
	 * @link {@link #NET_ERROR}{@link #TIMEOUT}{@link #PARAM_ERROR}
	 *       {@link #NOT_SERVICE}{@link #RETURN_NULL}{@link #UNKNOWN}
	 */
	private int statusCode;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isRequestSuccess() {
		return "SUCCESS".equalsIgnoreCase(status);
	}
}
