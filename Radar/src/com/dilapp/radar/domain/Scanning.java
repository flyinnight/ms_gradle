package com.dilapp.radar.domain;


public abstract class Scanning {
	public abstract void ScanAnalyzeAsync(ScanReq bean, BaseCall<ScanResp> call);

	public static class ScanReq extends BaseReq {

	}

	public static class ScanResp extends BaseResp {

	}
}
