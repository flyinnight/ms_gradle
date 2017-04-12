package com.dilapp.radar.ble;

import java.util.LinkedList;
import java.util.Queue;

public class LeTransformQueue {

//	private ExecutorService mExecutor;
	
	private static final Object mLockObject = new Object();
	private static Queue<byte[]> mQueue = new LinkedList<byte[]>();

//    private BluetoothGattCharacteristic mWriter;

//    private BleHelper bleHelper;
    
    
//    private static final int INTERVAL_TIME = 100;
    
    public static int size(){
		synchronized (mLockObject) {
			return mQueue.size();
		}
	}
	
	public static  void clearData(){
		synchronized (mLockObject) {
			mQueue.clear();		 
		}
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static boolean offerData(byte[] data){
		if(data == null) return false ;
		synchronized (mLockObject) {
			 return mQueue.offer(data);
		}
	}
	
	/**
	 *
	 * @return
	 */
	public static byte[] peekData(){
		synchronized (mLockObject) {
			if(mQueue.size() <= 0){
				return null;
			}
			return mQueue.peek();
		}
	}
	
	/**
	 * @return
	 */
	public static byte[] pollData(){
		synchronized (mLockObject) {
			if(mQueue.size() <= 0){
				return null;
			}
			return mQueue.poll();
		}
	}

//    public LeTransformQueue(Context context) {
//        mExecutor = Executors.newSingleThreadExecutor();
//        bleHelper = BleHelper.getInstance(context);
//    }

//    public void setBluetoothGattCharacteristic(BluetoothGattCharacteristic writer) {
//        mWriter = writer;
//    }

//    public void sendData(int command,final ArrayList<byte[]>splitpackets ) {
//        if (bleHelper != null) {
//         //   byte[] orignal = Parcel.packetRequest(data);
//        	   byte[] orignal = null;
//            try {
//                //final ArrayList<byte[]> splitpackets = PackHelper.setPacket(command, bodyPacket);
//                for (int i = 0; i < splitpackets.size(); i++) {
//                    final int index = i;
//                    mExecutor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (bleHelper != null) {
//                            		bleHelper.writeValue(splitpackets.get(index));
//                                try {
//                                    Thread.sleep(INTERVAL_TIME);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

	
	
}
