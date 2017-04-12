package com.dilapp.radar.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dilapp.radar.R;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.Slog;

public class ActivityRadarDetail extends BaseActivity implements OnClickListener{
	
	private TitleView mTitle;
	private WebView mRadarWeb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_radar_detail);
		
		mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
		mTitle.setCenterText("Radar", null);
        mTitle.setLeftIcon(R.drawable.btn_back_white, this);
        
        mRadarWeb = findViewById_(R.id.radar_web);
        
      WebSettings webSettings =   mRadarWeb.getSettings();       
      webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
      webSettings.setLoadWithOverviewMode(true);
//      webSettings.setJavaScriptEnabled(true);  
      webSettings.setBuiltInZoomControls(true);
      webSettings.setSupportZoom(true);
      webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//      webSettings.setSavePassword(true);
      webSettings.setSaveFormData(true);
      webSettings.setJavaScriptEnabled(true);
      webSettings.setAllowFileAccessFromFileURLs(true);
      // enable navigator.geolocation
      webSettings.setGeolocationEnabled(true);
      webSettings.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/"); 
      // enable Web Storage: localStorage, sessionStorage
      webSettings.setDomStorageEnabled(true);
      mRadarWeb.setScrollBarStyle(0);
      mRadarWeb.setClickable(true);
      mRadarWeb.requestFocus();
      mRadarWeb.setWebViewClient(new WebViewClient(){

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
            return false;
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				view.goBack();
//				return false;
			}
			return false;
//			return super.shouldOverrideKeyEvent(view, event);
		}
    	  
      });
      mRadarWeb.setDownloadListener(new DownloadListener() {
		
		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			// TODO Auto-generated method stub
			Slog.e("onDownloadStart : "+url);
			 Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
		}
	});

        mRadarWeb.loadUrl(HttpConstant.GET_RADAR_INFO_MORE);
        
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
//		Uri uri = Uri.parse("http://121.41.79.23:80/radar/"); 
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
		case TitleView.ID_LEFT:
			finish();
			break;
		}
	}

}
