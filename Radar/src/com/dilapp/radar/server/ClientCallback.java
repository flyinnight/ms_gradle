/*********************************************************************/
/*  文件名  ClientCallback.java    　                                   */
/*  程序名  ui回调接口                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

public interface ClientCallback {

	public void onSuccess(String result);

	public void onFailure(String result);
}
