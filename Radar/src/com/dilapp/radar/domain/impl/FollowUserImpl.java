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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.FollowUser;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class FollowUserImpl extends FollowUser {
	private Handler handler;
	private Context context;
	private ServerRequestParams params;

	public FollowUserImpl(Context context) {
		this.context = context;
	}

	@Override
	public void followUserAsync(FollowUserReq bean,
			final BaseCall<BaseResp> call) {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeLikeParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "followUserAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
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
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	private ServerRequestParams writeLikeParams(FollowUserReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.followUser(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", bean.getUserId());
		param.put("follow", Boolean.toString(bean.getFollow()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}

	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
}
