package com.dilapp.radar.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.ReleaseUtils;

public class RegisterImpl extends Register {
	private Context context;
	private Handler handler1;
	private Handler handler2;
	private ServerRequestParams params;

	public RegisterImpl(Context context) {
		this.context = context;
	}

	@Override
	public void regAsync(RegReq bean, final BaseCall<BaseResp> call) {

		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeRegParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				BaseResp resp = new BaseResp();
				Log.d("Radar", "regAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					String token = jsonObject3.optString("token");
					if ((token != null) && (!token.equals(""))) {
						writeLocalContent(token);
					}
					
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
	
	private void writeLocalContent(String token) {
		HttpConstant.TOKEN = token;
		//RadarProxy.getInstance(context).startLocalData(HttpConstant.getLoginUrl(null), token, null);
		SharePreCacheHelper.setUserToken(context, token);
	}
	
	@Override
	public void regRadarAsync(RegRadarReq bean, final BaseCall<RegRadarResp> call) {

		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((RegRadarResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeRadarRegParam(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				RegRadarResp resp = new RegRadarResp();
				Log.d("Radar", "regRadarAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setUserId(jsonObject3.optString("uid"));
					String UserId = jsonObject3.optString("uid");
					String EMUserId = jsonObject3.optString("msgUid");
					if (!TextUtils.isEmpty(UserId)) {
						SharePreCacheHelper.setUserID(context, UserId);
					}
					if (!TextUtils.isEmpty(EMUserId)) {
						SharePreCacheHelper.setEMUserId(context, EMUserId);
					}

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
				RegRadarResp resp = new RegRadarResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
				System.out.println(result);
			}
		});
	}

	private ServerRequestParams writeRegParams(RegReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getRegUrl(null));
		Map<String, Object> requestParam = new HashMap<String, Object>();
		requestParam.put("username", bean.getUserId());
		requestParam.put("password", bean.getPwd());
		requestParam.put("code", bean.getVerifyCode());
		requestParam.put("regionCode", bean.getRegionCode());
		params.setRequestParam(requestParam);
		return params;
	}
	
	private ServerRequestParams writeRadarRegParam(RegRadarReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getRadarRegUrl(null));
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(bean));
		return params;
	}

	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
	
}
