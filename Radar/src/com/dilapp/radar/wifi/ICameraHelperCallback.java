package com.dilapp.radar.wifi;

public interface ICameraHelperCallback {
	
	
	public void startStatus(StartStatus startStatus);
	
	public void endStatus(EndStatus endStatus);
	
	public void sdCardStatus(SdCardStatus sdCardstatus);

	
	
	public enum StartStatus{none,success,failed}
	
	
	public enum EndStatus{none,success,failed}
	
	
	public enum SdCardStatus{none,notready,full}
	

}
