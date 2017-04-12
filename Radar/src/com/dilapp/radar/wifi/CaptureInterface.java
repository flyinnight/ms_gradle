package com.dilapp.radar.wifi;

public interface CaptureInterface {

	/**
	 * 
	 * @param mId  0 rgb 1,
	 * @param status 0 Doing 1 done, 2 fail
	 */
	public void onCaptureStatus(int mId, int status);
	
}
