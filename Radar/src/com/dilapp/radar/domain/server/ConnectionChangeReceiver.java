package com.dilapp.radar.domain.server;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			// Network NG
		} else {
			// Network OK
/*			Object scanObj = ReqFactory.buildInterface(context, Scanning.class);
			ScanningImpl scanImpl = (ScanningImpl) scanObj;
			// Scanning.ScanReq req = new Scanning.ScanReq();
			scanImpl.ScanAnalyzeAsync(null, new BaseCall<ScanResp>() {

				@Override
				public void call(ScanResp resp) {
					// 入库成功

				}
			});
*/
		}
	}
}
