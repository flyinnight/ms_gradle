package com.dilapp.radar.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.location.Weather;
import com.dilapp.radar.location.WeatherImpl;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.Content;
import com.dilapp.radar.wifi.LocalWifi;
import com.dilapp.radar.wifi.WifiList;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author hj
 * @time 2015-05-8
 *
 */
public class SharePreCacheHelper {
	
	@SuppressWarnings("unchecked")
	public static List<LocalWifi> getLocalWifiList(Context context){
		
		List<LocalWifi> wifiList  = new ArrayList<LocalWifi>();
		WifiList list = (WifiList) SharePrefUtil.getObj(context, Content.wifilist);
		if(list != null){
		 wifiList  = list.localWifiList;
		}else{
			wifiList = null;
		}
		return wifiList;
		
	}
	
	/**
	 *  save  wifi info
	 * @param context
	 * @param localWifi
	 */
	public static void addLocalWifiList(Context context,LocalWifi localWifi){
		
		if (localWifi == null)
			return;
		List<LocalWifi> wifiList = getLocalWifiList(context);
		int i = -1;
		boolean flag = false;
		if (wifiList != null && wifiList.size() > 0) {
			for (LocalWifi wifiInfo : wifiList) {
				i++;
				if (wifiInfo.wifiMac.equals(localWifi.wifiMac)) {
					flag = true;
					break;
				}
			}
			if (flag) {
				Slog.sd("--second save : "+localWifi.wifiName+" "+localWifi.wifiMac);
				wifiList.remove(i);
				wifiList.add(localWifi);
				saveLocalWifiList(context, wifiList);
			}else{
				Slog.sd("--first save 11: "+localWifi.wifiName+" "+localWifi.wifiMac);
//				wifiList = new ArrayList<LocalWifi>();
				wifiList.add(localWifi);
				saveLocalWifiList(context, wifiList);
			}
		} else {
			Slog.sd("--first save 22: "+localWifi.wifiName+" "+localWifi.wifiMac);
			wifiList = new ArrayList<LocalWifi>();
			wifiList.add(localWifi);
			saveLocalWifiList(context, wifiList);
		}

	}
	
	public static void clearWifiList(Context context){
		WifiList wifiList = new WifiList();
		SharePrefUtil.saveObj(context, Content.wifilist, wifiList);
		Slog.sd("Clear WifiList!!!!!");
		Log.e("hj", "clear wifilist");
	}
	
	public static LocalWifi checkSSid(Context context,String mac,String ssid){
		boolean result = false;
		List<LocalWifi> wifiList = getLocalWifiList(context);
		LocalWifi localWifi = null;
		if(wifiList == null) {
			Slog.w("Check SSID Failed by Can not find Local wifiList!!!!");
			return localWifi;
		}
		for(LocalWifi wifiInfo : wifiList){
			if(wifiInfo.wifiMac.equals(mac)){
				result = true;
				localWifi = wifiInfo;
				Log.i("hj", " ssid is save ");
				break;
			}
		}
		if(localWifi == null){
			Slog.w("Check SSID Failed by Not FOUND : "+mac+" "+ssid);
		}
		return localWifi;
	}
	
	private static void saveLocalWifiList(Context context ,List<LocalWifi> list){
		if(list == null){
			return;
		}
		WifiList wifiList = new WifiList();
		wifiList.localWifiList = list;
		SharePrefUtil.saveObj(context, Content.wifilist, wifiList);
		Slog.sd("Save WifiList : ");
		for(int i=0;i<list.size();i++){
			Slog.sd(list.get(i).wifiName+"*"+list.get(i).wifiMac+";");
		}
		Log.e("hj", "save wifilist");
	}
	
	/**
	 *  ble device mac
	 * @param context
	 * @return
	 */
	public static String getBleMacAddress(Context context){
		
		return SharePrefUtil.getString(context, Content.bleMacStress, "");
		
	}
	
	public static void  saveBleMacAddress(Context context ,String macStress){
		SharePrefUtil.saveString(context, Content.bleMacStress, macStress);
	}
	
	public static void clearBleMacAddress(Context context){
		SharePrefUtil.saveString(context, Content.bleMacStress,"");
	}
	
	public static String getBleName(Context context){
		
		return SharePrefUtil.getString(context, Content.KEY_BLE_NAME, "");
		
	}
	
	public static void  saveBleName(Context context ,String name){
		SharePrefUtil.saveString(context, Content.KEY_BLE_NAME, name);
	}
	
	/**
	 * 是否配对成功
	 * @param context
	 * @return
	 */
	public static boolean getPairStatus(Context context){
		return SharePrefUtil.getBoolean(context, Content.pair, false);
	}
	
	public static void savePairStatus(Context context,boolean status){
		SharePrefUtil.saveBoolean(context, Content.pair, status);
	}
    
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getBleConnectStatus(Context context){
		return SharePrefUtil.getBoolean(context, Content.bleConnectSuccess, false);
	}
	
	public static void saveBleConnectStatus(Context context,boolean status){
		SharePrefUtil.saveBoolean(context, Content.bleConnectSuccess, status);
	}
	
	
	/**
	 *  default ssid
	 * @param context
	 * @param ssid
	 */
	public static void saveDefaultSSid(Context context,String ssid){
		SharePrefUtil.saveString(context, Content.defaultSSid_key, ssid);
	}
	
	public static String getDefaultSSid(Context context){
		return SharePrefUtil.getString(context, Content.defaultSSid_key,"");
	}
	
	public static void clearDefaultSSID(Context context){
		SharePrefUtil.saveString(context, Content.defaultSSid_key, "");
	}
	
	//add by kfir
	public static String getUserName(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_NAME, "");
	}

	public static void setUserName(Context context, String userName) {
		SharePrefUtil.saveString(context, Content.KEY_USER_NAME, userName);
	}

	public static String getNickName(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_NICK_NAME, "");
	}

	public static void setNickName(Context context, String nickname) {
		SharePrefUtil.saveString(context, Content.KEY_NICK_NAME, nickname);
	}

	public static String getPassword(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_PASSWORD,null);
	}

	public static void setPassword(Context context, String password) {
		SharePrefUtil.saveString(context, Content.KEY_PASSWORD, password);
	}

	public static String getUserIconUrl(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_ICON_URL, "");
	}

	public static void setUserIconUrl(Context context, String userIconUrl) {
		SharePrefUtil.saveString(context, Content.KEY_USER_ICON_URL, userIconUrl);
	}
	
	public static String getBindedPhone(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_BINDED_PHONE,null);
	}

	public static void setBindedPhone(Context context, String phone) {
		SharePrefUtil.saveString(context, Content.KEY_BINDED_PHONE, phone);
	}
	
	public static String getBindedEmail(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_BINDED_EMAIL,null);
	}
	
	public static void setBindedEmail(Context context, String email) {
		SharePrefUtil.saveString(context, Content.KEY_BINDED_EMAIL, email);
	}
	
	public static int getGender(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_GENDER, -1);
	}
	
	/**
	 * 
	 * @param context
	 * @param gender 1 男  2 女 3，保密
	 */
	public static void setGender(Context context, int gender){
		SharePrefUtil.saveInt(context, Content.KEY_GENDER, gender);
	}
	
	public static int getSkinType(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_SKIN_TYPE, -1);
	}
	
	/**
	 * 
	 * @param context
	 * @param skintype 1 干性 2 油性 3 混合
	 */
	public static void setSkinType(Context context, int skintype){
		SharePrefUtil.saveInt(context, Content.KEY_SKIN_TYPE, skintype);
	}
	
	public static Date getBirthDay(Context context){
		return (Date) SharePrefUtil.getObj(context, Content.key_BIRTHDAY);
	}
	
	public static void setBirthDay(Context context, Date birthday){
		SharePrefUtil.saveObj(context, Content.key_BIRTHDAY, birthday);
	}
	
	public static String getArea(Context context){
		return SharePrefUtil.getString(context, Content.KEY_AREA, null);
	}
	
	public static void setArea(Context context, String area){
		SharePrefUtil.saveString(context, Content.KEY_AREA, area);
	}
	
	public static int getLevel(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_LEVEL, 0);
	}
	
	public static void setLevel(Context context, int level){
		SharePrefUtil.saveInt(context, Content.KEY_LEVEL, level);
	}
	
	public static int getFollow(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_FOLLOW, 0);
	}
	
	public static void setFollow(Context context, int follow){
		SharePrefUtil.saveInt(context, Content.KEY_FOLLOW, follow);
	}
	
	//关注
	public static int getFollowed(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_FOLLOWED, 0);
	}
	
	//粉丝
	public static void setFollowed(Context context, int followed){
		SharePrefUtil.saveInt(context, Content.KEY_FOLLOWED, followed);
	}
	
	public boolean preferChoseSkin;
	public boolean publicPrivacy;
	
	public static boolean isPreferChooseSkinType(Context context){
		return SharePrefUtil.getBoolean(context, Content.KEY_PRE_CHOOSE_SKIN, false);
	}
	
	public static void setPreferChooseSkinType(Context context, boolean flag){
		SharePrefUtil.saveBoolean(context, Content.KEY_PRE_CHOOSE_SKIN, flag);
	}
	
	public static boolean isPublicPrivacy(Context context){
		return SharePrefUtil.getBoolean(context, Content.KEY_PRE_PUBLIC_PRIVATE, false);
	}
	
	public static void setPublicPrivacy(Context context, boolean flag){
		SharePrefUtil.saveBoolean(context, Content.KEY_PRE_PUBLIC_PRIVATE, flag);
	}
	//add by kfir END

	public static String getUserToken(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_TOKEN, "");
	}
	public static void setUserToken(Context context, String userid) {
		SharePrefUtil.saveString(context, Content.KEY_TOKEN, userid);
	}
	
	public static String getActionMap(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_ACTION_MAP, "");
	}
	public static void setActionMap(Context context, String actionMap) {
		SharePrefUtil.saveString(context, Content.KEY_ACTION_MAP, actionMap);
	}
	
	public static long getLocalPostId(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_LOCAL_POSTID, 0);
	}
	public static void setLocalPostId(Context context, long localPostId) {
		SharePrefUtil.saveLong(context, Content.KEY_LOCAL_POSTID, localPostId);
	}
	
	public static long getLocalSolutionId(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_LOCAL_SOLUTIONID, 0);
	}
	public static void setLocalSolutionId(Context context, long localSolutionId) {
		SharePrefUtil.saveLong(context, Content.KEY_LOCAL_SOLUTIONID, localSolutionId);
	}
	
	public static String getUserRole(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_ROLE, "");
	}
	public static void setUserRole(Context context, String userRole) {
		SharePrefUtil.saveString(context, Content.KEY_USER_ROLE, userRole);
	}

	public static long getTopicIdAdv(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_TOPICID_ADV, 0);
	}
	public static void setTopicIdAdv(Context context, long topicId) {
		SharePrefUtil.saveLong(context, Content.KEY_TOPICID_ADV, topicId);
	}
	public static long getTopicIdDry(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_TOPICID_DRY, 0);
	}
	public static void setTopicIdDry(Context context, long topicId) {
		SharePrefUtil.saveLong(context, Content.KEY_TOPICID_DRY, topicId);
	}
	public static long getTopicIdOil(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_TOPICID_OIL, 0);
	}
	public static void setTopicIdOil(Context context, long topicId) {
		SharePrefUtil.saveLong(context, Content.KEY_TOPICID_OIL, topicId);
	}
	public static long getTopicIdMix(Context context) {
		return SharePrefUtil.getLong(context, Content.KEY_TOPICID_MIX, 0);
	}
	public static void setTopicIdMix(Context context, long topicId) {
		SharePrefUtil.saveLong(context, Content.KEY_TOPICID_MIX, topicId);
	}
	
	public static String getTopicOwnerList(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_TOPIC_OWNER_LIST, "");
	}
	public static void setTopicOwnerList(Context context, String ownerList) {
		SharePrefUtil.saveString(context, Content.KEY_TOPIC_OWNER_LIST, ownerList);
	}
	public static String getTopicAdminList(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_TOPIC_ADMIN_LIST, "");
	}
	public static void setTopicAdminList(Context context, String adminList) {
		SharePrefUtil.saveString(context, Content.KEY_TOPIC_ADMIN_LIST, adminList);
	}
	public static String getTopicForbiddenList(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_TOPIC_FORBIDDEN_LIST, "");
	}
	public static void setTopicForbiddenList(Context context, String forbiddenList) {
		SharePrefUtil.saveString(context, Content.KEY_TOPIC_FORBIDDEN_LIST, forbiddenList);
	}
	
	public static String getUserPostId(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_POST_ID, "");
	}
	public static void setUserPostId(Context context, String userId) {
		SharePrefUtil.saveString(context, Content.KEY_USER_POST_ID, userId);
	}
	public static String getUserTopicId(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_TOPIC_ID, "");
	}
	public static void setUserTopicId(Context context, String userId) {
		SharePrefUtil.saveString(context, Content.KEY_USER_TOPIC_ID, userId);
	}
	
	public static String getEMUserId(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_EM_USER_ID, "");
	}
	public static void setEMUserId(Context context, String userid) {
		SharePrefUtil.saveString(context, Content.KEY_EM_USER_ID, userid);
	}
	
	public static String getUserID(Context context) {
		return SharePrefUtil.getString(context, Content.KEY_USER_ID, "");
	}
	public static void setUserID(Context context, String userId) {
		SharePrefUtil.saveString(context, Content.KEY_USER_ID, userId);
	}
	
	public static long getSavePicTimeByPart(Context context, int part){
		long result = 0;
		String key = null;
		switch(part){
		case AnalyzeType.FOREHEAD:
			key = Content.KEY_FOREHEAD;
			break;
		case AnalyzeType.CHEEK:
			key = Content.KEY_CHEEK;
			break;
		case AnalyzeType.EYE:
			key = Content.KEY_EYE;
			break;
		case AnalyzeType.NOSE:
			key = Content.KEY_NOSE;
			break;
		case AnalyzeType.HAND:
			key = Content.KEY_HAND;
			break;
		}
		if(key != null){
			result = SharePrefUtil.getLong(context, key, 0);
		}
		return result;
	}
	
	public static void setSavePicTimeByPart(Context context, int part, long time){
		String key = null;
		switch(part){
		case AnalyzeType.FOREHEAD:
			key = Content.KEY_FOREHEAD;
			break;
		case AnalyzeType.CHEEK:
			key = Content.KEY_CHEEK;
			break;
		case AnalyzeType.EYE:
			key = Content.KEY_EYE;
			break;
		case AnalyzeType.NOSE:
			key = Content.KEY_NOSE;
			break;
		case AnalyzeType.HAND:
			key = Content.KEY_HAND;
			break;
		}
		if(key != null){
			SharePrefUtil.saveLong(context, key, time);
		}
	}
	
	public static void setWeathData(Context context, Weather weather){
		if(context == null || weather == null) return;
		String sold = SharePrefUtil.getString(context, Content.KEY_WEATHER, null);
		if(TextUtils.isEmpty(sold) || !weather.equalString(sold)){
			SharePrefUtil.saveString(context, Content.KEY_WEATHER, weather.toString());
			context.sendBroadcast(new Intent(WeatherImpl.ACTION_WEATHER_CHANGED));
			Slog.i("Weather Data Changed : "+weather.toString());
		}
	}
	
	public static Weather getWeathData(Context context){
		String data = SharePrefUtil.getString(context, Content.KEY_WEATHER, null);
		if(TextUtils.isEmpty(data)) return null;
		Weather mWeather = new Weather();
		if(mWeather.parseFromString(data)){
			return mWeather;
		}else{
			Slog.e("Error Parse Weather Data : "+data);
		}
		return null;
	}
	
	/**
	 * 保存电池状态
	 * @param context
	 * @param status 0:正常 1:低电警告 2:低电关闭
	 */
	public static void setBatteryStatus(Context context, int status){
		if(context == null) return;
		SharePrefUtil.saveInt(context, Content.KEY_BATT_STATUS, status);
	}
	
	/**
	 * 获取电池状态
	 * @param context
	 * @return 0:正常 1:低电警告 2:低电关闭
	 */
	public static int getbatteryStatus(Context context){
		return SharePrefUtil.getInt(context, Content.KEY_BATT_STATUS, 0);
	}
	
	public static void setAppUpdateFlag(Context context, boolean needupdate){
		if(context == null) return;
		SharePrefUtil.saveBoolean(context, Content.KEY_APP_UPDATE, needupdate);
	}
	
	public static boolean isAppNeedUpate(Context context){
		return SharePrefUtil.getBoolean(context, Content.KEY_APP_UPDATE, false);
	}
}
