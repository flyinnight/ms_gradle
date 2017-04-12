package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

public class EnvParamsPacket extends BodyPacket{
	
	private int mCommand;
	
	private byte tempLength;
	private byte mTemp;
	
	private byte rhLength;
	private byte mRH;
	
	private byte uvLength;
	private byte mUV;
	
	public EnvParamsPacket(int command, byte[]data){
		mCommand = command;
		int length = data.length;
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(0);
		buff.limit(length);
		
		tempLength = buff.get();
		mTemp = buff.get();
		
		rhLength = buff.get();
		mRH = buff.get();
		
		uvLength = buff.get();
		mUV = buff.get();
	}

	@Override
	protected int getBodyLength() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	protected byte[] getBodyBytes() {
		// TODO Auto-generated method stub
		byte[] bodyData = new byte[6];
		 ByteBuffer buff = ByteBuffer.allocate(6);
		 byte block_data = (byte) tempLength;
		 buff.put(block_data);
		 block_data = (byte) mTemp;
		 buff.put(block_data);
		 
		 block_data = (byte) rhLength;
		 buff.put(block_data);
		 block_data = (byte) mRH;
		 buff.put(block_data);
		 
		 block_data = (byte) uvLength;
		 buff.put(block_data);
		 block_data = (byte) mUV;
		 buff.put(block_data);
		 
		 buff.position(0);
		 buff.get(bodyData, 0,6);
		return bodyData;
	}

	public int getmCommand() {
		return mCommand;
	}

	public void setmCommand(int mCommand) {
		this.mCommand = mCommand;
	}

	public byte getmTemp() {
		return mTemp;
	}

	public void setmTemp(byte mTemp) {
		this.mTemp = mTemp;
	}

	public byte getmRH() {
		return mRH;
	}

	public void setmRH(byte mRH) {
		this.mRH = mRH;
	}

	public byte getmUV() {
		return mUV;
	}

	public void setmUV(byte mUV) {
		this.mUV = mUV;
	}
	
	

}
