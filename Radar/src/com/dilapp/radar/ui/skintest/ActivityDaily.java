package com.dilapp.radar.ui.skintest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;

public class ActivityDaily extends BaseActivity implements OnClickListener {

	private TitleView mTitle;
	private GLSurfaceView sv_video;

	private CameraVideoHelper mVideoHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily);
		Context context = getApplicationContext();
		sv_video = findViewById_(R.id.sv_voide);

		mVideoHelper = new CameraVideoHelper(context, sv_video,false);
		// mVideoHelper.setOnTakeResultListener((OnTakeResultListener) mTak);
		mVideoHelper.onCreate(savedInstanceState);

		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.daily_title, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setRightIcon(R.drawable.btn_help, this);
	}

	private boolean testTake;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			// overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			break;
		case TitleView.ID_RIGHT:
			testTake = !testTake;
			Toast.makeText(getApplicationContext(),
					(testTake ? "开启" : "关闭") + "使用测试图片分析。", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.btn_face_test:
			break;
		default:
			break;
		}

	}


	public static int getPart(int flag) {
		int type = 0;
		switch (flag) {
		case R.string.normal_forehead:
			type = AnalyzeType.FOREHEAD;
			break;
		case R.string.normal_cheek:
			type = AnalyzeType.CHEEK;
			break;
		case R.string.normal_eye:
			type = AnalyzeType.EYE;
			break;
		case R.string.normal_nose:
			type = AnalyzeType.NOSE;
			break;
		case R.string.normal_hand:
			type = AnalyzeType.HAND;
			break;
		default:
			type = AnalyzeType.CHEEK;
			break;
		}
		return type;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mVideoHelper.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mVideoHelper.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		mVideoHelper.onPause();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// sw_switch.setChecked(false);
		mVideoHelper.onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mVideoHelper.onDestroy();
		super.onDestroy();
	}
}
