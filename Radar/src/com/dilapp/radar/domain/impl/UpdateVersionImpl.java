package com.dilapp.radar.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.UpdateVersion;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class UpdateVersionImpl extends UpdateVersion {
	private Handler handler;
	private Context context;
	private ServerRequestParams params;

	public UpdateVersionImpl(Context context) {
		this.context = context;
	}

	@Override
	public void getLatestVersionAsync(UpdateVersionReq bean,
			final BaseCall<UpdateVersionResp> call) {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UpdateVersionResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						UpdateVersionResp resp = new UpdateVersionResp();
						Log.d("Radar", "uploadTopicImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setVersion(jsonObject3.optString("version"));
							
							String strUrl = jsonObject3.optString("url");
							strUrl = strUrl.replace("/data/version","");
							resp.setUrl(strUrl);
							//resp.setStatus(jsonObject2.optString("ok"));//true

						} catch (JSONException e) {
							e.printStackTrace();
							resp.setStatus("FAILED");
							Log.d("Radar", "JSONException: " + e);
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						UpdateVersionResp resp = new UpdateVersionResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler.sendMessage(msg);
						System.out.println(result);
					}
				});
	}


	private ServerRequestParams writeParams(UpdateVersionReq bean) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getLatestVersion(null));
		param.put("appName", bean.getAppName());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}

	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
}
