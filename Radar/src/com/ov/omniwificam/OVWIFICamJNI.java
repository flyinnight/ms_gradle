package com.ov.omniwificam;

import com.dilapp.radar.util.Slog;
import com.ov.omniwificam.db.CamInfoTable;
import com.ov.omniwificam.db.CamSettingTable;
import com.ov.omniwificam.util.CameraDevInfo;

import android.R.integer;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class OVWIFICamJNI {	
		//these functions are not static because JNI need the object to call the displayCallback.
		public native int  nativeGet(int frmrate);
		public native void nativeSetRes(int width, int height);
		public native void nativeSetRes2(int width, int height, int idx);
		public native void nativeSetFrmrate(int frmrate);
		public native void nativeSetFrmrate2(int frmrate, int idx);
		public native void nativeSetBitrate(int bitrate);
		public native void nativeSetBitrate2(int bitrate, int idx);
		public native void nativeSetAudioCodec(int level, int idx);
		public native void nativeSetZoom(int level);
		public native void nativeSetZoom2(int level, int idx);
		public native void nativeSetRecord2(int level, int idx);
		public native void nativeSetInfrared2(int level, int idx);
		public native void nativeSetBrightness(int level);
		public native void nativeSetBrightness2(int level, int idx);
		public native void nativeSetContrast(int level);
		public native void nativeSetContrast2(int level, int idx);
		public native void nativeSetSaturation(int level);
		public native void nativeSetSaturation2(int level, int idx);
		public native void nativeSetFlipmirror(int level);
		public native void nativeSetFlipmirror2(int level, int idx);		
		public native void nativeSetAECtrl2(int level, int idx);
		public native void nativeSetGain2(int level, int idx);			
		//must call nativeSetRes() before nativeInit()
		public native void nativeInit();
		public native void nativeInit2(int idx);
		public native void nativeStart(String ipaddr);
		public native void nativeStart2(int ip, int port, int remote, int idx, byte data[], byte devname[], int password);
		public native int nativeStartByMac(long mac, int ch_idx);
		public native void nativeStopCamera2(int idx);
		public native int nativeStopCameraDone2(int idx);
		public native void nativeExit();
		public native void nativeExit2(int idx);	
		public native int nativeCheckCameraOnline();
		public native void nativeNewstunUpdateOffset(int offset);
		public native void nativeSetCurrentIdx(int idx);
		public native String nativeGetString();
		public native String nativeGetString2(int idx);
		public native int nativeStringChanged();
		public native int nativeStringChanged2(int idx);

		public native int nativeAllinfoDone2(int idx);
		public native int nativeAllinfoGetCnt(int idx, int param);
		public native int nativeAllinfoGetDef(int idx, int param);
		public native int nativeAllinfoGetCur(int idx, int param);
		public native int nativeAllinfoGetList(int idx, int param, int offset);
		public native int nativeAllinfoGetEffect(int idx, int effect);
		//localsave related native API
		public native void localsaveinit2(int framerate, int idx);
		public native void localsavestart(String filename);
		public native void localsavestart2(String filename, int idx);
		public native void localsavestop();
		public native void localsavestop2(int idx);
		
		public native void nativeUDC2(byte[] usrdata, int length, int idx);
		public native int nativeSetNewCarCtrl(int idx, int y, int x);
		public native void nativeSetDuplexAudio(int idx, int value);
		
		public native int nativeSetStreamPw(int idx, int value);
		public native int nativeGetStreamPermit(int idx);
		
		public native int nativeGetCameraCnt();
		public native int nativeGetCameraInfo(CameraDevInfo dev);
		public native int nativeGetCameraInfoByMac(CameraDevInfo dev, long mac);
		public native int nativeRetrieveCameraData(long mac, String user, String pw);
		public native int nativeCameraUpdated();
		public native int nativeBroadcastUpdateStart();
		public native int nativeBroadcastUpdateStop();

		public native void nativeSetPicShift(int shift, int idx);
		public native int  nativeGetPicShift(int idx);
		
		//database
		public native int nativeInitDatabase(String dbPath);
		public native int nativeInsertCamInfoTable(CamInfoTable camInfoTable);//0: success 1: fail
		public native int nativeQueryCamInfoTable(long mac, CamInfoTable camInfoTable);
		public native int nativeInsertCamSettingTable(long mac, CamSettingTable camSettingTable);//0: success 1: fail
		public native int nativeQueryCamSettingTable(long mac, CamSettingTable camSettingTable);
		
		//audio
		public native int nativeAudioEncInit(int format);
		public native void nativeAudioEnc(int idx, byte[] inbuf, int len, int format);
		public native void nativeAudioDecodeInit(int format);
		
		//image compress
		public native void nativeGetVBuffer(int idx, byte[] data, int pos, int w, int h);
		
		//led ctrl
		public native void nativeSetLEDCtrl(int idx, int value);		
		public native int nativeGenJavaCallBack(Object obj);
		
		//check network connection
		public native int nativelinklost2(int idx);
		
		//capture
		public native void nativeSetCapture(int idx, String filename, int len);
		public native int nativeCaptureDone(int idx);
		public native void nativeRestoreVideo(int idx);
		public native int nativeSetCaptureCallback(Object obj);
		
		//video
		public native void nativeSetVideo(int idx);
		public native void nativeCloseVideo(int idx);
		//camer status
		public native int nativeSetNetStatusCallback(Object obj);
		
		
		public static Aout mAout = null;
		public static Vout[] mVout = new Vout[4];
		public static GLSurfaceView[] mGLView = new GLSurfaceView[4];
		public static boolean mAudioInited = false;
		
		/**
		 * Open the Java audio output.
		 * This function is called by the native code
		 */
		public void initAout(int sampleRateInHz, int channels, int samples, int idx)
		{
			if(mAout == null)
				mAout = new Aout();
			
			Log.d("ov780wifi", "Opening the java audio output");
			if(!mAudioInited){
				Log.d("ov780wifi", "audio init");
				mAout.init(sampleRateInHz, channels, samples);
				mAudioInited=true;
			}
			else
				Log.d("ov780wifi", "audio inited, do nothing");
		}

		/**
		 * Play an audio buffer taken from the native code
		 * This function is called by the native code
		 */
		public void playAudio(byte[] audioData, int bufferSize, int nbSamples, int idx)
		{
//			Log.d("ov780wifi", "playaudio in Java");
			//if(idx==mRenderManager.audioIdx)
				mAout.playBuffer(audioData, bufferSize, nbSamples);
		}

		/**
		 * Close the Java audio output
		 * This function is called by the native code
		 */
		public void closeAout(int idx)
		{
			Log.d("ov780wifi", "Closing the java audio output");
			if(mAudioInited){
				Log.d("ov780wifi", "audio deinit");
				mAout.release();
				mAudioInited=false;
			}
			else
				Log.d("ov780wifi", "more than one are rendering, do nothing");
		}
		
		/**
		 * When start showing video and audio
		 * must init surface and render
		 */
		public void initVout(GLSurfaceView surface, Vout render, int idx)
		{
			mVout[idx] = null;
			mGLView[idx] = null;
			
			mVout[idx] = render;
			mGLView[idx] = surface;					
		}

		
		/**
		 * set vout size
		 * This function is called by the native code
		 */
		public void setVoutSize(int frameWidth, int frameHeight, int idx)
		{
			mVout[idx].frameWidth = frameWidth;
			mVout[idx].frameHeight = frameHeight;
			mVout[idx].mustInit = true;
		}

		/**
		 * OPENGL call back
		 * This function is called by the native code
		 */
		public void GL10displayCallback(byte[] image, int idx)
		{
			//request GL to render
			if(mVout[idx] != null && mGLView[idx] != null){
				mVout[idx].image = image;
				mVout[idx].hasReceivedFrame = true;
				mGLView[idx].requestRender();
			}else{
				Slog.e("Vout or GLView is NULLL!!!");
			}
		}

		/* set all the decoding param */
		public void sendDevSetting(CameraDevInfo dev, int idx)
		{
			nativeInit2(idx);

			int res_cur = dev.resList.get(dev.res_index);
			int width = (res_cur >> 16) & 0xffff;
			int height = res_cur & 0xffff;
			nativeSetRes2(width, height, idx);

			int framerate = dev.frmrateList.get(dev.frmrate_index);
			nativeSetFrmrate2(framerate, idx);

			int bitrate = dev.bitrateList.get(dev.bitrate_index);
			nativeSetBitrate2(bitrate, idx);

			if(dev.has_audio != 0){
				nativeSetAudioCodec(dev.audio_imaqt, idx);
			}
		}
		
		public void usr_ack_handler_cb(byte[] usrdata, int length)
		{
			Log.d("ov780wifi", "user ack handler called");
			//Log.d("ov780wifi", String.format("Ack data length = %d",length)) ;
		}
}
