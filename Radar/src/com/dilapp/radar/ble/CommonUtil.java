package com.dilapp.radar.ble;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;


public class CommonUtil {

	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	public static final int byteArrayToInt(byte[] bytes){
		int result = -1;
		if(bytes == null || bytes.length  != 4){
			return -1;
		}
		result = (bytes[0] & 0xFF) << 24;
		result |= (bytes[1] & 0xFF) << 16;
		result |= (bytes[2] & 0xFF) << 8;
		result |= (bytes[3] & 0xFF);
		return result;
	}
	
	public static Bitmap stringAsImg(String str){
		byte[] bytes = Base64.decode(str, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	public static byte[] imageAsBytes(Bitmap image){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 50, baos);
		image.recycle();
		byte[] bytes = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
		}
		return bytes;
	}
	
	public static Bitmap bytesAsImage(byte[] bytes){
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	public static String imgAsString(Bitmap image){
		String pngString = Base64.encodeToString(imageAsBytes(image), Base64.DEFAULT);
		return pngString;
	}
	
	public static void readLengthedData(int len, byte[] buffer, InputStream input) throws SocketException, IOException {
		//TODO len protection, len vs. buffer.length, large len leads to out of memory
		int left = len;
		int readCount = 0;
		while (readCount != len) {
			int read = input.read(buffer, readCount, left);
			if(read != -1){
				readCount += read;
				left -= read;
				if (readCount > len) {
					throw new IOException("readLengthedData error,required length:" + len
						+ ",but read:" + readCount);
				}
			}else{
				//throw new SocketException(Constants.SOCKET_CLOSED);
			}
		}
	}
	
}
