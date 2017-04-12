package com.dilapp.radar.ui.skintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;

public class ActivityBindConfirm extends BaseActivity implements OnClickListener{
	
	
	private TitleView mTitle;
	private Button mStartBtn;
	
	private boolean mNeedBack = false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_finish);

		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);
		
		mStartBtn = (Button) findViewById(R.id.start_app_btn);
		mStartBtn.setOnClickListener(this);
		
		mNeedBack = getIntent().getBooleanExtra("need_back", false);
		View title = findViewById(TitleView.ID_TITLE);
        mTitle = new TitleView(this, title);
        mTitle.setCenterText(R.string.finish_bind, null);
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));
        if(mNeedBack){
        		mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        }
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.vg_left:
			 finish();
		     //overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			break;
		case R.id.start_app_btn:
			Intent mintent = new Intent(ActivityBindConfirm.this, ActivityTabs.class);
			mintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(mintent);
			finish();
			break;
		}
	}

}
