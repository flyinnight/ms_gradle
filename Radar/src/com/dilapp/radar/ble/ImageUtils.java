package com.dilapp.radar.ble;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @description 图片工具类
 */
public class ImageUtils {

	/**
	 * 缩放图片
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// 首先设置 inJustDecodeBounds=true 来获取图片尺寸
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// 计算 inSampleSize 的值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// 根据计算出的 inSampleSize 来解码图片生成Bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 计算缩放系数
	 * 
	 * @param options
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 原始图片的宽高
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static String getImagePath(String remoteUrl) {
		String imageName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1,
				remoteUrl.length());
		String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
		EMLog.d("msg", "image path:" + path);
		return path;

	}

	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName = thumbRemoteUrl.substring(
				thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path = PathUtil.getInstance().getImagePath() + "/" + "th"
				+ thumbImageName;
		EMLog.d("msg", "thum image path:" + path);
		return path;
	}

}
