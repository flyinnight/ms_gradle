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
import com.dilapp.radar.domain.FoundAllTopic;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class FoundAllTopicImpl extends FoundAllTopic {
	private Handler handler;
	private boolean reTry;
	private Context context;
	private ServerRequestParams params;

	public FoundAllTopicImpl(Context context) {
		this.context = context;
		reTry = true;
	}


	@Override
	public void getAllTopicByTypeAsync(AllTopicReq bean, final BaseCall<AllTopicResp> call, int type) {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((AllTopicResp) msg.obj);
					reTry = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicListAllServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getTopicListAllLocal(bean);
		}
	}

	public void getTopicListAllServer(final AllTopicReq bean) {

		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				AllTopicResp resp = new AllTopicResp();
				Log.d("Radar", "getAllTopicByTypeAsync getTopicListAllServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					resp.setTotalPage(jsonObj.optInt("totalPage"));// ok
					resp.setPageNo(jsonObj.optInt("pageNo"));//SUCCESS		
					
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
						topicResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
						topicResp.setRegen(jsonObject3.optInt("postNum"));
						topicResp.setTopicimg(jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
						topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
						resList.add(topicResp);
					}
					resp.setTopicResp(resList);
					
					TopicListSave saveBean = new TopicListSave();
					saveBean.setTopicList(resList);
					saveBean.setType(TopicListCallBack.TOPIC_LIST_ALL);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_ALL), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler.sendMessage(msg);
					} else {
						if (reTry) {
							reTry = false;
							getTopicListAllLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getTopicListAllLocal(bean);
				} else {
					AllTopicResp resp = new AllTopicResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler.sendMessage(msg);
				}
			}
		});
	}
	
	public void getTopicListAllLocal(final AllTopicReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_ALL), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getAllTopicByTypeAsync getTopicListAllLocal: " + result);
				AllTopicResp resp = new AllTopicResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry && ((beanList == null) || (beanList.size() == 0))) {
					reTry = false;
					getTopicListAllServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicListAllServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setTopicResp(beanList);
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
						handler.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getTopicListAllServer(bean);
				} else {
					AllTopicResp resp = new AllTopicResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler.sendMessage(msg);
				}
			}
		});
	}
	
	private ServerRequestParams writeParams(AllTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.topicList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("pageNo", Integer.toString(bean.getPageNo()));
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
