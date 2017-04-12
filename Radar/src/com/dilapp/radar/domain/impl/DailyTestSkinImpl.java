/*********************************************************************/
/*  文件名  DailyTestSkinImpl.java    　                                */
/*  程序名  每日测试域实现                     						     				 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
package com.dilapp.radar.domain.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.ProductsTestSkin.SaveReordReq;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;


public class DailyTestSkinImpl extends DailyTestSkin {
	private final String TAG = "DailyTestSkinImpl.class";
	private Context context;
	private Handler handler;

	public DailyTestSkinImpl(Context context) {
		this.context = context;
	}

	//添加/上传单条测试数据
	@Override
	public void dailyTestSkinAsync(TestSkinReq bean, final BaseCall<TestSkinResp> call) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((TestSkinResp) msg.obj);
				}
			}
		};

		uploadServer(bean);
	}

	private void uploadServer(final TestSkinReq bean) {
		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				// TODO 返回上次的计算结果
				/**
				 * {"msg":"ok","status":"SUCCESS","ok":true,"values":{
				 * "elasticMax":100,"oilMax":100,"whiteningAvg":0,"poreMax":100,
				 * "sensitiveLast"
				 * :0,"elasticLast":0,"elasticAvg":0,"whiteningMax"
				 * :100,"poreAvg"
				 * :0,"poreLast":0,"whiteningLast":0,"sensitiveMax"
				 * :100,"waterMax"
				 * :100,"oilAvg":0,"waterAvg":0,"sensitiveAvg":0,"oilLast"
				 * :0,"waterLast":0}}
				 */
				JSONObject jsonObject;
				TestSkinResp resp = new TestSkinResp();
				Log.d("Radar", "dailyTestSkinAsync: uploadServer" + result);
				try {
					
					jsonObject = new JSONObject(result);
					JSONObject obj1 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(obj1.optString("msg"));
					resp.setStatus(obj1.optString("status"));
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					Object temp = obj1.opt("values");
					if (temp != null){
						TestSkinResp testSkinResp = GsonUtil.getGson().fromJson(obj1.optString("values"),
								TestSkinResp.class);

						resp.setWaterAvg(testSkinResp.getWaterAvg());
						resp.setWaterLast(testSkinResp.getWaterLast());
						resp.setWaterMax(testSkinResp.getWaterMax());

						resp.setOilAvg(testSkinResp.getOilAvg());
						resp.setOilLast(testSkinResp.getOilLast());
						resp.setOilMax(testSkinResp.getOilMax());

						resp.setElasticAvg(testSkinResp.getElasticAvg());
						resp.setElasticLast(testSkinResp.getElasticLast());
						resp.setElasticMax(testSkinResp.getElasticMax());

						resp.setSensitiveAvg(testSkinResp.getSensitiveAvg());
						resp.setSensitiveLast(testSkinResp.getSensitiveLast());
						resp.setSensitiveMax(testSkinResp.getSensitiveMax());

						resp.setWhiteningAvg(testSkinResp.getWhiteningAvg());
						resp.setWhiteningLast(testSkinResp.getWhiteningLast());
						resp.setWhiteningMax(testSkinResp.getWhiteningMax());

						resp.setPoreAvg(testSkinResp.getPoreAvg());
						resp.setPoreLast(testSkinResp.getPoreLast());
						resp.setPoreMax(testSkinResp.getPoreMax());
					}
					SaveReordReq saveRecords = analyzeSaveData(bean);
					if ("SUCCESS".equalsIgnoreCase(obj1.optString("status"))) {
						saveRecords.setIfCloud(DailyTestSkin.UPLOADED_SERVER);
					} else {
						saveRecords.setIfCloud(DailyTestSkin.NOT_UPLOAD_SERVER);
					}
					RadarProxy.getInstance(context).startLocalData(HttpConstant.PRODUCT_TEST_WIRTE_LISTS, GsonUtil.getGson().toJson(saveRecords), null);

				} catch (JSONException e) {
					e.printStackTrace();
					resp.setStatus("FAILED");
					Log.d("Radar", "JSONException: " + e);
					SaveReordReq saveRecords = analyzeSaveData(bean);
					saveRecords.setIfCloud(DailyTestSkin.NOT_UPLOAD_SERVER);
					RadarProxy.getInstance(context).startLocalData(HttpConstant.PRODUCT_TEST_WIRTE_LISTS, GsonUtil.getGson().toJson(saveRecords), null);
				}
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				TestSkinResp resp = new TestSkinResp();
				resp.setStatus("FAILED");
				SaveReordReq saveRecords = analyzeSaveData(bean);
				saveRecords.setIfCloud(DailyTestSkin.NOT_UPLOAD_SERVER);
				RadarProxy.getInstance(context).startLocalData(HttpConstant.PRODUCT_TEST_WIRTE_LISTS, GsonUtil.getGson().toJson(saveRecords), null);
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}


	private SaveReordReq analyzeSaveData(TestSkinReq bean) {
		SaveReordReq saveRecords = new SaveReordReq();
		FacialAnalyzeBean serverBean = new FacialAnalyzeBean();
		
		serverBean.setRid(bean.getRid());
		serverBean.setUid(bean.getUid());
		serverBean.setAnalyzeTime(bean.getAnalyzeTime());
		serverBean.setSubtype(bean.getSubtype());
		serverBean.setAnalyzePlace(bean.getAnalyzePlace());
		serverBean.setAnalyzeClimate(bean.getAnalyzeClimate());
		serverBean.setCosmeticID(bean.getCosmeticID());
		serverBean.setSchemaID(bean.getSchemaID());
		serverBean.setLabelID(bean.getLabelID());
		
		serverBean.setType(bean.getType());
		serverBean.setAnalyzePart(bean.getPart());
		serverBean.setParam1Value(1);
		serverBean.setParam1Result(String.valueOf(bean.getWater()));
		serverBean.setParam2Value(2);
		serverBean.setParam2Result(String.valueOf(bean.getOil()));
		serverBean.setParam3Value(3);
		serverBean.setParam3Result(String.valueOf(bean.getEastic()));
		serverBean.setParam4Value(4);
		serverBean.setParam4Result(String.valueOf(bean.getSensitive()));
		serverBean.setParam5Value(5);
		serverBean.setParam5Result(String.valueOf(bean.getWhitening()));
		serverBean.setParam6Value(6);
		serverBean.setParam6Result(String.valueOf(bean.getPore()));
		serverBean.setParam7Value(7);
		serverBean.setParam7Result(String.valueOf(bean.getSkinAge()));

		List<FacialAnalyzeBean> list = new ArrayList<FacialAnalyzeBean>();
		list.add(serverBean);
		saveRecords.setValue(list);;
		
		return saveRecords;
	}
	
	private ServerRequestParams writeParams(TestSkinReq bean) {
		ServerRequestParams serverRequestParams = new ServerRequestParams();
		serverRequestParams.setToken(HttpConstant.TOKEN);
		serverRequestParams.setRequestUrl(HttpConstant.getDailyTestSkinUrl(null));
		serverRequestParams.setRequestParam(null);
		serverRequestParams.setRequestEntity(analyzeBeanToJson(bean));
		return serverRequestParams;
	}

	private String analyzeBeanToJson(TestSkinReq bean) {
		FacialAnalyzeBean serverBean = new FacialAnalyzeBean();
		serverBean.setRid(bean.getRid());
		serverBean.setUid(bean.getUid());
		serverBean.setAnalyzeTime(bean.getAnalyzeTime());
		serverBean.setSubtype(bean.getSubtype());
		serverBean.setAnalyzePlace(bean.getAnalyzePlace());
		serverBean.setAnalyzeClimate(bean.getAnalyzeClimate());
		serverBean.setCosmeticID(bean.getCosmeticID());
		serverBean.setSchemaID(bean.getSchemaID());
		serverBean.setLabelID(bean.getLabelID());
		
		serverBean.setType(bean.getType());
		serverBean.setAnalyzePart(bean.getPart());
		serverBean.setParam1Value(1);
		serverBean.setParam1Result(String.valueOf(bean.getWater()));
		serverBean.setParam2Value(2);
		serverBean.setParam2Result(String.valueOf(bean.getOil()));
		serverBean.setParam3Value(3);
		serverBean.setParam3Result(String.valueOf(bean.getEastic()));
		serverBean.setParam4Value(4);
		serverBean.setParam4Result(String.valueOf(bean.getSensitive()));
		serverBean.setParam5Value(5);
		serverBean.setParam5Result(String.valueOf(bean.getWhitening()));
		serverBean.setParam6Value(6);
		serverBean.setParam6Result(String.valueOf(bean.getPore()));
		serverBean.setParam7Value(7);
		serverBean.setParam7Result(String.valueOf(bean.getSkinAge()));

		return GsonUtil.getGson().toJson(serverBean);
	}
}
