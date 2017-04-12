package com.dilapp.radar.db;


import android.database.Cursor;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.db.dao.impl.SolutionListDaoImpl;
import com.dilapp.radar.domain.SolutionList.SolutionListSave;
import com.dilapp.radar.util.GsonUtil;


public class SolutionListHelper {

	// 存储某种类型的护肤方案列表
	public static long saveSolutionList(String beanString) {
		SolutionListSave bean = GsonUtil.getGson().fromJson(beanString, SolutionListSave.class);
		long repId = 0;
		
		//先删除本地数据，避免重复存储，同一类型的list只保存一种
		SolutionListDaoImpl dbUtil = new SolutionListDaoImpl(RadarApplication.getInstance());
		dbUtil.deleteSolutionListByType(bean.getType());
				
		try {
			SolutionListDaoImpl dbUtil1 = new SolutionListDaoImpl(RadarApplication.getInstance());
			repId = dbUtil1.saveSolutionListByType(bean.getType(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 更新某种类型的护肤方案列表
	public static long updateSolutionList(String beanString) {
		SolutionListSave bean = GsonUtil.getGson().fromJson(beanString, SolutionListSave.class);
		SolutionListDaoImpl dbUtil = new SolutionListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.updateSolutionListByType(bean.getType(), bean.getUpdateTime(), beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	// 获取某种类型的护肤方案列表
	public static String getSolutionList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		SolutionListDaoImpl dbUtil = new SolutionListDaoImpl(RadarApplication.getInstance());
		
		SolutionListSave beanSave = null;

		Cursor curSolution = dbUtil.getSolutionListByType(type);
		if ((curSolution != null) && (curSolution.moveToFirst())) {
			beanSave = analyzeBeanSolution(type, curSolution);
			curSolution.close();
		}
		dbUtil.mDbclose();
		
		if (beanSave == null) {
			beanSave = new SolutionListSave();
		}

		return GsonUtil.getGson().toJson(beanSave);
	}

	// 删除某种类型的护肤方案列表
	public static long deleteSolutionList(String beanString) {
		Integer type = Integer.parseInt(beanString);
		SolutionListDaoImpl dbUtil = new SolutionListDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteSolutionListByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	public static SolutionListSave analyzeBeanSolution(int type, Cursor cur) {
		int typeVerify = cur.getInt(cur.getColumnIndex("solutionType"));
		String solutionJson = cur.getString(cur.getColumnIndex("solutionItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		//判断类型是否一致
		if(type == typeVerify) {
			SolutionListSave bean = GsonUtil.getGson().fromJson(solutionJson, SolutionListSave.class);
			bean.setUpdateTime(updateTime);
			return bean;
		}
		
		return null;
	}
	
}
