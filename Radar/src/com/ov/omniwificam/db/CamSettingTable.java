package com.ov.omniwificam.db;

public class CamSettingTable {
	public int fps;
	public int bitrate;
	public int resolution;//0:QVGA 1:VGA 2:720P 3:360;
	public int brightness;
	public int contrast;
	public int saturation;
	public int mirror;
	public int infrared;
	
	public CamSettingTable(int fps, int bitrate, int resolution,
			int brightness, int contrast, int saturation, int mirror,
			int infrared) {
		super();
		this.fps = fps;
		this.bitrate = bitrate;
		this.resolution = resolution;
		this.brightness = brightness;
		this.contrast = contrast;
		this.saturation = saturation;
		this.mirror = mirror;
		this.infrared = infrared;
	}

	public CamSettingTable(){
		
	}

}
