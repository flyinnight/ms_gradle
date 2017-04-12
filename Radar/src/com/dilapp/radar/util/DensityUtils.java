package com.dilapp.radar.util;

import android.content.Context;

public class DensityUtils {

	/**
	 * dipת����px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * pxת����dp
	 * 
	 * @param context
	 * @param pxValue
	 * **/
	public static float px2dip(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
