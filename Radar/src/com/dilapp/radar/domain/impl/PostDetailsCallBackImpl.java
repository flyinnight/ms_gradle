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
import com.dilapp.radar.domain.PostDetailsCallBack;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class PostDetailsCallBackImpl extends PostDetailsCallBack {
	private Handler handler2;
	private Handler handler3;
	private Context context;
	private ServerRequestParams params;
	//private boolean callBack;
	private MPostDetailResp respLocal;
	private boolean reTry;
	private boolean reTry2;

	public PostDetailsCallBackImpl(Context context) {
		this.context = context;
		reTry = true;
		reTry2 = true;
	}

	//获取主贴详情
	/*先获取本地数据，根据updatetime判断是否需要更新网络数据，网络数据直接存入本地，同时返回UI*/
	@Override
	public void getPostDetailsByTypeAsync(final MPostDetailReq bean, final BaseCall<MPostDetailResp> call, int type) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MPostDetailResp) msg.obj);
					reTry = true;
				}
			}
		};
		
		if (bean.getPageNo() == 1) {
			if (type == GetPostList.GET_DATA_SERVER) {
				getDetailServer(bean);
			} else if (type == GetPostList.GET_DATA_LOCAL) {
				getDetailServer(bean);//暂时都取服务器数据，避免时间长回帖失败
			}
		} else {
			getDetailServer(bean);
		}
	}
	
	private void getDetailServer(final MPostDetailReq bean) {
		
		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {

				Log.d("Radar", "getPostDetailsByTypeAsync getDetailServer: " + result);
				MPostDetailResp resp = new MPostDetailResp();
				int statusCode = BaseResp.OK;
				try {
					JSONObject jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));//true,false
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					statusCode = jsonObject.optInt("statusCode");
					JSONObject jsonObject1 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject1.optString("msg"));
					resp.setStatus(jsonObject1.optString("status"));
					
					if ("SUCCESS".equalsIgnoreCase(jsonObject1.optString("status"))) {
						Object temp = jsonObject1.opt("values");
						if (temp != null) {
							JSONObject jsonObj = new JSONObject(jsonObject1.optString("values"));
							Object temp1 = jsonObj.opt("posts");
							if (temp1 != null) {
								JSONArray jsonArr = new JSONArray(jsonObj.optString("posts"));
								
								List<MFollowPostResp> mPostRespList = new ArrayList<MFollowPostResp>();
								for (int i = 0; i < jsonArr.length(); i++) {
									MFollowPostResp mPostResp = analyzeRespSave((JSONObject)jsonArr.get(i));
									mPostRespList.add(mPostResp);
								}
								resp.setResp(mPostRespList);
								resp.setTotalPage(jsonObj.optInt("totalPage"));
								resp.setPageNo(jsonObj.optInt("pageNo"));
								
								if (bean.getPageNo() == 1) {
									RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_INSERT_UPDATE_DETAIL_ONE, GsonUtil.getGson().toJson(resp), null);
								}
								Message msg = Message.obtain();
								msg.obj = resp;
								handler2.sendMessage(msg);
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
					handleFailure(bean, statusCode);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry && (bean.getPageNo() == 1)) {
					reTry = false;
					getDetailLocal(bean);
				}  else {
					MPostDetailResp resp = new MPostDetailResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}
	public void handleFailure(MPostDetailReq bean, int statusCode) {
		if (statusCode == BaseResp.OK) {
			if (bean.getPageNo() == 1) {
				DeleteLocalPostReq deleteBean = new DeleteLocalPostReq();
				deleteBean.setPostId(bean.getPostId());
				deleteBean.setLocalPostId(0);
				RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_DELETE_ONE, GsonUtil.getGson().toJson(deleteBean), null);
			}
			
			MPostDetailResp resp = new MPostDetailResp();
			resp.setStatus("FAILED");
			Message msg = Message.obtain();
			msg.obj = resp;
			handler2.sendMessage(msg);
		} else {
			if (reTry && (bean.getPageNo() == 1)) {
				reTry = false;
				getDetailLocal(bean);
			}  else {
				MPostDetailResp resp = new MPostDetailResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler2.sendMessage(msg);
			}
		}
	}
	
	private void getDetailLocal(final MPostDetailReq bean) {
		
		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_GET_DETAIL_ONE, GsonUtil.getGson().toJson(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getPostDetailsByTypeAsync getDetailLocal: " + result);
				PostDetailRespLocal resp = GsonUtil.getGson().fromJson(result, PostDetailRespLocal.class);
				MPostDetailResp resp2 = resp.getDetailResp();

				if (reTry && ((resp2 == null) || (resp2.getResp() == null) || (resp2.getResp().size() == 0))) {
					reTry = false;
					getDetailServer(bean);
				} else {
					//if (resp.getUpdateTime() == 0) {//暂时都取服务器数据，避免时间长回帖失败
						//reTry = false;
						//getDetailServer(bean);
					//} else {
						if (resp2 == null) {
							resp2 = new MPostDetailResp();
							resp2.setStatus("FAILED");
						} else {
							if ((resp2.getResp() == null) || (resp2.getResp().size() == 0)) {
								resp2.setStatus("FAILED");
							} else {
								resp2.setStatus("SUCCESS");
							}
						}
						resp2.setStatusCode(BaseResp.DATA_LOCAL);
						Message msg = Message.obtain();
						msg.obj = resp2;
						handler2.sendMessage(msg);
					//}
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				if (reTry) {
					reTry = false;
					getDetailServer(bean);
				} else {
					MPostDetailResp resp = new MPostDetailResp();
					resp.setStatus("FAILED");
					resp.setStatusCode(BaseResp.DATA_LOCAL);
					Message msg = Message.obtain();
					msg.obj = resp;
					handler2.sendMessage(msg);
				}
			}
		});
	}

	private MFollowPostResp analyzeRespSave(JSONObject jsonObject4) {		
		
		MFollowPostResp mPostResp = new MFollowPostResp();
		try {
			mPostResp.setMain(jsonObject4.optBoolean("isMain"));
			mPostResp.setStoreUp(jsonObject4.optBoolean("isStoreUp"));
			mPostResp.setLike(jsonObject4.optBoolean("isLike"));
			mPostResp.setId(jsonObject4.optLong("id"));
			mPostResp.setPid(jsonObject4.optLong("parentId"));
			mPostResp.setTopicId(jsonObject4.optLong("topicId"));
			mPostResp.setTopicTitle(jsonObject4.optString("topicTitle"));
			mPostResp.setPostLevel(jsonObject4.optInt("postLevel"));
			mPostResp.setUserId(jsonObject4.optString("userId"));
			mPostResp.setUserName(jsonObject4.optString("userName"));
			mPostResp.setEMUserId(jsonObject4.optString("msgUid"));
			mPostResp.setToUserId(jsonObject4.optString("toUserId"));
			mPostResp.setToUserName(jsonObject4.optString("toUserName"));
			mPostResp.setPostTitle(jsonObject4.optString("postTitle"));
			mPostResp.setPostContent(jsonObject4.optString("postContent"));
			mPostResp.setFollowsUpNum(jsonObject4.optInt("followsUpNum"));
			mPostResp.setStoreupNum(jsonObject4.optInt("storeupNum"));
			mPostResp.setSelectedToSolution(jsonObject4.optBoolean("selectedToSolution"));
			mPostResp.setEffect(jsonObject4.optString("effect"));
			mPostResp.setPart(jsonObject4.optString("part"));
			mPostResp.setReport(jsonObject4.optBoolean("reported"));
			mPostResp.setOnTop(jsonObject4.optBoolean("onTop"));
			//mPostResp.setThumbURL((String[])(jsonObject4.optString("thumbURL").split(",")));
			Object thumb = jsonObject4.opt("thumbURL");
			if (thumb != null) {
				JSONArray jsonArrImg = new JSONArray(jsonObject4.optString("thumbURL"));
				List<String> imgList = new ArrayList<String>();
				for (int j = 0; j < jsonArrImg.length(); j++) {
					String imgItem = (String) jsonArrImg.get(j);
						if (imgItem.length() != 0)
						{
							imgList.add(imgItem);
						}
				}
				mPostResp.setThumbURL(imgList);
			}
			mPostResp.setLike(jsonObject4.optInt("favorite"));
			mPostResp.setDislike(jsonObject4.optInt("disfavorite"));
			mPostResp.setUpdateTime(jsonObject4.optLong("updateTime"));
			mPostResp.setCreateTime(jsonObject4.optLong("createTime"));
			mPostResp.setPostViewCount(jsonObject4.optInt("postViewCount"));
			mPostResp.setLevel(jsonObject4.optInt("level"));
			mPostResp.setLevelName(jsonObject4.optString("levelName"));
			mPostResp.setGender(jsonObject4.optInt("gender"));
			mPostResp.setBirthday(jsonObject4.optString("birthday"));
			mPostResp.setDesc(jsonObject4.optString("desc"));
			mPostResp.setOccupation(jsonObject4.optString("occupation"));
			mPostResp.setUserHeadIcon(jsonObject4.optString("portrait"));
			mPostResp.setSkinQuality(jsonObject4.optString("skinQuality"));
			mPostResp.setQq(jsonObject4.optString("qq"));
			mPostResp.setEmail(jsonObject4.optString("email"));
			mPostResp.setWechat(jsonObject4.optString("wechat"));
			mPostResp.setBlog(jsonObject4.optString("blog"));
			mPostResp.setTotalFollows(jsonObject4.optInt("totalFollows"));
			mPostResp.setFollowsUser(jsonObject4.optBoolean("isFollow"));

			Object temp = jsonObject4.opt("followPosts");
			if (temp != null) {
				JSONArray jsonArrComment = new JSONArray(jsonObject4.optString("followPosts"));
				List<MPostResp> commentList = new ArrayList<MPostResp>();
				for (int j = 0; j < jsonArrComment.length(); j++) {
					JSONObject jsonObject5 = (JSONObject) jsonArrComment.get(j);
					MPostResp comment = new MPostResp();
					comment.setId(jsonObject5.optLong("id"));
					comment.setPid(jsonObject5.optLong("parentId"));
					comment.setTopicId(jsonObject5.optLong("topicId"));
					comment.setPostLevel(jsonObject5.optInt("postLevel"));
					comment.setUserId(jsonObject5.optString("userId"));
					comment.setUserName(jsonObject5.optString("userName"));
					comment.setEMUserId(jsonObject5.optString("msgUid"));
					comment.setToUserId(jsonObject5.optString("toUserId"));
					comment.setToUserName(jsonObject5.optString("toUserName"));
					comment.setPostTitle(jsonObject5.optString("postTitle"));
					comment.setPostContent(jsonObject5.optString("postContent"));
					comment.setFollowsUpNum(jsonObject5.optInt("followsUpNum"));
					comment.setStoreupNum(jsonObject5.optInt("storeupNum"));
					comment.setSelectedToSolution(jsonObject5.optBoolean("selectedToSolution"));
					comment.setEffect(jsonObject5.optString("effect"));
					comment.setReport(jsonObject5.optBoolean("reported"));
					comment.setOnTop(jsonObject5.optBoolean("onTop"));
					//comment.setThumbURL((String[])(jsonObject5.optString("thumbURL").split(",")));
					Object temp2 = jsonObject5.opt("thumbURL");
					if (temp2 != null) {
						JSONArray jsonArrImg = new JSONArray(jsonObject5.optString("thumbURL"));
						List<String> imgList = new ArrayList<String>();
						for (int k = 0; k < jsonArrImg.length(); k++) {
							String imgItem = (String) jsonArrImg.get(k);
							if (imgItem.length() != 0)
							{
								imgList.add(imgItem);
							}
						}
						comment.setThumbURL(imgList);
					}
					comment.setLike(jsonObject5.optInt("favorite"));
					comment.setDislike(jsonObject5.optInt("disfavorite"));
					comment.setUpdateTime(jsonObject5.optLong("updateTime"));
					comment.setCreateTime(jsonObject5.optLong("createTime"));
					
					commentList.add(comment);
				}
				mPostResp.setComment(commentList);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mPostResp;
	}
	

	//读取跟帖的回复
	@Override
	public void getReplyByTypeAsync(MPostDetailReq bean, final BaseCall<MReplyResp> call, int type) {
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (call != null && !call.cancel) {
					call.call((MReplyResp) msg.obj);
					reTry2 = true;
				}
			}
		};
		
		if (type == GetPostList.GET_DATA_SERVER) {
			getReplyServer(bean);
		} else if (type == GetPostList.GET_DATA_LOCAL) {
			getReplyServer(bean);  //暂时都取服务器数据，回复没有根据postId做区分，存储一个列表意义不大
		}
	}
	
	public void getReplyServer(final MPostDetailReq bean) {
		RadarProxy.getInstance(context).startServerData(writeReplyParams(bean), new ClientCallbackImpl() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				MReplyResp resp = null;
				int statusCode = BaseResp.OK;
				try {
					Log.d("Radar", "getReplyServer: " + result);
					resp = new MReplyResp();
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
						
					Object temp = jsonObject3.opt("followPosts");
					if (temp != null) {
						JSONArray jsonArrComment = new JSONArray(jsonObject3.optString("followPosts"));
						List<MPostResp> commentList = new ArrayList<MPostResp>();
						for (int j = 0; j < jsonArrComment.length(); j++) {
							JSONObject jsonObject5 = (JSONObject) jsonArrComment.get(j);
							MPostResp comment = new MPostResp();
							comment.setId(jsonObject5.optLong("id"));
							comment.setPid(jsonObject5.optLong("parentId"));
							comment.setTopicId(jsonObject5.optLong("topicId"));
							comment.setPostLevel(jsonObject5.optInt("postLevel"));
							comment.setUserId(jsonObject5.optString("userId"));
							comment.setUserName(jsonObject5.optString("userName"));
							comment.setToUserId(jsonObject5.optString("toUserId"));
							comment.setToUserName(jsonObject5.optString("toUserName"));
							comment.setPostTitle(jsonObject5.optString("postTitle"));
							comment.setPostContent(jsonObject5.optString("postContent"));
							comment.setFollowsUpNum(jsonObject5.optInt("followsUpNum"));
							comment.setStoreupNum(jsonObject5.optInt("storeupNum"));
							comment.setSelectedToSolution(jsonObject5.optBoolean("selectedToSolution"));
							comment.setEffect(jsonObject5.optString("effect"));
							comment.setReport(jsonObject5.optBoolean("reported"));
							comment.setOnTop(jsonObject5.optBoolean("onTop"));
							//comment.setThumbURL((String[])(jsonObject5.optString("thumbURL").split(",")));
							Object temp2 = jsonObject5.opt("thumbURL");
							if (temp2 != null) {
								JSONArray jsonArrImg = new JSONArray(jsonObject5.optString("thumbURL"));
								List<String> imgList = new ArrayList<String>();
								for (int k = 0; k < jsonArrImg.length(); k++) {
									String imgItem = (String) jsonArrImg.get(k);
									if (imgItem.length() != 0)
									{
										imgList.add(imgItem);
									}
								}
								comment.setThumbURL(imgList);
							}
							comment.setLike(jsonObject5.optInt("favorite"));
							comment.setDislike(jsonObject5.optInt("disfavorite"));
							comment.setUpdateTime(jsonObject5.optLong("updateTime"));
							comment.setCreateTime(jsonObject5.optLong("createTime"));
								
							commentList.add(comment);
						}
						resp.setComment(commentList);
						
						//PostListSave saveBean = new PostListSave();
						//saveBean.setPostList(commentList);
						//saveBean.setType(GetPostList.POST_LIST_BY_REPLY);
						//saveBean.setUpdateTime(System.currentTimeMillis());
						//RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
						
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					} else {
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
				
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
				MReplyResp resp = new MReplyResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler3.sendMessage(msg);
			}
		});
	}
	
	public void getReplyLocal(final MPostDetailReq bean) {
		RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_LIST_GET_ONE_BYTYPE, Integer.toString(GetPostList.POST_LIST_BY_REPLY), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "getReplyLocal " + result);
				MReplyResp resp = new MReplyResp();
				PostListSave respSave = GsonUtil.getGson().fromJson(result, PostListSave.class);
				List<MPostResp> beanList = respSave.getPostList();
				long updateTime = respSave.getUpdateTime();

				if (reTry2 && ((beanList == null) || (beanList.size() == 0))) {
					reTry2 = false;
					getReplyServer(bean);
				} else {
					if ((System.currentTimeMillis() - updateTime) > GetPostList.UPDATE_SPAN_TIME) {
						getReplyServer(bean);
					} else {
						resp.setTotalPage(1);
						resp.setPageNo(1);
						resp.setComment(beanList);
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
				if (reTry2) {
					reTry2 = false;
					getReplyServer(bean);
				} else {
					MReplyResp resp = new MReplyResp();
					resp.setStatus("FAILED");
					Message msg = Message.obtain();
					msg.obj = resp;
					handler3.sendMessage(msg);
				}
			}
		});
	}

	
	private ServerRequestParams writeParams(MPostDetailReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.readMainPost(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("postId", Long.toString(bean.getPostId()));
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
	
	private ServerRequestParams writeReplyParams(MPostDetailReq bean) {
		params = getServerRequest();
		params.setRequestUrl(HttpConstant.readReply(null));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("token", HttpConstant.TOKEN);
		param.put("postId", Long.toString(bean.getPostId()));
		param.put("pageNo", Integer.toString(bean.getPageNo()));
		params.setRequestParam(param);
		return params;
	}
}
