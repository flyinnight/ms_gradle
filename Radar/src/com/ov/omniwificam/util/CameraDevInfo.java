package com.ov.omniwificam.util;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLSurfaceView;

import com.ov.omniwificam.Vout;

//包名必须是这个,jni

public class CameraDevInfo {
	public byte devicename[] = new byte[16];
	public String ipAddr;
	//public int idx; //idx for NDK API. not the same as in devinfo[] order.
	public long mac;
	public int ip;
	public int port;
	public byte status;
	public byte type;
	public int remote;  //网络的类型
	public byte login_en; // 0:open 1:need login
	public byte login_user[] = new byte[16];
	public byte login_pw[] = new byte[32];

	public int info_ready = -1; //if 1, nativeRetrieveCameraData() is already called.
	
	public int fwver;//firmware version

	//only can be access after nativeRetrieveCameraData();
	// add for stun end
	public int avol;
	public int online;
	// added for multiview
	public boolean IsOnRender = false;
	public int RenderIdx = -1;
	// for stun
	public byte data[] = new byte[36];
	// can record on camera ?
	public int camera_record_en = 0;

	public byte userflag[] = new byte[40];
	// -1: not available, -2: need up
	public int newcarctrl = 0;

	/* for CameraList Activity */
	public boolean selected = false; //selected in CameraList Activity

	/* for SingleView/MultiView activity, keep the decoder status and so on */
	public Vout Vout;
	public GLSurfaceView GLView;

	//camera setting from broadcast package
	public int has_audio;
	public int audio_imaqt;
	public int has_audio_out;
	public int audio_duplex;
	public int savefile;
	public int def_width;
	public int def_height;
	public int def_framerate;
	public int def_bitrate;
	public int def_flipmirror;

	public int res_index;
	public int frmrate_index;
	public int bitrate_index;
	public int zoom_index;
	public int flipmirror_cur;
	public int flipmirror_min;
	public int flipmirror_max;
	public int gain_cur;
	public int gain_min;
	public int gain_max;
	public int exposure_cur;
	public int exposure_min;
	public int exposure_max;
	
	public List<Integer> resList = new ArrayList<Integer>();
	public List<Integer> frmrateList = new ArrayList<Integer>();
	public List<Integer> bitrateList = new ArrayList<Integer>();
	
	public void initDefaultSetting(){
		res_index = 0;
		frmrate_index = 0;
		bitrate_index = 0;
		zoom_index = 0;
		
		flipmirror_cur = def_flipmirror;
		flipmirror_min = 0;
		flipmirror_max = 3;
		
		gain_cur = 32;
		gain_min = 0;
		gain_max = 255;
		
		exposure_cur = 736;
		exposure_min = 1;
		exposure_max = 15000;

		resList.add((def_width << 16) | def_height);
		frmrateList.add(def_framerate);
		def_bitrate = 3072;
		bitrateList.add(def_bitrate); //default 3072kbps

		if((def_width >= 1280) && (def_height < 720))
		{
			Vout.IsTriVga = true;
		}
	}
	
	public void frmrateList_InitDef()
	{
		frmrateList.clear();
		frmrateList.add(3);
		frmrateList.add(5);
		frmrateList.add(10);
		frmrateList.add(15);
		frmrateList.add(20);
		frmrateList.add(25);
		frmrateList.add(30);
		int i;
		for(i=0; i<frmrateList.size(); i++){
			if(frmrateList.get(i) == def_framerate){
				frmrate_index = i;
				return;
			}
		}
		frmrateList.add(def_framerate);
		frmrate_index = frmrateList.size()-1;
		return;
	}

	public void resList_InitDef()
	{
		resList.clear();
		resList.add((320 << 16) | 240);
		resList.add((640 << 16) | 480);
		resList.add((1280 << 16) | 720);
		int i;
		for(i=0; i<resList.size(); i++){
			if(resList.get(i) == ((def_width << 16) | def_height) ){
				res_index = i;
				return;
			}
		}
		resList.add( (def_width << 16) | def_height );
		res_index = resList.size()-1;
		return;
	}

	public void bitrateList_InitDef()
	{
		bitrateList.clear();
		bitrateList.add(128);
		bitrateList.add(256);
		bitrateList.add(512);
		bitrateList.add(768);
		bitrateList.add(1024);
		bitrateList.add(1536);
		bitrateList.add(2048);
		bitrateList.add(2560);
		bitrateList.add(3072);
		bitrateList.add(4096);
		int i;
		for(i=0; i<bitrateList.size(); i++){
			if(bitrateList.get(i) >= def_bitrate){
				bitrate_index = i;
				return;
			}
		}
		bitrateList.add(def_bitrate);
		bitrate_index = bitrateList.size()-1;
		return;
	}

}