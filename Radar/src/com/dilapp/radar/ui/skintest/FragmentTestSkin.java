package com.dilapp.radar.ui.skintest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.location.Weather;
import com.dilapp.radar.location.WeatherImpl;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.WelcomeLogin;
import com.dilapp.radar.ui.mine.DialogHeadChange;
import com.dilapp.radar.ui.skintest.BatteryHelper.BatteryListener;
import com.dilapp.radar.ui.skintest.BatteryHelper.STATUS;
import com.dilapp.radar.util.PathUtils;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.SerializableUtil;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.AllKfirManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import static com.dilapp.radar.textbuilder.utils.L.d;

public class FragmentTestSkin extends BaseFragment implements OnClickListener{

	//add by kfir
	private static final int REQUEST_CODE_PICK_IMAGE = 1;
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 2;
	private static final int REQUEST_CODE_PICK_RESULT = 3;
	private DialogHeadChange mHeadChangeDialog;
	
	private static final String IMAGE_FILE_LOCATION = PathUtils.RADAR_IMAGE_CACEH
			+ "radar_test_bg.jpg";
	private static final Uri imageUri = Uri.fromFile(new File(
			IMAGE_FILE_LOCATION));
	
	private static final String IMAGE_HEAD_PATH = PathUtils.RADAR_IMAGE_CACEH
			+ "radar_test_bg_cache.jpg";
	private static final Uri IMAGE_HEAD_CACHE = Uri.fromFile(new File(IMAGE_HEAD_PATH));
	
//	private BatteryHelper mBatteryHelper;
	private TitleNotify mTitleNotify;
	
    private TitleView title;
    private AllKfirManager allInfoManager;

    private TextView tv_weather;
    private TextView tv_temperature;
    private TextView tv_humidity;
    private TextView tv_uv;
//    private Drawable weather;
    private ImageView mWeather_Icon;
    private DisplayImageOptions options;
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BleUtils.ACTION_GET_ENV_PARAM.equals(action)){
				handleEnvParms();
			}
		}
	};
	
	private boolean hasRegister = false;

    @Override
    public void onCreateView(ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_testskin);
        setCacheView(true);
        getContentView().setBackground(
                ZBackgroundHelper.getDrawable(mContext, ZBackgroundHelper.TYPE_NORMAL));
        
        getContentView().setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				mHeadChangeDialog.show();
				return true;
			}
		});
        
        mHeadChangeDialog = new DialogHeadChange(getActivity());
		mHeadChangeDialog.setButtonsOnClickListener(this, this, this);

        View vg_title = findViewById(TitleView.ID_TITLE);
        title = new TitleView(mContext, vg_title);
        title.setLeftIcon(R.drawable.btn_history, this);
        title.setCenterText(R.string.test, null);
//        title.setRightIcon(R.drawable.btn_setting, this);
        title.setBackgroundColor(0x80000000);
        
//        mBatteryHelper = BatteryHelper.getInstance(getActivity());
		mTitleNotify = new TitleNotify(getActivity(), title);
//		mTitleNotify.setLowPowerDialog(getActivity(), true, null);
		mTitleNotify.setNotifyType(TitleNotify.NOTIFY_BATTERY_WARNING | TitleNotify.NOTIFY_BATTERY_LOW, 
				R.drawable.btn_setting, this);

        tv_weather = findViewById(R.id.tv_weather);
        mWeather_Icon = findViewById(R.id.weather_icon);
        Weather mWeather = SharePreCacheHelper.getWeathData(getActivity());
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.img_weather_sunny_00)
                // 正在加载的图片
        .showImageForEmptyUri(R.drawable.img_weather_sunny_00)
                // URL请求失败
        .showImageOnFail(R.drawable.img_weather_sunny_00)
                // 图片加载失败
        .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                // .displayer(new RoundedBitmapDisplayer(mContext.getResources().getDimensionPixelSize(R.dimen.topic_main_radius)))
        .displayer(new FadeInBitmapDisplayer(200))
        .imageScaleType(ImageScaleType.EXACTLY).build();
        if(mWeather != null){
        		tv_weather.setText(mWeather.getWeather()+"|"+mWeather.getCity());
        		ImageLoader.getInstance().displayImage(WeatherImpl.getWeatherIconUrlByCode(mWeather.getWeatherCode()), mWeather_Icon,options);
//        		weather = tv_weather.getCompoundDrawables()[0];
//            startAnimactions(weather.getCurrent());
        }
        
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_uv = findViewById(R.id.tv_uv);
        handleEnvParms();

        findViewById(R.id.v_daily).setOnClickListener(this);
        findViewById(R.id.v_skin).setOnClickListener(this);
        findViewById(R.id.v_microscope).setOnClickListener(this);
        findViewById(R.id.v_taste).setOnClickListener(this);

        test();
        if(!hasRegister){
        		IntentFilter mFilter = new IntentFilter(BleUtils.ACTION_GET_ENV_PARAM);
            getActivity().registerReceiver(mReceiver, mFilter);
            hasRegister = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void test() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        /*findViewById(R.id.v_taste).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                testTaste = !testTaste;
                Toast.makeText(mContext, (testTaste ? "开启" : "关闭"), Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
    }

    private void init() {
        allInfoManager = AllKfirManager.getInstance(getActivity());
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean haspaired = SharePreCacheHelper.getPairStatus(getActivity());
        if(haspaired && ReleaseUtils.CAUSE_END_AFTER_SKINTEST){
        		AllKfirManager.getInstance(getActivity()).endSkinTest();
        }
//        mBatteryHelper.onResume(this);
        if(mTitleNotify != null){
        		mTitleNotify.onResume();
        }
//        Slog.d("get Battery Status : "+mBatteryHelper.getBatteryStatus());
//    	allInfoManager.startSkinTest();
//    	allInfoManager.setSkinRunningFlag(false);  
    }
    
    @Override
    public void onPause(){
    	super.onPause();
//    	mBatteryHelper.onPause();
    	if(mTitleNotify != null){
    		mTitleNotify.onPause();
    	}
    }

    @Override
    public void onClick(View v) {
    		boolean needStart = false;//check if need start Skin test
        switch (v.getId()) {
            case TitleView.ID_LEFT:
                startActivity(new Intent(mContext, ActivityHistory.class));
                break;
            case TitleView.ID_RIGHT:
                startActivity(new Intent(mContext, ActivityDeviceInfo.class));
                break;

            case R.id.v_daily: {
                // Toast.makeText(mContext, getString(R.string.developing),
                // Toast.LENGTH_SHORT).show();
            	needStart = true;
            	Intent intent = new Intent(mContext, ActivityDailyChoosePart.class);
            	intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, Constants.TEST_PREVIEW);
                startActivity(intent);

                //startActivity(new Intent(mContext, ActivityNormal.class));
                // --connect(ActivityNormal.class);
                break;
            }
            case R.id.v_skin: {
        		needStart = true;
        		// 检查是否有肤质测试的结果
        		String fr = Constants.SKIN_TEST_RESULT_PATH(mContext, Constants.PART_FOREHEAD);
        		String cr = Constants.SKIN_TEST_RESULT_PATH(mContext, Constants.PART_CHEEK);
        		File ff = new File(fr);
        		File cf = new File(cr);
        		if(ff.isFile() && cf.isFile()) {
        			TestSkinReq fd = SerializableUtil.readSerializableObject(fr);
        			TestSkinReq cd = SerializableUtil.readSerializableObject(cr);
        			if(fd != null && cd != null) {
	        			needStart = false;
	        			Intent intent = new Intent(mContext, ActivitySkinResult.class);
	        			intent.putExtra("umeng", true);
	        			intent.putExtra(Constants.EXTRA_TAKING_RESULT(Constants.PART_FOREHEAD), fd);
	        			intent.putExtra(Constants.EXTRA_TAKING_RESULT(Constants.PART_CHEEK), cd);
	        			// intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, data.getPart());
	        			startActivity(intent);
        			} else {
        				ff.delete();
        				cf.delete();
        			}
        		} 
        		if(needStart) {
	        		Intent intent = new Intent(mContext, ActivitySkinChoosePart.class);
	            	intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, Constants.TEST_PREVIEW);
	                startActivity(intent);
        		}
                    // --  connect(ActivitySkinTest.class);
                    // startActivity(new Intent(mContext, ActivitySkinTest.class));
                break;
            }
            case R.id.v_microscope: {
                /*Toast toast = Toast.makeText(mContext, getString(R.string.developing), Toast.LENGTH_SHORT);
                ((TextView) toast.getView().findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                toast.show();*/
        		needStart = true;
                startActivity(new Intent(mContext, ActivityMicroscope.class));
                break;
            }
            case R.id.v_taste: {
        		needStart = true;
        		File tmpResult = new File(Constants.PRODUCT_TEST_TEMP_RESULT_PATH(mContext));
        		
                Intent intent = new Intent(mContext, ActivityTasteChoosePart.class);
            	intent.putExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, Constants.TEST_PREVIEW);
            	if(tmpResult.isFile()) {
            		TestSkinReq tmp = SerializableUtil.readSerializableObject(tmpResult.getAbsolutePath());
            		if(tmp != null) {
            			intent.putExtra(Constants.EXTRA_SKIN_TAKING_CURRENT_INDEX, 1);
            			intent.putExtra(Constants.EXTRA_TAKING_RESULT(Constants.getStringIDByPart(tmp.getPart())), tmp);
            			intent.putExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, Constants.getStringIDByPart(tmp.getPart()));
            			intent.putExtra("first", true);
            		} else {
            			tmpResult.delete();
            		}
            	}
                startActivity(intent);
                break;
            }
            case DialogHeadChange.ID_PHOTO:
				mHeadChangeDialog.dismiss();
				getImageFromAlbum();
				break;
			case DialogHeadChange.ID_TAKIN:
				mHeadChangeDialog.dismiss();
				getImageFromCamera();
				break;
			case DialogHeadChange.ID_CANCEL:
				mHeadChangeDialog.dismiss();
				break;
            default:
                break;
        }
        if(needStart){
        	AllKfirManager.getInstance(mContext).startSkinTest();
        }
    }

    private void startAnimactions(Drawable drawable) {
        if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }

    @Override
    public void onDestroy() {
//        if (Build.VERSION.SDK_INT >= Constants.SKIN_TEST_MIN_SDK) {
//            Slog.d("start endSkinTest!");
//            AllKfirManager.getInstance(mContext).endSkinTest();
//        }
        if(mTitleNotify != null){
        		mTitleNotify.onDestroy();
        }
        if(hasRegister){
        	getActivity().unregisterReceiver(mReceiver);
        	hasRegister = false;
        }
        super.onDestroy();
    }
    
    private void getImageFromAlbum() {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}

	private void getImageFromCamera() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent getImageByCamera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			// Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
			getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(getImageByCamera,
					REQUEST_CODE_CAPTURE_CAMEIA);
		} else {
			Toast.makeText(getActivity(), "请确认已经插入SD卡", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void cropImageUri(Uri uri, int requestCode) {
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		int outputX = wm.getDefaultDisplay().getWidth();
		int outputY = wm.getDefaultDisplay().getHeight();
		Slog.e("cropImageUri : "+outputX+"  "+outputY);
		Intent intent = new Intent("com.android.camera.action.CROP");
		Slog.w("cropImageUri Uri: " + uri.toString());
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String furl = PathUtils.getPath(getActivity(), uri);
			intent.setDataAndType(Uri.fromFile(new File(furl)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", outputX);
		intent.putExtra("aspectY", outputY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_HEAD_CACHE);
//		intent.putExtra("return-data", true);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);

	}
	
	private void handleEnvParms(){
		int mEnvParams = AllKfirManager.getInstance(getActivity()).getEnvParam();
		int itemp = ((mEnvParams >> 16) & 0xFF);
		int ihum = ((mEnvParams >> 8) & 0xFF);
		int iuv = ((mEnvParams) & 0xFF);
		tv_temperature.setText(ihum <= 0 ? "--":(""+itemp));
        tv_humidity.setText(ihum <= 0 ? "--":(""+ihum));
        tv_uv.setText(ihum <= 0 ? "--":(""+iuv));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			d("III", "not ok");
			return;
		}
		d("III", "ok");
    	switch (requestCode) {
		case REQUEST_CODE_CAPTURE_CAMEIA:
			cropImageUri(imageUri,REQUEST_CODE_PICK_RESULT);
			break;
		case REQUEST_CODE_PICK_IMAGE:
			if (data == null)
				return;
			cropImageUri(data.getData(),REQUEST_CODE_PICK_RESULT);
			break;
		case REQUEST_CODE_PICK_RESULT:
			if (data == null)
				return;
			Uri uri = data.getData();
			boolean isDone = false;
			if (uri == null) {
				// use bundle to get data
				Slog.d("use bundle to get Bitmap !!!!");
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
					// spath :生成图片取个名字和路径包含类型
					if (photo != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
						InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
						ZBackgroundHelper.setBackground(getActivity(), isBm, true);
						isDone = true;
					} else {
						try {
							ZBackgroundHelper.setBackground(getActivity(), IMAGE_HEAD_PATH);
							isDone = true;
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							Slog.e("FileNotFoundException : "+IMAGE_HEAD_PATH);
						}
					}
				} else {
					Slog.e("Can not get Photo Bitmap4 !!!!!");
				}
			} else {
				try {
					ZBackgroundHelper.setBackground(getActivity(), IMAGE_HEAD_PATH);
					isDone = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Slog.e("FileNotFoundException : "+IMAGE_HEAD_PATH);
				}
			}
			if(isDone){
				getContentView().setBackground(
		                ZBackgroundHelper.getDrawable(mContext, ZBackgroundHelper.TYPE_NORMAL));
			}
			break;
		}
    }

//	@Override
//	public void onBatteryStatusChanged(STATUS status) {
//		// TODO Auto-generated method stub
//		Slog.d("onBatteryStatusChanged : "+status);
//	}

    //    private void connect(Class<? extends Activity> classes){
//    	allInfoManager.startSkinTest();     //mode
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
////				allInfoManager.openBle();
//			    // allInfoManager.openWifi();
//			}
//		}).start();
//	
//		
//		checkBleStatus(classes);
//    }
//
//	private void checkBleStatus(Class<? extends Activity> classes) {
//		String defaultAddress = getSaveBleAddress();
//		if(TextUtils.isEmpty(defaultAddress)){
//			Log.i("hj","defaultAddress is null");
//			Intent intent = new Intent (getActivity(),ActivityScanBle.class);
//			startActivity(intent);
//			return;
//		}
//	    checkWifiStaus(classes);
//	}
//	private String getDefaultSSid() {
//		return SharePreCacheHelper.getDefaultSSid(getActivity());
//	}
//
//	private String getSaveBleAddress() {
//		return SharePreCacheHelper.getBleMacAddress(getActivity());
//		
//	}

//	private void checkWifiStaus(Class<? extends Activity> classes ) {                    	
//		Intent intent = null;                                 
//		String ssid = null;
//		allInfoManager.checkWifiStatus();
//		switch (AllInfoManager.getWifiState()) {
//		case none:
//		case disabled:                             
//		case enabled:
//		case enableding:
//		case ap_connected:
//		case ap_connecting:        //  Ap模式 
//			intentToSkinTestActivity(classes);
//			break;
//			
//		case connected_open:  // TODO 1.判断网速 2.发送 当前的ssid    //STA
//			intentToSkinTestActivity(classes);
//			break;
//		case connected_pw:                                     //STA
//			// 判断 ssid 可存在
//			LocalWifi localWifi = checkSSid();
//			if(localWifi != null  ){ // 存在
//				ssid = allInfoManager.getSSID();   
//				String pwd = localWifi.wifiPassword;
//			  intentToSkinTestActivity(classes);
//			}else{
//			    intent =  new Intent(getActivity(),ActivityWifiPassword.class);
//			    startActivity(intent);
//			}
//			break;
//	
//		default:
//			break;
//		}
//
//	}

//	private void intentToSkinTestActivity(Class<? extends Activity> classes){
//		allInfoManager.setAllInfoCallback(null);
//		Intent intent = new Intent(getActivity(),classes);
//		startActivity(intent);
//		getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
//		
//	}
//	
//	private LocalWifi checkSSid() {
//		String ssid = allInfoManager.getSSID();
//		String mac = allInfoManager.getMacAddress();
//		return  SharePreCacheHelper.checkSSid(getActivity(), mac, ssid);
//	}


}
