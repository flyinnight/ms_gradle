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
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.FoundAllTopic.AllTopicResp;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.TopicDetailSave;
import com.dilapp.radar.domain.GetPostList.TopicPostListResp;
import com.dilapp.radar.domain.SolutionDetails.SolutionResp;
import com.dilapp.radar.domain.SolutionList.SolutionListEffectReq;
import com.dilapp.radar.domain.SolutionList.SolutionListResp;
import com.dilapp.radar.domain.SolutionList;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.google.gson.reflect.TypeToken;

public class SolutionListImpl extends SolutionList {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private boolean reTry1;
	private boolean reTry2;
	private boolean reTry3;
	private boolean reTry4;
	private Context context;
	private ServerRequestParams params;

	public SolutionListImpl(Context context) {
		this.context = context;
		reTry1 = true;
		reTry2 = true;
		reTry3 = true;
		reTry4 = true;
	}

	
	//用户护肤方案列表
	@Override
	public void solutionListByTypeAsync(SolutionListReq bean, final BaseCall<SolutionListResp> call, int type) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolutionListResp) msg.obj);
					reTry1 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListLocal(bean);
		}
	}
	
	public void getSolutionListServer(final SolutionListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeSolutionParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				SolutionListResp resp = new SolutionListResp();
				Log.d("Radar", "getSolutionListServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<SolutionResp> resList = new ArrayList<SolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							SolutionResp mSolutionResp = new SolutionResp();
							
							mSolutionResp.setPostId(jsonObject4.optLong("postId"));
							mSolutionResp.setParentId(jsonObject4.optLong("pid"));
							mSolutionResp.setTopicId(jsonObject4.optLong("topicId"));
							mSolutionResp.setTopicTitle(jsonObject4.optString("topicTitle"));
							mSolutionResp.setPostLevel(jsonObject4.optInt("postLevel"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setUserName(jsonObject4.optString("username"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							mSolutionResp.setPostTitle(jsonObject4.optString("postTitle"));
							//resp.setThumbnail((String[])(jsonObject3.optString("thumbURL").split(",")));
							Object temp2 = jsonObject4.opt("thumbURL");
							if (temp2 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								mSolutionResp.setThumbURL(imgList);
							}
							mSolutionResp.setIsSolution(jsonObject4.optBoolean("isSolution"));
							mSolutionResp.setEffect(jsonObject4.optString("effect"));
							mSolutionResp.setPart(jsonObject4.optString("part"));
							mSolutionResp.setSkin(jsonObject4.optString("skin"));
							mSolutionResp.setFavorite(jsonObject4.optInt("favorite"));
							mSolutionResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
							mSolutionResp.setInUsed(jsonObject4.optBoolean("inUsed"));
							mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mSolutionResp.setScores(jsonObject4.optDouble("score"));
							resList.add(mSolutionResp);
						}
						resp.setSolutions(resList);
						
						SolutionListSave saveBean = new SolutionListSave();
						saveBean.setSolutionList(resList);
						saveBean.setType(SOLUTION_LIST_USER_STORE);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_USER_STORE), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler1.sendMessage(msg);
						} else {
							if (reTry1) {
								reTry1 = false;
								getSolutionListLocal(bean);
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler1.sendMessage(msg);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_USER_STORE), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						if (reTry1) {
							reTry1 = false;
							getSolutionListLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler1.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getSolutionListLocal(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	public void getSolutionListLocal(final SolutionListReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_GET_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_USER_STORE), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListLocal " + result);
				SolutionListResp resp = new SolutionListResp();
				SolutionListSave respSave = GsonUtil.getGson().fromJson(result, SolutionListSave.class);
				List<SolutionResp> beanList = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry1 && ((beanList == null) || (beanList.size() == 0))) {
					reTry1 = false;
					getSolutionListServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setSolutions(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getSolutionListServer(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	
	//根据部位显示护肤方案列表
	@Override
	public void solutionListPartByTypeAsync(SolutionListPartReq bean, final BaseCall<SolutionListResp> call, int type) {
		handler2 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolutionListResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListPartServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListPartLocal(bean);
		}
	}
	
	public void getSolutionListPartServer(final SolutionListPartReq bean) {

		RadarProxy.getInstance(context).startServerData(writeSolutionPartParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				SolutionListResp resp = new SolutionListResp();
				Log.d("Radar", "getSolutionListPartServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<SolutionResp> resList = new ArrayList<SolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							SolutionResp mSolutionResp = new SolutionResp();
							
							mSolutionResp.setPostId(jsonObject4.optLong("postId"));
							mSolutionResp.setParentId(jsonObject4.optLong("pid"));
							mSolutionResp.setTopicId(jsonObject4.optLong("topicId"));
							mSolutionResp.setPostLevel(jsonObject4.optInt("postLevel"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setUserName(jsonObject4.optString("username"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							mSolutionResp.setPostTitle(jsonObject4.optString("postTitle"));
							//resp.setThumbnail((String[])(jsonObject3.optString("thumbURL").split(",")));
							Object temp2 = jsonObject3.opt("thumbURL");
							if (temp2 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								mSolutionResp.setThumbURL(imgList);
							}
							mSolutionResp.setIsSolution(jsonObject4.optBoolean("isSolution"));
							mSolutionResp.setEffect(jsonObject4.optString("effect"));
							mSolutionResp.setPart(jsonObject4.optString("part"));
							mSolutionResp.setSkin(jsonObject4.optString("skin"));
							mSolutionResp.setInUsed(jsonObject4.optBoolean("inUsed"));
							JSONObject jsonObject5 = new JSONObject(jsonObject4.optString("updateTime"));
							mSolutionResp.setUpdateTime(jsonObject5.optLong("time"));
							mSolutionResp.setScores(jsonObject4.optDouble("score"));
							resList.add(mSolutionResp);
						}
						resp.setSolutions(resList);
						
						SolutionListSave saveBean = new SolutionListSave();
						saveBean.setSolutionList(resList);
						saveBean.setType(SOLUTION_LIST_BY_PART);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_PART), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						} else {
							if (reTry2) {
								reTry2 = false;
								getSolutionListPartLocal(bean);
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler2.sendMessage(msg);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_PART), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (reTry2) {
							reTry2 = false;
							getSolutionListPartLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getSolutionListPartLocal(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	public void getSolutionListPartLocal(final SolutionListPartReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_GET_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_PART), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListPartLocal " + result);
				SolutionListResp resp = new SolutionListResp();
				SolutionListSave respSave = GsonUtil.getGson().fromJson(result, SolutionListSave.class);
				List<SolutionResp> beanList = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getSolutionListPartServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListPartServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setSolutions(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getSolutionListPartServer(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	

	//根据功效显示护肤方案列表
	@Override
	public void solutionListEffectByTypeAsync(SolutionListEffectReq bean, final BaseCall<SolutionListResp> call, int type) {
		handler3 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolutionListResp) msg.obj);
					reTry3 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListEffectServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListEffectLocal(bean);
		}
	}
	
	public void getSolutionListEffectServer(final SolutionListEffectReq bean) {

		RadarProxy.getInstance(context).startServerData(writeSolutionEffectParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				SolutionListResp resp = new SolutionListResp();
				Log.d("Radar", "getSolutionListEffectServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<SolutionResp> resList = new ArrayList<SolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							SolutionResp mSolutionResp = new SolutionResp();
							
							mSolutionResp.setPostId(jsonObject4.optLong("postId"));
							mSolutionResp.setParentId(jsonObject4.optLong("pid"));
							mSolutionResp.setTopicId(jsonObject4.optLong("topicId"));
							mSolutionResp.setPostLevel(jsonObject4.optInt("postLevel"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setUserName(jsonObject4.optString("username"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							mSolutionResp.setPostTitle(jsonObject4.optString("postTitle"));
							//resp.setThumbnail((String[])(jsonObject3.optString("thumbURL").split(",")));
							Object temp2 = jsonObject3.opt("thumbURL");
							if (temp2 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								mSolutionResp.setThumbURL(imgList);
							}
							mSolutionResp.setIsSolution(jsonObject4.optBoolean("isSolution"));
							mSolutionResp.setEffect(jsonObject4.optString("effect"));
							mSolutionResp.setPart(jsonObject4.optString("part"));
							mSolutionResp.setSkin(jsonObject4.optString("skin"));
							mSolutionResp.setInUsed(jsonObject4.optBoolean("inUsed"));
							JSONObject jsonObject5 = new JSONObject(jsonObject4.optString("updateTime"));
							mSolutionResp.setUpdateTime(jsonObject5.optLong("time"));
							mSolutionResp.setScores(jsonObject4.optDouble("score"));
							resList.add(mSolutionResp);
						}
						resp.setSolutions(resList);
						
						SolutionListSave saveBean = new SolutionListSave();
						saveBean.setSolutionList(resList);
						saveBean.setType(SOLUTION_LIST_BY_EFFECT);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_EFFECT), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						} else {
							if (reTry3) {
								reTry3 = false;
								getSolutionListEffectLocal(bean);
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler3.sendMessage(msg);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_EFFECT), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (reTry3) {
							reTry3 = false;
							getSolutionListEffectLocal(bean);
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
					getSolutionListEffectLocal(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	public void getSolutionListEffectLocal(final SolutionListEffectReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_GET_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_BY_EFFECT), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListEffectLocal " + result);
				SolutionListResp resp = new SolutionListResp();
				SolutionListSave respSave = GsonUtil.getGson().fromJson(result, SolutionListSave.class);
				List<SolutionResp> beanList = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry3 && ((beanList == null) || (beanList.size() == 0))) {
					reTry3 = false;
					getSolutionListEffectServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListEffectServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setSolutions(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
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
					getSolutionListEffectServer(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	
	// 护肤方案排行榜
	@Override
	public void solutionListRankByTypeAsync(SolutionRankReq bean, final BaseCall<SolutionListResp> call, int type) {
		handler4 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolutionListResp) msg.obj);
					reTry4 = true;
				}
			}
		};

		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListRankServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListRankLocal(bean);
		}
	}
	
	public void getSolutionListRankServer(final SolutionRankReq bean) {

		RadarProxy.getInstance(context).startServerData(writeRankParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				SolutionListResp resp = new SolutionListResp();
				Log.d("Radar", "getSolutionListRankServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage((String) jsonObject2.optString("msg"));// ok
					resp.setStatus((String) jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("posts");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("posts"));
						List<SolutionResp> resList = new ArrayList<SolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							SolutionResp mSolutionResp = new SolutionResp();
							
							mSolutionResp.setPostId(jsonObject4.optLong("postId"));
							mSolutionResp.setParentId(jsonObject4.optLong("parentId"));
							mSolutionResp.setTopicId(jsonObject4.optLong("topicId"));
							mSolutionResp.setTopicTitle(jsonObject4.optString("topicTitle"));
							mSolutionResp.setPostLevel(jsonObject4.optInt("postLevel"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setUserName(jsonObject4.optString("username"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							mSolutionResp.setPostTitle(jsonObject4.optString("postTitle"));
							mSolutionResp.setThumbString((String[])(jsonObject4.optString("thumbURL").split(",")));
							/*Object temp2 = jsonObject4.opt("thumbURL");
							if (temp2 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int j = 0; j < jsonArrImg.length(); j++) {
									String imgItem = (String) jsonArrImg.get(j);
									imgList.add(imgItem);
								}
								mSolutionResp.setThumbURL(imgList);
							}*/
							mSolutionResp.setIsSolution(jsonObject4.optBoolean("selectedToSolution"));
							mSolutionResp.setEffect(jsonObject4.optString("effect"));
							mSolutionResp.setPart(jsonObject4.optString("part"));
							mSolutionResp.setSkin(jsonObject4.optString("skin"));
							mSolutionResp.setFavorite(jsonObject4.optInt("favorite"));
							mSolutionResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
							mSolutionResp.setInUsed(jsonObject4.optBoolean("inUsed"));
							mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mSolutionResp.setScores(jsonObject4.optDouble("score"));
							resList.add(mSolutionResp);
						}
						resp.setSolutions(resList);
						
						SolutionListSave saveBean = new SolutionListSave();
						saveBean.setSolutionList(resList);
						saveBean.setType(SOLUTION_LIST_RANK);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_RANK), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler4.sendMessage(msg);
						} else {
							if (reTry4) {
								reTry4 = false;
								getSolutionListRankLocal(bean);
							} else {
								resp.setStatus("FAILED");
								Message msg = Message.obtain();
								msg.obj = resp;
								handler4.sendMessage(msg);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_DELETE_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_RANK), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					} else {
						if (reTry4) {
							reTry4 = false;
							getSolutionListRankLocal(bean);
						} else {
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler4.sendMessage(msg);
						}
					}
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry4) {
					reTry4 = false;
					getSolutionListRankLocal(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	public void getSolutionListRankLocal(final SolutionRankReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_LIST_GET_ONE_BYTYPE, Integer.toString(SOLUTION_LIST_RANK), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListRankLocal " + result);
				SolutionListResp resp = new SolutionListResp();
				SolutionListSave respSave = GsonUtil.getGson().fromJson(result, SolutionListSave.class);
				List<SolutionResp> beanList = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry4 && ((beanList == null) || (beanList.size() == 0))) {
					reTry4 = false;
					getSolutionListRankServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListRankServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setSolutions(beanList);
						resp.setSuccess(true);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						resp.setMessage("ok");
						if ((beanList == null) || (beanList.size() == 0)) {
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry4) {
					reTry4 = false;
					getSolutionListRankServer(bean);
				} else {
					SolutionListResp resp = new SolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}

	private ServerRequestParams writeSolutionParams(SolutionListReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeSolutionPartParams(SolutionListPartReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionListByPart(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("part", bean.getPart());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeSolutionEffectParams(SolutionListEffectReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionListByEffect(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("effect", bean.getEffect());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeRankParams(SolutionRankReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSkinSolutionRank(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
}
