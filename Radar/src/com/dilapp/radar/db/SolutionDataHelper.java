package com.dilapp.radar.db;


import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.dilapp.radar.application.RadarApplication;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.db.dao.impl.SolutionDataDaoImpl;
import com.dilapp.radar.domain.SolutionCreateUpdate.SolutionCreateReq;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionListData.MSolutionListResp;
import com.dilapp.radar.domain.SolutionListData.SolutionDataGetDelete;
import com.dilapp.radar.domain.SolutionListData.SolutionDataSave;
import com.dilapp.radar.util.GsonUtil;


public class SolutionDataHelper {

	// 存储某种类型的护肤方案data
	public static String saveSolutionData(String beanString) {
		SolutionDataSave bean = GsonUtil.getGson().fromJson(beanString, SolutionDataSave.class);

		int type = bean.getType();
		long solutionId = bean.getSolutionId();
		long localSolutionId = bean.getLocalSolutionId();
		int sendState = bean.getSendState();
		long updateTime = bean.getUpdateTime();
		String tag = bean.getTag();
		if (tag == null) {
			tag = "";
		}
		long repId = 0;
		
		if ((type == SolutionListData.SOLUTION_DETAIL_DATA) || (type == SolutionListData.SOLUTION_LIST_COMMENT)) {
			//先删除本地数据，避免重复存储，同一类型的data只保存一种
			SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
			dbUtil.deleteSolutionDataItemByType(type, solutionId, localSolutionId, tag);
			
			long solutionIdFirst = 0;
			int solutionSize = 0;
			SolutionDataDaoImpl dbUtil1 = new SolutionDataDaoImpl(RadarApplication.getInstance());
			Cursor curDetail = dbUtil1.getSolutionDataItemByType(type, solutionId, localSolutionId, tag);
			if ((curDetail != null) && (curDetail.moveToFirst())) {
				solutionIdFirst = curDetail.getLong(curDetail.getColumnIndex("solutionId"));
				solutionSize++;
				while (curDetail.moveToNext()) {
					solutionSize++;
				}
				
				curDetail.close();
			}
			dbUtil1.mDbclose();
			
			if (solutionSize >= 100) {
				SolutionDataDaoImpl dbUtil2 = new SolutionDataDaoImpl(RadarApplication.getInstance());
				dbUtil2.deleteSolutionDataItemByType(type, solutionIdFirst, localSolutionId, tag);
			}

			try {
				SolutionDataDaoImpl dbUtil3 = new SolutionDataDaoImpl(RadarApplication.getInstance());
				repId = dbUtil3.saveSolutionDataByType(type, solutionId, localSolutionId, sendState, updateTime, tag, beanString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else if (type == SolutionListData.SOLUTION_LIST_TYPE){
			//先删除本地数据，避免重复存储，同一类型的data只保存一种
			SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
			dbUtil.deleteSolutionDataItemByType(type, solutionId, localSolutionId, tag);
			//删除护肤方案发送成功缓存的网络返回数据
			SolutionDataDaoImpl dbUtil1 = new SolutionDataDaoImpl(RadarApplication.getInstance());
			dbUtil1.deleteSolutionDataItemByType(SolutionListData.SOLUTION_SENDING_DATA, 0, 0, tag);
					
			try {
				SolutionDataDaoImpl dbUtil2 = new SolutionDataDaoImpl(RadarApplication.getInstance());
				repId = dbUtil2.saveSolutionDataByType(type, solutionId, localSolutionId, sendState, updateTime, tag, beanString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			//先删除本地数据，避免重复存储，同一类型的data只保存一种
			SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
			dbUtil.deleteSolutionDataItemByType(type, solutionId, localSolutionId, tag);
					
			try {
				SolutionDataDaoImpl dbUtil1 = new SolutionDataDaoImpl(RadarApplication.getInstance());
				repId = dbUtil1.saveSolutionDataByType(type, solutionId, localSolutionId, sendState, updateTime, tag, beanString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		BaseResp res = new BaseResp();
		res.setStatus("SUCCESS");
		return GsonUtil.getGson().toJson(res);
	}
	
	//更新某条护肤方案发送状态
	public static long updateSolutionStateItemByType(String beanString) {
		SolutionDataSave bean = GsonUtil.getGson().fromJson(beanString, SolutionDataSave.class);
		
		int type = bean.getType();
		long localSolutionId = bean.getLocalSolutionId();
		int sendState = bean.getSendState();
		long repId = 0;
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		try {
			repId = dbUtil.updateSolutionStateItemByType(type, localSolutionId, sendState);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	//更新某种类型的护肤方案发送状态
	public static long updateSolutionStateByType(String beanString) {
		SolutionDataSave bean = GsonUtil.getGson().fromJson(beanString, SolutionDataSave.class);
		
		int type = bean.getType();
		int sendState = bean.getSendState();
		long repId = 0;
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		try {
			repId = dbUtil.updateSolutionStateByType(type, sendState);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	//(图片发送成功)更新某条护肤方案内容
	public static long updateSolutionDataItemByType(String beanString) {
		SolutionDataSave bean = GsonUtil.getGson().fromJson(beanString, SolutionDataSave.class);
		
		int type = bean.getType();
		long localSolutionId = bean.getLocalSolutionId();
		long repId = 0;
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		try {
			repId = dbUtil.updateSolutionDataItemByType(type, localSolutionId, beanString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}

	//更新某种类型的护肤方案的Timestamp
	public static long updateSolutionTimestampByType(String beanString) {
		SolutionDataSave bean = GsonUtil.getGson().fromJson(beanString, SolutionDataSave.class);
		
		int type = bean.getType();
		long updateTime = bean.getUpdateTime();
		long repId = 0;
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		try {
			repId = dbUtil.updateSolutionTimestampByType(type, updateTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return repId;
	}
	
	// 获取某种类型的护肤方案data
	public static String getSolutionData(String beanString) {
		SolutionDataGetDelete bean = GsonUtil.getGson().fromJson(beanString, SolutionDataGetDelete.class);
		int type = bean.getType();
		int pageNum = bean.getPageNum();
		long solutionId = bean.getSolutionId();
		long localSolutionId = bean.getLocalSolutionId();
		String tag = bean.getTag();
		if (tag == null) {
			tag = "";
		}
		SolutionDataSave beanSave = null;
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		Cursor curSolution = dbUtil.getSolutionDataItemByType(type, solutionId, localSolutionId, tag);
		if ((curSolution != null) && (curSolution.moveToFirst())) {
			beanSave = analyzeBeanSolution(type, solutionId, curSolution);
			curSolution.close();
		}
		dbUtil.mDbclose();
		
		if ((type == SolutionListData.SOLUTION_LIST_TYPE) && (pageNum == 1) && (tag.equals(""))) {
			List<MSolutionResp> listData = new ArrayList<MSolutionResp>();
			
			SolutionDataDaoImpl dbUtil1 = new SolutionDataDaoImpl(RadarApplication.getInstance());
			Cursor curSending = dbUtil1.getSolutionDataByType(SolutionListData.SOLUTION_SENDING_DATA);
			if ((curSending != null) && (curSending.moveToFirst())) {
				MSolutionResp solutionItem = analyzeBeanGetLocal(curSending);
				if (solutionItem != null) {
					listData.add(solutionItem);
				}
				while (curSending.moveToNext()) {
					solutionItem = analyzeBeanGetLocal(curSending);
					if (solutionItem != null) {
						listData.add(solutionItem);
					}
				}
				curSending.close();
			}
			dbUtil1.mDbclose();
			
			if (beanSave == null) {
				beanSave = new SolutionDataSave();
				MSolutionListResp solutionList = new MSolutionListResp();
				solutionList.setDatas(listData);
				solutionList.setPageNo(1);
				solutionList.setTotalPage(1);
				solutionList.setSuccess(true);//true,false
				solutionList.setStatusCode(BaseResp.OK);
				solutionList.setMessage("ok");// ok
				solutionList.setStatus("SUCCESS");// SUCCESS
				beanSave.setSolutionList(solutionList);
			} else {
				MSolutionListResp solutionList1 = beanSave.getSolutionList();
				if (solutionList1 != null) {
					List<MSolutionResp> listData1 = solutionList1.getDatas();
					if ((listData1 != null) && (listData1.size() != 0)) {
						for (int i = 0; i < listData1.size(); i++) {
							listData.add(listData1.get(i));
						}
					}
				}
				MSolutionListResp solutionList = beanSave.getSolutionList();
				solutionList.setDatas(listData);
				beanSave.setSolutionList(solutionList);
			}
			
		} else {
			if (beanSave == null) {
				beanSave = new SolutionDataSave();
			}
		}
		
		return GsonUtil.getGson().toJson(beanSave);
	}
	
	// 删除某条护肤方案数据
	public static long deleteSolutionDataItem(String beanString) {
		SolutionDataGetDelete bean = GsonUtil.getGson().fromJson(beanString, SolutionDataGetDelete.class);
		int type = bean.getType();
		long solutionId = bean.getSolutionId();
		long localSolutionId = bean.getLocalSolutionId();
		String tag = bean.getTag();
		if (tag == null) {
			tag = "";
		}
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteSolutionDataItemByType(type, solutionId, localSolutionId, tag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	// 删除某种类型的护肤方案数据
	public static long deleteSolutionData(String beanString) {
		SolutionDataGetDelete bean = GsonUtil.getGson().fromJson(beanString, SolutionDataGetDelete.class);
		int type = bean.getType();
		
		SolutionDataDaoImpl dbUtil = new SolutionDataDaoImpl(RadarApplication.getInstance());
		long repId = 0;
		
		try {
			repId = dbUtil.deleteSolutionDataByType(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repId;
	}
	
	public static SolutionDataSave analyzeBeanSolution(int type, long solutionId, Cursor cur) {
		int typeVerify = cur.getInt(cur.getColumnIndex("solutionType"));
		long idVerify = cur.getLong(cur.getColumnIndex("solutionId"));
		String solutionJson = cur.getString(cur.getColumnIndex("solutionItemJson"));
		long updateTime = cur.getLong(cur.getColumnIndex("updateTime"));

		//判断类型Id是否一致
		if((type == typeVerify) && (idVerify == solutionId)) {
			SolutionDataSave bean = GsonUtil.getGson().fromJson(solutionJson, SolutionDataSave.class);
			bean.setUpdateTime(updateTime);
			return bean;
		}
		
		return null;
	}
	
	public static MSolutionResp analyzeBeanGetLocal(Cursor cur) {
		MSolutionResp resp = null;
		
		long localSolutionId = cur.getLong(cur.getColumnIndex("localSolutionId"));
		String postItemJson = cur.getString(cur.getColumnIndex("solutionItemJson"));

		//如果localSolutionId不等于0,需转换
		if(localSolutionId != 0) {
			resp = new MSolutionResp();
			SolutionDataSave saveBean = GsonUtil.getGson().fromJson(postItemJson, SolutionDataSave.class);
			SolutionCreateReq sendBean = saveBean.getSendingData();
			
			resp.setLocalSolutionId(localSolutionId);
			resp.setSendState(cur.getInt(cur.getColumnIndex("sendState")));
			resp.setEffect(sendBean.getEffect());
			resp.setPart(sendBean.getPart());
			resp.setTitle(sendBean.getTitle());
			resp.setIntroduction(sendBean.getIntroduction());
			resp.setContent(sendBean.getContent());
			resp.setCoverImgUrl(sendBean.getCoverUrl());
			resp.setUseCycle(sendBean.getUseCycle());
			resp.setCreateTime(cur.getLong(cur.getColumnIndex("updateTime")));
			resp.setUpdateTime(cur.getLong(cur.getColumnIndex("updateTime")));
			resp.setUserId(SharePreCacheHelper.getUserID(RadarApplication.getInstance()));
			resp.setNickName(SharePreCacheHelper.getNickName(RadarApplication.getInstance()));
			resp.setPortrait(SharePreCacheHelper.getUserIconUrl(RadarApplication.getInstance()));
		} else { //护肤方案发送成功，存储的网络返回数据
			SolutionDataSave saveBean = GsonUtil.getGson().fromJson(postItemJson, SolutionDataSave.class);
			resp = saveBean.getSolutionDetail();
		}
		
		return resp;
	}
	
}
