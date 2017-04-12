/*********************************************************************/
/*  文件名  BaseCall.java    　                                         */
/*  程序名  ui回调接口                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain;

public abstract class BaseCall<T> extends BaseCallNode{

//	public boolean cancel;
	
	public abstract void call(T resp);
}