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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionComment;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;


public class SolutionCommentImpl extends SolutionComment {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;

	public SolutionCommentImpl(Context context) {
		this.context = context;
	}

	@Override
	public void getSkinSolutionCommentsAsync(GetSolutionCommentReq bean, final BaseCall<GetSolutionCommentResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetSolutionCommentResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						GetSolutionCommentResp resp = new GetSolutionCommentResp();
						Log.d("Radar", "getSkinSolutionCommentsAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setScores(jsonObject3.optDouble("scores"));
							resp.setTotalPage(jsonObject3.optInt("totalPage"));
							resp.setPageNo(jsonObject3.optInt("pageNo"));

							Object temp2 = jsonObject3.opt("comments");
							if (temp2 != null) {
								JSONArray jsonArrComment = new JSONArray(jsonObject3.optString("comments"));
								List<Solutioncomment> commentList = new ArrayList<Solutioncomment>();
								for (int j = 0; j < jsonArrComment.length(); j++) {
									JSONObject jsonObject5 = (JSONObject) jsonArrComment.get(j);
									Solutioncomment comment = new Solutioncomment();
									comment.setUserId(jsonObject5.optString("userId"));
									comment.setUserName(jsonObject5.optString("username"));
									comment.setScore(jsonObject5.optInt("score"));
									comment.setComments(jsonObject5.optString("comments"));
									comment.setUpdateTime(jsonObject5.optLong("updateTime"));
									
									commentList.add(comment);
								}
								resp.setComments(commentList);
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
						GetSolutionCommentResp resp = new GetSolutionCommentResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	@Override
	public void postSkinSolutionCommentAsync(PostSolutionCommentReq bean, final BaseCall<PostSolutionCommentResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((PostSolutionCommentResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writePostParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						PostSolutionCommentResp resp = new PostSolutionCommentResp();
						Log.d("Radar", "postSkinSolutionCommentAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setScores(jsonObject3.optDouble("scores"));
							resp.setCommentId(jsonObject3.optLong("commentId"));

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
						PostSolutionCommentResp resp = new PostSolutionCommentResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}
	
	@Override
	public void deleteSkinSolutionCommentAsync(DeleteSolutionCommentReq bean, final BaseCall<BaseResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeDeleteParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "deleteSkinSolutionCommentAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS

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
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	private ServerRequestParams writeGetParams(GetSolutionCommentReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionComments(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writePostParams(PostSolutionCommentReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.postSkinSolutionComment(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("score", Integer.toString(bean.getScore()));
		param.put("comment", bean.getComment());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		return params;
	}
	
	private ServerRequestParams writeDeleteParams(DeleteSolutionCommentReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.deleteSkinSolutionComment(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("commentId", Long.toString(bean.getCommentId()));
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
