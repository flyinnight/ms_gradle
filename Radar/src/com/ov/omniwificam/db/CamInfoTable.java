package com.ov.omniwificam.db;

public class CamInfoTable {
	public long mac;
	public byte devicename[] = new byte[16];
	public int fwver;
	public int remote;
	public int newcarctrl = 0;
	public int has_audio;
	public int max_width;
	public int max_height;
	public int camera_record_en = 0;
	public String username;
	public String password;
	
	public CamInfoTable(long mac, byte[] devicename, int fwver, int remote,
			int newcarctrl, int has_audio, int max_width, int max_height,
			int camera_record_en, String username, String password) {
		super();
		this.mac = mac;
		this.devicename = devicename;
		this.fwver = fwver;
		this.remote = remote;
		this.newcarctrl = newcarctrl;
		this.has_audio = has_audio;
		this.max_width = max_width;
		this.max_height = max_height;
		this.camera_record_en = camera_record_en;
		this.username = username;
		this.password = password;
	}

	public CamInfoTable(){
		
	}

}
