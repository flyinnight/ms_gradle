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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;

public class PostReleaseCallBackAsyncImpl extends PostReleaseCallBack {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	
	public PostReleaseCallBackAsyncImpl(Context context) {
		this.context = context;
	}

	@Override
	public void uploadPostImgAsync(List<String> imgs,
			final BaseCall<MPostImgResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MPostImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						MPostImgResp resp = new MPostImgResp();
						Log.d("Radar", "uploadPostImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							
							Object temp = jsonObject3.opt("postImgURL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("postImgURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setPostImgURL(imgList);
							}
							//resp.setStatus((String)jsonObject2.get("ok"));//true

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
						MPostImgResp resp = new MPostImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	@Override
	public void createPostAsync(PostReleaseReq bean,
			final BaseCall<MPostResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MPostResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreatPostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						MPostResp resp = new MPostResp();
						Log.d("Radar", "createPostAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							JSONObject jsonObject4 = new JSONObject(jsonObject3.optString("post"));
							resp.setId(jsonObject4.optLong("id"));
							resp.setPid(jsonObject4.optLong("parentId"));
							resp.setTopicId(jsonObject4.optLong("topicId"));
							resp.setPostLevel(jsonObject4.optInt("postLevel"));
							resp.setUserId(jsonObject4.optString("userId"));
							resp.setUserName(jsonObject4.optString("username"));
							resp.setToUserId(jsonObject4.optString("toUserId"));
							resp.setToUserName(jsonObject4.optString("toUserName"));
							resp.setPostTitle(jsonObject4.optString("postTitle"));
							resp.setPostContent(jsonObject4.optString("postContent"));
							resp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
							resp.setStoreupNum(jsonObject4.optInt("storeupNum"));
							resp.setPostViewCount(jsonObject4.optInt("postViewCount"));
							resp.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
							resp.setEffect(jsonObject4.optString("effect"));
							resp.setPart(jsonObject4.optString("part"));
							resp.setReport(jsonObject4.optBoolean("reported"));
							resp.setOnTop(jsonObject4.optBoolean("onTop"));
							//resp.setThumbURL((String[])(jsonObject4.optString("thumbURL").split(",")));
							Object temp = jsonObject4.opt("thumbURL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setThumbURL(imgList);
							}
							resp.setLike(jsonObject4.optInt("favorite"));
							resp.setDislike(jsonObject4.optInt("disfavorite"));
							resp.setCreateTime(jsonObject4.optLong("createTime"));
							resp.setUpdateTime(jsonObject4.optLong("updateTime"));
							resp.setLevel(jsonObject4.optInt("level"));
							resp.setLevelName(jsonObject4.optString("levelName"));
							resp.setGender(jsonObject4.optInt("gender"));
							resp.setBirthday(jsonObject4.optString("birthday"));
							resp.setDesc(jsonObject4.optString("desc"));
							resp.setOccupation(jsonObject4.optString("occupation"));
							resp.setUserHeadIcon(jsonObject4.optString("portrait"));
							resp.setSkinQuality(jsonObject4.optString("skin"));
							resp.setQq(jsonObject4.optString("qq"));
							resp.setEmail(jsonObject4.optString("email"));
							resp.setWechat(jsonObject4.optString("wechat"));
							resp.setBlog(jsonObject4.optString("blog"));

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
						MPostResp resp = new MPostResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	@Override
	public void updatePostAsync(PostReleaseReq bean,
			final BaseCall<MPostResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MPostResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdatePostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						MPostResp resp = new MPostResp();
						Log.d("Radar", "updatePostAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							JSONObject jsonObject4 = new JSONObject(jsonObject3.optString("post"));
							resp.setId(jsonObject4.optLong("id"));
							resp.setPid(jsonObject4.optLong("pid"));
							resp.setTopicId(jsonObject4.optLong("topicId"));
							resp.setPostLevel(jsonObject4.optInt("postLevel"));
							resp.setUserId(jsonObject4.optString("userId"));
							resp.setUserName(jsonObject4.optString("userName"));
							resp.setToUserId(jsonObject4.optString("toUserId"));
							resp.setToUserName(jsonObject4.optString("toUserName"));
							resp.setPostTitle(jsonObject4.optString("postTitle"));
							resp.setPostContent(jsonObject4.optString("postContent"));
							resp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
							resp.setStoreupNum(jsonObject4.optInt("storeupNum"));
							resp.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
							resp.setEffect(jsonObject4.optString("effect"));
							resp.setReport(jsonObject4.optBoolean("report"));
							resp.setOnTop(jsonObject4.optBoolean("onTop"));
							//resp.setThumbURL((String[])(jsonObject4.optString("thumbURL").split(",")));
							Object temp = jsonObject4.opt("thumbURL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setThumbURL(imgList);
							}
							resp.setLike(jsonObject4.optInt("favorite"));
							resp.setDislike(jsonObject4.optInt("disfavorite"));
							resp.setUpdateTime(jsonObject4.optLong("updateTime"));
							resp.setLevel(jsonObject4.optInt("level"));
							resp.setGender(jsonObject4.optInt("gender"));
							resp.setBirthday(jsonObject4.optString("birthday"));
							resp.setDesc(jsonObject4.optString("desc"));
							resp.setOccupation(jsonObject4.optString("occupation"));
							resp.setUserHeadIcon(jsonObject4.optString("portrait"));
							resp.setSkinQuality(jsonObject4.optString("skinQuality"));
							resp.setQq(jsonObject4.optString("qq"));
							resp.setEmail(jsonObject4.optString("email"));
							resp.setWechat(jsonObject4.optString("wechat"));
							resp.setBlog(jsonObject4.optString("blog"));

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
						MPostResp resp = new MPostResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	
	// 删除未发送/更新成功的贴子
	@Override
	public void deleteLocalPostAsync(final PostReleaseReq bean, final BaseCall<MPostResp> call) {}
	
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的贴子
	@Override
	public void deleteAllSendingPostAsync(final BaseReq bean, final BaseCall<BaseResp> call) {}
	
	
	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadPostImg(null));
		param.put("postImgFile", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeCreatPostParams(PostReleaseReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.createPost(null));
		
		PostReleaseReqString newBean = new PostReleaseReqString();
		newBean.setTopicId(bean.getTopicId());
		newBean.setPostTitle(bean.getPostTitle());
		newBean.setPostContent(bean.getPostContent());
		newBean.setParentId(bean.getParentId());
		newBean.setPostLevel(bean.getPostLevel());
		newBean.setToUserId(bean.getToUserId());
		//newBean.setSelectedToSolution(bean.getSelectedToSolution());
		//newBean.setEffect(bean.getEffect());
		//newBean.setPart(bean.getPart());
		newBean.setSkin(bean.getSkin());
		
		if(bean.getThumbURL() != null){
			String ImgURL = "";
			for(int i = 0; i < bean.getThumbURL().size(); i++)
			{   
				ImgURL+=bean.getThumbURL().get(i);
				if (i < (bean.getThumbURL().size()-1)) {
					ImgURL+=",";
				}
			}
			newBean.setThumbURL(ImgURL);
		}
		
		params.setStatus(0);
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(newBean));
		return params;
	}

	private ServerRequestParams writeUpdatePostParams(PostReleaseReq bean) {
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.updatePost(null));
		
		UpdatePostReqString newBean = new UpdatePostReqString();
		newBean.setTopicId(bean.getTopicId());
		newBean.setPostId(bean.getPostId());
		newBean.setPostTitle(bean.getPostTitle());
		newBean.setPostContent(bean.getPostContent());
		newBean.setParentId(bean.getParentId());
		newBean.setPostLevel(bean.getPostLevel());
		newBean.setToUserId(bean.getToUserId());
		//newBean.setSelectedToSolution(bean.getSelectedToSolution());
		//newBean.setEffect(bean.getEffect());
		//newBean.setPart(bean.getPart());
		newBean.setSkin(bean.getSkin());
		
		if(bean.getThumbURL() != null){
			String ImgURL = "";
			for(int i = 0; i < bean.getThumbURL().size(); i++)
			{   
				ImgURL+=bean.getThumbURL().get(i);
				if (i < (bean.getThumbURL().size()-1)) {
					ImgURL+=",";
				}
			}
			newBean.setThumbURL(ImgURL);
		}
		
		params.setStatus(0);
		params.setRequestParam(null);
		params.setRequestEntity(GsonUtil.getGson().toJson(newBean));
		return params;
	}
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
	
	private String resultStatus(String message, int statusCode, boolean isSuccess) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("message", message);
			jsonObject.put("statusCode", statusCode);
			jsonObject.put("success", isSuccess);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
