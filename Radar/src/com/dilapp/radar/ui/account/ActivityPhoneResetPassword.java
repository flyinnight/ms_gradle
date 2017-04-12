package com.dilapp.radar.ui.account;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PasswordManage;
import com.dilapp.radar.domain.PasswordManage.ResetPwdPhoneReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.Slog;

/**
 * 
 * 手机找回密码
 * 
 * @author Administrator
 * 
 */
public class ActivityPhoneResetPassword extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityPhoneRegister";
	public static final int SMS_TIMER = 1002;
	public static final int SEND_TIMER = 1003;
	private static final int RESET_SUCCESS = 1004;
	private static final int RESET_FAILARE = 1005;
	private Context mContext;
	private EditText et_smsCode;
	private EditText et_password;
	private Button btn_get_smsCode;
	private Button btn_finish;
	private ImageButton mBtnBack;

	private boolean isTimer = false;
	private int SMS_VERIFY_TIME = 60;
	private Timer timer;
	private MyTimerTask task;

	private String phoneNumber;
	private String smsCode;
	private String newPassword;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case RESET_SUCCESS:
				Toast.makeText(mContext, R.string.reset_success,
						Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				break;
			case RESET_FAILARE:

				break;
			case SEND_TIMER:
				timer = new Timer();
				task = new MyTimerTask();
				timer.schedule(task, 0, 1000);
				isTimer = true;
				btn_get_smsCode.setClickable(false);
				break;
			case SMS_TIMER:
				if (SMS_VERIFY_TIME <= 0) {
					btn_get_smsCode.setClickable(true);
					isTimer = false;
					SMS_VERIFY_TIME = 60;
					btn_get_smsCode.setText("重新获取");
					task.cancel();
					timer = null;
				} else {
					btn_get_smsCode.setText((SMS_VERIFY_TIME--) + "s");
				}
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_reset_password);
		init_view();
	}

	private void init_view() {
		mContext = this;

		phoneNumber = getIntent().getStringExtra("phoneNumber");

		et_smsCode = (EditText) findViewById(R.id.et_smsCode);
		et_password = (EditText) findViewById(R.id.et_password);

		mBtnBack = findViewById_(R.id.button_back);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_get_smsCode = (Button) findViewById(R.id.btn_get_smsCode);
		btn_get_smsCode.setOnClickListener(this);

		mBtnBack.setOnClickListener(this);
		btn_finish.setOnClickListener(this);
		btn_finish.setClickable(true);
		btn_finish.getBackground().setAlpha(120);

		initSMSSDK();

		et_password.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() < 8 || s.toString().length() >= 18) {
					btn_finish.setClickable(false);
					btn_finish.getBackground().setAlpha(120);
				} else {
					btn_finish.setClickable(true);
					btn_finish.getBackground().setAlpha(225);
				}
			}
		});

	}

	private void initSMSSDK() {
		SMSSDK.initSDK(mContext, Constants.APPKEY, Constants.APPSECRET);
		EventHandler eh = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}

		};
		SMSSDK.registerEventHandler(eh);
	}

	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			if (isTimer)
				handler.sendEmptyMessage(SMS_TIMER);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.btn_get_smsCode:// 获取短信验证码
			if (!TextUtils.isEmpty(phoneNumber)) {
				SMSSDK.getVerificationCode("86", phoneNumber);
				Slog.i(TAG + "电话号码:" + phoneNumber);
			}
			handler.sendEmptyMessage(SEND_TIMER);
			break;
		case R.id.btn_finish:
			smsCode = et_smsCode.getText().toString().trim();
			newPassword = et_password.getText().toString().trim();
			if (!TextUtils.isEmpty(smsCode) && !TextUtils.isEmpty(newPassword)) {
				resetByPhonePwd(phoneNumber, smsCode, newPassword);
			}
			// if (!TextUtils.isEmpty(smsCode)) {
			// SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
			// }
			break;
		default:
			break;
		}

	}

	/**
	 * 根据手机号重置密码
	 */
	private void resetByPhonePwd(String phoneNumber, String smsCode,
			String newPassword) {
		PasswordManage mDetail = ReqFactory.buildInterface(mContext,
				PasswordManage.class);
		ResetPwdPhoneReq req = new ResetPwdPhoneReq();
		req.setPhoneNo(phoneNumber);
		req.setNewPwd(newPassword);
		req.setVerifyCode(smsCode);
		req.setRegionCode("86");
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Log.i(TAG,
								"status:" + resp.getStatus() + "---"
										+ resp.getMessage());
						handler.sendEmptyMessage(RESET_SUCCESS);
					} else {
						handler.sendEmptyMessage(RESET_FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.resetPwdByPhoneAsync(req, node);
		showWaitingDialog((ContextState) null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

}
