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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class SolutionCreateUpdateAsyncImpl extends SolutionCreateUpdate {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Context context;
	private ServerRequestParams params;

	
	public SolutionCreateUpdateAsyncImpl(Context context) {
		this.context = context;
	}

	// 上传护肤方案封面图片
	@Override
	public void solutionUplCoverImgAsync(String imgs, final BaseCall<CoverImgResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((CoverImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCoverImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						CoverImgResp resp = new CoverImgResp();
						Log.d("Radar", "solutionUplCoverImgAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							Object temp = jsonObject3.opt("URL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("URL"));
								if (jsonArrImg.length() > 0) {
									String imgItem = (String) jsonArrImg.get(0);
									resp.setCoverImgUrl(imgItem);
								}
							}
							
							Object temp1 = jsonObject3.opt("ThumbURL");
							if (temp1 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("ThumbURL"));
								if (jsonArrImg.length() > 0) {
									String imgItem = (String) jsonArrImg.get(0);
									resp.setCoverThumbImgUrl(imgItem);
								}
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
						System.out.println(result);
						CoverImgResp resp = new CoverImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}
				});
	}

	// 上传护肤方案正文图片
	@Override
	public void solutionUplTextImgAsync(List<String> imgs, final BaseCall<TextImgResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TextImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeTextImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						TextImgResp resp = new TextImgResp();
						Log.d("Radar", "solutionUplTextImgAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							Object temp = jsonObject3.opt("URL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("URL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setTextImgUrl(imgList);
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
						System.out.println(result);
						TextImgResp resp = new TextImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					}
				});
	}

	// 创建护肤方案
	@Override
	public void solutionCreateAsync(SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						MSolutionResp resp = new MSolutionResp();
						Log.d("Radar", "solutionCreateAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setSolutionId(jsonObject3.optLong("id"));
							resp.setEffect((String[])(jsonObject3.optString("effect").split(",")));
							resp.setPart((String[])(jsonObject3.optString("part").split(",")));
							resp.setTitle(jsonObject3.optString("title"));
							resp.setIntroduction(jsonObject3.optString("introduction"));
							resp.setContent(jsonObject3.optString("content"));
							resp.setCoverImgUrl(jsonObject3.optString("coverUrl"));
							resp.setCoverThumbImgUrl(jsonObject3.optString("coverThumbnailUrl"));
							resp.setUseCycle(jsonObject3.optInt("cycle"));
							resp.setScore(jsonObject3.optDouble("score"));
							resp.setUsedCount(jsonObject3.optInt("usedCount"));
							resp.setStoreUpCount(jsonObject3.optInt("usestoreupCount"));
							resp.setCreateTime(jsonObject3.optLong("createTime"));
							resp.setUpdateTime(jsonObject3.optLong("updateTime"));
							resp.setUserId(jsonObject3.optString("userId"));
							resp.setNickName(jsonObject3.optString("username"));
							resp.setPortrait(jsonObject3.optString("portrait"));
							
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
						MSolutionResp resp = new MSolutionResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}
				});
	}

	// 修改护肤方案
	@Override
	public void solutionUpdateAsync(SolutionUpdateReq bean, final BaseCall<MSolutionResp> call) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						MSolutionResp resp = new MSolutionResp();
						Log.d("Radar", "solutionUpdateAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setSolutionId(jsonObject3.optLong("id"));
							resp.setEffect((String[])(jsonObject3.optString("effect").split(",")));
							resp.setPart((String[])(jsonObject3.optString("part").split(",")));
							resp.setTitle(jsonObject3.optString("title"));
							resp.setIntroduction(jsonObject3.optString("introduction"));
							resp.setContent(jsonObject3.optString("content"));
							resp.setCoverImgUrl(jsonObject3.optString("coverUrl"));
							resp.setCoverThumbImgUrl(jsonObject3.optString("coverThumbnailUrl"));
							resp.setUseCycle(jsonObject3.optInt("cycle"));
							resp.setScore(jsonObject3.optDouble("score"));
							resp.setUsedCount(jsonObject3.optInt("usedCount"));
							resp.setStoreUpCount(jsonObject3.optInt("usestoreupCount"));
							resp.setCreateTime(jsonObject3.optLong("createTime"));
							resp.setUpdateTime(jsonObject3.optLong("updateTime"));
							resp.setUserId(jsonObject3.optString("userId"));
							resp.setNickName(jsonObject3.optString("username"));
							resp.setPortrait(jsonObject3.optString("portrait"));
							
						} catch (JSONException e) {
							e.printStackTrace();
							resp.setStatus("FAILED");
							Log.d("Radar", "JSONException: " + e);
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						MSolutionResp resp = new MSolutionResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				});
	}

	// 删除未发布成功/未更新成功的护肤方案
	public void solutionDeleteLocalItemAsync(long localSolutionId, BaseCall<BaseResp> call){};
	
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的护肤方案
	public void solutionDeleteAllLocalDataAsync(BaseCall<BaseResp> call){};
	

	private ServerRequestParams writeCoverImgParams(String imgs) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadImg(null));
		List<String> imgList = new ArrayList<String>();
		imgList.add(imgs);
		param.put("ImgFile", imgList);
		param.put("type", Integer.toString(8));
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeTextImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadImg(null));
		param.put("ImgFile", imgs);
		param.put("type", Integer.toString(7));
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}
	
	private ServerRequestParams writeCreateParams(SolutionCreateReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.createSolution(null));
		
		SolutionCreateServerReq newBean = new SolutionCreateServerReq();
		newBean.setTitle(bean.getTitle());
		newBean.setIntroduction(bean.getIntroduction());
		newBean.setContent(bean.getContent());
		newBean.setUseCycle(bean.getUseCycle());
		
		if(bean.getEffect() != null){
			String effect = "";
			for(int i = 0; i < bean.getEffect().length; i++)
			{   
				effect += bean.getEffect()[i];
				if (i < (bean.getEffect().length-1)) {
					effect+=",";
				}
			}
			newBean.setEffect(effect);
		}
		if(bean.getPart() != null){
			String part = "";
			for(int i = 0; i < bean.getPart().length; i++)
			{   
				part += bean.getPart()[i];
				if (i < (bean.getPart().length-1)) {
					part+=",";
				}
			}
			newBean.setPart(part);
		}
		
		newBean.setCoverUrl(bean.getCoverUrl());
		newBean.setCoverThumbUrl(bean.getCoverThumbUrl());
		
		params.setStatus(0);
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(newBean));
		return params;
	}

	private ServerRequestParams writeUpdateParams(SolutionUpdateReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.updateSolution(null));
		
		SolutionUpdateServerReq newBean = new SolutionUpdateServerReq();
		newBean.setTitle(bean.getTitle());
		newBean.setIntroduction(bean.getIntroduction());
		newBean.setContent(bean.getContent());
		newBean.setUseCycle(bean.getUseCycle());
		newBean.setSolutionId(bean.getSolutionId());
		
		if(bean.getEffect() != null){
			String effect = "";
			for(int i = 0; i < bean.getEffect().length; i++)
			{   
				effect += bean.getEffect()[i];
				if (i < (bean.getEffect().length-1)) {
					effect+=",";
				}
			}
			newBean.setEffect(effect);
		}
		if(bean.getPart() != null){
			String part = "";
			for(int i = 0; i < bean.getPart().length; i++)
			{   
				part += bean.getPart()[i];
				if (i < (bean.getPart().length-1)) {
					part+=",";
				}
			}
			newBean.setPart(part);
		}
		
		newBean.setCoverUrl(bean.getCoverUrl());
		newBean.setCoverThumbUrl(bean.getCoverThumbUrl());
		
		params.setStatus(0);
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(newBean));
		return params;
	}
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
	
}
