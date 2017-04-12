package com.dilapp.radar.domain.impl;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.SolutionCreateUpdate;
import com.dilapp.radar.domain.SolutionListData;
import com.dilapp.radar.domain.SolutionDetailData.MSolutionResp;
import com.dilapp.radar.domain.SolutionListData.SolutionDataGetDelete;
import com.dilapp.radar.domain.SolutionListData.SolutionDataSave;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.server.ServerRequestParams;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;

public class SolutionCreateUpdateImpl extends SolutionCreateUpdate {
	private Handler handler1;
	private Handler handler2;
	private Handler handler3;
	private Handler handler4;
	private Context context;
	private ServerRequestParams params;

	private final int SAVEDATA_SENDSOLUTION = 221;
	private final int DELETE_LOCALSOLUTION = 222;
	private final int DELETE_ALLSENDINGSOLUTION = 223;
	
	public SolutionCreateUpdateImpl(Context context) {
		this.context = context;
	}

	// 上传护肤方案封面图片
	@Override
	public void solutionUplCoverImgAsync(String imgs, final BaseCall<CoverImgResp> call) {}

	// 上传护肤方案正文图片
	@Override
	public void solutionUplTextImgAsync(List<String> imgs, final BaseCall<TextImgResp> call) {}

	// 创建护肤方案
	@Override
	public void solutionCreateAsync(SolutionCreateReq bean, final BaseCall<MSolutionResp> call) {
		handler1 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SAVEDATA_SENDSOLUTION:
					SolutionCreateReq bean= (SolutionCreateReq)msg.obj;
					boolean saveSql = (bean.getLocalSolutionId() == 0);
					SolutionCreateReq sendBean = analyzeBeanSave(bean);
					
					SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setSendingData(sendBean);
					saveBean.setLocalSolutionId(sendBean.getLocalSolutionId());
					saveBean.setSendState(SOLUTION_RELEASE_SENDING);
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setUpdateTime(System.currentTimeMillis());
					
					if (saveSql) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					}
					RadarProxy.getInstance(context).solutionReleaseBackground(sendBean, 1);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = SAVEDATA_SENDSOLUTION;
		msg.obj = bean;
		handler1.sendMessage(msg);
		
		MSolutionResp resp = new MSolutionResp();
		resp.setSendState(SOLUTION_RELEASE_SENDING);
		resp.setEffect(bean.getEffect());
		resp.setPart(bean.getPart());
		resp.setTitle(bean.getTitle());
		resp.setIntroduction(bean.getIntroduction());
		resp.setContent(bean.getContent());
		resp.setCoverImgUrl(bean.getCoverUrl());
		resp.setUseCycle(bean.getUseCycle());
		
		resp.setSuccess(true);//true,false
		resp.setStatusCode(BaseResp.OK);
		resp.setMessage("ok");// ok
		resp.setStatus("SUCCESS");// SUCCESS
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}

	// 修改护肤方案
	@Override
	public void solutionUpdateAsync(SolutionUpdateReq bean, final BaseCall<MSolutionResp> call) {
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case SAVEDATA_SENDSOLUTION:
					SolutionUpdateReq bean= (SolutionUpdateReq)msg.obj;
					boolean saveSql = (bean.getLocalSolutionId() == 0);
					SolutionUpdateReq sendBean = analyzeBeanSave2(bean);
					
					SolutionDataSave saveBean = new SolutionDataSave();
					saveBean.setSendingData(sendBean);
					saveBean.setLocalSolutionId(sendBean.getLocalSolutionId());
					saveBean.setSendState(SOLUTION_RELEASE_SENDING);
					saveBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					saveBean.setUpdateTime(System.currentTimeMillis());
					
					if (saveSql) {
						RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_SAVE_ONE_BYTYPE, GsonUtil.getGson().toJson(saveBean), null);
					}
					RadarProxy.getInstance(context).solutionReleaseBackground(sendBean, 2);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = SAVEDATA_SENDSOLUTION;
		msg.obj = bean;
		handler2.sendMessage(msg);
		
		MSolutionResp resp = new MSolutionResp();
		resp.setSendState(SOLUTION_RELEASE_SENDING);
		resp.setEffect(bean.getEffect());
		resp.setPart(bean.getPart());
		resp.setTitle(bean.getTitle());
		resp.setIntroduction(bean.getIntroduction());
		resp.setContent(bean.getContent());
		resp.setCoverImgUrl(bean.getCoverUrl());
		resp.setUseCycle(bean.getUseCycle());
		
		resp.setSuccess(true);//true,false
		resp.setStatusCode(BaseResp.OK);
		resp.setMessage("ok");// ok
		resp.setStatus("SUCCESS");// SUCCESS
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	}

	// 删除未发布成功/未更新成功的护肤方案
	public void solutionDeleteLocalItemAsync(final long localSolutionId, BaseCall<BaseResp> call){
		handler3 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DELETE_LOCALSOLUTION:
					SolutionDataGetDelete delBean = new SolutionDataGetDelete();
					delBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					delBean.setLocalSolutionId(localSolutionId);
					RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_ONE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = DELETE_LOCALSOLUTION;
		handler3.sendMessage(msg);
		
		BaseResp resp = new BaseResp();
		resp.setSuccess(true);//true,false
		resp.setStatusCode(BaseResp.OK);
		resp.setMessage("ok");// ok
		resp.setStatus("SUCCESS");// SUCCESS
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	};
	
	// 退出登录等操作后，删除所有本地缓存的待发送或发送失败的护肤方案
	public void solutionDeleteAllLocalDataAsync(BaseCall<BaseResp> call){
		handler4 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case DELETE_ALLSENDINGSOLUTION:
					SolutionDataGetDelete delBean = new SolutionDataGetDelete();
					delBean.setType(SolutionListData.SOLUTION_SENDING_DATA);
					RadarProxy.getInstance(context).startLocalData(HttpConstant.SOLUTION_DATA_DELETE_BYTYPE, GsonUtil.getGson().toJson(delBean), null);
					break;

				default:
					break;
				}
			}
		};
		
		Message msg = Message.obtain();
		msg.what = DELETE_ALLSENDINGSOLUTION;
		handler4.sendMessage(msg);
		
		BaseResp resp = new BaseResp();
		resp.setSuccess(true);//true,false
		resp.setStatusCode(BaseResp.OK);
		resp.setMessage("ok");// ok
		resp.setStatus("SUCCESS");// SUCCESS
		if (call != null && !call.cancel) {
			call.call(resp);
		}
	};
	
	
	private SolutionCreateReq analyzeBeanSave(SolutionCreateReq bean) {
		SolutionCreateReq newBean = new SolutionCreateReq();
		
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setTitle(bean.getTitle());
		newBean.setIntroduction(bean.getIntroduction());
		newBean.setContent(bean.getContent());
		newBean.setCoverUrl(bean.getCoverUrl());  //此处用getCoverLocalUrl
		newBean.setUseCycle(bean.getUseCycle());
		
		if (bean.getLocalSolutionId() == 0) {
			long LocalSolutionId = SharePreCacheHelper.getLocalSolutionId(context) + 1;
			if (LocalSolutionId > 9999999999999999L) {
				LocalSolutionId = 1;
			}
			newBean.setLocalSolutionId(LocalSolutionId);
			SharePreCacheHelper.setLocalSolutionId(context, LocalSolutionId);
		} else {
			newBean.setLocalSolutionId(bean.getLocalSolutionId());
		}

		return newBean;
	}
	
	private SolutionUpdateReq analyzeBeanSave2(SolutionUpdateReq bean) {
		SolutionUpdateReq newBean = new SolutionUpdateReq();
		
		newBean.setEffect(bean.getEffect());
		newBean.setPart(bean.getPart());
		newBean.setTitle(bean.getTitle());
		newBean.setIntroduction(bean.getIntroduction());
		newBean.setContent(bean.getContent());
		newBean.setCoverUrl(bean.getCoverUrl());
		newBean.setUseCycle(bean.getUseCycle());
		newBean.setSolutionId(bean.getSolutionId());
		
		if (bean.getLocalSolutionId() == 0) {
			long LocalSolutionId = SharePreCacheHelper.getLocalSolutionId(context) + 1;
			if (LocalSolutionId > 9999999999999999L) {
				LocalSolutionId = 1;
			}
			newBean.setLocalSolutionId(LocalSolutionId);
			SharePreCacheHelper.setLocalSolutionId(context, LocalSolutionId);
		} else {
			newBean.setLocalSolutionId(bean.getLocalSolutionId());
		}

		return newBean;
	}
	
	private ServerRequestParams getServerRequest() {
		if (params == null) {
			params = new ServerRequestParams();
		}
		return params;
	}
	
}
