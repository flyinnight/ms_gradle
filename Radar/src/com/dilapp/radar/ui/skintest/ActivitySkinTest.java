package com.dilapp.radar.ui.skintest;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinResp;
import com.dilapp.radar.ui.ActivityHelper.StopStateable;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.ContextState.State;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.skintest.CameraVideoHelper.OnTakeResultListener;
import com.dilapp.radar.view.AnimationListenerAdapter;
import com.dilapp.radar.view.RelativeRadioGroup;
import com.dilapp.radar.view.SlidingButton;
import com.dilapp.radar.wifi.Content;

/**
 * Created by husj1 on 2015/4/22.
 */
public class ActivitySkinTest extends BaseActivity implements
		View.OnClickListener,
		com.dilapp.radar.view.RelativeRadioGroup.OnCheckedChangeListener,
		CompoundButton.OnCheckedChangeListener {

	private final static int REQ_TEST = 10;

	private TitleView mTitle;
	private SelectPartsView mParts;
	private SlidingButton sw_switch;
	private TextView tv_btn;
	private TextView tv_need;
	private ImageView iv_cartoon;
	private GLSurfaceView sv_video;

	private CameraVideoHelper mVideoHelper;
	private Animation mCartoomShow, mCartoomHide;

	private State mException = new ExceptionState();// 以下的过程出了意外
	private State mBefore = new BeforeState();// 在以下操作执行之前执行
	private State mTak = new TakingPicturesState();// 拍照
	private State mAfter = new AfterState();// 在以下操作执行之后执行
	private ContextState mContextState = new ContextState(mBefore);

	private int mPartCount;
	private int mDefaultCheckId;

	private int remaining;
	private int checkedId;
	private int checkedFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin);
		Context context = getApplicationContext();

		tv_btn = findViewById_(R.id.tv_btn);
		tv_need = findViewById_(R.id.tv_need);
		sw_switch = findViewById_(R.id.sw_switch);
		sw_switch.setOnCheckedChangeListener(this);
		iv_cartoon = findViewById_(R.id.iv_cartoon);
		sv_video = findViewById_(R.id.sv_voide);

		mCartoomShow = AnimationUtils.loadAnimation(context,
				R.anim.test_cartoon_show);
		mCartoomHide = AnimationUtils.loadAnimation(context,
				R.anim.test_cartoon_hide);

		mVideoHelper = new CameraVideoHelper(context, sv_video,false);
		mVideoHelper.setOnTakeResultListener((OnTakeResultListener) mTak);
		mVideoHelper.onCreate(savedInstanceState);

		View title = findViewById(TitleView.ID_TITLE);
		mTitle = new TitleView(this, title);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setCenterText(R.string.skin_title, null);

		RelativeRadioGroup group = findViewById_(R.id.rag_part);
		String[] texts = new String[SelectPartsView.SKIN_BUTTONS_TEXT.length];
		for (int i = 0; i < texts.length; i++)
			texts[i] = getString(SelectPartsView.SKIN_BUTTONS_TEXT[i]);
		mParts = new SelectPartsView(this, group, texts,
				SelectPartsView.SKIN_BUTTONS_FLAG);
		mParts.setOnCheckedChangeListener(this);
		mPartCount = remaining = texts.length;
		mDefaultCheckId = mParts.getCheckedRadioButtonId();
		
		test();
	}

	private boolean testTake;
	private void test() {
		if(!BuildConfig.DEBUG) {
			return;
		}
		iv_cartoon.setClickable(true);
		iv_cartoon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testTake = !testTake;
				Toast.makeText(getApplicationContext(),
						(testTake ? "开启" : "关闭") + "使用测试图片分析。", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case R.id.btn_face_test:
			// --- 开始 ---
			mContextState.request();
			// --- 结束 ---
			break;
		}
	}

	@Override
	public void onCheckedChanged(RelativeRadioGroup group, int checkedId) {
		RadioButton radio = (RadioButton) group.findViewById(checkedId);
		tv_btn.setText(radio.getText() + getString(R.string.test));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mVideoHelper.setOpen(isChecked);
		if (isChecked) {
			if (!mVideoHelper.openVideo()) {
				buttonView.setChecked(false);
				Toast.makeText(getApplicationContext(),
						R.string.test_please_check_device, Toast.LENGTH_SHORT)
						.show();
				return;
			}
			iv_cartoon.setVisibility(View.INVISIBLE);
			iv_cartoon.startAnimation(mCartoomHide);
			try {
				sv_video.setVisibility(View.VISIBLE);
				// sv_video.startAnimation(AnimationUtils.loadAnimation(this,
				// R.anim.test_video_show));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			iv_cartoon.setVisibility(View.VISIBLE);
			iv_cartoon.startAnimation(mCartoomShow);
			mCartoomShow.setAnimationListener(new AnimationListenerAdapter() {
				@Override
				public void onAnimationEnd(Animation animation) {
					mCartoomShow.setAnimationListener(null);
					mVideoHelper.closeVideo();
					try {
						sv_video.setVisibility(View.INVISIBLE);
						// sv_video.startAnimation(AnimationUtils.loadAnimation(this,
						// R.anim.test_video_hide));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_TEST) {
			if (resultCode == RESULT_FIRST_USER) {
				resetRadioButtons();
			} else {
				finish();
			}
		}
	}

	private void resetRadioButtons() {
		sw_switch.setChecked(false);
		remaining = mPartCount;
		checkedId = 0;
		tv_need.setVisibility(View.INVISIBLE);
		int[] ids = mParts.getRadioButtonsId();
		for (int i = 0; i < ids.length; i++) {
			mParts.setEnableRadioButton(ids[i], true);
		}
		mParts.setChecked(mDefaultCheckId, true);
	}

	private class StopStateAdapter implements State, StopStateable {
		private AsyncTask<?, ?, ?> task;
		boolean isCancel;

		@Override
		public void stop() {
			isCancel = true;
			if (task != null) {
				if (!task.isCancelled()) {
					task.cancel(true);
				}
			}
			// 这个是用户手动取消了，就重置
			mContextState.setState(mBefore);
		}

		@Override
		public void handle(ContextState context, Object... params) {

		}

		public void setTask(AsyncTask<?, ?, ?> task) {
			this.task = task;
		}
	}

	private class ExceptionState extends StopStateAdapter {

		@Override
		public void handle(ContextState context, Object... params) {
			context.setState(mBefore);
			dimessWaitingDialog();
			if (params == null)
				return;
			Toast.makeText(getApplicationContext(), (String) params[0],
					Toast.LENGTH_LONG).show();
		}
	}

	private class BeforeState extends StopStateAdapter {
		@Override
		public void handle(ContextState context, Object... params) {
			if (checkedId == mParts.getCheckedRadioButtonId()) {
				return;
			}
			checkedId = mParts.getCheckedRadioButtonId();
			checkedFlag = mParts.getCheckedRadioButtonFlag();
			showWaitingDialog(context);

			if (!mVideoHelper.isTakeable() && !testTake) {
				// Toast.makeText(getApplicationContext(), "请检查WIFI或设备",
				// Toast.LENGTH_SHORT).show();
				params = new String[] { getString(R.string.test_please_check_device) };
				context.setState(mException);
			} else {
				// setWaitingText(getString(R.string.dialog_wait));
				context.setState(mTak);
			}
			context.request(params);
		}
	}

	private class TakingPicturesState extends StopStateAdapter implements
			OnTakeResultListener {
		private ContextState context;

		@Override
		public void handle(final ContextState context, Object... params) {
			this.context = context;
			Log.i("hj", "taking pictures");
			setWaitingText("正在拍照...");
			if(!testTake) {
				mVideoHelper.takeVideo();
			} else {

				setTask(new AsyncTask<Object, Object, String[]>() {
					@Override
					protected void onPreExecute() {
					}
	
					@Override
					protected String[] doInBackground(Object... params) {
						File fileRgb = new File(getCacheDir(), "rgb.jpg");
						File filePl = new File(getCacheDir(), "pl.jpg");
						return new String[] { fileRgb.getAbsolutePath(),
								filePl.getAbsolutePath() };
					}
	
					@Override
					protected void onPostExecute(String[] result) {
						TakingPicturesState.this.onPostExecute(result);
					}
				}.execute());
			}
		}

		protected void onPostExecute(String[] result) {
			if (isCancel) {
				return;
			}
			Log.i("hj", "onPostExecute");
			Object[] params = null;
			if (result == null || result.length < 2 || result[0] == null
					|| "".equals(result[0].trim()) || result[1] == null
					|| "".equals(result[1].trim())) {
				context.setState(mException);
				params = new String[] { "拍照错误, 请重试" };
			} else {
				// String rgbPath = result[0];
				// String plPath = result[1];
				// params = result;
				params = new Object[] { null, null, result };
				// 将状态设置为分析图片
				context.setState(mAfter);
			}
			context.request(params);
		}

		@Override
		public void onTakeResult(boolean success) {
			if (success) {
				String path = Content.RGB_PATH;
				onPostExecute(new String[] { path, path });
			} else {
				onPostExecute(null);
			}
		}
	}

	private class AfterState implements State {

		@Override
		public void handle(ContextState context, Object... params) {
			remaining--;
			if (remaining == 0) {
				sw_switch.setChecked(false);
				tv_need.setVisibility(View.INVISIBLE);
				startActivityForResult(new Intent(ActivitySkinTest.this,
						ActivitySkinResult.class), REQ_TEST);
			} else {
				tv_need.setVisibility(View.VISIBLE);// skin_need_test_what_part
			}
			tv_need.setText(getString(R.string.skin_need_test_what_part,
					remaining));
			mParts.setEnableRadioButton(checkedId, false);

			context.setState(mBefore);
			dimessWaitingDialog();// null;
			TestSkinReq req = (TestSkinReq) params[0];
			TestSkinResp resp = (TestSkinResp) params[1];
			String[] paths = (String[]) params[2];
			/*
			 * if(mCamera != null){ Log.i("hj","--endCamera");
			 * mCamera.endCamera(); }
			 */
			Intent intent = new Intent(ActivitySkinTest.this,
					ActivitySkinResult.class);
			intent.putExtra("photo_paths", paths);
			intent.putExtra("part",
					ActivityDaily.getPart(mParts.getCheckedRadioButtonFlag()));
			intent.putExtra("test_result", req);
			intent.putExtra("data_result", resp);
			startActivity(intent);
			finish();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		mVideoHelper.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mVideoHelper.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		mVideoHelper.onPause();
		super.onPause();
	}

	@Override
	protected void onStop() {
		// sw_switch.setChecked(false);
		mVideoHelper.onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mVideoHelper.onDestroy();
		super.onDestroy();
	}
}
