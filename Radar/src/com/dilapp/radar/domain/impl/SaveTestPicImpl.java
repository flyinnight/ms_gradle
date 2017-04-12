package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.SaveTestPic;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class SaveTestPicImpl extends SaveTestPic {
	private Handler handler1;
	private Handler handler2;
	private Context context;
	private ServerRequestParams params;

	public SaveTestPicImpl(Context context) {
		this.context = context;
	}

	@Override
	public void uploadFacialPicAsync(List<String> imgs,
			final BaseCall<FacialPicResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((FacialPicResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						FacialPicResp resp = new FacialPicResp();
						Log.d("Radar", "uploadFacialPicAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							
							Object temp = jsonObject3.opt("facialPicsUrl");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("facialPicsUrl"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setFacialPicsUrl(imgList);
							}
							
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
						FacialPicResp resp = new FacialPicResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	@Override
	public void saveFacialPicAsync(FacialPicReq bean,
			final BaseCall<FacialPicResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((FacialPicResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeSaveParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						FacialPicResp resp = new FacialPicResp();
						Log.d("Radar", "saveFacialPicAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.getBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.getInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject
									.get("message").toString());
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setId(jsonObject3.optLong("id"));
							resp.setUserName(jsonObject3.optString("username"));
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
						FacialPicResp resp = new FacialPicResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	

	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadFacialPic(null));
		param.put("facialPics", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeSaveParams(FacialPicReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.saveFacialPic(null));
		params.setStatus(0);
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
