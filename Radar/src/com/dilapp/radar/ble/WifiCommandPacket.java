package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

import android.text.TextUtils;

public class WifiCommandPacket extends BodyPacket {

	private int mode_length;	
	private int mode_value;	
	private int ssid_length;
	private String ssid_value;
	private int pwd_length;
	private String pwd_value;
	private int ip_length;
	private String ip_value;
	private int erroeCode_length;
	private int erroeCode_value;
	
	
	private int mCommand;
	private boolean phoneSend = false;   //phone -> device
	
	public WifiCommandPacket(int command){
		this.mCommand = command;
	}
	
	public WifiCommandPacket(int command,byte[]data){
		mCommand = command;
		
		int length = data.length;
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(0);
		buff.limit(length);
		byte temp = buff.get();
		setMode_length(temp);
		if(temp > 0){
			temp = buff.get();
			setMode_value(temp);
		}
		
		temp = buff.get();
		setSsid_length(temp);
		byte[] temps = null;
		if(temp > 0){
			temps = new byte[getSsid_length()];
			buff.get(temps, 0, getSsid_length());
			setSsid_value(temps);
		}else{
			temps = null;
			setSsid_value(temps);
		}
		
		
//		temp = buff.get();
//		setPwd_length(temp);
//		if(temp > 0){
//			temps = new byte[getPwd_length()];
//			buff.get(temps, 0, getPwd_length());
//			setSsid_value(temps);
//		}else{
//			temps = null;
//			setSsid_value(temps);
//		}
		
		
		temp = buff.get();
		setIp_length(temp);
		if(temp > 0){
			temps = new byte[getIp_length()];
			buff.get(temps, 0, getIp_length());
			setIp_value(temps);
		}else{
			temps = null;
			setIp_value(temps);
		}
		
		
		temp = buff.get();
		setErroeCode_length(temp);
		//temps = new byte[getErroeCode_length()];
		if(temp > 0){
			temp = buff.get();
			setErroeCode_value(temp);
		}else{
			setErroeCode_value((byte)BleUtils.ERROR_CODE_IDLE);
		}
		
		
	}
	public void setSSidAndPwd(String ssid,String pwd){
		this.ssid_value = ssid;
		this.pwd_value = pwd;
		this.ssid_length = TextUtils.isEmpty(ssid) ? 0 : ssid.getBytes().length;
		this.pwd_length = TextUtils.isEmpty(pwd) ? 0 : pwd.getBytes().length;
	}
 	

	
	@Override
	protected int getBodyLength() {
		int length = 0;
      if(mCommand == BleContent.cmd_staCommand){	
    	  length = 2+ssid_length+pwd_length;
      }
	return length;
	}
	
	
	
	@Override
	protected byte[] getBodyBytes() {
		byte date[] = null;
		if(mCommand == BleContent.cmd_staCommand){
			int length = getBodyLength();
			date = new  byte[length];
			ByteBuffer buff = ByteBuffer.allocate(getBodyLength());
			 byte block_data = (byte) ssid_length;
			 buff.put(block_data);
			 if(ssid_length > 0){
				 buff.put(ssid_value.getBytes());
			 }
			 block_data = (byte) pwd_length;
			 buff.put(block_data);
			 if(pwd_length > 0){
				 buff.put(pwd_value.getBytes());
			 }
			 buff.position(0);
			 buff.limit(length);
			 buff.get(date, 0,length);
			
		}
		return date;
	}

	
	

	public int getMode_length() {
		return mode_length;
	}

	public void setMode_length(int mode_length) {
		this.mode_length = mode_length;
	}

	public void setMode_length(byte mode_length) {
		this.mode_length = (int)mode_length;
	}
	public int getMode_value() {
		return mode_value;
	}

	public void setMode_value(int mode_value) {
		this.mode_value = mode_value;
	}
	public void setMode_value(byte mode_value) {
		this.mode_value = (int)mode_value;
	}
	public int getSsid_length() {
		return ssid_length;
	}

	public void setSsid_length(int ssid_length) {
		this.ssid_length = ssid_length;
	}
	public void setSsid_length(byte ssid_length) {
		this.ssid_length = (int) ssid_length;
	}

	public String getSsid_value() {
		return ssid_value;
	}

	public void setSsid_value(String ssid_value) {
		this.ssid_value = ssid_value;
	}
	
	public void setSsid_value(byte[] ssid_value) {
		if(ssid_value == null){
			this.ssid_value = null;
		}else{
			this.ssid_value = new String(ssid_value);
		}
	}

	public int getPwd_length() {
		return pwd_length;
	}

	public void setPwd_length(int pwd_length) {
		this.pwd_length = pwd_length;
	}
	
	public void setPwd_length(byte pwd_length) {
		this.pwd_length = (int) pwd_length;
	}

	public String getPwd_value() {
		return pwd_value;
	}

	public void setPwd_value(String pwd_value) {
		this.pwd_value = pwd_value;
	}
	
	public void setPwd_value(byte[] pwd_value) {
		if(pwd_value == null){
			this.pwd_value = null;
		}else{
			this.pwd_value = new String(pwd_value);
		}
	}

	public int getIp_length() {
		return ip_length;
	}

	public void setIp_length(int ip_length) {
		this.ip_length = ip_length;
	}

	public String getIp_value() {
		return ip_value;
	}

	public void setIp_value(String ip_value) {
		this.ip_value = ip_value;
	}
	
	public void setIp_value(byte[] ip_value) {
		if(ip_value == null){
			this.ip_value = null;
		}else{
			this.ip_value ="";
			for(int i=0;i<ip_value.length;i++){
				int ivalue = (int)(ip_value[i] & 0xFF);
				if(i != ip_value.length - 1){
					this.ip_value += ""+ivalue+".";
				}else{
					this.ip_value += ""+ivalue;
				}
			}
		}
		//this.ip_value = ip_value;
		//TODO  (4个字节高地位组合)
	}

	public int getErroeCode_length() {
		return erroeCode_length;
	}

	public void setErroeCode_length(int erroeCode_length) {
		this.erroeCode_length = erroeCode_length;
	}

	public void setErroeCode_length(byte erroeCode_length) {
		this.erroeCode_length = (int)erroeCode_length;
	}
	
	public int getErroeCode_value() {
		return erroeCode_value;
	}

	public void setErroeCode_value(byte erroeCode_value) {
		this.erroeCode_value = (int)erroeCode_value;
	}


}
