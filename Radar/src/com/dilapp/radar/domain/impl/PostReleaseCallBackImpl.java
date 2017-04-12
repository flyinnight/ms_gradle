package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseReq;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.PostDetailsCallBack.DeleteLocalPostReq;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;


public class PostReleaseCallBackImpl extends PostReleaseCallBack {
	private Handler handler4;
	private Handler handler5;
	private Handler handler6;
	private Handler handler7;
	private Context context;

	private final int SAVEDATA_UPLOADPOST = 111;
	private final int DELETE_LOCALPOST = 112;
	private final int DELETE_ALLSENDINGPOSTS = 113;

	public PostReleaseCallBackImpl(Context context) {
		this.context = context;
	}

	@Override
	public void uploadPostImgAsync(List<String> imgs,
			final BaseCall<MPostImgResp> call) {}


	// 后台发送贴子接口
	@Override
	public void createPostAsync(final PostReleaseReq bean, final BaseCall<MPostResp> call) {
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SAVEDATA_UPLOADPOST:
					PostReleaseReq sendBean= (PostReleaseReq)msg.obj;
					boolean saveSql = (sendBean.getLocalPostId() == 0);
					PostReleaseReq saveBean = analyzeBeanSave(sendBean);
					if ((bean.getPostLevel() == 0) && saveSql) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_RELEASE_INSERT_ONE, GsonUtil.getGson().toJson(saveBean), null);
                    } else if ((bean.getPostLevel() == 1) && saveSql) {
                    	RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_INSERT_SENDING_ONE, GsonUtil.getGson().toJson(saveBean), null);
                    }
					RadarProxy.getInstance(context).postReleaseBackground(saveBean, 1);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = SAVEDATA_UPLOADPOST;
		msg.obj = bean;
		handler4.sendMessage(msg);
		
		JSONObject jsonObject;
		MPostResp resp = new MPostResp();
		resp.setSendState(POST_RELEASE_SENDING);
		resp.setCreateTime(bean.getLocalCreateTime());
		resp.setToUserId(bean.getToUserId());
		resp.setSkinQuality(bean.getSkin());
		resp.setSelectedToSolution(bean.getSelectedToSolution());
		resp.setThumbURL(bean.getThumbURL());
		resp.setPostLevel(bean.getPostLevel());
		resp.setPart(bean.getPart());
		resp.setEffect(bean.getEffect());
		resp.setTopicTitle(bean.getTopicTitle());
		resp.setId(bean.getPostId());
		resp.setPid(bean.getParentId());
		resp.setTopicId(bean.getTopicId());
		resp.setPostTitle(bean.getPostTitle());
		resp.setPostContent(bean.getPostContent());
		resp.setLocalPostId(bean.getLocalPostId());
		String callback = resultStatus("{\"status\":\"SUCCESS\",\"msg\":\"ok\",\"ok\":true}", BaseResp.OK, true);
		Log.d("Radar", "postReleaseAsync callback: " + callback);
		try {
			//RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, GsonUtil.getGson().toJson(""), null);
			jsonObject = new JSONObject(callback);
			resp.setSuccess(jsonObject.optBoolean("success"));//true,false
			resp.setStatusCode(jsonObject.optInt("statusCode"));
			JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
			resp.setMessage((String) jsonObject2.optString("msg"));// ok
			resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS

		} catch (JSONException e) {
			e.printStackTrace();
			resp.setStatus("FAILED");
			Log.d("Radar", "JSONException: " + e);
		}
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}
    
	// 后台更新贴子接口
	@Override
	public void updatePostAsync(final PostReleaseReq bean, final BaseCall<MPostResp> call) {
		handler5 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SAVEDATA_UPLOADPOST:
					PostReleaseReq sendBean= (PostReleaseReq)msg.obj;
					boolean saveSql = (sendBean.getLocalPostId() == 0);
					
					BBSTextBuilder mTextBuilder;
			        mTextBuilder = new BBSTextBuilderImpl("[]");
			        mTextBuilder.setString(bean.getPostContent());

			        final List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
			        List<String> sendURL = sendBean.getThumbURL();
			        List<String> saveURL = new ArrayList<String>();
			        if (images != null) {
			            for (int i = 0; i < images.size(); i++) {
			            	String imgItem = images.get(i).getContent().toString();
			                if (TopicHelper.isImagePath(imgItem) != TopicHelper.PATH_LOCAL_SDCARD) {
			                	imgItem.replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, "");
			                	int index = imgItem.lastIndexOf("/");
			                	String matching = imgItem.substring(0, index);

			                	for (int j = 0; j < sendURL.size(); j++) {
			                		if (sendURL.get(j).startsWith(matching) ) {
			                			saveURL.add(sendURL.get(j));
			                			break;
			                		}
			                	}
			                } else {
			                	saveURL.add(imgItem);
			                }
			            }
			        }

					PostReleaseReq saveBean = analyzeBeanSave(sendBean);
					saveBean.setThumbURL(saveURL);
					
					if ((bean.getPostLevel() == 0) && saveSql) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_RELEASE_INSERT_ONE, GsonUtil.getGson().toJson(saveBean), null);
                    } else if ((bean.getPostLevel() == 1) && saveSql) {
                    	RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_INSERT_SENDING_ONE, GsonUtil.getGson().toJson(saveBean), null);
                    }
					RadarProxy.getInstance(context).postReleaseBackground(saveBean, 2);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = SAVEDATA_UPLOADPOST;
		msg.obj = bean;
		handler5.sendMessage(msg);
		
		JSONObject jsonObject;
		MPostResp resp = new MPostResp();
		resp.setSendState(POST_RELEASE_SENDING);
		resp.setCreateTime(bean.getLocalCreateTime());
		resp.setToUserId(bean.getToUserId());
		resp.setSkinQuality(bean.getSkin());
		resp.setSelectedToSolution(bean.getSelectedToSolution());
		resp.setThumbURL(bean.getThumbURL());
		resp.setPostLevel(bean.getPostLevel());
		resp.setPart(bean.getPart());
		resp.setEffect(bean.getEffect());
		resp.setTopicTitle(bean.getTopicTitle());
		resp.setId(bean.getPostId());
		resp.setPid(bean.getParentId());
		resp.setTopicId(bean.getTopicId());
		resp.setPostTitle(bean.getPostTitle());
		resp.setPostContent(bean.getPostContent());
		resp.setLocalPostId(bean.getLocalPostId());
		String callback = resultStatus("{\"status\":\"SUCCESS\",\"msg\":\"ok\",\"ok\":true}", BaseResp.OK, true);
		Log.d("Radar", "postUpdateAsync callback: " + callback);
		try {
			//RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, GsonUtil.getGson().toJson(""), null);
			jsonObject = new JSONObject(callback);
			resp.setSuccess(jsonObject.optBoolean("success"));//true,false
			resp.setStatusCode(jsonObject.optInt("statusCode"));
			JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
			resp.setMessage((String) jsonObject2.optString("msg"));// ok
			resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS

		} catch (JSONException e) {
			e.printStackTrace();
			resp.setStatus("FAILED");
			Log.d("Radar", "JSONException: " + e);
		}
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}
	
	// 删除未发送/更新成功的贴子
	@Override
	public void deleteLocalPostAsync(final PostReleaseReq bean, final BaseCall<MPostResp> call) {
		handler6 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DELETE_LOCALPOST:
					PostReleaseReq deleteBean = (PostReleaseReq)msg.obj;
					if (deleteBean.getPostLevel() == 0) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_RELEASE_DELETE_ONE, Long.toString(deleteBean.getLocalPostId()), null);
					} else if (bean.getPostLevel() == 1) {
						DeleteLocalPostReq deleteBean1 = new DeleteLocalPostReq();
						deleteBean1.setPostId(deleteBean.getPostId());
						deleteBean1.setLocalPostId(deleteBean.getLocalPostId());
						RadarProxy.getInstance(context).startLocalData(HttpConstant.POST_DETAIL_DELETE_ONE, GsonUtil.getGson().toJson(deleteBean1), null);
                    }
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = DELETE_LOCALPOST;
		msg.obj = bean;
		handler6.sendMessage(msg);
		
		JSONObject jsonObject;
		MPostResp resp = new MPostResp();
		String callback = resultStatus("{\"status\":\"SUCCESS\",\"msg\":\"ok\",\"ok\":true}", BaseResp.OK, true);
		Log.d("Radar", "postUpdateAsync callback: " + callback);
		try {
			//RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, GsonUtil.getGson().toJson(""), null);
			jsonObject = new JSONObject(callback);
			resp.setSuccess(jsonObject.optBoolean("success"));//true,false
			resp.setStatusCode(jsonObject.optInt("statusCode"));
			JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
			resp.setMessage((String) jsonObject2.optString("msg"));// ok
			resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS

		} catch (JSONException e) {
			e.printStackTrace();
			resp.setStatus("FAILED");
			Log.d("Radar", "JSONException: " + e);
		}
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}
	
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的贴子
	@Override
	public void deleteAllSendingPostAsync(final BaseReq bean, final BaseCall<BaseResp> call) {
		handler7 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DELETE_ALLSENDINGPOSTS:
					BaseReq deleteBean = (BaseReq)msg.obj;
					RadarProxy.getInstance(context).startLocalData(HttpConstant.DELETE_ALL_LOCAL_SENDING_POST, GsonUtil.getGson().toJson(deleteBean), null);
					
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = DELETE_ALLSENDINGPOSTS;
		msg.obj = bean;
		handler7.sendMessage(msg);
		
		JSONObject jsonObject;
		BaseResp resp = new BaseResp();
		String callback = resultStatus("{\"status\":\"SUCCESS\",\"msg\":\"ok\",\"ok\":true}", BaseResp.OK, true);
		Log.d("Radar", "postUpdateAsync callback: " + callback);
		try {
			//RadarProxy.getInstance(context).startLocalData(HttpConstant.GET_MAIN_POST_LIST_LOCAL, GsonUtil.getGson().toJson(""), null);
			jsonObject = new JSONObject(callback);
			resp.setSuccess(jsonObject.optBoolean("success"));//true,false
			resp.setStatusCode(jsonObject.optInt("statusCode"));
			JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
			resp.setMessage((String) jsonObject2.optString("msg"));// ok
			resp.setStatus((String)jsonObject2.optString("status"));// SUCCESS

		} catch (JSONException e) {
			e.printStackTrace();
			resp.setStatus("FAILED");
			Log.d("Radar", "JSONException: " + e);
		}
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}

	
	private PostReleaseReq analyzeBeanSave(PostReleaseReq bean) {
		PostReleaseReq newBean = new PostReleaseReq();
		
		newBean.setTopicId(bean.getTopicId());
		newBean.setTopicTitle(bean.getTopicTitle());
		newBean.setPostId(bean.getPostId());
		newBean.setPostTitle(bean.getPostTitle());
		newBean.setPostContent(bean.getPostContent());
		newBean.setParentId(bean.getParentId());
		newBean.setPostLevel(bean.getPostLevel());
		newBean.setToUserId(bean.getToUserId());
		newBean.setSelectedToSolution(bean.getSelectedToSolution());
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setSkin(bean.getSkin());
		newBean.setThumbURL(bean.getThumbURL());
		
		long time = System.currentTimeMillis();
		newBean.setLocalCreateTime(time);
		
		if (bean.getLocalPostId() == 0) {
			long LocalPostId = SharePreCacheHelper.getLocalPostId(context) + 1;
			if (LocalPostId > 9999999999999999L) {
				LocalPostId = 1;
			}
			newBean.setLocalPostId(LocalPostId);
			SharePreCacheHelper.setLocalPostId(context, LocalPostId);
		} else {
			newBean.setLocalPostId(bean.getLocalPostId());
		}

		return newBean;
	}
	
	private String resultStatus(String message, int statusCode, boolean isSuccess) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("message", message);
			jsonObject.put("statusCode", statusCode);
			jsonObject.put("success", isSuccess);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
