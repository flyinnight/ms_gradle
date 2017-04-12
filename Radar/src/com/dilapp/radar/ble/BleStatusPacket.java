package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

public class BleStatusPacket extends BodyPacket {
	
	private int linkStatus_length;
	private int linkStatus_value;
	
	private int powerStatus_length;
	private int powerStatus_value;
	
	private boolean mIsCallback = false;
	
	public BleStatusPacket(boolean isCallback){
		mIsCallback = isCallback;
	}
	
	public BleStatusPacket(boolean isCallback,byte[]data){
		mIsCallback = isCallback;
		int length = data.length;
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(0);
		buff.limit(length);
		byte temp = buff.get();
		setLinkStatus_length(temp);
		temp = buff.get();
		setLinkStatus_value(temp);
		temp = buff.get();
		setPowerStatus_length(temp);
		temp = buff.get();
		setPowerStatus_value(temp);
		
	}
	
	@Override
	protected int getBodyLength() {
		return 4;
	}

	@Override
	protected byte[] getBodyBytes() {
		 byte[] bodyData = new byte[4];
		 ByteBuffer buff = ByteBuffer.allocate(4);
		 byte block_data = (byte) linkStatus_length;
		 buff.put(block_data);
		 block_data = (byte) linkStatus_value;
		 buff.put(block_data);
		 
		 block_data = (byte) powerStatus_length;
		 buff.put(block_data);
		 block_data = (byte) powerStatus_value;
		 buff.put(block_data);
		 
		 
		 buff.position(0);
		 buff.get(bodyData, 0,4);
		
		return null;
	}

	public int getLinkStatus_length() {
		return linkStatus_length;
	}

	public void setLinkStatus_length(int linkStatus_length) {
		this.linkStatus_length = linkStatus_length;
	}

	public void setLinkStatus_length(byte linkStatus_length) {
		this.linkStatus_length = (int)linkStatus_length;
	}
	
	public int getLinkStatus_value() {
		return linkStatus_value;
	}

	public void setLinkStatus_value(int linkStatus_value) {
		this.linkStatus_value = linkStatus_value;
	}
	
	public void setLinkStatus_value(byte linkStatus_value) {
		this.linkStatus_value = (int)linkStatus_value;
	}

	public int getPowerStatus_length() {
		return powerStatus_length;
	}

	public void setPowerStatus_length(int powerStatus_length) {
		this.powerStatus_length = powerStatus_length;
	}
	
	public void setPowerStatus_length(byte powerStatus_length) {
		this.powerStatus_length = (int)powerStatus_length;
	}

	public int getPowerStatus_value() {
		return powerStatus_value;
	}

	public void setPowerStatus_value(int powerStatus_value) {
		this.powerStatus_value = powerStatus_value;
	}
	public void setPowerStatus_value(byte powerStatus_value) {
		this.powerStatus_value = (int)powerStatus_value;
	}



	public boolean ismIsCallback() {
		return mIsCallback;
	}



	public void setmIsCallback(boolean mIsCallback) {
		this.mIsCallback = mIsCallback;
	}
	
	
}
