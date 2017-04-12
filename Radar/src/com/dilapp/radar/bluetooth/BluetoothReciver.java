package com.dilapp.radar.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
			int bluetooth_int = 0;
			bluetooth_int = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
			if(bluetooth_int == 12){ // bluetooth is open  start service
			 Intent serviceIntent = new Intent(context,BluetoothServer.class);
			 context.startService(serviceIntent);
			}
		}
	}

}
