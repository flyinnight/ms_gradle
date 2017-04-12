package com.dilapp.radar.wifi;

public interface IWifiKfirHelperCallback {
	
	/**
	 * wifi disabled
	 */
	public void wifiDisabled();
	/**
	 * wifi enable
	 */
	public void wifiEnable();
	
	public void wifiEnableding();
	
	public void onWifiConnectSuccess(String ssid);
	
	public void onWifiDisconnected();
	public void onWifiConnecting();
}
