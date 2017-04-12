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
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.TopicGroups;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class TopicGroupsImpl extends TopicGroups {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Context context;
	private ServerRequestParams params;

	public TopicGroupsImpl(Context context) {
		this.context = context;
	}

	//创建用户话题分组
	@Override
	public void createTopicGroupsAsync(CreateGroupsReq bean,
			final BaseCall<CreateUpdateGroupsResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((CreateUpdateGroupsResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						CreateUpdateGroupsResp resp = new CreateUpdateGroupsResp();
						Log.d("Radar", "createTopicGroupsAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
							JSONArray jsonArr = new JSONArray(jsonObj.optString("groups"));
							List<GroupsList> groupLists = new ArrayList<GroupsList>();
							
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								GroupsList group = new GroupsList();
								
								group.setGroupId(jsonObj.optLong("groupId"));
								group.setGroupName(jsonObject3.optString("groupName"));
								groupLists.add(group);
							
							}
							resp.setUpTopicGroups(groupLists);
							
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
						CreateUpdateGroupsResp resp = new CreateUpdateGroupsResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	//更新用户话题分组
	@Override
	public void updateTopicGroupsAsync(UpdateGroupsReq bean,
			final BaseCall<CreateUpdateGroupsResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((CreateUpdateGroupsResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdateParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						CreateUpdateGroupsResp resp = new CreateUpdateGroupsResp();
						Log.d("Radar", "updateTopicGroupsAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
							JSONArray jsonArr = new JSONArray(jsonObj.optString("groups"));
							List<GroupsList> groupLists = new ArrayList<GroupsList>();
							
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								GroupsList group = new GroupsList();
								
								group.setGroupId(jsonObj.optLong("groupId"));
								group.setGroupName(jsonObject3.optString("groupName"));
								groupLists.add(group);
							
							}
							resp.setUpTopicGroups(groupLists);
							
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
						CreateUpdateGroupsResp resp = new CreateUpdateGroupsResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	//显示用户话题分组
	@Override
	public void getTopicGroupsAsync(BaseReq bean,
			final BaseCall<GetGroupsResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetGroupsResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						GetGroupsResp resp = new GetGroupsResp();
						Log.d("Radar", "getTopicGroupsAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
							JSONArray jsonArr = new JSONArray(jsonObj.optString("groups"));
							List<GetGroupsList> groupLists = new ArrayList<GetGroupsList>();
							
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								GetGroupsList group = new GetGroupsList();
								
								group.setGroupId(jsonObj.optLong("groupId"));
								group.setGroupName(jsonObject3.optString("groupName"));
								
								JSONArray jsonArr2 = new JSONArray(jsonObj.optString("topicIds"));
								List<Integer> topicIds = new ArrayList<Integer>();
								for (int j = 0; j < jsonArr2.length(); j++) {
									Integer id = (Integer) jsonArr2.get(j);

									topicIds.add(id);
								}
								group.setTopicIds(topicIds);
								groupLists.add(group);
							}
							resp.setUpTopicGroups(groupLists);
							
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
						GetGroupsResp resp = new GetGroupsResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	//点击话题分组进入帖子列表
	@Override
	public void getUserTopicGroupPostFlowAsync(GetGroupsPostReq bean,
			final BaseCall<GetGroupsPostResp> call) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetGroupsPostResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetPostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						GetGroupsPostResp resp = new GetGroupsPostResp();
						Log.d("Radar", "getUserTopicGroupPostFlowAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							// http return message
							resp.setSuccess(jsonObject.optBoolean("success"));
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							// 后台返回
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));
							resp.setStatus(jsonObject2.optString("status"));
							JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
							resp.setTotalPage(jsonObj.optInt("totalPage"));
							resp.setPageNo(jsonObj.optInt("pageNo"));
							JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
							
							 List<MPostResp> resList = new ArrayList<MPostResp>();
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								MPostResp postResp = new MPostResp();
								
								postResp.setTopicId(jsonObj.optLong("topicId"));
								postResp.setId(jsonObject3.optLong("id"));
								postResp.setPid(jsonObject3.optLong("pid"));
								postResp.setTopicId(jsonObject3.optLong("topicId"));
								//postResp.setTopicTitle(jsonObject3.optString("topicTitle"));
								postResp.setPostLevel(jsonObject3.optInt("postLevel"));
								postResp.setUserId(jsonObject3.optString("userId"));
								postResp.setUserName(jsonObject3.optString("userName"));
								postResp.setToUserId(jsonObject3.optString("toUserId"));
								postResp.setToUserName(jsonObject3.optString("toUserName"));
								postResp.setPostTitle(jsonObject3.optString("postTitle"));
								postResp.setPostContent(jsonObject3.optString("postContent"));
								postResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
								postResp.setStoreupNum(jsonObject3.optInt("storeupNum"));
								postResp.setSelectedToSolution(jsonObject3.optBoolean("selectedToSolution"));
								postResp.setEffect(jsonObject3.optString("effect"));
								postResp.setReport(jsonObject3.optBoolean("report"));
								postResp.setOnTop(jsonObject3.optBoolean("onTop"));
								//resp.setThumbURL((String[])(jsonObject3.optString("thumbURL").split(",")));
								Object temp = jsonObject3.opt("thumbURL");
								if (temp != null) {
									JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("thumbURL"));
									List<String> imgList = new ArrayList<String>();
									for (int j = 0; j < jsonArrImg.length(); j++) {
										String imgItem = (String) jsonArrImg.get(j);
										imgList.add(imgItem);
									}
									postResp.setThumbURL(imgList);
								}
								postResp.setLike(jsonObject3.optInt("favorite"));
								postResp.setDislike(jsonObject3.optInt("disfavorite"));
								postResp.setUpdateTime(jsonObject3.optLong("updateTime"));
								postResp.setCreateTime(jsonObject3.optLong("createTime"));
								postResp.setPostViewCount(jsonObject3.optInt("postViewCount"));
								postResp.setTotalFollows(jsonObject3.optInt("totalFollows"));
								postResp.setLevel(jsonObject3.optInt("level"));
								postResp.setLevelName(jsonObject3.optString("levelName"));
								postResp.setGender(jsonObject3.optInt("gender"));
								postResp.setBirthday(jsonObject3.optString("birthday"));
								postResp.setDesc(jsonObject3.optString("desc"));
								postResp.setOccupation(jsonObject3.optString("occupation"));
								postResp.setUserHeadIcon(jsonObject3.optString("portrait"));
								postResp.setSkinQuality(jsonObject3.optString("skinQuality"));
								postResp.setQq(jsonObject3.optString("qq"));
								postResp.setEmail(jsonObject3.optString("email"));
								postResp.setWechat(jsonObject3.optString("wechat"));
								postResp.setBlog(jsonObject3.optString("blog"));
								resList.add(postResp);
							}
							resp.setPostLists(resList);
							
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
						GetGroupsPostResp resp = new GetGroupsPostResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	

	private ServerRequestParams writeCreateParams(CreateGroupsReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.createTopicGroups(null));
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(bean.getCreateGroups()));
		return params;
	}

	private ServerRequestParams writeUpdateParams(UpdateGroupsReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.updateTopicGroups(null));
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(bean.getUpdateGroups()));
		return params;
	}
	
	private ServerRequestParams writeGetParams(BaseReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getTopicGroups(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeGetPostParams(GetGroupsPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getUserTopicGroupPostFlow(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("groupId", Long.toString(bean.getGroupId()));
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
