package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.PostListSave;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.domain.TopicListCallBack.TopicListSave;
import com.dilapp.radar.domain.UserPostTopicList;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.google.gson.reflect.TypeToken;

public class UserPostTopicListImpl extends UserPostTopicList {
	private Handler handler1;
	private Handler handler2;
	private boolean reTry1;
	private boolean reTry2;
	private Context context;
	private ServerRequestParams params;

	public UserPostTopicListImpl(Context context) {
		this.context = context;
		reTry1 = true;
		reTry2 = true;
	}


	// 获取他人所发帖子的列表
	@Override
	public void getUserCreatPostByTypeAsync(UserPostTopicReq bean, final BaseCall<UserPostResp> call, int type) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UserPostResp) msg.obj);
					reTry1 = true;
				}
			}
		};
		
		String userId = SharePreCacheHelper.getUserPostId(context);
		if ((type == GetPostList.GET_DATA_LOCAL) && (userId.equals(bean.getUserId()))) {
			getUserCreatPostLocal(bean);
		} else {
			getUserCreatPostServer(bean);
			if (!userId.equals(bean.getUserId())) {
				reTry1 = false;
				RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_USER_SEND), null);
			}
		}
	}
	
	public void getUserCreatPostServer(final UserPostTopicReq bean) {

		RadarProxy.getInstance(context).startServerData(writePostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				UserPostResp resp = new UserPostResp();
				Log.d("Radar", "getUserCreatPostServer: " + result);
				try {
					jsonObject = new JSONObject(result);

					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						//resp.setTotalPage(jsonObj.optInt("totalPage"));
						//resp.setPageNo(jsonObj.optInt("pageNo"));
						
						JSONArray jsonArr = new JSONArray(jsonObject2.optString("values"));
						List<MPostResp> resList = new ArrayList<MPostResp>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							MPostResp mPostResp = new MPostResp();
							mPostResp.setId(jsonObject4.optLong("id"));
							mPostResp.setPid(jsonObject4.optLong("parentId"));
							mPostResp.setTopicId(jsonObject4.optLong("topicId"));
							mPostResp.setTopicTitle(jsonObject4.optString("topicTitle"));
							mPostResp.setPostLevel(jsonObject4.optInt("postLevel"));
							mPostResp.setUserId(jsonObject4.optString("userId"));
							mPostResp.setPostTitle(jsonObject4.optString("postTitle"));
							mPostResp.setPostContent(jsonObject4.optString("postContent"));
							mPostResp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
							mPostResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
							mPostResp.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
							mPostResp.setEffect(jsonObject4.optString("effect"));
							mPostResp.setReport(jsonObject4.optBoolean("reported"));
							mPostResp.setOnTop(jsonObject4.optBoolean("onTop"));
							Object temp1 = jsonObject4.opt("thumbURL");
							if (temp1 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									if (imgItem.length() != 0) {
										imgList.add(imgItem);
									}
								}
								mPostResp.setThumbURL(imgList);
							}
							mPostResp.setLike(jsonObject4.optInt("favorite"));
							mPostResp.setDislike(jsonObject4.optInt("disfavorite"));
							mPostResp.setCreateTime(jsonObject4.optLong("createTime"));
							mPostResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mPostResp.setTotalFollows(jsonObject4.optInt("totalFollows"));
							mPostResp.setPostViewCount(jsonObject4.optInt("postViewCount"));
								
							resList.add(mPostResp);
						}
						resp.setDatas(resList);
							
						PostListSave saveBean = new PostListSave();
						saveBean.setPostList(resList);
						saveBean.setType(GetPostList.POST_LIST_BY_USER_SEND);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						SharePreCacheHelper.setUserPostId(context, bean.getUserId());
							
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_USER_SEND), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler1.sendMessage(msg);
						} else {
							if (reTry1) {
								reTry1 = false;
								getUserCreatPostLocal(bean);
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler1.sendMessage(msg);
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_USER_SEND), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						if (reTry1) {
							reTry1 = false;
							getUserCreatPostLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler1.sendMessage(msg);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getUserCreatPostLocal(bean);
				} else {
					UserPostResp resp = new UserPostResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	public void getUserCreatPostLocal(final UserPostTopicReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_USER_SEND), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getUserCreatPostLocal " + result);
				UserPostResp resp = new UserPostResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();

				if (reTry1 && ((beanList == null) || (beanList.size() == 0))) {
					reTry1 = false;
					getUserCreatPostServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getUserCreatPostServer(bean);
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
						handler1.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getUserCreatPostServer(bean);
				} else {
					UserPostResp resp = new UserPostResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}

	
	//他人发布的话题
	@Override
	public void getUserCreatTopicByTypeAsync(UserPostTopicReq bean, final BaseCall<UserTopicResp> call, int type) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UserTopicResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		String userId = SharePreCacheHelper.getUserTopicId(context);
		if ((type == GetPostList.GET_DATA_LOCAL) && (userId.equals(bean.getUserId()))) {
			getUserCreatTopicLocal(bean);
		} else {
			getUserCreatTopicServer(bean);
			if (!userId.equals(bean.getUserId())) {
				reTry2 = false;
				RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_USER_SEND), null);
			}
		}
	}
	
	public void getUserCreatTopicServer(final UserPostTopicReq bean) {

		RadarProxy.getInstance(context).startServerData(writeTopicParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				UserTopicResp resp = new UserTopicResp();
				int statusCode = BaseResp.OK;
				String status = "FAILED";
				List<MTopicResp> resList = null;
				Log.d("Radar", "getUserCreatTopicServer: " + result);
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
						//resp.setTotalPage(jsonObj.optInt("totalPage"));// ok
						//resp.setPageNo(jsonObj.optInt("pageNo"));//SUCCESS
						JSONArray jsonArr = new JSONArray(jsonObject2.optString("values"));
						resList = new ArrayList<MTopicResp>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
							MTopicResp topicResp = new MTopicResp();
							topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
							topicResp.setTopicId(jsonObject3.optLong("topicId"));
							topicResp.setContent(jsonObject3.optString("topicDesc"));
							//topicResp.setUsername(jsonObject3.optString("userName"));
							//topicResp.setUserId(jsonObject3.optString("userId"));
							//topicResp.setFollowup(jsonObject3.optBoolean("followup"));
							topicResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
							//topicResp.setRegen(jsonObject3.optInt("postNum"));
							topicResp.setTopicimg(jsonObject3.optString("topicIcon").split(","));// "topic/icon/1432101339995/katong.jpg"
							topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
							resList.add(topicResp);
						}
						resp.setDatas(resList);
							
						TopicListSave saveBean = new TopicListSave();
						saveBean.setTopicList(resList);
						saveBean.setType(TopicListCallBack.TOPIC_LIST_BY_USER_SEND);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						SharePreCacheHelper.setUserTopicId(context, bean.getUserId());
							
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_USER_SEND), null);
							resp.setStatus(status);
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						} else {
							if (reTry2) {
								reTry2 = false;
								getUserCreatTopicLocal(bean);
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
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_DELETE_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_USER_SEND), null);
						resp.setStatus(status);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (reTry2) {
							reTry2 = false;
							getUserCreatTopicLocal(bean);
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
					getUserCreatTopicLocal(bean);
				} else {
					UserTopicResp resp = new UserTopicResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	public void getUserCreatTopicLocal(final UserPostTopicReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_LIST_GET_ONE_BYTYPE, Integer.toString(TopicListCallBack.TOPIC_LIST_BY_USER_SEND), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getUserCreatTopicLocal " + result);
				UserTopicResp resp = new UserTopicResp();
				TopicListSave respSave = GsonUtil.getGson().fromJson(result, TopicListSave.class);
				List<MTopicResp> beanList = respSave.getTopicList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getUserCreatTopicServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getUserCreatTopicServer(bean);
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
						handler2.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getUserCreatTopicServer(bean);
				} else {
					UserTopicResp resp = new UserTopicResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams writePostParams(UserPostTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getPostSentByUser(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", bean.getUserId());
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeTopicParams(UserPostTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getTopicSentByUser(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", bean.getUserId());
		param.put("pageNo", Integer.toString(bean.getPageNo()));
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
