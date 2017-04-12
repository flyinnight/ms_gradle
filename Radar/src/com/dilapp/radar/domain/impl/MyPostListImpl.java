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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.GetPostList.PostListSave;
import com.dilapp.radar.domain.MyPostList;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class MyPostListImpl extends MyPostList {
	private Handler handler1;
	private Handler handler2;
	private boolean reTry1;
	private boolean reTry2;
	private Context context;
	private ServerRequestParams params;

	public MyPostListImpl(Context context) {
		this.context = context;
		reTry1 = true;
		reTry2 = true;
	}


	// 获取自己所发帖子的列表
	@Override
	public void getMyCreatPostByTypeAsync(MyCreatPostReq bean, final BaseCall<MyCreatPostResp> call, int type) {
		handler1 = new Handler() {
			@SuppressWarnings("NewApi")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MyCreatPostResp) msg.obj);
					reTry1 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getMyCreatPostServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getMyCreatPostLocal(bean);
		}
	}
	
	public void getMyCreatPostServer(final MyCreatPostReq bean) {

		RadarProxy.getInstance(context).startServerData(writeMyCreatParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MyCreatPostResp resp = new MyCreatPostResp();
				Log.d("Radar", "getMyCreatPostAsync: " + result);
				try {
					jsonObject = new JSONObject(result);

					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					// 后台返回
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("totalPage"));
						resp.setPageNo(jsonObj.optInt("pageNo"));
						Object temp1 = jsonObj.opt("mainPosts");
						if (temp1 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("mainPosts"));
							
							List<MPostResp> resList = new ArrayList<MPostResp>();
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
								MPostResp mPostResp = new MPostResp();
								mPostResp.setId(jsonObject4.optLong("id"));
								mPostResp.setTopicId(jsonObject4.optLong("topicId"));
								mPostResp.setPostLevel(jsonObject4.optInt("postLevel"));
								mPostResp.setUserId(jsonObject4.optString("userId"));
								mPostResp.setPostTitle(jsonObject4.optString("postTitle"));
								mPostResp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
								mPostResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
								mPostResp.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
								mPostResp.setEffect(jsonObject4.optString("effect"));
								mPostResp.setReport(jsonObject4.optBoolean("reported"));
								mPostResp.setOnTop(jsonObject4.optBoolean("onTop"));
								Object temp2 = jsonObject4.opt("thumbURL");
								if (temp2 != null) {
									JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
									List<String> imgList = new ArrayList<String>();
									for (int j = 0; j < jsonArrImg.length(); j++) {
										String imgItem = (String) jsonArrImg.get(j);
										imgList.add(imgItem);
									}
									mPostResp.setThumbURL(imgList);
								}
								mPostResp.setLike(jsonObject4.optInt("favorite"));
								mPostResp.setDislike(jsonObject4.optInt("disfavorite"));
								mPostResp.setUpdateTime(jsonObject4.optLong("updateTime"));
								mPostResp.setTotalFollows(jsonObject4.optInt("totalFollows"));
								mPostResp.setPostViewCount(jsonObject4.optInt("postViewCount"));
								
								resList.add(mPostResp);
							}
							resp.setDatas(resList);
							
							PostListSave saveBean = new PostListSave();
							saveBean.setPostList(resList);
							saveBean.setType(GetPostList.POST_LIST_BY_SEND);
							saveBean.setUpdateTime(System.currentTimeMillis());
							RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
							
							Message msg = Message.obtain();
							msg.obj = resp;
							handler1.sendMessage(msg);
						} else {
							handleCreatFailure(bean, statusCode);
						}
					} else {
						handleCreatFailure(bean, statusCode);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					handleCreatFailure(bean, statusCode);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry1) {
					reTry1 = false;
					getMyCreatPostLocal(bean);
				} else {
					MyCreatPostResp resp = new MyCreatPostResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}
	
	private void handleCreatFailure(MyCreatPostReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_SEND), null);
			MyCreatPostResp resp = new MyCreatPostResp();
			resp.setStatus("FAILED");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler1.sendMessage(msg);
		} else {
			if (reTry1) {
				reTry1 = false;
				getMyCreatPostLocal(bean);
			} else {
				MyCreatPostResp resp = new MyCreatPostResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
			}
		}
	}
	
	public void getMyCreatPostLocal(final MyCreatPostReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_SEND), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getMyCreatPostLocal " + result);
				MyCreatPostResp resp = new MyCreatPostResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();

				if (reTry1 && ((beanList == null) || (beanList.size() == 0))) {
					reTry1 = false;
					getMyCreatPostServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getMyCreatPostServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setDatas(beanList);
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
					getMyCreatPostServer(bean);
				} else {
					MyCreatPostResp resp = new MyCreatPostResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler1.sendMessage(msg);
				}
			}
		});
	}

	
	//获取自己收藏帖子的列表
	@Override
	public void getMyStorePostByTypeAsync(MyStorePostReq bean, final BaseCall<MyStorePostResp> call, int type) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MyStorePostResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getMyStorePostServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getMyStorePostLocal(bean);
		}
	}
	
	public void getMyStorePostServer(final MyStorePostReq bean) {

		RadarProxy.getInstance(context).startServerData(writeMyStoreupParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				MyStorePostResp resp = new MyStorePostResp();
				Log.d("Radar", "getMyStorePostAsync: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));// true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));// ok
					resp.setStatus(jsonObject2.optString("status"));//SUCCESS
					Object temp = jsonObject2.opt("values");
					if (temp != null) {
						JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
						resp.setTotalPage(jsonObj.optInt("allPages"));// ok
						resp.setPageNo(jsonObj.optInt("currPage"));//SUCCESS
						resp.setType(jsonObj.optString("type"));//SUCCESS
						
						Object temp1 = jsonObj.opt("list");
						if (temp1 != null) {
							JSONArray jsonArr = new JSONArray(jsonObj.optString("list"));
							List<MPostResp> resList = new ArrayList<MPostResp>();
							for (int i = 0; i < jsonArr.length(); i++) {
								JSONObject jsonObject3 = (JSONObject) jsonArr.get(i);
								MPostResp postResp = new MPostResp();

								postResp.setPostTitle(jsonObject3.optString("title"));// "Whattimetostickmasknight"
								postResp.setId(jsonObject3.optLong("id"));
								postResp.setPostContent(jsonObject3.optString("desc"));
								postResp.setUserName(jsonObject3.optString("name"));
								postResp.setUserId(jsonObject3.optString("uid"));
								Object temp2 = jsonObject3.opt("thumbURL");
								if (temp2 != null) {
									JSONArray jsonArrImg = new JSONArray(jsonObject3.optString("thumbURL"));
									List<String> imgList = new ArrayList<String>();
									for (int j = 0; j < jsonArrImg.length(); j++) {
										String imgItem = (String) jsonArrImg.get(j);
										imgList.add(imgItem);
									}
									postResp.setThumbURL(imgList);
								}
								postResp.setUpdateTime(jsonObject3.optLong("timestamp"));// 1432190979000
								postResp.setLike(jsonObject3.optInt("favorite"));
								postResp.setTotalFollows(jsonObject3.optInt("totalFollows"));
								postResp.setPostViewCount(jsonObject3.optInt("postViewCount"));
								resList.add(postResp);
							}
							resp.setDatas(resList);
							
							PostListSave saveBean = new PostListSave();
							saveBean.setPostList(resList);
							saveBean.setType(GetPostList.POST_LIST_BY_STORE);
							saveBean.setUpdateTime(System.currentTimeMillis());
							RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
							
							Message msg = Message.obtain();
							msg.obj = resp;
							handler2.sendMessage(msg);
							
						} else {
							handleStoreFailure(bean, statusCode);
						}
					} else {
						handleStoreFailure(bean, statusCode);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					handleStoreFailure(bean, statusCode);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry2) {
					reTry2 = false;
					getMyStorePostLocal(bean);
				} else {
					MyStorePostResp resp = new MyStorePostResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	private void handleStoreFailure(MyStorePostReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_DELETE_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_STORE), null);
			MyStorePostResp resp = new MyStorePostResp();
			resp.setStatus("FAILED");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler2.sendMessage(msg);
		} else {
			if (reTry2) {
				reTry2 = false;
				getMyStorePostLocal(bean);
			} else {
				MyStorePostResp resp = new MyStorePostResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		}
	}
	
	public void getMyStorePostLocal(final MyStorePostReq bean) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_STORE), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getMyStorePostLocal " + result);
				MyStorePostResp resp = new MyStorePostResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();

				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getMyStorePostServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getMyStorePostServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setDatas(beanList);
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
					getMyStorePostServer(bean);
				} else {
					MyStorePostResp resp = new MyStorePostResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	
	
	private ServerRequestParams writeMyCreatParams(MyCreatPostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getMainPostsSentByUser(null));
		Map<String, Object> param = new HashMap<String, Object>();
		int pageSize = 7;
		if (bean.getPageSize() != 0) {
			pageSize = bean.getPageSize();
		}
		param.put("pageSize", Integer.toString(pageSize));
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		param.put("token", HttpConstant.TOKEN);
		params.setRequestParam(param);
		return params;
	}
	
	private ServerRequestParams writeMyStoreupParams(MyStorePostReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.userFollowTopicList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("type", "stored");
		param.put("pageNo", Integer.toString(bean.getPageNo()));
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
