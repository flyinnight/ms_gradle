package com.dilapp.radar.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.dilapp.radar.R;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PasswordManage;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.DialogUtils;
import com.dilapp.radar.util.StringUtils;

/**
 * 
 * 忘记密码--找回密码页面
 * 
 * @author Administrator
 * 
 */
public class ActivityRetrievePassword extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ActivityRetrievePassword";
	private static final int QUERY_PHONE_SUCCESS = 1222;
	private static final int QUERY_PHONE_FAILARE = 1223;
	private static final int SEND_EMAIL_SUCCESS = 1224;
	private static final int SEND_EMAIL_FAILARE = 1225;
	private Context mContext;
	private Button btn_next;
	private ImageButton mBtnBack;
	private EditText et_phone;
	private String accountNumber;

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QUERY_PHONE_SUCCESS:
				if (!TextUtils.isEmpty(accountNumber)) {
					Intent intent = new Intent(mContext,
							ActivityPhoneResetPassword.class);
					intent.putExtra("phoneNumber", accountNumber);
					startActivityForResult(intent, 0);
				}
				break;
			case QUERY_PHONE_FAILARE:
				DialogUtils.promptInfoDialog(mContext, "请输入您绑定的手机号");
				break;
			case SEND_EMAIL_SUCCESS:
				Toast.makeText(mContext, R.string.send_email_success,
						Toast.LENGTH_SHORT).show();
				Intent intent1 = new Intent(mContext,
						ActivityEmailRetrieve.class);
				intent1.putExtra("register_email", accountNumber);
				startActivity(intent1);
				break;
			case SEND_EMAIL_FAILARE:
				DialogUtils.promptInfoDialog(mContext, "请输入您绑定的邮箱账号");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrieve_password);
		init_view();
	}

	private void init_view() {
		mContext = this;

		mBtnBack = findViewById_(R.id.button_back);
		mBtnBack.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		btn_next.setClickable(false);
		btn_next.getBackground().setAlpha(120);

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
				if (s.toString().length() > 0) {
					btn_next.setClickable(true);
					btn_next.getBackground().setAlpha(225);
				} else {
					btn_next.setClickable(false);
					btn_next.getBackground().setAlpha(120);
				}
			}
		});

	}

	/**
	 * 找回密码查询手机号
	 */
	private void queryPhone(String phoneNumber) {
		PasswordManage mDetail = ReqFactory.buildInterface(mContext,
				PasswordManage.class);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Log.i(TAG,
								"status:" + resp.getStatus() + "---"
										+ resp.getMessage());
						mhandler.sendEmptyMessage(QUERY_PHONE_SUCCESS);
					} else {
						mhandler.sendEmptyMessage(QUERY_PHONE_FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.retrievePwdByPhoneAsync(phoneNumber, node);
		showWaitingDialog((ContextState) null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.btn_next:
			accountNumber = et_phone.getText().toString().trim();
			if (!TextUtils.isEmpty(accountNumber)) {
				if (accountNumber.contains("@")) {
					sendAmendPwdEmail(accountNumber);
				} else {
					if (StringUtils.isMobileNum(accountNumber))
						queryPhone(accountNumber);
					else
						DialogUtils.promptInfoDialog(mContext, "请输入您绑定的手机号");
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 发送修改密码连接到邮箱
	 */
	private void sendAmendPwdEmail(String email) {
		PasswordManage mDetail = ReqFactory.buildInterface(mContext,
				PasswordManage.class);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null) {
					if ("SUCCESS".equals(resp.getStatus())) {
						Log.i(TAG,
								"status:" + resp.getStatus() + "---"
										+ resp.getMessage());
						mhandler.sendEmptyMessage(SEND_EMAIL_SUCCESS);
					} else {
						mhandler.sendEmptyMessage(SEND_EMAIL_FAILARE);
					}
				}
			}
		};
		addCallback(node);
		mDetail.retrievePwdByEmailAsync(email, node);
		showWaitingDialog((ContextState) null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			finish();
		}
	}
}
