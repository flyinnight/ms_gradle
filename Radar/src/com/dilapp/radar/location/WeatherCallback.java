package com.dilapp.radar.location;

public interface WeatherCallback {
	
	public void onWeatherResult(int errorCode, Weather weather);

}
