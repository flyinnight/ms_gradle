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
import android.text.TextUtils;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionListData.SolCommentResp;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class SolutionListDataImpl extends SolutionListData {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Handler handler5;
	private boolean reTry1;
	private boolean reTry2;
	private boolean reTry3;
	private boolean reTry4;
	private Context context;
	private ServerRequestParams params;

	public SolutionListDataImpl(Context context) {
		this.context = context;
		reTry1 = true;
		reTry2 = true;
		reTry3 = true;
		reTry4 = true;
	}

	
	//获取护肤方案列表（大全/分类）
	@Override
	public void getSolutionListByTypeAsync(MSolutionListReq bean, final BaseCall<MSolutionListResp> call, int type) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionListResp) msg.obj);
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
	
	private void getSolutionListServerTag(final MSolutionListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MSolutionListResp resp = new MSolutionListResp();
				Log.d("Radar", "getSolutionListServerTag: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<MSolutionResp> resList = new ArrayList<MSolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							MSolutionResp mSolutionResp = analyzeRespSave((JSONObject)jsonArr.get(i));
							resList.add(mSolutionResp);
						}
						resp.setDatas(resList);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					} else {
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MSolutionListResp resp = new MSolutionListResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		});
	}
	
	private void getSolutionListServer(final MSolutionListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MSolutionListResp resp = new MSolutionListResp();
				Log.d("Radar", "getSolutionListServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<MSolutionResp> resList = new ArrayList<MSolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							MSolutionResp mSolutionResp = analyzeRespSave((JSONObject)jsonArr.get(i));
							resList.add(mSolutionResp);
						}
						resp.setDatas(resList);
						
						SolutionDataSave saveBean = new SolutionDataSave();
						saveBean.setSolutionList(resp);
						saveBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
						saveBean.setUpdateTime(System.currentTimeMillis());
						if (TextUtils.isEmpty(bean.getTag())) {
							saveBean.setTag("");
						} else {
							saveBean.setTag(bean.getTag());
						}
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), new ClientCallbackImpl() {
							@Override
							public void onSuccess(String result) {
								Log.d("Radar", "getSolutionListServer: SOLUTION_DATA_SAVE_ONE_BYTYPE " + result);
								
								SolutionDataGetDelete getBean = new SolutionDataGetDelete();
								getBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
								getBean.setPageNum(bean.getPageNo());
								if (TextUtils.isEmpty(bean.getTag())) {
									getBean.setTag("");
								} else {
									getBean.setTag(bean.getTag());
								}

								RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
									@Override
									public void onSuccess(String result) {
										Log.d("Radar", "getSolutionListServer: SOLUTION_DATA_GET_ONE_BYTYPE" + result);
										
										SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
										MSolutionListResp resp = respSave.getSolutionList();
										if (resp == null) {
											resp = new MSolutionListResp();
										}
										if ((resp.getDatas() == null) || (resp.getDatas().size() == 0)) {
											resp.setStatus("FAILED");
										}
										Message msg = Message.obtain();
										msg.obj = resp;
										handler1.sendMessage(msg);
									}

									@Override
									public void onFailure(String result) {
										System.out.println(result);
										MSolutionListResp resp = new MSolutionListResp();
										resp.setStatus("FAILED");
										Message msg = Message.obtain();
										msg.obj = resp;
										handler1.sendMessage(msg);
									}
								});
							}

							@Override
							public void onFailure(String result) {
								System.out.println(result);
								if (reTry1) {
									reTry1 = false;
									getSolutionListLocal(bean);
								} else {
									MSolutionListResp resp = new MSolutionListResp();
									resp.setStatus("FAILED");
									Message msg = Message.obtain();
									msg.obj = resp;
									handler1.sendMessage(msg);
								}
							}
						});
						
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
				if (reTry1) {
					reTry1 = false;
					getSolutionListLocal(bean);
				} else {
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	private MSolutionResp analyzeRespSave(JSONObject jsonObject4) {

		MSolutionResp mSolutionResp = new MSolutionResp();
		
		mSolutionResp.setRank(jsonObject4.optInt("rank"));
		mSolutionResp.setSolutionId(jsonObject4.optLong("id"));
		mSolutionResp.setEffect((String[])(jsonObject4.optString("effect").split(",")));
		mSolutionResp.setPart((String[])(jsonObject4.optString("part").split(",")));
		mSolutionResp.setTitle(jsonObject4.optString("title"));
		mSolutionResp.setIntroduction(jsonObject4.optString("introduction"));
		mSolutionResp.setContent(jsonObject4.optString("content"));
		mSolutionResp.setCoverImgUrl(jsonObject4.optString("coverUrl"));
		mSolutionResp.setCoverThumbImgUrl(jsonObject4.optString("coverThumbnailUrl"));
		
		mSolutionResp.setUseCycle(jsonObject4.optInt("cycle"));
		mSolutionResp.setScore(jsonObject4.optDouble("score"));
		mSolutionResp.setMyScore(jsonObject4.optInt("myScore"));
		mSolutionResp.setUsedCount(jsonObject4.optInt("usedCount"));
		mSolutionResp.setStoreUpCount(jsonObject4.optInt("usestoreupCount"));
		mSolutionResp.setCreateTime(jsonObject4.optLong("createTime"));
		mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
		mSolutionResp.setUserId(jsonObject4.optString("userId"));
		mSolutionResp.setNickName(jsonObject4.optString("userName"));
		mSolutionResp.setPortrait(jsonObject4.optString("portrait"));

		return mSolutionResp;
	}
	
	private void handleFailure(MSolutionListReq bean, int statusCode) {
		MSolutionListResp resp = new MSolutionListResp();
		
		if (statusCode == BaseResp.OK) {
			SolutionDataGetDelete delBean = new SolutionDataGetDelete();
			delBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
			if (TextUtils.isEmpty(bean.getTag())) {
				delBean.setTag("");
			} else {
				delBean.setTag(bean.getTag());
			}
			RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
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
	
	private void getSolutionListLocal(final MSolutionListReq bean) {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
		getBean.setPageNum(bean.getPageNo());
		if (TextUtils.isEmpty(bean.getTag())) {
			getBean.setTag("");
		} else {
			getBean.setTag(bean.getTag());
		}

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				MSolutionListResp resp = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry1 && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry1 = false;
					getSolutionListServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListServer(bean);
					} else {
						if (resp == null) {
							resp = new MSolutionListResp();
						}
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						if ((resp.getDatas() == null) || (resp.getDatas().size() == 0)) {
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
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	
	//获取用户收藏的护肤方案列表
	@Override
	public void getSolutionListStoreupByTypeAsync(SolutionListStoreupReq bean, final BaseCall<MSolutionListResp> call, int type) {
		handler2 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionListResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListStoreupServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListStoreupLocal(bean);
		}
	}
	
	private void getSolutionListStoreupServer(final SolutionListStoreupReq bean) {

		RadarProxy.getInstance(context).startServerData(writeStoreupParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MSolutionListResp resp = new MSolutionListResp();
				Log.d("Radar", "getSolutionListStoreupServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<MSolutionResp> resList = new ArrayList<MSolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							MSolutionResp mSolutionResp = new MSolutionResp();
							
							mSolutionResp.setSolutionId(jsonObject4.optLong("id"));
							mSolutionResp.setEffect((String[])(jsonObject4.optString("effect").split(",")));
							mSolutionResp.setPart((String[])(jsonObject4.optString("part").split(",")));
							mSolutionResp.setTitle(jsonObject4.optString("title"));
							mSolutionResp.setIntroduction(jsonObject4.optString("introduction"));
							mSolutionResp.setContent(jsonObject4.optString("content"));
							mSolutionResp.setCoverImgUrl(jsonObject4.optString("coverUrl"));
							mSolutionResp.setCoverThumbImgUrl(jsonObject4.optString("coverThumbnailUrl"));
							
							mSolutionResp.setUseCycle(jsonObject4.optInt("cycle"));
							mSolutionResp.setScore(jsonObject4.optDouble("score"));
							mSolutionResp.setMyScore(jsonObject4.optInt("myScore"));
							mSolutionResp.setUsedCount(jsonObject4.optInt("usedCount"));
							mSolutionResp.setStoreUpCount(jsonObject4.optInt("usestoreupCount"));
							mSolutionResp.setCreateTime(jsonObject4.optLong("createTime"));
							mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setNickName(jsonObject4.optString("userName"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							resList.add(mSolutionResp);
						}
						resp.setDatas(resList);
						
						SolutionDataSave saveBean = new SolutionDataSave();
						saveBean.setSolutionList(resp);
						saveBean.setType(SolutionListData.SOLUTION_LIST_STOREUP);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							SolutionDataGetDelete delBean = new SolutionDataGetDelete();
							delBean.setType(SolutionListData.SOLUTION_LIST_STOREUP);
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
						} else {
							if (reTry2) {
								reTry2 = false;
								getSolutionListStoreupLocal(bean);
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
						SolutionDataGetDelete delBean = new SolutionDataGetDelete();
						delBean.setType(SolutionListData.SOLUTION_LIST_STOREUP);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
					} else {
						if (reTry2) {
							reTry2 = false;
							getSolutionListStoreupLocal(bean);
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
					getSolutionListStoreupLocal(bean);
				} else {
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	private void getSolutionListStoreupLocal(final SolutionListStoreupReq bean) {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_LIST_STOREUP);

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListStoreupLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				MSolutionListResp resp = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry2 && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry2 = false;
					getSolutionListStoreupServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListStoreupServer(bean);
					} else {
						if (resp == null) {
							resp = new MSolutionListResp();
						}
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						if ((resp.getDatas() == null) || (resp.getDatas().size() == 0)) {
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
					getSolutionListStoreupServer(bean);
				} else {
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	
	}
	

	//获取用户发布的护肤方案列表
	@Override
	public void getSolutionListCreateByTypeAsync(SolutionListCreateReq bean, final BaseCall<MSolutionListResp> call, int type) {
		handler3 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MSolutionListResp) msg.obj);
					reTry3 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListCreateServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListCreateLocal(bean);
		}
	}
	
	private void getSolutionListCreateServer(final SolutionListCreateReq bean) {

		RadarProxy.getInstance(context).startServerData(writeCreateParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MSolutionListResp resp = new MSolutionListResp();
				Log.d("Radar", "getSolutionListCreateServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("solutions");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("solutions"));
						List<MSolutionResp> resList = new ArrayList<MSolutionResp>();

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							MSolutionResp mSolutionResp = new MSolutionResp();
							
							mSolutionResp.setSolutionId(jsonObject4.optLong("id"));
							mSolutionResp.setEffect((String[])(jsonObject4.optString("effect").split(",")));
							mSolutionResp.setPart((String[])(jsonObject4.optString("part").split(",")));
							mSolutionResp.setTitle(jsonObject4.optString("title"));
							mSolutionResp.setIntroduction(jsonObject4.optString("introduction"));
							mSolutionResp.setContent(jsonObject4.optString("content"));
							mSolutionResp.setCoverImgUrl(jsonObject4.optString("coverUrl"));
							mSolutionResp.setCoverThumbImgUrl(jsonObject4.optString("coverThumbnailUrl"));
							mSolutionResp.setUseCycle(jsonObject4.optInt("cycle"));
							mSolutionResp.setScore(jsonObject4.optDouble("score"));
							mSolutionResp.setMyScore(jsonObject4.optInt("myScore"));
							mSolutionResp.setUsedCount(jsonObject4.optInt("usedCount"));
							mSolutionResp.setStoreUpCount(jsonObject4.optInt("usestoreupCount"));
							mSolutionResp.setCreateTime(jsonObject4.optLong("createTime"));
							mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setNickName(jsonObject4.optString("userName"));
							mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							resList.add(mSolutionResp);
						}
						resp.setDatas(resList);
						
						SolutionDataSave saveBean = new SolutionDataSave();
						saveBean.setSolutionList(resp);
						saveBean.setType(SolutionListData.SOLUTION_LIST_CREATE);
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (statusCode == BaseResp.OK) {
							SolutionDataGetDelete delBean = new SolutionDataGetDelete();
							delBean.setType(SolutionListData.SOLUTION_LIST_CREATE);
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler3.sendMessage(msg);
						} else {
							if (reTry3) {
								reTry3 = false;
								getSolutionListCreateLocal(bean);
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
						SolutionDataGetDelete delBean = new SolutionDataGetDelete();
						delBean.setType(SolutionListData.SOLUTION_LIST_CREATE);
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						if (reTry3) {
							reTry3 = false;
							getSolutionListCreateLocal(bean);
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
					getSolutionListCreateLocal(bean);
				} else {
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}
	
	private void getSolutionListCreateLocal(final SolutionListCreateReq bean) {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_LIST_CREATE);

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListCreateLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				MSolutionListResp resp = respSave.getSolutionList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry3 && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry3 = false;
					getSolutionListCreateServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListCreateServer(bean);
					} else {
						if (resp == null) {
							resp = new MSolutionListResp();
						}
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						if ((resp.getDatas() == null) || (resp.getDatas().size() == 0)) {
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
					getSolutionListCreateServer(bean);
				} else {
					MSolutionListResp resp = new MSolutionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	
	}
	
	
	//获取护肤方案评论列表
	@Override
	public void getSolutionCommentListByTypeAsync(SolCommentListReq bean, final BaseCall<SolCommentListResp> call, int type) {
		handler4 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((SolCommentListResp) msg.obj);
					reTry4 = true;
				}
			}
		};

		if (type == GetPostList.GET_DATA_SERVER) {
			getSolutionListCommentServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getSolutionListCommentServer(bean);  //与详情保持一致，默认返回网络数据，失败的话返回缓存数据
		}
	}
	
	private void getSolutionListCommentServer(final SolCommentListReq bean) {

		RadarProxy.getInstance(context).startServerData(writeCommentParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				SolCommentListResp resp = new SolCommentListResp();
				Log.d("Radar", "getSolutionListCommentServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					resp.setTotalCount(jsonObject3.optInt("totalCount"));
					
					Object temp = jsonObject3.opt("values");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("values"));
						List<SolCommentResp> datas = new ArrayList<SolCommentResp>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							SolCommentResp mCommentResp = new SolCommentResp();
							
							mCommentResp.setHasMore(jsonObject4.optBoolean("hasMore"));
							mCommentResp.setCommentId(jsonObject4.optLong("id"));
							mCommentResp.setParentCommId(jsonObject4.optLong("parentId"));
							mCommentResp.setSolutionId(jsonObject4.optLong("solutionId"));
							mCommentResp.setContent(jsonObject4.optString("content"));
							//mCommentResp.setEffect((String[])(jsonObject4.optString("effect").split(",")));
							//mCommentResp.setPart((String[])(jsonObject4.optString("part").split(",")));
							//mCommentResp.setTitle(jsonObject4.optString("title"));
							//mCommentResp.setIntroduction(jsonObject4.optString("introduction"));
							//mCommentResp.setUseCycle(jsonObject4.optInt("cycle"));
							//mCommentResp.setScore(jsonObject4.optDouble("score"));
							//mCommentResp.setMyScore(jsonObject4.optInt("myScore"));
							//mCommentResp.setUsedCount(jsonObject4.optInt("usedCount"));
							//mCommentResp.setStoreUpCount(jsonObject4.optInt("storeupCount"));
							mCommentResp.setLikeCount(jsonObject4.optInt("likeCount"));
							mCommentResp.setIsLike(jsonObject4.optBoolean("like"));
							mCommentResp.setCreateTime(jsonObject4.optLong("createTime"));
							mCommentResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mCommentResp.setUserId(jsonObject4.optString("userId"));
							mCommentResp.setNickName(jsonObject4.optString("userName"));
							mCommentResp.setToUserId(jsonObject4.optString("toUserId"));
							//mCommentResp.setToNickName(jsonObject4.optString("toName"));
							mCommentResp.setPortrait(jsonObject4.optString("portrait"));
							
							Object temp1 = jsonObject4.opt("comments");
							if (temp1 != null) {
								JSONArray jsonArr1 = new JSONArray(jsonObject4.optString("comments"));
								List<MSolutionResp> resList = new ArrayList<MSolutionResp>();
								for (int j = 0; j < jsonArr1.length(); j++) {
									JSONObject jsonObject5 = (JSONObject) jsonArr1.get(j);
									MSolutionResp mSolutionResp1 = new MSolutionResp();
									
									mSolutionResp1.setCommentId(jsonObject5.optLong("id"));
									mSolutionResp1.setParentCommId(jsonObject5.optLong("parentId"));
									mSolutionResp1.setSolutionId(jsonObject5.optLong("solutionId"));
									mSolutionResp1.setContent(jsonObject5.optString("content"));
									//mSolutionResp1.setEffect((String[])(jsonObject5.optString("effect").split(",")));
									//mSolutionResp1.setPart((String[])(jsonObject5.optString("part").split(",")));
									//mSolutionResp1.setTitle(jsonObject5.optString("title"));
									//mSolutionResp1.setIntroduction(jsonObject5.optString("introduction"));
									//mSolutionResp1.setUseCycle(jsonObject5.optInt("cycle"));
									//mSolutionResp1.setScore(jsonObject5.optDouble("score"));
									//mSolutionResp1.setMyScore(jsonObject5.optInt("myScore"));
									//mSolutionResp1.setUsedCount(jsonObject5.optInt("usedCount"));
									//mSolutionResp1.setStoreUpCount(jsonObject5.optInt("storeupCount"));
									mSolutionResp1.setLikeCount(jsonObject5.optInt("likeCount"));
									mSolutionResp1.setIsLike(jsonObject5.optBoolean("like"));
									//mSolutionResp1.setHasMore(jsonObject5.optBoolean("hasMore"));
									mSolutionResp1.setCreateTime(jsonObject5.optLong("createTime"));
									mSolutionResp1.setUpdateTime(jsonObject5.optLong("updateTime"));
									mSolutionResp1.setUserId(jsonObject5.optString("userId"));
									mSolutionResp1.setNickName(jsonObject5.optString("userName"));
									mSolutionResp1.setToUserId(jsonObject5.optString("toUserId"));
									mSolutionResp1.setToNickName(jsonObject5.optString("toUserName"));
									mSolutionResp1.setPortrait(jsonObject5.optString("portrait"));
									resList.add(mSolutionResp1);
								}
								mCommentResp.setFollowComments(resList);
							}
							
							datas.add(mCommentResp);
						}
						resp.setDatas(datas);
						
						SolutionDataSave saveBean = new SolutionDataSave();
						saveBean.setCommentList(resp);
						saveBean.setType(SolutionListData.SOLUTION_LIST_COMMENT);
						saveBean.setSolutionId(bean.getSolutionId());
						saveBean.setUpdateTime(System.currentTimeMillis());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
						
					} else {
						if (statusCode == BaseResp.OK) {
							SolutionDataGetDelete delBean = new SolutionDataGetDelete();
							delBean.setType(SolutionListData.SOLUTION_LIST_COMMENT);
							delBean.setSolutionId(bean.getSolutionId());
							RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
							resp.setStatus("FAILED");
							Message msg = Message.obtain();
							msg.obj = resp;
							handler4.sendMessage(msg);
						} else {
							if (reTry4) {
								reTry4 = false;
								getSolutionListCommentLocal(bean);
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
						SolutionDataGetDelete delBean = new SolutionDataGetDelete();
						delBean.setType(SolutionListData.SOLUTION_LIST_COMMENT);
						delBean.setSolutionId(bean.getSolutionId());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					} else {
						if (reTry4) {
							reTry4 = false;
							getSolutionListCommentLocal(bean);
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
					getSolutionListCommentLocal(bean);
				} else {
					SolCommentListResp resp = new SolCommentListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	private void getSolutionListCommentLocal(final SolCommentListReq bean) {
		
		SolutionDataGetDelete getBean = new SolutionDataGetDelete();
		getBean.setType(SolutionListData.SOLUTION_LIST_COMMENT);
		getBean.setSolutionId(bean.getSolutionId());

		RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_GET_ONE_BYTYPE, GsonUtil.getGson().toJson(getBean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getSolutionListCommentLocal " + result);
				SolutionDataSave respSave = GsonUtil.getGson().fromJson(result, SolutionDataSave.class);
				SolCommentListResp resp = respSave.getCommentList();
				long updateTime = respSave.getUpdateTime();
				
				if (reTry4 && ((resp == null) || (resp.getTotalCount() == 0))) {
					reTry4 = false;
					getSolutionListCommentServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getSolutionListCommentServer(bean);
					} else {
						if (resp == null) {
							resp = new SolCommentListResp();
						}
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						if (resp.getTotalCount() == 0) {
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
					getSolutionListCommentServer(bean);
				} else {
					SolCommentListResp resp = new SolCommentListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	
	}
	
	
	//获取护肤方案二级评论列表
	@Override
	public void getSolution2ndCommentListAsync(SolCommentList2ndReq bean, final BaseCall<Sol2ndCommentListResp> call) {
		handler5 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((Sol2ndCommentListResp) msg.obj);
				}
			}
		};

		RadarProxy.getInstance(context).startServerData(write2ndCommentParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				Sol2ndCommentListResp resp = new Sol2ndCommentListResp();
				Log.d("Radar", "getSolution2ndCommentListAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					
					JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
					resp.setTotalPage(jsonObject3.optInt("totalPage"));
					resp.setPageNo(jsonObject3.optInt("pageNo"));
					
					Object temp = jsonObject3.opt("comments");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject3.optString("comments"));
						List<MSolutionResp> datas = new ArrayList<MSolutionResp>();
						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							MSolutionResp mSolutionResp = new MSolutionResp();
							
							mSolutionResp.setCommentId(jsonObject4.optLong("id"));
							mSolutionResp.setParentCommId(jsonObject4.optLong("parentId"));
							mSolutionResp.setSolutionId(jsonObject4.optLong("solutionId"));
							mSolutionResp.setContent(jsonObject4.optString("content"));
							//mSolutionResp.setEffect((String[])(jsonObject4.optString("effect").split(",")));
							//mSolutionResp.setPart((String[])(jsonObject4.optString("part").split(",")));
							//mSolutionResp.setTitle(jsonObject4.optString("title"));
							//mSolutionResp.setIntroduction(jsonObject4.optString("introduction"));
							//mSolutionResp.setUseCycle(jsonObject4.optInt("cycle"));
							//mSolutionResp.setScore(jsonObject4.optDouble("score"));
							//mSolutionResp.setMyScore(jsonObject4.optInt("myScore"));
							//mSolutionResp.setUsedCount(jsonObject4.optInt("usedCount"));
							//mSolutionResp.setStoreUpCount(jsonObject4.optInt("storeupCount"));
							mSolutionResp.setLikeCount(jsonObject4.optInt("likeCount"));
							mSolutionResp.setIsLike(jsonObject4.optBoolean("like"));
							mSolutionResp.setCreateTime(jsonObject4.optLong("createTime"));
							mSolutionResp.setUpdateTime(jsonObject4.optLong("updateTime"));
							mSolutionResp.setUserId(jsonObject4.optString("userId"));
							mSolutionResp.setNickName(jsonObject4.optString("userName"));
							mSolutionResp.setToUserId(jsonObject4.optString("toUserId"));
							mSolutionResp.setToNickName(jsonObject4.optString("toUserName"));
							//mSolutionResp.setPortrait(jsonObject4.optString("portrait"));
							
							datas.add(mSolutionResp);
						}
						resp.setDatas(datas);
						
					} else {
						resp.setStatus("FAILED");
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					resp.setStatus("FAILED");
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler5.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				Sol2ndCommentListResp resp = new Sol2ndCommentListResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler5.sendMessage(msg);
			}
		});
	}

	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}

	private ServerRequestParams writeParams(MSolutionListReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSolitionList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("tag", bean.getTag());
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeStoreupParams(SolutionListStoreupReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSolStoreupList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeCreateParams(SolutionListCreateReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSolCreateList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeCommentParams(SolCommentListReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getSolCommentList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("solutionId", Long.toString(bean.getSolutionId()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams write2ndCommentParams(SolCommentList2ndReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.get2ndSolCommentList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("commentId", Long.toString(bean.getCommentId()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
}
