package com.dilapp.radar.db.dao;

import android.database.Cursor;


public interface SolutionListDao {
	//存储某种类型的护肤方案列表
	public long saveSolutionListByType(int solutionType, long updateTime, String solutionBean);
	//更新某种类型的护肤方案列表
	public long updateSolutionListByType(int solutionType, long updateTime, String solutionBean);
	//获取某种类型的护肤方案列表
	public Cursor getSolutionListByType(int solutionType);
	//删除某种类型的护肤方案列表
	public long deleteSolutionListByType(int solutionType);
	//删除所有护肤方案列表
	public long deleteAllSolutionList();
	
}
