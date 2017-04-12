package com.dilapp.radar.ui.account;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.domain.Register.RegReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.util.Slog;
import com.dilapp.radar.util.StringUtils;

/**
 * 
 * 手机注册页面
 * 
 * @author Administrator
 * 
 */
public class ActivityPhoneRegister extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityPhoneRegister";
	public static final int REGISTER_USERINFO_SUCCESS = 1102;
	public static final int REGISTER_USERINFO_FAILED = 1103;
	public static final int SMS_TIMER = 1104;
	public static final int SEND_TIMER = 1105;
	public static final int SEND_CODE_RESULT = 1106;
	private Context mContext;
	private EditText et_phone;
	private EditText et_smsCode;
	private EditText et_password;
	private TextView tv_common_register;
	private TextView tv_error_hine;
	private Button btn_get_smsCode;
	private Button btn_next;
	private ImageButton mBtnBack;

	private boolean isTimer = false;
	private int SMS_VERIFY_TIME = 60;
	private Timer timer;
	private MyTimerTask task;

	private String userName;
	private String smsCode;
	private String password;
	private String message;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_USERINFO_SUCCESS:
				Intent intent = new Intent(mContext, ActivityRegisterAge.class);
				startActivityForResult(intent, 0);
				break;
			case REGISTER_USERINFO_FAILED:
				if (!TextUtils.isEmpty(message)) {
					if (message.contains("registered")){
						tv_error_hine.setText(R.string.username_registered);
						tv_error_hine.setVisibility(View.VISIBLE);
					}else
						tv_error_hine.setText(R.string.register_failed);
				}
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
			case SEND_CODE_RESULT:
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示新好友
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						// 无需验证，后台进行验证
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(mContext, "验证码已经发送", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					((Throwable) data).printStackTrace();
					// DialogUtils.promptInfoDialog(mContext, "验证码错误");
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
		setContentView(R.layout.activity_phone_register);
		init_view();
	}

	private void init_view() {
		mContext = this;
		mBtnBack = findViewById_(R.id.button_back);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_smsCode = (EditText) findViewById(R.id.et_smsCode);
		et_password = (EditText) findViewById(R.id.et_password);
		tv_error_hine = (TextView) findViewById(R.id.tv_error_hine);
		tv_common_register = (TextView) findViewById(R.id.tv_common_register);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_get_smsCode = (Button) findViewById(R.id.btn_get_smsCode);

		mBtnBack.setOnClickListener(this);
		btn_get_smsCode.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		tv_common_register.setOnClickListener(this);
		btn_next.setClickable(false);
		btn_get_smsCode.setClickable(false);
		btn_get_smsCode.getBackground().setAlpha(120);
		btn_next.getBackground().setAlpha(120);

		initSMSSDK();

		et_phone.addTextChangedListener(new TextWatcher() {
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
				if (s.toString().length() != 11) {
					btn_get_smsCode.setClickable(false);
					btn_get_smsCode.getBackground().setAlpha(120);
				} else {
					btn_get_smsCode.setClickable(true);
					btn_get_smsCode.getBackground().setAlpha(225);
				}
			}
		});
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
					btn_next.setClickable(false);
					btn_next.getBackground().setAlpha(120);
				} else {
					btn_next.setClickable(true);
					btn_next.getBackground().setAlpha(225);
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
				msg.what = SEND_CODE_RESULT;
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
			userName = et_phone.getText().toString().trim();
			if (!TextUtils.isEmpty(userName)) {
				if (StringUtils.isMobileNum(userName)) {
					handler.sendEmptyMessage(SEND_TIMER);
					SMSSDK.getVerificationCode("86", userName);
					Slog.i(TAG + "电话号码:" + userName);
				} else {
					DialogUtils.promptInfoDialog(mContext, "请输入正确的手机号");
				}
			}
			break;
		case R.id.btn_next:
			userName = et_phone.getText().toString().trim();
			smsCode = et_smsCode.getText().toString().trim();
			password = et_password.getText().toString().trim();
			if (!TextUtils.isEmpty(smsCode)) {
				registerUserInfo(userName, password);
				hiddenSoftInput();
			} else {
				DialogUtils.promptInfoDialog(mContext, "请输入验证码");
			}

			break;
		case R.id.tv_common_register:
			Intent intent = new Intent(mContext, ActivityCommonRegister.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 注册用户名和密码
	 */
	private void registerUserInfo(String userName, String password) {
		Register mDetail = ReqFactory.buildInterface(mContext, Register.class);
		RegReq mRegReq = new RegReq();
		mRegReq.setUserId(userName);
		mRegReq.setPwd(password);
		if (!TextUtils.isEmpty(smsCode))
			mRegReq.setVerifyCode(smsCode);
		else
			mRegReq.setVerifyCode("0");
		mRegReq.setRegionCode("86");
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Log.i(TAG,
								"status:" + resp.getStatus() + "---"
										+ resp.getMessage());
						handler.sendEmptyMessage(REGISTER_USERINFO_SUCCESS);
					} else {
						message = resp.getMessage();
						handler.sendEmptyMessage(REGISTER_USERINFO_FAILED);
					}
				}
			}
		};
		addCallback(node);
		mDetail.regAsync(mRegReq, node);
		showWaitingDialog((ContextState) null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			boolean success = data.getBooleanExtra("success", true);
			String msg = data.getStringExtra("message");
			Slog.e("Register Result : " + success + "  " + msg);
			if (success) {
				SharePreCacheHelper.setUserName(mContext, userName);
				SharePreCacheHelper.setPassword(mContext, password);
				Intent intent = new Intent(this, ActivityTabs.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
				finish();
			}
		} else {
			// finish();
		}

	}

	private void hiddenSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}
}
