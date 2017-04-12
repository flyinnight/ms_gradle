package com.dilapp.radar.domain.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DeletePostTopic;
import com.dilapp.radar.domain.FollowupPostTopic;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class FollowupPostTopicImpl extends FollowupPostTopic {
	private Handler handler1;
	private Handler handler2;
	private Context context;
	private ServerRequestParams params;

	public FollowupPostTopicImpl(Context context) {
		this.context = context;
	}

	@Override
	public void followupPostAsync(FollowupPostReq bean,
			final BaseCall<BaseResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "followupPostAsync: " + result);
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
						handler1.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	//��ע����
	@Override
	public void followupTopicAsync(FollowupTopicReq bean,
			final BaseCall<BaseResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeTopicParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "followupTopicAsync: " + result);
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
						handler2.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	private ServerRequestParams writePostParams(FollowupPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.followupPost(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("followup", Boolean.toString(bean.getFollowup()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}

	private ServerRequestParams writeTopicParams(FollowupTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.followupTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("follow", Boolean.toString(bean.getFollowup()));
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
