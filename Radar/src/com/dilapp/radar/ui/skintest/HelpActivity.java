package com.dilapp.radar.ui.skintest;

import com.dilapp.radar.R;
import com.dilapp.radar.R.id;
import com.dilapp.radar.R.layout;
import com.dilapp.radar.R.string;
import com.dilapp.radar.ui.TitleView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

public class HelpActivity extends Activity implements OnClickListener {
     //ui
//	private TextView tv_center;
//	private LinearLayout back;
	private TitleView mTitle;
	private ScrollView scrollView;
	private LinearLayout helperconfirmLayput;
	private LinearLayout nethelperLayput;
	private LinearLayout connecthelperLayput;
	
	private int  infoId;
	
	private static final int MSG_SCROLL_TO = 1;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MSG_SCROLL_TO:
				scrollById();
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
		initView();
		init();
		
	}
	
	

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if(intent != null){
			infoId = intent.getIntExtra("infoId",0);
		}
	}



	private void init() {
//		tv_center.setText(getString(R.string.activity_helper_title));
//		back.setOnClickListener(this);
		
		Intent intent = getIntent();
		if(intent != null){
			infoId = intent.getIntExtra("infoId",0);
		}
		
	}

	private void initView() {
		View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.activity_helper_title, null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
//		tv_center = (TextView) this.findViewById(R.id.title_center);
//		back = (LinearLayout) this.findViewById(R.id.title_left);
		scrollView = (ScrollView)this.findViewById(R.id.scroller);
		helperconfirmLayput = (LinearLayout) this.findViewById(R.id.helperconfirm);
		nethelperLayput = (LinearLayout) this.findViewById(R.id.nethelper);
		connecthelperLayput = (LinearLayout) this.findViewById(R.id.connecthelper);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.removeMessages(MSG_SCROLL_TO);
		mHandler.sendEmptyMessageDelayed(MSG_SCROLL_TO, 800);
	}
	
	private void scrollById(){
		switch (infoId) {
		case 0://net 
			scrollView.smoothScrollTo((int)nethelperLayput.getX(), (int)nethelperLayput.getY());
			break;
		case 1://confirm
			scrollView.smoothScrollTo((int)helperconfirmLayput.getX(), (int)helperconfirmLayput.getY());
		break;
		case 2://connecthelperLayput
			scrollView.smoothScrollTo((int)connecthelperLayput.getX(), (int)connecthelperLayput.getY());
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			finish();
			break;

		default:
			break;
		}
		
	}



}
