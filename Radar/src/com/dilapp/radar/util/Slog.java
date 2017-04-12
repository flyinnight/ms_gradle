package com.dilapp.radar.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Slog {
	
	public static final String TAG = "RADAR-TAG";
	
	public static void v(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.v(TAG, getCaller() + msg);
    }

    public static void v(String msg, Throwable e) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.v(TAG, getCaller() + msg, e);
    }

    public static void d(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.d(TAG, getCaller() + msg);
    }

    public static void d(String msg, Throwable e) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.d(TAG, getCaller() + msg, e);
    }

    public static void i(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.i(TAG, getCaller() + msg);
    }

    public static void i(String msg, Throwable e) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.i(TAG, getCaller() + msg, e);
    }

    public static void w(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.w(TAG, getCaller() + msg);
    }

    public static void w(String msg, Throwable e) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.w(TAG, getCaller() + msg, e);
    }

    public static void e(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.e(TAG, getCaller() + msg);
    }

    public static void e(String msg, Throwable e) {
        if (ReleaseUtils.DEBUG_LOG)
            Log.e(TAG, getCaller() + msg, e);
        // TODO umeng report
    }
    
    
    public static void f(String msg) {
        if (ReleaseUtils.DEBUG_LOG)
        	sd(msg);
    }
    
    
	public static void sd(String msg){
    		if(ReleaseUtils.DEBUG_LOG){
    			long time=System.currentTimeMillis();  
    	        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    	        Date d1=new Date(time);  
    	        String t1=format.format(d1);
    	        saveLogTo(t1+" : "+msg+"\r\n");
    		}
    }

    private static String getCaller() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length < 5)
            return null;
        StackTraceElement caller = stack[4];
        String className = caller.getClassName();
        int shortIndex = className.lastIndexOf(".");
        if (shortIndex > 0)
            className = className.substring(shortIndex + 1, className.length());
        return "[" + className + " - " + caller.getLineNumber() + "] ";
    }
    
    /** 
     * 追加文件：使用FileWriter 
     *  
     * @param fileName 
     * @param content 
     */  
    public static void saveLogTo(String content) {  
        try {  
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        		long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();  
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");  
            Date d1=new Date(time);  
            String t1 = format.format(d1);
            String path = PathUtils.SD_LOG+t1+".txt";
            FileWriter writer = new FileWriter(ABFileUtil.getFileAutoCreated(path), true);  
            writer.write(content);  
            writer.close();  
        } catch (IOException e) {
        		Slog.e("",e);
        }  
    }

}
