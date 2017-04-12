package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface SolutionDataDao {
	//存储某种类型的护肤方案数据
	public long saveSolutionDataByType(int solutionType, long solutionId, long localSolutionId, int sendState, long updateTime, String tag, String solutionBean);
	//更新某条护肤方案发送状态
	public long updateSolutionStateItemByType(int solutionType, long localSolutionId, int sendState);
	//更新某种类型的护肤方案发送状态
	public long updateSolutionStateByType(int solutionType, int sendState);
	//(图片发送成功)更新某条护肤方案内容
	public long updateSolutionDataItemByType(int solutionType, long localSolutionId, String solutionBean);
	//更新某种类型的护肤方案的Timestamp
	public long updateSolutionTimestampByType(int solutionType, long updateTime);
	//获取某条护肤方案数据
	public Cursor getSolutionDataItemByType(int solutionType, long solutionId, long localSolutionId, String tag);
	//获取某种类型的护肤方案数据
	public Cursor getSolutionDataByType(int solutionType);
	//删除某条护肤方案数据
	public long deleteSolutionDataItemByType(int solutionType, long solutionId, long localSolutionId, String tag);
	//删除某种类型的护肤方案数据
	public long deleteSolutionDataByType(int solutionType);
	//删除所有护肤方案数据
	public long deleteAllSolutionData();
	
}
