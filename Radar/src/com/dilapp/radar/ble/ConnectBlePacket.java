package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

import com.dilapp.radar.util.Slog;

public class ConnectBlePacket extends BodyPacket {
	
	private int statusLength;
	private int status;
	
	public ConnectBlePacket(){
		
	}
	public ConnectBlePacket(byte[]data){
		int length = data.length;
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(0);
		buff.limit(length);
		byte temp = buff.get();
		setStatusLength(temp);
		temp = buff.get();
		setStatus(temp);
	}

	@Override
	protected int getBodyLength() {
		return 2;
	}

	@Override
	protected byte[] getBodyBytes() {
		 byte[] bodyData = new byte[2];
		 ByteBuffer buff = ByteBuffer.allocate(2);
		 byte block_data = (byte) statusLength;
		 buff.put(block_data);
		 block_data = (byte) status;
		 buff.put(block_data);
		 buff.position(0);
		 buff.get(bodyData, 0,2);
		return bodyData;
	}

	public int getStatusLength() {
		
		return statusLength;
	}

	public void setStatusLength(int statusLength) {
		this.statusLength = statusLength;
	}
	
	public void setStatusLength(byte statusLength) {
		this.statusLength = (int)statusLength;
	}
	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public void setStatus(byte status) {
		this.status = (int)status;
	}
	
}
