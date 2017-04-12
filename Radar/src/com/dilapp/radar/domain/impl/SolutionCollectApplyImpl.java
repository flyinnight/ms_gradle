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
import com.dilapp.radar.domain.SolutionCollectApply;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;


public class SolutionCollectApplyImpl extends SolutionCollectApply {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	public SolutionCollectApplyImpl(Context context) {
		this.context = context;
	}

	@Override
	public void storeupPostAsSolutionAsync(StoreupSolutionReq bean, final BaseCall<BaseResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeStoreupParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "storeupPostAsSolutionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
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
	
	@Override
	public void changePostToSolutionAsync(ChangePostSolutionReq bean, final BaseCall<BaseResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeChangeParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "changePostToSolutionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
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
	
	@Override
	public void selectedSolutionAsync(SelectedSolutionReq bean, final BaseCall<SelectedSolutionResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SelectedSolutionResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeSelectedParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						SelectedSolutionResp resp = new SelectedSolutionResp();
						Log.d("Radar", "selectedSolutionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setPostId(jsonObject3.optLong("postId"));

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
						SelectedSolutionResp resp = new SelectedSolutionResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	private ServerRequestParams writeStoreupParams(StoreupSolutionReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.storeupPostAsSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("selectedSolution", Boolean.toString(bean.getSelectedSolution()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeChangeParams(ChangePostSolutionReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.changePostToSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("part", bean.getPart());
		param.put("effect", bean.getEffect());
		param.put("skin", bean.getSkin());
		param.put("setPlan", Boolean.toString(bean.getSetPlan()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeSelectedParams(SelectedSolutionReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.selectedSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("selected", Boolean.toString(bean.getSelected()));
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
