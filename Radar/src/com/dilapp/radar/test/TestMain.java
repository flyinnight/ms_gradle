package com.dilapp.radar.test;

import java.util.HashMap;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.util.UmengUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestMain extends BaseActivity{
	
	private Button mBtn;
	private TextView mResultText;
	private String mResultS = "";
	
	private HashMap<Integer, CaseNode> mCaseMap = new HashMap<Integer, CaseNode>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_main_layout);
		
//		initCases();
		
		mBtn = (Button) findViewById(R.id.CaseBtn);
		mResultText = (TextView) findViewById(R.id.result);
		
		mBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initCases();
				UmengUtils.onEventSkinTest(TestMain.this, UmengUtils.TYPE_TEST_DAILY);
				startCaseQueue();
			}
		});
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			CaseNode node = mCaseMap.get(msg.what);
			if(node != null){
				mResultS += ""+msg.what+" "+node.getReportResult()+"\n";
				mResultText.setText(mResultS);
				mCaseMap.remove(msg.what);
			}
			if(mCaseMap.size() <= 0){
				mBtn.setEnabled(true);
			}
		}
		
	};
	
	
	private void startCaseQueue(){
		mBtn.setEnabled(false);
		mCaseMap.get(1).startCast();
	}
	
	private void initCases(){
		mCaseMap.clear();
		mResultS = "";
		mResultText.setText("测试进行中");
		mCaseMap.put(1, new LoginCase(this, mHandler, 1));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	

}
