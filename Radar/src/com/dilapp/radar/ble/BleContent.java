package com.dilapp.radar.ble;

public class BleContent {
	
	public static final String RADAR_DEVICE = "0000d001-0000-1000-8000-00805f9b34fb";
	
    //command name
	public static final int cmd_connectBle            = 1;
	public static final int cmd_bleStatus             = 2;
	public static final int cmd_bleStatusCallback     = 3;
	public static final int cmd_staCommand            = 4;
	public static final int cmd_apCommand             = 5;
	public static final int cmd_wifiStatus            = 6;
	public static final int cmd_wifiStatusCallBack    = 7;
	public static final int cmd_prepareWifiMode       = 8;
	public static final int cmd_ble_photo_cmd		 = 9;
	public static final int cmd_time_check			 = 10;// 9 has been used
	public static final int cmd_get_battery_level 	 = 11;
	public static final int cmd_report_battery_level  = 12;
	public static final int cmd_get_env_params		 = 13;
	
	
	//linkStatus
	public static final int link_Idel = 0;
	public static final int link_ready = 1;
	public static final int link_connecting = 2;
	public static final int link_connected = 3;
	
	//powerStatus
	public static final int power_Idel = 0;
	public static final int power_lowPower = 1;
	public static final int power_changing = 2;
	
	
	//errorCode
	public static final int error_Idel = 0;
	public static final int error_notExistAp = -1;
	public static final int error_pwd = -2;
	public static final int error_connect_ovver_time = -3;
	
	
	
	
	
	
	
	
	
	

}
