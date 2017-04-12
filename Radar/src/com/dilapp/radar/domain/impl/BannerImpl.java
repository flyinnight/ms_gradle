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

import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class BannerImpl extends Banner {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private boolean reTry;
	private Context context;
	private ServerRequestParams params;

	public BannerImpl(Context context) {
		this.context = context;
		reTry = true;
	}

	//上传banner图片
	@Override
	public void uploadBannerImgAsync(List<String> imgs,
			final BaseCall<UploadBannerImgResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UploadBannerImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						UploadBannerImgResp resp = new UploadBannerImgResp();
						Log.d("Radar", "uploadBannerImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							//resp.setBannerImgURL(jsonObject3.optString("bannerImgURL"));
							Object temp = jsonObject3.opt("bannerImgURL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("bannerImgURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setBannerImgURL(imgList);
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
						UploadBannerImgResp resp = new UploadBannerImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	//添加banner
	@Override
	public void createBannerAsync(CreateBannerReq bean,
			final BaseCall<BaseResp> call) {
			handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "createBannerAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
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
	
	//删除banner
	@Override
	public void deleteBannerAsync(DeleteBannerReq bean,
			final BaseCall<BaseResp> call) {
			handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeDeleteParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "deleteBannerAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
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
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
		});
	}
	
	//banner顺序
	@Override
	public void updateBannerPriorityAsync(BannerPriorityReq bean,
			final BaseCall<BaseResp> call) {
			handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePriorityParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "updateBannerPriorityAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
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
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
						System.out.println(result);
					}
		});
	}
	

	//获取banner列表
	@Override
	public void getBannerListByTypeAsync(BaseReq bean, final BaseCall<GetBannerListResp> call, int type) {
			handler5 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetBannerListResp) msg.obj);
					reTry = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getBannerListServer();
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getBannerListLocal();
		}
	}
	
	public void getBannerListServer() {

		RadarProxy.getInstance(context).startServerData(writeGetParams(), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				GetBannerListResp resp = new GetBannerListResp();
				Log.d("Radar", "getBannerListServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
				
					JSONArray jsonArr = new JSONArray(jsonObj.optString("banners"));
					List<BannerResp> resList = new ArrayList<BannerResp>();

					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
						BannerResp bannerResp = new BannerResp();
					
						bannerResp.setPostId(jsonObject3.optLong("postId"));
						bannerResp.setTopicId(jsonObject3.optLong("topicId"));
						bannerResp.setSolutionId(jsonObject3.optLong("solutionId"));
						bannerResp.setSlogan(jsonObject3.optString("slogan"));
						//bannerResp.setBannerUrl(jsonObject3.optString("bannerUrl"));
						Object temp = jsonObject3.opt("bannerUrl");
						if (temp != null) {
							JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("bannerUrl"));
							List<String> imgList = new ArrayList<String>();
							for (int j = 0; j < jsonArrImg.length(); j++) {
								String imgItem = (String) jsonArrImg.get(j);
								imgList.add(imgItem);
							}
							bannerResp.setBannerUrl(imgList);
						}
						bannerResp.setPriority(jsonObject3.optInt("priority"));
						bannerResp.setUpdateTime(jsonObject3.optLong("updateTime"));
						bannerResp.setPostUpdateTime(jsonObject3.optLong("postUpdateTime"));
						resList.add(bannerResp);
					}
					resp.setDatas(resList);
					
					BannerCollectionSave saveBean = new BannerCollectionSave();
					saveBean.setBannerContent(resp);
					saveBean.setType(Banner.LIST_BY_BANNER);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_DELETE_ONE, Integer.toString(Banner.LIST_BY_BANNER), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					} else {
						if (reTry) {
							reTry = false;
							getBannerListLocal();
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler5.sendMessage(msg);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getBannerListLocal();
				} else {
					GetBannerListResp resp = new GetBannerListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	public void getBannerListLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_GET_ONE, Integer.toString(Banner.LIST_BY_BANNER), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getBannerListLocal " + result);
				GetBannerListResp resp = null;
				BannerCollectionSave beanSave = GsonUtil.getGson().fromJson(result, BannerCollectionSave.class);
				resp = beanSave.getBannerContent();
				long updateTime = beanSave.getUpdateTime();

				if (reTry && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry = false;
					getBannerListServer();
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getBannerListServer();
					} else {
						if ((resp == null)) {
							resp = new GetBannerListResp();
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getBannerListServer();
				} else {
					GetBannerListResp resp = new GetBannerListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadBannerImg(null));
		param.put("bannerImgFile", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}
	
	private ServerRequestParams writeCreateParams(CreateBannerReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.createBanner(null));
		params.setToken(HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(bean));
		return params;
	}
	
	private ServerRequestParams writeDeleteParams(DeleteBannerReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.deleteBanner(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("priority", Integer.toString(bean.getPriority()));
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writePriorityParams(BannerPriorityReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.updateBannerPriority(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("priority1", Integer.toString(bean.getPriority1()));
		param.put("priority2", Integer.toString(bean.getPriority2()));
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeGetParams() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getBannerList(null));
		params.setStatus(0);
		params.setRequestParam(null);
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
