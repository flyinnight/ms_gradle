package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

public class WifiPreModePacket extends BodyPacket {
	
	private int state_length = 1;
	private int state_value;
	
    public WifiPreModePacket(int status){
    	this.state_value = status;
    	
    }
	
	@Override
	protected int getBodyLength() {
		return 2;
	}

	@Override
	protected byte[] getBodyBytes() {
		int length = 2;
		byte date [] = new  byte[length];
		ByteBuffer buff = ByteBuffer.allocate(2);
		 byte block_data = (byte) state_length;
		 buff.put(block_data);
		 block_data = (byte)state_value;
		 buff.put(block_data);
		 buff.position(0);
		 buff.limit(length);
		 buff.get(date, 0,length);
		return date;
	}

	public int getState_length() {
		return state_length;
	}

	public void setState_length(int state_length) {
		this.state_length = state_length;
	}
	
	public void setState_length(byte state_length) {
		this.state_length = (int)state_length;
	}

	public int getState_value() {
		return state_value;
	}

	public void setState_value(int state_value) {
		this.state_value = state_value;
	}
	public void setState_value(byte state_value) {
		this.state_value = (int)state_value;
	}
	


}
