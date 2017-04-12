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

import com.dilapp.radar.domain.AuthorizeRoles;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class AuthorizeRolesImpl extends AuthorizeRoles {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	public AuthorizeRolesImpl(Context context) {
		this.context = context;
	}

	//授权(取消)其它用户管理员权限
	@Override
	public void authorizeUserTopicPermissionAsync(AuthorizeTopicReq bean,
										 final BaseCall<AuthorizeTopicResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((AuthorizeTopicResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeTopicParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						AuthorizeTopicResp resp = new AuthorizeTopicResp();
						Log.d("Radar", "authorizeUserTopicPermissionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);

							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							Object temp = jsonObject3.opt("successList");
							if (temp != null) {
								JSONArray jsonArr = new JSONArray(jsonObject3.optString("successList"));
								List<String> successList = new ArrayList<String>();
								for (int i = 0; i < jsonArr.length(); i++) {
									String success = (String) jsonArr.get(i);
									successList.add(success);
								}
								resp.setSuccessList(successList);
							}
							
							Object temp2 = jsonObject3.opt("failedList");
							if (temp2 != null) {
								JSONArray jsonArr2 = new JSONArray(jsonObject3.optString("failedList"));
								List<String> failedList = new ArrayList<String>();
								for (int i = 0; i < jsonArr2.length(); i++) {
									String failed = (String) jsonArr2.get(i);
									failedList.add(failed);
								}
								resp.setFailedList(failedList);
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
						AuthorizeTopicResp resp = new AuthorizeTopicResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	//获取用户动态权限
	@Override
	public void updateRolesAsync(UpdateRolesReq bean,
										final BaseCall<UpdateRolesResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UpdateRolesResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeRolesParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						UpdateRolesResp resp = new UpdateRolesResp();
						Log.d("Radar", "updateRolesAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setUserId(jsonObject3.optString("userId"));
							Object temp = jsonObject3.opt("roles");
							if (temp != null) {
								JSONArray jsonArr = new JSONArray(jsonObject3.optString("roles"));
								List<String> roleList = new ArrayList<String>();
								for (int i = 0; i < jsonArr.length(); i++) {
									String role = (String) jsonArr.get(i);
									roleList.add(role);
								}
								resp.setRoles(roleList);
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
						UpdateRolesResp resp = new UpdateRolesResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	//从主页进入帖子时获得角色权限
	@Override
	public void updatePostRolesAsync(UpdateRolesReq bean,
										  final BaseCall<UpdateRolesResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UpdateRolesResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						UpdateRolesResp resp = new UpdateRolesResp();
						Log.d("Radar", "updatePostRolesAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setUserId(jsonObject3.optString("userId"));
							Object temp = jsonObject3.opt("roles");
							if (temp != null) {
								JSONArray jsonArr = new JSONArray(jsonObject3.optString("roles"));
								List<String> roleList = new ArrayList<String>();
								for (int i = 0; i < jsonArr.length(); i++) {
									String role = (String) jsonArr.get(i);
									roleList.add(role);
								}
								resp.setRoles(roleList);
							}

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
						UpdateRolesResp resp = new UpdateRolesResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}


	private ServerRequestParams writeTopicParams(AuthorizeTopicReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.authorizeUserTopicPermission(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("toUserIdList", bean.getToUserIdList());
		param.put("grant", Boolean.toString(bean.getGrant()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}

	private ServerRequestParams writeRolesParams(UpdateRolesReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.updateRoles(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}

	private ServerRequestParams writePostParams(UpdateRolesReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.updatePostRoles(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
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
