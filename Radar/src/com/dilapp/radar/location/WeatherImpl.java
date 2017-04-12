package com.dilapp.radar.location;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.text.TextUtils;

import com.dilapp.radar.R;
import com.dilapp.radar.util.Slog;

public class WeatherImpl {
	
	
	public static final String ACTION_WEATHER_CHANGED = "action_radar_weather_changed";
	
	private static final String API_KEY = "9da317cbe16685f2922186fd017d42db";
	
	private static final String httpUrlId = "http://apis.baidu.com/apistore/weatherservice/cityid";
	private static final String httpUrlName = "http://apis.baidu.com/apistore/weatherservice/cityname";
	private static final String httpUrlPinyin = "http://apis.baidu.com/apistore/weatherservice/weather";
	private static final String httpArgId = "cityid=";
	private static final String httpArgName = "cityname=";
	private static final String httpArgPinyin = "citypinyin=";
	
	private static WeatherCallback mCallback;
//	String jsonResult = request(httpUrl, httpArg);
	
	public static void startRequestWeatherById(final String cityid, WeatherCallback callback){
		mCallback = callback;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = request(httpUrlId, httpArgId+cityid);
				if(result != null){
					handleJson(result);
				}
			}
		}).start();
	}
	
	public static int getWeatherIconByName(String name){
		int result = -1;
		if(TextUtils.isEmpty(name)) return result;
		for(int i=0;i<mWeatherTypeList.length;i++){
			if(mWeatherTypeList[i].equals(name)){
				result = mWeatherIconList[i];
				break;
			}
		}
		return result;
	}
	
	public static int getWeatherIconByCode(int code){
		int result = -1;
		for(int i=0;i<mWeatherCodeList.length;i++){
			if(mWeatherCodeList[i] == code){
				result = mWeatherIconList[i];
				break;
			}
		}
		return result;
	}
	
	public static String getWeatherIconUrlByCode(int code){
		String result = null;
		result = "http://files.heweather.com/cond_icon/"+code+".png";
		return result;
	}
	
	public static int getWeatherCodeByName(String name){
		int result = -1;
		if(TextUtils.isEmpty(name)) return result;
		for(int i=0;i<mWeatherTypeList.length;i++){
			if(mWeatherTypeList[i].equals(name)){
				result = mWeatherCodeList[i];
				break;
			}
		}
		return result;
	}
	
	private static void handleJson(String json){
		try{
			JSONObject object = new JSONObject(json);
			int errNum = object.getInt("errNum");
			if(errNum == 0){
				JSONObject jdata = object.getJSONObject("retData");
				if(jdata != null){
					Weather mWeather = new Weather();
					mWeather.setCity(jdata.getString("city"));
					mWeather.setCityCode(jdata.getString("citycode"));
					mWeather.setDate(jdata.getString("date"));
					mWeather.setTime(jdata.getString("time"));
					String weather = jdata.getString("weather");
					mWeather.setWeather(weather);
					mWeather.setWeatherCode(getWeatherCodeByName(weather));
					Slog.i("Get Weather Data : "+mWeather.toString());
					if(mCallback != null){
						mCallback.onWeatherResult(0, mWeather);
						mCallback = null;
					}
				}else{
					if(mCallback != null){
						mCallback.onWeatherResult(-2, null);
						mCallback = null;
					}
				}
			}else{
				if(mCallback != null){
					mCallback.onWeatherResult(-1, null);
					mCallback = null;
				}
			}
		}catch(Exception e){
			Slog.e("Can not get Weather Json!!!");
		}
	}
	
	private static String request(String httpUrl, String httpArg) {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    httpUrl = httpUrl + "?" + httpArg;

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("apikey", API_KEY);
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
//	        Slog.i("weather result : "+result);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
	private static String[] mWeatherTypeList = new String[]{
		"晴","多云","少云","晴间多云","阴",//5
		"有风","平静","微风","和风","清风",//10
		"强风/劲风","疾风","大风","烈风","风暴",//15
		"狂爆风","飓风","龙卷风","热带风暴","阵雨",//20
		"强阵雨","雷阵雨","强雷阵雨","雷阵雨伴有冰雹","小雨",//25
		"中雨","大雨","极端降雨","毛毛雨/细雨","暴雨",//30
		"大暴雨","特大暴雨","冻雨","小雪","中雪",//35
		"大雪","暴雪","雨夹雪","雨雪天气","阵雨夹雪",//40
		"阵雪","薄雾","雾","霾","扬沙",//45
		"浮尘","火山灰","沙尘暴","强沙尘暴","热",//50
		"冷","未知"//52
	};
	
	private static int[] mWeatherCodeList = new int[]{
		100,101,102,103,104,
		200,201,202,203,204,
		205,206,207,208,209,
		210,211,212,213,300,
		301,302,303,304,305,
		306,307,308,309,310,
		311,312,313,400,401,
		402,403,404,405,406,
		407,500,501,502,503,
		504,506,507,508,900,
		901,999
	};
	
	private static int[] mWeatherIconList = new int[]{
		//0
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//5
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//10
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//15
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//20
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//25
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//30
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//35
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//40
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//45
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//50
		R.drawable.img_weater_lightrain,
		R.drawable.img_weater_lightrain,
		//52
	};

}
