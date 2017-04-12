package com.dilapp.radar.ble;

public class WifiStatusPacket extends BodyPacket {

	private int mode;	
	private String ssid;
	private String pwd;
	private String ip;
	
	
	
	
	
	
	
	@Override
	protected int getBodyLength() {
		return 0;
	}

	@Override
	protected byte[] getBodyBytes() {
		return null;
	}

}
