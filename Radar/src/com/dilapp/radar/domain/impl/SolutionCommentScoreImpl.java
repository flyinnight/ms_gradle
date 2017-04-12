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
import com.dilapp.radar.domain.SolutionCommentScore;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.XUtilsHelper;


public class SolutionCommentScoreImpl extends SolutionCommentScore {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private Context context;
	private ServerRequestParams params;

	public SolutionCommentScoreImpl(Context context) {
		this.context = context;
	}

	// 上传评论图片
	@Override
	public void solutionUplCommentImgAsync(List<String> imgs, final BaseCall<CommentImgResp> call) {
		handler5 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((CommentImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCommentImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						CommentImgResp resp = new CommentImgResp();
						Log.d("Radar", "solutionUplCommentImgAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							Object temp = jsonObject3.opt("URL");
							if (temp != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("URL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								resp.setCommentImgUrl(imgList);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							resp.setStatus("FAILED");
							Log.d("Radar", "JSONException: " + e);
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						CommentImgResp resp = new CommentImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					}
				});
	}
	
	// 添加评论
	@Override
	public void solutionCreatCommentsAsync(CreatCommentReq bean, final BaseCall<MSolutionResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeCreateCommParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						MSolutionResp resp = new MSolutionResp();
						Log.d("Radar", "SolutionCreatCommentsAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setCommentId(jsonObject3.optLong("id"));
							resp.setSolutionId(jsonObject3.optLong("solutionId"));
							resp.setParentCommId(jsonObject3.optLong("parentId"));
							resp.setContent(jsonObject3.optString("content"));
							resp.setUserId(jsonObject3.optString("userId"));
							resp.setNickName(jsonObject3.optString("username"));
							resp.setPortrait(jsonObject3.optString("portrait"));
							resp.setIsLike(jsonObject3.optBoolean("like"));
							resp.setLikeCount(jsonObject3.optInt("likeCount"));
							resp.setCreateTime(jsonObject3.optLong("createTime"));
							resp.setUpdateTime(jsonObject3.optLong("updateTime"));

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
						System.out.println(result);
						MSolutionResp resp = new MSolutionResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}
				});
	}
	
	// 创建/修改评分
	@Override
	public void solutionUpdateScoreAsync(UpdateScoreReq bean, final BaseCall<BaseResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeUpdateScoreParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						BaseResp resp = new BaseResp();
						Log.d("Radar", "SolutionUpdateScoreAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
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
						handler2.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					}
				});
	}

	// 获取评分
	@Override
	public void solutionGetScoreAsync(long solutionId, final BaseCall<GetScoreResp> call) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetScoreResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeGetScoreParams(solutionId),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						GetScoreResp resp = new GetScoreResp();
						Log.d("Radar", "solutionGetScoreAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));// true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setScore(jsonObject3.optInt("score"));

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
						System.out.println(result);
						GetScoreResp resp = new GetScoreResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}
				});
	}

	// 给评论点赞/取消点赞
	@Override
	public void solutionLikeCommentAsync(LikeCommentReq bean, final BaseCall<BaseResp> call) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeLikeCommentParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						BaseResp resp = new BaseResp();
						Log.d("Radar", "solutionLikeCommentAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
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
						handler4.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				});
	}
	
	// 删除评论
	@Override
	public void solutionDeleteCommentAsync(long commentId, final BaseCall<BaseResp> call) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeDeleteCommentParams(commentId),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						BaseResp resp = new BaseResp();
						Log.d("Radar", "solutionDeleteCommentAsync: " + result);
						try {
							JSONObject jsonObject = new JSONObject(result);
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
						handler4.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						System.out.println(result);
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				});
	}
	
	private ServerRequestParams writeCommentImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadImg(null));
		param.put("ImgFile", imgs);
		param.put("type", Integer.toString(9));
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}
	
	private ServerRequestParams writeCreateCommParams(CreatCommentReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.createComment(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(bean.getSolutionId()));
		param.put("parentId", Long.toString(bean.getParentCommId()));
		param.put("content", bean.getContent());
		if (bean.getToUserId() == null) {
			param.put("toUserId", "");
		} else {
			param.put("toUserId", bean.getToUserId());
		}
		
		if(bean.getPicUrl() != null){
			String ImgURL = "";
			for(int i = 0; i < bean.getPicUrl().size(); i++)
			{   
				ImgURL += bean.getPicUrl().get(i);
				if (i < (bean.getPicUrl().size()-1)) {
					ImgURL += ",";
				}
			}
			param.put("picUrl", ImgURL);
		} else {
			param.put("picUrl", "");
		}
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(0);
		return params;
	}
	
	private ServerRequestParams writeUpdateScoreParams(UpdateScoreReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.updateScore(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(bean.getSolutionId()));
		param.put("score", Integer.toString(bean.getScore()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(0);
		return params;
	}
	
	private ServerRequestParams writeGetScoreParams(long solutionId) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getScore(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("solutionId", Long.toString(solutionId));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(0);
		return params;
	}
	
	private ServerRequestParams writeLikeCommentParams(LikeCommentReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.likeComment(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("commentId", Long.toString(bean.getCommentId()));
		param.put("isLike", Boolean.toString(bean.getIsLike()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(0);
		return params;
	}
	
	private ServerRequestParams writeDeleteCommentParams(long commentId) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.deleteComment(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("commentId", Long.toString(commentId));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(0);
		return params;
	}
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
}
