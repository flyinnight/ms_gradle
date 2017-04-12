package com.dilapp.radar.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.AnalyzeDaoImpl;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.HistoricalRecords.HistoricalResp;
import com.dilapp.radar.domain.HistoricalRecords.LocalTestDataReq;
import com.dilapp.radar.domain.ProductsTestSkin.SaveReordReq;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.util.GsonUtil;


public class AnalyzeHelper {

	//更新测试数据是否已经上传至服务器状态״̬
	public static String updateRecordState(String beanString) {
		SaveReordReq saveRecord = GsonUtil.getGson().fromJson(beanString, SaveReordReq.class);
		List<FacialAnalyzeBean> beanList = saveRecord.getValue();
		
		try {
			for (FacialAnalyzeBean bean : beanList) {
				AnalyzeDaoImpl dbUtil = new AnalyzeDaoImpl(RadarApplication.getInstance());
				dbUtil.updateRecordState(bean.getRid(), saveRecord.getIfCloud());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		BaseResp base = new BaseResp();
		base.setStatus("SUCCESS");

		return GsonUtil.getGson().toJson(base);
	}

	public static String saveAnalyzeForList(String beanString) {
		SaveReordReq saveRecord = GsonUtil.getGson().fromJson(beanString, SaveReordReq.class);
		List<FacialAnalyzeBean> beanList = saveRecord.getValue();

		try {
			for (FacialAnalyzeBean bean : beanList) {
				AnalyzeDaoImpl dbUtil = new AnalyzeDaoImpl(RadarApplication.getInstance());
				dbUtil.saveAnalyze(bean, saveRecord.getIfCloud());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		BaseResp base = new BaseResp();
		base.setStatus("SUCCESS");

		return GsonUtil.getGson().toJson(base);
	}

	public static String getLastType(String content) {
		AnalyzeDaoImpl dbUtil = null;
		Cursor cur = null;
		List<FacialAnalyzeBean> recordLists = new ArrayList<FacialAnalyzeBean>();
		try {
			LocalTestDataReq localBean = GsonUtil.getGson().fromJson(content, LocalTestDataReq.class);
			dbUtil = new AnalyzeDaoImpl(RadarApplication.getInstance());
			cur = dbUtil.fetchLastIdByType(localBean.getType(), localBean.getAnalyzePart(), localBean.getUserId());
			if (cur.moveToFirst()) {
				FacialAnalyzeBean record = analyzeBean(cur);
				if (record != null) {
					recordLists.add(record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cur.close();
			dbUtil.mDbclose();
		}

		HistoricalResp res = new HistoricalResp();
		res.setValue(recordLists);
		res.setStatus("SUCCESS");
		return GsonUtil.getGson().toJson(res);
	}
	
	public static String queryHistoryRecord(String content) {
		AnalyzeDaoImpl dbUtil = null;
		Cursor cur = null;
		List<FacialAnalyzeBean> recordLists = new ArrayList<FacialAnalyzeBean>();
		try {
			LocalTestDataReq localBean = GsonUtil.getGson().fromJson(content, LocalTestDataReq.class);
			dbUtil = new AnalyzeDaoImpl(RadarApplication.getInstance());
			cur = dbUtil.fetchAnalyzeDataByTypePart(localBean.getType(), localBean.getAnalyzePart(), localBean.getUserId(), localBean.getStartTime(), localBean.getEndTime());
			
			if (cur.moveToFirst()) {
				FacialAnalyzeBean record = analyzeBean(cur);
				if (record != null) {
					recordLists.add(record);
				}
				while (cur.moveToNext()) {
					record = analyzeBean(cur);
					if (record != null) {
						recordLists.add(record);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cur.close();
			dbUtil.mDbclose();
		}

		HistoricalResp res = new HistoricalResp();
		res.setValue(recordLists);
		res.setStatus("SUCCESS");
		return GsonUtil.getGson().toJson(res);
	}

	public static String fetchAllStupidAnalyzeData() {
		AnalyzeDaoImpl dbUtil = new AnalyzeDaoImpl(RadarApplication.getInstance());
		Cursor cur = dbUtil.fetchAllStupidAnalyzeData(DailyTestSkin.NOT_UPLOAD_SERVER);
		List<FacialAnalyzeBean> recordLists = new ArrayList<FacialAnalyzeBean>();
		
		if (cur.moveToFirst()) {
			FacialAnalyzeBean record = analyzeBean(cur);
			if (record != null) {
				recordLists.add(record);
			}
			while (cur.moveToNext()) {
				record = analyzeBean(cur);
				if (record != null) {
					recordLists.add(record);
				}
			}
		}
		cur.close();
		dbUtil.mDbclose();
		
		HistoricalResp res = new HistoricalResp();
		res.setValue(recordLists);
		res.setStatus("SUCCESS");
		return GsonUtil.getGson().toJson(res);
	}

	private static FacialAnalyzeBean analyzeBean(Cursor cur) {
		FacialAnalyzeBean bean = new FacialAnalyzeBean();
		
		bean.setUid(cur.getString(cur.getColumnIndex("userId")));
		bean.setRid(cur.getString(cur.getColumnIndex("recordId")));
		bean.setType(Integer.parseInt(cur.getString(cur.getColumnIndex("type"))));
		bean.setSubtype(Integer.parseInt(cur.getString(cur.getColumnIndex("subtype"))));
		
		bean.setAnalyzePart(Integer.parseInt(cur.getString(cur.getColumnIndex("analyzePart"))));
		bean.setAnalyzeTime(Long.parseLong(cur.getString(cur.getColumnIndex("analyzeTime"))));
		bean.setAnalyzePlace(cur.getString(cur.getColumnIndex("analyzePlace")));
		bean.setAnalyzeClimate(cur.getString(cur.getColumnIndex("analyzeClimate")));
		
		bean.setParam1Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param1Value"))));
		bean.setParam1Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param1Standard"))));
		bean.setParam1Result(cur.getString(cur.getColumnIndex("param1Result")));
		
		bean.setParam2Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param2Value"))));
		bean.setParam2Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param2Standard"))));
		bean.setParam2Result(cur.getString(cur.getColumnIndex("param2Result")));

		bean.setParam3Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param3Value"))));
		bean.setParam3Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param3Standard"))));
		bean.setParam3Result(cur.getString(cur.getColumnIndex("param3Result")));

		bean.setParam4Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param4Value"))));
		bean.setParam4Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param4Standard"))));
		bean.setParam4Result(cur.getString(cur.getColumnIndex("param4Result")));

		bean.setParam5Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param5Value"))));
		bean.setParam5Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param5Standard"))));
		bean.setParam5Result(cur.getString(cur.getColumnIndex("param5Result")));

		bean.setParam6Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param6Value"))));
		bean.setParam6Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param6Standard"))));
		bean.setParam6Result(cur.getString(cur.getColumnIndex("param6Result")));

		bean.setParam7Value(Integer.parseInt(cur.getString(cur.getColumnIndex("param7Value"))));
		bean.setParam7Standard(Integer.parseInt(cur.getString(cur.getColumnIndex("param7Standard"))));
		bean.setParam7Result(cur.getString(cur.getColumnIndex("param7Result")));
		
		bean.setCosmeticID(cur.getString(cur.getColumnIndex("cosmetic_id")));
		bean.setSchemaID(cur.getString(cur.getColumnIndex("schema_id")));
		bean.setLabelID(cur.getString(cur.getColumnIndex("label_id")));
		return bean;
	}

}
