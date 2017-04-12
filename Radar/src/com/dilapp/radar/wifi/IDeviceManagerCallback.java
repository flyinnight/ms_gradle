package com.dilapp.radar.wifi;

import android.bluetooth.BluetoothDevice;

public interface IDeviceManagerCallback {

	public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record);
	
	
	public void deviceConnectStatus(DeviceConnectStatus deviceConnectStatus,BluetoothDevice device);
	
	public void deviceConfirmed(BluetoothDevice device, boolean isconfirm);
	
	
	public enum DeviceConnectStatus{idel,success,failed};
	
	public void onDeviceLightStatus(int linkstatus, int powerstatus);
	
	public void onDeviceWifiStatus(int mode, String ssid, String ip, int error);
	
//	public void onDeviceEnvParamsChanged(int envParams);
}
