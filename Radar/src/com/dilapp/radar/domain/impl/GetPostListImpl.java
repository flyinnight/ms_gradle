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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.TopicListCallBack;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.Slog;

public class GetPostListImpl extends GetPostList {
	private Handler handler1;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private Context context;
	private ServerRequestParams params;
	
	private boolean reTry3;
	private boolean reTry4;
	private boolean reTry5;

	public GetPostListImpl(Context context) {
		this.context = context;
		reTry3 = true;
		reTry4 = true;
		reTry5 = true;
	}


	// 获得某个话题的帖子列表
	@Override
	public void getPostsOfOneTopicByTypeAsync(final MPostReq bean, final BaseCall<TopicPostListResp> call, int type) {
		handler3 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicPostListResp) msg.obj);
					reTry3 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getTopicDetailServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			TopicDetailGet topicGet = new TopicDetailGet();
			topicGet.setTopicId(bean.getTopicId());
			topicGet.setType(TopicListCallBack.TOPIC_DETAIL_POSTLIST);
			getTopicDetailLocal(topicGet, bean);
		}
	}
	
	public void getTopicDetailServer(final MPostReq bean) {

		RadarProxy.getInstance(context).startServerData(writePostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				TopicPostListResp resp = new TopicPostListResp();
				List<MPostResp> resList = null;
				Log.d("Radar", "getTopicDetailServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObj.optInt("totalPage"));
					resp.setPageNo(jsonObj.optInt("pageNo"));
					JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
					
					resList = new ArrayList<MPostResp>();
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
						MPostResp postResp = new MPostResp();
						
						postResp.setId(jsonObject3.optLong("id"));
						postResp.setPid(jsonObject3.optLong("pid"));
						postResp.setTopicId(jsonObject3.optLong("topicId"));
						postResp.setTopicTitle(jsonObject3.optString("topicTitle"));
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
					resp.setPostLists(resList);
					
					TopicDetailSave saveBean = new TopicDetailSave();
					saveBean.setPostList(resp);
					saveBean.setTopicId(bean.getTopicId());
					saveBean.setType(TopicListCallBack.TOPIC_DETAIL_POSTLIST);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						TopicDetailGet topicGet = new TopicDetailGet();
						topicGet.setTopicId(bean.getTopicId());
						topicGet.setType(TopicListCallBack.TOPIC_DETAIL_POSTLIST);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_DELETE_ONE, GsonUtil.getGson().toJson(topicGet), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (reTry3) {
							reTry3 = false;
							TopicDetailGet topicGet = new TopicDetailGet();
							topicGet.setTopicId(bean.getTopicId());
							topicGet.setType(TopicListCallBack.TOPIC_DETAIL_POSTLIST);
							getTopicDetailLocal(topicGet, bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry3) {
					reTry3 = false;
					TopicDetailGet topicGet = new TopicDetailGet();
					topicGet.setTopicId(bean.getTopicId());
					topicGet.setType(TopicListCallBack.TOPIC_DETAIL_POSTLIST);
					getTopicDetailLocal(topicGet, bean);
				} else {
					TopicPostListResp resp = new TopicPostListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	public void getTopicDetailLocal(TopicDetailGet getBean, final MPostReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.TOPIC_DETAIL_GET_ONE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getTopicDetailLocal " + result);
				TopicDetailSave respSave = GsonUtil.getGson().fromJson(result, TopicDetailSave.class);
				TopicPostListResp resp = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry3 && ((resp == null) || (resp.getPostLists() == null) || (resp.getPostLists().size() == 0))) {
					reTry3 = false;
					getTopicDetailServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getTopicDetailServer(bean);
					} else {
						if (resp == null) {
							resp = new TopicPostListResp();
							resp.setStatus("FAILED");
						} else {
							if ((resp.getPostLists() == null) || (resp.getPostLists().size() == 0)) {
								resp.setStatus("FAILED");
							} else {
								resp.setStatus("SUCCESS");
							}
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
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
					getTopicDetailServer(bean);
				} else {
					TopicPostListResp resp = new TopicPostListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}


	// 主页显示用户关注话题的帖子列表
	@Override
	public void getPostsOfFollowTopicByTypeAsync(final MFollowTopicPostReq bean, final BaseCall<TopicPostListResp> call, int type) {
		handler4 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicPostListResp) msg.obj);
					reTry4 = true;
				}
			}
		};

		if (type == GetPostList.GET_DATA_SERVER) {
			getPostsOfFollowTopicServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getPostsOfFollowTopicLocal(bean);
		}
	}
	
	public void getPostsOfFollowTopicServer(final MFollowTopicPostReq bean) {
		
		RadarProxy.getInstance(context).startServerData(writeFollowTopicPostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				int statusCode = BaseResp.OK;
				Log.d("Radar", "getPostsOfFollowTopicServer: " + result);
				try {
					final JSONObject jsonObject = new JSONObject(result);
					final JSONObject jsonObject1 = new JSONObject(jsonObject.optString("message"));
					statusCode = jsonObject.optInt("statusCode");
					
					if ("SUCCESS".equalsIgnoreCase(jsonObject1.optString("status"))) {
						Object temp = jsonObject1.opt("values");
						if (temp != null) {
							final JSONObject jsonObj = new JSONObject(jsonObject1.optString("values"));
							Object temp1 = jsonObj.opt("posts");
							if (temp1 != null) {

								JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
								MainPostListSave respSave = new MainPostListSave();
								List<MPostResp> resList = new ArrayList<MPostResp>();
								for (int i = 0; i < jsonArr.length(); i++) {
									MPostResp resp = analyzeRespSave((JSONObject)jsonArr.get(i));
									resList.add(resp);
								}

								respSave.setPostLists(resList);
								respSave.setUpdateTime(System.currentTimeMillis());
								
								RadarProxy.getInstance(context).startLocalData(HttpConstant.UPDATE_MAIN_POST_LIST_LOCAL, GsonUtil.getGson().toJson(respSave), new ClientCallbackImpl() {
									@Override
									public void onSuccess(String result) {

										Log.d("Radar", "mainpostlists: UPDATE_MAIN_POST_LIST_LOCAL " + result);
										RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, Integer.toString(bean.getPageNo()), new ClientCallbackImpl() {
											@Override
											public void onSuccess(String result) {
												Log.d("Radar", "mainpostlists: GET_MAIN_POST_LIST_LOCAL " + result);
												MainPostListSave respSave = GsonUtil.getGson().fromJson(result, MainPostListSave.class);
												TopicPostListResp resp = new TopicPostListResp();
												
												resp.setSuccess(jsonObject.optBoolean("success"));//true,false
												resp.setStatusCode(jsonObject.optInt("statusCode"));
												resp.setMessage(jsonObject1.optString("msg"));
												resp.setStatus(jsonObject1.optString("status"));
												resp.setTotalPage(jsonObj.optInt("totalPage"));
												resp.setPageNo(jsonObj.optInt("pageNo"));
												resp.setPostLists(respSave.getPostLists());

												Message msg = Message.obtain();
												msg.obj = resp;
												handler4.sendMessage(msg);
											}

											@Override
											public void onFailure(String result) {
												TopicPostListResp resp = new TopicPostListResp();
												resp.setStatus("FAILED");
												Message msg = Message.obtain();
												msg.obj = resp;
												handler4.sendMessage(msg);
												System.out.println(result);
											}
										});
									}

									@Override
									public void onFailure(String result) {
										System.out.println(result);
										if (reTry4) {
											reTry4 = false;
											getPostsOfFollowTopicLocal(bean);
										} else {
											TopicPostListResp resp = new TopicPostListResp();
											resp.setStatus("FAILED");
											Message msg = Message.obtain();
											msg.obj = resp;
											handler4.sendMessage(msg);
										}
									}
								});
							} else {
								handleFailure(bean, statusCode);
							}
						} else {
							handleFailure(bean, statusCode);
						}
					} else {
						handleFailure(bean, statusCode);
					}
				
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					handleFailure(bean, statusCode);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry4) {
					reTry4 = false;
					getPostsOfFollowTopicLocal(bean);
				} else {
					TopicPostListResp resp = new TopicPostListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	public void handleFailure(MFollowTopicPostReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			RadarProxy.getInstance(context).startLocalData(HttpConstant.MAIN_POST_DELETE_REMOTE, null, null);
			TopicPostListResp resp = new TopicPostListResp();
			resp.setStatus("FAILED");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler4.sendMessage(msg);
		} else {
			if (reTry4) {
				reTry4 = false;
				getPostsOfFollowTopicLocal(bean);
			} else {
				TopicPostListResp resp = new TopicPostListResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler4.sendMessage(msg);
			}
		}
	}
	
	public void getPostsOfFollowTopicLocal(final MFollowTopicPostReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, Integer.toString(bean.getPageNo()), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getPostsOfFollowTopicLocal " + result);
				MainPostListSave respSave = GsonUtil.getGson().fromJson(result, MainPostListSave.class);
				TopicPostListResp resp = new TopicPostListResp();
				List<MPostResp> beanList = respSave.getPostLists();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry4 && ((beanList == null) || (beanList.size() == 0))) {
					reTry4 = false;
					getPostsOfFollowTopicServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getPostsOfFollowTopicServer(bean);
					} else {
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setPostLists(beanList);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				if (reTry4) {
					reTry4 = false;
					getPostsOfFollowTopicServer(bean);
				} else {
					TopicPostListResp resp = new TopicPostListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
					System.out.println(result);
				}
			}
		});
	}
	

	// 获取用户评论过的帖子以及原帖
	@Override
	public void getCommentedPostByTypeAsync(CommentedPostReq bean, final BaseCall<CommentedPostListResp> call, int type) {
		handler5 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((CommentedPostListResp) msg.obj);
					reTry5 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getCommentedPostServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getCommentedPostLocal(bean);
		}
	}
	
	public void getCommentedPostServer(final CommentedPostReq bean) {

		RadarProxy.getInstance(context).startServerData(writeCommentedParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				CommentedPostListResp resp = new CommentedPostListResp();
				Log.d("Radar", "getCommentedPostServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObj.optInt("totalPage"));
					resp.setPageNo(jsonObj.optInt("pageNo"));
					
					JSONArray jsonArr = new JSONArray(jsonObj.optString("post"));
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
						postResp.setUserName(jsonObject3.optString("username"));
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
						//postResp.setCommentedImg((String[])(jsonObject3.optString("thumbURL").split(",")));
						Object temp = jsonObject3.opt("thumbURL");
						if (temp != null) {
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
						postResp.setSkinQuality(jsonObject3.optString("skin"));
						postResp.setPart(jsonObject3.optString("part"));
						postResp.setQq(jsonObject3.optString("qq"));
						postResp.setEmail(jsonObject3.optString("email"));
						postResp.setWechat(jsonObject3.optString("wechat"));
						postResp.setBlog(jsonObject3.optString("blog"));
						resList.add(postResp);
					}
					resp.setPostLists(resList);
					
					JSONArray jsonArr2 = new JSONArray(jsonObj.optString("parentPost"));
					List<MPostResp> resList2 = new ArrayList<MPostResp>();
					for (int k = 0; k < jsonArr2.length(); k++) {
						JSONObject jsonObject4 = (JSONObject) jsonArr2.get(k);
						MPostResp postResp2 = new MPostResp();
						
						postResp2.setId(jsonObject4.optLong("id"));
						postResp2.setPid(jsonObject4.optLong("parentId"));
						postResp2.setTopicId(jsonObject4.optLong("topicId"));
						postResp2.setTopicTitle(jsonObject4.optString("topicTitle"));
						postResp2.setPostLevel(jsonObject4.optInt("postLevel"));
						postResp2.setUserId(jsonObject4.optString("userId"));
						postResp2.setUserName(jsonObject4.optString("username"));
						postResp2.setToUserId(jsonObject4.optString("toUserId"));
						postResp2.setToUserName(jsonObject4.optString("toUserName"));
						postResp2.setPostTitle(jsonObject4.optString("postTitle"));
						postResp2.setPostContent(jsonObject4.optString("postContent"));
						postResp2.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
						postResp2.setStoreupNum(jsonObject4.optInt("storeupNum"));
						postResp2.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
						postResp2.setEffect(jsonObject4.optString("effect"));
						postResp2.setReport(jsonObject4.optBoolean("reported"));
						postResp2.setOnTop(jsonObject4.optBoolean("onTop"));
						//postResp2.setCommentedImg((String[])(jsonObject4.optString("thumbURL").split(",")));
						Object temp1 = jsonObject4.opt("thumbURL");
						if (temp1 != null) {
							String thumbs = jsonObject4.optString("thumbURL");
							List<String> imgList = Arrays.asList(thumbs.split(","));
							postResp2.setThumbURL(imgList);
						}

						postResp2.setLike(jsonObject4.optInt("favorite"));
						postResp2.setDislike(jsonObject4.optInt("disfavorite"));
						postResp2.setUpdateTime(jsonObject4.optLong("updateTime"));
						postResp2.setCreateTime(jsonObject4.optLong("createTime"));
						postResp2.setPostViewCount(jsonObject4.optInt("postViewCount"));
						postResp2.setTotalFollows(jsonObject4.optInt("totalFollows"));
						postResp2.setLevel(jsonObject4.optInt("level"));
						postResp2.setLevelName(jsonObject4.optString("levelName"));
						postResp2.setGender(jsonObject4.optInt("gender"));
						postResp2.setBirthday(jsonObject4.optString("birthday"));
						postResp2.setDesc(jsonObject4.optString("desc"));
						postResp2.setOccupation(jsonObject4.optString("occupation"));
						postResp2.setUserHeadIcon(jsonObject4.optString("portrait"));
						postResp2.setSkinQuality(jsonObject4.optString("skinQuality"));
						postResp2.setQq(jsonObject4.optString("qq"));
						postResp2.setEmail(jsonObject4.optString("email"));
						postResp2.setWechat(jsonObject4.optString("wechat"));
						postResp2.setBlog(jsonObject4.optString("blog"));
						resList2.add(postResp2);
					}
					resp.setParenPostLists(resList2);
					
					PostListSave saveBean = new PostListSave();
					saveBean.setPostList(resList);
					saveBean.setType(POST_LIST_BY_COMMENT);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					saveBean.setPostList(resList2);
					saveBean.setType(POST_LIST_BY_COMMENT_PARENT);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_COMMENT), null);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_COMMENT_PARENT), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler5.sendMessage(msg);
					} else {
						if (reTry5) {
							reTry5 = false;
							getCommentedPostLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler5.sendMessage(msg);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry5) {
					reTry5 = false;
					getCommentedPostLocal(bean);
				} else {
					CommentedPostListResp resp = new CommentedPostListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	public void getCommentedPostLocal(final CommentedPostReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(POST_LIST_BY_COMMENT), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getCommentedPostLocal " + result);
				final CommentedPostListResp resp = new CommentedPostListResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry5 && ((beanList == null) || (beanList.size() == 0))) {
					reTry5 = false;
					getCommentedPostServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getCommentedPostServer(bean);
					} else {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(POST_LIST_BY_COMMENT_PARENT), new ClientCallbackImpl() {
							@Override
							public void onSuccess(String result) {
								Log.d("Radar", "getCommentedPostLocal parent" + result);
								PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
								List<MPostResp> beanList2 = respSave.getPostList();
								long updateTime2 = respSave.getUpdateTime();

								if (reTry5 && ((beanList2 == null) || (beanList2.size() == 0))) {
									reTry5 = false;
									getCommentedPostServer(bean);
								} else {
									if ((System.currentTimeMillis() - updateTime2) > GetPostList.UPDATE_SPAN_TIME) {
										getCommentedPostServer(bean);
									} else {
										resp.setTotalPage(1);
										resp.setPageNo(1);
										resp.setParenPostLists(beanList2);
										resp.setSuccess(true);
										resp.setStatusCode(BaseResp.DATA_LOCAL);
										resp.setMessage("ok");
										if ((beanList2 == null) || (beanList2.size() == 0)) {
											resp.setStatus("FAILED");
										} else {
											resp.setStatus("SUCCESS");
										}
										Message msg = Message.obtain();
										msg.obj = resp;
										handler5.sendMessage(msg);
									}
								}
								
							}

							@Override
							public void onFailure(String result) {
								System.out.println(result);
								if (reTry5) {
									reTry5 = false;
									getCommentedPostServer(bean);
								} else {
									CommentedPostListResp resp = new CommentedPostListResp();
									resp.setStatus("FAILED");
									resp.setStatusCode(BaseResp.DATA_LOCAL);
									Message msg = Message.obtain();
									msg.obj = resp;
									handler5.sendMessage(msg);
								}
							}
						});
			
						resp.setPostLists(beanList);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry5) {
					reTry5 = false;
					getCommentedPostServer(bean);
				} else {
					TopicPostListResp resp = new TopicPostListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler5.sendMessage(msg);
				}
			}
		});
	}
	
	
	/**
	 * 根据测试结果推荐贴子
	 */	
	@Override
	public void recommendPostsByTestAsync(final PostsTestReq bean, final BaseCall<TopicPostListResp> call) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((TopicPostListResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeRecommendPostParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				TopicPostListResp resp = new TopicPostListResp();
				List<MPostResp> resList = null;
				Log.d("Radar", "recommendPostsByTestAsync: " + result);
				Slog.f("Filelog: recommendPostsByTestAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObj.optInt("totalPage"));
					resp.setPageNo(jsonObj.optInt("startNo"));
					JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
					
					resList = new ArrayList<MPostResp>();
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
						postResp.setSelectedToSolution(jsonObject3.optBoolean("selectedToSolution")); //isSolution
						postResp.setEffect(jsonObject3.optString("effect"));
						postResp.setPart(jsonObject3.optString("part"));
						postResp.setSkinQuality(jsonObject3.optString("skinQuality"));
						postResp.setReport(jsonObject3.optBoolean("reported"));
						postResp.setOnTop(jsonObject3.optBoolean("onTop"));
						//resp.setThumbURL((String[])(jsonObject3.optString("thumbURL").split(",")));
						Object temp = jsonObject3.opt("thumbURL");
						if (temp != null) {
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
						postResp.setLocation(jsonObject3.optString("location"));
						postResp.setQq(jsonObject3.optString("qq"));
						postResp.setEmail(jsonObject3.optString("email"));
						postResp.setWechat(jsonObject3.optString("wechat"));
						postResp.setBlog(jsonObject3.optString("blog"));
						resList.add(postResp);
					}
					resp.setPostLists(resList);
					
					/*PostListSave saveBean = new PostListSave();
					saveBean.setPostList(resList);
					saveBean.setType(POST_LIST_RECOMMEND_BYTEST);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);*/
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					/*if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_RECOMMEND_BYTEST), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}*/
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				TopicPostListResp resp = new TopicPostListResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		});
	}
	
	public void getRecommendPostsByTestLocal() {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(POST_LIST_RECOMMEND_BYTEST), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getRecommendPostsByTestLocal " + result);
				TopicPostListResp resp = new TopicPostListResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();

				resp.setTotalPage(1);
				resp.setPageNo(1);
				resp.setPostLists(beanList);
				resp.setSuccess(true);
				resp.setStatusCode(BaseResp.DATA_LOCAL);
				resp.setMessage("ok");
				resp.setStatus("SUCCESS");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				TopicPostListResp resp = new TopicPostListResp();
				resp.setStatus("FAILED");
				resp.setStatusCode(BaseResp.DATA_LOCAL);
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	
	public static MPostResp analyzeRespSave(JSONObject jsonObject3) {

		MPostResp resp = new MPostResp();
		try {
			resp.setId(jsonObject3.optLong("id"));
			resp.setLocalPostId(jsonObject3.optLong("localPostId"));
			resp.setPid(jsonObject3.optLong("parentId"));
			resp.setTopicId(jsonObject3.optLong("topicId"));
			resp.setTopicTitle(jsonObject3.optString("topicTitle"));
			resp.setPostLevel(jsonObject3.optInt("postLevel"));
			resp.setUserId(jsonObject3.optString("userId"));
			resp.setUserName(jsonObject3.optString("userName"));
			resp.setToUserId(jsonObject3.optString("toUserId"));
			resp.setToUserName(jsonObject3.optString("toUserName"));
			resp.setPostTitle(jsonObject3.optString("postTitle"));
			resp.setPostContent(jsonObject3.optString("postContent"));
			resp.setFollowsUpNum(jsonObject3.optInt("followsUpNum"));
			resp.setStoreupNum(jsonObject3.optInt("storeupNum"));
			resp.setSelectedToSolution(jsonObject3.optBoolean("selectedToSolution"));
			resp.setEffect(jsonObject3.optString("effect"));
			resp.setReport(jsonObject3.optBoolean("report"));
			resp.setOnTop(jsonObject3.optBoolean("onTop"));
			//resp.setThumbURL((String[])(jsonObject3.optString("thumbURL").split(",")));
			Object temp = jsonObject3.opt("thumbURL");
			if (temp != null) {
				JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("thumbURL"));
				List<String> imgList = new ArrayList<String>();
				for (int j = 0; j < jsonArrImg.length(); j++) {
					String imgItem = (String) jsonArrImg.get(j);
					imgList.add(imgItem);
				}
				resp.setThumbURL(imgList);
			}
			resp.setLike(jsonObject3.optInt("favorite"));
			resp.setDislike(jsonObject3.optInt("disfavorite"));
			resp.setUpdateTime(jsonObject3.optLong("updateTime"));
			resp.setCreateTime(jsonObject3.optLong("createTime"));
			resp.setPostViewCount(jsonObject3.optInt("postViewCount"));
			resp.setTotalFollows(jsonObject3.optInt("totalFollows"));
			resp.setLevel(jsonObject3.optInt("level"));
			resp.setLevelName(jsonObject3.optString("levelName"));
			resp.setGender(jsonObject3.optInt("gender"));
			resp.setBirthday(jsonObject3.optString("birthday"));
			resp.setDesc(jsonObject3.optString("desc"));
			resp.setOccupation(jsonObject3.optString("occupation"));
			resp.setUserHeadIcon(jsonObject3.optString("portrait"));
			resp.setSkinQuality(jsonObject3.optString("skinQuality"));
			resp.setQq(jsonObject3.optString("qq"));
			resp.setEmail(jsonObject3.optString("email"));
			resp.setWechat(jsonObject3.optString("wechat"));
			resp.setBlog(jsonObject3.optString("blog"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resp;
	}
	
	
	private ServerRequestParams writePostParams(MPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.postList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topicId", Long.toString(bean.getTopicId()));
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeFollowTopicPostParams(MFollowTopicPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getPostsOfFollowTopic(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeCommentedParams(CommentedPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getCommentedPost(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeRecommendPostParams(PostsTestReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.recommendPostTest(null));
		Map<String, Object> param = new HashMap<String, Object>();
		String[] tmp = bean.getPostParam();  
		String post = "";  
		for(int i = 0; i < tmp.length; i++){
			post += tmp[i];
			if (i < (tmp.length-1)) {
				post+=",";
			}
		}
		param.put("postParam", post);
		param.put("startNo", Integer.toString(bean.getStartNo()));
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
