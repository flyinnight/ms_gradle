package com.dilapp.radar.ble;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.dilapp.radar.util.Slog;

public class PackHelper {
	
	public final static int PACKET_LENGTH_MAX = 20;
	public final static int HEAD_LENGTH_MAX = 4;
	public final static int BODY_LENGTH_MAX = 16;
	public final static int BLOCK_ITEM_LEN = 1;
	
	private static PackHelper mSelf;
	private IBleCmdCallback mCallback;
	
	//API START
	public static PackHelper getInstance(){
		if(mSelf == null){
			mSelf = new PackHelper();
		}
		return mSelf;
	}
	
	public void setCallback(IBleCmdCallback callback){
		this.mCallback = callback;
	}
	
	/**
	 * 接收数据
	 * @param orignal
	 */
	public  void splitBodys(byte[] orignal){
		if(orignal != null){
			String log = "";
			for(int i=0;i<orignal.length;i++){
				log += Integer.toHexString(orignal[i] & 0xFF)+" ";
			}
			Slog.d("splitBodys : "+log);
		}
		if(orignal == null || (int)(orignal[0]) != -86){
			Slog.e("ERROR COMMAND and ignore!!!!!!!!");
			return;
		}
		 HeadPacket headPacket = recvHeadData(orignal);
	     if(headPacket == null || headPacket.getPacket_direction() == 0){
	    	 Slog.e("Error splitBodys HEAD ERROR!!!!");
	    	 return ;
	     }
	     if(ChannelQueue.mCommand == -1){
		   ChannelQueue.mCommand = headPacket.getCommand();
		   ChannelQueue.max_pkg = headPacket.getTotal_pack();
	     }
	     if( ChannelQueue.mCommand != headPacket.getCommand() ||  headPacket.getCurr_pack() > ChannelQueue.max_pkg){
	    	 	Slog.e("Error splitBodys Command Error : "+ChannelQueue.mCommand+" "+headPacket.getCommand()
	    			 +"   ;  "+headPacket.getCurr_pack()+"  "+ChannelQueue.max_pkg);
	    	 	ChannelQueue.clearData();
	    	 //start new data
	    	 	ChannelQueue.mCommand = headPacket.getCommand();
	    	 	ChannelQueue.max_pkg = headPacket.getTotal_pack();
	    	 
	     }
	     ByteBuffer buff = ByteBuffer.wrap(orignal,HEAD_LENGTH_MAX,orignal.length-HEAD_LENGTH_MAX);
		 byte temp [] = new byte[orignal.length-HEAD_LENGTH_MAX]; 
	     
		 if(ChannelQueue.mCommand == headPacket.getCommand() && headPacket.getCurr_pack() <= ChannelQueue.max_pkg){
		     Slog.i("splitBodys current pkg is "+headPacket.getCurr_pack() + " maxPkg is  "+ ChannelQueue.max_pkg+" cmd : "+ChannelQueue.mCommand);
//	    	 buff.position(0);
	    	 buff.get(temp, 0, orignal.length-HEAD_LENGTH_MAX);
	    	 ChannelQueue.offerData(temp,headPacket.getCurr_pack());
		 }
	     //结束
	     if(ChannelQueue.mCommand == headPacket.getCommand() && headPacket.getCurr_pack() == ChannelQueue.max_pkg){
	    	 	notifyDataSuccess(headPacket);
	     }
	}
	
	/**
	 * 组包
	 * @param command
	 * @param bodyPacket  可以为null
	 * @return
	 */
	public ArrayList<byte[]> setPacket(int command,BodyPacket bodyPacket){
		ArrayList<byte[]> packetList = new ArrayList<byte[]>();
		byte data[] = null;
		byte[] bodyData  = null;
		HeadPacket headPacket = new HeadPacket();
		headPacket.setCommand(command);
		headPacket.setMagicCode(0xaa); // TODO
		headPacket.setPacket_direction(0);
		switch (command) {
		case BleContent.cmd_connectBle:
		case BleContent.cmd_bleStatus:
		case BleContent.cmd_apCommand:
		case BleContent.cmd_wifiStatus:
		case BleContent.cmd_time_check:
		case BleContent.cmd_get_battery_level:
		case BleContent.cmd_get_env_params:
			headPacket.setCurr_pack(1);
			headPacket.setTotal_pack(1);
			headPacket.setData_block_number(0);
			ByteBuffer buff = ByteBuffer.allocate(PACKET_LENGTH_MAX);
			buff.put(setHeadPacket(headPacket));
//			buff.put((byte)0);
			buff.position(0);
			buff.limit(PACKET_LENGTH_MAX);
			data = new byte[HEAD_LENGTH_MAX];
			buff.get(data, 0, HEAD_LENGTH_MAX);
			packetList.add(data);
			return packetList;
		case BleContent.cmd_staCommand:
			bodyData = bodyPacket.getBodyBytes();
            headPacket.setData_block_number(2);
			break;
		case BleContent.cmd_prepareWifiMode:
			bodyData = bodyPacket.getBodyBytes();
            headPacket.setData_block_number(1);
			break;
		default:
			bodyData = null;
			break;
		}
		int bodayTotalLength = (bodyPacket == null) ? 0 : bodyPacket.getBodyLength();
		int pic_num = bodayTotalLength / BODY_LENGTH_MAX;
		pic_num += bodayTotalLength % BODY_LENGTH_MAX == 0 ? 0 : 1;
		ByteBuffer buff = ByteBuffer.allocate(PACKET_LENGTH_MAX);
		int offset = 0;
		for(int i = 0;i < pic_num; i++ ){
			buff.clear();
			buff.position(0);
			headPacket.setTotal_pack(pic_num);
			headPacket.setCurr_pack(i+1);
			buff.put(setHeadPacket(headPacket));
			offset = BODY_LENGTH_MAX * i;
		    int endoffset = (i == (pic_num - 1)) ? bodayTotalLength  : (offset + BODY_LENGTH_MAX);
            buff.put(bodyData, offset, endoffset-offset);
            buff.position(0);
            buff.limit(PACKET_LENGTH_MAX);
            data = new byte[HEAD_LENGTH_MAX + endoffset - offset];
            buff.get(data, 0, HEAD_LENGTH_MAX + endoffset - offset);
            packetList.add(data);
		}
		return packetList;
	}
	//API END
	
	
	
	/*********************private******************************/
	
	/**
	 * 拆头
	 * @param orignal
	 * @return
	 */	
	private byte[] splitHead(byte[] orignal){
      byte [] splitHead = new byte[HEAD_LENGTH_MAX];
      int packetlength = orignal.length;
      if (packetlength < HEAD_LENGTH_MAX){
          return null;
       }
      splitHead = Arrays.copyOfRange(orignal, 0, HEAD_LENGTH_MAX);
	  return splitHead;
	}
	
	/**
	 * 拆data(*废弃*)
	 * @param orignal
	 * @return
	 */
//	private static ArrayList<byte[]> splitBody(byte[] orignal){
//	     ArrayList<byte[]> splitBody = new ArrayList<byte[]>();
//	        int packetlength = orignal.length;
//	        if (packetlength < HEAD_LENGTH_MAX){
//	           return null;
//	        }
//	        int piecenum = packetlength / PACKET_LENGTH_MAX;
//	        piecenum = piecenum + ((packetlength % PACKET_LENGTH_MAX) == 0 ? 0:1);
//	        int offset = 0;
//	        for (int index = 0; index < piecenum; index++) {
//	            offset = PACKET_LENGTH_MAX * index;
//	            int endoffset = index == (piecenum - 1) ? packetlength - 1 : offset
//	                    + PACKET_LENGTH_MAX;
//	            byte[] splitpiece = Arrays.copyOfRange(orignal, offset, endoffset);
//	            splitBody.add(splitpiece);
//	        }
//	   return splitBody;
//	}

	private void notifyDataSuccess(HeadPacket headPacket){
		transforData(headPacket);
	}
	
	/**
	 * 
	 * @param data orignal data
	 * @return HeadPacket
	 */
	private HeadPacket recvHeadData(byte[] data){
		if(data == null) return null;
		HeadPacket headPacket = new HeadPacket();
		byte [] headData = splitHead(data);
		int length = headData.length;
		ByteBuffer buff = ByteBuffer.wrap(headData);
		buff.position(0);
		buff.limit(length);
		byte temp =	buff.get();
		Slog.d("setMagicCode : "+temp);
		headPacket.setMagicCode(temp);
		temp = buff.get();
		Slog.d("cmd and dir :"+((temp & 0xF0) >> 4)+" "+(temp & 0x0F));
		headPacket.setCommand(temp);
		headPacket.setPacket_direction(temp);
		temp = buff.get();	
		Slog.d("cmd curr and total : "+((temp & 0xF0) >> 4)+" "+(temp & 0x0F));
		headPacket.setTotal_pack(temp);
		headPacket.setCurr_pack(temp);
		temp = buff.get();
		headPacket.setData_block_number(temp);
		return headPacket;
	}
	
	
	/**
	 * 
	 * @param data  拆掉head data
	 * @return
	 */
	private BodyPacket getBodyData(byte[] data,int command){
		if(data == null) return null;
		BodyPacket bodyPacket = null;
		switch (command) { 
		case BleContent.cmd_connectBle:
			bodyPacket  = new ConnectBlePacket(data);
			break;
		case BleContent.cmd_apCommand:
            bodyPacket = new WifiCommandPacket(command,data);
			break;
		case BleContent.cmd_bleStatusCallback:
			bodyPacket = new BleStatusPacket(true,data);
			break;
		case BleContent.cmd_bleStatus:
			bodyPacket = new BleStatusPacket(false,data);
			break;
		case BleContent.cmd_staCommand:
			 bodyPacket = new WifiCommandPacket(command,data);
			break;
		case BleContent.cmd_wifiStatus:
			 bodyPacket = new WifiCommandPacket(command,data);
			break;
		case BleContent.cmd_wifiStatusCallBack:
			 bodyPacket = new WifiCommandPacket(command,data);
			break;
		case BleContent.cmd_prepareWifiMode:
			break;
		case BleContent.cmd_get_battery_level:
		case BleContent.cmd_report_battery_level:
			bodyPacket = new BatteryLevelPacket(command,data);
			break;
		case BleContent.cmd_get_env_params:
			bodyPacket = new EnvParamsPacket(command,data);
			break;
		default:
			break;
		}
		
		return bodyPacket;
	}
	
	private BodyPacket getBodyData(ArrayList<byte[]> data,int command){
		if(data == null) return null;
		int packet_size = data.size();
		int allSize = packet_size * PACKET_LENGTH_MAX;
		ByteBuffer buff = ByteBuffer.allocate(allSize);
		int realSize = 0;
		
		for(int i = 0;i<data.size();i++){
			realSize += data.get(i).length;
			buff.put(data.get(i));
		}
		byte[] temp = new byte[realSize];
		buff.position(0);
		buff.limit(realSize);
		buff.get(temp, 0, realSize);
		return getBodyData(temp,command);
	
	}
	
   /**
    * 解析数据
    * @param headPacket
    */
	private void transforData(HeadPacket headPacket){
		if(headPacket.getPacket_direction() == 0) {
			Slog.e("Error getPacket_direction : "+headPacket.getPacket_direction());
			return;
		}
		int command = headPacket.getCommand();
		BodyPacket bodyPacket = null;
		switch (command) { 
		case BleContent.cmd_connectBle:
		   bodyPacket = (ConnectBlePacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());			   
		   doingBleConnectedStatus((ConnectBlePacket)bodyPacket);
		   break;
		case BleContent.cmd_apCommand:
			bodyPacket = (WifiCommandPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			doingApCommand((WifiCommandPacket)bodyPacket);
			break;
		case BleContent.cmd_bleStatusCallback:
			bodyPacket  = (BleStatusPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			doingbleStatusCallback((BleStatusPacket)bodyPacket );
			break;
		case BleContent.cmd_bleStatus:
			bodyPacket  = (BleStatusPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			doingBleStatus((BleStatusPacket)bodyPacket);
			break;
		case BleContent.cmd_staCommand:
			bodyPacket= (WifiCommandPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
		    
			doingWifiStatusCallback((WifiCommandPacket)bodyPacket,command);
			break;	
		case BleContent.cmd_wifiStatus:
			bodyPacket= (WifiCommandPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			doingWifiStatusCallback((WifiCommandPacket)bodyPacket,command);
			break;
		case BleContent.cmd_wifiStatusCallBack:
			bodyPacket= (WifiCommandPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			doingWifiStatusCallback((WifiCommandPacket)bodyPacket,command);
			break;
		case BleContent.cmd_prepareWifiMode:  //none
			//by kfir
			if(mCallback != null){
				mCallback.onPreSetWifiModeResult();
			}
			break;
		case BleContent.cmd_ble_photo_cmd:
			if(mCallback != null){
				mCallback.onPhotoCmdFromDevice();
			}
			break;
		case BleContent.cmd_time_check:
			//TODO
			break;
		case BleContent.cmd_get_battery_level:
		case BleContent.cmd_report_battery_level:
			bodyPacket= (BatteryLevelPacket) getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());	
			batteryLevelCallback(((BatteryLevelPacket)bodyPacket).getLevel(),command);
			break;
		case BleContent.cmd_get_env_params:
			bodyPacket= getBodyData(ChannelQueue.pollAllData(),headPacket.getCommand());
			EnvParamsPacket params = (EnvParamsPacket) bodyPacket;
			EnvParamsCallback(params.getmTemp(), params.getmRH(), params.getmUV(), params.getmCommand());
			break;
		default:
			break;
		}
	}

	/**
	 * 头部 组包
	 * @param headPacket
	 * @return
	 */
	private byte[] setHeadPacket(HeadPacket headPacket){
	   byte[] headData = new byte[HEAD_LENGTH_MAX];
	   ByteBuffer buff = ByteBuffer.allocate(HEAD_LENGTH_MAX);
	   byte temp = (byte)headPacket.getMagicCode();
	   byte cmd = (byte) headPacket.getCommand();
	   byte total = (byte) headPacket.getTotal_pack();
	   buff.put(temp);
	   temp = (byte) ((( cmd  & 0x0F) << 4) | ((byte)(headPacket.getPacket_direction() & 0x0F))) ; 
	   buff.put(temp);
	   temp = (byte) ((( total & 0x0F) << 4) | ((byte)(headPacket.getCurr_pack() & 0x0F))) ; 
	   buff.put(temp);
	   temp = (byte)headPacket.getData_block_number(); 
	   buff.put(temp);
	   buff.position(0);
	   buff.limit(HEAD_LENGTH_MAX);
	   buff.get(headData, 0,HEAD_LENGTH_MAX);
	   return headData;
	   
	}
	
	/***************************************************/
	//HANDLE BLE NOTIFY INFO
	/**
	 * 请求连接BLE
	 * @param connectBlePacket
	 */
	private void doingBleConnectedStatus(ConnectBlePacket connectBlePacket){
		if(connectBlePacket == null ) return;
		int result = connectBlePacket.getStatus();//0 refused; 1 accepted
		if(mCallback != null){
			mCallback.onConnectRequestResult(result);
		}
	}
	
	/**
	 * 反馈AP启动结果
	 * @param wifiCommandPacket
	 */
	private void doingApCommand(WifiCommandPacket wifiCommandPacket) {
		doingWifiStatusCallback(wifiCommandPacket, BleContent.cmd_apCommand);
    }
	
	/**
	 * 状态改变
	 * @param bodyPacket
	 */
	private void doingbleStatusCallback(BleStatusPacket bodyPacket) {
		if(bodyPacket == null) return;
		bleStatusCallBack(bodyPacket,BleContent.cmd_bleStatusCallback);
    }
	
	/**
	 * 设备运行状态
	 * @param bodyPacket
	 */
	private void doingBleStatus(BleStatusPacket bodyPacket) {
		if(bodyPacket == null) return;
		bleStatusCallBack(bodyPacket, BleContent.cmd_bleStatus);
    }

	private void bleStatusCallBack(BleStatusPacket bodyPacket,int cmd) {	
		if(mCallback != null){
			mCallback.onDeviceStatusResult(bodyPacket.getLinkStatus_value(), 
					bodyPacket.getPowerStatus_value(), cmd);
		}
	}
	
	/**
	 * 设备wifi状态
	 * @param bodyPacket
	 * @param command
	 */
	private void doingWifiStatusCallback(WifiCommandPacket bodyPacket,int command)
	{
		if(bodyPacket == null) return;
		if(mCallback != null){
			mCallback.onWifiStatusResult(bodyPacket.getMode_value(), 
					bodyPacket.getSsid_value(), 
					bodyPacket.getIp_value(), 
					bodyPacket.getErroeCode_value(),
					command);
		}
     }
	
	private void batteryLevelCallback(int level, int command){
		if(mCallback != null){
			mCallback.onBatteryLevelChanged(level, command);
		}
	}
	
	private void EnvParamsCallback(byte temp, byte rh, byte uv, int command){
		if(mCallback != null){
			mCallback.onEnvParamsResult(temp, rh, uv, command);
		}
	}
	
//	private byte[] setBodyPacket(int command,BodyPacket bodyPacket){
//		
//		 return bodyPacket.getBodyBytes();
//	}
	
	

}
