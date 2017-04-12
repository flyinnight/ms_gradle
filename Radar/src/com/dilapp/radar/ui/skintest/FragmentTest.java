package com.dilapp.radar.ui.skintest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dilapp.radar.R;
import com.dilapp.radar.ble.BleUtils;
import com.dilapp.radar.cache.SharePreCacheHelper;
import com.dilapp.radar.ui.BaseFragment;
import com.dilapp.radar.ui.Constants;
import com.dilapp.radar.wifi.AllKfirManager;
import com.dilapp.radar.wifi.LocalWifi;

import static com.dilapp.radar.textbuilder.utils.L.d;

/**
 * Created by husj1 on 2015/6/8.
 */
public class FragmentTest extends BaseFragment {

    private Fragment mConnDevice;
    private Fragment mTestSkin;
   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnDevice = new FragmentConnDevice();
        mTestSkin = new FragmentTestSkin();
        
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    }
    
   
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(getLayout());

     
        return getContentView();
    
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d("III", "FragmentTest onActivityResult req " + requestCode + ", res " + resultCode);
        // if (mTestSkin.isVisible() && mTestSkin.isInLayout()) {
		/*if (Constants.TEST_PREVIEW || BleUtils.BLE_DEBUG || SharePreCacheHelper.getPairStatus(mContext)) {
            mTestSkin.onActivityResult(requestCode, resultCode, data);
        } else {
            mConnDevice.onActivityResult(requestCode, resultCode, data);
        }*/
        mConnDevice.onActivityResult(requestCode, resultCode, data);
        mTestSkin.onActivityResult(requestCode, resultCode, data);
	}
    
    @Override
    public void onResume() {
    	super.onResume();
        if (Build.VERSION.SDK_INT < Constants.SKIN_TEST_MIN_SDK) {
            return;
        }
    	
    	FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(!mConnDevice.isAdded()) {
            ft.add(R.id.fragment_container, mConnDevice, "connDevice");
        }
        if(!mTestSkin.isAdded()) {
            ft.add(R.id.fragment_container, mTestSkin, "testSkin");
        }
        
        if (Constants.TEST_PREVIEW || BleUtils.BLE_DEBUG || SharePreCacheHelper.getPairStatus(mContext)) {
            ft.hide(mConnDevice);
            ft.show(mTestSkin);
        	
        } else {
            ft.hide(mTestSkin);
            ft.show(mConnDevice);
        }
        ft.commit();
//        AllKfirManager.getInstance(mContext).startSkinBle();
    }
    
    public void notifyChange(boolean connShow, boolean testShow) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
    	if(connShow) {
    		ft.show(mConnDevice);
    		//((FragmentTest)getParentFragment()).notifyChange(false, true);
    	} else {
    		ft.hide(mConnDevice);
    	}
    	if(testShow) {
    		ft.show(mTestSkin);
    	} else {
    		ft.hide(mTestSkin);
    	}
    	
    	ft.commit();
    }

    private ViewGroup getLayout() {
        FrameLayout vg = new FrameLayout(mContext);
        vg.setId(R.id.fragment_container);
        return vg;
    }
    
    
  
	
}
