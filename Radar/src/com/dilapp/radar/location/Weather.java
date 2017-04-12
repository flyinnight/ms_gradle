package com.dilapp.radar.location;

import android.text.TextUtils;

public class Weather {  
    private String city;
    private String cityCode;
    private String date;
    private String time;
    private String weather;//天气
    private int weatherCode;
    
    public String toString(){
    		return (""+city+";"+cityCode+";"+date+";"+time+";"+weather+";"+weatherCode);
    }
    
    public boolean parseFromString(String data){
    		if(TextUtils.isEmpty(data)) return false;
    		String[] list = data.split(";");
    		if(list == null || list.length != 6) return false;
    		this.city = list[0];
    		this.cityCode = list[1];
    		this.date = list[2];
    		this.time = list[3];
    		this.weather = list[4];
    		this.weatherCode = Integer.parseInt(list[5]);
    		return true;
    }
    
    public boolean equalString(String data){
    		if(TextUtils.isEmpty(data)) return false;
    		String src = toString();
    		if(src.equals(data)){
    			return true;
    		}
    		return false;
    }
  
    public String getCity() {  
        return city;  
    }  
  
    public void setCity(String city) {  
        this.city = city;  
    }  
  
    public String getCityCode() {
        return cityCode;  
    }  
  
    public void setCityCode(String cityCode) {  
        this.cityCode = cityCode;  
    } 
  
    public String getWeather() {  
        return weather;  
    }  
  
    public void setWeather(String weather) {  
        this.weather = weather;  
    }

	public int getWeatherCode() {
		return weatherCode;
	}

	public void setWeatherCode(int weatherCode) {
		this.weatherCode = weatherCode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	} 

} 
