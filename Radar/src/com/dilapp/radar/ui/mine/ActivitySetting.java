package com.dilapp.radar.ui.mine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.UpdateVersion;
import com.dilapp.radar.domain.UpdateVersion.UpdateVersionReq;
import com.dilapp.radar.domain.UpdateVersion.UpdateVersionResp;
import com.dilapp.radar.domain.impl.UpdateVersionImpl;
import com.dilapp.radar.server.RadarProxy;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.ui.BaseFragmentActivity;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.WelcomeLogin;
import com.dilapp.radar.ui.mine.FragmentMineList.MineGroup;
import com.dilapp.radar.ui.mine.FragmentMineList.MineItem;
import com.dilapp.radar.update.FTP;
import com.dilapp.radar.update.FtpUtils;
import com.dilapp.radar.update.FTP.DownLoadProgressListener;
import com.dilapp.radar.util.GsonUtil;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.MineInfoUtils;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class ActivitySetting extends BaseFragmentActivity implements
		OnClickListener, UmengUpdateListener{

	private TitleView mTitle;
	private FragmentMineList mFragmentMineList;
	private AlertDialog mNewDialog;
	private String mServerPath;
	private String mVersion;
	private static boolean isDownloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		View title = findViewById(R.id.vg_title);
		findViewById_(R.id.tv_logout).setOnClickListener(this);
		mTitle = new TitleView(getApplicationContext(), title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setCenterText(R.string.setting_title, null);

		mFragmentMineList = new FragmentMineList();
		mFragmentMineList.setGroups(genGroups());
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, mFragmentMineList, "mineList")
				.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case R.string.setting_about:
			// Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, ActivityAbout.class));
			break;
		case R.string.setting_update:
			// Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
			if(ReleaseUtils.UPDATE_FROM_UMENG){
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(this);
				UmengUpdateAgent.forceUpdate(this);
			}else{
				DownloadRadar();
			}
			break;
		case R.string.setting_help:
			Toast.makeText(this, v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.tv_logout:
			SharePreCacheHelper.setPassword(getApplicationContext(), "");
			MineInfoUtils.clearUserInfo(this);
			//
			/*Intent intent = new Intent(getApplicationContext(), WelcomeLogin.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			startActivity(intent);*/
			SharePreCacheHelper.setUserRole(getApplicationContext(), "");
			SharePreCacheHelper.setTopicOwnerList(getApplicationContext(), "");
			SharePreCacheHelper.setTopicAdminList(getApplicationContext(), "");
			SharePreCacheHelper.setTopicForbiddenList(getApplicationContext(), "");
			RadarProxy.getInstance(getApplicationContext()).startLocalData(HttpConstant.DELETE_ALL_LOCAL_SENDING_POST, null, null);
			
			setResult(RESULT_OK);
			finish();
			break;
		default:
			break;
		}

	}

	private List<MineGroup> genGroups() {
		OnClickListener l = this;
		List<MineGroup> groups = new ArrayList<FragmentMineList.MineGroup>(1);
		List<MineItem> items = new ArrayList<MineItem>(3);
		items.add(new MineItem(R.string.setting_about, 0,
				R.string.setting_about, false, false, l));
		boolean needUpdate = SharePreCacheHelper.isAppNeedUpate(this);
		MineItem updateItem = new MineItem(R.string.setting_update, 0,
				R.string.setting_update, false, false, l);
		if(needUpdate){
			updateItem.setDot(true);
		}
		items.add(updateItem);
		items.add(new MineItem(R.string.setting_help, 0, R.string.setting_help,
				false, false, l));
		groups.add(new MineGroup(true, true, true, items));
		return groups;
	}
	
	private void openApkFile(File apk) {
        // TODO Auto-generated method stub
		Slog.e("start install apk : "+apk.getAbsolutePath());
        Intent intent = new Intent();   
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),   
                        "application/vnd.android.package-archive");   
        startActivity(intent);
     } 
	
	private String getVersionName(){
		String result = null;
		try{
			PackageManager pm = this.getPackageManager();
			PackageInfo info = pm.getPackageInfo(this.getPackageName(), 0);
			result = info.versionName;
		}catch(Exception e){
			result = null;
		}
		return result;
	}
	
	private boolean isNewVersion(String curr, String target){
		if(TextUtils.isEmpty(curr) || TextUtils.isEmpty(target)){
			return false;
		}
		return (!curr.equals(target));
//		boolean result = false;
//		String[] lcurr = curr.split(".");
//		String[] ltarget = target.split(".");
//		
//		if(Integer.parseInt(ltarget[0]) > Integer.parseInt(lcurr[0])){
//		}
	}
	
	private void startDownload(){
		if(isDownloading){
			Slog.e("apk is downloading!!!!");
			return;
		}
		
		Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
			isDownloading = true;
			final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			final Notification notification = new Notification();
			 notification.icon = android.R.drawable.stat_sys_download;
			 notification.tickerText = "Radar-"+mVersion+".apk" + "开始下载";
			 notification.when = System.currentTimeMillis();
             notification.defaults = Notification.DEFAULT_LIGHTS;
             notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
             notification.setLatestEventInfo(getApplicationContext(), "Radar-"+mVersion+".apk", "0%", null);
             nm.notify(1, notification);
			FTP ftp = new FTP("114.215.181.127", 2222, "admin", "admin");

			try {
				ftp.downloadSingleFile(mServerPath, PathUtils.UPDATE_CACHE, "Radar-"+mVersion+".apk", new DownLoadProgressListener() {
					@Override
					public void onDownLoadProgress(String currentStep, long downProcess, File file) {
						if(FtpUtils.FTP_DOWN_SUCCESS.equals(currentStep)){
							nm.cancel(1);
							openApkFile(file);
						}else if(FtpUtils.FTP_CONNECT_FAIL.equals(currentStep)
								|| FtpUtils.FTP_FILE_NOTEXISTS.equals(currentStep)
								|| FtpUtils.FTP_DOWN_FAIL.equals(currentStep)){
							nm.cancel(1);
							Toast.makeText(ActivitySetting.this, "下载版本失败!!", Toast.LENGTH_SHORT).show();
						}else{
							Log.d("TAG", "onDownLoadProgress currentStep: " + currentStep + "---downProcess: " + downProcess);
							if(FtpUtils.FTP_DOWN_LOADING.equals(currentStep)){
								notification.setLatestEventInfo(getApplicationContext(), "Radar-"+mVersion+".apk", downProcess+"%", null);
								nm.notify(1, notification);
							}
							
						}
					}
				});
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				isDownloading = false;
				nm.cancel(1);
				e1.printStackTrace();
			}
			isDownloading = false;
		}
	});
	thread.start();
	}
	
    private void DownloadRadar() {
		Object updateVer = ReqFactory.buildInterface(this, UpdateVersion.class);
		UpdateVersionImpl updateVerImp = (UpdateVersionImpl) updateVer;
		UpdateVersionReq loginReq = new UpdateVersionReq();
		loginReq.setAppName("Radar");
		BaseCall<UpdateVersionResp> mBaseCallF = new BaseCall<UpdateVersionResp>(){
			@Override
			public void call(UpdateVersionResp obj) {
				Slog.d(obj.getStatus() + "****" + obj.getMessage());
				if(!obj.isRequestSuccess()) {
					Toast.makeText(ActivitySetting.this, "获取版本失败!!", Toast.LENGTH_SHORT).show();
					return;
				}
					
				mServerPath = obj.getUrl();
				mVersion = obj.getVersion();
				Slog.e("Version : "+mVersion+" currVersion : "+getVersionName()+"  path : "+mServerPath);
				boolean isNew = isNewVersion(mVersion, getVersionName());
						
				if(isNew){
					
					AlertDialog.Builder builder = new Builder(ActivitySetting.this);
					builder.setMessage("要下载新版本吗？");
					builder.setTitle("版本更新");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mNewDialog.dismiss();
							startDownload();
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mNewDialog.dismiss();
						}
					});
					mNewDialog = builder.create();
					mNewDialog.show();
				}else{
					Toast.makeText(ActivitySetting.this, "当前已是最新版本!!", Toast.LENGTH_SHORT).show();
				}
			}
		};
		addCallback(mBaseCallF);
		updateVerImp.getLatestVersionAsync(loginReq,mBaseCallF);
	}

	@Override
	public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
		// TODO Auto-generated method stub
		switch (updateStatus) {
        case UpdateStatus.Yes: // has update
        		Slog.d("有更新");
        		SharePreCacheHelper.setAppUpdateFlag(ActivitySetting.this, true);
            UmengUpdateAgent.showUpdateDialog(ActivitySetting.this, updateInfo);
            break;
        case UpdateStatus.No: // has no update
        		SharePreCacheHelper.setAppUpdateFlag(ActivitySetting.this, false);
        		Slog.d("没有更新");
        		Toast.makeText(ActivitySetting.this, "当前已是最新版本!!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(mContext, "没有更新", Toast.LENGTH_SHORT).show();
            break;
        case UpdateStatus.NoneWifi: // none wifi
        		Toast.makeText(ActivitySetting.this, "当前非WI-FI模式!!", Toast.LENGTH_SHORT).show();
        		
//            Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
            break;
        case UpdateStatus.Timeout: // time out
//        		Slog.d("超时");
//            Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
            break;
        }
	}
}
