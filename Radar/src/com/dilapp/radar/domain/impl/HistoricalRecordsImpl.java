/*********************************************************************/
/*  文件名  HistoricalRecordsImpl.java    　                            */
/*  程序名  历史记录域实现                     						     				 */
/*  版本履历   2015/5/5  修改                  刘伟    			                             */
/*         Copyright 2015 LENOVO. All Rights Reserved.               */
/*********************************************************************/
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

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.HistoricalRecords;
import com.dilapp.radar.domain.ProductsTestSkin.SaveReordReq;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.google.gson.reflect.TypeToken;

public class HistoricalRecordsImpl extends HistoricalRecords {
	private Context context;

	public HistoricalRecordsImpl(Context context) {
		this.context = context;
	}

	//根据条件查询服务器存储的测试数据
	@SuppressLint("HandlerLeak")
	@Override
	public void historicalRecordsAsync(HistoricalReq bean, final BaseCall<MHistoricalResp> call) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((MHistoricalResp) msg.obj);
				}
			}
		};
		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				MHistoricalResp mresp = new MHistoricalResp();
				Log.d("Radar", "historicalRecordsAsync " + result);
				try {
					jsonObject = new JSONObject(result);
					mresp.setSuccess(jsonObject.optBoolean("success"));
					mresp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject1 = new JSONObject(jsonObject.optString("message"));
					mresp.setStatus(jsonObject1.optString("status"));
					mresp.setMessage(jsonObject1.optString("msg"));
					Object temp = jsonObject1.opt("values");
					if (temp != null) {
						List<FacialAnalyzeBean> respList = GsonUtil.getGson().fromJson(jsonObject1.optString("values"),
								new TypeToken<List<FacialAnalyzeBean>>() {
								}.getType());
						List<FacialAnalyzeResp> mrespList = new ArrayList<FacialAnalyzeResp>();
						for (int i = 0; i < respList.size(); i++) {
							FacialAnalyzeResp resp = analyzeBeanToResp(respList.get(i));
							mrespList.add(resp);
						}
						mresp.setValue(mrespList);
					}
					
					/*if ("SUCCESS".equalsIgnoreCase(jsonObject1.optString("status"))) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SCANNING_ANALYZE, null, new ClientCallbackImpl() {
							@Override
							public void onSuccess(String result) {
								Log.d("Radar", "historicalRecordsAsync: SCANNING_ANALYZE " + result);

								HistoricalResp resp1 = GsonUtil.getGson().fromJson(result, HistoricalResp.class);
								uploadScanningDataServer(resp1);
							}

							@Override
							public void onFailure(String result) {
								System.out.println(result);
							}
						});
					}*/
					
				} catch (JSONException e) {
					e.printStackTrace();
					mresp.setStatus("FAILED");
					Log.d("Radar", "JSONException: " + e);
				}
				Message msg = Message.obtain();
				msg.obj = mresp;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				MHistoricalResp resp = new MHistoricalResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	private FacialAnalyzeResp analyzeBeanToResp(FacialAnalyzeBean bean) {
		FacialAnalyzeResp serverResp = new FacialAnalyzeResp();
		serverResp.setRid(bean.getRid());
		serverResp.setUid(bean.getUid());
		serverResp.setType(bean.getType());
		serverResp.setSubtype(bean.getSubtype());
		serverResp.setAnalyzePart(bean.getAnalyzePart());
		serverResp.setAnalyzeTime(bean.getAnalyzeTime());
		serverResp.setAnalyzePlace(bean.getAnalyzePlace());
		serverResp.setAnalyzeClimate(bean.getAnalyzeClimate());
		serverResp.setCosmeticID(bean.getCosmeticID());
		serverResp.setSchemaID(bean.getSchemaID());
		serverResp.setLabelID(bean.getLabelID());
		
		serverResp.setWaterStandard(bean.getParam1Standard());
		serverResp.setWaterResult(Integer.parseInt(bean.getParam1Result()));
		serverResp.setOilStandard(bean.getParam2Standard());
		serverResp.setOilResult(Integer.parseInt(bean.getParam2Result()));
		serverResp.setElasticStandard(bean.getParam3Standard());
		serverResp.setElasticResult(Integer.parseInt(bean.getParam3Result()));
		serverResp.setSensitiveStandard(bean.getParam4Standard());
		serverResp.setSensitiveResult(Integer.parseInt(bean.getParam4Result()));
		serverResp.setWhiteningStandard(bean.getParam5Standard());
		serverResp.setWhiteningResult(Integer.parseInt(bean.getParam5Result()));
		serverResp.setPoreStandard(bean.getParam6Standard());
		serverResp.setPoreResult(Integer.parseInt(bean.getParam6Result()));
		serverResp.setSkinAgeStandard(bean.getParam7Standard());
		serverResp.setSkinAgeResult(Integer.parseInt(bean.getParam7Result()));

		return serverResp;
	}
	
	
	//根据条件查询服务器上平均测试数据
	@Override
	public void queryAverageTestDataAsync(AverageDataReq bean, final BaseCall<MAverageResp> call) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((MAverageResp) msg.obj);
				}
			}
		};
		
		RadarProxy.getInstance(context).startServerData(writeAverageParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				MAverageResp mresp = new MAverageResp();
				Log.d("Radar", "queryAverageTestDataAsync " + result);
				try {
					jsonObject = new JSONObject(result);
					mresp.setSuccess(jsonObject.optBoolean("success"));
					mresp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject1 = new JSONObject(jsonObject.optString("message"));
					mresp.setStatus(jsonObject1.optString("status"));
					mresp.setMessage(jsonObject1.optString("msg"));
					
					Object temp = jsonObject1.opt("values");
					if (temp != null) {
						JSONArray jsonArr = new JSONArray(jsonObject1.optString("values"));

						for (int i = 0; i < jsonArr.length(); i++) {
							JSONObject jsonObject2 = (JSONObject) jsonArr.get(i);
							AverageData mAverageData = new AverageData();
							int analyzePart;
							
							mAverageData.setUid(jsonObject2.optString("uid"));
							mAverageData.setType(jsonObject2.optInt("type"));
							mAverageData.setAnalyzePart(jsonObject2.optInt("analyzePart"));
							analyzePart = jsonObject2.optInt("analyzePart");
							
							Object temp1 = jsonObject2.opt("infoList");
							if (temp1 != null) {
								JSONArray jsonArr1 = new JSONArray(jsonObject2.optString("infoList"));
								List<AverageResult> resList1 = new ArrayList<AverageResult>();

								for (int j = 0; j < jsonArr1.length(); j++) {
									JSONObject jsonObject3 = (JSONObject) jsonArr1.get(j);
									AverageResult mAverageResult = new AverageResult();
									
									mAverageResult.setAnalyzeTime(jsonObject3.optLong("analyzeTime"));
									mAverageResult.setWaterResult(jsonObject3.optInt("param1Result"));
									mAverageResult.setOilResult(jsonObject3.optInt("param2Result"));
									mAverageResult.setElasticResult(jsonObject3.optInt("param3Result"));
									mAverageResult.setSensitiveResult(jsonObject3.optInt("param4Result"));
									mAverageResult.setWhiteningResult(jsonObject3.optInt("param5Result"));
									mAverageResult.setPoreResult(jsonObject3.optInt("param6Result"));
									mAverageResult.setSkinAgeResult(jsonObject3.optInt("param7Result"));
									
									resList1.add(mAverageResult);
								}
								mAverageData.setValue(resList1);
							}
							
							switch(analyzePart) {
								case AnalyzeType.FOREHEAD:
									mresp.setForeheadValue(mAverageData);
									break;
								
								case AnalyzeType.CHEEK:
									mresp.setCheekValue(mAverageData);
									break;

								case AnalyzeType.EYE:
									mresp.setEyeValue(mAverageData);
									break;
							
								case AnalyzeType.NOSE:
									mresp.setNoseValue(mAverageData);
									break;
								
								case AnalyzeType.HAND:
									mresp.setHandValue(mAverageData);
									break;
								
								default:
									break;
							}
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					mresp.setStatus("FAILED");
					Log.d("Radar", "JSONException: " + e);
				}
				Message msg = Message.obtain();
				msg.obj = mresp;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				MAverageResp resp = new MAverageResp();
				resp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = resp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	
	
	//根据条件查询本地测试数据
	@Override
	public void queryLocalRecordsAsync(HistoricalReq bean, final BaseCall<MHistoricalResp> call) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((MHistoricalResp) msg.obj);
				}
			}
		};
		
		LocalTestDataReq localQuery = analyzeLocalQuery(bean);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.QUERY_HISTORY_RECORD, GsonUtil.getGson().toJson(localQuery), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				Log.d("Radar", "queryLocalRecordsAsync " + result);

				MHistoricalResp mresp = new MHistoricalResp();
				HistoricalResp resp = GsonUtil.getGson().fromJson(result, HistoricalResp.class);
				List<FacialAnalyzeResp> mrespList = new ArrayList<FacialAnalyzeResp>();
				for (int i = 0; i < resp.getValue().size(); i++) {
					FacialAnalyzeResp tmp = analyzeBeanToResp(resp.getValue().get(i));
					mrespList.add(tmp);
				}
				mresp.setValue(mrespList);
				
				Message msg = Message.obtain();
				msg.obj = mresp;
				handler.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				MHistoricalResp mresp = new MHistoricalResp();
				mresp.setStatus("FAILED");
				Message msg = Message.obtain();
				msg.obj = mresp;
				handler.sendMessage(msg);
				System.out.println(result);
			}
		});
	}
	

	//上传扫描出的未上传成功的数据
	private void uploadScanningDataServer(final HistoricalResp bean) {
		RadarProxy.getInstance(context).startServerData(writeParams2(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				Log.d("Radar", "uploadScanningDataServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));

					SaveReordReq saveRecords = new SaveReordReq();
					saveRecords.setValue(bean.getValue());
					if ("SUCCESS".equalsIgnoreCase(jsonObject2.optString("status"))) {
						saveRecords.setIfCloud(DailyTestSkin.UPLOADED_SERVER);
					} else {
						saveRecords.setIfCloud(DailyTestSkin.NOT_UPLOAD_SERVER);
					}
					RadarProxy.getInstance(context).startLocalData(HttpConstant.SKIN_TEST_UPDATE_STATE, GsonUtil.getGson().toJson(saveRecords), null);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("Radar", "JSONException: " + e);
				}
			}

			@Override
			public void onFailure(String result) {
				System.out.println(result);
			}
		});
	}

	
	private ServerRequestParams writeParams2(HistoricalResp bean) {
		ServerRequestParams serverRequestParams = new ServerRequestParams();
		serverRequestParams.setToken(HttpConstant.TOKEN);
		serverRequestParams.setRequestUrl(HttpConstant.getProductsTestUrl(null));
		serverRequestParams.setRequestParam(null);
		serverRequestParams.setRequestEntity(GsonUtil.getGson().toJson(bean.getValue()));
		return serverRequestParams;
	}
	
	private LocalTestDataReq analyzeLocalQuery(HistoricalReq bean) {	
		LocalTestDataReq localReq = new LocalTestDataReq();
		
		localReq.setType(Integer.toString(bean.getType()));
		localReq.setAnalyzePart(Integer.toString(bean.getAnalyzePart()));
		localReq.setStartTime(Long.toString(bean.getStartTime()));
		localReq.setEndTime(Long.toString(bean.getEndTime()));
		localReq.setUserId(SharePreCacheHelper.getUserID(context));
		return localReq;
	}
	
	private ServerRequestParams writeParams(HistoricalReq bean) {
		ServerRequestParams serverRequestParams = new ServerRequestParams();
		serverRequestParams.setRequestUrl(HttpConstant.getHistoricalRecordsUrl(null));
		Map<String, Object> requestParam = new HashMap<String, Object>();
		requestParam.put("token", HttpConstant.TOKEN);
		requestParam.put("type", Integer.toString(bean.getType()));
		requestParam.put("analyzePart", Integer.toString(bean.getAnalyzePart()));
		requestParam.put("startTime", Long.toString(bean.getStartTime()));
		requestParam.put("endTime", Long.toString(bean.getEndTime()));
		requestParam.put("pageTime", Long.toString(bean.getPageTime()));
		serverRequestParams.setRequestParam(requestParam);
		serverRequestParams.setRequestEntity(null);
		return serverRequestParams;
	}

	private ServerRequestParams writeAverageParams(AverageDataReq bean) {
		ServerRequestParams serverRequestParams = new ServerRequestParams();
		serverRequestParams.setRequestUrl(HttpConstant.queryChartData(null));
		Map<String, Object> requestParam = new HashMap<String, Object>();
		requestParam.put("token", HttpConstant.TOKEN);
		requestParam.put("type", Integer.toString(bean.getType()));
		requestParam.put("queryType", Integer.toString(bean.getQueryType()));
		requestParam.put("startTime", Long.toString(bean.getStartTime()));
		requestParam.put("endTime", Long.toString(bean.getEndTime()));
		serverRequestParams.setRequestParam(requestParam);
		serverRequestParams.setRequestEntity(null);
		return serverRequestParams;
	}
}
