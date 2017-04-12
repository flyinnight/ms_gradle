package com.dilapp.radar.ble;

public class BleUtils {
	
	public static final boolean BLE_DEBUG 			= false;
	public static final String BLE_NAME_START 		= "radar";//"krait";
	
	public static final String ACTION_BATTERY_CHANGED = "action_radar_battery_changed";
	public static final String KEY_BATTERY_STATUS = "key_battery_status";
	public static final String ACTION_GET_ENV_PARAM = "action_radar_get_env_params";
	public static final String KEY_ENV_PARAM = "key_env_params";
	
	public static final int BATTERY_LEVEL_WARNING	= 40;
	public static final int BATTERY_LEVEL_LOW		= 30;
	
	public static final int DEVICE_MODE_IDLE 		= 0;
	public static final int DEVICE_MODE_STA			= 1;
	public static final int DEVICE_MODE_AP 			= 2;
	
	public static final int DEVICE_LINK_IDLE 		= 0;
	public static final int DEVICE_LINK_READY 		= 1;
	public static final int DEVICE_LINK_CONNECTED 	= 2;
	public static final int DEVICE_LINK_CONNECTING 	= 3;
	
	public static final int DEVICE_POWER_IDLE 		= 0;
	public static final int DEVICE_POWER_LOWP		= 1;
	public static final int DEVICE_POWER_CHARGE		= 2;
	
	public static final int ERROR_CODE_IDLE			= 0;
	public static final int ERROR_CODE_NOEXIST		= 1;
	public static final int ERROR_CODE_ERRORPWD		= 5;
	public static final int ERROR_CODE_TIMEOUT 		= 3;
	public static final int ERROR_CODE_ERRORFAILED		= 6;

}
