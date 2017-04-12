package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.SearchCallBack;
import com.dilapp.radar.domain.TopicListCallBack.MTopicResp;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class SearchCallBackImpl extends SearchCallBack {
	private Handler handler1;
	private Handler handler2;
	private Context context;
	private ServerRequestParams params;

	public SearchCallBackImpl(Context context) {
		this.context = context;
	}

	//查询返回话题列表
	@Override
	public void TopicSearchAsync(TopicSearchReq bean, final BaseCall<TopicSearchResp> call) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicSearchResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeTopicParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				TopicSearchResp resp = new TopicSearchResp();
				Log.d("Radar", "TopicSearchAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("totalPage"));// ok
						resp.setPageNo(jsonObj.optInt("pageNo"));//SUCCESS
						
						Object temp1 = jsonObj.opt("topics");
						if (temp1 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("topics"));
							List<MTopicResp> resList = new ArrayList<MTopicResp>();
							
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								MTopicResp topicResp = new MTopicResp();

								topicResp.setTopictitle(jsonObject3.optString("topicTitle"));// "Whattimetostickmasknight"
								topicResp.setTopicId(jsonObject3.optLong("topicId"));
								topicResp.setContent(jsonObject3.optString("topicDes"));
								topicResp.setUsername(jsonObject3.optString("userName"));
								topicResp.setUserId(jsonObject3.optString("userId"));
								topicResp.setFollowup(jsonObject3.optBoolean("followup"));
								topicResp.setFollowsUpNum(jsonObject3.optInt("followsupNum"));
								topicResp.setRegen(jsonObject3.optInt("postNum"));
								topicResp.setTopicimg((String[])jsonObject3.optString("topicURL").split(","));// "topic/icon/1432101339995/katong.jpg"
								topicResp.setReleasetime(jsonObject3.optLong("updateTime"));// 1432190979000
								resList.add(topicResp);
							}
							resp.setDatas(resList);
						}
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
				TopicSearchResp resp = new TopicSearchResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	//查询返回帖子列表
	@Override
	public void PostSearchAsync(PostSearchReq bean, final BaseCall<PostSearchResp> call) {
		handler2 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((PostSearchResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				PostSearchResp resp = new PostSearchResp();
				Log.d("Radar", "PostSearchAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("totalPage"));
						resp.setPageNo(jsonObj.optInt("startNo"));
						
						Object temp1 = jsonObj.opt("posts");
						if (temp1 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
							List<MPostResp> resList = new ArrayList<MPostResp>();

							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								MPostResp postResp = new MPostResp();
								
								postResp.setId(jsonObject3.optLong("id"));
								postResp.setPid(jsonObject3.optLong("parentId"));
								postResp.setTopicId(jsonObject3.optLong("topicId"));
								postResp.setTopicTitle(jsonObject3.optString("topicTitle"));
								postResp.setPostLevel(jsonObject3.optInt("postLevel"));
								postResp.setUserId(jsonObject3.optString("userId"));
								postResp.setUserName(jsonObject3.optString("name"));
								postResp.setToUserId(jsonObject3.optString("toUserId"));
								postResp.setToUserName(jsonObject3.optString("toUserName"));
								postResp.setPostTitle(jsonObject3.optString("postTitle"));
								postResp.setPostContent(jsonObject3.optString("postContent"));
								postResp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
								postResp.setStoreupNum(jsonObject3.optInt("storeupNum"));
								postResp.setSelectedToSolution(jsonObject3.optBoolean("selectedToSolution"));
								postResp.setEffect(jsonObject3.optString("effect"));
								postResp.setReport(jsonObject3.optBoolean("reported"));
								postResp.setOnTop(jsonObject3.optBoolean("onTop"));
								Object temp2 = jsonObject3.opt("thumbURL");
								if (temp2 != null) {
									String thumbs = jsonObject3.optString("thumbURL");
									List<String> imgList = Arrays.asList(thumbs.split(","));
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
							resp.setDatas(resList);
						}
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
				PostSearchResp resp = new PostSearchResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
				System.out.println(result);
			}
		});
	}

	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}

	private ServerRequestParams writeTopicParams(TopicSearchReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.searchReturnTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		//param.put("topicId", bean.getTopicId());
		param.put("startNo", Integer.toString(bean.getStartNo()));
		String[] tmp = bean.getTopicParam();
		String topic="";
		for(int i = 0; i < tmp.length; i++){
			topic += tmp[i];
			if (i < (tmp.length-1)) {
				topic+=",";
			}
		}
		param.put("topicParam", topic);
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writePostParams(PostSearchReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.searchReturnPost(null));
		Map<String, Object> param = new HashMap<String, Object>();
		//param.put("topicId", bean.getTopicId());
		param.put("startNo", Integer.toString(bean.getStartNo()));
		String[] tmp = bean.getPostParam();  
		String post="";  
		for(int i = 0; i < tmp.length; i++){
			post += tmp[i];
			if (i < (tmp.length-1)) {
				post+=",";
			}
		}
		param.put("postParam", post);
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	

}
