package com.dilapp.radar.ui.skintest;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.imageanalysis.ImageProcess;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.update.UpdateTestDataImpl;
import com.dilapp.radar.util.Slog;

/**
 * Created by husj1 on 2015/6/9.
 */
public class ActivityTakingConfirm extends BaseActivity implements
		View.OnClickListener {

	private final static int REQ_RESULT = 10;

	private TitleView mTitle;

	private ImageView iv_epidermis;
	private ImageView iv_genuine;
	private TextView tv_epidermis;
	private TextView tv_genuine;
	private View vg_no_skin;
	private View vg_skin;
	private View btn_retaking;
	private TextView tv_skin_text;
	private TextView btn_result;

	private String epidermisPath;// 表皮图片路径
	private String genuinePath;// 真皮图片路径
	private String resultActivity;// 结果按钮的跳转位置
	private int currIndex;// 在整个逻辑中的当前位置
	private int choosedPart;// 选择的部位

	private boolean isAnalysed;// 图片是否分析完成
	private boolean isClickResult;// 是否在检测出结果前点击了“查看结果”按钮
	private TestSkinReq analysData;// 分析后的数据
	private boolean isTest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taking_confirm);
		// Context context = getApplicationContext();
		ZBackgroundHelper.setBackgroundForActivity(this, ZBackgroundHelper.TYPE_BLACK_BLUR);

		Intent data = getIntent();
		isTest		  = data.getBooleanExtra(Constants.EXTRA_CHOOSE_PART_IS_TEST, false);
		epidermisPath = data.getStringExtra(Constants.EXTRA_CONFIRM_TAKING_EPIDERMIS_PATH);
		genuinePath	  = data.getStringExtra(Constants.EXTRA_CONFIRM_TAKING_GENUINE_PATH);
		choosedPart	  = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CHOOSED_PART, 0);
		currIndex	  = data.getIntExtra(Constants.EXTRA_SKIN_TAKING_CURRENT_INDEX, 0);

		mTitle = new TitleView(this, findViewById(TitleView.ID_TITLE));
		mTitle.setCenterText(R.string.confirm_title, null);
		mTitle.setLeftIcon(R.drawable.btn_close_white, this);
		mTitle.setBackgroundColor(getResources().getColor(R.color.test_title_color));

		iv_epidermis = findViewById_(R.id.iv_epidermis);
		iv_genuine	 = findViewById_(R.id.iv_genuine);
		tv_epidermis = findViewById_(R.id.tv_epidermis);
		tv_genuine	 = findViewById_(R.id.tv_genuine);
		vg_no_skin	 = findViewById_(R.id.vg_no_skin);
		vg_skin		 = findViewById_(R.id.vg_skin);
		btn_retaking = findViewById_(R.id.btn_retaking);
		tv_skin_text = findViewById_(R.id.tv_skin_text);
		btn_result	 = findViewById_(R.id.btn_result);

		// 设置标题
		String text	  = data.getStringExtra(Constants.EXTRA_SKIN_TAKING_TEXT_INFO);
		tv_skin_text.setText
			(getString(R.string.taking_title, text == null ? "No Skin Text" : text));

		// 设置“查看结果”按钮下一个跳转的界面
		String[] activities	 = data.getStringArrayExtra(Constants.EXTRA_SKIN_TAKING_RESULT_ACTIVITIES);
		if (activities != null && currIndex < activities.length && currIndex >= 0) {
			resultActivity = activities[currIndex];
		}
		// 设置“查看结果”按钮的文本
		String[] buttonTexts = data.getStringArrayExtra(Constants.EXTRA_SKIN_TAKING_RESULT_BUTTON_TEXTS);
		if (buttonTexts != null && currIndex < buttonTexts.length && currIndex >= 0) {
			if (null != buttonTexts[currIndex]) {
				btn_result.setText(buttonTexts[currIndex]);
			}
		}
		data.putExtra(Constants.EXTRA_SKIN_TAKING_CURRENT_INDEX, 1 + currIndex);

		// 将皮肤图片显示到控件上
		StringBuilder errMsg = new StringBuilder();
		if (epidermisPath != null && !"".equals(epidermisPath.trim())) {
			iv_epidermis.setImageBitmap(getBitmapForFile(epidermisPath));
		} else {
			errMsg.append("epidermis ");
		}

		if (genuinePath != null && !"".equals(genuinePath.trim())) {
			iv_genuine.setImageBitmap(getBitmapForFile(genuinePath));
		} else {
			errMsg.append("genuine ");
		}
		if (errMsg.length() != 0) {
			errMsg.append("not found");
			Toast.makeText(this, errMsg.toString(), Toast.LENGTH_SHORT).show();
		}

		// skin是皮肤图片结果
		int skin = data.getIntExtra(Constants.EXTRA_CONFIRM_TAKING_IS_SKIN_IMG, 0);
		boolean isSkin = skin == Constants.SKIN_OK;
		skinImageChange(isSkin);
		if(isSkin) {
			new AnalysSkinAsyncTask().execute(epidermisPath, genuinePath);
		}
//		new CheckSkinAsyncTask().execute(data
//				.getStringExtra(Constants.EXTRA_CONFIRM_TAKING_EPIDERMIS_PATH));
		test();
	}

	private void test() {
		if (!isTest) {
			return;
		}
		btn_retaking.setOnLongClickListener(new View.OnLongClickListener() {
			private boolean isSkinImage;
			@Override
			public boolean onLongClick(View v) {
				isSkinImage = !isSkinImage;
				skinImageChange(isSkinImage);
				return true;
			}
		});
	}
	
	

	@Override
    public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
        super.onBackPressed();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.btn_retaking:
			setResult(RESULT_FIRST_USER);
			finish();
			break;
		case R.id.btn_result:
			//add by kfir
			Slog.i("start save test picture by part!!!! ");
			UpdateTestDataImpl.getInstance(getApplicationContext()).startSaveTestPicByPart(Constants.getPartByStringID(choosedPart));
			if(isAnalysed) {
				startNextActivity();
			} else {
				isClickResult = true;
				AsyncTask<?, ?, ?> task = null;
				showWaitingDialog(task);
			}

			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQ_RESULT == requestCode) {
			Log.i("III", "ATakingConfirm req " + requestCode + ", result " + resultCode);
			switch (resultCode) {
			case RESULT_OK:
				setResult(resultCode);
				finish();
				break;
			case RESULT_CANCELED:
				setResult(resultCode);
				finish();
				break;
			}
		}
	}

	private int convertServerPart(int localPartId) {
		switch (localPartId) {
		case Constants.PART_FOREHEAD:
			return AnalyzeType.FOREHEAD;
		case Constants.PART_EYE:
			return AnalyzeType.EYE;
		case Constants.PART_NOSE:
			return AnalyzeType.NOSE;
		case Constants.PART_CHEEK:
			return AnalyzeType.CHEEK;
		case Constants.PART_HAND:
			return AnalyzeType.HAND;
		default:
			return 0;
		}
	}

	private void startNextActivity() {
		if (resultActivity != null) {
			try {
				Class<?> clazz = Class.forName(resultActivity);
				// Log.i("III", "name " + clazz.getName());
				Intent data = getIntent();
				String key = Constants.EXTRA_TAKING_RESULT(choosedPart);
				Intent intent = new Intent(this, clazz);
				intent.putExtras(data.getExtras());
				if(data.hasExtra(key)) {
					Serializable exists = data.getSerializableExtra(key);
					ArrayList<Serializable> list = null;
					if(exists instanceof ArrayList) {
						list = (ArrayList<Serializable>) exists;
					} else {
						list = new ArrayList<Serializable>(2);
						list.add(exists);
					}
					list.add(analysData);
					intent.putExtra(key, list);
				} else {
					intent.putExtra(key, analysData);
				}
				startActivityForResult(intent, REQ_RESULT);
				setResult(RESULT_OK);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void skinImageChange(boolean isSkinImage) {

		if (isSkinImage) {
			btn_result.setEnabled(true);
			vg_skin.setVisibility(View.VISIBLE);
			vg_no_skin.setVisibility(View.INVISIBLE);
			tv_epidermis.setVisibility(View.VISIBLE);
			tv_genuine.setVisibility(View.VISIBLE);
		} else {
			btn_result.setEnabled(false);
			vg_skin.setVisibility(View.INVISIBLE);
			vg_no_skin.setVisibility(View.VISIBLE);
			tv_epidermis.setVisibility(View.INVISIBLE);
			tv_genuine.setVisibility(View.INVISIBLE);
		}
	}

	private Bitmap getBitmapForFile(String path) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	// Check Skin不在这个界面做了，所以这个没用到，先留着吧
	class CheckSkinAsyncTask extends AsyncTask<String, Object, String> {

		@Override
		protected String doInBackground(String... params) {
			ImageProcess ip = ImageProcess.getInstance();
			return ip.checkImg(params[0]);
		}

		@Override
		protected void onPostExecute(String msg) {
			/*
			 * SKIN_OK SKIN_IMAGE_ERROR SKIN_INPUT_NOT_SKIN SKIN_OUT_FOCUS
			 */
			Log.i("III", "->check skin " + msg);
			if (msg != null && msg.contains("SKIN_OK")) {
				skinImageChange(true);
				new AnalysSkinAsyncTask().execute(epidermisPath, genuinePath);
			} else {
				skinImageChange(false);
			}
		}
	}

	class AnalysSkinAsyncTask extends AsyncTask<String, Object, TestSkinReq> {

		@Override
		protected TestSkinReq doInBackground(String... params) {
			String rgb = params[0];
			String pl = params[1];
			TestSkinReq bean = new TestSkinReq();
			try{
				float[] result = ImageProcess.getInstance().runSkinImageAnalysis(
						rgb, pl);
				bean.setWater((int) (result[getResources().getInteger(
						R.integer.test_water_index)] * 100f));
				bean.setOil((int) (result[getResources().getInteger(
						R.integer.test_oil_index)] * 100f));
				bean.setElastic((int) (result[getResources().getInteger(
						R.integer.test_eastic_index)] * 100f));
				bean.setSensitive(100 - (int) (result[getResources().getInteger(
						R.integer.test_sensitive_index)] * 100f));
				bean.setWhitening(100 - (int) (result[getResources().getInteger(
						R.integer.test_whitening_index)] * 100f));
				bean.setPore(100 - (int) (result[getResources().getInteger(
						R.integer.test_pore_index)] * 100f));
				bean.setSkinAge((int) (result[getResources().getInteger(
						R.integer.test_skinage_index)]));
				bean.setPart(Constants.getPartByStringID(choosedPart));
				bean.setAnalyzeTime(System.currentTimeMillis());
				bean.setUid(SharePreCacheHelper.getUserID(getApplicationContext()));
			}catch(Exception e){
				Slog.e("runSkinImageAnalysis Error!!!!");
			}
			
			return bean;
		}

		@Override
		protected void onPostExecute(TestSkinReq result) {
			isAnalysed = true;
			analysData = result;
			if(isClickResult) {
				dimessWaitingDialog();
				startNextActivity();
			}
		}
	}
}
