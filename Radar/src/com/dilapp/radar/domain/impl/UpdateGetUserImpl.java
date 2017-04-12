package com.dilapp.radar.domain.impl;

import java.util.Date;
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
import com.dilapp.radar.domain.Register.RegRadarReq;
import com.dilapp.radar.domain.UpdateGetUser;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class UpdateGetUserImpl extends UpdateGetUser {
	private Context context;
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private ServerRequestParams params;

	public UpdateGetUserImpl(Context context) {
		this.context = context;
	}

	//上传用户头像
	@Override
	public void uploadPortraitAsync(List<String> imgs, final BaseCall<UpdateUserResp> call) {

		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UpdateUserResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePortraitParams(imgs), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				UpdateUserResp resp = new UpdateUserResp();
				Log.d("Radar", "uploadPortraitAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setPortraitURL(jsonObject3.optString("portraitURL"));
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
				UpdateUserResp resp = new UpdateUserResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	//更新用户
	@Override
	public void updateUserAsync(RegRadarReq bean, final BaseCall<UpdateUserResp> call) {

		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UpdateUserResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdateParam(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				UpdateUserResp resp = new UpdateUserResp();
				Log.d("Radar", "updateUserAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setUserId(jsonObject3.optString("uid"));

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
				UpdateUserResp resp = new UpdateUserResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	//获取发言人
	@Override
	public void getUserAsync(GetUserReq bean, final BaseCall<GetUserResp> call){

		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetUserResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetParam(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				GetUserResp resp = new GetUserResp();
				Log.d("Radar", "getUserAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setUserId(jsonObject3.optString("userId"));
					resp.setName(jsonObject3.optString("name"));
					resp.setGender(jsonObject3.optInt("gender"));
					long birthday = jsonObject3.optLong("birthday");
					resp.setBirthday(new Date(birthday));
					resp.setDesc(jsonObject3.optString("desc"));
					resp.setLocation(jsonObject3.optString("location"));
					resp.setAddress(jsonObject3.optString("address"));
					resp.setFollowCount(jsonObject3.optInt("followCount"));
					resp.setFollowedCount(jsonObject3.optInt("followedCount"));
					resp.setFollowTopicCount(jsonObject3.optInt("followTopicCount"));
					resp.setTopicCount(jsonObject3.optInt("topicCount"));
					resp.setStoredPostCount(jsonObject3.optInt("storedPostCount"));
					resp.setOccupation(jsonObject3.optString("occupation"));
					resp.setPortrait(jsonObject3.optString("portrait"));
					resp.setSkinQuality(jsonObject3.optInt("skinQuality"));
					resp.setPreferChoseSkin(jsonObject3.optBoolean("preferChoseSkin"));
					resp.setSkinQualityCalculated(jsonObject3.optInt("skinQualityCalculated"));
					resp.setPublicPrivacy(jsonObject3.optBoolean("publicPrivacy"));
					resp.setLevel(jsonObject3.optInt("level"));
					resp.setQq(jsonObject3.optString("qq"));
					if (jsonObject3.optBoolean("validEmail")) {
						resp.setEmail(jsonObject3.optString("email"));
					}
					resp.setWechat(jsonObject3.optString("wechat"));
					resp.setBlog(jsonObject3.optString("blog"));
					resp.setPhone(jsonObject3.optString("phone"));
					resp.setLevelName(jsonObject3.optString("levelName"));
					resp.setId(jsonObject3.optLong("id"));
					resp.setFollowsUser(jsonObject3.optBoolean("isFollow"));
					resp.setEMUserId(jsonObject3.optString("msgUid"));

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
				GetUserResp resp = new GetUserResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	//根据环信Id获取发言人信息(可扩展为获取多人信息)
	@Override
	public void getUserByEMIdAsync(String EMUserId, final BaseCall<GetUserResp> call){

		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetUserResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetEMParam(EMUserId), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				GetUserResp resp = new GetUserResp();
				Log.d("Radar", "getUserByEMIdAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					JSONArray jsonArr = new JSONArray(jsonObject2.optString("values"));
					if (jsonArr.length() > 0) {
						JSONObject jsonObject3 = (JSONObject) jsonArr.get(0);
						resp.setUserId(jsonObject3.optString("userId"));
						resp.setName(jsonObject3.optString("name"));
						resp.setGender(jsonObject3.optInt("gender"));
						long birthday = jsonObject3.optLong("birthday");
						resp.setBirthday(new Date(birthday));
						resp.setPortrait(jsonObject3.optString("portrait"));
						resp.setSkinQuality(jsonObject3.optInt("skinQuality"));
						resp.setPreferChoseSkin(jsonObject3.optBoolean("preferChoseSkin"));
						resp.setPublicPrivacy(jsonObject3.optBoolean("publicPrivacy"));
						resp.setLevel(jsonObject3.optInt("level"));
						resp.setEmail(jsonObject3.optString("email"));
						resp.setPhone(jsonObject3.optString("phone"));
						resp.setId(jsonObject3.optLong("id"));
						resp.setFollowsUser(jsonObject3.optBoolean("followed"));
						resp.setEMUserId(jsonObject3.optString("msgUid"));
					}
					
					/*resp.setDesc(jsonObject3.optString("desc"));
					resp.setLocation(jsonObject3.optString("location"));
					resp.setAddress(jsonObject3.optString("address"));
					resp.setFollowCount(jsonObject3.optInt("followCount"));
					resp.setFollowedCount(jsonObject3.optInt("followedCount"));
					resp.setFollowTopicCount(jsonObject3.optInt("followTopicCount"));
					resp.setTopicCount(jsonObject3.optInt("topicCount"));
					resp.setStoredPostCount(jsonObject3.optInt("storedPostCount"));
					resp.setOccupation(jsonObject3.optString("occupation"));
					resp.setSkinQualityCalculated(jsonObject3.optInt("skinQualityCalculated"));
					resp.setQq(jsonObject3.optString("qq"));
					resp.setWechat(jsonObject3.optString("wechat"));
					resp.setBlog(jsonObject3.optString("blog"));
					resp.setLevelName(jsonObject3.optString("levelName"));
					resp.setSuccess(jsonObject.optBoolean("validEmail"));
					resp.setSuccess(jsonObject.optBoolean("validPhone"));*/

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
				System.out.println(result);
				GetUserResp resp = new GetUserResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler4.sendMessage(msg);
			}
		});
	}
	

	private ServerRequestParams writePortraitParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadPortrait(null));
		param.put("portrait", imgs);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeUpdateParam(RegRadarReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.updateUser(null));
		params.setStatus(0);
		params.setRequestParam(null);
		
		//radar系统不再上传email和phone，改为在user系统维护
		RegRadarReq newBean = new RegRadarReq();
		newBean.setName(bean.getName());
		newBean.setGender(bean.getGender());
		newBean.setBirthday(bean.getBirthday());
		newBean.setLocation(bean.getLocation());
		newBean.setAddress(bean.getAddress());
		newBean.setDesc(bean.getDesc());
		newBean.setOccupation(bean.getOccupation());
		newBean.setPortrait(bean.getPortrait());
		newBean.setSkinQuality(bean.getSkinQuality());
		newBean.setPreferChoseSkin(bean.getPreferChoseSkin());
		newBean.setPublicPrivacy(bean.getPublicPrivacy());
		newBean.setQq(bean.getQq());
		newBean.setBlog(bean.getBlog());
		newBean.setWechat(bean.getWechat());
		
		params.setRequestEntity(GsonUtil.getGson().toJson(newBean));
		return params;
	}

	private ServerRequestParams writeGetParam(GetUserReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getUser(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("uid", bean.getUserId());
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeGetEMParam(String EMUserId) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getUsersByMsgIdList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("msgIdList", EMUserId);
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
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
