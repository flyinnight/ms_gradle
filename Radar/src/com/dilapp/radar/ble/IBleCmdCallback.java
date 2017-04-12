package com.dilapp.radar.ble;

public interface IBleCmdCallback {

	public void onConnectRequestResult(int result);
	public void onWifiStatusResult(int mode, String ssid, String ip, int errorcode, int cmd);
	public void onDeviceStatusResult(int net_status, int power_status,int cmd);
	public void onPreSetWifiModeResult();
	public void onPhotoCmdFromDevice();
	public void onBatteryLevelChanged(int level, int cmd);
	public void onEnvParamsResult(byte temp, byte rh, byte uv, int cmd);
}
