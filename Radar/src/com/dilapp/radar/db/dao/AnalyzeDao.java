package com.dilapp.radar.db.dao;

import android.database.Cursor;

import com.dilapp.radar.domain.server.FacialAnalyzeBean;

public interface AnalyzeDao {

	public long saveAnalyze(FacialAnalyzeBean bean, int state);

	public long updateRecordState(String recordId, int state);

	public boolean deleteAnalyzeById(String recordId);

	public Cursor fetchAllAnalyzeData();

	public Cursor fetchAnalyzeDataById(String recordId);

	public Cursor fetchLastIdByType(String type, String part, String uid);
	
	public Cursor fetchAnalyzeDataByTypePart(String type, String part, String uid, String startTime, String endTime);

	public boolean updateAnalyzeDataById(FacialAnalyzeBean bean);

	public Cursor fetchAllStupidAnalyzeData(int state);

}
