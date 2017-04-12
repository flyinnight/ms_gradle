package com.dilapp.radar.test;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public abstract class CaseNode {
	
	public static final int ERROR_CANCELLED = -1;
	public static final int ERROR_EXCEPTION = -2;
	
	protected Context mContext;
	protected boolean useTask = false;
	protected boolean needCallback = false;
	
	protected String mReportResult = null;
	protected boolean isStarted = false;
	protected String mCaseName = "CaseNode";
	protected CaseTask mCaseTask = null;
	
	protected Handler mTargetHandler;
	protected int mMsgID;
	
	public CaseNode(Context context, String name, Handler tarHandler, int msgid){
		this.mContext = context;
		
		if(name != null && !name.isEmpty()){
			this.mCaseName = name;
		}
		this.mTargetHandler = tarHandler;
		this.mMsgID = msgid;
	}
	
	public void isStartByAsyncTask(boolean flag){
		this.useTask = flag;
	}
	
	public void isNeedWaitCallback(boolean flag){
		this.needCallback = flag;
	}
	
	/**
	 * if needCallback == true this must be called in callback
	 */
	public final void notifyCallback(){
		mReportResult = ""+mCaseName+" : " + onCaseEnd();
		notifyCaseEnd();
	}
	
	/**
	 * 装填具体的测试逻辑
	 */
	public abstract void onCaseStart();
	
	/**
	 * 进程出错，反馈错误类型，之后不再调用onCaseEnd()
	 * @param errorcode 测试结论报告，用于打印结果。
	 */
	public abstract String onError(int errorcode, String msg);
	
	/**
	 * 装填测试结论，并反馈出去
	 * @return 测试结论报告，用于打印结果。
	 */
	public abstract String onCaseEnd();

	/*********************************/
	
	/**
	 * only called by Main test process
	 * @return
	 */
	public String getReportResult(){
		return this.mReportResult;
	}
	
	public synchronized void startCast(){
		if(isStarted) return;
		isStarted = true;
		
		if(useTask){
			mCaseTask = new CaseTask();
			mCaseTask.execute(0);
		}else{
			try{
				onCaseStart();
				if(!needCallback){
					mReportResult = ""+mCaseName+" : " + onCaseEnd();
				}
			}catch(Exception e){
				mReportResult = ""+mCaseName+" : " + onError(ERROR_EXCEPTION, e.toString());
				needCallback = false;
			}
			if(!needCallback){
				notifyCaseEnd();
			}
		}
	}
	
	private void notifyCaseEnd(){
		if(mTargetHandler != null){
			mTargetHandler.sendEmptyMessage(mMsgID);
		}
		mTargetHandler = null;
	}
	
	private class CaseTask extends AsyncTask<Integer, Integer, Integer>{

		
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int result = -1;
			try{
				onCaseStart();
				result = 100;
			}catch(Exception e){
				mReportResult = ""+mCaseName+" : " + onError(ERROR_EXCEPTION, e.toString());
				needCallback = false;
				result = -1;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result > 0 && !needCallback){
				mReportResult = ""+mCaseName+" : " + onCaseEnd();
			}
			if(!needCallback){
				notifyCaseEnd();
			}
		}

		@Override
		protected void onCancelled(Integer result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			mReportResult = ""+mCaseName+" : " + onError(ERROR_CANCELLED, null);
			needCallback = false;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			mReportResult = ""+mCaseName+" : " + onError(ERROR_CANCELLED, null);
			needCallback = false;
		}
		
		
		
	}
}
