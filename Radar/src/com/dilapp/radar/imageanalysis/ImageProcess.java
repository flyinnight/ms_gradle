package com.dilapp.radar.imageanalysis;

import java.io.File;

import com.dilapp.radar.util.Slog;
import com.dilapp.radar.wifi.Content;

import android.util.Log;

public class ImageProcess
{
	
	static {
	     System.loadLibrary("RadarIP-SA");
	}
	public native String  skinImageAnalysis(String rgb_file, String pl_file);
	
	public native String  checkImage(String rgb_file);
	
	
	
	private static Object object = new Object();
	private  static ImageProcess imageProcess = null;
	private ImageProcess (){
		
	}
	
	public static ImageProcess getInstance(){
		synchronized (object) {
			if(imageProcess == null){
				imageProcess = new ImageProcess();
			}
		}
		
		return imageProcess;
	}
	
	/**
	 * 
	 * input rgb image, output image status in string:
	 * eg.: "SKIN_OK,time used:200 ms"
	 * SKIN_OK
	 * SKIN_IMAGE_ERROR
	 * SKIN_INPUT_NOT_SKIN
	 * SKIN_OUT_FOCUS
	*/
	public String checkImg(String rgb_file){
		if(Content.IGNORE_SKIN_CHECK){
			return "SKIN_OK";
		}
		String result = checkImage(rgb_file);
		
		return result;
		
	}
   
	
    /**
     * 
     * @param rgb_file  图片的地址
     * @param pl_file   
     * 油,色素,弹性,胶原蛋白,水份,敏感度,毛孔,痤疮
     */
	public float[] runSkinImageAnalysis(String rgb_file, String pl_file){
		Slog.i("start runSkinImageAnalysis");
		Long startTime = System.currentTimeMillis();
		File file_rgb = new File(rgb_file.trim());
		if(!file_rgb.exists() || file_rgb.length() <= 0){
			Slog.e("rgb_file is not exist");
			return null;
		}
		file_rgb = new File(pl_file.trim());
		if(!file_rgb.exists() || file_rgb.length() <= 0){
			Slog.e("pl_file is not exist");
			return null;
		}
		
		String result = skinImageAnalysis(rgb_file, pl_file);
		
		String [] items = result.split(","); 
		for (int i = 0; i < items.length -1; i++) {
			int start = items[i].indexOf(":") + 1;
			items[i] = items[i].substring(start);
			Log.i("SkinImageAnalysis", items[i] );
        }
		
		Float oil;			//油
		Float melanin;		//色素
		Float elasticity;	//弹性
		Float collagen;		//胶原蛋白
		Float moisture;		//水份
		Float sensitivity;	//敏感度
		Float pore;			//毛孔
		Float acne;			//痤疮
		int 	skin_age;		//皮肤年龄
//		long time_used;		//耗时
		float [] data = new float[9];
		if (items.length > 7) {
			oil = Float.parseFloat(items[0]);
			data[0] = oil;
			melanin = Float.parseFloat(items[1]);
			data[1] = melanin;
			elasticity = Float.parseFloat(items[2]);
			data[2] = elasticity;
			collagen = Float.parseFloat(items[3]);
			data[3] = collagen;
			moisture = Float.parseFloat(items[4]);
			data[4] = moisture;
			sensitivity = Float.parseFloat(items[5]);
			data[5] =  sensitivity;
			pore = Float.parseFloat(items[6]);
			data[6] = pore;
			acne = Float.parseFloat(items[7]);
			data[7] = acne;
			skin_age = Integer.parseInt(items[8]);
//			skin_age -= 20;
			if(skin_age < 10){
				skin_age = 10;
			}
			data[8] = skin_age;
//			time_used = Long.parseLong(items[9]);
//			data[9] = time_used;
			
			Slog.i("oil "+oil.toString() );
			Slog.i("melanin "+melanin.toString() );
			Slog.i("elasticity "+elasticity.toString() );
			Slog.i("collagen "+collagen.toString() );
			Slog.i("moisture "+moisture.toString() );
			Slog.i("sensitivity "+sensitivity.toString() );
			Slog.i("pore "+pore.toString() );
			Slog.i("acne "+acne.toString() );
			Slog.i("skin_age "+skin_age);
			Slog.i("time_used "+items[9]);
		}else{
			data = null;
		}
		Slog.i("iMAGE  analyse finish  run time "+ (System.currentTimeMillis() - startTime));
		return (float[]) data;
	}
	
}


