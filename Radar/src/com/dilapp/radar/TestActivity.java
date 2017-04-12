package com.dilapp.radar;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.dilapp.radar.wifi.CameraKfirHelper;

public class TestActivity extends Activity {
	private GLSurfaceView glSurfaceView;
	private CameraKfirHelper cameraHelper;
	static final int CAMERA_CHANNEL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		
		setContentView(R.layout.activity_test);
		
		glSurfaceView = (GLSurfaceView) this.findViewById(R.id.sv_voide);

		//cameraHelper = CameraHelper.getInstance(this);
		
		//cameraHelper.startVideo(glSurfaceView, this);
		
		
		Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
		getImage.addCategory(Intent.CATEGORY_OPENABLE);
		getImage.setType("image/*");
		startActivityForResult(getImage, 1000);

	}

}
