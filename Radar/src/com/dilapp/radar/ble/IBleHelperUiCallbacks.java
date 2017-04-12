package com.dilapp.radar.ble;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public interface IBleHelperUiCallbacks {

public void uiDeviceFound(final BluetoothDevice device, int rssi, byte[] record);
	
	public void uiDeviceConnected(final BluetoothGatt gatt,  final BluetoothDevice device);
				 				
	
	public void uiDeviceDisconnected(final BluetoothGatt gatt, final BluetoothDevice device);
	
	public void uiDeviceConfirm(final BluetoothGatt gatt,  final BluetoothDevice device, boolean confirm);
			   						
	
	public void uiAvailableServices(final BluetoothGatt gatt, final BluetoothDevice device,final List<BluetoothGattService> services);
	
	
//	public void uiCharacteristicForService(final BluetoothGatt gatt,
//            		 					   final BluetoothDevice device,
//            							   final BluetoothGattService service,
//            							   final List<BluetoothGattCharacteristic> chars);
//
//	public void uiCharacteristicsDetails(final BluetoothGatt gatt,
//			  							 final BluetoothDevice device,
//			  							 final BluetoothGattService service,
//			  							 final BluetoothGattCharacteristic characteristic);	
//	
//	public void uiNewValueForCharacteristic(final BluetoothGatt gatt,
//            								final BluetoothDevice device,
//            								final BluetoothGattService service,
//            								final BluetoothGattCharacteristic ch,
//            								final String strValue,
//            								final int intValue,
//            								final byte[] rawValue,
//            								final String timestamp);
	
//	public void uiGotNotification(final BluetoothGatt gatt,
//                                  final BluetoothDevice device,
//                                  final BluetoothGattService service,
//                                  final BluetoothGattCharacteristic characteristic);
//	
//	public void uiSuccessfulWrite(final BluetoothGatt gatt,
//                                  final BluetoothDevice device,
//                                  final BluetoothGattService service,
//                                  final BluetoothGattCharacteristic ch,
//                                  final String description);
//	
//	public void uiFailedWrite(final BluetoothGatt gatt,
//			                  final BluetoothDevice device,
//			                  final BluetoothGattService service,
//			                  final BluetoothGattCharacteristic ch,
//			                  final String description);
	
//	public void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi);

	public void bleEnable();
	
	public void bleDisable();
	
	public void bleEnabling();
	
	/**
	 * 
	 * @param mode
	 * @param ssid
	 * @param ip
	 * @param errorcode
	 */
	public void onWifiStatusResult(int mode, String ssid, String ip, int errorcode);
	
	/**
	 * 
	 * @param net_status
	 * @param power_status
	 */
	public void onDeviceStatusResult(int net_status, int power_status);
	
	public void onPhotoCmdFromDevice();
	
	public void onBatteryChanged(int level);
	
	public void onEnvParamsResult(byte temp, byte rh, byte uv);
	
}
