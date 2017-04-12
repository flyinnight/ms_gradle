package com.dilapp.radar.ui.skintest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.domain.AnalyzeType;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.DailyTestSkin;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinReq;
import com.dilapp.radar.domain.DailyTestSkin.TestSkinResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.imageanalysis.ImageProcess;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.ui.ContextState.State;
import com.dilapp.radar.view.OverScrollView;
import com.dilapp.radar.view.SpiderWebView;
import com.dilapp.radar.view.SpiderWebView.SpiderWebType;

public class ActivityNormalResult extends BaseActivity implements
		OnClickListener, SeekBarView.OnSeekBarChangedListener,
		OverScrollView.OnScrollChangedListener{

	private TitleView mTitle;

	private SpiderWebView sv_draw;
	private OverScrollView osv_scroll;

	private SeekBarView mWaterSeekBar;
	private TextView tv_avg_water;
	private TextView tv_reduce_water;
	private TextView tv_current_water;
	private TextView tv_relative_last_water;
	private TextView tv_current_indicator_water;

	private SeekBarView mEasticSeekBar;
	private TextView tv_avg_eastic;
	private TextView tv_reduce_eastic;
	private TextView tv_current_eastic;
	private TextView tv_relative_last_eastic;
	private TextView tv_current_indicator_eastic;

	private SeekBarView mWhiteningSeekBar;
	private TextView tv_avg_whitening;
	private TextView tv_reduce_whitening;
	private TextView tv_current_whitening;
	private TextView tv_relative_last_whitening;
	private TextView tv_current_indicator_whitening;

	private SeekBarView mPoreSeekBar;
	private TextView tv_avg_pore;
	private TextView tv_reduce_pore;
	private TextView tv_current_pore;
	private TextView tv_relative_last_pore;
	private TextView tv_current_indicator_pore;

	private SeekBarView mSensitiveSeekBar;
	private TextView tv_avg_sensitive;
	private TextView tv_reduce_sensitive;
	private TextView tv_current_sensitive;
	private TextView tv_relative_last_sensitive;
	private TextView tv_current_indicator_sensitive;

	private SeekBarView mOilSeekBar;
	private TextView tv_avg_oil;
	private TextView tv_reduce_oil;
	private TextView tv_current_oil;
	private TextView tv_relative_last_oil;
	private TextView tv_current_indicator_oil;

	private State mException = new ExceptionState();// 以下的过程出了意外
	private State mBefore = new BeforeState();// 在以下操作执行之前执行
	private State mAnalysis = new AnalysisState();// 分析拍照结果
	private State mReqSvr = new RequestServerState();// 将拍照结果保存到服务器
	private State mAfter = new AfterState();// 在以下操作执行之后执行
	private ContextState mContextState = new ContextState(mBefore);

	private int part;
	private TestSkinReq req;
	private TestSkinResp resp;
	private View v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_normal_result);
		part = getIntent().getIntExtra("part", 0);
		req = (TestSkinReq) getIntent().getSerializableExtra("test_result");
		resp = (TestSkinResp) getIntent().getSerializableExtra("data_result");
		Object[] paths = getIntent().getStringArrayExtra("photo_paths");

		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.normal_result_title, null);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		mTitle.setRightIcon(R.drawable.btn_share, this);

		sv_draw = findViewById_(R.id.sv_draw);
		sv_draw.setMinAndMax(0, 100);
		sv_draw.setTypes(getSpiderWebTypes());
		sv_draw.setInterpolator(new BounceInterpolator());

		osv_scroll = findViewById_(R.id.osv_scroll);
		osv_scroll.setOnScrollChangedListener(this);

		SeekBarView.SeekBarViewStyle[] styles = getSeekBarStyles();

		ViewGroup waterContainer = findViewById_(R.id.vg_water);
		ViewGroup water = (ViewGroup) waterContainer
				.findViewById(R.id.vg_indicator_container);
		mWaterSeekBar = new SeekBarView(this, water, R.id.vg_water);
		mWaterSeekBar.setOnSeekBarChangedListener(this);
		// mWaterSeekBar.setCurrentValue(mWaterSeekBar.getMin());
		tv_avg_water = (TextView) waterContainer.findViewById(R.id.tv_avg);
		tv_reduce_water = (TextView) waterContainer.findViewById(R.id.tv_reduce);
		tv_current_water = (TextView) waterContainer.findViewById(R.id.tv_current);
		tv_relative_last_water = (TextView) waterContainer.findViewById(R.id.tv_relative_last);
		tv_current_indicator_water = (TextView) waterContainer.findViewById(R.id.tv_current_indicator);

		ViewGroup easticContainer = findViewById_(R.id.vg_eastic);
		SeekBarView.setSeekBarViewStyle(this, easticContainer, styles[2]);
		ViewGroup eastic = (ViewGroup) easticContainer
				.findViewById(R.id.vg_indicator_container);
		mEasticSeekBar = new SeekBarView(this, eastic, R.id.vg_eastic);
		mEasticSeekBar.setOnSeekBarChangedListener(this);
		// mEasticSeekBar.setCurrentValue(mEasticSeekBar.getMin());
		tv_avg_eastic = (TextView) easticContainer.findViewById(R.id.tv_avg);
		tv_reduce_eastic = (TextView) easticContainer
				.findViewById(R.id.tv_reduce);
		tv_current_eastic = (TextView) easticContainer
				.findViewById(R.id.tv_current);
		tv_relative_last_eastic = (TextView) easticContainer
				.findViewById(R.id.tv_relative_last);
		tv_current_indicator_eastic = (TextView) easticContainer
				.findViewById(R.id.tv_current_indicator);

		ViewGroup whiteningContainer = findViewById_(R.id.vg_whitening);
		SeekBarView.setSeekBarViewStyle(this, whiteningContainer, styles[4]);
		ViewGroup whitening = (ViewGroup) whiteningContainer
				.findViewById(R.id.vg_indicator_container);
		mWhiteningSeekBar = new SeekBarView(this, whitening, R.id.vg_whitening);
		mWhiteningSeekBar.setOnSeekBarChangedListener(this);
		// mWhiteningSeekBar.setCurrentValue(mWhiteningSeekBar.getMin());
		tv_avg_whitening = (TextView) whiteningContainer
				.findViewById(R.id.tv_avg);
		tv_reduce_whitening = (TextView) whiteningContainer
				.findViewById(R.id.tv_reduce);
		tv_current_whitening = (TextView) whiteningContainer
				.findViewById(R.id.tv_current);
		tv_relative_last_whitening = (TextView) whiteningContainer
				.findViewById(R.id.tv_relative_last);
		tv_current_indicator_whitening = (TextView) whiteningContainer
				.findViewById(R.id.tv_current_indicator);

		ViewGroup poreContainer = findViewById_(R.id.vg_pore);
		SeekBarView.setSeekBarViewStyle(this, poreContainer, styles[5]);
		ViewGroup pore = (ViewGroup) poreContainer
				.findViewById(R.id.vg_indicator_container);
		mPoreSeekBar = new SeekBarView(this, pore, R.id.vg_pore);
		mPoreSeekBar.setOnSeekBarChangedListener(this);
		// mPoreSeekBar.setCurrentValue(mPoreSeekBar.getMin());
		tv_avg_pore = (TextView) poreContainer.findViewById(R.id.tv_avg);
		tv_reduce_pore = (TextView) poreContainer.findViewById(R.id.tv_reduce);
		tv_current_pore = (TextView) poreContainer
				.findViewById(R.id.tv_current);
		tv_relative_last_pore = (TextView) poreContainer
				.findViewById(R.id.tv_relative_last);
		tv_current_indicator_pore = (TextView) poreContainer
				.findViewById(R.id.tv_current_indicator);

		ViewGroup sensitiveContainer = findViewById_(R.id.vg_sensitive);
		SeekBarView.setSeekBarViewStyle(this, sensitiveContainer, styles[3]);
		ViewGroup sensitive = (ViewGroup) sensitiveContainer
				.findViewById(R.id.vg_indicator_container);
		mSensitiveSeekBar = new SeekBarView(this, sensitive, R.id.vg_sensitive);
		mSensitiveSeekBar.setOnSeekBarChangedListener(this);
		// mSensitiveSeekBar.setCurrentValue(mSensitiveSeekBar.getMin());
		tv_avg_sensitive = (TextView) sensitiveContainer
				.findViewById(R.id.tv_avg);
		tv_reduce_sensitive = (TextView) sensitiveContainer
				.findViewById(R.id.tv_reduce);
		tv_current_sensitive = (TextView) sensitiveContainer
				.findViewById(R.id.tv_current);
		tv_relative_last_sensitive = (TextView) sensitiveContainer
				.findViewById(R.id.tv_relative_last);
		tv_current_indicator_sensitive = (TextView) sensitiveContainer
				.findViewById(R.id.tv_current_indicator);

		ViewGroup oilContainer = findViewById_(R.id.vg_oil);
		SeekBarView.setSeekBarViewStyle(this, oilContainer, styles[1]);
		ViewGroup oil = (ViewGroup) oilContainer
				.findViewById(R.id.vg_indicator_container);
		mOilSeekBar = new SeekBarView(this, oil, R.id.vg_oil);
		mOilSeekBar.setOnSeekBarChangedListener(this);
		// mOilSeekBar.setCurrentValue(mOilSeekBar.getMax());
		tv_avg_oil = (TextView) oilContainer.findViewById(R.id.tv_avg);
		tv_reduce_oil = (TextView) oilContainer.findViewById(R.id.tv_reduce);
		tv_current_oil = (TextView) oilContainer.findViewById(R.id.tv_current);
		tv_relative_last_oil = (TextView) oilContainer
				.findViewById(R.id.tv_relative_last);
		tv_current_indicator_oil = (TextView) oilContainer
				.findViewById(R.id.tv_current_indicator);

		mContextState.request(paths);
		setUiDataFromBean(req, resp);

		v = pore;
		test();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case TitleView.ID_LEFT:
			finish();
			break;
		case TitleView.ID_RIGHT:
			break;
		default:
			break;
		}
	}

	@Override
	public void changed(SeekBarView seek, int value) {
		switch (seek.getId()) {
		case R.id.vg_water:
			tv_current_water.setText(value + "");
			tv_current_indicator_water.setText(value + "");
			break;
		case R.id.vg_oil:
			tv_current_oil.setText(value + "");
			tv_current_indicator_oil.setText(value + "");
			break;
		case R.id.vg_eastic:
			tv_current_eastic.setText(value + "");
			tv_current_indicator_eastic.setText(value + "");
			break;
		case R.id.vg_sensitive:
			tv_current_sensitive.setText(value + "");
			tv_current_indicator_sensitive.setText(value + "");
			break;
		case R.id.vg_whitening:
			tv_current_whitening.setText(value + "");
			tv_current_indicator_whitening.setText(value + "");
			break;
		case R.id.vg_pore:
			tv_current_pore.setText(value + "");
			tv_current_indicator_pore.setText(value + "");
			break;
		}
	}

	private void setUiDataFromBean(TestSkinReq req, TestSkinResp resp) {
		if(req == null) {
			req = new TestSkinReq();
		}
		if (resp == null) {
			resp = new TestSkinResp();
		}

		List<int[]> values = new ArrayList<int[]>(1);
		int[] testValue = getValuesFromTestSkinReq(req);// testValue(sv_draw.getTypes().length,
														// sv_draw.getMax());
		values.add(testValue);
		/*for (int i = 0; i < sv_draw.getTypes().length; i++)
			sv_draw.getTypes()[i].setValue(ActivityDailyResult.getTextFromValue(testValue[i]));*/
		// values.add(new int[]{ 60, 90, 50, 50, 50, 45});
		List<Paint> paints = new ArrayList<Paint>(1);
		SpiderWebView.SpiderWebPaint paint = new SpiderWebView.SpiderWebPaint(
				Paint.ANTI_ALIAS_FLAG);
		paint.setStartAndEndColor(
				getResources().getColor(R.color.test_normal_result_value_start),
				getResources().getColor(R.color.test_normal_result_value_end));
		paint.setStyle(Paint.Style.FILL);
		paints.add(paint);
		Paint small = new Paint(Paint.ANTI_ALIAS_FLAG);
		small.setTextAlign(Align.CENTER);
		small.setTextSize(getResources().getDimensionPixelSize(
				R.dimen.test_normal_result_spider_small_text_size));
		small.setColor(getResources().getColor(
				R.color.test_normal_result_spider_result_text_color));
		Paint big = new Paint(small);
		big.setFakeBoldText(true);
		big.setTextSize(getResources().getDimensionPixelSize(
				R.dimen.test_normal_result_spider_big_text_size));
		sv_draw.setResultText(getString(R.string.normal_result_skin_age),
				small, "18", big, getString(R.string.normal_result_balance),
				small);
		
		sv_draw.setValues(values, paints);
		sv_draw.show(1000);
		
		
		mWaterSeekBar.setCurrentValue(req.getWater());
		tv_current_indicator_water.setText(req.getWater() + "");
		tv_avg_water.setText(resp.getWaterAvg() + "%");
		tv_reduce_water.setText(resp.getWaterLast() + "%");

		mOilSeekBar.setCurrentValue(req.getOil());
		tv_current_indicator_oil.setText(req.getOil() + "");
		tv_avg_oil.setText(resp.getOilAvg() + "%");
		tv_reduce_oil.setText(resp.getOilLast() + "%");

		mEasticSeekBar.setCurrentValue(req.getEastic());
		tv_current_indicator_eastic.setText(req.getEastic() + "");
		tv_avg_eastic.setText(resp.getElasticAvg() + "%");
		tv_reduce_eastic.setText(resp.getElasticLast() + "%");

		mSensitiveSeekBar.setCurrentValue(req.getSensitive());
		tv_current_indicator_sensitive.setText(req.getSensitive() + "");
		tv_avg_sensitive.setText(resp.getSensitiveAvg() + "%");
		tv_reduce_sensitive.setText(resp.getSensitiveLast() + "%");

		mWhiteningSeekBar.setCurrentValue(req.getWhitening());
		tv_current_indicator_whitening.setText(req.getWhitening() + "");
		tv_avg_whitening.setText(resp.getWhiteningAvg() + "%");
		tv_reduce_whitening.setText(resp.getWhiteningLast() + "%");

		mPoreSeekBar.setCurrentValue(req.getPore());
		tv_current_indicator_pore.setText(req.getPore() + "");
		tv_avg_pore.setText(resp.getPoreAvg() + "%");
		tv_reduce_pore.setText(resp.getPoreLast() + "%");
		// tv_avg_oil;
		// tv_reduce_oil;
	}

/*	@Override
	public void call(TestSkinResp resp) {
		this.resp = resp;
		setUiDataFromBean(req, resp);
	}*/

	private int[] getValuesFromTestSkinReq(TestSkinReq result) {
		int[] values = new int[6];
		values[0] = result.getWater();
		values[1] = result.getEastic();
		values[2] = result.getWhitening();
		values[3] = result.getPore();
		values[4] = result.getSensitive();
		values[5] = result.getOil();
		return values;
	}

	private SpiderWebType[] getSpiderWebTypes() {
		Resources res = getResources();
		int nameColor = res.getColor(R.color.spiderWeb_type_text_color);
		SpiderWebType[] types = new SpiderWebType[6];
		types[0] = new SpiderWebType(getString(R.string.test_water), nameColor,
				res.getColor(R.color.test_normal_result_water_text_color));
		types[1] = new SpiderWebType(getString(R.string.test_eastic),
				nameColor,
				res.getColor(R.color.test_normal_result_eastic_text_color));
		types[2] = new SpiderWebType(getString(R.string.test_whitening),
				nameColor,
				res.getColor(R.color.test_normal_result_whitening_text_color));
		types[3] = new SpiderWebType(getString(R.string.test_pore), nameColor,
				res.getColor(R.color.test_normal_result_pore_text_color));
		types[4] = new SpiderWebType(getString(R.string.test_sensitive),
				nameColor,
				res.getColor(R.color.test_normal_result_sensitive_text_color));
		types[5] = new SpiderWebType(getString(R.string.test_oil), nameColor,
				res.getColor(R.color.test_normal_result_oil_text_color));
		return types;
	}

	private SeekBarView.SeekBarViewStyle[] getSeekBarStyles() {
		SeekBarView.SeekBarViewStyle[] styles = new SeekBarView.SeekBarViewStyle[6];
		styles[0] = new SeekBarView.SeekBarViewStyle(R.string.test_water,
				R.string.test_water_english,
				R.drawable.bg_normal_result_circle_water,
				R.drawable.bg_normal_result_circle_water,
				R.drawable.bg_result_line_water,
				R.color.test_normal_result_water_text_color, true);

		styles[1] = new SeekBarView.SeekBarViewStyle(R.string.test_oil,
				R.string.test_oil_english,
				R.drawable.bg_normal_result_circle_oil,
				R.drawable.bg_normal_result_circle_oil,
				R.drawable.bg_result_line_oil,
				R.color.test_normal_result_oil_text_color, false);

		styles[2] = new SeekBarView.SeekBarViewStyle(R.string.test_eastic,
				R.string.test_eastic_english,
				R.drawable.bg_normal_result_circle_eastic,
				R.drawable.bg_normal_result_circle_eastic,
				R.drawable.bg_result_line_eastic,
				R.color.test_normal_result_eastic_text_color, true);

		styles[3] = new SeekBarView.SeekBarViewStyle(R.string.test_sensitive,
				R.string.test_sensitive_english,
				R.drawable.bg_normal_result_circle_sensitive,
				R.drawable.bg_normal_result_circle_sensitive,
				R.drawable.bg_result_line_sensitive,
				R.color.test_normal_result_sensitive_text_color, true);

		styles[4] = new SeekBarView.SeekBarViewStyle(R.string.test_whitening,
				R.string.test_whitening_english,
				R.drawable.bg_normal_result_circle_whitening,
				R.drawable.bg_normal_result_circle_whitening,
				R.drawable.bg_result_line_whitening,
				R.color.test_normal_result_whitening_text_color, true);

		styles[5] = new SeekBarView.SeekBarViewStyle(R.string.test_pore,
				R.string.test_pore_english,
				R.drawable.bg_normal_result_circle_pore,
				R.drawable.bg_normal_result_circle_pore,
				R.drawable.bg_result_line_pore,
				R.color.test_normal_result_pore_text_color, true);
		return styles;
	}

	private void test() {
		if (!BuildConfig.DEBUG)
			return;

		mOilSeekBar.enableDrag();
		findViewById(R.id.vg_water).findViewById(android.R.id.button1)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int val = mWaterSeekBar.getCurrentValue() + 1;
						if (val > mWaterSeekBar.getMax()) {
							val = mWaterSeekBar.getMin();
						}
						mWaterSeekBar.setCurrentValue(val);
					}
				});

		findViewById(R.id.vg_eastic).findViewById(android.R.id.button1)
				.setOnClickListener(new OnClickListener() {
					boolean isMax;

					@Override
					public void onClick(View v) {
						int val = mEasticSeekBar.getCurrentValue();
						int duration = 3000;
						if (val >= mEasticSeekBar.getMax()) {
							isMax = true;
							mEasticSeekBar.setCurrentValue(mEasticSeekBar.getMin(),
									duration);
						} else if (val <= mEasticSeekBar.getMin()) {
							isMax = false;
							mEasticSeekBar.setCurrentValue(mEasticSeekBar.getMax(),
									duration);
						} else {
							mEasticSeekBar.setCurrentValue(
									isMax ? mEasticSeekBar.getMin() : mEasticSeekBar
											.getMax(), duration);
							isMax = !isMax;
						}
					}
				});
		findViewById(R.id.vg_pore).findViewById(android.R.id.button1)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int val = mPoreSeekBar.getCurrentValue() - 1;
						if (val < mPoreSeekBar.getMin()) {
							val = mPoreSeekBar.getMax();
						}
						mPoreSeekBar.setCurrentValue(val);
					}
				});
		ViewConfiguration configuration = ViewConfiguration.get(getApplicationContext());
		final int KEY_PEPEAT_TIMEOUT = configuration.getKeyRepeatTimeout();
		final int KEY_REPEAT_DELAY = 31;// configuration.getKeyRepeatDelay();// windows里面是31ms
		findViewById(R.id.vg_whitening).findViewById(android.R.id.button1)
				.setOnTouchListener(new View.OnTouchListener() {
					boolean isPressed = false;
					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							switch (msg.what) {
								case 2:
									addValue();
									isPressed = true;
									handler.sendEmptyMessageDelayed(1, KEY_PEPEAT_TIMEOUT);
									break;
								case 1:
									if (isPressed) {
										addValue();
										handler.sendEmptyMessageDelayed(1, KEY_REPEAT_DELAY);
									}
									break;
								case 0:
									isPressed = false;
									break;
							}
						}
					};

					private void addValue() {
						int value = mWhiteningSeekBar.getCurrentValue();
						if (++value > mWhiteningSeekBar.getMax()) {
							value = mWhiteningSeekBar.getMin();
						}
						mWhiteningSeekBar.setCurrentValue(value);
						tv_current_whitening.setText(value + "");
						tv_current_indicator_whitening.setText(value + "");
					}

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								handler.sendEmptyMessage(2);
								break;
							case MotionEvent.ACTION_MOVE:
								break;
							case MotionEvent.ACTION_UP:
							case MotionEvent.ACTION_CANCEL:
								handler.removeMessages(2);
								handler.sendEmptyMessage(0);
								break;
						}
						return true;
					}
				});
		findViewById(R.id.vg_sensitive).findViewById(android.R.id.button1)
				.setOnTouchListener(new View.OnTouchListener() {
					boolean isPressed = false;
					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							switch (msg.what) {
								case 2:
									subValue();
									isPressed = true;
									handler.sendEmptyMessageDelayed(1, KEY_PEPEAT_TIMEOUT);
									break;
								case 1:
									if(isPressed) {
										subValue();
										handler.sendEmptyMessageDelayed(1, KEY_REPEAT_DELAY);
									}
									break;
								case 0:
									isPressed = false;
									break;
							}
						}
					};

					private void subValue() {
						int value = mSensitiveSeekBar.getCurrentValue();
						if(--value < mSensitiveSeekBar.getMin()) {
							value = mSensitiveSeekBar.getMax();
						}
						mSensitiveSeekBar.setCurrentValue(value);
						tv_current_sensitive.setText(value + "");
						tv_current_indicator_sensitive.setText(value + "");
					}
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								handler.sendEmptyMessage(2);
								break;
							case MotionEvent.ACTION_MOVE:
								break;
							case MotionEvent.ACTION_UP:
							case MotionEvent.ACTION_CANCEL:
								handler.removeMessages(2);
								handler.sendEmptyMessage(0);
								break;
						}
						return true;
					}
				});
		mWaterSeekBar.setCurrentValue(mWaterSeekBar.getMax(), 3000);
		mEasticSeekBar.setCurrentValue(mOilSeekBar.getMin(), 3000);
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				sv_draw.show(2000);
			}
		}.sendEmptyMessageDelayed(0, 500);
	}

	public static int[] testValue(int length, int max) {
		Random ran = new Random();
		int[] value = new int[length];
		for (int i = 0; i < value.length; i++) {
			value[i] = ran.nextInt(max);
		}
		return value;
	}

	@Override
	public void onScrollChanged(int x, int y, int oldx, int oldy) {
		// Log.i(getClass().getName(), "x " + x + ", y " + y +
		// ", oldx " + oldx + ", oldy " + oldy +
		// ", vx " + v.getX() + ", vy " + v.getY() +
		// ", vtx " + v.getTranslationX() + ", vty " + v.getTranslationX() +
		// ", vpx " + v.getPivotX() + ", vpy " + v.getPivotY());
		// Log.i("III",
		// "l " + v.getLeft() + ", t " + v.getTop() + ", r "
		// + v.getRight() + ", b " + v.getBottom());
	}

	@Override
	public void scrollBottom() {

	}

	private class ExceptionState implements ContextState.State {

		@Override
		public void handle(ContextState context, Object... params) {
			context.setState(mBefore);
			dimessWaitingDialog();
			if (params == null)
				return;
//			Toast.makeText(getApplicationContext(), (String) params[0],
//					Toast.LENGTH_LONG).show();
		}
	}

	private class BeforeState implements ContextState.State {
		@Override
		public void handle(ContextState context, Object... params) {
			// setWaitingText(getString(R.string.dialog_wait));
			if(params == null) {
				params = new String[]{ "没有拍照结果" };
				context.setState(mException);
			} else {
				context.setState(mAnalysis);
			}
			context.request(params);
		}
	}

	private class AnalysisState implements ContextState.State {
		@Override
		public void handle(final ContextState context, Object... params) {

			new AsyncTask<Object, Object, TestSkinReq>() {

				@Override
				protected void onPreExecute() {
					Log.i("hj", "onPreExecute analysis");
					setWaitingText("正在分析拍照结果...");
				}

				@Override
				protected TestSkinReq doInBackground(Object... param) {
					Log.i("III", "doInBackground analysis");
					File fileRgb = new File((String) param[0]);
					File filePl = new File((String) param[1]);
					Resources res = getResources();
					final int waIndex = res
							.getInteger(R.integer.test_water_index);
					final int eIndex = res
							.getInteger(R.integer.test_eastic_index);
					final int whIndex = res
							.getInteger(R.integer.test_whitening_index);
					final int pIndex = res
							.getInteger(R.integer.test_pore_index);
					final int sIndex = res
							.getInteger(R.integer.test_sensitive_index);
					final int oIndex = res.getInteger(R.integer.test_oil_index);
					final int ageIndex = res.getInteger(R.integer.test_skinage_index);
					long st = System.currentTimeMillis();
					float[] result = ImageProcess.getInstance()
							.runSkinImageAnalysis(fileRgb.getAbsolutePath(),
									filePl.getAbsolutePath());
					long et = System.currentTimeMillis();
					if (result == null) {
						return null;
					}
					// String msg = "";
					// for (int i = 0; i < result.length; i++) {
					// msg += result[i] + ", ";
					// }
					Log.i("hj", "analysis " + (et - st));
					TestSkinReq req = new TestSkinReq();
					req.setWater((int) (result[waIndex] * 100f));
					req.setElastic((int) (result[eIndex] * 100f));
					req.setWhitening((int) (result[whIndex] * 100f));
					req.setPore((int) (result[pIndex] * 100f));
					req.setSensitive((int) (result[sIndex] * 100f));
					req.setOil((int) (result[oIndex] * 100f));
					req.setSkinAge((int)result[ageIndex]);
					req.setType(AnalyzeType.DAILY);
					req.setPart(part);
					return req;
				}

				@Override
				protected void onPostExecute(TestSkinReq result) {
					Object[] params = null;
					if (result == null) {
						params = new String[] { "分析结果失败" };
						context.setState(mException);
					} else {
						params = new TestSkinReq[] { result, null };
						context.setState(mAfter);
					}
					context.request(params);
				}
			}.execute(params);
		}
	}

	private class RequestServerState implements ContextState.State {

		private ContextState context;

		@Override
		public void handle(final ContextState context, Object... params) {
			this.context = context;

			setWaitingText("正在上传分析结果...");
			final TestSkinReq req = (TestSkinReq) params[0];

			DailyTestSkin interf = ReqFactory.buildInterface(
					getApplicationContext(), DailyTestSkin.class);
			BaseCall<DailyTestSkin.TestSkinResp> node = new BaseCall<DailyTestSkin.TestSkinResp>() {

				@Override
				public void call(TestSkinResp resp) {
					Object[] params = null;
					if (resp == null) {
						params = new String[] { "请检查网络, 结果上传失败..." };
						context.setState(mException);
						setUiDataFromBean(req, resp);
					} else if (resp.isSuccess()) {
						params = new Object[] { req, resp };
						context.setState(mAfter);
					} else {
						params = new String[] { "结果上传失败... "
								+ resp.getMessage() };
						context.setState(mException);
						setUiDataFromBean(req, resp);
					}
					context.request(params);

				}
			};
			addCallback(node);
			interf.dailyTestSkinAsync(req, node);
		}
	}

	private class AfterState implements State {

		@Override
		public void handle(ContextState context, Object... params) {
			context.setState(mBefore);
			dimessWaitingDialog();// null;
			TestSkinReq req = (TestSkinReq) params[0];
			TestSkinResp resp = (TestSkinResp) params[1];
			ActivityNormalResult.this.req = req;
			ActivityNormalResult.this.resp = resp;
			setUiDataFromBean(req, resp);
			/*
			 * if(mCamera != null){ Log.i("hj","--endCamera");
			 * mCamera.endCamera(); }
			 */
			// Intent intent = new Intent(ActivityNormal.this,
			// ActivityNormalResult.class);
			// intent.putExtra("part",
			// getPart(mParts.getCheckedRadioButtonFlag()));
			// intent.putExtra("test_result", req);
			// intent.putExtra("data_result", resp);
			// startActivity(intent);
			// finish();
		}

	}
}
