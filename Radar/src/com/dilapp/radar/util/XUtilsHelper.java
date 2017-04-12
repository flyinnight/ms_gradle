/*********************************************************************/
/*  文件名  XUtilsHelper.java    　                                     */
/*  程序名  XUtils帮助类                     						     				 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.o;
import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.server.HttpCallback;
import com.dilapp.radar.server.ServerRequestParams;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
//import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
//import com.lidroid.xutils.http.client.multipart.MultipartEntity;
//import com.lidroid.xutils.http.client.multipart.content.FileBody;
//import com.lidroid.xutils.http.client.multipart.content.StringBody;

public class XUtilsHelper {
	// 上传图片
	public static final int UPLOAD_FILE = -1;
	public static final int UPLOAD_FILE_MULTI_PARAM = -2;
	private final String TAG = "XUtilsHelper";
	private Context context;
	private HttpCallback httpCallback;
	private HttpUtils httpUtils;
	private HttpClient httpClient;
	//private RequestParams params;
	//private String uploadUrl;
	private String filename;

	/**
	 * @param context
	 * @param httpCallback
	 *            http结果回调
	 */
	public XUtilsHelper(Context context, HttpCallback httpCallback) {
		this.context = context;
		this.httpUtils = XutilsHttpClient.getInstence(context);
		this.httpCallback = httpCallback;
	}

	public void send(final ServerRequestParams serverRequestParams, final int callBackId) {
		//this.uploadUrl = httpUrlEncoder(serverRequestParams.getRequestUrl());
		Log.d("Radar", "XUtilsHelper send: " + callBackId);
		
		if (!StringUtils.isEmpty(serverRequestParams.getStatus())) {
			switch(serverRequestParams.getStatus()) {
				/*case UPLOAD_FILE:
					setRequestParamsFile(serverRequestParams.getRequestParam());
					sendPostLess(callBackId); 
					return;*/
				case UPLOAD_FILE_MULTI_PARAM:
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							sendPostEntityFile(serverRequestParams.getToken(), serverRequestParams.getRequestUrl(), serverRequestParams.getRequestParam(), callBackId);
						}
					});
					thread.start();
					return; 

				/*case GET_USER_INFO_TMP:
					sendGet(HttpConstant.OFFICIAL_USER_HOST_IP + "user/ws/getUsersByToken/" + serverRequestParams.getToken(), callBackId);
					return; */
					
				default: 
					break; 
				} 
		}
		 
		if (StringUtils.isEmpty(serverRequestParams.getRequestParam())
				&& StringUtils.isEmpty(serverRequestParams.getRequestEntity())) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("token", serverRequestParams.getToken());
			sendPostLess(serverRequestParams.getRequestUrl(), map, callBackId);
		} else if (serverRequestParams.getRequestEntity() == null) {
			sendPostLess(serverRequestParams.getRequestUrl(), serverRequestParams.getRequestParam(), callBackId);
		} else {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					sendPostEntity(serverRequestParams.getToken(), serverRequestParams.getRequestUrl(), serverRequestParams.getRequestEntity(), callBackId);
				}
			});
			thread.start();
		}
	}

	private void sendGet(final String uploadUrl,final int callBackId) {
		httpUtils.send(HttpRequest.HttpMethod.GET, uploadUrl, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO
				httpCallback.onServerMessage(arg0.result, callBackId);
			}
		});
	}

	/**
	 * @param map
	 */
/*	private void setRequestParams(Map<String, ?> map) {
		params = new RequestParams("UTF-8");
		Iterator<?> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> currentEntry = (Entry<String, ?>) iterator.next();
			
			//if ((((String) currentEntry.getValue()).indexOf("/storage") != -1) || (((String) currentEntry.getValue()).indexOf("/sdcard") != -1)) {
				//File file = new File((String) currentEntry.getValue());
				//params.addBodyParameter(currentEntry.getKey(), file);
			//}

			params.addBodyParameter(currentEntry.getKey(), (String) currentEntry.getValue());
		}
	}*/
	
/*	private void setRequestParamsFile(Map<String, ?> map) {
		params = new RequestParams("UTF-8");
		Iterator<?> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> currentEntry = (Entry<String, ?>) iterator.next();

			if ((currentEntry.getKey().equals("postImgFile")) || (currentEntry.getKey().equals("topicImgFile"))
					|| (currentEntry.getKey().equals("portrait")) || (currentEntry.getKey().equals("facialPics"))) {
				ArrayList<String> mImgList = null;
				mImgList = (ArrayList<String>) currentEntry.getValue();
				if(mImgList != null){
					for (int i = 0; i < mImgList.size(); i++) {
						File file = new File((String) mImgList.get(i));
						params.addBodyParameter(currentEntry.getKey(), file);
					}
				}
				continue;
			}

			params.addBodyParameter(currentEntry.getKey(), (String) currentEntry.getValue());
		}
	}*/
	
	private String httpUrlEncoder(String initialUrl) {
		String new_url = null;
		try {
			// 保存网络资源文件名，要在转码之前保存，否则是乱码
			filename = initialUrl.substring(initialUrl.lastIndexOf("/") + 1, initialUrl.length());
			String old_url = URLEncoder.encode(initialUrl, "UTF-8");
			new_url = old_url.replace("%3A", ":").replace("%2F", "/").replace("%3F", "?").replace("%3D", "=")
					.replace("%26", "&").replace("%2C", ",").replace("%20", " ").replace("+", "%20")
					.replace("%2B", "+").replace("%23", "#").replace("#", "%23");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new_url;
	}

	/**
	 * POST方式请求服务器资源
	 */
	private void sendPostLess(final String uploadUrl, final Map<String, ?> map, final int callBackId) {
		RequestParams params;
		
		Log.d("Radar", "sendPostLess uploadUrl: " + uploadUrl + " callBackId: " + callBackId);
		params = new RequestParams("UTF-8");
		params.setHeader("Accept", "application/json");
		Iterator<?> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> currentEntry = (Entry<String, ?>) iterator.next();
			params.addBodyParameter(currentEntry.getKey(), (String) currentEntry.getValue());
		}
		
		httpUtils.send(HttpRequest.HttpMethod.POST, uploadUrl, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				if (arg0.statusCode == 200) {
					// 无数据判断
					if (StringUtils.isEmpty(arg0.result)
							|| arg0.result.trim().startsWith("<response totalRows='0'>".trim())
							|| arg0.result.trim().contains("<items totalRows='0'>".trim())
							|| arg0.result.trim().contains("<items totalRows='0'".trim())
							|| "<classes/>".trim().equals(arg0.result.trim())
							|| "<classes/>".trim().equals(arg0.result.trim())
							|| "[{\"totalRows\":0}]".trim().equals(arg0.result.trim())) {

						httpCallback.onServerMessage(resultStatus(arg0.result, statuCodeFactory(arg0.result), false), callBackId);
					} else {
						httpCallback.onServerMessage(resultStatus(arg0.result, statuCodeFactory(arg0.result), true), callBackId);
					}
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", statuCodeFactory(arg1), false), callBackId);
				arg0.printStackTrace();
				Slog.f("Filelog: sendPostLess onFailure: " + arg0);
			}
		});
	}

	/**
	 * 上传文件到服务器
	 * 
	 * @param param
	 *            提交参数名称
	 * @param file
	 *            要上传的文件对象
	 */
	@SuppressWarnings("unused")
	private void uploadFile(final String uploadUrl, final Map<String, ?> map, final int callBackId) {
		RequestParams params;
		
		params = new RequestParams("UTF-8");
		Iterator<?> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> currentEntry = (Entry<String, ?>) iterator.next();
			params.addBodyParameter(currentEntry.getKey(), (String) currentEntry.getValue());
		}
		
		httpUtils.send(HttpRequest.HttpMethod.POST, uploadUrl, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				// TODO
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				httpCallback.onServerMessage(resultStatus(arg0.result, BaseResp.OK, true), callBackId);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
				httpCallback.onServerMessage(arg1, callBackId);
			}
		});
	}

	/**
	 * 从服务器上下载文件保存到磁盘
	 * 
	 * @param saveLocation
	 *            下载的文件保存路径
	 * @param downloadBtn
	 *            触发下载操作的控件按钮，用于设置下载进度情况
	 */
	@SuppressWarnings("unused")
	private void downloadFile(final String uploadUrl, String saveLocation, final Button downloadBtn, final int callBackId) {
		httpUtils.download(uploadUrl, saveLocation + filename, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				// TODO 连接服务器中...
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO 下载中...
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// TODO 打开文件
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
			}
		});
	}

	/**
	 * 从服务器上下载文件保存到系统磁盘上，此方法会弹出进度对话框显示下载进度信息（
	 * 有的需要知道文件是否下载完成，如果下载完成返回的是改文件在磁盘中的完整路径）
	 * 
	 * @param saveLocation
	 *            下载的文件保存路径
	 */
	@SuppressWarnings("unused")
	private void downloadFile(final String uploadUrl, String saveLocation, final int callBackId) {
		httpUtils.download(uploadUrl, saveLocation + filename, new RequestCallBack<File>() {

			@Override
			public void onStart() {
				// TODO 连接服务器中...
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO 下载中...
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				// TODO 打开文件...
				httpCallback.onServerMessage(null, callBackId);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				arg0.printStackTrace();
			}
		});
	}

	// When HttpClient instance is no longer needed,
	// shut down the connection manager to ensure
	// immediate deallocation of all system resources
	private void sendPostEntity(final String token, final String uploadUrl, final String entityParam, final int callBackId) {
		if (httpClient == null) {
			this.httpClient = httpUtils.getHttpClient();
		}
		Log.d("Radar", "sendPostEntity uploadUrl: " + uploadUrl + " callBackId: " + callBackId);
		httpClient.getParams().setIntParameter("http.socket.timeout", 20000);
		HttpPost httppost = new HttpPost(uploadUrl);
		httppost.setHeader("token", token);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-Type", "application/json");
		//httppost.setHeader("charset", "utf-8");
		
		HttpResponse response;
		try {
			httppost.setEntity(httpEntity(entityParam));
			response = httpClient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String responseResults = EntityUtils.toString(entity, "UTF-8");
				httpCallback.onServerMessage(resultStatus(responseResults, statuCodeFactory(responseResults), true), callBackId);
			}
		} catch (ClientProtocolException e) {
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.UNKNOWN, false), callBackId);
			e.printStackTrace();
		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.TIME_OUT, false), callBackId);
			e.printStackTrace();
		}  catch (IOException e) {
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.NET_ERROR, false), callBackId);
			e.printStackTrace();
		} finally {
			// httpClient.getConnectionManager().shutdown();
		}
	}


	private void sendPostEntityFile(final String token, final String uploadUrl, final Map<String, ?> map, final int callBackId) {
		if (httpClient == null) {
			this.httpClient = httpUtils.getHttpClient();
		}
		Log.d("Radar", "sendPostEntityFile uploadUrl: " + uploadUrl + " callBackId: " + callBackId);
		httpClient.getParams().setIntParameter("http.socket.timeout", 20000);
		HttpPost httppost = new HttpPost(uploadUrl);
/*		httppost.setHeader("token", token);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("charset", "UTF-8");*/
		HttpResponse response;
		
		try {
			/*MultipartEntity multiEntity = new MultipartEntity();
			multiEntity.addPart("postImgFile", new FileBody(new File(entityParam)));
			multiEntity.addPart("postImgFile", new StringBody(entityParam));
			httppost.setEntity(multiEntity);
			response = httpClient.execute(httppost);*/

	        httppost.setHeader("token", token);
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	        builder.setCharset(Charset.forName("UTF-8"));
	        
			Iterator<?> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("unchecked")
				Entry<String, ?> currentEntry = (Entry<String, ?>) iterator.next();

				if ((currentEntry.getKey().equals("postImgFile")) || (currentEntry.getKey().equals("topicImgFile"))
						|| (currentEntry.getKey().equals("portrait")) || (currentEntry.getKey().equals("facialPics"))
						|| (currentEntry.getKey().equals("bannerImgFile")) || (currentEntry.getKey().equals("ImgFile"))) {
					ArrayList<String> mImgList = null;
					mImgList = (ArrayList<String>) currentEntry.getValue();
					if(mImgList != null){
						for (int i = 0; i < mImgList.size(); i++) {
							File file = new File((String) mImgList.get(i));
					        builder.addPart(currentEntry.getKey(), new FileBody(file));
						}
					}
					//break;
				}
				
				if (currentEntry.getKey().equals("type")) {
					builder.addPart(currentEntry.getKey(), new StringBody((String) currentEntry.getValue()));
				}
			}

	        httppost.setEntity(builder.build());
	        response = httpClient.execute(httppost);
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String responseResults = EntityUtils.toString(entity, "UTF-8");
				httpCallback.onServerMessage(resultStatus(responseResults, statuCodeFactory(responseResults), true), callBackId);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.UNKNOWN, false), callBackId);
		} catch (org.apache.http.conn.ConnectTimeoutException e) {
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.TIME_OUT, false), callBackId);
			e.printStackTrace();
		} catch (IOException e) {
			httpCallback.onServerMessage(resultStatus("{\"status\":\"FAILED\",\"msg\":\"\",\"ok\":false}", BaseResp.NET_ERROR, false), callBackId);
			e.printStackTrace();
		} finally {
			// httpClient.getConnectionManager().shutdown();
		}
	}

	private HttpEntity httpEntity(String entityParam) {
		HttpEntity entity = null;
		try {
			entity = new StringEntity(entityParam, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
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

	// 工厂方法
	private int statuCodeFactory(String result) {
		
		if (StringUtils.isEmpty(result)) {
			return BaseResp.RETURN_NULL;
		}
		if (result.contains("ConnectTimeoutException") || result.contains("ConnectionPoolTimeoutException") || result.contains("SocketTimeoutException")) {
			return BaseResp.TIME_OUT;
		}
		if (result.contains("HttpHostConnectException") || result.contains("NoHttpResponseException")) {
			return BaseResp.NET_ERROR;
		}
		//if (result.contains("Internal Server Error") || result.contains("WelcomePage")) {
		//	return BaseResp.INTERNAL_SERVER_ERROR;
		//}
		
		try {
			JSONObject obj = new JSONObject(result);
			String statusString = obj.optString("msg");
			if (("username or password is wrong!".equals(statusString)) || ("phone number is invalid!".equals(statusString)) 
					|| ("user has registered!".equals(statusString))) {
				return BaseResp.PARAM_ERROR;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return BaseResp.OK;
	}
}
