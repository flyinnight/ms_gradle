package com.dilapp.radar.domain.impl;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionDetailData;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionListData.SolutionDataGetDelete;
import com.dilapp.radar.domain.SolutionListData.SolutionDataSave;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class SolutionDetailDataImpl extends SolutionDetailData {
	private Handler handler1;
	private Handler handler2;
	private Context context;
	private ServerRequestParams params;

	public SolutionDetailDataImpl(Context context) {
		this.context = context;
	}

	// 获取护肤方案详情
	@Override
	public void getSolutionDetailDataAsync(final long solutionId, final BaseCall<MSolutionResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionResp) msg.obj);
				}
			}
		};
		
		RadarProxy.getInstance(context).startServerData(writeDetailParams(solutionId), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				MSolutionResp resp =new MSolutionResp();
				int statusCode = BaseResp.OK;
				Log.d("Radar", "getSolutionDetailDataAsync: " + result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setSolutionId(jsonObject3.optLong("id"));
					resp.setEffect((String[])(jsonObject3.optString("effect").split(",")));
					resp.setPart((String[])(jsonObject3.optString("part").split(",")));
					resp.setTitle(jsonObject3.optString("title"));
					resp.setIntroduction(jsonObject3.optString("introduction"));
					resp.setContent(jsonObject3.optString("content"));
					resp.setCoverImgUrl(jsonObject3.optString("coverUrl"));
					resp.setCoverThumbImgUrl(jsonObject3.optString("coverThumbnailUrl"));
					/*Object temp1 = jsonObject3.opt("textImgUrl");
					if (temp1 != null) {
						JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("textImgUrl"));
						List<String> imgList = new ArrayList<String>();
						for (int j = 0; j < jsonArrImg.length(); j++) {
							String imgItem = (String) jsonArrImg.get(j);
							imgList.add(imgItem);
						}
						resp.settextImgUrl(imgList);
					}*/
					resp.setUseCycle(jsonObject3.optInt("cycle"));
					resp.setScore(jsonObject3.optDouble("score"));
					resp.setMyScore(jsonObject3.optInt("myScore"));
					resp.setUsedCount(jsonObject3.optInt("usedCount"));
					resp.setStoreUpCount(jsonObject3.optInt("usestoreupCount"));
					resp.setCreateTime(jsonObject3.optLong("createTime"));
					resp.setUpdateTime(jsonObject3.optLong("updateTime"));
					resp.setUserId(jsonObject3.optString("userId"));
					resp.setNickName(jsonObject3.optString("username"));
					resp.setPortrait(jsonObject3.optString("portrait"));
					resp.setLevel(jsonObject3.optInt("level"));
					resp.setIsStoreup(jsonObject3.optBoolean("isStoreup"));
					resp.setInUse(jsonObject3.optBoolean("inUse"));
					
					SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setSolutionDetail(resp);
					saveBean.setSolutionId(solutionId);
					saveBean.setType(SolutionListData.SOLUTION_DETAIL_DATA);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						SolutionDataGetDelete delBean = new SolutionDataGetDelete();
						delBean.setType(SolutionListData.SOLUTION_DETAIL_DATA);
						delBean.setSolutionId(solutionId);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						getSolutionDetailLocal(solutionId);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MSolutionResp resp = new MSolutionResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		});
	}
	
	private void getSolutionDetailLocal(long solutionId) {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_DETAIL_DATA);
		getBean.setSolutionId(solutionId);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionDetailLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				MSolutionResp resp = respSave.getSolutionDetail();
				
				if (resp == null) {
					resp = new MSolutionResp();
					resp.setStatus("FAILED");
				} else {
					resp.setStatus("SUCCESS");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MSolutionResp resp = new MSolutionResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		});
	
	
	}
	

	// 获取正在使用的护肤方案
	@Override
	public void getSolutionInUsedDataAsync(final BaseCall<MSolutionResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionResp) msg.obj);
				}
			}
		};
		
		RadarProxy.getInstance(context).startServerData(writeInUsedParams(), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				MSolutionResp resp =new MSolutionResp();
				int statusCode = BaseResp.OK;
				Log.d("Radar", "getSolutionInUsedDataAsync: " + result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setSolutionId(jsonObject3.optLong("id"));
					resp.setEffect((String[])(jsonObject3.optString("effect").split(",")));
					resp.setPart((String[])(jsonObject3.optString("part").split(",")));
					resp.setTitle(jsonObject3.optString("title"));
					resp.setIntroduction(jsonObject3.optString("introduction"));
					resp.setContent(jsonObject3.optString("content"));
					resp.setCoverImgUrl(jsonObject3.optString("coverUrl"));
					resp.setCoverThumbImgUrl(jsonObject3.optString("coverThumbnailUrl"));
					/*Object temp1 = jsonObject3.opt("textImgUrl");
					if (temp1 != null) {
						JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("textImgUrl"));
						List<String> imgList = new ArrayList<String>();
						for (int j = 0; j < jsonArrImg.length(); j++) {
							String imgItem = (String) jsonArrImg.get(j);
							imgList.add(imgItem);
						}
						resp.settextImgUrl(imgList);
					}*/
					resp.setUseCycle(jsonObject3.optInt("cycle"));
					resp.setScore(jsonObject3.optDouble("score"));
					resp.setMyScore(jsonObject3.optInt("myScore"));
					resp.setUsedCount(jsonObject3.optInt("usedCount"));
					resp.setStoreUpCount(jsonObject3.optInt("usestoreupCount"));
					resp.setCreateTime(jsonObject3.optLong("createTime"));
					resp.setUpdateTime(jsonObject3.optLong("updateTime"));
					resp.setStartTime(jsonObject3.optLong("startTime"));
					resp.setUserId(jsonObject3.optString("userId"));
					resp.setNickName(jsonObject3.optString("username"));
					resp.setPortrait(jsonObject3.optString("portrait"));
					resp.setIsStoreup(jsonObject3.optBoolean("isStoreup"));
					resp.setInUse(jsonObject3.optBoolean("inUse"));
					
					SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setSolutionDetail(resp);
					saveBean.setSolutionId(-1);
					saveBean.setType(SolutionListData.SOLUTION_INUSED_DATA);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						SolutionDataGetDelete delBean = new SolutionDataGetDelete();
						delBean.setType(SolutionListData.SOLUTION_INUSED_DATA);
						delBean.setSolutionId(-1);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						getSolutionInUsedLocal();
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MSolutionResp resp = new MSolutionResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		});
	}
	
	private void getSolutionInUsedLocal() {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_INUSED_DATA);
		getBean.setSolutionId(-1);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionInUsedLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				MSolutionResp resp = respSave.getSolutionDetail();
				
				if (resp == null) {
					resp = new MSolutionResp();
					resp.setStatus("FAILED");
				} else {
					resp.setStatus("SUCCESS");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MSolutionResp resp = new MSolutionResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		});
	
	
	}

	
	private ServerRequestParams writeDetailParams(long solutionId) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.solutionDetail(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("solutionId", Long.toString(solutionId));
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeInUsedParams() {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.solutionInUsed(null));
		Map<String, Object> param = new HashMap<String, Object>();
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
