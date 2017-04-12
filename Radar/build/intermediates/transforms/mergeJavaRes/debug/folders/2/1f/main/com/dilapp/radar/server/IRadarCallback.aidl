/*********************************************************************/
/*  文件名  IRadarCallback.aidl    　                                    */
/*  程序名 代理回调接口                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

interface IRadarCallback{
void onTestScriptResult(String result, in int callBackId);
}