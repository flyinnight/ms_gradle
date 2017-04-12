package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicResp;
import com.dilapp.radar.domain.GetPostList.TopicDetailGet;
import com.dilapp.radar.domain.GetPostList.TopicDetailSave;
import com.dilapp.radar.domain.Login.LoginResp;
import com.dilapp.radar.domain.PostOperation.TopPostReq;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.google.gson.reflect.TypeToken;

public class TopicListCallBackImpl extends TopicListCallBack {
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private boolean reTry2;
	private boolean reTry4;
	private boolean reTry5;
	private Context context;
	private ServerRequestParams params;

	public TopicListCallBackImpl(Context context) {
		this.context = context;
		reTry2 = true;
		reTry4 = true;
		reTry5 = true;
	}


	// 自动推荐话题列表请求
	@Override
	public void getRecommendTopicListByTypeAsync(BaseReq bean, final BaseCall<TopicListResp> call, int type) {
		handler2 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicListResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListRecommendAutoServer();
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListRecommendAutoLocal();
		}
	}
	
	public void getTopicListRecommendAutoServer() {

		RadarProxy.getInstance(context).startServerData(writeRecommendTopicParams(), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				TopicListResp resp = new TopicListResp();;
				Log.d("Radar", "getTopicListRecommendAutoServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					
					Object temp = jsonObj.opt("topics");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObj.optString("topics"));
						List<MTopicResp> resList = new ArrayList<MTopicResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
							MTopicResp topicResp = new MTopicResp();

							// 话题标题
							topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
							topicResp.setTopicId(jsonObject3.optLong("topicId"));
							topicResp.setContent(jsonObject3.optString("topicDes"));
							topicResp.setUsername(jsonObject3.optString("userName"));
							topicResp.setUserId(jsonObject3.optString("userId"));
							topicResp.setFollowup(jsonObject3.optBoolean("followup"));
							topicResp.setFollowsUpNum(jsonObject3.optInt("followsupNum"));
							topicResp.setRegen(jsonObject3.optInt("postNum"));
							topicResp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
							topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
							resList.add(topicResp);
						}
						resp.setDatas(resList);
						
						TopicListSave saveBean = new TopicListSave();
						saveBean.setTopicList(resList);
						saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_AUTO);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_AUTO), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						} else {
							if (reTry2) {
								reTry2 = false;
								getTopicListRecommendAutoLocal();
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler2.sendMessage(msg);
							}
						}
					}
					

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_AUTO), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (reTry2) {
							reTry2 = false;
							getTopicListRecommendAutoLocal();
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getTopicListRecommendAutoLocal();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	public void getTopicListRecommendAutoLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_AUTO), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListRecommendAutoLocal " + result);
				TopicListResp resp = new TopicListResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getTopicListRecommendAutoServer();
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListRecommendAutoServer();
					} else {
						resp.setDatas(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getTopicListRecommendAutoServer();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	
	/**
	 * 根据测试结果推荐话题
	 */	
	@Override
	public void getRecommendTopicListOfTestAsync(TopicsTestReq bean, final BaseCall<TopicListResp> call) {
		handler3 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicListResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeRecommendTopicTestParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				TopicListResp resp = new TopicListResp();
				int statusCode = BaseResp.OK;
				Log.d("Radar", "recommendTopicsTestAsync: " + result);
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
					
					Object temp = jsonObj.opt("topics");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObj.optString("topics"));
						List<MTopicResp> resList = new ArrayList<MTopicResp>();
						
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
							MTopicResp topicResp = new MTopicResp();
							
							// 话题标题
							topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
							topicResp.setTopicId(jsonObject3.optLong("topicId"));
							topicResp.setContent(jsonObject3.optString("topicDes"));
							topicResp.setUsername(jsonObject3.optString("userName"));
							topicResp.setUserId(jsonObject3.optString("userId"));
							topicResp.setFollowup(jsonObject3.optBoolean("followup"));
							topicResp.setFollowsUpNum(jsonObject3.optInt("followsupNum"));
							topicResp.setRegen(jsonObject3.optInt("postNum"));
							//topicResp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
							topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
							resList.add(topicResp);
						}
						resp.setDatas(resList);
						
						/*TopicListSave saveBean = new TopicListSave();
						saveBean.setTopicList(resList);
						saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_TEST);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);*/
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						
					} else {
						/*if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_TEST), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						} else {
							getTopicListRecommendTestLocal();
						}*/
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					/*if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_TEST), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						getTopicListRecommendTestLocal();
					}*/
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				TopicListResp resp = new TopicListResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
			}
		});
	}	

	public void getTopicListRecommendTestLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_RECOMMEND_TEST), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListRecommendTestLocal " + result);
				TopicListResp resp = new TopicListResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				
				resp.setDatas(beanList);
				resp.setSuccess(true);
				resp.setStatusCode(BaseResp.DATA_LOCAL);
				resp.setMessage("ok");
				resp.setStatus("SUCCESS");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				TopicListResp resp = new TopicListResp();
				resp.setStatus("FAILED");
				resp.setStatusCode(BaseResp.DATA_LOCAL);
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	
	// 获取单个话题/话题详情
	@Override
	public void getTopicDetailByTypeAsync(final TopicDetailReq bean, final BaseCall<MTopicResp> call, int type) {
		handler4 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MTopicResp) msg.obj);
					reTry4 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListSingleItemServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListSingleItemLocal(bean);
		}
	}
	
	public void getTopicListSingleItemServer(final TopicDetailReq bean) {

		RadarProxy.getInstance(context).startServerData(writeTopicDetailParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				MTopicResp resp = new MTopicResp();
				int statusCode = BaseResp.OK;
				Log.d("Radar", "getTopicListSingleItemServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					
					// 话题标题
					resp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
					resp.setTopicId(jsonObject3.optLong("topicId"));
					resp.setContent(jsonObject3.optString("topicDes"));
					resp.setUsername(jsonObject3.optString("userName"));
					resp.setUserId(jsonObject3.optString("userId"));
					resp.setFollowup(jsonObject3.optBoolean("followup"));
					resp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
					resp.setRegen(jsonObject3.optInt("postNum"));
					resp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
					resp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
					
					String role = "";
					Object temp1 = jsonObject3.opt("roles");
					if (temp1 != null) {
						JSONArray jsonArr1 = new JSONArray(jsonObject3.optString("roles"));
						for (int i = 0; i < jsonArr1.length(); i++) {
							role = (String)jsonArr1.get(0);
						}
					}
					
					String sTopicId = jsonObject3.optString("topicId");
					if (role.equalsIgnoreCase("topic_owner")) {
						boolean addId = true;
				    	String topicOwner = SharePreCacheHelper.getTopicOwnerList(context);
				    	if (topicOwner != null) {
				    		String[] topicOwnerList = topicOwner.split(",");
					    	for (int i = 0; i<topicOwnerList.length; i++) {
								if (sTopicId.equals(topicOwnerList[i])) {
									addId = false;
									break;
								}
							}
				    	}
				    	
				    	if (addId) {
				    		topicOwner += ",";
				    		topicOwner += sTopicId;
				    		SharePreCacheHelper.setTopicOwnerList(context, topicOwner);
				    	}
				    	
					} else if (role.equalsIgnoreCase("topic_admin")) {
						boolean addId = true;
				    	String topicAdmin = SharePreCacheHelper.getTopicAdminList(context);
				    	if (topicAdmin != null) {
				    		String[] topicAdminList = topicAdmin.split(",");
					    	for (int i = 0; i < topicAdminList.length; i++) {
								if (sTopicId.equals(topicAdminList[i])) {
									addId = false;
									break;
								}
							}
				    	}
				    	
				    	if (addId) {
				    		topicAdmin += ",";
				    		topicAdmin += sTopicId;
				    		SharePreCacheHelper.setTopicAdminList(context, topicAdmin);
				    	}
				    	
					} else if (role.equalsIgnoreCase("forbidden")) {
						boolean addId = true;
				    	String topicForbidden = SharePreCacheHelper.getTopicForbiddenList(context);
				    	if (topicForbidden != null) {
				    		String[] topicForbiddenList = topicForbidden.split(",");
					    	for (int i = 0; i < topicForbiddenList.length; i++) {
								if (sTopicId.equals(topicForbiddenList[i])) {
									addId = false;
									break;
								}
							}
				    	}
				    	
				    	if (addId) {
				    		topicForbidden += ",";
				    		topicForbidden += sTopicId;
				    		SharePreCacheHelper.setTopicForbiddenList(context, topicForbidden);
				    	}
					}
					
					TopicDetailSave saveBean = new TopicDetailSave();;
					saveBean.setTopicContent(resp);
					saveBean.setTopicId(bean.getTopicId());
					saveBean.setType(TopicListCallBack.TOPIC_DETAIL_CONTENT);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						TopicDetailGet getBean = new TopicDetailGet();
						getBean.setTopicId(bean.getTopicId());
						getBean.setType(TopicListCallBack.TOPIC_DETAIL_CONTENT);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_DELETE_ONE, GsonUtil.getGson().toJson(getBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					} else {
						if (reTry4) {
							reTry4 = false;
							getTopicListSingleItemLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler4.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry4) {
					reTry4 = false;
					getTopicListSingleItemLocal(bean);
				} else {
					MTopicResp resp = new MTopicResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	public void getTopicListSingleItemLocal(final TopicDetailReq bean) {

		TopicDetailGet getBean = new TopicDetailGet();
		getBean.setTopicId(bean.getTopicId());
		getBean.setType(TopicListCallBack.TOPIC_DETAIL_CONTENT);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_GET_ONE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListSingleItemLocal " + result);
				TopicDetailSave respSave = GsonUtil.getGson().fromJson(result, TopicDetailSave.class);
				MTopicResp resp = respSave.getTopicContent();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry4 && ((resp == null) || (!resp.isRequestSuccess()))) {
					reTry4 = false;
					getTopicListSingleItemServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListSingleItemServer(bean);
					} else {
						if (resp == null) {
							resp = new MTopicResp();
							resp.setStatus("FAILED");
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry4) {
					reTry4 = false;
					getTopicListSingleItemServer(bean);
				} else {
					MTopicResp resp = new MTopicResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	
	// 用户最关注话题列表
	@Override
	public void getTopFavourTopicListByTypeAsync(BaseReq bean, final BaseCall<TopicListResp> call, int type) {
		handler5 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicListResp) msg.obj);
					reTry5 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListTopFavourServer();
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListTopFavourLocal();
		}
	}
	
	public void getTopicListTopFavourServer() {

		RadarProxy.getInstance(context).startServerData(writeTopTopicParams(), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				TopicListResp resp = new TopicListResp();
				int statusCode = BaseResp.OK;
				Log.d("Radar", "getTopicListTopFavourServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					JSONArray jsonArr = new JSONArray(jsonObj.optString("topics"));
					List<MTopicResp> resList = null;
					resList = new ArrayList<MTopicResp>();

					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
						MTopicResp topicResp = new MTopicResp();
						
						// 话题标题
						topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
						topicResp.setTopicId(jsonObject3.optLong("topicId"));
						topicResp.setContent(jsonObject3.optString("topicDes"));
						topicResp.setUsername(jsonObject3.optString("userName"));
						topicResp.setUserId(jsonObject3.optString("userId"));
						topicResp.setFollowup(jsonObject3.optBoolean("followup"));
						topicResp.setFollowsUpNum(jsonObject3.optInt("followsupNum"));
						topicResp.setRegen(jsonObject3.optInt("postNum"));
						topicResp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
						topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
						resList.add(topicResp);
					}
					resp.setDatas(resList);
					
					TopicListSave saveBean = new TopicListSave();
					saveBean.setTopicList(resList);
					saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_TOPFAVOUR);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_TOPFAVOUR), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					} else {
						if (reTry5) {
							reTry5 = false;
							getTopicListTopFavourLocal();
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
				if (reTry5) {
					reTry5 = false;
					getTopicListTopFavourLocal();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	public void getTopicListTopFavourLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_TOPFAVOUR), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListTopFavourLocal " + result);
				TopicListResp resp = new TopicListResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry5 && ((beanList == null) || (beanList.size() == 0))) {
					reTry5 = false;
					getTopicListTopFavourServer();
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListTopFavourServer();
					} else {
						resp.setDatas(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry5) {
					reTry5 = false;
					getTopicListTopFavourServer();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}


	private ServerRequestParams writeFixedTopicParams() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getFixedTopicList(null));
		params.setRequestParam(null);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeRecommendTopicParams() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getRecommendTopics(null));
		params.setRequestParam(null);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeRecommendTopicTestParams(TopicsTestReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.recommendTopicsTest(null));
		Map<String, Object> param = new HashMap<String, Object>();
		String[] tmp = bean.getTopicParam();  
		String topic="";
		for(int i = 0; i < tmp.length; i++){
			topic += tmp[i];
			if (i < (tmp.length-1)) {
				topic+=",";
			}
		}
		param.put("topicParam", topic);
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeTopicDetailParams(TopicDetailReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeTopTopicParams() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.topFavourTopicList(null));
		params.setRequestParam(null);
		params.setRequestEntity(null);
		return params;
	}
}
