/*********************************************************************/
/*  文件名  RadarServer.java    　                                      */
/*  程序名  定义服务类                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.dilapp.radar.db.DBFactory;
import com.dilapp.radar.util.XUtilsHelper;

public class RadarServer extends Service {
	private IRadarCallback mCallback;
	XUtilsHelper xutilsHelper;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mIBinder;
	}

	private final IRadarServer.Stub mIBinder = new IRadarServer.Stub() {

		@Override
		public void unRegisterCallback() throws RemoteException {
			mCallback = null;
		}

		@Override
		public void startTestScript(int script) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void registerCallback(IRadarCallback callback) throws RemoteException {
			mCallback = callback;
		}

		@Override
		public void startUploadServer(ServerRequestParams requestParams, final int callBackId) throws RemoteException {
			if (xutilsHelper == null) {
				xutilsHelper = new XUtilsHelper(getApplicationContext(), new httpCallbackImpl());
			}
			xutilsHelper.send(requestParams, callBackId);
		}

		@Override
		public void startLocalData(String requestAction, String localContent, final int callBackId) throws RemoteException {
			DBFactory.buildManager(requestAction, localContent, new httpCallbackImpl(), callBackId);
		}
	};

	private class httpCallbackImpl implements HttpCallback {

		@Override
		public void onServerMessage(String serverResult, final int callBackId) {
			clientCallbackResult(serverResult, callBackId);
		}
	}

	private void clientCallbackResult(String serverResult, final int callBackId) {
		try {
			mCallback.onTestScriptResult(serverResult, callBackId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
