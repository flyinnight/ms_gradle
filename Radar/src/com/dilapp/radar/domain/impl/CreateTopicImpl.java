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
import com.dilapp.radar.domain.CreateTopic;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class CreateTopicImpl extends CreateTopic {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	public CreateTopicImpl(Context context) {
		this.context = context;
	}

	@Override
	public void uploadTopicImgAsync(List<String> imgs,
			final BaseCall<TopicReleaseResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicReleaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						TopicReleaseResp resp = new TopicReleaseResp();
						Log.d("Radar", "uploadTopicImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setTopicImgUrl(jsonObject3.optString("imageUrl"));// {"imageUrl":"topic/icon/1435390758441/666999.jpg"]}
							//resp.setStatus(jsonObject2.optString("ok"));//true

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
						TopicReleaseResp resp = new TopicReleaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	@Override
	public void createTopicAsync(TopicReleaseReq bean,
			final BaseCall<TopicReleaseResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicReleaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreatParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						TopicReleaseResp resp = new TopicReleaseResp();
						Log.d("Radar", "createTopicAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setRoles(jsonObject3.optString("roles"));
							resp.setTopicId(jsonObject3.optLong("topicId"));
							resp.setUserId(jsonObject3.optString("userId"));
							//resp.setStatus((String)jsonObject2.get("ok"));//true

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
						TopicReleaseResp resp = new TopicReleaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	@Override
	public void updateTopicAsync(TopicReleaseReq bean,
			final BaseCall<TopicReleaseResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicReleaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						TopicReleaseResp resp = new TopicReleaseResp();
						Log.d("Radar", "updateTopicAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							/*JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setRoles(jsonObject3.optString("roles"));
							resp.setTopicId(jsonObject3.optLong("topicId"));
							resp.setUserId(jsonObject3.optString("userId"));*/
							//resp.setStatus((String)jsonObject2.get("ok"));//true

						} catch (JSONException e) {
							e.printStackTrace();
							resp.setStatus("FAILED");
							Log.d("Radar", "JSONException: " + e);
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						TopicReleaseResp resp = new TopicReleaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	

	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadTopicImg(null));
		param.put("topicImgFile", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeCreatParams(TopicReleaseReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.createTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicTitle", bean.getTopicTitle());
		param.put("topicDes", bean.getTopicDes());
		param.put("topicImgUrl", bean.getTopicImgUrl());
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeUpdateParams(TopicReleaseReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.updateTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("topicTitle", bean.getTopicTitle());
		param.put("topicDes", bean.getTopicDes());
		param.put("topicImgUrl", bean.getTopicImgUrl());
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
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
