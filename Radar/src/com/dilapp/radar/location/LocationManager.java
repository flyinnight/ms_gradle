package com.dilapp.radar.location;


import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.util.LocalCity;

import android.content.Context;
import android.text.TextUtils;

public class LocationManager implements WeatherCallback{
	
	private static LocationManager mSelf;
	private boolean hasStart = false;
	private long lastStartTime = 0;
	private static final long START_GAP = 1000;
	private Context mContext;
	
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();

	/**
	 * 必须在主线程中调用
	 * @param context
	 * @return
	 */
	public synchronized static LocationManager getInstance(Context context){
		if(mSelf == null){
			mSelf = new LocationManager(context.getApplicationContext());
		}
		return mSelf;
	}
	
	public synchronized void start(){
		if(!hasStart || (System.currentTimeMillis() - lastStartTime) > (START_GAP + 1000)){
			hasStart = true;
			lastStartTime = System.currentTimeMillis();
			mLocationClient.start();
		}
	}
	
	private LocationManager(Context context){
		mContext = context;
		mLocationClient = new LocationClient(context);     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );
	    initLocation();
	}
	
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=(int) START_GAP;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
	
	private void startGetWeatherByCityName(String city){
		if(TextUtils.isEmpty(city)) return;
		if (city.contains("市") || city.contains("省")) { 
            city = city.substring(0, city.length() - 1);  
        }
		WeatherImpl.startRequestWeatherById(LocalCity.getCityIdByName(city), this);
	}
	
	public class MyLocationListener implements BDLocationListener {
		 
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
        	if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
        		Address mAddress = location.getAddress();
            if(mAddress != null){
            		startGetWeatherByCityName(mAddress.city);
            }
        }
        		
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());// 单位：公里每小时
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\nheight : ");
//                sb.append(location.getAltitude());// 单位：米
//                sb.append("\ndirection : ");
//                sb.append(location.getDirection());// 单位度
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//                sb.append("\ndescribe : ");
//                sb.append("gps定位成功");
// 
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
//                sb.append("\naddr : ");
//                Address mAddress = location.getAddress();
//                if(mAddress != null){
//                		sb.append(mAddress.city +" "+mAddress.cityCode);
//                }
//                
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());
//                sb.append("\ndescribe : ");
//                sb.append("网络定位成功");
//            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                sb.append("\ndescribe : ");
//                sb.append("离线定位成功，离线定位结果也是有效的");
//            } else if (location.getLocType() == BDLocation.TypeServerError) {
//                sb.append("\ndescribe : ");
//                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                sb.append("\ndescribe : ");
//                sb.append("网络不同导致定位失败，请检查网络是否通畅");
//            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                sb.append("\ndescribe : ");
//                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//            }
//            sb.append("\nlocationdescribe : ");
//            sb.append(location.getLocationDescribe());// 位置语义化信息
//                List<Poi> list = location.getPoiList();// POI数据
//                if (list != null) {
//                    sb.append("\npoilist size = : ");
//                    sb.append(list.size());
//                    for (Poi p : list) {
//                        sb.append("\npoi= : ");
//                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//                    }
//                }
//            Slog.i("BaiduLocationApiDem:  "+ sb.toString());
            mLocationClient.stop();
            hasStart = false;
        }
	}

	@Override
	public void onWeatherResult(int errorCode, Weather weather) {
		// TODO Auto-generated method stub
		if(errorCode == 0 && weather != null){
			SharePreCacheHelper.setWeathData(mContext, weather);
		}
	}
}
