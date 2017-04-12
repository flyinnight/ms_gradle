package com.dilapp.radar.ui.account;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PhoneEmailManage;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.domain.Register.RegRadarReq;
import com.dilapp.radar.domain.Register.RegRadarResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.view.GridRadioGroup;
import com.dilapp.radar.view.GridRadioGroup.OnCheckedChangeListener;

/**
 * 用户选择年龄页面
 * 
 * @author Administrator
 * 
 */
public class ActivityRegisterAge extends BaseActivity implements
		OnClickListener {

	private static final int SEND_EMAIL_SUCCESS = 1011;
	private static final int SEND_EMAIL_FAILED = 1012;
	private static final int REGISTER_USERAGE_SUCCESS = 1013;
	private static final int REGISTER_USERAGE_FAILED = 1014;
	private static boolean CHOOSE_AGE = false;
	private Context mContext;
	private Button mBtnStart;
	private ImageButton mBtnBack;
	private GridRadioGroup mAgeGroup;

	private String mUserName;
	private int mAgeId = -1;

	private String message;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case SEND_EMAIL_SUCCESS:
				if (!TextUtils.isEmpty(mUserName)) {
					String emailMsg = mContext.getResources().getString(
							R.string.send_email_success2, mUserName);
					DialogUtils.promptInfoDialog(mContext, emailMsg);
				}
				break;
			case SEND_EMAIL_FAILED:
				Slog.i(message);
				DialogUtils.promptInfoDialog(mContext, "邮件发送失败！");
				break;
			case REGISTER_USERAGE_SUCCESS:
				Slog.i("账户名：" + mUserName);
				CHOOSE_AGE = true;
				if (!TextUtils.isEmpty(mUserName) && mUserName.contains("@")) {
					sendTestifyEmail();
				}
				Intent mIntent = new Intent();
				mIntent.putExtra("success", true);
				mIntent.putExtra("message", message);
				setResult(RESULT_OK, mIntent);
				finish();
				break;
			case REGISTER_USERAGE_FAILED:
				CHOOSE_AGE = true;
				Intent intent = new Intent();
				intent.putExtra("success", false);
				intent.putExtra("message", message);
				setResult(RESULT_OK, intent);
				finish();
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_two);

		init_view();
	}

	private void init_view() {
		mContext = this;
		mUserName = getIntent().getStringExtra("mUserName");

		mBtnBack = findViewById_(R.id.button_back);
		mBtnBack.setOnClickListener(this);
		mBtnStart = findViewById_(R.id.start_register);
		mBtnStart.setOnClickListener(this);

		mAgeGroup = findViewById_(R.id.age_group);
		mAgeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(GridRadioGroup group, int checkedId) {
				mAgeId = checkedId;
			}
		});
	}

	/**
	 * 注册用户年龄信息
	 */
	private void registerUserAge() {
		Register mDetail = ReqFactory.buildInterface(mContext, Register.class);
		RegRadarReq mRegReq = new RegRadarReq();
		Date mBirth = getDateByAgeID(mAgeId);
		if (mBirth != null) {
			mRegReq.setBirthday(mBirth.getTime());
		}
		BaseCall<RegRadarResp> node = new BaseCall<RegRadarResp>() {
			@Override
			public void call(RegRadarResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Slog.i("status:" + resp.getStatus() + "---"
								+ resp.getMessage());
						message = resp.getMessage();
						handler.sendEmptyMessage(REGISTER_USERAGE_SUCCESS);
					} else {
						message = resp.getMessage();
						handler.sendEmptyMessage(REGISTER_USERAGE_FAILED);
					}
				}
			}
		};
		addCallback(node);
		mDetail.regRadarAsync(mRegReq, node);
		showWaitingDialog((ContextState) null);
	}

	/**
	 * 发送验证邮件到邮箱
	 */
	private void sendTestifyEmail() {
		PhoneEmailManage mDetail = ReqFactory.buildInterface(mContext,
				PhoneEmailManage.class);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						handler.sendEmptyMessage(SEND_EMAIL_SUCCESS);
						Slog.i("邮件已发送" + resp.getMessage());
					} else {
						handler.sendEmptyMessage(SEND_EMAIL_FAILED);
					}
				}
			}
		};
		addCallback(node);
		mDetail.emailVerifyAsync(node);
		showWaitingDialog((ContextState) null);
	}

	private Date getDateByAgeID(int id) {
		Date result = null;
		Calendar a = Calendar.getInstance();
		int year = a.get(Calendar.YEAR);
		year -= 1900;
		// Slog.e("getDateByAgeID currYEAR : "+year);
		switch (id) {
		case R.id.age_less_15:
			year -= 14;
			break;
		case R.id.age_less_20:
			year -= 19;
			break;
		case R.id.age_less_25:
			year -= 24;
			break;
		case R.id.age_less_30:
			year -= 29;
			break;
		case R.id.age_less_40:
			year -= 39;
			break;
		case R.id.age_less_50:
			year -= 49;
			break;
		case R.id.age_more_50:
			year -= 51;
			break;
		default:
			return null;
		}
		result = new Date(year, 0, 1);
		return result;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.start_register:
			registerUserAge();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (CHOOSE_AGE)
				finish();
			else
				DialogUtils.promptInfoDialog(mContext, "请选择您的年龄");
		}
		return super.onKeyDown(keyCode, event);
	}
}
