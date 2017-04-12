package com.dilapp.radar.update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.SaveTestPic;
import com.dilapp.radar.domain.SaveTestPic.FacialPicReq;
import com.dilapp.radar.domain.SaveTestPic.FacialPicResp;
import com.dilapp.radar.domain.server.FacialAnalyzeBean;
import com.dilapp.radar.util.ABFileUtil;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.Content;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

public class UpdateTestDataImpl {
	
	private static UpdateTestDataImpl mSelf;
	private Context mContext;
	private SaveTestPic mPicBean;
	private boolean isWorking = false;
	
	private static final int MSG_CHECK_SEND_STATUS = 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_CHECK_SEND_STATUS:
				handleCheckSendStatus();
				break;
			}
		}
		
	};
	
	public synchronized static UpdateTestDataImpl getInstance(Context context){
		if(mSelf == null){
			mSelf = new UpdateTestDataImpl(context);
		}
		return mSelf;
	}
	
	public void startCheckSend(){
		if(isWorking){
			Slog.e("startCheckSend  has started !!!!!!");
			return;
		}
		mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 500);
	}
	
	public void startSaveTestPicByPart(int part){
		Date mcurrDate = new Date(System.currentTimeMillis());
		SimpleDateFormat currDF = new SimpleDateFormat("yyy-MM-dd");
		currDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String currFormat = currDF.format(mcurrDate);
		String pathNR = PathUtils.TEST_IMAGE_BASE+part+"_"+currFormat+"_NR"+".jpg";
		String pathPL = PathUtils.TEST_IMAGE_BASE+part+"_"+currFormat+"_PL"+".jpg";
		File fNR = new File(pathNR);
		File fPL = new File(pathPL);
		if(fNR.exists() && fPL.exists()){
			Slog.e("Failed save PIC has existed : "+pathNR);
			return;
		}
		ABFileUtil.copyFile(Content.PL_PATH, pathPL);
		ABFileUtil.copyFile(Content.RGB_PATH, pathNR);
		startCheckSend();
		
	}
	
	private synchronized void handleCheckSendStatus(){
		isWorking = true;
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null){
			int netType = networkInfo.getType();
			if(netType == ConnectivityManager.TYPE_WIFI){
				int i = 1;
				for(i=1;i<=5;i++){
					if(handleSendPicByPartInter(i) >= 0){
						break;
					}
				}
				if(i > 5){
					isWorking = false;
				}
			}else{
				isWorking = false;
			}
		}else{
			isWorking = false;
		}
	}
	
	@SuppressWarnings("deprecation")
	private int handleSendPicByPartInter(final int part){
		long lasttime = SharePreCacheHelper.getSavePicTimeByPart(mContext, part);
		long currTime = System.currentTimeMillis();
		Date mcurrDate = new Date(currTime);
		Date mlastDate = new Date(lasttime);
		SimpleDateFormat currDF = new SimpleDateFormat("yyy-MM-dd");
		SimpleDateFormat lastDF = new SimpleDateFormat("yyy-MM-dd");
		currDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		lastDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String currFormat = currDF.format(mcurrDate);
		String lastFormat = lastDF.format(mlastDate);
		if(currFormat.equals(lastFormat)){
			return -1;
		}
		String pathNR = PathUtils.TEST_IMAGE_BASE+part+"_"+currFormat+"_NR"+".jpg";
		String pathPL = PathUtils.TEST_IMAGE_BASE+part+"_"+currFormat+"_PL"+".jpg";
		File fNR = new File(pathNR);
		File fPL = new File(pathPL);
		if(!fNR.exists() || !fPL.exists()){
			return -2;
		}
		if(mPicBean == null){
			mPicBean = ReqFactory.buildInterface(mContext, SaveTestPic.class);
		}
		List<String> plist = new ArrayList<String>();
		plist.add(pathNR);
		plist.add(pathPL);
		mPicBean.uploadFacialPicAsync(plist, new BaseCall<SaveTestPic.FacialPicResp>() {
			
			@Override
			public void call(FacialPicResp resp) {
				// TODO Auto-generated method stub
				if(resp.isRequestSuccess()){
					List<String> plist = resp.getFacialPicsUrl();
					if(plist == null || plist.size() != 2){
						Slog.e("Error get getFacialPicsUrl !!!!!!");
						mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
						mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 500);
						return;
					}
					FacialPicReq mPicReq = new FacialPicReq();
					mPicReq.setPicUrl(plist.get(0)+","+plist.get(1));
					mPicReq.setDay(System.currentTimeMillis());
					mPicReq.setPart(""+part);
					mPicReq.setSkinQuality(SharePreCacheHelper.getSkinType(mContext));
					mPicBean.saveFacialPicAsync(mPicReq, new BaseCall<SaveTestPic.FacialPicResp>() {

						@Override
						public void call(FacialPicResp resp) {
							// TODO Auto-generated method stub
							if(resp.isRequestSuccess()){
								Slog.i("SUCCESS !!!  saveFacialPicAsync : part : "+part);
								SharePreCacheHelper.setSavePicTimeByPart(mContext, part, System.currentTimeMillis());
								mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
								mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 500);
							}else{
								Slog.e("Failed saveFacialPicAsync!!!!!!!");
							}
						}
					});
					mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 10000);
				}else{
					Slog.e("Failed uploadFacialPicAsync !!!"+resp.getMessage());
//					mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
//					mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 500);
				}
			}
		});
		mHandler.removeMessages(MSG_CHECK_SEND_STATUS);
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_SEND_STATUS, 10000);
		return 0;
	}
	
	private UpdateTestDataImpl(Context context){
		mContext = context;
	}

}
