package com.dilapp.radar.wifi;

public interface NetStatusInterface {

	/**
	 * 
	 * @param status -1 failed 0 idle 1 success
	 */
	public void onNetStatusChanged(int status);


	
}
