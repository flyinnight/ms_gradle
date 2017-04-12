package com.dilapp.radar.ui.account;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PhoneEmailManage;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.view.GridRadioGroup;
import com.dilapp.radar.view.GridRadioGroup.OnCheckedChangeListener;

public class ActivityRegisterTwo extends BaseActivity implements
		OnClickListener {

	private static final int REGISTER_SUCCESS = 1010;
	private static final int SEND_EMAIL_SUCCESS = 1011;
	private static final int SEND_EMAIL_FAILARE = 1012;
	private Context mContext;
	private Button mBtnStart;
	private ImageButton mBtnBack;
	private GridRadioGroup mAgeGroup;

	private String mUserName;
	private String mPassword;
	private int mAgeId = -1;

	private int statusCode;
	private String sStatus;
	private String mMsg;
	private String smsCode;
	private Register mRegister;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_SUCCESS:
				sendTestifyEmail();
				break;
			case SEND_EMAIL_SUCCESS:
				Toast.makeText(mContext, R.string.send_email_success,
						Toast.LENGTH_SHORT).show();
				break;
			case SEND_EMAIL_FAILARE:

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
		mRegister = ReqFactory.buildInterface(this, Register.class);
		mUserName = getIntent().getStringExtra("user_name");
		mPassword = getIntent().getStringExtra("password");
		smsCode = getIntent().getStringExtra("smsCode");

		mBtnBack = findViewById_(R.id.button_back);
		mBtnBack.setOnClickListener(this);
		mBtnStart = findViewById_(R.id.start_register);
		mBtnStart.setOnClickListener(this);

		mAgeGroup = findViewById_(R.id.age_group);
		mAgeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(GridRadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				mAgeId = checkedId;
				// getDateByAgeID(mAgeId);
			}
		});
		addCallback(mRegCall);
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
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						handler.sendEmptyMessage(SEND_EMAIL_SUCCESS);
						Slog.i("邮件已发送"+resp.getMessage());
					} else {
						handler.sendEmptyMessage(SEND_EMAIL_FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.emailVerifyAsync(node);
	}

	private void handleRegister() {
		Slog.i("注册信息: " + mUserName + "--" + mPassword + "====" + smsCode);
		if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword)) {
			Toast.makeText(this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
		} else if (mAgeId <= 0) {
			Toast.makeText(this, "请选择年龄！", Toast.LENGTH_SHORT).show();
		} else {
			Register.RegReq mRegReq = new Register.RegReq();
			mRegReq.setUserId(mUserName);
			mRegReq.setPwd(mPassword);
			if (!TextUtils.isEmpty(smsCode))
				mRegReq.setVerifyCode(smsCode);
			else
				mRegReq.setVerifyCode("0");
			mRegReq.setRegionCode("86");
			mRegister.regAsync(mRegReq, mRegCall);
			showWaitingDialog((ContextState) null);
		}
	}

	private BaseCall<BaseResp> mRegCall = new BaseCall<BaseResp>() {

		@Override
		public void call(BaseResp resp) {
			statusCode = resp.getStatusCode();
			sStatus = resp.getStatus();
			mMsg = resp.getMessage();
			Slog.e("register result : " + statusCode + "  " + sStatus + "  "
					+ mMsg);
			dimessWaitingDialog();
			if (resp.isRequestSuccess()) {
				if (mUserName.indexOf("@") != -1) {
					handler.sendEmptyMessage(REGISTER_SUCCESS);
				Slog.i("------------------------" + "包含");
			} else {
				Slog.i("===========================" + "不不包含");
			}
				Date mBirth = getDateByAgeID(mAgeId);
				if (mBirth != null) {
					Register.RegRadarReq mRadarReq = new Register.RegRadarReq();
					mRadarReq.setBirthday(mBirth.getTime());
					mRegister.regRadarAsync(mRadarReq, null);
				}
				Intent mIntent = new Intent();
				mIntent.putExtra("success", true);
				mIntent.putExtra("message", resp.getMessage());
				setResult(RESULT_OK, mIntent);
				finish();
			} else {
				Intent mIntent = new Intent();
				mIntent.putExtra("success", false);
				mIntent.putExtra("message", resp.getMessage());
				setResult(RESULT_OK, mIntent);
				finish();
			}
		}
	};

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.start_register:
			handleRegister();
			break;
		}
	}

}
