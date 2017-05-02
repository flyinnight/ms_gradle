package com.dilapp.radar.test;

import com.dilapp.radar.domain.BaseCall;
import com.dilapp.radar.domain.Login;
import com.dilapp.radar.domain.ReqFactory;
import com.dilapp.radar.domain.Login.LoginResp;

import android.content.Context;
import android.os.Handler;

public class LoginCase extends CaseNode {

    private Login mLogin;
    private Login.LoginReq mReq;
//	private Login.LoginResp mResp;

    private int statusCode;
    private String sStatus;
    private String mMsg;

    public LoginCase(Context context, Handler tarHandler, int msgid) {
        super(context, "LoginCase", tarHandler, msgid);
        // TODO Auto-generated constructor stub
        mLogin = ReqFactory.buildInterface(context, Login.class);
        mReq = new Login.LoginReq();
//		mResp = new Login.LoginResp();
        mReq.setUsername("kfir2");
        mReq.setPwd("123456");

        this.isNeedWaitCallback(true);
        this.isStartByAsyncTask(false);
    }

    @Override
    public void onCaseStart() {
        // TODO Auto-generated method stub
        mLogin.loginAsync(mReq, new BaseCall<Login.LoginResp>() {

            @Override
            public void call(LoginResp resp) {
                // TODO Auto-generated method stub
                statusCode = resp.getStatusCode();
                sStatus = resp.getStatus();
                mMsg = resp.getMessage();
                notifyCallback();
            }
        });
    }

    @Override
    public String onError(int errorcode, String msg) {
        // TODO Auto-generated method stub
        return "Error : " + errorcode + " : " + msg;
    }

    @Override
    public String onCaseEnd() {
        // TODO Auto-generated method stub
        return "Code : " + statusCode + " Status : " + sStatus + " mMsg : " + mMsg;
    }
}
