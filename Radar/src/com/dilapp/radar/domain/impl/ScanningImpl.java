package com.dilapp.radar.domain.impl;

import android.content.Context;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.Scanning;
import com.dilapp.radar.server.ClientCallback;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.util.HttpConstant;

public class ScanningImpl extends Scanning {
	private Context context;

	public ScanningImpl(Context context) {
		this.context = context;
	}

	@Override
	public void ScanAnalyzeAsync(ScanReq bean, BaseCall<ScanResp> call) {
		scanningLocalData();
	}

	/**
	 * 扫描测试库，返回未上传的数据
	 */
	private void scanningLocalData() {
		RadarProxy.getInstance(context).startLocalData(HttpConstant.SCANNING_ANALYZE, null, new clientCall());
	}

	public class clientCall implements ClientCallback {

		@Override
		public void onSuccess(String result) {
			
		}

		@Override
		public void onFailure(String result) {
			
		}

	}
}
