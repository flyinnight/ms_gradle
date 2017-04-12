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
import com.dilapp.radar.domain.SolutionOperate;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;


public class SolutionOperateImpl extends SolutionOperate {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	public SolutionOperateImpl(Context context) {
		this.context = context;
	}

	// 删除护肤方案
	@Override
	public void solutionDeleteAsync(long solutionId, final BaseCall<BaseResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeDeleteParams(solutionId),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						BaseResp resp = new BaseResp();
						Log.d("Radar", "solutionDeleteAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
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
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}
				});
	}
	
	// 收藏护肤方案
	@Override
	public void solutionStoreupAsync(StoreupReq bean, final BaseCall<BaseResp> call) {
		handler2 = new Handler() {
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
						BaseResp resp = new BaseResp();
						Log.d("Radar", "solutionStoreupAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
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
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					}
				});
	}
	
	// 使用护肤方案
	@Override
	public void solutionUseAsync(UseReq bean, final BaseCall<BaseResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUseParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						BaseResp resp = new BaseResp();
						Log.d("Radar", "solutionUseAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS

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
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}
				});
	}
	

	private ServerRequestParams writeDeleteParams(long solutionId) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.deleteSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(solutionId));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeStoreupParams(StoreupReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.storeupSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(bean.getSolutionId()));
		param.put("isStoreup", Boolean.toString(bean.getIsStoreup()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeUseParams(UseReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.useSolution(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(bean.getSolutionId()));
		param.put("isUse", Boolean.toString(bean.getIsUse()));
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
