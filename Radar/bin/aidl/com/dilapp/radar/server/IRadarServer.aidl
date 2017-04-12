/*********************************************************************/
/*  文件名  IRadarServer.aidl    　                                     */
/*  程序名  服务回调接口                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

import com.dilapp.radar.server.IRadarCallback;
import com.dilapp.radar.server.ServerRequestParams;

interface IRadarServer{
void registerCallback(in IRadarCallback callback);

void unRegisterCallback();

void startTestScript(int script);

void startUploadServer(in ServerRequestParams requestParams, in int callBackId);

void startLocalData(String localRequestParams,String localContent, in int callBackId);
}