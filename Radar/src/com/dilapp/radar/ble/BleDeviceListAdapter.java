package com.dilapp.radar.ble;

import java.util.ArrayList;
import java.util.List;

import com.dilapp.radar.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleDeviceListAdapter extends BaseAdapter {
	private List<BluetoothDevice> mLeDevices;
	private LayoutInflater mInflator;
	private ViewHolder viewHolder;

	public BleDeviceListAdapter(Context context,List<BluetoothDevice> mLeDevices) {
		this.mLeDevices =  mLeDevices ;
		mInflator = LayoutInflater.from(context);
	}

	public void addDevice(BluetoothDevice device) {
		if(mLeDevices == null){
			mLeDevices = new ArrayList<BluetoothDevice>();
		}
		if (!mLeDevices.contains(device)) {
			mLeDevices.add(device);
		}
	}

	public BluetoothDevice getDevice(int position) {
		return mLeDevices.get(position);
	}

	public void clear() {
		mLeDevices.clear();
	}

	@Override
	public int getCount() {
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return mLeDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
	
		if (view == null) {
			view = mInflator.inflate(R.layout.listitem_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		BluetoothDevice device = mLeDevices.get(i);
		final String deviceName = device.getName();
		if (!TextUtils.isEmpty(deviceName)) {
			viewHolder.deviceName.setText(deviceName);
		} else {
			viewHolder.deviceName.setText("未知");
		}
		//viewHolder.deviceAddress.setText(device.getAddress());
		return view;
	}

	private class ViewHolder {

		public TextView deviceName;
		public TextView deviceAddress;

	}

}
