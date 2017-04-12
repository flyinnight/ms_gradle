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

import com.dilapp.radar.domain.Banner;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList;
import com.dilapp.radar.domain.PostCollection;
import com.dilapp.radar.domain.Banner.BannerCollectionSave;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.XUtilsHelper;

public class PostCollectionImpl extends PostCollection {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Context context;
	private boolean reTry;
	private ServerRequestParams params;

	public PostCollectionImpl(Context context) {
		this.context = context;
		reTry = true;
	}

	//上传精选帖子图片
	@Override
	public void uploadCollectionImgAsync(List<String> imgs,
										 final BaseCall<UploadCollectionImgResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((UploadCollectionImgResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeImgParams(imgs),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						UploadCollectionImgResp resp = new UploadCollectionImgResp();
						Log.d("Radar", "uploadSelectedImgAsync: " + result);
						try {
							jsonObject = new JSONObject(result);

							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage(jsonObject2.optString("msg"));// ok
							resp.setStatus(jsonObject2.optString("status"));//SUCCESS
							JSONObject jsonObject3 = new JSONObject(jsonObject2.optString("values"));
							resp.setPicUrl(jsonObject3.optString("imageUrl"));// {"imageUrl":"topic/icon/1435390758441/666999.jpg"]}
							//resp.setStatus(jsonObject2.optString("ok"));//true

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
						UploadCollectionImgResp resp = new UploadCollectionImgResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler1.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	//更新精选帖子
	@Override
	public void editPostCollectionAsync(EditCollectionReq bean, final BaseCall<BaseResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeEditParams(bean),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						JSONObject jsonObject;
						BaseResp resp = new BaseResp();
						Log.d("Radar", "editPostCollectionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
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
						BaseResp resp = new BaseResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler2.sendMessage(msg);
						System.out.println(result);
					}
				});
	}

	//删除精选帖子
	@Override
	public void deletePostCollectionAsync(DeleteCollectionReq bean,
										  final BaseCall<BaseResp> call) {
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
						Log.d("Radar", "deletePostCollectionAsync: " + result);
						try {
							jsonObject = new JSONObject(result);
							resp.setSuccess(jsonObject.optBoolean("success"));//true,false
							resp.setStatusCode(jsonObject.optInt("statusCode"));
							JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
							resp.setMessage((String) jsonObject2.optString("msg"));// ok
							resp.setStatus((String)jsonObject2.optString("status"));//SUCCESS
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


	//获取精选帖子列表
	@Override
	public void getPostCollectionListByTypeAsync(int pageNo, final BaseCall<GetPostCollectionListResp> call, int type) {
		handler4 = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((GetPostCollectionListResp) msg.obj);
					reTry = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getCollectionServer(pageNo);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getCollectionLocal(pageNo);
		}
	}

	public void getCollectionServer(final int pageNo) {

		RadarProxy.getInstance(context).startServerData(writeGetParams(pageNo), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				int statusCode = BaseResp.OK;
				GetPostCollectionListResp resp = new GetPostCollectionListResp();
				Log.d("Radar", "getCollectionServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					// http return message
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					JSONObject jsonObj = new JSONObject(jsonObject2.optString("values"));
					
					Object temp = jsonObj.opt("solutions");
					if (temp != null) {
						List<PostCollectionResp> resList = new ArrayList<PostCollectionResp>();  //total
						List<PostCollectionResp> resList1 = new ArrayList<PostCollectionResp>();  //posts
						List<PostCollectionResp> resList2 = new ArrayList<PostCollectionResp>();  //solutions
						JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
						JSONObject jsonObject3 = new JSONObject(jsonObj.optString("solutions"));
						JSONArray jsonArr1 = new JSONArray(jsonObject3.optString("selected"));
						resp.setTotalPage(jsonObject3.optInt("totalPage"));
						resp.setPageNo(jsonObject3.optInt("pageNo"));

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							PostCollectionResp collectionResp = new PostCollectionResp();

							collectionResp.setPostId(jsonObject4.optLong("postId"));
							collectionResp.setTopicId(jsonObject4.optLong("topicId"));
							collectionResp.setTopicTitle(jsonObject4.optString("topicTitle"));
							collectionResp.setSlogan(jsonObject4.optString("slogan"));
							collectionResp.setpicUrl(jsonObject4.optString("picUrl"));
							collectionResp.setUpdateTime(jsonObject4.optLong("timestamp"));
							collectionResp.setPostUpdateTime(jsonObject4.optLong("postUpdateTime"));
							resList1.add(collectionResp);
						}
						
						for (int j = 0; j < jsonArr1.length(); j++) {
							JSONObject jsonObject5 = (JSONObject) jsonArr1.get(j);
							PostCollectionResp collectionResp = new PostCollectionResp();

							collectionResp.setSolutionId(jsonObject5.optLong("id"));
							collectionResp.setSlogan(jsonObject5.optString("slogan"));
							collectionResp.setSolutionTitle(jsonObject5.optString("title"));
							collectionResp.setpicUrl(jsonObject5.optString("coverUrl"));
							collectionResp.setUpdateTime(jsonObject5.optLong("updateTime"));
							resList2.add(collectionResp);
						}
						
						int k = 0;
						for (; k < resList1.size(); k++) {
							resList.add(resList1.get(k));
							if (k < resList2.size()) {
								resList.add(resList2.get(k));
							}
						}
						
						if (k < resList2.size()) {
							for (; k < resList2.size(); k++) {
								resList.add(resList2.get(k));
							}
						}
						resp.setDatas(resList);
						
					} else {
						List<PostCollectionResp> resList1 = new ArrayList<PostCollectionResp>();  //posts
						JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject4 = (JSONObject) jsonArr.get(i);
							PostCollectionResp collectionResp = new PostCollectionResp();

							collectionResp.setPostId(jsonObject4.optLong("postId"));
							collectionResp.setTopicId(jsonObject4.optLong("topicId"));
							collectionResp.setTopicTitle(jsonObject4.optString("topicTitle"));
							collectionResp.setSlogan(jsonObject4.optString("slogan"));
							collectionResp.setpicUrl(jsonObject4.optString("picUrl"));
							collectionResp.setUpdateTime(jsonObject4.optLong("timestamp"));
							collectionResp.setPostUpdateTime(jsonObject4.optLong("postUpdateTime"));
							resList1.add(collectionResp);
						}
						
						resp.setDatas(resList1);
					}
					
					BannerCollectionSave saveBean = new BannerCollectionSave();
					saveBean.setCollectionList(resp);
					saveBean.setType(Banner.LIST_BY_COLLECTION);
					saveBean.setUpdateTime(System.currentTimeMillis());
					RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_SAVE_ONE, GsonUtil.getGson().toJson(saveBean), null);
					
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);

				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
					if (statusCode == BaseResp.OK) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_DELETE_ONE, Integer.toString(Banner.LIST_BY_COLLECTION), null);
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					} else {
						if (reTry) {
							reTry = false;
							getCollectionLocal(pageNo);
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
				if (reTry) {
					reTry = false;
					getCollectionLocal(pageNo);
				} else {
					GetPostCollectionListResp resp = new GetPostCollectionListResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	
	public void getCollectionLocal(final int pageNo) {

		RadarProxy.getInstance(context).startLocalData(HttpConstant.BANNER_COLLECTION_GET_ONE, Integer.toString(Banner.LIST_BY_COLLECTION), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getCollectionLocal " + result);
				GetPostCollectionListResp resp = null;
				BannerCollectionSave beanSave = GsonUtil.getGson().fromJson(result, BannerCollectionSave.class);
				resp = beanSave.getCollectionList();
				long updateTime = beanSave.getUpdateTime();

				if (reTry && ((resp == null) || (resp.getDatas() == null) || (resp.getDatas().size() == 0))) {
					reTry = false;
					getCollectionServer(pageNo);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getCollectionServer(pageNo);
					} else {
						if ((resp == null)) {
							resp = new GetPostCollectionListResp();
							resp.setStatus("FAILED");
						} else {
							resp.setStatus("SUCCESS");
						}
						resp.setStatusCode(BaseResp.DATA_LOCAL);
						Message msg = Message.obtain();
						msg.obj = resp;
						handler4.sendMessage(msg);
					}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getCollectionServer(pageNo);
				} else {
					GetPostCollectionListResp resp = new GetPostCollectionListResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler4.sendMessage(msg);
				}
			}
		});
	}
	

	private ServerRequestParams writeImgParams(List<String> imgs) {
		Map<String, Object> param = new HashMap<String, Object>();

		params = getServerRequest();
		params.setToken(HttpConstant.TOKEN);
		params.setRequestUrl(HttpConstant.uploadSelectedImg(null));
		param.put("topicImgFile", imgs);
		params.setRequestParam(param);
		params.setRequestEntity(null);
		params.setStatus(XUtilsHelper.UPLOAD_FILE_MULTI_PARAM);
		return params;
	}

	private ServerRequestParams writeEditParams(EditCollectionReq bean) {
		params = getServerRequest();
		Map<String, Object> param = new HashMap<String, Object>();
		if ((bean.getTopicId() != 0) && (bean.getPostId() != 0)) {
			params.setRequestUrl(HttpConstant.editPostCollection(null));
			param.put("topicId", Long.toString(bean.getTopicId()));
			param.put("postId", Long.toString(bean.getPostId()));
			param.put("picUrl", bean.getpicUrl());
		} else {
			params.setRequestUrl(HttpConstant.editSolutionCollection(null));
			param.put("solutionId", Long.toString(bean.getSolutionId()));
			param.put("coverUrl", bean.getpicUrl());
		}
		param.put("slogan", bean.getSlogan());
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		return params;
	}

	private ServerRequestParams writeDeleteParams(DeleteCollectionReq bean) {
		params = getServerRequest();
		Map<String, Object> param = new HashMap<String, Object>();
		if (bean.getTopicId() != 0) {
			params.setRequestUrl(HttpConstant.deletePostCollection(null));
			param.put("topicId", Long.toString(bean.getTopicId()));
		} else {
			params.setRequestUrl(HttpConstant.deleteSolutionCollection(null));
			param.put("solutionIds", Long.toString(bean.getSolutionId()));
		}
		param.put("token", HttpConstant.TOKEN);
		params.setStatus(0);
		params.setRequestParam(param);
		return params;
	}

	private ServerRequestParams writeGetParams(int pageNo) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.getPostCollectionList(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pageNo", Integer.toString(pageNo));
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
