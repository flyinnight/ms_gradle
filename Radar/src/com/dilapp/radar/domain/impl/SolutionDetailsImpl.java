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
import com.dilapp.radar.domain.SolutionComment.Solutioncomment;
import com.dilapp.radar.domain.SolutionDetails;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;

public class SolutionDetailsImpl extends SolutionDetails {
	private Handler handler;
	private Context context;
	private ServerRequestParams params;

	public SolutionDetailsImpl(Context context) {
		this.context = context;
	}


	@Override
	public void getSkinSolutionAndCommentsAsync(SolutionDetailReq bean, final BaseCall<SolutionDetailResp> call) {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolutionDetailResp) msg.obj);
				}
			}
		};
		
		RadarProxy.getInstance(context).startServerData(writeSolutionParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				SolutionDetailResp resp =new SolutionDetailResp();
				Log.d("Radar", "getSkinSolutionAndCommentsAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("post");
					if (temp != null) {
						JSONObject jsonObject4 = new JSONObject(jsonObject3.optString("post"));
						
						SolutionResp mSolutionResp = new SolutionResp();
						mSolutionResp.setPostId(jsonObject4.optLong("id"));
						mSolutionResp.setParentId(jsonObject4.optLong("parentId"));
						mSolutionResp.setTopicId(jsonObject4.optLong("topicId"));
						mSolutionResp.setPostLevel(jsonObject4.optInt("postLevel"));
						mSolutionResp.setUserId(jsonObject4.optString("userId"));
						mSolutionResp.setUserName(jsonObject4.optString("userName"));
						mSolutionResp.setPostTitle(jsonObject4.optString("postTitle"));
						mSolutionResp.setPostContent(jsonObject4.optString("postContent"));
						mSolutionResp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
						mSolutionResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
						mSolutionResp.setIsSolution(jsonObject4.optBoolean("selectedToSolution"));
						mSolutionResp.setEffect(jsonObject4.optString("effect"));
						mSolutionResp.setPart(jsonObject4.optString("part"));
						mSolutionResp.setSkin(jsonObject4.optString("skin"));
						mSolutionResp.setReport(jsonObject4.optBoolean("reported"));
						mSolutionResp.setOnTop(jsonObject4.optBoolean("onTop"));
						mSolutionResp.setFavorite(jsonObject4.optInt("favorite"));
						mSolutionResp.setDisfavorite(jsonObject4.optInt("disfavorite"));
						mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
						mSolutionResp.setThumbString((String[])(jsonObject4.optString("thumbURL").split(",")));
						
						resp.setSolutionResp(mSolutionResp);
					}
					
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
						resp.setComment(commentList);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					resp.setStatus("FAILED");
					Log.d("Radar", "JSONException: " + e);
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				SolutionDetailResp resp = new SolutionDetailResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}

	
	private ServerRequestParams writeSolutionParams(SolutionDetailReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionAndComments(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("postId", Long.toString(bean.getPostId()));
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
