package com.dilapp.radar.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
/**
 * @function bluetooth connect and get data 
 * 
 * @author hj
 * @time  2015/03/18
 *
 */
public class BluetoothServer extends Service {

	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private BluetoothAdapter mAdapter;
	private ConnectedThread mConnectedThread;

	private BluetoothServerSocket mmServerSocket;
	private BluetoothSocket socket = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hj", "--BluetoothService oncreate");
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		registerBroadcast();
		start();
	}

	public synchronized void start() {
		socket = null;
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		
	}

	public synchronized void connected(BluetoothSocket socket,	BluetoothDevice device) {
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
	}
	
	public synchronized void stopThreead(){
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		
	}

   /**
    * 
    * @author hj
    *
    *  @function  get data from device 
    */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {

			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {

		}

		public void cancel() {
			try {
				mmSocket.close();
				if(mmInStream!=null){
					mmInStream.close();
				}
				if(mmOutStream != null){
					mmOutStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("hj", "bluetoothServer cancel thread is error");
			}
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent data) {

			String action = data.getAction();
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				Log.i("hj", "---BluetoothService disconnect");
				start();
			} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				Log.i("hj", "--BluetoothService connected");
				try {
					mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("Server",	SPP_UUID);
					if(mmServerSocket != null){
					  socket = mmServerSocket.accept();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						mmServerSocket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (socket != null && socket.getRemoteDevice() != null ) {
					connected(socket, socket.getRemoteDevice());
				}
			}else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
	/*			蓝牙关闭 : int STATE_OFF , 值为10, 蓝牙模块处于关闭状态;
				蓝牙打开中 : int STATE_TURNING_ON , 值为11, 蓝牙模块正在打开;
				蓝牙开启 : int STATE_ON , 值为12, 蓝牙模块处于开启状态;
				蓝牙开启中 : int STATE_TURNING_OFF , 值为13, 蓝牙模块正在关闭;*/
				int bluetooth = data.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
			    if(bluetooth == 10){   
			    	stopThreead();
			    	stopSelf();
			    }
			   
			}

		}
	};

	private void registerBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
