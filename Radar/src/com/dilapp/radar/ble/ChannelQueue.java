package com.dilapp.radar.ble;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ChannelQueue {
	
	private static final Object mLockObject = new Object();
	private static Queue<byte[]> mQueue = new LinkedList<byte[]>();
	public static int mCommand = -1;
	public static int mCurrent_pkg = -1;
	public static int max_pkg = -1;  
	
	public int size(){
		synchronized (mLockObject) {
			return mQueue.size();
		}
	}
	
	public static  void clearData(){
		synchronized (mLockObject) {
			mQueue.clear();
			mCommand = -1;
			mCurrent_pkg = -1;
			max_pkg = -1;
			 
		}
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static boolean offerData(byte[] data,int current_pkg){
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
	
	
	public static ArrayList<byte[]> pollAllData(){
		ArrayList<byte[]> datas = new ArrayList<byte[]>();
		synchronized (mLockObject) {
			if(mQueue.size() <= 0){
				return null;
			}
			byte[] temp = null;
			while((temp = mQueue.poll())!=null){
				datas.add(temp);
			}
			mQueue.clear();
			mCommand = -1;
			return datas;
			
		}
		
	}
	
	
	

}
