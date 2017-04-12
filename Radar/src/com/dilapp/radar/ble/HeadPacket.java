package com.dilapp.radar.ble;

public class HeadPacket {
	public static final int Head_packet_Max = 32;
	
	private int magicCode  = 0xaa; 
	private int packet_direction = 0;
	private int command ;
	private int total_pack;
	private int curr_pack;
	private int data_block_number;
	
	
	
	public int getMagicCode() {
		return magicCode;
	}
	public void setMagicCode(int magicCode) {
		this.magicCode = magicCode;
	}
	public void setMagicCode(byte magicCode) {
		//this.magicCode = CommonUtil.byteArrayToInt(magicCode);
		this.magicCode = (int) magicCode;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public void setCommand(byte command) {
		//this.command = CommonUtil.byteArrayToInt(command);
		this.command =  (command & 0xF0) >> 4;
	}
	
	
	public int getPacket_direction() {
		return packet_direction;
	}
	public void setPacket_direction(int packet_direction) {
		this.packet_direction = packet_direction;
	}
	
	public void setPacket_direction(byte  packet_direction) {
		//this.packet_direction = CommonUtil.byteArrayToInt(packet_direction);
		this.packet_direction =  (command & 0x0F) ;
	}
	public int getTotal_pack() {
		return total_pack;
	}
	public void setTotal_pack(int total_pack) {
		this.total_pack = total_pack;
	}
	public void setTotal_pack(byte total_pack) {
		//this.total_pack =  CommonUtil.byteArrayToInt(total_pack);
		this.total_pack = (total_pack & 0xF0) >> 4;
	}
	
	public int getCurr_pack() {
		return curr_pack;
	}
	public void setCurr_pack(int curr_pack) {
		this.curr_pack = curr_pack;
	}
	public void setCurr_pack(byte curr_pack) {
		//this.curr_pack = CommonUtil.byteArrayToInt(curr_pack);
		this.curr_pack = (curr_pack & 0x0F) ;
	}
	
	public int getData_block_number() {
		return data_block_number;
	}
	public void setData_block_number(int data_block_number) {
		this.data_block_number = data_block_number;
	}
	
	public void setData_block_number(byte data_block_number) {
		this.data_block_number = (int) data_block_number ;
	}
	
	public  int getHeadPacketMax() {
		return Head_packet_Max;
	}
	public int getCommand() {
		return command;
	}
	
	
}
