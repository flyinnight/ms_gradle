package com.dilapp.radar.ble;

import java.util.List;

import org.w3c.dom.Text;

import com.dilapp.radar.R;
import com.dilapp.radar.wifi.LocalWifi;
import com.dilapp.radar.wifi.WifiKfirHelper;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BleDialogUtil {
	
	public static  void showFindBleDialog(final Activity context) {
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		final Dialog dialog = new Dialog(context,R.style.transparentFrameWindowStyle);
		View view  = LayoutInflater.from(context).inflate(R.layout.dialog_findble_layout,null);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		//setting ble btn
		view.findViewById(R.id.setble).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
			}
		});
		
		// close dialog
		view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				dialog.dismiss();
			}
		});
		
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.exist_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = (int) (width * 0.9);
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.CENTER;
		dialog.onWindowAttributesChanged(wl);
		dialog.show();
		
	}
	
	
	public static void showSelectBleDialog(Activity context ,List<BluetoothDevice> devices){
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		final Dialog dialog = new Dialog(context,R.style.transparentFrameWindowStyle);
		View view  = LayoutInflater.from(context).inflate(R.layout.dialog_selectble_layout,null);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		ListView listView = (ListView) view.findViewById(R.id.listview);
		if(devices != null){
		//  BleDeviceListAdapter adapter = new BleDeviceListAdapter(context,devices);
		  //listView.setAdapter(adapter);
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//TODO
				
				
			}
			
			
		});
		
		// close dialog
		view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.exist_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = (int) (width * 0.9);
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.CENTER;
		dialog.onWindowAttributesChanged(wl);
		dialog.show();
		
	}
	
	
	
	public static void  showInputWifiPasswordDialog(Activity context){
		
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		final Dialog dialog = new Dialog(context,R.style.transparentFrameWindowStyle);
		View view  = LayoutInflater.from(context).inflate(R.layout.dialog_connect_wifi_layout,null);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		final LocalWifi localWifi = new LocalWifi();
		localWifi.wifiMac = WifiKfirHelper.getInstance(context).getCurrBSSID();
		localWifi.wifiName = WifiKfirHelper.getInstance(context).getCurrSSID();
		// ssid
		TextView tx_ssid = (TextView) view.findViewById(R.id.ssid);
		tx_ssid.setText(localWifi.wifiName);
		
		final EditText editTextview = (EditText) view.findViewById(R.id.et_wifi_password);
	    
		// close dialog
		view.findViewById(R.id.dialog_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String password = editTextview.getText().toString().trim();
				 // 发送指令到设备
				
				
			}
		});
		
		
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.exist_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = (int) (width * 0.9);
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.CENTER;
		dialog.onWindowAttributesChanged(wl);
		dialog.show();
		
	}
	
	
	private static Dialog waitDialog = null;
	public static void  showWaitDialog(Activity context,String text){
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		dismissWaitDialog();
		waitDialog = new Dialog(context,R.style.transparentFrameWindowStyle);//
		View view  = LayoutInflater.from(context).inflate(R.layout.dialog_wait_layout,null);
		waitDialog.setContentView(view);
		waitDialog.setCanceledOnTouchOutside(true);
		waitDialog.setCancelable(true);
		ImageView anim = (ImageView) view.findViewById(R.id.creating_progressBar_wt_main);
		// 这里需要手动启动一下动画，否则在有的手机不会自动启动
		((AnimationDrawable) anim.getBackground()).start();
		
		
		TextView textView = (TextView) view.findViewById(R.id.prompt_ap_text_wt_main);
		textView.setText(text);
		
		Window window = waitDialog.getWindow();
		window.setWindowAnimations(R.style.exist_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = (int) (width * 0.5);
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.CENTER;
		waitDialog.onWindowAttributesChanged(wl);
		waitDialog.show();
		
	}
	
	public static void dismissWaitDialog(){
		Log.i("hj", "dismissWaitDialog");
		if(waitDialog != null && waitDialog.isShowing()){
			waitDialog.dismiss();
			waitDialog = null;
		}
	}
	
	
	
	private static Dialog  clearDeviceDialog = null;
	public static void  showclearDeviceInfo(Activity context,OnClickListener clearListener,OnClickListener cancle){
		dismissClearDeviceDialog();
		int width = context.getWindowManager().getDefaultDisplay().getWidth();
		clearDeviceDialog = new Dialog(context,R.style.transparentFrameWindowStyle);
		View view  = LayoutInflater.from(context).inflate(R.layout.dialog_celar_deviceinfo_layout,null);
		clearDeviceDialog.setContentView(view);
		clearDeviceDialog.setCanceledOnTouchOutside(true);
		clearDeviceDialog.setCancelable(true);
		view.findViewById(R.id.celarDeviceInfo).setOnClickListener(clearListener);
		
		view.findViewById(R.id.cancle).setOnClickListener(cancle);
		
		
		Window window = clearDeviceDialog.getWindow();
		window.setWindowAnimations(R.style.exist_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.width = width;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		wl.gravity = Gravity.BOTTOM;
		clearDeviceDialog.onWindowAttributesChanged(wl);
		clearDeviceDialog.show();
		
	}
	
	public static void dismissClearDeviceDialog(){
		if(clearDeviceDialog != null && clearDeviceDialog.isShowing()){
			clearDeviceDialog.dismiss();
			clearDeviceDialog = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
