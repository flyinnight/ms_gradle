package com.dilapp.radar.application;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.ui.skintest.ZBackgroundHelper;
import com.dilapp.radar.util.EMChatUtils;
import com.dilapp.radar.util.EMChatUtils;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ov.omniwificam.OVBroadcast;
import com.ov.omniwificam.OVWIFICamJNI;

public class RadarApplication extends Application {
	public static Context context;
	public static RadarApplication radarApplication = null;
	private static OVWIFICamJNI mOVJNI = null;
	private AllKfirManager allInfoHelper = null;
	private ActivityManager activityManager = null;

	@Override
	public void onCreate() {
		Slog.w("RadarApplication onCreate!!!!");
		super.onCreate();

		int pid = android.os.Process.myPid();
		String processAppName = EMChatUtils.getAppName(this, pid);
		if (processAppName == null
				|| !processAppName.equalsIgnoreCase("com.dilapp.radar")) {
			Slog.e("This is not the UI Process and return : " + processAppName);
			return;
		}

		/* 需要放在注册之前，获取本地存储的token */
		HttpConstant.TOKEN = SharePreCacheHelper.getUserToken(this);
		startLoadLibary();
		radarApplication = this;
		startBindServer();
		init();
		initImageLoader(this);
		ZBackgroundHelper.initBackground(this, null);
		EMChatUtils.init(this);
	}

	private void init() {
		if (Build.VERSION.SDK_INT >= com.dilapp.radar.ui.Constants.SKIN_TEST_MIN_SDK) {
			allInfoHelper = AllKfirManager.getInstance(getApplicationContext());
			allInfoHelper.registerBroadcast(getApplicationContext());
			activityManager = ActivityManager
					.getActivityManagerIntance(getApplicationContext());
		}
	}

	public OVWIFICamJNI getJNI() {
		if (mOVJNI == null) {
			mOVJNI = new OVWIFICamJNI();
		}
		return mOVJNI;
	}

	public static RadarApplication getInstance() {
		return radarApplication;
	}

	private void startBindServer() {
		RadarProxy radarProxy = RadarProxy.getInstance(getApplicationContext());
		radarProxy.bindServer();
	}

	private void startLoadLibary() {
		Intent intentService = new Intent(getApplicationContext(),
				OVBroadcast.class);
		intentService.putExtra("alertmode", "0");
		startService(intentService);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	public void onDestory() {

		allInfoHelper = null;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				PathUtils.IMAGE_LOAD_CACHE);
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		config.memoryCacheExtraOptions(1000, 1000);// 即保存的每个缓存文件的最大长宽
		config.discCache(new UnlimitedDiskCache(cacheDir));
		config.discCacheFileCount(100);
		config.imageDownloader(new BaseImageDownloader(context, 5 * 1000,
				30 * 1000));
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}
