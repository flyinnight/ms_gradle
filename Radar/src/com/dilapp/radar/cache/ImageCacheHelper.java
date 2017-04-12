package com.dilapp.radar.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.BitmapGlobalConfig;
/**
 * 
 * @author hj
 * @time 2015-03-17
 *
 */
public class ImageCacheHelper {
	
	private static BitmapUtils bitmapUtils = null;
	private ImageCacheHelper cacheHelper = null;
	
	private static final String diskCachePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/radar"+"/.imagecache/";
	private static final  float memoryCachePercent = 0.1f;
	private static final  int diskCacheSize = 0;
	
	private ImageCacheHelper (){
		
	}
	public  synchronized ImageCacheHelper getInstance (Context context ){
		if(cacheHelper == null){
			cacheHelper = new ImageCacheHelper();
		}
		
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		setBitmapConfig();
		return cacheHelper;
	}

	private void setBitmapConfig(){
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		bitmapUtils.configMemoryCacheEnabled(true);
        bitmapUtils.configDiskCacheEnabled(true);
	}
	/**
	 * 
	 * @param context
	 * @param resId
	 */
	public  void setDefaultDisplayImage(Context context ,int resId){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		bitmapUtils.configDefaultLoadingImage(resId);
	}
	
	public  void setDefaultDisplayImage(Context context,Bitmap bitmap){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		bitmapUtils.configDefaultLoadingImage(bitmap);
		
	}
	
	public  void setDefaultDisplayImage(Context context,Drawable drawable){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		 bitmapUtils.configDefaultLoadingImage(drawable);
		
	}
	
	public  void setDefaultLoadFailedImage(Context context ,int resId){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		
		bitmapUtils.configDefaultLoadFailedImage(resId);
	}
	
	
	public void setDefaultLoadFailedImage(Context context ,Bitmap bitmap){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		
		bitmapUtils.configDefaultLoadFailedImage(bitmap);
	}
	
	public void setDefaultLoadFailedImage(Context context ,Drawable drawable){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		
		bitmapUtils.configDefaultLoadFailedImage(drawable);
	}
	
	/**
	 * 
	 * @param context
	 * @param maxWidth
	 * @param maxHeight
	 */
	public  void setDefaultBitmapMaxSize(Context context ,int maxWidth,int maxHeight){
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		bitmapUtils.configDefaultBitmapMaxSize(maxHeight,maxHeight);
	}
	/**
	 *  defaultExpiry is 30 days
	 * @param context
	 * @param defaultExpiry
	 */
	public void configDefaultCacheExpiry(Context context,long defaultExpiry) {
		if(bitmapUtils == null){
			bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		bitmapUtils.configDefaultCacheExpiry(defaultExpiry);
	}
	
	/**
	 *  display image
	 * @param context
	 * @param container
	 * @param uri
	 */
	public  <T extends View> void display(Context context,T container,String uri){
		if(bitmapUtils == null){
		  bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
		}
		
		bitmapUtils.display(container, uri);
	}
	/**
	 * clear image cache
	 * @param context
	 */
	public void clearCache(Context context){
		if(bitmapUtils == null){
			 bitmapUtils = new BitmapUtils(context,diskCachePath,memoryCachePercent,diskCacheSize);
			}
		bitmapUtils.clearCache();
	}
	
	/**
	 * 
	 * @return diskCachePath
	 */
	public static String getDiskCachePath(){
		return diskCachePath;
	}
	
	
	

}
