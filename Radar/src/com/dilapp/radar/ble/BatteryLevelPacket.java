package com.dilapp.radar.ble;

import java.nio.ByteBuffer;

public class BatteryLevelPacket extends BodyPacket{
	
	private int mCommand;
	private int levelLength;
	private int level;
	
	public BatteryLevelPacket(int command, byte[]data){
		mCommand = command;
		int length = data.length;
		ByteBuffer buff = ByteBuffer.wrap(data);
		buff.position(0);
		buff.limit(length);
		byte temp = buff.get();
		levelLength = (0xFF & temp);
		temp = buff.get();
		level = (0xFF & temp);
	}

	@Override
	protected int getBodyLength() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	protected byte[] getBodyBytes() {
		// TODO Auto-generated method stub
		byte[] bodyData = new byte[2];
		 ByteBuffer buff = ByteBuffer.allocate(2);
		 byte block_data = (byte) levelLength;
		 buff.put(block_data);
		 block_data = (byte) level;
		 buff.put(block_data);
		 buff.position(0);
		 buff.get(bodyData, 0,2);
		return bodyData;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getmCommand() {
		return mCommand;
	}

	public void setmCommand(int mCommand) {
		this.mCommand = mCommand;
	}

}
