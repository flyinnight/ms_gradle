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
import com.dilapp.radar.domain.StatisticalInfo;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class StatisticalInfoImpl extends StatisticalInfo {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private Context context;
	private ServerRequestParams params;

	public StatisticalInfoImpl(Context context) {
		this.context = context;
	}

	//日新帖数目统计
	@Override
	public void totalPostCountAsync(DailyCountReq bean, final BaseCall<DailyCountResp> call) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((DailyCountResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				DailyCountResp resp = new DailyCountResp();
				Log.d("Radar", "totalPostCountAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setCount(jsonObject3.optInt("count"));
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				DailyCountResp resp = new DailyCountResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		});
	}
	
	//日新话题数目
	@Override
	public void newTopicCountAsync(DailyCountReq bean, final BaseCall<DailyCountResp> call) {
		handler2 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((DailyCountResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeTopicParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				DailyCountResp resp = new DailyCountResp();
				Log.d("Radar", "newTopicCountAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setCount(jsonObject3.optInt("count"));
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				DailyCountResp resp = new DailyCountResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		});
	}
	
	//新设备激活
	@Override
	public void activatedDeviceAsync(String sn, final BaseCall<BaseResp> call) {
		handler3 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeActivateParams(sn), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				BaseResp resp = new BaseResp();
				Log.d("Radar", "activatedDeviceAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
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
	
	//日新设备激活数
	@Override
	public void totalActivatedDeviceNumAsync(DailyCountReq bean, final BaseCall<DailyCountResp> call) {
		handler4 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((DailyCountResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeDeviceParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				DailyCountResp resp = new DailyCountResp();
				Log.d("Radar", "totalActivatedDeviceNumAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setCount(jsonObject3.optInt("total"));
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler4.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				DailyCountResp resp = new DailyCountResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler4.sendMessage(msg);
			}
		});
	}
	
	//日新设备激活数
	@Override
	public void totalReplyCountAsync(DailyCountReq bean, final BaseCall<DailyCountResp> call) {
		handler5 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((DailyCountResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeReplyParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				DailyCountResp resp = new DailyCountResp();
				Log.d("Radar", "totalReplyCountAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setCount(jsonObject3.optInt("totalCount"));
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler5.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				DailyCountResp resp = new DailyCountResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler5.sendMessage(msg);
			}
		});
	}
	
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}

	private ServerRequestParams writePostParams(DailyCountReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.totalPostCount(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("from", Long.toString(bean.getFrom()));
		param.put("to", Long.toString(bean.getTo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeTopicParams(DailyCountReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.newTopicCount(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("fromDate", Long.toString(bean.getFrom()));
		param.put("toDate", Long.toString(bean.getTo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeActivateParams(String sn) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.activatedDevice(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sn", sn);
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeDeviceParams(DailyCountReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.totalActivatedDeviceNum(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("from", Long.toString(bean.getFrom()));
		param.put("to", Long.toString(bean.getTo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeReplyParams(DailyCountReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.totalReplyCount(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("startDate", Long.toString(bean.getFrom()));
		param.put("endDate", Long.toString(bean.getTo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
}
