package com.dilapp.radar.ui.mine;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.BaseResp;
import com.dilapp.radar.domain.PasswordManage;
import com.dilapp.radar.domain.PasswordManage.ChangePwdReq;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.BaseActivity;
import com.dilapp.radar.ui.ContextState;
import com.dilapp.radar.ui.TitleView;
import com.dilapp.radar.util.Slog;

public class ActivityAmendPassword extends BaseActivity implements
		OnClickListener {
	private final int REQUEST_SUCCESS = 1233;
	private final int REQUEST_FAILED = 1234;
	private Context mContext;
	private TitleView mTitle;
	private TextView tv_hint_info;

	private EditText old_password;
	private EditText new_password;
	private EditText verify_password;
	private String tv_old_pwd, tv_new_pwd, tv_verify_pwd;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REQUEST_SUCCESS:
				Toast.makeText(mContext, R.string.reset_success,
						Toast.LENGTH_SHORT).show();
				SharePreCacheHelper.setPassword(mContext, tv_new_pwd);
				finish();
				break;
			case REQUEST_FAILED:
				tv_hint_info.setText(R.string.reset_failed);
				tv_hint_info.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amend_pwd);

		init_view();
	}

	private void init_view() {
		mContext = this;
		View title = findViewById(R.id.vg_title);
		mTitle = new TitleView(this, title);
		mTitle.setLeftText(R.string.cancel, this);
		mTitle.setCenterText(R.string.info_amend_password, null);
		mTitle.setRightText(R.string.finish, this);

		tv_hint_info = (TextView) findViewById(R.id.tv_hint_info);

		old_password = findViewById_(R.id.old_password);
		new_password = findViewById_(R.id.new_password);
		verify_password = findViewById_(R.id.verify_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vg_left:
			finish();
			break;
		case R.id.vg_right:
			tv_old_pwd = old_password.getText().toString().trim();
			tv_new_pwd = new_password.getText().toString().trim();
			tv_verify_pwd = verify_password.getText().toString().trim();
			if (TextUtils.isEmpty(tv_old_pwd) || TextUtils.isEmpty(tv_new_pwd)
					|| TextUtils.isEmpty(tv_verify_pwd)) {
				tv_hint_info.setText(R.string.password_empty);
				tv_hint_info.setVisibility(View.VISIBLE);
			} else if (!tv_new_pwd.equals(tv_verify_pwd)) {
				tv_hint_info.setText(R.string.password_error);
				tv_hint_info.setVisibility(View.VISIBLE);
			} else if(tv_new_pwd.length()<8){
				tv_hint_info.setText(R.string.password_error_length);
				tv_hint_info.setVisibility(View.VISIBLE);
			}else{
				tv_hint_info.setVisibility(View.GONE);
				amendPassword(tv_old_pwd, tv_new_pwd);
			}
			break;
		}
	}

	private void amendPassword(String oldPwd, String newPwd) {
		PasswordManage mDetail = ReqFactory.buildInterface(mContext,
				PasswordManage.class);
		ChangePwdReq request_parm = new ChangePwdReq();
		request_parm.setOldPwd(oldPwd);
		request_parm.setNewPwd(newPwd);
		BaseCall<BaseResp> node = new BaseCall<BaseResp>() {
			@Override
			public void call(BaseResp resp) {
				dimessWaitingDialog();
				if (resp != null && resp.isRequestSuccess()) {
					mHandler.sendEmptyMessage(REQUEST_SUCCESS);
					Slog.i("修改成功：" + resp.getMessage());
				} else {
					mHandler.sendEmptyMessage(REQUEST_FAILED);
					Slog.i("修改失败：" + resp.getMessage());
				}
			}
		};
		addCallback(node);
		mDetail.changePwdAsync(request_parm, node);
		hiddenSoftInput();
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
