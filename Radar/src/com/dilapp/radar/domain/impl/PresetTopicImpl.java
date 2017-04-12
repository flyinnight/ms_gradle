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
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseReq;
import com.dilapp.radar.domain.CreateTopic.TopicReleaseResp;
import com.dilapp.radar.domain.PresetTopic;
import com.dilapp.radar.domain.MyPostList.MyCreatPostResp;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicReq;
import com.dilapp.radar.domain.MyTopicCallBack.MMyTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;
import com.google.gson.reflect.TypeToken;

public class PresetTopicImpl extends PresetTopic {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private boolean reTry1;
	private Context context;
	private ServerRequestParams params;

	public PresetTopicImpl(Context context) {
		this.context = context;
		reTry1 = true;
	}

	@Override
	public void uploadTopicImgAsync(List<String> imgs,
			final BaseCall<TopicReleaseResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicReleaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						TopicReleaseResp resp = new TopicReleaseResp();
						Log.d("Radar", "uploadTopicImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setTopicImgUrl(jsonObject3.optString("imageUrl"));// {"imageUrl":"topic/icon/1435390758441/666999.jpg"]}
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
						TopicReleaseResp resp = new TopicReleaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	@Override
	public void createPresetTopicAsync(TopicReleaseReq bean, final BaseCall<TopicReleaseResp> call) {
			handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicReleaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreatParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						TopicReleaseResp resp = new TopicReleaseResp();
						Log.d("Radar", "createPresetTopicAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setRoles(jsonObject3.optString("roles"));
							resp.setTopicId(jsonObject3.optLong("topicId"));
							resp.setUserId(jsonObject3.optString("userId"));
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
						TopicReleaseResp resp = new TopicReleaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	
	@Override
	public void getPresetTopicListByTypeAsync(BaseReq bean, final BaseCall<TopicListResp> call, int type) {
			handler3 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicListResp) msg.obj);
					reTry1 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getPresetTopicListServer();
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getPresetTopicListLocal();
		}
	}
	
	public void getPresetTopicListServer() {

		RadarProxy.getInstance(context).startServerData(writeGetParams(), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				TopicListResp resp = new TopicListResp();
				Log.d("Radar", "getPresetTopicListServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					//
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					
					JSONArray jsonArr = new JSONArray(jsonObj.optString("presetTopic"));
					List<MTopicResp> resList = null;
					resList = new ArrayList<MTopicResp>();

					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
						MTopicResp topicResp = new MTopicResp();
						
						//
						topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
						topicResp.setTopicId(jsonObject3.optLong("topicId"));
						topicResp.setContent(jsonObject3.optString("topicDes"));
						topicResp.setUsername(jsonObject3.optString("userName"));
						topicResp.setUserId(jsonObject3.optString("userId"));
						topicResp.setFollowup(jsonObject3.optBoolean("followup"));
						topicResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
						topicResp.setRegen(jsonObject3.optInt("postNum"));
						topicResp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
						topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
						resList.add(topicResp);
					}
					resp.setDatas(resList);
					
					TopicListSave saveBean = new TopicListSave();
					saveBean.setTopicList(resList);
					saveBean.setType(TopicListCallBack.TOPIC_LIST_PRESET);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_PRESET), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (reTry1) {
							reTry1 = false;
							getPresetTopicListLocal();
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getPresetTopicListLocal();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	public void getPresetTopicListLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_PRESET), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getPresetTopicListLocal " + result);
				TopicListResp resp = new TopicListResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry1 && ((beanList == null) || (beanList.size() == 0))) {
					reTry1 = false;
					getPresetTopicListServer();
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getPresetTopicListServer();
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
						handler3.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getPresetTopicListServer();
				} else {
					TopicListResp resp = new TopicListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadTopicImg(null));
		param.put("topicImgFile", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeCreatParams(TopicReleaseReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.createPresetTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicTitle", bean.getTopicTitle());
		param.put("topicDes", bean.getTopicDes());
		param.put("topicImgUrl", bean.getTopicImgUrl());
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeGetParams() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getPresetTopicList(null));
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
