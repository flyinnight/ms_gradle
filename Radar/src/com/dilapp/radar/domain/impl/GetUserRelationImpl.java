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

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetUserRelation;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class GetUserRelationImpl extends GetUserRelation {
	private Handler handler2;
	private Handler handler3;
	private boolean reTry2;
	private boolean reTry3;
	private Context context;
	private ServerRequestParams params;

	public GetUserRelationImpl(Context context) {
		this.context = context;
		reTry2 = true;
		reTry3 = true;
	}

	
	//获取他人的关注列表
	@Override
	public void getUserFollowsListByTypeAsync(getUserListReq bean, final BaseCall<getUserRelationResp> call, int type) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((getUserRelationResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getUserFollowsServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getUserFollowsLocal(bean);
		}
	}
	
	public void getUserFollowsServer(final getUserListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeFollowParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						int statusCode = BaseResp.OK;
						getUserRelationResp resp = new getUserRelationResp();
						Log.d("Radar", "getUserFollowsServer: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							statusCode = jsonObject.optInt("statusCode");
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setTotalPage(jsonObject3.optInt("totalPage"));
							resp.setPageNo(jsonObject3.optInt("currentPage"));
							Object temp = jsonObject3.opt("users");
							List<RelationList> rRelationList = new ArrayList<RelationList>();
							if (temp != null) {
								JSONArray jsonArr = new JSONArray(jsonObject3.optString("users"));
									
								for (int i = 0; i < jsonArr.length(); i++) {
									JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
									RelationList relation = null;
									relation = new RelationList();
									relation.setUserId(jsonObject4.optString("userId"));
									relation.setName(jsonObject4.optString("userName"));
									relation.setPortrait(jsonObject4.optString("portrait"));
									relation.setGender(jsonObject4.optInt("gender"));
									relation.setLevel(jsonObject4.optInt("level"));
									relation.setLevelName(jsonObject4.optString("levelName"));
									relation.setLocation(jsonObject4.optString("location"));
									relation.setFollowsUser(jsonObject4.optBoolean("isFollow"));
									relation.setFollowsCount(jsonObject4.optInt("followCount"));
									relation.setFansCount(jsonObject4.optInt("followedCount"));
									relation.setEMUserId(jsonObject4.optString("msgUid"));
									rRelationList.add(relation);
								}
								
								resp.setDatas(rRelationList);
								
								UserRelationSave saveBean = new UserRelationSave();
								saveBean.setUserId(bean.getUserId());
								saveBean.setType(GetUserRelation.USER_RELATION_BY_FOLLOWS);
								saveBean.setUserResp(resp);
								saveBean.setUpdateTime(System.currentTimeMillis());
								RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
								
								Message msg = Message.obtain();
								msg.obj = resp;
								handler2.sendMessage(msg);
							} else {
								handleUserFollowsFailure(bean, statusCode);
							}
								
						} catch (JSONException e) {
							e.printStackTrace();
							Log.d("Radar", "JSONException: " + e);
							handleUserFollowsFailure(bean, statusCode);
						}
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						if (reTry2) {
							reTry2 = false;
							getUserFollowsLocal(bean);
						} else {
							getUserRelationResp resp = new getUserRelationResp();
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						}
					}
			});
	}
	
	private void handleUserFollowsFailure(getUserListReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			UserRelationGetLocal deleteBean = new UserRelationGetLocal();
			deleteBean.setUserId(bean.getUserId());
			deleteBean.setType(GetUserRelation.USER_RELATION_BY_FOLLOWS);
			RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_DELETE_ONE, GsonUtil.getGson().toJson(deleteBean), null);
			
			getUserRelationResp resp = new getUserRelationResp();
			resp.setStatus("SUCCESS");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler2.sendMessage(msg);
		} else {
			if (reTry2) {
				reTry2 = false;
				getUserFollowsLocal(bean);
			} else {
				getUserRelationResp resp = new getUserRelationResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		}
	}
	
	public void getUserFollowsLocal(final getUserListReq bean) {

		UserRelationGetLocal getBean = new UserRelationGetLocal();
		getBean.setUserId(bean.getUserId());
		getBean.setType(GetUserRelation.USER_RELATION_BY_FOLLOWS);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_GET_ONE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getUserFollowsLocal " + result);
				getUserRelationResp resp = null;
				UserRelationSave beanSave = GsonUtil.getGson().fromJson(result, UserRelationSave.class);
				resp = beanSave.getUserResp();
				long updateTime = beanSave.getUpdateTime();

				if (reTry2 && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry2 = false;
					getUserFollowsServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getUserFollowsServer(bean);
					} else {
						if ((resp == null)) {
							resp = new getUserRelationResp();
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setTotalPage(1);
						resp.setPageNo(1);
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
					getUserFollowsServer(bean);
				} else {
					getUserRelationResp resp = new getUserRelationResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	
	//获取他人的粉丝列表
	@Override
	public void getUserFansListByTypeAsync(getUserListReq bean, final BaseCall<getUserRelationResp> call, int type) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((getUserRelationResp) msg.obj);
					reTry3 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getUserFansServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getUserFansLocal(bean);
		}
	}
	
	public void getUserFansServer(final getUserListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeFollowedParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						int statusCode = BaseResp.OK;
						getUserRelationResp resp = new getUserRelationResp();
						Log.d("Radar", "getUserFansServer: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							statusCode = jsonObject.optInt("statusCode");
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setTotalPage(jsonObject3.optInt("totalPage"));
							resp.setPageNo(jsonObject3.optInt("currentPage"));
							Object temp = jsonObject3.opt("users");
							List<RelationList> rRelationList = new ArrayList<RelationList>();
							if (temp != null) {
								JSONArray jsonArr = new JSONArray(jsonObject3.optString("users"));
									
								for (int i = 0; i < jsonArr.length(); i++) {
									JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
									RelationList relation = null;
									relation = new RelationList();
									relation.setUserId(jsonObject4.optString("userId"));
									relation.setName(jsonObject4.optString("userName"));
									relation.setPortrait(jsonObject4.optString("portrait"));
									relation.setGender(jsonObject4.optInt("gender"));
									relation.setLevel(jsonObject4.optInt("level"));
									relation.setLevelName(jsonObject4.optString("levelName"));
									relation.setLocation(jsonObject4.optString("location"));
									relation.setFollowsUser(jsonObject4.optBoolean("isFollow"));
									relation.setFollowsCount(jsonObject4.optInt("followCount"));
									relation.setFansCount(jsonObject4.optInt("followedCount"));
									relation.setEMUserId(jsonObject4.optString("msgUid"));
									rRelationList.add(relation);
								}
								
								resp.setDatas(rRelationList);
								
								UserRelationSave saveBean = new UserRelationSave();
								saveBean.setUserId(bean.getUserId());
								saveBean.setType(GetUserRelation.USER_RELATION_BY_FANS);
								saveBean.setUserResp(resp);
								saveBean.setUpdateTime(System.currentTimeMillis());
								RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
								
								Message msg = Message.obtain();
								msg.obj = resp;
								handler3.sendMessage(msg);
							} else {
								handleUserFansFailure(bean, statusCode);
							}
								
						} catch (JSONException e) {
							e.printStackTrace();
							Log.d("Radar", "JSONException: " + e);
							handleUserFansFailure(bean, statusCode);
						}
						
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						if (reTry3) {
							reTry3 = false;
							getUserFansLocal(bean);
						} else {
							getUserRelationResp resp = new getUserRelationResp();
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						}
					}
			});
	}
	
	private void handleUserFansFailure(getUserListReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			UserRelationGetLocal deleteBean = new UserRelationGetLocal();
			deleteBean.setUserId(bean.getUserId());
			deleteBean.setType(GetUserRelation.USER_RELATION_BY_FANS);
			RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_DELETE_ONE, GsonUtil.getGson().toJson(deleteBean), null);
			
			getUserRelationResp resp = new getUserRelationResp();
			resp.setStatus("SUCCESS");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler3.sendMessage(msg);
		} else {
			if (reTry3) {
				reTry3 = false;
				getUserFansLocal(bean);
			} else {
				getUserRelationResp resp = new getUserRelationResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
			}
		}
	}
	
	public void getUserFansLocal(final getUserListReq bean) {

		UserRelationGetLocal getBean = new UserRelationGetLocal();
		getBean.setUserId(bean.getUserId());
		getBean.setType(GetUserRelation.USER_RELATION_BY_FANS);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.USER_RELATION_GET_ONE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getUserFansLocal " + result);
				getUserRelationResp resp = null;
				UserRelationSave beanSave = GsonUtil.getGson().fromJson(result, UserRelationSave.class);
				resp = beanSave.getUserResp();
				long updateTime = beanSave.getUpdateTime();

				if (reTry3 && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry3 = false;
					getUserFansServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getUserFansServer(bean);
					} else {
						if ((resp == null)) {
							resp = new getUserRelationResp();
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setTotalPage(1);
						resp.setPageNo(1);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry3) {
					reTry3 = false;
					getUserFansServer(bean);
				} else {
					getUserRelationResp resp = new getUserRelationResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	

	private ServerRequestParams writeUserParams(getUserRelationReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getUserRelation(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("follow", Boolean.toString(bean.getFollow()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeFollowParams(getUserListReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getFollowList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("userId", bean.getUserId());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeFollowedParams(getUserListReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getFollowedList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("userId", bean.getUserId());
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
