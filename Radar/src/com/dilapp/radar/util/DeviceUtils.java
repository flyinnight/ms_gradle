package com.dilapp.radar.util;

import android.content.Context;
import android.os.Vibrator;
import android.util.DisplayMetrics;

public class DeviceUtils {
	/**
	 * �õ���ǰ�豸�ķֱ���
	 * 
	 * @param context
	 * @return int[0]=Width int[1]=Height
	 */
	public static int[] getScreenWidthAndHeight(Context context) {
		int[] mWidthAndHeight = null;
		if (context != null) {
			mWidthAndHeight = new int[2];
			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			mWidthAndHeight[0] = dm.widthPixels;
			mWidthAndHeight[1] = dm.heightPixels;
			return mWidthAndHeight;
		}
		return mWidthAndHeight;
	}

	/**
	 * 
	 * @param index
	 * @param context
	 */
	public static void getDeviceVibrator(Context context) {
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 400, 100 }; // 停止 开启 停止 开启
		vibrator.vibrate(pattern, -1);
		try {
			Thread.sleep(1000);
			if (vibrator == null)
				return;
			vibrator.cancel();
			vibrator = null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void DeviceStopVibrator(Vibrator vibrator) {

	}
}
