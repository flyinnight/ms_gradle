package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.MyTopicCallBack;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.Slog;
import com.google.gson.reflect.TypeToken;

public class MyTopicCallBackImpl extends MyTopicCallBack {
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private boolean reTry1;
	private boolean reTry2;
	private Context context;
	private ServerRequestParams params;

	public MyTopicCallBackImpl(Context context) {
		this.context = context;
		reTry1 = true;
		reTry2 = true;
	}

	
	//我发布的话题
	@Override
	public void getMyCreateTopicByTypeAsync(MMyTopicReq bean, final BaseCall<MMyTopicResp> call, int type) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MMyTopicResp) msg.obj);
					reTry1 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListBySendServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListBySendLocal(bean);
		}
	}
	
	public void getTopicListBySendServer(final MMyTopicReq bean) {

		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MMyTopicResp resp = new MMyTopicResp();
				List<MTopicResp> resList = null;
				Log.d("Radar", "getTopicListBySendServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));// true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("totalPage"));// ok
						resp.setPageNo(jsonObj.optInt("pageNo"));//SUCCESS
						
						Object temp2 = jsonObj.opt("topics");
						if (temp2 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("topics"));
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
								topicResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
								topicResp.setRegen(jsonObject3.optInt("postNum"));
								topicResp.setTopicimg(jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
								topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
								resList.add(topicResp);
							}
							resp.setDatas(resList);
							
							TopicListSave saveBean = new TopicListSave();
							saveBean.setTopicList(resList);
							saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_SEND);
							saveBean.setUpdateTime(System.currentTimeMillis());
							RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
							
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						} else {
							handleSendFailure(bean, statusCode);
						}
					} else {
						handleSendFailure(bean, statusCode);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					handleSendFailure(bean, statusCode);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getTopicListBySendLocal(bean);
				} else {
					MMyTopicResp resp = new MMyTopicResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	private void handleSendFailure(MMyTopicReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_SEND), null);
			MMyTopicResp resp = new MMyTopicResp();
			resp.setStatus("FAILED");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler3.sendMessage(msg);
		} else {
			if (reTry1) {
				reTry1 = false;
				getTopicListBySendLocal(bean);
			} else {
				MMyTopicResp resp = new MMyTopicResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
			}
		}
	}
	
	public void getTopicListBySendLocal(final MMyTopicReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_SEND), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListBySendLocal " + result);
				MMyTopicResp resp = new MMyTopicResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry1 && ((beanList == null) || (beanList.size() == 0))) {
					reTry1 = false;
					getTopicListBySendServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListBySendServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
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
						handler3.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getTopicListBySendServer(bean);
				} else {
					MMyTopicResp resp = new MMyTopicResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	
	//我关注的话题
	@Override
	public void getMyFollowTopicByTypeAsync(MMyTopicReq bean, final BaseCall<MMyFollowTopicResp> call, int type) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MMyFollowTopicResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListByFollowServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListByFollowLocal(bean);
		}
	}
	
	public void getTopicListByFollowServer(final MMyTopicReq bean) {
		RadarProxy.getInstance(context).startServerData(writeFollowParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				String status = "FAILED";
				MMyFollowTopicResp resp = new MMyFollowTopicResp();
				Log.d("Radar", "getTopicListByFollowServer: " + result);
				Slog.f("Filelog: getTopicListByFollowServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));// true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					status = jsonObject2.optString("status");
					
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("allPages"));// ok
						resp.setPageNo(jsonObj.optInt("currPage"));//SUCCESS
						resp.setType(jsonObj.optString("type"));//SUCCESS
						
						Object temp1 = jsonObj.opt("list");
						if (temp1 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("list"));
							List<MTopicResp> resList = new ArrayList<MTopicResp>();
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								MTopicResp topicResp = new MTopicResp();

								// 话题标题
								topicResp.setTopictitle(jsonObject3.optString("title"));// "Whattimetostickmasknight"
								topicResp.setTopicId(jsonObject3.optLong("id"));
								topicResp.setContent(jsonObject3.optString("desc"));
								topicResp.setUsername(jsonObject3.optString("name"));
								topicResp.setUserId(jsonObject3.optString("uid"));
								topicResp.setFollowup(jsonObject3.optBoolean("followup"));
								topicResp.setFollowsUpNum(jsonObject3.optInt("followsupNum"));
								topicResp.setRegen(jsonObject3.optInt("postNum"));
								//topicResp.setTopicimg(jsonObject3.optString("images").split(","));// "topic/icon/1432101339995/katong.jpg"
								Object temp2 = jsonObject3.opt("images");
								if (temp2 != null) {
									JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("images"));
									//List<String> imgList = new ArrayList<String>();
									String[] topicImg = new String[jsonArrImg.length()];
									for (int j = 0; j < jsonArrImg.length(); j++) {
										String imgItem = (String) jsonArrImg.get(j);
										//imgList.add(imgItem);
										topicImg[j] = imgItem;
									}
									//String[] topicImg = imgList.toArray(new String[imgList.size()]);
									topicResp.setTopicimg(topicImg);
								}
								topicResp.setReleasetime(jsonObject3.optLong("timestamp"));// 1432190979000
								resList.add(topicResp);
							}
							resp.setDatas(resList);
							
							TopicListSave saveBean = new TopicListSave();
							saveBean.setTopicList(resList);
							saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_FOLLOW);
							saveBean.setUpdateTime(System.currentTimeMillis());
							RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
							
							Message msg = Message.obtain();
							msg.obj = resp;
							handler4.sendMessage(msg);
						} else {
							handleFollowFailure(bean, statusCode, status);
						}
					} else {
						handleFollowFailure(bean, statusCode, status);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					handleFollowFailure(bean, statusCode, status);
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				Slog.f("Filelog: getTopicListByFollowServer onFailure: " + result);
				if (reTry2) {
					reTry2 = false;
					getTopicListByFollowLocal(bean);
				} else {
					MMyFollowTopicResp resp = new MMyFollowTopicResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	private void handleFollowFailure(MMyTopicReq bean, int statusCode, String status) {
		if (statusCode == BaseResp.OK) {
			RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_FOLLOW), null);
			MMyFollowTopicResp resp = new MMyFollowTopicResp();
			resp.setStatus(status);
			Message msg = Message.obtain();
			msg.obj = resp;
			handler4.sendMessage(msg);
		} else {
			if (reTry2) {
				reTry2 = false;
				getTopicListByFollowLocal(bean);
			} else {
				MMyFollowTopicResp resp = new MMyFollowTopicResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler4.sendMessage(msg);
			}
		}
	}
	
	public void getTopicListByFollowLocal(final MMyTopicReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_FOLLOW), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicListByFollowLocal " + result);
				MMyFollowTopicResp resp = new MMyFollowTopicResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getTopicListByFollowServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListByFollowServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setDatas(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.OK);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
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
				if (reTry2) {
					reTry2 = false;
					getTopicListByFollowServer(bean);
				} else {
					MMyFollowTopicResp resp = new MMyFollowTopicResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	
	//判断登录用户是否有关注的话题
	@Override
	public void hasFollowTopicAsync(final BaseCall<HasFollowTopicResp> call) {
		handler5 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((HasFollowTopicResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeHasParams(),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						int statusCode = BaseResp.OK;
						HasFollowTopicResp resp = new HasFollowTopicResp();
						Log.d("Radar", "hasFollowTopicAsync: " + result);
						Slog.f("Filelog: hasFollowTopicAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							statusCode = jsonObject.optInt("statusCode");
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setHasFollow(jsonObject3.optBoolean("has"));

						} catch (JSONException e) {
							e.printStackTrace();
							resp.setStatus("FAILED");
							Log.d("Radar", "JSONException: " + e);
						}
						
						if ((statusCode == BaseResp.TIME_OUT) || (statusCode == BaseResp.NET_ERROR)) {
							resp.setStatus("SUCCESS");
							resp.setHasFollow(true);
						}
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						HasFollowTopicResp resp = new HasFollowTopicResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}
				});
	}
	
	
	private ServerRequestParams writeParams(MMyTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.userTopicList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeFollowParams(MMyTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.userFollowTopicList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("type", "topic");
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		params.setRequestParam(param);
		Log.d("Radar", "writeFollowParams mytoken: " + HttpConstant.TOKEN);
		Slog.f("Filelog: writeFollowParams mytoken: " + HttpConstant.TOKEN);
		return params;
	}
	
	private ServerRequestParams writeHasParams() {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.hasFollowTopic(null));
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
