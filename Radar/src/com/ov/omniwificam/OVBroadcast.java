package com.ov.omniwificam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
//包名必须是这个,jni
public class OVBroadcast extends Service {

	WifiManager.MulticastLock lock;

	private native void nativeBroadcastStart2();

	private native void nativeBroadcastStop2();

	private native void nativeSetBroadcastIP(long ip);
	

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("hj", "Service bind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("hj", "Service unbind");
		super.onUnbind(intent);
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hj", "Service create");
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		lock = manager.createMulticastLock("test wifi");
		lock.acquire();
		//wifiHelper = WifiHelper.getInstance(getApplicationContext());
		// check mobile phone ap mode
	/*	if (isMobileApMode()) {
			InetAddress mobileAddress = null;
			byte[] bdAddress = null;
			mobileAddress = getMobileIPAdress();
			if (mobileAddress != null) {
				bdAddress = getMobileApBroadcastAdress(mobileAddress);
			}

			if (bdAddress != null) {
				// set broadcast address
				long nIP = convertIP2Int(bdAddress);
				Log.d("ov780wifi",
						"mobile phone ap mode broadcast ip:"
								+ Long.toString(nIP));
				nativeSetBroadcastIP(nIP);
			}
		}*/
        new Thread(new Runnable() {
			@Override
			public void run() {
				nativeBroadcastStart2();
			}
		}).start();
       
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("ov780wifi", "Service start");
		super.onStart(intent, startId);
		
	}

	@Override
	public void onDestroy() {
		Log.d("ov780wifi", "Service destroy");

		nativeBroadcastStop2();
		lock.release();
		//wifiHelper.unRegister(getApplicationContext());
		super.onDestroy();
	}

	static {
		System.loadLibrary("ov780wifi");
	}

	public boolean isMobileApMode() {
		boolean bApOn = false;
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
		for (Method method : wmMethods) {
			if (method.getName().equals("isWifiApEnabled")) {
				try {
					if ((Boolean) method.invoke(wifiManager)) {
						bApOn = true;
						Log.d("ov780wifi", "mobile phone open ap mode");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bApOn;
	}

	public InetAddress getMobileIPAdress() {
		try {
			InetAddress inetAddress = null;
			InetAddress mobileAddress = null;
			for (Enumeration<NetworkInterface> networkInterface = NetworkInterface
					.getNetworkInterfaces(); networkInterface.hasMoreElements();) {
				NetworkInterface sNetInterface = networkInterface.nextElement();
				for (Enumeration<InetAddress> ipAddresses = sNetInterface
						.getInetAddresses(); ipAddresses.hasMoreElements();) {
					inetAddress = ipAddresses.nextElement();
					// if(!inetAddress.isLoopbackAddress() &&
					// (sNetInterface.getDisplayName().contains("wlan0") ||
					// sNetInterface.getDisplayName().contains("eth0")))
					if (!inetAddress.isLoopbackAddress()) {
						mobileAddress = inetAddress;
						Log.d("ov780wifi", "moblie phone address:"
								+ mobileAddress);
					}
				}
			}
			return mobileAddress;

		} catch (SocketException ex) {
			Log.e("ov780wifi", ex.toString());
		}

		return null;
	}

	public byte[] getMobileApBroadcastAdress(InetAddress mbAddress) {
		if (mbAddress == null) {
			return null;
		}

		byte[] ipByte = mbAddress.getAddress();
		ipByte[ipByte.length - 1] = (byte) 255;
		return ipByte;
	}

	public long convertIP2Int(byte[] ipByte) {
		long a, b, c, d;
		a = byte2int(ipByte[0]);
		b = byte2int(ipByte[1]);
		c = byte2int(ipByte[2]);
		d = byte2int(ipByte[3]);
		long result = (a << 24) | (b << 16) | (c << 8) | d;

		return result;
	}

	public static int byte2int(byte b) {
		return b & 0xff;
	}
	
	

}
