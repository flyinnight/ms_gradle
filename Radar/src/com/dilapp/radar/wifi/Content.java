package com.dilapp.radar.wifi;

import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.ReleaseUtils;

import android.os.Environment;

/**
 * 公共变量
 * @author hj
 *
 */
public class Content {
	
	public static final boolean ONE_CAPTURE = ReleaseUtils.ONE_CAPTURE;
	public static final boolean IGNORE_SKIN_CHECK = ReleaseUtils.IGNORE_SKIN_CHECK;
	public static final boolean AP_DEBUG_MODE = ReleaseUtils.AP_DEBUG_MODE;

	public static final String bleMacStress = "ble_mac_Stress";
	public static final String wifiMacStress = "wifiMacStress";
	public static final String wifilist = "wifilist";   //设备连接过的wifi集合
	public static final String pair = "pair";   //ble配对成功是否
	public static final String bleConnectSuccess = "ble_connected";
	
	public static final String defaultSSid_value = "ovt_default_ap";
	public static final String defaultSSid_key = "ssid";
	
	public static final String KEY_BLE_NAME = "key_ble_name";
	
	public static final String KEY_AP_SSID = "key_ap_ssid";
	
	public static final String KEY_USER_NAME = "key_user_name";
	public static final String KEY_NICK_NAME = "key_nick_name";
	public static final String KEY_PASSWORD = "key_password";
	public static final String KEY_USER_ICON_URL = "key_user_icon_url";
	public static final String KEY_BINDED_PHONE = "key_binded_phone";
	public static final String KEY_BINDED_EMAIL = "key_binded_email";
	public static final String KEY_GENDER  = "key_gender";
	public static final String KEY_SKIN_TYPE = "key_skin_type";
	public static final String key_BIRTHDAY = "key_birthday";
	public static final String KEY_AREA	= "key_area";
	public static final String KEY_LEVEL = "key_level";
	public static final String KEY_FOLLOW = "key_follow";//关注
	public static final String KEY_FOLLOWED = "key_followed";//粉丝
	public static final String KEY_PRE_CHOOSE_SKIN = "key_pre_choose_skin";
	public static final String KEY_PRE_PUBLIC_PRIVATE = "key_pre_public_private";
	
	public static final String KEY_TOKEN = "key_token";
	public static final String KEY_ACTION_MAP = "key_action_map";
	public static final String KEY_LOCAL_POSTID = "key_local_postid";
	public static final String KEY_LOCAL_SOLUTIONID = "key_local_solutionid";
	public static final String KEY_USER_ROLE = "key_user_role";
	public static final String KEY_TOPICID_ADV = "key_topicid_adv";
	public static final String KEY_TOPICID_DRY = "key_topicid_dry";
	public static final String KEY_TOPICID_OIL = "key_topicid_oil";
	public static final String KEY_TOPICID_MIX = "key_topicid_mix";
	public static final String KEY_TOPIC_OWNER_LIST = "key_topic_owner_list";
	public static final String KEY_TOPIC_ADMIN_LIST = "key_topic_admin_list";
	public static final String KEY_TOPIC_FORBIDDEN_LIST = "key_topic_forbidden_list";
	public static final String KEY_USER_POST_ID = "key_user_post_id";
	public static final String KEY_USER_TOPIC_ID = "key_user_topic_id";
	public static final String KEY_EM_USER_ID = "key_em_user_id";
	public static final String KEY_USER_ID = "key_user_id";
	
	public static final String KEY_FOREHEAD = "key_forehead";
	public static final String KEY_CHEEK = "key_cheek";
	public static final String KEY_EYE = "key_eye";
	public static final String KEY_NOSE = "key_nose";
	public static final String KEY_HAND = "key_hand";
	public static final String KEY_WEATHER = "key_weather";
	public static final String KEY_BATT_STATUS ="key_batt_status";
	public static final String KEY_APP_UPDATE = "key_app_update";
	
	//file path
	public static final String RGB_FILE_NAME = "rgb";
	public static final String PL_FILE_NAME = "pl";
	public static final String RGB_PATH = PathUtils.RADAR_IMAGE_CACEH + RGB_FILE_NAME + ".jpg";
	public static final String PL_PATH = PathUtils.RADAR_IMAGE_CACEH + PL_FILE_NAME + ".jpg";
	
	
	
	
	
	
	
}
