package com.dilapp.radar.ui.mine;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PhoneEmailManage;
import com.dilapp.radar.domain.PhoneEmailManage.BindPhoneReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.StringUtils;

public class ActivityBindingPhone extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityBindingPhone";
	public static final int SMS_TIMER = 1006;
	public static final int SEND_TIMER = 1007;
	private static final int BINDING_PHONE_SUCCESS = 1008;
	private static final int BINDING_PHONE_FAILARE = 1009;
	public static final int SEND_CODE_RESULT = 1010;
	private Context mContext;
	private TitleView mTitle;
	private View title;
	private ImageButton mBtnClear;
	private Button btn_get_smsCode;
	private EditText et_phone, et_smsCode;
	private String phoneNumber, smsCode;

	private boolean isTimer = false;
	private int SMS_VERIFY_TIME = 60;
	private Timer timer;
	private MyTimerTask task;

	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case BINDING_PHONE_SUCCESS:
				Toast.makeText(mContext, R.string.binding_success,
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra("phone", phoneNumber);
				setResult(RESULT_OK, intent);
				finish();
				break;
			case BINDING_PHONE_FAILARE:
				Toast.makeText(mContext, R.string.binding_failed,
						Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getApplicationContext(), "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					((Throwable) data).printStackTrace();
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
		setContentView(R.layout.activity_binding_phone);

		init_view();
	}

	private void init_view() {
		mContext = this;
		title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setCenterText(R.string.binding_phone, null);
		mTitle.setRightText(R.string.save, this);
		mTitle.setLeftIcon(R.drawable.btn_back, this);
		et_phone = findViewById_(R.id.et_phone);
		et_smsCode = findViewById_(R.id.et_smsCode);
		mBtnClear = findViewById_(R.id.button_clear);
		btn_get_smsCode = findViewById_(R.id.btn_get_smsCode);

		mBtnClear.setOnClickListener(this);
		btn_get_smsCode.setOnClickListener(this);
		btn_get_smsCode.setClickable(false);
		btn_get_smsCode.getBackground().setAlpha(120);

		String userPhone = SharePreCacheHelper.getBindedPhone(mContext);
		if (!TextUtils.isEmpty(userPhone)) {
			et_phone.setText(userPhone);
		}

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

		initSMSSDK();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			phoneNumber = et_phone.getEditableText().toString();
			smsCode = et_smsCode.getEditableText().toString();
			if (!TextUtils.isEmpty(phoneNumber)) {
				if (StringUtils.isMobileNum(phoneNumber)) {
					if (!TextUtils.isEmpty(smsCode))
						bindingPhone(phoneNumber, smsCode);
					else {
						Toast.makeText(mContext, R.string.input_phone_smscode,
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(mContext, R.string.input_phone_number,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(mContext, R.string.input_phone_number2,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_get_smsCode:
			phoneNumber = et_phone.getText().toString().trim();
			if (!TextUtils.isEmpty(phoneNumber)) {
				if (StringUtils.isMobileNum(phoneNumber)) {
					handler.sendEmptyMessage(SEND_TIMER);
					SMSSDK.getVerificationCode("86", phoneNumber);
				} else {
					Toast.makeText(mContext, R.string.input_phone_number,
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.button_clear:
			et_phone.setText("");
			break;
		}
	}

	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			if (isTimer)
				handler.sendEmptyMessage(SMS_TIMER);
		}
	}

	/**
	 * 根据手机号重置密码
	 */
	private void bindingPhone(String phoneNumber, String smsCode) {
		PhoneEmailManage mDetail = ReqFactory.buildInterface(mContext,
				PhoneEmailManage.class);
		BindPhoneReq req = new BindPhoneReq();
		req.setPhoneNo(phoneNumber);
		req.setVerifyCode(smsCode);
		req.setRegionCode("86");
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Log.i(TAG,
								"status:" + resp.getStatus() + "--"
										+ resp.getMessage());
						handler.sendEmptyMessage(BINDING_PHONE_SUCCESS);
					} else {
						handler.sendEmptyMessage(BINDING_PHONE_FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.bindPhoneNoAsync(req, node);
		showWaitingDialog((ContextState) null);
	}

}
