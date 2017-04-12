package com.dilapp.radar.wifi;

public interface IWifiHelperUiCallback {
	
	/**
	 * Ap connect success
	 * @param ip
	 */
	public void connectApSuccess(String ip);
	
	/**
	 * wifi connect success
	 * @param ssid
	 * @param ip
	 */
	public void connectWifiSuccess(String ssid,String ip);
	
	/**
	 * wifi disconnected
	 */
	public void wifiDisconnected();
	
	/**
	 * ap disconnected
	 */
	public void apDisConnected();
	
	
	public void apConnecting();
	
	public void apConnectError();
	
	
	/**
	 * device connecte wifi success
	 */
	public void deviceConnected();
	
	
	public void deviceDisConnected();
	
	
	
	
	
	
	
	
	/**
	 * wifi disabled
	 */
	public void wifiDisabled();
	/**
	 * wifi enable
	 */
	public void wifiEnable();
	
	public void wifiEnableding();
	

}
