/*********************************************************************/
/*  文件名  LoginImpl.java    　                                        */
/*  程序名  登录域实现                     						     				     */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
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
import android.text.TextUtils;
import android.util.Log;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.Login;
import com.dilapp.radar.domain.server.User;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;

public class LoginImpl extends Login {
	private final String TAG = "LoginImpl";
	private Context context;
	private ServerRequestParams params;
	private Handler handler;

	public LoginImpl(Context context) {
		this.context = context;
	}


	@SuppressLint("HandlerLeak")
	@Override
	public void loginAsync(LoginReq paramBean, final BaseCall<LoginResp> call) {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((LoginResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeHttpParams(paramBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				LoginResp resp = new LoginResp();
				Log.d("Radar", "loginAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setSuccess(jsonObject.getBoolean("success"));
					resp.setStatusCode(jsonObject.getInt("statusCode"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));

					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					String token = jsonObject3.optString("token");
					if ((token != null) && (!token.equals(""))) {
						writeLocalContent(token);
					}
					
					if ("SUCCESS".equals(jsonObject2.optString("status"))) {
						//radar系统登录  登录成功后通知UI
						radarLogin();
					} else {
						Log.d("Radar", "loginAsync: failed");
					}
					Message msg = Message.obtain();
					msg.obj = resp;
					handler.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					resp.setStatus("FAILED");
					Log.d("Radar", "JSONException: " + e);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler.sendMessage(msg);
				}
			}

			@Override
			public void onFailure(String result) {
				LoginResp resp = new LoginResp();
				resp.setSuccess(false);
				Message msg = new Message();
				msg.obj = resp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}

	private void writeLocalContent(String token) {
		HttpConstant.TOKEN = token;
		//RadarProxy.getInstance(context).startLocalData(HttpConstant.getLoginUrl(null), token, null);
		SharePreCacheHelper.setUserToken(context, token);
		Log.d("Radar", "writeLocalContent mytoken: " + HttpConstant.TOKEN);
		Slog.f("Filelog: writeLocalContent mytoken: " + HttpConstant.TOKEN);
	}

	private ServerRequestParams writeHttpParams(LoginReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getLoginUrl(null));
		Map<String, Object> requestParam = new HashMap<String, Object>();
		requestParam.put("username", bean.getUsername());
		requestParam.put("password", bean.getPwd());
		params.setRequestParam(requestParam);
		params.setRequestEntity(null);
		return params;
	}

	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}

	private void radarLogin() {
		RadarProxy.getInstance(context).startServerData(writeRadarRegParam(), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObj;
				LoginResp resp = null;
				try {
					Log.d("Radar", "radarLogin: " + result);
					resp = new LoginResp();
					jsonObj = new JSONObject(result);
					
					resp.setSuccess(jsonObj.optBoolean("success"));
					resp.setStatusCode(jsonObj.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObj.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					if ("SUCCESS".equalsIgnoreCase(jsonObject2.optString("status"))) {
						JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
						
						resp.setUserId(jsonObject3.optString("userId"));
						resp.setPoint(jsonObject3.optInt("point"));
						resp.setLevel(jsonObject3.optInt("level"));
						SharePreCacheHelper.setLevel(context, jsonObject3.optInt("level"));
						//resp.setLevelName(jsonObject3.optString("levelName"));
						String UserId = jsonObject3.optString("userId");
						String EMUserId = jsonObject3.optString("msgUid");
						String userName = jsonObject3.optString("username");
						String portrait = jsonObject3.optString("portrait");
						if (!TextUtils.isEmpty(UserId)) {
							SharePreCacheHelper.setUserID(context, UserId);
						}
						if (!TextUtils.isEmpty(EMUserId)) {
							SharePreCacheHelper.setEMUserId(context, EMUserId);
						}
						if (!TextUtils.isEmpty(userName)) {
							SharePreCacheHelper.setNickName(context, userName);
						}
						if (!TextUtils.isEmpty(portrait)) {
							SharePreCacheHelper.setUserIconUrl(context, portrait);
						}
						
						JSONObject jsonObject4 = new JSONObject(jsonObject3.optString("roles"));
						resp.setRoles(jsonObject4.optString("staticRole"));
						SharePreCacheHelper.setUserRole(context, jsonObject4.optString("staticRole"));
						
						JSONObject jsonObjRole = new JSONObject(jsonObject4.optString("topicRole"));
						Object temp1 = jsonObjRole.opt("topic_owner");
						if (temp1 != null) {
							JSONArray jsonArr1 = new JSONArray(jsonObjRole.optString("topic_owner"));
							String topicOwner = "";
							
							for (int i = 0; i < jsonArr1.length(); i++) {
								Object tmp = (Object)jsonArr1.get(i);
								if(tmp instanceof Long) {
									topicOwner += Long.toString((Long)jsonArr1.get(i));
								} else if (tmp instanceof Integer) {
									topicOwner += Integer.toString((Integer)jsonArr1.get(i));
								}
								if (i < (jsonArr1.length()-1)) {
									topicOwner += ",";
								}
							}
							SharePreCacheHelper.setTopicOwnerList(context, topicOwner);
						}
						
						Object temp2 = jsonObjRole.opt("topic_admin");
						if (temp2 != null) {
							JSONArray jsonArr2 = new JSONArray(jsonObjRole.optString("topic_admin"));
							String topicAdmin = "";
							
							for (int i = 0; i < jsonArr2.length(); i++) {
								Object tmp = (Object)jsonArr2.get(i);
								if(tmp instanceof Long) {
									topicAdmin += Long.toString((Long)jsonArr2.get(i));
								} else if (tmp instanceof Integer) {
									topicAdmin += Integer.toString((Integer)jsonArr2.get(i));
								}
								if (i < (jsonArr2.length()-1)) {
									topicAdmin += ",";
								}
							}
							SharePreCacheHelper.setTopicAdminList(context, topicAdmin);
						}
						
						Object temp3 = jsonObjRole.opt("forbidden");
						if (temp3 != null) {
							JSONArray jsonArr3 = new JSONArray(jsonObjRole.optString("forbidden"));
							String topicForbidden = "";
							
							for (int i = 0; i < jsonArr3.length(); i++) {
								Object tmp = (Object)jsonArr3.get(i);
								if(tmp instanceof Long) {
									topicForbidden += Long.toString((Long)jsonArr3.get(i));
								} else if (tmp instanceof Integer) {
									topicForbidden += Integer.toString((Integer)jsonArr3.get(i));
								}
								if (i < (jsonArr3.length()-1)) {
									topicForbidden += ",";
								}
							}
							SharePreCacheHelper.setTopicForbiddenList(context, topicForbidden);
						}
						
						/*Object temp2 = jsonObject3.opt("actionMap");
						if (temp2 != null) {
							JSONArray jsonArrMap = new JSONArray(jsonObject3.optString("actionMap"));
							JSONArray jsonArraySave = new JSONArray();
							
							for (int i = 0; i < jsonArrMap.length(); i++) {
								JSONObject jsonObject = (JSONObject) jsonArrMap.get(i);
								String role = jsonObject.optString("role");
								String actionString = "";
								JSONArray jsonArrAction = new JSONArray(jsonObject.optString("action"));
								
								for (int j = 0; j < jsonArrAction.length(); j++) {
									String action = (String) jsonArrAction.get(j);
									
									actionString += action;
									if (j < (jsonArrAction.length()-1)) {
										actionString+=",";
									}
								}
								
								JSONObject objSave = new JSONObject();
								objSave.put(role, actionString);
								jsonArraySave.put(objSave);
							}
							SharePreCacheHelper.setActionMap(context, jsonArraySave.toString());
						}*/
						
						JSONObject jsonObject5 = new JSONObject(jsonObject3.optString("topics"));
						SharePreCacheHelper.setTopicIdAdv(context, jsonObject5.optLong("ad"));
						SharePreCacheHelper.setTopicIdDry(context, jsonObject5.optLong("dry"));
						SharePreCacheHelper.setTopicIdOil(context, jsonObject5.optLong("oil"));
						SharePreCacheHelper.setTopicIdMix(context, jsonObject5.optLong("mix"));
					}
					
/*					User user = new User();
					user.setPoint(jsonObject3.optString("point"));
					user.setLevel(jsonObject3.optString("level"));
					user.setToken(jsonObject3.optString("token"));
					user.setCreateTopic(jsonObject3.optString("createTopic"));
					user.setUserId(jsonObject3.optString("userId"));
					
					Object temp2 = jsonObject3.opt("actionMap");
					if (temp2 != null) {
						JSONArray jsonArrMap = new JSONArray(jsonObject3.optString("actionMap"));
						List<Map<String, List<String>>> actionMapList = new ArrayList<Map<String, List<String>>>();
						JSONArray jsonArraySave = new JSONArray();
						
						for (int i = 0; i < jsonArrMap.length(); i++) {
							JSONObject jsonObject = (JSONObject) jsonArrMap.get(i);
							String role = jsonObject.optString("role");
							String actionString = "";
							JSONArray jsonArrAction = new JSONArray(jsonObject.optString("action"));
							Map<String, List<String>> actionMapItem = new HashMap<String, List<String>>();
							
							List<String> actionList = new ArrayList<String>();
							for (int j = 0; j < jsonArrAction.length(); j++) {
								String action = (String) jsonArrAction.get(j);
								actionList.add(action);
								
								actionString += action;
								if (j < (jsonArrAction.length()-1)) {
									actionString+=",";
								}
							}
							
							JSONObject objSave = new JSONObject();
							objSave.put(role, actionString);
							jsonArraySave.put(objSave);
							
							actionMapItem.put(role, actionList);
							actionMapList.add(actionMapItem);
						}
						resp.setActionMap(actionMapList);
					}
					
					// 存储到权限表中 TODO
					jsonObject3.get("actionMap").toString();
					JSONArray jsonArray = new JSONArray(jsonObject3.get("actionMap").toString());
					JSONArray jsonArray2 = new JSONArray();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						String key = jsonObject.get("role").toString();
						String authString = jsonObject.get("action").toString();
						String[] authArray = authString.replace("\"", "").split(",");
						for (int j = 0; j < authArray.length; j++) {
							String value = authArray[j];
							JSONObject obj2 = new JSONObject();
							obj2.put(key, value);
							jsonArray2.put(obj2);
						}
					}
					
					//插入权限数据
					RadarProxy.getInstance(context).startLocalData(HttpConstant.AUTH_WRITE, jsonArray2.toString(), null);
					// 更新本地user信息表
					RadarProxy.getInstance(context).startLocalData(HttpConstant.LOGIN_WIRTE_ONE, GsonUtil.getGson().toJson(user), null);*/
				} catch (JSONException e) {
					e.printStackTrace();
					resp = new LoginResp();
					resp.setStatus("FAILED");
					//resp.setStatusCode(BaseResp.TIMEOUT);
					resp.setSuccess(false);
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				//handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				LoginResp resp = new LoginResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				//handler.sendMessage(msg);
				System.out.println(result);
			}
		});

	}

	private ServerRequestParams writeRadarRegParam() {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.getRadarLoginUrl(null));
		params.setRequestParam(null);
		params.setRequestEntity(null);
		return params;
	}
}
