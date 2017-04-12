package com.dilapp.radar.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.ActivityTabs;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.util.Slog;

public class ActivityRegisterOne extends BaseActivity implements
		OnClickListener {

	private Context mContext;
	private Button mBtnStart;
	private ImageButton mBtnBack;
	private EditText mUserNameEdit;
	private EditText mPasswordEdit;
	private LinearLayout mNameErrCover;
	private LinearLayout mPwdErrCover;
	private TextView tv_phoneRegister;
	private String mUserName;
	private String mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_one);

		init_view();
	}

	private void init_view() {
		mContext = this;
		findViewById_(R.id.register_1_layout).setOnClickListener(this);
		mBtnStart = (Button) findViewById_(R.id.start_next);
		mBtnStart.setOnClickListener(this);

		mBtnBack = findViewById_(R.id.button_back);
		mBtnBack.setOnClickListener(this);

		mUserNameEdit = findViewById_(R.id.register_username);
		mPasswordEdit = findViewById_(R.id.register_pwd);
		mNameErrCover = findViewById_(R.id.error_username);
		mPwdErrCover = findViewById_(R.id.error_password);
		tv_phoneRegister = findViewById_(R.id.privacy_clause);
		tv_phoneRegister.setOnClickListener(this);
		mUserNameEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mNameErrCover.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mPasswordEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s == null || s.length() < 8) {
					mPwdErrCover.setVisibility(View.GONE);
				} else {
					mPwdErrCover.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.start_next:

			mUserName = mUserNameEdit.getText().toString();
			mPassword = mPasswordEdit.getText().toString();

			if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword)) {
				Toast.makeText(this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
			} else if (mPwdErrCover.getVisibility() != View.GONE) {
				Toast.makeText(this, "密码格式错误！", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(ActivityRegisterOne.this,
						ActivityRegisterTwo.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.putExtra("user_name", mUserName);
				intent.putExtra("password", mPassword);
				hiddenSoftInput();
				startActivityForResult(intent, 0);
			}

			break;
		case R.id.button_back:
			finish();
			break;
		case R.id.register_1_layout:
			hiddenSoftInput();
		case R.id.privacy_clause:
			Intent intent = new Intent(mContext, ActivityPhoneRegister.class);
			startActivity(intent);
			break;
		}
	}

	private void hiddenSoftInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}

}
