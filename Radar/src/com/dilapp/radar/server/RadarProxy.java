/*********************************************************************/
/*  文件名  RadarProxy.java    　                                       */
/*  程序名  Radar Service代理                     						     			 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionCreateUpdate.CoverImgResp;
import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionUpdateReq;
import com.dilapp.radar.domain.SolutionCreateUpdate.TextImgResp;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.GetPostList.MPostResp;
import com.dilapp.radar.domain.PostReleaseCallBack;
import com.dilapp.radar.domain.PostReleaseCallBack.MPostImgResp;
import com.dilapp.radar.domain.PostReleaseCallBack.PostReleaseReq;
import com.dilapp.radar.domain.PostReleaseCallBack.UpdatePostSendingState;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionCreateReq;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionListData.SolutionDataGetDelete;
import com.dilapp.radar.domain.SolutionListData.SolutionDataSave;
import com.dilapp.radar.domain.impl.PostReleaseCallBackAsyncImpl;
import com.dilapp.radar.domain.impl.SolutionCreateUpdateAsyncImpl;
import com.dilapp.radar.textbuilder.BBSDescribeItem;
import com.dilapp.radar.textbuilder.BBSTextBuilder;
import com.dilapp.radar.textbuilder.impl.BBSTextBuilderImpl;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.topic.TopicHelper;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.Slog;

public class RadarProxy implements Handler.Callback {
	private final String TAG = "RadarProxy";
	private static RadarProxy instance;
	private CallbackMap mCallbackMap;
	private int callbackID;
	private IRadarServer mServer;
	private Handler mHandler;
	private Context mContext;
	//private ClientCallback clientCallback;

	private boolean needBinder = false;
	private boolean isBinding = false;
	private boolean bindService = false;
	private boolean serverIsNull = false;
	private String bindRemoteServerName = "com.dilapp.radar.server.RadarServer";

	private PostReleaseCallBackAsyncImpl postRelease;
	private SolutionCreateUpdateAsyncImpl solutionRelease;
	private BBSTextBuilder mTextBuilder;
	private BBSTextBuilder mTextBuilderSolution;
	
	// API STARTED

	public static RadarProxy getInstance(Context context) {
		if (instance == null) {
		   synchronized (RadarProxy.class) { 
		        if (instance == null) { 
		        	instance = new RadarProxy(context);
		        }
		    } 
		}
		return instance;
	}

	/**
	 * start bind Server called when UI creating if used
	 */
	public void bindServer() {
		needBinder = true;
		connectService();
	}

	/**
	 * unbindServer called when UI quit
	 */
	public void unBindServer() {
		needBinder = false;
		disconnectService();
	}

	/**
	 * set UI listener
	 * 
	 * @param listener
	 */
	// public void setLinkListener(ILinkLIstener listener) {
	// this.mRdbListener = listener;
	// if(this.mRdbListener != null){
	// bindServer();
	// }
	// }

	// API END

	private RadarProxy(Context context) {
		this.mContext = context.getApplicationContext();
		mHandler = new Handler(this);
		mCallbackMap = new CallbackMap(20);
		postRelease = new PostReleaseCallBackAsyncImpl(mContext);
		solutionRelease = new SolutionCreateUpdateAsyncImpl(mContext);
	}

	private final int MSG_BIND_TIMEOUT = 1;
	private final int MSG_RESULT_CALLBACK = 2;

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_BIND_TIMEOUT:
			isBinding = false;
			break;
		case MSG_RESULT_CALLBACK:
			try {
//				Slog.d("handleMessage MSG_RESULT_CALLBACK msg: "+ msg);
				if (!mCallbackMap.isEmpty()) {
					ClientCallback clientCallback = mCallbackMap.getEntry(msg.arg1);
					if (clientCallback != null) {
						clientCallback.onSuccess(msg.obj.toString());
					} else {
						Slog.e("MSG_RESULT_CALLBACK: clientCallback is null");
					}
				} else {
					Slog.e("MSG_RESULT_CALLBACK: mCallbackMap is Empty");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		return true;
	}

	private final int MSG_SERVER_ISNOTNULL = 0;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Slog.i("on RadarService Connected!");
			mServer = IRadarServer.Stub.asInterface(arg1);
			if (serverIsNull) {
				handler.sendEmptyMessage(MSG_SERVER_ISNOTNULL);
				serverIsNull = false;
			}
			bindService = true;
			isBinding = false;
			mHandler.removeMessages(MSG_BIND_TIMEOUT);
			registerCallback();
			startLocalData(HttpConstant.POST_RELEASE_UPDATE_STATE_ALL, null, null);
			startLocalData(HttpConstant.POST_DETAIL_RESTORE_STATE_ALL, null, null);
			SolutionDataSave saveBean = new SolutionDataSave();
			saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
			saveBean.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
        	startLocalData(HttpConstant.SOLUTION_STATE_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Slog.i("on RadarService Disconnected!");
			mServer = null;
			bindService = false;
			isBinding = false;
			mHandler.removeMessages(MSG_BIND_TIMEOUT);
			if (needBinder) {
				bindServer();
			}
		}

	};

	private void connectService() {
		if (mServer == null && !isBinding) {
			Intent intent = new Intent(mContext, RadarServer.class);// Intent(bindRemoteServerName);
			mContext.startService(intent);

			mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

			Slog.i("bind RadarServer Started!");
			isBinding = true;
			mHandler.removeMessages(MSG_BIND_TIMEOUT);
			mHandler.sendEmptyMessageDelayed(MSG_BIND_TIMEOUT, 5 * 1000);
		} else {
			Slog.w("connect RadarService rejected! mRemoteServer != null or isBinding : " + isBinding);
			if (mServer != null) {
				registerCallback();
			}
		}
	}

	private void disconnectService() {
		if (bindService) {
			try {
				unRegisterCallback();
				mContext.unbindService(mServiceConnection);
			} catch (Exception e) {
				Slog.e("disconnect RadarService Error!", e);
			}
			bindService = false;
			isBinding = false;
			mHandler.removeMessages(MSG_BIND_TIMEOUT);
		}
	}

	private void registerCallback() {
		try {
			if (mServer != null) {
				mServer.registerCallback(mIRadarCallback);
				Slog.i("register IRadarCallback successfully!");
			} else {
				connectService();
			}
		} catch (RemoteException e) {
			Slog.e("register IRadarCallback error : ", e);
			mServer = null;
			bindService = false;
			connectService();
		}
	}

	private void unRegisterCallback() {
		try {
			if (mServer != null) {
				mServer.unRegisterCallback();
				Slog.i("unRegister IRadarCallback successfully!");
			}
		} catch (RemoteException e) {
			Slog.e("unRegister IRadarCallback error : ", e);
		}
	}

	Handler handler;

	public void startServerData(final ServerRequestParams serverRequestParams, final ClientCallback cltCallback) {
		if (mServer == null) {
			serverIsNull = true;
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_SERVER_ISNOTNULL:
						startUpServer(serverRequestParams, cltCallback);
						break;
					default:
						break;
					}
				}
			};
			connectService();
		} else {
			startUpServer(serverRequestParams, cltCallback);
		}
	}

	private void startUpServer(ServerRequestParams serverRequestParams, ClientCallback cltCallback) {
		try {
			if (callbackID > 999999999) {
				callbackID = 0;
			}
			mServer.startUploadServer(serverRequestParams, ++callbackID);
			//RadarProxy.this.clientCallback = cltCallback;
			if (!mCallbackMap.isFull()) {
				if (cltCallback != null) {
					mCallbackMap.insert(callbackID, cltCallback);
				}
			} else {
				if (cltCallback != null) {
					String callback = resultStatus("{\"status\":\"FAILED\",\"msg\":\"call back queue is full\",\"ok\":true}", BaseResp.UNKNOWN, false);
					cltCallback.onSuccess(callback);
				}
			}
			Slog.d("startUpServer cltCallback: " + cltCallback + " callbackID: " + callbackID);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void startLocalData(final String localRequestParams, final String localContent,
			final ClientCallback cltCallback) {
		if (mServer == null) {
			serverIsNull = true;
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_SERVER_ISNOTNULL:
						startUpLocal(localRequestParams, localContent, cltCallback);
						break;
					default:
						break;
					}
				}
			};
			connectService();
		} else {
			startUpLocal(localRequestParams, localContent, cltCallback);
		}
	}

	
	
	public void postReleaseBackground(final PostReleaseReq bean, final int type) {

        mTextBuilder = new BBSTextBuilderImpl("[]");
        mTextBuilder.setString(bean.getPostContent());

        final List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
        if (images != null) {
            int imageSize = images.size();
            // 图片已经上传的话就不需要上传了
            for (int i = 0; i < images.size(); i++) {
                // 如果图片不是本地的就不要上传了
                // 注意，不是本地的，和是服务器的有区别
                // 不是本地的代表是服务器的相对地址或者绝对地址还有可能是错误的地址
                if (TopicHelper.isImagePath(images.get(i).getContent().toString()) != TopicHelper.PATH_LOCAL_SDCARD) {
                    images.remove(i--);
                }
            }
            uploadImage(bean, images, imageSize, type);
        }
	}

    private void uploadImage(final PostReleaseReq bean, final List<BBSDescribeItem> images, int thumbSize, final int type) {
        List<String> imageUrls = TopicHelper.describeItemContent2Strings(images);
        if (Constants.COMPRESS_POST_IMAGE) {
            List<String> compress = TopicHelper.compress(imageUrls);
            if (compress != null && compress.size() == imageUrls.size()) {
            	imageUrls.clear();
                imageUrls.addAll(compress);
            }
        }
        String log = "";// 打印LOG用的，把路径全都拼起来打印
        if (imageUrls != null) {
            for (String str : imageUrls) {
                log += str + ", ";
            }
        }
        Log.i("III_logic", "paths " + log);
        if (imageUrls != null && imageUrls.size() == 0) {
            // 一般是编辑的时候用到的，这个情况代表图片全都不是本地的，一般情况来说，也就是图片都是服务器的
            List<String> thumbs = new ArrayList<String>();
            if (imageUrls.size() != thumbSize) {
                // 这个代表
                List<BBSDescribeItem> imgs = TopicHelper.findImages(mTextBuilder);
                for (int i = 0; i < imgs.size(); i++) {
                    // 将服务的绝对地址改为相对地址
                    thumbs.add(imgs.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                }
                // 缩略图的地址
            }

            // 将空内容去掉，比如没有文字的文本框
            TopicHelper.trimBBSTextBuilder(mTextBuilder);
            if (type == 1) {
            	releasePost(bean, mTextBuilder.getString(), thumbs);
            } else if (type == 2) {
            	updatePost(bean, mTextBuilder.getString(), thumbs);
            }
            Log.i("III_logic", "帖子中没有图片，或已上传成功");
            return;
        }
        postRelease.uploadPostImgAsync(imageUrls, new BaseCall<MPostImgResp>() {
            @Override
            public void call(MPostImgResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                    // 第一步，判断服务器给的图片地址和本地上传的图片数量一样多
                    if (resp.getPostImgURL() != null && resp.getPostImgURL().size() == images.size()) {

                        // 将帖子中途地址全部换成服务器的地址
                        TopicHelper.setStrings2BBSDescribeItemContent(resp.getPostImgURL(), images, ""/*HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP*/);
                        TopicHelper.trimBBSTextBuilder(mTextBuilder);

                        List<BBSDescribeItem> images = TopicHelper.findImages(mTextBuilder);
                        List<String> thumbs = new ArrayList<String>(images.size());
                        for (int i = 0; i < images.size(); i++) {
                            thumbs.add(images.get(i).getContent().toString().replace(HttpConstant.OFFICIAL_RADAR_DOWNLOAD_IMG_IP, ""));
                        }
                        if (bean.getPostLevel() == 0) {
                        	PostReleaseReq imgBean = analyzeBeanImg(bean, mTextBuilder.getString(), thumbs);
                        	startLocalData(HttpConstant.POST_RELEASE_UPDATEIMG_ONE, GsonUtil.getGson().toJson(imgBean), null);
                        } else if (bean.getPostLevel() == 1) {
                        	PostReleaseReq imgBean = analyzeBeanImg(bean, mTextBuilder.getString(), thumbs);
                        	startLocalData(HttpConstant.POST_DETAIL_UPDATEIMG_ONE, GsonUtil.getGson().toJson(imgBean), null);
                        }
                        
                        if (type == 1) {
                        	releasePost(bean, mTextBuilder.getString(), thumbs);
                        } else if (type == 2) {
                        	updatePost(bean, mTextBuilder.getString(), thumbs);
                        }
                        Slog.d("postReleaseBackground uploadImage sucess");
                    } else {
                    	Slog.d("postReleaseBackground uploadImage failed");
                    	if (bean.getPostLevel() == 0) {
                    		UpdatePostSendingState stateBean = new UpdatePostSendingState();
                    		stateBean.setLocalPostId(bean.getLocalPostId());
                    		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                    		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                        	startLocalData(HttpConstant.POST_RELEASE_UPDATE_STATE_ONE, GsonUtil.getGson().toJson(stateBean), null);
                        } else if (bean.getPostLevel() == 1) {
                        	UpdatePostSendingState stateBean = new UpdatePostSendingState();
                    		stateBean.setLocalPostId(bean.getLocalPostId());
                    		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                    		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                        	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_STATE, GsonUtil.getGson().toJson(stateBean), null);
                        }
                    	
                    	MPostResp respFailed = new MPostResp();
                    	respFailed.setSuccess(resp.isSuccess());//true,false
                    	respFailed.setStatusCode(resp.getStatusCode());
                    	respFailed.setMessage(resp.getMessage());
                    	respFailed.setStatus(resp.getStatus());
                    	respFailed.setLocalPostId(bean.getLocalPostId());
                    	respFailed.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	Intent intent = new Intent();
                    	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                    	Bundle bundle = new Bundle();
                    	bundle.putSerializable("RespData", respFailed);
                    	intent.putExtras(bundle);
                        
                        mContext.sendBroadcast(intent);
                    }
                } else {
                	if (bean.getPostLevel() == 0) {
                		UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_RELEASE_UPDATE_STATE_ONE, GsonUtil.getGson().toJson(stateBean), null);
                    } else if (bean.getPostLevel() == 1) {
                    	UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_STATE, GsonUtil.getGson().toJson(stateBean), null);
                    }
                	
                	MPostResp respFailed = new MPostResp();
                	respFailed.setSuccess(resp.isSuccess());//true,false
                	respFailed.setStatusCode(resp.getStatusCode());
                	respFailed.setMessage(resp.getMessage());
                	respFailed.setStatus(resp.getStatus());
                	respFailed.setLocalPostId(bean.getLocalPostId());
                	respFailed.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                	Intent intent = new Intent();
                	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    
                    mContext.sendBroadcast(intent);
                    Slog.d("postReleaseBackground uploadImage failed");
                }
            }
        });
    }
    
    private void releasePost(final PostReleaseReq bean, String content, List<String> imgs) {

    	final PostReleaseReq relBean = analyzeBeanRelease(bean);
    	relBean.setPostContent(content);
        relBean.setThumbURL(imgs);
        
        postRelease.createPostAsync(relBean, new BaseCall<MPostResp>() {
            @Override
            public void call(MPostResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                	
                	MPostResp respSuccess = analyzeBeanSuccess(resp);
                	respSuccess.setLocalPostId(relBean.getLocalPostId());
                	if (relBean.getPostLevel() == 0) {
                		startLocalData(HttpConstant.POST_RELEASE_UPDATE_ONE, GsonUtil.getGson().toJson(respSuccess), null);
                    } else if (relBean.getPostLevel() == 1) {
                    	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_ONE, GsonUtil.getGson().toJson(respSuccess), null);
                    }
                	Intent intent = new Intent();
                	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                	respSuccess.setSendState(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respSuccess);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("postReleaseBackground createPost sucess");
                } else {
                	if (bean.getPostLevel() == 0) {
                		UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_RELEASE_UPDATE_STATE_ONE, GsonUtil.getGson().toJson(stateBean), null);
                    } else if (bean.getPostLevel() == 1) {
                    	UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_STATE, GsonUtil.getGson().toJson(stateBean), null);
                    }
                	
                	MPostResp respFailed = analyzeBeanSuccess(resp);
                	respFailed.setLocalPostId(relBean.getLocalPostId());
                	respFailed.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                	Intent intent = new Intent();
                	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("postReleaseBackground createPost failed");
                }
            }
        });
    }
    
    private void updatePost(final PostReleaseReq bean, String content, List<String> imgs) {

    	final PostReleaseReq relBean = analyzeBeanUpdate(bean);
    	relBean.setPostContent(content);
        relBean.setThumbURL(imgs);

        postRelease.updatePostAsync(relBean, new BaseCall<MPostResp>() {
            @Override
            public void call(MPostResp resp) {
                if (resp != null && resp.isRequestSuccess()) {

                	MPostResp respSuccess = analyzeBeanSuccess(resp);
                	respSuccess.setLocalPostId(relBean.getLocalPostId());
                	if (relBean.getPostLevel() == 0) {
                		startLocalData(HttpConstant.POST_RELEASE_UPDATE_ONE, GsonUtil.getGson().toJson(respSuccess), null);
                	} else if (relBean.getPostLevel() == 1) {
                    	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_ONE, GsonUtil.getGson().toJson(respSuccess), null);
                    }
                	Intent intent = new Intent();
                	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                	respSuccess.setSendState(PostReleaseCallBack.POST_RELEASE_SENDSUCCESS);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respSuccess);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("postReleaseBackground createPost sucess");
                } else {
                	if (bean.getPostLevel() == 0) {
                		UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_RELEASE_UPDATE_STATE_ONE, GsonUtil.getGson().toJson(stateBean), null);
                    } else if (bean.getPostLevel() == 1) {
                    	UpdatePostSendingState stateBean = new UpdatePostSendingState();
                		stateBean.setLocalPostId(bean.getLocalPostId());
                		stateBean.setLocalCreateTime(bean.getLocalCreateTime());
                		stateBean.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                    	startLocalData(HttpConstant.POST_DETAIL_UPDATE_SENDING_STATE, GsonUtil.getGson().toJson(stateBean), null);
                    }
                	
                	MPostResp respFailed = analyzeBeanSuccess(resp);
                	respFailed.setLocalPostId(relBean.getLocalPostId());
                	respFailed.setSendState(PostReleaseCallBack.POST_RELEASE_SENDFAILED);
                	Intent intent = new Intent();
                	intent.setAction(PostReleaseCallBack.MAINPOST_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("postReleaseBackground createPost failed");
                }
            }
        });
    }
    
	private PostReleaseReq analyzeBeanImg(final PostReleaseReq bean, String content, List<String> imgs) {
		PostReleaseReq newBean = new PostReleaseReq();
		
		newBean.setTopicId(bean.getTopicId());
		newBean.setTopicTitle(bean.getTopicTitle());
		newBean.setPostId(bean.getPostId());
		newBean.setLocalPostId(bean.getLocalPostId());
		newBean.setPostTitle(bean.getPostTitle());
		newBean.setParentId(bean.getParentId());
		newBean.setPostLevel(bean.getPostLevel());
		newBean.setToUserId(bean.getToUserId());
		newBean.setSelectedToSolution(bean.getSelectedToSolution());
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setSkin(bean.getSkin());
		newBean.setLocalCreateTime(bean.getLocalCreateTime());
		
		newBean.setPostContent(content);
		newBean.setThumbURL(imgs);
		
		return newBean;
	}
	
	private PostReleaseReq analyzeBeanRelease(PostReleaseReq bean) {
		PostReleaseReq newBean = new PostReleaseReq();
		
		newBean.setLocalPostId(bean.getLocalPostId());
		newBean.setTopicId(bean.getTopicId());
		newBean.setPostTitle(bean.getPostTitle());
		newBean.setPostContent(bean.getPostContent());
		newBean.setParentId(bean.getParentId());
		newBean.setPostLevel(bean.getPostLevel());
		newBean.setToUserId(bean.getToUserId());
		newBean.setSelectedToSolution(bean.getSelectedToSolution());
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setSkin(bean.getSkin());
		
		return newBean;
	}
	
	private PostReleaseReq analyzeBeanUpdate(PostReleaseReq bean) {
		PostReleaseReq newBean = new PostReleaseReq();
		
		newBean.setLocalPostId(bean.getLocalPostId());
		newBean.setTopicId(bean.getTopicId());
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

		return newBean;
	}
	
	private MPostResp analyzeBeanSuccess(MPostResp bean) {
		MPostResp resp = new MPostResp();
		
		resp.setId(bean.getId());
		resp.setPid(bean.getPid());
		resp.setTopicId(bean.getTopicId());
		resp.setTopicTitle(bean.getTopicTitle());
		resp.setPostLevel(bean.getPostLevel());
		resp.setUserId(bean.getUserId());
		resp.setUserName(bean.getUserName());
		resp.setToUserId(bean.getToUserId());
		resp.setToUserName(bean.getToUserName());
		resp.setPostTitle(bean.getPostTitle());
		resp.setPostContent(bean.getPostContent());
		resp.setFollowsUpNum(bean.getFollowsUpNum());
		resp.setStoreupNum(bean.getStoreupNum());
		resp.setSelectedToSolution(bean.isSelectedToSolution());
		resp.setEffect(bean.getEffect());
		resp.setReport(bean.getReport());
		resp.setOnTop(bean.getOnTop());
		resp.setThumbURL(bean.getThumbURL());
		resp.setLike(bean.getLike());
		resp.setDislike(bean.getDislike());
		resp.setUpdateTime(bean.getUpdateTime());
		resp.setCreateTime(bean.getCreateTime());
		resp.setPostViewCount(bean.getPostViewCount());
		resp.setTotalFollows(bean.getTotalFollows());
		resp.setLevel(bean.getLevel());
		resp.setLevelName(bean.getLevelName());
		resp.setGender(bean.isGender());
		resp.setBirthday(bean.getBirthday());
		resp.setDesc(bean.getDesc());
		resp.setOccupation(bean.getOccupation());
		resp.setUserHeadIcon(bean.getUserHeadIcon());
		resp.setSkinQuality(bean.getSkinQuality());
		resp.setQq(bean.getQq());
		resp.setEmail(bean.getEmail());
		resp.setWechat(bean.getWechat());
		resp.setBlog(bean.getBlog());

		return resp;
	}


	
	public void solutionReleaseBackground(SolutionCreateReq bean, int type) {

        if (TopicHelper.isImagePath(bean.getCoverUrl()) == TopicHelper.PATH_LOCAL_SDCARD) {
            Slog.d("solutionReleaseBackground upload cover img");
            solutionCoverImage(bean, type);
        } else {
            Slog.d("solutionReleaseBackground no cover img");
            solutionTextImage(bean, type);
        }
	}
	
    private void solutionCoverImage(final SolutionCreateReq bean, final int type) {
        
    	solutionRelease.solutionUplCoverImgAsync(bean.getCoverUrl(), new BaseCall<CoverImgResp>() {
            @Override
            public void call(CoverImgResp resp) {
                if (resp != null && resp.isRequestSuccess() &&
                        resp.getCoverImgUrl() != null &&
                        resp.getCoverThumbImgUrl() != null &&
                        !"".equals(resp.getCoverImgUrl().trim()) &&
                        !"".equals(resp.getCoverThumbImgUrl().trim())) {
                	Slog.d("solutionCoverImage sucess");
                	
                	bean.setCoverUrl(resp.getCoverImgUrl());
                    bean.setCoverThumbUrl(resp.getCoverThumbImgUrl());
                    
                    SolutionDataSave saveBean = new SolutionDataSave();  // (图片发送成功)更新某条护肤方案内容
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
					saveBean.setSendingData(bean);
                	startLocalData(HttpConstant.SOLUTION_DATA_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                	
                	solutionTextImage(bean, type);
                    
                } else {
                	SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
					saveBean.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	startLocalData(HttpConstant.SOLUTION_STATE_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                
                	MSolutionResp respFailed = new MSolutionResp();
                	respFailed.setSuccess(resp.isSuccess());//true,false
                	respFailed.setStatusCode(resp.getStatusCode());
                	respFailed.setMessage(resp.getMessage());
                	respFailed.setStatus(resp.getStatus());
                	respFailed.setLocalSolutionId(bean.getLocalSolutionId());
                	respFailed.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	
                	Intent intent = new Intent();
                	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    
                    Slog.d("solutionCoverImage failed");
                }
            }
        });
    }
    
    
    private void solutionTextImage(final SolutionCreateReq bean, final int type) {
        
        final BBSTextBuilder build = new BBSTextBuilderImpl(bean.getContent());
        if (build.getBBSDescribe() == null || build.getBBSDescribe().size() == 0) {
            // 没有图片，直接发布
            Slog.d("solutionTextImage no text img");
            if (type == 1) {
            	solutionRelease(bean);
            } else if (type == 2) {
            	solutionUpdate((SolutionUpdateReq)bean);
            }
        } else {
            final List<String> images = new ArrayList<String>();
            final SparseArray<String> imagesSparse = new SparseArray<String>();

            String log = "";
            // 将图片从帖子内容中抽取出来
            for (int i = 0; i < build.size(); i++) {
                BBSDescribeItem item = build.get(i);
                if (item.getType() == TopicHelper.TYPE_PLAN_STEP) {
                    String image = (String) item.getParam("image_01");
                    if (TopicHelper.isImagePath(image) == TopicHelper.PATH_LOCAL_SDCARD) {
                        images.add(image);
                        imagesSparse.put(i, image);
                        log += i + ":" + image + ", ";
                    }
                }
            }

            if (imagesSparse.size() == 0) {
                // 没有图片，直接发布
            	Slog.d("solutionTextImage no text img: imagesSparse.size() = 0");
                if (type == 1) {
                	solutionRelease(bean);
                } else if (type == 2) {
                	solutionUpdate((SolutionUpdateReq)bean);
                }
            } else {
            	Slog.d("solutionTextImage upload text images: " + log);
                if (Constants.COMPRESS_POST_IMAGE) {
                    List<String> compress = TopicHelper.compress(images);
                    if (compress != null && compress.size() == images.size()) {
                        images.clear();
                        images.addAll(compress);
                    }
                }
                // 上传图片
                solutionRelease.solutionUplTextImgAsync(images, new BaseCall<TextImgResp>() {
                    @Override
                    public void call(TextImgResp resp) {
                        if (resp != null
                                && resp.isRequestSuccess()
                                && resp.getTextImgUrl() != null
                                && resp.getTextImgUrl().size() == images.size()) {
                            
                            	String log = "";
                            	// 上传没有任何问题
                            	for (int i = 0; i < imagesSparse.size(); i++) {
                                	// 这里是为了减少不必要的循环，imagesSparse里面已经排序好保存内容中需要上传图片的索引
                                	String s = resp.getTextImgUrl().get(i);
                               		build.get(imagesSparse.keyAt(i)).putParam("image_01", s);
                                	log += i + ":" + s + ", ";
                                }
                            	bean.setContent(build.getString());
                            	Slog.d("solutionTextImage upload text images finished: " + log);
                                
                                SolutionDataSave saveBean = new SolutionDataSave();  // (图片发送成功)更新某条护肤方案内容
            					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
            					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
            					saveBean.setSendingData(bean);
                            	startLocalData(HttpConstant.SOLUTION_DATA_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                                
                                if (type == 1) {
                                	solutionRelease(bean);
                                } else if (type == 2) {
                                	solutionUpdate((SolutionUpdateReq)bean);
                                }
                                Slog.d("solutionTextImage upload text img sucess");
                            
                        } else {
                        	Slog.d("solutionTextImage upload text img failed");
                        	
                        	SolutionDataSave saveBean = new SolutionDataSave();
        					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
        					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
        					saveBean.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                        	startLocalData(HttpConstant.SOLUTION_STATE_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                        	
                            MSolutionResp respFailed = new MSolutionResp();
                        	respFailed.setSuccess(resp.isSuccess());//true,false
                        	respFailed.setStatusCode(resp.getStatusCode());
                        	respFailed.setMessage(resp.getMessage());
                        	respFailed.setStatus(resp.getStatus());
                        	respFailed.setLocalSolutionId(bean.getLocalSolutionId());
                        	respFailed.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                        	
                        	Intent intent = new Intent();
                        	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                        	Bundle bundle = new Bundle();
                        	bundle.putSerializable("RespData", respFailed);
                        	intent.putExtras(bundle);
                            mContext.sendBroadcast(intent);
                        }
                    }
                });
            }
        }
    }
	
    private void solutionRelease(final SolutionCreateReq bean) {
        
        solutionRelease.solutionCreateAsync(bean, new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                	SolutionDataSave saveBean = new SolutionDataSave();  // 及时更新方案列表数据(包含其他分类)
					saveBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
					saveBean.setUpdateTime(0);
                	startLocalData(HttpConstant.SOLUTION_TIMESTAMP_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                	
                	SolutionDataGetDelete delBean = new SolutionDataGetDelete();  // 发送成功，删除本地数据
					delBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					delBean.setLocalSolutionId(bean.getLocalSolutionId());
					startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
                	
					MSolutionResp respSuccess = analyzeRespRelease(resp);
                	respSuccess.setLocalSolutionId(bean.getLocalSolutionId());
                	respSuccess.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_SUCCESS);
                	
                	Intent intent = new Intent();
                	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respSuccess);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("solutionRelease create solution sucess");
                    
                } else {
            		SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
					saveBean.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	startLocalData(HttpConstant.SOLUTION_STATE_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                	
                	MSolutionResp respFailed = analyzeRespRelease(resp);
                	respFailed.setLocalSolutionId(bean.getLocalSolutionId());
                	respFailed.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	
                	Intent intent = new Intent();
                	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("solutionRelease create solution failed");
                }
            }
        });
    }
    
	public static MSolutionResp analyzeRespRelease(MSolutionResp bean) {
		MSolutionResp resp = new MSolutionResp();
		
		resp.setSolutionId(bean.getSolutionId());
		resp.setLocalSolutionId(bean.getLocalSolutionId());
		resp.setSendState(bean.getSendState());
		resp.setCommentId(bean.getCommentId());
		resp.setParentCommId(bean.getParentCommId());
		resp.setEffect(bean.getEffect());
		resp.setPart(bean.getPart());
		resp.setTitle(bean.getTitle());
		resp.setIntroduction(bean.getIntroduction());
		resp.setContent(bean.getContent());
		resp.setCoverImgUrl(bean.getCoverImgUrl());
		resp.setCoverThumbImgUrl(bean.getCoverThumbImgUrl());
		resp.settextImgUrl(bean.getTextImgUrl());
		resp.setUseCycle(bean.getUseCycle());
		resp.setScore(bean.getScore());
		resp.setUsedCount(bean.getUsedCount());
		resp.setStoreUpCount(bean.getStoreUpCount());
		resp.setCreateTime(bean.getCreateTime());
		resp.setUpdateTime(bean.getUpdateTime());
		resp.setStartTime(bean.getStartTime());
		resp.setUserId(bean.getUserId());
		resp.setNickName(bean.getNickName());
		resp.setToUserId(bean.getToUserId());
		resp.setToNickName(bean.getToNickName());
		resp.setPortrait(bean.getPortrait());
		resp.setIsStoreup(bean.getIsStoreup());
		resp.setInUse(bean.getInUse());
		resp.setRank(bean.getRank());
		resp.setLikeCount(bean.getLikeCount());
		resp.setIsLike(bean.getIsLike());
		
		return resp;
	}
	
    private void solutionUpdate(final SolutionUpdateReq bean) {
        
        solutionRelease.solutionUpdateAsync(bean, new BaseCall<MSolutionResp>() {
            @Override
            public void call(MSolutionResp resp) {
                if (resp != null && resp.isRequestSuccess()) {
                	SolutionDataSave saveBean = new SolutionDataSave();  // 及时更新大全列表数据
					saveBean.setType(SolutionListData.SOLUTION_LIST_TYPE);
					saveBean.setUpdateTime(0);
                	startLocalData(HttpConstant.SOLUTION_TIMESTAMP_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                	
                	SolutionDataGetDelete delBean = new SolutionDataGetDelete();  // 发送成功，删除本地数据
					delBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					delBean.setLocalSolutionId(bean.getLocalSolutionId());
					startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
                	
					MSolutionResp respSuccess = analyzeRespRelease(resp);
                	respSuccess.setLocalSolutionId(bean.getLocalSolutionId());
                	respSuccess.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_SUCCESS);
                	
                	Intent intent = new Intent();
                	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respSuccess);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("solutionUpdate update solution sucess");
                    
                } else {
            		SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setLocalSolutionId(bean.getLocalSolutionId());
					saveBean.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	startLocalData(HttpConstant.SOLUTION_STATE_ITEM_UPDATE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
                	
                	MSolutionResp respFailed = analyzeRespRelease(resp);
                	respFailed.setLocalSolutionId(bean.getLocalSolutionId());
                	respFailed.setSendState(SolutionCreateUpdate.SOLUTION_RELEASE_FAILED);
                	
                	Intent intent = new Intent();
                	intent.setAction(SolutionCreateUpdate.SOLUTION_RELEASE_END);
                	Bundle bundle = new Bundle();
                	bundle.putSerializable("RespData", respFailed);
                	intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                    Slog.d("solutionUpdate update solution failed");
                }
            }
        });
    }
    
	private SolutionUpdateReq analyzeReqUpdate(SolutionUpdateReq bean) {
		SolutionUpdateReq newBean = new SolutionUpdateReq();
		
		newBean.setSolutionId(bean.getSolutionId());
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setTitle(bean.getTitle());
		newBean.setIntroduction(bean.getIntroduction());
		newBean.setContent(bean.getContent());
		newBean.setCoverUrl(bean.getCoverUrl());
		newBean.setCoverThumbUrl(bean.getCoverThumbUrl());
		newBean.setUseCycle(bean.getUseCycle());

		return newBean;
	}
    
	
	
	private void startUpLocal(final String localRequestParams, final String localContent, final ClientCallback cltCallback) {
		try {
			if (callbackID > 999999999) {
				callbackID = 0;
			}
			mServer.startLocalData(localRequestParams, localContent, ++callbackID);
			//this.clientCallback = cltCallback;
			if (!mCallbackMap.isFull()) {
				if (cltCallback != null) {
					mCallbackMap.insert(callbackID, cltCallback);
				}
			} else {
				if (cltCallback != null) {
					String callback = resultStatus("{\"status\":\"FAILED\",\"msg\":\"call back queue is full\",\"ok\":true}", BaseResp.UNKNOWN, false);
					cltCallback.onSuccess(callback);
				}
			}
			Slog.d("startUpLocal cltCallback: " + cltCallback + " callbackID: " + callbackID);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void cancelCallback(final int callBackId) {
		if (!mCallbackMap.isEmpty()) {
			mCallbackMap.remove(callBackId);
		}
	}
	
	private IRadarCallback.Stub mIRadarCallback = new IRadarCallback.Stub() {
		@Override
		public void onTestScriptResult(String serverResult, final int callBackId) throws RemoteException {
			Message msg = Message.obtain();
			msg = mHandler.obtainMessage(MSG_RESULT_CALLBACK, callBackId, 1, serverResult);
			mHandler.sendMessage(msg);
		}
	};

	
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
	
	
	class CallbackMap    //回调map类
	{
		private int maxSize;  // 回调map长度，由构造函数初始化, 做一下限制
		private Map<Integer, Object> callBackParam;

		@SuppressLint("UseSparseArrays")
		public CallbackMap(int size)
		{
			maxSize = size;
			callBackParam = new HashMap<Integer, Object>();
		}

		public void insert(int callBackId, ClientCallback callBack)    // 添加回调
		{
			callBackParam.put(callBackId, callBack);
    		Slog.d("CallbackMap insert callBackId: " + callBackId + "  callBack: " + callBack);
    	}

		public void remove(int callBackId)    // 删除回调
		{
			Iterator<Integer> it = callBackParam.keySet().iterator(); 
			while(it.hasNext())
			{
				it = callBackParam.keySet().iterator();
				Integer Id = (Integer)it.next();               
				if (Id == callBackId) {
					callBackParam.remove(callBackId);
					Slog.d("CallbackMap remove callBackId: " + callBackId);
					break;
				}
			}
		}
        
		public ClientCallback getEntry(int callBackId)    // 获取回调
		{
			Iterator<?> it = callBackParam.entrySet().iterator();
			ClientCallback callBack = null;
			while(it.hasNext())
			{
				@SuppressWarnings("unchecked")
				Entry<Integer, ?> currentEntry = (Entry<Integer, ?>) it.next();
            	
				if (currentEntry.getKey() == callBackId) {
					callBack = (ClientCallback) currentEntry.getValue();
					callBackParam.remove(callBackId);
					break;
				}
			}
			Slog.d("CallbackMap getEntry callBackId: " + callBackId + "  callBack: " + callBack);
			return callBack;
		}

		public boolean isEmpty()    // 判队列是否为空。若为空返回一个真值，否则返回一个假值。
		{
			return (callBackParam.size() == 0);
    	}

		public boolean isFull()    // 判断回调是否满
		{
			return (callBackParam.size() == maxSize);
		}

		public int size()    // 回调map的长度
		{
			return callBackParam.size();
		}
        
	}

}
