/*********************************************************************/
/*  文件名  HttpCallback.java    　                                     */
/*  程序名  http数据回调                     						     				 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

public interface HttpCallback {
	
	public void onServerMessage(String result, final int callBackId);
	
}
