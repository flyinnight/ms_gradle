package com.dilapp.radar.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dilapp.radar.BuildConfig;
import com.dilapp.radar.R;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.Login;
import com.dilapp.radar.domain.Login.LoginResp;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.ui.account.ActivityPhoneRegister;
import com.dilapp.radar.ui.account.ActivityRetrievePassword;
import com.dilapp.radar.util.HttpConstant;
import com.dilapp.radar.util.ReleaseUtils;
import com.dilapp.radar.util.Slog;

public class WelcomeLogin extends BaseActivity implements OnClickListener {

    private Context mContext;
    private ViewPager mPager;
    private RadioButton mPagerF0;
    private RadioButton mPagerF1;
    private RadioButton mPagerF2;

    private View mPagerOne;
    private View mPagerTwo;
    private View mPagerThree;
    private List<View> mViewList;
    private WelcomeAdapter mAdapter;

    private TextView mNewRegister;
    private TextView forgetPassword;
    private TextView tv_error_hint;
    private ImageButton mBtnClose;
    private EditText mUserNameEdit;
    private EditText mPasswordEdit;
    private Button mLoginBtn;

    private Login mLogin;

    private int statusCode;
    private String sStatus;
    private String mMsg;
    private String mUserName;
    private String mPassword;
    private String mMessage;
    private boolean isOnPause = false;

    private static final int PAGE_3_ID = 0x3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_layout);

        // findViewById_(R.id.welcome_layout).setOnClickListener(this);

        mLogin = ReqFactory.buildInterface(this, Login.class);
        addCallback(mBaseCall);
        mPager = (ViewPager) findViewById(R.id.wel_pager);
        mPagerF0 = (RadioButton) findViewById(R.id.pager_0);
        mPagerF1 = (RadioButton) findViewById(R.id.pager_1);
        mPagerF2 = (RadioButton) findViewById(R.id.pager_2);

        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                switch (arg0) {
                    case 0:
                        mPagerF0.setChecked(true);
                        break;
                    case 1:
                        mPagerF1.setChecked(true);
                        break;
                    case 2:
                        mPagerF2.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        mViewList = new ArrayList<View>();
        @SuppressWarnings("static-access")
        LayoutInflater lf = getLayoutInflater().from(this);
        mPagerOne = lf.inflate(R.layout.welcome_pager_one, null);
        mPagerTwo = lf.inflate(R.layout.welcome_pager_one, null);
        mPagerThree = lf.inflate(R.layout.welcome_pager_three, null);
        mPagerThree.setId(PAGE_3_ID);
        mPagerThree.setOnClickListener(this);
        ImageView mPagerImage = (ImageView) mPagerOne
                .findViewById(R.id.pager_image);
        mPagerImage.setBackgroundResource(R.drawable.welcome_pager_1);
        mPagerImage = (ImageView) mPagerTwo.findViewById(R.id.pager_image);
        mPagerImage.setBackgroundResource(R.drawable.welcome_pager_2);

        mViewList.add(mPagerOne);
        mViewList.add(mPagerTwo);
        mViewList.add(mPagerThree);
        mAdapter = new WelcomeAdapter(mViewList);
        mPager.setAdapter(mAdapter);
        initView();
        test();
    }

    private void test() {
        if (BuildConfig.DEBUG)
            mPagerF0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mLoginBtn.setClickable(true);
                        mLoginBtn.getBackground().setAlpha(225);
                    } else {
                        mLoginBtn.setClickable(false);
                        mLoginBtn.getBackground().setAlpha(120);
                    }
                }
            });
    }

    private void initView() {
        mContext = this;
        mNewRegister = (TextView) mPagerThree.findViewById(R.id.new_user);
        forgetPassword = (TextView) mPagerThree
                .findViewById(R.id.tv_forget_pwd);
        tv_error_hint = (TextView) mPagerThree.findViewById(R.id.tv_error_hint);
        mBtnClose = (ImageButton) mPagerThree.findViewById(R.id.button_close);
        mUserNameEdit = (EditText) mPagerThree
                .findViewById(R.id.input_username);
        mPasswordEdit = (EditText) mPagerThree.findViewById(R.id.input_pwd);
        mLoginBtn = (Button) mPagerThree.findViewById(R.id.start_login);
        mNewRegister.setOnClickListener(this);
        mBtnClose.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        mLoginBtn.setClickable(false);
        mLoginBtn.getBackground().setAlpha(120);

        String currName = SharePreCacheHelper.getUserName(this);
        String currPwd = SharePreCacheHelper.getPassword(this);

        if (!TextUtils.isEmpty(currName)) {
            mUserNameEdit.setText(currName);
        }
        if (!TextUtils.isEmpty(currPwd)) {
            mPasswordEdit.setText(currPwd);
        }

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
                String userName = mUserNameEdit.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(mContext, R.string.input_username,
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (s.toString().length() < 8
                            || s.toString().length() >= 18) {
                        mLoginBtn.setClickable(false);
                        mLoginBtn.getBackground().setAlpha(120);
                    } else {
                        mLoginBtn.setClickable(true);
                        mLoginBtn.getBackground().setAlpha(225);
                    }
                }
            }
        });

    }

    private void handleLogin() {
        mUserName = mUserNameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();

        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            Login.LoginReq mReq = new Login.LoginReq();
            mReq.setUsername(mUserName);
            mReq.setPwd(mPassword);
            mLogin.loginAsync(mReq, mBaseCall);
            hiddenSoftInput();
            showWaitingDialog((ContextState) null);
        }
    }

    private BaseCall<Login.LoginResp> mBaseCall = new BaseCall<Login.LoginResp>() {

        @Override
        public void call(LoginResp resp) {
            statusCode = resp.getStatusCode();
            sStatus = resp.getStatus();
            mMsg = resp.getMessage();
            Slog.e("login result : " + statusCode + "  " + sStatus + "  "
                    + mMsg);
            dimessWaitingDialog();
            //cancel for user register part
//            if (resp.isRequestSuccess() && !isOnPause) {
                SharePreCacheHelper.setUserName(getApplicationContext(),
                        mUserName);
                SharePreCacheHelper.setPassword(getApplicationContext(),
                        mPassword);
                Intent intent = new Intent(WelcomeLogin.this,
                        ActivityTabs.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                finish();
//            } else {
//                mMessage = resp.getMessage();
//                tv_error_hint.setText(R.string.username_password_error);
//                tv_error_hint.setVisibility(View.VISIBLE);
//                Slog.i(mMessage);
//            }
        }
    };

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        String currID = SharePreCacheHelper.getUserName(this);
        String currPwd = SharePreCacheHelper.getPassword(this);
        if (!TextUtils.isEmpty(currID) && !TextUtils.isEmpty(currPwd)) {
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        dimessWaitingDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                Intent intent = new Intent(WelcomeLogin.this,
                        ActivityPhoneRegister.class);
                // Intent intent = new Intent(WelcomeLogin.this,
                // ActivityRegisterOne.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                break;
            case R.id.tv_forget_pwd:
                Intent intent1 = new Intent(WelcomeLogin.this,
                        ActivityRetrievePassword.class);
                startActivity(intent1);
                break;
            case R.id.button_close:
                finish();
                break;
            case R.id.start_login:
                handleLogin();
                break;
            case PAGE_3_ID:
                hiddenSoftInput();
                break;
        }
    }

}
