package com.dilapp.radar.ui.account;

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

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.Register;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.Register.RegReq;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.util.Slog;

/**
 * 
 * 用户名或者邮箱注册
 * 
 * @author Administrator
 * 
 */
public class ActivityCommonRegister extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "ActivityCommonRegister";
	public static final int REGISTER_USERINFO_SUCCESS = 1202;
	public static final int REGISTER_USERINFO_FAILED = 1203;
	private Context mContext;
	private Button btn_next;
	private ImageButton mBtnBack;
	private EditText mUserNameEdit;
	private EditText mPasswordEdit;
	private TextView tv_phoneRegister;
	private TextView tv_error_hine;
	private String mUserName;
	private String mPassword;
	private String smsCode = "0";
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_USERINFO_SUCCESS:
				Intent intent = new Intent(mContext, ActivityRegisterAge.class);
				intent.putExtra("mUserName", mUserName);
				startActivityForResult(intent, 0);
				break;
			case REGISTER_USERINFO_FAILED:
				tv_error_hine.setText(R.string.username_registered);
				tv_error_hine.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_register);
		init_view();
	}

	private void init_view() {
		mContext = this;
		findViewById_(R.id.register_1_layout).setOnClickListener(this);
		btn_next = (Button) findViewById_(R.id.btn_next);
		mBtnBack = findViewById_(R.id.button_back);
		mUserNameEdit = findViewById_(R.id.register_username);
		mPasswordEdit = findViewById_(R.id.register_pwd);
		tv_phoneRegister = findViewById_(R.id.tv_phone_register);
		tv_error_hine = findViewById_(R.id.tv_error_hine);

		mBtnBack.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		tv_phoneRegister.setOnClickListener(this);

		btn_next.setClickable(false);
		btn_next.getBackground().setAlpha(120);

		mPasswordEdit.addTextChangedListener(new TextWatcher() {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			mUserName = mUserNameEdit.getText().toString();
			mPassword = mPasswordEdit.getText().toString();
			if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPassword)) {
				registerUserInfo(mUserName, mPassword);
				hiddenSoftInput();
			}
			// if (!TextUtils.isEmpty(mUserName) &&
			// !TextUtils.isEmpty(mPassword)) {
			// Intent intent = new Intent(ActivityCommonRegister.this,
			// ActivityRegisterTwo.class);
			// // intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			// intent.putExtra("user_name", mUserName);
			// intent.putExtra("password", mPassword);
			// hiddenSoftInput();
			// startActivityForResult(intent, 0);
			// }
			break;
		case R.id.button_back:
			finish();
			break;
		case R.id.register_1_layout:
			hiddenSoftInput();
			break;
		case R.id.tv_phone_register:
			Intent intent1 = new Intent(mContext, ActivityPhoneRegister.class);
			startActivity(intent1);
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			boolean success = data.getBooleanExtra("success", true);
			String msg = data.getStringExtra("message");
			Slog.e("Register Result : " + success + "  " + msg);
			if (success) {
				SharePreCacheHelper.setUserName(getApplicationContext(),
						mUserName);
				SharePreCacheHelper.setPassword(getApplicationContext(),
						mPassword);
				Intent intent = new Intent(this, ActivityTabs.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
				finish();
			}
		} else {
			// finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
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
						handler.sendEmptyMessage(REGISTER_USERINFO_FAILED);
					}
				}
			}
		};
		addCallback(node);
		mDetail.regAsync(mRegReq, node);
		showWaitingDialog((ContextState) null);
	}

	private void hiddenSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}

}
