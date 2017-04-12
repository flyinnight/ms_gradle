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
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.HistoricalRecords.FacialAnalyzeResp;
import com.dilapp.radar.domain.HistoricalRecords.HistoricalReq;
import com.dilapp.radar.domain.HistoricalRecords.HistoricalResp;
import com.dilapp.radar.domain.HistoricalRecords.LocalTestDataReq;
import com.dilapp.radar.domain.HistoricalRecords.MHistoricalResp;
import com.dilapp.radar.domain.ProductsTestSkin;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.server.ClientCallbackImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class ProductsTestSkinImpl extends ProductsTestSkin {
	private Handler handler1;
	private Handler handler3;
	private Context context;

	public ProductsTestSkinImpl(Context context) {
		this.context = context;
	}

	@Override
	public void productsTestSkinAsync(ProductsTestSkinReq bean, final BaseCall<BaseResp> call) {
		handler1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((BaseResp) msg.obj);
				}
			}
		};

		uploadServer(bean);
	}

	private void uploadServer(final ProductsTestSkinReq bean) {
		RadarProxy.getInstance(context).startServerData(writeParams(bean), new ClientCallbackImpl() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject;
				BaseResp resp = new BaseResp();
				Log.d("Radar", "productsTestSkinAsync: uploadServer: " + result);
				try {
					jsonObject = new JSONObject(result);
					resp.setSuccess(jsonObject.optBoolean("success"));
					resp.setStatusCode(jsonObject.optInt("statusCode"));
					JSONObject jsonObject2 = new JSONObject(jsonObject.optString("message"));
					resp.setMessage(jsonObject2.optString("msg"));
					resp.setStatus(jsonObject2.optString("status"));
					
					SaveReordReq saveRecords = analyzeSaveData(bean);
					if ("SUCCESS".equalsIgnoreCase(jsonObject2.optString("status"))) {
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
				handler1.sendMessage(msg);
			}

			@Override
			public void onFailure(String result) {
				BaseResp resp = new BaseResp();
				resp.setStatus("FAILED");
				SaveReordReq saveRecords = analyzeSaveData(bean);
				saveRecords.setIfCloud(DailyTestSkin.NOT_UPLOAD_SERVER);
				RadarProxy.getInstance(context).startLocalData(HttpConstant.PRODUCT_TEST_WIRTE_LISTS, GsonUtil.getGson().toJson(saveRecords), null);
				
				Message msg = Message.obtain();
				msg.obj = resp;
				handler1.sendMessage(msg);
				System.out.println(result);
			}
		});
	}

	private ServerRequestParams writeParams(ProductsTestSkinReq bean) {
		ServerRequestParams serverRequestParams = new ServerRequestParams();
		serverRequestParams.setToken(HttpConstant.TOKEN);
		serverRequestParams.setRequestUrl(HttpConstant.getProductsTestUrl(null));
		serverRequestParams.setRequestParam(null);
		serverRequestParams.setRequestEntity(analyzeBeanToJson(bean));
		return serverRequestParams;
	}

	private String analyzeBeanToJson(ProductsTestSkinReq bean) {
		List<FacialAnalyzeBean> list = new ArrayList<FacialAnalyzeBean>();
		FacialAnalyzeBean analyBeanBefore = writeBean(bean.getBefore());
		list.add(analyBeanBefore);
		FacialAnalyzeBean analyBeanAfter = writeBean(bean.getAfter());
		list.add(analyBeanAfter);
		String analyJson = GsonUtil.getGson().toJson(list);
		return analyJson;
	}

	private FacialAnalyzeBean writeBean(TestSkinReq req) {
		FacialAnalyzeBean serverBean = new FacialAnalyzeBean();
		serverBean.setParam1Value(1);
		serverBean.setParam1Result(String.valueOf(req.getWater()));
		serverBean.setParam2Value(2);
		serverBean.setParam2Result(String.valueOf(req.getOil()));
		serverBean.setParam3Value(3);
		serverBean.setParam3Result(String.valueOf(req.getEastic()));
		serverBean.setParam4Value(4);
		serverBean.setParam4Result(String.valueOf(req.getSensitive()));
		serverBean.setParam5Value(5);
		serverBean.setParam5Result(String.valueOf(req.getWhitening()));
		serverBean.setParam6Value(6);
		serverBean.setParam6Result(String.valueOf(req.getPore()));
		serverBean.setParam7Value(7);
		serverBean.setParam7Result(String.valueOf(req.getSkinAge()));
		
		serverBean.setRid(req.getRid());
		serverBean.setUid(req.getUid());
		serverBean.setType(req.getType());
		serverBean.setAnalyzePart(req.getPart());
		serverBean.setAnalyzeTime(req.getAnalyzeTime());
		serverBean.setSubtype(req.getSubtype());
		serverBean.setAnalyzePlace(req.getAnalyzePlace());
		serverBean.setAnalyzeClimate(req.getAnalyzeClimate());
		serverBean.setCosmeticID(req.getCosmeticID());
		serverBean.setSchemaID(req.getSchemaID());
		serverBean.setLabelID(req.getLabelID());
		return serverBean;

	}

	private SaveReordReq analyzeSaveData(ProductsTestSkinReq bean) {
		SaveReordReq saveRecords = new SaveReordReq();
		List<FacialAnalyzeBean> list = new ArrayList<FacialAnalyzeBean>();
		FacialAnalyzeBean analyBeanBefore = writeBean(bean.getBefore());
		list.add(analyBeanBefore);
		FacialAnalyzeBean analyBeanAfter = writeBean(bean.getAfter());
		list.add(analyBeanAfter);
		
		saveRecords.setValue(list);;
		
		return saveRecords;
	}
	

	//本地查找护肤品上条数据
	@Override
	public void getLastProductsTestSkin(HistoricalReq bean, final BaseCall<MHistoricalResp> call) {
		handler3 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (call != null && !call.cancel) {
					call.call((MHistoricalResp) msg.obj);
				}
			}
		};

		LocalTestDataReq localQuery = analyzeLocalQuery(bean);
		RadarProxy.getInstance(context).startLocalData(HttpConstant.PRODUCT_GET_LAST, GsonUtil.getGson().toJson(localQuery),
				new ClientCallbackImpl() {
					@Override
					public void onSuccess(String result) {
						Log.d("Radar", "getLastProductsTestSkin" + result);
						HistoricalResp resp = GsonUtil.getGson().fromJson(result, HistoricalResp.class);
						MHistoricalResp mresp = new MHistoricalResp();
						List<FacialAnalyzeResp> mrespList = new ArrayList<FacialAnalyzeResp>();
						for (int i = 0; i < resp.getValue().size(); i++) {
							FacialAnalyzeResp tmp = analyzeBeanToResp(resp.getValue().get(i));
							mrespList.add(tmp);
						}
						mresp.setValue(mrespList);
						
						Message msg = Message.obtain();
						msg.obj = mresp;
						handler3.sendMessage(msg);
					}

					@Override
					public void onFailure(String result) {
						MHistoricalResp resp = new MHistoricalResp();
						resp.setStatus("FAILED");
						Message msg = Message.obtain();
						msg.obj = resp;
						handler3.sendMessage(msg);
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

	private LocalTestDataReq analyzeLocalQuery(HistoricalReq bean) {	
		LocalTestDataReq localReq = new LocalTestDataReq();
		
		localReq.setType(Integer.toString(bean.getType()));
		localReq.setAnalyzePart(Integer.toString(bean.getAnalyzePart()));
		//localReq.setStartTime(Long.toString(bean.getStartTime()));
		//localReq.setEndTime(Long.toString(bean.getEndTime()));
		localReq.setUserId(SharePreCacheHelper.getUserID(context));
		return localReq;
	}

}

